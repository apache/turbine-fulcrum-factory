package org.apache.fulcrum.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */



import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Basic tests of the fulcrum factory service
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * 
 * @version $Id$ 
 */
public class FactoryServiceTest extends BaseUnit5Test
{
	/** Default factory service **/
    private FactoryService factoryService = null;

    @BeforeEach
    public void setUp() throws Exception
    {
        setConfigurationFileName("src/test/TestComponentConfig.xml");
        setRoleFileName("src/test/TestRoleConfig.xml");
        factoryService = (FactoryService) this.lookup(FactoryService.class.getName());    	
    }
    
    /**
     * Class to test for Object getInstance(String)
     * @throws Exception if factory fails to generate object
     */
    @Test
    public void testGetInstanceString() throws Exception
    {
        Object object = factoryService.getInstance("java.lang.StringBuilder");
        assertTrue(object instanceof StringBuilder);
    }
    
    /**
     * Class to test for Object getInstance(String, ClassLoader)
     *
     * @throws Exception Generic exception
     */
    @Test
    public void testGetInstanceStringClassLoader() throws Exception
    {
        Object object = factoryService.getInstance("java.lang.StringBuilder", StringBuilder.class.getClassLoader());
        assertTrue(object instanceof StringBuilder);
    }
    
    /**
     * Class to test for Object getInstance(String, Object[], String[])
     * @throws Exception Generic exception
     */
    @Test
    public void testGetInstanceStringObjectArrayStringArray() throws Exception
    {
    	String sourceValue = "testing";
    	Object params[] = new Object[] { sourceValue };
        String signature[] = new String[] { "java.lang.String" };

        Object object = factoryService.getInstance("java.lang.StringBuilder", params, signature);
        assertTrue(object instanceof StringBuilder);
        assertEquals(sourceValue, object.toString());
    }
    
    /**
     * Class to test for Object getInstance(String, ClassLoader, Object[], String[])
     * 
     * @throws Exception Generic exception
     */    
    @Test
    public void testGetInstanceStringClassLoaderObjectArrayStringArray() throws Exception
    {
    	String sourceValue = "testing";
    	Object params[] = new Object[] { sourceValue };
        String signature[] = new String[] { "java.lang.String" };

        Object object =
            factoryService.getInstance(
                "java.lang.StringBuilder",
                StringBuilder.class.getClassLoader(),
                params,
                signature);
        assertTrue(object instanceof StringBuilder);
        assertEquals(sourceValue, object.toString());

    }
    
    /**
     * Test if the loader is supported
     * 
     * @throws Exception Generic exception
     */
    @Test
    public void testIsLoaderSupported() throws Exception
    {
        // TODO Need to run a test where the loader is NOT supported.
        assertTrue(factoryService.isLoaderSupported("java.lang.String"));
    }
    

    /**
     * Test get signature
     * 
     * @throws Exception Generic exception
     */
    @Test
    public void testGetSignature() throws Exception
    {
    	String sourceValue = "testing";
    	Object params[] = new Object[] { sourceValue };
        String signature[] = new String[] { "java.lang.String" };

        Class<?>[] results = factoryService.getSignature(StringBuilder.class, params, signature);
        assertEquals(1, results.length);
        assertTrue(results[0].equals(String.class));

        Integer sourceValueInteger = new Integer(10);
        params[0] = sourceValueInteger;
        signature[0] = "java.lang.Integer";
        results = factoryService.getSignature(ArrayList.class, params, signature);
        assertEquals(1, results.length);
        assertTrue(results[0].equals(Integer.class));
    }
}
