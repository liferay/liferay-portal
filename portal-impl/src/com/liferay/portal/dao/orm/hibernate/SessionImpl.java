/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.spi.ast.DefaultASTNodeListener;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.orm.LockMode;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;

import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.metamodel.spi.MappingMetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class SessionImpl implements Session {

	public SessionImpl(
		org.hibernate.Session session, ClassLoader sessionFactoryClassLoader) {

		_session = session;
		_sessionFactoryClassLoader = sessionFactoryClassLoader;
	}

	@Override
	public void apply(UnsafeConsumer<Connection, SQLException> unsafeConsumer)
		throws ORMException {

		try {
			_session.doWork(unsafeConsumer::accept);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public void clear() throws ORMException {
		try {
			_session.clear();
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public Connection close() throws ORMException {
		try {
			_session.close();

			return null;
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public boolean contains(Object object) throws ORMException {
		try {
			SessionImplementor sessionImplementor =
				(SessionImplementor)_session;

			SessionFactoryImplementor sessionFactoryImplementor =
				sessionImplementor.getSessionFactory();

			MappingMetamodelImplementor mappingMetamodelImplementor =
				sessionFactoryImplementor.getMappingMetamodel();

			EntityPersister entityPersister =
				mappingMetamodelImplementor.findEntityDescriptor(
					object.getClass());

			if (entityPersister == null) {
				return false;
			}

			return _session.contains(object);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public Query createQuery(String queryString) throws ORMException {
		return createQuery(queryString, true);
	}

	@Override
	public Query createQuery(String queryString, boolean strictName)
		throws ORMException {

		if (_sessionFactoryClassLoader == null) {
			return _createQuery(queryString, strictName);
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				_sessionFactoryClassLoader)) {

			return _createQuery(queryString, strictName);
		}
	}

	@Override
	public SQLQuery createSQLQuery(String queryString) throws ORMException {
		return createSQLQuery(queryString, true);
	}

	@Override
	public SQLQuery createSQLQuery(String queryString, boolean strictName)
		throws ORMException {

		try {
			queryString = SQLTransformer.transformFromJPQLToHQL(queryString);

			return new SQLQueryImpl(
				_session.createNativeQuery(queryString, (Class<?>)null),
				strictName);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public SQLQuery createSynchronizedSQLQuery(DSLQuery dslQuery)
		throws ORMException {

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		SQLQuery sqlQuery = createSynchronizedSQLQuery(
			dslQuery.toSQL(defaultASTNodeListener), true,
			defaultASTNodeListener.getTableNames());

		List<Object> scalarValues = defaultASTNodeListener.getScalarValues();

		if (!scalarValues.isEmpty()) {
			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			for (Object value : scalarValues) {
				queryPos.add(value);
			}
		}

		return sqlQuery;
	}

	@Override
	public SQLQuery createSynchronizedSQLQuery(String queryString)
		throws ORMException {

		return createSynchronizedSQLQuery(queryString, true);
	}

	@Override
	public SQLQuery createSynchronizedSQLQuery(
			String queryString, boolean strictName)
		throws ORMException {

		return createSynchronizedSQLQuery(
			queryString, strictName,
			SQLQueryTableNamesUtil.getTableNames(queryString));
	}

	@Override
	public SQLQuery createSynchronizedSQLQuery(
			String queryString, boolean strictName, String[] tableNames)
		throws ORMException {

		try {
			queryString = SQLTransformer.transformFromJPQLToHQL(queryString);

			SQLQuery sqlQuery = new SQLQueryImpl(
				_session.createNativeQuery(queryString, (Class<?>)null),
				strictName);

			sqlQuery.addSynchronizedQuerySpaces(tableNames);

			return sqlQuery;
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public void delete(Object object) throws ORMException {
		try {
			_session.remove(object);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public void evict(Class<?> clazz, Serializable id) throws ORMException {
		try {
			EventSource eventSource = (EventSource)_session;

			PersistenceContext persistenceContext =
				eventSource.getPersistenceContext();

			SessionFactoryImplementor sessionFactoryImplementor =
				eventSource.getFactory();

			MappingMetamodelImplementor mappingMetamodelImplementor =
				sessionFactoryImplementor.getMappingMetamodel();

			EntityPersister entityPersister =
				mappingMetamodelImplementor.findEntityDescriptor(clazz);

			Object object = persistenceContext.getEntity(
				new EntityKey(id, entityPersister));

			if (object == null) {
				return;
			}

			eventSource.evict(object);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public void evict(Object object) throws ORMException {
		try {
			_session.evict(object);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public void flush() throws ORMException {
		try {
			_session.flush();
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public Object get(Class<?> clazz, Serializable id) throws ORMException {
		try {
			return _session.find(clazz, id);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public Object get(Class<?> clazz, Serializable id, LockMode lockMode)
		throws ORMException {

		org.hibernate.LockMode hibernateLockMode = LockModeTranslator.translate(
			lockMode);

		try {
			return _session.find(clazz, id, hibernateLockMode.toJpaLockMode());
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public Object getWrappedSession() {
		return _session;
	}

	@Override
	public boolean isDirty() throws ORMException {
		try {
			return _session.isDirty();
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public void load(Class<?> clazz, Serializable id) throws ORMException {
		try {
			_session.load(clazz, id);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public Object merge(Object object) throws ORMException {
		try {
			return _session.merge(object);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception, _session, object);
		}
	}

	@Override
	public void save(Object object) throws ORMException {
		try {
			_session.persist(object);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	@Override
	public void saveOrUpdate(Object object) throws ORMException {
		try {
			_session.merge(object);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception, _session, object);
		}
	}

	@Override
	public String toString() {
		return StringBundler.concat("{_session=", _session, "}");
	}

	private Query _createQuery(String queryString, boolean strictName)
		throws ORMException {

		try {
			queryString = SQLTransformer.transformFromJPQLToHQL(queryString);

			return new QueryImpl(
				_session.createQuery(queryString, null), strictName);
		}
		catch (Exception exception) {
			throw ExceptionTranslator.translate(exception);
		}
	}

	private final org.hibernate.Session _session;
	private final ClassLoader _sessionFactoryClassLoader;

}