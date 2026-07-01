/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchAutoEscapeEntryException;
import com.liferay.portal.tools.service.builder.test.model.AutoEscapeEntry;
import com.liferay.portal.tools.service.builder.test.model.AutoEscapeEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.AutoEscapeEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.AutoEscapeEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.AutoEscapeEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.AutoEscapeEntryUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * The persistence implementation for the auto escape entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AutoEscapeEntryPersistenceImpl
	extends BasePersistenceImpl<AutoEscapeEntry, NoSuchAutoEscapeEntryException>
	implements AutoEscapeEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AutoEscapeEntryUtil</code> to access the auto escape entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AutoEscapeEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public AutoEscapeEntryPersistenceImpl() {
		setModelClass(AutoEscapeEntry.class);

		setModelImplClass(AutoEscapeEntryImpl.class);
		setModelPKClass(long.class);

		setTable(AutoEscapeEntryTable.INSTANCE);
	}

	/**
	 * Creates a new auto escape entry with the primary key. Does not add the auto escape entry to the database.
	 *
	 * @param autoEscapeEntryId the primary key for the new auto escape entry
	 * @return the new auto escape entry
	 */
	@Override
	public AutoEscapeEntry create(long autoEscapeEntryId) {
		AutoEscapeEntry autoEscapeEntry = new AutoEscapeEntryImpl();

		autoEscapeEntry.setNew(true);
		autoEscapeEntry.setPrimaryKey(autoEscapeEntryId);

		return autoEscapeEntry;
	}

	/**
	 * Removes the auto escape entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param autoEscapeEntryId the primary key of the auto escape entry
	 * @return the auto escape entry that was removed
	 * @throws NoSuchAutoEscapeEntryException if a auto escape entry with the primary key could not be found
	 */
	@Override
	public AutoEscapeEntry remove(long autoEscapeEntryId)
		throws NoSuchAutoEscapeEntryException {

		return remove((Serializable)autoEscapeEntryId);
	}

	@Override
	protected AutoEscapeEntry removeImpl(AutoEscapeEntry autoEscapeEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(autoEscapeEntry)) {
				autoEscapeEntry = (AutoEscapeEntry)session.get(
					AutoEscapeEntryImpl.class,
					autoEscapeEntry.getPrimaryKeyObj());
			}

			if (autoEscapeEntry != null) {
				session.delete(autoEscapeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (autoEscapeEntry != null) {
			clearCache(autoEscapeEntry);
		}

		return autoEscapeEntry;
	}

	@Override
	public AutoEscapeEntry updateImpl(AutoEscapeEntry autoEscapeEntry) {
		boolean isNew = autoEscapeEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(autoEscapeEntry);
			}
			else {
				autoEscapeEntry = (AutoEscapeEntry)session.merge(
					autoEscapeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(autoEscapeEntry, false);

		if (isNew) {
			autoEscapeEntry.setNew(false);
		}

		autoEscapeEntry.resetOriginalValues();

		return autoEscapeEntry;
	}

	/**
	 * Returns the auto escape entry with the primary key or throws a <code>NoSuchAutoEscapeEntryException</code> if it could not be found.
	 *
	 * @param autoEscapeEntryId the primary key of the auto escape entry
	 * @return the auto escape entry
	 * @throws NoSuchAutoEscapeEntryException if a auto escape entry with the primary key could not be found
	 */
	@Override
	public AutoEscapeEntry findByPrimaryKey(long autoEscapeEntryId)
		throws NoSuchAutoEscapeEntryException {

		return findByPrimaryKey((Serializable)autoEscapeEntryId);
	}

	/**
	 * Returns the auto escape entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param autoEscapeEntryId the primary key of the auto escape entry
	 * @return the auto escape entry, or <code>null</code> if a auto escape entry with the primary key could not be found
	 */
	@Override
	public AutoEscapeEntry fetchByPrimaryKey(long autoEscapeEntryId) {
		return fetchByPrimaryKey((Serializable)autoEscapeEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "autoEscapeEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_AUTOESCAPEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return AutoEscapeEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the auto escape entry persistence.
	 */
	public void afterPropertiesSet() {
		AutoEscapeEntryUtil.setPersistence(this);
	}

	public void destroy() {
		AutoEscapeEntryUtil.setPersistence(null);

		entityCache.removeCache(AutoEscapeEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_AUTOESCAPEENTRY =
		"SELECT autoEscapeEntry FROM AutoEscapeEntry autoEscapeEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1368142791