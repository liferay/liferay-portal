/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchReassociateEntryException;
import com.liferay.portal.tools.service.builder.test.model.ReassociateEntry;
import com.liferay.portal.tools.service.builder.test.model.ReassociateEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.ReassociateEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.ReassociateEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.ReassociateEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ReassociateEntryUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * The persistence implementation for the reassociate entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ReassociateEntryPersistenceImpl
	extends BasePersistenceImpl
		<ReassociateEntry, NoSuchReassociateEntryException>
	implements ReassociateEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ReassociateEntryUtil</code> to access the reassociate entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ReassociateEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public ReassociateEntryPersistenceImpl() {
		setModelClass(ReassociateEntry.class);

		setModelImplClass(ReassociateEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ReassociateEntryTable.INSTANCE);
	}

	/**
	 * Creates a new reassociate entry with the primary key. Does not add the reassociate entry to the database.
	 *
	 * @param reassociateEntryId the primary key for the new reassociate entry
	 * @return the new reassociate entry
	 */
	@Override
	public ReassociateEntry create(long reassociateEntryId) {
		ReassociateEntry reassociateEntry = new ReassociateEntryImpl();

		reassociateEntry.setNew(true);
		reassociateEntry.setPrimaryKey(reassociateEntryId);

		return reassociateEntry;
	}

	/**
	 * Removes the reassociate entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param reassociateEntryId the primary key of the reassociate entry
	 * @return the reassociate entry that was removed
	 * @throws NoSuchReassociateEntryException if a reassociate entry with the primary key could not be found
	 */
	@Override
	public ReassociateEntry remove(long reassociateEntryId)
		throws NoSuchReassociateEntryException {

		return remove((Serializable)reassociateEntryId);
	}

	@Override
	protected ReassociateEntry removeImpl(ReassociateEntry reassociateEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(reassociateEntry)) {
				reassociateEntry = (ReassociateEntry)session.get(
					ReassociateEntryImpl.class,
					reassociateEntry.getPrimaryKeyObj());
			}

			if (reassociateEntry != null) {
				session.delete(reassociateEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (reassociateEntry != null) {
			clearCache(reassociateEntry);
		}

		return reassociateEntry;
	}

	@Override
	public ReassociateEntry updateImpl(ReassociateEntry reassociateEntry) {
		boolean isNew = reassociateEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(reassociateEntry);
			}
			else {
				reassociateEntry = (ReassociateEntry)session.merge(
					reassociateEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(reassociateEntry, false);

		if (isNew) {
			reassociateEntry.setNew(false);
		}

		reassociateEntry.resetOriginalValues();

		return reassociateEntry;
	}

	/**
	 * Returns the reassociate entry with the primary key or throws a <code>NoSuchReassociateEntryException</code> if it could not be found.
	 *
	 * @param reassociateEntryId the primary key of the reassociate entry
	 * @return the reassociate entry
	 * @throws NoSuchReassociateEntryException if a reassociate entry with the primary key could not be found
	 */
	@Override
	public ReassociateEntry findByPrimaryKey(long reassociateEntryId)
		throws NoSuchReassociateEntryException {

		return findByPrimaryKey((Serializable)reassociateEntryId);
	}

	/**
	 * Returns the reassociate entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param reassociateEntryId the primary key of the reassociate entry
	 * @return the reassociate entry, or <code>null</code> if a reassociate entry with the primary key could not be found
	 */
	@Override
	public ReassociateEntry fetchByPrimaryKey(long reassociateEntryId) {
		return fetchByPrimaryKey((Serializable)reassociateEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "reassociateEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_REASSOCIATEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ReassociateEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the reassociate entry persistence.
	 */
	public void afterPropertiesSet() {
		ReassociateEntryUtil.setPersistence(this);
	}

	public void destroy() {
		ReassociateEntryUtil.setPersistence(null);

		entityCache.removeCache(ReassociateEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_REASSOCIATEENTRY =
		"SELECT reassociateEntry FROM ReassociateEntry reassociateEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1902393810