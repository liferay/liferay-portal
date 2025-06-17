/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.verify;

import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.helper.CommerceRoleHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.verify.VerifyProcess;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(property = "initial.deployment=true", service = VerifyProcess.class)
public class CommerceAccountServiceVerifyProcess extends VerifyProcess {

	public void verifyAccountGroup() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_companyLocalService.forEachCompanyId(
				companyId -> _accountGroupLocalService.checkGuestAccountGroup(
					companyId));
		}
	}

	public void verifyAccountRoles() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_companyLocalService.forEachCompanyId(
				companyId -> {
					ServiceContext serviceContext = new ServiceContext();

					serviceContext.setAttribute(
						"forceReloadPermissions", Boolean.TRUE);
					serviceContext.setCompanyId(companyId);
					serviceContext.setUserId(_getAdminUserId(companyId));
					serviceContext.setUuid(PortalUUIDUtil.generate());

					_commerceRoleHelper.checkCommerceAccountRoles(
						serviceContext);
				});
		}
	}

	@Override
	protected void doVerify() throws Exception {
		verifyAccountRoles();
		verifyAccountGroup();
	}

	private long _getAdminUserId(long companyId) throws PortalException {
		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		long[] userIds = _userLocalService.getRoleUserIds(role.getRoleId());

		if (userIds.length == 0) {
			throw new NoSuchUserException(
				StringBundler.concat(
					"No user exists in company ", companyId, " with role ",
					role.getName()));
		}

		return userIds[0];
	}

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private CommerceRoleHelper _commerceRoleHelper;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}