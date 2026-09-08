/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.internal.dto.v1_0.converter;

import com.liferay.commerce.product.constants.CPMeasurementUnitConstants;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.service.CPMeasurementUnitService;
import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.MeasurementUnit;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.product.model.CPMeasurementUnit"
	},
	service = DTOConverter.class
)
public class MeasurementUnitDTOConverter
	implements DTOConverter<CPMeasurementUnit, MeasurementUnit> {

	@Override
	public String getContentType() {
		return MeasurementUnit.class.getSimpleName();
	}

	@Override
	public MeasurementUnit toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPMeasurementUnit cpMeasurementUnit =
			_cpMeasurementUnitService.getCPMeasurementUnit(
				(Long)dtoConverterContext.getId());

		return new MeasurementUnit() {
			{
				setCompanyId(cpMeasurementUnit::getCompanyId);
				setExternalReferenceCode(
					cpMeasurementUnit::getExternalReferenceCode);
				setId(cpMeasurementUnit::getCPMeasurementUnitId);
				setKey(cpMeasurementUnit::getKey);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						cpMeasurementUnit.getNameMap()));
				setPrimary(cpMeasurementUnit::isPrimary);
				setPriority(cpMeasurementUnit::getPriority);
				setRate(cpMeasurementUnit::getRate);
				setType(
					() -> _language.get(
						dtoConverterContext.getLocale(),
						StringUtil.toLowerCase(
							CPMeasurementUnitConstants.typesMap.get(
								cpMeasurementUnit.getType()))));
			}
		};
	}

	@Reference
	private CPMeasurementUnitService _cpMeasurementUnitService;

	@Reference
	private Language _language;

}