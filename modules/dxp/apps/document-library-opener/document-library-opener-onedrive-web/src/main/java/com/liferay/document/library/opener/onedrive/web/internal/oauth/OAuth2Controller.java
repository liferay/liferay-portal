/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.oauth;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

/**
 * @author Cristina González
 */
public interface OAuth2Controller {

	public void execute(
			PortletRequest portletRequest, PortletResponse portletResponse,
			UnsafeFunction<PortletRequest, JSONObject, PortalException>
				unsafeFunction)
		throws PortalException;

}