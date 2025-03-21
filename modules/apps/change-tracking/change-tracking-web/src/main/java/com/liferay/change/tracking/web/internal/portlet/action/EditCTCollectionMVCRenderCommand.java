/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionTemplateLocalService;
import com.liferay.change.tracking.service.CTRemoteLocalService;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.web.internal.constants.CTWebKeys;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/add_ct_collection",
		"mvc.command.name=/change_tracking/edit_ct_collection",
		"mvc.command.name=/change_tracking/undo_ct_collection"
	},
	service = MVCRenderCommand.class
)
public class EditCTCollectionMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long ctCollectionId = ParamUtil.getLong(
			renderRequest, "ctCollectionId");

		try {
			if (ctCollectionId != 0) {
				_ctCollectionModelResourcePermission.check(
					themeDisplay.getPermissionChecker(), ctCollectionId,
					ActionKeys.UPDATE);
			}
		}
		catch (Exception exception) {
			SessionErrors.add(renderRequest, exception.getClass());

			return "/publications/error.jsp";
		}

		renderRequest.setAttribute(
			CTWebKeys.CT_COLLECTION,
			_ctCollectionLocalService.fetchCTCollection(ctCollectionId));

		long ctRemoteId = ParamUtil.getLong(renderRequest, "ctRemoteId");

		if (ctRemoteId != 0) {
			renderRequest.setAttribute(
				CTWebKeys.CT_REMOTE,
				_ctRemoteLocalService.fetchCTRemote(ctRemoteId));
		}

		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		List<CTCollectionTemplate> ctCollectionTemplates =
			_ctCollectionTemplateLocalService.getCTCollectionTemplates(
				themeDisplay.getCompanyId(), 0, 100);

		renderRequest.setAttribute(
			CTWebKeys.CT_COLLECTION_TEMPLATES,
			jsonSerializer.serializeDeep(ctCollectionTemplates));

		if (ctCollectionId == 0) {
			renderRequest.setAttribute(
				CTWebKeys.DEFAULT_CT_COLLECTION_TEMPLATE_ID,
				_ctSettingsConfigurationHelper.getDefaultCTCollectionTemplateId(
					themeDisplay.getCompanyId()));
		}

		Map<Long, JSONObject> map = new HashMap<>();

		for (CTCollectionTemplate ctCollectionTemplate :
				ctCollectionTemplates) {

			JSONObject jsonObject = ctCollectionTemplate.getJSONObject();

			jsonObject.put(
				"description",
				ctCollectionTemplate.getParsedPublicationDescription()
			).put(
				"name", ctCollectionTemplate.getParsedPublicationName()
			);

			map.put(
				ctCollectionTemplate.getCtCollectionTemplateId(), jsonObject);
		}

		renderRequest.setAttribute(
			CTWebKeys.CT_COLLECTION_TEMPLATES_DATA,
			_jsonFactory.looseSerializeDeep(map));

		return "/publications/edit_ct_collection.jsp";
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollection)"
	)
	private ModelResourcePermission<CTCollection>
		_ctCollectionModelResourcePermission;

	@Reference
	private CTCollectionTemplateLocalService _ctCollectionTemplateLocalService;

	@Reference
	private CTRemoteLocalService _ctRemoteLocalService;

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private JSONFactory _jsonFactory;

}