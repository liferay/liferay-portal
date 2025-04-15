/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.portlet.action;

import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordSetService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.PortletPreferencesException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.StrictPortletPreferencesImpl;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS,
		"mvc.command.name=/dynamic_data_lists/add_record_set"
	},
	service = MVCActionCommand.class
)
public class AddRecordSetMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		DDLRecordSet recordSet = _addRecordSet(actionRequest);

		updateWorkflowDefinitionLink(actionRequest, recordSet);

		updatePortletPreferences(actionRequest, recordSet);
	}

	protected void updatePortletPreferences(
			ActionRequest actionRequest, DDLRecordSet recordSet)
		throws Exception {

		PortletPreferences portletPreferences = _getStrictPortletPreferences(
			actionRequest);

		if (portletPreferences == null) {
			return;
		}

		portletPreferences.reset("displayDDMTemplateId");
		portletPreferences.reset("editable");
		portletPreferences.reset("formDDMTemplateId");
		portletPreferences.reset("spreadsheet");

		portletPreferences.setValue(
			"recordSetId", String.valueOf(recordSet.getRecordSetId()));

		portletPreferences.store();
	}

	protected void updateWorkflowDefinitionLink(
			ActionRequest actionRequest, DDLRecordSet recordSet)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		String workflowDefinition = ParamUtil.getString(
			actionRequest, "workflowDefinition");

		workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			themeDisplay.getUserId(), themeDisplay.getCompanyId(), groupId,
			DDLRecordSet.class.getName(), recordSet.getRecordSetId(), 0,
			workflowDefinition);
	}

	@Reference
	protected DDLRecordSetService ddlRecordSetService;

	@Reference
	protected Localization localization;

	@Reference
	protected WorkflowDefinitionLinkLocalService
		workflowDefinitionLinkLocalService;

	private DDLRecordSet _addRecordSet(ActionRequest actionRequest)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		long ddmStructureId = ParamUtil.getLong(
			actionRequest, "ddmStructureId");
		Map<Locale, String> nameMap = localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = localization.getLocalizationMap(
			actionRequest, "description");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDLRecordSet.class.getName(), actionRequest);

		return ddlRecordSetService.addRecordSet(
			groupId, ddmStructureId, null, nameMap, descriptionMap,
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT,
			DDLRecordSetConstants.SCOPE_DYNAMIC_DATA_LISTS, serviceContext);
	}

	private PortletPreferences _getStrictPortletPreferences(
			ActionRequest actionRequest)
		throws Exception {

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _getStrictPortletPreferences(
			themeDisplay.getLayout(), portletResource);
	}

	private PortletPreferences _getStrictPortletPreferences(
			Layout layout, String portletId)
		throws Exception {

		if (Validator.isNull(portletId)) {
			return null;
		}

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				layout, portletId);

		if (portletPreferences instanceof StrictPortletPreferencesImpl) {
			throw new PortletPreferencesException.MustBeStrict(portletId);
		}

		return portletPreferences;
	}

}