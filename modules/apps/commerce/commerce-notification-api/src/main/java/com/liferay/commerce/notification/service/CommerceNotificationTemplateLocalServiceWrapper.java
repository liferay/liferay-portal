/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceNotificationTemplateLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceNotificationTemplateLocalService
 * @deprecated As of Cavanaugh (7.4.x)
 * @generated
 */
@Deprecated
public class CommerceNotificationTemplateLocalServiceWrapper
	implements CommerceNotificationTemplateLocalService,
			   ServiceWrapper<CommerceNotificationTemplateLocalService> {

	public CommerceNotificationTemplateLocalServiceWrapper() {
		this(null);
	}

	public CommerceNotificationTemplateLocalServiceWrapper(
		CommerceNotificationTemplateLocalService
			commerceNotificationTemplateLocalService) {

		_commerceNotificationTemplateLocalService =
			commerceNotificationTemplateLocalService;
	}

	/**
	 * Adds the commerce notification template to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplate the commerce notification template
	 * @return the commerce notification template that was added
	 */
	@Override
	public com.liferay.commerce.notification.model.CommerceNotificationTemplate
		addCommerceNotificationTemplate(
			com.liferay.commerce.notification.model.CommerceNotificationTemplate
				commerceNotificationTemplate) {

		return _commerceNotificationTemplateLocalService.
			addCommerceNotificationTemplate(commerceNotificationTemplate);
	}

	@Override
	public com.liferay.commerce.notification.model.CommerceNotificationTemplate
			addCommerceNotificationTemplate(
				long userId, long groupId, String name, String description,
				String from,
				java.util.Map<java.util.Locale, String> fromNameMap, String to,
				String cc, String bcc, String type, boolean enabled,
				java.util.Map<java.util.Locale, String> subjectMap,
				java.util.Map<java.util.Locale, String> bodyMap,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateLocalService.
			addCommerceNotificationTemplate(
				userId, groupId, name, description, from, fromNameMap, to, cc,
				bcc, type, enabled, subjectMap, bodyMap, serviceContext);
	}

	/**
	 * Creates a new commerce notification template with the primary key. Does not add the commerce notification template to the database.
	 *
	 * @param commerceNotificationTemplateId the primary key for the new commerce notification template
	 * @return the new commerce notification template
	 */
	@Override
	public com.liferay.commerce.notification.model.CommerceNotificationTemplate
		createCommerceNotificationTemplate(
			long commerceNotificationTemplateId) {

		return _commerceNotificationTemplateLocalService.
			createCommerceNotificationTemplate(commerceNotificationTemplateId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce notification template from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplate the commerce notification template
	 * @return the commerce notification template that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.notification.model.CommerceNotificationTemplate
			deleteCommerceNotificationTemplate(
				com.liferay.commerce.notification.model.
					CommerceNotificationTemplate commerceNotificationTemplate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateLocalService.
			deleteCommerceNotificationTemplate(commerceNotificationTemplate);
	}

	/**
	 * Deletes the commerce notification template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplateId the primary key of the commerce notification template
	 * @return the commerce notification template that was removed
	 * @throws PortalException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.notification.model.CommerceNotificationTemplate
			deleteCommerceNotificationTemplate(
				long commerceNotificationTemplateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateLocalService.
			deleteCommerceNotificationTemplate(commerceNotificationTemplateId);
	}

	@Override
	public void deleteCommerceNotificationTemplates(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceNotificationTemplateLocalService.
			deleteCommerceNotificationTemplates(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceNotificationTemplateLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceNotificationTemplateLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceNotificationTemplateLocalService.dynamicQuery();
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

		return _commerceNotificationTemplateLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateModelImpl</code>.
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

		return _commerceNotificationTemplateLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateModelImpl</code>.
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

		return _commerceNotificationTemplateLocalService.dynamicQuery(
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

		return _commerceNotificationTemplateLocalService.dynamicQueryCount(
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

		return _commerceNotificationTemplateLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.notification.model.CommerceNotificationTemplate
		fetchCommerceNotificationTemplate(long commerceNotificationTemplateId) {

		return _commerceNotificationTemplateLocalService.
			fetchCommerceNotificationTemplate(commerceNotificationTemplateId);
	}

	/**
	 * Returns the commerce notification template matching the UUID and group.
	 *
	 * @param uuid the commerce notification template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public com.liferay.commerce.notification.model.CommerceNotificationTemplate
		fetchCommerceNotificationTemplateByUuidAndGroupId(
			String uuid, long groupId) {

		return _commerceNotificationTemplateLocalService.
			fetchCommerceNotificationTemplateByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceNotificationTemplateLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce notification template with the primary key.
	 *
	 * @param commerceNotificationTemplateId the primary key of the commerce notification template
	 * @return the commerce notification template
	 * @throws PortalException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.notification.model.CommerceNotificationTemplate
			getCommerceNotificationTemplate(long commerceNotificationTemplateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateLocalService.
			getCommerceNotificationTemplate(commerceNotificationTemplateId);
	}

	/**
	 * Returns the commerce notification template matching the UUID and group.
	 *
	 * @param uuid the commerce notification template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce notification template
	 * @throws PortalException if a matching commerce notification template could not be found
	 */
	@Override
	public com.liferay.commerce.notification.model.CommerceNotificationTemplate
			getCommerceNotificationTemplateByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateLocalService.
			getCommerceNotificationTemplateByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the commerce notification templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @return the range of commerce notification templates
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.notification.model.CommerceNotificationTemplate>
			getCommerceNotificationTemplates(int start, int end) {

		return _commerceNotificationTemplateLocalService.
			getCommerceNotificationTemplates(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.notification.model.CommerceNotificationTemplate>
			getCommerceNotificationTemplates(
				long groupId, String type, boolean enabled) {

		return _commerceNotificationTemplateLocalService.
			getCommerceNotificationTemplates(groupId, type, enabled);
	}

	/**
	 * Returns all the commerce notification templates matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce notification templates
	 * @param companyId the primary key of the company
	 * @return the matching commerce notification templates, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.notification.model.CommerceNotificationTemplate>
			getCommerceNotificationTemplatesByUuidAndCompanyId(
				String uuid, long companyId) {

		return _commerceNotificationTemplateLocalService.
			getCommerceNotificationTemplatesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of commerce notification templates matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce notification templates
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce notification templates, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.notification.model.CommerceNotificationTemplate>
			getCommerceNotificationTemplatesByUuidAndCompanyId(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.notification.model.
						CommerceNotificationTemplate> orderByComparator) {

		return _commerceNotificationTemplateLocalService.
			getCommerceNotificationTemplatesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce notification templates.
	 *
	 * @return the number of commerce notification templates
	 */
	@Override
	public int getCommerceNotificationTemplatesCount() {
		return _commerceNotificationTemplateLocalService.
			getCommerceNotificationTemplatesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceNotificationTemplateLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceNotificationTemplateLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceNotificationTemplateLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce notification template in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplate the commerce notification template
	 * @return the commerce notification template that was updated
	 */
	@Override
	public com.liferay.commerce.notification.model.CommerceNotificationTemplate
		updateCommerceNotificationTemplate(
			com.liferay.commerce.notification.model.CommerceNotificationTemplate
				commerceNotificationTemplate) {

		return _commerceNotificationTemplateLocalService.
			updateCommerceNotificationTemplate(commerceNotificationTemplate);
	}

	@Override
	public com.liferay.commerce.notification.model.CommerceNotificationTemplate
			updateCommerceNotificationTemplate(
				long commerceNotificationTemplateId, String name,
				String description, String from,
				java.util.Map<java.util.Locale, String> fromNameMap, String to,
				String cc, String bcc, String type, boolean enabled,
				java.util.Map<java.util.Locale, String> subjectMap,
				java.util.Map<java.util.Locale, String> bodyMap,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateLocalService.
			updateCommerceNotificationTemplate(
				commerceNotificationTemplateId, name, description, from,
				fromNameMap, to, cc, bcc, type, enabled, subjectMap, bodyMap,
				serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceNotificationTemplateLocalService.getBasePersistence();
	}

	@Override
	public CommerceNotificationTemplateLocalService getWrappedService() {
		return _commerceNotificationTemplateLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceNotificationTemplateLocalService
			commerceNotificationTemplateLocalService) {

		_commerceNotificationTemplateLocalService =
			commerceNotificationTemplateLocalService;
	}

	private CommerceNotificationTemplateLocalService
		_commerceNotificationTemplateLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-900652774