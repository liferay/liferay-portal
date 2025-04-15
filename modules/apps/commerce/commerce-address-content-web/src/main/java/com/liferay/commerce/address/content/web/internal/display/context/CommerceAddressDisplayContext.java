/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.address.content.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.address.content.web.internal.portlet.action.helper.ActionHelper;
import com.liferay.commerce.address.content.web.internal.portlet.configuration.CommerceAddressContentPortletInstanceConfiguration;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceAddressDisplayContext {

	public CommerceAddressDisplayContext(
			ActionHelper actionHelper,
			CommerceAccountHelper commerceAccountHelper,
			CommerceAddressService commerceAddressService,
			CountryService countryService, GroupLocalService groupLocalService,
			HttpServletRequest httpServletRequest, RegionService regionService)
		throws PortalException {

		_actionHelper = actionHelper;
		_commerceAccountHelper = commerceAccountHelper;
		_commerceAddressService = commerceAddressService;
		_countryService = countryService;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_regionService = regionService;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);

		_liferayPortletRequest = _cpRequestHelper.getLiferayPortletRequest();
		_liferayPortletResponse = _cpRequestHelper.getLiferayPortletResponse();

		_commerceAddressContentPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				CommerceAddressContentPortletInstanceConfiguration.class,
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY));
	}

	public AccountEntry getAccountEntry() throws PortalException {
		return _commerceAccountHelper.getCurrentAccountEntry(
			_cpRequestHelper.getCommerceChannelGroupId(), _httpServletRequest);
	}

	public String getAddCommerceAddressURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/commerce_address_content/edit_commerce_address"
		).setRedirect(
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return themeDisplay.getURLCurrent();
			}
		).buildString();
	}

	public CommerceAddress getCommerceAddress() throws PortalException {
		if (_commerceAddress != null) {
			return _commerceAddress;
		}

		_commerceAddress = _actionHelper.getCommerceAddress(
			_cpRequestHelper.getRenderRequest());

		return _commerceAddress;
	}

	public long getCommerceAddressId() throws PortalException {
		CommerceAddress commerceAddress = getCommerceAddress();

		if (commerceAddress == null) {
			return 0;
		}

		return commerceAddress.getCommerceAddressId();
	}

	public List<Country> getCountries() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _countryService.getCompanyCountries(
			themeDisplay.getCompanyId(), true);
	}

	public long getCountryId() throws PortalException {
		long countryId = 0;

		CommerceAddress commerceAddress = getCommerceAddress();

		if (commerceAddress != null) {
			countryId = commerceAddress.getCountryId();
		}

		return countryId;
	}

	public String getDeleteCommerceAddressURL(long commerceAddressId) {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/commerce_address_content/edit_commerce_address"
		).setCMD(
			Constants.DELETE
		).setRedirect(
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return themeDisplay.getURLCurrent();
			}
		).setParameter(
			"commerceAddressId", commerceAddressId
		).buildString();
	}

	public String getDisplayStyle() {
		return _commerceAddressContentPortletInstanceConfiguration.
			displayStyle();
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != null) {
			return _displayStyleGroupId;
		}

		String displayStyleGroupExternalReferenceCode =
			_commerceAddressContentPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		ThemeDisplay themeDisplay = _cpRequestHelper.getThemeDisplay();

		Group group = themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupId = group.getGroupId();
		}
		else {
			_displayStyleGroupId = themeDisplay.getScopeGroupId();
		}

		return _displayStyleGroupId;
	}

	public String getDisplayStyleGroupKey() {
		if (Validator.isNotNull(_displayStyleGroupKey)) {
			return _displayStyleGroupKey;
		}

		String displayStyleGroupExternalReferenceCode =
			_commerceAddressContentPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		ThemeDisplay themeDisplay = _cpRequestHelper.getThemeDisplay();

		Group group = themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupKey = group.getGroupKey();
		}
		else {
			_displayStyleGroupKey = StringPool.BLANK;
		}

		return _displayStyleGroupKey;
	}

	public String getEditCommerceAddressURL(long commerceAddressId) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/commerce_address_content/edit_commerce_address"
		).setRedirect(
			(PortletURL)_liferayPortletResponse.createRenderURL()
		).setParameter(
			"commerceAddressId", commerceAddressId
		).buildString();
	}

	public PortletURL getPortletURL() throws PortalException {
		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		CommerceAddress commerceAddress = getCommerceAddress();

		if (commerceAddress != null) {
			portletURL.setParameter(
				"commerceAddressId", String.valueOf(getCommerceAddressId()));
		}

		String delta = ParamUtil.getString(_httpServletRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			_httpServletRequest, "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		return portletURL;
	}

	public long getRegionId() throws PortalException {
		long regionId = 0;

		CommerceAddress commerceAddress = getCommerceAddress();

		if (commerceAddress != null) {
			regionId = commerceAddress.getRegionId();
		}

		return regionId;
	}

	public List<Region> getRegions() throws PortalException {
		return _regionService.getRegions(getCountryId(), true);
	}

	public SearchContainer<CommerceAddress> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			_liferayPortletRequest, getPortletURL(), null,
			"there-are-no-addresses");

		_searchContainer.setOrderByCol("create-date");
		_searchContainer.setOrderByComparator(
			CommerceUtil.getCommerceAddressOrderByComparator(
				"create-date", "desc"));
		_searchContainer.setOrderByType("desc");

		AccountEntry accountEntry = getAccountEntry();

		_searchContainer.setResultsAndTotal(
			_commerceAddressService.searchCommerceAddresses(
				accountEntry.getCompanyId(), AccountEntry.class.getName(),
				accountEntry.getAccountEntryId(), null,
				_searchContainer.getStart(), _searchContainer.getEnd(), null));

		return _searchContainer;
	}

	public boolean hasCommerceChannel() throws PortalException {
		CommerceContext commerceContext =
			(CommerceContext)_httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext == null) {
			return false;
		}

		long commerceChannelId = commerceContext.getCommerceChannelId();

		if (commerceChannelId > 0) {
			return true;
		}

		return false;
	}

	private final ActionHelper _actionHelper;
	private final CommerceAccountHelper _commerceAccountHelper;
	private CommerceAddress _commerceAddress;
	private final CommerceAddressContentPortletInstanceConfiguration
		_commerceAddressContentPortletInstanceConfiguration;
	private final CommerceAddressService _commerceAddressService;
	private final CountryService _countryService;
	private final CPRequestHelper _cpRequestHelper;
	private Long _displayStyleGroupId;
	private String _displayStyleGroupKey;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final RegionService _regionService;
	private SearchContainer<CommerceAddress> _searchContainer;

}