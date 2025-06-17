/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.option;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.frontend.helper.ProductHelper;
import com.liferay.commerce.frontend.model.ProductSettingsModel;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.option.CommerceOptionType;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Sku;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.converter.SkuDTOConverterContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import java.math.BigDecimal;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"commerce.option.type.display.order:Integer=200",
		"commerce.option.type.key=" + CPConstants.PRODUCT_OPTION_SELECT_KEY
	},
	service = CommerceOptionType.class
)
public class SelectCommerceOptionTypeImpl implements CommerceOptionType {

	@Override
	public String getKey() {
		return CPConstants.PRODUCT_OPTION_SELECT_KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "select-from-list");
	}

	@Override
	public boolean hasValues() {
		return true;
	}

	@Override
	public boolean isValid(
		CPDefinitionOptionRel cpDefinitionOptionRel, String[] values) {

		if (ArrayUtil.isEmpty(values) || Validator.isBlank(values[0])) {
			return true;
		}

		for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
				cpDefinitionOptionRel.getCPDefinitionOptionValueRels()) {

			if (Objects.equals(
					cpDefinitionOptionValueRel.getKey(), values[0])) {

				return true;
			}
		}

		return false;
	}

	@Override
	public void render(
			CPDefinitionOptionRel cpDefinitionOptionRel,
			long defaultCPInstanceId, boolean forceRequired, String json,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (cpDefinitionOptionRel == null) {
			return;
		}

		AccountEntry accountEntry = null;
		BigDecimal minQuantity = BigDecimal.ONE;
		Object productOption = null;
		Sku sku = null;

		CPDefinition cpDefinition = cpDefinitionOptionRel.getCPDefinition();

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry,
				cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				_portal.getLocale(httpServletRequest), null,
				_portal.getUser(httpServletRequest));

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		boolean admin = CPPortletKeys.CP_DEFINITIONS.equals(
			portletDisplay.getPortletName());

		if (admin) {
			defaultDTOConverterContext.setAttribute(
				"showProductOptionValues", Boolean.TRUE);

			productOption = _adminProductOptionDTOConverter.toDTO(
				defaultDTOConverterContext);
		}
		else {
			accountEntry = commerceContext.getAccountEntry();

			ProductSettingsModel productSettingsModel =
				_productHelper.getProductSettingsModel(
					cpDefinition.getCPDefinitionId(), commerceContext);

			minQuantity = productSettingsModel.getMinQuantity();

			defaultDTOConverterContext.setAttribute(
				"commerceContext", commerceContext);
			defaultDTOConverterContext.setAttribute(
				"productOptionValueId",
				_getSelectedCPDefinitionOptionValueRelId(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					defaultCPInstanceId));
			defaultDTOConverterContext.setAttribute(
				"skuId", defaultCPInstanceId);

			productOption = _productOptionDTOConverter.toDTO(
				defaultDTOConverterContext);

			if (defaultCPInstanceId > 0) {
				sku = _skuDTOConverter.toDTO(
					new SkuDTOConverterContext(
						commerceContext,
						_portal.getCompanyId(httpServletRequest), cpDefinition,
						_portal.getLocale(httpServletRequest), minQuantity,
						defaultCPInstanceId, null, StringPool.BLANK, null,
						_portal.getUser(httpServletRequest)));
			}
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write("<div>");

		_reactRenderer.renderReact(
			new ComponentDescriptor(
				"{ProductOptionSelect} from commerce-frontend-js"),
			HashMapBuilder.<String, Object>put(
				"accountId",
				(accountEntry == null) ? 0 : accountEntry.getAccountEntryId()
			).put(
				"channelId",
				(commerceContext == null) ? 0 :
					commerceContext.getCommerceChannelId()
			).put(
				"componentId", StringUtil.randomId()
			).put(
				"forceRequired", forceRequired
			).put(
				"isAdmin", admin
			).put(
				"json", json
			).put(
				"minQuantity", minQuantity
			).put(
				"namespace", portletDisplay.getNamespace()
			).put(
				"productId", cpDefinition.getCProductId()
			).put(
				"productOption", productOption
			).put(
				"sku", sku
			).build(),
			httpServletRequest, printWriter);

		printWriter.write("</div>");
	}

	private long _getSelectedCPDefinitionOptionValueRelId(
		long cpDefinitionOptionRelId, long defaultCPInstanceId) {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				fetchCPDefinitionOptionValueRel(
					cpDefinitionOptionRelId, defaultCPInstanceId);

		if (cpDefinitionOptionValueRel == null) {
			return 0;
		}

		return cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId();
	}

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.ProductOptionDTOConverter)"
	)
	private DTOConverter
		<CPDefinitionOptionRel,
		 com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOption>
			_adminProductOptionDTOConverter;

	@Reference
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private ProductHelper _productHelper;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.ProductOptionDTOConverter)"
	)
	private DTOConverter<CPDefinitionOptionRel, ProductOption>
		_productOptionDTOConverter;

	@Reference
	private ReactRenderer _reactRenderer;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.SkuDTOConverter)"
	)
	private DTOConverter<CPInstance, Sku> _skuDTOConverter;

}