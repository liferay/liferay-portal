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
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.site.cmp.site.initializer.internal.constants.CMPActionConstants;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.AssigneeSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.CMPProjectSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.CreateDateFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.DueDateRangeFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.StateSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter.TagSelectionFDSFilter;
import com.liferay.site.cmp.site.initializer.internal.util.ActionUtil;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Fábio Alves
 */
public abstract class BaseTasksSectionDisplayContext
	extends BaseSectionDisplayContext {

	public BaseTasksSectionDisplayContext(
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

		super(httpServletRequest, cmpTaskObjectDefinition, objectEntryService);

		this.assetTagLocalService = assetTagLocalService;
		this.classNameLocalService = classNameLocalService;
		this.cmpProjectObjectDefinition = cmpProjectObjectDefinition;
		this.depotEntryLocalService = depotEntryLocalService;
		this.listTypeEntryLocalService = listTypeEntryLocalService;
		this.objectFieldLocalService = objectFieldLocalService;
		this.objectStateFlowLocalService = objectStateFlowLocalService;
		this.objectStateLocalService = objectStateLocalService;
		this.roleService = roleService;
	}

	public Map<String, Object> getAdditionalProps() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"cmpProjectObjectDefinitionId",
			cmpProjectObjectDefinition.getObjectDefinitionId()
		).put(
			"cmpProjectObjectEntryId",
			() -> {
				if (assetEntry == null) {
					return null;
				}

				return assetEntry.getClassPK();
			}
		).put(
			"hasAddTaskPermission", hasAddObjectEntryPortletResourcePermission()
		).put(
			"states",
			() -> {
				JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

				ObjectField objectField =
					objectFieldLocalService.fetchObjectField(
						objectDefinition.getObjectDefinitionId(), "state");

				if (objectField == null) {
					return jsonArray;
				}

				for (ListTypeEntry listTypeEntry :
						listTypeEntryLocalService.getListTypeEntries(
							objectField.getListTypeDefinitionId())) {

					jsonArray.put(
						JSONUtil.put(
							"key", listTypeEntry.getKey()
						).put(
							"name",
							listTypeEntry.getName(themeDisplay.getLocale())
						).put(
							"nextStates",
							_getNextStatesJSONArray(listTypeEntry, objectField)
						));
				}

				return jsonArray;
			}
		).build();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setIcon(
				"date-time"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "update-due-date")
			).setMethod(
				"post"
			).setPermissionKey(
				"update"
			).build(
				"update-due-date"
			),
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setHref(
				"#"
			).setIcon(
				"user"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "assign-to-...")
			).setPermissionKey(
				"update"
			).build(
				"assign-to"
			),
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setIcon(
				"arrow-start"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "update-state")
			).setMethod(
				"post"
			).setPermissionKey(
				"update"
			).build(
				"update-state"
			),
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setHref(
				"#"
			).setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "delete")
			).setPermissionKey(
				"delete"
			).build(
				"delete"
			));
	}

	@Override
	public CreationMenu getCreationMenu() throws Exception {
		if (!hasAddObjectEntryPortletResourcePermission()) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", CMPActionConstants.CREATE_TASK);
				dropdownItem.putData(
					"addProjectURL",
					StringBundler.concat(
						ActionUtil.getAddProjectURL(
							cmpProjectObjectDefinition, themeDisplay),
						"&action=",
						CMPActionConstants.CREATE_PROJECT_GLOBAL_TASK));
				dropdownItem.putData(
					"addTaskURL",
					StringBundler.concat(
						ActionUtil.getAddTaskURL(
							0, objectDefinition, 0, themeDisplay),
						"&action=", CMPActionConstants.CREATE_GLOBAL_TASK));
				dropdownItem.putData(
					"cmpProjectObjectDefinitionId",
					String.valueOf(
						cmpProjectObjectDefinition.getObjectDefinitionId()));
				dropdownItem.putData(
					"objectDefinitionId",
					String.valueOf(objectDefinition.getObjectDefinitionId()));

				if (assetEntry != null) {
					dropdownItem.putData(
						"redirect",
						ActionUtil.getAddTaskURL(
							assetEntry.getGroupId(), objectDefinition,
							assetEntry.getClassPK(), themeDisplay));
				}

				dropdownItem.putData(
					"title",
					objectDefinition.getLabel(themeDisplay.getLocale()));
				dropdownItem.setIcon("forms");
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						(assetEntry == null) ? "new" : "new-task"));
			}
		).build();
	}

	@Override
	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				httpServletRequest, "click-new-to-create-your-first-task")
		).put(
			"image", "/states/cmp_empty_state_tasks.svg"
		).put(
			"title", LanguageUtil.get(httpServletRequest, "no-tasks-yet")
		).build();
	}

	public List<FDSFilter> getFDSFilters() {
		List<FDSFilter> fdsFilters = new ArrayList<>();

		fdsFilters.add(
			new AssigneeSelectionFDSFilter(
				classNameLocalService,
				cmpProjectObjectDefinition.getCompanyId(), roleService));
		fdsFilters.add(new CreateDateFDSFilter());
		fdsFilters.add(new DueDateRangeFDSFilter());

		if (assetEntry == null) {
			fdsFilters.add(
				new CMPProjectSelectionFDSFilter(cmpProjectObjectDefinition));
		}

		fdsFilters.add(new StateSelectionFDSFilter());
		fdsFilters.add(
			new TagSelectionFDSFilter(
				assetTagLocalService, cmpProjectObjectDefinition,
				depotEntryLocalService, assetEntry));

		return fdsFilters;
	}

	public List<FDSActionDropdownItem> getProjectTasksFDSActionDropdownItems(
		String entryClassName) {

		Map<String, Object> visibilityFilters =
			HashMapBuilder.<String, Object>put(
				"entryClassName", entryClassName
			).build();

		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					ActionUtil.getBaseEditTaskURL(
						objectDefinition, themeDisplay),
					"{embedded.id}?redirect=", themeDisplay.getURLCurrent())
			).setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "edit")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"edit"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					ActionUtil.getBaseViewTaskURL(
						objectDefinition, themeDisplay),
					"{embedded.id}?redirect=", themeDisplay.getURLCurrent())
			).setIcon(
				"view"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "view")
			).setPermissionKey(
				"get"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"actionLink"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					"/o", objectDefinition.getRESTContextPath(),
					"/scopes/{embedded.scopeId}/by-external-reference-code",
					"/{embedded.externalReferenceCode}/subscribe")
			).setIcon(
				"bell-on"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "watch-task")
			).setMethod(
				"post"
			).setPermissionKey(
				"subscribe"
			).setTarget(
				"async"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"subscribe"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					"/o", objectDefinition.getRESTContextPath(),
					"/scopes/{embedded.scopeId}/by-external-reference-code",
					"/{embedded.externalReferenceCode}/unsubscribe")
			).setIcon(
				"bell-off"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "stop-watching-task")
			).setMethod(
				"post"
			).setPermissionKey(
				"unsubscribe"
			).setTarget(
				"async"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"unsubscribe"
			),
			FDSActionDropdownItemBuilder.setLabel(
				LanguageUtil.get(httpServletRequest, "assign-to-...")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"assign-to"
			),
			FDSActionDropdownItemBuilder.setIcon(
				"date-time"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "update-due-date")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"update-due-date"
			),
			FDSActionDropdownItemBuilder.setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "delete")
			).setPermissionKey(
				"delete"
			).setVisibilityFilters(
				visibilityFilters
			).build(
				"delete"
			));
	}

	public String getSelectionType() throws Exception {
		if (!hasAddObjectEntryPortletResourcePermission()) {
			return null;
		}

		return "multiple";
	}

	public List<FDSActionDropdownItem>
		getWorkflowTasksFDSActionDropdownItems() {

		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setHref(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest, PortletKeys.MY_WORKFLOW_TASK,
						ActionRequest.RENDER_PHASE)
				).setMVCPath(
					"/edit_workflow_task.jsp"
				).setRedirect(
					themeDisplay.getURLCurrent()
				).setParameter(
					"workflowTaskId", "{embedded.id}"
				).buildString()
			).setIcon(
				"view"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "view")
			).setPermissionKey(
				"get"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"entryClassName", KaleoTaskInstanceToken.class.getName()
				).build()
			).build(
				"actionLinkWorkflowTask"
			),
			FDSActionDropdownItemBuilder.setLabel(
				LanguageUtil.get(httpServletRequest, "assign-to-me")
			).setPermissionKey(
				"assignToMe"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"embedded.assignedToMe", false
				).put(
					"embedded.completed", false
				).put(
					"entryClassName", KaleoTaskInstanceToken.class.getName()
				).build()
			).build(
				"assignToMeWorkflowTask"
			),
			FDSActionDropdownItemBuilder.setLabel(
				LanguageUtil.get(httpServletRequest, "assign-to-...")
			).setPermissionKey(
				"assignToUser"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"embedded.completed", false
				).put(
					"entryClassName", KaleoTaskInstanceToken.class.getName()
				).build()
			).build(
				"assignToWorkflowTask"
			),
			FDSActionDropdownItemBuilder.setIcon(
				"date-time"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "update-due-date")
			).setPermissionKey(
				"updateDueDate"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"embedded.completed", false
				).put(
					"entryClassName", KaleoTaskInstanceToken.class.getName()
				).build()
			).build(
				"updateDueDateWorkflowTask"
			));
	}

	protected FDSActionDropdownItem
		getWorkflowTransitionsGroupFDSActionDropdownItem() {

		return FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
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
		);
	}

	protected final AssetTagLocalService assetTagLocalService;
	protected final ClassNameLocalService classNameLocalService;
	protected final ObjectDefinition cmpProjectObjectDefinition;
	protected final DepotEntryLocalService depotEntryLocalService;
	protected final ListTypeEntryLocalService listTypeEntryLocalService;
	protected final ObjectFieldLocalService objectFieldLocalService;
	protected final ObjectStateFlowLocalService objectStateFlowLocalService;
	protected final ObjectStateLocalService objectStateLocalService;
	protected final RoleService roleService;

	private JSONArray _getNextStatesJSONArray(
		ListTypeEntry currentListTypeEntry, ObjectField objectField) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		ObjectStateFlow objectStateFlow =
			objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField.getObjectFieldId());

		if (objectStateFlow == null) {
			return jsonArray;
		}

		ObjectState objectState =
			objectStateLocalService.fetchObjectStateFlowObjectState(
				currentListTypeEntry.getListTypeEntryId(),
				objectStateFlow.getObjectStateFlowId());

		if (objectState == null) {
			return jsonArray;
		}

		for (ObjectState nextObjectState :
				objectStateLocalService.getNextObjectStates(
					objectState.getObjectStateId())) {

			ListTypeEntry nextListTypeEntry =
				listTypeEntryLocalService.fetchListTypeEntry(
					nextObjectState.getListTypeEntryId());

			jsonArray.put(nextListTypeEntry.getKey());
		}

		return jsonArray;
	}

}