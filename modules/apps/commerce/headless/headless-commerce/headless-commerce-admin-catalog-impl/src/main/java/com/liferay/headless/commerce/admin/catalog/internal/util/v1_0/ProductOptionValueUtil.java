/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class ProductOptionValueUtil {

	public static CPDefinitionOptionValueRel
			addOrUpdateCPDefinitionOptionValueRel(
				CPDefinitionOptionValueRelService
					cpDefinitionOptionValueRelService,
				CPInstanceService cpInstanceService,
				ProductOptionValue productOptionValue,
				long cpDefinitionOptionRelId, ServiceContext serviceContext)
		throws PortalException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel = null;

		String externalReferenceCode =
			productOptionValue.getExternalReferenceCode();

		if (Validator.isNotNull(externalReferenceCode)) {
			cpDefinitionOptionValueRel =
				cpDefinitionOptionValueRelService.
					fetchCPDefinitionOptionValueRelByExternalReferenceCode(
						externalReferenceCode, serviceContext.getCompanyId());
		}

		if (cpDefinitionOptionValueRel == null) {
			cpDefinitionOptionValueRel =
				cpDefinitionOptionValueRelService.
					fetchCPDefinitionOptionValueRel(
						cpDefinitionOptionRelId, productOptionValue.getKey());
		}

		long cpInstanceId = 0;

		CPInstance cpInstance =
			cpInstanceService.fetchCPInstanceByExternalReferenceCode(
				productOptionValue.getSkuExternalReferenceCode(),
				serviceContext.getCompanyId());

		if (cpInstance == null) {
			cpInstance = cpInstanceService.fetchCPInstance(
				GetterUtil.getLong(productOptionValue.getSkuId()));
		}

		if ((cpInstance == null) && (cpDefinitionOptionValueRel != null)) {
			cpInstance = cpDefinitionOptionValueRel.fetchCPInstance();
		}

		if (cpInstance != null) {
			cpInstanceId = cpInstance.getCPInstanceId();
		}

		Map<String, String> nameMap = productOptionValue.getName();

		if ((cpDefinitionOptionValueRel != null) && (nameMap == null)) {
			nameMap = LanguageUtils.getLanguageIdMap(
				cpDefinitionOptionValueRel.getNameMap());
		}

		if (cpDefinitionOptionValueRel == null) {
			cpDefinitionOptionValueRel =
				cpDefinitionOptionValueRelService.addCPDefinitionOptionValueRel(
					cpDefinitionOptionRelId, cpInstanceId,
					productOptionValue.getKey(),
					LanguageUtils.getLocalizedMap(nameMap),
					GetterUtil.get(productOptionValue.getPreselected(), false),
					BigDecimalUtil.get(
						productOptionValue.getDeltaPrice(), null),
					GetterUtil.get(productOptionValue.getPriority(), 0D),
					BigDecimalUtil.get(
						productOptionValue.getQuantity(), BigDecimal.ONE),
					GetterUtil.get(
						productOptionValue.getUnitOfMeasureKey(),
						StringPool.BLANK),
					serviceContext);
		}
		else {
			cpDefinitionOptionValueRel =
				cpDefinitionOptionValueRelService.
					updateCPDefinitionOptionValueRel(
						cpDefinitionOptionValueRel.
							getCPDefinitionOptionValueRelId(),
						cpInstanceId,
						GetterUtil.get(
							productOptionValue.getKey(),
							cpDefinitionOptionValueRel.getKey()),
						LanguageUtils.getLocalizedMap(nameMap),
						GetterUtil.get(
							productOptionValue.getPreselected(),
							cpDefinitionOptionValueRel.isPreselected()),
						BigDecimalUtil.get(
							productOptionValue.getDeltaPrice(),
							cpDefinitionOptionValueRel.getPrice()),
						GetterUtil.get(
							productOptionValue.getPriority(),
							cpDefinitionOptionValueRel.getPriority()),
						BigDecimalUtil.get(
							productOptionValue.getQuantity(),
							cpDefinitionOptionValueRel.getQuantity()),
						GetterUtil.get(
							productOptionValue.getUnitOfMeasureKey(),
							cpDefinitionOptionValueRel.getUnitOfMeasureKey()),
						serviceContext);
		}

		if (Validator.isNotNull(externalReferenceCode)) {
			cpDefinitionOptionValueRel =
				cpDefinitionOptionValueRelService.updateExternalReferenceCode(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId(),
					externalReferenceCode);
		}

		return cpDefinitionOptionValueRel;
	}

}