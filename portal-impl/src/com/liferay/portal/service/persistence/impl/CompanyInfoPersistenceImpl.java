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
import com.liferay.portal.kernel.exception.NoSuchCompanyInfoException;
import com.liferay.portal.kernel.model.CompanyInfo;
import com.liferay.portal.kernel.model.CompanyInfoTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.CompanyInfoPersistence;
import com.liferay.portal.kernel.service.persistence.CompanyInfoUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.CompanyInfoImpl;
import com.liferay.portal.model.impl.CompanyInfoModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the company info service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CompanyInfoPersistenceImpl
	extends BasePersistenceImpl<CompanyInfo, NoSuchCompanyInfoException>
	implements CompanyInfoPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CompanyInfoUtil</code> to access the company info persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CompanyInfoImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<CompanyInfo, NoSuchCompanyInfoException>
		_uniquePersistenceFinderByCompanyId;

	/**
	 * Returns the company info where companyId = &#63; or throws a <code>NoSuchCompanyInfoException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @return the matching company info
	 * @throws NoSuchCompanyInfoException if a matching company info could not be found
	 */
	@Override
	public CompanyInfo findByCompanyId(long companyId)
		throws NoSuchCompanyInfoException {

		return _uniquePersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the company info where companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching company info, or <code>null</code> if a matching company info could not be found
	 */
	@Override
	public CompanyInfo fetchByCompanyId(
		long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByCompanyId.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			useFinderCache);
	}

	/**
	 * Removes the company info where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @return the company info that was removed
	 */
	@Override
	public CompanyInfo removeByCompanyId(long companyId)
		throws NoSuchCompanyInfoException {

		CompanyInfo companyInfo = findByCompanyId(companyId);

		return remove(companyInfo);
	}

	/**
	 * Returns the number of company infos where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching company infos
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _uniquePersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	public CompanyInfoPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CompanyInfo.class);

		setModelImplClass(CompanyInfoImpl.class);
		setModelPKClass(long.class);

		setTable(CompanyInfoTable.INSTANCE);
	}

	/**
	 * Creates a new company info with the primary key. Does not add the company info to the database.
	 *
	 * @param companyInfoId the primary key for the new company info
	 * @return the new company info
	 */
	@Override
	public CompanyInfo create(long companyInfoId) {
		CompanyInfo companyInfo = new CompanyInfoImpl();

		companyInfo.setNew(true);
		companyInfo.setPrimaryKey(companyInfoId);

		companyInfo.setCompanyId(CompanyThreadLocal.getCompanyId());

		return companyInfo;
	}

	/**
	 * Removes the company info with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param companyInfoId the primary key of the company info
	 * @return the company info that was removed
	 * @throws NoSuchCompanyInfoException if a company info with the primary key could not be found
	 */
	@Override
	public CompanyInfo remove(long companyInfoId)
		throws NoSuchCompanyInfoException {

		return remove((Serializable)companyInfoId);
	}

	@Override
	protected CompanyInfo removeImpl(CompanyInfo companyInfo) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(companyInfo)) {
				companyInfo = (CompanyInfo)session.get(
					CompanyInfoImpl.class, companyInfo.getPrimaryKeyObj());
			}

			if (companyInfo != null) {
				session.delete(companyInfo);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (companyInfo != null) {
			clearCache(companyInfo);
		}

		return companyInfo;
	}

	@Override
	public CompanyInfo updateImpl(CompanyInfo companyInfo) {
		boolean isNew = companyInfo.isNew();

		if (!(companyInfo instanceof CompanyInfoModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(companyInfo.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(companyInfo);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in companyInfo proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CompanyInfo implementation " +
					companyInfo.getClass());
		}

		CompanyInfoModelImpl companyInfoModelImpl =
			(CompanyInfoModelImpl)companyInfo;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(companyInfo);
			}
			else {
				companyInfo = (CompanyInfo)session.merge(companyInfo);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(companyInfo, false);

		if (isNew) {
			companyInfo.setNew(false);
		}

		companyInfo.resetOriginalValues();

		return companyInfo;
	}

	/**
	 * Returns the company info with the primary key or throws a <code>NoSuchCompanyInfoException</code> if it could not be found.
	 *
	 * @param companyInfoId the primary key of the company info
	 * @return the company info
	 * @throws NoSuchCompanyInfoException if a company info with the primary key could not be found
	 */
	@Override
	public CompanyInfo findByPrimaryKey(long companyInfoId)
		throws NoSuchCompanyInfoException {

		return findByPrimaryKey((Serializable)companyInfoId);
	}

	/**
	 * Returns the company info with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param companyInfoId the primary key of the company info
	 * @return the company info, or <code>null</code> if a company info with the primary key could not be found
	 */
	@Override
	public CompanyInfo fetchByPrimaryKey(long companyInfoId) {
		return fetchByPrimaryKey((Serializable)companyInfoId);
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
		return "companyInfoId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMPANYINFO;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CompanyInfoModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the company info persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByCompanyId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCompanyId",
				new String[] {Long.class.getName()}, new String[] {"companyId"},
				0, 0, false, CompanyInfo::getCompanyId),
			_SQL_SELECT_COMPANYINFO_WHERE, "",
			new FinderColumn<>(
				"companyInfo.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, CompanyInfo::getCompanyId));

		CompanyInfoUtil.setPersistence(this);
	}

	public void destroy() {
		CompanyInfoUtil.setPersistence(null);

		EntityCacheUtil.removeCache(CompanyInfoImpl.class.getName());
	}

	private static final String _SQL_SELECT_COMPANYINFO =
		"SELECT companyInfo FROM CompanyInfo companyInfo";

	private static final String _SQL_SELECT_COMPANYINFO_WHERE =
		"SELECT companyInfo FROM CompanyInfo companyInfo WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:31304860