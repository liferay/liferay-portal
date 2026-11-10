/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.settings.internal.configuration;

import com.liferay.mail.kernel.service.MailService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jiefeng Wu
 */
@Component(
	configurationPid = "com.liferay.mail.settings.configuration.MailSettingCompanyConfiguration",
	service = {}
)
public class MailServiceCompanyConfigurationHandler {

	@Activate
	@Modified
	protected void activate() {
		_mailService.clearSession();
	}

	@Reference
	private MailService _mailService;

}