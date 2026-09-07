/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry"
	},
	service = DTOConverter.class
)
public class MappedProductDTOConverter
	implements DTOConverter<CSDiagramEntry, MappedProduct> {

	@Override
	public String getContentType() {
		return MappedProduct.class.getSimpleName();
	}

	@Override
	public MappedProduct toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CSDiagramEntry csDiagramEntry =
			_csDiagramEntryService.getCSDiagramEntry(
				(Long)dtoConverterContext.getId());

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(
				csDiagramEntry.getCProductId(), false);

		return new MappedProduct() {
			{
				setActions(dtoConverterContext::getActions);
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CSDiagramEntry.class.getName(),
						csDiagramEntry.getCSDiagramEntryId(),
						csDiagramEntry.getCompanyId(),
						dtoConverterContext.getLocale()));
				setId(csDiagramEntry::getCSDiagramEntryId);
				setProductExternalReferenceCode(
					() -> {
						if (cpDefinition == null) {
							return StringPool.BLANK;
						}

						CProduct cProduct = cpDefinition.getCProduct();

						return cProduct.getExternalReferenceCode();
					});
				setProductId(csDiagramEntry::getCProductId);
				setProductName(
					() -> {
						if (cpDefinition == null) {
							return null;
						}

						return LanguageUtils.getLanguageIdMap(
							cpDefinition.getNameMap());
					});
				setQuantity(csDiagramEntry::getQuantity);
				setSequence(csDiagramEntry::getSequence);
				setSku(csDiagramEntry::getSku);
				setSkuExternalReferenceCode(
					() -> {
						CPInstance cpInstance =
							_cpInstanceService.fetchCPInstance(
								GetterUtil.getLong(
									csDiagramEntry.getCPInstanceId()));

						if (cpInstance == null) {
							return StringPool.BLANK;
						}

						return cpInstance.getExternalReferenceCode();
					});
				setSkuId(
					() -> GetterUtil.getLong(csDiagramEntry.getCPInstanceId()));
				setType(
					() -> {
						if (csDiagramEntry.isDiagram()) {
							return MappedProduct.Type.create(
								Type.DIAGRAM.getValue());
						}

						if (csDiagramEntry.getCPInstanceId() > 0) {
							return MappedProduct.Type.create(
								Type.SKU.getValue());
						}

						return MappedProduct.Type.create(
							Type.EXTERNAL.getValue());
					});
			}
		};
	}

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference
	private CSDiagramEntryService _csDiagramEntryService;

}