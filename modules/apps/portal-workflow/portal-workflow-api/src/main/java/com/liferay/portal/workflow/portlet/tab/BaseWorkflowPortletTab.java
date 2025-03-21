/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.portlet.tab;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam Brandizzi
 */
public abstract class BaseWorkflowPortletTab
	extends BaseJSPDynamicInclude implements WorkflowPortletTab {

	@Override
	public PortletURL getSearchURL(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCPath(
			"/view.jsp"
		).setParameter(
			"groupId",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return themeDisplay.getScopeGroupId();
			}
		).setParameter(
			"tab", getName()
		).buildPortletURL();
	}

	@Override
	public void prepareDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {
	}

	@Override
	public void prepareProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {
	}

	@Override
	public void prepareRender(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {
	}

	@Override
	protected Log getLog() {
		Class<? extends BaseWorkflowPortletTab> clazz = getClass();

		if (!_logs.containsKey(clazz)) {
			_logs.put(clazz, LogFactoryUtil.getLog(clazz));
		}

		return _logs.get(clazz);
	}

	private static final Map<Class<? extends BaseWorkflowPortletTab>, Log>
		_logs = new HashMap<>();

}