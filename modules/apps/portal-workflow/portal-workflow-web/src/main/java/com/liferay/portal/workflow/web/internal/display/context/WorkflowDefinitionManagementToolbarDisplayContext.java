/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class WorkflowDefinitionManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public WorkflowDefinitionManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			RenderRequest renderRequest, int status,
			WorkflowDefinitionDisplayContext workflowDefinitionDisplayContext)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			workflowDefinitionDisplayContext.getSearch(
				httpServletRequest, renderRequest, status));

		_workflowDefinitionDisplayContext = workflowDefinitionDisplayContext;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (!_workflowDefinitionDisplayContext.canPublishWorkflowDefinition()) {
			return null;
		}

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(), "mvcPath",
					"/definition/edit_workflow_definition.jsp");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "new-workflow"));
			}
		).build();
	}

	@Override
	public String getOrderByCol() {
		return _workflowDefinitionDisplayContext.getOrderByCol();
	}

	@Override
	public String getOrderByType() {
		return _workflowDefinitionDisplayContext.getOrderByType();
	}

	@Override
	public String getSearchActionURL() {
		return String.valueOf(getPortletURL());
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "published", "not-published"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"last-modified", "title"};
	}

	private final WorkflowDefinitionDisplayContext
		_workflowDefinitionDisplayContext;

}