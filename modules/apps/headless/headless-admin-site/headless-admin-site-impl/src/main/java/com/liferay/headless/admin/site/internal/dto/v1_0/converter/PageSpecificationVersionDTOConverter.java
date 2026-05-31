/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.PageSpecificationVersion;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

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
				setDateCreated(layoutContentVersion::getCreateDate);
				setDateModified(layoutContentVersion::getModifiedDate);
				setExternalReferenceCode(
					layoutContentVersion::getExternalReferenceCode);
				setName(
					() -> layoutContentVersion.getName(
						LocaleUtil.getSiteDefault()));
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

}