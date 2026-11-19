/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link MBDiscussionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see MBDiscussionLocalService
 * @generated
 */
public class MBDiscussionLocalServiceWrapper
	implements MBDiscussionLocalService,
			   ServiceWrapper<MBDiscussionLocalService> {

	public MBDiscussionLocalServiceWrapper() {
		this(null);
	}

	public MBDiscussionLocalServiceWrapper(
		MBDiscussionLocalService mbDiscussionLocalService) {

		_mbDiscussionLocalService = mbDiscussionLocalService;
	}

	@Override
	public MBDiscussion addDiscussion(
			long userId, long groupId, long classNameId, long classPK,
			long threadId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbDiscussionLocalService.addDiscussion(
			userId, groupId, classNameId, classPK, threadId, serviceContext);
	}

	/**
	 * Adds the message boards discussion to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBDiscussionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbDiscussion the message boards discussion
	 * @return the message boards discussion that was added
	 */
	@Override
	public MBDiscussion addMBDiscussion(MBDiscussion mbDiscussion) {
		return _mbDiscussionLocalService.addMBDiscussion(mbDiscussion);
	}

	/**
	 * Creates a new message boards discussion with the primary key. Does not add the message boards discussion to the database.
	 *
	 * @param discussionId the primary key for the new message boards discussion
	 * @return the new message boards discussion
	 */
	@Override
	public MBDiscussion createMBDiscussion(long discussionId) {
		return _mbDiscussionLocalService.createMBDiscussion(discussionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbDiscussionLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the message boards discussion with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBDiscussionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param discussionId the primary key of the message boards discussion
	 * @return the message boards discussion that was removed
	 * @throws PortalException if a message boards discussion with the primary key could not be found
	 */
	@Override
	public MBDiscussion deleteMBDiscussion(long discussionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbDiscussionLocalService.deleteMBDiscussion(discussionId);
	}

	/**
	 * Deletes the message boards discussion from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBDiscussionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbDiscussion the message boards discussion
	 * @return the message boards discussion that was removed
	 */
	@Override
	public MBDiscussion deleteMBDiscussion(MBDiscussion mbDiscussion) {
		return _mbDiscussionLocalService.deleteMBDiscussion(mbDiscussion);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbDiscussionLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _mbDiscussionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _mbDiscussionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _mbDiscussionLocalService.dynamicQuery();
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

		return _mbDiscussionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBDiscussionModelImpl</code>.
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

		return _mbDiscussionLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBDiscussionModelImpl</code>.
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

		return _mbDiscussionLocalService.dynamicQuery(
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

		return _mbDiscussionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _mbDiscussionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public MBDiscussion fetchDiscussion(long discussionId) {
		return _mbDiscussionLocalService.fetchDiscussion(discussionId);
	}

	@Override
	public MBDiscussion fetchDiscussion(long classNameId, long classPK) {
		return _mbDiscussionLocalService.fetchDiscussion(classNameId, classPK);
	}

	@Override
	public MBDiscussion fetchDiscussion(String className, long classPK) {
		return _mbDiscussionLocalService.fetchDiscussion(className, classPK);
	}

	@Override
	public MBDiscussion fetchMBDiscussion(long discussionId) {
		return _mbDiscussionLocalService.fetchMBDiscussion(discussionId);
	}

	/**
	 * Returns the message boards discussion matching the UUID and group.
	 *
	 * @param uuid the message boards discussion's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards discussion, or <code>null</code> if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion fetchMBDiscussionByUuidAndGroupId(
		String uuid, long groupId) {

		return _mbDiscussionLocalService.fetchMBDiscussionByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public MBDiscussion fetchThreadDiscussion(long threadId) {
		return _mbDiscussionLocalService.fetchThreadDiscussion(threadId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _mbDiscussionLocalService.getActionableDynamicQuery();
	}

	@Override
	public MBDiscussion getDiscussion(String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbDiscussionLocalService.getDiscussion(className, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _mbDiscussionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _mbDiscussionLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the message boards discussion with the primary key.
	 *
	 * @param discussionId the primary key of the message boards discussion
	 * @return the message boards discussion
	 * @throws PortalException if a message boards discussion with the primary key could not be found
	 */
	@Override
	public MBDiscussion getMBDiscussion(long discussionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbDiscussionLocalService.getMBDiscussion(discussionId);
	}

	/**
	 * Returns the message boards discussion matching the UUID and group.
	 *
	 * @param uuid the message boards discussion's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards discussion
	 * @throws PortalException if a matching message boards discussion could not be found
	 */
	@Override
	public MBDiscussion getMBDiscussionByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbDiscussionLocalService.getMBDiscussionByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the message boards discussions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBDiscussionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards discussions
	 * @param end the upper bound of the range of message boards discussions (not inclusive)
	 * @return the range of message boards discussions
	 */
	@Override
	public java.util.List<MBDiscussion> getMBDiscussions(int start, int end) {
		return _mbDiscussionLocalService.getMBDiscussions(start, end);
	}

	/**
	 * Returns all the message boards discussions matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards discussions
	 * @param companyId the primary key of the company
	 * @return the matching message boards discussions, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<MBDiscussion> getMBDiscussionsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _mbDiscussionLocalService.getMBDiscussionsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of message boards discussions matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards discussions
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of message boards discussions
	 * @param end the upper bound of the range of message boards discussions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching message boards discussions, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<MBDiscussion> getMBDiscussionsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBDiscussion>
			orderByComparator) {

		return _mbDiscussionLocalService.getMBDiscussionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of message boards discussions.
	 *
	 * @return the number of message boards discussions
	 */
	@Override
	public int getMBDiscussionsCount() {
		return _mbDiscussionLocalService.getMBDiscussionsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _mbDiscussionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbDiscussionLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public MBDiscussion getThreadDiscussion(long threadId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbDiscussionLocalService.getThreadDiscussion(threadId);
	}

	@Override
	public void subscribeDiscussion(
			long userId, long groupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbDiscussionLocalService.subscribeDiscussion(
			userId, groupId, className, classPK);
	}

	@Override
	public void unsubscribeDiscussion(
			long userId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbDiscussionLocalService.unsubscribeDiscussion(
			userId, className, classPK);
	}

	/**
	 * Updates the message boards discussion in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBDiscussionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbDiscussion the message boards discussion
	 * @return the message boards discussion that was updated
	 */
	@Override
	public MBDiscussion updateMBDiscussion(MBDiscussion mbDiscussion) {
		return _mbDiscussionLocalService.updateMBDiscussion(mbDiscussion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _mbDiscussionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<MBDiscussion> getCTPersistence() {
		return _mbDiscussionLocalService.getCTPersistence();
	}

	@Override
	public Class<MBDiscussion> getModelClass() {
		return _mbDiscussionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<MBDiscussion>, R, E>
				updateUnsafeFunction)
		throws E {

		return _mbDiscussionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public MBDiscussionLocalService getWrappedService() {
		return _mbDiscussionLocalService;
	}

	@Override
	public void setWrappedService(
		MBDiscussionLocalService mbDiscussionLocalService) {

		_mbDiscussionLocalService = mbDiscussionLocalService;
	}

	private MBDiscussionLocalService _mbDiscussionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1328737848