/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.MasterPage;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecificationVersion;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.Settings;
import com.liferay.headless.admin.site.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.dto.v1_0.UtilityPage;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageSpecification;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.SettingsUtil;
import com.liferay.headless.admin.site.internal.util.EnabledUtil;
import com.liferay.headless.admin.site.resource.v1_0.PageSpecificationResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.service.LayoutContentVersionService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/page-specification.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = PageSpecificationResource.class
)
public class PageSpecificationResourceImpl
	extends BasePageSpecificationResourceImpl {

	@Override
	@Tags({@Tag(description = "[DEV]", name = "PageSpecification")})
	public void deleteSitePageSpecification(
			String siteExternalReferenceCode,
			String pageSpecificationExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-74328")) {

			throw new UnsupportedOperationException();
		}

		Layout layout = _getLayout(
			GroupUtil.getGroupId(
				true, contextCompany.getCompanyId(), siteExternalReferenceCode),
			pageSpecificationExternalReferenceCode);

		if (!layout.isDraftLayout() ||
			(layout.isApproved() &&
			 GetterUtil.getBoolean(
				 layout.getTypeSettingsProperty(
					 LayoutTypeSettingsConstants.KEY_PUBLISHED)))) {

			throw new IllegalArgumentException("The page status is not valid");
		}

		_discardDraftLayout(layout);
	}

	@NestedField(
		parentClass = DisplayPageTemplate.class, value = "pageSpecifications"
	)
	@Override
	public Page<PageSpecification>
			getSiteDisplayPageTemplatePageSpecificationsPage(
				String siteExternalReferenceCode,
				@NestedFieldId(value = "externalReferenceCode") String
					displayPageTemplateExternalReferenceCode)
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
				"The page must be a display page");
		}

		return Page.of(
			_toPageSpecifications(
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid())));
	}

	@NestedField(parentClass = MasterPage.class, value = "pageSpecifications")
	@Override
	public Page<PageSpecification> getSiteMasterPagePageSpecificationsPage(
			String siteExternalReferenceCode,
			@NestedFieldId(value = "externalReferenceCode") String
				masterPageExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					masterPageExternalReferenceCode,
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				layoutPageTemplateEntry.getType())) {

			throw new IllegalArgumentException(
				"The page must be a master page");
		}

		return Page.of(
			_toPageSpecifications(
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid())));
	}

	@Override
	public PageSpecification getSitePageSpecification(
			String siteExternalReferenceCode,
			String pageSpecificationExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		Layout layout = _getLayout(
			GroupUtil.getGroupId(
				true, true, contextCompany.getCompanyId(),
				siteExternalReferenceCode),
			pageSpecificationExternalReferenceCode);

		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent() &&
			!layout.isTypePortlet()) {

			throw new IllegalArgumentException(
				"Only content, portlet, or asset display pages are supported");
		}

		return _pageSpecificationDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest, layout.getPlid(), contextUriInfo,
				contextUser),
			layout);
	}

	@NestedField(parentClass = PageTemplate.class, value = "pageSpecifications")
	@Override
	public Page<PageSpecification> getSitePageTemplatePageSpecificationsPage(
			String siteExternalReferenceCode,
			@NestedFieldId(value = "externalReferenceCode") String
				pageTemplateExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode,
					GroupUtil.getGroupId(
						true, true, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.BASIC,
				layoutPageTemplateEntry.getType()) &&
			!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
				layoutPageTemplateEntry.getType())) {

			throw new IllegalArgumentException(
				"The page template type must be either basic or widget");
		}

		return Page.of(
			_toPageSpecifications(
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid())));
	}

	@NestedField(parentClass = SitePage.class, value = "pageSpecifications")
	@Override
	public Page<PageSpecification> getSiteSitePagePageSpecificationsPage(
			String siteExternalReferenceCode,
			@NestedFieldId(value = "externalReferenceCode") String
				sitePageExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		Layout layout = _layoutService.getLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode,
			GroupUtil.getGroupId(
				true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		if (layout.isDraftLayout() || layout.isTypeAssetDisplay() ||
			layout.isTypeUtility()) {

			throw new IllegalArgumentException(
				"This page type cannot be modified through this endpoint");
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry != null) {
			throw new IllegalArgumentException(
				"The provided page external reference code belongs to a page " +
					"template and cannot be used");
		}

		return Page.of(_toPageSpecifications(layout));
	}

	@NestedField(
		parentClass = PageSpecificationVersion.class,
		value = "pageSpecification"
	)
	@Override
	public PageSpecification
			getSiteSitePagePageSpecificationVersionPageSpecification(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode,
				@NestedFieldId(value = "externalReferenceCode") String
					pageSpecificationVersionExternalReferenceCode)
		throws Exception {

		FeatureFlagManagerUtil.checkEnabled(
			contextCompany.getCompanyId(), "LPD-10622");

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionService.
				getLayoutContentVersionByExternalReferenceCode(
					pageSpecificationVersionExternalReferenceCode,
					GroupUtil.getStagingAwareGroupId(
						contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		return PageSpecification.unsafeToDTO(layoutContentVersion.getData());
	}

	@NestedField(parentClass = UtilityPage.class, value = "pageSpecifications")
	@Override
	public Page<PageSpecification> getSiteUtilityPagePageSpecificationsPage(
			String siteExternalReferenceCode,
			@NestedFieldId(value = "externalReferenceCode") String
				utilityPageExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					utilityPageExternalReferenceCode,
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		return Page.of(
			_toPageSpecifications(
				_layoutLocalService.getLayout(
					layoutUtilityPageEntry.getPlid())));
	}

	@Override
	public PageSpecification putSitePageSpecification(
			String siteExternalReferenceCode,
			String pageSpecificationExternalReferenceCode,
			PageSpecification pageSpecification)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-74328")) {

			throw new UnsupportedOperationException();
		}

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		Layout layout = _getLayout(
			groupId, pageSpecificationExternalReferenceCode);

		ServiceContext serviceContext = ServiceContextUtil.createServiceContext(
			groupId, contextHttpServletRequest, contextUser.getUserId());

		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent()) {
			if (!Objects.equals(
					pageSpecification.getStatus(),
					PageSpecification.Status.APPROVED) ||
				!Objects.equals(
					PageSpecification.Type.WIDGET_PAGE_SPECIFICATION,
					pageSpecification.getType())) {

				throw new IllegalArgumentException(
					"The page specification must be widget and be in " +
						"approved status");
			}

			return _pageSpecificationDTOConverter.toDTO(
				DTOConverterContextUtil.getDTOConverterContext(
					contextAcceptLanguage, _dtoConverterRegistry,
					contextHttpServletRequest, layout.getPlid(), contextUriInfo,
					contextUser),
				LayoutUtil.updateLayout(
					_cetManager, _fragmentEntryProcessorRegistry,
					_infoItemServiceRegistry, layout, layout.getNameMap(),
					layout.getTitleMap(), layout.getDescriptionMap(),
					layout.getKeywordsMap(), layout.getRobotsMap(),
					layout.getFriendlyURLMap(), pageSpecification,
					layout.getStatus(), serviceContext));
		}

		if (!Objects.equals(
				PageSpecification.Type.CONTENT_PAGE_SPECIFICATION,
				pageSpecification.getType()) ||
			!layout.isDraftLayout() ||
			!Objects.equals(
				pageSpecification.getStatus(),
				PageSpecification.Status.DRAFT)) {

			throw new IllegalArgumentException(
				"The page specification must be in draft status for content " +
					"pages");
		}

		return _pageSpecificationDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest, layout.getPlid(), contextUriInfo,
				contextUser),
			LayoutUtil.updateLayout(
				_cetManager, _fragmentEntryProcessorRegistry,
				_infoItemServiceRegistry, layout, layout.getNameMap(),
				layout.getTitleMap(), layout.getDescriptionMap(),
				layout.getKeywordsMap(), layout.getRobotsMap(),
				layout.getFriendlyURLMap(), pageSpecification,
				WorkflowConstants.STATUS_DRAFT, serviceContext));
	}

	@Override
	protected void preparePatch(
		PageSpecification pageSpecification,
		PageSpecification existingPageSpecification) {

		Settings settings = SettingsUtil.getSettings(pageSpecification);

		if (settings != null) {
			if (pageSpecification instanceof ContentPageSpecification) {
				ContentPageSpecification existingContentPageSpecification =
					(ContentPageSpecification)existingPageSpecification;

				existingContentPageSpecification.setSettings(() -> settings);
			}

			if (pageSpecification instanceof WidgetPageSpecification) {
				WidgetPageSpecification existingWidgetPageSpecification =
					(WidgetPageSpecification)existingPageSpecification;

				existingWidgetPageSpecification.setSettings(() -> settings);
			}
		}

		if (!Objects.equals(
				PageSpecification.Type.CONTENT_PAGE_SPECIFICATION,
				existingPageSpecification.getType())) {

			return;
		}

		_preparePatch(
			(ContentPageSpecification)pageSpecification,
			(ContentPageSpecification)existingPageSpecification);
	}

	private void _discardDraftLayout(Layout draftLayout) throws Exception {
		Layout layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		try {
			boolean published = LayoutUtil.isPublished(layout);

			draftLayout = _layoutLocalService.copyLayoutContent(
				layout, draftLayout);

			ServiceContext serviceContext = ServiceContextBuilder.create(
				layout.getGroupId(), contextHttpServletRequest, null
			).build();

			serviceContext.setAttribute(
				LayoutTypeSettingsConstants.KEY_PUBLISHED, published);
			serviceContext.setUserId(contextUser.getUserId());

			_layoutLocalService.updateStatus(
				contextUser.getUserId(), draftLayout.getPlid(),
				WorkflowConstants.STATUS_APPROVED, serviceContext);
		}
		catch (Exception exception) {
			if (!(exception instanceof LockedLayoutException) &&
				!(exception.getCause() instanceof LockedLayoutException)) {

				throw new IllegalArgumentException(
					"The page status is not valid");
			}
		}
	}

	private Layout _getLayout(
			long groupId, String pageSpecificationExternalReferenceCode)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					pageSpecificationExternalReferenceCode, groupId);

		if ((layoutPageTemplateEntry != null) &&
			(layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			return _layoutLocalService.getLayout(
				layoutPageTemplateEntry.getPlid());
		}

		return _layoutService.getLayoutByExternalReferenceCode(
			pageSpecificationExternalReferenceCode, groupId);
	}

	private void _preparePatch(
		ContentPageSpecification contentPageSpecification,
		ContentPageSpecification existingContentPageSpecification) {

		if (contentPageSpecification.getPageExperiences() != null) {
			existingContentPageSpecification.setPageExperiences(
				contentPageSpecification::getPageExperiences);
		}
	}

	private List<PageSpecification> _toPageSpecifications(Layout layout)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			if (!layout.isTypePortlet() &&
				!Objects.equals(
					layout.getType(), LayoutConstants.TYPE_EMBEDDED) &&
				!Objects.equals(
					layout.getType(), LayoutConstants.TYPE_LINK_TO_LAYOUT) &&
				!Objects.equals(layout.getType(), LayoutConstants.TYPE_NODE) &&
				!Objects.equals(layout.getType(), LayoutConstants.TYPE_URL)) {

				throw new IllegalArgumentException(
					"Content pages must have a draft layout");
			}

			return ListUtil.fromArray(
				_pageSpecificationDTOConverter.toDTO(
					DTOConverterContextUtil.getDTOConverterContext(
						contextAcceptLanguage, _dtoConverterRegistry,
						contextHttpServletRequest, layout.getPlid(),
						contextUriInfo, contextUser),
					layout));
		}

		return ListUtil.fromArray(
			_pageSpecificationDTOConverter.toDTO(
				DTOConverterContextUtil.getDTOConverterContext(
					contextAcceptLanguage, _dtoConverterRegistry,
					contextHttpServletRequest, layout.getPlid(), contextUriInfo,
					contextUser),
				layout),
			_pageSpecificationDTOConverter.toDTO(
				DTOConverterContextUtil.getDTOConverterContext(
					contextAcceptLanguage, _dtoConverterRegistry,
					contextHttpServletRequest, draftLayout.getPlid(),
					contextUriInfo, contextUser),
				draftLayout));
	}

	@Reference
	private CETManager _cetManager;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutContentVersionService _layoutContentVersionService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationDTOConverter)"
	)
	private DTOConverter<Layout, PageSpecification>
		_pageSpecificationDTOConverter;

}