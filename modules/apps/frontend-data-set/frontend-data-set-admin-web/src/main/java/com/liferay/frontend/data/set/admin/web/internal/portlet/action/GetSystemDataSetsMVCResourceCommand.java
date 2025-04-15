/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.portlet.action;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.SystemFDSEntryRegistry;
import com.liferay.frontend.data.set.admin.web.internal.constants.FDSAdminPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.util.TransformUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FDSAdminPortletKeys.FDS_ADMIN,
		"mvc.command.name=/frontend_data_set_admin/get_system_data_sets"
	},
	service = MVCResourceCommand.class
)
public class GetSystemDataSetsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		Set<String> systemFDSNames =
			_systemFDSEntryRegistry.getSystemFDSNames();

		if (systemFDSNames == null) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put("items", _jsonFactory.createJSONArray()));

			return;
		}

		List<SystemFDSEntry> systemFDSEntries = TransformUtil.transform(
			systemFDSNames,
			systemFDSName -> _systemFDSEntryRegistry.getSystemFDSEntry(
				systemFDSName));

		Collections.sort(
			systemFDSEntries,
			Comparator.comparing(
				systemFDSEntry -> {
					if (systemFDSEntry != null) {
						return systemFDSEntry.getTitle();
					}

					return StringPool.BLANK;
				}));

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ObjectDefinition dataSetObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET", themeDisplay.getCompanyId());

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(resourceRequest));

		String search = ParamUtil.getString(httpServletRequest, "search");

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"items",
				JSONUtil.toJSONArray(
					systemFDSEntries,
					systemFDSEntry -> {
						if (!StringUtil.matchesIgnoreCase(
								systemFDSEntry.getTitle(), search)) {

							return null;
						}

						ObjectEntry objectEntry =
							_objectEntryLocalService.fetchObjectEntry(
								systemFDSEntry.getName(),
								dataSetObjectDefinition.
									getObjectDefinitionId());

						return JSONUtil.put(
							"additionalAPIURLParameters",
							systemFDSEntry.getAdditionalAPIURLParameters()
						).put(
							"defaultItemsPerPage",
							systemFDSEntry.getDefaultItemsPerPage()
						).put(
							"description", systemFDSEntry.getDescription()
						).put(
							"imported", objectEntry != null
						).put(
							"name", systemFDSEntry.getName()
						).put(
							"restApplication",
							systemFDSEntry.getRESTApplication()
						).put(
							"restEndpoint", systemFDSEntry.getRESTEndpoint()
						).put(
							"restSchema", systemFDSEntry.getRESTSchema()
						).put(
							"symbol", systemFDSEntry.getSymbol()
						).put(
							"title", systemFDSEntry.getTitle()
						);
					})));
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SystemFDSEntryRegistry _systemFDSEntryRegistry;

}