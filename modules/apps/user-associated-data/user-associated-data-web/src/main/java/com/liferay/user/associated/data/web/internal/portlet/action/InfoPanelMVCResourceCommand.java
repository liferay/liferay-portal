/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.web.internal.constants.UADWebKeys;
import com.liferay.user.associated.data.web.internal.display.UADEntity;
import com.liferay.user.associated.data.web.internal.display.UADHierarchyDisplay;
import com.liferay.user.associated.data.web.internal.display.UADInfoPanelDisplay;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
		"mvc.command.name=/user_associated_data/info_panel"
	},
	service = MVCResourceCommand.class
)
public class InfoPanelMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		UADInfoPanelDisplay uadInfoPanelDisplay = new UADInfoPanelDisplay();

		List<String> entityTypes = new ArrayList<>();

		Map<String, String[]> parameterMap = resourceRequest.getParameterMap();

		for (String key : parameterMap.keySet()) {
			if (key.startsWith("uadRegistryKey__")) {
				entityTypes.add(
					StringUtil.removeSubstring(key, "uadRegistryKey__"));
			}
		}

		for (String entityType : entityTypes) {
			List<UADEntity<Object>> uadEntities = new ArrayList<>();

			String uadRegistryKey = ParamUtil.getString(
				resourceRequest, "uadRegistryKey__" + entityType);

			UADDisplay<Object> uadDisplay =
				(UADDisplay<Object>)_uadRegistry.getUADDisplay(uadRegistryKey);

			String[] rowIds = ParamUtil.getStringValues(
				resourceRequest, "rowIds" + entityType);

			for (String rowId : rowIds) {
				Object entity = uadDisplay.get(rowId);

				UADEntity<Object> uadEntity = new UADEntity(
					entity, uadDisplay.getPrimaryKey(entity), null, false,
					uadDisplay.getTypeKey(), true, null);

				uadEntities.add(uadEntity);
			}

			if (!uadEntities.isEmpty()) {
				uadInfoPanelDisplay.addUADEntities(uadEntities);
				uadInfoPanelDisplay.setUADDisplay(uadDisplay);
			}
		}

		if (uadInfoPanelDisplay.getUADEntitiesCount() != 1) {
			String uadRegistryKey = ParamUtil.getString(
				resourceRequest, "uadRegistryKey");

			if (Validator.isNull(uadRegistryKey)) {
				uadRegistryKey = ParamUtil.getString(
					resourceRequest, "parentContainerTypeKey");
			}

			if (Validator.isNull(uadRegistryKey)) {
				String applicationKey = ParamUtil.getString(
					resourceRequest, "applicationKey");

				UADHierarchyDisplay uadHierarchyDisplay =
					_uadRegistry.getUADHierarchyDisplay(applicationKey);

				if (uadHierarchyDisplay != null) {
					uadRegistryKey =
						uadHierarchyDisplay.getFirstContainerTypeKey();
				}
				else {
					uadRegistryKey = ParamUtil.getString(
						resourceRequest,
						"uadRegistryKey__" + entityTypes.get(0));
				}
			}

			uadInfoPanelDisplay.setUADDisplay(
				(UADDisplay<Object>)_uadRegistry.getUADDisplay(uadRegistryKey));
		}

		uadInfoPanelDisplay.setHierarchyView(
			ParamUtil.getBoolean(resourceRequest, "hierarchyView"));
		uadInfoPanelDisplay.setTopLevelView(
			ParamUtil.getBoolean(resourceRequest, "topLevelView"));

		resourceRequest.setAttribute(
			UADWebKeys.UAD_INFO_PANEL_DISPLAY, uadInfoPanelDisplay);

		include(resourceRequest, resourceResponse, "/info_panel.jsp");
	}

	@Reference
	private UADRegistry _uadRegistry;

}