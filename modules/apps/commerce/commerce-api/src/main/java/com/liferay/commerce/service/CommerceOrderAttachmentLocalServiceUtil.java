/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceOrderAttachment. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceOrderAttachmentLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderAttachmentLocalService
 * @generated
 */
public class CommerceOrderAttachmentLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceOrderAttachmentLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce order attachment to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderAttachment the commerce order attachment
	 * @return the commerce order attachment that was added
	 */
	public static CommerceOrderAttachment addCommerceOrderAttachment(
		CommerceOrderAttachment commerceOrderAttachment) {

		return getService().addCommerceOrderAttachment(commerceOrderAttachment);
	}

	public static CommerceOrderAttachment addCommerceOrderAttachment(
			String externalReferenceCode, long userId, long commerceOrderId,
			double priority, boolean restricted, String title, String type,
			String fileName, InputStream inputStream)
		throws PortalException {

		return getService().addCommerceOrderAttachment(
			externalReferenceCode, userId, commerceOrderId, priority,
			restricted, title, type, fileName, inputStream);
	}

	/**
	 * Creates a new commerce order attachment with the primary key. Does not add the commerce order attachment to the database.
	 *
	 * @param commerceOrderAttachmentId the primary key for the new commerce order attachment
	 * @return the new commerce order attachment
	 */
	public static CommerceOrderAttachment createCommerceOrderAttachment(
		long commerceOrderAttachmentId) {

		return getService().createCommerceOrderAttachment(
			commerceOrderAttachmentId);
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
	 * Deletes the commerce order attachment from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderAttachment the commerce order attachment
	 * @return the commerce order attachment that was removed
	 * @throws PortalException
	 */
	public static CommerceOrderAttachment deleteCommerceOrderAttachment(
			CommerceOrderAttachment commerceOrderAttachment)
		throws PortalException {

		return getService().deleteCommerceOrderAttachment(
			commerceOrderAttachment);
	}

	/**
	 * Deletes the commerce order attachment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderAttachmentId the primary key of the commerce order attachment
	 * @return the commerce order attachment that was removed
	 * @throws PortalException if a commerce order attachment with the primary key could not be found
	 */
	public static CommerceOrderAttachment deleteCommerceOrderAttachment(
			long commerceOrderAttachmentId)
		throws PortalException {

		return getService().deleteCommerceOrderAttachment(
			commerceOrderAttachmentId);
	}

	public static void deleteCommerceOrderAttachments(long commerceOrderId)
		throws PortalException {

		getService().deleteCommerceOrderAttachments(commerceOrderId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderAttachmentModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderAttachmentModelImpl</code>.
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

	public static CommerceOrderAttachment fetchCommerceOrderAttachment(
		long commerceOrderAttachmentId) {

		return getService().fetchCommerceOrderAttachment(
			commerceOrderAttachmentId);
	}

	public static CommerceOrderAttachment
		fetchCommerceOrderAttachmentByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommerceOrderAttachmentByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order attachment matching the UUID and group.
	 *
	 * @param uuid the commerce order attachment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order attachment, or <code>null</code> if a matching commerce order attachment could not be found
	 */
	public static CommerceOrderAttachment
		fetchCommerceOrderAttachmentByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchCommerceOrderAttachmentByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce order attachment with the primary key.
	 *
	 * @param commerceOrderAttachmentId the primary key of the commerce order attachment
	 * @return the commerce order attachment
	 * @throws PortalException if a commerce order attachment with the primary key could not be found
	 */
	public static CommerceOrderAttachment getCommerceOrderAttachment(
			long commerceOrderAttachmentId)
		throws PortalException {

		return getService().getCommerceOrderAttachment(
			commerceOrderAttachmentId);
	}

	public static CommerceOrderAttachment
			getCommerceOrderAttachmentByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceOrderAttachmentByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order attachment matching the UUID and group.
	 *
	 * @param uuid the commerce order attachment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order attachment
	 * @throws PortalException if a matching commerce order attachment could not be found
	 */
	public static CommerceOrderAttachment
			getCommerceOrderAttachmentByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getCommerceOrderAttachmentByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the commerce order attachments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order attachments
	 * @param end the upper bound of the range of commerce order attachments (not inclusive)
	 * @return the range of commerce order attachments
	 */
	public static List<CommerceOrderAttachment> getCommerceOrderAttachments(
		int start, int end) {

		return getService().getCommerceOrderAttachments(start, end);
	}

	public static List<CommerceOrderAttachment> getCommerceOrderAttachments(
		long commerceOrderId, boolean restricted, int start, int end,
		OrderByComparator<CommerceOrderAttachment> orderByComparator) {

		return getService().getCommerceOrderAttachments(
			commerceOrderId, restricted, start, end, orderByComparator);
	}

	public static List<CommerceOrderAttachment> getCommerceOrderAttachments(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderAttachment> orderByComparator) {

		return getService().getCommerceOrderAttachments(
			commerceOrderId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce order attachments matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce order attachments
	 * @param companyId the primary key of the company
	 * @return the matching commerce order attachments, or an empty list if no matches were found
	 */
	public static List<CommerceOrderAttachment>
		getCommerceOrderAttachmentsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCommerceOrderAttachmentsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of commerce order attachments matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce order attachments
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce order attachments
	 * @param end the upper bound of the range of commerce order attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce order attachments, or an empty list if no matches were found
	 */
	public static List<CommerceOrderAttachment>
		getCommerceOrderAttachmentsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CommerceOrderAttachment> orderByComparator) {

		return getService().getCommerceOrderAttachmentsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce order attachments.
	 *
	 * @return the number of commerce order attachments
	 */
	public static int getCommerceOrderAttachmentsCount() {
		return getService().getCommerceOrderAttachmentsCount();
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
	 * Updates the commerce order attachment in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderAttachment the commerce order attachment
	 * @return the commerce order attachment that was updated
	 */
	public static CommerceOrderAttachment updateCommerceOrderAttachment(
		CommerceOrderAttachment commerceOrderAttachment) {

		return getService().updateCommerceOrderAttachment(
			commerceOrderAttachment);
	}

	public static CommerceOrderAttachment updateCommerceOrderAttachment(
			long commerceOrderAttachmentId, double priority, boolean restricted,
			String title, String type)
		throws PortalException {

		return getService().updateCommerceOrderAttachment(
			commerceOrderAttachmentId, priority, restricted, title, type);
	}

	public static CommerceOrderAttachmentLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceOrderAttachmentLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceOrderAttachmentLocalServiceUtil.class,
			CommerceOrderAttachmentLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:219463742