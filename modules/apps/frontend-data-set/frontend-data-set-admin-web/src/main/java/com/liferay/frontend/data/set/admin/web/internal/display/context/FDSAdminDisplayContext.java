/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.display.context;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.SystemFDSEntryRegistry;
import com.liferay.frontend.data.set.admin.web.internal.constants.FDSAdminPortletKeys;
import com.liferay.frontend.data.set.admin.web.internal.portlet.FDSAdminPortlet;
import com.liferay.frontend.data.set.url.FDSAPIURLResolver;
import com.liferay.frontend.data.set.url.FDSAPIURLResolverRegistry;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.definition.security.permission.resource.ObjectDefinitionPortletResourcePermissionRegistryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author Marko Cikos
 */
public class FDSAdminDisplayContext {

	public FDSAdminDisplayContext(
		CETManager cetManager,
		FDSAPIURLResolverRegistry fdsAPIURLResolverRegistry,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		RenderRequest renderRequest, RenderResponse renderResponse,
		ServiceTrackerList<FDSAdminPortlet.CompanyScopedOpenAPIResource>
			serviceTrackerList,
		SystemFDSEntryRegistry systemFDSEntryRegistry) {

		_cetManager = cetManager;
		_fdsAPIURLResolverRegistry = fdsAPIURLResolverRegistry;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_serviceTrackerList = serviceTrackerList;
		_systemFDSEntryRegistry = systemFDSEntryRegistry;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_dataSetObjectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				_themeDisplay.getCompanyId(), "DataSet");
	}

	public JSONArray getCellClientExtensionRenderersJSONArray()
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return JSONUtil.toJSONArray(
			_cetManager.getCETs(
				themeDisplay.getCompanyId(), null,
				ClientExtensionEntryConstants.TYPE_FDS_CELL_RENDERER,
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null),
			fdsCellRendererCET -> JSONUtil.put(
				"externalReferenceCode",
				fdsCellRendererCET.getExternalReferenceCode()
			).put(
				"name", fdsCellRendererCET.getName(themeDisplay.getLocale())
			));
	}

	public String getDataSetPermissionsURL() {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_renderRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setRedirect(
			PortletURLUtil.getCurrent(_renderRequest, _renderResponse)
		).setParameter(
			"modelResource", _dataSetObjectDefinition.getClassName()
		).setParameter(
			"modelResourceDescription",
			_dataSetObjectDefinition.getLabel(_themeDisplay.getLocale())
		).setParameter(
			"resourcePrimKey", "{id}"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getEditDataSetURL() {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				_renderRequest, FDSAdminPortletKeys.FDS_ADMIN,
				RenderRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/frontend_data_set_admin/edit_data_set"
		).setBackURL(
			_themeDisplay.getURLCurrent()
		).buildString();
	}

	public JSONArray getFDSFilterCETsJSONArray() throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return JSONUtil.toJSONArray(
			_cetManager.getCETs(
				themeDisplay.getCompanyId(), null,
				ClientExtensionEntryConstants.TYPE_FDS_FILTER,
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null),
			fdsFilterCET -> JSONUtil.put(
				"externalReferenceCode", fdsFilterCET.getExternalReferenceCode()
			).put(
				"name", fdsFilterCET.getName(themeDisplay.getLocale())
			));
	}

	public String getImportSystemDataSetURL() {
		ResourceURL resourceURL =
			(ResourceURL)PortalUtil.getControlPanelPortletURL(
				_renderRequest, _themeDisplay.getScopeGroup(),
				FDSAdminPortletKeys.FDS_ADMIN, 0, 0,
				RenderRequest.RESOURCE_PHASE);

		resourceURL.setResourceID(
			"/frontend_data_set_admin/import_system_data_set");

		return resourceURL.toString();
	}

	public JSONArray getRESTApplicationResolvedSchemasJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<FDSAPIURLResolver> fdsAPIURLResolvers =
			_fdsAPIURLResolverRegistry.getFDSAPIURLResolvers();

		for (FDSAPIURLResolver fdsAPIURLResolver : fdsAPIURLResolvers) {
			jsonArray.put(fdsAPIURLResolver.getSchema());
		}

		return jsonArray;
	}

	public JSONArray getRESTApplicationsJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<FDSAdminPortlet.CompanyScopedOpenAPIResource>
			companyScopedOpenAPIResources = _serviceTrackerList.toList();

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		companyScopedOpenAPIResources = ListUtil.filter(
			companyScopedOpenAPIResources,
			companyScopedOpenAPIResource ->
				companyScopedOpenAPIResource.matches(
					themeDisplay.getCompanyId()));

		Collections.sort(
			companyScopedOpenAPIResources,
			Comparator.comparing(
				FDSAdminPortlet.CompanyScopedOpenAPIResource::
					getOpenAPIResourcePath,
				String::compareTo));

		for (FDSAdminPortlet.CompanyScopedOpenAPIResource
				companyScopedOpenAPIResource : companyScopedOpenAPIResources) {

			jsonArray.put(
				companyScopedOpenAPIResource.getOpenAPIResourcePath());
		}

		return jsonArray;
	}

	public String getSaveDataSetSortURL() {
		ResourceURL resourceURL =
			(ResourceURL)PortalUtil.getControlPanelPortletURL(
				_renderRequest, _themeDisplay.getScopeGroup(),
				FDSAdminPortletKeys.FDS_ADMIN, 0, 0,
				RenderRequest.RESOURCE_PHASE);

		resourceURL.setResourceID(
			"/frontend_data_set_admin/save_data_set_sort");

		return resourceURL.toString();
	}

	public String getSaveDataSetTableSectionsURL() {
		ResourceURL resourceURL =
			(ResourceURL)PortalUtil.getControlPanelPortletURL(
				_renderRequest, _themeDisplay.getScopeGroup(),
				FDSAdminPortletKeys.FDS_ADMIN, 0, 0,
				RenderRequest.RESOURCE_PHASE);

		resourceURL.setResourceID(
			"/frontend_data_set_admin/save_data_set_table_sections");

		return resourceURL.toString();
	}

	public String getSystemDataSetsURL() {
		ResourceURL resourceURL =
			(ResourceURL)PortalUtil.getControlPanelPortletURL(
				_renderRequest, _themeDisplay.getScopeGroup(),
				FDSAdminPortletKeys.FDS_ADMIN, 0, 0,
				RenderRequest.RESOURCE_PHASE);

		resourceURL.setResourceID(
			"/frontend_data_set_admin/get_system_data_sets");

		return resourceURL.toString();
	}

	public JSONArray getSystemFDSEntryJSONArray() throws Exception {
		Set<String> systemFDSNames =
			_systemFDSEntryRegistry.getSystemFDSNames();

		if (systemFDSNames == null) {
			return JSONFactoryUtil.createJSONArray();
		}

		return JSONUtil.toJSONArray(
			systemFDSNames,
			systemFDSName -> {
				SystemFDSEntry systemFDSEntry =
					_systemFDSEntryRegistry.getSystemFDSEntry(systemFDSName);

				return JSONUtil.put(
					"additionalAPIURLParameters",
					systemFDSEntry.getAdditionalAPIURLParameters()
				).put(
					"defaultItemsPerPage",
					systemFDSEntry.getDefaultItemsPerPage()
				).put(
					"description", systemFDSEntry.getDescription()
				).put(
					"name", systemFDSEntry.getName()
				).put(
					"restApplication", systemFDSEntry.getRESTApplication()
				).put(
					"restEndpoint", systemFDSEntry.getRESTEndpoint()
				).put(
					"restSchema", systemFDSEntry.getRESTSchema()
				).put(
					"symbol", systemFDSEntry.getSymbol()
				).put(
					"title", systemFDSEntry.getTitle()
				);
			});
	}

	public boolean hasAddDataSetObjectEntryPermission() {
		PortletResourcePermission portletResourcePermission =
			ObjectDefinitionPortletResourcePermissionRegistryUtil.getService(
				_dataSetObjectDefinition.getResourceName());

		return portletResourcePermission.contains(
			_themeDisplay.getPermissionChecker(), 0,
			ObjectActionKeys.ADD_OBJECT_ENTRY);
	}

	private final CETManager _cetManager;
	private final ObjectDefinition _dataSetObjectDefinition;
	private final FDSAPIURLResolverRegistry _fdsAPIURLResolverRegistry;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ServiceTrackerList
		<FDSAdminPortlet.CompanyScopedOpenAPIResource> _serviceTrackerList;
	private final SystemFDSEntryRegistry _systemFDSEntryRegistry;
	private final ThemeDisplay _themeDisplay;

}