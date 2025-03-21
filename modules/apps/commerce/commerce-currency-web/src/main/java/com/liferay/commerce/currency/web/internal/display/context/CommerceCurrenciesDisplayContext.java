/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.web.internal.display.context;

import com.liferay.commerce.currency.configuration.CommerceCurrencyConfiguration;
import com.liferay.commerce.currency.configuration.RoundingTypeConfiguration;
import com.liferay.commerce.currency.constants.CommerceCurrencyActionKeys;
import com.liferay.commerce.currency.constants.CommerceCurrencyConstants;
import com.liferay.commerce.currency.constants.CommerceCurrencyExchangeRateConstants;
import com.liferay.commerce.currency.constants.CommerceCurrencyPortletKeys;
import com.liferay.commerce.currency.constants.RoundingTypeConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.currency.util.ExchangeRateProviderRegistry;
import com.liferay.commerce.currency.web.internal.util.CommerceCurrencyUtil;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Andrea Di Giorgi
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 * @author Luca Pellizzon
 */
public class CommerceCurrenciesDisplayContext {

	public CommerceCurrenciesDisplayContext(
		CommerceCurrencyService commerceCurrencyService,
		CommercePriceFormatter commercePriceFormatter,
		ConfigurationProvider configurationProvider,
		ExchangeRateProviderRegistry exchangeRateProviderRegistry,
		PortletResourcePermission portletResourcePermission,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_commerceCurrencyService = commerceCurrencyService;
		_commercePriceFormatter = commercePriceFormatter;
		_configurationProvider = configurationProvider;
		_exchangeRateProviderRegistry = exchangeRateProviderRegistry;
		_portletResourcePermission = portletResourcePermission;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_liferayPortletRequest = PortalUtil.getLiferayPortletRequest(
			renderRequest);
		_liferayPortletResponse = PortalUtil.getLiferayPortletResponse(
			renderResponse);

		_httpServletRequest = _liferayPortletRequest.getHttpServletRequest();
	}

	public String format(BigDecimal rate) throws PortalException {
		return _commercePriceFormatter.format(
			rate, PortalUtil.getLocale(_renderRequest));
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest,
						CommerceCurrencyPortletKeys.COMMERCE_CURRENCY,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/commerce_currency/edit_commerce_currency"
				).setCMD(
					"updateExchangeRates"
				).buildString(),
				"reload", "update-exchange-rates",
				LanguageUtil.get(_httpServletRequest, "update-exchange-rates"),
				"patch", null, null),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest,
						CommerceCurrencyPortletKeys.COMMERCE_CURRENCY,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/commerce_currency/edit_commerce_currency"
				).setCMD(
					Constants.DELETE
				).buildString(),
				"trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "delete", null,
				null));
	}

	public CommerceCurrency getCommerceCurrency() throws PortalException {
		if (_commerceCurrency != null) {
			return _commerceCurrency;
		}

		long commerceCurrencyId = ParamUtil.getLong(
			_renderRequest, "commerceCurrencyId");

		if (commerceCurrencyId > 0) {
			_commerceCurrency = _commerceCurrencyService.getCommerceCurrency(
				commerceCurrencyId);
		}

		return _commerceCurrency;
	}

	public CommerceCurrencyConfiguration getCommerceCurrencyConfiguration()
		throws ConfigurationException {

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _configurationProvider.getConfiguration(
			CommerceCurrencyConfiguration.class,
			new CompanyServiceSettingsLocator(
				themeDisplay.getCompanyId(),
				CommerceCurrencyExchangeRateConstants.SERVICE_NAME));
	}

	public String getCurrencyApiURL() {
		return "/o/headless-commerce-admin-catalog/v1.0/currencies";
	}

	public String getDefaultFormatPattern() throws ConfigurationException {
		return CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN;
	}

	public int getDefaultMaxFractionDigits() throws ConfigurationException {
		RoundingTypeConfiguration roundingTypeConfiguration =
			_getRoundingTypeConfiguration();

		return roundingTypeConfiguration.maximumFractionDigits();
	}

	public int getDefaultMinFractionDigits() throws ConfigurationException {
		RoundingTypeConfiguration roundingTypeConfiguration =
			_getRoundingTypeConfiguration();

		return roundingTypeConfiguration.minimumFractionDigits();
	}

	public String getDefaultRoundingMode() throws ConfigurationException {
		RoundingTypeConfiguration roundingTypeConfiguration =
			_getRoundingTypeConfiguration();

		RoundingMode roundingMode = roundingTypeConfiguration.roundingMode();

		return roundingMode.name();
	}

	public Iterable<String> getExchangeRateProviderKeys() {
		return _exchangeRateProviderRegistry.getExchangeRateProviderKeys();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest,
						CommerceCurrencyPortletKeys.COMMERCE_CURRENCY,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/commerce_currency/edit_commerce_currency"
				).setParameter(
					"commerceCurrencyId", "{id}"
				).buildString(),
				"pencil", "view", LanguageUtil.get(_httpServletRequest, "edit"),
				"get", null, null),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest,
						CommerceCurrencyPortletKeys.COMMERCE_CURRENCY,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/commerce_currency/edit_commerce_currency"
				).setCMD(
					"setActive"
				).setParameter(
					"active", Boolean.TRUE
				).setParameter(
					"commerceCurrencyId", "{id}"
				).buildString(),
				"pencil", "toggle-active",
				LanguageUtil.get(_httpServletRequest, "toggle-active"), "patch",
				null, null),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest,
						CommerceCurrencyPortletKeys.COMMERCE_CURRENCY,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/commerce_currency/edit_commerce_currency"
				).setCMD(
					"setPrimary"
				).setParameter(
					"commerceCurrencyId", "{id}"
				).setParameter(
					"primary", Boolean.TRUE
				).buildString(),
				"pencil", "primary",
				LanguageUtil.get(_httpServletRequest, "primary"), "patch", null,
				null),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest,
						CommerceCurrencyPortletKeys.COMMERCE_CURRENCY,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/commerce_currency/edit_commerce_currency"
				).setCMD(
					"updateExchangeRates"
				).setParameter(
					"commerceCurrencyId", "{id}"
				).buildString(),
				"reload", "update-exchange-rates",
				LanguageUtil.get(_httpServletRequest, "update-exchange-rates"),
				"get", null, null),
			new FDSActionDropdownItem(
				LanguageUtil.get(
					_httpServletRequest,
					"are-you-sure-you-want-to-delete-this-entry"),
				getCurrencyApiURL() + "/{id}", "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "delete", null,
				"async"));
	}

	public CreationMenu getFDSCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName",
					"/commerce_currency/edit_commerce_currency", "redirect",
					PortalUtil.getCurrentURL(_liferayPortletRequest));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "add-currency"));
			}
		).build();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_renderRequest, CommerceCurrencyPortletKeys.COMMERCE_CURRENCY,
			"priority");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_renderRequest, CommerceCurrencyPortletKeys.COMMERCE_CURRENCY,
			"asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setNavigation(
			_getNavigation()
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildPortletURL();
	}

	public CommerceCurrency getPrimaryCommerceCurrency()
		throws PortalException {

		if (_primaryCommerceCurrency != null) {
			return _primaryCommerceCurrency;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_primaryCommerceCurrency =
			_commerceCurrencyService.fetchPrimaryCommerceCurrency(
				themeDisplay.getCompanyId());

		return _primaryCommerceCurrency;
	}

	public String getRoundingModeLabel(String roundingModeName) {
		return LanguageUtil.get(
			PortalUtil.getLocale(_renderRequest),
			StringUtil.replace(
				StringUtil.toLowerCase(roundingModeName), CharPool.UNDERLINE,
				CharPool.DASH));
	}

	public SearchContainer<CommerceCurrency> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Boolean active = null;
		String emptyResultsMessage = "there-are-no-currencies";

		String navigation = _getNavigation();

		if (navigation.equals("active")) {
			active = Boolean.TRUE;
			emptyResultsMessage = "there-are-no-active-currencies";
		}
		else if (navigation.equals("inactive")) {
			active = Boolean.FALSE;
			emptyResultsMessage = "there-are-no-inactive-currencies";
		}

		_searchContainer = new SearchContainer<>(
			_renderRequest, getPortletURL(), null, emptyResultsMessage);

		_searchContainer.setOrderByCol(getOrderByCol());
		_searchContainer.setOrderByComparator(
			CommerceCurrencyUtil.getCommerceCurrencyOrderByComparator(
				getOrderByCol(), getOrderByType()));
		_searchContainer.setOrderByType(getOrderByType());

		String keywords = ParamUtil.getString(_renderRequest, "keywords");

		if (Validator.isBlank(keywords)) {
			if (active != null) {
				boolean finalActive = active;

				_searchContainer.setResultsAndTotal(
					() -> _commerceCurrencyService.getCommerceCurrencies(
						themeDisplay.getCompanyId(), finalActive,
						_searchContainer.getStart(), _searchContainer.getEnd(),
						_searchContainer.getOrderByComparator()),
					_commerceCurrencyService.getCommerceCurrenciesCount(
						themeDisplay.getCompanyId(), finalActive));
			}
			else {
				_searchContainer.setResultsAndTotal(
					() -> _commerceCurrencyService.getCommerceCurrencies(
						themeDisplay.getCompanyId(),
						_searchContainer.getStart(), _searchContainer.getEnd(),
						_searchContainer.getOrderByComparator()),
					_commerceCurrencyService.getCommerceCurrenciesCount(
						themeDisplay.getCompanyId()));
			}
		}
		else {
			LinkedHashMap<String, Object> params = new LinkedHashMap<>();

			if (active != null) {
				params.put(CPField.ACTIVE, active);
			}

			_searchContainer.setResultsAndTotal(
				_commerceCurrencyService.searchCommerceCurrencies(
					themeDisplay.getCompanyId(), keywords, params,
					_searchContainer.getStart(), _searchContainer.getEnd(),
					CommerceCurrencyUtil.getCommerceCurrencySort(
						getOrderByCol(), getOrderByType())));
		}

		_searchContainer.setRowChecker(_getRowChecker());

		return _searchContainer;
	}

	public boolean hasManageCommerceCurrencyPermission() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null,
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);
	}

	private String _getNavigation() {
		return ParamUtil.getString(_renderRequest, "navigation");
	}

	private RoundingTypeConfiguration _getRoundingTypeConfiguration()
		throws ConfigurationException {

		return _configurationProvider.getConfiguration(
			RoundingTypeConfiguration.class,
			new SystemSettingsLocator(RoundingTypeConstants.SERVICE_NAME));
	}

	private RowChecker _getRowChecker() {
		if (_rowChecker == null) {
			_rowChecker = new EmptyOnClickRowChecker(_renderResponse);
		}

		return _rowChecker;
	}

	private CommerceCurrency _commerceCurrency;
	private final CommerceCurrencyService _commerceCurrencyService;
	private final CommercePriceFormatter _commercePriceFormatter;
	private final ConfigurationProvider _configurationProvider;
	private final ExchangeRateProviderRegistry _exchangeRateProviderRegistry;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByCol;
	private String _orderByType;
	private final PortletResourcePermission _portletResourcePermission;
	private CommerceCurrency _primaryCommerceCurrency;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private RowChecker _rowChecker;
	private SearchContainer<CommerceCurrency> _searchContainer;

}