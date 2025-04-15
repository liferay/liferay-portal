/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseBlogsPortlet extends MVCPortlet {

	@Override
	protected boolean isAddSuccessMessage(ActionRequest actionRequest) {
		boolean ajax = ParamUtil.getBoolean(actionRequest, "ajax");

		if (ajax) {
			return false;
		}

		return super.isAddSuccessMessage(actionRequest);
	}

}