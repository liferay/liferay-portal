/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.flags.service.impl;

import com.liferay.flags.internal.messaging.FlagsRequest;
import com.liferay.flags.service.base.FlagsEntryServiceBaseImpl;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageService;
import com.liferay.message.boards.service.MBSuspiciousActivityService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.EmailAddressException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMode;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = {
		"json.web.service.context.name=flags",
		"json.web.service.context.path=FlagsEntry"
	},
	service = AopService.class
)
public class FlagsEntryServiceImpl extends FlagsEntryServiceBaseImpl {

	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	@Override
	public void addEntry(
			String className, long classPK, String reporterEmailAddress,
			long reportedUserId, String contentTitle, String contentURL,
			String reason, ServiceContext serviceContext)
		throws PortalException {

		if (!Validator.isEmailAddress(reporterEmailAddress)) {
			throw new EmailAddressException();
		}

		FlagsRequest flagsRequest = new FlagsRequest(
			className, classPK, reporterEmailAddress, reportedUserId,
			contentTitle, contentURL, reason, serviceContext);

		User reportedUser = _userLocalService.fetchUser(reportedUserId);

		if ((reportedUser != null) && !reportedUser.isGuestUser()) {
			try {
				flagsRequest.setReportedUserURL(
					reportedUser.getDisplayURL(
						serviceContext.getThemeDisplay()));
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		Message message = new Message();

		message.setPayload(flagsRequest);

		_messageBus.sendMessage(DestinationNames.FLAGS, message);

		if (className.equals("com.liferay.message.boards.model.MBMessage")) {
			MBMessage mbMessage = _mbMessageService.getMessage(classPK);

			if (mbMessage.isRoot()) {
				_mbSuspiciousActivityService.
					addOrUpdateThreadSuspiciousActivity(
						mbMessage.getThreadId(), reason);
			}
			else {
				_mbSuspiciousActivityService.
					addOrUpdateMessageSuspiciousActivity(classPK, reason);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FlagsEntryServiceImpl.class);

	@Reference
	private MBMessageService _mbMessageService;

	@Reference
	private MBSuspiciousActivityService _mbSuspiciousActivityService;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private UserLocalService _userLocalService;

}