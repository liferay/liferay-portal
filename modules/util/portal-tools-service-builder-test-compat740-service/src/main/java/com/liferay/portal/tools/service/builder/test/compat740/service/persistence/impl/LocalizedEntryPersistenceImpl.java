/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchLocalizedEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.LocalizedEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.LocalizedEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.LocalizedEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.LocalizedEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.LocalizedEntryLocalizationPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.LocalizedEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.LocalizedEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the localized entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LocalizedEntryPersistence.class)
public class LocalizedEntryPersistenceImpl
	extends BasePersistenceImpl<LocalizedEntry, NoSuchLocalizedEntryException>
	implements LocalizedEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LocalizedEntryUtil</code> to access the localized entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LocalizedEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public LocalizedEntryPersistenceImpl() {
		setModelClass(LocalizedEntry.class);

		setModelImplClass(LocalizedEntryImpl.class);
		setModelPKClass(long.class);

		setTable(LocalizedEntryTable.INSTANCE);
	}

	/**
	 * Creates a new localized entry with the primary key. Does not add the localized entry to the database.
	 *
	 * @param localizedEntryId the primary key for the new localized entry
	 * @return the new localized entry
	 */
	@Override
	public LocalizedEntry create(long localizedEntryId) {
		LocalizedEntry localizedEntry = new LocalizedEntryImpl();

		localizedEntry.setNew(true);
		localizedEntry.setPrimaryKey(localizedEntryId);

		return localizedEntry;
	}

	/**
	 * Removes the localized entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param localizedEntryId the primary key of the localized entry
	 * @return the localized entry that was removed
	 * @throws NoSuchLocalizedEntryException if a localized entry with the primary key could not be found
	 */
	@Override
	public LocalizedEntry remove(long localizedEntryId)
		throws NoSuchLocalizedEntryException {

		return remove((Serializable)localizedEntryId);
	}

	@Override
	protected LocalizedEntry removeImpl(LocalizedEntry localizedEntry) {
		localizedEntryLocalizationPersistence.removeByLocalizedEntryId(
			localizedEntry.getLocalizedEntryId());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(localizedEntry)) {
				localizedEntry = (LocalizedEntry)session.get(
					LocalizedEntryImpl.class,
					localizedEntry.getPrimaryKeyObj());
			}

			if (localizedEntry != null) {
				session.delete(localizedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (localizedEntry != null) {
			clearCache(localizedEntry);
		}

		return localizedEntry;
	}

	@Override
	public LocalizedEntry updateImpl(LocalizedEntry localizedEntry) {
		boolean isNew = localizedEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(localizedEntry);
			}
			else {
				localizedEntry = (LocalizedEntry)session.merge(localizedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(localizedEntry, false);

		if (isNew) {
			localizedEntry.setNew(false);
		}

		localizedEntry.resetOriginalValues();

		return localizedEntry;
	}

	/**
	 * Returns the localized entry with the primary key or throws a <code>NoSuchLocalizedEntryException</code> if it could not be found.
	 *
	 * @param localizedEntryId the primary key of the localized entry
	 * @return the localized entry
	 * @throws NoSuchLocalizedEntryException if a localized entry with the primary key could not be found
	 */
	@Override
	public LocalizedEntry findByPrimaryKey(long localizedEntryId)
		throws NoSuchLocalizedEntryException {

		return findByPrimaryKey((Serializable)localizedEntryId);
	}

	/**
	 * Returns the localized entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param localizedEntryId the primary key of the localized entry
	 * @return the localized entry, or <code>null</code> if a localized entry with the primary key could not be found
	 */
	@Override
	public LocalizedEntry fetchByPrimaryKey(long localizedEntryId) {
		return fetchByPrimaryKey((Serializable)localizedEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "localizedEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LOCALIZEDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LocalizedEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the localized entry persistence.
	 */
	@Activate
	public void activate() {
		LocalizedEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LocalizedEntryUtil.setPersistence(null);

		entityCache.removeCache(LocalizedEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	@Reference
	protected LocalizedEntryLocalizationPersistence
		localizedEntryLocalizationPersistence;

	private static final String _SQL_SELECT_LOCALIZEDENTRY =
		"SELECT localizedEntry FROM LocalizedEntry localizedEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1255964933