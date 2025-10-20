/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.helper;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.exception.AccountEntryTypeException;
import com.liferay.account.manager.CurrentAccountEntryManager;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryModel;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.role.AccountRolePermissionThreadLocal;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.commerce.configuration.CommerceAccountGroupServiceConfiguration;
import com.liferay.commerce.configuration.CommerceAccountServiceConfiguration;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.util.CommerceGroupThreadLocal;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(service = CommerceAccountHelper.class)
public class CommerceAccountHelperImpl implements CommerceAccountHelper {

	@Override
	public AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long userId, long[] roleIds,
			ServiceContext serviceContext)
		throws PortalException {

		AccountEntryUserRel accountEntryUserRel = addAccountEntryUserRel(
			accountEntryId, userId, serviceContext);

		if (roleIds != null) {
			AccountEntry accountEntry =
				_accountEntryLocalService.getAccountEntry(accountEntryId);

			Group group = accountEntry.getAccountEntryGroup();

			if (group == null) {
				throw new PortalException();
			}

			_userGroupRoleLocalService.addUserGroupRoles(
				userId, group.getGroupId(), roleIds);
		}

		return accountEntryUserRel;
	}

	@Override
	public AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long userId, ServiceContext serviceContext)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			accountEntryId);

		if (accountEntry.isPersonalAccount()) {
			List<AccountEntryUserRel> accountEntryUserRels =
				_accountEntryUserRelLocalService.
					getAccountEntryUserRelsByAccountUserId(userId);

			for (AccountEntryUserRel accountEntryUserRel :
					accountEntryUserRels) {

				AccountEntry curAccountEntry =
					_accountEntryLocalService.getAccountEntry(
						accountEntryUserRel.getAccountEntryId());

				if (curAccountEntry.isPersonalAccount()) {
					throw new AccountEntryTypeException();
				}
			}
		}

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelLocalService.addAccountEntryUserRel(
				accountEntryId, userId);

		addDefaultRoles(userId);

		return accountEntryUserRel;
	}

	@Override
	public void addAccountEntryUserRels(
			long accountEntryId, long[] userIds, String[] emailAddresses,
			long[] roleIds, ServiceContext serviceContext)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			accountEntryId);

		Group group = accountEntry.getAccountEntryGroup();

		if (group == null) {
			throw new PortalException();
		}

		if (userIds != null) {
			for (long userId : userIds) {
				User user = _userLocalService.getUser(userId);

				addAccountEntryUserRel(
					accountEntryId, user.getUserId(), serviceContext);

				if (!ArrayUtil.contains(
						user.getGroupIds(), group.getGroupId())) {

					_userLocalService.addGroupUsers(
						group.getGroupId(), new long[] {userId});
				}

				if (!ArrayUtil.contains(
						user.getGroupIds(), serviceContext.getScopeGroupId())) {

					_userLocalService.addGroupUsers(
						serviceContext.getScopeGroupId(), new long[] {userId});
				}

				if (roleIds != null) {
					_userGroupRoleLocalService.addUserGroupRoles(
						user.getUserId(), group.getGroupId(), roleIds);
				}
			}
		}

		if (emailAddresses != null) {
			for (String emailAddress : emailAddresses) {
				_accountEntryUserRelLocalService.inviteUser(
					accountEntryId, roleIds, emailAddress,
					_userLocalService.getUser(accountEntry.getUserId()),
					serviceContext);
			}
		}
	}

	@Override
	public void addDefaultRoles(long userId) throws PortalException {
		CommerceAccountServiceConfiguration
			commerceAccountServiceConfiguration =
				_configurationProvider.getSystemConfiguration(
					CommerceAccountServiceConfiguration.class);

		String[] siteRoles = commerceAccountServiceConfiguration.siteRoles();

		if ((siteRoles == null) && ArrayUtil.isEmpty(siteRoles)) {
			return;
		}

		User user = _userLocalService.getUser(userId);

		Set<Role> roles = new HashSet<>();

		for (String siteRole : siteRoles) {
			Role role = _roleLocalService.fetchRole(
				user.getCompanyId(), siteRole);

			if ((role == null) || (role.getType() != RoleConstants.TYPE_SITE)) {
				continue;
			}

			roles.add(role);
		}

		long[] roleIds = TransformUtil.transformToLongArray(
			roles, Role::getRoleId);

		for (AccountEntryUserRel accountEntryUserRel :
				_accountEntryUserRelLocalService.
					getAccountEntryUserRelsByAccountUserId(userId)) {

			AccountEntry accountEntry =
				_accountEntryLocalService.getAccountEntry(
					accountEntryUserRel.getAccountEntryId());

			_userGroupRoleLocalService.addUserGroupRoles(
				userId, accountEntry.getAccountEntryGroupId(), roleIds);
		}
	}

	@Override
	public int countUserCommerceAccounts(
			long userId, long commerceChannelGroupId)
		throws PortalException {

		return _accountEntryLocalService.getUserAccountEntriesCount(
			userId, AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			StringPool.BLANK, getAccountEntryTypes(commerceChannelGroupId),
			WorkflowConstants.STATUS_ANY);
	}

	@Override
	public String[] getAccountEntryTypes(long commerceChannelGroupId)
		throws ConfigurationException {

		return toAccountEntryTypes(getCommerceSiteType(commerceChannelGroupId));
	}

	@Override
	public String getAccountManagementPortletURL(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		long groupId = _portal.getScopeGroupId(httpServletRequest);

		long plid = _portal.getPlidFromPortletId(
			groupId, AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT);

		if (plid > 0) {
			PortletURL portletURL = _portletURLFactory.create(
				httpServletRequest,
				AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT, plid,
				PortletRequest.RENDER_PHASE);

			return portletURL.toString();
		}

		return StringPool.BLANK;
	}

	@Override
	public int getCommerceSiteType(long commerceChannelGroupId)
		throws ConfigurationException {

		CommerceAccountGroupServiceConfiguration
			commerceAccountGroupServiceConfiguration =
				_configurationProvider.getConfiguration(
					CommerceAccountGroupServiceConfiguration.class,
					new GroupServiceSettingsLocator(
						commerceChannelGroupId,
						CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

		return commerceAccountGroupServiceConfiguration.commerceSiteType();
	}

	@Override
	public AccountEntry getCurrentAccountEntry(
			long commerceChannelGroupId, HttpServletRequest httpServletRequest)
		throws PortalException {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		CommerceOrder commerceOrder = (CommerceOrder)httpSession.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER_ON_ACCOUNT_SELECTION);

		if (commerceOrder != null) {
			setCurrentCommerceAccount(
				httpServletRequest, commerceChannelGroupId,
				AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT);

			return null;
		}

		int commerceSiteType = getCommerceSiteType(commerceChannelGroupId);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceChannelGroupId);
		long userId = _portal.getUserId(httpServletRequest);

		AccountEntry accountEntry =
			_currentAccountEntryManager.getCurrentAccountEntry(
				commerceChannel.getSiteGroupId(), userId);

		if (((accountEntry == null) ||
			 (accountEntry.getStatus() != WorkflowConstants.STATUS_APPROVED)) &&
			((commerceSiteType == CommerceChannelConstants.SITE_TYPE_B2C) ||
			 (commerceSiteType == CommerceChannelConstants.SITE_TYPE_B2X))) {

			accountEntry = _accountEntryLocalService.fetchPersonAccountEntry(
				userId);

			if (accountEntry == null) {
				accountEntry =
					_accountEntryLocalService.fetchSupplierAccountEntry(userId);

				if (accountEntry == null) {
					User user = _userLocalService.getUser(userId);

					if (user.isGuestUser()) {
						accountEntry =
							_accountEntryLocalService.getGuestAccountEntry(
								commerceChannel.getCompanyId());
					}
					else {
						ServiceContext serviceContext = new ServiceContext();

						serviceContext.setCompanyId(user.getCompanyId());
						serviceContext.setUserId(userId);

						accountEntry =
							_accountEntryLocalService.addAccountEntry(
								StringPool.BLANK, userId,
								AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
								user.getFullName(), null, null,
								user.getEmailAddress(), null, StringPool.BLANK,
								AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON,
								WorkflowConstants.STATUS_APPROVED,
								serviceContext);

						addAccountEntryUserRel(
							accountEntry.getAccountEntryId(), userId,
							serviceContext);
					}
				}
			}
		}

		if (accountEntry != null) {
			if (accountEntry.isGuestAccount()) {
				setCurrentCommerceAccount(
					httpServletRequest, commerceChannelGroupId,
					AccountConstants.ACCOUNT_ENTRY_ID_GUEST);

				return accountEntry;
			}

			CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
				_commerceChannelAccountEntryRelLocalService.
					fetchCommerceChannelAccountEntryRel(
						accountEntry.getAccountEntryId(),
						commerceChannel.getCommerceChannelId(),
						CommerceChannelAccountEntryRelConstants.TYPE_USER);

			if (commerceChannelAccountEntryRel != null) {
				setCurrentCommerceAccount(
					httpServletRequest, commerceChannelGroupId,
					commerceChannelAccountEntryRel.getAccountEntryId());

				return accountEntry;
			}

			commerceChannelAccountEntryRel =
				_commerceChannelAccountEntryRelLocalService.
					fetchCommerceChannelAccountEntryRel(
						accountEntry.getAccountEntryId(),
						commerceChannel.getCommerceChannelId(),
						CommerceChannelAccountEntryRelConstants.
							TYPE_ELIGIBILITY);

			if (commerceChannelAccountEntryRel != null) {
				setCurrentCommerceAccount(
					httpServletRequest, commerceChannelGroupId,
					commerceChannelAccountEntryRel.getAccountEntryId());

				return accountEntry;
			}

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			int count =
				_commerceChannelAccountEntryRelLocalService.
					getCommerceChannelAccountEntryRelsCount(
						commerceChannel.getCommerceChannelId(), null,
						CommerceChannelAccountEntryRelConstants.
							TYPE_ELIGIBILITY);

			if (permissionChecker.hasPermission(
					commerceChannelGroupId, AccountEntry.class.getName(),
					commerceChannel.getCompanyId(), ActionKeys.VIEW) &&
				(count == 0)) {

				setCurrentCommerceAccount(
					httpServletRequest, commerceChannelGroupId,
					accountEntry.getAccountEntryId());

				return accountEntry;
			}

			List<AccountEntry> commerceChannelAccountEntries =
				_getCommerceChannelAccountEntries(
					userId, commerceChannel.getCommerceChannelId());

			for (AccountEntry commerceChannelAccountEntry :
					commerceChannelAccountEntries) {

				if (accountEntry.getAccountEntryId() ==
						commerceChannelAccountEntry.getAccountEntryId()) {

					setCurrentCommerceAccount(
						httpServletRequest, commerceChannelGroupId,
						accountEntry.getAccountEntryId());

					return accountEntry;
				}
			}

			if (!commerceChannelAccountEntries.isEmpty()) {
				accountEntry = commerceChannelAccountEntries.get(0);

				setCurrentCommerceAccount(
					httpServletRequest, commerceChannelGroupId,
					accountEntry.getAccountEntryId());
			}
			else {
				setCurrentCommerceAccount(
					httpServletRequest, commerceChannelGroupId,
					AccountConstants.ACCOUNT_ENTRY_ID_GUEST);

				if (!accountEntry.isGuestAccount()) {
					return null;
				}
			}
		}
		else {
			setCurrentCommerceAccount(
				httpServletRequest, commerceChannelGroupId,
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST);

			return null;
		}

		return accountEntry;
	}

	@Override
	public long[] getUserCommerceAccountIds(
			long userId, long commerceChannelGroupId)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceChannelGroupId);

		List<AccountEntry> accountEntries =
			_accountEntryLocalService.getUserAccountEntries(
				userId, AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK, getAccountEntryTypes(commerceChannelGroupId),
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		return ListUtil.toLongArray(
			_filterAccountEntries(
				accountEntries, commerceChannel.getCommerceChannelId()),
			AccountEntryModel::getAccountEntryId);
	}

	@Override
	public void setCurrentCommerceAccount(
			HttpServletRequest httpServletRequest, long commerceChannelGroupId,
			long commerceAccountId)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceChannelGroupId);

		if ((commerceAccountId > 0) &&
			(commerceAccountId !=
				AccountRolePermissionThreadLocal.getAccountEntryId())) {

			Group group = CommerceGroupThreadLocal.get();

			if (group == null) {
				CommerceGroupThreadLocal.setCommerceGroupWithSafeCloseable(
					commerceChannel.getGroupId());
			}

			AccountEntry accountEntry = _accountEntryService.getAccountEntry(
				commerceAccountId);

			_checkAccountType(
				commerceChannelGroupId, accountEntry.getAccountEntryId());
		}

		AccountRolePermissionThreadLocal.setAccountEntryIdWithSafeCloseable(
			commerceAccountId);

		if (PortalSessionThreadLocal.getHttpSession() == null) {
			PortalSessionThreadLocal.setHttpSession(
				httpServletRequest.getSession());
		}

		long userId = _portal.getUserId(httpServletRequest);

		_currentAccountEntryManager.setCurrentAccountEntry(
			commerceAccountId, commerceChannel.getGroupId(), userId);
		_currentAccountEntryManager.setCurrentAccountEntry(
			commerceAccountId, commerceChannel.getSiteGroupId(), userId);
	}

	@Override
	public Integer toAccountEntryStatus(Boolean commerceAccountActive) {
		if (commerceAccountActive == null) {
			return WorkflowConstants.STATUS_ANY;
		}

		if (commerceAccountActive) {
			return WorkflowConstants.STATUS_APPROVED;
		}

		return WorkflowConstants.STATUS_INACTIVE;
	}

	@Override
	public String[] toAccountEntryTypes(int commerceSiteType) {
		if (commerceSiteType == CommerceChannelConstants.SITE_TYPE_B2B) {
			return new String[] {AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS};
		}
		else if (commerceSiteType == CommerceChannelConstants.SITE_TYPE_B2C) {
			return new String[] {AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON};
		}
		else if (commerceSiteType == CommerceChannelConstants.SITE_TYPE_B2X) {
			return new String[] {
				AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
				AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON
			};
		}

		return null;
	}

	private void _checkAccountType(
			long commerceChannelGroupId, long commerceAccountId)
		throws PortalException {

		int commerceSiteType = getCommerceSiteType(commerceChannelGroupId);

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			commerceAccountId);

		if ((commerceSiteType == CommerceChannelConstants.SITE_TYPE_B2C) &&
			accountEntry.isBusinessAccount()) {

			throw new PortalException(
				"Only personal accounts are allowed in a b2c site");
		}

		if ((commerceSiteType == CommerceChannelConstants.SITE_TYPE_B2B) &&
			accountEntry.isPersonalAccount()) {

			throw new PortalException(
				"Only business accounts are allowed in a b2b site");
		}
	}

	private List<AccountEntry> _filterAccountEntries(
		List<AccountEntry> accountEntries, long commerceChannelId) {

		List<CommerceChannelAccountEntryRel> commerceChannelAccountEntryRels =
			_commerceChannelAccountEntryRelLocalService.
				getCommerceChannelAccountEntryRels(
					commerceChannelId, null,
					CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		if (commerceChannelAccountEntryRels.isEmpty()) {
			return accountEntries;
		}

		Set<Long> channelAccountEntryIds = new HashSet<>(
			ListUtil.toList(
				commerceChannelAccountEntryRels,
				CommerceChannelAccountEntryRel::getAccountEntryId));

		return TransformUtil.transform(
			accountEntries,
			accountEntry -> {
				if (channelAccountEntryIds.contains(
						accountEntry.getAccountEntryId())) {

					return accountEntry;
				}

				return null;
			});
	}

	private List<AccountEntry> _getCommerceChannelAccountEntries(
			long userId, long commerceChannelId)
		throws PortalException {

		List<AccountEntry> accountEntries =
			_accountEntryLocalService.getUserAccountEntries(
				userId, AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT, null,
				new String[] {
					AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
					AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST,
					AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON,
					AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER
				},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		return _filterAccountEntries(accountEntries, commerceChannelId);
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CurrentAccountEntryManager _currentAccountEntryManager;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}