/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPOptionException;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class ProductOptionUtil {

	public static CPDefinitionOptionRel addOrUpdateCPDefinitionOptionRel(
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPOptionService cpOptionService, ProductOption productOption,
			long cpDefinitionId, ServiceContext serviceContext)
		throws PortalException {

		CPOption cpOption = null;

		long optionId = GetterUtil.getLong(productOption.getOptionId());

		if (optionId > 0) {
			cpOption = cpOptionService.getCPOption(optionId);
		}
		else {
			cpOption = cpOptionService.fetchCPOptionByExternalReferenceCode(
				productOption.getOptionExternalReferenceCode(),
				serviceContext.getCompanyId());

			if (cpOption == null) {
				throw new NoSuchCPOptionException();
			}
		}

		CPDefinitionOptionRel cpDefinitionOptionRel = null;

		String externalReferenceCode = productOption.getExternalReferenceCode();

		if (Validator.isNotNull(externalReferenceCode)) {
			cpDefinitionOptionRel =
				cpDefinitionOptionRelService.
					fetchCPDefinitionOptionRelByExternalReferenceCode(
						externalReferenceCode, serviceContext.getCompanyId());
		}

		if (cpDefinitionOptionRel == null) {
			cpDefinitionOptionRel =
				cpDefinitionOptionRelService.fetchCPDefinitionOptionRel(
					cpDefinitionId, cpOption.getCPOptionId());
		}

		Map<String, String> nameMap = productOption.getName();

		if ((cpDefinitionOptionRel != null) && (nameMap == null)) {
			nameMap = LanguageUtils.getLanguageIdMap(
				cpDefinitionOptionRel.getNameMap());
		}

		Map<String, String> descriptionMap = productOption.getDescription();

		if ((cpDefinitionOptionRel != null) && (descriptionMap == null)) {
			descriptionMap = LanguageUtils.getLanguageIdMap(
				cpDefinitionOptionRel.getDescriptionMap());
		}

		Map<String, Serializable> expandoBridgeAttributes =
			CustomFieldsUtil.toMap(
				CPDefinitionOptionRel.class.getName(),
				serviceContext.getCompanyId(), productOption.getCustomFields(),
				serviceContext.getLocale());

		if (expandoBridgeAttributes == null) {
			expandoBridgeAttributes = new HashMap<>();
		}

		serviceContext.setExpandoBridgeAttributes(expandoBridgeAttributes);

		if (cpDefinitionOptionRel == null) {
			cpDefinitionOptionRel =
				cpDefinitionOptionRelService.addCPDefinitionOptionRel(
					cpDefinitionId, cpOption.getCPOptionId(),
					LanguageUtils.getLocalizedMap(nameMap),
					LanguageUtils.getLocalizedMap(descriptionMap),
					GetterUtil.get(
						productOption.getFieldType(),
						cpOption.getCommerceOptionTypeKey()),
					GetterUtil.get(
						productOption.getInfoItemServiceKey(),
						StringPool.BLANK),
					GetterUtil.get(productOption.getPriority(), 0D),
					GetterUtil.get(productOption.getDefinedExternally(), false),
					GetterUtil.get(
						productOption.getFacetable(), cpOption.isFacetable()),
					GetterUtil.get(
						productOption.getRequired(), cpOption.isRequired()),
					GetterUtil.get(
						productOption.getSkuContributor(),
						cpOption.isSkuContributor()),
					ArrayUtil.isEmpty(productOption.getProductOptionValues()),
					GetterUtil.get(
						productOption.getPriceType(), StringPool.BLANK),
					GetterUtil.get(
						productOption.getTypeSettings(), StringPool.BLANK),
					serviceContext);
		}
		else {
			cpDefinitionOptionRel =
				cpDefinitionOptionRelService.updateCPDefinitionOptionRel(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					productOption.getOptionId(),
					LanguageUtils.getLocalizedMap(nameMap),
					LanguageUtils.getLocalizedMap(descriptionMap),
					GetterUtil.get(
						productOption.getFieldType(),
						cpDefinitionOptionRel.getCommerceOptionTypeKey()),
					GetterUtil.get(
						productOption.getInfoItemServiceKey(),
						cpDefinitionOptionRel.getInfoItemServiceKey()),
					GetterUtil.get(
						productOption.getPriority(),
						cpDefinitionOptionRel.getPriority()),
					GetterUtil.get(
						productOption.getDefinedExternally(),
						cpDefinitionOptionRel.isDefinedExternally()),
					GetterUtil.get(
						productOption.getFacetable(),
						cpDefinitionOptionRel.isFacetable()),
					GetterUtil.get(
						productOption.getRequired(),
						cpDefinitionOptionRel.isRequired()),
					GetterUtil.get(
						productOption.getSkuContributor(),
						cpDefinitionOptionRel.isSkuContributor()),
					GetterUtil.get(
						productOption.getPriceType(),
						cpDefinitionOptionRel.getPriceType()),
					GetterUtil.get(
						productOption.getTypeSettings(),
						cpDefinitionOptionRel.getTypeSettings()),
					serviceContext);
		}

		if (Validator.isNotNull(externalReferenceCode)) {
			cpDefinitionOptionRel =
				cpDefinitionOptionRelService.updateExternalReferenceCode(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					externalReferenceCode);
		}

		return cpDefinitionOptionRel;
	}

}