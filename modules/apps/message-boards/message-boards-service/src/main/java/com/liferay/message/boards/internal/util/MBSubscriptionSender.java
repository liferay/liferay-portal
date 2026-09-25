/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.util;

import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.model.SMTPAccount;
import com.liferay.message.boards.model.MBMailingList;
import com.liferay.message.boards.service.MBMailingListLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupSubscriptionCheckSubscriptionSender;

/**
 * @author Brian Wing Shun Chan
 * @author Thiago Moreira
 * @author Roberto Díaz
 */
public class MBSubscriptionSender
	extends GroupSubscriptionCheckSubscriptionSender {

	public MBSubscriptionSender() {
	}

	public MBSubscriptionSender(String resourceName) {
		super(resourceName);
	}

	public void addMailingListSubscriber(long groupId, long categoryId) {
		if (_calledAddMailingListSubscriber) {
			throw new IllegalStateException("Method may only be called once");
		}

		_calledAddMailingListSubscriber = true;

		MBMailingList mailingList =
			MBMailingListLocalServiceUtil.fetchCategoryMailingList(
				groupId, categoryId);

		if ((mailingList == null) || !mailingList.isActive()) {
			return;
		}

		setFrom(mailingList.getOutEmailAddress(), null);
		setReplyToAddress(mailingList.getEmailAddress());

		if (mailingList.isOutCustom()) {
			String protocol = Account.PROTOCOL_SMTP;

			if (mailingList.isOutUseSSL()) {
				protocol = Account.PROTOCOL_SMTPS;
			}

			SMTPAccount smtpAccount = (SMTPAccount)Account.getInstance(
				protocol, mailingList.getOutServerPort());

			smtpAccount.setHost(mailingList.getOutServerName());
			smtpAccount.setUser(mailingList.getOutUserName());
			smtpAccount.setPassword(
				MBMailingListPasswordUtil.getPassword(
					mailingList.getCategoryId(), mailingList.getCompanyId(),
					"outPassword", mailingList.getOutPassword()));

			setSMTPAccount(smtpAccount);
		}

		setSubject(_getMailingListSubject(subject, mailId));

		addRuntimeSubscribers(
			mailingList.getEmailAddress(), mailingList.getEmailAddress());
	}

	public void setAnonymous(boolean anonymous) {
		_anonymous = anonymous;
	}

	public void setFullName(String fullName) {
		_fullName = fullName;
	}

	@Override
	protected void populateNotificationEventJSONObject(
		JSONObject notificationEventJSONObject) {

		notificationEventJSONObject.put(
			"anonymous", _anonymous
		).put(
			"fullName", _fullName
		);

		super.populateNotificationEventJSONObject(notificationEventJSONObject);
	}

	@Override
	protected void sendNotification(User user, boolean notifyImmediately)
		throws Exception {

		sendEmailNotification(user);

		if (currentUserId == user.getUserId()) {
			if (_log.isDebugEnabled()) {
				_log.debug("Skip notification for user " + currentUserId);
			}

			return;
		}

		sendUserNotification(user, notifyImmediately);
	}

	private String _getMailingListSubject(String subject, String mailId) {
		subject = GetterUtil.getString(subject);
		mailId = GetterUtil.getString(mailId);

		return StringBundler.concat(subject, StringPool.SPACE, mailId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MBSubscriptionSender.class);

	private boolean _anonymous;
	private boolean _calledAddMailingListSubscriber;
	private String _fullName;

}