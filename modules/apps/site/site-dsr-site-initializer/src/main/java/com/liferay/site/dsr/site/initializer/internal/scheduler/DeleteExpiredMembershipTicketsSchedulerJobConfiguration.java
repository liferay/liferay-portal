/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.scheduler;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.site.dsr.site.initializer.constants.DSRTicketConstants;

import java.util.Arrays;
import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = SchedulerJobConfiguration.class)
public class DeleteExpiredMembershipTicketsSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> {
			ActionableDynamicQuery actionableDynamicQuery =
				_ticketLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> dynamicQuery.add(
					RestrictionsFactoryUtil.and(
						RestrictionsFactoryUtil.in(
							"type",
							Arrays.asList(
								DSRTicketConstants.TYPE_EXPIRE_MEMBERSHIP,
								DSRTicketConstants.TYPE_INVITE_MEMBER)),
						RestrictionsFactoryUtil.le(
							"expirationDate", new Date()))));
			actionableDynamicQuery.setPerformActionMethod(
				(Ticket ticket) -> {
					if (ticket.getType() ==
							DSRTicketConstants.TYPE_INVITE_MEMBER) {

						_ticketLocalService.deleteTicket(ticket);

						return;
					}

					JSONObject jsonObject = _jsonFactory.createJSONObject(
						ticket.getExtraInfo());

					long groupId = ticket.getClassPK();
					long userId = jsonObject.getLong("userId");

					LiveUsers.leaveGroup(
						ticket.getCompanyId(), groupId, userId);

					_userGroupRoleLocalService.deleteUserGroupRoles(
						new long[] {userId}, groupId);

					_userLocalService.deleteGroupUser(groupId, userId);

					_ticketLocalService.deleteTicket(ticket);
				});

			actionableDynamicQuery.performActions();
		};
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			1, TimeUnit.HOUR);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}