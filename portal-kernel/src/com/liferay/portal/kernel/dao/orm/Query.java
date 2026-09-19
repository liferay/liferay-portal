/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.util.Iterator;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
@ProviderType
public interface Query {

	public int executeUpdate() throws ORMException;

	@SuppressWarnings("rawtypes")
	public Iterator iterate() throws ORMException;

	@SuppressWarnings("rawtypes")
	public Iterator iterate(boolean modifiable) throws ORMException;

	public Object iterateNext() throws ORMException;

	@SuppressWarnings("rawtypes")
	public List list() throws ORMException;

	@SuppressWarnings("rawtypes")
	public List list(boolean unmodifiable) throws ORMException;

	@SuppressWarnings("rawtypes")
	public List list(boolean copy, boolean unmodifiable) throws ORMException;

	public ScrollableResults scroll() throws ORMException;

	public Query setBigDecimal(int pos, BigDecimal value);

	public Query setBigDecimal(String name, BigDecimal value);

	public Query setBoolean(int pos, boolean value);

	public Query setBoolean(String name, boolean value);

	public Query setCacheMode(CacheMode cacheMode);

	public Query setCacheRegion(String cacheRegion);

	public Query setCacheable(boolean cacheable);

	public Query setDouble(int pos, double value);

	public Query setDouble(String name, double value);

	public Query setFirstResult(int firstResult);

	public Query setFloat(int pos, float value);

	public Query setFloat(String name, float value);

	public Query setInteger(int pos, int value);

	public Query setInteger(String name, int value);

	public Query setLockMode(String alias, LockMode lockMode);

	public Query setLong(int pos, long value);

	public Query setLong(String name, long value);

	public Query setMaxResults(int maxResults);

	public Query setSerializable(int pos, Serializable value);

	public Query setSerializable(String name, Serializable value);

	public Query setShort(int pos, short value);

	public Query setShort(String name, short value);

	public Query setString(int pos, String value);

	public Query setString(String name, String value);

	public Query setTimestamp(int pos, Timestamp value);

	public Query setTimestamp(String name, Timestamp value);

	public Object uniqueResult() throws ORMException;

}