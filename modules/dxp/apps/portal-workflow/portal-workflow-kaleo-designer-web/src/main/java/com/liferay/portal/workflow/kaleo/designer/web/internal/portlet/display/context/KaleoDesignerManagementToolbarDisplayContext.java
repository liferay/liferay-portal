/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.portlet.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.workflow.kaleo.designer.web.constants.KaleoDesignerPortletKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class KaleoDesignerManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public KaleoDesignerManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, int status,
		KaleoDesignerDisplayContext kaleoDesignerDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			kaleoDesignerDisplayContext.getKaleoDefinitionVersionSearch(
				status));

		_kaleoDesignerDisplayContext = kaleoDesignerDisplayContext;
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
		if (!_kaleoDesignerDisplayContext.canPublishWorkflowDefinition() ||
			!_kaleoDesignerDisplayContext.
				isSaveKaleoDefinitionVersionButtonVisible(null)) {

			return null;
		}

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(
						KaleoDesignerPortletKeys.KALEO_DESIGNER),
					"mvcPath", "/designer/edit_workflow_definition.jsp",
					"redirect", PortalUtil.getCurrentURL(httpServletRequest),
					"clearSessionMessage", "true");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "new-workflow"));
			}
		).build();
	}

	@Override
	public String getOrderByCol() {
		return ParamUtil.getString(
			httpServletRequest, "orderByCol", "last-modified");
	}

	@Override
	public String getSearchActionURL() {
		return String.valueOf(getPortletURL());
	}

	@Override
	public String getSearchContainerId() {
		return "kaleoDefinitionVersions";
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "not-published", "published"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"last-modified", "title"};
	}

	private final KaleoDesignerDisplayContext _kaleoDesignerDisplayContext;

}