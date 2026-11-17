/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.resource.v1_0;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.headless.commerce.admin.account.dto.v1_0.User;
import com.liferay.headless.commerce.admin.account.internal.util.v1_0.AccountMemberUtil;
import com.liferay.headless.commerce.admin.account.resource.v1_0.UserResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.util.Calendar;
import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user.properties",
	scope = ServiceScope.PROTOTYPE, service = UserResource.class
)
@Deprecated
public class UserResourceImpl extends BaseUserResourceImpl {

	@Override
	public User postAccountByExternalReferenceCodeAccountMemberCreateUser(
			String externalReferenceCode, User user)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.getAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		_accountEntryModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(),
			accountEntry.getAccountEntryId(), AccountActionKeys.ASSIGN_USERS);

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			accountEntry.getAccountEntryGroupId());

		com.liferay.portal.kernel.model.User invitedUser = null;

		if (user.getId() != null) {
			invitedUser = _userLocalService.fetchUserById(user.getId());
		}
		else if (Validator.isNotNull(user.getExternalReferenceCode())) {
			invitedUser = _userLocalService.fetchUserByExternalReferenceCode(
				user.getExternalReferenceCode(), contextCompany.getCompanyId());
		}

		if (invitedUser == null) {
			invitedUser = _userService.addUser(
				contextCompany.getCompanyId(), true, null, null, true, null,
				user.getEmail(), 0L, null, LocaleUtil.getDefault(),
				user.getFirstName(), user.getMiddleName(), user.getLastName(),
				0L, 0L, GetterUtil.getBoolean(user.getMale(), true), 1, 1, 1970,
				user.getJobTitle(),
				new long[] {accountEntry.getAccountEntryGroupId()}, null, null,
				null, false, serviceContext);
		}
		else {
			Date birthday = invitedUser.getBirthday();

			Calendar calendar = CalendarFactoryUtil.getCalendar(
				birthday.getTime(), invitedUser.getTimeZone());

			invitedUser = _userService.updateUser(
				invitedUser.getUserId(), null, null, null, false,
				invitedUser.getReminderQueryQuestion(),
				invitedUser.getReminderQueryAnswer(),
				invitedUser.getScreenName(),
				GetterUtil.get(user.getEmail(), invitedUser.getEmailAddress()),
				invitedUser.getLanguageId(), invitedUser.getTimeZoneId(),
				invitedUser.getGreeting(), invitedUser.getComments(),
				user.getFirstName(),
				GetterUtil.get(
					user.getMiddleName(), invitedUser.getMiddleName()),
				user.getLastName(), 0L, 0L,
				GetterUtil.get(user.getMale(), invitedUser.isMale()),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), null, null, null, null, null,
				GetterUtil.get(user.getJobTitle(), invitedUser.getJobTitle()),
				invitedUser.getGroupIds(), invitedUser.getOrganizationIds(),
				invitedUser.getRoleIds(), null, invitedUser.getUserGroupIds(),
				serviceContext);
		}

		// External reference code

		if (Validator.isNotNull(user.getExternalReferenceCode())) {
			invitedUser.setExternalReferenceCode(
				user.getExternalReferenceCode());

			_userLocalService.updateUser(invitedUser);
		}

		// Account rel

		long[] roleIds = new long[0];

		String[] roles = user.getRoles();

		if (roles != null) {
			for (String role : roles) {
				Role curRole = _roleLocalService.getRole(
					contextCompany.getCompanyId(), role);

				roleIds = ArrayUtil.append(roleIds, curRole.getRoleId());
			}
		}

		AccountMemberUtil.validateAccountRoleIds(
			accountEntry.getAccountEntryId(), roleIds);

		_commerceAccountHelper.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), invitedUser.getUserId(), roleIds,
			serviceContext);

		return _userDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				invitedUser.getUserId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.account.internal.dto.v1_0.converter.UserDTOConverter)"
	)
	private DTOConverter<com.liferay.portal.kernel.model.User, User>
		_userDTOConverter;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserService _userService;

}