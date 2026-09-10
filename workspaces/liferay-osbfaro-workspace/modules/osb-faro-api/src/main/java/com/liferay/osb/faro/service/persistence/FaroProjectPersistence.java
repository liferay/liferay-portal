/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence;

import com.liferay.osb.faro.exception.NoSuchFaroProjectException;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the faro project service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @see FaroProjectUtil
 * @generated
 */
@ProviderType
public interface FaroProjectPersistence extends BasePersistence<FaroProject> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link FaroProjectUtil} to access the faro project persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the faro project where groupId = &#63; or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @return the matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	public FaroProject findByGroupId(long groupId)
		throws NoSuchFaroProjectException;

	/**
	 * Returns the faro project where groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public FaroProject fetchByGroupId(long groupId, boolean useFinderCache);

	/**
	 * Removes the faro project where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @return the faro project that was removed
	 */
	public FaroProject removeByGroupId(long groupId)
		throws NoSuchFaroProjectException;

	/**
	 * Returns the number of faro projects where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching faro projects
	 */
	public int countByGroupId(long groupId);

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
	public java.util.List<FaroProject> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProject>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first faro project in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	public FaroProject findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator<FaroProject>
				orderByComparator)
		throws NoSuchFaroProjectException;

	/**
	 * Returns the first faro project in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public FaroProject fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProject>
			orderByComparator);

	/**
	 * Removes all the faro projects where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of faro projects where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching faro projects
	 */
	public int countByUserId(long userId);

	/**
	 * Returns the faro project where corpProjectUuid = &#63; or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @return the matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	public FaroProject findByCorpProjectUuid(String corpProjectUuid)
		throws NoSuchFaroProjectException;

	/**
	 * Returns the faro project where corpProjectUuid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public FaroProject fetchByCorpProjectUuid(
		String corpProjectUuid, boolean useFinderCache);

	/**
	 * Removes the faro project where corpProjectUuid = &#63; from the database.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @return the faro project that was removed
	 */
	public FaroProject removeByCorpProjectUuid(String corpProjectUuid)
		throws NoSuchFaroProjectException;

	/**
	 * Returns the number of faro projects where corpProjectUuid = &#63;.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @return the number of matching faro projects
	 */
	public int countByCorpProjectUuid(String corpProjectUuid);

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
	public java.util.List<FaroProject> findByServerLocation(
		String serverLocation, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProject>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first faro project in the ordered set where serverLocation = &#63;.
	 *
	 * @param serverLocation the server location
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	public FaroProject findByServerLocation_First(
			String serverLocation,
			com.liferay.portal.kernel.util.OrderByComparator<FaroProject>
				orderByComparator)
		throws NoSuchFaroProjectException;

	/**
	 * Returns the first faro project in the ordered set where serverLocation = &#63;.
	 *
	 * @param serverLocation the server location
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public FaroProject fetchByServerLocation_First(
		String serverLocation,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProject>
			orderByComparator);

	/**
	 * Removes all the faro projects where serverLocation = &#63; from the database.
	 *
	 * @param serverLocation the server location
	 */
	public void removeByServerLocation(String serverLocation);

	/**
	 * Returns the number of faro projects where serverLocation = &#63;.
	 *
	 * @param serverLocation the server location
	 * @return the number of matching faro projects
	 */
	public int countByServerLocation(String serverLocation);

	/**
	 * Returns the faro project where weDeployKey = &#63; or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param weDeployKey the we deploy key
	 * @return the matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	public FaroProject findByWeDeployKey(String weDeployKey)
		throws NoSuchFaroProjectException;

	/**
	 * Returns the faro project where weDeployKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param weDeployKey the we deploy key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public FaroProject fetchByWeDeployKey(
		String weDeployKey, boolean useFinderCache);

	/**
	 * Removes the faro project where weDeployKey = &#63; from the database.
	 *
	 * @param weDeployKey the we deploy key
	 * @return the faro project that was removed
	 */
	public FaroProject removeByWeDeployKey(String weDeployKey)
		throws NoSuchFaroProjectException;

	/**
	 * Returns the number of faro projects where weDeployKey = &#63;.
	 *
	 * @param weDeployKey the we deploy key
	 * @return the number of matching faro projects
	 */
	public int countByWeDeployKey(String weDeployKey);

	/**
	 * Creates a new faro project with the primary key. Does not add the faro project to the database.
	 *
	 * @param faroProjectId the primary key for the new faro project
	 * @return the new faro project
	 */
	public FaroProject create(long faroProjectId);

	/**
	 * Removes the faro project with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project that was removed
	 * @throws NoSuchFaroProjectException if a faro project with the primary key could not be found
	 */
	public FaroProject remove(long faroProjectId)
		throws NoSuchFaroProjectException;

	public FaroProject updateImpl(FaroProject faroProject);

	/**
	 * Returns the faro project with the primary key or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project
	 * @throws NoSuchFaroProjectException if a faro project with the primary key could not be found
	 */
	public FaroProject findByPrimaryKey(long faroProjectId)
		throws NoSuchFaroProjectException;

	/**
	 * Returns the faro project with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project, or <code>null</code> if a faro project with the primary key could not be found
	 */
	public FaroProject fetchByPrimaryKey(long faroProjectId);

	/**
	 * Returns the faro project where groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public default FaroProject fetchByGroupId(long groupId) {
		return fetchByGroupId(groupId, true);
	}

	/**
	 * Returns the faro project where corpProjectUuid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public default FaroProject fetchByCorpProjectUuid(String corpProjectUuid) {
		return fetchByCorpProjectUuid(corpProjectUuid, true);
	}

	/**
	 * Returns the faro project where weDeployKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param weDeployKey the we deploy key
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	public default FaroProject fetchByWeDeployKey(String weDeployKey) {
		return fetchByWeDeployKey(weDeployKey, true);
	}

	/**
	 * Returns all the faro projects where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching faro projects
	 */
	public default java.util.List<FaroProject> findByUserId(long userId) {
		return findByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<FaroProject> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null, true);
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
	public default java.util.List<FaroProject> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProject>
			orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the faro projects where serverLocation = &#63;.
	 *
	 * @param serverLocation the server location
	 * @return the matching faro projects
	 */
	public default java.util.List<FaroProject> findByServerLocation(
		String serverLocation) {

		return findByServerLocation(
			serverLocation, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<FaroProject> findByServerLocation(
		String serverLocation, int start, int end) {

		return findByServerLocation(serverLocation, start, end, null, true);
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
	public default java.util.List<FaroProject> findByServerLocation(
		String serverLocation, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroProject>
			orderByComparator) {

		return findByServerLocation(
			serverLocation, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2094140413