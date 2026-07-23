/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.headless.admin.site.dto.v1_0.ClassSubtypeReference;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateOpenGraphSettings;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateSEOSettings;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateSettings;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.SitemapSettings;
import com.liferay.headless.admin.site.dto.v1_0.util.FileEntryUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.SubtypeUtil;
import com.liferay.headless.admin.site.internal.odata.entity.v1_0.DisplayPageTemplateEntityModel;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.DisplayPageTemplateFolderUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.PageSpecificationUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.site.internal.util.EnabledUtil;
import com.liferay.headless.admin.site.internal.util.LogUtil;
import com.liferay.headless.admin.site.resource.v1_0.DisplayPageTemplateResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 * @author Lourdes Fernández Besada
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/display-page-template.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = DisplayPageTemplateResource.class
)
public class DisplayPageTemplateResourceImpl
	extends BaseDisplayPageTemplateResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate
		<DisplayPageTemplate> {

	@Override
	public void deleteSiteDisplayPageTemplate(
			String siteExternalReferenceCode,
			String displayPageTemplateExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			displayPageTemplateExternalReferenceCode,
			GroupUtil.getStagingAwareGroupId(
				contextCompany.getCompanyId(), siteExternalReferenceCode));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<LayoutPageTemplateEntry>
		getExportImportDescriptor() {

		return new ExportImportDescriptor<>() {

			@Override
			public Function<LayoutPageTemplateEntry, Boolean>
				getApplicableModelFunction() {

				return layoutPageTemplateEntry ->
					layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE;
			}

			@Override
			public String getKey() {
				return LayoutPageTemplateEntry.class.getName() + "-" +
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE;
			}

			@Override
			public String getLabelLanguageKey() {
				return "display-page-templates";
			}

			@Override
			public Class<LayoutPageTemplateEntry> getModelClass() {
				return LayoutPageTemplateEntry.class;
			}

			@Override
			public List<String> getNestedFields() {
				return List.of("pageSpecifications", "thumbnailURLReference");
			}

			@Override
			public String getPortletId() {
				return LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES;
			}

			@Override
			public Scope getScope() {
				return Scope.SITE;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_DESIGN;
			}

			@Override
			public boolean isStagingSupported() {
				return true;
			}

		};
	}

	@Override
	public Page<DisplayPageTemplate>
			getSiteDisplayPageTemplateFolderDisplayPageTemplatesPage(
				String siteExternalReferenceCode,
				String displayPageTemplateFolderExternalReferenceCode,
				Boolean flatten)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					displayPageTemplateFolderExternalReferenceCode, groupId);

		if (!Objects.equals(
				LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
				layoutPageTemplateCollection.getType())) {

			throw new IllegalArgumentException(
				"The display page template folder type does not match the " +
					"display page type");
		}

		return Page.of(
			transform(
				_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
					groupId,
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
				layoutPageTemplateEntry ->
					_displayPageTemplateDTOConverter.toDTO(
						DTOConverterContextUtil.getDTOConverterContext(
							contextAcceptLanguage, _dtoConverterRegistry,
							contextHttpServletRequest,
							layoutPageTemplateEntry.
								getLayoutPageTemplateEntryId(),
							contextUriInfo, contextUser),
						layoutPageTemplateEntry)));
	}

	@Override
	public DisplayPageTemplate
			postSiteDisplayPageTemplateFolderDisplayPageTemplate(
				String siteExternalReferenceCode,
				String displayPageTemplateFolderExternalReferenceCode,
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			contextCompany.getCompanyId(), siteExternalReferenceCode);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					displayPageTemplateFolderExternalReferenceCode, groupId);

		if (!Objects.equals(
				LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
				layoutPageTemplateCollection.getType())) {

			throw new IllegalArgumentException(
				"The display page template folder type does not match the " +
					"display page type");
		}

		return _addDisplayPageTemplate(
			displayPageTemplate, groupId,
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId());
	}

	@Override
	public ContentPageSpecification
			postSiteDisplayPageTemplatePageSpecification(
				String siteExternalReferenceCode,
				String pageTemplateExternalReferenceCode,
				ContentPageSpecification contentPageSpecification)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode,
					GroupUtil.getStagingAwareGroupId(
						contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				layoutPageTemplateEntry.getType())) {

			throw new IllegalArgumentException(
				"The display page template type does not match the display " +
					"page type");
		}

		return (ContentPageSpecification)_pageSpecificationDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest, layoutPageTemplateEntry.getPlid(),
				contextUriInfo, contextUser),
			LayoutUtil.addDraftToLayout(
				_cetManager, contentPageSpecification,
				_fragmentEntryProcessorRegistry, _infoItemServiceRegistry,
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				ServiceContextUtil.createServiceContext(
					layoutPageTemplateEntry.getGroupId(),
					contextHttpServletRequest, contextUser.getUserId())));
	}

	@Override
	protected DisplayPageTemplate doGetSiteDisplayPageTemplate(
			String siteExternalReferenceCode,
			String displayPageTemplateExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplateExternalReferenceCode,
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				layoutPageTemplateEntry.getType())) {

			throw new IllegalArgumentException(
				"The display page template type does not match the display " +
					"page type");
		}

		return _displayPageTemplateDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				contextUriInfo, contextUser),
			layoutPageTemplateEntry);
	}

	@Override
	protected Page<DisplayPageTemplate> doGetSiteDisplayPageTemplatesPage(
			String siteExternalReferenceCode, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			filter, LayoutPageTemplateEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"types",
					new String[] {
						String.valueOf(
							LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE)
					});
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> _displayPageTemplateDTOConverter.toDTO(
				_layoutPageTemplateEntryService.fetchLayoutPageTemplateEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	protected DisplayPageTemplate doPostSiteDisplayPageTemplate(
			String siteExternalReferenceCode,
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			contextCompany.getCompanyId(), siteExternalReferenceCode);

		return _addDisplayPageTemplate(
			displayPageTemplate, groupId,
			_getLayoutPageTemplateCollectionId(displayPageTemplate, groupId));
	}

	@Override
	protected DisplayPageTemplate doPutSiteDisplayPageTemplate(
			String siteExternalReferenceCode,
			String displayPageTemplateExternalReferenceCode,
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			contextCompany.getCompanyId(), siteExternalReferenceCode);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplateExternalReferenceCode, groupId);

		if (layoutPageTemplateEntry == null) {
			return _addDisplayPageTemplate(
				displayPageTemplate, groupId,
				_getLayoutPageTemplateCollectionId(
					displayPageTemplate, groupId));
		}

		if (!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				layoutPageTemplateEntry.getType())) {

			throw new IllegalArgumentException(
				"The display page template type does not match the display " +
					"page type");
		}

		long layoutPageTemplateCollectionId =
			_getLayoutPageTemplateCollectionId(displayPageTemplate, groupId);

		if (!Objects.equals(
				layoutPageTemplateEntry.getLayoutPageTemplateCollectionId(),
				layoutPageTemplateCollectionId)) {

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.moveLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					layoutPageTemplateCollectionId);
		}

		ClassSubtypeReference contentTypeClassSubtypeReference =
			displayPageTemplate.getContentTypeReference();

		if ((contentTypeClassSubtypeReference == null) ||
			Validator.isNull(contentTypeClassSubtypeReference.getClassName())) {

			throw new IllegalArgumentException(
				"The content type reference and class name cannot be empty");
		}

		long classNameId = _getClassNameId(
			contentTypeClassSubtypeReference.getClassName());
		String classTypeKey = SubtypeUtil.getClassTypeKey(
			contentTypeClassSubtypeReference.getClassName(), groupId,
			contentTypeClassSubtypeReference.getSubTypeExternalReference());

		if ((classNameId != layoutPageTemplateEntry.getClassNameId()) ||
			!StringUtil.equals(
				classTypeKey, layoutPageTemplateEntry.getClassTypeKey())) {

			_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				classNameId, classTypeKey);
		}

		long previewFileEntryId = FileEntryUtil.getPreviewFileEntryId(
			groupId, LayoutAdminPortletKeys.GROUP_PAGES,
			displayPageTemplate.getThumbnailURLReference(),
			contextUser.getUserId());

		if (previewFileEntryId !=
				layoutPageTemplateEntry.getPreviewFileEntryId()) {

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					previewFileEntryId);
		}

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		UnicodeProperties typeSettingsUnicodeProperties = _getUnicodeProperties(
			displayPageTemplate.getDisplayPageTemplateSettings());

		layout = LayoutUtil.updateContentLayout(
			_cetManager, _fragmentEntryProcessorRegistry,
			_infoItemServiceRegistry, layout, layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(),
			_getRobotsMap(displayPageTemplate.getDisplayPageTemplateSettings()),
			LocalizedMapUtil.getLocalizedMap(
				displayPageTemplate.getFriendlyUrlPath_i18n()),
			typeSettingsUnicodeProperties,
			displayPageTemplate.getPageSpecifications(),
			_getServiceContext(displayPageTemplate, groupId));

		if (!layoutPageTemplateEntry.isApproved() &&
			LayoutUtil.isPublished(layout)) {

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.updateStatus(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					WorkflowConstants.STATUS_APPROVED);
		}

		if (!Objects.equals(
				GetterUtil.getBoolean(displayPageTemplate.getMarkedAsDefault()),
				layoutPageTemplateEntry.isDefaultTemplate())) {

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					GetterUtil.getBoolean(
						displayPageTemplate.getMarkedAsDefault()));
		}

		return _displayPageTemplateDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				contextUriInfo, contextUser),
			_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				displayPageTemplate.getName()));
	}

	@Override
	protected Long getPermissionCheckerResourceId(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode,
					getPermissionCheckerGroupId(groupExternalReferenceCode));

		return layoutPageTemplateEntry.getPrimaryKey();
	}

	@Override
	protected String getPermissionCheckerResourceName(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		return LayoutPageTemplateEntry.class.getName();
	}

	@Override
	protected void preparePatch(
		DisplayPageTemplate displayPageTemplate,
		DisplayPageTemplate existingDisplayPageTemplate) {

		if (displayPageTemplate.getContentTypeReference() != null) {
			existingDisplayPageTemplate.setContentTypeReference(
				displayPageTemplate::getContentTypeReference);
		}

		if (displayPageTemplate.getDisplayPageTemplateSettings() != null) {
			existingDisplayPageTemplate.setDisplayPageTemplateSettings(
				displayPageTemplate::getDisplayPageTemplateSettings);
		}

		if (displayPageTemplate.getFriendlyUrlPath_i18n() != null) {
			existingDisplayPageTemplate.setFriendlyUrlPath_i18n(
				displayPageTemplate::getFriendlyUrlPath_i18n);
		}

		if (displayPageTemplate.getPageSpecifications() != null) {
			existingDisplayPageTemplate.setPageSpecifications(
				displayPageTemplate::getPageSpecifications);
		}

		if (displayPageTemplate.getParentFolder() != null) {
			existingDisplayPageTemplate.setParentFolder(
				displayPageTemplate::getParentFolder);
		}

		if (displayPageTemplate.getThumbnailURLReference() != null) {
			existingDisplayPageTemplate.setThumbnailURLReference(
				displayPageTemplate::getThumbnailURLReference);
		}
	}

	private DisplayPageTemplate _addDisplayPageTemplate(
			DisplayPageTemplate displayPageTemplate, long groupId,
			long layoutPageTemplateCollectionId)
		throws Exception {

		ClassSubtypeReference contentTypeClassSubtypeReference =
			displayPageTemplate.getContentTypeReference();

		if ((contentTypeClassSubtypeReference == null) ||
			Validator.isNull(contentTypeClassSubtypeReference.getClassName())) {

			throw new IllegalArgumentException(
				"The content type reference and class name cannot be empty");
		}

		Map<Locale, String> nameMap = Collections.singletonMap(
			_portal.getSiteDefaultLocale(groupId),
			displayPageTemplate.getName());

		DisplayPageTemplateSettings displayPageTemplateSettings =
			displayPageTemplate.getDisplayPageTemplateSettings();

		ServiceContext serviceContext = _getServiceContext(
			displayPageTemplate, groupId);

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);
		serviceContext.setAttribute(
			"layout.page.template.entry.type",
			LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);

		Layout layout = LayoutUtil.addContentLayout(
			_cetManager, _fragmentEntryProcessorRegistry, groupId,
			_infoItemServiceRegistry,
			displayPageTemplate.getPageSpecifications(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, nameMap, null, null, null,
			_getRobotsMap(displayPageTemplateSettings),
			LayoutConstants.TYPE_ASSET_DISPLAY,
			_getUnicodeProperties(displayPageTemplateSettings), true, true,
			LocalizedMapUtil.getLocalizedMap(
				displayPageTemplate.getFriendlyUrlPath_i18n()),
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				displayPageTemplate.getExternalReferenceCode(), groupId,
				layoutPageTemplateCollectionId, displayPageTemplate.getKey(),
				_getClassNameId(
					contentTypeClassSubtypeReference.getClassName()),
				SubtypeUtil.getClassTypeKey(
					contentTypeClassSubtypeReference.getClassName(),
					layout.getGroupId(),
					contentTypeClassSubtypeReference.
						getSubTypeExternalReference()),
				displayPageTemplate.getName(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
				FileEntryUtil.getPreviewFileEntryId(
					groupId, LayoutAdminPortletKeys.GROUP_PAGES,
					displayPageTemplate.getThumbnailURLReference(),
					contextUser.getUserId()),
				GetterUtil.getBoolean(displayPageTemplate.getMarkedAsDefault()),
				0L, layout.getPlid(), 0L,
				PageSpecificationUtil.getPublishedStatus(
					displayPageTemplate.getPageSpecifications()),
				serviceContext);

		return _displayPageTemplateDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				contextUriInfo, contextUser),
			layoutPageTemplateEntry);
	}

	private long _getClassNameId(String contentTypeClassName) {
		ClassName className = _classNameLocalService.fetchClassName(
			contentTypeClassName);

		if ((className != null) && (className.getClassNameId() != 0)) {
			return className.getClassNameId();
		}

		LogUtil.logOptionalReference(contentTypeClassName);

		return _portal.getClassNameId(contentTypeClassName);
	}

	private long _getLayoutPageTemplateCollectionId(
			DisplayPageTemplate displayPageTemplate, long groupId)
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			displayPageTemplate.getParentFolder();

		if (displayPageTemplateFolder == null) {
			return LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT;
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					displayPageTemplateFolder.getExternalReferenceCode(),
					groupId);

		if (layoutPageTemplateCollection == null) {
			if (!LazyReferencingThreadLocal.isEnabled()) {
				throw new IllegalArgumentException(
					"The provided external reference code does not point to " +
						"a display page template folder");
			}

			layoutPageTemplateCollection =
				DisplayPageTemplateFolderUtil.addLayoutPageTemplateCollection(
					displayPageTemplateFolder, groupId,
					contextHttpServletRequest);
		}
		else if (!Objects.equals(
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					layoutPageTemplateCollection.getType())) {

			throw new IllegalArgumentException(
				"The display page template folder type does not match the " +
					"display page type");
		}

		return layoutPageTemplateCollection.getLayoutPageTemplateCollectionId();
	}

	private Map<Locale, String> _getRobotsMap(
		DisplayPageTemplateSettings displayPageTemplateSettings) {

		Map<Locale, String> robotsMap = new HashMap<>();

		if ((displayPageTemplateSettings != null) &&
			(displayPageTemplateSettings.getSeoSettings() != null)) {

			DisplayPageTemplateSEOSettings displayPageTemplateSEOSettings =
				displayPageTemplateSettings.getSeoSettings();

			robotsMap = LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(), null,
				displayPageTemplateSEOSettings.getRobots_i18n());
		}

		return robotsMap;
	}

	private ServiceContext _getServiceContext(
		DisplayPageTemplate displayPageTemplate, long groupId) {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, contextHttpServletRequest, null
		).build();

		serviceContext.setCompanyId(contextCompany.getCompanyId());
		serviceContext.setCreateDate(displayPageTemplate.getDateCreated());
		serviceContext.setModifiedDate(displayPageTemplate.getDateModified());
		serviceContext.setUserId(contextUser.getUserId());
		serviceContext.setUuid(displayPageTemplate.getUuid());

		return serviceContext;
	}

	private UnicodeProperties _getUnicodeProperties(
		DisplayPageTemplateSettings displayPageTemplateSettings) {

		UnicodeProperties unicodeProperties = new UnicodeProperties();

		if (displayPageTemplateSettings == null) {
			return unicodeProperties;
		}

		DisplayPageTemplateOpenGraphSettings
			displayPageTemplateOpenGraphSettings =
				displayPageTemplateSettings.getOpenGraphSettings();

		if (displayPageTemplateOpenGraphSettings != null) {
			unicodeProperties.setProperty(
				"mapped-openGraphDescription",
				displayPageTemplateOpenGraphSettings.getDescriptionTemplate());
			unicodeProperties.setProperty(
				"mapped-openGraphImageAlt",
				displayPageTemplateOpenGraphSettings.getImageAltTemplate());
			unicodeProperties.setProperty(
				"mapped-openGraphImage",
				displayPageTemplateOpenGraphSettings.getImageTemplate());
			unicodeProperties.setProperty(
				"mapped-openGraphTitle",
				displayPageTemplateOpenGraphSettings.getTitleTemplate());
		}

		SitemapSettings sitemapSettings = null;

		DisplayPageTemplateSEOSettings displayPageTemplateSEOSettings =
			displayPageTemplateSettings.getSeoSettings();

		if (displayPageTemplateSEOSettings != null) {
			sitemapSettings =
				displayPageTemplateSEOSettings.getSitemapSettings();

			unicodeProperties.setProperty(
				"mapped-description",
				displayPageTemplateSEOSettings.getDescriptionTemplate());
			unicodeProperties.setProperty(
				"mapped-title",
				displayPageTemplateSEOSettings.getHtmlTitleTemplate());
		}

		if (sitemapSettings != null) {
			SitemapSettings.ChangeFrequency changeFrequency =
				sitemapSettings.getChangeFrequency();

			if (changeFrequency != null) {
				unicodeProperties.setProperty(
					LayoutTypePortletConstants.SITEMAP_CHANGEFREQ,
					StringUtil.lowerCaseFirstLetter(
						changeFrequency.toString()));
			}

			Boolean include = sitemapSettings.getInclude();

			if (include != null) {
				String sitemapInclude = "0";

				if (include) {
					sitemapInclude = "1";
				}

				unicodeProperties.setProperty(
					LayoutTypePortletConstants.SITEMAP_INCLUDE, sitemapInclude);
			}

			unicodeProperties.setProperty(
				LayoutTypePortletConstants.SITEMAP_PRIORITY,
				String.valueOf(sitemapSettings.getPagePriority()));
		}

		return unicodeProperties;
	}

	private static final EntityModel _entityModel =
		new DisplayPageTemplateEntityModel();

	@Reference
	private CETManager _cetManager;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.DisplayPageTemplateDTOConverter)"
	)
	private DTOConverter<LayoutPageTemplateEntry, DisplayPageTemplate>
		_displayPageTemplateDTOConverter;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationDTOConverter)"
	)
	private DTOConverter<Layout, PageSpecification>
		_pageSpecificationDTOConverter;

	@Reference
	private Portal _portal;

}