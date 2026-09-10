/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the faro project service. This utility wraps <code>com.liferay.osb.faro.service.persistence.impl.FaroProjectPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @see FaroProjectPersistence
 * @generated
 */
public class FaroProjectUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<FaroProject> faroProjects) {
		getPersistence().cacheResult(faroProjects);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(FaroProject faroProject) {
		getPersistence().cacheResult(faroProject);
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
	public static void clearCache(FaroProject faroProject) {
		getPersistence().clearCache(faroProject);
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
	public static Map<Serializable, FaroProject> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FaroProject> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FaroProject> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FaroProject> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FaroProject> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FaroProject update(FaroProject faroProject) {
		return getPersistence().update(faroProject);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FaroProject update(
		FaroProject faroProject, ServiceContext serviceContext) {

		return getPersistence().update(faroProject, serviceContext);
	}

	/**
	 * Returns the faro project where groupId = &#63; or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @return the matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	public static FaroProject findByGroupId(long groupId)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectException {

		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns the faro project where groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public static FaroProject fetchByGroupId(
		long groupId, boolean useFinderCache) {

		return getPersistence().fetchByGroupId(groupId, useFinderCache);
	}

	/**
	 * Removes the faro project where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @return the faro project that was removed
	 */
	public static FaroProject removeByGroupId(long groupId)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectException {

		return getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of faro projects where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching faro projects
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the faro projects where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of faro projects
	 * @param end the upper bound of the range of faro projects (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro projects
	 */
	public static List<FaroProject> findByUserId(
		long userId, int start, int end,
		OrderByComparator<FaroProject> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro project in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	public static FaroProject findByUserId_First(
			long userId, OrderByComparator<FaroProject> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first faro project in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public static FaroProject fetchByUserId_First(
		long userId, OrderByComparator<FaroProject> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Removes all the faro projects where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of faro projects where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching faro projects
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns the faro project where corpProjectUuid = &#63; or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @return the matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	public static FaroProject findByCorpProjectUuid(String corpProjectUuid)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectException {

		return getPersistence().findByCorpProjectUuid(corpProjectUuid);
	}

	/**
	 * Returns the faro project where corpProjectUuid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public static FaroProject fetchByCorpProjectUuid(
		String corpProjectUuid, boolean useFinderCache) {

		return getPersistence().fetchByCorpProjectUuid(
			corpProjectUuid, useFinderCache);
	}

	/**
	 * Removes the faro project where corpProjectUuid = &#63; from the database.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @return the faro project that was removed
	 */
	public static FaroProject removeByCorpProjectUuid(String corpProjectUuid)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectException {

		return getPersistence().removeByCorpProjectUuid(corpProjectUuid);
	}

	/**
	 * Returns the number of faro projects where corpProjectUuid = &#63;.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @return the number of matching faro projects
	 */
	public static int countByCorpProjectUuid(String corpProjectUuid) {
		return getPersistence().countByCorpProjectUuid(corpProjectUuid);
	}

	/**
	 * Returns an ordered range of all the faro projects where serverLocation = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectModelImpl</code>.
	 * </p>
	 *
	 * @param serverLocation the server location
	 * @param start the lower bound of the range of faro projects
	 * @param end the upper bound of the range of faro projects (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro projects
	 */
	public static List<FaroProject> findByServerLocation(
		String serverLocation, int start, int end,
		OrderByComparator<FaroProject> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByServerLocation(
			serverLocation, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro project in the ordered set where serverLocation = &#63;.
	 *
	 * @param serverLocation the server location
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	public static FaroProject findByServerLocation_First(
			String serverLocation,
			OrderByComparator<FaroProject> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectException {

		return getPersistence().findByServerLocation_First(
			serverLocation, orderByComparator);
	}

	/**
	 * Returns the first faro project in the ordered set where serverLocation = &#63;.
	 *
	 * @param serverLocation the server location
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public static FaroProject fetchByServerLocation_First(
		String serverLocation,
		OrderByComparator<FaroProject> orderByComparator) {

		return getPersistence().fetchByServerLocation_First(
			serverLocation, orderByComparator);
	}

	/**
	 * Removes all the faro projects where serverLocation = &#63; from the database.
	 *
	 * @param serverLocation the server location
	 */
	public static void removeByServerLocation(String serverLocation) {
		getPersistence().removeByServerLocation(serverLocation);
	}

	/**
	 * Returns the number of faro projects where serverLocation = &#63;.
	 *
	 * @param serverLocation the server location
	 * @return the number of matching faro projects
	 */
	public static int countByServerLocation(String serverLocation) {
		return getPersistence().countByServerLocation(serverLocation);
	}

	/**
	 * Returns the faro project where weDeployKey = &#63; or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param weDeployKey the we deploy key
	 * @return the matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	public static FaroProject findByWeDeployKey(String weDeployKey)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectException {

		return getPersistence().findByWeDeployKey(weDeployKey);
	}

	/**
	 * Returns the faro project where weDeployKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param weDeployKey the we deploy key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public static FaroProject fetchByWeDeployKey(
		String weDeployKey, boolean useFinderCache) {

		return getPersistence().fetchByWeDeployKey(weDeployKey, useFinderCache);
	}

	/**
	 * Removes the faro project where weDeployKey = &#63; from the database.
	 *
	 * @param weDeployKey the we deploy key
	 * @return the faro project that was removed
	 */
	public static FaroProject removeByWeDeployKey(String weDeployKey)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectException {

		return getPersistence().removeByWeDeployKey(weDeployKey);
	}

	/**
	 * Returns the number of faro projects where weDeployKey = &#63;.
	 *
	 * @param weDeployKey the we deploy key
	 * @return the number of matching faro projects
	 */
	public static int countByWeDeployKey(String weDeployKey) {
		return getPersistence().countByWeDeployKey(weDeployKey);
	}

	/**
	 * Creates a new faro project with the primary key. Does not add the faro project to the database.
	 *
	 * @param faroProjectId the primary key for the new faro project
	 * @return the new faro project
	 */
	public static FaroProject create(long faroProjectId) {
		return getPersistence().create(faroProjectId);
	}

	/**
	 * Removes the faro project with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project that was removed
	 * @throws NoSuchFaroProjectException if a faro project with the primary key could not be found
	 */
	public static FaroProject remove(long faroProjectId)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectException {

		return getPersistence().remove(faroProjectId);
	}

	public static FaroProject updateImpl(FaroProject faroProject) {
		return getPersistence().updateImpl(faroProject);
	}

	/**
	 * Returns the faro project with the primary key or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project
	 * @throws NoSuchFaroProjectException if a faro project with the primary key could not be found
	 */
	public static FaroProject findByPrimaryKey(long faroProjectId)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectException {

		return getPersistence().findByPrimaryKey(faroProjectId);
	}

	/**
	 * Returns the faro project with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project, or <code>null</code> if a faro project with the primary key could not be found
	 */
	public static FaroProject fetchByPrimaryKey(long faroProjectId) {
		return getPersistence().fetchByPrimaryKey(faroProjectId);
	}

	/**
	 * Returns the faro project where groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public static FaroProject fetchByGroupId(long groupId) {
		return getPersistence().fetchByGroupId(groupId);
	}

	/**
	 * Returns the faro project where corpProjectUuid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public static FaroProject fetchByCorpProjectUuid(String corpProjectUuid) {
		return getPersistence().fetchByCorpProjectUuid(corpProjectUuid);
	}

	/**
	 * Returns the faro project where weDeployKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param weDeployKey the we deploy key
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public static FaroProject fetchByWeDeployKey(String weDeployKey) {
		return getPersistence().fetchByWeDeployKey(weDeployKey);
	}

	/**
	 * Returns all the faro projects where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching faro projects
	 */
	public static List<FaroProject> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns a range of all the faro projects where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of faro projects
	 * @param end the upper bound of the range of faro projects (not inclusive)
	 * @return the range of matching faro projects
	 */
	public static List<FaroProject> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

	/**
	 * Returns an ordered range of all the faro projects where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of faro projects
	 * @param end the upper bound of the range of faro projects (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro projects
	 */
	public static List<FaroProject> findByUserId(
		long userId, int start, int end,
		OrderByComparator<FaroProject> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the faro projects where serverLocation = &#63;.
	 *
	 * @param serverLocation the server location
	 * @return the matching faro projects
	 */
	public static List<FaroProject> findByServerLocation(
		String serverLocation) {

		return getPersistence().findByServerLocation(serverLocation);
	}

	/**
	 * Returns a range of all the faro projects where serverLocation = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectModelImpl</code>.
	 * </p>
	 *
	 * @param serverLocation the server location
	 * @param start the lower bound of the range of faro projects
	 * @param end the upper bound of the range of faro projects (not inclusive)
	 * @return the range of matching faro projects
	 */
	public static List<FaroProject> findByServerLocation(
		String serverLocation, int start, int end) {

		return getPersistence().findByServerLocation(
			serverLocation, start, end);
	}

	/**
	 * Returns an ordered range of all the faro projects where serverLocation = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectModelImpl</code>.
	 * </p>
	 *
	 * @param serverLocation the server location
	 * @param start the lower bound of the range of faro projects
	 * @param end the upper bound of the range of faro projects (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro projects
	 */
	public static List<FaroProject> findByServerLocation(
		String serverLocation, int start, int end,
		OrderByComparator<FaroProject> orderByComparator) {

		return getPersistence().findByServerLocation(
			serverLocation, start, end, orderByComparator);
	}

	public static FaroProjectPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(FaroProjectPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile FaroProjectPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1585587381