/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecificationVersion;
import com.liferay.headless.admin.site.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.site.internal.util.EnabledUtil;
import com.liferay.headless.admin.site.internal.util.SitePageUtil;
import com.liferay.headless.admin.site.resource.v1_0.PageSpecificationVersionResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.content.service.LayoutContentVersionService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.util.ActionUtil;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/page-specification-version.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = PageSpecificationVersionResource.class
)
public class PageSpecificationVersionResourceImpl
	extends BasePageSpecificationVersionResourceImpl {

	@Override
	public void deleteSiteSitePagePageSpecificationVersion(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			String pageSpecificationVersionExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkPageSpecificationVersionEnabled(contextCompany);

		Layout layout = _getLayout(
			siteExternalReferenceCode, sitePageExternalReferenceCode);

		LayoutContentVersion layoutContentVersion = _getLayoutContentVersion(
			pageSpecificationVersionExternalReferenceCode,
			layout.fetchDraftLayout(), siteExternalReferenceCode);

		_layoutContentVersionService.deleteLayoutContentVersion(
			layoutContentVersion.getLayoutContentVersionId());
	}

	@Override
	public PageSpecificationVersion getSiteSitePagePageSpecificationVersion(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			String pageSpecificationVersionExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkPageSpecificationVersionEnabled(contextCompany);

		Layout layout = _getLayout(
			siteExternalReferenceCode, sitePageExternalReferenceCode);

		Layout draftLayout = layout.fetchDraftLayout();

		return _toPageSpecificationVersion(
			_layoutContentVersionLocalService.
				getLatestApprovedLayoutContentVersionId(draftLayout.getPlid()),
			_getLayoutContentVersion(
				pageSpecificationVersionExternalReferenceCode, draftLayout,
				siteExternalReferenceCode),
			siteExternalReferenceCode, sitePageExternalReferenceCode);
	}

	@NestedField(
		parentClass = SitePage.class, value = "pageSpecificationVersions"
	)
	@Override
	public Page<PageSpecificationVersion>
			getSiteSitePagePageSpecificationVersionsPage(
				String siteExternalReferenceCode,
				@NestedFieldId(value = "externalReferenceCode") String
					sitePageExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkPageSpecificationVersionEnabled(contextCompany);

		Layout layout = SitePageUtil.getSitePageLayout(
			GroupUtil.getGroupId(
				false, true, contextCompany.getCompanyId(),
				siteExternalReferenceCode),
			sitePageExternalReferenceCode);

		if (!layout.isTypeContent()) {
			return Page.of(Collections.emptyList());
		}

		Layout draftLayout = layout.fetchDraftLayout();

		long latestApprovedLayoutContentVersionId =
			_layoutContentVersionLocalService.
				getLatestApprovedLayoutContentVersionId(draftLayout.getPlid());

		return Page.of(
			transform(
				_layoutContentVersionService.getLayoutContentVersions(
					draftLayout.getPlid()),
				layoutContentVersion -> _toPageSpecificationVersion(
					latestApprovedLayoutContentVersionId, layoutContentVersion,
					siteExternalReferenceCode, sitePageExternalReferenceCode)));
	}

	@Override
	public PageSpecification postSiteSitePagePageSpecificationVersionRestore(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			String pageSpecificationVersionExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkPageSpecificationVersionEnabled(contextCompany);

		Layout layout = _getLayout(
			siteExternalReferenceCode, sitePageExternalReferenceCode);

		Layout draftLayout = layout.fetchDraftLayout();

		LayoutContentVersion layoutContentVersion = _getLayoutContentVersion(
			pageSpecificationVersionExternalReferenceCode, draftLayout,
			siteExternalReferenceCode);

		draftLayout = LayoutUtil.updateLayout(
			_cetManager, _fragmentEntryProcessorRegistry,
			_infoItemServiceRegistry, draftLayout, layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			draftLayout.getKeywordsMap(), draftLayout.getRobotsMap(),
			draftLayout.getFriendlyURLMap(),
			ContentPageSpecification.unsafeToDTO(
				layoutContentVersion.getData()),
			WorkflowConstants.STATUS_DRAFT,
			ServiceContextUtil.createServiceContext(
				layout.getGroupId(), contextHttpServletRequest,
				contextUser.getUserId()));

		return _pageSpecificationDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest, draftLayout.getPlid(),
				contextUriInfo, contextUser),
			draftLayout);
	}

	private Map<String, String> _addAction(
		LayoutContentVersion layoutContentVersion, String methodName,
		Map<String, String> templateParameterMap) {

		return ActionUtil.addAction(
			ActionKeys.UPDATE, PageSpecificationVersionResourceImpl.class,
			layoutContentVersion.getPlid(), methodName, contextScopeChecker,
			_layoutModelResourcePermission, templateParameterMap,
			contextUriInfo);
	}

	private Map<String, Map<String, String>> _getActions(
		long latestApprovedLayoutContentVersionId,
		LayoutContentVersion layoutContentVersion,
		String siteExternalReferenceCode,
		String sitePageExternalReferenceCode) {

		Map<String, String> templateParameterMap = HashMapBuilder.put(
			"pageSpecificationVersionExternalReferenceCode",
			layoutContentVersion.getExternalReferenceCode()
		).put(
			"siteExternalReferenceCode", siteExternalReferenceCode
		).put(
			"sitePageExternalReferenceCode", sitePageExternalReferenceCode
		).build();

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			() -> {
				if ((layoutContentVersion.getStatus() ==
						WorkflowConstants.STATUS_APPROVED) &&
					(layoutContentVersion.getLayoutContentVersionId() ==
						latestApprovedLayoutContentVersionId)) {

					return null;
				}

				return _addAction(
					layoutContentVersion,
					"deleteSiteSitePagePageSpecificationVersion",
					templateParameterMap);
			}
		).put(
			"get",
			_addAction(
				layoutContentVersion, "getSiteSitePagePageSpecificationVersion",
				templateParameterMap)
		).put(
			"restore",
			_addAction(
				layoutContentVersion,
				"postSiteSitePagePageSpecificationVersionRestore",
				templateParameterMap)
		).build();
	}

	private Layout _getLayout(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode)
		throws Exception {

		Layout layout = SitePageUtil.getSitePageLayout(
			GroupUtil.getGroupId(
				false, false, contextCompany.getCompanyId(),
				siteExternalReferenceCode),
			sitePageExternalReferenceCode);

		if (!layout.isTypeContent()) {
			throw new IllegalArgumentException(
				"The page must be a content page");
		}

		return layout;
	}

	private LayoutContentVersion _getLayoutContentVersion(
			String externalReferenceCode, Layout layout,
			String siteExternalReferenceCode)
		throws Exception {

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionService.
				getLayoutContentVersionByExternalReferenceCode(
					externalReferenceCode,
					GroupUtil.getStagingAwareGroupId(
						contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (layoutContentVersion.getPlid() != layout.getPlid()) {
			throw new IllegalArgumentException(
				"The page specification version must belong to the site page");
		}

		return layoutContentVersion;
	}

	private PageSpecificationVersion _toPageSpecificationVersion(
			long latestApprovedLayoutContentVersionId,
			LayoutContentVersion layoutContentVersion,
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode)
		throws Exception {

		return _pageSpecificationVersionDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage,
				_getActions(
					latestApprovedLayoutContentVersionId, layoutContentVersion,
					siteExternalReferenceCode, sitePageExternalReferenceCode),
				Collections.emptyMap(), _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutContentVersion.getLayoutContentVersionId(),
				contextUriInfo, contextUser),
			layoutContentVersion);
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
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Reference
	private LayoutContentVersionService _layoutContentVersionService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Layout)"
	)
	private ModelResourcePermission<Layout> _layoutModelResourcePermission;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationDTOConverter)"
	)
	private DTOConverter<Layout, PageSpecification>
		_pageSpecificationDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationVersionDTOConverter)"
	)
	private DTOConverter<LayoutContentVersion, PageSpecificationVersion>
		_pageSpecificationVersionDTOConverter;

}