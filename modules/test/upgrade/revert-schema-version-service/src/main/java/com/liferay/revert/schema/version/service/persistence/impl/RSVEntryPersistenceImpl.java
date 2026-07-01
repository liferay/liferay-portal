/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.revert.schema.version.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.revert.schema.version.exception.NoSuchEntryException;
import com.liferay.revert.schema.version.model.RSVEntry;
import com.liferay.revert.schema.version.model.RSVEntryTable;
import com.liferay.revert.schema.version.model.impl.RSVEntryImpl;
import com.liferay.revert.schema.version.model.impl.RSVEntryModelImpl;
import com.liferay.revert.schema.version.service.persistence.RSVEntryPersistence;
import com.liferay.revert.schema.version.service.persistence.RSVEntryUtil;
import com.liferay.revert.schema.version.service.persistence.impl.constants.RSVPersistenceConstants;

import java.io.Serializable;

import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the rsv entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = RSVEntryPersistence.class)
public class RSVEntryPersistenceImpl
	extends BasePersistenceImpl<RSVEntry, NoSuchEntryException>
	implements RSVEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RSVEntryUtil</code> to access the rsv entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RSVEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public RSVEntryPersistenceImpl() {
		setModelClass(RSVEntry.class);

		setModelImplClass(RSVEntryImpl.class);
		setModelPKClass(long.class);

		setTable(RSVEntryTable.INSTANCE);
	}

	/**
	 * Creates a new rsv entry with the primary key. Does not add the rsv entry to the database.
	 *
	 * @param rsvEntryId the primary key for the new rsv entry
	 * @return the new rsv entry
	 */
	@Override
	public RSVEntry create(long rsvEntryId) {
		RSVEntry rsvEntry = new RSVEntryImpl();

		rsvEntry.setNew(true);
		rsvEntry.setPrimaryKey(rsvEntryId);

		rsvEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return rsvEntry;
	}

	/**
	 * Removes the rsv entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param rsvEntryId the primary key of the rsv entry
	 * @return the rsv entry that was removed
	 * @throws NoSuchEntryException if a rsv entry with the primary key could not be found
	 */
	@Override
	public RSVEntry remove(long rsvEntryId) throws NoSuchEntryException {
		return remove((Serializable)rsvEntryId);
	}

	@Override
	protected RSVEntry removeImpl(RSVEntry rsvEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(rsvEntry)) {
				rsvEntry = (RSVEntry)session.get(
					RSVEntryImpl.class, rsvEntry.getPrimaryKeyObj());
			}

			if (rsvEntry != null) {
				session.delete(rsvEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (rsvEntry != null) {
			clearCache(rsvEntry);
		}

		return rsvEntry;
	}

	@Override
	public RSVEntry updateImpl(RSVEntry rsvEntry) {
		boolean isNew = rsvEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(rsvEntry);
			}
			else {
				rsvEntry = (RSVEntry)session.merge(rsvEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(rsvEntry, false);

		if (isNew) {
			rsvEntry.setNew(false);
		}

		rsvEntry.resetOriginalValues();

		return rsvEntry;
	}

	/**
	 * Returns the rsv entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param rsvEntryId the primary key of the rsv entry
	 * @return the rsv entry
	 * @throws NoSuchEntryException if a rsv entry with the primary key could not be found
	 */
	@Override
	public RSVEntry findByPrimaryKey(long rsvEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)rsvEntryId);
	}

	/**
	 * Returns the rsv entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param rsvEntryId the primary key of the rsv entry
	 * @return the rsv entry, or <code>null</code> if a rsv entry with the primary key could not be found
	 */
	@Override
	public RSVEntry fetchByPrimaryKey(long rsvEntryId) {
		return fetchByPrimaryKey((Serializable)rsvEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "rsvEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_RSVENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RSVEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the rsv entry persistence.
	 */
	@Activate
	public void activate() {
		RSVEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		RSVEntryUtil.setPersistence(null);

		entityCache.removeCache(RSVEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = RSVPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = RSVPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = RSVPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_RSVENTRY =
		"SELECT rsvEntry FROM RSVEntry rsvEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-672710915