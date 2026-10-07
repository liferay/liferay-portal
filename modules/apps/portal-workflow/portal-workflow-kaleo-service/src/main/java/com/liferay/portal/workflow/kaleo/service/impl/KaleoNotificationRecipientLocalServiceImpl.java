/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.definition.AddressRecipient;
import com.liferay.portal.workflow.kaleo.definition.NotificationReceptionType;
import com.liferay.portal.workflow.kaleo.definition.Recipient;
import com.liferay.portal.workflow.kaleo.definition.RecipientType;
import com.liferay.portal.workflow.kaleo.definition.RoleRecipient;
import com.liferay.portal.workflow.kaleo.definition.ScriptLanguage;
import com.liferay.portal.workflow.kaleo.definition.ScriptRecipient;
import com.liferay.portal.workflow.kaleo.definition.UserRecipient;
import com.liferay.portal.workflow.kaleo.internal.util.RoleUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipient;
import com.liferay.portal.workflow.kaleo.service.base.KaleoNotificationRecipientLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipient",
	service = AopService.class
)
public class KaleoNotificationRecipientLocalServiceImpl
	extends KaleoNotificationRecipientLocalServiceBaseImpl {

	@Override
	public KaleoNotificationRecipient addKaleoNotificationRecipient(
			long kaleoDefinitionId, long kaleoDefinitionVersionId,
			long kaleoNotificationId, Recipient recipient,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(
			serviceContext.getGuestOrUserId());
		Date date = new Date();

		long kaleoNotificationRecipientId = counterLocalService.increment();

		KaleoNotificationRecipient kaleoNotificationRecipient =
			kaleoNotificationRecipientPersistence.create(
				kaleoNotificationRecipientId);

		kaleoNotificationRecipient.setCompanyId(user.getCompanyId());
		kaleoNotificationRecipient.setUserId(user.getUserId());
		kaleoNotificationRecipient.setUserName(user.getFullName());
		kaleoNotificationRecipient.setCreateDate(date);
		kaleoNotificationRecipient.setModifiedDate(date);
		kaleoNotificationRecipient.setKaleoDefinitionId(kaleoDefinitionId);
		kaleoNotificationRecipient.setKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
		kaleoNotificationRecipient.setKaleoNotificationId(kaleoNotificationId);

		NotificationReceptionType notificationReceptionType =
			recipient.getNotificationReceptionType();

		kaleoNotificationRecipient.setNotificationReceptionType(
			notificationReceptionType.getValue());

		_setRecipient(kaleoNotificationRecipient, recipient, serviceContext);

		return kaleoNotificationRecipientPersistence.update(
			kaleoNotificationRecipient);
	}

	@Override
	public void deleteCompanyKaleoNotificationRecipients(long companyId) {
		kaleoNotificationRecipientPersistence.removeByCompanyId(companyId);
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoNotificationRecipients(
		long kaleoDefinitionVersionId) {

		kaleoNotificationRecipientPersistence.removeByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
	}

	@Override
	public List<KaleoNotificationRecipient> getKaleoNotificationRecipients(
		long kaleoNotificationId) {

		return kaleoNotificationRecipientPersistence.findByKaleoNotificationId(
			kaleoNotificationId);
	}

	private void _setRecipient(
			KaleoNotificationRecipient kaleoNotificationRecipient,
			Recipient recipient, ServiceContext serviceContext)
		throws PortalException {

		RecipientType recipientType = recipient.getRecipientType();

		kaleoNotificationRecipient.setRecipientClassName(
			recipientType.getValue());

		if (recipientType.equals(RecipientType.ADDRESS)) {
			AddressRecipient addressRecipient = (AddressRecipient)recipient;

			kaleoNotificationRecipient.setAddress(
				addressRecipient.getAddress());
		}
		else if (recipientType.equals(RecipientType.ROLE)) {
			RoleRecipient roleRecipient = (RoleRecipient)recipient;

			int roleType = 0;

			Role role = null;

			if (Validator.isNotNull(
					roleRecipient.getRoleExternalReferenceCode())) {

				role = RoleUtil.getOrAddEmptyRole(
					roleRecipient.getRoleExternalReferenceCode(),
					roleRecipient.getRoleType(), serviceContext);

				roleType = role.getType();
			}
			else if (Validator.isNotNull(roleRecipient.getRoleName())) {
				roleType = RoleUtil.getRoleType(roleRecipient.getRoleType());

				role = RoleUtil.getRole(
					roleRecipient.getRoleName(), roleType,
					roleRecipient.isAutoCreate(), serviceContext);
			}
			else {
				role = _roleLocalService.getRole(roleRecipient.getRoleId());

				roleType = role.getType();
			}

			kaleoNotificationRecipient.setRecipientClassPK(role.getClassPK());
			kaleoNotificationRecipient.setRecipientRoleType(roleType);
		}
		else if (recipientType.equals(RecipientType.SCRIPT)) {
			ScriptRecipient scriptRecipient = (ScriptRecipient)recipient;

			kaleoNotificationRecipient.setRecipientScript(
				scriptRecipient.getScript());

			ScriptLanguage scriptLanguage = scriptRecipient.getScriptLanguage();

			kaleoNotificationRecipient.setRecipientScriptLanguage(
				scriptLanguage.getValue());

			kaleoNotificationRecipient.setRecipientScriptContexts(
				scriptRecipient.getScriptRequiredContexts());
		}
		else if (recipientType.equals(RecipientType.USER)) {
			UserRecipient userRecipient = (UserRecipient)recipient;

			User user = null;

			if (userRecipient.getUserId() > 0) {
				user = _userLocalService.getUser(userRecipient.getUserId());
			}
			else if (Validator.isNotNull(userRecipient.getScreenName())) {
				user = _userLocalService.getUserByScreenName(
					serviceContext.getCompanyId(),
					userRecipient.getScreenName());
			}
			else if (Validator.isNotNull(userRecipient.getEmailAddress())) {
				user = _userLocalService.getUserByEmailAddress(
					serviceContext.getCompanyId(),
					userRecipient.getEmailAddress());
			}

			if (user != null) {
				kaleoNotificationRecipient.setRecipientClassPK(
					user.getUserId());
			}
		}
	}

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}