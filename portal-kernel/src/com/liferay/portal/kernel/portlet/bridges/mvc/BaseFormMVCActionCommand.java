/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.bridges.mvc;

import com.liferay.portal.kernel.servlet.SessionErrors;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseFormMVCActionCommand
	extends BaseMVCActionCommand implements FormMVCActionCommand {

	@Override
	public boolean validateForm(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			doValidateForm(actionRequest, actionResponse);

			return SessionErrors.isEmpty(actionRequest);
		}
		catch (PortletException portletException) {
			throw portletException;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	protected abstract void doValidateForm(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception;

}