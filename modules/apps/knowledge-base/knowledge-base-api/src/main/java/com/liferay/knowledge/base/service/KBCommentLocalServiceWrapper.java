/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service;

import com.liferay.knowledge.base.model.KBComment;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link KBCommentLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KBCommentLocalService
 * @generated
 */
public class KBCommentLocalServiceWrapper
	implements KBCommentLocalService, ServiceWrapper<KBCommentLocalService> {

	public KBCommentLocalServiceWrapper() {
		this(null);
	}

	public KBCommentLocalServiceWrapper(
		KBCommentLocalService kbCommentLocalService) {

		_kbCommentLocalService = kbCommentLocalService;
	}

	/**
	 * Adds the kb comment to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbComment the kb comment
	 * @return the kb comment that was added
	 */
	@Override
	public KBComment addKBComment(KBComment kbComment) {
		return _kbCommentLocalService.addKBComment(kbComment);
	}

	@Override
	public KBComment addKBComment(
			long userId, long classNameId, long classPK, String content,
			int userRating,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.addKBComment(
			userId, classNameId, classPK, content, userRating, serviceContext);
	}

	@Override
	public KBComment addKBComment(
			long userId, long classNameId, long classPK, String content,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.addKBComment(
			userId, classNameId, classPK, content, serviceContext);
	}

	/**
	 * Creates a new kb comment with the primary key. Does not add the kb comment to the database.
	 *
	 * @param kbCommentId the primary key for the new kb comment
	 * @return the new kb comment
	 */
	@Override
	public KBComment createKBComment(long kbCommentId) {
		return _kbCommentLocalService.createKBComment(kbCommentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the kb comment from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbComment the kb comment
	 * @return the kb comment that was removed
	 * @throws PortalException
	 */
	@Override
	public KBComment deleteKBComment(KBComment kbComment)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.deleteKBComment(kbComment);
	}

	/**
	 * Deletes the kb comment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment that was removed
	 * @throws PortalException if a kb comment with the primary key could not be found
	 */
	@Override
	public KBComment deleteKBComment(long kbCommentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.deleteKBComment(kbCommentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kbCommentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kbCommentLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kbCommentLocalService.dynamicQuery();
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

		return _kbCommentLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
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

		return _kbCommentLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
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

		return _kbCommentLocalService.dynamicQuery(
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

		return _kbCommentLocalService.dynamicQueryCount(dynamicQuery);
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

		return _kbCommentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KBComment fetchKBComment(long kbCommentId) {
		return _kbCommentLocalService.fetchKBComment(kbCommentId);
	}

	/**
	 * Returns the kb comment matching the UUID and group.
	 *
	 * @param uuid the kb comment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	@Override
	public KBComment fetchKBCommentByUuidAndGroupId(String uuid, long groupId) {
		return _kbCommentLocalService.fetchKBCommentByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kbCommentLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _kbCommentLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kbCommentLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kb comment with the primary key.
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment
	 * @throws PortalException if a kb comment with the primary key could not be found
	 */
	@Override
	public KBComment getKBComment(long kbCommentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.getKBComment(kbCommentId);
	}

	@Override
	public KBComment getKBComment(long userId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.getKBComment(userId, className, classPK);
	}

	/**
	 * Returns the kb comment matching the UUID and group.
	 *
	 * @param uuid the kb comment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb comment
	 * @throws PortalException if a matching kb comment could not be found
	 */
	@Override
	public KBComment getKBCommentByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.getKBCommentByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the kb comments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of kb comments
	 */
	@Override
	public java.util.List<KBComment> getKBComments(int start, int end) {
		return _kbCommentLocalService.getKBComments(start, end);
	}

	@Override
	public java.util.List<KBComment> getKBComments(
		long groupId, int status, int start, int end) {

		return _kbCommentLocalService.getKBComments(
			groupId, status, start, end);
	}

	@Override
	public java.util.List<KBComment> getKBComments(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return _kbCommentLocalService.getKBComments(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<KBComment> getKBComments(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return _kbCommentLocalService.getKBComments(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<KBComment> getKBComments(
		long userId, String className, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return _kbCommentLocalService.getKBComments(
			userId, className, classPK, start, end, orderByComparator);
	}

	@Override
	public java.util.List<KBComment> getKBComments(
		String className, long classPK, int status, int start, int end) {

		return _kbCommentLocalService.getKBComments(
			className, classPK, status, start, end);
	}

	@Override
	public java.util.List<KBComment> getKBComments(
		String className, long classPK, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return _kbCommentLocalService.getKBComments(
			className, classPK, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<KBComment> getKBComments(
		String className, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return _kbCommentLocalService.getKBComments(
			className, classPK, start, end, orderByComparator);
	}

	@Override
	public java.util.List<KBComment> getKBComments(
		String className, long classPK, int[] status, int start, int end) {

		return _kbCommentLocalService.getKBComments(
			className, classPK, status, start, end);
	}

	/**
	 * Returns all the kb comments matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb comments
	 * @param companyId the primary key of the company
	 * @return the matching kb comments, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<KBComment> getKBCommentsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _kbCommentLocalService.getKBCommentsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of kb comments matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb comments
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching kb comments, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<KBComment> getKBCommentsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return _kbCommentLocalService.getKBCommentsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kb comments.
	 *
	 * @return the number of kb comments
	 */
	@Override
	public int getKBCommentsCount() {
		return _kbCommentLocalService.getKBCommentsCount();
	}

	@Override
	public int getKBCommentsCount(long groupId, int status) {
		return _kbCommentLocalService.getKBCommentsCount(groupId, status);
	}

	@Override
	public int getKBCommentsCount(long userId, String className, long classPK) {
		return _kbCommentLocalService.getKBCommentsCount(
			userId, className, classPK);
	}

	@Override
	public int getKBCommentsCount(String className, long classPK) {
		return _kbCommentLocalService.getKBCommentsCount(className, classPK);
	}

	@Override
	public int getKBCommentsCount(String className, long classPK, int status) {
		return _kbCommentLocalService.getKBCommentsCount(
			className, classPK, status);
	}

	@Override
	public int getKBCommentsCount(
		String className, long classPK, int[] status) {

		return _kbCommentLocalService.getKBCommentsCount(
			className, classPK, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kbCommentLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the kb comment in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbComment the kb comment
	 * @return the kb comment that was updated
	 */
	@Override
	public KBComment updateKBComment(KBComment kbComment) {
		return _kbCommentLocalService.updateKBComment(kbComment);
	}

	@Override
	public KBComment updateKBComment(
			long kbCommentId, long classNameId, long classPK, String content,
			int userRating, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.updateKBComment(
			kbCommentId, classNameId, classPK, content, userRating, status,
			serviceContext);
	}

	@Override
	public KBComment updateKBComment(
			long kbCommentId, long classNameId, long classPK, String content,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.updateKBComment(
			kbCommentId, classNameId, classPK, content, status, serviceContext);
	}

	@Override
	public KBComment updateStatus(
			long userId, long kbCommentId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbCommentLocalService.updateStatus(
			userId, kbCommentId, status, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kbCommentLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KBComment> getCTPersistence() {
		return _kbCommentLocalService.getCTPersistence();
	}

	@Override
	public Class<KBComment> getModelClass() {
		return _kbCommentLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KBComment>, R, E> updateUnsafeFunction)
		throws E {

		return _kbCommentLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KBCommentLocalService getWrappedService() {
		return _kbCommentLocalService;
	}

	@Override
	public void setWrappedService(KBCommentLocalService kbCommentLocalService) {
		_kbCommentLocalService = kbCommentLocalService;
	}

	private KBCommentLocalService _kbCommentLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1289439867