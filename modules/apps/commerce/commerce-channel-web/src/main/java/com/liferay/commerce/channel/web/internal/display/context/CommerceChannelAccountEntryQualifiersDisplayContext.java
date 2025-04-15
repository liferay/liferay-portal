/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.display.context;

import com.liferay.commerce.configuration.CommerceAccountGroupServiceConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Danny Situ
 */
public class CommerceChannelAccountEntryQualifiersDisplayContext {

	public CommerceChannelAccountEntryQualifiersDisplayContext(
		CommerceChannelAccountEntryRelService
			commerceChannelAccountEntryRelService,
		CommerceChannelLocalService commerceChannelLocalService,
		ConfigurationProvider configurationProvider,
		HttpServletRequest httpServletRequest) {

		_commerceChannelAccountEntryRelService =
			commerceChannelAccountEntryRelService;
		_commerceChannelLocalService = commerceChannelLocalService;
		_configurationProvider = configurationProvider;
		_httpServletRequest = httpServletRequest;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);
	}

	public String getActiveAccountEligibility() throws PortalException {
		long count =
			_commerceChannelAccountEntryRelService.
				getCommerceChannelAccountEntryRelsCount(
					getCommerceChannelId(), null,
					CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		if (count > 0) {
			return "accounts";
		}

		return "all";
	}

	public CommerceChannel getCommerceChannel() throws PortalException {
		if (_commerceChannel != null) {
			return _commerceChannel;
		}

		long commerceChannelId = ParamUtil.getLong(
			_cpRequestHelper.getRenderRequest(), "commerceChannelId");

		if (commerceChannelId > 0) {
			_commerceChannel = _commerceChannelLocalService.getCommerceChannel(
				commerceChannelId);
		}

		return _commerceChannel;
	}

	public String getCommerceChannelAccountEntriesAPIURL()
		throws PortalException {

		return "/o/headless-commerce-admin-channel/v1.0/channels/" +
			getCommerceChannelId() + "/channel-accounts?nestedFields=account";
	}

	public List<FDSActionDropdownItem>
			getCommerceChannelAccountEntryFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionTemplates(false);
	}

	public long getCommerceChannelId() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		if (commerceChannel == null) {
			return 0;
		}

		return commerceChannel.getCommerceChannelId();
	}

	public int getCommerceSiteType() throws PortalException {
		CommerceAccountGroupServiceConfiguration
			commerceAccountGroupServiceConfiguration =
				_getCommerceAccountGroupServiceConfiguration();

		return commerceAccountGroupServiceConfiguration.commerceSiteType();
	}

	protected List<FDSActionDropdownItem> getFDSActionTemplates(
		boolean sidePanel) {

		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		FDSActionDropdownItem fdsActionDropdownItem = new FDSActionDropdownItem(
			null, "trash", "remove",
			LanguageUtil.get(_httpServletRequest, "remove"), "delete", "delete",
			"headless");

		if (sidePanel) {
			fdsActionDropdownItem.setTarget("sidePanel");
		}

		fdsActionDropdownItems.add(fdsActionDropdownItem);

		return fdsActionDropdownItems;
	}

	private CommerceAccountGroupServiceConfiguration
			_getCommerceAccountGroupServiceConfiguration()
		throws PortalException {

		if (_commerceAccountGroupServiceConfiguration != null) {
			return _commerceAccountGroupServiceConfiguration;
		}

		CommerceChannel commerceChannel = getCommerceChannel();

		_commerceAccountGroupServiceConfiguration =
			_configurationProvider.getConfiguration(
				CommerceAccountGroupServiceConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

		return _commerceAccountGroupServiceConfiguration;
	}

	private CommerceAccountGroupServiceConfiguration
		_commerceAccountGroupServiceConfiguration;
	private CommerceChannel _commerceChannel;
	private final CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;
	private final CommerceChannelLocalService _commerceChannelLocalService;
	private final ConfigurationProvider _configurationProvider;
	private final CPRequestHelper _cpRequestHelper;
	private final HttpServletRequest _httpServletRequest;

}