/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.display.context;

import com.liferay.batch.planner.batch.engine.task.TaskItemUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Matija Petanjek
 */
public abstract class BaseDisplayContext {

	public BaseDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		this.renderRequest = renderRequest;
		this.renderResponse = renderResponse;
		httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	public List<NavigationItem> getNavigationItems() {
		String tabs1 = ParamUtil.getString(
			renderRequest, "tabs1", "batch-planner-plans");

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(tabs1.equals("batch-planner-plans"));
				navigationItem.setHref(
					renderResponse.createRenderURL(), "tabs1",
					"batch-planner-plans");
				navigationItem.setLabel(
					LanguageUtil.get(
						PortalUtil.getHttpServletRequest(renderRequest),
						"import-and-export"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					tabs1.equals("batch-planner-plan-templates"));
				navigationItem.setHref(
					renderResponse.createRenderURL(), "tabs1",
					"batch-planner-plan-templates", "mvcRenderCommandName",
					"/batch_planner/view_batch_planner_plan_templates");
				navigationItem.setLabel(
					LanguageUtil.get(
						PortalUtil.getHttpServletRequest(renderRequest),
						"templates"));
			}
		).build();
	}

	public String getSimpleClassName(String internalClassNameKey) {
		return TaskItemUtil.getSimpleClassName(internalClassNameKey);
	}

	protected boolean isExport(String navigation) {
		return Objects.equals(navigation, "export");
	}

	protected HttpServletRequest httpServletRequest;
	protected RenderRequest renderRequest;
	protected RenderResponse renderResponse;

}