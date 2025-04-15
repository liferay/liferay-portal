/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.portlet;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class DefineObjectsTei extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData tagData) {
		return Concealer._variableInfo;
	}

	private static class Concealer {

		private static final VariableInfo[] _variableInfo = {
			new VariableInfo(
				"actionRequest", ActionRequest.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"actionResponse", ActionResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"eventRequest", EventRequest.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"eventResponse", EventResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"liferayPortletRequest", LiferayPortletRequest.class.getName(),
				true, VariableInfo.AT_END),
			new VariableInfo(
				"liferayPortletResponse",
				LiferayPortletResponse.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletConfig", PortletConfig.class.getName(), true,
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
				"portletSession", PortletSession.class.getName(), true,
				VariableInfo.AT_END),
			new VariableInfo(
				"portletSessionScope", Map.class.getName(), true,
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
				VariableInfo.AT_END)
		};

	}

}