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

/**
 * Exception thrown when there is a problem with the FactoryService
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class FactoryException extends Exception {
	/**
	 * Serial number
	 */
	private static final long serialVersionUID = 8954422192583295720L;

	/**
	 * Default constructor
	 */
	public FactoryException() 
	{
		super();
	}

	/**
	 * {@link java.lang.Exception#Exception(String, Throwable)}
	 * 
	 * @param message the message
	 * @param e       the exception
	 */
	public FactoryException(String message, Throwable e) 
	{
		super(message, e);
	}

	/**
	 * {@link java.lang.Exception#Exception(Throwable)}
	 * 
	 * @param e the exception to bubble up
	 */
	public FactoryException(Throwable e) 
	{
		super(e);
	}

	/**
	 * {@link java.lang.Exception#Exception(String)}
	 * 
	 * @param msg the message to bubble up
	 */
	public FactoryException(String msg) 
	{
		super(msg);
	}
}
