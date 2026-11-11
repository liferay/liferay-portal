/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.fragment.renderer;

import com.liferay.content.marketing.platform.web.internal.constants.CMPActionConstants;
import com.liferay.content.marketing.platform.web.internal.util.ActionUtil;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Franca
 */
@Component(service = FragmentRenderer.class)
public class EditorToolbarComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "project-editor";
	}

	@Override
	protected String getComponentName(HttpServletRequest httpServletRequest) {
		return "EditorToolbar";
	}

	@Override
	protected String getLabelKey() {
		return "editor-management-bar";
	}

	@Override
	protected String getModuleName() {
		return "content-marketing-platform-web";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		ObjectEntry objectEntry = _getObjectEntry(httpServletRequest);

		if (objectEntry == null) {
			return Collections.emptyMap();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (objectDefinition == null) {
			return Collections.emptyMap();
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String externalReferenceCode =
			objectDefinition.getExternalReferenceCode();

		ObjectEntry parentCMPTaskObjectEntry = _getParentCMPTaskObjectEntry(
			externalReferenceCode, objectEntry);

		return HashMapBuilder.<String, Object>put(
			"backURL", ParamUtil.getString(httpServletRequest, "redirect")
		).put(
			"discardURL",
			() -> {
				if (objectEntry.isDraft() &&
					(Objects.equals(externalReferenceCode, "L_CMP_PROJECT") ||
					 Objects.equals(externalReferenceCode, "L_CMP_TASK"))) {

					return StringBundler.concat(
						"/o", objectDefinition.getRESTContextPath(),
						StringPool.SLASH, objectEntry.getObjectEntryId());
				}

				return null;
			}
		).put(
			"formSubmitURL",
			() -> {
				String action = ParamUtil.getString(
					httpServletRequest, "action");

				if (StringUtil.equals(
						action, CMPActionConstants.CREATE_GLOBAL_TASK)) {

					return ParamUtil.getString(httpServletRequest, "redirect");
				}

				if (!objectEntry.isDraft()) {
					return null;
				}

				if (StringUtil.equals(
						action,
						CMPActionConstants.CREATE_PROJECT_GLOBAL_TASK)) {

					return StringBundler.concat(
						ActionUtil.getAddTaskURL(
							objectEntry.getGroupId(),
							_objectDefinitionLocalService.
								getObjectDefinitionByExternalReferenceCode(
									"L_CMP_TASK", themeDisplay.getCompanyId()),
							objectEntry.getObjectEntryId(),
							ParamUtil.getString(httpServletRequest, "redirect"),
							themeDisplay),
						"&action=", CMPActionConstants.CREATE_GLOBAL_TASK);
				}

				if (Objects.equals(externalReferenceCode, "L_CMP_PROJECT")) {
					String baseViewProjectURL =
						ActionUtil.getBaseViewProjectURL(
							objectDefinition, themeDisplay);

					return baseViewProjectURL + objectEntry.getObjectEntryId();
				}

				return null;
			}
		).put(
			"groupId", objectEntry.getGroupId()
		).put(
			"hasUpdatePermission",
			() -> {
				ModelResourcePermission<ObjectEntry> modelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(
							objectEntry.getModelClassName());

				return modelResourcePermission.contains(
					themeDisplay.getPermissionChecker(),
					objectEntry.getObjectEntryId(), ActionKeys.UPDATE);
			}
		).put(
			"isNew", objectEntry.isDraft()
		).put(
			"parentCMPTask",
			() -> {
				if (parentCMPTaskObjectEntry == null) {
					return null;
				}

				Map<String, Serializable> values =
					parentCMPTaskObjectEntry.getValues();

				return HashMapBuilder.<String, Object>put(
					"isDone",
					Objects.equals(MapUtil.getString(values, "state"), "done")
				).put(
					"saveAndAddAnotherURL",
					() -> {
						if (!objectEntry.isDraft()) {
							return null;
						}

						return StringBundler.concat(
							ActionUtil.getAddTaskURL(
								objectEntry.getGroupId(), objectDefinition,
								MapUtil.getLong(
									values,
									"r_cmpProjectToCMPTasks_c_cmpProjectId"),
								ParamUtil.getString(
									httpServletRequest, "redirect"),
								themeDisplay),
							"&parentId=",
							parentCMPTaskObjectEntry.getObjectEntryId());
					}
				).put(
					"title", MapUtil.getString(values, "title")
				).build();
			}
		).put(
			"title",
			() -> {
				if (Objects.equals(externalReferenceCode, "L_CMP_PROJECT")) {
					return LanguageUtil.get(
						themeDisplay.getLocale(),
						objectEntry.isDraft() ? "new-project" : "edit-project");
				}

				if (parentCMPTaskObjectEntry != null) {
					return LanguageUtil.get(
						themeDisplay.getLocale(),
						objectEntry.isDraft() ? "new-subtask" : "edit-subtask");
				}

				return LanguageUtil.get(
					themeDisplay.getLocale(),
					objectEntry.isDraft() ? "new-task" : "edit-task");
			}
		).put(
			"viewURL",
			() -> {
				if (parentCMPTaskObjectEntry == null) {
					return null;
				}

				String baseViewTaskURL = ActionUtil.getBaseViewTaskURL(
					objectDefinition, themeDisplay);

				return baseViewTaskURL + objectEntry.getObjectEntryId();
			}
		).build();
	}

	private ObjectEntry _getObjectEntry(HttpServletRequest httpServletRequest) {
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

		return (ObjectEntry)displayObject;
	}

	private ObjectEntry _getParentCMPTaskObjectEntry(
		String externalReferenceCode, ObjectEntry objectEntry) {

		if (!Objects.equals(externalReferenceCode, "L_CMP_TASK") ||
			!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-107678")) {

			return null;
		}

		return _objectEntryLocalService.fetchObjectEntry(
			MapUtil.getLong(
				objectEntry.getValues(), "r_cmpTaskToCMPTasks_c_cmpTaskId"));
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}