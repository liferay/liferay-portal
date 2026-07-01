/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDynamicQueryEntryException;
import com.liferay.portal.tools.service.builder.test.model.DynamicQueryEntry;
import com.liferay.portal.tools.service.builder.test.model.DynamicQueryEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.DynamicQueryEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.DynamicQueryEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.DynamicQueryEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DynamicQueryEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.Map;

/**
 * The persistence implementation for the dynamic query entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DynamicQueryEntryPersistenceImpl
	extends BasePersistenceImpl
		<DynamicQueryEntry, NoSuchDynamicQueryEntryException>
	implements DynamicQueryEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DynamicQueryEntryUtil</code> to access the dynamic query entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DynamicQueryEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public DynamicQueryEntryPersistenceImpl() {
		setModelClass(DynamicQueryEntry.class);

		setModelImplClass(DynamicQueryEntryImpl.class);
		setModelPKClass(long.class);

		setTable(DynamicQueryEntryTable.INSTANCE);
	}

	/**
	 * Creates a new dynamic query entry with the primary key. Does not add the dynamic query entry to the database.
	 *
	 * @param dynamicQueryEntryId the primary key for the new dynamic query entry
	 * @return the new dynamic query entry
	 */
	@Override
	public DynamicQueryEntry create(long dynamicQueryEntryId) {
		DynamicQueryEntry dynamicQueryEntry = new DynamicQueryEntryImpl();

		dynamicQueryEntry.setNew(true);
		dynamicQueryEntry.setPrimaryKey(dynamicQueryEntryId);

		return dynamicQueryEntry;
	}

	/**
	 * Removes the dynamic query entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dynamicQueryEntryId the primary key of the dynamic query entry
	 * @return the dynamic query entry that was removed
	 * @throws NoSuchDynamicQueryEntryException if a dynamic query entry with the primary key could not be found
	 */
	@Override
	public DynamicQueryEntry remove(long dynamicQueryEntryId)
		throws NoSuchDynamicQueryEntryException {

		return remove((Serializable)dynamicQueryEntryId);
	}

	@Override
	protected DynamicQueryEntry removeImpl(
		DynamicQueryEntry dynamicQueryEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dynamicQueryEntry)) {
				dynamicQueryEntry = (DynamicQueryEntry)session.get(
					DynamicQueryEntryImpl.class,
					dynamicQueryEntry.getPrimaryKeyObj());
			}

			if (dynamicQueryEntry != null) {
				session.delete(dynamicQueryEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dynamicQueryEntry != null) {
			clearCache(dynamicQueryEntry);
		}

		return dynamicQueryEntry;
	}

	@Override
	public DynamicQueryEntry updateImpl(DynamicQueryEntry dynamicQueryEntry) {
		boolean isNew = dynamicQueryEntry.isNew();

		if (!(dynamicQueryEntry instanceof DynamicQueryEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dynamicQueryEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dynamicQueryEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dynamicQueryEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DynamicQueryEntry implementation " +
					dynamicQueryEntry.getClass());
		}

		DynamicQueryEntryModelImpl dynamicQueryEntryModelImpl =
			(DynamicQueryEntryModelImpl)dynamicQueryEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dynamicQueryEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				dynamicQueryEntry.setCreateDate(date);
			}
			else {
				dynamicQueryEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!dynamicQueryEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dynamicQueryEntry.setModifiedDate(date);
			}
			else {
				dynamicQueryEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dynamicQueryEntry);
			}
			else {
				dynamicQueryEntry = (DynamicQueryEntry)session.merge(
					dynamicQueryEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dynamicQueryEntry, false);

		if (isNew) {
			dynamicQueryEntry.setNew(false);
		}

		dynamicQueryEntry.resetOriginalValues();

		return dynamicQueryEntry;
	}

	/**
	 * Returns the dynamic query entry with the primary key or throws a <code>NoSuchDynamicQueryEntryException</code> if it could not be found.
	 *
	 * @param dynamicQueryEntryId the primary key of the dynamic query entry
	 * @return the dynamic query entry
	 * @throws NoSuchDynamicQueryEntryException if a dynamic query entry with the primary key could not be found
	 */
	@Override
	public DynamicQueryEntry findByPrimaryKey(long dynamicQueryEntryId)
		throws NoSuchDynamicQueryEntryException {

		return findByPrimaryKey((Serializable)dynamicQueryEntryId);
	}

	/**
	 * Returns the dynamic query entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dynamicQueryEntryId the primary key of the dynamic query entry
	 * @return the dynamic query entry, or <code>null</code> if a dynamic query entry with the primary key could not be found
	 */
	@Override
	public DynamicQueryEntry fetchByPrimaryKey(long dynamicQueryEntryId) {
		return fetchByPrimaryKey((Serializable)dynamicQueryEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "dynamicQueryEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DYNAMICQUERYENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DynamicQueryEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the dynamic query entry persistence.
	 */
	public void afterPropertiesSet() {
		DynamicQueryEntryUtil.setPersistence(this);
	}

	public void destroy() {
		DynamicQueryEntryUtil.setPersistence(null);

		entityCache.removeCache(DynamicQueryEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DYNAMICQUERYENTRY =
		"SELECT dynamicQueryEntry FROM DynamicQueryEntry dynamicQueryEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1052825793