/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rodrigo Paulino
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/export_data_definition"
	},
	service = MVCResourceCommand.class
)
public class ExportDataDefinitionMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long dataDefinitionId = ParamUtil.getLong(
			resourceRequest, "dataDefinitionId");

		DataDefinitionResource.Builder dataDefinitionResourcedBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourcedBuilder.user(
				themeDisplay.getUser()
			).build();

		DataDefinition dataDefinition =
			dataDefinitionResource.getDataDefinition(dataDefinitionId);

		Map<String, Object> nameMap = dataDefinition.getName();

		_sanitize(dataDefinition);

		String dataDefinitionString = String.valueOf(dataDefinition);

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse,
			StringBundler.concat(
				"Structure_",
				nameMap.get(dataDefinition.getDefaultLanguageId()),
				StringPool.UNDERLINE, dataDefinitionId, StringPool.UNDERLINE,
				Time.getTimestamp(), ".json"),
			dataDefinitionString.getBytes(), ContentTypes.APPLICATION_JSON);
	}

	private void _sanitize(DataDefinition dataDefinition) {
		dataDefinition.setDateCreated(() -> (Date)null);
		dataDefinition.setDateModified(() -> (Date)null);
		dataDefinition.setId(() -> (Long)null);
		dataDefinition.setSiteId(() -> (Long)null);
		dataDefinition.setUserId(() -> (Long)null);

		DataLayout dataLayout = dataDefinition.getDefaultDataLayout();

		dataLayout.setDataDefinitionId(() -> (Long)null);
		dataLayout.setDataLayoutKey(() -> (String)null);
		dataLayout.setDateCreated(() -> (Date)null);
		dataLayout.setDateModified(() -> (Date)null);
		dataLayout.setId(() -> (Long)null);
		dataLayout.setSiteId(() -> (Long)null);
		dataLayout.setUserId(() -> (Long)null);
	}

	@Reference
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

}