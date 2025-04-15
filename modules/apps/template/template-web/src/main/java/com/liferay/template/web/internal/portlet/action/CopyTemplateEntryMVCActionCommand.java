/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TemplatePortletKeys.TEMPLATE,
		"mvc.command.name=/template/copy_template_entry"
	},
	service = MVCActionCommand.class
)
public class CopyTemplateEntryMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long templateEntryId = ParamUtil.getLong(
			actionRequest, "templateEntryId");

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMTemplate.class.getName(), actionRequest);

		TemplateEntry templateEntry =
			_templateEntryLocalService.fetchTemplateEntry(templateEntryId);

		DDMTemplate ddmTemplate = _ddmTemplateService.copyTemplate(
			templateEntry.getDDMTemplateId(), nameMap, descriptionMap,
			serviceContext);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		serviceContext = ServiceContextFactory.getInstance(
			TemplateEntry.class.getName(), actionRequest);

		_templateEntryLocalService.addTemplateEntry(
			null, themeDisplay.getUserId(), templateEntry.getGroupId(),
			ddmTemplate.getTemplateId(), templateEntry.getInfoItemClassName(),
			templateEntry.getInfoItemFormVariationKey(), serviceContext);
	}

	@Reference
	private DDMTemplateService _ddmTemplateService;

	@Reference
	private Localization _localization;

	@Reference
	private TemplateEntryLocalService _templateEntryLocalService;

}