/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.dao.orm.Dialect;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionCustomizer;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PreloadClassLoader;
import com.liferay.portal.kernel.util.PropsValues;

import java.sql.Connection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.hibernate.SessionBuilder;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.resource.jdbc.spi.LogicalConnectionImplementor;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class SessionFactoryImpl implements SessionFactory {

	@Override
	public void closeSession(Session session) throws ORMException {
		if ((session != null) &&
			!PropsValues.SPRING_HIBERNATE_SESSION_DELEGATED) {

			session.flush();
			session.close();
		}
	}

	public void destroy() {
		_sessionCustomizers.close();
	}

	@Override
	public Session getCurrentSession() throws ORMException {
		SessionFactoryImplementor sessionFactoryImplementor =
			getSessionFactoryImplementor();

		return wrapSession(sessionFactoryImplementor.getCurrentSession());
	}

	@Override
	public Dialect getDialect() throws ORMException {
		SessionFactoryImplementor sessionFactoryImplementor =
			getSessionFactoryImplementor();

		JdbcServices jdbcServices = sessionFactoryImplementor.getJdbcServices();

		return new DialectImpl(jdbcServices.getDialect());
	}

	public SessionFactoryImplementor getSessionFactoryImplementor() {
		return _sessionFactoryImplementorSupplier.get();
	}

	@Override
	public Session openNewSession(Connection connection) throws ORMException {
		SessionFactoryImplementor sessionFactoryImplementor =
			getSessionFactoryImplementor();

		SessionBuilder sessionBuilder = sessionFactoryImplementor.withOptions();

		return wrapSession(
			sessionBuilder.connection(
				connection
			).openSession());
	}

	@Override
	public Session openSession() throws ORMException {
		org.hibernate.Session session = null;

		SessionFactoryImplementor sessionFactoryImplementor =
			getSessionFactoryImplementor();

		if (PropsValues.SPRING_HIBERNATE_SESSION_DELEGATED) {
			session = sessionFactoryImplementor.getCurrentSession();
		}
		else {
			session = sessionFactoryImplementor.openSession();
		}

		if (_log.isDebugEnabled()) {
			org.hibernate.internal.SessionImpl sessionImpl =
				(org.hibernate.internal.SessionImpl)session;

			JdbcCoordinator jdbcCoordinator = sessionImpl.getJdbcCoordinator();

			LogicalConnectionImplementor logicalConnectionImplementor =
				jdbcCoordinator.getLogicalConnection();

			PhysicalConnectionHandlingMode physicalConnectionHandlingMode =
				logicalConnectionImplementor.getConnectionHandlingMode();

			_log.debug(
				"Session is using connection release mode " +
					physicalConnectionHandlingMode.getReleaseMode());
		}

		return wrapSession(session);
	}

	public void setSessionFactoryClassLoader(
		ClassLoader sessionFactoryClassLoader) {

		if (sessionFactoryClassLoader !=
				PortalClassLoaderUtil.getClassLoader()) {

			_sessionFactoryClassLoader = new PreloadClassLoader(
				sessionFactoryClassLoader, getPreloadClassLoaderClasses());
		}
	}

	public void setSessionFactoryImplementor(
		SessionFactoryImplementor sessionFactoryImplementor) {

		setSessionFactoryImplementorSupplier(() -> sessionFactoryImplementor);
	}

	public void setSessionFactoryImplementorSupplier(
		Supplier<SessionFactoryImplementor> sessionFactoryImplementorSupplier) {

		_sessionFactoryImplementorSupplier = sessionFactoryImplementorSupplier;
	}

	protected Map<String, Class<?>> getPreloadClassLoaderClasses() {
		try {
			Map<String, Class<?>> classes = new HashMap<>();

			for (String className : _PRELOAD_CLASS_NAMES) {
				ClassLoader portalClassLoader =
					PortalClassLoaderUtil.getClassLoader();

				Class<?> clazz = portalClassLoader.loadClass(className);

				classes.put(className, clazz);
			}

			return classes;
		}
		catch (ClassNotFoundException classNotFoundException) {
			throw new RuntimeException(classNotFoundException);
		}
	}

	protected Session wrapSession(org.hibernate.Session session) {
		Session liferaySession = new SessionImpl(
			session, _sessionFactoryClassLoader);

		for (SessionCustomizer sessionCustomizer : _sessionCustomizers) {
			liferaySession = sessionCustomizer.customize(liferaySession);
		}

		return liferaySession;
	}

	private static final String[] _PRELOAD_CLASS_NAMES =
		PropsValues.
			SPRING_HIBERNATE_SESSION_FACTORY_PRELOAD_CLASSLOADER_CLASSES;

	private static final Log _log = LogFactoryUtil.getLog(
		SessionFactoryImpl.class);

	private final ServiceTrackerList<SessionCustomizer> _sessionCustomizers =
		ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(), SessionCustomizer.class);
	private ClassLoader _sessionFactoryClassLoader;
	private Supplier<SessionFactoryImplementor>
		_sessionFactoryImplementorSupplier;

}