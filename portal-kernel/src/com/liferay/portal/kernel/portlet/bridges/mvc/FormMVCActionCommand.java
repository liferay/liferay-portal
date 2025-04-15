/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.bridges.mvc;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

/**
 * @author Tomas Polesovsky
 */
public interface FormMVCActionCommand extends MVCActionCommand {

	public boolean validateForm(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException;

}