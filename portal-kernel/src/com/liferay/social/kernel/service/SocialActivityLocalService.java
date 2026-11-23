/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.messaging.async.Async;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.social.kernel.model.SocialActivity;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for SocialActivity. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivityLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.social.kernel.model.SocialActivity"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SocialActivityLocalService
	extends BaseLocalService, CTService<SocialActivity>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.social.service.impl.SocialActivityLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the social activity local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SocialActivityLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Records an activity with the given time in the database.
	 *
	 * <p>
	 * This method records a social activity done on an asset, identified by its
	 * class name and class primary key, in the database. Additional information
	 * (such as the original message ID for a reply to a forum post) is passed
	 * in via the <code>extraData</code> in JSON format. For activities
	 * affecting another user, a mirror activity is generated that describes the
	 * action from the user's point of view. The target user's ID is passed in
	 * via the <code>receiverUserId</code>.
	 * </p>
	 *
	 * <p>
	 * Example for a mirrored activity:<br> When a user replies to a message
	 * boards post, the reply action is stored in the database with the
	 * <code>receiverUserId</code> being the ID of the author of the original
	 * message. The <code>extraData</code> contains the ID of the original
	 * message in JSON format. A mirror activity is generated with the values of
	 * the <code>userId</code> and the <code>receiverUserId</code> swapped. This
	 * mirror activity basically describes a "replied to" event.
	 * </p>
	 *
	 * <p>
	 * Mirror activities are most often used in relation to friend requests and
	 * activities.
	 * </p>
	 *
	 * @param userId the primary key of the acting user
	 * @param groupId the primary key of the group
	 * @param createDate the activity's date
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @param type the activity's type
	 * @param extraData any extra data regarding the activity
	 * @param receiverUserId the primary key of the receiving user
	 */
	public void addActivity(
			long userId, long groupId, Date createDate, String className,
			long classPK, int type, String extraData, long receiverUserId)
		throws PortalException;

	/**
	 * Records an activity in the database, using a time based on the current
	 * time in an attempt to make the activity's time unique.
	 *
	 * @param userId the primary key of the acting user
	 * @param groupId the primary key of the group
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @param type the activity's type
	 * @param extraData any extra data regarding the activity
	 * @param receiverUserId the primary key of the receiving user
	 */
	public void addActivity(
			long userId, long groupId, String className, long classPK, int type,
			String extraData, long receiverUserId)
		throws PortalException;

	@Async
	public void addActivity(
			SocialActivity activity, SocialActivity mirrorActivity)
		throws PortalException;

	/**
	 * Adds the social activity to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivity the social activity
	 * @return the social activity that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SocialActivity addSocialActivity(SocialActivity socialActivity);

	/**
	 * Records an activity in the database, but only if there isn't already an
	 * activity with the same parameters.
	 *
	 * <p>
	 * For the main functionality see {@link #addActivity(long, long, Date,
	 * String, long, int, String, long)}
	 * </p>
	 *
	 * @param userId the primary key of the acting user
	 * @param groupId the primary key of the group
	 * @param createDate the activity's date
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @param type the activity's type
	 * @param extraData any extra data regarding the activity
	 * @param receiverUserId the primary key of the receiving user
	 */
	public void addUniqueActivity(
			long userId, long groupId, Date createDate, String className,
			long classPK, int type, String extraData, long receiverUserId)
		throws PortalException;

	/**
	 * Records an activity with the current time in the database, but only if
	 * there isn't one with the same parameters.
	 *
	 * <p>
	 * For the main functionality see {@link #addActivity(long, long, Date,
	 * String, long, int, String, long)}
	 * </p>
	 *
	 * @param userId the primary key of the acting user
	 * @param groupId the primary key of the group
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @param type the activity's type
	 * @param extraData any extra data regarding the activity
	 * @param receiverUserId the primary key of the receiving user
	 */
	public void addUniqueActivity(
			long userId, long groupId, String className, long classPK, int type,
			String extraData, long receiverUserId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new social activity with the primary key. Does not add the social activity to the database.
	 *
	 * @param activityId the primary key for the new social activity
	 * @return the new social activity
	 */
	@Transactional(enabled = false)
	public SocialActivity createSocialActivity(long activityId);

	/**
	 * Removes stored activities for the asset.
	 *
	 * @param assetEntry the asset from which to remove stored activities
	 */
	public void deleteActivities(AssetEntry assetEntry) throws PortalException;

	public void deleteActivities(long groupId);

	/**
	 * Removes stored activities for the asset identified by the class name and
	 * class primary key.
	 *
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 */
	public void deleteActivities(String className, long classPK)
		throws PortalException;

	/**
	 * Removes the stored activity from the database.
	 *
	 * @param activityId the primary key of the stored activity
	 */
	public void deleteActivity(long activityId) throws PortalException;

	/**
	 * Removes the stored activity and its mirror activity from the database.
	 *
	 * @param activity the activity to be removed
	 */
	public void deleteActivity(SocialActivity activity) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the social activity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity that was removed
	 * @throws PortalException if a social activity with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public SocialActivity deleteSocialActivity(long activityId)
		throws PortalException;

	/**
	 * Deletes the social activity from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivity the social activity
	 * @return the social activity that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public SocialActivity deleteSocialActivity(SocialActivity socialActivity);

	/**
	 * Removes the user's stored activities from the database.
	 *
	 * <p>
	 * This method removes all activities where the user is either the actor or
	 * the receiver.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 */
	public void deleteUserActivities(long userId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SocialActivity fetchFirstActivity(
		String className, long classPK, int type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SocialActivity fetchSocialActivity(long activityId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * @param classNameId the target asset's class name ID
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getActivities(long, String, int, int)}  Returns a range of
	 all the activities done on assets identified by the class
	 name ID.  <p> Useful when paginating results. Returns a
	 maximum of <code>end - start</code> instances.
	 <code>start</code> and <code>end</code> are not primary keys,
	 they are indexes in the result set. Thus, <code>0</code>
	 refers to the first result in the set. Setting both
	 <code>start</code> and <code>end</code> to {@link
	 QueryUtil#ALL_POS} will return the full result set.</p>
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getActivities(
		long classNameId, int start, int end);

	/**
	 * Returns a range of all the activities done on the asset identified by the
	 * class name ID and class primary key that are mirrors of the activity
	 * identified by the mirror activity ID.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param mirrorActivityId the primary key of the mirror activity
	 * @param classNameId the target asset's class name ID
	 * @param classPK the primary key of the target asset
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getActivities(
		long mirrorActivityId, long classNameId, long classPK, int start,
		int end);

	/**
	 * Returns a range of all the activities done on assets identified by the
	 * company ID and class name.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param className the target asset's class name
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getActivities(
		long companyId, String className, int start, int end);

	/**
	 * Returns a range of all the activities done on the asset identified by the
	 * class name and the class primary key that are mirrors of the activity
	 * identified by the mirror activity ID.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param mirrorActivityId the primary key of the mirror activity
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getActivities(
		long mirrorActivityId, String className, long classPK, int start,
		int end);

	/**
	 * @param classNameId the target asset's class name ID
	 * @return the number of matching activities
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getActivitiesCount(long, String)}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getActivitiesCount(long classNameId);

	/**
	 * Returns the number of activities done on the asset identified by the
	 * class name ID and class primary key that are mirrors of the activity
	 * identified by the mirror activity ID.
	 *
	 * @param mirrorActivityId the primary key of the mirror activity
	 * @param classNameId the target asset's class name ID
	 * @param classPK the primary key of the target asset
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getActivitiesCount(
		long mirrorActivityId, long classNameId, long classPK);

	/**
	 * Returns the number of activities done on assets identified by company ID
	 * and class name.
	 *
	 * @param companyId the primary key of the company
	 * @param className the target asset's class name
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getActivitiesCount(long companyId, String className);

	/**
	 * Returns the number of activities done on the asset identified by the
	 * class name and class primary key that are mirrors of the activity
	 * identified by the mirror activity ID.
	 *
	 * @param mirrorActivityId the primary key of the mirror activity
	 * @param className the target asset's class name
	 * @param classPK the primary key of the target asset
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getActivitiesCount(
		long mirrorActivityId, String className, long classPK);

	/**
	 * Returns the activity identified by its primary key.
	 *
	 * @param activityId the primary key of the activity
	 * @return Returns the activity
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SocialActivity getActivity(long activityId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getActivitySetActivities(
		long activitySetId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getApprovedActivities(
		long classPK, double version);

	/**
	 * Returns a range of all the activities done in the group.
	 *
	 * <p>
	 * This method only finds activities without mirrors.
	 * </p>
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getGroupActivities(
		long groupId, int start, int end);

	/**
	 * Returns the number of activities done in the group.
	 *
	 * <p>
	 * This method only counts activities without mirrors.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupActivitiesCount(long groupId);

	/**
	 * Returns a range of activities done by users that are members of the
	 * group.
	 *
	 * <p>
	 * This method only finds activities without mirrors.
	 * </p>
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getGroupUsersActivities(
		long groupId, int start, int end);

	/**
	 * Returns the number of activities done by users that are members of the
	 * group.
	 *
	 * <p>
	 * This method only counts activities without mirrors.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupUsersActivitiesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns a range of all the activities done in the organization. This
	 * method only finds activities without mirrors.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param organizationId the primary key of the organization
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getOrganizationActivities(
		long organizationId, int start, int end);

	/**
	 * Returns the number of activities done in the organization. This method
	 * only counts activities without mirrors.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationActivitiesCount(long organizationId);

	/**
	 * Returns a range of all the activities done by users of the organization.
	 * This method only finds activities without mirrors.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param organizationId the primary key of the organization
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getOrganizationUsersActivities(
		long organizationId, int start, int end);

	/**
	 * Returns the number of activities done by users of the organization. This
	 * method only counts activities without mirrors.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationUsersActivitiesCount(long organizationId);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns a range of all the activities done by users in a relationship
	 * with the user identified by the user ID.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getRelationActivities(
		long userId, int start, int end);

	/**
	 * Returns a range of all the activities done by users in a relationship of
	 * type <code>type</code> with the user identified by <code>userId</code>.
	 * This method only finds activities without mirrors.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param type the relationship type
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getRelationActivities(
		long userId, int type, int start, int end);

	/**
	 * Returns the number of activities done by users in a relationship with the
	 * user identified by userId.
	 *
	 * @param userId the primary key of the user
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRelationActivitiesCount(long userId);

	/**
	 * Returns the number of activities done by users in a relationship of type
	 * <code>type</code> with the user identified by <code>userId</code>. This
	 * method only counts activities without mirrors.
	 *
	 * @param userId the primary key of the user
	 * @param type the relationship type
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRelationActivitiesCount(long userId, int type);

	/**
	 * Returns a range of all the social activities.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of social activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getSocialActivities(int start, int end);

	/**
	 * Returns the number of social activities.
	 *
	 * @return the number of social activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSocialActivitiesCount();

	/**
	 * Returns the social activity with the primary key.
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity
	 * @throws PortalException if a social activity with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SocialActivity getSocialActivity(long activityId)
		throws PortalException;

	/**
	 * Returns a range of all the activities done by the user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getUserActivities(
		long userId, int start, int end);

	/**
	 * Returns a range of all the activities done in the user's groups. This
	 * method only finds activities without mirrors.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getUserGroupsActivities(
		long userId, int start, int end);

	/**
	 * Returns the number of activities done in user's groups. This method only
	 * counts activities without mirrors.
	 *
	 * @param userId the primary key of the user
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserGroupsActivitiesCount(long userId);

	/**
	 * Returns a range of all the activities done in the user's groups and
	 * organizations. This method only finds activities without mirrors.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getUserGroupsAndOrganizationsActivities(
		long userId, int start, int end);

	/**
	 * Returns the number of activities done in user's groups and organizations.
	 * This method only counts activities without mirrors.
	 *
	 * @param userId the primary key of the user
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserGroupsAndOrganizationsActivitiesCount(long userId);

	/**
	 * Returns a range of all activities done in the user's organizations. This
	 * method only finds activities without mirrors.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialActivity> getUserOrganizationsActivities(
		long userId, int start, int end);

	/**
	 * Returns the number of activities done in the user's organizations. This
	 * method only counts activities without mirrors.
	 *
	 * @param userId the primary key of the user
	 * @return the number of matching activities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserOrganizationsActivitiesCount(long userId);

	/**
	 * Updates the social activity in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivity the social activity
	 * @return the social activity that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SocialActivity updateSocialActivity(SocialActivity socialActivity);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<SocialActivity> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<SocialActivity> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SocialActivity>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:957180637