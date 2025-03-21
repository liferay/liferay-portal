/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.portlet;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.portlet.ActionParameters;
import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.ClientDataRequest;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderParameters;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceParameters;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.StateAwareResponse;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

import java.util.Locale;
import java.util.Map;

/**
 * @author Neil Griffin
 */
public class DefineObjectsTei3 extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData tagData) {
		return Concealer._variableInfo;
	}

	private static class Concealer {

		private static final VariableInfo[] _variableInfo = {
			new VariableInfo(
				"actionParams", ActionParameters.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"actionRequest", ActionRequest.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"actionResponse", ActionResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"clientDataRequest", ClientDataRequest.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"contextPath", String.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"cookies", Cookie[].class.getCanonicalName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"eventRequest", EventRequest.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"eventResponse", EventResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"headerRequest", HeaderRequest.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"headerResponse", HeaderResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"liferayPortletRequest", LiferayPortletRequest.class.getName(),
				true, VariableInfo.AT_END),
			new VariableInfo(
				"liferayPortletResponse",
				LiferayPortletResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"locale", Locale.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"locales", Locale[].class.getCanonicalName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"mimeResponse", MimeResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"mutableRenderParams", MutableRenderParameters.class.getName(),
				true, VariableInfo.AT_END),
			new VariableInfo(
				"namespace", String.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"portletConfig", PortletConfig.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletContext", PortletContext.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletMode", PortletMode.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletName", String.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletPreferences", PortletPreferences.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletPreferencesValues", Map.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletRequest", PortletRequest.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletResponse", PortletResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletSession", PortletSession.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletSessionScope", Map.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"renderParams", RenderParameters.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"renderRequest", RenderRequest.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"renderResponse", RenderResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"resourceRequest", ResourceRequest.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"resourceResponse", ResourceResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"resourceParams", ResourceParameters.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"stateAwareResponse", StateAwareResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"windowId", String.class.getName(), true, VariableInfo.AT_END),
			new VariableInfo(
				"windowState", WindowState.class.getName(), true,
				VariableInfo.AT_END)
		};

	}

}