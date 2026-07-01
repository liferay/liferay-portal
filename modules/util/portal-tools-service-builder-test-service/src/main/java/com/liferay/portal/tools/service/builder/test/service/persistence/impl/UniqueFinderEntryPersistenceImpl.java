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
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchUniqueFinderEntryException;
import com.liferay.portal.tools.service.builder.test.model.UniqueFinderEntry;
import com.liferay.portal.tools.service.builder.test.model.UniqueFinderEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.UniqueFinderEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.UniqueFinderEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.UniqueFinderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.UniqueFinderEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.Map;

/**
 * The persistence implementation for the unique finder entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UniqueFinderEntryPersistenceImpl
	extends BasePersistenceImpl
		<UniqueFinderEntry, NoSuchUniqueFinderEntryException>
	implements UniqueFinderEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UniqueFinderEntryUtil</code> to access the unique finder entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UniqueFinderEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<UniqueFinderEntry, NoSuchUniqueFinderEntryException>
			_uniquePersistenceFinderByName;

	/**
	 * Returns the unique finder entry where name = &#63; or throws a <code>NoSuchUniqueFinderEntryException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching unique finder entry
	 * @throws NoSuchUniqueFinderEntryException if a matching unique finder entry could not be found
	 */
	@Override
	public UniqueFinderEntry findByName(String name)
		throws NoSuchUniqueFinderEntryException {

		return _uniquePersistenceFinderByName.find(
			finderCache, new Object[] {name});
	}

	/**
	 * Returns the unique finder entry where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching unique finder entry, or <code>null</code> if a matching unique finder entry could not be found
	 */
	@Override
	public UniqueFinderEntry fetchByName(String name, boolean useFinderCache) {
		return _uniquePersistenceFinderByName.fetch(
			finderCache, new Object[] {name}, useFinderCache);
	}

	/**
	 * Removes the unique finder entry where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the unique finder entry that was removed
	 */
	@Override
	public UniqueFinderEntry removeByName(String name)
		throws NoSuchUniqueFinderEntryException {

		UniqueFinderEntry uniqueFinderEntry = findByName(name);

		return remove(uniqueFinderEntry);
	}

	/**
	 * Returns the number of unique finder entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching unique finder entries
	 */
	@Override
	public int countByName(String name) {
		return _uniquePersistenceFinderByName.count(
			finderCache, new Object[] {name});
	}

	public UniqueFinderEntryPersistenceImpl() {
		setModelClass(UniqueFinderEntry.class);

		setModelImplClass(UniqueFinderEntryImpl.class);
		setModelPKClass(long.class);

		setTable(UniqueFinderEntryTable.INSTANCE);
	}

	/**
	 * Creates a new unique finder entry with the primary key. Does not add the unique finder entry to the database.
	 *
	 * @param uniqueFinderEntryId the primary key for the new unique finder entry
	 * @return the new unique finder entry
	 */
	@Override
	public UniqueFinderEntry create(long uniqueFinderEntryId) {
		UniqueFinderEntry uniqueFinderEntry = new UniqueFinderEntryImpl();

		uniqueFinderEntry.setNew(true);
		uniqueFinderEntry.setPrimaryKey(uniqueFinderEntryId);

		return uniqueFinderEntry;
	}

	/**
	 * Removes the unique finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param uniqueFinderEntryId the primary key of the unique finder entry
	 * @return the unique finder entry that was removed
	 * @throws NoSuchUniqueFinderEntryException if a unique finder entry with the primary key could not be found
	 */
	@Override
	public UniqueFinderEntry remove(long uniqueFinderEntryId)
		throws NoSuchUniqueFinderEntryException {

		return remove((Serializable)uniqueFinderEntryId);
	}

	@Override
	protected UniqueFinderEntry removeImpl(
		UniqueFinderEntry uniqueFinderEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(uniqueFinderEntry)) {
				uniqueFinderEntry = (UniqueFinderEntry)session.get(
					UniqueFinderEntryImpl.class,
					uniqueFinderEntry.getPrimaryKeyObj());
			}

			if (uniqueFinderEntry != null) {
				session.delete(uniqueFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (uniqueFinderEntry != null) {
			clearCache(uniqueFinderEntry);
		}

		return uniqueFinderEntry;
	}

	@Override
	public UniqueFinderEntry updateImpl(UniqueFinderEntry uniqueFinderEntry) {
		boolean isNew = uniqueFinderEntry.isNew();

		if (!(uniqueFinderEntry instanceof UniqueFinderEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(uniqueFinderEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					uniqueFinderEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in uniqueFinderEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UniqueFinderEntry implementation " +
					uniqueFinderEntry.getClass());
		}

		UniqueFinderEntryModelImpl uniqueFinderEntryModelImpl =
			(UniqueFinderEntryModelImpl)uniqueFinderEntry;

		if (!uniqueFinderEntryModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				uniqueFinderEntry.setModifiedDate(date);
			}
			else {
				uniqueFinderEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(uniqueFinderEntry);
			}
			else {
				uniqueFinderEntry = (UniqueFinderEntry)session.merge(
					uniqueFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(uniqueFinderEntry, false);

		if (isNew) {
			uniqueFinderEntry.setNew(false);
		}

		uniqueFinderEntry.resetOriginalValues();

		return uniqueFinderEntry;
	}

	/**
	 * Returns the unique finder entry with the primary key or throws a <code>NoSuchUniqueFinderEntryException</code> if it could not be found.
	 *
	 * @param uniqueFinderEntryId the primary key of the unique finder entry
	 * @return the unique finder entry
	 * @throws NoSuchUniqueFinderEntryException if a unique finder entry with the primary key could not be found
	 */
	@Override
	public UniqueFinderEntry findByPrimaryKey(long uniqueFinderEntryId)
		throws NoSuchUniqueFinderEntryException {

		return findByPrimaryKey((Serializable)uniqueFinderEntryId);
	}

	/**
	 * Returns the unique finder entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param uniqueFinderEntryId the primary key of the unique finder entry
	 * @return the unique finder entry, or <code>null</code> if a unique finder entry with the primary key could not be found
	 */
	@Override
	public UniqueFinderEntry fetchByPrimaryKey(long uniqueFinderEntryId) {
		return fetchByPrimaryKey((Serializable)uniqueFinderEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "uniqueFinderEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_UNIQUEFINDERENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return UniqueFinderEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the unique finder entry persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByName = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, false, convertNullFunction(UniqueFinderEntry::getName)),
			_SQL_SELECT_UNIQUEFINDERENTRY_WHERE, "",
			new FinderColumn<>(
				"uniqueFinderEntry.", "name", FinderColumn.Type.STRING, "=",
				true, true, UniqueFinderEntry::getName));

		UniqueFinderEntryUtil.setPersistence(this);
	}

	public void destroy() {
		UniqueFinderEntryUtil.setPersistence(null);

		entityCache.removeCache(UniqueFinderEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_UNIQUEFINDERENTRY =
		"SELECT uniqueFinderEntry FROM UniqueFinderEntry uniqueFinderEntry";

	private static final String _SQL_SELECT_UNIQUEFINDERENTRY_WHERE =
		"SELECT uniqueFinderEntry FROM UniqueFinderEntry uniqueFinderEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:126697093