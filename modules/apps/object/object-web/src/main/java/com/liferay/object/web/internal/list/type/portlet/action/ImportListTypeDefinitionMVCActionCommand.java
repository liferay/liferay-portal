/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.list.type.portlet.action;

import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.object.constants.ObjectPortletKeys;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Murilo Stodolni
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.LIST_TYPE_DEFINITIONS,
		"mvc.command.name=/list_type_definitions/import_list_type_definition"
	},
	service = MVCActionCommand.class
)
public class ImportListTypeDefinitionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_importListTypeDefinition(actionRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"title",
					_language.get(
						_portal.getHttpServletRequest(actionRequest),
						"the-picklist-failed-to-import")));
		}

		hideDefaultSuccessMessage(actionRequest);
	}

	private void _importListTypeDefinition(ActionRequest actionRequest)
		throws Exception {

		ListTypeDefinitionResource.Builder builder =
			_listTypeDefinitionResourceFactory.create();

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ListTypeDefinitionResource listTypeDefinitionResource = builder.user(
			themeDisplay.getUser()
		).build();

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		String listTypeDefinitionJSON = FileUtil.read(
			uploadPortletRequest.getFile("listTypeDefinitionJSON"));

		JSONObject listTypeDefinitionJSONObject = _jsonFactory.createJSONObject(
			listTypeDefinitionJSON);

		ListTypeDefinition listTypeDefinition = ListTypeDefinition.toDTO(
			listTypeDefinitionJSONObject.toString());

		Map<String, String> nameI18n = listTypeDefinition.getName_i18n();

		listTypeDefinition.setName_i18n(
			() -> LocalizedMapUtil.mergeI18nMap(
				nameI18n, LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
				ParamUtil.getString(actionRequest, "name")));

		listTypeDefinitionResource.putListTypeDefinitionByExternalReferenceCode(
			listTypeDefinition.getExternalReferenceCode(), listTypeDefinition);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImportListTypeDefinitionMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ListTypeDefinitionResource.Factory
		_listTypeDefinitionResourceFactory;

	@Reference
	private Portal _portal;

}