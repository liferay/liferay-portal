/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.web.internal.display.context;

import com.liferay.commerce.constants.CommerceTaxScreenNavigationConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.comparator.CPTaxCategoryCreateDateComparator;
import com.liferay.commerce.tax.engine.fixed.web.internal.display.context.helper.CommerceTaxFixedRateRequestHelper;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import java.math.BigDecimal;

import java.text.NumberFormat;

import java.util.List;
import java.util.Locale;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class BaseCommerceTaxFixedRateDisplayContext {

	public BaseCommerceTaxFixedRateDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceTaxMethodService commerceTaxMethodService,
		CPTaxCategoryService cpTaxCategoryService,
		ModelResourcePermission<CommerceChannel> modelResourcePermission,
		PercentageFormatter percentageFormatter, RenderRequest renderRequest) {

		this.commerceChannelLocalService = commerceChannelLocalService;
		this.commerceCurrencyLocalService = commerceCurrencyLocalService;
		this.commerceTaxMethodService = commerceTaxMethodService;
		this.cpTaxCategoryService = cpTaxCategoryService;
		this.modelResourcePermission = modelResourcePermission;
		this.percentageFormatter = percentageFormatter;

		commerceTaxFixedRateRequestHelper =
			new CommerceTaxFixedRateRequestHelper(renderRequest);
	}

	public List<CPTaxCategory> getAvailableCPTaxCategories()
		throws PortalException {

		return cpTaxCategoryService.getCPTaxCategories(
			commerceTaxFixedRateRequestHelper.getCompanyId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS,
			CPTaxCategoryCreateDateComparator.getInstance(false));
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
			commerceTaxFixedRateRequestHelper.getRequest(),
			"commerceChannelId");
	}

	public String getCommerceCurrencyCode() throws PortalException {
		CommerceTaxMethod commerceTaxMethod = getCommerceTaxMethod();

		CommerceChannel commerceChannel =
			commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceTaxMethod.getGroupId());

		return commerceChannel.getCommerceCurrencyCode();
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
			commerceTaxFixedRateRequestHelper.getRequest(),
			"commerceTaxMethodId");
	}

	public String getLocalizedPercentage(double percentage, Locale locale)
		throws PortalException {

		CommerceChannel commerceChannel =
			commerceChannelLocalService.getCommerceChannel(
				getCommerceChannelId());

		CommerceCurrency commerceCurrency =
			commerceCurrencyLocalService.getCommerceCurrency(
				commerceChannel.getCompanyId(),
				commerceChannel.getCommerceCurrencyCode());

		return StringUtil.removeSubstring(
			percentageFormatter.getLocalizedPercentage(
				locale, commerceCurrency.getMaxFractionDigits(),
				commerceCurrency.getMinFractionDigits(),
				new BigDecimal(percentage)),
			StringPool.PERCENT);
	}

	public String getLocalizedRate(
			boolean percentage, double rate, Locale locale)
		throws PortalException {

		if (percentage) {
			return getLocalizedPercentage(rate, locale);
		}

		return getLocalizedRate(rate, locale);
	}

	public String getLocalizedRate(double rate, Locale locale)
		throws PortalException {

		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

		return numberFormat.format(rate);
	}

	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.createRenderURL(
			commerceTaxFixedRateRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_tax_methods/edit_commerce_tax_method"
		).setRedirect(
			() -> {
				String redirect = ParamUtil.getString(
					commerceTaxFixedRateRequestHelper.getRequest(), "redirect");

				if (Validator.isNotNull(redirect)) {
					return redirect;
				}

				return null;
			}
		).setParameter(
			"commerceTaxMethodId",
			() -> {
				CommerceTaxMethod commerceTaxMethod = getCommerceTaxMethod();

				if (commerceTaxMethod != null) {
					return commerceTaxMethod.getCommerceTaxMethodId();
				}

				return null;
			}
		).setParameter(
			"delta",
			() -> {
				String delta = ParamUtil.getString(
					commerceTaxFixedRateRequestHelper.getRequest(), "delta");

				if (Validator.isNotNull(delta)) {
					return delta;
				}

				return null;
			}
		).setParameter(
			"engineKey",
			() -> {
				String engineKey = ParamUtil.getString(
					commerceTaxFixedRateRequestHelper.getRequest(),
					"engineKey");

				if (Validator.isNotNull(engineKey)) {
					return engineKey;
				}

				return null;
			}
		).setParameter(
			"screenNavigationCategoryKey",
			_getSelectedScreenNavigationCategoryKey()
		).buildPortletURL();
	}

	public String getScreenNavigationCategoryKey() {
		return CommerceTaxScreenNavigationConstants.
			CATEGORY_KEY_COMMERCE_TAX_METHOD_DETAIL;
	}

	public boolean hasUpdateCommerceChannelPermission() throws PortalException {
		return modelResourcePermission.contains(
			commerceTaxFixedRateRequestHelper.getPermissionChecker(),
			commerceChannelLocalService.getCommerceChannel(
				getCommerceChannelId()),
			ActionKeys.UPDATE);
	}

	protected final CommerceChannelLocalService commerceChannelLocalService;
	protected final CommerceCurrencyLocalService commerceCurrencyLocalService;
	protected final CommerceTaxFixedRateRequestHelper
		commerceTaxFixedRateRequestHelper;
	protected final CommerceTaxMethodService commerceTaxMethodService;
	protected final CPTaxCategoryService cpTaxCategoryService;
	protected final ModelResourcePermission<CommerceChannel>
		modelResourcePermission;
	protected final PercentageFormatter percentageFormatter;

	private String _getSelectedScreenNavigationCategoryKey() {
		return ParamUtil.getString(
			commerceTaxFixedRateRequestHelper.getRequest(),
			"screenNavigationCategoryKey", getScreenNavigationCategoryKey());
	}

	private CommerceTaxMethod _commerceTaxMethod;

}