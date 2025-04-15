/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.web.internal.portlet.action;

import com.liferay.microblogs.constants.MicroblogsPortletKeys;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "jakarta.portlet.name=" + MicroblogsPortletKeys.MICROBLOGS_STATUS_UPDATE,
	service = ConfigurationAction.class
)
public class MicroblogsStatusUpdateConfigurationAction
	extends DefaultConfigurationAction {
}