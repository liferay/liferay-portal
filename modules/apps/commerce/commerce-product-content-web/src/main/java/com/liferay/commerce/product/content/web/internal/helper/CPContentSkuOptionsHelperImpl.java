/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.helper;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.content.helper.CPContentSkuOptionsHelper;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Sku;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.SkuOption;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.converter.SkuDTOConverterContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = CPContentSkuOptionsHelper.class)
public class CPContentSkuOptionsHelperImpl
	implements CPContentSkuOptionsHelper {

	@Override
	public String getDefaultCPInstanceSkuOptions(
			long cpDefinitionId, HttpServletRequest httpServletRequest)
		throws Exception {

		CPInstance defaultCPInstance =
			_cpInstanceLocalService.fetchDefaultCPInstance(cpDefinitionId);

		if (defaultCPInstance == null) {
			return "[]";
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		Sku sku = _skuDTOConverter.toDTO(
			new SkuDTOConverterContext(
				commerceContext, _portal.getCompanyId(httpServletRequest),
				defaultCPInstance.getCPDefinition(),
				_portal.getLocale(httpServletRequest), BigDecimal.ONE,
				defaultCPInstance.getCPInstanceId(), null, StringPool.BLANK,
				null, _portal.getUser(httpServletRequest)));

		SkuOption[] skuOptions = sku.getSkuOptions();

		if (skuOptions == null) {
			return "[]";
		}

		for (SkuOption skuOption : skuOptions) {
			JSONObject jsonObject = _jsonFactory.createJSONObject();

			jsonObject.put(
				"key", skuOption.getSkuOptionKey()
			).put(
				"skuOptionName", skuOption.getSkuOptionName()
			).put(
				"skuOptionValueNames", skuOption.getSkuOptionValueNames()
			).put(
				"value", skuOption.getSkuOptionValueKey()
			);

			if (Validator.isNotNull(skuOption.getSkuId())) {
				jsonObject.put(
					"price", skuOption.getPrice()
				).put(
					"priceType", skuOption.getPriceType()
				).put(
					"quantity", skuOption.getQuantity()
				).put(
					"skuId", String.valueOf(skuOption.getSkuId())
				);
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray.toString();
	}

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.SkuDTOConverter)"
	)
	private DTOConverter<CPInstance, Sku> _skuDTOConverter;

}