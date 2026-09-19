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
import com.liferay.portal.kernel.dao.orm.ScrollableResults;
import com.liferay.portal.kernel.util.ListUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.LockOptions;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class QueryImpl implements Query {

	public QueryImpl(org.hibernate.Query query, boolean strictName) {
		_query = query;
		_strictName = strictName;

		String[] names = null;

		if (!_strictName) {
			names = query.getNamedParameters();

			Arrays.sort(names);
		}

		_names = names;
	}

	@Override
	public int executeUpdate() throws ORMException {
		try {
			return _query.executeUpdate();
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public Iterator<?> iterate() throws ORMException {
		return iterate(true);
	}

	@Override
	public Iterator<?> iterate(boolean unmodifiable) throws ORMException {
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
			List<?> list = _query.list();

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
			return new ScrollableResultsImpl(_query.scroll());
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public Query setBigDecimal(int pos, BigDecimal value) {
		_query.setBigDecimal(pos, value);

		return this;
	}

	@Override
	public Query setBigDecimal(String name, BigDecimal value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_query.setBigDecimal(name, value);

		return this;
	}

	@Override
	public Query setBoolean(int pos, boolean value) {
		_query.setBoolean(pos, value);

		return this;
	}

	@Override
	public Query setBoolean(String name, boolean value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_query.setBoolean(name, value);

		return this;
	}

	@Override
	public Query setCacheMode(CacheMode cacheMode) {
		_query.setCacheMode(CacheModeTranslator.translate(cacheMode));

		return this;
	}

	@Override
	public Query setCacheRegion(String cacheRegion) {
		_query.setCacheRegion(cacheRegion);

		return this;
	}

	@Override
	public Query setCacheable(boolean cacheable) {
		_query.setCacheable(cacheable);

		return this;
	}

	@Override
	public Query setDouble(int pos, double value) {
		_query.setDouble(pos, value);

		return this;
	}

	@Override
	public Query setDouble(String name, double value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_query.setDouble(name, value);

		return this;
	}

	@Override
	public Query setFirstResult(int firstResult) {
		_query.setFirstResult(firstResult);

		return this;
	}

	@Override
	public Query setFloat(int pos, float value) {
		_query.setFloat(pos, value);

		return this;
	}

	@Override
	public Query setFloat(String name, float value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_query.setFloat(name, value);

		return this;
	}

	@Override
	public Query setInteger(int pos, int value) {
		_query.setInteger(pos, value);

		return this;
	}

	@Override
	public Query setInteger(String name, int value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_query.setInteger(name, value);

		return this;
	}

	@Override
	public Query setLockMode(String alias, LockMode lockMode) {
		org.hibernate.LockMode hibernateLockMode = LockModeTranslator.translate(
			lockMode);

		LockOptions lockOptions = new LockOptions(hibernateLockMode);

		lockOptions.setAliasSpecificLockMode(alias, hibernateLockMode);

		_query.setLockOptions(lockOptions);

		return this;
	}

	@Override
	public Query setLong(int pos, long value) {
		_query.setLong(pos, value);

		return this;
	}

	@Override
	public Query setLong(String name, long value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_query.setLong(name, value);

		return this;
	}

	@Override
	public Query setMaxResults(int maxResults) {
		_query.setMaxResults(maxResults);

		return this;
	}

	@Override
	public Query setSerializable(int pos, Serializable value) {
		_query.setSerializable(pos, value);

		return this;
	}

	@Override
	public Query setSerializable(String name, Serializable value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_query.setSerializable(name, value);

		return this;
	}

	@Override
	public Query setShort(int pos, short value) {
		_query.setShort(pos, value);

		return this;
	}

	@Override
	public Query setShort(String name, short value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_query.setShort(name, value);

		return this;
	}

	@Override
	public Query setString(int pos, String value) {
		_query.setString(pos, value);

		return this;
	}

	@Override
	public Query setString(String name, String value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_query.setString(name, value);

		return this;
	}

	@Override
	public Query setTimestamp(int pos, Timestamp value) {
		_query.setTimestamp(pos, value);

		return this;
	}

	@Override
	public Query setTimestamp(String name, Timestamp value) {
		if (!_strictName && (Arrays.binarySearch(_names, name) < 0)) {
			return this;
		}

		_query.setTimestamp(name, value);

		return this;
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{names=", Arrays.toString(_names), ", _query=", _query,
			", _strictName=", _strictName, "}");
	}

	@Override
	public Object uniqueResult() throws ORMException {
		try {
			return _query.uniqueResult();
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	private final String[] _names;
	private final org.hibernate.Query _query;
	private final boolean _strictName;

}