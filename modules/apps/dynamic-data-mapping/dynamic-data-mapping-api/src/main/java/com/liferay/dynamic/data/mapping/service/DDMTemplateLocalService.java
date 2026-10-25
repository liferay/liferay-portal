/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;
import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for DDMTemplate. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMTemplateLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DDMTemplateLocalService
	extends BaseLocalService, CTService<DDMTemplate>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMTemplateLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the ddm template local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DDMTemplateLocalServiceUtil} if injection and service tracking are not available.
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
	@Indexable(type = IndexableType.REINDEX)
	public DDMTemplate addDDMTemplate(DDMTemplate ddmTemplate);

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
	public DDMTemplate addTemplate(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long resourceClassNameId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			String type, String mode, String language, String script,
			ServiceContext serviceContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public DDMTemplate addTemplate(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long resourceClassNameId,
			String templateKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, String type, String mode,
			String language, String script, boolean cacheable,
			boolean smallImage, String smallImageURL, File smallImageFile,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the resources to the template.
	 *
	 * @param template the template to add resources to
	 * @param addGroupPermissions whether to add group permissions
	 * @param addGuestPermissions whether to add guest permissions
	 * @throws PortalException
	 */
	public void addTemplateResources(
			DDMTemplate template, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	/**
	 * Adds the model resources with the permissions to the template.
	 *
	 * @param template the template to add resources to
	 * @param modelPermissions the model permissions to be added
	 * @throws PortalException if a portal exception occurred
	 */
	public void addTemplateResources(
			DDMTemplate template, ModelPermissions modelPermissions)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public DDMTemplate copyTemplate(
			long userId, long sourceTemplateId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DDMTemplate copyTemplate(
			long userId, long sourceTemplateId, ServiceContext serviceContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public List<DDMTemplate> copyTemplates(
			long userId, long classNameId, long sourceClassPK,
			long targetClassPK, String type, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new ddm template with the primary key. Does not add the ddm template to the database.
	 *
	 * @param templateId the primary key for the new ddm template
	 * @return the new ddm template
	 */
	@Transactional(enabled = false)
	public DDMTemplate createDDMTemplate(long templateId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public DDMTemplate deleteDDMTemplate(DDMTemplate ddmTemplate);

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
	@Indexable(type = IndexableType.DELETE)
	public DDMTemplate deleteDDMTemplate(long templateId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the template and its resources.
	 *
	 * @param template the template to be deleted
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public DDMTemplate deleteTemplate(DDMTemplate template)
		throws PortalException;

	/**
	 * Deletes the template and its resources.
	 *
	 * @param templateId the primary key of the template to be deleted
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteTemplate(long templateId) throws PortalException;

	public DDMTemplate deleteTemplate(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Deletes all the templates of the group.
	 *
	 * @param groupId the primary key of the group
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteTemplates(long groupId) throws PortalException;

	public void deleteTemplates(long groupId, long classNameId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate fetchDDMTemplate(long templateId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate fetchDDMTemplateByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate fetchDDMTemplateByExternalReferenceCode(
		String externalReferenceCode, long groupId,
		boolean includeAncestorTemplates);

	/**
	 * Returns the ddm template matching the UUID and group.
	 *
	 * @param uuid the ddm template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate fetchDDMTemplateByUuidAndGroupId(
		String uuid, long groupId);

	/**
	 * Returns the template with the primary key.
	 *
	 * @param templateId the primary key of the template
	 * @return the matching template, or <code>null</code> if a matching
	 template could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate fetchTemplate(long templateId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate fetchTemplate(
		long groupId, long classNameId, String templateKey);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate fetchTemplate(
		long groupId, long classNameId, String templateKey,
		boolean includeAncestorTemplates);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the ddm template with the primary key.
	 *
	 * @param templateId the primary key of the ddm template
	 * @return the ddm template
	 * @throws PortalException if a ddm template with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate getDDMTemplate(long templateId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate getDDMTemplateByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the ddm template matching the UUID and group.
	 *
	 * @param uuid the ddm template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm template
	 * @throws PortalException if a matching ddm template could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate getDDMTemplateByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getDDMTemplates(int start, int end);

	/**
	 * Returns all the ddm templates matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm templates
	 * @param companyId the primary key of the company
	 * @return the matching ddm templates, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getDDMTemplatesByUuidAndCompanyId(
		String uuid, long companyId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getDDMTemplatesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator);

	/**
	 * Returns the number of ddm templates.
	 *
	 * @return the number of ddm templates
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDDMTemplatesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns the template with the primary key.
	 *
	 * @param templateId the primary key of the template
	 * @return the template with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate getTemplate(long templateId) throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate getTemplate(
			long groupId, long classNameId, String templateKey)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate getTemplate(
			long groupId, long classNameId, String templateKey,
			boolean includeAncestorTemplates)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMTemplate getTemplateBySmallImageId(long smallImageId)
		throws PortalException;

	/**
	 * Returns all the templates with the class PK.
	 *
	 * @param classPK the primary key of the template's related entity
	 * @return the templates with the class PK
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplates(long classPK);

	/**
	 * Returns all the templates matching the group and class name ID.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @return the matching templates
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplates(long groupId, long classNameId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplates(
		long groupId, long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplates(
			long groupId, long classNameId, long classPK,
			boolean includeAncestorTemplates)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplates(
		long groupId, long classNameId, long classPK, String type);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplates(
		long groupId, long classNameId, long classPK, String type, String mode);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplates(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplates(
		long[] groupIds, long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplatesByClassPK(long groupId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplatesByClassPK(
		long groupId, long classPK, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplatesByClassPK(
		long[] groupIds, long classPK);

	/**
	 * Returns the number of templates matching the group and class PK.
	 *
	 * @param groupId the primary key of the group
	 * @param classPK the primary key of the template's related entity
	 * @return the number of templates belonging to the group and class PK
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTemplatesByClassPKCount(long groupId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplatesByGroupId(long groupId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> getTemplatesByStructureClassNameId(
		long groupId, long structureClassNameId, int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator);

	/**
	 * Returns the number of templates belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the number of templates belonging to the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTemplatesCount(long groupId);

	/**
	 * Returns the number of templates matching the group and class name ID.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the template's
	 related model
	 * @return the number of matching templates
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTemplatesCount(long groupId, long classNameId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTemplatesCount(long groupId, long classNameId, long classPK);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTemplatesCount(
		long[] groupIds, long classNameId, long classPK);

	public void revertTemplate(
			long userId, long templateId, String version,
			ServiceContext serviceContext)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> search(
		long companyId, long groupId, long classNameId, long classPK,
		long resourceClassNameId, String keywords, String type, String mode,
		int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> search(
		long companyId, long groupId, long classNameId, long classPK,
		long resourceClassNameId, String name, String description, String type,
		String mode, String language, int status, boolean andOperator,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> search(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String keywords, String type, String mode,
		int status, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMTemplate> search(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String name, String description, String type,
		String mode, String language, int status, boolean andOperator,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long groupId, long classNameId, long classPK,
		long resourceClassNameId, String keywords, String type, String mode,
		int status);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long groupId, long classNameId, long classPK,
		long resourceClassNameId, String name, String description, String type,
		String mode, String language, int status, boolean andOperator);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String keywords, String type, String mode,
		int status);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String name, String description, String type,
		String mode, String language, int status, boolean andOperator);

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
	@Indexable(type = IndexableType.REINDEX)
	public DDMTemplate updateDDMTemplate(DDMTemplate ddmTemplate);

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
	@Indexable(type = IndexableType.REINDEX)
	public DDMTemplate updateTemplate(
			long userId, long templateId, long classPK,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			String type, String mode, String language, String script,
			boolean cacheable, boolean smallImage, String smallImageURL,
			File smallImageFile, ServiceContext serviceContext)
		throws PortalException;

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
	public DDMTemplate updateTemplate(
			long userId, long templateId, long classPK,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			String type, String mode, String language, String script,
			boolean cacheable, ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<DDMTemplate> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<DDMTemplate> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMTemplate>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-357812603