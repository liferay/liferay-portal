/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.roles.admin.role.type.contributor;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountRoleService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;

import java.util.Collections;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "service.ranking:Integer=500",
	service = RoleTypeContributor.class
)
public class AccountRoleTypeContributor implements RoleTypeContributor {

	@Override
	public String getClassName() {
		return AccountRole.class.getName();
	}

	@Override
	public String getIcon() {
		return "briefcase";
	}

	@Override
	public String getName() {
		return "account";
	}

	@Override
	public String[] getSubtypes() {
		return new String[0];
	}

	@Override
	public String getTabTitle(Locale locale) {
		return _language.get(locale, "account-roles");
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "account-role");
	}

	@Override
	public int getType() {
		return RoleConstants.TYPE_ACCOUNT;
	}

	@Override
	public boolean isAllowAssignMembers(Role role) {
		return false;
	}

	@Override
	public boolean isAllowDefinePermissions(Role role) {
		return true;
	}

	@Override
	public boolean isAllowDelete(Role role) {
		if ((role == null) || AccountRoleConstants.isRequiredRole(role)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isAutomaticallyAssigned(Role role) {
		return AccountRoleConstants.isImpliedRole(role);
	}

	@Override
	public BaseModelSearchResult<Role> searchRoles(
		long companyId, String keywords, String subtype, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		try {
			BaseModelSearchResult<AccountRole>
				accountRoleBaseModelSearchResult =
					_accountRoleService.searchAccountRoles(
						companyId,
						new long[] {AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT},
						keywords, null, start, end, orderByComparator);

			return new BaseModelSearchResult<>(
				TransformUtil.transform(
					accountRoleBaseModelSearchResult.getBaseModels(),
					AccountRole::getRole),
				accountRoleBaseModelSearchResult.getLength());
		}
		catch (Exception exception) {
			_log.error(exception);

			return new BaseModelSearchResult<>(
				Collections.<Role>emptyList(), 0);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountRoleTypeContributor.class);

	@Reference
	private AccountRoleService _accountRoleService;

	@Reference
	private Language _language;

}