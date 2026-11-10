/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service;

import com.liferay.commerce.notification.model.CommerceNotificationAttachment;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceNotificationAttachment. This utility wraps
 * <code>com.liferay.commerce.notification.service.impl.CommerceNotificationAttachmentLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceNotificationAttachmentLocalService
 * @deprecated As of Cavanaugh (7.4.x)
 * @generated
 */
@Deprecated
public class CommerceNotificationAttachmentLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.notification.service.impl.CommerceNotificationAttachmentLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce notification attachment to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationAttachment the commerce notification attachment
	 * @return the commerce notification attachment that was added
	 */
	public static CommerceNotificationAttachment
		addCommerceNotificationAttachment(
			CommerceNotificationAttachment commerceNotificationAttachment) {

		return getService().addCommerceNotificationAttachment(
			commerceNotificationAttachment);
	}

	/**
	 * Creates a new commerce notification attachment with the primary key. Does not add the commerce notification attachment to the database.
	 *
	 * @param commerceNotificationAttachmentId the primary key for the new commerce notification attachment
	 * @return the new commerce notification attachment
	 */
	public static CommerceNotificationAttachment
		createCommerceNotificationAttachment(
			long commerceNotificationAttachmentId) {

		return getService().createCommerceNotificationAttachment(
			commerceNotificationAttachmentId);
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
	 * Deletes the commerce notification attachment from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationAttachment the commerce notification attachment
	 * @return the commerce notification attachment that was removed
	 */
	public static CommerceNotificationAttachment
		deleteCommerceNotificationAttachment(
			CommerceNotificationAttachment commerceNotificationAttachment) {

		return getService().deleteCommerceNotificationAttachment(
			commerceNotificationAttachment);
	}

	/**
	 * Deletes the commerce notification attachment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationAttachmentId the primary key of the commerce notification attachment
	 * @return the commerce notification attachment that was removed
	 * @throws PortalException if a commerce notification attachment with the primary key could not be found
	 */
	public static CommerceNotificationAttachment
			deleteCommerceNotificationAttachment(
				long commerceNotificationAttachmentId)
		throws PortalException {

		return getService().deleteCommerceNotificationAttachment(
			commerceNotificationAttachmentId);
	}

	public static void deleteCommerceNotificationAttachments(
		long commerceNotificationQueueEntryId) {

		getService().deleteCommerceNotificationAttachments(
			commerceNotificationQueueEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationAttachmentModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationAttachmentModelImpl</code>.
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

	public static CommerceNotificationAttachment
		fetchCommerceNotificationAttachment(
			long commerceNotificationAttachmentId) {

		return getService().fetchCommerceNotificationAttachment(
			commerceNotificationAttachmentId);
	}

	/**
	 * Returns the commerce notification attachment matching the UUID and group.
	 *
	 * @param uuid the commerce notification attachment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce notification attachment, or <code>null</code> if a matching commerce notification attachment could not be found
	 */
	public static CommerceNotificationAttachment
		fetchCommerceNotificationAttachmentByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchCommerceNotificationAttachmentByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce notification attachment with the primary key.
	 *
	 * @param commerceNotificationAttachmentId the primary key of the commerce notification attachment
	 * @return the commerce notification attachment
	 * @throws PortalException if a commerce notification attachment with the primary key could not be found
	 */
	public static CommerceNotificationAttachment
			getCommerceNotificationAttachment(
				long commerceNotificationAttachmentId)
		throws PortalException {

		return getService().getCommerceNotificationAttachment(
			commerceNotificationAttachmentId);
	}

	/**
	 * Returns the commerce notification attachment matching the UUID and group.
	 *
	 * @param uuid the commerce notification attachment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce notification attachment
	 * @throws PortalException if a matching commerce notification attachment could not be found
	 */
	public static CommerceNotificationAttachment
			getCommerceNotificationAttachmentByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getCommerceNotificationAttachmentByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the commerce notification attachments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce notification attachments
	 * @param end the upper bound of the range of commerce notification attachments (not inclusive)
	 * @return the range of commerce notification attachments
	 */
	public static List<CommerceNotificationAttachment>
		getCommerceNotificationAttachments(int start, int end) {

		return getService().getCommerceNotificationAttachments(start, end);
	}

	/**
	 * Returns all the commerce notification attachments matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce notification attachments
	 * @param companyId the primary key of the company
	 * @return the matching commerce notification attachments, or an empty list if no matches were found
	 */
	public static List<CommerceNotificationAttachment>
		getCommerceNotificationAttachmentsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().
			getCommerceNotificationAttachmentsByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of commerce notification attachments matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce notification attachments
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce notification attachments
	 * @param end the upper bound of the range of commerce notification attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce notification attachments, or an empty list if no matches were found
	 */
	public static List<CommerceNotificationAttachment>
		getCommerceNotificationAttachmentsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CommerceNotificationAttachment>
				orderByComparator) {

		return getService().
			getCommerceNotificationAttachmentsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce notification attachments.
	 *
	 * @return the number of commerce notification attachments
	 */
	public static int getCommerceNotificationAttachmentsCount() {
		return getService().getCommerceNotificationAttachmentsCount();
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
	 * Updates the commerce notification attachment in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationAttachmentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationAttachment the commerce notification attachment
	 * @return the commerce notification attachment that was updated
	 */
	public static CommerceNotificationAttachment
		updateCommerceNotificationAttachment(
			CommerceNotificationAttachment commerceNotificationAttachment) {

		return getService().updateCommerceNotificationAttachment(
			commerceNotificationAttachment);
	}

	public static CommerceNotificationAttachmentLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceNotificationAttachmentLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceNotificationAttachmentLocalServiceUtil.class,
			CommerceNotificationAttachmentLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1911605894