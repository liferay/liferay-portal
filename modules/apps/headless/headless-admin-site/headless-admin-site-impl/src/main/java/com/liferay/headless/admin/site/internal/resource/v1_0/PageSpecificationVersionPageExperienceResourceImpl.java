/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecificationVersion;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecificationVersionPageExperience;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.PageSpecificationVersionUtil;
import com.liferay.headless.admin.site.internal.util.EnabledUtil;
import com.liferay.headless.admin.site.resource.v1_0.PageSpecificationVersionPageExperienceResource;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.service.LayoutContentVersionPreviewLocalService;
import com.liferay.layout.content.service.LayoutContentVersionService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.Arrays;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/page-specification-version-page-experience.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = PageSpecificationVersionPageExperienceResource.class
)
public class PageSpecificationVersionPageExperienceResourceImpl
	extends BasePageSpecificationVersionPageExperienceResourceImpl {

	@NestedField(
		parentClass = PageSpecificationVersion.class,
		value = "pageSpecificationVersionPageExperiences"
	)
	@Override
	public Page<PageSpecificationVersionPageExperience>
			getSiteSitePagePageSpecificationVersionPageSpecificationVersionPageExperiencesPage(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode,
				@NestedFieldId(value = "externalReferenceCode") String
					pageSpecificationVersionExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkPageSpecificationVersionEnabled(contextCompany);

		Layout layout = PageSpecificationVersionUtil.getLayout(
			contextCompany.getCompanyId(), siteExternalReferenceCode,
			sitePageExternalReferenceCode);

		LayoutContentVersion layoutContentVersion =
			PageSpecificationVersionUtil.getLayoutContentVersion(
				contextCompany.getCompanyId(),
				pageSpecificationVersionExternalReferenceCode,
				layout.fetchDraftLayout(), _layoutContentVersionService,
				siteExternalReferenceCode);

		ContentPageSpecification contentPageSpecification =
			ContentPageSpecification.toDTO(layoutContentVersion.getData());

		if ((contentPageSpecification == null) ||
			(contentPageSpecification.getPageExperiences() == null)) {

			return Page.of(Collections.emptyList());
		}

		long layoutContentVersionId =
			layoutContentVersion.getLayoutContentVersionId();

		DTOConverterContext dtoConverterContext =
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage,
				HashMapBuilder.<String, Object>put(
					"segmentsExperienceERCsLanguageIds",
					_layoutContentVersionPreviewLocalService.
						getSegmentsExperienceERCsLanguageIds(
							layoutContentVersionId)
				).build(),
				_dtoConverterRegistry, contextHttpServletRequest,
				layoutContentVersionId, contextUriInfo, contextUser);

		return Page.of(
			transform(
				Arrays.asList(contentPageSpecification.getPageExperiences()),
				pageExperience ->
					_pageSpecificationVersionPageExperienceDTOConverter.toDTO(
						dtoConverterContext, pageExperience)));
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private LayoutContentVersionPreviewLocalService
		_layoutContentVersionPreviewLocalService;

	@Reference
	private LayoutContentVersionService _layoutContentVersionService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationVersionPageExperienceDTOConverter)"
	)
	private DTOConverter<PageExperience, PageSpecificationVersionPageExperience>
		_pageSpecificationVersionPageExperienceDTOConverter;

}