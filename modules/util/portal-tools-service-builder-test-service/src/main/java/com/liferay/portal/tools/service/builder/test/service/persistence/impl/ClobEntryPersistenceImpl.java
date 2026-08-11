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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchClobEntryException;
import com.liferay.portal.tools.service.builder.test.model.ClobEntry;
import com.liferay.portal.tools.service.builder.test.model.ClobEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.ClobEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.ClobEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.ClobEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ClobEntryUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * The persistence implementation for the clob entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ClobEntryPersistenceImpl
	extends BasePersistenceImpl<ClobEntry, NoSuchClobEntryException>
	implements ClobEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ClobEntryUtil</code> to access the clob entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ClobEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public ClobEntryPersistenceImpl() {
		setModelClass(ClobEntry.class);

		setModelImplClass(ClobEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ClobEntryTable.INSTANCE);
	}

	/**
	 * Creates a new clob entry with the primary key. Does not add the clob entry to the database.
	 *
	 * @param clobEntryId the primary key for the new clob entry
	 * @return the new clob entry
	 */
	@Override
	public ClobEntry create(long clobEntryId) {
		ClobEntry clobEntry = new ClobEntryImpl();

		clobEntry.setNew(true);
		clobEntry.setPrimaryKey(clobEntryId);

		return clobEntry;
	}

	/**
	 * Removes the clob entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clobEntryId the primary key of the clob entry
	 * @return the clob entry that was removed
	 * @throws NoSuchClobEntryException if a clob entry with the primary key could not be found
	 */
	@Override
	public ClobEntry remove(long clobEntryId) throws NoSuchClobEntryException {
		return remove((Serializable)clobEntryId);
	}

	@Override
	protected ClobEntry removeImpl(ClobEntry clobEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(clobEntry)) {
				clobEntry = (ClobEntry)session.get(
					ClobEntryImpl.class, clobEntry.getPrimaryKeyObj());
			}

			if (clobEntry != null) {
				session.delete(clobEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (clobEntry != null) {
			clearCache(clobEntry);
		}

		return clobEntry;
	}

	@Override
	public ClobEntry updateImpl(ClobEntry clobEntry) {
		boolean isNew = clobEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(clobEntry);
			}
			else {
				clobEntry = (ClobEntry)session.merge(clobEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(clobEntry, false);

		if (isNew) {
			clobEntry.setNew(false);
		}

		clobEntry.resetOriginalValues();

		return clobEntry;
	}

	/**
	 * Returns the clob entry with the primary key or throws a <code>NoSuchClobEntryException</code> if it could not be found.
	 *
	 * @param clobEntryId the primary key of the clob entry
	 * @return the clob entry
	 * @throws NoSuchClobEntryException if a clob entry with the primary key could not be found
	 */
	@Override
	public ClobEntry findByPrimaryKey(long clobEntryId)
		throws NoSuchClobEntryException {

		return findByPrimaryKey((Serializable)clobEntryId);
	}

	/**
	 * Returns the clob entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param clobEntryId the primary key of the clob entry
	 * @return the clob entry, or <code>null</code> if a clob entry with the primary key could not be found
	 */
	@Override
	public ClobEntry fetchByPrimaryKey(long clobEntryId) {
		return fetchByPrimaryKey((Serializable)clobEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "clobEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CLOBENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ClobEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the clob entry persistence.
	 */
	public void afterPropertiesSet() {
		ClobEntryUtil.setPersistence(this);
	}

	public void destroy() {
		ClobEntryUtil.setPersistence(null);

		entityCache.removeCache(ClobEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_CLOBENTRY =
		"SELECT clobEntry FROM ClobEntry clobEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1777950923