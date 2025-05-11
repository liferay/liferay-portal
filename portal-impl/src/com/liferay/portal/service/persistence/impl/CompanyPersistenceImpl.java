/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchCompanyException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyTable;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.service.persistence.CompanyUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	extends BasePersistenceImpl<Company> implements CompanyPersistence {

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

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByWebId;

	/**
	 * Returns the company where webId = &#63; or throws a <code>NoSuchCompanyException</code> if it could not be found.
	 *
	 * @param webId the web ID
	 * @return the matching company
	 * @throws NoSuchCompanyException if a matching company could not be found
	 */
	@Override
	public Company findByWebId(String webId) throws NoSuchCompanyException {
		Company company = fetchByWebId(webId);

		if (company == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("webId=");
			sb.append(webId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCompanyException(sb.toString());
		}

		return company;
	}

	/**
	 * Returns the company where webId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param webId the web ID
	 * @return the matching company, or <code>null</code> if a matching company could not be found
	 */
	@Override
	public Company fetchByWebId(String webId) {
		return fetchByWebId(webId, true);
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
		webId = Objects.toString(webId, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {webId};
		}

		Object result = null;

		if (useFinderCache) {
			result = FinderCacheUtil.getResult(
				_finderPathFetchByWebId, finderArgs, this);
		}

		if (result instanceof Company) {
			Company company = (Company)result;

			if (!Objects.equals(webId, company.getWebId())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_COMPANY_WHERE);

			boolean bindWebId = false;

			if (webId.isEmpty()) {
				sb.append(_FINDER_COLUMN_WEBID_WEBID_3);
			}
			else {
				bindWebId = true;

				sb.append(_FINDER_COLUMN_WEBID_WEBID_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindWebId) {
					queryPos.add(webId);
				}

				List<Company> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathFetchByWebId, finderArgs, list);
					}
				}
				else {
					Company company = list.get(0);

					result = company;

					cacheResult(company);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (Company)result;
		}
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
		Company company = fetchByWebId(webId);

		if (company == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_WEBID_WEBID_2 =
		"company.webId = ?";

	private static final String _FINDER_COLUMN_WEBID_WEBID_3 =
		"(company.webId IS NULL OR company.webId = '')";

	private FinderPath _finderPathWithPaginationFindByLogoId;
	private FinderPath _finderPathWithoutPaginationFindByLogoId;
	private FinderPath _finderPathCountByLogoId;

	/**
	 * Returns all the companies where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @return the matching companies
	 */
	@Override
	public List<Company> findByLogoId(long logoId) {
		return findByLogoId(logoId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the companies where logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CompanyModelImpl</code>.
	 * </p>
	 *
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of companies
	 * @param end the upper bound of the range of companies (not inclusive)
	 * @return the range of matching companies
	 */
	@Override
	public List<Company> findByLogoId(long logoId, int start, int end) {
		return findByLogoId(logoId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the companies where logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CompanyModelImpl</code>.
	 * </p>
	 *
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of companies
	 * @param end the upper bound of the range of companies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching companies
	 */
	@Override
	public List<Company> findByLogoId(
		long logoId, int start, int end,
		OrderByComparator<Company> orderByComparator) {

		return findByLogoId(logoId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the companies where logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CompanyModelImpl</code>.
	 * </p>
	 *
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of companies
	 * @param end the upper bound of the range of companies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching companies
	 */
	@Override
	public List<Company> findByLogoId(
		long logoId, int start, int end,
		OrderByComparator<Company> orderByComparator, boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByLogoId;
				finderArgs = new Object[] {logoId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByLogoId;
			finderArgs = new Object[] {logoId, start, end, orderByComparator};
		}

		List<Company> list = null;

		if (useFinderCache) {
			list = (List<Company>)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (Company company : list) {
					if (logoId != company.getLogoId()) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					3 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(3);
			}

			sb.append(_SQL_SELECT_COMPANY_WHERE);

			sb.append(_FINDER_COLUMN_LOGOID_LOGOID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CompanyModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(logoId);

				list = (List<Company>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					FinderCacheUtil.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first company in the ordered set where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching company
	 * @throws NoSuchCompanyException if a matching company could not be found
	 */
	@Override
	public Company findByLogoId_First(
			long logoId, OrderByComparator<Company> orderByComparator)
		throws NoSuchCompanyException {

		Company company = fetchByLogoId_First(logoId, orderByComparator);

		if (company != null) {
			return company;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("logoId=");
		sb.append(logoId);

		sb.append("}");

		throw new NoSuchCompanyException(sb.toString());
	}

	/**
	 * Returns the first company in the ordered set where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching company, or <code>null</code> if a matching company could not be found
	 */
	@Override
	public Company fetchByLogoId_First(
		long logoId, OrderByComparator<Company> orderByComparator) {

		List<Company> list = findByLogoId(logoId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last company in the ordered set where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching company
	 * @throws NoSuchCompanyException if a matching company could not be found
	 */
	@Override
	public Company findByLogoId_Last(
			long logoId, OrderByComparator<Company> orderByComparator)
		throws NoSuchCompanyException {

		Company company = fetchByLogoId_Last(logoId, orderByComparator);

		if (company != null) {
			return company;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("logoId=");
		sb.append(logoId);

		sb.append("}");

		throw new NoSuchCompanyException(sb.toString());
	}

	/**
	 * Returns the last company in the ordered set where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching company, or <code>null</code> if a matching company could not be found
	 */
	@Override
	public Company fetchByLogoId_Last(
		long logoId, OrderByComparator<Company> orderByComparator) {

		int count = countByLogoId(logoId);

		if (count == 0) {
			return null;
		}

		List<Company> list = findByLogoId(
			logoId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the companies before and after the current company in the ordered set where logoId = &#63;.
	 *
	 * @param companyId the primary key of the current company
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next company
	 * @throws NoSuchCompanyException if a company with the primary key could not be found
	 */
	@Override
	public Company[] findByLogoId_PrevAndNext(
			long companyId, long logoId,
			OrderByComparator<Company> orderByComparator)
		throws NoSuchCompanyException {

		Company company = findByPrimaryKey(companyId);

		Session session = null;

		try {
			session = openSession();

			Company[] array = new CompanyImpl[3];

			array[0] = getByLogoId_PrevAndNext(
				session, company, logoId, orderByComparator, true);

			array[1] = company;

			array[2] = getByLogoId_PrevAndNext(
				session, company, logoId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Company getByLogoId_PrevAndNext(
		Session session, Company company, long logoId,
		OrderByComparator<Company> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_COMPANY_WHERE);

		sb.append(_FINDER_COLUMN_LOGOID_LOGOID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(CompanyModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(logoId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(company)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Company> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the companies where logoId = &#63; from the database.
	 *
	 * @param logoId the logo ID
	 */
	@Override
	public void removeByLogoId(long logoId) {
		for (Company company :
				findByLogoId(
					logoId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(company);
		}
	}

	/**
	 * Returns the number of companies where logoId = &#63;.
	 *
	 * @param logoId the logo ID
	 * @return the number of matching companies
	 */
	@Override
	public int countByLogoId(long logoId) {
		FinderPath finderPath = _finderPathCountByLogoId;

		Object[] finderArgs = new Object[] {logoId};

		Long count = (Long)FinderCacheUtil.getResult(
			finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMPANY_WHERE);

			sb.append(_FINDER_COLUMN_LOGOID_LOGOID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(logoId);

				count = (Long)query.uniqueResult();

				FinderCacheUtil.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_LOGOID_LOGOID_2 =
		"company.logoId = ?";

	public CompanyPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("active", "active_");
		dbColumnNames.put("type", "type_");
		dbColumnNames.put("size", "size_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Company.class);

		setModelImplClass(CompanyImpl.class);
		setModelPKClass(long.class);

		setTable(CompanyTable.INSTANCE);
	}

	/**
	 * Caches the company in the entity cache if it is enabled.
	 *
	 * @param company the company
	 */
	@Override
	public void cacheResult(Company company) {
		EntityCacheUtil.putResult(
			CompanyImpl.class, company.getPrimaryKey(), company);

		FinderCacheUtil.putResult(
			_finderPathFetchByWebId, new Object[] {company.getWebId()},
			company);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the companies in the entity cache if it is enabled.
	 *
	 * @param companies the companies
	 */
	@Override
	public void cacheResult(List<Company> companies) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (companies.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (Company company : companies) {
			Company cachedCompany = (Company)EntityCacheUtil.getResult(
				CompanyImpl.class, company.getPrimaryKey());

			if (cachedCompany == null) {
				cacheResult(company);
			}
			else {
				CompanyModelImpl companyModelImpl = (CompanyModelImpl)company;
				CompanyModelImpl cachedCompanyModelImpl =
					(CompanyModelImpl)cachedCompany;

				companyModelImpl.setGroupId(
					cachedCompanyModelImpl.getGroupId());

				companyModelImpl.setVirtualHostname(
					cachedCompanyModelImpl.getVirtualHostname());
			}
		}
	}

	/**
	 * Clears the cache for all companies.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(CompanyImpl.class);

		FinderCacheUtil.clearCache(CompanyImpl.class);
	}

	/**
	 * Clears the cache for the company.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(Company company) {
		EntityCacheUtil.removeResult(CompanyImpl.class, company);
	}

	@Override
	public void clearCache(List<Company> companies) {
		for (Company company : companies) {
			EntityCacheUtil.removeResult(CompanyImpl.class, company);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(CompanyImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(CompanyImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(CompanyModelImpl companyModelImpl) {
		Object[] args = new Object[] {companyModelImpl.getWebId()};

		FinderCacheUtil.putResult(
			_finderPathFetchByWebId, args, companyModelImpl);
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

	/**
	 * Removes the company with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the company
	 * @return the company that was removed
	 * @throws NoSuchCompanyException if a company with the primary key could not be found
	 */
	@Override
	public Company remove(Serializable primaryKey)
		throws NoSuchCompanyException {

		Session session = null;

		try {
			session = openSession();

			Company company = (Company)session.get(
				CompanyImpl.class, primaryKey);

			if (company == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCompanyException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(company);
		}
		catch (NoSuchCompanyException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
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

		EntityCacheUtil.putResult(
			CompanyImpl.class, companyModelImpl, false, true);

		cacheUniqueFindersCache(companyModelImpl);

		if (isNew) {
			company.setNew(false);
		}

		company.resetOriginalValues();

		return company;
	}

	/**
	 * Returns the company with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the company
	 * @return the company
	 * @throws NoSuchCompanyException if a company with the primary key could not be found
	 */
	@Override
	public Company findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCompanyException {

		Company company = fetchByPrimaryKey(primaryKey);

		if (company == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCompanyException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

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

	/**
	 * Returns all the companies.
	 *
	 * @return the companies
	 */
	@Override
	public List<Company> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the companies.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CompanyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of companies
	 * @param end the upper bound of the range of companies (not inclusive)
	 * @return the range of companies
	 */
	@Override
	public List<Company> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the companies.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CompanyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of companies
	 * @param end the upper bound of the range of companies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of companies
	 */
	@Override
	public List<Company> findAll(
		int start, int end, OrderByComparator<Company> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the companies.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CompanyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of companies
	 * @param end the upper bound of the range of companies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of companies
	 */
	@Override
	public List<Company> findAll(
		int start, int end, OrderByComparator<Company> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<Company> list = null;

		if (useFinderCache) {
			list = (List<Company>)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_COMPANY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_COMPANY;

				sql = sql.concat(CompanyModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<Company>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					FinderCacheUtil.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the companies from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (Company company : findAll()) {
			remove(company);
		}
	}

	/**
	 * Returns the number of companies.
	 *
	 * @return the number of companies
	 */
	@Override
	public int countAll() {
		Long count = (Long)FinderCacheUtil.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_COMPANY);

				count = (Long)query.uniqueResult();

				FinderCacheUtil.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
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
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathFetchByWebId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByWebId",
			new String[] {String.class.getName()}, new String[] {"webId"},
			true);

		_finderPathWithPaginationFindByLogoId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLogoId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"logoId"}, true);

		_finderPathWithoutPaginationFindByLogoId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByLogoId",
			new String[] {Long.class.getName()}, new String[] {"logoId"}, true);

		_finderPathCountByLogoId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByLogoId",
			new String[] {Long.class.getName()}, new String[] {"logoId"},
			false);

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

	private static final String _SQL_COUNT_COMPANY =
		"SELECT COUNT(company) FROM Company company";

	private static final String _SQL_COUNT_COMPANY_WHERE =
		"SELECT COUNT(company) FROM Company company WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "company.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No Company exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Company exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"active", "type", "size"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}