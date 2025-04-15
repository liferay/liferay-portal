/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.display;

import com.liferay.account.configuration.AccountEntryEmailDomainsConfiguration;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.account.service.AccountEntryUserRelLocalServiceUtil;
import com.liferay.account.service.AccountRoleLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * @author Pei-Jung Lan
 */
public class AccountUserDisplay {

	public static String getBlockedDomains(long companyId) {
		try {
			AccountEntryEmailDomainsConfiguration
				accountEntryEmailDomainsConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						AccountEntryEmailDomainsConfiguration.class, companyId);

			String[] blockedDomains = StringUtil.split(
				accountEntryEmailDomainsConfiguration.blockedEmailDomains(),
				StringPool.NEW_LINE);

			return StringUtil.merge(blockedDomains, StringPool.COMMA);
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		return StringPool.BLANK;
	}

	public static AccountUserDisplay of(User user) {
		if (user == null) {
			return null;
		}

		return new AccountUserDisplay(user);
	}

	public String getAccountEntryNamesString(
		HttpServletRequest httpServletRequest) {

		List<AccountEntryUserRel> accountEntryUserRels =
			_getAccountEntryUserRels(getUserId());

		if (ListUtil.isEmpty(accountEntryUserRels)) {
			return LanguageUtil.get(httpServletRequest, "no-assigned-account");
		}

		List<String> accountEntryNames = TransformUtil.transform(
			accountEntryUserRels,
			accountEntryUserRel -> {
				AccountEntry accountEntry =
					AccountEntryLocalServiceUtil.fetchAccountEntry(
						accountEntryUserRel.getAccountEntryId());

				if (accountEntry == null) {
					return null;
				}

				return accountEntry.getName();
			});

		return StringUtil.merge(
			ListUtil.sort(accountEntryNames), StringPool.COMMA_AND_SPACE);
	}

	public String getAccountEntryNamesStyle() {
		return _accountEntryNamesStyle;
	}

	public String getAccountRoleNamesString(long accountEntryId, Locale locale)
		throws PortalException {

		List<String> accountRoleNames = TransformUtil.transform(
			AccountRoleLocalServiceUtil.getAccountRoles(
				accountEntryId, getUserId()),
			accountRole -> {
				Role role = accountRole.getRole();

				return role.getTitle(locale);
			});

		return StringUtil.merge(
			ListUtil.sort(accountRoleNames), StringPool.COMMA_AND_SPACE);
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public String getJobTitle() {
		return _jobTitle;
	}

	public String getName() {
		return _name;
	}

	public int getStatus() {
		return _status;
	}

	public String getStatusLabel() {
		return _statusLabel;
	}

	public String getStatusLabelStyle() {
		return _statusLabelStyle;
	}

	public User getUser() {
		return _user;
	}

	public long getUserId() {
		return _userId;
	}

	public String getValidDomainsString() {
		Set<String> commonDomains = null;

		for (AccountEntryUserRel accountEntryUserRel :
				_getAccountEntryUserRels(getUserId())) {

			AccountEntry accountEntry =
				AccountEntryLocalServiceUtil.fetchAccountEntry(
					accountEntryUserRel.getAccountUserId());

			if (accountEntry == null) {
				continue;
			}

			Set<String> domains = SetUtil.fromArray(
				StringUtil.split(accountEntry.getDomains()));

			if (commonDomains == null) {
				commonDomains = domains;
			}
			else {
				commonDomains = SetUtil.intersect(commonDomains, domains);
			}

			if (commonDomains.isEmpty()) {
				return StringPool.BLANK;
			}
		}

		if (commonDomains == null) {
			return StringPool.BLANK;
		}

		return StringUtil.merge(commonDomains, StringPool.COMMA);
	}

	public boolean isValidateEmailAddress() throws PortalException {
		AccountEntryEmailDomainsConfiguration
			accountEntryEmailDomainsConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					AccountEntryEmailDomainsConfiguration.class, _companyId);

		if (!accountEntryEmailDomainsConfiguration.
				enableEmailDomainValidation()) {

			return false;
		}

		List<AccountEntryUserRel> accountEntryUserRels =
			_getAccountEntryUserRels(getUserId());

		if (accountEntryUserRels.isEmpty()) {
			return false;
		}

		if (accountEntryUserRels.size() != 1) {
			return true;
		}

		AccountEntryUserRel accountEntryUserRel = accountEntryUserRels.get(0);

		AccountEntry accountEntry =
			AccountEntryLocalServiceUtil.getAccountEntry(
				accountEntryUserRel.getAccountEntryId());

		if (Validator.isNotNull(accountEntry.getDomains()) &&
			!Objects.equals(
				accountEntry.getType(),
				AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON)) {

			return true;
		}

		return false;
	}

	private AccountUserDisplay(User user) {
		_user = user;

		_accountEntryNamesStyle = _getAccountEntryNamesStyle(user.getUserId());
		_companyId = user.getCompanyId();
		_emailAddress = user.getEmailAddress();
		_jobTitle = user.getJobTitle();
		_name = user.getFullName();
		_status = user.getStatus();
		_statusLabel = _getStatusLabel(user);
		_statusLabelStyle = _getStatusLabelStyle(user);
		_userId = user.getUserId();
	}

	private String _getAccountEntryNamesStyle(long userId) {
		if (ListUtil.isEmpty(_getAccountEntryUserRels(userId))) {
			return "font-italic text-muted";
		}

		return StringPool.BLANK;
	}

	private List<AccountEntryUserRel> _getAccountEntryUserRels(long userId) {
		List<AccountEntryUserRel> accountEntryUserRels =
			AccountEntryUserRelLocalServiceUtil.
				getAccountEntryUserRelsByAccountUserId(userId);

		return ListUtil.filter(
			accountEntryUserRels,
			accountEntryUserRel -> !Objects.equals(
				accountEntryUserRel.getAccountEntryId(),
				AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT));
	}

	private String _getStatusLabel(User user) {
		int status = user.getStatus();

		if (status == WorkflowConstants.STATUS_APPROVED) {
			return "active";
		}

		if (status == WorkflowConstants.STATUS_INACTIVE) {
			return "inactive";
		}

		return StringPool.BLANK;
	}

	private String _getStatusLabelStyle(User user) {
		String status = _getStatusLabel(user);

		if (status.equals("active")) {
			return "success";
		}

		if (status.equals("inactive")) {
			return "secondary";
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountUserDisplay.class);

	private final String _accountEntryNamesStyle;
	private final long _companyId;
	private final String _emailAddress;
	private final String _jobTitle;
	private final String _name;
	private final int _status;
	private final String _statusLabel;
	private final String _statusLabelStyle;
	private final User _user;
	private final long _userId;

}