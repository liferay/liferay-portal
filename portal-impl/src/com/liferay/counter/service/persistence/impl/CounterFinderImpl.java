/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.counter.service.persistence.impl;

import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.service.persistence.CounterFinder;
import com.liferay.counter.model.CounterHolder;
import com.liferay.counter.model.CounterRegister;
import com.liferay.counter.model.impl.CounterImpl;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.cache.CacheRegistryItem;
import com.liferay.portal.kernel.concurrent.CompeteLatch;
import com.liferay.portal.kernel.dao.orm.LockMode;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.ObjectNotFoundException;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

/**
 * @author Brian Wing Shun Chan
 * @author Harry Mark
 * @author Michael Young
 * @author Shuyang Zhou
 * @author Edward Han
 */
public class CounterFinderImpl implements CacheRegistryItem, CounterFinder {

	@Override
	public long getCurrentId(String name) {
		try (Connection connection = getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_SQL_SELECT_ID_BY_NAME)) {

			preparedStatement.setString(1, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong(1);
				}
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}

		return 0;
	}

	@Override
	public List<String> getNames() {
		try (Connection connection = getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_SQL_SELECT_NAMES);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			List<String> list = new ArrayList<>();

			while (resultSet.next()) {
				list.add(resultSet.getString(1));
			}

			return list;
		}
		catch (SQLException sqlException) {
			throw processException(sqlException);
		}
	}

	@Override
	public String getRegistryName() {
		return CounterFinderImpl.class.getName();
	}

	@Override
	public long increment() {
		return increment(_NAME);
	}

	@Override
	public long increment(String name) {
		return increment(name, _MINIMUM_INCREMENT_SIZE);
	}

	@Override
	public long increment(String name, int size) {
		if (size < _MINIMUM_INCREMENT_SIZE) {
			size = _MINIMUM_INCREMENT_SIZE;
		}

		return _competeIncrement(getCounterRegister(name), size);
	}

	@Override
	public void invalidate() {
		if (!DBPartition.isPartitionEnabled() ||
			(CompanyThreadLocal.getCompanyId() == CompanyConstants.SYSTEM)) {

			_counterRegisterMap.clear();

			return;
		}

		for (String key : _counterRegisterMap.keySet()) {
			if (key.endsWith(
					StringPool.AT + CompanyThreadLocal.getCompanyId())) {

				_counterRegisterMap.remove(key);
			}
		}
	}

	@Override
	public void rename(String oldName, String newName) {
		CounterRegister counterRegister = getCounterRegister(oldName);

		synchronized (counterRegister) {
			if (_counterRegisterMap.containsKey(
					DBPartitionUtil.getPartitionKey(newName))) {

				throw new SystemException(
					StringBundler.concat(
						"Cannot rename ", oldName, " to ", newName));
			}

			try (Connection connection = getConnection();
				PreparedStatement preparedStatement =
					connection.prepareStatement(_SQL_UPDATE_NAME_BY_NAME)) {

				preparedStatement.setString(1, newName);
				preparedStatement.setString(2, oldName);

				preparedStatement.executeUpdate();
			}
			catch (ObjectNotFoundException objectNotFoundException) {
				if (_log.isDebugEnabled()) {
					_log.debug(objectNotFoundException);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}

			counterRegister.setName(newName);

			_counterRegisterMap.put(
				DBPartitionUtil.getPartitionKey(newName), counterRegister);
			_counterRegisterMap.remove(
				DBPartitionUtil.getPartitionKey(oldName));
		}
	}

	@Override
	public void reset(String name) {
		CounterRegister counterRegister = getCounterRegister(name);

		synchronized (counterRegister) {
			Session session = null;

			try (Connection connection = getConnection()) {
				connection.setAutoCommit(false);

				session = _sessionFactory.openNewSession(connection);

				Counter counter = (Counter)session.get(CounterImpl.class, name);

				session.delete(counter);

				session.flush();

				connection.commit();
			}
			catch (ObjectNotFoundException objectNotFoundException) {
				if (_log.isDebugEnabled()) {
					_log.debug(objectNotFoundException);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}

			_counterRegisterMap.remove(DBPartitionUtil.getPartitionKey(name));
		}
	}

	@Override
	public void reset(String name, long size) {
		CounterRegister counterRegister = createCounterRegister(name, size);

		_counterRegisterMap.put(
			DBPartitionUtil.getPartitionKey(name), counterRegister);
	}

	protected void closeSession(Session session) throws ORMException {
		_sessionFactory.closeSession(session);
	}

	protected CounterRegister createCounterRegister(String name) {
		return createCounterRegister(name, -1);
	}

	protected CounterRegister createCounterRegister(String name, long size) {
		long rangeMin = -1;

		try (Connection connection = getConnection();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				_SQL_SELECT_ID_BY_NAME)) {

			preparedStatement1.setString(1, name);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				if (!resultSet.next()) {
					rangeMin = _DEFAULT_CURRENT_ID;

					if (size > rangeMin) {
						rangeMin = size;
					}

					try (PreparedStatement preparedStatement2 =
							connection.prepareStatement(_SQL_INSERT)) {

						preparedStatement2.setString(1, name);
						preparedStatement2.setLong(2, rangeMin);

						preparedStatement2.executeUpdate();
					}
				}
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}

		int rangeSize = getRangeSize(name);

		CounterHolder counterHolder = _obtainIncrement(name, rangeSize, size);

		return new CounterRegister(name, counterHolder, rangeSize);
	}

	protected Connection getConnection() throws SQLException {
		return _dataSource.getConnection();
	}

	protected CounterRegister getCounterRegister(String name) {
		CounterRegister counterRegister = _counterRegisterMap.get(
			DBPartitionUtil.getPartitionKey(name));

		if (counterRegister != null) {
			return counterRegister;
		}

		synchronized (_counterRegisterMap) {

			// Double check

			counterRegister = _counterRegisterMap.get(
				DBPartitionUtil.getPartitionKey(name));

			if (counterRegister == null) {
				counterRegister = createCounterRegister(name);

				_counterRegisterMap.put(
					DBPartitionUtil.getPartitionKey(name), counterRegister);
			}

			return counterRegister;
		}
	}

	protected int getRangeSize(String name) {
		if (name.equals(_NAME)) {
			return PropsValues.COUNTER_INCREMENT;
		}

		String incrementType = null;

		int pos = name.indexOf(CharPool.POUND);

		if (pos != -1) {
			incrementType = name.substring(0, pos);
		}
		else {
			incrementType = name;
		}

		Integer rangeSize = _rangeSizeMap.get(
			DBPartitionUtil.getPartitionKey(incrementType));

		if (rangeSize == null) {
			rangeSize = GetterUtil.getInteger(
				PropsUtil.get(
					PropsKeys.COUNTER_INCREMENT_PREFIX + incrementType),
				PropsValues.COUNTER_INCREMENT);

			_rangeSizeMap.put(
				DBPartitionUtil.getPartitionKey(incrementType), rangeSize);
		}

		return rangeSize.intValue();
	}

	protected SystemException processException(Exception exception) {
		if (!(exception instanceof ORMException)) {
			_log.error("Caught unexpected exception", exception);
		}
		else if (_log.isDebugEnabled()) {
			_log.debug(exception);
		}

		return new SystemException(exception);
	}

	protected void setDataSource(DataSource dataSource) {
		_dataSource = dataSource;
	}

	protected void setSessionFactory(SessionFactory sessionFactory) {
		_sessionFactory = sessionFactory;
	}

	private long _competeIncrement(CounterRegister counterRegister, int size) {
		CounterHolder counterHolder = counterRegister.getCounterHolder();

		// Try to use the fast path

		long newValue = counterHolder.addAndGet(size);

		if (newValue <= counterHolder.getRangeMax()) {
			return newValue;
		}

		// Use the slow path

		CompeteLatch competeLatch = counterRegister.getCompeteLatch();

		if (!competeLatch.compete()) {

			// Loser thread has to wait for the winner thread to finish its job

			try {
				competeLatch.await();
			}
			catch (InterruptedException interruptedException) {
				throw processException(interruptedException);
			}

			// Compete again

			return _competeIncrement(counterRegister, size);
		}

		// Winner thread

		try {

			// Double check

			counterHolder = counterRegister.getCounterHolder();

			newValue = counterHolder.addAndGet(size);

			if (newValue > counterHolder.getRangeMax()) {
				int range = counterRegister.getRangeSize();

				if (size > range) {
					range = size;
				}

				CounterHolder newCounterHolder = _obtainIncrement(
					counterRegister.getName(), range, 0);

				newValue = newCounterHolder.addAndGet(size);

				counterRegister.setCounterHolder(newCounterHolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {

			// Winner thread opens the latch so that loser threads can continue

			competeLatch.done();
		}

		return newValue;
	}

	private CounterHolder _obtainIncrement(
		String counterName, long range, long size) {

		Session session = null;

		try (Connection connection = getConnection()) {
			connection.setAutoCommit(false);

			session = _sessionFactory.openNewSession(connection);

			Counter counter = (Counter)session.get(
				CounterImpl.class, counterName, LockMode.UPGRADE);

			long newValue = counter.getCurrentId();

			if (size > newValue) {
				newValue = size;
			}

			long rangeMax = newValue + range;

			counter.setCurrentId(rangeMax);

			CounterHolder counterHolder = new CounterHolder(newValue, rangeMax);

			session.saveOrUpdate(counter);

			session.flush();

			connection.commit();

			return counterHolder;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final int _DEFAULT_CURRENT_ID = 0;

	private static final int _MINIMUM_INCREMENT_SIZE = 1;

	private static final String _NAME = Counter.class.getName();

	private static final String _SQL_INSERT =
		"insert into Counter(name, currentId) values (?, ?)";

	private static final String _SQL_SELECT_ID_BY_NAME =
		"select currentId from Counter where name = ?";

	private static final String _SQL_SELECT_NAMES =
		"select name from Counter order by name asc";

	private static final String _SQL_UPDATE_NAME_BY_NAME =
		"update Counter set name = ? where name = ?";

	private static final Log _log = LogFactoryUtil.getLog(
		CounterFinderImpl.class);

	private final Map<String, CounterRegister> _counterRegisterMap =
		new ConcurrentHashMap<>();
	private DataSource _dataSource;
	private final Map<String, Integer> _rangeSizeMap =
		new ConcurrentHashMap<>();
	private SessionFactory _sessionFactory;

}