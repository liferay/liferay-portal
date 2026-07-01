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
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchManyColumnsEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.ManyColumnsEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.ManyColumnsEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ManyColumnsEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ManyColumnsEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ManyColumnsEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ManyColumnsEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the many columns entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ManyColumnsEntryPersistence.class)
public class ManyColumnsEntryPersistenceImpl
	extends BasePersistenceImpl
		<ManyColumnsEntry, NoSuchManyColumnsEntryException>
	implements ManyColumnsEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ManyColumnsEntryUtil</code> to access the many columns entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ManyColumnsEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public ManyColumnsEntryPersistenceImpl() {
		setModelClass(ManyColumnsEntry.class);

		setModelImplClass(ManyColumnsEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ManyColumnsEntryTable.INSTANCE);
	}

	/**
	 * Creates a new many columns entry with the primary key. Does not add the many columns entry to the database.
	 *
	 * @param manyColumnsEntryId the primary key for the new many columns entry
	 * @return the new many columns entry
	 */
	@Override
	public ManyColumnsEntry create(long manyColumnsEntryId) {
		ManyColumnsEntry manyColumnsEntry = new ManyColumnsEntryImpl();

		manyColumnsEntry.setNew(true);
		manyColumnsEntry.setPrimaryKey(manyColumnsEntryId);

		return manyColumnsEntry;
	}

	/**
	 * Removes the many columns entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param manyColumnsEntryId the primary key of the many columns entry
	 * @return the many columns entry that was removed
	 * @throws NoSuchManyColumnsEntryException if a many columns entry with the primary key could not be found
	 */
	@Override
	public ManyColumnsEntry remove(long manyColumnsEntryId)
		throws NoSuchManyColumnsEntryException {

		return remove((Serializable)manyColumnsEntryId);
	}

	@Override
	protected ManyColumnsEntry removeImpl(ManyColumnsEntry manyColumnsEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(manyColumnsEntry)) {
				manyColumnsEntry = (ManyColumnsEntry)session.get(
					ManyColumnsEntryImpl.class,
					manyColumnsEntry.getPrimaryKeyObj());
			}

			if (manyColumnsEntry != null) {
				session.delete(manyColumnsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (manyColumnsEntry != null) {
			clearCache(manyColumnsEntry);
		}

		return manyColumnsEntry;
	}

	@Override
	public ManyColumnsEntry updateImpl(ManyColumnsEntry manyColumnsEntry) {
		boolean isNew = manyColumnsEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(manyColumnsEntry);
			}
			else {
				manyColumnsEntry = (ManyColumnsEntry)session.merge(
					manyColumnsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(manyColumnsEntry, false);

		if (isNew) {
			manyColumnsEntry.setNew(false);
		}

		manyColumnsEntry.resetOriginalValues();

		return manyColumnsEntry;
	}

	/**
	 * Returns the many columns entry with the primary key or throws a <code>NoSuchManyColumnsEntryException</code> if it could not be found.
	 *
	 * @param manyColumnsEntryId the primary key of the many columns entry
	 * @return the many columns entry
	 * @throws NoSuchManyColumnsEntryException if a many columns entry with the primary key could not be found
	 */
	@Override
	public ManyColumnsEntry findByPrimaryKey(long manyColumnsEntryId)
		throws NoSuchManyColumnsEntryException {

		return findByPrimaryKey((Serializable)manyColumnsEntryId);
	}

	/**
	 * Returns the many columns entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param manyColumnsEntryId the primary key of the many columns entry
	 * @return the many columns entry, or <code>null</code> if a many columns entry with the primary key could not be found
	 */
	@Override
	public ManyColumnsEntry fetchByPrimaryKey(long manyColumnsEntryId) {
		return fetchByPrimaryKey((Serializable)manyColumnsEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "manyColumnsEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MANYCOLUMNSENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ManyColumnsEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the many columns entry persistence.
	 */
	@Activate
	public void activate() {
		ManyColumnsEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ManyColumnsEntryUtil.setPersistence(null);

		entityCache.removeCache(ManyColumnsEntryImpl.class.getName());
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

	private static final String _SQL_SELECT_MANYCOLUMNSENTRY =
		"SELECT manyColumnsEntry FROM ManyColumnsEntry manyColumnsEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1224484978