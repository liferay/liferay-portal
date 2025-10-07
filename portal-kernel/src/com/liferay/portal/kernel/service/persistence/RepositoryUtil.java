/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the repository service. This utility wraps <code>com.liferay.portal.service.persistence.impl.RepositoryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RepositoryPersistence
 * @generated
 */
public class RepositoryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(Repository repository) {
		getPersistence().clearCache(repository);
	}

	/**
	 * @see BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, Repository> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<Repository> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<Repository> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<Repository> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<Repository> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static Repository update(Repository repository) {
		return getPersistence().update(repository);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static Repository update(
		Repository repository, ServiceContext serviceContext) {

		return getPersistence().update(repository, serviceContext);
	}

	/**
	 * Returns all the repositories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching repositories
	 */
	public static List<Repository> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the repositories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @return the range of matching repositories
	 */
	public static List<Repository> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the repositories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching repositories
	 */
	public static List<Repository> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Repository> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the repositories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching repositories
	 */
	public static List<Repository> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Repository> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first repository in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	public static Repository findByUuid_First(
			String uuid, OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first repository in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByUuid_First(
		String uuid, OrderByComparator<Repository> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last repository in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	public static Repository findByUuid_Last(
			String uuid, OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last repository in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByUuid_Last(
		String uuid, OrderByComparator<Repository> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the repositories before and after the current repository in the ordered set where uuid = &#63;.
	 *
	 * @param repositoryId the primary key of the current repository
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next repository
	 * @throws NoSuchRepositoryException if a repository with the primary key could not be found
	 */
	public static Repository[] findByUuid_PrevAndNext(
			long repositoryId, String uuid,
			OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByUuid_PrevAndNext(
			repositoryId, uuid, orderByComparator);
	}

	/**
	 * Removes all the repositories where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of repositories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching repositories
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the repository where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchRepositoryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	public static Repository findByUUID_G(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the repository where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the repository where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the repository where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the repository that was removed
	 */
	public static Repository removeByUUID_G(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of repositories where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching repositories
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the repositories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching repositories
	 */
	public static List<Repository> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the repositories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @return the range of matching repositories
	 */
	public static List<Repository> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the repositories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching repositories
	 */
	public static List<Repository> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Repository> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the repositories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching repositories
	 */
	public static List<Repository> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Repository> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first repository in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	public static Repository findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first repository in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Repository> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last repository in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	public static Repository findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last repository in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<Repository> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the repositories before and after the current repository in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param repositoryId the primary key of the current repository
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next repository
	 * @throws NoSuchRepositoryException if a repository with the primary key could not be found
	 */
	public static Repository[] findByUuid_C_PrevAndNext(
			long repositoryId, String uuid, long companyId,
			OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByUuid_C_PrevAndNext(
			repositoryId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the repositories where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of repositories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching repositories
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the repositories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching repositories
	 */
	public static List<Repository> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the repositories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @return the range of matching repositories
	 */
	public static List<Repository> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the repositories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching repositories
	 */
	public static List<Repository> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<Repository> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the repositories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching repositories
	 */
	public static List<Repository> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<Repository> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first repository in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	public static Repository findByGroupId_First(
			long groupId, OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first repository in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByGroupId_First(
		long groupId, OrderByComparator<Repository> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last repository in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	public static Repository findByGroupId_Last(
			long groupId, OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last repository in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByGroupId_Last(
		long groupId, OrderByComparator<Repository> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the repositories before and after the current repository in the ordered set where groupId = &#63;.
	 *
	 * @param repositoryId the primary key of the current repository
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next repository
	 * @throws NoSuchRepositoryException if a repository with the primary key could not be found
	 */
	public static Repository[] findByGroupId_PrevAndNext(
			long repositoryId, long groupId,
			OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByGroupId_PrevAndNext(
			repositoryId, groupId, orderByComparator);
	}

	/**
	 * Removes all the repositories where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of repositories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching repositories
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns all the repositories where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @return the matching repositories
	 */
	public static List<Repository> findByPortletId(String portletId) {
		return getPersistence().findByPortletId(portletId);
	}

	/**
	 * Returns a range of all the repositories where portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @return the range of matching repositories
	 */
	public static List<Repository> findByPortletId(
		String portletId, int start, int end) {

		return getPersistence().findByPortletId(portletId, start, end);
	}

	/**
	 * Returns an ordered range of all the repositories where portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching repositories
	 */
	public static List<Repository> findByPortletId(
		String portletId, int start, int end,
		OrderByComparator<Repository> orderByComparator) {

		return getPersistence().findByPortletId(
			portletId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the repositories where portletId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching repositories
	 */
	public static List<Repository> findByPortletId(
		String portletId, int start, int end,
		OrderByComparator<Repository> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPortletId(
			portletId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first repository in the ordered set where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	public static Repository findByPortletId_First(
			String portletId, OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByPortletId_First(
			portletId, orderByComparator);
	}

	/**
	 * Returns the first repository in the ordered set where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByPortletId_First(
		String portletId, OrderByComparator<Repository> orderByComparator) {

		return getPersistence().fetchByPortletId_First(
			portletId, orderByComparator);
	}

	/**
	 * Returns the last repository in the ordered set where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	public static Repository findByPortletId_Last(
			String portletId, OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByPortletId_Last(
			portletId, orderByComparator);
	}

	/**
	 * Returns the last repository in the ordered set where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByPortletId_Last(
		String portletId, OrderByComparator<Repository> orderByComparator) {

		return getPersistence().fetchByPortletId_Last(
			portletId, orderByComparator);
	}

	/**
	 * Returns the repositories before and after the current repository in the ordered set where portletId = &#63;.
	 *
	 * @param repositoryId the primary key of the current repository
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next repository
	 * @throws NoSuchRepositoryException if a repository with the primary key could not be found
	 */
	public static Repository[] findByPortletId_PrevAndNext(
			long repositoryId, String portletId,
			OrderByComparator<Repository> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByPortletId_PrevAndNext(
			repositoryId, portletId, orderByComparator);
	}

	/**
	 * Removes all the repositories where portletId = &#63; from the database.
	 *
	 * @param portletId the portlet ID
	 */
	public static void removeByPortletId(String portletId) {
		getPersistence().removeByPortletId(portletId);
	}

	/**
	 * Returns the number of repositories where portletId = &#63;.
	 *
	 * @param portletId the portlet ID
	 * @return the number of matching repositories
	 */
	public static int countByPortletId(String portletId) {
		return getPersistence().countByPortletId(portletId);
	}

	/**
	 * Returns the repository where groupId = &#63; and name = &#63; and portletId = &#63; or throws a <code>NoSuchRepositoryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @return the matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	public static Repository findByG_N_P(
			long groupId, String name, String portletId)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByG_N_P(groupId, name, portletId);
	}

	/**
	 * Returns the repository where groupId = &#63; and name = &#63; and portletId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @return the matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByG_N_P(
		long groupId, String name, String portletId) {

		return getPersistence().fetchByG_N_P(groupId, name, portletId);
	}

	/**
	 * Returns the repository where groupId = &#63; and name = &#63; and portletId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByG_N_P(
		long groupId, String name, String portletId, boolean useFinderCache) {

		return getPersistence().fetchByG_N_P(
			groupId, name, portletId, useFinderCache);
	}

	/**
	 * Removes the repository where groupId = &#63; and name = &#63; and portletId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @return the repository that was removed
	 */
	public static Repository removeByG_N_P(
			long groupId, String name, String portletId)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().removeByG_N_P(groupId, name, portletId);
	}

	/**
	 * Returns the number of repositories where groupId = &#63; and name = &#63; and portletId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @return the number of matching repositories
	 */
	public static int countByG_N_P(
		long groupId, String name, String portletId) {

		return getPersistence().countByG_N_P(groupId, name, portletId);
	}

	/**
	 * Returns the repository where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchRepositoryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching repository
	 * @throws NoSuchRepositoryException if a matching repository could not be found
	 */
	public static Repository findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the repository where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the repository where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching repository, or <code>null</code> if a matching repository could not be found
	 */
	public static Repository fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the repository where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the repository that was removed
	 */
	public static Repository removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of repositories where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching repositories
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Caches the repository in the entity cache if it is enabled.
	 *
	 * @param repository the repository
	 */
	public static void cacheResult(Repository repository) {
		getPersistence().cacheResult(repository);
	}

	/**
	 * Caches the repositories in the entity cache if it is enabled.
	 *
	 * @param repositories the repositories
	 */
	public static void cacheResult(List<Repository> repositories) {
		getPersistence().cacheResult(repositories);
	}

	/**
	 * Creates a new repository with the primary key. Does not add the repository to the database.
	 *
	 * @param repositoryId the primary key for the new repository
	 * @return the new repository
	 */
	public static Repository create(long repositoryId) {
		return getPersistence().create(repositoryId);
	}

	/**
	 * Removes the repository with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param repositoryId the primary key of the repository
	 * @return the repository that was removed
	 * @throws NoSuchRepositoryException if a repository with the primary key could not be found
	 */
	public static Repository remove(long repositoryId)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().remove(repositoryId);
	}

	public static Repository updateImpl(Repository repository) {
		return getPersistence().updateImpl(repository);
	}

	/**
	 * Returns the repository with the primary key or throws a <code>NoSuchRepositoryException</code> if it could not be found.
	 *
	 * @param repositoryId the primary key of the repository
	 * @return the repository
	 * @throws NoSuchRepositoryException if a repository with the primary key could not be found
	 */
	public static Repository findByPrimaryKey(long repositoryId)
		throws com.liferay.portal.kernel.exception.NoSuchRepositoryException {

		return getPersistence().findByPrimaryKey(repositoryId);
	}

	/**
	 * Returns the repository with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param repositoryId the primary key of the repository
	 * @return the repository, or <code>null</code> if a repository with the primary key could not be found
	 */
	public static Repository fetchByPrimaryKey(long repositoryId) {
		return getPersistence().fetchByPrimaryKey(repositoryId);
	}

	/**
	 * Returns all the repositories.
	 *
	 * @return the repositories
	 */
	public static List<Repository> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the repositories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @return the range of repositories
	 */
	public static List<Repository> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the repositories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of repositories
	 */
	public static List<Repository> findAll(
		int start, int end, OrderByComparator<Repository> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the repositories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of repositories
	 */
	public static List<Repository> findAll(
		int start, int end, OrderByComparator<Repository> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the repositories from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of repositories.
	 *
	 * @return the number of repositories
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static RepositoryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(RepositoryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile RepositoryPersistence _persistence;

}