/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchCompanyException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyTable;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.service.persistence.CompanyUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the company service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CompanyPersistenceImpl
	extends BasePersistenceImpl<Company, NoSuchCompanyException>
	implements CompanyPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CompanyUtil</code> to access the company persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CompanyImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<Company, NoSuchCompanyException>
		_uniquePersistenceFinderByWebId;

	/**
	 * Returns the company where webId = &#63; or throws a <code>NoSuchCompanyException</code> if it could not be found.
	 *
	 * @param webId the web ID
	 * @return the matching company
	 * @throws NoSuchCompanyException if a matching company could not be found
	 */
	@Override
	public Company findByWebId(String webId) throws NoSuchCompanyException {
		return _uniquePersistenceFinderByWebId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {webId});
	}

	/**
	 * Returns the company where webId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param webId the web ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching company, or <code>null</code> if a matching company could not be found
	 */
	@Override
	public Company fetchByWebId(String webId, boolean useFinderCache) {
		return _uniquePersistenceFinderByWebId.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {webId},
			useFinderCache);
	}

	/**
	 * Removes the company where webId = &#63; from the database.
	 *
	 * @param webId the web ID
	 * @return the company that was removed
	 */
	@Override
	public Company removeByWebId(String webId) throws NoSuchCompanyException {
		Company company = findByWebId(webId);

		return remove(company);
	}

	/**
	 * Returns the number of companies where webId = &#63;.
	 *
	 * @param webId the web ID
	 * @return the number of matching companies
	 */
	@Override
	public int countByWebId(String webId) {
		return _uniquePersistenceFinderByWebId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {webId});
	}

	public CompanyPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Company.class);

		setModelImplClass(CompanyImpl.class);
		setModelPKClass(long.class);

		setTable(CompanyTable.INSTANCE);
	}

	/**
	 * Creates a new company with the primary key. Does not add the company to the database.
	 *
	 * @param companyId the primary key for the new company
	 * @return the new company
	 */
	@Override
	public Company create(long companyId) {
		Company company = new CompanyImpl();

		company.setNew(true);
		company.setPrimaryKey(companyId);

		return company;
	}

	/**
	 * Removes the company with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param companyId the primary key of the company
	 * @return the company that was removed
	 * @throws NoSuchCompanyException if a company with the primary key could not be found
	 */
	@Override
	public Company remove(long companyId) throws NoSuchCompanyException {
		return remove((Serializable)companyId);
	}

	@Override
	protected Company removeImpl(Company company) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(company)) {
				company = (Company)session.get(
					CompanyImpl.class, company.getPrimaryKeyObj());
			}

			if (company != null) {
				session.delete(company);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (company != null) {
			clearCache(company);
		}

		return company;
	}

	@Override
	public Company updateImpl(Company company) {
		boolean isNew = company.isNew();

		if (!(company instanceof CompanyModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(company.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(company);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in company proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Company implementation " +
					company.getClass());
		}

		CompanyModelImpl companyModelImpl = (CompanyModelImpl)company;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (company.getCreateDate() == null)) {
			if (serviceContext == null) {
				company.setCreateDate(date);
			}
			else {
				company.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!companyModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				company.setModifiedDate(date);
			}
			else {
				company.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(company);
			}
			else {
				company = (Company)session.merge(company);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(company, false);

		if (isNew) {
			company.setNew(false);
		}

		company.resetOriginalValues();

		return company;
	}

	/**
	 * Returns the company with the primary key or throws a <code>NoSuchCompanyException</code> if it could not be found.
	 *
	 * @param companyId the primary key of the company
	 * @return the company
	 * @throws NoSuchCompanyException if a company with the primary key could not be found
	 */
	@Override
	public Company findByPrimaryKey(long companyId)
		throws NoSuchCompanyException {

		return findByPrimaryKey((Serializable)companyId);
	}

	/**
	 * Returns the company with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param companyId the primary key of the company
	 * @return the company, or <code>null</code> if a company with the primary key could not be found
	 */
	@Override
	public Company fetchByPrimaryKey(long companyId) {
		return fetchByPrimaryKey((Serializable)companyId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "companyId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMPANY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CompanyModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the company persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByWebId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByWebId",
				new String[] {String.class.getName()}, new String[] {"webId"},
				0, 1, false, convertNullFunction(Company::getWebId)),
			_SQL_SELECT_COMPANY_WHERE, "",
			new FinderColumn<>(
				"company.", "webId", FinderColumn.Type.STRING, "=", true, true,
				Company::getWebId));

		CompanyUtil.setPersistence(this);
	}

	public void destroy() {
		CompanyUtil.setPersistence(null);

		EntityCacheUtil.removeCache(CompanyImpl.class.getName());
	}

	private static final String _SQL_SELECT_COMPANY =
		"SELECT company FROM Company company";

	private static final String _SQL_SELECT_COMPANY_WHERE =
		"SELECT company FROM Company company WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"active"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1121413014