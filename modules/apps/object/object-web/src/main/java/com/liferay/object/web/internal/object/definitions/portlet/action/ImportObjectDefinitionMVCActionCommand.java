/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.exception.ObjectDefinitionNameException;
import com.liferay.object.exception.ObjectDefinitionStatusException;
import com.liferay.object.exception.ObjectViewColumnFieldNameException;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Gabriel Albuquerque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/import_object_definition"
	},
	service = MVCActionCommand.class
)
public class ImportObjectDefinitionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject errorsJSONObject = _importObjectDefinition(actionRequest);

		if (errorsJSONObject != null) {
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, errorsJSONObject);
		}

		hideDefaultSuccessMessage(actionRequest);
	}

	private JSONObject _handleImportException(
		ActionRequest actionRequest, Exception exception) {

		if (_log.isDebugEnabled()) {
			_log.debug(exception);
		}

		if (exception instanceof ObjectDefinitionNameException) {
			Class<?> clazz = exception.getClass();

			return JSONUtil.put(
				"type",
				"ObjectDefinitionNameException." + clazz.getSimpleName());
		}
		else if (exception instanceof ObjectDefinitionStatusException) {
			return JSONUtil.put("title", exception.getMessage());
		}
		else if (exception instanceof ObjectViewColumnFieldNameException) {
			return JSONUtil.put(
				"title",
				_language.get(
					_portal.getHttpServletRequest(actionRequest),
					"the-object-definition-was-imported-without-a-custom-" +
						"view"));
		}

		return JSONUtil.put(
			"title",
			_language.get(
				_portal.getHttpServletRequest(actionRequest),
				"the-object-definition-failed-to-import"));
	}

	private JSONObject _importObjectDefinition(ActionRequest actionRequest)
		throws Exception {

		JSONArray errorsMessageJSONArray = _jsonFactory.createJSONArray();

		ObjectDefinitionResource.Builder builder =
			_objectDefinitionResourceFactory.create();

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ObjectDefinitionResource objectDefinitionResource = builder.user(
			themeDisplay.getUser()
		).build();

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		String objectDefinitionJSON = FileUtil.read(
			uploadPortletRequest.getFile("objectDefinitionJSON"));

		boolean importMultipleObjectDefinitions = Validator.isNull(
			objectDefinitionJSON);

		if (importMultipleObjectDefinitions) {
			jsonArray = _jsonFactory.createJSONArray(
				ParamUtil.getString(uploadPortletRequest, "objectDefinitions"));
		}
		else {
			jsonArray.put(_jsonFactory.createJSONObject(objectDefinitionJSON));
		}

		for (Object object : jsonArray) {
			JSONObject jsonObject = (JSONObject)object;

			jsonObject.remove("accountEntryRestrictedObjectFieldId");

			ObjectDefinition objectDefinition = ObjectDefinition.toDTO(
				jsonObject.toString());

			objectDefinition.setActive(() -> false);

			String externalReferenceCode = ParamUtil.getString(
				actionRequest, "externalReferenceCode");

			if (Validator.isNotNull(externalReferenceCode)) {
				objectDefinition.setExternalReferenceCode(
					() -> externalReferenceCode);
			}

			String name = ParamUtil.getString(actionRequest, "name");

			if (Validator.isNotNull(name)) {
				objectDefinition.setName(() -> name);
			}

			objectDefinition.setObjectFolderExternalReferenceCode(
				() -> ParamUtil.getString(
					uploadPortletRequest, "objectFolderExternalReferenceCode"));

			try {
				ObjectDefinition putObjectDefinition =
					objectDefinitionResource.
						putObjectDefinitionByExternalReferenceCode(
							objectDefinition.getExternalReferenceCode(),
							objectDefinition);

				putObjectDefinition.setPortlet(objectDefinition::getPortlet);

				if (!FeatureFlagManagerUtil.isEnabled("LPS-135430")) {
					putObjectDefinition.setStorageType(() -> StringPool.BLANK);
				}

				objectDefinitionResource.putObjectDefinition(
					putObjectDefinition.getId(), putObjectDefinition);
			}
			catch (Exception exception) {
				if (importMultipleObjectDefinitions) {
					errorsMessageJSONArray.put(
						JSONUtil.put(
							"error",
							_handleImportException(actionRequest, exception)
						).put(
							"objectDefinitionName", objectDefinition.getName()
						));
				}
				else {
					errorsMessageJSONArray.put(
						_handleImportException(actionRequest, exception));
				}
			}
		}

		if (errorsMessageJSONArray.length() == 0) {
			return null;
		}

		if (!importMultipleObjectDefinitions) {
			return errorsMessageJSONArray.getJSONObject(0);
		}

		return JSONUtil.put(
			"message", errorsMessageJSONArray
		).put(
			"type", "importMultipleObjectDefinitions"
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImportObjectDefinitionMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

	@Reference
	private Portal _portal;

}