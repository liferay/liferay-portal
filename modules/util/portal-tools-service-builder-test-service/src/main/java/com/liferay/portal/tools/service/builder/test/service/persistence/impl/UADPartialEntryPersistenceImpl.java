/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchUADPartialEntryException;
import com.liferay.portal.tools.service.builder.test.model.UADPartialEntry;
import com.liferay.portal.tools.service.builder.test.model.UADPartialEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.UADPartialEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.UADPartialEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.UADPartialEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.UADPartialEntryUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * The persistence implementation for the uad partial entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UADPartialEntryPersistenceImpl
	extends BasePersistenceImpl<UADPartialEntry, NoSuchUADPartialEntryException>
	implements UADPartialEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UADPartialEntryUtil</code> to access the uad partial entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UADPartialEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public UADPartialEntryPersistenceImpl() {
		setModelClass(UADPartialEntry.class);

		setModelImplClass(UADPartialEntryImpl.class);
		setModelPKClass(long.class);

		setTable(UADPartialEntryTable.INSTANCE);
	}

	/**
	 * Creates a new uad partial entry with the primary key. Does not add the uad partial entry to the database.
	 *
	 * @param uadPartialEntryId the primary key for the new uad partial entry
	 * @return the new uad partial entry
	 */
	@Override
	public UADPartialEntry create(long uadPartialEntryId) {
		UADPartialEntry uadPartialEntry = new UADPartialEntryImpl();

		uadPartialEntry.setNew(true);
		uadPartialEntry.setPrimaryKey(uadPartialEntryId);

		return uadPartialEntry;
	}

	/**
	 * Removes the uad partial entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param uadPartialEntryId the primary key of the uad partial entry
	 * @return the uad partial entry that was removed
	 * @throws NoSuchUADPartialEntryException if a uad partial entry with the primary key could not be found
	 */
	@Override
	public UADPartialEntry remove(long uadPartialEntryId)
		throws NoSuchUADPartialEntryException {

		return remove((Serializable)uadPartialEntryId);
	}

	@Override
	protected UADPartialEntry removeImpl(UADPartialEntry uadPartialEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(uadPartialEntry)) {
				uadPartialEntry = (UADPartialEntry)session.get(
					UADPartialEntryImpl.class,
					uadPartialEntry.getPrimaryKeyObj());
			}

			if (uadPartialEntry != null) {
				session.delete(uadPartialEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (uadPartialEntry != null) {
			clearCache(uadPartialEntry);
		}

		return uadPartialEntry;
	}

	@Override
	public UADPartialEntry updateImpl(UADPartialEntry uadPartialEntry) {
		boolean isNew = uadPartialEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(uadPartialEntry);
			}
			else {
				uadPartialEntry = (UADPartialEntry)session.merge(
					uadPartialEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(uadPartialEntry, false);

		if (isNew) {
			uadPartialEntry.setNew(false);
		}

		uadPartialEntry.resetOriginalValues();

		return uadPartialEntry;
	}

	/**
	 * Returns the uad partial entry with the primary key or throws a <code>NoSuchUADPartialEntryException</code> if it could not be found.
	 *
	 * @param uadPartialEntryId the primary key of the uad partial entry
	 * @return the uad partial entry
	 * @throws NoSuchUADPartialEntryException if a uad partial entry with the primary key could not be found
	 */
	@Override
	public UADPartialEntry findByPrimaryKey(long uadPartialEntryId)
		throws NoSuchUADPartialEntryException {

		return findByPrimaryKey((Serializable)uadPartialEntryId);
	}

	/**
	 * Returns the uad partial entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param uadPartialEntryId the primary key of the uad partial entry
	 * @return the uad partial entry, or <code>null</code> if a uad partial entry with the primary key could not be found
	 */
	@Override
	public UADPartialEntry fetchByPrimaryKey(long uadPartialEntryId) {
		return fetchByPrimaryKey((Serializable)uadPartialEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "uadPartialEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_UADPARTIALENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return UADPartialEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the uad partial entry persistence.
	 */
	public void afterPropertiesSet() {
		UADPartialEntryUtil.setPersistence(this);
	}

	public void destroy() {
		UADPartialEntryUtil.setPersistence(null);

		entityCache.removeCache(UADPartialEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_UADPARTIALENTRY =
		"SELECT uadPartialEntry FROM UADPartialEntry uadPartialEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1399815502