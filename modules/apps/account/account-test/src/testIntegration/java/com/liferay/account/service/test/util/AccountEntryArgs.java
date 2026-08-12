/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.test.util;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Drew Brokke
 */
public class AccountEntryArgs {

	public static final Consumer STATUS_INACTIVE = accountEntryArgs ->
		accountEntryArgs.status = WorkflowConstants.STATUS_INACTIVE;

	public static final Consumer TYPE_PERSON = accountEntryArgs ->
		accountEntryArgs.type = AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON;

	public static Consumer withAccountGroups(AccountGroup... accountGroups) {
		return accountEntryArgs ->
			accountEntryArgs.accountGroups = accountGroups;
	}

	public static Consumer withDescription(String description) {
		return accountEntryArgs -> accountEntryArgs.description = description;
	}

	public static Consumer withDomains(String... domains) {
		return accountEntryArgs -> accountEntryArgs.domains = domains;
	}

	public static Consumer withExternalReferenceCode(
		String externalReferenceCode) {

		return accountEntryArgs ->
			accountEntryArgs.externalReferenceCode = externalReferenceCode;
	}

	public static Consumer withName(String name) {
		return accountEntryArgs -> accountEntryArgs.name = name;
	}

	public static Consumer withOrganizations(Organization... organizations) {
		return accountEntryArgs ->
			accountEntryArgs.organizations = organizations;
	}

	public static Consumer withOwner(User user) {
		return accountEntryArgs -> accountEntryArgs.userId = user.getUserId();
	}

	public static Consumer withTaxIdNumber(String taxIdNumber) {
		return accountEntryArgs -> accountEntryArgs.taxIdNumber = taxIdNumber;
	}

	public static Consumer withUsers(User... users) {
		return accountEntryArgs -> accountEntryArgs.users = users;
	}

	public AccountEntryArgs() throws PortalException {
	}

	public AccountGroup[] accountGroups = null;
	public String[] assetTagNames = null;
	public String description = RandomTestUtil.randomString(50);
	public String[] domains = null;
	public String emailAddress = null;
	public String externalReferenceCode = null;
	public byte[] logoBytes = null;
	public String name = RandomTestUtil.randomString(50);
	public Organization[] organizations = null;
	public AccountEntry parentAccountEntry = null;
	public boolean restrictMembership = true;
	public ServiceContext serviceContext = null;
	public int status = WorkflowConstants.STATUS_APPROVED;
	public String taxIdNumber = RandomTestUtil.randomString(50);
	public String type = AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS;
	public long userId = TestPropsValues.getUserId();
	public User[] users = null;

	@FunctionalInterface
	public interface Consumer
		extends java.util.function.Consumer<AccountEntryArgs> {
	}

}