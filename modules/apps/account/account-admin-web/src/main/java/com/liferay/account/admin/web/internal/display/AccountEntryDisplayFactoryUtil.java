/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.display;

import com.liferay.account.admin.web.internal.util.AccountEntryEmailAddressValidatorFactoryUtil;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.account.validator.AccountEntryEmailAddressValidator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Drew Brokke
 * @author Pei-Jung Lan
 */
public class AccountEntryDisplayFactoryUtil {

	public static AccountEntryDisplay create(
		AccountEntry accountEntry, PortletRequest portletRequest) {

		if (accountEntry == null) {
			accountEntry = AccountEntryLocalServiceUtil.createAccountEntry(0L);

			accountEntry.setRestrictMembership(true);
			accountEntry.setType(AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);
			accountEntry.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		AccountEntryDisplay accountEntryDisplayAlt = new AccountEntryDisplay(
			accountEntry);

		String defaultLogoURL =
			PortalUtil.getPathContext(portletRequest) +
				"/account_entries_admin/icons/briefcase.svg";

		accountEntryDisplayAlt.setDefaultLogoURL(defaultLogoURL);

		AccountEntryEmailAddressValidator accountEntryEmailAddressValidator =
			AccountEntryEmailAddressValidatorFactoryUtil.create(
				accountEntry.getCompanyId(), new String[0]);

		accountEntryDisplayAlt.setEmailAddressDomainValidationEnabled(
			accountEntryEmailAddressValidator.
				isEmailAddressDomainValidationEnabled());

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		accountEntryDisplayAlt.setLogoURL(defaultLogoURL);

		if (accountEntry.getLogoId() > 0) {
			accountEntryDisplayAlt.setLogoURL(
				StringBundler.concat(
					themeDisplay.getPathImage(), "/account_entry_logo?img_id=",
					accountEntry.getLogoId(), "&t=",
					WebServerServletTokenUtil.getToken(
						accountEntry.getLogoId())));
		}

		accountEntryDisplayAlt.setOrganizationNames(
			_getOrganizationNames(accountEntry, themeDisplay.getLocale()));
		accountEntryDisplayAlt.setPersonAccountEntryUser(
			_getPersonAccountEntryUser(accountEntry));
		accountEntryDisplayAlt.setStatusLabel(
			WorkflowConstants.getStatusLabel(accountEntry.getStatus()));

		if (accountEntry.isApproved()) {
			accountEntryDisplayAlt.setStatusLabel("active");
		}

		accountEntryDisplayAlt.setStatusLabelStyle(
			WorkflowConstants.getStatusStyle(accountEntry.getStatus()));

		if (accountEntryEmailAddressValidator.
				isEmailAddressDomainValidationEnabled() &&
			ArrayUtil.isNotEmpty(accountEntry.getDomainsArray())) {

			accountEntryDisplayAlt.setValidateUserEmailAddress(true);
		}

		return accountEntryDisplayAlt;
	}

	public static AccountEntryDisplay create(
		long accountEntryId, HttpServletRequest httpServletRequest) {

		return create(
			accountEntryId,
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST));
	}

	public static AccountEntryDisplay create(
		long accountEntryId, PortletRequest portletRequest) {

		return create(
			AccountEntryLocalServiceUtil.fetchAccountEntry(accountEntryId),
			portletRequest);
	}

	private static String _getOrganizationNames(
		AccountEntry accountEntry, Locale locale) {

		StringBundler sb = new StringBundler(4);

		List<Organization> organizations = accountEntry.fetchOrganizations();

		List<String> names = new ArrayList<>();

		for (Organization organization : organizations) {
			if (names.size() == _ORGANIZATION_NAMES_LIMIT) {
				break;
			}

			names.add(organization.getName());
		}

		sb.append(StringUtil.merge(names, StringPool.COMMA_AND_SPACE));

		if (organizations.size() > _ORGANIZATION_NAMES_LIMIT) {
			sb.append(StringPool.COMMA_AND_SPACE);
			sb.append(
				LanguageUtil.format(
					locale, "and-x-more",
					organizations.size() - _ORGANIZATION_NAMES_LIMIT));
			sb.append(StringPool.TRIPLE_PERIOD);
		}

		return sb.toString();
	}

	private static User _getPersonAccountEntryUser(AccountEntry accountEntry) {
		if (!Objects.equals(
				AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON,
				accountEntry.getType())) {

			return null;
		}

		List<User> users = accountEntry.fetchUsers();

		if (ListUtil.isNotEmpty(users)) {
			return users.get(0);
		}

		return null;
	}

	private static final int _ORGANIZATION_NAMES_LIMIT = 5;

}