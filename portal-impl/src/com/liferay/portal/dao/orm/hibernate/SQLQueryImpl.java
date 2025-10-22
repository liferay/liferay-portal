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

import jakarta.persistence.Parameter;

import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.query.NativeQuery;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class SQLQueryImpl implements SQLQuery {

	public SQLQueryImpl(NativeQuery nativeQuery, boolean strictName) {
		_nativeQuery = nativeQuery;
		_strictName = strictName;

		String[] names = null;

		if (!_strictName) {
			Set<Parameter<?>> parameters = nativeQuery.getParameters();

			List<String> nameList = new ArrayList<>();

			for (Parameter<?> parameter : parameters) {
				nameList.add(parameter.getName());
			}

			names = nameList.toArray(new String[0]);

			Arrays.sort(names);
		}

		_names = names;
	}

	@Override
	public SQLQuery addEntity(String alias, Class<?> entityClass) {
		_nativeQuery.addEntity(alias, entityClass);

		return this;
	}

	@Override
	public SQLQuery addScalar(String columnAlias, Type type) {
		_nativeQuery.addScalar(columnAlias, TypeTranslator.translate(type));

		return this;
	}

	@Override
	public SQLQuery addSynchronizedEntityClass(Class<?> entityClass) {
		_nativeQuery.addSynchronizedEntityClass(entityClass);

		return this;
	}

	@Override
	public SQLQuery addSynchronizedEntityClasses(Class<?>... entityClasses) {
		for (Class<?> entityClass : entityClasses) {
			_nativeQuery.addSynchronizedEntityClass(entityClass);
		}

		return this;
	}

	@Override
	public SQLQuery addSynchronizedEntityName(String entityName) {
		_nativeQuery.addSynchronizedEntityName(entityName);

		return this;
	}

	@Override
	public SQLQuery addSynchronizedEntityNames(String... entityNames) {
		for (String entityName : entityNames) {
			_nativeQuery.addSynchronizedEntityName(entityName);
		}

		return this;
	}

	@Override
	public SQLQuery addSynchronizedQuerySpace(String querySpace) {
		_nativeQuery.addSynchronizedQuerySpace(querySpace);

		return this;
	}

	@Override
	public SQLQuery addSynchronizedQuerySpaces(String... querySpaces) {
		for (String querySpace : querySpaces) {
			_nativeQuery.addSynchronizedQuerySpace(querySpace);
		}

		return this;
	}

	@Override
	public int executeUpdate() throws ORMException {
		try {
			return _nativeQuery.executeUpdate();
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
			List<?> list = _nativeQuery.list();

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
			return new ScrollableResultsImpl(_nativeQuery.scroll());
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public Query setBigDecimal(int pos, BigDecimal value) {
		_nativeQuery.setParameter(pos, value, BigDecimal.class);

		return this;
	}

	@Override
	public Query setBigDecimal(String name, BigDecimal value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_nativeQuery.setParameter(name, value, BigDecimal.class);

		return this;
	}

	@Override
	public Query setBoolean(int pos, boolean value) {
		_nativeQuery.setParameter(pos, value, Boolean.class);

		return this;
	}

	@Override
	public Query setBoolean(String name, boolean value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_nativeQuery.setParameter(name, value, Boolean.class);

		return this;
	}

	@Override
	public Query setCacheable(boolean cacheable) {
		_nativeQuery.setCacheable(cacheable);

		return this;
	}

	@Override
	public Query setCacheMode(CacheMode cacheMode) {
		_nativeQuery.setCacheMode(CacheModeTranslator.translate(cacheMode));

		return this;
	}

	@Override
	public Query setCacheRegion(String cacheRegion) {
		_nativeQuery.setCacheRegion(cacheRegion);

		return this;
	}

	@Override
	public Query setDouble(int pos, double value) {
		_nativeQuery.setParameter(pos, value, Double.class);

		return this;
	}

	@Override
	public Query setDouble(String name, double value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_nativeQuery.setParameter(name, value, Double.class);

		return this;
	}

	@Override
	public Query setFirstResult(int firstResult) {
		_nativeQuery.setFirstResult(firstResult);

		return this;
	}

	@Override
	public Query setFloat(int pos, float value) {
		_nativeQuery.setParameter(pos, value, Float.class);

		return this;
	}

	@Override
	public Query setFloat(String name, float value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_nativeQuery.setParameter(name, value, Float.class);

		return this;
	}

	@Override
	public Query setInteger(int pos, int value) {
		_nativeQuery.setParameter(pos, value, Integer.class);

		return this;
	}

	@Override
	public Query setInteger(String name, int value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_nativeQuery.setParameter(name, value, Integer.class);

		return this;
	}

	@Override
	public Query setLockMode(String alias, LockMode lockMode) {
		_nativeQuery.setLockMode(alias, LockModeTranslator.translate(lockMode));

		return this;
	}

	@Override
	public Query setLong(int pos, long value) {
		_nativeQuery.setParameter(pos, value, Long.class);

		return this;
	}

	@Override
	public Query setLong(String name, long value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_nativeQuery.setParameter(name, value, Long.class);

		return this;
	}

	@Override
	public Query setMaxResults(int maxResults) {
		_nativeQuery.setMaxResults(maxResults);

		return this;
	}

	@Override
	public Query setSerializable(int pos, Serializable value) {
		_nativeQuery.setParameter(pos, value, Serializable.class);

		return this;
	}

	@Override
	public Query setSerializable(String name, Serializable value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_nativeQuery.setParameter(name, value, Serializable.class);

		return this;
	}

	@Override
	public Query setShort(int pos, short value) {
		_nativeQuery.setParameter(pos, value, Short.class);

		return this;
	}

	@Override
	public Query setShort(String name, short value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_nativeQuery.setParameter(name, value, Short.class);

		return this;
	}

	@Override
	public Query setString(int pos, String value) {
		_nativeQuery.setParameter(pos, value, String.class);

		return this;
	}

	@Override
	public Query setString(String name, String value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_nativeQuery.setParameter(name, value, String.class);

		return this;
	}

	@Override
	public Query setTimestamp(int pos, Timestamp value) {
		_nativeQuery.setParameter(pos, value, Timestamp.class);

		return this;
	}

	@Override
	public Query setTimestamp(String name, Timestamp value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_nativeQuery.setParameter(name, value, Timestamp.class);

		return this;
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{names=", Arrays.toString(_names), ", _nativeQuery=", _nativeQuery,
			", _strictName=", _strictName, "}");
	}

	@Override
	public Object uniqueResult() throws ORMException {
		try {
			return _nativeQuery.uniqueResult();
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	private final String[] _names;
	private final NativeQuery _nativeQuery;
	private final boolean _strictName;

}