/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecificationVersion;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecificationVersionPageExperience;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.service.LayoutContentVersionPreviewLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = DTOConverter.class)
public class PageSpecificationVersionDTOConverter
	implements DTOConverter<LayoutContentVersion, PageSpecificationVersion> {

	@Override
	public String getContentType() {
		return PageSpecificationVersion.class.getSimpleName();
	}

	@Override
	public PageSpecificationVersion toDTO(
		DTOConverterContext dtoConverterContext,
		LayoutContentVersion layoutContentVersion) {

		return new PageSpecificationVersion() {
			{
				setActions(
					() -> {
						if (dtoConverterContext == null) {
							return null;
						}

						return dtoConverterContext.getActions();
					});
				setCreator(
					() -> CreatorUtil.toCreator(
						layoutContentVersion.getUserId(),
						layoutContentVersion.getUserName()));
				setDateCreated(layoutContentVersion::getCreateDate);
				setDateModified(layoutContentVersion::getModifiedDate);
				setExternalReferenceCode(
					layoutContentVersion::getExternalReferenceCode);
				setName(
					() -> layoutContentVersion.getName(
						LocaleUtil.getSiteDefault()));
				setPageSpecificationVersionPageExperiences(
					() -> _toPageSpecificationVersionPageExperiences(
						dtoConverterContext, layoutContentVersion));
				setStatus(
					() -> {
						if (layoutContentVersion.getStatus() ==
								WorkflowConstants.STATUS_APPROVED) {

							return PageSpecificationVersion.Status.APPROVED;
						}

						return PageSpecificationVersion.Status.DRAFT;
					});
				setStatusDate(layoutContentVersion::getStatusDate);
				setVersion(layoutContentVersion::getVersion);
			}
		};
	}

	private PageSpecificationVersionPageExperience[]
			_toPageSpecificationVersionPageExperiences(
				DTOConverterContext dtoConverterContext,
				LayoutContentVersion layoutContentVersion)
		throws PortalException {

		ContentPageSpecification contentPageSpecification =
			ContentPageSpecification.toDTO(layoutContentVersion.getData());

		if ((contentPageSpecification == null) ||
			(contentPageSpecification.getPageExperiences() == null)) {

			return new PageSpecificationVersionPageExperience[0];
		}

		dtoConverterContext.setAttribute(
			"segmentsExperienceERCsLanguageIds",
			_layoutContentVersionPreviewLocalService.
				getSegmentsExperienceERCsLanguageIds(
					layoutContentVersion.getLayoutContentVersionId()));

		return TransformUtil.transform(
			contentPageSpecification.getPageExperiences(),
			pageExperience ->
				_pageSpecificationVersionPageExperienceDTOConverter.toDTO(
					dtoConverterContext, pageExperience),
			PageSpecificationVersionPageExperience.class);
	}

	@Reference
	private LayoutContentVersionPreviewLocalService
		_layoutContentVersionPreviewLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationVersionPageExperienceDTOConverter)"
	)
	private DTOConverter<PageExperience, PageSpecificationVersionPageExperience>
		_pageSpecificationVersionPageExperienceDTOConverter;

}