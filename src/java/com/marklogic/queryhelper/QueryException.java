/*
 * Copyright 2004 Mark Logic Corporation. All Rights Reserved.
 */
package com.marklogic.queryhelper;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: May 30, 2004
 * Time: 12:23:04 AM
 */
public class QueryException extends RuntimeException
{
	public QueryException() { }

	public QueryException(String msg) { super(msg); }
}
