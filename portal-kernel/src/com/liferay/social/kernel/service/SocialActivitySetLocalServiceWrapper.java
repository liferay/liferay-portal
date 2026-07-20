/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.social.kernel.model.SocialActivitySet;

/**
 * Provides a wrapper for {@link SocialActivitySetLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivitySetLocalService
 * @generated
 */
public class SocialActivitySetLocalServiceWrapper
	implements ServiceWrapper<SocialActivitySetLocalService>,
			   SocialActivitySetLocalService {

	public SocialActivitySetLocalServiceWrapper() {
		this(null);
	}

	public SocialActivitySetLocalServiceWrapper(
		SocialActivitySetLocalService socialActivitySetLocalService) {

		_socialActivitySetLocalService = socialActivitySetLocalService;
	}

	@Override
	public SocialActivitySet addActivitySet(long activityId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySetLocalService.addActivitySet(activityId);
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
	@Override
	public SocialActivitySet addSocialActivitySet(
		SocialActivitySet socialActivitySet) {

		return _socialActivitySetLocalService.addSocialActivitySet(
			socialActivitySet);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySetLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new social activity set with the primary key. Does not add the social activity set to the database.
	 *
	 * @param activitySetId the primary key for the new social activity set
	 * @return the new social activity set
	 */
	@Override
	public SocialActivitySet createSocialActivitySet(long activitySetId) {
		return _socialActivitySetLocalService.createSocialActivitySet(
			activitySetId);
	}

	@Override
	public void decrementActivityCount(long activitySetId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivitySetLocalService.decrementActivityCount(activitySetId);
	}

	@Override
	public void decrementActivityCount(long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivitySetLocalService.decrementActivityCount(
			classNameId, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySetLocalService.deletePersistedModel(
			persistedModel);
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
	@Override
	public SocialActivitySet deleteSocialActivitySet(long activitySetId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySetLocalService.deleteSocialActivitySet(
			activitySetId);
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
	@Override
	public SocialActivitySet deleteSocialActivitySet(
		SocialActivitySet socialActivitySet) {

		return _socialActivitySetLocalService.deleteSocialActivitySet(
			socialActivitySet);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _socialActivitySetLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _socialActivitySetLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _socialActivitySetLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _socialActivitySetLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _socialActivitySetLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _socialActivitySetLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _socialActivitySetLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _socialActivitySetLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public SocialActivitySet fetchSocialActivitySet(long activitySetId) {
		return _socialActivitySetLocalService.fetchSocialActivitySet(
			activitySetId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _socialActivitySetLocalService.getActionableDynamicQuery();
	}

	@Override
	public SocialActivitySet getClassActivitySet(
			long classNameId, long classPK, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySetLocalService.getClassActivitySet(
			classNameId, classPK, type);
	}

	@Override
	public SocialActivitySet getClassActivitySet(
			long userId, long classNameId, long classPK, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySetLocalService.getClassActivitySet(
			userId, classNameId, classPK, type);
	}

	@Override
	public java.util.List<SocialActivitySet> getGroupActivitySets(
		long groupId, int start, int end) {

		return _socialActivitySetLocalService.getGroupActivitySets(
			groupId, start, end);
	}

	@Override
	public int getGroupActivitySetsCount(long groupId) {
		return _socialActivitySetLocalService.getGroupActivitySetsCount(
			groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _socialActivitySetLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<SocialActivitySet> getOrganizationActivitySets(
		long organizationId, int start, int end) {

		return _socialActivitySetLocalService.getOrganizationActivitySets(
			organizationId, start, end);
	}

	@Override
	public int getOrganizationActivitySetsCount(long organizationId) {
		return _socialActivitySetLocalService.getOrganizationActivitySetsCount(
			organizationId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _socialActivitySetLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySetLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<SocialActivitySet> getRelationActivitySets(
		long userId, int start, int end) {

		return _socialActivitySetLocalService.getRelationActivitySets(
			userId, start, end);
	}

	@Override
	public java.util.List<SocialActivitySet> getRelationActivitySets(
		long userId, int type, int start, int end) {

		return _socialActivitySetLocalService.getRelationActivitySets(
			userId, type, start, end);
	}

	@Override
	public int getRelationActivitySetsCount(long userId) {
		return _socialActivitySetLocalService.getRelationActivitySetsCount(
			userId);
	}

	@Override
	public int getRelationActivitySetsCount(long userId, int type) {
		return _socialActivitySetLocalService.getRelationActivitySetsCount(
			userId, type);
	}

	/**
	 * Returns the social activity set with the primary key.
	 *
	 * @param activitySetId the primary key of the social activity set
	 * @return the social activity set
	 * @throws PortalException if a social activity set with the primary key could not be found
	 */
	@Override
	public SocialActivitySet getSocialActivitySet(long activitySetId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySetLocalService.getSocialActivitySet(
			activitySetId);
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
	@Override
	public java.util.List<SocialActivitySet> getSocialActivitySets(
		int start, int end) {

		return _socialActivitySetLocalService.getSocialActivitySets(start, end);
	}

	/**
	 * Returns the number of social activity sets.
	 *
	 * @return the number of social activity sets
	 */
	@Override
	public int getSocialActivitySetsCount() {
		return _socialActivitySetLocalService.getSocialActivitySetsCount();
	}

	@Override
	public SocialActivitySet getUserActivitySet(
			long groupId, long userId, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySetLocalService.getUserActivitySet(
			groupId, userId, type);
	}

	@Override
	public SocialActivitySet getUserActivitySet(
			long groupId, long userId, long classNameId, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialActivitySetLocalService.getUserActivitySet(
			groupId, userId, classNameId, type);
	}

	@Override
	public java.util.List<SocialActivitySet> getUserActivitySets(
		long userId, int start, int end) {

		return _socialActivitySetLocalService.getUserActivitySets(
			userId, start, end);
	}

	@Override
	public int getUserActivitySetsCount(long userId) {
		return _socialActivitySetLocalService.getUserActivitySetsCount(userId);
	}

	@Override
	public java.util.List<SocialActivitySet> getUserGroupsActivitySets(
		long userId, int start, int end) {

		return _socialActivitySetLocalService.getUserGroupsActivitySets(
			userId, start, end);
	}

	@Override
	public int getUserGroupsActivitySetsCount(long userId) {
		return _socialActivitySetLocalService.getUserGroupsActivitySetsCount(
			userId);
	}

	@Override
	public java.util.List<SocialActivitySet> getUserViewableActivitySets(
		long userId, int start, int end) {

		return _socialActivitySetLocalService.getUserViewableActivitySets(
			userId, start, end);
	}

	@Override
	public int getUserViewableActivitySetsCount(long userId) {
		return _socialActivitySetLocalService.getUserViewableActivitySetsCount(
			userId);
	}

	@Override
	public void incrementActivityCount(long activitySetId, long activityId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialActivitySetLocalService.incrementActivityCount(
			activitySetId, activityId);
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
	@Override
	public SocialActivitySet updateSocialActivitySet(
		SocialActivitySet socialActivitySet) {

		return _socialActivitySetLocalService.updateSocialActivitySet(
			socialActivitySet);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _socialActivitySetLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<SocialActivitySet> getCTPersistence() {
		return _socialActivitySetLocalService.getCTPersistence();
	}

	@Override
	public Class<SocialActivitySet> getModelClass() {
		return _socialActivitySetLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SocialActivitySet>, R, E>
				updateUnsafeFunction)
		throws E {

		return _socialActivitySetLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public SocialActivitySetLocalService getWrappedService() {
		return _socialActivitySetLocalService;
	}

	@Override
	public void setWrappedService(
		SocialActivitySetLocalService socialActivitySetLocalService) {

		_socialActivitySetLocalService = socialActivitySetLocalService;
	}

	private SocialActivitySetLocalService _socialActivitySetLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:595185053