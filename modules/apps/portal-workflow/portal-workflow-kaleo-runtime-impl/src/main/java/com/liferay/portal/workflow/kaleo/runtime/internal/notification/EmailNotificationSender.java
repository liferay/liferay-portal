/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.notification;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.kaleo.definition.NotificationReceptionType;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.internal.settings.WorkflowGroupServiceSettings;
import com.liferay.portal.workflow.kaleo.runtime.notification.BaseNotificationSender;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationRecipient;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationSender;

import jakarta.mail.internet.InternetAddress;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = {
		"fromAddress=no-reply@liferay.com",
		"fromName=Liferay Portal Workflow Notifications"
	},
	service = NotificationSender.class
)
public class EmailNotificationSender
	extends BaseNotificationSender implements NotificationSender {

	@Override
	public String getNotificationType() {
		return "email";
	}

	protected void activate(Map<String, Object> properties) {
		_fromAddress = (String)properties.get("fromAddress");
		_fromName = (String)properties.get("fromName");
	}

	@Override
	protected void doSendNotification(
			Map<NotificationReceptionType, Set<NotificationRecipient>>
				notificationRecipients,
			String defaultSubject, String notificationMessage,
			ExecutionContext executionContext)
		throws Exception {

		Map<String, Serializable> workflowContext =
			executionContext.getWorkflowContext();

		long companyId = GetterUtil.getLong(
			workflowContext.get(WorkflowConstants.CONTEXT_COMPANY_ID));

		WorkflowGroupServiceSettings workflowGroupServiceSettings =
			WorkflowGroupServiceSettings.getInstance(companyId);

		String fromAddress = (String)workflowContext.get(
			WorkflowConstants.CONTEXT_NOTIFICATION_SENDER_ADDRESS);

		if (Validator.isNull(fromAddress)) {
			fromAddress = workflowGroupServiceSettings.getEmailFromAddress();
		}

		if (Validator.isNull(fromAddress)) {
			fromAddress = _fromAddress;
		}

		String fromName = (String)workflowContext.get(
			WorkflowConstants.CONTEXT_NOTIFICATION_SENDER_NAME);

		if (Validator.isNull(fromName)) {
			fromName = workflowGroupServiceSettings.getEmailFromName();
		}

		if (Validator.isNull(fromName)) {
			fromName = _fromName;
		}

		InternetAddress from = new InternetAddress(fromAddress, fromName);

		String subject = (String)workflowContext.get(
			WorkflowConstants.CONTEXT_NOTIFICATION_SUBJECT);

		if (Validator.isNull(subject)) {
			subject = defaultSubject;
		}

		MailMessage mailMessage = new MailMessage(
			from, subject, notificationMessage, true);

		mailMessage.setTo(
			_getInternetAddresses(
				getDeliverableNotificationRecipients(
					notificationRecipients.get(NotificationReceptionType.TO),
					UserNotificationDeliveryConstants.TYPE_EMAIL)));
		mailMessage.setCC(
			_getInternetAddresses(
				getDeliverableNotificationRecipients(
					notificationRecipients.get(NotificationReceptionType.CC),
					UserNotificationDeliveryConstants.TYPE_EMAIL)));
		mailMessage.setBCC(
			_getInternetAddresses(
				getDeliverableNotificationRecipients(
					notificationRecipients.get(NotificationReceptionType.BCC),
					UserNotificationDeliveryConstants.TYPE_EMAIL)));

		_mailService.sendEmail(mailMessage);
	}

	private InternetAddress[] _getInternetAddresses(
			Set<NotificationRecipient> notificationRecipients)
		throws Exception {

		if (notificationRecipients == null) {
			return new InternetAddress[0];
		}

		List<InternetAddress> internetAddresses = new ArrayList<>(
			notificationRecipients.size());

		for (NotificationRecipient notificationRecipient :
				notificationRecipients) {

			internetAddresses.add(notificationRecipient.getInternetAddress());
		}

		return internetAddresses.toArray(new InternetAddress[0]);
	}

	private String _fromAddress;
	private String _fromName;

	@Reference
	private MailService _mailService;

}