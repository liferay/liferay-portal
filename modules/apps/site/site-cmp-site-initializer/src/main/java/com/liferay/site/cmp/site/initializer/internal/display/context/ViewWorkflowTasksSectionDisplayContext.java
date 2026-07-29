/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.AssigneeSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.WorkflowTaskDueDateRangeFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.WorkflowTaskStatusSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.util.WorkflowTaskSearchURLUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Fábio Alves
 */
public class ViewWorkflowTasksSectionDisplayContext
	extends BaseTasksSectionDisplayContext {

	public ViewWorkflowTasksSectionDisplayContext(
		AssetTagLocalService assetTagLocalService,
		ClassNameLocalService classNameLocalService,
		ObjectDefinition cmpProjectObjectDefinition,
		ObjectDefinition cmpTaskObjectDefinition,
		DepotEntryLocalService depotEntryLocalService,
		HttpServletRequest httpServletRequest,
		ListTypeEntryLocalService listTypeEntryLocalService,
		ObjectEntryService objectEntryService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectStateFlowLocalService objectStateFlowLocalService,
		ObjectStateLocalService objectStateLocalService,
		RoleService roleService) {

		super(
			assetTagLocalService, classNameLocalService,
			cmpProjectObjectDefinition, cmpTaskObjectDefinition,
			depotEntryLocalService, httpServletRequest,
			listTypeEntryLocalService, objectEntryService,
			objectFieldLocalService, objectStateFlowLocalService,
			objectStateLocalService, roleService);
	}

	@Override
	public String getAPIURL() {
		return WorkflowTaskSearchURLUtil.getSearchURL() +
			"&nestedFields=embedded";
	}

	@Override
	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setIcon(
				"date-time"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "update-due-date")
			).setPermissionKey(
				"updateDueDate"
			).build(
				"update-due-date"
			),
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setIcon(
				"user"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "assign-to-...")
			).setPermissionKey(
				"assignToUser"
			).build(
				"assign-to"
			));
	}

	@Override
	public CreationMenu getCreationMenu() {
		return null;
	}

	@Override
	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				httpServletRequest,
				"there-are-no-workflow-tasks-related-to-your-projects")
		).put(
			"image", "/states/cmp_empty_state_tasks.svg"
		).put(
			"title", LanguageUtil.get(httpServletRequest, "no-tasks")
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return FDSActionDropdownItemList.of(
			getWorkflowTransitionsGroupFDSActionDropdownItem(),
			FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
				getWorkflowTasksFDSActionDropdownItems()
			).setSeparator(
				true
			).setType(
				"group"
			).build(
				"other-actions"
			));
	}

	@Override
	public List<FDSFilter> getFDSFilters() {
		return ListUtil.fromArray(
			new AssigneeSelectionFDSFilter(
				classNameLocalService,
				cmpProjectObjectDefinition.getCompanyId(), roleService),
			new WorkflowTaskDueDateRangeFDSFilter(),
			new WorkflowTaskStatusSelectionFDSFilter());
	}

}