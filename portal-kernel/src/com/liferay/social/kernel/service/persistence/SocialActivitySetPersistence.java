/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.social.kernel.exception.NoSuchActivitySetException;
import com.liferay.social.kernel.model.SocialActivitySet;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the social activity set service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivitySetUtil
 * @generated
 */
@ProviderType
public interface SocialActivitySetPersistence
	extends BasePersistence<SocialActivitySet>,
			CTPersistence<SocialActivitySet> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SocialActivitySetUtil} to access the social activity set persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the social activity sets where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity sets
	 */
	public java.util.List<SocialActivitySet> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivitySet>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity set in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set
	 * @throws NoSuchActivitySetException if a matching social activity set could not be found
	 */
	public SocialActivitySet findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialActivitySet>
				orderByComparator)
		throws NoSuchActivitySetException;

	/**
	 * Returns the first social activity set in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set, or <code>null</code> if a matching social activity set could not be found
	 */
	public SocialActivitySet fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivitySet>
			orderByComparator);

	/**
	 * Removes all the social activity sets where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of social activity sets where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching social activity sets
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the social activity sets where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity sets
	 */
	public java.util.List<SocialActivitySet> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivitySet>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity set in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set
	 * @throws NoSuchActivitySetException if a matching social activity set could not be found
	 */
	public SocialActivitySet findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialActivitySet>
				orderByComparator)
		throws NoSuchActivitySetException;

	/**
	 * Returns the first social activity set in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set, or <code>null</code> if a matching social activity set could not be found
	 */
	public SocialActivitySet fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivitySet>
			orderByComparator);

	/**
	 * Removes all the social activity sets where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of social activity sets where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching social activity sets
	 */
	public int countByUserId(long userId);

	/**
	 * Creates a new social activity set with the primary key. Does not add the social activity set to the database.
	 *
	 * @param activitySetId the primary key for the new social activity set
	 * @return the new social activity set
	 */
	public SocialActivitySet create(long activitySetId);

	/**
	 * Removes the social activity set with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param activitySetId the primary key of the social activity set
	 * @return the social activity set that was removed
	 * @throws NoSuchActivitySetException if a social activity set with the primary key could not be found
	 */
	public SocialActivitySet remove(long activitySetId)
		throws NoSuchActivitySetException;

	public SocialActivitySet updateImpl(SocialActivitySet socialActivitySet);

	/**
	 * Returns the social activity set with the primary key or throws a <code>NoSuchActivitySetException</code> if it could not be found.
	 *
	 * @param activitySetId the primary key of the social activity set
	 * @return the social activity set
	 * @throws NoSuchActivitySetException if a social activity set with the primary key could not be found
	 */
	public SocialActivitySet findByPrimaryKey(long activitySetId)
		throws NoSuchActivitySetException;

	/**
	 * Returns the social activity set with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param activitySetId the primary key of the social activity set
	 * @return the social activity set, or <code>null</code> if a social activity set with the primary key could not be found
	 */
	public SocialActivitySet fetchByPrimaryKey(long activitySetId);

	/**
	 * Returns all the social activity sets where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching social activity sets
	 */
	public default java.util.List<SocialActivitySet> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activity sets where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @return the range of matching social activity sets
	 */
	public default java.util.List<SocialActivitySet> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activity sets where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activity sets
	 */
	public default java.util.List<SocialActivitySet> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivitySet>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social activity sets where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching social activity sets
	 */
	public default java.util.List<SocialActivitySet> findByUserId(long userId) {
		return findByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activity sets where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @return the range of matching social activity sets
	 */
	public default java.util.List<SocialActivitySet> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activity sets where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activity sets
	 */
	public default java.util.List<SocialActivitySet> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivitySet>
			orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1443295799