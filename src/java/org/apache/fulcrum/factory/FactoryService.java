package org.apache.fulcrum.factory;

import org.apache.avalon.framework.service.ServiceException;


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



/**
 * The Factory Service instantiates objects using either default
 * class loaders or a specified one. Whether specified class
 * loaders are supported for a class depends on implementation
 * and can be tested with the isLoaderSupported method.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public interface FactoryService
{
    /**
     * The key under which this component is known by an avalon container.
     */
    String ROLE = FactoryService.class.getName();


	/**
	* Gets an instance of a class.
	*
	* @param clazz the name of the class.
	* @return the instance.
	* @throws ServiceException if instantiation fails.
	*/
   <T> T getInstance(Class<T> clazz)
	   throws FactoryException;

    /**
     * Gets an instance of a named class.
     *
     * @param className the name of the class.
     * @return the instance.
     * @throws ServiceException if instantiation fails.
     */
    <T> T getInstance(String className)
        throws FactoryException;

    /**
     * Gets an instance of a named class using a specified class loader.
     *
     * <p>Class loaders are supported only if the isLoaderSupported
     * method returns true. Otherwise the loader parameter is ignored.
     *
     * @param className the name of the class.
     * @param loader the class loader.
     * @return the instance.
     * @throws ServiceException if instantiation fails.
     */
    <T> T getInstance(String className,
                              ClassLoader loader)
        throws FactoryException;

    /**
     * Gets an instance of a named class.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * @param className the name of the class.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws ServiceException if instantiation fails.
     */
    <T> T getInstance(String className,
                              Object[] params,
                              String[] signature)
        throws FactoryException;

    /**
     * Gets an instance of a named class using a specified class loader.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * <p>Class loaders are supported only if the isLoaderSupported
     * method returns true. Otherwise the loader parameter is ignored.
     *
     * @param className the name of the class.
     * @param loader the class loader.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws ServiceException if instantiation fails.
     */
    <T> T getInstance(String className,
                              ClassLoader loader,
                              Object[] params,
                              String[] signature)
        throws FactoryException;

    /**
     * Tests if specified class loaders are supported for a named class.
     *
     * @param className the name of the class.
     * @return true if class loaders are supported, false otherwise.
     * @throws ServiceException if test fails.
     */
    boolean isLoaderSupported(String className)
        throws FactoryException;

    /**
     * Gets the signature classes for parameters of a method of a class.
     *
     * @param clazz the class.
     * @param params an array containing the parameters of the method.
     * @param signature an array containing the signature of the method.
     * @return an array of signature classes. Note that in some cases
     * objects in the parameter array can be switched to the context
     * of a different class loader.
     * @throws ClassNotFoundException if any of the classes is not found.
     */
    Class<?>[] getSignature(Class<?> clazz,
            Object params[],
            String signature[])
            throws ClassNotFoundException;
}
