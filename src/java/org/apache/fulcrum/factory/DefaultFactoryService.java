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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.fulcrum.factory.utils.ObjectInputStreamForContext;

/**
 * The Factory Service instantiates objects using specified
 * class loaders. If none is specified, the default one
 * will be used.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Id$
 *
 * @avalon.component name="factory" lifestyle="singleton"
 * @avalon.service type="org.apache.fulcrum.factory.FactoryService"
 */
public class DefaultFactoryService
    extends AbstractLogEnabled
    implements FactoryService, Configurable, Initializable
{
    protected boolean initialized = false;
    //private boolean disposed = false;
    /**
     * The property specifying a set of additional class loaders.
     */
    private static final String CLASS_LOADER = "classloader";
    /**
     * The property prefix specifying additional object factories.
     */
    private static final String OBJECT_FACTORY = "object-factory";
    /**
     * The name of the default factory.
     */
    protected static final String DEFAULT_FACTORY = "default";
    /**
     * Primitive classes for reflection of constructors.
     */
    private static HashMap primitiveClasses;
    {
        primitiveClasses = new HashMap(8);
        primitiveClasses.put(Boolean.TYPE.toString(), Boolean.TYPE);
        primitiveClasses.put(Character.TYPE.toString(), Character.TYPE);
        primitiveClasses.put(Byte.TYPE.toString(), Byte.TYPE);
        primitiveClasses.put(Short.TYPE.toString(), Short.TYPE);
        primitiveClasses.put(Integer.TYPE.toString(), Integer.TYPE);
        primitiveClasses.put(Long.TYPE.toString(), Long.TYPE);
        primitiveClasses.put(Float.TYPE.toString(), Float.TYPE);
        primitiveClasses.put(Double.TYPE.toString(), Double.TYPE);
    }
    /**
     * temporary storage of class names between configure and initialize
     */
    private String[] loaderNames;
    /**
     * Additional class loaders.
     */
    private ArrayList classLoaders = new ArrayList();
    /**
     * Customized object factories.
     */
    private HashMap objectFactories = new HashMap();
    /**
     * Gets the class of a primitive type.
     *
     * @param type a primitive type.
     * @return the corresponding class, or null.
     */
    protected static Class getPrimitiveClass(String type)
    {
        return (Class) primitiveClasses.get(type);
    }

    /**
     * Gets an instance of a named class.
     *
     * @param className the name of the class.
     * @return the instance.
     * @throws FactoryException if instantiation fails.
     */
    public Object getInstance(String className) throws FactoryException
    {
        if (className == null)
        {
            throw new FactoryException("Missing String className");
        }
        Factory factory = getFactory(className);
        if (factory == null)
        {
            Class clazz;
            try
            {
                clazz = loadClass(className);
            }
            catch (ClassNotFoundException x)
            {
                throw new FactoryException("Instantiation failed for class " + className, x);
            }
            return getInstance(clazz);
        }
        else
        {
            return factory.getInstance();
        }
    }
    /**
     * Gets an instance of a named class using a specified class loader.
     *
     * <p>Class loaders are supported only if the isLoaderSupported
     * method returns true. Otherwise the loader parameter is ignored.
     *
     * @param className the name of the class.
     * @param loader the class loader.
     * @return the instance.
     * @throws FactoryException if instantiation fails.
     */
    public Object getInstance(String className, ClassLoader loader) throws FactoryException
    {
        Factory factory = getFactory(className);
        if (factory == null)
        {
            if (loader != null)
            {
                Class clazz;
                try
                {
                    clazz = loadClass(className, loader);
                }
                catch (ClassNotFoundException x)
                {
                    throw new FactoryException("Instantiation failed for class " + className, x);
                }
                return getInstance(clazz);
            }
            else
            {
                return getInstance(className);
            }
        }
        else
        {
            return factory.getInstance(loader);
        }
    }
    /**
     * Gets an instance of a named class.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * @param className the name of the class.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws FactoryException if instantiation fails.
     */
    public Object getInstance(String className, Object[] params, String[] signature) throws FactoryException
    {
        Factory factory = getFactory(className);
        if (factory == null)
        {
            Class clazz;
            try
            {
                clazz = loadClass(className);
            }
            catch (ClassNotFoundException x)
            {
                throw new FactoryException("Instantiation failed for class " + className, x);
            }
            return getInstance(clazz, params, signature);
        }
        else
        {
            return factory.getInstance(params, signature);
        }
    }
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
     * @throws FactoryException if instantiation fails.
     */
    public Object getInstance(String className, ClassLoader loader, Object[] params, String[] signature)
        throws FactoryException
    {
        Factory factory = getFactory(className);
        if (factory == null)
        {
            if (loader != null)
            {
                Class clazz;
                try
                {
                    clazz = loadClass(className, loader);
                }
                catch (ClassNotFoundException x)
                {
                    throw new FactoryException("Instantiation failed for class " + className, x);
                }
                return getInstance(clazz, params, signature);
            }
            else
            {
                return getInstance(className, params, signature);
            }
        }
        else
        {
            return factory.getInstance(loader, params, signature);
        }
    }
    /**
     * Tests if specified class loaders are supported for a named class.
     *
     * @param className the name of the class.
     * @return true if class loaders are supported, false otherwise.
     * @throws FactoryException if test fails.
     */
    public boolean isLoaderSupported(String className) throws FactoryException
    {
        Factory factory = getFactory(className);
        return factory != null ? factory.isLoaderSupported() : true;
    }
    /**
     * Gets an instance of a specified class.
     *
     * @param clazz the class.
     * @return the instance.
     * @throws FactoryException if instantiation fails.
     */
    public Object getInstance(Class clazz) throws FactoryException
    {
        try
        {
            return clazz.newInstance();
        }
        catch (Exception x)
        {
            throw new FactoryException("Instantiation failed for " + clazz.getName(), x);
        }
    }
    /**
     * Gets an instance of a specified class.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * @param clazz the class.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws FactoryException if instantiation fails.
     */
    protected Object getInstance(Class clazz, Object params[], String signature[]) throws FactoryException
    {
        /* Try to construct. */
        try
        {
            Class[] sign = getSignature(clazz, params, signature);
            return clazz.getConstructor(sign).newInstance(params);
        }
        catch (Exception x)
        {
            throw new FactoryException("Instantiation failed for " + clazz.getName(), x);
        }
    }
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
    public Class[] getSignature(Class clazz, Object params[], String signature[]) throws ClassNotFoundException
    {
        if (signature != null)
        {
            /* We have parameters. */
            ClassLoader tempLoader;
            ClassLoader loader = clazz.getClassLoader();
            Class[] sign = new Class[signature.length];
            for (int i = 0; i < signature.length; i++)
            {
                /* Check primitive types. */
                sign[i] = getPrimitiveClass(signature[i]);
                if (sign[i] == null)
                {
                    /* Not a primitive one, continue building. */
                    if (loader != null)
                    {
                        /* Use the class loader of the target object. */
                        sign[i] = loader.loadClass(signature[i]);
                        tempLoader = sign[i].getClassLoader();
                        if ((params[i] != null)
                            && (tempLoader != null)
                            && !tempLoader.equals(params[i].getClass().getClassLoader()))
                        {
                            /*
                             * The class uses a different class loader,
                             * switch the parameter.
                             */
                            params[i] = switchObjectContext(params[i], loader);
                        }
                    }
                    else
                    {
                        /* Use the default class loader. */
                        sign[i] = loadClass(signature[i]);
                    }
                }
            }
            return sign;
        }
        else
        {
            return null;
        }
    }
    /**
     * Switches an object into the context of a different class loader.
     *
     * @param object an object to switch.
     * @param loader the loader of the new context.
     */
    protected Object switchObjectContext(Object object, ClassLoader loader)
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(object);
            out.flush();
        }
        catch (Exception x)
        {
            return object;
        }
        try
        {
            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            ObjectInputStreamForContext in = new ObjectInputStreamForContext(bin, loader);
            return in.readObject();
        }
        catch (Exception x)
        {
            return object;
        }
    }
    /**
     * Loads the named class using the default class loader.
     *
     * @param className the name of the class to load.
     * @return the loaded class.
     * @throws ClassNotFoundException if the class was not found.
     */
    protected Class loadClass(String className) throws ClassNotFoundException
    {
        ClassLoader loader = this.getClass().getClassLoader();
        try
        {
            return loader != null ? loader.loadClass(className) : Class.forName(className);
        }
        catch (ClassNotFoundException x)
        {
            /* Go through additional loaders. */
            for (Iterator i = classLoaders.iterator(); i.hasNext();)
            {
                try
                {
                    return ((ClassLoader) i.next()).loadClass(className);
                }
                catch (ClassNotFoundException xx)
                {
                    // continue
                }
            }
            /* Give up. */
            throw x;
        }
    }
    /**
     * Loads the named class using a specified class loader.
     *
     * @param className the name of the class to load.
     * @param loader the loader to use.
     * @return the loaded class.
     * @throws ClassNotFoundException if the class was not found.
     */
    protected Class loadClass(String className, ClassLoader loader) throws ClassNotFoundException
    {
        return loader != null ? loader.loadClass(className) : loadClass(className);
    }
    /**
     * Gets a customized factory for a named class. If no class-specific
     * factory is specified but a default factory is, will use the default
     * factory.
     *
     * @param className the name of the class to load.
     * @return the factory, or null if not specified and no default.
     * @throws FactoryException if instantiation of the factory fails.
     */
    protected Factory getFactory(String className) throws FactoryException
    {
        HashMap factories = objectFactories;
        Object factory = factories.get(className);
        if (factory == null)
        {
            //No named factory for this; try the default, if one
            //exists.
            factory = factories.get(DEFAULT_FACTORY);
        }
        if (factory != null)
        {
            if (factory instanceof String)
            {
                /* Not yet instantiated... */
                try
                {
                    factory = (Factory) getInstance((String) factory);
                    ((Factory) factory).init(className);
                }
                catch (FactoryException x)
                {
                    throw x;
                }
                catch (ClassCastException x)
                {
                    throw new FactoryException("Incorrect factory " + (String) factory + " for class " + className, x);
                }
                factories = (HashMap) factories.clone();
                factories.put(className, factory);
                objectFactories = factories;
            }
            return (Factory) factory;
        }
        else
        {
            return null;
        }
    }
    // ---------------- Avalon Lifecycle Methods ---------------------
    /**
     * Avalon component lifecycle method
     */
    public void configure(Configuration conf) throws ConfigurationException
    {
        final Configuration[] loaders = conf.getChildren(CLASS_LOADER);
        if (loaders != null)
        {
            loaderNames = new String[loaders.length];
            for (int i = 0; i < loaders.length; i++)
            {
                loaderNames[i] = loaders[i].getValue();
            }
        }
        final Configuration factories = conf.getChild(OBJECT_FACTORY, false);
        if (factories != null)
        {
            Configuration[] nameVal = factories.getChildren();
            for (int i = 0; i < nameVal.length; i++)
            {
                String key = nameVal[i].getName();
                String factory = nameVal[i].getValue();
                // Store the factory to the table as a string and
                // instantiate it by using the service when needed.
                objectFactories.put(key, factory);
            }
        }
    }
    /**
     * Avalon component lifecycle method
     * Initializes the service by loading default class loaders
     * and customized object factories.
     *
     * @throws InitializationException if initialization fails.
     */
    public void initialize() throws Exception
    {
        if (loaderNames != null)
        {
            for (int i = 0; i < loaderNames.length; i++)
            {
                try
                {
                    classLoaders.add(loadClass(loaderNames[i]).newInstance());
                }
                catch (Exception x)
                {
                    throw new Exception(
                        "No such class loader '" + loaderNames[i] + "' for DefaultFactoryService",
                        x);
                }
            }
            loaderNames = null;
        }
    }
}
