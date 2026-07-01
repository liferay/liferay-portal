/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDataLimitEntryException;
import com.liferay.portal.tools.service.builder.test.model.DataLimitEntry;
import com.liferay.portal.tools.service.builder.test.model.DataLimitEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.DataLimitEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.DataLimitEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.DataLimitEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DataLimitEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.Map;

/**
 * The persistence implementation for the data limit entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DataLimitEntryPersistenceImpl
	extends BasePersistenceImpl<DataLimitEntry, NoSuchDataLimitEntryException>
	implements DataLimitEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DataLimitEntryUtil</code> to access the data limit entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DataLimitEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public DataLimitEntryPersistenceImpl() {
		setModelClass(DataLimitEntry.class);

		setModelImplClass(DataLimitEntryImpl.class);
		setModelPKClass(long.class);

		setTable(DataLimitEntryTable.INSTANCE);
	}

	/**
	 * Creates a new data limit entry with the primary key. Does not add the data limit entry to the database.
	 *
	 * @param dataLimitEntryId the primary key for the new data limit entry
	 * @return the new data limit entry
	 */
	@Override
	public DataLimitEntry create(long dataLimitEntryId) {
		DataLimitEntry dataLimitEntry = new DataLimitEntryImpl();

		dataLimitEntry.setNew(true);
		dataLimitEntry.setPrimaryKey(dataLimitEntryId);

		dataLimitEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dataLimitEntry;
	}

	/**
	 * Removes the data limit entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dataLimitEntryId the primary key of the data limit entry
	 * @return the data limit entry that was removed
	 * @throws NoSuchDataLimitEntryException if a data limit entry with the primary key could not be found
	 */
	@Override
	public DataLimitEntry remove(long dataLimitEntryId)
		throws NoSuchDataLimitEntryException {

		return remove((Serializable)dataLimitEntryId);
	}

	@Override
	protected DataLimitEntry removeImpl(DataLimitEntry dataLimitEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dataLimitEntry)) {
				dataLimitEntry = (DataLimitEntry)session.get(
					DataLimitEntryImpl.class,
					dataLimitEntry.getPrimaryKeyObj());
			}

			if (dataLimitEntry != null) {
				session.delete(dataLimitEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dataLimitEntry != null) {
			clearCache(dataLimitEntry);
		}

		return dataLimitEntry;
	}

	@Override
	public DataLimitEntry updateImpl(DataLimitEntry dataLimitEntry) {
		boolean isNew = dataLimitEntry.isNew();

		if (!(dataLimitEntry instanceof DataLimitEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dataLimitEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dataLimitEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dataLimitEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DataLimitEntry implementation " +
					dataLimitEntry.getClass());
		}

		DataLimitEntryModelImpl dataLimitEntryModelImpl =
			(DataLimitEntryModelImpl)dataLimitEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dataLimitEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				dataLimitEntry.setCreateDate(date);
			}
			else {
				dataLimitEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!dataLimitEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dataLimitEntry.setModifiedDate(date);
			}
			else {
				dataLimitEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dataLimitEntry);
			}
			else {
				dataLimitEntry = (DataLimitEntry)session.merge(dataLimitEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dataLimitEntry, false);

		if (isNew) {
			dataLimitEntry.setNew(false);
		}

		dataLimitEntry.resetOriginalValues();

		return dataLimitEntry;
	}

	/**
	 * Returns the data limit entry with the primary key or throws a <code>NoSuchDataLimitEntryException</code> if it could not be found.
	 *
	 * @param dataLimitEntryId the primary key of the data limit entry
	 * @return the data limit entry
	 * @throws NoSuchDataLimitEntryException if a data limit entry with the primary key could not be found
	 */
	@Override
	public DataLimitEntry findByPrimaryKey(long dataLimitEntryId)
		throws NoSuchDataLimitEntryException {

		return findByPrimaryKey((Serializable)dataLimitEntryId);
	}

	/**
	 * Returns the data limit entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dataLimitEntryId the primary key of the data limit entry
	 * @return the data limit entry, or <code>null</code> if a data limit entry with the primary key could not be found
	 */
	@Override
	public DataLimitEntry fetchByPrimaryKey(long dataLimitEntryId) {
		return fetchByPrimaryKey((Serializable)dataLimitEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "dataLimitEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DATALIMITENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DataLimitEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the data limit entry persistence.
	 */
	public void afterPropertiesSet() {
		DataLimitEntryUtil.setPersistence(this);
	}

	public void destroy() {
		DataLimitEntryUtil.setPersistence(null);

		entityCache.removeCache(DataLimitEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DATALIMITENTRY =
		"SELECT dataLimitEntry FROM DataLimitEntry dataLimitEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-61106388