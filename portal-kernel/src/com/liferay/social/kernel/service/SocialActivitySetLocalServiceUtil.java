/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.social.kernel.model.SocialActivitySet;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SocialActivitySet. This utility wraps
 * <code>com.liferay.portlet.social.service.impl.SocialActivitySetLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivitySetLocalService
 * @generated
 */
public class SocialActivitySetLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.social.service.impl.SocialActivitySetLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static SocialActivitySet addActivitySet(long activityId)
		throws PortalException {

		return getService().addActivitySet(activityId);
	}

	/**
	 * Adds the social activity set to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivitySetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivitySet the social activity set
	 * @return the social activity set that was added
	 */
	public static SocialActivitySet addSocialActivitySet(
		SocialActivitySet socialActivitySet) {

		return getService().addSocialActivitySet(socialActivitySet);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new social activity set with the primary key. Does not add the social activity set to the database.
	 *
	 * @param activitySetId the primary key for the new social activity set
	 * @return the new social activity set
	 */
	public static SocialActivitySet createSocialActivitySet(
		long activitySetId) {

		return getService().createSocialActivitySet(activitySetId);
	}

	public static void decrementActivityCount(long activitySetId)
		throws PortalException {

		getService().decrementActivityCount(activitySetId);
	}

	public static void decrementActivityCount(long classNameId, long classPK)
		throws PortalException {

		getService().decrementActivityCount(classNameId, classPK);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the social activity set with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivitySetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param activitySetId the primary key of the social activity set
	 * @return the social activity set that was removed
	 * @throws PortalException if a social activity set with the primary key could not be found
	 */
	public static SocialActivitySet deleteSocialActivitySet(long activitySetId)
		throws PortalException {

		return getService().deleteSocialActivitySet(activitySetId);
	}

	/**
	 * Deletes the social activity set from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivitySetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivitySet the social activity set
	 * @return the social activity set that was removed
	 */
	public static SocialActivitySet deleteSocialActivitySet(
		SocialActivitySet socialActivitySet) {

		return getService().deleteSocialActivitySet(socialActivitySet);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static SocialActivitySet fetchSocialActivitySet(long activitySetId) {
		return getService().fetchSocialActivitySet(activitySetId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static SocialActivitySet getClassActivitySet(
			long classNameId, long classPK, int type)
		throws PortalException {

		return getService().getClassActivitySet(classNameId, classPK, type);
	}

	public static SocialActivitySet getClassActivitySet(
			long userId, long classNameId, long classPK, int type)
		throws PortalException {

		return getService().getClassActivitySet(
			userId, classNameId, classPK, type);
	}

	public static List<SocialActivitySet> getGroupActivitySets(
		long groupId, int start, int end) {

		return getService().getGroupActivitySets(groupId, start, end);
	}

	public static int getGroupActivitySetsCount(long groupId) {
		return getService().getGroupActivitySetsCount(groupId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static List<SocialActivitySet> getOrganizationActivitySets(
		long organizationId, int start, int end) {

		return getService().getOrganizationActivitySets(
			organizationId, start, end);
	}

	public static int getOrganizationActivitySetsCount(long organizationId) {
		return getService().getOrganizationActivitySetsCount(organizationId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<SocialActivitySet> getRelationActivitySets(
		long userId, int start, int end) {

		return getService().getRelationActivitySets(userId, start, end);
	}

	public static List<SocialActivitySet> getRelationActivitySets(
		long userId, int type, int start, int end) {

		return getService().getRelationActivitySets(userId, type, start, end);
	}

	public static int getRelationActivitySetsCount(long userId) {
		return getService().getRelationActivitySetsCount(userId);
	}

	public static int getRelationActivitySetsCount(long userId, int type) {
		return getService().getRelationActivitySetsCount(userId, type);
	}

	/**
	 * Returns the social activity set with the primary key.
	 *
	 * @param activitySetId the primary key of the social activity set
	 * @return the social activity set
	 * @throws PortalException if a social activity set with the primary key could not be found
	 */
	public static SocialActivitySet getSocialActivitySet(long activitySetId)
		throws PortalException {

		return getService().getSocialActivitySet(activitySetId);
	}

	/**
	 * Returns a range of all the social activity sets.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @return the range of social activity sets
	 */
	public static List<SocialActivitySet> getSocialActivitySets(
		int start, int end) {

		return getService().getSocialActivitySets(start, end);
	}

	/**
	 * Returns the number of social activity sets.
	 *
	 * @return the number of social activity sets
	 */
	public static int getSocialActivitySetsCount() {
		return getService().getSocialActivitySetsCount();
	}

	public static SocialActivitySet getUserActivitySet(
			long groupId, long userId, int type)
		throws PortalException {

		return getService().getUserActivitySet(groupId, userId, type);
	}

	public static SocialActivitySet getUserActivitySet(
			long groupId, long userId, long classNameId, int type)
		throws PortalException {

		return getService().getUserActivitySet(
			groupId, userId, classNameId, type);
	}

	public static List<SocialActivitySet> getUserActivitySets(
		long userId, int start, int end) {

		return getService().getUserActivitySets(userId, start, end);
	}

	public static int getUserActivitySetsCount(long userId) {
		return getService().getUserActivitySetsCount(userId);
	}

	public static List<SocialActivitySet> getUserGroupsActivitySets(
		long userId, int start, int end) {

		return getService().getUserGroupsActivitySets(userId, start, end);
	}

	public static int getUserGroupsActivitySetsCount(long userId) {
		return getService().getUserGroupsActivitySetsCount(userId);
	}

	public static List<SocialActivitySet> getUserViewableActivitySets(
		long userId, int start, int end) {

		return getService().getUserViewableActivitySets(userId, start, end);
	}

	public static int getUserViewableActivitySetsCount(long userId) {
		return getService().getUserViewableActivitySetsCount(userId);
	}

	public static void incrementActivityCount(
			long activitySetId, long activityId)
		throws PortalException {

		getService().incrementActivityCount(activitySetId, activityId);
	}

	/**
	 * Updates the social activity set in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialActivitySetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialActivitySet the social activity set
	 * @return the social activity set that was updated
	 */
	public static SocialActivitySet updateSocialActivitySet(
		SocialActivitySet socialActivitySet) {

		return getService().updateSocialActivitySet(socialActivitySet);
	}

	public static SocialActivitySetLocalService getService() {
		return _service;
	}

	public static void setService(SocialActivitySetLocalService service) {
		_service = service;
	}

	private static volatile SocialActivitySetLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-205466759