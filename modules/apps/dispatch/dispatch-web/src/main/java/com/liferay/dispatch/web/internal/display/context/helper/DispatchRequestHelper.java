/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.display.context.helper;

import com.liferay.dispatch.constants.DispatchWebKeys;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author guywandji
 */
public class DispatchRequestHelper extends BaseRequestHelper {

	public DispatchRequestHelper(RenderRequest renderRequest) {
		super(PortalUtil.getHttpServletRequest(renderRequest));
	}

	public DispatchTrigger getDispatchTrigger() {
		HttpServletRequest httpServletRequest = getRequest();

		return (DispatchTrigger)httpServletRequest.getAttribute(
			DispatchWebKeys.DISPATCH_TRIGGER);
	}

}