/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
 * 
 * This file is part of Pineapple.
 * 
 * Pineapple is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * Pineapple is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public 
 * license for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/


package com.alpha.pineapple.plugin.weblogic.jmx.model.xmlbeans;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.reflection.GetterMethodMatcher;
import com.alpha.pineapple.plugin.weblogic.jmx.reflection.MethodUtils;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;

/**
 * Helper class for accessing model objects generated by XMLBeans.
 */
public class XmlBeansModelAccessorImpl implements XmlBeansModelAccessor
{
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Methods reflection utility object.
     */
    @Resource
    MethodUtils methodUtils;

    /**
     * XMLBean method matcher object
     */
    @Resource( name = "xmlBeansGetterMethodMatcher" )
    GetterMethodMatcher matcher;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
        
    public Object getModelObject( Object modelObject, Method getterMethod ) throws ModelResolutionFailedException
    {
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
    		Object[] args = { StringUtils.substringBefore( modelObject.toString(), "\n" ), getterMethod  };        	
        	logger.debug( messageProvider.getMessage("xbma.modelobject_start", args ) );        	
        }
    	
        try
        {
            // skip object retrieval if getter has arguments
            if ( !methodUtils.isMethodWithNoParameters( getterMethod ) )
            {
        		Object[] args = { StringUtils.substringBefore( modelObject.toString(), "\n" ), getterMethod  };
        		String message = messageProvider.getMessage("xbma.modelobject_hasargs_failed", args );        	
                throw new ModelResolutionFailedException( message );
            }

            // invoke method
            Object result = methodUtils.invokeMethodWithNoArgs( modelObject, getterMethod );

            // log debug message
            if ( logger.isDebugEnabled() )
            {
        		Object[] args = { ReflectionToStringBuilder.toString( result ) };        	
            	logger.debug( messageProvider.getMessage("xbma.modelobject_completed", args ) );        	
            }

            return result;
        }
        catch ( Exception e )
        {
    		Object[] args = { StringUtils.substringBefore( modelObject.toString(), "\n" ), getterMethod, StackTraceHelper.getStrackTrace( e ) };
    		String message = messageProvider.getMessage("xbma.modelobject_error", args );        	
            throw new ModelResolutionFailedException( message );
        }
    }

    public Method resolveGetterMethod( String attributeName, Object modelObject )
    {

        // resolve getter methods
        Method[] getterMethods = methodUtils.resolveGetterMethods( modelObject, attributeName, matcher );

        // if a single method is return then exit
        if ( getterMethods.length == 1 )
            return getterMethods[0];

        // first, try to select the method with no arguments
        for ( Method getterMethod : getterMethods )
        {

            // get number of parameters
            Class<?>[] parameters = getterMethod.getParameterTypes();

            // return method with no parameters
            if ( parameters.length == 0 )
                return getterMethod;
        }

        // TODO: handle case where multiple methods where returned
        return null;
    }

    public Method[] getGetterMethods( Object modelObject )
    {
        return methodUtils.resolveGetterMethods( modelObject, matcher );
    }
    
    /**
     * Returns true if type defines an collection.
     * 
     * @param attributeType Type of object who is tested.
     * 
     * @return True if type is object collection.
     * 
     */
    boolean isCollection(SchemaProperty attributeType )    
    {
    	return (attributeType.extendsJavaArray());
    }
    
    public String getNameAttributeFromModelObject( Object modelObject ) throws Exception
    {
        // get the getName method
        Method getNameMethod = methodUtils.getMethod( modelObject, "getName" );

        // invoke the get name method to get the XMLBean name.
        Object result = methodUtils.invokeMethodWithNoArgs( modelObject, getNameMethod );

        return (String) result;
    }

    public boolean isMethodEncrypted( Method method )
    {
        // get method name
        String methodName = method.getName();

        return methodName.indexOf( "Encrypted" ) != -1;
    }

    
    public boolean isValueSet( SchemaProperty attributeType, XmlObject xmlBean ) throws ModelResolutionFailedException {
    	
        try
        {        	
            // create method name
            String methodName = createIsSetMethodName( attributeType );
            
            // get isSet method
            Method isSetMethod = methodUtils.getMethod( xmlBean, methodName );

            // if no method is found then exit
            if ( isSetMethod == null ) {
            	return false;
            }

            // declare the result
            boolean result = false;

            // invoke the method
            Object stateAsObject = methodUtils.invokeMethodWithNoArgs( xmlBean, isSetMethod );

            // if result is a boolean (for all non array types) then interpret it
            if ( stateAsObject instanceof Boolean )
            {
                result = ( (Boolean) stateAsObject ).booleanValue();
            }
            // if result is a integer (for arrays) then interpret it            
            else if ( stateAsObject instanceof Integer )
            {
                int size = ( (Integer) stateAsObject ).intValue();

                // is set if array contains entries
                result = ( size != 0 );
            }

            // log debug message
            if ( logger.isDebugEnabled() )
            {
        		Object[] args = { result, attributeType.getName() };        	
            	logger.debug( messageProvider.getMessage("xbma.isvalueset_info", args ) );        	            	
            } 

            return result;
        }
        catch ( Exception e )
        {
    		Object[] args = { attributeType.getName(), xmlBean , StackTraceHelper.getStrackTrace( e ) };
    		String message = messageProvider.getMessage("xbma.isvalueset_error", args );        	
            throw new ModelResolutionFailedException( message, e );
        }    	
    }     
    
    /**
     * Create method name for isSetXX on an XMLBean. If the type is array then the created name is
     * <code>sizeOfXXArray</code> else the name is <code>isSetXX</code>.
     * 
     * @param attributeType SchemaProperty for the attribute.
     * 
     * @return method name for <code>isSetXX</code> for XMLBean or <code>sizeOfXXArray</code>
     * for array type. 
     */
    String createIsSetMethodName( SchemaProperty attributeType )
    {   	
        // declare name
        StringBuilder methodName = new StringBuilder();

        // create method name
        if ( isCollection( attributeType ) )
        {
            // create method name for array
            methodName.append( "sizeOf" );
            methodName.append( attributeType.getJavaPropertyName() );
            methodName.append( "Array" );
        }
        else
        {
            methodName = new StringBuilder();
            methodName.append( "isSet" );
            methodName.append( attributeType.getJavaPropertyName());
        }

        return methodName.toString();
    }    
    

    public Object getDefaultValue(SchemaProperty attributeType, XmlObject xmlBean) throws ModelResolutionFailedException {

    	// return value as String
        String valueAsString = attributeType.getDefaultValue().getStringValue();

        // log debug message
        if ( logger.isDebugEnabled() )
        {
    		Object[] args = { valueAsString, attributeType };        	
        	logger.debug( messageProvider.getMessage("xbma.getdefaultvalue_info", args ) );        	            	
        }

        // return converted value
        return ConvertUtils.convert( valueAsString, attributeType.getType().getJavaClass() );
	}

 
    public boolean isDefaultValueDefined( SchemaProperty attributeType, XmlObject xmlBean ) throws ModelResolutionFailedException
    {
    	return ( attributeType.hasDefault() == SchemaProperty.CONSISTENTLY );
    }
    
    
    
    public boolean isNil(SchemaProperty attributeType, XmlObject xmlBean) throws ModelResolutionFailedException {

        try {        	
    	
	        // if property isn't nillable then exit as non-nil.
	        if ( attributeType.hasNillable() != SchemaProperty.CONSISTENTLY )
	            return false;
	
	        // create method name
	        String methodName = createIsNilMethodName( attributeType );
	
	        // get the isNil method
            Method isNilMethod = methodUtils.getMethod( xmlBean, methodName );
	
	        // if no method is found then exit
	        if ( isNilMethod == null ) {
	            return false;        	
	        }
	        
	        // declare the result
	        boolean result = false;
	        
	        // invoke the method
	        Object stateAsObject = methodUtils.invokeMethodWithNoArgs( xmlBean, isNilMethod );
	        
	        // if result is a boolean (for all non array types) then interpret it
	        if ( stateAsObject instanceof Boolean )
	        {                           
	            result = ( (Boolean) stateAsObject ).booleanValue();
	
	            // if result is a integer (for arrays) then interpret it
	        }
	        else if ( stateAsObject instanceof Integer )
	        {                
	            int size = ( (Integer) stateAsObject ).intValue();
	
	            // is nil if array is empty 
	            result = ( size == 0 );
	        }
	
	        // log debug message
	        if ( logger.isDebugEnabled() )
	        {
	    		Object[] args = { result, attributeType.getName() };        	
	        	logger.debug( messageProvider.getMessage("xbma.isnil_info", args ) );        	            	
	        }
	
	        return result;
        }
        catch ( Exception e ) {
    		Object[] args = { attributeType.getName(), xmlBean , StackTraceHelper.getStrackTrace( e ) };
    		String message = messageProvider.getMessage("xbma.isnil_error", args );        	
            throw new ModelResolutionFailedException( message, e );        	
        }	        
	}

    /**
     * Create method name for isNilXX on an XMLBean. If the type is array then the created name is
     * <code>sizeOfXXArray</code> else the name is <code>isNilXX</code>.
     * 
     * @param attributeType SchemaProperty for the attribute.
     * 
     * @return method name for isNilXX on an XMLBean.
     */
    String createIsNilMethodName( SchemaProperty attributeType )
    {
        // declare name
        StringBuilder methodName = new StringBuilder();

        // create method name
        if ( isCollection( attributeType ) )
        {
            // create method name for array
            methodName.append( "sizeOf" );
            methodName.append( attributeType.getJavaPropertyName() );
            methodName.append( "Array" );
        }
        else
        {
            methodName = new StringBuilder();
            methodName.append( "isNil" );
            methodName.append( attributeType.getJavaPropertyName() );
        }

        return methodName.toString();
    }
                    
	public SchemaProperty getSchemaPropertyByName(String attributeName, SchemaProperty schemaProperty ) {

		// get schema type
		SchemaType type = schemaProperty.getType();

		// resolve type
		return getSchemaPropertyByName(attributeName, type );
	}

	public SchemaProperty getSchemaPropertyByName(String attributeName, SchemaType schemaType ) {
		
        // get schema properties (elements and attributes)
        SchemaProperty[] schemaProperties = schemaType.getProperties();

        // iterate to get schema property
        for ( SchemaProperty property : schemaProperties )
        {
            if ( property.getJavaPropertyName().equals( attributeName ) )
            {
                return property;
            }
        }

        return null;
	}
	
	public SchemaProperty getSchemaPropertyByName(String attributeName, XmlObject xmlObject) {
		
		// get schema type
		SchemaType schemaType = xmlObject.schemaType();		
				
		return getSchemaPropertyByName(attributeName, schemaType);
	}

	public boolean isCollection(ResolvedParticipant parent, ResolvedParticipant child) throws ModelResolutionFailedException {

        // get participant type 
        Object parentType = parent.getType();

        // fail if contained isn't a XMLBeans schema property
        if (!(parentType instanceof SchemaProperty )) {

    		Object[] args = { child };
    		String message = messageProvider.getMessage("xbma.iscollection_parent_failed", args );        	
            throw new ModelResolutionFailedException( message );        	
        }

        // get participant type 
        Object childType = child.getType();

        // fail if contained isn't a XMLBeans schema property
        if (!(childType instanceof SchemaProperty )) {

    		Object[] args = { child };
    		String message = messageProvider.getMessage("xbma.iscollection_child_failed", args );        	
            throw new ModelResolutionFailedException( message );        	
        }

        // type cast child type as schema property
        SchemaProperty shemaProperty =  (SchemaProperty) childType;       

        // get schema type
        SchemaType schemaType = shemaProperty.getType();

        // return negative result if type is simple type                
        if (schemaType.isSimpleType()) return false;
        
        // return negative result if child isn't array
        if (!shemaProperty.extendsJavaArray()) return false;
        
        // return positive result if the types are different.
        if( parentType != childType ) return true;
        
        return false;        
	}


	public boolean isEnum(ResolvedParticipant participant) throws ModelResolutionFailedException {

		// get participant type 
        Object participantType = participant.getType();

        // fail if contained isn't a XMLBeans schema property
        if (!(participantType instanceof SchemaProperty )) {

    		Object[] args = { participant };
    		String message = messageProvider.getMessage("xbma.isenum_failed", args );        	
            throw new ModelResolutionFailedException( message );        	
        }
                
        // type cast type as schema property
        SchemaProperty property = (SchemaProperty) participantType;
                
        // get schema type
        SchemaType schemaType = property.getType();

        // return negative result if type isn't simple type                
        if (!(schemaType.isSimpleType())) return false;
        
        // get enum values
        Object enumValues = schemaType.getEnumerationValues();
        
        return (enumValues != null);
	}

	public boolean isObject(ResolvedParticipant parent, ResolvedParticipant child) throws ModelResolutionFailedException {

        // get participant type 
        Object parentType = parent.getType();

        // fail if contained isn't a XMLBeans schema property
        if (!(parentType instanceof SchemaProperty )) {

    		Object[] args = { child };
    		String message = messageProvider.getMessage("xbma.isobject_parent_failed", args );        	
            throw new ModelResolutionFailedException( message );        	
        }

        // get participant type 
        Object childType = child.getType();

        // fail if contained isn't a XMLBeans schema property
        if (!(childType instanceof SchemaProperty )) {

    		Object[] args = { child };
    		String message = messageProvider.getMessage("xbma.isobject_child_failed", args );        	
            throw new ModelResolutionFailedException( message );        	
        }

        // type cast child type as schema property
        SchemaProperty shemaProperty =  (SchemaProperty) childType;       

        // get schema type
        SchemaType schemaType = shemaProperty.getType();

        // return negative result if type is simple type                
        if (schemaType.isSimpleType()) return false;
        
        // return positive result if child isn't array, e.g. an unbounded type
        if (!shemaProperty.extendsJavaArray()) return true;
        
        // now we have unbounded type, e.g. an array
        // return positive result if the types are equal, e.g. the type
        // is reused from the parent type.
        if( parentType == childType ) return true;
        
        return false;        
	}
	
	
	public boolean isPrimitive(ResolvedParticipant participant) throws ModelResolutionFailedException {

		// get primary participant type 
        Object participantType = participant.getType();
        
        // fail if contained isn't a XMLBeans schema property
        if (!(participantType instanceof SchemaProperty )) {

    		Object[] args = { participant };
    		String message = messageProvider.getMessage("xbma.isprimitive_failed", args );        	
            throw new ModelResolutionFailedException( message );        	            
        }
                
        // type cast type as schema property
        SchemaProperty property =  (SchemaProperty) participantType;

        // get schema type
        SchemaType schemaType = property.getType();
        
        // return negative result if type isn't simple type                
        if (!(schemaType.isSimpleType())) return false;
                
        // return positive result if type isn't enum                
        return (schemaType.getEnumerationValues() == null );
	}
    
    
}
