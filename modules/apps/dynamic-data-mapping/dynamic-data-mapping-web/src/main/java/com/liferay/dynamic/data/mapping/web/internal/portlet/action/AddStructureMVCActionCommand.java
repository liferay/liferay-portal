/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;

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
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING,
		"mvc.command.name=/dynamic_data_mapping/add_structure"
	},
	service = MVCActionCommand.class
)
public class AddStructureMVCActionCommand extends BaseDDMMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		DDMStructure structure = _addStructure(actionRequest);

		addSuccessMessage(actionRequest, actionResponse);

		setRedirectAttribute(actionRequest, structure);
	}

	private DDMStructure _addStructure(ActionRequest actionRequest)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		long scopeClassNameId = ParamUtil.getLong(
			actionRequest, "scopeClassNameId");
		String structureKey = ParamUtil.getString(
			actionRequest, "structureKey");
		long parentStructureId = ParamUtil.getLong(
			actionRequest, "parentStructureId",
			DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID);
		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		DDMForm ddmForm = _ddm.getDDMForm(actionRequest);

		DDMFormLayout ddmFormLayout = _ddm.getDefaultDDMFormLayout(ddmForm);

		String storageType = ParamUtil.getString(actionRequest, "storageType");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMStructure.class.getName(), actionRequest);

		return _ddmStructureService.addStructure(
			groupId, parentStructureId, scopeClassNameId, structureKey, nameMap,
			descriptionMap, ddmForm, ddmFormLayout, storageType,
			DDMStructureConstants.TYPE_DEFAULT, serviceContext);
	}

	@Reference
	private DDM _ddm;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private Localization _localization;

}