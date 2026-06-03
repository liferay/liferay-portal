/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.headless.admin.site.dto.v1_0.PageSpecificationVersion;
import com.liferay.headless.admin.site.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.util.SitePageUtil;
import com.liferay.headless.admin.site.resource.v1_0.PageSpecificationVersionResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.service.LayoutContentVersionService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;

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
	public PageSpecificationVersion getSiteSitePagePageSpecificationVersion(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			String pageSpecificationVersionExternalReferenceCode)
		throws Exception {

		_getLayout(
			false, siteExternalReferenceCode, sitePageExternalReferenceCode);

		return _toPageSpecificationVersion(
			_getLayoutContentVersion(
				siteExternalReferenceCode,
				pageSpecificationVersionExternalReferenceCode));
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

		Layout layout = _getLayout(
			true, siteExternalReferenceCode, sitePageExternalReferenceCode);

		Layout draftLayout = layout.fetchDraftLayout();

		return Page.of(
			transform(
				_layoutContentVersionService.getLayoutContentVersions(
					draftLayout.getPlid()),
				this::_toPageSpecificationVersion));
	}

	private Layout _getLayout(
			boolean allowLiveGroup, String siteExternalReferenceCode,
			String sitePageExternalReferenceCode)
		throws Exception {

		Layout layout = SitePageUtil.getSitePageLayout(
			GroupUtil.getGroupId(
				false, allowLiveGroup, contextCompany.getCompanyId(),
				siteExternalReferenceCode),
			sitePageExternalReferenceCode);

		if (!layout.isTypeContent()) {
			throw new IllegalArgumentException(
				"The page must be a content page");
		}

		return layout;
	}

	private LayoutContentVersion _getLayoutContentVersion(
			String siteExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		return _layoutContentVersionService.
			getLayoutContentVersionByExternalReferenceCode(
				externalReferenceCode,
				GroupUtil.getStagingAwareGroupId(
					contextCompany.getCompanyId(), siteExternalReferenceCode));
	}

	private PageSpecificationVersion _toPageSpecificationVersion(
			LayoutContentVersion layoutContentVersion)
		throws Exception {

		return _pageSpecificationVersionDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutContentVersion.getLayoutContentVersionId(),
				contextUriInfo, contextUser),
			layoutContentVersion);
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private LayoutContentVersionService _layoutContentVersionService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationVersionDTOConverter)"
	)
	private DTOConverter<LayoutContentVersion, PageSpecificationVersion>
		_pageSpecificationVersionDTOConverter;

}