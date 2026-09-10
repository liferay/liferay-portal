/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence;

import com.liferay.osb.faro.model.FaroProjectEmailDomain;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the faro project email domain service. This utility wraps <code>com.liferay.osb.faro.service.persistence.impl.FaroProjectEmailDomainPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @see FaroProjectEmailDomainPersistence
 * @generated
 */
public class FaroProjectEmailDomainUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<FaroProjectEmailDomain> faroProjectEmailDomains) {

		getPersistence().cacheResult(faroProjectEmailDomains);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		FaroProjectEmailDomain faroProjectEmailDomain) {

		getPersistence().cacheResult(faroProjectEmailDomain);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(
		FaroProjectEmailDomain faroProjectEmailDomain) {

		getPersistence().clearCache(faroProjectEmailDomain);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, FaroProjectEmailDomain> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FaroProjectEmailDomain> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FaroProjectEmailDomain> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FaroProjectEmailDomain> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FaroProjectEmailDomain> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FaroProjectEmailDomain update(
		FaroProjectEmailDomain faroProjectEmailDomain) {

		return getPersistence().update(faroProjectEmailDomain);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FaroProjectEmailDomain update(
		FaroProjectEmailDomain faroProjectEmailDomain,
		ServiceContext serviceContext) {

		return getPersistence().update(faroProjectEmailDomain, serviceContext);
	}

	/**
	 * Returns an ordered range of all the faro project email domains where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro project email domains
	 */
	public static List<FaroProjectEmailDomain> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FaroProjectEmailDomain> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro project email domain in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain
	 * @throws NoSuchFaroProjectEmailDomainException if a matching faro project email domain could not be found
	 */
	public static FaroProjectEmailDomain findByGroupId_First(
			long groupId,
			OrderByComparator<FaroProjectEmailDomain> orderByComparator)
		throws com.liferay.osb.faro.exception.
			NoSuchFaroProjectEmailDomainException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first faro project email domain in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain, or <code>null</code> if a matching faro project email domain could not be found
	 */
	public static FaroProjectEmailDomain fetchByGroupId_First(
		long groupId,
		OrderByComparator<FaroProjectEmailDomain> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the faro project email domains where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of faro project email domains where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching faro project email domains
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the faro project email domains where faroProjectId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param faroProjectId the faro project ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro project email domains
	 */
	public static List<FaroProjectEmailDomain> findByFaroProjectId(
		long faroProjectId, int start, int end,
		OrderByComparator<FaroProjectEmailDomain> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFaroProjectId(
			faroProjectId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro project email domain in the ordered set where faroProjectId = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain
	 * @throws NoSuchFaroProjectEmailDomainException if a matching faro project email domain could not be found
	 */
	public static FaroProjectEmailDomain findByFaroProjectId_First(
			long faroProjectId,
			OrderByComparator<FaroProjectEmailDomain> orderByComparator)
		throws com.liferay.osb.faro.exception.
			NoSuchFaroProjectEmailDomainException {

		return getPersistence().findByFaroProjectId_First(
			faroProjectId, orderByComparator);
	}

	/**
	 * Returns the first faro project email domain in the ordered set where faroProjectId = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain, or <code>null</code> if a matching faro project email domain could not be found
	 */
	public static FaroProjectEmailDomain fetchByFaroProjectId_First(
		long faroProjectId,
		OrderByComparator<FaroProjectEmailDomain> orderByComparator) {

		return getPersistence().fetchByFaroProjectId_First(
			faroProjectId, orderByComparator);
	}

	/**
	 * Removes all the faro project email domains where faroProjectId = &#63; from the database.
	 *
	 * @param faroProjectId the faro project ID
	 */
	public static void removeByFaroProjectId(long faroProjectId) {
		getPersistence().removeByFaroProjectId(faroProjectId);
	}

	/**
	 * Returns the number of faro project email domains where faroProjectId = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @return the number of matching faro project email domains
	 */
	public static int countByFaroProjectId(long faroProjectId) {
		return getPersistence().countByFaroProjectId(faroProjectId);
	}

	/**
	 * Creates a new faro project email domain with the primary key. Does not add the faro project email domain to the database.
	 *
	 * @param faroProjectEmailDomainId the primary key for the new faro project email domain
	 * @return the new faro project email domain
	 */
	public static FaroProjectEmailDomain create(long faroProjectEmailDomainId) {
		return getPersistence().create(faroProjectEmailDomainId);
	}

	/**
	 * Removes the faro project email domain with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroProjectEmailDomainId the primary key of the faro project email domain
	 * @return the faro project email domain that was removed
	 * @throws NoSuchFaroProjectEmailDomainException if a faro project email domain with the primary key could not be found
	 */
	public static FaroProjectEmailDomain remove(long faroProjectEmailDomainId)
		throws com.liferay.osb.faro.exception.
			NoSuchFaroProjectEmailDomainException {

		return getPersistence().remove(faroProjectEmailDomainId);
	}

	public static FaroProjectEmailDomain updateImpl(
		FaroProjectEmailDomain faroProjectEmailDomain) {

		return getPersistence().updateImpl(faroProjectEmailDomain);
	}

	/**
	 * Returns the faro project email domain with the primary key or throws a <code>NoSuchFaroProjectEmailDomainException</code> if it could not be found.
	 *
	 * @param faroProjectEmailDomainId the primary key of the faro project email domain
	 * @return the faro project email domain
	 * @throws NoSuchFaroProjectEmailDomainException if a faro project email domain with the primary key could not be found
	 */
	public static FaroProjectEmailDomain findByPrimaryKey(
			long faroProjectEmailDomainId)
		throws com.liferay.osb.faro.exception.
			NoSuchFaroProjectEmailDomainException {

		return getPersistence().findByPrimaryKey(faroProjectEmailDomainId);
	}

	/**
	 * Returns the faro project email domain with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroProjectEmailDomainId the primary key of the faro project email domain
	 * @return the faro project email domain, or <code>null</code> if a faro project email domain with the primary key could not be found
	 */
	public static FaroProjectEmailDomain fetchByPrimaryKey(
		long faroProjectEmailDomainId) {

		return getPersistence().fetchByPrimaryKey(faroProjectEmailDomainId);
	}

	/**
	 * Returns all the faro project email domains where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching faro project email domains
	 */
	public static List<FaroProjectEmailDomain> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the faro project email domains where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @return the range of matching faro project email domains
	 */
	public static List<FaroProjectEmailDomain> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the faro project email domains where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro project email domains
	 */
	public static List<FaroProjectEmailDomain> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FaroProjectEmailDomain> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the faro project email domains where faroProjectId = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @return the matching faro project email domains
	 */
	public static List<FaroProjectEmailDomain> findByFaroProjectId(
		long faroProjectId) {

		return getPersistence().findByFaroProjectId(faroProjectId);
	}

	/**
	 * Returns a range of all the faro project email domains where faroProjectId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param faroProjectId the faro project ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @return the range of matching faro project email domains
	 */
	public static List<FaroProjectEmailDomain> findByFaroProjectId(
		long faroProjectId, int start, int end) {

		return getPersistence().findByFaroProjectId(faroProjectId, start, end);
	}

	/**
	 * Returns an ordered range of all the faro project email domains where faroProjectId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param faroProjectId the faro project ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro project email domains
	 */
	public static List<FaroProjectEmailDomain> findByFaroProjectId(
		long faroProjectId, int start, int end,
		OrderByComparator<FaroProjectEmailDomain> orderByComparator) {

		return getPersistence().findByFaroProjectId(
			faroProjectId, start, end, orderByComparator);
	}

	public static FaroProjectEmailDomainPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		FaroProjectEmailDomainPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile FaroProjectEmailDomainPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:2038548929