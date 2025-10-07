/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.kernel.service;

import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.mail.Session;

/**
 * @author Brian Wing Shun Chan
 */
public class MailServiceUtil {

	public static void clearSession() {
		getService().clearSession();
	}

	public static String getMailId(
		String mx, String popPortletPrefix, Object... ids) {

		return getService().getMailId(mx, popPortletPrefix, ids);
	}

	public static MailService getService() {
		return _mailServiceSnapshot.get();
	}

	public static Session getSession() {
		return getService().getSession();
	}

	public static Session getSession(Account account) {
		return getService().getSession(account);
	}

	public static Session getSession(long companyId) {
		return getService().getSession(companyId);
	}

	public static boolean isPopServerUser(String emailAddress) {
		return getService().isPOPServerUser(emailAddress);
	}

	public static void sendEmail(MailMessage mailMessage) {
		getService().sendEmail(mailMessage);
	}

	private static final Snapshot<MailService> _mailServiceSnapshot =
		new Snapshot<>(MailServiceUtil.class, MailService.class);

}