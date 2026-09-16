/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alessio Antonio Rendina
 */
public class MappedProductUtil {

	public static CSDiagramEntry addCSDiagramEntry(
			long companyId, long cpDefinitionId,
			CPDefinitionService cpDefinitionService,
			CPInstanceService cpInstanceService,
			CSDiagramEntryService csDiagramEntryService, long groupId,
			Locale locale, MappedProduct mappedProduct,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		String skuExternalReferenceCode =
			mappedProduct.getSkuExternalReferenceCode();

		long skuId = 0;

		if (Validator.isNull(skuExternalReferenceCode)) {
			skuId = GetterUtil.getLong(mappedProduct.getSkuId());
		}
		else {
			CPInstance cpInstance =
				cpInstanceService.fetchCPInstanceByExternalReferenceCode(
					skuExternalReferenceCode, companyId);

			if (cpInstance != null) {
				skuId = cpInstance.getCPInstanceId();
			}
		}

		long cProductId = getCProductId(
			companyId, cpDefinitionService, groupId, mappedProduct);

		ServiceContext serviceContext = serviceContextHelper.getServiceContext(
			groupId);

		serviceContext.setExpandoBridgeAttributes(
			getExpandoBridgeAttributes(companyId, locale, mappedProduct));

		return csDiagramEntryService.addCSDiagramEntry(
			cpDefinitionId, skuId, cProductId, isDiagram(null, mappedProduct),
			GetterUtil.getInteger(mappedProduct.getQuantity()),
			GetterUtil.getString(mappedProduct.getSequence()),
			GetterUtil.getString(mappedProduct.getSku()), serviceContext);
	}

	public static CSDiagramEntry addOrUpdateCSDiagramEntry(
			long companyId, long cpDefinitionId,
			CPDefinitionService cpDefinitionService,
			CPInstanceService cpInstanceService,
			CSDiagramEntryService csDiagramEntryService, long groupId,
			Locale locale, MappedProduct mappedProduct,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		CSDiagramEntry csDiagramEntry =
			csDiagramEntryService.fetchCSDiagramEntry(
				cpDefinitionId, mappedProduct.getSequence());

		if (csDiagramEntry == null) {
			return addCSDiagramEntry(
				companyId, cpDefinitionId, cpDefinitionService,
				cpInstanceService, csDiagramEntryService, groupId, locale,
				mappedProduct, serviceContextHelper);
		}

		return updateCSDiagramEntry(
			companyId, csDiagramEntry, csDiagramEntryService, groupId, locale,
			mappedProduct, serviceContextHelper);
	}

	public static long getCProductId(
			long companyId, CPDefinitionService cpDefinitionService,
			long groupId, MappedProduct mappedProduct)
		throws PortalException {

		String productExternalReferenceCode =
			mappedProduct.getProductExternalReferenceCode();

		if (Validator.isNull(productExternalReferenceCode)) {
			return GetterUtil.getLong(mappedProduct.getProductId());
		}

		CPDefinition cpDefinition =
			ProductUtil.getCPDefinitionByCProductExternalReferenceCode(
				companyId, cpDefinitionService, productExternalReferenceCode,
				groupId, mappedProduct.getProductType());

		return cpDefinition.getCProductId();
	}

	public static Map<String, Serializable> getExpandoBridgeAttributes(
		long companyId, Locale locale, MappedProduct mappedProduct) {

		Map<String, Serializable> expandoBridgeAttributes =
			CustomFieldsUtil.toMap(
				CSDiagramEntry.class.getName(), companyId,
				mappedProduct.getCustomFields(), locale);

		if (expandoBridgeAttributes == null) {
			expandoBridgeAttributes = new HashMap<>();
		}

		return expandoBridgeAttributes;
	}

	public static boolean isDiagram(
		CSDiagramEntry csDiagramEntry, MappedProduct mappedProduct) {

		if ((csDiagramEntry == null) && (mappedProduct.getType() == null)) {
			return false;
		}

		if (mappedProduct.getType() != null) {
			return Objects.equals(
				MappedProduct.Type.DIAGRAM.getValue(),
				mappedProduct.getTypeAsString());
		}

		return csDiagramEntry.isDiagram();
	}

	public static CSDiagramEntry updateCSDiagramEntry(
			long companyId, CSDiagramEntry csDiagramEntry,
			CSDiagramEntryService csDiagramEntryService, long groupId,
			Locale locale, MappedProduct mappedProduct,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext = serviceContextHelper.getServiceContext(
			groupId);

		serviceContext.setExpandoBridgeAttributes(
			getExpandoBridgeAttributes(companyId, locale, mappedProduct));

		return csDiagramEntryService.updateCSDiagramEntry(
			csDiagramEntry.getCSDiagramEntryId(),
			GetterUtil.get(
				mappedProduct.getSkuId(), csDiagramEntry.getCPInstanceId()),
			GetterUtil.get(
				mappedProduct.getProductId(), csDiagramEntry.getCProductId()),
			isDiagram(csDiagramEntry, mappedProduct),
			GetterUtil.get(
				mappedProduct.getQuantity(), csDiagramEntry.getQuantity()),
			GetterUtil.get(
				mappedProduct.getSequence(), csDiagramEntry.getSequence()),
			GetterUtil.get(mappedProduct.getSku(), csDiagramEntry.getSku()),
			serviceContext);
	}

}