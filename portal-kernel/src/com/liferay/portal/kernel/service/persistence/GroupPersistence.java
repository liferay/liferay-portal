/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the group service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see GroupUtil
 * @generated
 */
@ProviderType
public interface GroupPersistence
	extends BasePersistence<Group>, CTPersistence<Group> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link GroupUtil} to access the group persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of groups where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching groups
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the group where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByUUID_G(String uuid, long groupId)
		throws NoSuchGroupException;

	/**
	 * Returns the group where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the group where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the group that was removed
	 */
	public Group removeByUUID_G(String uuid, long groupId)
		throws NoSuchGroupException;

	/**
	 * Returns the number of groups where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching groups
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of groups where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of groups where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns an ordered range of all the groups where liveGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param liveGroupId the live group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByLiveGroupId(
		long liveGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByLiveGroupId_First(
			long liveGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByLiveGroupId_First(
		long liveGroupId,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where liveGroupId = &#63; from the database.
	 *
	 * @param liveGroupId the live group ID
	 */
	public void removeByLiveGroupId(long liveGroupId);

	/**
	 * Returns the number of groups where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @return the number of matching groups
	 */
	public int countByLiveGroupId(long liveGroupId);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_C(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_C_First(
			long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_C_First(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public void removeByC_C(long companyId, long classNameId);

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching groups
	 */
	public int countByC_C(long companyId, long classNameId);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_P(
		long companyId, long parentGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_P_First(
			long companyId, long parentGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_P_First(
		long companyId, long parentGroupId,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 */
	public void removeByC_P(long companyId, long parentGroupId);

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	public int countByC_P(long companyId, long parentGroupId);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and groupKey = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_GK(
		long companyId, String[] groupKeys, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the group where companyId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_GK(long companyId, String groupKey)
		throws NoSuchGroupException;

	/**
	 * Returns the group where companyId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_GK(
		long companyId, String groupKey, boolean useFinderCache);

	/**
	 * Removes the group where companyId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	public Group removeByC_GK(long companyId, String groupKey)
		throws NoSuchGroupException;

	/**
	 * Returns the number of groups where companyId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	public int countByC_GK(long companyId, String groupKey);

	/**
	 * Returns the number of groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @return the number of matching groups
	 */
	public int countByC_GK(long companyId, String[] groupKeys);

	/**
	 * Returns the group where companyId = &#63; and friendlyURL = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_F(long companyId, String friendlyURL)
		throws NoSuchGroupException;

	/**
	 * Returns the group where companyId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_F(
		long companyId, String friendlyURL, boolean useFinderCache);

	/**
	 * Removes the group where companyId = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the group that was removed
	 */
	public Group removeByC_F(long companyId, String friendlyURL)
		throws NoSuchGroupException;

	/**
	 * Returns the number of groups where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the number of matching groups
	 */
	public int countByC_F(long companyId, String friendlyURL);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_S(
		long companyId, boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_S_First(
			long companyId, boolean site,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_S_First(
		long companyId, boolean site,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 */
	public void removeByC_S(long companyId, boolean site);

	/**
	 * Returns the number of groups where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	public int countByC_S(long companyId, boolean site);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_A(
		long companyId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_A_First(
			long companyId, boolean active,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_A_First(
		long companyId, boolean active,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	public void removeByC_A(long companyId, boolean active);

	/**
	 * Returns the number of groups where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching groups
	 */
	public int countByC_A(long companyId, boolean active);

	/**
	 * Returns an ordered range of all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_CPK(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_CPK_First(
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_CPK_First(
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByC_CPK(long classNameId, long classPK);

	/**
	 * Returns the number of groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching groups
	 */
	public int countByC_CPK(long classNameId, long classPK);

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_C_C(long companyId, long classNameId, long classPK)
		throws NoSuchGroupException;

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_C_C(
		long companyId, long classNameId, long classPK, boolean useFinderCache);

	/**
	 * Removes the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the group that was removed
	 */
	public Group removeByC_C_C(long companyId, long classNameId, long classPK)
		throws NoSuchGroupException;

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching groups
	 */
	public int countByC_C_C(long companyId, long classNameId, long classPK);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_C_P_First(
			long companyId, long classNameId, long parentGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_C_P_First(
		long companyId, long classNameId, long parentGroupId,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 */
	public void removeByC_C_P(
		long companyId, long classNameId, long parentGroupId);

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	public int countByC_C_P(
		long companyId, long classNameId, long parentGroupId);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_C_S_First(
			long companyId, long classNameId, boolean site,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_C_S_First(
		long companyId, long classNameId, boolean site,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 */
	public void removeByC_C_S(long companyId, long classNameId, boolean site);

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	public int countByC_C_S(long companyId, long classNameId, boolean site);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_P_S_First(
			long companyId, long parentGroupId, boolean site,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_P_S_First(
		long companyId, long parentGroupId, boolean site,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 */
	public void removeByC_P_S(long companyId, long parentGroupId, boolean site);

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	public int countByC_P_S(long companyId, long parentGroupId, boolean site);

	/**
	 * Returns the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_L_GK(long companyId, long liveGroupId, String groupKey)
		throws NoSuchGroupException;

	/**
	 * Returns the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_L_GK(
		long companyId, long liveGroupId, String groupKey,
		boolean useFinderCache);

	/**
	 * Removes the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	public Group removeByC_L_GK(
			long companyId, long liveGroupId, String groupKey)
		throws NoSuchGroupException;

	/**
	 * Returns the number of groups where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	public int countByC_L_GK(long companyId, long liveGroupId, String groupKey);

	/**
	 * Returns all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @return the matching groups
	 */
	public java.util.List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site);

	/**
	 * Returns a range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public java.util.List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_LikeT_S_First(
			long companyId, String treePath, boolean site,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_LikeT_S_First(
		long companyId, String treePath, boolean site,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 */
	public void removeByC_LikeT_S(
		long companyId, String treePath, boolean site);

	/**
	 * Returns the number of groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @return the number of matching groups
	 */
	public int countByC_LikeT_S(long companyId, String treePath, boolean site);

	/**
	 * Returns all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @return the matching groups
	 */
	public java.util.List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site);

	/**
	 * Returns a range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public java.util.List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_LikeN_S_First(
			long companyId, String name, boolean site,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_LikeN_S_First(
		long companyId, String name, boolean site,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and name LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 */
	public void removeByC_LikeN_S(long companyId, String name, boolean site);

	/**
	 * Returns the number of groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @return the number of matching groups
	 */
	public int countByC_LikeN_S(long companyId, String name, boolean site);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_S_A(
		long companyId, boolean site, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_S_A_First(
			long companyId, boolean site, boolean active,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_S_A_First(
		long companyId, boolean site, boolean active,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and site = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 */
	public void removeByC_S_A(long companyId, boolean site, boolean active);

	/**
	 * Returns the number of groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @return the number of matching groups
	 */
	public int countByC_S_A(long companyId, boolean site, boolean active);

	/**
	 * Returns all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	public java.util.List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId);

	/**
	 * Returns a range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public java.util.List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end);

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByGtG_C_C_P_First(
			long groupId, long companyId, long classNameId, long parentGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByGtG_C_C_P_First(
		long groupId, long companyId, long classNameId, long parentGroupId,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 */
	public void removeByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId);

	/**
	 * Returns the number of groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	public int countByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId);

	/**
	 * Returns all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the matching groups
	 */
	public java.util.List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site);

	/**
	 * Returns a range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public java.util.List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end);

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByGtG_C_P_S_First(
			long groupId, long companyId, long parentGroupId, boolean site,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByGtG_C_P_S_First(
		long groupId, long companyId, long parentGroupId, boolean site,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 */
	public void removeByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site);

	/**
	 * Returns the number of groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	public int countByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site);

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_C_L_GK(
			long companyId, long classNameId, long liveGroupId, String groupKey)
		throws NoSuchGroupException;

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_C_L_GK(
		long companyId, long classNameId, long liveGroupId, String groupKey,
		boolean useFinderCache);

	/**
	 * Removes the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	public Group removeByC_C_L_GK(
			long companyId, long classNameId, long liveGroupId, String groupKey)
		throws NoSuchGroupException;

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	public int countByC_C_L_GK(
		long companyId, long classNameId, long liveGroupId, String groupKey);

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @return the matching groups
	 */
	public java.util.List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site);

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public java.util.List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_P_LikeN_S_First(
			long companyId, long parentGroupId, String name, boolean site,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_P_LikeN_S_First(
		long companyId, long parentGroupId, String name, boolean site,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 */
	public void removeByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site);

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @return the number of matching groups
	 */
	public int countByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site);

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public java.util.List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByC_P_S_I_First(
			long companyId, long parentGroupId, boolean site,
			boolean inheritContent,
			com.liferay.portal.kernel.util.OrderByComparator<Group>
				orderByComparator)
		throws NoSuchGroupException;

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByC_P_S_I_First(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator);

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 */
	public void removeByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent);

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @return the number of matching groups
	 */
	public int countByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent);

	/**
	 * Returns the group where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public Group findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchGroupException;

	/**
	 * Returns the group where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public Group fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the group where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the group that was removed
	 */
	public Group removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchGroupException;

	/**
	 * Returns the number of groups where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new group with the primary key. Does not add the group to the database.
	 *
	 * @param groupId the primary key for the new group
	 * @return the new group
	 */
	public Group create(long groupId);

	/**
	 * Removes the group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param groupId the primary key of the group
	 * @return the group that was removed
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	public Group remove(long groupId) throws NoSuchGroupException;

	public Group updateImpl(Group group);

	/**
	 * Returns the group with the primary key or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param groupId the primary key of the group
	 * @return the group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	public Group findByPrimaryKey(long groupId) throws NoSuchGroupException;

	/**
	 * Returns the group with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param groupId the primary key of the group
	 * @return the group, or <code>null</code> if a group with the primary key could not be found
	 */
	public Group fetchByPrimaryKey(long groupId);

	/**
	 * Returns the primaryKeys of organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of organizations associated with the group
	 */
	public long[] getOrganizationPrimaryKeys(long pk);

	/**
	 * Returns all the organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the organizations associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.Organization>
		getOrganizations(long pk);

	/**
	 * Returns a range of all the organizations associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of organizations associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.Organization>
		getOrganizations(long pk, int start, int end);

	/**
	 * Returns an ordered range of all the organizations associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of organizations associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.Organization>
		getOrganizations(
			long pk, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Organization>
					orderByComparator);

	/**
	 * Returns the number of organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of organizations associated with the group
	 */
	public int getOrganizationsSize(long pk);

	/**
	 * Returns <code>true</code> if the organization is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 * @return <code>true</code> if the organization is associated with the group; <code>false</code> otherwise
	 */
	public boolean containsOrganization(long pk, long organizationPK);

	/**
	 * Returns <code>true</code> if the group has any organizations associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with organizations
	 * @return <code>true</code> if the group has any organizations associated with it; <code>false</code> otherwise
	 */
	public boolean containsOrganizations(long pk);

	/**
	 * Adds an association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 * @return <code>true</code> if an association between the group and the organization was added; <code>false</code> if they were already associated
	 */
	public boolean addOrganization(long pk, long organizationPK);

	/**
	 * Adds an association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organization the organization
	 * @return <code>true</code> if an association between the group and the organization was added; <code>false</code> if they were already associated
	 */
	public boolean addOrganization(
		long pk, com.liferay.portal.kernel.model.Organization organization);

	/**
	 * Adds an association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations
	 * @return <code>true</code> if at least one association between the group and the organizations was added; <code>false</code> if they were all already associated
	 */
	public boolean addOrganizations(long pk, long[] organizationPKs);

	/**
	 * Adds an association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations
	 * @return <code>true</code> if at least one association between the group and the organizations was added; <code>false</code> if they were all already associated
	 */
	public boolean addOrganizations(
		long pk,
		java.util.List<com.liferay.portal.kernel.model.Organization>
			organizations);

	/**
	 * Clears all associations between the group and its organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated organizations from
	 */
	public void clearOrganizations(long pk);

	/**
	 * Removes the association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 */
	public void removeOrganization(long pk, long organizationPK);

	/**
	 * Removes the association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organization the organization
	 */
	public void removeOrganization(
		long pk, com.liferay.portal.kernel.model.Organization organization);

	/**
	 * Removes the association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations
	 */
	public void removeOrganizations(long pk, long[] organizationPKs);

	/**
	 * Removes the association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations
	 */
	public void removeOrganizations(
		long pk,
		java.util.List<com.liferay.portal.kernel.model.Organization>
			organizations);

	/**
	 * Sets the organizations associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations to be associated with the group
	 */
	public void setOrganizations(long pk, long[] organizationPKs);

	/**
	 * Sets the organizations associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations to be associated with the group
	 */
	public void setOrganizations(
		long pk,
		java.util.List<com.liferay.portal.kernel.model.Organization>
			organizations);

	/**
	 * Returns the primaryKeys of roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of roles associated with the group
	 */
	public long[] getRolePrimaryKeys(long pk);

	/**
	 * Returns all the roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the roles associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.Role> getRoles(
		long pk);

	/**
	 * Returns a range of all the roles associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of roles associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.Role> getRoles(
		long pk, int start, int end);

	/**
	 * Returns an ordered range of all the roles associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of roles associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.Role> getRoles(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.Role> orderByComparator);

	/**
	 * Returns the number of roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of roles associated with the group
	 */
	public int getRolesSize(long pk);

	/**
	 * Returns <code>true</code> if the role is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 * @return <code>true</code> if the role is associated with the group; <code>false</code> otherwise
	 */
	public boolean containsRole(long pk, long rolePK);

	/**
	 * Returns <code>true</code> if the group has any roles associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with roles
	 * @return <code>true</code> if the group has any roles associated with it; <code>false</code> otherwise
	 */
	public boolean containsRoles(long pk);

	/**
	 * Adds an association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 * @return <code>true</code> if an association between the group and the role was added; <code>false</code> if they were already associated
	 */
	public boolean addRole(long pk, long rolePK);

	/**
	 * Adds an association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param role the role
	 * @return <code>true</code> if an association between the group and the role was added; <code>false</code> if they were already associated
	 */
	public boolean addRole(long pk, com.liferay.portal.kernel.model.Role role);

	/**
	 * Adds an association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles
	 * @return <code>true</code> if at least one association between the group and the roles was added; <code>false</code> if they were all already associated
	 */
	public boolean addRoles(long pk, long[] rolePKs);

	/**
	 * Adds an association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles
	 * @return <code>true</code> if at least one association between the group and the roles was added; <code>false</code> if they were all already associated
	 */
	public boolean addRoles(
		long pk, java.util.List<com.liferay.portal.kernel.model.Role> roles);

	/**
	 * Clears all associations between the group and its roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated roles from
	 */
	public void clearRoles(long pk);

	/**
	 * Removes the association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 */
	public void removeRole(long pk, long rolePK);

	/**
	 * Removes the association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param role the role
	 */
	public void removeRole(long pk, com.liferay.portal.kernel.model.Role role);

	/**
	 * Removes the association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles
	 */
	public void removeRoles(long pk, long[] rolePKs);

	/**
	 * Removes the association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles
	 */
	public void removeRoles(
		long pk, java.util.List<com.liferay.portal.kernel.model.Role> roles);

	/**
	 * Sets the roles associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles to be associated with the group
	 */
	public void setRoles(long pk, long[] rolePKs);

	/**
	 * Sets the roles associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles to be associated with the group
	 */
	public void setRoles(
		long pk, java.util.List<com.liferay.portal.kernel.model.Role> roles);

	/**
	 * Returns the primaryKeys of user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of user groups associated with the group
	 */
	public long[] getUserGroupPrimaryKeys(long pk);

	/**
	 * Returns all the user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the user groups associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.UserGroup>
		getUserGroups(long pk);

	/**
	 * Returns a range of all the user groups associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of user groups associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.UserGroup>
		getUserGroups(long pk, int start, int end);

	/**
	 * Returns an ordered range of all the user groups associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of user groups associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.UserGroup>
		getUserGroups(
			long pk, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.UserGroup> orderByComparator);

	/**
	 * Returns the number of user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of user groups associated with the group
	 */
	public int getUserGroupsSize(long pk);

	/**
	 * Returns <code>true</code> if the user group is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 * @return <code>true</code> if the user group is associated with the group; <code>false</code> otherwise
	 */
	public boolean containsUserGroup(long pk, long userGroupPK);

	/**
	 * Returns <code>true</code> if the group has any user groups associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with user groups
	 * @return <code>true</code> if the group has any user groups associated with it; <code>false</code> otherwise
	 */
	public boolean containsUserGroups(long pk);

	/**
	 * Adds an association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 * @return <code>true</code> if an association between the group and the user group was added; <code>false</code> if they were already associated
	 */
	public boolean addUserGroup(long pk, long userGroupPK);

	/**
	 * Adds an association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroup the user group
	 * @return <code>true</code> if an association between the group and the user group was added; <code>false</code> if they were already associated
	 */
	public boolean addUserGroup(
		long pk, com.liferay.portal.kernel.model.UserGroup userGroup);

	/**
	 * Adds an association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups
	 * @return <code>true</code> if at least one association between the group and the user groups was added; <code>false</code> if they were all already associated
	 */
	public boolean addUserGroups(long pk, long[] userGroupPKs);

	/**
	 * Adds an association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups
	 * @return <code>true</code> if at least one association between the group and the user groups was added; <code>false</code> if they were all already associated
	 */
	public boolean addUserGroups(
		long pk,
		java.util.List<com.liferay.portal.kernel.model.UserGroup> userGroups);

	/**
	 * Clears all associations between the group and its user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated user groups from
	 */
	public void clearUserGroups(long pk);

	/**
	 * Removes the association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 */
	public void removeUserGroup(long pk, long userGroupPK);

	/**
	 * Removes the association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroup the user group
	 */
	public void removeUserGroup(
		long pk, com.liferay.portal.kernel.model.UserGroup userGroup);

	/**
	 * Removes the association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups
	 */
	public void removeUserGroups(long pk, long[] userGroupPKs);

	/**
	 * Removes the association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups
	 */
	public void removeUserGroups(
		long pk,
		java.util.List<com.liferay.portal.kernel.model.UserGroup> userGroups);

	/**
	 * Sets the user groups associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups to be associated with the group
	 */
	public void setUserGroups(long pk, long[] userGroupPKs);

	/**
	 * Sets the user groups associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups to be associated with the group
	 */
	public void setUserGroups(
		long pk,
		java.util.List<com.liferay.portal.kernel.model.UserGroup> userGroups);

	/**
	 * Returns the primaryKeys of users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of users associated with the group
	 */
	public long[] getUserPrimaryKeys(long pk);

	/**
	 * Returns all the users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the users associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.User> getUsers(
		long pk);

	/**
	 * Returns a range of all the users associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of users associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end);

	/**
	 * Returns an ordered range of all the users associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of users associated with the group
	 */
	public java.util.List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.User> orderByComparator);

	/**
	 * Returns the number of users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of users associated with the group
	 */
	public int getUsersSize(long pk);

	/**
	 * Returns <code>true</code> if the user is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if the user is associated with the group; <code>false</code> otherwise
	 */
	public boolean containsUser(long pk, long userPK);

	/**
	 * Returns <code>true</code> if the group has any users associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with users
	 * @return <code>true</code> if the group has any users associated with it; <code>false</code> otherwise
	 */
	public boolean containsUsers(long pk);

	/**
	 * Adds an association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if an association between the group and the user was added; <code>false</code> if they were already associated
	 */
	public boolean addUser(long pk, long userPK);

	/**
	 * Adds an association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param user the user
	 * @return <code>true</code> if an association between the group and the user was added; <code>false</code> if they were already associated
	 */
	public boolean addUser(long pk, com.liferay.portal.kernel.model.User user);

	/**
	 * Adds an association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users
	 * @return <code>true</code> if at least one association between the group and the users was added; <code>false</code> if they were all already associated
	 */
	public boolean addUsers(long pk, long[] userPKs);

	/**
	 * Adds an association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param users the users
	 * @return <code>true</code> if at least one association between the group and the users was added; <code>false</code> if they were all already associated
	 */
	public boolean addUsers(
		long pk, java.util.List<com.liferay.portal.kernel.model.User> users);

	/**
	 * Clears all associations between the group and its users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated users from
	 */
	public void clearUsers(long pk);

	/**
	 * Removes the association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 */
	public void removeUser(long pk, long userPK);

	/**
	 * Removes the association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param user the user
	 */
	public void removeUser(long pk, com.liferay.portal.kernel.model.User user);

	/**
	 * Removes the association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users
	 */
	public void removeUsers(long pk, long[] userPKs);

	/**
	 * Removes the association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param users the users
	 */
	public void removeUsers(
		long pk, java.util.List<com.liferay.portal.kernel.model.User> users);

	/**
	 * Sets the users associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users to be associated with the group
	 */
	public void setUsers(long pk, long[] userPKs);

	/**
	 * Sets the users associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param users the users to be associated with the group
	 */
	public void setUsers(
		long pk, java.util.List<com.liferay.portal.kernel.model.User> users);

	/**
	 * Returns the group where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public default Group fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the group where companyId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public default Group fetchByC_GK(long companyId, String groupKey) {
		return fetchByC_GK(companyId, groupKey, true);
	}

	/**
	 * Returns the group where companyId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public default Group fetchByC_F(long companyId, String friendlyURL) {
		return fetchByC_F(companyId, friendlyURL, true);
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public default Group fetchByC_C_C(
		long companyId, long classNameId, long classPK) {

		return fetchByC_C_C(companyId, classNameId, classPK, true);
	}

	/**
	 * Returns the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public default Group fetchByC_L_GK(
		long companyId, long liveGroupId, String groupKey) {

		return fetchByC_L_GK(companyId, liveGroupId, groupKey, true);
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public default Group fetchByC_C_L_GK(
		long companyId, long classNameId, long liveGroupId, String groupKey) {

		return fetchByC_C_L_GK(
			companyId, classNameId, liveGroupId, groupKey, true);
	}

	/**
	 * Returns the group where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public default Group fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the groups where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByLiveGroupId(long liveGroupId) {
		return findByLiveGroupId(
			liveGroupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where liveGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param liveGroupId the live group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByLiveGroupId(
		long liveGroupId, int start, int end) {

		return findByLiveGroupId(liveGroupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where liveGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param liveGroupId the live group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByLiveGroupId(
		long liveGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByLiveGroupId(
			liveGroupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByC_C(
		long companyId, long classNameId) {

		return findByC_C(
			companyId, classNameId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByC_C(
		long companyId, long classNameId, int start, int end) {

		return findByC_C(companyId, classNameId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByC_C(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByC_C(
			companyId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByC_P(
		long companyId, long parentGroupId) {

		return findByC_P(
			companyId, parentGroupId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByC_P(
		long companyId, long parentGroupId, int start, int end) {

		return findByC_P(companyId, parentGroupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByC_P(
		long companyId, long parentGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByC_P(
			companyId, parentGroupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByC_GK(
		long companyId, String[] groupKeys) {

		return findByC_GK(
			companyId, groupKeys,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByC_GK(
		long companyId, String[] groupKeys, int start, int end) {

		return findByC_GK(companyId, groupKeys, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByC_GK(
		long companyId, String[] groupKeys, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByC_GK(
			companyId, groupKeys, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByC_S(
		long companyId, boolean site) {

		return findByC_S(
			companyId, site,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByC_S(
		long companyId, boolean site, int start, int end) {

		return findByC_S(companyId, site, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByC_S(
		long companyId, boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByC_S(companyId, site, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByC_A(
		long companyId, boolean active) {

		return findByC_A(
			companyId, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByC_A(
		long companyId, boolean active, int start, int end) {

		return findByC_A(companyId, active, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByC_A(
		long companyId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByC_A(
			companyId, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByC_CPK(
		long classNameId, long classPK) {

		return findByC_CPK(
			classNameId, classPK,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByC_CPK(
		long classNameId, long classPK, int start, int end) {

		return findByC_CPK(classNameId, classPK, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByC_CPK(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByC_CPK(
			classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId) {

		return findByC_C_P(
			companyId, classNameId, parentGroupId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId, int start,
		int end) {

		return findByC_C_P(
			companyId, classNameId, parentGroupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByC_C_P(
			companyId, classNameId, parentGroupId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site) {

		return findByC_C_S(
			companyId, classNameId, site,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site, int start, int end) {

		return findByC_C_S(
			companyId, classNameId, site, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByC_C_S(
			companyId, classNameId, site, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site) {

		return findByC_P_S(
			companyId, parentGroupId, site,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site, int start, int end) {

		return findByC_P_S(
			companyId, parentGroupId, site, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByC_P_S(
			companyId, parentGroupId, site, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByC_S_A(
		long companyId, boolean site, boolean active) {

		return findByC_S_A(
			companyId, site, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByC_S_A(
		long companyId, boolean site, boolean active, int start, int end) {

		return findByC_S_A(companyId, site, active, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByC_S_A(
		long companyId, boolean site, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByC_S_A(
			companyId, site, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @return the matching groups
	 */
	public default java.util.List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		return findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public default java.util.List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, int start, int end) {

		return findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public default java.util.List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Group>
			orderByComparator) {

		return findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1468281963