/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
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
 * Provides the local service utility for DDMTemplate. This utility wraps
 * <code>com.liferay.dynamic.data.mapping.service.impl.DDMTemplateLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMTemplateLocalService
 * @generated
 */
public class DDMTemplateLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMTemplateLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the ddm template to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmTemplate the ddm template
	 * @return the ddm template that was added
	 */
	public static DDMTemplate addDDMTemplate(DDMTemplate ddmTemplate) {
		return getService().addDDMTemplate(ddmTemplate);
	}

	/**
	 * Adds a template.
	 *
	 * @param externalReferenceCode the template's external reference code
	 * @param userId the primary key of the template's creator/owner
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param classPK the primary key of the template's related entity
	 * @param resourceClassNameId the primary key of the class name for
	 template's resource model
	 * @param nameMap the template's locales and localized names
	 * @param descriptionMap the template's locales and localized descriptions
	 * @param type the template's type. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param mode the template's mode. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param language the template's script language. For more information,
	 see DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param script the template's script
	 * @param serviceContext the service context to be applied. Can set the
	 UUID, creation date, modification date, guest permissions, and
	 group permissions for the template.
	 * @return the template
	 * @throws PortalException if a portal exception occurred
	 */
	public static DDMTemplate addTemplate(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long resourceClassNameId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, String type,
			String mode, String language, String script,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addTemplate(
			externalReferenceCode, userId, groupId, classNameId, classPK,
			resourceClassNameId, nameMap, descriptionMap, type, mode, language,
			script, serviceContext);
	}

	/**
	 * Adds a template with additional parameters.
	 *
	 * @param externalReferenceCode the template's external reference code
	 * @param userId the primary key of the template's creator/owner
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param classPK the primary key of the template's related entity
	 * @param resourceClassNameId the primary key of the class name for
	 template's resource model
	 * @param templateKey the unique string identifying the template
	 (optionally <code>null</code>)
	 * @param nameMap the template's locales and localized names
	 * @param descriptionMap the template's locales and localized descriptions
	 * @param type the template's type. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param mode the template's mode. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param language the template's script language. For more information,
	 see DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param script the template's script
	 * @param cacheable whether the template is cacheable
	 * @param smallImage whether the template has a small image
	 * @param smallImageURL the template's small image URL (optionally
	 <code>null</code>)
	 * @param smallImageFile the template's small image file (optionally
	 <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 UUID, creation date, modification date, guest permissions, and
	 group permissions for the template.
	 * @return the template
	 * @throws PortalException if a portal exception occurred
	 */
	public static DDMTemplate addTemplate(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long resourceClassNameId,
			String templateKey, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, String type,
			String mode, String language, String script, boolean cacheable,
			boolean smallImage, String smallImageURL,
			java.io.File smallImageFile,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addTemplate(
			externalReferenceCode, userId, groupId, classNameId, classPK,
			resourceClassNameId, templateKey, nameMap, descriptionMap, type,
			mode, language, script, cacheable, smallImage, smallImageURL,
			smallImageFile, serviceContext);
	}

	/**
	 * Adds the resources to the template.
	 *
	 * @param template the template to add resources to
	 * @param addGroupPermissions whether to add group permissions
	 * @param addGuestPermissions whether to add guest permissions
	 * @throws PortalException
	 */
	public static void addTemplateResources(
			DDMTemplate template, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addTemplateResources(
			template, addGroupPermissions, addGuestPermissions);
	}

	/**
	 * Adds the model resources with the permissions to the template.
	 *
	 * @param template the template to add resources to
	 * @param modelPermissions the model permissions to be added
	 * @throws PortalException if a portal exception occurred
	 */
	public static void addTemplateResources(
			DDMTemplate template,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addTemplateResources(template, modelPermissions);
	}

	/**
	 * Copies the template, creating a new template with all the values
	 * extracted from the original one. This method supports defining a new name
	 * and description.
	 *
	 * @param userId the primary key of the template's creator/owner
	 * @param sourceTemplateId the primary key of the template to be copied
	 * @param nameMap the new template's locales and localized names
	 * @param descriptionMap the new template's locales and localized
	 descriptions
	 * @param serviceContext the service context to be applied. Can set the
	 UUID, creation date, modification date, guest permissions, and
	 group permissions for the template.
	 * @return the new template
	 * @throws PortalException if a portal exception occurred
	 */
	public static DDMTemplate copyTemplate(
			long userId, long sourceTemplateId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().copyTemplate(
			userId, sourceTemplateId, nameMap, descriptionMap, serviceContext);
	}

	public static DDMTemplate copyTemplate(
			long userId, long sourceTemplateId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().copyTemplate(
			userId, sourceTemplateId, serviceContext);
	}

	/**
	 * Copies all the templates matching the class name ID, class PK, and type.
	 * This method creates new templates, extracting all the values from the old
	 * ones and updating their class PKs.
	 *
	 * @param userId the primary key of the template's creator/owner
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param sourceClassPK the primary key of the old template's related entity
	 * @param targetClassPK the primary key of the new template's related entity
	 * @param type the template's type. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param serviceContext the service context to be applied. Can set the
	 creation date, modification date, guest permissions, and group
	 permissions for the new templates.
	 * @return the new templates
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<DDMTemplate> copyTemplates(
			long userId, long classNameId, long sourceClassPK,
			long targetClassPK, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().copyTemplates(
			userId, classNameId, sourceClassPK, targetClassPK, type,
			serviceContext);
	}

	/**
	 * Creates a new ddm template with the primary key. Does not add the ddm template to the database.
	 *
	 * @param templateId the primary key for the new ddm template
	 * @return the new ddm template
	 */
	public static DDMTemplate createDDMTemplate(long templateId) {
		return getService().createDDMTemplate(templateId);
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
	 * Deletes the ddm template from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmTemplate the ddm template
	 * @return the ddm template that was removed
	 */
	public static DDMTemplate deleteDDMTemplate(DDMTemplate ddmTemplate) {
		return getService().deleteDDMTemplate(ddmTemplate);
	}

	/**
	 * Deletes the ddm template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateId the primary key of the ddm template
	 * @return the ddm template that was removed
	 * @throws PortalException if a ddm template with the primary key could not be found
	 */
	public static DDMTemplate deleteDDMTemplate(long templateId)
		throws PortalException {

		return getService().deleteDDMTemplate(templateId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the template and its resources.
	 *
	 * @param template the template to be deleted
	 * @throws PortalException if a portal exception occurred
	 */
	public static DDMTemplate deleteTemplate(DDMTemplate template)
		throws PortalException {

		return getService().deleteTemplate(template);
	}

	/**
	 * Deletes the template and its resources.
	 *
	 * @param templateId the primary key of the template to be deleted
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteTemplate(long templateId) throws PortalException {
		getService().deleteTemplate(templateId);
	}

	public static DDMTemplate deleteTemplate(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteTemplate(externalReferenceCode, groupId);
	}

	/**
	 * Deletes all the templates of the group.
	 *
	 * @param groupId the primary key of the group
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteTemplates(long groupId) throws PortalException {
		getService().deleteTemplates(groupId);
	}

	public static void deleteTemplates(long groupId, long classNameId)
		throws PortalException {

		getService().deleteTemplates(groupId, classNameId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl</code>.
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

	public static DDMTemplate fetchDDMTemplate(long templateId) {
		return getService().fetchDDMTemplate(templateId);
	}

	public static DDMTemplate fetchDDMTemplateByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchDDMTemplateByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static DDMTemplate fetchDDMTemplateByExternalReferenceCode(
		String externalReferenceCode, long groupId,
		boolean includeAncestorTemplates) {

		return getService().fetchDDMTemplateByExternalReferenceCode(
			externalReferenceCode, groupId, includeAncestorTemplates);
	}

	/**
	 * Returns the ddm template matching the UUID and group.
	 *
	 * @param uuid the ddm template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	public static DDMTemplate fetchDDMTemplateByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDDMTemplateByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns the template with the primary key.
	 *
	 * @param templateId the primary key of the template
	 * @return the matching template, or <code>null</code> if a matching
	 template could not be found
	 */
	public static DDMTemplate fetchTemplate(long templateId) {
		return getService().fetchTemplate(templateId);
	}

	/**
	 * Returns the template matching the group and template key.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param templateKey the unique string identifying the template
	 * @return the matching template, or <code>null</code> if a matching
	 template could not be found
	 */
	public static DDMTemplate fetchTemplate(
		long groupId, long classNameId, String templateKey) {

		return getService().fetchTemplate(groupId, classNameId, templateKey);
	}

	/**
	 * Returns the template matching the group and template key, optionally
	 * searching ancestor sites (that have sharing enabled) and global scoped
	 * sites.
	 *
	 * <p>
	 * This method first searches in the given group. If the template is still
	 * not found and <code>includeAncestorTemplates</code> is set to
	 * <code>true</code>, this method searches the group's ancestor sites (that
	 * have sharing enabled) and lastly searches global scoped sites.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param templateKey the unique string identifying the template
	 * @param includeAncestorTemplates whether to include ancestor sites (that
	 have sharing enabled) and include global scoped sites in the
	 search in the search
	 * @return the matching template, or <code>null</code> if a matching
	 template could not be found
	 */
	public static DDMTemplate fetchTemplate(
		long groupId, long classNameId, String templateKey,
		boolean includeAncestorTemplates) {

		return getService().fetchTemplate(
			groupId, classNameId, templateKey, includeAncestorTemplates);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm template with the primary key.
	 *
	 * @param templateId the primary key of the ddm template
	 * @return the ddm template
	 * @throws PortalException if a ddm template with the primary key could not be found
	 */
	public static DDMTemplate getDDMTemplate(long templateId)
		throws PortalException {

		return getService().getDDMTemplate(templateId);
	}

	public static DDMTemplate getDDMTemplateByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getDDMTemplateByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the ddm template matching the UUID and group.
	 *
	 * @param uuid the ddm template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm template
	 * @throws PortalException if a matching ddm template could not be found
	 */
	public static DDMTemplate getDDMTemplateByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDDMTemplateByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the ddm templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @return the range of ddm templates
	 */
	public static List<DDMTemplate> getDDMTemplates(int start, int end) {
		return getService().getDDMTemplates(start, end);
	}

	/**
	 * Returns all the ddm templates matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm templates
	 * @param companyId the primary key of the company
	 * @return the matching ddm templates, or an empty list if no matches were found
	 */
	public static List<DDMTemplate> getDDMTemplatesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDDMTemplatesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of ddm templates matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm templates
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching ddm templates, or an empty list if no matches were found
	 */
	public static List<DDMTemplate> getDDMTemplatesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return getService().getDDMTemplatesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of ddm templates.
	 *
	 * @return the number of ddm templates
	 */
	public static int getDDMTemplatesCount() {
		return getService().getDDMTemplatesCount();
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
	 * Returns the template with the primary key.
	 *
	 * @param templateId the primary key of the template
	 * @return the template with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static DDMTemplate getTemplate(long templateId)
		throws PortalException {

		return getService().getTemplate(templateId);
	}

	/**
	 * Returns the template matching the group and template key.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param templateKey the unique string identifying the template
	 * @return the matching template
	 * @throws PortalException if a portal exception occurred
	 */
	public static DDMTemplate getTemplate(
			long groupId, long classNameId, String templateKey)
		throws PortalException {

		return getService().getTemplate(groupId, classNameId, templateKey);
	}

	/**
	 * Returns the template matching the group and template key, optionally
	 * searching ancestor sites (that have sharing enabled) and global scoped
	 * sites.
	 *
	 * <p>
	 * This method first searches in the group. If the template is still not
	 * found and <code>includeAncestorTemplates</code> is set to
	 * <code>true</code>, this method searches the group's ancestor sites (that
	 * have sharing enabled) and lastly searches global scoped sites.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param templateKey the unique string identifying the template
	 * @param includeAncestorTemplates whether to include ancestor sites (that
	 have sharing enabled) and include global scoped sites in the
	 search in the search
	 * @return the matching template
	 * @throws PortalException if a portal exception occurred
	 */
	public static DDMTemplate getTemplate(
			long groupId, long classNameId, String templateKey,
			boolean includeAncestorTemplates)
		throws PortalException {

		return getService().getTemplate(
			groupId, classNameId, templateKey, includeAncestorTemplates);
	}

	public static DDMTemplate getTemplateBySmallImageId(long smallImageId)
		throws PortalException {

		return getService().getTemplateBySmallImageId(smallImageId);
	}

	/**
	 * Returns all the templates with the class PK.
	 *
	 * @param classPK the primary key of the template's related entity
	 * @return the templates with the class PK
	 */
	public static List<DDMTemplate> getTemplates(long classPK) {
		return getService().getTemplates(classPK);
	}

	/**
	 * Returns all the templates matching the group and class name ID.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @return the matching templates
	 */
	public static List<DDMTemplate> getTemplates(
		long groupId, long classNameId) {

		return getService().getTemplates(groupId, classNameId);
	}

	/**
	 * Returns all the templates matching the group, class name ID, and class
	 * PK.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param classPK the primary key of the template's related entity
	 * @return the matching templates
	 */
	public static List<DDMTemplate> getTemplates(
		long groupId, long classNameId, long classPK) {

		return getService().getTemplates(groupId, classNameId, classPK);
	}

	public static List<DDMTemplate> getTemplates(
			long groupId, long classNameId, long classPK,
			boolean includeAncestorTemplates)
		throws PortalException {

		return getService().getTemplates(
			groupId, classNameId, classPK, includeAncestorTemplates);
	}

	/**
	 * Returns all the templates matching the group, class name ID, class PK,
	 * and type.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param classPK the primary key of the template's related entity
	 * @param type the template's type. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @return the matching templates
	 */
	public static List<DDMTemplate> getTemplates(
		long groupId, long classNameId, long classPK, String type) {

		return getService().getTemplates(groupId, classNameId, classPK, type);
	}

	/**
	 * Returns all the templates matching the group, class name ID, class PK,
	 * type, and mode.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param classPK the primary key of the template's related entity
	 * @param type the template's type. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param mode the template's mode. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @return the matching templates
	 */
	public static List<DDMTemplate> getTemplates(
		long groupId, long classNameId, long classPK, String type,
		String mode) {

		return getService().getTemplates(
			groupId, classNameId, classPK, type, mode);
	}

	public static List<DDMTemplate> getTemplates(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return getService().getTemplates(
			companyId, groupIds, classNameIds, classPKs, resourceClassNameId,
			start, end, orderByComparator);
	}

	public static List<DDMTemplate> getTemplates(
		long[] groupIds, long classNameId, long classPK) {

		return getService().getTemplates(groupIds, classNameId, classPK);
	}

	public static List<DDMTemplate> getTemplatesByClassPK(
		long groupId, long classPK) {

		return getService().getTemplatesByClassPK(groupId, classPK);
	}

	public static List<DDMTemplate> getTemplatesByClassPK(
		long groupId, long classPK, int start, int end) {

		return getService().getTemplatesByClassPK(groupId, classPK, start, end);
	}

	public static List<DDMTemplate> getTemplatesByClassPK(
		long[] groupIds, long classPK) {

		return getService().getTemplatesByClassPK(groupIds, classPK);
	}

	/**
	 * Returns the number of templates matching the group and class PK.
	 *
	 * @param groupId the primary key of the group
	 * @param classPK the primary key of the template's related entity
	 * @return the number of templates belonging to the group and class PK
	 */
	public static int getTemplatesByClassPKCount(long groupId, long classPK) {
		return getService().getTemplatesByClassPKCount(groupId, classPK);
	}

	public static List<DDMTemplate> getTemplatesByGroupId(long groupId) {
		return getService().getTemplatesByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the templates matching the group,
	 * structure class name ID, and status.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param structureClassNameId the primary key of the class name for the
	 template's related structure
	 * @param status the template's workflow status. For more information see
	 {@link WorkflowConstants} for constants starting with the
	 "STATUS_" prefix.
	 * @param start the lower bound of the range of templates to return
	 * @param end the upper bound of the range of templates to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the templates
	 (optionally <code>null</code>)
	 * @return the range of matching templates ordered by the comparator
	 */
	public static List<DDMTemplate> getTemplatesByStructureClassNameId(
		long groupId, long structureClassNameId, int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return getService().getTemplatesByStructureClassNameId(
			groupId, structureClassNameId, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of templates belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the number of templates belonging to the group
	 */
	public static int getTemplatesCount(long groupId) {
		return getService().getTemplatesCount(groupId);
	}

	/**
	 * Returns the number of templates matching the group and class name ID.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @return the number of matching templates
	 */
	public static int getTemplatesCount(long groupId, long classNameId) {
		return getService().getTemplatesCount(groupId, classNameId);
	}

	/**
	 * Returns the number of templates matching the group, class name ID, and
	 * class PK.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param classPK the primary key of the template's related entity
	 * @return the number of matching templates
	 */
	public static int getTemplatesCount(
		long groupId, long classNameId, long classPK) {

		return getService().getTemplatesCount(groupId, classNameId, classPK);
	}

	/**
	 * Returns the number of templates matching the group IDs, class name ID,
	 * and class PK.
	 *
	 * @param groupIds the primary keys of the groups
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param classPK the primary key of the template's related entity
	 * @return the number of matching templates
	 */
	public static int getTemplatesCount(
		long[] groupIds, long classNameId, long classPK) {

		return getService().getTemplatesCount(groupIds, classNameId, classPK);
	}

	public static void revertTemplate(
			long userId, long templateId, String version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().revertTemplate(
			userId, templateId, version, serviceContext);
	}

	/**
	 * Returns an ordered range of all the templates matching the group, class
	 * name ID, class PK, type, mode, and status, and matching the keywords in
	 * the template names and descriptions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the template's company
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param classPK the primary key of the template's related entity
	 * @param resourceClassNameId the primary key of the class name for
	 template's resource model
	 * @param keywords the keywords (space separated), which may occur in the
	 template's name or description (optionally <code>null</code>)
	 * @param type the template's type (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param mode the template's mode (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param status the template's workflow status. For more information see
	 {@link WorkflowConstants} for constants starting with the
	 "STATUS_" prefix.
	 * @param start the lower bound of the range of templates to return
	 * @param end the upper bound of the range of templates to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the templates
	 (optionally <code>null</code>)
	 * @return the range of matching templates ordered by the comparator
	 */
	public static List<DDMTemplate> search(
		long companyId, long groupId, long classNameId, long classPK,
		long resourceClassNameId, String keywords, String type, String mode,
		int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return getService().search(
			companyId, groupId, classNameId, classPK, resourceClassNameId,
			keywords, type, mode, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the templates matching the group, class
	 * name ID, class PK, name keyword, description keyword, type, mode, status,
	 * and language.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the template's company
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param classPK the primary key of the template's related entity
	 * @param resourceClassNameId the primary key of the class name for
	 template's resource model
	 * @param name the name keywords (optionally <code>null</code>)
	 * @param description the description keywords (optionally
	 <code>null</code>)
	 * @param type the template's type (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param mode the template's mode (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param language the template's script language (optionally
	 <code>null</code>). For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param status the template's workflow status. For more information see
	 {@link WorkflowConstants} for constants starting with the
	 "STATUS_" prefix.
	 * @param andOperator whether every field must match its keywords, or just
	 one field
	 * @param start the lower bound of the range of templates to return
	 * @param end the upper bound of the range of templates to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the templates
	 (optionally <code>null</code>)
	 * @return the range of matching templates ordered by the comparator
	 */
	public static List<DDMTemplate> search(
		long companyId, long groupId, long classNameId, long classPK,
		long resourceClassNameId, String name, String description, String type,
		String mode, String language, int status, boolean andOperator,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator) {

		return getService().search(
			companyId, groupId, classNameId, classPK, resourceClassNameId, name,
			description, type, mode, language, status, andOperator, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the templates matching the group IDs,
	 * class Name IDs, class PK, type, mode, and status, and include the
	 * keywords on its names and descriptions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the template's company
	 * @param groupIds the primary keys of the groups
	 * @param classNameIds the primary keys of the entity's instances the
	 templates are related to
	 * @param classPKs the primary keys of the template's related entities
	 * @param resourceClassNameId the primary key of the class name for
	 template's resource model
	 * @param keywords the keywords (space separated), which may occur in the
	 template's name or description (optionally <code>null</code>)
	 * @param type the template's type (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param mode the template's mode (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param status the template's workflow status. For more information see
	 {@link WorkflowConstants} for constants starting with the
	 "STATUS_" prefix.
	 * @param start the lower bound of the range of templates to return
	 * @param end the upper bound of the range of templates to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the templates
	 (optionally <code>null</code>)
	 * @return the range of matching templates ordered by the comparator
	 */
	public static List<DDMTemplate> search(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String keywords, String type, String mode,
		int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return getService().search(
			companyId, groupIds, classNameIds, classPKs, resourceClassNameId,
			keywords, type, mode, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the templates matching the group IDs,
	 * class name IDs, class PK, name keyword, description keyword, type, mode,
	 * language, and status.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the template's company
	 * @param groupIds the primary keys of the groups
	 * @param classNameIds the primary keys of the entity's instances the
	 templates are related to
	 * @param classPKs the primary keys of the template's related entities
	 * @param resourceClassNameId the primary key of the class name for
	 template's resource model
	 * @param name the name keywords (optionally <code>null</code>)
	 * @param description the description keywords (optionally
	 <code>null</code>)
	 * @param type the template's type (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param mode the template's mode (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param language the template's script language (optionally
	 <code>null</code>). For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param status the template's workflow status. For more information see
	 {@link WorkflowConstants} for constants starting with the
	 "STATUS_" prefix.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @param start the lower bound of the range of templates to return
	 * @param end the upper bound of the range of templates to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the templates
	 (optionally <code>null</code>)
	 * @return the range of matching templates ordered by the comparator
	 */
	public static List<DDMTemplate> search(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String name, String description, String type,
		String mode, String language, int status, boolean andOperator,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator) {

		return getService().search(
			companyId, groupIds, classNameIds, classPKs, resourceClassNameId,
			name, description, type, mode, language, status, andOperator, start,
			end, orderByComparator);
	}

	/**
	 * Returns the number of templates matching the group, class name ID, class
	 * PK, type, mode, and status, and matching the keywords in the template
	 * names and descriptions.
	 *
	 * @param companyId the primary key of the template's company
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param classPK the primary key of the template's related entity
	 * @param resourceClassNameId the primary key of the class name for
	 template's resource model
	 * @param keywords the keywords (space separated), which may occur in the
	 template's name or description (optionally <code>null</code>)
	 * @param type the template's type (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param mode the template's mode (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param status the template's workflow status. For more information see
	 {@link WorkflowConstants} for constants starting with the
	 "STATUS_" prefix.
	 * @return the number of matching templates
	 */
	public static int searchCount(
		long companyId, long groupId, long classNameId, long classPK,
		long resourceClassNameId, String keywords, String type, String mode,
		int status) {

		return getService().searchCount(
			companyId, groupId, classNameId, classPK, resourceClassNameId,
			keywords, type, mode, status);
	}

	/**
	 * Returns the number of templates matching the group, class name ID, class
	 * PK, name keyword, description keyword, type, mode, language, and status.
	 *
	 * @param companyId the primary key of the template's company
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @param classPK the primary key of the template's related entity
	 * @param resourceClassNameId the primary key of the class name for
	 template's resource model
	 * @param name the name keywords (optionally <code>null</code>)
	 * @param description the description keywords (optionally
	 <code>null</code>)
	 * @param type the template's type (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param mode the template's mode (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param language the template's script language (optionally
	 <code>null</code>). For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param status the template's workflow status. For more information see
	 {@link WorkflowConstants} for constants starting with the
	 "STATUS_" prefix.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @return the number of matching templates
	 */
	public static int searchCount(
		long companyId, long groupId, long classNameId, long classPK,
		long resourceClassNameId, String name, String description, String type,
		String mode, String language, int status, boolean andOperator) {

		return getService().searchCount(
			companyId, groupId, classNameId, classPK, resourceClassNameId, name,
			description, type, mode, language, status, andOperator);
	}

	/**
	 * Returns the number of templates matching the group IDs, class name IDs,
	 * class PK, type, mode, and status, and matching the keywords in the
	 * template names and descriptions.
	 *
	 * @param companyId the primary key of the template's company
	 * @param groupIds the primary keys of the groups
	 * @param classNameIds the primary keys of the entity's instance the
	 templates are related to
	 * @param classPKs the primary keys of the template's related entities
	 * @param resourceClassNameId the primary key of the class name for
	 template's resource model
	 * @param keywords the keywords (space separated), which may occur in the
	 template's name or description (optionally <code>null</code>)
	 * @param type the template's type (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param mode the template's mode (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param status the template's workflow status. For more information see
	 {@link WorkflowConstants} for constants starting with the
	 "STATUS_" prefix.
	 * @return the number of matching templates
	 */
	public static int searchCount(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String keywords, String type, String mode,
		int status) {

		return getService().searchCount(
			companyId, groupIds, classNameIds, classPKs, resourceClassNameId,
			keywords, type, mode, status);
	}

	/**
	 * Returns the number of templates matching the group IDs, class name IDs,
	 * class PKs, name keyword, description keyword, type, mode, language, and
	 * status.
	 *
	 * @param companyId the primary key of the templates company
	 * @param groupIds the primary keys of the groups
	 * @param classNameIds the primary keys of the entity's instance the
	 templates are related to
	 * @param classPKs the primary keys of the template's related entities
	 * @param resourceClassNameId the primary key of the class name for
	 template's resource model
	 * @param name the name keywords (optionally <code>null</code>)
	 * @param description the description keywords (optionally
	 <code>null</code>)
	 * @param type the template's type (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param mode the template's mode (optionally <code>null</code>). For more
	 information, see DDMTemplateConstants in the
	 dynamic-data-mapping-api module.
	 * @param language the template's script language (optionally
	 <code>null</code>). For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param status the template's workflow status. For more information see
	 {@link WorkflowConstants} for constants starting with the
	 "STATUS_" prefix.
	 * @param andOperator whether every field must match its keywords, or just
	 one field.
	 * @return the number of matching templates
	 */
	public static int searchCount(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String name, String description, String type,
		String mode, String language, int status, boolean andOperator) {

		return getService().searchCount(
			companyId, groupIds, classNameIds, classPKs, resourceClassNameId,
			name, description, type, mode, language, status, andOperator);
	}

	/**
	 * Updates the ddm template in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmTemplate the ddm template
	 * @return the ddm template that was updated
	 */
	public static DDMTemplate updateDDMTemplate(DDMTemplate ddmTemplate) {
		return getService().updateDDMTemplate(ddmTemplate);
	}

	/**
	 * Updates the template matching the ID.
	 *
	 * @param userId the primary key of the template's creator/owner
	 * @param templateId the primary key of the template
	 * @param classPK the primary key of the template's related entity
	 * @param nameMap the template's new locales and localized names
	 * @param descriptionMap the template's new locales and localized
	 description
	 * @param type the template's type. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param mode the template's mode. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param language the template's script language. For more information,
	 see DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param script the template's script
	 * @param cacheable whether the template is cacheable
	 * @param smallImage whether the template has a small image
	 * @param smallImageURL the template's small image URL (optionally
	 <code>null</code>)
	 * @param smallImageFile the template's small image file (optionally
	 <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 modification date.
	 * @return the updated template
	 * @throws PortalException if a portal exception occurred
	 */
	public static DDMTemplate updateTemplate(
			long userId, long templateId, long classPK,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, String type,
			String mode, String language, String script, boolean cacheable,
			boolean smallImage, String smallImageURL,
			java.io.File smallImageFile,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateTemplate(
			userId, templateId, classPK, nameMap, descriptionMap, type, mode,
			language, script, cacheable, smallImage, smallImageURL,
			smallImageFile, serviceContext);
	}

	/**
	 * Updates the template matching the primary key.
	 *
	 * @param userId the primary key of the template's creator/owner
	 * @param templateId the primary key of the template
	 * @param classPK the primary key of the template's related entity
	 * @param nameMap the template's new locales and localized names
	 * @param descriptionMap the template's new locales and localized
	 description
	 * @param type the template's type. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param mode the template's mode. For more information, see
	 DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param language the template's script language. For more information,
	 see DDMTemplateConstants in the dynamic-data-mapping-api module.
	 * @param script the template's script
	 * @param cacheable whether the template is cacheable
	 * @param serviceContext the service context to be applied. Can set the
	 modification date.
	 * @return the updated template
	 * @throws PortalException if a portal exception occurred
	 */
	public static DDMTemplate updateTemplate(
			long userId, long templateId, long classPK,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, String type,
			String mode, String language, String script, boolean cacheable,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateTemplate(
			userId, templateId, classPK, nameMap, descriptionMap, type, mode,
			language, script, cacheable, serviceContext);
	}

	public static DDMTemplateLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DDMTemplateLocalService> _serviceSnapshot =
		new Snapshot<>(
			DDMTemplateLocalServiceUtil.class, DDMTemplateLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1017814388