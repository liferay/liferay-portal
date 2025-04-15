/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.service.CTCollectionTemplateService;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/edit_ct_collection_template"
	},
	service = MVCActionCommand.class
)
public class EditCTCollectionTemplateMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		long ctCollectionTemplateId = ParamUtil.getLong(
			actionRequest, "ctCollectionTemplateId");

		String description = ParamUtil.getString(actionRequest, "description");
		boolean defaultSandboxCTCollectionTemplate = ParamUtil.getBoolean(
			actionRequest, "defaultSandboxCTCollectionTemplate");
		boolean defaultCTCollectionTemplate = ParamUtil.getBoolean(
			actionRequest, "defaultCTCollectionTemplate");
		String name = ParamUtil.getString(actionRequest, "name");
		String json = JSONUtil.put(
			"description",
			ParamUtil.getString(actionRequest, "publicationDescription")
		).put(
			"name", ParamUtil.getString(actionRequest, "publicationName")
		).put(
			"publicationsUserRoleUserIds",
			ParamUtil.getLongValues(
				actionRequest, "publicationsUserRoleUserIds")
		).put(
			"roleValues",
			ParamUtil.getIntegerValues(actionRequest, "roleValues")
		).put(
			"userIds", ParamUtil.getLongValues(actionRequest, "userIds")
		).toString();

		try {
			CTCollectionTemplate ctCollectionTemplate = null;

			if (ctCollectionTemplateId > 0) {
				ctCollectionTemplate =
					_ctCollectionTemplateService.updateCTCollectionTemplate(
						ctCollectionTemplateId, name, description, json);
			}
			else {
				ctCollectionTemplate =
					_ctCollectionTemplateService.addCTCollectionTemplate(
						name, description, json);

				ctCollectionTemplateId =
					ctCollectionTemplate.getCtCollectionTemplateId();
			}

			CTSettingsConfiguration ctSettingsConfiguration =
				_ctSettingsConfigurationHelper.getCTSettingsConfiguration(
					ctCollectionTemplate.getCompanyId());

			long defaultCTCollectionTemplateId =
				ctSettingsConfiguration.defaultCTCollectionTemplateId();
			long defaultSandboxCTCollectionTemplateId =
				ctSettingsConfiguration.defaultSandboxCTCollectionTemplateId();

			if (defaultCTCollectionTemplate) {
				defaultCTCollectionTemplateId = ctCollectionTemplateId;
			}
			else if (defaultCTCollectionTemplateId == ctCollectionTemplateId) {
				defaultCTCollectionTemplateId = 0;
			}

			if (defaultSandboxCTCollectionTemplate) {
				defaultSandboxCTCollectionTemplateId = ctCollectionTemplateId;
			}
			else if (defaultSandboxCTCollectionTemplateId ==
						ctCollectionTemplateId) {

				defaultSandboxCTCollectionTemplateId = 0;
			}

			_ctSettingsConfigurationHelper.save(
				ctCollectionTemplate.getCompanyId(),
				HashMapBuilder.<String, Object>put(
					"defaultCTCollectionTemplateId",
					defaultCTCollectionTemplateId
				).put(
					"defaultSandboxCTCollectionTemplateId",
					defaultSandboxCTCollectionTemplateId
				).build());
		}
		catch (PortalException portalException) {
			SessionErrors.add(actionRequest, portalException.getClass());

			_portal.copyRequestParameters(actionRequest, actionResponse);

			actionResponse.setRenderParameter(
				"mvcPath", "/edit_ct_collection_template.jsp");
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

	@Reference
	private CTCollectionTemplateService _ctCollectionTemplateService;

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private Portal _portal;

}