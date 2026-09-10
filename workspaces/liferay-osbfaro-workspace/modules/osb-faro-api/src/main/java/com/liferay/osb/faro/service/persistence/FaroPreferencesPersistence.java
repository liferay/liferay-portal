/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence;

import com.liferay.osb.faro.exception.NoSuchFaroPreferencesException;
import com.liferay.osb.faro.model.FaroPreferences;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the faro preferences service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @see FaroPreferencesUtil
 * @generated
 */
@ProviderType
public interface FaroPreferencesPersistence
	extends BasePersistence<FaroPreferences> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link FaroPreferencesUtil} to access the faro preferences persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the faro preferenceses where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro preferenceses
	 * @param end the upper bound of the range of faro preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro preferenceses
	 */
	public java.util.List<FaroPreferences> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroPreferences>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first faro preferences in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro preferences
	 * @throws NoSuchFaroPreferencesException if a matching faro preferences could not be found
	 */
	public FaroPreferences findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<FaroPreferences>
				orderByComparator)
		throws NoSuchFaroPreferencesException;

	/**
	 * Returns the first faro preferences in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro preferences, or <code>null</code> if a matching faro preferences could not be found
	 */
	public FaroPreferences fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<FaroPreferences>
			orderByComparator);

	/**
	 * Removes all the faro preferenceses where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of faro preferenceses where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching faro preferenceses
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns the faro preferences where groupId = &#63; and ownerId = &#63; or throws a <code>NoSuchFaroPreferencesException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param ownerId the owner ID
	 * @return the matching faro preferences
	 * @throws NoSuchFaroPreferencesException if a matching faro preferences could not be found
	 */
	public FaroPreferences findByG_O(long groupId, long ownerId)
		throws NoSuchFaroPreferencesException;

	/**
	 * Returns the faro preferences where groupId = &#63; and ownerId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param ownerId the owner ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro preferences, or <code>null</code> if a matching faro preferences could not be found
	 */
	public FaroPreferences fetchByG_O(
		long groupId, long ownerId, boolean useFinderCache);

	/**
	 * Removes the faro preferences where groupId = &#63; and ownerId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param ownerId the owner ID
	 * @return the faro preferences that was removed
	 */
	public FaroPreferences removeByG_O(long groupId, long ownerId)
		throws NoSuchFaroPreferencesException;

	/**
	 * Returns the number of faro preferenceses where groupId = &#63; and ownerId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param ownerId the owner ID
	 * @return the number of matching faro preferenceses
	 */
	public int countByG_O(long groupId, long ownerId);

	/**
	 * Creates a new faro preferences with the primary key. Does not add the faro preferences to the database.
	 *
	 * @param faroPreferencesId the primary key for the new faro preferences
	 * @return the new faro preferences
	 */
	public FaroPreferences create(long faroPreferencesId);

	/**
	 * Removes the faro preferences with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroPreferencesId the primary key of the faro preferences
	 * @return the faro preferences that was removed
	 * @throws NoSuchFaroPreferencesException if a faro preferences with the primary key could not be found
	 */
	public FaroPreferences remove(long faroPreferencesId)
		throws NoSuchFaroPreferencesException;

	public FaroPreferences updateImpl(FaroPreferences faroPreferences);

	/**
	 * Returns the faro preferences with the primary key or throws a <code>NoSuchFaroPreferencesException</code> if it could not be found.
	 *
	 * @param faroPreferencesId the primary key of the faro preferences
	 * @return the faro preferences
	 * @throws NoSuchFaroPreferencesException if a faro preferences with the primary key could not be found
	 */
	public FaroPreferences findByPrimaryKey(long faroPreferencesId)
		throws NoSuchFaroPreferencesException;

	/**
	 * Returns the faro preferences with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroPreferencesId the primary key of the faro preferences
	 * @return the faro preferences, or <code>null</code> if a faro preferences with the primary key could not be found
	 */
	public FaroPreferences fetchByPrimaryKey(long faroPreferencesId);

	/**
	 * Returns the faro preferences where groupId = &#63; and ownerId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param ownerId the owner ID
	 * @return the matching faro preferences, or <code>null</code> if a matching faro preferences could not be found
	 */
	public default FaroPreferences fetchByG_O(long groupId, long ownerId) {
		return fetchByG_O(groupId, ownerId, true);
	}

	/**
	 * Returns all the faro preferenceses where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching faro preferenceses
	 */
	public default java.util.List<FaroPreferences> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the faro preferenceses where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro preferenceses
	 * @param end the upper bound of the range of faro preferenceses (not inclusive)
	 * @return the range of matching faro preferenceses
	 */
	public default java.util.List<FaroPreferences> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the faro preferenceses where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro preferenceses
	 * @param end the upper bound of the range of faro preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro preferenceses
	 */
	public default java.util.List<FaroPreferences> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FaroPreferences>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1391568508