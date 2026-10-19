/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.batch.engine.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelService;
import com.liferay.batch.engine.BaseBatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountMember;
import com.liferay.headless.commerce.admin.account.internal.constants.v1_0.AccountMemberBatchEngineTaskItemDelegateConstants;
import com.liferay.headless.commerce.admin.account.internal.util.v1_0.AccountMemberUtil;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "batch.engine.task.item.delegate.name=" + AccountMemberBatchEngineTaskItemDelegateConstants.COMMERCE_ACCOUNT_MEMBER,
	scope = ServiceScope.PROTOTYPE, service = BatchEngineTaskItemDelegate.class
)
public class AccountMemberBatchEngineTaskItemDelegate
	extends BaseBatchEngineTaskItemDelegate<AccountMember> {

	@Override
	public AccountMember createItem(
			AccountMember accountMember, Map<String, Serializable> parameters)
		throws Exception {

		Serializable id = parameters.get("id");

		AccountMemberUtil.addAccountEntryUserRel(
			_accountEntryModelResourcePermission, _accountEntryUserRelService,
			accountMember,
			_accountEntryLocalService.getAccountEntry(
				Long.valueOf(id.toString())),
			_commerceAccountHelper,
			AccountMemberUtil.getUser(
				_userLocalService, accountMember,
				contextCompany.getCompanyId()),
			_serviceContextHelper.getServiceContext());

		return null;
	}

	@Override
	public Page<AccountMember> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return null;
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private AccountEntryUserRelService _accountEntryUserRelService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference
	private UserLocalService _userLocalService;

}