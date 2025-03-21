/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayActionResponse;
import com.liferay.portlet.internal.ActionRequestImpl;
import com.liferay.portlet.internal.ActionResponseImpl;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletException;
import jakarta.portlet.filter.ActionRequestWrapper;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class ActionResponseFactory {

	public static LiferayActionResponse create(
			ActionRequest actionRequest,
			HttpServletResponse httpServletResponse, User user, Layout layout)
		throws PortletException {

		while (actionRequest instanceof ActionRequestWrapper) {
			ActionRequestWrapper actionRequestWrapper =
				(ActionRequestWrapper)actionRequest;

			actionRequest = actionRequestWrapper.getRequest();
		}

		ActionResponseImpl actionResponseImpl = new ActionResponseImpl();

		actionResponseImpl.init(
			(ActionRequestImpl)actionRequest, httpServletResponse, user, layout,
			true);

		return actionResponseImpl;
	}

}