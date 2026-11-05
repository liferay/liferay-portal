/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Jan Brychta
 */
public class ViewTasksDisplayContext {

	public ViewTasksDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		return StringBundler.concat(
			"/o/search/v1.0/search?emptySearch=true&entryClassNames=",
			KaleoTaskInstanceToken.class.getName(),
			"&nestedFields=embedded,embedded.objectReviewed",
			"&nestedFieldsDepth=2");
	}

	public Map<String, Object> getBreadcrumbProps() {
		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems",
			JSONUtil.put(
				JSONUtil.put(
					"active", false
				).put(
					"href", (String)null
				).put(
					"label", _getLayoutName()
				))
		).put(
			"hideSpace", true
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(_httpServletRequest, "there-are-no-tasks")
		).put(
			"title", LanguageUtil.get(_httpServletRequest, "no-tasks")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return FDSActionDropdownItemList.of(
			FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
				FDSActionDropdownItemList.of(
					FDSActionDropdownItemBuilder.setTarget(
						"modal-workflow-transition"
					).build(
						"workflow-transition"
					))
			).setSeparator(
				true
			).setType(
				"group"
			).build(
				"workflow-transitions"
			),
			FDSActionDropdownItemBuilder.setHref(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest, PortletKeys.MY_WORKFLOW_TASK,
						ActionRequest.RENDER_PHASE)
				).setMVCPath(
					"/edit_workflow_task.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"workflowTaskId", "{embedded.id}"
				).buildString()
			).setIcon(
				"view"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "view")
			).setPermissionKey(
				"get"
			).build(
				"actionLinkWorkflowTask"
			),
			FDSActionDropdownItemBuilder.setLabel(
				LanguageUtil.get(_httpServletRequest, "assign-to-me")
			).setPermissionKey(
				"assignToMe"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"embedded.assignedToMe", false
				).put(
					"embedded.completed", false
				).build()
			).build(
				"assignToMeWorkflowTask"
			),
			FDSActionDropdownItemBuilder.setLabel(
				LanguageUtil.get(_httpServletRequest, "assign-to-...")
			).setPermissionKey(
				"assignToUser"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"embedded.completed", false
				).build()
			).build(
				"assignToWorkflowTask"
			),
			FDSActionDropdownItemBuilder.setIcon(
				"date-time"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "update-due-date")
			).setPermissionKey(
				"updateDueDate"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"embedded.completed", false
				).build()
			).build(
				"updateDueDateWorkflowTask"
			));
	}

	private String _getLayoutName() {
		Layout layout = _themeDisplay.getLayout();

		if (layout == null) {
			return null;
		}

		return layout.getName(_themeDisplay.getLocale(), true);
	}

	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}