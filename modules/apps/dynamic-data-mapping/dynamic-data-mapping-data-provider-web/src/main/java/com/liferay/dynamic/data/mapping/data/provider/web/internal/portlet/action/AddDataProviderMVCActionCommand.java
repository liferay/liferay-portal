/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRegistry;
import com.liferay.dynamic.data.mapping.exception.DataProviderInstanceURLException;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_DATA_PROVIDER,
		"mvc.command.name=/dynamic_data_mapping_data_provider/add_data_provider"
	},
	service = MVCActionCommand.class
)
public class AddDataProviderMVCActionCommand extends BaseMVCActionCommand {

	public DDMFormValues getDDMFormValues(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortalException {

		String type = ParamUtil.getString(actionRequest, "type");

		DDMDataProvider ddmDataProvider =
			ddmDataProviderRegistry.getDDMDataProvider(type);

		Class<?> clazz = ddmDataProvider.getSettings();

		DDMForm ddmForm = DDMFormFactory.create(clazz);

		return ddmFormValuesFactory.create(actionRequest, ddmForm);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");
		DDMFormValues ddmFormValues = getDDMFormValues(
			actionRequest, actionResponse);
		String type = ParamUtil.getString(actionRequest, "type");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMDataProviderInstance.class.getName(), actionRequest);

		try {
			ddmDataProviderInstanceService.addDataProviderInstance(
				groupId,
				getLocalizedMap(themeDisplay.getSiteDefaultLocale(), name),
				getLocalizedMap(
					themeDisplay.getSiteDefaultLocale(), description),
				ddmFormValues, type, serviceContext);
		}
		catch (DataProviderInstanceURLException
					dataProviderInstanceURLException) {

			hideDefaultErrorMessage(actionRequest);

			SessionErrors.add(
				actionRequest, dataProviderInstanceURLException.getClass());
		}
	}

	protected Map<Locale, String> getLocalizedMap(Locale locale, String value) {
		return HashMapBuilder.put(
			locale, value
		).build();
	}

	@Reference
	protected DDMDataProviderInstanceService ddmDataProviderInstanceService;

	@Reference
	protected DDMDataProviderRegistry ddmDataProviderRegistry;

	@Reference
	protected DDMFormValuesFactory ddmFormValuesFactory;

}