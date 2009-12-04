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
package org.apache.directory.server.schema;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.NoSuchAttributeException;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.schema.loader.ldif.LdifSchemaLoaderTest;
import org.apache.directory.shared.ldap.exception.LdapSchemaViolationException;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.schema.DefaultSchemaManager;
import org.apache.directory.shared.schema.loader.ldif.JarLdifSchemaLoader;
import org.apache.directory.shared.schema.loader.ldif.LdifSchemaLoader;
import org.apache.directory.shared.schema.loader.ldif.SchemaEntityFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * A test class for SchemaManager.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaManagerTest
{
    // A directory in which the ldif files will be stored
    private static String workingDirectory;

    // A LDIF loader
    private static LdifSchemaLoader ldifLoader;

    // A SchemaObject factory
    private static SchemaEntityFactory factory;


    @BeforeClass
    public static void setup() throws Exception
    {
        workingDirectory = System.getProperty( "workingDirectory" );

        if ( workingDirectory == null )
        {
            String path = LdifSchemaLoaderTest.class.getResource( "" ).getPath();
            int targetPos = path.indexOf( "target" );
            workingDirectory = path.substring( 0, targetPos + 6 );
        }

        // Cleanup the target directory
        FileUtils.deleteDirectory( new File( workingDirectory + "/schema" ) );

        SchemaLdifExtractor extractor = new SchemaLdifExtractor( new File( workingDirectory ) );
        extractor.extractOrCopy();

        ldifLoader = new LdifSchemaLoader( new File( workingDirectory, "schema" ) );
        factory = new SchemaEntityFactory();
    }


    @AfterClass
    public static void cleanup() throws IOException
    {
        // Cleanup the target directory
        FileUtils.deleteDirectory( new File( workingDirectory + "/schema" ) );
    }


    private SchemaManager loadSystem() throws Exception
    {
        JarLdifSchemaLoader loader = new JarLdifSchemaLoader();
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        String schemaName = "system";

        schemaManager.loadWithDeps( schemaName );

        return schemaManager;
    }


    private boolean isATPresent( SchemaManager schemaManager, String oid )
    {
        try
        {
            AttributeType attributeType = schemaManager.lookupAttributeTypeRegistry( oid );

            return attributeType != null;
        }
        catch ( NoSuchAttributeException nsae )
        {
            return false;
        }
        catch ( NamingException ne )
        {
            return false;
        }
    }


    //=========================================================================
    // AttributeType addition tests
    //-------------------------------------------------------------------------
    // First, not defined superior
    //-------------------------------------------------------------------------
    /**
     * Try to inject an AttributeType without any superior nor Syntax : it's invalid
     */
    @Test
    public void testAddAttributeTypeNoSupNoSyntaxNoSuperior() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
    }


    /**
     * Try to inject an AttributeType which is Collective, and userApplication AT
     */
    @Test
    public void testAddAttributeTypeNoSupCollectiveUser() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );
        attributeType.setCollective( true );

        // It should not fail
        assertTrue( schemaManager.add( attributeType ) );

        assertTrue( isATPresent( schemaManager, "1.1.0" ) );
    }


    /**
     * Try to inject an AttributeType which is Collective, but an operational AT
     */
    @Test
    public void testAddAttributeTypeNoSupCollectiveOperational() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.DIRECTORY_OPERATION );
        attributeType.setCollective( true );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
    }


    /**
     * Try to inject an AttributeType which is a NO-USER-MODIFICATION and userApplication
     */
    @Test
    public void testAddAttributeTypeNoSupNoUserModificationUserAplication() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );
        attributeType.setUserModifiable( false );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
    }


    /**
     * Try to inject an AttributeType which is a NO-USER-MODIFICATION and is operational
     */
    @Test
    public void testAddAttributeTypeNoSupNoUserModificationOpAttr() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.DISTRIBUTED_OPERATION );
        attributeType.setUserModifiable( false );

        // It should not fail
        assertTrue( schemaManager.add( attributeType ) );

        assertTrue( isATPresent( schemaManager, "1.1.0" ) );
    }


    /**
     * Try to inject an AttributeType with an invalid EQUALITY MR
     */
    @Test
    public void testAddAttributeTypeNoSupInvalidEqualityMR() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "0.0" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
    }


    /**
     * Try to inject an AttributeType with an invalid ORDERING MR
     */
    @Test
    public void testAddAttributeTypeNoSupInvalidOrderingMR() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( "0.0" );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
    }


    /**
     * Try to inject an AttributeType with an invalid SUBSTR MR
     */
    @Test
    public void testAddAttributeTypeNoSupInvalidSubstringMR() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( "0.0" );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
    }


    /**
     * Try to inject an AttributeType with valid MRs
     */
    @Test
    public void testAddAttributeTypeNoSupValidMR() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( "2.5.13.1" );
        attributeType.setSubstringOid( "2.5.13.1" );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );

        // It should not fail
        assertTrue( schemaManager.add( attributeType ) );

        assertTrue( isATPresent( schemaManager, "1.1.0" ) );
    }


    /**
     * Try to inject an AttributeType which already exist
     */
    @Test
    public void testAddAttributeTypeAlreadyExist() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "2.5.18.4" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( "2.5.13.1" );
        attributeType.setSubstringOid( "2.5.13.1" );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        // The AT must be there
        assertTrue( isATPresent( schemaManager, "2.5.18.4" ) );

        // Check that it hasen't changed
        AttributeType original = schemaManager.lookupAttributeTypeRegistry( "2.5.18.4" );
        assertEquals( "distinguishedNameMatch", original.getEqualityOid() );
    }


    //-------------------------------------------------------------------------
    // Then, with a superior
    //-------------------------------------------------------------------------
    /**
     * Try to inject an AttributeType with a superior and no Syntax : it should
     * take its superior' syntax and MR
     */
    @Test
    public void testAddAttributeTypeSupNoSyntaxNoSuperior() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSuperiorOid( "2.5.18.4" );
        attributeType.setUsage( UsageEnum.DIRECTORY_OPERATION );

        // It should not fail
        assertTrue( schemaManager.add( attributeType ) );

        AttributeType result = schemaManager.lookupAttributeTypeRegistry( "1.1.0" );

        assertEquals( "1.3.6.1.4.1.1466.115.121.1.12", result.getSyntaxOid() );
        assertEquals( "2.5.13.1", result.getEqualityOid() );
    }


    /**
     * Try to inject an AttributeType with a superior and different USAGE
     */
    @Test
    public void testAddAttributeTypeSupDifferentUsage() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSuperiorOid( "2.5.18.4" );
        attributeType.setUsage( UsageEnum.DISTRIBUTED_OPERATION );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
    }


    /**
     * Try to inject an AttributeType with itself as a superior
     */
    @Test
    public void testAddAttributeTypeSupWithOwnSup() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSuperiorOid( "1.1.0" );
        attributeType.setUsage( UsageEnum.DISTRIBUTED_OPERATION );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
    }


    /**
     * Try to inject an AttributeType with a bad superior
     */
    @Test
    public void testAddAttributeTypeSupBadSup() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSuperiorOid( "0.0" );
        attributeType.setUsage( UsageEnum.DISTRIBUTED_OPERATION );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
    }
}
