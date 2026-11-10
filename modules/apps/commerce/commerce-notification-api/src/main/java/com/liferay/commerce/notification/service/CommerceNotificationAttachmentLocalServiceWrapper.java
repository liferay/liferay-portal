/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceNotificationAttachmentLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceNotificationAttachmentLocalService
 * @deprecated As of Cavanaugh (7.4.x)
 * @generated
 */
@Deprecated
public class CommerceNotificationAttachmentLocalServiceWrapper
	implements CommerceNotificationAttachmentLocalService,
			   ServiceWrapper<CommerceNotificationAttachmentLocalService> {

	public CommerceNotificationAttachmentLocalServiceWrapper() {
		this(null);
	}

	public CommerceNotificationAttachmentLocalServiceWrapper(
		CommerceNotificationAttachmentLocalService
			commerceNotificationAttachmentLocalService) {

		_commerceNotificationAttachmentLocalService =
			commerceNotificationAttachmentLocalService;
	}

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
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationAttachment
			addCommerceNotificationAttachment(
				com.liferay.commerce.notification.model.
					CommerceNotificationAttachment
						commerceNotificationAttachment) {

		return _commerceNotificationAttachmentLocalService.
			addCommerceNotificationAttachment(commerceNotificationAttachment);
	}

	/**
	 * Creates a new commerce notification attachment with the primary key. Does not add the commerce notification attachment to the database.
	 *
	 * @param commerceNotificationAttachmentId the primary key for the new commerce notification attachment
	 * @return the new commerce notification attachment
	 */
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationAttachment
			createCommerceNotificationAttachment(
				long commerceNotificationAttachmentId) {

		return _commerceNotificationAttachmentLocalService.
			createCommerceNotificationAttachment(
				commerceNotificationAttachmentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationAttachmentLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationAttachment
			deleteCommerceNotificationAttachment(
				com.liferay.commerce.notification.model.
					CommerceNotificationAttachment
						commerceNotificationAttachment) {

		return _commerceNotificationAttachmentLocalService.
			deleteCommerceNotificationAttachment(
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
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationAttachment
				deleteCommerceNotificationAttachment(
					long commerceNotificationAttachmentId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationAttachmentLocalService.
			deleteCommerceNotificationAttachment(
				commerceNotificationAttachmentId);
	}

	@Override
	public void deleteCommerceNotificationAttachments(
		long commerceNotificationQueueEntryId) {

		_commerceNotificationAttachmentLocalService.
			deleteCommerceNotificationAttachments(
				commerceNotificationQueueEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationAttachmentLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceNotificationAttachmentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceNotificationAttachmentLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceNotificationAttachmentLocalService.dynamicQuery();
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

		return _commerceNotificationAttachmentLocalService.dynamicQuery(
			dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commerceNotificationAttachmentLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commerceNotificationAttachmentLocalService.dynamicQuery(
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

		return _commerceNotificationAttachmentLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _commerceNotificationAttachmentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationAttachment
			fetchCommerceNotificationAttachment(
				long commerceNotificationAttachmentId) {

		return _commerceNotificationAttachmentLocalService.
			fetchCommerceNotificationAttachment(
				commerceNotificationAttachmentId);
	}

	/**
	 * Returns the commerce notification attachment matching the UUID and group.
	 *
	 * @param uuid the commerce notification attachment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce notification attachment, or <code>null</code> if a matching commerce notification attachment could not be found
	 */
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationAttachment
			fetchCommerceNotificationAttachmentByUuidAndGroupId(
				String uuid, long groupId) {

		return _commerceNotificationAttachmentLocalService.
			fetchCommerceNotificationAttachmentByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceNotificationAttachmentLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce notification attachment with the primary key.
	 *
	 * @param commerceNotificationAttachmentId the primary key of the commerce notification attachment
	 * @return the commerce notification attachment
	 * @throws PortalException if a commerce notification attachment with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationAttachment
				getCommerceNotificationAttachment(
					long commerceNotificationAttachmentId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationAttachmentLocalService.
			getCommerceNotificationAttachment(commerceNotificationAttachmentId);
	}

	/**
	 * Returns the commerce notification attachment matching the UUID and group.
	 *
	 * @param uuid the commerce notification attachment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce notification attachment
	 * @throws PortalException if a matching commerce notification attachment could not be found
	 */
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationAttachment
				getCommerceNotificationAttachmentByUuidAndGroupId(
					String uuid, long groupId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationAttachmentLocalService.
			getCommerceNotificationAttachmentByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List
		<com.liferay.commerce.notification.model.CommerceNotificationAttachment>
			getCommerceNotificationAttachments(int start, int end) {

		return _commerceNotificationAttachmentLocalService.
			getCommerceNotificationAttachments(start, end);
	}

	/**
	 * Returns all the commerce notification attachments matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce notification attachments
	 * @param companyId the primary key of the company
	 * @return the matching commerce notification attachments, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.notification.model.CommerceNotificationAttachment>
			getCommerceNotificationAttachmentsByUuidAndCompanyId(
				String uuid, long companyId) {

		return _commerceNotificationAttachmentLocalService.
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
	@Override
	public java.util.List
		<com.liferay.commerce.notification.model.CommerceNotificationAttachment>
			getCommerceNotificationAttachmentsByUuidAndCompanyId(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.notification.model.
						CommerceNotificationAttachment> orderByComparator) {

		return _commerceNotificationAttachmentLocalService.
			getCommerceNotificationAttachmentsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce notification attachments.
	 *
	 * @return the number of commerce notification attachments
	 */
	@Override
	public int getCommerceNotificationAttachmentsCount() {
		return _commerceNotificationAttachmentLocalService.
			getCommerceNotificationAttachmentsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceNotificationAttachmentLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceNotificationAttachmentLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceNotificationAttachmentLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationAttachmentLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationAttachment
			updateCommerceNotificationAttachment(
				com.liferay.commerce.notification.model.
					CommerceNotificationAttachment
						commerceNotificationAttachment) {

		return _commerceNotificationAttachmentLocalService.
			updateCommerceNotificationAttachment(
				commerceNotificationAttachment);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceNotificationAttachmentLocalService.getBasePersistence();
	}

	@Override
	public CommerceNotificationAttachmentLocalService getWrappedService() {
		return _commerceNotificationAttachmentLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceNotificationAttachmentLocalService
			commerceNotificationAttachmentLocalService) {

		_commerceNotificationAttachmentLocalService =
			commerceNotificationAttachmentLocalService;
	}

	private CommerceNotificationAttachmentLocalService
		_commerceNotificationAttachmentLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1103688283