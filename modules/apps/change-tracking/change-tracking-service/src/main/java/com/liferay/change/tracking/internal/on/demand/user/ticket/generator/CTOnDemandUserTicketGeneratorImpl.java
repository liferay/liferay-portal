/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.on.demand.user.ticket.generator;

import com.liferay.change.tracking.constants.CTOnDemandUserConstants;
import com.liferay.change.tracking.constants.CTRoleConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.on.demand.user.ticket.generator.CTOnDemandUserTicketGenerator;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = CTOnDemandUserTicketGenerator.class)
public class CTOnDemandUserTicketGeneratorImpl
	implements CTOnDemandUserTicketGenerator {

	@Override
	public Ticket generate(long ctCollectionId) throws PortalException {
		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		if ((ctCollection == null) || !ctCollection.isShareable()) {
			return null;
		}

		List<Ticket> tickets = _ticketLocalService.getTickets(
			ctCollection.getCompanyId(), CTCollection.class.getName(),
			ctCollectionId, TicketConstants.TYPE_ON_DEMAND_USER_LOGIN);

		if (!tickets.isEmpty()) {
			return tickets.get(0);
		}

		User user = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			user = _getCTOnDemandUser(ctCollection);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		if (user == null) {
			return null;
		}

		AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			CTOnDemandUserConstants.
				AUDIT_EVENT_TYPE_CT_ON_DEMAND_USER_TICKET_GENERATED,
			CTCollection.class.getName(), ctCollectionId, null);

		auditMessage.setAdditionalInfo(
			JSONUtil.put(
				"companyId", ctCollection.getCompanyId()
			).put(
				"onDemandUserId", user.getUserId()
			));

		_auditRouter.route(auditMessage);

		return _ticketLocalService.addDistinctTicket(
			user.getCompanyId(), CTCollection.class.getName(), ctCollectionId,
			TicketConstants.TYPE_ON_DEMAND_USER_LOGIN,
			String.valueOf(user.getUserId()), null, null);
	}

	private User _getCTOnDemandUser(CTCollection ctCollection)
		throws PortalException {

		User user = _userLocalService.fetchUserById(
			ctCollection.getOnDemandUserId());

		if (user != null) {
			return user;
		}

		Company company = _companyLocalService.getCompany(
			ctCollection.getCompanyId());

		String screenName = StringBundler.concat(
			CTOnDemandUserConstants.SCREEN_NAME_PREFIX_CT_ON_DEMAND_USER,
			StringPool.UNDERLINE, ctCollection.getCtCollectionId());

		String password = PwdGenerator.getPassword(20);

		Date date = new Date();

		Role role = _roleLocalService.getRole(
			ctCollection.getCompanyId(), CTRoleConstants.PUBLICATIONS_REVIEWER);

		user = _userLocalService.addUser(
			PrincipalThreadLocal.getUserId(), company.getCompanyId(), false,
			password, password, true, screenName,
			StringBundler.concat(screenName, StringPool.AT, company.getMx()),
			company.getLocale(), ctCollection.getName(), null,
			"Publication Reviewer", 0, 0, true, date.getMonth(), date.getDate(),
			date.getYear(), null, UserConstants.TYPE_ON_DEMAND_USER, null, null,
			new long[] {role.getRoleId()}, null, false, new ServiceContext());

		user.setPasswordReset(false);
		user.setAgreedToTermsOfUse(true);
		user.setEmailAddressVerified(true);

		user = _userLocalService.updateUser(user);

		ctCollection.setOnDemandUserId(user.getUserId());

		ctCollection = _ctCollectionLocalService.updateCTCollection(
			ctCollection);

		Group group = _groupLocalService.fetchGroup(
			ctCollection.getCompanyId(),
			_classNameLocalService.getClassNameId(CTCollection.class),
			ctCollection.getCtCollectionId());

		if (group == null) {
			group = _groupLocalService.addGroup(
				StringPool.BLANK, ctCollection.getUserId(),
				GroupConstants.DEFAULT_PARENT_GROUP_ID,
				CTCollection.class.getName(), ctCollection.getCtCollectionId(),
				GroupConstants.DEFAULT_LIVE_GROUP_ID,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), ctCollection.getName()
				).build(),
				null, GroupConstants.TYPE_SITE_PRIVATE, null, false,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false,
				false, true, null);
		}

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), group.getGroupId(), role.getRoleId());

		return user;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTOnDemandUserTicketGeneratorImpl.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}