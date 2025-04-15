/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.admin.rest.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFolderResource;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.exception.ObjectFolderItemObjectDefinitionIdException;
import com.liferay.object.exception.ObjectFolderNameException;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
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
 * @author Guilherme Sa
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/import_object_folder"
	},
	service = MVCActionCommand.class
)
public class ImportObjectFolderMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_importObjectFolder(actionRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			JSONObject jsonObject = null;

			if (exception instanceof
					ObjectFolderItemObjectDefinitionIdException) {

				ObjectFolderItemObjectDefinitionIdException
					objectFolderItemObjectDefinitionIdException =
						(ObjectFolderItemObjectDefinitionIdException)exception;

				jsonObject = JSONUtil.put(
					"title",
					_language.format(
						_portal.getHttpServletRequest(actionRequest),
						"failed-to-import-the-following-object-definitions-x",
						StringUtil.merge(
							objectFolderItemObjectDefinitionIdException.
								getObjectDefinitionNames(),
							StringPool.COMMA_AND_SPACE)));
			}
			else if (exception instanceof ObjectFolderNameException) {
				Class<?> clazz = exception.getClass();

				jsonObject = JSONUtil.put(
					"type",
					"ObjectFolderNameException." + clazz.getSimpleName());
			}
			else {
				jsonObject = JSONUtil.put(
					"title",
					_language.get(
						_portal.getHttpServletRequest(actionRequest),
						"the-object-folder-failed-to-import"));
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}

		hideDefaultSuccessMessage(actionRequest);
	}

	private void _importObjectFolder(ActionRequest actionRequest)
		throws Exception {

		ObjectFolderResource.Builder builder =
			_objectFolderResourceFactory.create();

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ObjectFolderResource objectFolderResource = builder.user(
			themeDisplay.getUser()
		).build();

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		String objectFolderJSON = FileUtil.read(
			uploadPortletRequest.getFile("objectFolderJSON"));

		JSONObject objectFolderJSONObject = _jsonFactory.createJSONObject(
			objectFolderJSON);

		ObjectFolder objectFolder = ObjectFolder.toDTO(
			objectFolderJSONObject.toString());

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		if (Validator.isNotNull(externalReferenceCode)) {
			objectFolder.setExternalReferenceCode(() -> externalReferenceCode);
		}

		objectFolder.setName(() -> ParamUtil.getString(actionRequest, "name"));

		objectFolderResource.putObjectFolderByExternalReferenceCode(
			objectFolder.getExternalReferenceCode(), objectFolder);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImportObjectFolderMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ObjectFolderResource.Factory _objectFolderResourceFactory;

	@Reference
	private Portal _portal;

}