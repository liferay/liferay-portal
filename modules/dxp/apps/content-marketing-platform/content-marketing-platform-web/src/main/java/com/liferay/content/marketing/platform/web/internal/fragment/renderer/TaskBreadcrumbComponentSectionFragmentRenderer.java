/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.fragment.renderer;

import com.liferay.content.marketing.platform.web.internal.util.ActionUtil;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(service = FragmentRenderer.class)
public class TaskBreadcrumbComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "task-breadcrumb";
	}

	@Override
	protected String getComponentName(HttpServletRequest httpServletRequest) {
		return "Breadcrumb";
	}

	@Override
	protected String getLabelKey() {
		return "task-breadcrumb";
	}

	@Override
	protected String getModuleName() {
		return "site-cms-site-initializer";
	}

	@Override
	protected Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception {

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		if (layoutDisplayPageObjectProvider == null) {
			return null;
		}

		Object displayObject =
			layoutDisplayPageObjectProvider.getDisplayObject();

		if (!(displayObject instanceof ObjectEntry)) {
			return null;
		}

		ObjectEntry objectEntry = (ObjectEntry)displayObject;

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());
		long parentObjectEntryId = MapUtil.getLong(
			objectEntry.getValues(), "r_cmpProjectToCMPTasks_c_cmpProjectId");
		String title = MapUtil.getString(objectEntry.getValues(), "title");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"actionItems",
			() -> {
				JSONArray jsonArray = _jsonFactory.createJSONArray();

				if (objectEntryService.hasModelResourcePermission(
						objectEntry, ActionKeys.UPDATE)) {

					jsonArray.put(
						JSONUtil.put(
							"href",
							StringBundler.concat(
								ActionUtil.getBaseEditTaskURL(
									objectDefinition, themeDisplay),
								objectEntry.getObjectEntryId(), "?redirect=",
								themeDisplay.getURLCurrent())
						).put(
							"label",
							LanguageUtil.get(httpServletRequest, "edit")
						).put(
							"symbolLeft", "pencil"
						));
				}

				String viewTaskURL = ActionUtil.getBaseViewTaskURL(
					objectDefinition, themeDisplay);

				addSubscribeActionItem(
					httpServletRequest, jsonArray, "watch-task",
					"stop-watching-task", objectDefinition, objectEntry,
					viewTaskURL + objectEntry.getObjectEntryId(), themeDisplay,
					title);

				if (objectEntryService.hasModelResourcePermission(
						objectEntry, ActionKeys.DELETE)) {

					jsonArray.put(
						JSONUtil.put(
							"className", "text-danger"
						).put(
							"confirmationMessage",
							_getDeleteConfirmationMessage(
								httpServletRequest, objectEntry, title)
						).put(
							"confirmationTitle",
							LanguageUtil.format(
								httpServletRequest,
								"delete-asset-confirmation-title", title)
						).put(
							"href",
							StringBundler.concat(
								"/o", objectDefinition.getRESTContextPath(),
								StringPool.SLASH,
								objectEntry.getObjectEntryId())
						).put(
							"label",
							LanguageUtil.get(httpServletRequest, "delete")
						).put(
							"redirect", ActionUtil.getProjectsURL(themeDisplay)
						).put(
							"successMessage",
							LanguageUtil.format(
								httpServletRequest,
								"x-was-successfully-deleted",
								StringBundler.concat(
									"<strong>", title, "</strong>"))
						).put(
							"symbolLeft", "trash"
						).put(
							"target", "asyncDelete"
						));
				}

				if (jsonArray.length() == 0) {
					return null;
				}

				return jsonArray;
			}
		).put(
			"breadcrumbItems",
			JSONUtil.putAll(
				JSONUtil.put(
					"active", false
				).put(
					"href", ActionUtil.getProjectsURL(themeDisplay)
				).put(
					"label", LanguageUtil.get(httpServletRequest, "projects")
				),
				JSONUtil.put(
					"active", false
				).put(
					"href",
					() -> {
						String viewProjectURL =
							ActionUtil.getBaseViewProjectURL(
								_objectDefinitionLocalService.
									fetchObjectDefinitionByExternalReferenceCode(
										"L_CMP_PROJECT",
										themeDisplay.getCompanyId()),
								themeDisplay);

						return viewProjectURL + parentObjectEntryId;
					}
				).put(
					"label",
					() -> {
						ObjectEntry parentObjectEntry =
							objectEntryService.getObjectEntry(
								parentObjectEntryId);

						return MapUtil.getString(
							parentObjectEntry.getValues(), "title");
					}
				),
				JSONUtil.put(
					"active", true
				).put(
					"href", StringPool.BLANK
				).put(
					"label", title
				))
		).put(
			"hideSpace", true
		).put(
			"size", "lg"
		).build();
	}

	private String _getDeleteConfirmationMessage(
			HttpServletRequest httpServletRequest, ObjectEntry objectEntry,
			String title)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-107678")) {

			return LanguageUtil.format(
				httpServletRequest, "delete-task-confirmation-body", title);
		}

		int descendantCMPTaskObjectEntriesCount =
			_getDescendantCMPTaskObjectEntriesCount(objectEntry);

		if (descendantCMPTaskObjectEntriesCount == 0) {
			return LanguageUtil.format(
				httpServletRequest, "delete-task-confirmation-body", title);
		}

		if (descendantCMPTaskObjectEntriesCount == 1) {
			return LanguageUtil.format(
				httpServletRequest,
				"delete-task-and-one-subtask-confirmation-body", title);
		}

		return LanguageUtil.format(
			httpServletRequest, "delete-task-and-x-subtasks-confirmation-body",
			new Object[] {title, descendantCMPTaskObjectEntriesCount});
	}

	private int _getDescendantCMPTaskObjectEntriesCount(
			ObjectEntry cmpTaskObjectEntry)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				cmpTaskObjectEntry.getObjectDefinitionId(),
				"cmpTaskToCMPTasks");

		List<ObjectEntry> childCMPTaskObjectEntries =
			_objectEntryLocalService.getRelatedObjectEntries(
				cmpTaskObjectEntry.getGroupId(),
				objectRelationship.getObjectRelationshipId(),
				ObjectEntryTable.INSTANCE.status.neq(
					WorkflowConstants.STATUS_DRAFT),
				false, cmpTaskObjectEntry.getObjectEntryId(), true, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		int count = childCMPTaskObjectEntries.size();

		for (ObjectEntry childCMPTaskObjectEntry : childCMPTaskObjectEntries) {
			count += _objectEntryLocalService.getRelatedObjectEntriesCount(
				childCMPTaskObjectEntry.getGroupId(),
				objectRelationship.getObjectRelationshipId(),
				ObjectEntryTable.INSTANCE.status.neq(
					WorkflowConstants.STATUS_DRAFT),
				childCMPTaskObjectEntry.getObjectEntryId(), true, null);
		}

		return count;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}