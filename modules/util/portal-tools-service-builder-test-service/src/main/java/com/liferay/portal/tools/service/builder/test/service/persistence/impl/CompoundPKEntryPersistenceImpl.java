/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCompoundPKEntryException;
import com.liferay.portal.tools.service.builder.test.model.CompoundPKEntry;
import com.liferay.portal.tools.service.builder.test.model.CompoundPKEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.CompoundPKEntryPK;
import com.liferay.portal.tools.service.builder.test.service.persistence.CompoundPKEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.CompoundPKEntryUtil;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the compound pk entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CompoundPKEntryPersistenceImpl
	extends BasePersistenceImpl<CompoundPKEntry, NoSuchCompoundPKEntryException>
	implements CompoundPKEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CompoundPKEntryUtil</code> to access the compound pk entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CompoundPKEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public CompoundPKEntryPersistenceImpl() {
		setModelClass(CompoundPKEntry.class);

		setModelImplClass(CompoundPKEntryImpl.class);
		setModelPKClass(CompoundPKEntryPK.class);

		setTable(CompoundPKEntryTable.INSTANCE);
	}

	/**
	 * Creates a new compound pk entry with the primary key. Does not add the compound pk entry to the database.
	 *
	 * @param compoundPKEntryPK the primary key for the new compound pk entry
	 * @return the new compound pk entry
	 */
	@Override
	public CompoundPKEntry create(CompoundPKEntryPK compoundPKEntryPK) {
		CompoundPKEntry compoundPKEntry = new CompoundPKEntryImpl();

		compoundPKEntry.setNew(true);
		compoundPKEntry.setPrimaryKey(compoundPKEntryPK);

		compoundPKEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return compoundPKEntry;
	}

	/**
	 * Removes the compound pk entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry that was removed
	 * @throws NoSuchCompoundPKEntryException if a compound pk entry with the primary key could not be found
	 */
	@Override
	public CompoundPKEntry remove(CompoundPKEntryPK compoundPKEntryPK)
		throws NoSuchCompoundPKEntryException {

		return remove((Serializable)compoundPKEntryPK);
	}

	@Override
	protected CompoundPKEntry removeImpl(CompoundPKEntry compoundPKEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(compoundPKEntry)) {
				compoundPKEntry = (CompoundPKEntry)session.get(
					CompoundPKEntryImpl.class,
					compoundPKEntry.getPrimaryKeyObj());
			}

			if (compoundPKEntry != null) {
				session.delete(compoundPKEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (compoundPKEntry != null) {
			clearCache(compoundPKEntry);
		}

		return compoundPKEntry;
	}

	@Override
	public CompoundPKEntry updateImpl(CompoundPKEntry compoundPKEntry) {
		boolean isNew = compoundPKEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(compoundPKEntry);
			}
			else {
				compoundPKEntry = (CompoundPKEntry)session.merge(
					compoundPKEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(compoundPKEntry, false);

		if (isNew) {
			compoundPKEntry.setNew(false);
		}

		compoundPKEntry.resetOriginalValues();

		return compoundPKEntry;
	}

	/**
	 * Returns the compound pk entry with the primary key or throws a <code>NoSuchCompoundPKEntryException</code> if it could not be found.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry
	 * @throws NoSuchCompoundPKEntryException if a compound pk entry with the primary key could not be found
	 */
	@Override
	public CompoundPKEntry findByPrimaryKey(CompoundPKEntryPK compoundPKEntryPK)
		throws NoSuchCompoundPKEntryException {

		return findByPrimaryKey((Serializable)compoundPKEntryPK);
	}

	/**
	 * Returns the compound pk entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry, or <code>null</code> if a compound pk entry with the primary key could not be found
	 */
	@Override
	public CompoundPKEntry fetchByPrimaryKey(
		CompoundPKEntryPK compoundPKEntryPK) {

		return fetchByPrimaryKey((Serializable)compoundPKEntryPK);
	}

	@Override
	public Set<String> getCompoundPKColumnNames() {
		return _compoundPKColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "compoundPKEntryPK";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMPOUNDPKENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CompoundPKEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the compound pk entry persistence.
	 */
	public void afterPropertiesSet() {
		CompoundPKEntryUtil.setPersistence(this);
	}

	public void destroy() {
		CompoundPKEntryUtil.setPersistence(null);

		entityCache.removeCache(CompoundPKEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_COMPOUNDPKENTRY =
		"SELECT compoundPKEntry FROM CompoundPKEntry compoundPKEntry";

	private static final Set<String> _compoundPKColumnNames = SetUtil.fromArray(
		new String[] {"companyId", "classNameId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1541974422