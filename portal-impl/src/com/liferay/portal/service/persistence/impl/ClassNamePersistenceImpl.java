/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchClassNameException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.ClassNameTable;
import com.liferay.portal.kernel.service.persistence.ClassNamePersistence;
import com.liferay.portal.kernel.service.persistence.ClassNameUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.ClassNameImpl;
import com.liferay.portal.model.impl.ClassNameModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Map;

/**
 * The persistence implementation for the class name service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ClassNamePersistenceImpl
	extends BasePersistenceImpl<ClassName, NoSuchClassNameException>
	implements ClassNamePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ClassNameUtil</code> to access the class name persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ClassNameImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<ClassName, NoSuchClassNameException>
		_uniquePersistenceFinderByValue;

	/**
	 * Returns the class name where value = &#63; or throws a <code>NoSuchClassNameException</code> if it could not be found.
	 *
	 * @param value the value
	 * @return the matching class name
	 * @throws NoSuchClassNameException if a matching class name could not be found
	 */
	@Override
	public ClassName findByValue(String value) throws NoSuchClassNameException {
		return _uniquePersistenceFinderByValue.find(
			FinderCacheUtil.getFinderCache(), new Object[] {value});
	}

	/**
	 * Returns the class name where value = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param value the value
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching class name, or <code>null</code> if a matching class name could not be found
	 */
	@Override
	public ClassName fetchByValue(String value, boolean useFinderCache) {
		return _uniquePersistenceFinderByValue.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {value},
			useFinderCache);
	}

	/**
	 * Removes the class name where value = &#63; from the database.
	 *
	 * @param value the value
	 * @return the class name that was removed
	 */
	@Override
	public ClassName removeByValue(String value)
		throws NoSuchClassNameException {

		ClassName className = findByValue(value);

		return remove(className);
	}

	/**
	 * Returns the number of class names where value = &#63;.
	 *
	 * @param value the value
	 * @return the number of matching class names
	 */
	@Override
	public int countByValue(String value) {
		return _uniquePersistenceFinderByValue.count(
			FinderCacheUtil.getFinderCache(), new Object[] {value});
	}

	public ClassNamePersistenceImpl() {
		setModelClass(ClassName.class);

		setModelImplClass(ClassNameImpl.class);
		setModelPKClass(long.class);

		setTable(ClassNameTable.INSTANCE);
	}

	/**
	 * Creates a new class name with the primary key. Does not add the class name to the database.
	 *
	 * @param classNameId the primary key for the new class name
	 * @return the new class name
	 */
	@Override
	public ClassName create(long classNameId) {
		ClassName className = new ClassNameImpl();

		className.setNew(true);
		className.setPrimaryKey(classNameId);

		return className;
	}

	/**
	 * Removes the class name with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param classNameId the primary key of the class name
	 * @return the class name that was removed
	 * @throws NoSuchClassNameException if a class name with the primary key could not be found
	 */
	@Override
	public ClassName remove(long classNameId) throws NoSuchClassNameException {
		return remove((Serializable)classNameId);
	}

	@Override
	protected ClassName removeImpl(ClassName className) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(className)) {
				className = (ClassName)session.get(
					ClassNameImpl.class, className.getPrimaryKeyObj());
			}

			if (className != null) {
				session.delete(className);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (className != null) {
			clearCache(className);
		}

		return className;
	}

	@Override
	public ClassName updateImpl(ClassName className) {
		boolean isNew = className.isNew();

		if (!(className instanceof ClassNameModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(className.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(className);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in className proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ClassName implementation " +
					className.getClass());
		}

		ClassNameModelImpl classNameModelImpl = (ClassNameModelImpl)className;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(className);
			}
			else {
				className = (ClassName)session.merge(className);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(className, false);

		if (isNew) {
			className.setNew(false);
		}

		className.resetOriginalValues();

		return className;
	}

	/**
	 * Returns the class name with the primary key or throws a <code>NoSuchClassNameException</code> if it could not be found.
	 *
	 * @param classNameId the primary key of the class name
	 * @return the class name
	 * @throws NoSuchClassNameException if a class name with the primary key could not be found
	 */
	@Override
	public ClassName findByPrimaryKey(long classNameId)
		throws NoSuchClassNameException {

		return findByPrimaryKey((Serializable)classNameId);
	}

	/**
	 * Returns the class name with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param classNameId the primary key of the class name
	 * @return the class name, or <code>null</code> if a class name with the primary key could not be found
	 */
	@Override
	public ClassName fetchByPrimaryKey(long classNameId) {
		return fetchByPrimaryKey((Serializable)classNameId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "classNameId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CLASSNAME;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ClassNameModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the class name persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByValue = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByValue",
				new String[] {String.class.getName()}, new String[] {"value"},
				0, 1, false, convertNullFunction(ClassName::getValue)),
			_SQL_SELECT_CLASSNAME_WHERE, "",
			new FinderColumn<>(
				"className.", "value", FinderColumn.Type.STRING, "=", true,
				true, ClassName::getValue));

		ClassNameUtil.setPersistence(this);
	}

	public void destroy() {
		ClassNameUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ClassNameImpl.class.getName());
	}

	private static final String _SQL_SELECT_CLASSNAME =
		"SELECT className FROM ClassName className";

	private static final String _SQL_SELECT_CLASSNAME_WHERE =
		"SELECT className FROM ClassName className WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ClassName exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ClassNamePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1456419362