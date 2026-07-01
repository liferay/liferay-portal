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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDSLQueryStatusEntryException;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryStatusEntry;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryStatusEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.DSLQueryStatusEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.DSLQueryStatusEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryStatusEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryStatusEntryUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * The persistence implementation for the dsl query status entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DSLQueryStatusEntryPersistenceImpl
	extends BasePersistenceImpl
		<DSLQueryStatusEntry, NoSuchDSLQueryStatusEntryException>
	implements DSLQueryStatusEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DSLQueryStatusEntryUtil</code> to access the dsl query status entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DSLQueryStatusEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public DSLQueryStatusEntryPersistenceImpl() {
		setModelClass(DSLQueryStatusEntry.class);

		setModelImplClass(DSLQueryStatusEntryImpl.class);
		setModelPKClass(long.class);

		setTable(DSLQueryStatusEntryTable.INSTANCE);
	}

	/**
	 * Creates a new dsl query status entry with the primary key. Does not add the dsl query status entry to the database.
	 *
	 * @param dslQueryStatusEntryId the primary key for the new dsl query status entry
	 * @return the new dsl query status entry
	 */
	@Override
	public DSLQueryStatusEntry create(long dslQueryStatusEntryId) {
		DSLQueryStatusEntry dslQueryStatusEntry = new DSLQueryStatusEntryImpl();

		dslQueryStatusEntry.setNew(true);
		dslQueryStatusEntry.setPrimaryKey(dslQueryStatusEntryId);

		return dslQueryStatusEntry;
	}

	/**
	 * Removes the dsl query status entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dslQueryStatusEntryId the primary key of the dsl query status entry
	 * @return the dsl query status entry that was removed
	 * @throws NoSuchDSLQueryStatusEntryException if a dsl query status entry with the primary key could not be found
	 */
	@Override
	public DSLQueryStatusEntry remove(long dslQueryStatusEntryId)
		throws NoSuchDSLQueryStatusEntryException {

		return remove((Serializable)dslQueryStatusEntryId);
	}

	@Override
	protected DSLQueryStatusEntry removeImpl(
		DSLQueryStatusEntry dslQueryStatusEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dslQueryStatusEntry)) {
				dslQueryStatusEntry = (DSLQueryStatusEntry)session.get(
					DSLQueryStatusEntryImpl.class,
					dslQueryStatusEntry.getPrimaryKeyObj());
			}

			if (dslQueryStatusEntry != null) {
				session.delete(dslQueryStatusEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dslQueryStatusEntry != null) {
			clearCache(dslQueryStatusEntry);
		}

		return dslQueryStatusEntry;
	}

	@Override
	public DSLQueryStatusEntry updateImpl(
		DSLQueryStatusEntry dslQueryStatusEntry) {

		boolean isNew = dslQueryStatusEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dslQueryStatusEntry);
			}
			else {
				dslQueryStatusEntry = (DSLQueryStatusEntry)session.merge(
					dslQueryStatusEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dslQueryStatusEntry, false);

		if (isNew) {
			dslQueryStatusEntry.setNew(false);
		}

		dslQueryStatusEntry.resetOriginalValues();

		return dslQueryStatusEntry;
	}

	/**
	 * Returns the dsl query status entry with the primary key or throws a <code>NoSuchDSLQueryStatusEntryException</code> if it could not be found.
	 *
	 * @param dslQueryStatusEntryId the primary key of the dsl query status entry
	 * @return the dsl query status entry
	 * @throws NoSuchDSLQueryStatusEntryException if a dsl query status entry with the primary key could not be found
	 */
	@Override
	public DSLQueryStatusEntry findByPrimaryKey(long dslQueryStatusEntryId)
		throws NoSuchDSLQueryStatusEntryException {

		return findByPrimaryKey((Serializable)dslQueryStatusEntryId);
	}

	/**
	 * Returns the dsl query status entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dslQueryStatusEntryId the primary key of the dsl query status entry
	 * @return the dsl query status entry, or <code>null</code> if a dsl query status entry with the primary key could not be found
	 */
	@Override
	public DSLQueryStatusEntry fetchByPrimaryKey(long dslQueryStatusEntryId) {
		return fetchByPrimaryKey((Serializable)dslQueryStatusEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "dslQueryStatusEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DSLQUERYSTATUSENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DSLQueryStatusEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the dsl query status entry persistence.
	 */
	public void afterPropertiesSet() {
		DSLQueryStatusEntryUtil.setPersistence(this);
	}

	public void destroy() {
		DSLQueryStatusEntryUtil.setPersistence(null);

		entityCache.removeCache(DSLQueryStatusEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DSLQUERYSTATUSENTRY =
		"SELECT dslQueryStatusEntry FROM DSLQueryStatusEntry dslQueryStatusEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-629767824