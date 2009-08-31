/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.ldap.schema.registries;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.DITContentRule;
import org.apache.directory.shared.ldap.schema.DITStructureRule;
import org.apache.directory.shared.ldap.schema.LdapSyntax;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.MatchingRuleUse;
import org.apache.directory.shared.ldap.schema.NameForm;
import org.apache.directory.shared.ldap.schema.ObjectClass;


/**
 * Document this class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class Registries implements SchemaLoaderListener
{
    /**
     * A String name to Schema object map for those schemas loaded into this
     * registry.
     */
    protected Map<String, Schema> loadedSchemas = new HashMap<String, Schema>();

    /** The AttributeType registry */
    protected AttributeTypeRegistry attributeTypeRegistry;
    
    /** The ObjectClass registry */
    protected ObjectClassRegistry objectClassRegistry;

    /** The LdapSyntax registry */
    protected ComparatorRegistry comparatorRegistry;

    /** The DitContentRule registry */
    protected DITContentRuleRegistry ditContentRuleRegistry;

    /** The DitStructureRule registry */
    protected DITStructureRuleRegistry ditStructureRuleRegistry;

    /** The MatchingRule registry */
    protected MatchingRuleRegistry matchingRuleRegistry;

    /** The MatchingRuleUse registry */
    protected MatchingRuleUseRegistry matchingRuleUseRegistry;

    /** The NameForm registry */
    protected NameFormRegistry nameFormRegistry;

    /** The Normalizer registry */
    protected NormalizerRegistry normalizerRegistry;

    /** The OID registry */
    protected OidRegistry oidRegistry;

    /** The SyntaxChecker registry */
    protected SyntaxCheckerRegistry syntaxCheckerRegistry;

    /** The LdapSyntax registry */
    protected LdapSyntaxRegistry ldapSyntaxRegistry;
    

    /**
     * Creates a new instance of Registries.
     *
     * @param oidRegistry the OID registry
     */
    public Registries()
    {
        this.oidRegistry = new OidRegistry();
        normalizerRegistry = new NormalizerRegistry( oidRegistry );
        comparatorRegistry = new ComparatorRegistry( oidRegistry );
        syntaxCheckerRegistry = new SyntaxCheckerRegistry( oidRegistry );
        ldapSyntaxRegistry = new LdapSyntaxRegistry( oidRegistry );
        matchingRuleRegistry = new MatchingRuleRegistry( oidRegistry );
        attributeTypeRegistry = new AttributeTypeRegistry( oidRegistry );
        objectClassRegistry = new ObjectClassRegistry( oidRegistry );
        ditContentRuleRegistry = new DITContentRuleRegistry( oidRegistry );
        ditStructureRuleRegistry = new DITStructureRuleRegistry( oidRegistry );
        matchingRuleUseRegistry = new MatchingRuleUseRegistry( oidRegistry );
        nameFormRegistry = new NameFormRegistry( oidRegistry );
    }

    
    /**
     * @return The AttributeType registry
     */
    public AttributeTypeRegistry getAttributeTypeRegistry()
    {
        return attributeTypeRegistry;
    }

    
    /**
     * @return The Comparator registry
     */
    public ComparatorRegistry getComparatorRegistry()
    {
        return comparatorRegistry;
    }

    
    /**
     * @return The DITContentRule registry
     */
    public DITContentRuleRegistry getDitContentRuleRegistry()
    {
        return ditContentRuleRegistry;
    }

    
    /**
     * @return The DITStructureRule registry
     */
    public DITStructureRuleRegistry getDitStructureRuleRegistry()
    {
        return ditStructureRuleRegistry;
    }

    
    /**
     * @return The MatchingRule registry
     */
    public MatchingRuleRegistry getMatchingRuleRegistry()
    {
        return matchingRuleRegistry;
    }

    
    /**
     * @return The MatchingRuleUse registry
     */
    public MatchingRuleUseRegistry getMatchingRuleUseRegistry()
    {
        return matchingRuleUseRegistry;
    }

    
    /**
     * @return The NameForm registry
     */
    public NameFormRegistry getNameFormRegistry()
    {
        return nameFormRegistry;
    }

    
    /**
     * @return The Normalizer registry
     */
    public NormalizerRegistry getNormalizerRegistry()
    {
        return normalizerRegistry;
    }

    
    /**
     * @return The ObjectClass registry
     */
    public ObjectClassRegistry getObjectClassRegistry()
    {
        return objectClassRegistry;
    }

    
    /**
     * @return The getOid registry
     */
    public OidRegistry getOidRegistry()
    {
        return oidRegistry;
    }

    
    /**
     * @return The SyntaxChecker registry
     */
    public SyntaxCheckerRegistry getSyntaxCheckerRegistry()
    {
        return syntaxCheckerRegistry;
    }

    
    /**
     * @return The LdapSyntax registry
     */
    public LdapSyntaxRegistry getLdapSyntaxRegistry()
    {
        return ldapSyntaxRegistry;
    }
    
    
    /**
     * Get an OID from a name. As we have many possible registries, we 
     * have to look in all of them to get the one containing the OID.
     *
     * @param name The name we are looking at
     * @return The associated OID
     */
    public String getOid( String name )
    {
        // we have many possible Registries to look at.
        // AttributeType
        try
        {
            AttributeType attributeType = attributeTypeRegistry.lookup( name );
            
            return attributeType.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }
        
        // ObjectClass
        try
        {
            ObjectClass objectClass = objectClassRegistry.lookup( name );
            
            return objectClass.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }

        // LdapSyntax
        try
        {
            LdapSyntax ldapSyntax = ldapSyntaxRegistry.lookup( name );
            
            return ldapSyntax.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }
        
        // MatchingRule
        try
        {
            MatchingRule matchingRule = matchingRuleRegistry.lookup( name );
            
            return matchingRule.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }
        
        // MatchingRuleUse
        try
        {
            MatchingRuleUse matchingRuleUse = matchingRuleUseRegistry.lookup( name );
            
            return matchingRuleUse.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }
        
        // NameForm
        try
        {
            NameForm nameForm = nameFormRegistry.lookup( name );
            
            return nameForm.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }
        
        // DITContentRule
        try
        {
            DITContentRule ditContentRule = ditContentRuleRegistry.lookup( name );
            
            return ditContentRule.getOid();
        }
        catch ( NamingException ne )
        {
            // Fall down to the next registry
        }

        // DITStructureRule
        try
        {
            DITStructureRule ditStructureRule = ditStructureRuleRegistry.lookup( name );
            
            return ditStructureRule.getOid();
        }
        catch ( NamingException ne )
        {
            // No more registries to look at...
            return null;
        }
    }


    /**
     * Gets a schema that has been loaded into these Registries.
     * 
     * @param schemaName the name of the schema to lookup
     * @return the loaded Schema if one corresponding to the name exists
     */
    public Schema getLoadedSchema( String schemaName )
    {
        return loadedSchemas.get( schemaName );
    }


    /**
     * Checks to see if a particular Schema is loaded.
     *
     * @param schemaName the name of the Schema to check
     * @return true if the Schema is loaded, false otherwise
     */
    public boolean isSchemaLoaded( String schemaName )
    {
        return loadedSchemas.containsKey( schemaName );
    }


    // ------------------------------------------------------------------------
    // Code used to sanity check the resolution of entities in registries
    // ------------------------------------------------------------------------
    /**
     * Attempts to resolve the dependent schema objects of all entities that
     * refer to other objects within the registries.  Null references will be
     * handed appropriately.
     *
     * @return a list of exceptions encountered while resolving entities
     */
    public List<Throwable> checkRefInteg()
    {
        ArrayList<Throwable> errors = new ArrayList<Throwable>();

        // Check the ObjectClasses
        for ( ObjectClass objectClass : objectClassRegistry )
        {
            resolve( objectClass, errors );
        }

        // Check the AttributeTypes
        for ( AttributeType attributeType : attributeTypeRegistry )
        {
            resolve( attributeType, errors );
        }

        // Check the MatchingRules
        for ( MatchingRule matchingRule : matchingRuleRegistry )
        {
            resolve( matchingRule, errors );
        }

        // Check the LdapSyntax
        for ( LdapSyntax ldapSyntax : ldapSyntaxRegistry )
        {
            resolve( ldapSyntax, errors );
        }

        return errors;
    }

    
    /**
     * Attempts to resolve the SyntaxChecker associated with a Syntax.
     *
     * @param syntax the LdapSyntax to resolve the SyntaxChecker of
     * @param errors the list of errors to add exceptions to
     * @return true if it succeeds, false otherwise
     */
    private boolean resolve( LdapSyntax syntax, List<Throwable> errors )
    {
        if ( syntax == null )
        {
            return true;
        }

        try
        {
            syntax.getSyntaxChecker();
            return true;
        }
        catch ( Exception e )
        {
            errors.add( e );
            return false;
        }
    }


    /**
     * Check if the Normalizer and Comparator are existing for a matchingRule
     */
    private boolean resolve( MatchingRule mr, List<Throwable> errors )
    {
        boolean isSuccess = true;

        if ( mr == null )
        {
            return true;
        }

        try
        {
            if ( mr.getLdapComparator() == null )
            {
                String schema = matchingRuleRegistry.getSchemaName( mr.getOid() );
                errors.add( new NullPointerException( "matchingRule " + mr.getName() + " in schema " + schema
                    + " with OID " + mr.getOid() + " has a null comparator" ) );
                isSuccess = false;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        try
        {
            if ( mr.getNormalizer() == null )
            {
                String schema = matchingRuleRegistry.getSchemaName( mr.getOid() );
                errors.add( new NullPointerException( "matchingRule " + mr.getName() + " in schema " + schema
                    + " with OID " + mr.getOid() + " has a null normalizer" ) );
                isSuccess = false;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        try
        {
            isSuccess &= resolve( mr.getSyntax(), errors );

            if ( mr.getSyntax() == null )
            {
                String schema = matchingRuleRegistry.getSchemaName( mr.getOid() );
                errors.add( new NullPointerException( "matchingRule " + mr.getName() + " in schema " + schema
                    + " with OID " + mr.getOid() + " has a null Syntax" ) );
                isSuccess = false;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        return isSuccess;
    }


    /**
     * Check the inheritance, and the existence of MatchingRules and LdapSyntax
     * for an attribute 
     */
    private boolean resolve( AttributeType at, List<Throwable> errors )
    {
        boolean isSuccess = true;

        boolean hasMatchingRule = false;

        if ( at == null )
        {
            return true;
        }

        try
        {
            isSuccess &= resolve( at.getSuperior(), errors );
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        try
        {
            isSuccess &= resolve( at.getEquality(), errors );

            if ( at.getEquality() != null )
            {
                hasMatchingRule |= true;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        try
        {
            isSuccess &= resolve( at.getOrdering(), errors );

            if ( at.getOrdering() != null )
            {
                hasMatchingRule |= true;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        try
        {
            isSuccess &= resolve( at.getSubstring(), errors );

            if ( at.getSubstring() != null )
            {
                hasMatchingRule |= true;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        try
        {
            isSuccess &= resolve( at.getSyntax(), errors );

            if ( at.getSyntax() == null )
            {
                String schema = attributeTypeRegistry.getSchemaName( at.getOid() );

                errors.add( new NullPointerException( "attributeType " + at.getName() + " in schema " + schema
                    + " with OID " + at.getOid() + " has a null Syntax" ) );

                isSuccess = false;
            }
        }
        catch ( Exception e )
        {
            errors.add( e );
            isSuccess = false;
        }

        return isSuccess;
    }


    private boolean resolve( ObjectClass oc, List<Throwable> errors )
    {
        boolean isSuccess = true;

        if ( oc == null )
        {
            return true;
        }

        List<ObjectClass> superiors = oc.getSuperiors();

        if ( ( superiors == null ) || ( superiors.size() == 0 ) )
        {
            isSuccess = false;
        }
        else
        {
            for ( ObjectClass superior : superiors )
            {
                isSuccess &= resolve( superior, errors );
            }
        }

        for ( AttributeType attributeType : oc.getMayAttributeTypes() )
        {
            isSuccess &= resolve( attributeType, errors );
        }

        for ( AttributeType attributeType : oc.getMustAttributeTypes() )
        {
            isSuccess &= resolve( attributeType, errors );
        }

        return isSuccess;
    }


    /**
     * Merely adds the schema to the set of loaded schemas.  Does not
     * actually do any work to add schema objects to registries.
     * 
     * {@inheritDoc}
     */
	public void schemaLoaded( Schema schema ) 
	{
		this.loadedSchemas.put( schema.getSchemaName(), schema );
	}


    /**
     * Merely removes the schema from the set of loaded schemas.  Does not
     * actually do any work to remove schema objects from registries.
     * 
     * {@inheritDoc}
     */
	public void schemaUnloaded(Schema schema) 
	{
		this.loadedSchemas.remove( schema.getSchemaName() );
	}


	/**
	 * Gets an unmodifiable Map of schema names to loaded Schema objects. 
	 * 
	 * @return the map of loaded Schema objects
	 */
	public Map<String, Schema> getLoadedSchemas() 
	{
		return Collections.unmodifiableMap( loadedSchemas );
	}
}
