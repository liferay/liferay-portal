/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.exception.StructureDefinitionException;
import com.liferay.dynamic.data.mapping.exception.StructureLayoutException;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializer;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializerRequest;
import com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.helper.SaveFormInstanceMVCCommandHelper;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/save_structure"
	},
	service = MVCActionCommand.class
)
public class SaveStructureMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMStructure.class.getName(), actionRequest);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		long structureId = ParamUtil.getLong(actionRequest, "structureId");
		String structureKey = ParamUtil.getString(
			actionRequest, "structureKey");
		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");
		DDMForm ddmForm = getDDMForm(actionRequest);
		DDMFormLayout ddmFormLayout = getDDMFormLayout(actionRequest);
		Map<Locale, String> nameMap =
			saveFormInstanceMVCCommandHelper.getNameMap(
				ddmForm, name, "untitled-element-set");
		Map<Locale, String> descriptionMap = _getLocalizedMap(
			description, ddmForm.getAvailableLocales(),
			ddmForm.getDefaultLocale());

		DDMStructure ddmStructure = null;

		if (structureId == 0) {
			ddmStructure = _ddmStructureService.addStructure(
				groupId, DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
				_portal.getClassNameId(DDMFormInstance.class), structureKey,
				nameMap, descriptionMap, ddmForm, ddmFormLayout,
				StorageType.DEFAULT.toString(),
				DDMStructureConstants.TYPE_FRAGMENT, serviceContext);
		}
		else {
			ddmStructure = _ddmStructureService.updateStructure(
				groupId, DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
				_portal.getClassNameId(DDMFormInstance.class), structureKey,
				nameMap, descriptionMap, ddmForm, ddmFormLayout,
				serviceContext);
		}

		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, themeDisplay.getPpid(), PortletRequest.RENDER_PHASE);

		String mvcRenderCommandName = ParamUtil.getString(
			actionRequest, "mvcRenderCommandName");

		portletURL.setParameter("mvcRenderCommandName", mvcRenderCommandName);

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		portletURL.setParameter(
			"structureId", String.valueOf(ddmStructure.getStructureId()));
		portletURL.setParameter("redirect", redirect);

		actionRequest.setAttribute(WebKeys.REDIRECT, portletURL.toString());
	}

	protected DDMForm getDDMForm(PortletRequest portletRequest)
		throws PortalException {

		try {
			String serializedFormContext = ParamUtil.getString(
				portletRequest, "serializedFormBuilderContext");

			return ddlFormBuilderContextToDDMForm.deserialize(
				DDMFormContextDeserializerRequest.with(serializedFormContext));
		}
		catch (PortalException portalException) {
			throw new StructureDefinitionException(portalException);
		}
	}

	protected DDMFormLayout getDDMFormLayout(PortletRequest portletRequest)
		throws PortalException {

		try {
			String serializedFormContext = ParamUtil.getString(
				portletRequest, "serializedFormBuilderContext");

			return ddlFormBuilderContextToDDMFormLayout.deserialize(
				DDMFormContextDeserializerRequest.with(serializedFormContext));
		}
		catch (PortalException portalException) {
			throw new StructureLayoutException(portalException);
		}
	}

	@Reference(
		target = "(dynamic.data.mapping.form.builder.context.deserializer.type=form)"
	)
	protected DDMFormContextDeserializer<DDMForm>
		ddlFormBuilderContextToDDMForm;

	@Reference(
		target = "(dynamic.data.mapping.form.builder.context.deserializer.type=formLayout)"
	)
	protected DDMFormContextDeserializer<DDMFormLayout>
		ddlFormBuilderContextToDDMFormLayout;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected SaveFormInstanceMVCCommandHelper saveFormInstanceMVCCommandHelper;

	private Map<Locale, String> _getLocalizedMap(
			String value, Set<Locale> availableLocales, Locale defaultLocale)
		throws Exception {

		Map<Locale, String> localizedMap = new HashMap<>();

		JSONObject jsonObject = jsonFactory.createJSONObject(value);

		String defaultValueString = jsonObject.getString(
			LocaleUtil.toLanguageId(defaultLocale));

		for (Locale availableLocale : availableLocales) {
			String valueString = jsonObject.getString(
				LocaleUtil.toLanguageId(availableLocale), defaultValueString);

			localizedMap.put(availableLocale, valueString);
		}

		return localizedMap;
	}

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private Portal _portal;

}