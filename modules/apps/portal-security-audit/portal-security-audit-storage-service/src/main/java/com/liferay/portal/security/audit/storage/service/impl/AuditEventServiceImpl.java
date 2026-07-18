/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.impl;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.security.audit.storage.comparator.AuditEventCreateDateComparator;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.base.AuditEventServiceBaseImpl;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=audit",
		"json.web.service.context.path=AuditEvent"
	},
	service = AopService.class
)
@CTAware
public class AuditEventServiceImpl extends AuditEventServiceBaseImpl {

	@Override
	public List<AuditEvent> getAuditEvents(long companyId, int start, int end)
		throws PortalException {

		_checkPermission(null, companyId);

		return auditEventPersistence.findByCompanyId(
			companyId, start, end, new AuditEventCreateDateComparator());
	}

	@Override
	public List<AuditEvent> getAuditEvents(
			long companyId, int start, int end,
			OrderByComparator<AuditEvent> orderByComparator)
		throws PortalException {

		_checkPermission(null, companyId);

		return auditEventPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public List<AuditEvent> getAuditEvents(
			long companyId, long groupId, long userId, String userName,
			Date createDateGT, Date createDateLT, long[] accountEntryIds,
			String className, String classPK, String clientHost,
			String clientIP, String contextName, String eventType,
			String serverName, int serverPort, String sessionID,
			boolean andSearch, int start, int end)
		throws PortalException {

		_checkPermission(accountEntryIds, companyId);

		return auditEventLocalService.getAuditEvents(
			companyId, groupId, userId, userName, createDateGT, createDateLT,
			accountEntryIds, className, classPK, clientHost, clientIP,
			contextName, eventType, serverName, serverPort, sessionID,
			andSearch, start, end);
	}

	@Override
	public List<AuditEvent> getAuditEvents(
			long companyId, long groupId, long userId, String userName,
			Date createDateGT, Date createDateLT, long[] accountEntryIds,
			String className, String classPK, String clientHost,
			String clientIP, String contextName, String eventType,
			String serverName, int serverPort, String sessionID,
			boolean andSearch, int start, int end,
			OrderByComparator<AuditEvent> orderByComparator)
		throws PortalException {

		_checkPermission(accountEntryIds, companyId);

		return auditEventLocalService.getAuditEvents(
			companyId, groupId, userId, userName, createDateGT, createDateLT,
			accountEntryIds, className, classPK, clientHost, clientIP,
			contextName, eventType, serverName, serverPort, sessionID,
			andSearch, start, end, orderByComparator);
	}

	@Override
	public int getAuditEventsCount(long companyId) throws PortalException {
		_checkPermission(null, companyId);

		return auditEventPersistence.countByCompanyId(companyId);
	}

	@Override
	public int getAuditEventsCount(
			long companyId, long groupId, long userId, String userName,
			Date createDateGT, Date createDateLT, long[] accountEntryIds,
			String className, String classPK, String clientHost,
			String clientIP, String contextName, String eventType,
			String serverName, int serverPort, String sessionID,
			boolean andSearch)
		throws PortalException {

		_checkPermission(accountEntryIds, companyId);

		return auditEventLocalService.getAuditEventsCount(
			companyId, groupId, userId, userName, createDateGT, createDateLT,
			accountEntryIds, className, classPK, clientHost, clientIP,
			contextName, eventType, serverName, serverPort, sessionID,
			andSearch);
	}

	private void _checkPermission(long[] accountEntryIds, long companyId)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (permissionChecker.isCompanyAdmin(companyId) ||
			_userLocalService.hasRoleUser(
				companyId, RoleConstants.ANALYTICS_ADMINISTRATOR,
				permissionChecker.getUserId(), true)) {

			return;
		}

		if (ArrayUtil.isEmpty(accountEntryIds)) {
			throw new PrincipalException();
		}

		Role role = _roleLocalService.getRole(
			companyId,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);

		AccountRole accountRole =
			_accountRoleLocalService.getAccountRoleByRoleId(role.getRoleId());

		for (long accountEntryId : accountEntryIds) {
			if (!_accountRoleLocalService.hasUserAccountRole(
					accountEntryId, accountRole.getAccountRoleId(),
					permissionChecker.getUserId())) {

				throw new PrincipalException();
			}
		}
	}

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}