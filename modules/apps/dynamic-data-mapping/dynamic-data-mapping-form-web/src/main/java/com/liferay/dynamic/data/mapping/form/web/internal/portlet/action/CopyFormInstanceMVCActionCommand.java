/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormInstanceExpirationStatusUtil;
import com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.helper.SaveFormInstanceMVCCommandHelper;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Queiroz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/copy_form_instance"
	},
	service = MVCActionCommand.class
)
public class CopyFormInstanceMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	protected DDMFormValues createFormInstanceSettingsDDMFormValues(
			DDMFormInstance formInstance, ThemeDisplay themeDisplay)
		throws Exception {

		DDMFormValues settingsDDMFormValuesCopy =
			formInstance.getSettingsDDMFormValues();

		_setDefaultDDMFormFieldValues(
			formInstance, settingsDDMFormValuesCopy, themeDisplay);

		return settingsDDMFormValuesCopy;
	}

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		long formInstanceId = ParamUtil.getLong(
			actionRequest, "formInstanceId");

		DDMFormInstance formInstance = ddmFormInstanceService.getFormInstance(
			formInstanceId);

		DDMStructure ddmStructure = formInstance.getStructure();

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			ddmStructure.getDefaultLanguageId());

		DDMFormValues settingsDDMFormValues =
			createFormInstanceSettingsDDMFormValues(
				formInstance,
				(ThemeDisplay)actionRequest.getAttribute(
					WebKeys.THEME_DISPLAY));

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMFormInstance.class.getName(), actionRequest);

		ddmFormInstanceService.copyFormInstance(
			groupId, getNameMap(formInstance, defaultLocale), formInstance,
			settingsDDMFormValues, serviceContext);
	}

	protected Map<Locale, String> getNameMap(
		DDMFormInstance formInstance, Locale defaultLocale) {

		Map<Locale, String> nameMap = formInstance.getNameMap();

		String name = _language.format(
			getResourceBundle(defaultLocale), "copy-of-x",
			nameMap.get(defaultLocale));

		nameMap.put(defaultLocale, name);

		return nameMap;
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundle portalResourceBundle = portal.getResourceBundle(locale);

		ResourceBundle moduleResourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return new AggregateResourceBundle(
			moduleResourceBundle, portalResourceBundle);
	}

	@Reference
	protected DDMFormInstanceService ddmFormInstanceService;

	@Reference
	protected DDMStructureService ddmStructureService;

	@Reference
	protected Portal portal;

	@Reference
	protected SaveFormInstanceMVCCommandHelper saveFormInstanceMVCCommandHelper;

	private void _setDefaultDDMFormFieldValues(
			DDMFormInstance ddmFormInstance, DDMFormValues ddmFormValues,
			ThemeDisplay themeDisplay)
		throws Exception {

		boolean expired = DDMFormInstanceExpirationStatusUtil.isFormExpired(
			ddmFormInstance, themeDisplay.getTimeZone());

		for (DDMFormFieldValue ddmFormFieldValue :
				ddmFormValues.getDDMFormFieldValues()) {

			if (Objects.equals(ddmFormFieldValue.getName(), "published")) {
				ddmFormFieldValue.setValue(new UnlocalizedValue("false"));
			}

			if (!expired) {
				continue;
			}

			if (StringUtil.equals(
					ddmFormFieldValue.getName(), "expirationDate")) {

				ddmFormFieldValue.setValue(new UnlocalizedValue(""));
			}
			else if (StringUtil.equals(
						ddmFormFieldValue.getName(), "neverExpire")) {

				ddmFormFieldValue.setValue(new UnlocalizedValue("true"));
			}
		}
	}

	@Reference
	private Language _language;

}