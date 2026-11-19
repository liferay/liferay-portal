/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service;

import com.liferay.knowledge.base.model.KBComment;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for KBComment. This utility wraps
 * <code>com.liferay.knowledge.base.service.impl.KBCommentLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KBCommentLocalService
 * @generated
 */
public class KBCommentLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.knowledge.base.service.impl.KBCommentLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static KBComment addKBComment(KBComment kbComment) {
		return getService().addKBComment(kbComment);
	}

	public static KBComment addKBComment(
			long userId, long classNameId, long classPK, String content,
			int userRating,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKBComment(
			userId, classNameId, classPK, content, userRating, serviceContext);
	}

	public static KBComment addKBComment(
			long userId, long classNameId, long classPK, String content,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKBComment(
			userId, classNameId, classPK, content, serviceContext);
	}

	/**
	 * Creates a new kb comment with the primary key. Does not add the kb comment to the database.
	 *
	 * @param kbCommentId the primary key for the new kb comment
	 * @return the new kb comment
	 */
	public static KBComment createKBComment(long kbCommentId) {
		return getService().createKBComment(kbCommentId);
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
	public static KBComment deleteKBComment(KBComment kbComment)
		throws PortalException {

		return getService().deleteKBComment(kbComment);
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
	public static KBComment deleteKBComment(long kbCommentId)
		throws PortalException {

		return getService().deleteKBComment(kbCommentId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
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

	public static KBComment fetchKBComment(long kbCommentId) {
		return getService().fetchKBComment(kbCommentId);
	}

	/**
	 * Returns the kb comment matching the UUID and group.
	 *
	 * @param uuid the kb comment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public static KBComment fetchKBCommentByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchKBCommentByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kb comment with the primary key.
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment
	 * @throws PortalException if a kb comment with the primary key could not be found
	 */
	public static KBComment getKBComment(long kbCommentId)
		throws PortalException {

		return getService().getKBComment(kbCommentId);
	}

	public static KBComment getKBComment(
			long userId, String className, long classPK)
		throws PortalException {

		return getService().getKBComment(userId, className, classPK);
	}

	/**
	 * Returns the kb comment matching the UUID and group.
	 *
	 * @param uuid the kb comment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb comment
	 * @throws PortalException if a matching kb comment could not be found
	 */
	public static KBComment getKBCommentByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getKBCommentByUuidAndGroupId(uuid, groupId);
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
	public static List<KBComment> getKBComments(int start, int end) {
		return getService().getKBComments(start, end);
	}

	public static List<KBComment> getKBComments(
		long groupId, int status, int start, int end) {

		return getService().getKBComments(groupId, status, start, end);
	}

	public static List<KBComment> getKBComments(
		long groupId, int status, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getService().getKBComments(
			groupId, status, start, end, orderByComparator);
	}

	public static List<KBComment> getKBComments(
		long groupId, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getService().getKBComments(
			groupId, start, end, orderByComparator);
	}

	public static List<KBComment> getKBComments(
		long userId, String className, long classPK, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getService().getKBComments(
			userId, className, classPK, start, end, orderByComparator);
	}

	public static List<KBComment> getKBComments(
		String className, long classPK, int status, int start, int end) {

		return getService().getKBComments(
			className, classPK, status, start, end);
	}

	public static List<KBComment> getKBComments(
		String className, long classPK, int status, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getService().getKBComments(
			className, classPK, status, start, end, orderByComparator);
	}

	public static List<KBComment> getKBComments(
		String className, long classPK, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getService().getKBComments(
			className, classPK, start, end, orderByComparator);
	}

	public static List<KBComment> getKBComments(
		String className, long classPK, int[] status, int start, int end) {

		return getService().getKBComments(
			className, classPK, status, start, end);
	}

	/**
	 * Returns all the kb comments matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb comments
	 * @param companyId the primary key of the company
	 * @return the matching kb comments, or an empty list if no matches were found
	 */
	public static List<KBComment> getKBCommentsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getKBCommentsByUuidAndCompanyId(uuid, companyId);
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
	public static List<KBComment> getKBCommentsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBComment> orderByComparator) {

		return getService().getKBCommentsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kb comments.
	 *
	 * @return the number of kb comments
	 */
	public static int getKBCommentsCount() {
		return getService().getKBCommentsCount();
	}

	public static int getKBCommentsCount(long groupId, int status) {
		return getService().getKBCommentsCount(groupId, status);
	}

	public static int getKBCommentsCount(
		long userId, String className, long classPK) {

		return getService().getKBCommentsCount(userId, className, classPK);
	}

	public static int getKBCommentsCount(String className, long classPK) {
		return getService().getKBCommentsCount(className, classPK);
	}

	public static int getKBCommentsCount(
		String className, long classPK, int status) {

		return getService().getKBCommentsCount(className, classPK, status);
	}

	public static int getKBCommentsCount(
		String className, long classPK, int[] status) {

		return getService().getKBCommentsCount(className, classPK, status);
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
	public static KBComment updateKBComment(KBComment kbComment) {
		return getService().updateKBComment(kbComment);
	}

	public static KBComment updateKBComment(
			long kbCommentId, long classNameId, long classPK, String content,
			int userRating, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateKBComment(
			kbCommentId, classNameId, classPK, content, userRating, status,
			serviceContext);
	}

	public static KBComment updateKBComment(
			long kbCommentId, long classNameId, long classPK, String content,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateKBComment(
			kbCommentId, classNameId, classPK, content, status, serviceContext);
	}

	public static KBComment updateStatus(
			long userId, long kbCommentId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(
			userId, kbCommentId, status, serviceContext);
	}

	public static KBCommentLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KBCommentLocalService> _serviceSnapshot =
		new Snapshot<>(
			KBCommentLocalServiceUtil.class, KBCommentLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-433843822