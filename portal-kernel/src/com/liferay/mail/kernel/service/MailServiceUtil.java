/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.kernel.service;

import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

import jakarta.mail.Session;

/**
 * @author Brian Wing Shun Chan
 */
public class MailServiceUtil {

	public static void clearSession() {
		getService().clearSession();
	}

	public static MailService getService() {
		if (_mailService == null) {
			_mailService = (MailService)PortalBeanLocatorUtil.locate(
				MailService.class.getName());
		}

		return _mailService;
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

	public static void sendEmail(MailMessage mailMessage) {
		getService().sendEmail(mailMessage);
	}

	public void setService(MailService mailService) {
		_mailService = mailService;
	}

	private static MailService _mailService;

}