/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewHomeWorkflowTasksDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Christian Dorado
 */
@Component(service = FragmentRenderer.class)
public class ViewHomeWorkflowTasksJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<ViewHomeWorkflowTasksDisplayContext> {

	@Override
	public String getLabelKey() {
		return "home-workflow-tasks";
	}

	@Override
	protected ViewHomeWorkflowTasksDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewHomeWorkflowTasksDisplayContext(
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY));
	}

	@Override
	protected String getJSPPath() {
		return "/view_home_workflow_tasks.jsp";
	}

}