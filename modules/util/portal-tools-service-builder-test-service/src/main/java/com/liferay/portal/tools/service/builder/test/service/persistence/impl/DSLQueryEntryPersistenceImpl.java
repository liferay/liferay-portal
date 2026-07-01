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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDSLQueryEntryException;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryEntry;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.DSLQueryEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.DSLQueryEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryEntryUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * The persistence implementation for the dsl query entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DSLQueryEntryPersistenceImpl
	extends BasePersistenceImpl<DSLQueryEntry, NoSuchDSLQueryEntryException>
	implements DSLQueryEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DSLQueryEntryUtil</code> to access the dsl query entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DSLQueryEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public DSLQueryEntryPersistenceImpl() {
		setModelClass(DSLQueryEntry.class);

		setModelImplClass(DSLQueryEntryImpl.class);
		setModelPKClass(long.class);

		setTable(DSLQueryEntryTable.INSTANCE);
	}

	/**
	 * Creates a new dsl query entry with the primary key. Does not add the dsl query entry to the database.
	 *
	 * @param dslQueryEntryId the primary key for the new dsl query entry
	 * @return the new dsl query entry
	 */
	@Override
	public DSLQueryEntry create(long dslQueryEntryId) {
		DSLQueryEntry dslQueryEntry = new DSLQueryEntryImpl();

		dslQueryEntry.setNew(true);
		dslQueryEntry.setPrimaryKey(dslQueryEntryId);

		return dslQueryEntry;
	}

	/**
	 * Removes the dsl query entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dslQueryEntryId the primary key of the dsl query entry
	 * @return the dsl query entry that was removed
	 * @throws NoSuchDSLQueryEntryException if a dsl query entry with the primary key could not be found
	 */
	@Override
	public DSLQueryEntry remove(long dslQueryEntryId)
		throws NoSuchDSLQueryEntryException {

		return remove((Serializable)dslQueryEntryId);
	}

	@Override
	protected DSLQueryEntry removeImpl(DSLQueryEntry dslQueryEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dslQueryEntry)) {
				dslQueryEntry = (DSLQueryEntry)session.get(
					DSLQueryEntryImpl.class, dslQueryEntry.getPrimaryKeyObj());
			}

			if (dslQueryEntry != null) {
				session.delete(dslQueryEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dslQueryEntry != null) {
			clearCache(dslQueryEntry);
		}

		return dslQueryEntry;
	}

	@Override
	public DSLQueryEntry updateImpl(DSLQueryEntry dslQueryEntry) {
		boolean isNew = dslQueryEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dslQueryEntry);
			}
			else {
				dslQueryEntry = (DSLQueryEntry)session.merge(dslQueryEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dslQueryEntry, false);

		if (isNew) {
			dslQueryEntry.setNew(false);
		}

		dslQueryEntry.resetOriginalValues();

		return dslQueryEntry;
	}

	/**
	 * Returns the dsl query entry with the primary key or throws a <code>NoSuchDSLQueryEntryException</code> if it could not be found.
	 *
	 * @param dslQueryEntryId the primary key of the dsl query entry
	 * @return the dsl query entry
	 * @throws NoSuchDSLQueryEntryException if a dsl query entry with the primary key could not be found
	 */
	@Override
	public DSLQueryEntry findByPrimaryKey(long dslQueryEntryId)
		throws NoSuchDSLQueryEntryException {

		return findByPrimaryKey((Serializable)dslQueryEntryId);
	}

	/**
	 * Returns the dsl query entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dslQueryEntryId the primary key of the dsl query entry
	 * @return the dsl query entry, or <code>null</code> if a dsl query entry with the primary key could not be found
	 */
	@Override
	public DSLQueryEntry fetchByPrimaryKey(long dslQueryEntryId) {
		return fetchByPrimaryKey((Serializable)dslQueryEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "dslQueryEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DSLQUERYENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DSLQueryEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the dsl query entry persistence.
	 */
	public void afterPropertiesSet() {
		DSLQueryEntryUtil.setPersistence(this);
	}

	public void destroy() {
		DSLQueryEntryUtil.setPersistence(null);

		entityCache.removeCache(DSLQueryEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DSLQUERYENTRY =
		"SELECT dslQueryEntry FROM DSLQueryEntry dslQueryEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:300625658