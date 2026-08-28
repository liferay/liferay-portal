/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecificationVersionPageExperience;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = DTOConverter.class)
public class PageSpecificationVersionPageExperienceDTOConverter
	implements DTOConverter
		<PageExperience, PageSpecificationVersionPageExperience> {

	@Override
	public String getContentType() {
		return PageSpecificationVersionPageExperience.class.getSimpleName();
	}

	@Override
	public PageSpecificationVersionPageExperience toDTO(
		DTOConverterContext dtoConverterContext,
		PageExperience pageExperience) {

		Map<String, List<String>> segmentsExperienceERCsLanguageIds =
			(Map<String, List<String>>)dtoConverterContext.getAttribute(
				"segmentsExperienceERCsLanguageIds");

		return new PageSpecificationVersionPageExperience() {
			{
				setAvailablePreviewLanguageIds(
					() -> {
						List<String> languageIds =
							segmentsExperienceERCsLanguageIds.getOrDefault(
								pageExperience.getExternalReferenceCode(),
								Collections.emptyList());

						return languageIds.toArray(new String[0]);
					});
				setExternalReferenceCode(
					pageExperience::getExternalReferenceCode);
				setName_i18n(pageExperience::getName_i18n);
				setPriority(pageExperience::getPriority);
			}
		};
	}

}