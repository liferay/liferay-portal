/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.portlet.tab.WorkflowPortletTab;
import com.liferay.portal.workflow.web.internal.display.context.helper.WorkflowNavigationRequestHelper;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Jeyvison Nascimento
 */
public class WorkflowNavigationDisplayContext {

	public WorkflowNavigationDisplayContext(
		RenderRequest renderRequest,
		ResourceBundleLoader resourceBundleLoader) {

		_workflowNavigationRequestHelper = new WorkflowNavigationRequestHelper(
			renderRequest);

		_resourceBundleLoader = resourceBundleLoader;
	}

	public List<NavigationItem> getNavigationItems(
		WorkflowPortletTab selectedWorkflowPortletTab,
		List<WorkflowPortletTab> workflowPortletTabs) {

		if (workflowPortletTabs.size() == 1) {
			return Collections.emptyList();
		}

		ResourceBundle resourceBundle =
			_resourceBundleLoader.loadResourceBundle(
				_workflowNavigationRequestHelper.getLocale());

		LiferayPortletResponse liferayPortletResponse =
			_workflowNavigationRequestHelper.getLiferayPortletResponse();

		PortletURL renderURL = liferayPortletResponse.createRenderURL(
			WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW);

		return new NavigationItemList() {
			{
				for (WorkflowPortletTab workflowPortletTab :
						workflowPortletTabs) {

					add(
						navigationItem -> {
							navigationItem.setActive(
								Objects.equals(
									workflowPortletTab.getName(),
									selectedWorkflowPortletTab.getName()));
							navigationItem.setHref(
								renderURL, "tab", workflowPortletTab.getName());
							navigationItem.setLabel(
								LanguageUtil.get(
									resourceBundle,
									workflowPortletTab.getName()));
						});
				}
			}
		};
	}

	private final ResourceBundleLoader _resourceBundleLoader;
	private final WorkflowNavigationRequestHelper
		_workflowNavigationRequestHelper;

}