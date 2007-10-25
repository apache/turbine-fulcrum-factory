package org.apache.fulcrum.factory.utils;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

public class ObjectInputStreamForContextTest extends TestCase
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ObjectInputStreamForContextTest.class);
    }
    /*
     * Class to test for void ObjectInputStreamForContext(InputStream, ClassLoader)
     */
    public void testObjectInputStreamForContextInputStreamClassLoader() throws Exception
    {
        Object object = new String("I am testing");
        Object object2 = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(object);
        out.flush();
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStreamForContext in = new ObjectInputStreamForContext(bin, String.class.getClassLoader());
        object2 = in.readObject();
        assertEquals(object.toString(), object2.toString());
        assertEquals(object, object2);
    }
}
