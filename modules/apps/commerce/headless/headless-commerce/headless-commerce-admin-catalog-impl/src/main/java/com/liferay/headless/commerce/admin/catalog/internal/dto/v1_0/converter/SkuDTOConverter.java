/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuOption;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuUnitOfMeasure;
import com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.TransformUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.product.model.CPInstance"
	},
	service = DTOConverter.class
)
public class SkuDTOConverter implements DTOConverter<CPInstance, Sku> {

	@Override
	public String getContentType() {
		return Sku.class.getSimpleName();
	}

	@Override
	public Sku toDTO(DTOConverterContext dtoConverterContext) throws Exception {
		CPInstance cpInstance = _cpInstanceService.getCPInstance(
			(Long)dtoConverterContext.getId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();
		CPInstance replacementCPInstance =
			_cpInstanceService.fetchCProductInstance(
				cpInstance.getReplacementCProductId(),
				cpInstance.getReplacementCPInstanceUuid());

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			(CPInstanceUnitOfMeasure)dtoConverterContext.getAttribute(
				"cpInstanceUnitOfMeasure");

		return new Sku() {
			{
				setCost(cpInstance::getCost);
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CPInstance.class.getName(),
						cpInstance.getCPInstanceId(), cpInstance.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDepth(cpInstance::getDepth);
				setDiscontinued(cpInstance::isDiscontinued);
				setDiscontinuedDate(cpInstance::getDiscontinuedDate);
				setDisplayDate(cpInstance::getDisplayDate);
				setExpirationDate(cpInstance::getExpirationDate);
				setExternalReferenceCode(cpInstance::getExternalReferenceCode);
				setGtin(cpInstance::getGtin);
				setHeight(cpInstance::getHeight);
				setId(cpInstance::getCPInstanceId);
				setManufacturerPartNumber(
					cpInstance::getManufacturerPartNumber);
				setPrice(cpInstance::getPrice);
				setProductId(cpDefinition::getCProductId);
				setProductName(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinition.getNameMap()));
				setPromoPrice(cpInstance::getPromoPrice);
				setPublished(cpInstance::isPublished);
				setPurchasable(cpInstance::isPurchasable);
				setReplacementSkuExternalReferenceCode(
					() -> {
						if (replacementCPInstance == null) {
							return null;
						}

						return replacementCPInstance.getExternalReferenceCode();
					});
				setReplacementSkuId(
					() -> {
						if (replacementCPInstance != null) {
							return replacementCPInstance.getCPInstanceId();
						}

						return null;
					});
				setSku(cpInstance::getSku);
				setSkuOptions(
					() -> TransformUtil.transformToArray(
						_cpInstanceHelper.
							getCPInstanceCPInstanceOptionValueRels(
								cpInstance.getCPInstanceId()),
						cpInstanceOptionValueRel -> {
							CPDefinitionOptionRel cpDefinitionOptionRel =
								_cpDefinitionOptionRelLocalService.
									fetchCPDefinitionOptionRel(
										cpInstanceOptionValueRel.
											getCPDefinitionOptionRelId());

							if (cpDefinitionOptionRel == null) {
								return null;
							}

							CPDefinitionOptionValueRel
								cpDefinitionOptionValueRel =
									_cpDefinitionOptionValueRelLocalService.
										fetchCPDefinitionOptionValueRel(
											cpInstanceOptionValueRel.
												getCPDefinitionOptionValueRelId());

							if (cpDefinitionOptionValueRel == null) {
								return null;
							}

							return new SkuOption() {
								{
									setKey(cpDefinitionOptionRel::getKey);
									setOptionId(
										cpDefinitionOptionRel::
											getCPDefinitionOptionRelId);
									setOptionValueId(
										cpDefinitionOptionValueRel::
											getCPDefinitionOptionValueRelId);
									setValue(
										cpDefinitionOptionValueRel::getKey);
								}
							};
						},
						SkuOption.class));
				setSkuUnitOfMeasures(
					() -> _toSkuUnitOfMeasures(
						cpInstance, dtoConverterContext));
				setUnitOfMeasureKey(
					() -> {
						if (cpInstanceUnitOfMeasure != null) {
							return cpInstanceUnitOfMeasure.getKey();
						}

						return null;
					});
				setUnitOfMeasureName(
					() -> {
						if (cpInstanceUnitOfMeasure == null) {
							return null;
						}

						return LanguageUtils.getLanguageIdMap(
							cpInstanceUnitOfMeasure.getNameMap());
					});
				setUnitOfMeasureSkuId(
					() -> {
						if (cpInstanceUnitOfMeasure != null) {
							return StringBundler.concat(
								cpInstance.getCPInstanceId(), StringPool.DASH,
								cpInstanceUnitOfMeasure.
									getCPInstanceUnitOfMeasureId());
						}

						return String.valueOf(cpInstance.getCPInstanceId());
					});
				setUnspsc(cpInstance::getUnspsc);
				setWeight(cpInstance::getWeight);
				setWidth(cpInstance::getWidth);
			}
		};
	}

	private SkuUnitOfMeasure[] _toSkuUnitOfMeasures(
		CPInstance cpInstance, DTOConverterContext dtoConverterContext) {

		return TransformUtil.transformToArray(
			cpInstance.getCPInstanceUnitOfMeasures(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			cpInstanceUnitOfMeasure -> {
				DefaultDTOConverterContext defaultDTOConverterContext =
					new DefaultDTOConverterContext(
						cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId(),
						dtoConverterContext.getLocale());

				defaultDTOConverterContext.setAttribute(
					"id", cpInstance.getCPInstanceId());

				return _skuUnitOfMeasureDTOConverter.toDTO(
					defaultDTOConverterContext);
			},
			SkuUnitOfMeasure.class);
	}

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference(target = DTOConverterConstants.SKU_UNIT_OF_MEASURE_DTO_CONVERTER)
	private DTOConverter<CPInstanceUnitOfMeasure, SkuUnitOfMeasure>
		_skuUnitOfMeasureDTOConverter;

}