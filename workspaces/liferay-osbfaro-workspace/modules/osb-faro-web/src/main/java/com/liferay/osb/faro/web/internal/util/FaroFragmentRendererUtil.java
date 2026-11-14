/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.osb.faro.constants.FaroChannelConstants;
import com.liferay.osb.faro.engine.client.constants.LCPProjectConstants;
import com.liferay.osb.faro.provisioning.client.constants.FaroSubscriptionConstants;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.osb.faro.web.internal.constants.FaroPaginationConstants;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.taglib.aui.LinkTag;
import com.liferay.taglib.aui.ScriptTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Eudaldo Alonso
 */
public class FaroFragmentRendererUtil {

	public static String getFaroConstants() throws Exception {
		return JSONUtil.writeValueAsString(
			HashMapBuilder.<String, Object>put(
				"channelPermissionTypes",
				FaroChannelConstants.getPermissionTypes()
			).put(
				"faroURL", FaroPropsValues.FARO_URL
			).put(
				"pagination", FaroPaginationConstants.getConstants()
			).put(
				"projectLocations", LCPProjectConstants.getLocations()
			).put(
				"subscriptionPlans",
				FaroSubscriptionConstants.getFaroSubscriptionPlans()
			).build());
	}

	public static String getStaticResourceURL(
		HttpServletRequest httpServletRequest, String uri) {

		return HtmlUtil.escapeAttribute(
			PortalUtil.getStaticResourceURL(httpServletRequest, uri));
	}

	public static void renderLinkTag(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String uri)
		throws JspException {

		LinkTag linkTag = new LinkTag();

		linkTag.setHref(getStaticResourceURL(httpServletRequest, uri));
		linkTag.setRel("stylesheet");
		linkTag.setType("text/css");

		linkTag.doTag(httpServletRequest, httpServletResponse);
	}

	public static void renderScriptTag(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String position,
			String script, String src, String type)
		throws JspException {

		ScriptTag scriptTag = new ScriptTag();

		scriptTag.setPosition(position);
		scriptTag.setSrc(src);
		scriptTag.setType(type);

		scriptTag.doBodyTag(
			httpServletRequest, httpServletResponse,
			pageContext -> {
				if (script == null) {
					return;
				}

				try {
					Writer writer = pageContext.getOut();

					writer.write(script);
				}
				catch (IOException ioException) {
					ReflectionUtil.throwException(ioException);
				}
			});
	}

}