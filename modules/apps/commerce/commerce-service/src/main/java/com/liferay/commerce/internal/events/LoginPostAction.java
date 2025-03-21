/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.events;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.commerce.configuration.CommerceAccountServiceConfiguration;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Gianmarco Brunialti Masera
 */
@Component(property = "key=login.events.post", service = LifecycleAction.class)
public class LoginPostAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			_addDefaultAccountRoles(httpServletRequest);

			_run(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private void _addBusinessAccountRoles(
		AccountEntry accountEntry, User user) {

		try {
			Role accountAdmnistratorRole = _roleLocalService.getRole(
				user.getCompanyId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);
			Role accountBuyerRole = _roleLocalService.getRole(
				user.getCompanyId(),
				AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER);

			AccountRole accountAdmnistratorAccountRole =
				_accountRoleLocalService.getAccountRoleByRoleId(
					accountAdmnistratorRole.getRoleId());
			AccountRole accountBuyerAccountRole =
				_accountRoleLocalService.getAccountRoleByRoleId(
					accountBuyerRole.getRoleId());

			_accountRoleLocalService.associateUser(
				accountEntry.getAccountEntryId(),
				new long[] {
					accountAdmnistratorAccountRole.getAccountRoleId(),
					accountBuyerAccountRole.getAccountRoleId()
				},
				user.getUserId());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
	}

	private void _addDefaultAccountRoles(HttpServletRequest httpServletRequest)
		throws Exception {

		CommerceAccountServiceConfiguration
			commerceAccountServiceConfiguration =
				_configurationProvider.getSystemConfiguration(
					CommerceAccountServiceConfiguration.class);

		if (commerceAccountServiceConfiguration.
				applyDefaultRoleToExistingUsers()) {

			_commerceAccountHelper.addDefaultRoles(
				_portal.getUserId(httpServletRequest));
		}
	}

	private void _associateAccountEntryToCommerceOrder(
			AccountEntry accountEntry, long commerceChannelGroupId,
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest,
			long userId)
		throws PortalException {

		CommerceOrder userCommerceOrder =
			_commerceOrderLocalService.fetchCommerceOrder(
				accountEntry.getAccountEntryId(), commerceChannelGroupId,
				userId, CommerceOrderConstants.ORDER_STATUS_OPEN);

		if (userCommerceOrder != null) {
			CommerceContext commerceContext = _commerceContextFactory.create(
				accountEntry.getAccountEntryId(), commerceChannelGroupId, null,
				userCommerceOrder.getCommerceOrderId(),
				_portal.getCompanyId(httpServletRequest));

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(
					_portal.getUser(httpServletRequest)));

			_commerceOrderLocalService.mergeGuestCommerceOrder(
				userId, commerceOrder.getCommerceOrderId(),
				userCommerceOrder.getCommerceOrderId(), commerceContext,
				ServiceContextFactory.getInstance(httpServletRequest));
		}
		else {
			_commerceOrderLocalService.updateAccount(
				commerceOrder.getCommerceOrderId(), userId,
				accountEntry.getAccountEntryId());
		}
	}

	private AccountEntry _createAccountEntry(
			String name, String type, User user)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setUserId(user.getUserId());

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			user.getUserId(), AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			name, null, null, user.getEmailAddress(), null, StringPool.BLANK,
			type, WorkflowConstants.STATUS_APPROVED, serviceContext);

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());

		if (type.equals(AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS)) {
			_addBusinessAccountRoles(accountEntry, user);
		}

		return accountEntry;
	}

	private AccountEntry _getAccountEntry(int commerceSiteType, long userId) {
		AccountEntry accountEntry = null;

		String[] accountEntryTypes = {
			AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON
		};

		if (commerceSiteType == CommerceChannelConstants.SITE_TYPE_B2B) {
			accountEntryTypes = new String[] {
				AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS
			};
		}
		else if (commerceSiteType == CommerceChannelConstants.SITE_TYPE_B2X) {
			accountEntryTypes = new String[] {
				AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
				AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON
			};
		}

		try {
			List<AccountEntry> accountEntries =
				_accountEntryLocalService.getUserAccountEntries(
					userId, null, null, accountEntryTypes, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			if (accountEntries.size() != 1) {
				return null;
			}

			accountEntry = accountEntries.get(0);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return accountEntry;
	}

	private long _getCommerceChannelGroupId(String key) {
		return GetterUtil.getLong(
			StringUtil.extractLast(key, StringPool.POUND));
	}

	private CommerceOrder _getCommerceOrderByUuidAndGroupId(
		long commerceChannelGroupId, String commerceOrderUuid) {

		try {
			return _commerceOrderLocalService.getCommerceOrderByUuidAndGroupId(
				commerceOrderUuid, commerceChannelGroupId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private Map<String, String> _parseAccountEntryInformation(
		int commerceSiteType, String cookieValue, User user) {

		String accountEntryName = user.getFullName();
		String accountEntryType = AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON;
		String userEmailAddress = user.getEmailAddress();

		String[] keyValues = cookieValue.split(StringPool.POUND);

		for (String keyValue : keyValues) {
			if (keyValue.startsWith("accountEntryName=")) {
				accountEntryName = StringUtil.extractLast(
					keyValue, StringPool.EQUAL);
			}
			else if (keyValue.startsWith("accountEntryType=")) {
				String value = StringUtil.extractLast(
					keyValue, StringPool.EQUAL);

				if (value.equals(
						AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS) ||
					value.equals(AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON)) {

					accountEntryType = value;
				}
			}
			else if (keyValue.startsWith("userEmailAddress=")) {
				userEmailAddress = StringUtil.extractLast(
					keyValue, StringPool.EQUAL);
			}
		}

		if (commerceSiteType == CommerceChannelConstants.SITE_TYPE_B2B) {
			accountEntryType = AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS;
		}
		else if (commerceSiteType == CommerceChannelConstants.SITE_TYPE_B2C) {
			accountEntryType = AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON;
		}

		return HashMapBuilder.put(
			"accountEntryName", accountEntryName
		).put(
			"accountEntryType", accountEntryType
		).put(
			"userEmailAddress", userEmailAddress
		).build();
	}

	private void _prepareOrderForCheckout(
		CommerceOrder commerceOrder, HttpServletRequest httpServletRequest) {

		httpServletRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		httpSession.setAttribute(
			CommerceCheckoutWebKeys.SUFFIX_IMMEDIATE_CHECKOUT, Boolean.TRUE);
	}

	private void _run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		Cookie[] cookies = httpServletRequest.getCookies();

		if (cookies == null) {
			return;
		}

		AccountEntry accountEntry = null;
		CommerceOrder commerceOrder = null;
		boolean immediateCheckout = false;

		User user = _portal.getUser(httpServletRequest);

		for (Cookie cookie : cookies) {
			String cookieName = cookie.getName();

			if (cookieName.startsWith(_COOKIE_NAME_PREFIX_ACCOUNT_ENTRY)) {
				Map<String, String> accountEntryInformation =
					_parseAccountEntryInformation(
						_commerceAccountHelper.getCommerceSiteType(
							_getCommerceChannelGroupId(cookieName)),
						cookie.getValue(), user);

				String userEmailAddress = accountEntryInformation.get(
					"userEmailAddress");

				if (userEmailAddress.equals(user.getEmailAddress())) {
					String accountEntryName = accountEntryInformation.get(
						"accountEntryName");
					String accountEntryType = accountEntryInformation.get(
						"accountEntryType");

					accountEntry = _createAccountEntry(
						accountEntryName, accountEntryType, user);
				}

				CookiesManagerUtil.deleteCookies(
					cookie.getDomain(), httpServletRequest, httpServletResponse,
					cookieName);
			}
			else if (cookieName.startsWith(
						_COOKIE_NAME_PREFIX_COMMERCE_ORDER)) {

				long commerceChannelGroupId = _getCommerceChannelGroupId(
					cookieName);

				String commerceOrderUuid = cookie.getValue();

				if (commerceOrderUuid.endsWith(
						CommerceCheckoutWebKeys.SUFFIX_IMMEDIATE_CHECKOUT)) {

					commerceOrderUuid = StringUtil.removeSubstring(
						commerceOrderUuid,
						CommerceCheckoutWebKeys.SUFFIX_IMMEDIATE_CHECKOUT);
					immediateCheckout = true;
				}

				commerceOrder = _getCommerceOrderByUuidAndGroupId(
					commerceChannelGroupId, commerceOrderUuid);

				if ((commerceOrder != null) && immediateCheckout) {
					_prepareOrderForCheckout(commerceOrder, httpServletRequest);
				}
			}
		}

		if ((accountEntry == null) && (commerceOrder != null)) {
			accountEntry = _getAccountEntry(
				_commerceAccountHelper.getCommerceSiteType(
					commerceOrder.getGroupId()),
				user.getUserId());

			if (accountEntry == null) {
				commerceOrder.setUserId(user.getUserId());

				commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
					commerceOrder);

				HttpServletRequest originalHttpServletRequest =
					_portal.getOriginalServletRequest(httpServletRequest);

				HttpSession httpSession =
					originalHttpServletRequest.getSession();

				httpSession.setAttribute(
					CommerceCheckoutWebKeys.COMMERCE_ORDER_ON_ACCOUNT_SELECTION,
					commerceOrder);

				httpSession.setAttribute(
					_COOKIE_NAME_PREFIX_COMMERCE_ORDER +
						commerceOrder.getGroupId(),
					commerceOrder.getUuid());
			}
		}

		if ((accountEntry != null) && (commerceOrder != null)) {
			_associateAccountEntryToCommerceOrder(
				accountEntry, commerceOrder.getGroupId(), commerceOrder,
				httpServletRequest, user.getUserId());
		}
	}

	private static final String _COOKIE_NAME_PREFIX_ACCOUNT_ENTRY =
		AccountEntry.class.getName() + StringPool.POUND;

	private static final String _COOKIE_NAME_PREFIX_COMMERCE_ORDER =
		CommerceOrder.class.getName() + StringPool.POUND;

	private static final Log _log = LogFactoryUtil.getLog(
		LoginPostAction.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

}