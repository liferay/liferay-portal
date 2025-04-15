/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.taglib.internal.jaxrs.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.liferay.frontend.data.set.model.FDSDataRow;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.data.set.provider.FDSActionProviderRegistry;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.FDSDataProviderRegistry;
import com.liferay.frontend.data.set.provider.search.FDSKeywordsFactory;
import com.liferay.frontend.data.set.provider.search.FDSKeywordsFactoryRegistry;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.frontend.data.set.taglib.internal.jaxrs.context.provider.PaginationContextProvider;
import com.liferay.frontend.data.set.taglib.internal.jaxrs.context.provider.SortContextProvider;
import com.liferay.frontend.data.set.taglib.internal.jaxrs.context.provider.ThemeDisplayContextProvider;
import com.liferay.frontend.data.set.taglib.internal.servlet.ServletContextUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/frontend-data-set-taglib/app",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=Liferay.Frontend.FDS",
		"auth.verifier.auth.verifier.PortalSessionAuthVerifier.urls.includes=/*",
		"auth.verifier.guest.allowed=true", "liferay.oauth2=false"
	},
	service = Application.class
)
public class FDSApplication extends Application {

	@DELETE
	@Path("/fds/{fdsName}/custom-views/{fdsCustomViewId}")
	public Response deleteFDSCustomView(
		@PathParam("fdsName") String fdsName,
		@PathParam("fdsCustomViewId") String fdsCustomViewId,
		@Context HttpServletRequest httpServletRequest,
		@Context ThemeDisplay themeDisplay) {

		try {
			PortalPreferences portalPreferences =
				PortletPreferencesFactoryUtil.getPortalPreferences(
					httpServletRequest);

			String fdsSettingsNamespace =
				ServletContextUtil.getFDSSettingsNamespace(
					httpServletRequest, fdsName);

			JSONObject customViewsJSONObject = _jsonFactory.createJSONObject(
				portalPreferences.getValue(
					fdsSettingsNamespace, "customViews", "{}"));

			customViewsJSONObject.remove(fdsCustomViewId);

			portalPreferences.setValue(
				fdsSettingsNamespace, "customViews",
				customViewsJSONObject.toString());

			return Response.noContent(
			).build();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Response.status(
			Response.Status.INTERNAL_SERVER_ERROR
		).build();
	}

	@GET
	@Path("/data-set/{tableName}/{fdsDataProviderKey}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFDSData(
		@PathParam("fdsDataProviderKey") String fdsDataProviderKey,
		@PathParam("tableName") String tableName,
		@QueryParam("groupId") long groupId, @QueryParam("plid") long plid,
		@QueryParam("portletId") String portletId,
		@Context HttpServletRequest httpServletRequest,
		@Context HttpServletResponse httpServletResponse,
		@Context FDSPagination fdsPagination, @Context Sort sort,
		@Context ThemeDisplay themeDisplay, @Context UriInfo uriInfo) {

		FDSDataProvider fdsDataProvider =
			_fdsDataProviderRegistry.getFDSDataProvider(fdsDataProviderKey);

		if ((fdsDataProvider == null) && _log.isDebugEnabled()) {
			_log.debug(
				"No frontend data set data provider is associated with " +
					fdsDataProviderKey);
		}

		try {
			FDSKeywordsFactory fdsKeywordsFactory =
				_fdsKeywordsFactoryRegistry.getFDSKeywordsFactory(
					fdsDataProviderKey);

			return Response.ok(
				_objectMapper.writeValueAsString(
					new FDSResponse(
						_getFDSTableRows(
							fdsDataProvider.getItems(
								fdsKeywordsFactory.create(httpServletRequest),
								fdsPagination, httpServletRequest, sort),
							tableName, httpServletRequest, groupId),
						fdsDataProvider.getItemsCount(
							fdsKeywordsFactory.create(httpServletRequest),
							httpServletRequest))),
				MediaType.APPLICATION_JSON
			).build();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Response.status(
			Response.Status.NOT_FOUND
		).build();
	}

	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();

		singletons.add(new PaginationContextProvider());
		singletons.add(new SortContextProvider());
		singletons.add(
			new ThemeDisplayContextProvider(_language, _layoutLocalService));
		singletons.add(this);

		return singletons;
	}

	@Path("/fds/{fdsName}/custom-views/{fdsCustomViewId}/label")
	@POST
	public Response renameFDSCustomView(
		@PathParam("fdsName") String fdsName,
		@PathParam("fdsCustomViewId") String fdsCustomViewId,
		@FormParam("customViewLabel") String fdsCustomViewLabel,
		@Context HttpServletRequest httpServletRequest,
		@Context ThemeDisplay themeDisplay) {

		try {
			PortalPreferences portalPreferences =
				PortletPreferencesFactoryUtil.getPortalPreferences(
					httpServletRequest);

			String fdsSettingsNamespace =
				ServletContextUtil.getFDSSettingsNamespace(
					httpServletRequest, fdsName);

			JSONObject customViewsJSONObject = _jsonFactory.createJSONObject(
				portalPreferences.getValue(
					fdsSettingsNamespace, "customViews", "{}"));

			JSONObject customViewJSONObject =
				customViewsJSONObject.getJSONObject(fdsCustomViewId);

			if (customViewJSONObject == null) {
				return Response.status(
					Response.Status.NOT_FOUND
				).build();
			}

			customViewJSONObject.put("customViewLabel", fdsCustomViewLabel);

			customViewsJSONObject.put(fdsCustomViewId, customViewJSONObject);

			portalPreferences.setValue(
				fdsSettingsNamespace, "customViews",
				customViewsJSONObject.toString());

			return Response.ok(
			).build();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Response.status(
			Response.Status.INTERNAL_SERVER_ERROR
		).build();
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/data-set/{id}/save-active-view-settings")
	@POST
	public Response saveActiveFDSViewSettings(
		@PathParam("id") String id,
		@Context HttpServletRequest httpServletRequest,
		@Context HttpServletResponse httpServletResponse,
		@Context ThemeDisplay themeDisplay, @Context UriInfo uriInfo,
		String activeViewSettingsJSON) {

		try {
			PortalPreferences portalPreferences =
				PortletPreferencesFactoryUtil.getPortalPreferences(
					httpServletRequest);

			String currentActiveViewSettingsJSON = portalPreferences.getValue(
				ServletContextUtil.getFDSSettingsNamespace(
					httpServletRequest, id),
				"activeViewSettingsJSON", "{}");

			JSONObject currentActiveViewSettingsJSONObject =
				_jsonFactory.createJSONObject(currentActiveViewSettingsJSON);

			JSONObject activeViewSettingsJSONObject =
				_jsonFactory.createJSONObject(activeViewSettingsJSON);

			for (String key : activeViewSettingsJSONObject.keySet()) {
				currentActiveViewSettingsJSONObject.put(
					key, activeViewSettingsJSONObject.get(key));
			}

			portalPreferences.setValue(
				ServletContextUtil.getFDSSettingsNamespace(
					httpServletRequest, id),
				"activeViewSettingsJSON",
				currentActiveViewSettingsJSONObject.toString());

			return Response.ok(
			).build();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Response.status(
			Response.Status.NOT_FOUND
		).build();
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/fds/{fdsName}/custom-views")
	@POST
	public Response saveFDSCustomView(
		@PathParam("fdsName") String fdsName,
		@Context HttpServletRequest httpServletRequest,
		@Context ThemeDisplay themeDisplay, String customViewJSON) {

		try {
			PortalPreferences portalPreferences =
				PortletPreferencesFactoryUtil.getPortalPreferences(
					httpServletRequest);

			String fdsSettingsNamespace =
				ServletContextUtil.getFDSSettingsNamespace(
					httpServletRequest, fdsName);

			JSONObject customViewsJSONObject = _jsonFactory.createJSONObject(
				portalPreferences.getValue(
					fdsSettingsNamespace, "customViews", "{}"));

			JSONObject customViewJSONObject = _jsonFactory.createJSONObject(
				customViewJSON);

			customViewsJSONObject.put(
				String.valueOf(customViewJSONObject.get("customViewId")),
				customViewJSONObject.get("viewState"));

			portalPreferences.setValue(
				fdsSettingsNamespace, "customViews",
				customViewsJSONObject.toString());

			return Response.ok(
			).build();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Response.status(
			Response.Status.INTERNAL_SERVER_ERROR
		).build();
	}

	private List<FDSDataRow> _getFDSTableRows(
			List<Object> items, String tableName,
			HttpServletRequest httpServletRequest, long groupId)
		throws Exception {

		List<FDSDataRow> fdsDataRows = new ArrayList<>();

		List<FDSActionProvider> fdsActionProviders =
			_fdsActionProviderRegistry.getFDSActionProviders(tableName);

		for (Object item : items) {
			FDSDataRow fdsDataRow = new FDSDataRow(item);

			if (fdsActionProviders != null) {
				for (FDSActionProvider fdsActionProvider : fdsActionProviders) {
					List<DropdownItem> actionDropdownItems =
						fdsActionProvider.getDropdownItems(
							groupId, httpServletRequest, item);

					if (actionDropdownItems != null) {
						fdsDataRow.addActionDropdownItems(actionDropdownItems);
					}
				}
			}

			fdsDataRows.add(fdsDataRow);
		}

		return fdsDataRows;
	}

	private static final Log _log = LogFactoryUtil.getLog(FDSApplication.class);

	private static final ObjectMapper _objectMapper = new ObjectMapper() {
		{
			configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
			disable(SerializationFeature.INDENT_OUTPUT);
		}
	};

	@Reference
	private FDSActionProviderRegistry _fdsActionProviderRegistry;

	@Reference
	private FDSDataProviderRegistry _fdsDataProviderRegistry;

	@Reference
	private FDSKeywordsFactoryRegistry _fdsKeywordsFactoryRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	private class FDSResponse {

		public FDSResponse(List<FDSDataRow> fdsDataRows, int totalCount) {
			_fdsDataRows = fdsDataRows;
			_totalCount = totalCount;
		}

		@JsonProperty("items")
		private final List<FDSDataRow> _fdsDataRows;

		@JsonProperty("totalCount")
		private final int _totalCount;

	}

}