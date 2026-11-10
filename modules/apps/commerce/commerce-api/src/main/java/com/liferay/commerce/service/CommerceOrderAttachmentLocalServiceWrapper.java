/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceOrderAttachmentLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderAttachmentLocalService
 * @generated
 */
public class CommerceOrderAttachmentLocalServiceWrapper
	implements CommerceOrderAttachmentLocalService,
			   ServiceWrapper<CommerceOrderAttachmentLocalService> {

	public CommerceOrderAttachmentLocalServiceWrapper() {
		this(null);
	}

	public CommerceOrderAttachmentLocalServiceWrapper(
		CommerceOrderAttachmentLocalService
			commerceOrderAttachmentLocalService) {

		_commerceOrderAttachmentLocalService =
			commerceOrderAttachmentLocalService;
	}

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
	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
		addCommerceOrderAttachment(
			com.liferay.commerce.model.CommerceOrderAttachment
				commerceOrderAttachment) {

		return _commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
			commerceOrderAttachment);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
			addCommerceOrderAttachment(
				String externalReferenceCode, long userId, long commerceOrderId,
				double priority, boolean restricted, String title, String type,
				String fileName, java.io.InputStream inputStream)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
			externalReferenceCode, userId, commerceOrderId, priority,
			restricted, title, type, fileName, inputStream);
	}

	/**
	 * Creates a new commerce order attachment with the primary key. Does not add the commerce order attachment to the database.
	 *
	 * @param commerceOrderAttachmentId the primary key for the new commerce order attachment
	 * @return the new commerce order attachment
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
		createCommerceOrderAttachment(long commerceOrderAttachmentId) {

		return _commerceOrderAttachmentLocalService.
			createCommerceOrderAttachment(commerceOrderAttachmentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderAttachmentLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
			deleteCommerceOrderAttachment(
				com.liferay.commerce.model.CommerceOrderAttachment
					commerceOrderAttachment)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderAttachmentLocalService.
			deleteCommerceOrderAttachment(commerceOrderAttachment);
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
	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
			deleteCommerceOrderAttachment(long commerceOrderAttachmentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderAttachmentLocalService.
			deleteCommerceOrderAttachment(commerceOrderAttachmentId);
	}

	@Override
	public void deleteCommerceOrderAttachments(long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderAttachmentLocalService.deleteCommerceOrderAttachments(
			commerceOrderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderAttachmentLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceOrderAttachmentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceOrderAttachmentLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceOrderAttachmentLocalService.dynamicQuery();
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

		return _commerceOrderAttachmentLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commerceOrderAttachmentLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commerceOrderAttachmentLocalService.dynamicQuery(
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

		return _commerceOrderAttachmentLocalService.dynamicQueryCount(
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

		return _commerceOrderAttachmentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
		fetchCommerceOrderAttachment(long commerceOrderAttachmentId) {

		return _commerceOrderAttachmentLocalService.
			fetchCommerceOrderAttachment(commerceOrderAttachmentId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
		fetchCommerceOrderAttachmentByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceOrderAttachmentLocalService.
			fetchCommerceOrderAttachmentByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order attachment matching the UUID and group.
	 *
	 * @param uuid the commerce order attachment's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order attachment, or <code>null</code> if a matching commerce order attachment could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
		fetchCommerceOrderAttachmentByUuidAndGroupId(
			String uuid, long groupId) {

		return _commerceOrderAttachmentLocalService.
			fetchCommerceOrderAttachmentByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceOrderAttachmentLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce order attachment with the primary key.
	 *
	 * @param commerceOrderAttachmentId the primary key of the commerce order attachment
	 * @return the commerce order attachment
	 * @throws PortalException if a commerce order attachment with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
			getCommerceOrderAttachment(long commerceOrderAttachmentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderAttachmentLocalService.getCommerceOrderAttachment(
			commerceOrderAttachmentId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
			getCommerceOrderAttachmentByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderAttachmentLocalService.
			getCommerceOrderAttachmentByExternalReferenceCode(
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
	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
			getCommerceOrderAttachmentByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderAttachmentLocalService.
			getCommerceOrderAttachmentByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderAttachment>
		getCommerceOrderAttachments(int start, int end) {

		return _commerceOrderAttachmentLocalService.getCommerceOrderAttachments(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderAttachment>
		getCommerceOrderAttachments(
			long commerceOrderId, boolean restricted, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrderAttachment>
					orderByComparator) {

		return _commerceOrderAttachmentLocalService.getCommerceOrderAttachments(
			commerceOrderId, restricted, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderAttachment>
		getCommerceOrderAttachments(
			long commerceOrderId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrderAttachment>
					orderByComparator) {

		return _commerceOrderAttachmentLocalService.getCommerceOrderAttachments(
			commerceOrderId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce order attachments matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce order attachments
	 * @param companyId the primary key of the company
	 * @return the matching commerce order attachments, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderAttachment>
		getCommerceOrderAttachmentsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _commerceOrderAttachmentLocalService.
			getCommerceOrderAttachmentsByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderAttachment>
		getCommerceOrderAttachmentsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrderAttachment>
					orderByComparator) {

		return _commerceOrderAttachmentLocalService.
			getCommerceOrderAttachmentsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce order attachments.
	 *
	 * @return the number of commerce order attachments
	 */
	@Override
	public int getCommerceOrderAttachmentsCount() {
		return _commerceOrderAttachmentLocalService.
			getCommerceOrderAttachmentsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceOrderAttachmentLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceOrderAttachmentLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceOrderAttachmentLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderAttachmentLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
		updateCommerceOrderAttachment(
			com.liferay.commerce.model.CommerceOrderAttachment
				commerceOrderAttachment) {

		return _commerceOrderAttachmentLocalService.
			updateCommerceOrderAttachment(commerceOrderAttachment);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderAttachment
			updateCommerceOrderAttachment(
				long commerceOrderAttachmentId, double priority,
				boolean restricted, String title, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderAttachmentLocalService.
			updateCommerceOrderAttachment(
				commerceOrderAttachmentId, priority, restricted, title, type);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceOrderAttachmentLocalService.getBasePersistence();
	}

	@Override
	public CommerceOrderAttachmentLocalService getWrappedService() {
		return _commerceOrderAttachmentLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceOrderAttachmentLocalService
			commerceOrderAttachmentLocalService) {

		_commerceOrderAttachmentLocalService =
			commerceOrderAttachmentLocalService;
	}

	private CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1271752911