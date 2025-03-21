/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolvedPackageNameUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import java.util.ResourceBundle;

/**
 * @author Adolfo Pérez
 */
public class DefineObjectsTag extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest != null) {
			LiferayPortletRequest liferayPortletRequest =
				PortalUtil.getLiferayPortletRequest(portletRequest);

			PortletResponse portletResponse =
				(PortletResponse)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_RESPONSE);

			if (portletResponse != null) {
				PortletURL currentURLObj = PortletURLUtil.getCurrent(
					liferayPortletRequest,
					PortalUtil.getLiferayPortletResponse(portletResponse));

				pageContext.setAttribute(
					"currentURL", currentURLObj.toString());
				pageContext.setAttribute("currentURLObj", currentURLObj);
			}

			pageContext.setAttribute(
				"windowState", liferayPortletRequest.getWindowState());
		}

		String npmResolvedPackageName = NPMResolvedPackageNameUtil.get(
			pageContext.getServletContext());

		if (Validator.isNotNull(npmResolvedPackageName)) {
			pageContext.setAttribute(
				"npmResolvedPackageName", npmResolvedPackageName);
		}

		if (_overrideResourceBundle != null) {
			pageContext.setAttribute("resourceBundle", _overrideResourceBundle);
		}
		else {
			pageContext.setAttribute(
				"resourceBundle",
				TagResourceBundleUtil.getResourceBundle(
					httpServletRequest,
					PortalUtil.getLocale(httpServletRequest)));
		}

		return SKIP_BODY;
	}

	public void setOverrideResourceBundle(
		ResourceBundle overrideResourceBundle) {

		_overrideResourceBundle = overrideResourceBundle;
	}

	private ResourceBundle _overrideResourceBundle;

}