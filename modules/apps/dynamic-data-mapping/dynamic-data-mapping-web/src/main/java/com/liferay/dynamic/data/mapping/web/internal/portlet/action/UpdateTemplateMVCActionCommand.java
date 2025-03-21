/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.File;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING,
		"jakarta.portlet.name=" + PortletKeys.PORTLET_DISPLAY_TEMPLATE,
		"mvc.command.name=/dynamic_data_mapping/update_template"
	},
	service = MVCActionCommand.class
)
public class UpdateTemplateMVCActionCommand
	extends AddTemplateMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		DDMTemplate template = _updateTemplate(actionRequest);

		updatePortletPreferences(actionRequest, template);

		addSuccessMessage(actionRequest, actionResponse);

		setRedirectAttribute(actionRequest, template);
	}

	private DDMTemplate _updateTemplate(ActionRequest actionRequest)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			portal.getUploadPortletRequest(actionRequest);

		long templateId = ParamUtil.getLong(uploadPortletRequest, "templateId");

		long classPK = ParamUtil.getLong(uploadPortletRequest, "classPK");
		Map<Locale, String> nameMap = localization.getLocalizationMap(
			uploadPortletRequest, "name");
		Map<Locale, String> descriptionMap = localization.getLocalizationMap(
			uploadPortletRequest, "description");
		String type = ParamUtil.getString(uploadPortletRequest, "type");
		String mode = ParamUtil.getString(uploadPortletRequest, "mode");
		String language = ParamUtil.getString(
			uploadPortletRequest, "language", TemplateConstants.LANG_TYPE_VM);

		String script = getScript(uploadPortletRequest);

		boolean cacheable = ParamUtil.getBoolean(
			uploadPortletRequest, "cacheable");
		boolean smallImage = ParamUtil.getBoolean(
			uploadPortletRequest, "smallImage");
		String smallImageURL = ParamUtil.getString(
			uploadPortletRequest, "smallImageURL");
		File smallImageFile = uploadPortletRequest.getFile("smallImageFile");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMTemplate.class.getName(), uploadPortletRequest);

		return ddmTemplateService.updateTemplate(
			templateId, classPK, nameMap, descriptionMap, type, mode, language,
			script, cacheable, smallImage, smallImageURL, smallImageFile,
			serviceContext);
	}

}