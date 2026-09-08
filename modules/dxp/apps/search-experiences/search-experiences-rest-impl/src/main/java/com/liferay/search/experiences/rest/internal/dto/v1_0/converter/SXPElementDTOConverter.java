/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.internal.dto.v1_0.converter;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.search.experiences.rest.dto.v1_0.SXPElement;
import com.liferay.search.experiences.rest.dto.v1_0.util.ElementDefinitionUtil;
import com.liferay.search.experiences.rest.internal.dto.v1_0.converter.util.SXPDTOConverterUtil;
import com.liferay.search.experiences.service.SXPElementLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	enabled = false,
	property = {
		"default=true",
		"dto.class.name=com.liferay.search.experiences.model.SXPElement"
	},
	service = DTOConverter.class
)
public class SXPElementDTOConverter
	implements DTOConverter
		<com.liferay.search.experiences.model.SXPElement, SXPElement> {

	@Override
	public String getContentType() {
		return SXPElement.class.getSimpleName();
	}

	@Override
	public SXPElement toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		com.liferay.search.experiences.model.SXPElement sxpElement =
			_sxpElementLocalService.getSXPElement(
				(Long)dtoConverterContext.getId());

		return toDTO(dtoConverterContext, sxpElement);
	}

	@Override
	public SXPElement toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.search.experiences.model.SXPElement sxpElement) {

		return new SXPElement() {
			{
				setCreateDate(sxpElement::getCreateDate);
				setDescription(
					() -> SXPDTOConverterUtil.translate(
						sxpElement.getFallbackDescription(), _language,
						dtoConverterContext.getLocale(),
						sxpElement.getDescriptionMap()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						true, sxpElement.getDescriptionMap()));
				setElementDefinition(
					() -> SXPDTOConverterUtil.translate(
						ElementDefinitionUtil.toElementDefinition(
							sxpElement.getElementDefinitionJSON()),
						_language, dtoConverterContext.getLocale()));
				setExternalReferenceCode(sxpElement::getExternalReferenceCode);
				setId(sxpElement::getSXPElementId);
				setModifiedDate(sxpElement::getModifiedDate);
				setReadOnly(sxpElement::isReadOnly);
				setSchemaVersion(sxpElement::getSchemaVersion);
				setTitle(
					() -> SXPDTOConverterUtil.translate(
						sxpElement.getFallbackTitle(), _language,
						dtoConverterContext.getLocale(),
						sxpElement.getTitleMap()));
				setTitle_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						true, sxpElement.getTitleMap()));
				setType(sxpElement::getType);
				setUserName(sxpElement::getUserName);
				setVersion(sxpElement::getVersion);
			}
		};
	}

	@Reference
	private Language _language;

	@Reference
	private SXPElementLocalService _sxpElementLocalService;

}