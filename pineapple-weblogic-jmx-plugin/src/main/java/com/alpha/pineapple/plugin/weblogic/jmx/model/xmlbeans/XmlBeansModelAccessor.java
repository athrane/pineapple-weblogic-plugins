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

import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;

/**
 * Interface accessing model objects generated by XMLBeans. 
 */
public interface XmlBeansModelAccessor
{

    /**
     * Get model object using getter method. If the getter method expected parameters
     * then model object retrieval is skipped and null is returned.  
     * 
     * @param modelObject The model object whose getter method is invoked.
     * @param getterMethod the getter method which is invoked.
     * 
     * @return model object which is retrieved by invoking the getter method. If the 
     * getter method expected parameters then model object retrieval is skipped and 
     * null is returned.
     * 
     * @throws ModelResolutionFailedException If object retrieval fails.
     */    
    public Object getModelObject( Object modelObject, Method getterMethod ) throws ModelResolutionFailedException;

    /**
     * Resolve getter method from attribute name.
     * 
     * @param attributeName Name of the attribute.
     * @param modelObject The model object which contains the attribute.
     * 
     * @return Getter method which provides access to the attribute.
     */
    public Method resolveGetterMethod( String attributeName, Object modelObject );

    /**
     * Get all getter methods.
     * 
     * @param modelObject The model object whose getter methods should be returned.
     * 
     * @return Array of all getter methods.
     */    
    public Method[] getGetterMethods( Object modelObject );    
                    
    /**
     * Return the value of the attribute named "Name" from a model object.
     * @param modelObject The model object to query.
     * 
     * @return Value of the attribute named "Name" from a model object.
     * 
     * @throws Exception If attribute value retrieval fails.
     */
    public String getNameAttributeFromModelObject( Object modelObject ) throws Exception;
    
    /**
     * Return true if method is encrypted.
     * 
     * @param method The method to process.
     * 
     * @return true if method is encrypted.
     */    
    public boolean isMethodEncrypted( Method method );
 
    /**
     * Returns true if the attribute has its value set on the XMLBean.
     *  
     * @param attributeType SchemaProperty for the attribute.
     * @param xmlBean XMLBean which contains the attribute. 
     * 
     * @return True if the attribute value is set on the model object.  
     * 
     * @throws ModelResolutionFailedException If getting the set state fails.
     */
    public boolean isValueSet( SchemaProperty attributeType, XmlObject xmlBean ) throws ModelResolutionFailedException;
        
    /**
     * Returns true if the attribute has default value defined in the schema.
     *  
     * @param attributeType SchemaProperty for the attribute.
     * @param xmlBean XMLBean which contains the attribute. 
     * 
     * @return True if the attribute value has default value defined in the schema.
     *   
     * @throws ModelResolutionFailedException If the query fails.
     */    
    public boolean isDefaultValueDefined( SchemaProperty attributeType, XmlObject xmlBean ) throws ModelResolutionFailedException;
        
    /**
     * Get default value from attribute defined in the schema.
     *  
     * @param attributeType SchemaProperty for the attribute.
     * @param xmlBean XMLBean which contains the attribute. 
     * 
     * @return Default value from attribute defined in the schema.
     *   
     * @throws ModelResolutionFailedException If the query fails.
     */        
    public Object getDefaultValue( SchemaProperty attributeType, XmlObject xmlBean ) throws ModelResolutionFailedException;
    
    
    /**
     * Returns true if the attribute value is nil.
     *  
     * @param attributeType SchemaProperty for the attribute.
     * @param xmlBean XMLBean which contains the attribute. 
     * 
     * @return True if the attribute value is nil.   
     * @throws ModelResolutionFailedException If the query fails.
     */    
    public boolean isNil( SchemaProperty attributeType, XmlObject xmlBean ) throws ModelResolutionFailedException;
    
    /**
     * Get XMLBean schema property from schema type object.
     *  
     * @param attributeName Name of the attribute to get the schema property for.
     * @param type Schema property object which represents the type of an model object.
     * 
     * @return XMLBean schema property from attribute. Returns null if schema type dosen't
     * contain attribute with requested name.
     */
    SchemaProperty getSchemaPropertyByName( String attributeName, SchemaProperty schemaProperty );   

    /**
     * Get XMLBean schema property from schema type object.
     *  
     * @param attributeName Name of the attribute to get the schema property for.
     * @param type Schema type object which represents the type of an model object.
     * 
     * @return XMLBean schema property from attribute. Returns null if schema type dosen't
     * contain attribute with requested name.
     */
    SchemaProperty getSchemaPropertyByName( String attributeName, SchemaType schemaType );   

    /**
     * Get XMLBean schema property from XmlObject object.
     *  
     * @param attributeName Name of the attribute to get the schema property for.
     * @param type XmlObject whose schema property is returned for selected attribute.
     * 
     * @return XMLBean schema property from attribute. Returns null if schema type dosen't
     * contain attribute with requested name.
     */
    SchemaProperty getSchemaPropertyByName( String attributeName, XmlObject xmlObject );   
        
    
    /**
     * Returns true if participant type is an instance of <code>SchemaProperty</code> 
     * and the schema type represents an simple type.
     * 
     * @param participant Participant whose type is tested.
     * 
     * @return true if participant type is an instance of <code>SchemaProperty</code> 
     * and the schema type represents an simple type.
     * 
     * @throws ModelResolutionFailedException if the type of the participant isn't
     * an instance of <code>SchemaProperty</code>.
     */
    public boolean isPrimitive(ResolvedParticipant participant ) throws ModelResolutionFailedException;
    

    /**
     * Returns true if child participant type is an instance of <code>SchemaProperty</code> 
     * and the schema type represents an unbounded attribute, e.g. an array/collection.
     * 
     * @param parent Parent participant.
     * @param child Child participant whose type is tested. 
     * 
     * @return true if participant type is an instance of <code>SchemaProperty</code> 
     * and the schema type represents an array/collection.
     * 
     * @throws ModelResolutionFailedException if the type of the child participant isn't
     * an instance of <code>SchemaProperty</code>.
     * 
     * @throws ModelResolutionFailedException if the type of the parent participant isn't
     * an instance of <code>SchemaProperty</code>. 
     */
    public boolean isCollection( ResolvedParticipant parent, ResolvedParticipant child ) throws ModelResolutionFailedException;
    
    /**
     * Returns true if participant type is an instance of <code>SchemaProperty</code> 
     * and the schema type represents an enumeration.
     * 
     * @param participant Participant whose type is tested.
     * 
     * @return true if participant type is an instance of <code>SchemaProperty</code> 
     * and the schema type represents an enumeration.
     * 
     * @throws ModelResolutionFailedException if the type of the participant isn't
     * an instance of <code>SchemaProperty</code>.
     */
    public boolean isEnum( ResolvedParticipant participant ) throws ModelResolutionFailedException;
    
    /**
     * Returns true if child participant type is an instance of <code>SchemaProperty</code> 
     * and the schema type represents an complex type, e.g. an object.
     * 
     * @param parent Parent participant.
     * @param child Child participant whose type is tested. 
     * 
     * @return true if participant type is an instance of <code>SchemaProperty</code> 
     * and the schema type represents an object.
     * 
     * @throws ModelResolutionFailedException if the type of the child participant isn't
     * an instance of <code>SchemaProperty</code>.
     * 
     * @throws ModelResolutionFailedException if the type of the parent participant isn't
     * an instance of <code>SchemaProperty</code>.      
     */    
    public boolean isObject(ResolvedParticipant parent, ResolvedParticipant child) throws ModelResolutionFailedException;
    
}