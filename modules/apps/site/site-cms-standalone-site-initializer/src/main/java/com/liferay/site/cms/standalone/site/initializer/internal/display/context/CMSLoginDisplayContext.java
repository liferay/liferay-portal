/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.display.context;

import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Jürgen Kappler
 */
public class CMSLoginDisplayContext {

	public CMSLoginDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public String getQueryString() {
		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(_httpServletRequest);

		if (!Objects.equals(
				ParamUtil.getString(originalHttpServletRequest, "p_p_id"),
				LoginPortletKeys.LOGIN)) {

			return StringPool.BLANK;
		}

		return "mvcRenderCommandName=/login/login";
	}

	private final HttpServletRequest _httpServletRequest;

}