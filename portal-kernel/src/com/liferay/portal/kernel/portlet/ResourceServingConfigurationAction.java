/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

/**
 * @author Brian Wing Shun Chan
 */
public interface ResourceServingConfigurationAction {

	public void serveResource(
			PortletConfig portletConfig, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse)
		throws Exception;

}