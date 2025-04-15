/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.template.constants.TemplatePortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TemplatePortletKeys.TEMPLATE,
		"mvc.command.name=/template/copy_ddm_template"
	},
	service = MVCActionCommand.class
)
public class CopyDDMTemplateMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long ddmTemplateId = ParamUtil.getLong(actionRequest, "ddmTemplateId");

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMTemplate.class.getName(), actionRequest);

		_ddmTemplateService.copyTemplate(
			ddmTemplateId, nameMap, descriptionMap, serviceContext);
	}

	@Reference
	private DDMTemplateService _ddmTemplateService;

	@Reference
	private Localization _localization;

}