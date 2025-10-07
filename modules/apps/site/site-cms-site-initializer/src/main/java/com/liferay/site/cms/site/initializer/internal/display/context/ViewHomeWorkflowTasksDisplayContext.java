/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.util.ObjectDefinitionUtil;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Dorado
 */
public class ViewHomeWorkflowTasksDisplayContext {

	public ViewHomeWorkflowTasksDisplayContext(
		HttpServletRequest httpServletRequest, JSONFactory jsonFactory,
		ObjectDefinitionResource.Factory objectDefinitionResourceFactory,
		ThemeDisplay themeDisplay) {

		_httpServletRequest = httpServletRequest;
		_jsonFactory = jsonFactory;
		_objectDefinitionResourceFactory = objectDefinitionResourceFactory;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getReactData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"id", CMSSiteInitializerFDSNames.HOME_MY_WORKFLOW_TASKS_SECTION
		).put(
			"myRolesWorkflowTasksURL",
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					_httpServletRequest, PortletKeys.MY_WORKFLOW_TASK,
					ActionRequest.RENDER_PHASE)
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).setTabs1(
				"assigned-to-my-roles"
			).buildString()
		).put(
			"myWorkflowTasksURL",
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					_httpServletRequest, PortletKeys.MY_WORKFLOW_TASK,
					ActionRequest.RENDER_PHASE)
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).buildString()
		).put(
			"objectDefinitions", _getObjectDefinitionsJSONArray()
		).build();
	}

	private JSONObject _getObjectDefinitionJSONObject(
			ObjectDefinition objectDefinition)
		throws Exception {

		if (objectDefinition == null) {
			return null;
		}

		ObjectDefinitionUtil.prepareObjectDefinitionForExport(
			_jsonFactory, objectDefinition);

		return _jsonFactory.createJSONObject(objectDefinition.toString());
	}

	private List<ObjectDefinition> _getObjectDefinitions() throws Exception {
		if (_objectDefinitions != null) {
			return _objectDefinitions;
		}

		ObjectDefinitionResource.Builder builder =
			_objectDefinitionResourceFactory.create();

		ObjectDefinitionResource objectDefinitionResource = builder.user(
			_themeDisplay.getUser()
		).build();

		Page<ObjectDefinition> page =
			objectDefinitionResource.getObjectDefinitionsPage(
				null, null,
				objectDefinitionResource.toFilter(
					StringBundler.concat(
						"((objectFolderExternalReferenceCode eq '",
						ObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
						"') or (objectFolderExternalReferenceCode eq '",
						ObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_FILE_TYPES,
						"') or (objectFolderExternalReferenceCode eq '",
						ObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_STRUCTURE_REPEATABLE_GROUPS,
						"')) and (status/any(x:(x eq 0)))"),
					Collections.emptyMap()),
				null, null);

		_objectDefinitions = new ArrayList<>(page.getItems());

		return _objectDefinitions;
	}

	private JSONArray _getObjectDefinitionsJSONArray() throws Exception {
		List<ObjectDefinition> objectDefinitions = _getObjectDefinitions();

		if (objectDefinitions == null) {
			return null;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			JSONObject objectDefinitionJSONObject =
				_getObjectDefinitionJSONObject(objectDefinition);

			if (objectDefinitionJSONObject != null) {
				jsonArray.put(objectDefinitionJSONObject);
			}
		}

		return jsonArray;
	}

	private final HttpServletRequest _httpServletRequest;
	private final JSONFactory _jsonFactory;
	private final ObjectDefinitionResource.Factory
		_objectDefinitionResourceFactory;
	private List<ObjectDefinition> _objectDefinitions;
	private final ThemeDisplay _themeDisplay;

}