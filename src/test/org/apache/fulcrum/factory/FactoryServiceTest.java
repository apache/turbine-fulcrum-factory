package org.apache.fulcrum.factory;

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


import java.util.ArrayList;

import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * @author Eric Pugh
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 *
 */
public class FactoryServiceTest extends BaseUnitTest
{
    private FactoryService factoryService = null;
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public FactoryServiceTest(String name)
    {
        super(name);
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        factoryService = (FactoryService) this.resolve( FactoryService.class.getName() );

    }
    /*
     * Class to test for Object getInstance(String)
     */
    public void testGetInstanceString() throws Exception
    {
        Object object = factoryService.getInstance("java.lang.StringBuilder");
        assertTrue(object instanceof StringBuilder);
    }
    /*
     * Class to test for Object getInstance(String, ClassLoader)
     */
    public void testGetInstanceStringClassLoader() throws Exception
    {
        Object object = factoryService.getInstance("java.lang.StringBuilder", StringBuilder.class.getClassLoader());
        assertTrue(object instanceof StringBuilder);
    }
    /*
     * Class to test for Object getInstance(String, Object[], String[])
     */
    public void testGetInstanceStringObjectArrayStringArray() throws Exception
    {
        Object params[] = new Object[1];
        String sourceValue = "testing";
        params[0] = sourceValue;
        String signature[] = new String[1];
        signature[0] = "java.lang.String";
        Object object = factoryService.getInstance("java.lang.StringBuilder", params, signature);
        assertTrue(object instanceof StringBuilder);
        assertEquals(sourceValue, object.toString());

    }
    /*
     * Class to test for Object getInstance(String, ClassLoader, Object[], String[])
     */
    public void testGetInstanceStringClassLoaderObjectArrayStringArray() throws Exception
    {
        Object params[] = new Object[1];
        String sourceValu = "testing";
        params[0] = sourceValu;
        String signature[] = new String[1];
        signature[0] = "java.lang.String";
        Object object =
            factoryService.getInstance(
                "java.lang.StringBuilder",
                StringBuilder.class.getClassLoader(),
                params,
                signature);
        assertTrue(object instanceof StringBuilder);
        assertEquals(sourceValu, object.toString());

    }
    
    /**
     * Test if the loader is supported
     * 
     * @throws Exception Generic exception
     */
    public void testIsLoaderSupported() throws Exception
    {
        // TODO Need to run a test where the loader is NOT supported.
        assertTrue(factoryService.isLoaderSupported("java.lang.String"));
    }
    
    public void testGetSignature() throws Exception
    {
        Object params[] = new Object[1];
        String sourceValu = "testing";
        params[0] = sourceValu;
        String signature[] = new String[1];
        signature[0] = "java.lang.String";
        Class<?>[] results = factoryService.getSignature(StringBuilder.class, params, signature);
        assertEquals(1, results.length);
        assertTrue(results[0].equals(String.class));

        Integer sourceValueInteger = new Integer(10);
        params[0] = sourceValueInteger;
        signature[0] = "java.lang.Integer";
        results = factoryService.getSignature(ArrayList.class, params, signature);
        assertEquals(1, results.length);
        assertTrue("Result:" + results[0].getName(),results[0].equals(Integer.class));
    }
}
