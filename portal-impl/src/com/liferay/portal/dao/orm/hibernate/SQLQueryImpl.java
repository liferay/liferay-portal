/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.CacheMode;
import com.liferay.portal.kernel.dao.orm.LockMode;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.ScrollableResults;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.util.ListUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class SQLQueryImpl implements SQLQuery {

	public SQLQueryImpl(org.hibernate.SQLQuery sqlQuery, boolean strictName) {
		_sqlQuery = sqlQuery;
		_strictName = strictName;

		String[] names = null;

		if (!_strictName) {
			names = sqlQuery.getNamedParameters();

			Arrays.sort(names);
		}

		_names = names;
	}

	@Override
	public SQLQuery addEntity(String alias, Class<?> entityClass) {
		_sqlQuery.addEntity(alias, entityClass);

		return this;
	}

	@Override
	public SQLQuery addScalar(String columnAlias, Type type) {
		_sqlQuery.addScalar(columnAlias, TypeTranslator.translate(type));

		return this;
	}

	@Override
	public SQLQuery addSynchronizedEntityClass(Class<?> entityClass) {
		_sqlQuery.addSynchronizedEntityClass(entityClass);

		return this;
	}

	@Override
	public SQLQuery addSynchronizedEntityClasses(Class<?>... entityClasses) {
		for (Class<?> entityClass : entityClasses) {
			_sqlQuery.addSynchronizedEntityClass(entityClass);
		}

		return this;
	}

	@Override
	public SQLQuery addSynchronizedEntityName(String entityName) {
		_sqlQuery.addSynchronizedEntityName(entityName);

		return this;
	}

	@Override
	public SQLQuery addSynchronizedEntityNames(String... entityNames) {
		for (String entityName : entityNames) {
			_sqlQuery.addSynchronizedEntityName(entityName);
		}

		return this;
	}

	@Override
	public SQLQuery addSynchronizedQuerySpace(String querySpace) {
		_sqlQuery.addSynchronizedQuerySpace(querySpace);

		return this;
	}

	@Override
	public SQLQuery addSynchronizedQuerySpaces(String... querySpaces) {
		for (String querySpace : querySpaces) {
			_sqlQuery.addSynchronizedQuerySpace(querySpace);
		}

		return this;
	}

	@Override
	public int executeUpdate() throws ORMException {
		try {
			return _sqlQuery.executeUpdate();
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Iterator iterate() throws ORMException {
		return iterate(true);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Iterator iterate(boolean unmodifiable) throws ORMException {
		try {
			List<?> list = list(unmodifiable);

			return list.iterator();
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public Object iterateNext() throws ORMException {
		Iterator<?> iterator = iterate(false);

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}

	@Override
	public List<?> list() throws ORMException {
		return list(false, false);
	}

	@Override
	public List<?> list(boolean unmodifiable) throws ORMException {
		return list(true, unmodifiable);
	}

	@Override
	public List<?> list(boolean copy, boolean unmodifiable)
		throws ORMException {

		try {
			List<?> list = _sqlQuery.list();

			if (unmodifiable) {
				list = Collections.unmodifiableList(list);
			}
			else if (copy) {
				list = ListUtil.copy(list);
			}

			return list;
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public ScrollableResults scroll() throws ORMException {
		try {
			return new ScrollableResultsImpl(_sqlQuery.scroll());
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public Query setBigDecimal(int pos, BigDecimal value) {
		_sqlQuery.setBigDecimal(pos, value);

		return this;
	}

	@Override
	public Query setBigDecimal(String name, BigDecimal value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_sqlQuery.setBigDecimal(name, value);

		return this;
	}

	@Override
	public Query setBoolean(int pos, boolean value) {
		_sqlQuery.setBoolean(pos, value);

		return this;
	}

	@Override
	public Query setBoolean(String name, boolean value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_sqlQuery.setBoolean(name, value);

		return this;
	}

	@Override
	public Query setCacheMode(CacheMode cacheMode) {
		_sqlQuery.setCacheMode(CacheModeTranslator.translate(cacheMode));

		return this;
	}

	@Override
	public Query setCacheRegion(String cacheRegion) {
		_sqlQuery.setCacheRegion(cacheRegion);

		return this;
	}

	@Override
	public Query setCacheable(boolean cacheable) {
		_sqlQuery.setCacheable(cacheable);

		return this;
	}

	@Override
	public Query setDouble(int pos, double value) {
		_sqlQuery.setDouble(pos, value);

		return this;
	}

	@Override
	public Query setDouble(String name, double value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_sqlQuery.setDouble(name, value);

		return this;
	}

	@Override
	public Query setFirstResult(int firstResult) {
		_sqlQuery.setFirstResult(firstResult);

		return this;
	}

	@Override
	public Query setFloat(int pos, float value) {
		_sqlQuery.setFloat(pos, value);

		return this;
	}

	@Override
	public Query setFloat(String name, float value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_sqlQuery.setFloat(name, value);

		return this;
	}

	@Override
	public Query setInteger(int pos, int value) {
		_sqlQuery.setInteger(pos, value);

		return this;
	}

	@Override
	public Query setInteger(String name, int value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_sqlQuery.setInteger(name, value);

		return this;
	}

	@Override
	public Query setLockMode(String alias, LockMode lockMode) {
		_sqlQuery.setLockMode(alias, LockModeTranslator.translate(lockMode));

		return this;
	}

	@Override
	public Query setLong(int pos, long value) {
		_sqlQuery.setLong(pos, value);

		return this;
	}

	@Override
	public Query setLong(String name, long value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_sqlQuery.setLong(name, value);

		return this;
	}

	@Override
	public Query setMaxResults(int maxResults) {
		_sqlQuery.setMaxResults(maxResults);

		return this;
	}

	@Override
	public Query setSerializable(int pos, Serializable value) {
		_sqlQuery.setSerializable(pos, value);

		return this;
	}

	@Override
	public Query setSerializable(String name, Serializable value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_sqlQuery.setSerializable(name, value);

		return this;
	}

	@Override
	public Query setShort(int pos, short value) {
		_sqlQuery.setShort(pos, value);

		return this;
	}

	@Override
	public Query setShort(String name, short value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_sqlQuery.setShort(name, value);

		return this;
	}

	@Override
	public Query setString(int pos, String value) {
		_sqlQuery.setString(pos, value);

		return this;
	}

	@Override
	public Query setString(String name, String value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_sqlQuery.setString(name, value);

		return this;
	}

	@Override
	public Query setTimestamp(int pos, Timestamp value) {
		_sqlQuery.setTimestamp(pos, value);

		return this;
	}

	@Override
	public Query setTimestamp(String name, Timestamp value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_sqlQuery.setTimestamp(name, value);

		return this;
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{names=", Arrays.toString(_names), ", _sqlQuery=", _sqlQuery,
			", _strictName=", _strictName, "}");
	}

	@Override
	public Object uniqueResult() throws ORMException {
		try {
			return _sqlQuery.uniqueResult();
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	private final String[] _names;
	private final org.hibernate.SQLQuery _sqlQuery;
	private final boolean _strictName;

}