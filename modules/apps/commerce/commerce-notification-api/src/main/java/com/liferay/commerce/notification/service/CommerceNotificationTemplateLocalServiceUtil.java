/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service;

import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for CommerceNotificationTemplate. This utility wraps
 * <code>com.liferay.commerce.notification.service.impl.CommerceNotificationTemplateLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceNotificationTemplateLocalService
 * @deprecated As of Cavanaugh (7.4.x)
 * @generated
 */
@Deprecated
public class CommerceNotificationTemplateLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.notification.service.impl.CommerceNotificationTemplateLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CommerceNotificationTemplate addCommerceNotificationTemplate(
		CommerceNotificationTemplate commerceNotificationTemplate) {

		return getService().addCommerceNotificationTemplate(
			commerceNotificationTemplate);
	}

	public static CommerceNotificationTemplate addCommerceNotificationTemplate(
			long userId, long groupId, String name, String description,
			String from, Map<java.util.Locale, String> fromNameMap, String to,
			String cc, String bcc, String type, boolean enabled,
			Map<java.util.Locale, String> subjectMap,
			Map<java.util.Locale, String> bodyMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceNotificationTemplate(
			userId, groupId, name, description, from, fromNameMap, to, cc, bcc,
			type, enabled, subjectMap, bodyMap, serviceContext);
	}

	/**
	 * Creates a new commerce notification template with the primary key. Does not add the commerce notification template to the database.
	 *
	 * @param commerceNotificationTemplateId the primary key for the new commerce notification template
	 * @return the new commerce notification template
	 */
	public static CommerceNotificationTemplate
		createCommerceNotificationTemplate(
			long commerceNotificationTemplateId) {

		return getService().createCommerceNotificationTemplate(
			commerceNotificationTemplateId);
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
	public static CommerceNotificationTemplate
			deleteCommerceNotificationTemplate(
				CommerceNotificationTemplate commerceNotificationTemplate)
		throws PortalException {

		return getService().deleteCommerceNotificationTemplate(
			commerceNotificationTemplate);
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
	public static CommerceNotificationTemplate
			deleteCommerceNotificationTemplate(
				long commerceNotificationTemplateId)
		throws PortalException {

		return getService().deleteCommerceNotificationTemplate(
			commerceNotificationTemplateId);
	}

	public static void deleteCommerceNotificationTemplates(long groupId)
		throws PortalException {

		getService().deleteCommerceNotificationTemplates(groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateModelImpl</code>.
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

	public static CommerceNotificationTemplate
		fetchCommerceNotificationTemplate(long commerceNotificationTemplateId) {

		return getService().fetchCommerceNotificationTemplate(
			commerceNotificationTemplateId);
	}

	/**
	 * Returns the commerce notification template matching the UUID and group.
	 *
	 * @param uuid the commerce notification template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	public static CommerceNotificationTemplate
		fetchCommerceNotificationTemplateByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchCommerceNotificationTemplateByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce notification template with the primary key.
	 *
	 * @param commerceNotificationTemplateId the primary key of the commerce notification template
	 * @return the commerce notification template
	 * @throws PortalException if a commerce notification template with the primary key could not be found
	 */
	public static CommerceNotificationTemplate getCommerceNotificationTemplate(
			long commerceNotificationTemplateId)
		throws PortalException {

		return getService().getCommerceNotificationTemplate(
			commerceNotificationTemplateId);
	}

	/**
	 * Returns the commerce notification template matching the UUID and group.
	 *
	 * @param uuid the commerce notification template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce notification template
	 * @throws PortalException if a matching commerce notification template could not be found
	 */
	public static CommerceNotificationTemplate
			getCommerceNotificationTemplateByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getCommerceNotificationTemplateByUuidAndGroupId(
			uuid, groupId);
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
	public static List<CommerceNotificationTemplate>
		getCommerceNotificationTemplates(int start, int end) {

		return getService().getCommerceNotificationTemplates(start, end);
	}

	public static List<CommerceNotificationTemplate>
		getCommerceNotificationTemplates(
			long groupId, String type, boolean enabled) {

		return getService().getCommerceNotificationTemplates(
			groupId, type, enabled);
	}

	/**
	 * Returns all the commerce notification templates matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce notification templates
	 * @param companyId the primary key of the company
	 * @return the matching commerce notification templates, or an empty list if no matches were found
	 */
	public static List<CommerceNotificationTemplate>
		getCommerceNotificationTemplatesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCommerceNotificationTemplatesByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<CommerceNotificationTemplate>
		getCommerceNotificationTemplatesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return getService().getCommerceNotificationTemplatesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce notification templates.
	 *
	 * @return the number of commerce notification templates
	 */
	public static int getCommerceNotificationTemplatesCount() {
		return getService().getCommerceNotificationTemplatesCount();
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
	 * Updates the commerce notification template in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplate the commerce notification template
	 * @return the commerce notification template that was updated
	 */
	public static CommerceNotificationTemplate
		updateCommerceNotificationTemplate(
			CommerceNotificationTemplate commerceNotificationTemplate) {

		return getService().updateCommerceNotificationTemplate(
			commerceNotificationTemplate);
	}

	public static CommerceNotificationTemplate
			updateCommerceNotificationTemplate(
				long commerceNotificationTemplateId, String name,
				String description, String from,
				Map<java.util.Locale, String> fromNameMap, String to, String cc,
				String bcc, String type, boolean enabled,
				Map<java.util.Locale, String> subjectMap,
				Map<java.util.Locale, String> bodyMap,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceNotificationTemplate(
			commerceNotificationTemplateId, name, description, from,
			fromNameMap, to, cc, bcc, type, enabled, subjectMap, bodyMap,
			serviceContext);
	}

	public static CommerceNotificationTemplateLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceNotificationTemplateLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceNotificationTemplateLocalServiceUtil.class,
			CommerceNotificationTemplateLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:160886374