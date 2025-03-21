/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.avalara.tax.engine.fixed.web.internal.display.context;

import com.liferay.commerce.avalara.tax.engine.fixed.web.internal.display.context.helper.CommerceAvalaraTaxRateRequestHelper;
import com.liferay.commerce.constants.CommerceTaxScreenNavigationConstants;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class BaseCommerceAvalaraTaxRateDisplayContext {

	public BaseCommerceAvalaraTaxRateDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		ModelResourcePermission<CommerceChannel>
			commerceChannelModelResourcePermission,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceTaxMethodService commerceTaxMethodService,
		CPTaxCategoryService cpTaxCategoryService,
		PercentageFormatter percentageFormatter, RenderRequest renderRequest) {

		this.commerceChannelLocalService = commerceChannelLocalService;
		this.commerceChannelModelResourcePermission =
			commerceChannelModelResourcePermission;
		this.commerceCurrencyLocalService = commerceCurrencyLocalService;
		this.commerceTaxMethodService = commerceTaxMethodService;
		this.cpTaxCategoryService = cpTaxCategoryService;
		this.percentageFormatter = percentageFormatter;

		commerceAvalaraTaxRateRequestHelper =
			new CommerceAvalaraTaxRateRequestHelper(renderRequest);
	}

	public long getCommerceChannelId() throws PortalException {
		CommerceTaxMethod commerceTaxMethod = getCommerceTaxMethod();

		if (commerceTaxMethod != null) {
			CommerceChannel commerceChannel =
				commerceChannelLocalService.getCommerceChannelByGroupId(
					commerceTaxMethod.getGroupId());

			return commerceChannel.getCommerceChannelId();
		}

		return ParamUtil.getLong(
			commerceAvalaraTaxRateRequestHelper.getRequest(),
			"commerceChannelId");
	}

	public CommerceTaxMethod getCommerceTaxMethod() throws PortalException {
		if (_commerceTaxMethod != null) {
			return _commerceTaxMethod;
		}

		long commerceTaxMethodId = getCommerceTaxMethodId();

		if (commerceTaxMethodId > 0) {
			_commerceTaxMethod = commerceTaxMethodService.getCommerceTaxMethod(
				commerceTaxMethodId);
		}

		return _commerceTaxMethod;
	}

	public long getCommerceTaxMethodId() throws PortalException {
		return ParamUtil.getLong(
			commerceAvalaraTaxRateRequestHelper.getRequest(),
			"commerceTaxMethodId");
	}

	public PortletURL getPortletURL() throws PortalException {
		LiferayPortletResponse liferayPortletResponse =
			commerceAvalaraTaxRateRequestHelper.getLiferayPortletResponse();

		return PortletURLBuilder.create(
			liferayPortletResponse.createRenderURL()
		).setMVCPath(
			"/commerce_tax_methods/edit_commerce_tax_method"
		).setRedirect(
			ParamUtil.getString(
				commerceAvalaraTaxRateRequestHelper.getRequest(), "redirect")
		).setParameter(
			"commerceTaxMethodId",
			() -> {
				CommerceTaxMethod commerceTaxMethod = getCommerceTaxMethod();

				if (commerceTaxMethod != null) {
					return String.valueOf(
						commerceTaxMethod.getCommerceTaxMethodId());
				}

				return null;
			}
		).setParameter(
			"delta",
			ParamUtil.getString(
				commerceAvalaraTaxRateRequestHelper.getRequest(), "delta")
		).setParameter(
			"engineKey",
			ParamUtil.getString(
				commerceAvalaraTaxRateRequestHelper.getRequest(), "engineKey")
		).setParameter(
			"screenNavigationCategoryKey",
			getSelectedScreenNavigationCategoryKey()
		).buildPortletURL();
	}

	public String getScreenNavigationCategoryKey() {
		return CommerceTaxScreenNavigationConstants.
			CATEGORY_KEY_COMMERCE_TAX_METHOD_DETAIL;
	}

	protected String getSelectedScreenNavigationCategoryKey() {
		return ParamUtil.getString(
			commerceAvalaraTaxRateRequestHelper.getRequest(),
			"screenNavigationCategoryKey", getScreenNavigationCategoryKey());
	}

	protected final CommerceAvalaraTaxRateRequestHelper
		commerceAvalaraTaxRateRequestHelper;
	protected final CommerceChannelLocalService commerceChannelLocalService;
	protected final ModelResourcePermission<CommerceChannel>
		commerceChannelModelResourcePermission;
	protected final CommerceCurrencyLocalService commerceCurrencyLocalService;
	protected final CommerceTaxMethodService commerceTaxMethodService;
	protected final CPTaxCategoryService cpTaxCategoryService;
	protected final PercentageFormatter percentageFormatter;

	private CommerceTaxMethod _commerceTaxMethod;

}