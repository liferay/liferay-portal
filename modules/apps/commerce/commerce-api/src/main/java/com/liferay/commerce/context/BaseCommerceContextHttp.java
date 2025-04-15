/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.configuration.CommerceAccountGroupServiceConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.util.comparator.CommerceCurrencyPriorityComparator;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.discovery.CPConfigurationListDiscovery;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.util.AccountEntryAllowedTypesUtil;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class BaseCommerceContextHttp implements CommerceContext {

	public BaseCommerceContextHttp(
		HttpServletRequest httpServletRequest,
		AccountGroupLocalService accountGroupLocalService,
		CommerceAccountHelper commerceAccountHelper,
		CommerceCatalogLocalService commerceCatalogLocalService,
		CommerceChannelAccountEntryRelLocalService
			commerceChannelAccountEntryRelLocalService,
		CommerceChannelLocalService commerceChannelLocalService,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceOrderHttpHelper commerceOrderHttpHelper,
		ConfigurationProvider configurationProvider,
		CPConfigurationListDiscovery cpConfigurationListDiscovery,
		Portal portal) {

		_httpServletRequest = httpServletRequest;
		_accountGroupLocalService = accountGroupLocalService;
		_commerceAccountHelper = commerceAccountHelper;
		_commerceCatalogLocalService = commerceCatalogLocalService;
		_commerceChannelAccountEntryRelLocalService =
			commerceChannelAccountEntryRelLocalService;
		_commerceChannelLocalService = commerceChannelLocalService;
		_commerceCurrencyLocalService = commerceCurrencyLocalService;
		_commerceOrderHttpHelper = commerceOrderHttpHelper;
		_cpConfigurationListDiscovery = cpConfigurationListDiscovery;
		_portal = portal;

		try {
			CommerceChannel commerceChannel = _fetchCommerceChannel();

			if (commerceChannel != null) {
				_commerceAccountGroupServiceConfiguration =
					configurationProvider.getConfiguration(
						CommerceAccountGroupServiceConfiguration.class,
						new GroupServiceSettingsLocator(
							commerceChannel.getGroupId(),
							CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	@Override
	public AccountEntry getAccountEntry() throws PortalException {
		CommerceChannel commerceChannel = _fetchCommerceChannel();

		if ((commerceChannel == null) && (_accountEntry != null)) {
			return _accountEntry;
		}

		_accountEntry = _commerceAccountHelper.getCurrentAccountEntry(
			commerceChannel.getGroupId(), _httpServletRequest);

		return _accountEntry;
	}

	@Override
	public String[] getAccountEntryAllowedTypes() throws PortalException {
		if (_accountEntryAllowedTypes != null) {
			return _accountEntryAllowedTypes;
		}

		_accountEntryAllowedTypes =
			AccountEntryAllowedTypesUtil.getAllowedTypes(getCommerceSiteType());

		return _accountEntryAllowedTypes;
	}

	@Override
	public long[] getCommerceAccountGroupIds() throws PortalException {
		if (_commerceAccountGroupIds != null) {
			return _commerceAccountGroupIds.clone();
		}

		AccountEntry accountEntry = getAccountEntry();

		if (accountEntry == null) {
			return new long[0];
		}

		_commerceAccountGroupIds = _accountGroupLocalService.getAccountGroupIds(
			accountEntry.getAccountEntryId());

		return _commerceAccountGroupIds.clone();
	}

	@Override
	public long getCommerceChannelGroupId() throws PortalException {
		return _commerceChannelLocalService.
			getCommerceChannelGroupIdBySiteGroupId(
				_portal.getScopeGroupId(_httpServletRequest));
	}

	@Override
	public long getCommerceChannelId() throws PortalException {
		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				_portal.getScopeGroupId(_httpServletRequest));

		if (commerceChannel == null) {
			return 0;
		}

		return commerceChannel.getCommerceChannelId();
	}

	@Override
	public CommerceCurrency getCommerceCurrency() throws PortalException {
		if (_commerceCurrency != null) {
			return _commerceCurrency;
		}

		CommerceOrder commerceOrder = getCommerceOrder();

		if (commerceOrder != null) {
			return commerceOrder.getCommerceCurrency();
		}

		String commerceCurrencyCode = CookiesManagerUtil.getCookieValue(
			CommerceCurrency.class.getName() + StringPool.POUND +
				getCommerceChannelGroupId(),
			_httpServletRequest);

		if (!Validator.isBlank(commerceCurrencyCode)) {
			_commerceCurrency =
				_commerceCurrencyLocalService.fetchCommerceCurrency(
					_portal.getCompanyId(_httpServletRequest),
					commerceCurrencyCode);

			if ((_commerceCurrency != null) && _commerceCurrency.isActive()) {
				return _commerceCurrency;
			}
		}

		long commerceChannelId = 0;

		CommerceChannel commerceChannel = _fetchCommerceChannel();

		if (commerceChannel != null) {
			commerceChannelId = commerceChannel.getCommerceChannelId();
		}

		AccountEntry accountEntry = getAccountEntry();

		if (accountEntry != null) {
			CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
				_commerceChannelAccountEntryRelLocalService.
					fetchCommerceChannelAccountEntryRel(
						accountEntry.getAccountEntryId(), commerceChannelId,
						CommerceChannelAccountEntryRelConstants.TYPE_CURRENCY);

			if (commerceChannelAccountEntryRel != null) {
				CommerceCurrency commerceCurrency =
					_commerceCurrencyLocalService.getCommerceCurrency(
						commerceChannelAccountEntryRel.getClassPK());

				if (commerceCurrency.isActive()) {
					_commerceCurrency = commerceCurrency;

					return _commerceCurrency;
				}
			}
		}

		if (commerceChannel == null) {
			_commerceCurrency =
				_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
					_portal.getCompanyId(_httpServletRequest));
		}
		else {
			_commerceCurrency = _getCommerceCurrency(
				_portal.getCompanyId(_httpServletRequest),
				commerceChannel.getCommerceCurrencyCode());
		}

		return _commerceCurrency;
	}

	@Override
	public CommerceOrder getCommerceOrder() {
		try {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
					_portal.getScopeGroupId(_httpServletRequest));

			if (commerceChannel == null) {
				return null;
			}

			_commerceOrder = _commerceOrderHttpHelper.getCurrentCommerceOrder(
				_httpServletRequest);

			if (_commerceOrder != null) {
				HttpServletRequest originalHttpServletRequest =
					_portal.getOriginalServletRequest(_httpServletRequest);

				long groupId = commerceChannel.getGroupId();

				HttpSession httpSession =
					originalHttpServletRequest.getSession();

				if (_commerceOrder.isGuestOrder()) {
					httpSession.removeAttribute(
						CommerceOrder.class.getName() + StringPool.POUND +
							groupId);

					return _commerceOrder;
				}

				if (_isChannelAccountEntry(
						_commerceOrder.getCommerceAccountId(),
						getCommerceChannelId())) {

					httpSession.setAttribute(
						CommerceOrder.class.getName() + StringPool.POUND +
							groupId,
						_commerceOrder.getUuid());
				}
				else {
					httpSession.setAttribute(
						CommerceOrder.class.getName() + StringPool.POUND +
							groupId,
						StringPool.BLANK);

					_commerceOrder = null;
				}
			}

			return _commerceOrder;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	@Override
	public int getCommerceSiteType() {
		if (_commerceAccountGroupServiceConfiguration == null) {
			return CommerceChannelConstants.SITE_TYPE_B2C;
		}

		return _commerceAccountGroupServiceConfiguration.commerceSiteType();
	}

	@Override
	public long getCPConfigurationListId(long groupId) throws PortalException {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-10889")) {
			return 0;
		}

		Map<Long, CPConfigurationList> cpConfigurationLists =
			_getCPConfigurationLists();

		CPConfigurationList cpConfigurationList = cpConfigurationLists.get(
			groupId);

		return cpConfigurationList.getCPConfigurationListId();
	}

	@Override
	public long[] getCPConfigurationListIds() throws PortalException {
		Map<Long, CPConfigurationList> cpConfigurationLists =
			_getCPConfigurationLists();

		return TransformUtil.transformToLongArray(
			cpConfigurationLists.values(),
			CPConfigurationList::getCPConfigurationListId);
	}

	private CommerceChannel _fetchCommerceChannel() throws PortalException {
		return _commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
			_portal.getScopeGroupId(_httpServletRequest));
	}

	private CommerceCurrency _getCommerceCurrency(
		long companyId, String currencyCode) {

		CommerceCurrency commerceCurrency = null;

		try {
			commerceCurrency =
				_commerceCurrencyLocalService.getCommerceCurrency(
					companyId, currencyCode);
		}
		catch (NoSuchCurrencyException noSuchCurrencyException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchCurrencyException);
			}
		}

		if ((commerceCurrency != null) && commerceCurrency.isActive()) {
			return commerceCurrency;
		}

		commerceCurrency =
			_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
				companyId);

		if (commerceCurrency != null) {
			return commerceCurrency;
		}

		List<CommerceCurrency> commerceCurrencies =
			_commerceCurrencyLocalService.getCommerceCurrencies(
				companyId, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				CommerceCurrencyPriorityComparator.getInstance(true));

		if (!commerceCurrencies.isEmpty()) {
			commerceCurrency = commerceCurrencies.get(0);
		}

		return commerceCurrency;
	}

	private Map<Long, CPConfigurationList> _getCPConfigurationLists()
		throws PortalException {

		if (MapUtil.isNotEmpty(_cpConfigurationLists)) {
			return _cpConfigurationLists;
		}

		_cpConfigurationLists = new HashMap<>();

		long orderTypeId = 0;

		CommerceOrder commerceOrder = getCommerceOrder();

		if (commerceOrder != null) {
			orderTypeId = commerceOrder.getCommerceOrderTypeId();
		}

		for (long groupId :
				TransformUtil.transformToLongArray(
					_commerceCatalogLocalService.getCommerceCatalogs(
						_portal.getCompanyId(_httpServletRequest)),
					CommerceCatalog::getGroupId)) {

			_cpConfigurationLists.put(
				groupId,
				_cpConfigurationListDiscovery.getCPConfigurationList(
					_portal.getCompanyId(_httpServletRequest), groupId,
					CommerceUtil.getCommerceAccountId(this),
					getCommerceChannelId(), orderTypeId));
		}

		return _cpConfigurationLists;
	}

	private boolean _isChannelAccountEntry(
		long accountEntryId, long commerceChannelId) {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelLocalService.
				fetchCommerceChannelAccountEntryRel(
					accountEntryId, commerceChannelId,
					CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		int count =
			_commerceChannelAccountEntryRelLocalService.
				getCommerceChannelAccountEntryRelsCount(
					commerceChannelId, null,
					CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);

		if ((commerceChannelAccountEntryRel != null) || (count == 0)) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseCommerceContextHttp.class);

	private AccountEntry _accountEntry;
	private String[] _accountEntryAllowedTypes;
	private final AccountGroupLocalService _accountGroupLocalService;
	private long[] _commerceAccountGroupIds;
	private CommerceAccountGroupServiceConfiguration
		_commerceAccountGroupServiceConfiguration;
	private final CommerceAccountHelper _commerceAccountHelper;
	private final CommerceCatalogLocalService _commerceCatalogLocalService;
	private final CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;
	private final CommerceChannelLocalService _commerceChannelLocalService;
	private CommerceCurrency _commerceCurrency;
	private final CommerceCurrencyLocalService _commerceCurrencyLocalService;
	private CommerceOrder _commerceOrder;
	private final CommerceOrderHttpHelper _commerceOrderHttpHelper;
	private final CPConfigurationListDiscovery _cpConfigurationListDiscovery;
	private Map<Long, CPConfigurationList> _cpConfigurationLists;
	private final HttpServletRequest _httpServletRequest;
	private final Portal _portal;

}