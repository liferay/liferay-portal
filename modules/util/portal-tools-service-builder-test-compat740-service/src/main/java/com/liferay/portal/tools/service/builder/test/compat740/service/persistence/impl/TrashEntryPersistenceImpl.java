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
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchTrashEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.TrashEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.TrashEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.TrashEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.TrashEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.TrashEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.TrashEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the trash entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = TrashEntryPersistence.class)
public class TrashEntryPersistenceImpl
	extends BasePersistenceImpl<TrashEntry, NoSuchTrashEntryException>
	implements TrashEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>TrashEntryUtil</code> to access the trash entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		TrashEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public TrashEntryPersistenceImpl() {
		setModelClass(TrashEntry.class);

		setModelImplClass(TrashEntryImpl.class);
		setModelPKClass(long.class);

		setTable(TrashEntryTable.INSTANCE);
	}

	/**
	 * Creates a new trash entry with the primary key. Does not add the trash entry to the database.
	 *
	 * @param trashEntryId the primary key for the new trash entry
	 * @return the new trash entry
	 */
	@Override
	public TrashEntry create(long trashEntryId) {
		TrashEntry trashEntry = new TrashEntryImpl();

		trashEntry.setNew(true);
		trashEntry.setPrimaryKey(trashEntryId);

		trashEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return trashEntry;
	}

	/**
	 * Removes the trash entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param trashEntryId the primary key of the trash entry
	 * @return the trash entry that was removed
	 * @throws NoSuchTrashEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry remove(long trashEntryId)
		throws NoSuchTrashEntryException {

		return remove((Serializable)trashEntryId);
	}

	@Override
	protected TrashEntry removeImpl(TrashEntry trashEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(trashEntry)) {
				trashEntry = (TrashEntry)session.get(
					TrashEntryImpl.class, trashEntry.getPrimaryKeyObj());
			}

			if (trashEntry != null) {
				session.delete(trashEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (trashEntry != null) {
			clearCache(trashEntry);
		}

		return trashEntry;
	}

	@Override
	public TrashEntry updateImpl(TrashEntry trashEntry) {
		boolean isNew = trashEntry.isNew();

		if (!(trashEntry instanceof TrashEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(trashEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(trashEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in trashEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom TrashEntry implementation " +
					trashEntry.getClass());
		}

		TrashEntryModelImpl trashEntryModelImpl =
			(TrashEntryModelImpl)trashEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (trashEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				trashEntry.setCreateDate(date);
			}
			else {
				trashEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!trashEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				trashEntry.setModifiedDate(date);
			}
			else {
				trashEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(trashEntry);
			}
			else {
				trashEntry = (TrashEntry)session.merge(trashEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(trashEntry, false);

		if (isNew) {
			trashEntry.setNew(false);
		}

		trashEntry.resetOriginalValues();

		return trashEntry;
	}

	/**
	 * Returns the trash entry with the primary key or throws a <code>NoSuchTrashEntryException</code> if it could not be found.
	 *
	 * @param trashEntryId the primary key of the trash entry
	 * @return the trash entry
	 * @throws NoSuchTrashEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry findByPrimaryKey(long trashEntryId)
		throws NoSuchTrashEntryException {

		return findByPrimaryKey((Serializable)trashEntryId);
	}

	/**
	 * Returns the trash entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param trashEntryId the primary key of the trash entry
	 * @return the trash entry, or <code>null</code> if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry fetchByPrimaryKey(long trashEntryId) {
		return fetchByPrimaryKey((Serializable)trashEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "trashEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TRASHENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return TrashEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the trash entry persistence.
	 */
	@Activate
	public void activate() {
		TrashEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		TrashEntryUtil.setPersistence(null);

		entityCache.removeCache(TrashEntryImpl.class.getName());
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

	private static final String _SQL_SELECT_TRASHENTRY =
		"SELECT trashEntry FROM TrashEntry trashEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:195036600