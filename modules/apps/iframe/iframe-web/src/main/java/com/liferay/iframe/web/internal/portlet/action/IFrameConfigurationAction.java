/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.iframe.web.internal.portlet.action;

import com.liferay.iframe.web.internal.constants.IFramePortletKeys;
import com.liferay.iframe.web.internal.util.IFrameUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ReadOnlyException;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "jakarta.portlet.name=" + IFramePortletKeys.IFRAME,
	service = ConfigurationAction.class
)
public class IFrameConfigurationAction extends DefaultConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String src = getParameter(actionRequest, "src");

		if (!src.startsWith("/") && !StringUtil.startsWith(src, "http://") &&
			!StringUtil.startsWith(src, "https://") &&
			!StringUtil.startsWith(src, "mhtml://")) {

			src = HttpComponentsUtil.getProtocol(actionRequest) + "://" + src;

			setPreference(actionRequest, "src", src);
		}

		String[] htmlAttributes = StringUtil.splitLines(
			getParameter(actionRequest, "htmlAttributes"));

		for (String htmlAttribute : htmlAttributes) {
			int pos = htmlAttribute.indexOf(CharPool.EQUAL);

			if (pos == -1) {
				continue;
			}

			String key = htmlAttribute.substring(0, pos);
			String value = htmlAttribute.substring(pos + 1);

			setPreference(actionRequest, key, value);
		}

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Override
	protected void postProcess(
			long companyId, PortletRequest portletRequest,
			PortletPreferences portletPreferences)
		throws PortalException {

		String formPassword = portletPreferences.getValue(
			"formPassword", StringPool.BLANK);

		if (Validator.isNotNull(formPassword) &&
			formPassword.contains("@password@") &&
			!IFrameUtil.isPasswordTokenEnabled(portletRequest)) {

			formPassword = formPassword.replaceAll("@password@", "");

			try {
				portletPreferences.setValue("formPassword", formPassword);
			}
			catch (ReadOnlyException readOnlyException) {
				throw new PortalException(readOnlyException);
			}
		}
	}

}