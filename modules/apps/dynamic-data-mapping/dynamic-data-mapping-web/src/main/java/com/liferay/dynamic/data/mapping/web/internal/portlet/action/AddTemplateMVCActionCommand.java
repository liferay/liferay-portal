/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.exception.TemplateScriptException;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.File;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING,
		"jakarta.portlet.name=" + PortletKeys.PORTLET_DISPLAY_TEMPLATE,
		"mvc.command.name=/dynamic_data_mapping/add_template"
	},
	service = MVCActionCommand.class
)
public class AddTemplateMVCActionCommand extends BaseDDMMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		DDMTemplate template = _addTemplate(actionRequest);

		updatePortletPreferences(actionRequest, template);

		addSuccessMessage(actionRequest, actionResponse);

		setRedirectAttribute(actionRequest, template);
	}

	protected String getScript(UploadPortletRequest uploadPortletRequest)
		throws Exception {

		String fileScriptContent = _getFileScriptContent(uploadPortletRequest);

		if (Validator.isNotNull(fileScriptContent)) {
			return fileScriptContent;
		}

		return ParamUtil.getString(uploadPortletRequest, "scriptContent");
	}

	@Reference
	protected DDMTemplateService ddmTemplateService;

	@Reference
	protected Localization localization;

	@Reference
	protected Portal portal;

	private DDMTemplate _addTemplate(ActionRequest actionRequest)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			portal.getUploadPortletRequest(actionRequest);

		long groupId = ParamUtil.getLong(uploadPortletRequest, "groupId");
		long classNameId = ParamUtil.getLong(
			uploadPortletRequest, "classNameId");
		long classPK = ParamUtil.getLong(uploadPortletRequest, "classPK");
		long resourceClassNameId = ParamUtil.getLong(
			uploadPortletRequest, "resourceClassNameId");
		String templateKey = ParamUtil.getString(
			uploadPortletRequest, "templateKey");
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

		return ddmTemplateService.addTemplate(
			null, groupId, classNameId, classPK, resourceClassNameId,
			templateKey, nameMap, descriptionMap, type, mode, language, script,
			cacheable, smallImage, smallImageURL, smallImageFile,
			serviceContext);
	}

	private String _getFileScriptContent(
			UploadPortletRequest uploadPortletRequest)
		throws Exception {

		File file = uploadPortletRequest.getFile("script");

		if (file == null) {
			return null;
		}

		String fileScriptContent = FileUtil.read(file);

		String contentType = MimeTypesUtil.getContentType(file);

		if (Validator.isNotNull(fileScriptContent) &&
			!_isValidContentType(contentType)) {

			throw new TemplateScriptException(
				"Invalid contentType " + contentType);
		}

		return fileScriptContent;
	}

	private boolean _isValidContentType(String contentType) {
		if (contentType.equals(ContentTypes.APPLICATION_XSLT_XML) ||
			contentType.startsWith(ContentTypes.TEXT)) {

			return true;
		}

		return false;
	}

}