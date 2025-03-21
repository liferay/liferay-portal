/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.kernel.service;

import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.transaction.Transactional;

import jakarta.mail.Session;

/**
 * @author Brian Wing Shun Chan
 */
@Transactional(rollbackFor = {PortalException.class, SystemException.class})
public interface MailService {

	public void clearSession();

	public void clearSession(long companyId);

	public Session getSession();

	public Session getSession(Account account);

	public Session getSession(long companyId);

	public void sendEmail(MailMessage mailMessage);

}