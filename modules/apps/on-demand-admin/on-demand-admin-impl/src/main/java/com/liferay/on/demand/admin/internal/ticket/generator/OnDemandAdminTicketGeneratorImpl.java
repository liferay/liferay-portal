/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.on.demand.admin.internal.ticket.generator;

import com.liferay.on.demand.admin.constants.OnDemandAdminConstants;
import com.liferay.on.demand.admin.internal.configuration.OnDemandAdminConfiguration;
import com.liferay.on.demand.admin.internal.helper.OnDemandAdminHelper;
import com.liferay.on.demand.admin.ticket.generator.OnDemandAdminTicketGenerator;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = OnDemandAdminTicketGenerator.class)
public class OnDemandAdminTicketGeneratorImpl
	implements OnDemandAdminTicketGenerator {

	@Override
	public Ticket generate(
			Company company, String justification, long requestorUserId)
		throws PortalException {

		return generate(
			company, justification, _userLocalService.getUser(requestorUserId));
	}

	@Override
	public Ticket generate(
			Company company, String justification, User requestorUser)
		throws PortalException {

		_onDemandAdminHelper.checkRequestAdministratorAccessPermission(
			company.getCompanyId(), requestorUser.getUserId());

		User user = _addOnDemandAdminUser(
			requestorUser.getUserId(), company.getCompanyId(), company.getMx(),
			requestorUser.getLocale(), requestorUser.getFirstName(),
			requestorUser.getMiddleName(), requestorUser.getLastName(),
			requestorUser.getMale());

		AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			"grant_on_demand_access", User.class.getName(),
			requestorUser.getUserId(),
			OnDemandAdminConstants.
				AUDIT_EVENT_TYPE_ON_DEMAND_ADMIN_TICKET_GENERATED,
			null, null);

		auditMessage.setAdditionalInfo(
			JSONUtil.put(
				"justification", justification
			).put(
				"requestedCompanyId", company.getCompanyId()
			).put(
				"requestedCompanyWebId", company.getWebId()
			));

		_auditRouter.route(auditMessage);

		OnDemandAdminConfiguration onDemandAdminConfiguration =
			_configurationProvider.getSystemConfiguration(
				OnDemandAdminConfiguration.class);

		int expirationTime =
			onDemandAdminConfiguration.authenticationTokenExpirationTime();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			return _ticketLocalService.addDistinctTicket(
				user.getCompanyId(), User.class.getName(), user.getUserId(),
				TicketConstants.TYPE_ON_DEMAND_ADMIN_LOGIN, justification,
				new Date(
					System.currentTimeMillis() +
						TimeUnit.MINUTES.toMillis(expirationTime)),
				null);
		}
	}

	private User _addOnDemandAdminUser(
			long userId, long companyId, String mx, Locale locale,
			String firstName, String middleName, String lastName, boolean male)
		throws PortalException {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			String password = PwdGenerator.getPassword(20);

			String screenName = _getScreenName(userId, 0);

			String emailAddress = screenName + StringPool.AT + mx;

			Calendar calendar = Calendar.getInstance();
			Role role = _roleLocalService.getRole(
				companyId, RoleConstants.ADMINISTRATOR);

			User user = _userLocalService.addUser(
				0, companyId, false, password, password, true, null,
				emailAddress, locale, firstName, middleName, lastName, 0, 0,
				male, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), null, UserConstants.TYPE_REGULAR,
				null, null, new long[] {role.getRoleId()}, null, false,
				new ServiceContext());

			screenName = _getScreenName(userId, user.getUserId());

			user.setScreenName(screenName);
			user.setEmailAddress(screenName + StringPool.AT + mx);

			user.setEmailAddressVerified(true);

			return _userLocalService.updateUser(user);
		}
	}

	private String _getScreenName(long requestorUserId, long userId)
		throws PortalException {

		return StringBundler.concat(
			OnDemandAdminConstants.SCREEN_NAME_PREFIX_ON_DEMAND_ADMIN,
			StringPool.UNDERLINE, requestorUserId, StringPool.UNDERLINE,
			userId);
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private OnDemandAdminHelper _onDemandAdminHelper;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserLocalService _userLocalService;

}