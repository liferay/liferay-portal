/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.service.CTCollectionTemplateLocalService;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.web.internal.constants.CTWebKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

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
	service = MVCRenderCommand.class
)
public class EditCTCollectionTemplateMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		long ctCollectionTemplateId = ParamUtil.getLong(
			renderRequest, "ctCollectionTemplateId");

		if (ctCollectionTemplateId > 0) {
			CTCollectionTemplate ctCollectionTemplate =
				_ctCollectionTemplateLocalService.fetchCTCollectionTemplate(
					ctCollectionTemplateId);

			renderRequest.setAttribute(
				CTWebKeys.CT_COLLECTION_TEMPLATE, ctCollectionTemplate);

			CTSettingsConfiguration ctSettingsConfiguration =
				_ctSettingsConfigurationHelper.getCTSettingsConfiguration(
					ctCollectionTemplate.getCompanyId());

			if (ctCollectionTemplateId ==
					ctSettingsConfiguration.defaultCTCollectionTemplateId()) {

				renderRequest.setAttribute(
					CTWebKeys.DEFAULT_CT_COLLECTION_TEMPLATE, Boolean.TRUE);
			}

			if (ctCollectionTemplateId ==
					ctSettingsConfiguration.
						defaultSandboxCTCollectionTemplateId()) {

				renderRequest.setAttribute(
					CTWebKeys.DEFAULT_SANDBOX_CT_COLLECTION_TEMPLATE,
					Boolean.TRUE);
			}
		}

		return "/publications/edit_ct_collection_template.jsp";
	}

	@Reference
	private CTCollectionTemplateLocalService _ctCollectionTemplateLocalService;

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

}