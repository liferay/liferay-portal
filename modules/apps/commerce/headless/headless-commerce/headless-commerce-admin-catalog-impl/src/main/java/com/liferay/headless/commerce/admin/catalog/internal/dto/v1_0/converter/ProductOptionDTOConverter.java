/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.product.model.CPDefinitionOptionRel",
	service = DTOConverter.class
)
public class ProductOptionDTOConverter
	implements DTOConverter<CPDefinitionOptionRel, ProductOption> {

	@Override
	public String getContentType() {
		return ProductOption.class.getSimpleName();
	}

	@Override
	public ProductOption toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelService.getCPDefinitionOptionRel(
				(Long)dtoConverterContext.getId());

		CPOption cpOption = _cpOptionLocalService.fetchCPOption(
			cpDefinitionOptionRel.getCPOptionId());

		return new ProductOption() {
			{
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CPDefinitionOptionRel.class.getName(),
						cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
						cpDefinitionOptionRel.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinitionOptionRel.getDescriptionMap()));
				setExternalReferenceCode(
					cpDefinitionOptionRel::getExternalReferenceCode);
				setFacetable(cpDefinitionOptionRel::isFacetable);
				setFieldType(cpDefinitionOptionRel::getCommerceOptionTypeKey);
				setId(cpDefinitionOptionRel::getCPDefinitionOptionRelId);
				setKey(cpDefinitionOptionRel::getKey);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinitionOptionRel.getNameMap()));
				setOptionExternalReferenceCode(
					() -> {
						if (cpOption == null) {
							return null;
						}

						return cpOption.getExternalReferenceCode();
					});
				setOptionId(
					() -> {
						if (cpOption == null) {
							return null;
						}

						return cpOption.getCPOptionId();
					});
				setPriceType(cpDefinitionOptionRel::getPriceType);
				setProductOptionValues(
					() -> {
						if (!GetterUtil.getBoolean(
								dtoConverterContext.getAttribute(
									"showProductOptionValues"))) {

							return null;
						}

						return _toProductOptionValues(
							cpDefinitionOptionRel, dtoConverterContext);
					});
				setRequired(cpDefinitionOptionRel::isRequired);
				setSkuContributor(cpDefinitionOptionRel::isSkuContributor);
				setTypeSettings(cpDefinitionOptionRel::getTypeSettings);
			}
		};
	}

	private ProductOptionValue[] _toProductOptionValues(
			CPDefinitionOptionRel cpDefinitionOptionRel,
			DTOConverterContext dtoConverterContext)
		throws Exception {

		return TransformUtil.transformToArray(
			_cpDefinitionOptionValueRelService.getCPDefinitionOptionValueRels(
				cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			cpDefinitionOptionValueRel -> _productOptionValueDTOConverter.toDTO(
				new DefaultDTOConverterContext(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId(),
					dtoConverterContext.getLocale()),
				cpDefinitionOptionValueRel),
			ProductOptionValue.class);
	}

	@Reference
	private CPDefinitionOptionRelService _cpDefinitionOptionRelService;

	@Reference
	private CPDefinitionOptionValueRelService
		_cpDefinitionOptionValueRelService;

	@Reference
	private CPOptionLocalService _cpOptionLocalService;

	@Reference(
		target = DTOConverterConstants.PRODUCT_OPTION_VALUE_DTO_CONVERTER
	)
	private DTOConverter<CPDefinitionOptionValueRel, ProductOptionValue>
		_productOptionValueDTOConverter;

}