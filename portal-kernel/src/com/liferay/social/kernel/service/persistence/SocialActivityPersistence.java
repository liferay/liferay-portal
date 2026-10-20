/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.social.kernel.exception.NoSuchActivityException;
import com.liferay.social.kernel.model.SocialActivity;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the social activity service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivityUtil
 * @generated
 */
@ProviderType
public interface SocialActivityPersistence
	extends BasePersistence<SocialActivity>, CTPersistence<SocialActivity> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SocialActivityUtil} to access the social activity persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the social activities where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public java.util.List<SocialActivity> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public SocialActivity findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
				orderByComparator)
		throws NoSuchActivityException;

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public SocialActivity fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator);

	/**
	 * Removes all the social activities where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of social activities where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching social activities
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the social activities where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public java.util.List<SocialActivity> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public SocialActivity findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
				orderByComparator)
		throws NoSuchActivityException;

	/**
	 * Returns the first social activity in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public SocialActivity fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator);

	/**
	 * Removes all the social activities where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of social activities where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching social activities
	 */
	public int countByUserId(long userId);

	/**
	 * Returns an ordered range of all the social activities where activitySetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param activitySetId the activity set ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public java.util.List<SocialActivity> findByActivitySetId(
		long activitySetId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity in the ordered set where activitySetId = &#63;.
	 *
	 * @param activitySetId the activity set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public SocialActivity findByActivitySetId_First(
			long activitySetId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
				orderByComparator)
		throws NoSuchActivityException;

	/**
	 * Returns the first social activity in the ordered set where activitySetId = &#63;.
	 *
	 * @param activitySetId the activity set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public SocialActivity fetchByActivitySetId_First(
		long activitySetId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator);

	/**
	 * Removes all the social activities where activitySetId = &#63; from the database.
	 *
	 * @param activitySetId the activity set ID
	 */
	public void removeByActivitySetId(long activitySetId);

	/**
	 * Returns the number of social activities where activitySetId = &#63;.
	 *
	 * @param activitySetId the activity set ID
	 * @return the number of matching social activities
	 */
	public int countByActivitySetId(long activitySetId);

	/**
	 * Returns the social activity where mirrorActivityId = &#63; or throws a <code>NoSuchActivityException</code> if it could not be found.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @return the matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public SocialActivity findByMirrorActivityId(long mirrorActivityId)
		throws NoSuchActivityException;

	/**
	 * Returns the social activity where mirrorActivityId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public SocialActivity fetchByMirrorActivityId(
		long mirrorActivityId, boolean useFinderCache);

	/**
	 * Removes the social activity where mirrorActivityId = &#63; from the database.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @return the social activity that was removed
	 */
	public SocialActivity removeByMirrorActivityId(long mirrorActivityId)
		throws NoSuchActivityException;

	/**
	 * Returns the number of social activities where mirrorActivityId = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @return the number of matching social activities
	 */
	public int countByMirrorActivityId(long mirrorActivityId);

	/**
	 * Returns an ordered range of all the social activities where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public java.util.List<SocialActivity> findByReceiverUserId(
		long receiverUserId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public SocialActivity findByReceiverUserId_First(
			long receiverUserId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
				orderByComparator)
		throws NoSuchActivityException;

	/**
	 * Returns the first social activity in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public SocialActivity fetchByReceiverUserId_First(
		long receiverUserId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator);

	/**
	 * Removes all the social activities where receiverUserId = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 */
	public void removeByReceiverUserId(long receiverUserId);

	/**
	 * Returns the number of social activities where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social activities
	 */
	public int countByReceiverUserId(long receiverUserId);

	/**
	 * Returns an ordered range of all the social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public java.util.List<SocialActivity> findByC_CN(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public SocialActivity findByC_CN_First(
			long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
				orderByComparator)
		throws NoSuchActivityException;

	/**
	 * Returns the first social activity in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public SocialActivity fetchByC_CN_First(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator);

	/**
	 * Removes all the social activities where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public void removeByC_CN(long companyId, long classNameId);

	/**
	 * Returns the number of social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching social activities
	 */
	public int countByC_CN(long companyId, long classNameId);

	/**
	 * Returns an ordered range of all the social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public java.util.List<SocialActivity> findByC_C(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public SocialActivity findByC_C_First(
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
				orderByComparator)
		throws NoSuchActivityException;

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public SocialActivity fetchByC_C_First(
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator);

	/**
	 * Removes all the social activities where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByC_C(long classNameId, long classPK);

	/**
	 * Returns the number of social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching social activities
	 */
	public int countByC_C(long classNameId, long classPK);

	/**
	 * Returns an ordered range of all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public java.util.List<SocialActivity> findByM_C_C(
		long mirrorActivityId, long classNameId, long classPK, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity in the ordered set where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public SocialActivity findByM_C_C_First(
			long mirrorActivityId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
				orderByComparator)
		throws NoSuchActivityException;

	/**
	 * Returns the first social activity in the ordered set where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public SocialActivity fetchByM_C_C_First(
		long mirrorActivityId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator);

	/**
	 * Removes all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByM_C_C(
		long mirrorActivityId, long classNameId, long classPK);

	/**
	 * Returns the number of social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching social activities
	 */
	public int countByM_C_C(
		long mirrorActivityId, long classNameId, long classPK);

	/**
	 * Returns an ordered range of all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public java.util.List<SocialActivity> findByC_C_T(
		long classNameId, long classPK, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public SocialActivity findByC_C_T_First(
			long classNameId, long classPK, int type,
			com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
				orderByComparator)
		throws NoSuchActivityException;

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public SocialActivity fetchByC_C_T_First(
		long classNameId, long classPK, int type,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator);

	/**
	 * Removes all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	public void removeByC_C_T(long classNameId, long classPK, int type);

	/**
	 * Returns the number of social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching social activities
	 */
	public int countByC_C_T(long classNameId, long classPK, int type);

	/**
	 * Returns an ordered range of all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public java.util.List<SocialActivity> findByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public SocialActivity findByG_U_C_C_T_R_First(
			long groupId, long userId, long classNameId, long classPK, int type,
			long receiverUserId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
				orderByComparator)
		throws NoSuchActivityException;

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public SocialActivity fetchByG_U_C_C_T_R_First(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator);

	/**
	 * Removes all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 */
	public void removeByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId);

	/**
	 * Returns the number of social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social activities
	 */
	public int countByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId);

	/**
	 * Returns the social activity where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or throws a <code>NoSuchActivityException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public SocialActivity findByG_U_CD_C_C_T_R(
			long groupId, long userId, long createDate, long classNameId,
			long classPK, int type, long receiverUserId)
		throws NoSuchActivityException;

	/**
	 * Returns the social activity where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public SocialActivity fetchByG_U_CD_C_C_T_R(
		long groupId, long userId, long createDate, long classNameId,
		long classPK, int type, long receiverUserId, boolean useFinderCache);

	/**
	 * Removes the social activity where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the social activity that was removed
	 */
	public SocialActivity removeByG_U_CD_C_C_T_R(
			long groupId, long userId, long createDate, long classNameId,
			long classPK, int type, long receiverUserId)
		throws NoSuchActivityException;

	/**
	 * Returns the number of social activities where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social activities
	 */
	public int countByG_U_CD_C_C_T_R(
		long groupId, long userId, long createDate, long classNameId,
		long classPK, int type, long receiverUserId);

	/**
	 * Creates a new social activity with the primary key. Does not add the social activity to the database.
	 *
	 * @param activityId the primary key for the new social activity
	 * @return the new social activity
	 */
	public SocialActivity create(long activityId);

	/**
	 * Removes the social activity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity that was removed
	 * @throws NoSuchActivityException if a social activity with the primary key could not be found
	 */
	public SocialActivity remove(long activityId)
		throws NoSuchActivityException;

	public SocialActivity updateImpl(SocialActivity socialActivity);

	/**
	 * Returns the social activity with the primary key or throws a <code>NoSuchActivityException</code> if it could not be found.
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity
	 * @throws NoSuchActivityException if a social activity with the primary key could not be found
	 */
	public SocialActivity findByPrimaryKey(long activityId)
		throws NoSuchActivityException;

	/**
	 * Returns the social activity with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity, or <code>null</code> if a social activity with the primary key could not be found
	 */
	public SocialActivity fetchByPrimaryKey(long activityId);

	/**
	 * Returns the social activity where mirrorActivityId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @return the matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public default SocialActivity fetchByMirrorActivityId(
		long mirrorActivityId) {

		return fetchByMirrorActivityId(mirrorActivityId, true);
	}

	/**
	 * Returns the social activity where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public default SocialActivity fetchByG_U_CD_C_C_T_R(
		long groupId, long userId, long createDate, long classNameId,
		long classPK, int type, long receiverUserId) {

		return fetchByG_U_CD_C_C_T_R(
			groupId, userId, createDate, classNameId, classPK, type,
			receiverUserId, true);
	}

	/**
	 * Returns all the social activities where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching social activities
	 */
	public default java.util.List<SocialActivity> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activities where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activities where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social activities where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching social activities
	 */
	public default java.util.List<SocialActivity> findByUserId(long userId) {
		return findByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activities where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activities where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social activities where activitySetId = &#63;.
	 *
	 * @param activitySetId the activity set ID
	 * @return the matching social activities
	 */
	public default java.util.List<SocialActivity> findByActivitySetId(
		long activitySetId) {

		return findByActivitySetId(
			activitySetId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activities where activitySetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param activitySetId the activity set ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByActivitySetId(
		long activitySetId, int start, int end) {

		return findByActivitySetId(activitySetId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activities where activitySetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param activitySetId the activity set ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByActivitySetId(
		long activitySetId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator) {

		return findByActivitySetId(
			activitySetId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social activities where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @return the matching social activities
	 */
	public default java.util.List<SocialActivity> findByReceiverUserId(
		long receiverUserId) {

		return findByReceiverUserId(
			receiverUserId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activities where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByReceiverUserId(
		long receiverUserId, int start, int end) {

		return findByReceiverUserId(receiverUserId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activities where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByReceiverUserId(
		long receiverUserId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator) {

		return findByReceiverUserId(
			receiverUserId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching social activities
	 */
	public default java.util.List<SocialActivity> findByC_CN(
		long companyId, long classNameId) {

		return findByC_CN(
			companyId, classNameId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByC_CN(
		long companyId, long classNameId, int start, int end) {

		return findByC_CN(companyId, classNameId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByC_CN(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator) {

		return findByC_CN(
			companyId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching social activities
	 */
	public default java.util.List<SocialActivity> findByC_C(
		long classNameId, long classPK) {

		return findByC_C(
			classNameId, classPK,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return findByC_C(classNameId, classPK, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByC_C(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator) {

		return findByC_C(
			classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching social activities
	 */
	public default java.util.List<SocialActivity> findByM_C_C(
		long mirrorActivityId, long classNameId, long classPK) {

		return findByM_C_C(
			mirrorActivityId, classNameId, classPK,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByM_C_C(
		long mirrorActivityId, long classNameId, long classPK, int start,
		int end) {

		return findByM_C_C(
			mirrorActivityId, classNameId, classPK, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByM_C_C(
		long mirrorActivityId, long classNameId, long classPK, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator) {

		return findByM_C_C(
			mirrorActivityId, classNameId, classPK, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the matching social activities
	 */
	public default java.util.List<SocialActivity> findByC_C_T(
		long classNameId, long classPK, int type) {

		return findByC_C_T(
			classNameId, classPK, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByC_C_T(
		long classNameId, long classPK, int type, int start, int end) {

		return findByC_C_T(classNameId, classPK, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByC_C_T(
		long classNameId, long classPK, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator) {

		return findByC_C_T(
			classNameId, classPK, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the matching social activities
	 */
	public default java.util.List<SocialActivity> findByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId) {

		return findByG_U_C_C_T_R(
			groupId, userId, classNameId, classPK, type, receiverUserId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId, int start, int end) {

		return findByG_U_C_C_T_R(
			groupId, userId, classNameId, classPK, type, receiverUserId, start,
			end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public default java.util.List<SocialActivity> findByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialActivity>
			orderByComparator) {

		return findByG_U_C_C_T_R(
			groupId, userId, classNameId, classPK, type, receiverUserId, start,
			end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1887619906