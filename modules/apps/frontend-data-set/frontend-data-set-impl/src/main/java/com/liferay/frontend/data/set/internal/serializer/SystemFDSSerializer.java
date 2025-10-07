/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.serializer;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.SystemFDSEntryRegistry;
import com.liferay.frontend.data.set.action.FDSBulkActions;
import com.liferay.frontend.data.set.action.FDSBulkActionsRegistry;
import com.liferay.frontend.data.set.action.FDSCreationMenu;
import com.liferay.frontend.data.set.action.FDSCreationMenuRegistry;
import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.action.FDSItemsActionsRegistry;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilterContextContributor;
import com.liferay.frontend.data.set.filter.FDSFilterContextContributorRegistry;
import com.liferay.frontend.data.set.filter.FDSFilterRegistry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.frontend.data.set.sort.FDSSorts;
import com.liferay.frontend.data.set.sort.FDSSortsRegistry;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewContextContributor;
import com.liferay.frontend.data.set.view.FDSViewContextContributorRegistry;
import com.liferay.frontend.data.set.view.FDSViewRegistry;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 */
@Component(
	property = "frontend.data.set.serializer.type=" + FDSSerializer.TYPE_SYSTEM,
	service = FDSSerializer.class
)
public class SystemFDSSerializer
	extends BaseFDSSerializer implements FDSSerializer {

	@Override
	public boolean isAvailable(
		String fdsName, HttpServletRequest httpServletRequest) {

		if ((fdsViewRegistry.getFDSViews(fdsName) != null) ||
			(systemFDSEntryRegistry.getSystemFDSEntry(fdsName) != null)) {

			return true;
		}

		return false;
	}

	@Override
	public String serializeAdditionalAPIURLParameters(
		String fdsName, HttpServletRequest httpServletRequest,
		boolean interpolate, JSONObject tokenResolutionsJSONObject) {

		SystemFDSEntry systemFDSEntry =
			systemFDSEntryRegistry.getSystemFDSEntry(fdsName);

		if (systemFDSEntry == null) {
			return null;
		}

		return createFDSAPIURLBuilder(
			httpServletRequest, systemFDSEntry.getRESTApplication(),
			systemFDSEntry.getRESTEndpoint(), systemFDSEntry.getRESTSchema()
		).addQueryString(
			systemFDSEntry.getAdditionalAPIURLParameters()
		).setTokenResolutions(
			tokenResolutionsJSONObject
		).buildQueryString(
			interpolate
		);
	}

	@Override
	public String serializeAPIURL(
		String fdsName, HttpServletRequest httpServletRequest,
		boolean interpolate, JSONObject tokenResolutionsJSONObject) {

		SystemFDSEntry systemFDSEntry =
			systemFDSEntryRegistry.getSystemFDSEntry(fdsName);

		if (systemFDSEntry == null) {
			return null;
		}

		return createFDSAPIURLBuilder(
			httpServletRequest, systemFDSEntry.getRESTApplication(),
			systemFDSEntry.getRESTEndpoint(), systemFDSEntry.getRESTSchema()
		).setTokenResolutions(
			tokenResolutionsJSONObject
		).build(
			interpolate
		);
	}

	@Override
	public List<FDSActionDropdownItem> serializeBulkActions(
		String fdsName, HttpServletRequest httpServletRequest) {

		FDSBulkActions fdsBulkActions =
			fdsBulkActionsRegistry.getFDSBulkActions(fdsName);

		if (fdsBulkActions == null) {
			return Collections.emptyList();
		}

		return fdsBulkActions.getFDSActionDropdownItems(httpServletRequest);
	}

	@Override
	public CreationMenu serializeCreationMenu(
		String fdsName, HttpServletRequest httpServletRequest) {

		FDSCreationMenu fdsCreationMenu =
			fdsCreationMenuRegistry.getFDSCreationMenu(fdsName);

		if (fdsCreationMenu == null) {
			return new CreationMenu();
		}

		return fdsCreationMenu.getCreationMenu(httpServletRequest);
	}

	@Override
	public JSONArray serializeFilters(
		List<FDSFilter> fdsFilters, String fdsName,
		HttpServletRequest httpServletRequest) {

		JSONArray jsonArray = JSONUtil.putAll();

		Locale locale = PortalUtil.getLocale(httpServletRequest);

		_serializeFilters(fdsFilters, jsonArray, locale);
		_serializeFilters(
			fdsFilterRegistry.getFDSFilters(fdsName), jsonArray, locale);

		return jsonArray;
	}

	@Override
	public JSONArray serializeFilters(
		String fdsName, HttpServletRequest httpServletRequest) {

		return serializeFilters(
			Collections.emptyList(), fdsName, httpServletRequest);
	}

	@Override
	public List<FDSActionDropdownItem> serializeItemsActions(
		String fdsName, HttpServletRequest httpServletRequest) {

		FDSItemsActions fdsItemsActions =
			fdsItemsActionsRegistry.getFDSItemsActions(fdsName);

		if (fdsItemsActions == null) {
			return Collections.emptyList();
		}

		return fdsItemsActions.getFDSActionDropdownItems(httpServletRequest);
	}

	@Override
	public JSONObject serializePagination(
		String fdsName, HttpServletRequest httpServletRequest) {

		SystemFDSEntry systemFDSEntry =
			systemFDSEntryRegistry.getSystemFDSEntry(fdsName);

		if (systemFDSEntry == null) {
			return null;
		}

		return JSONUtil.put(
			"deltas",
			() -> {
				int[] listOfItemsPerPage =
					systemFDSEntry.getListOfItemsPerPage();

				if (ArrayUtil.isEmpty(listOfItemsPerPage)) {
					listOfItemsPerPage =
						_systemFDSEntry.getListOfItemsPerPage();
				}

				return JSONUtil.toJSONArray(
					ListUtil.fromArray(listOfItemsPerPage),
					itemsPerPage -> {
						if (itemsPerPage > 0) {
							return JSONUtil.put("label", itemsPerPage);
						}

						return null;
					});
			}
		).put(
			"initialDelta",
			() -> {
				if (systemFDSEntry.getDefaultItemsPerPage() > 0) {
					return systemFDSEntry.getDefaultItemsPerPage();
				}

				return _systemFDSEntry.getDefaultItemsPerPage();
			}
		);
	}

	@Override
	public String serializePropsTransformer(
		String fdsName, HttpServletRequest httpServletRequest) {

		SystemFDSEntry systemFDSEntry =
			systemFDSEntryRegistry.getSystemFDSEntry(fdsName);

		if (systemFDSEntry == null) {
			return null;
		}

		return systemFDSEntry.getPropsTransformer();
	}

	@Override
	public List<FDSSortItem> serializeSorts(
		String fdsName, HttpServletRequest httpServletRequest) {

		FDSSorts fdsSorts = fdsSortsRegistry.getFDSSorts(fdsName);

		if (fdsSorts == null) {
			return Collections.emptyList();
		}

		return fdsSorts.getFDSSortItems(httpServletRequest);
	}

	@Override
	public JSONArray serializeViews(
		String fdsName, HttpServletRequest httpServletRequest) {

		JSONArray jsonArray = JSONUtil.putAll();

		for (FDSView fdsView : fdsViewRegistry.getFDSViews(fdsName)) {
			JSONObject jsonObject = JSONUtil.put(
				"contentRenderer", fdsView.getContentRenderer()
			).put(
				"contentRendererModuleURL",
				fdsView.getContentRendererModuleURL()
			).put(
				"default", fdsView.isDefault()
			).put(
				"label",
				LanguageUtil.get(
					ResourceBundleUtil.getBundle(
						"content.Language",
						PortalUtil.getLocale(httpServletRequest), getClass()),
					fdsView.getLabel())
			).put(
				"name", fdsView.getName()
			).put(
				"thumbnail", fdsView.getThumbnail()
			);

			List<FDSViewContextContributor> fdsViewContextContributors =
				fdsViewContextContributorRegistry.getFDSViewContextContributors(
					fdsView.getContentRenderer());

			for (FDSViewContextContributor fdsViewContextContributor :
					fdsViewContextContributors) {

				Map<String, Object> fdsViewContext =
					fdsViewContextContributor.getFDSViewContext(
						fdsView, PortalUtil.getLocale(httpServletRequest));

				if (fdsViewContext == null) {
					continue;
				}

				for (Map.Entry<String, Object> entry :
						fdsViewContext.entrySet()) {

					jsonObject.put(entry.getKey(), entry.getValue());
				}
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	@Reference
	protected FDSBulkActionsRegistry fdsBulkActionsRegistry;

	@Reference
	protected FDSCreationMenuRegistry fdsCreationMenuRegistry;

	@Reference
	protected FDSFilterContextContributorRegistry
		fdsFilterContextContributorRegistry;

	@Reference
	protected FDSFilterRegistry fdsFilterRegistry;

	@Reference
	protected FDSItemsActionsRegistry fdsItemsActionsRegistry;

	@Reference
	protected FDSSortsRegistry fdsSortsRegistry;

	@Reference
	protected FDSViewContextContributorRegistry
		fdsViewContextContributorRegistry;

	@Reference
	protected FDSViewRegistry fdsViewRegistry;

	@Reference
	protected SystemFDSEntryRegistry systemFDSEntryRegistry;

	private void _serializeFilters(
		List<FDSFilter> fdsFilters, JSONArray jsonArray, Locale locale) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		for (FDSFilter fdsFilter : fdsFilters) {
			if (!fdsFilter.isEnabled()) {
				continue;
			}

			JSONObject jsonObject = JSONUtil.put(
				"entityFieldType", fdsFilter.getEntityFieldType()
			).put(
				"id", fdsFilter.getId()
			).put(
				"label", LanguageUtil.get(resourceBundle, fdsFilter.getLabel())
			).put(
				"preloadedData", fdsFilter.getPreloadedData()
			).put(
				"type", fdsFilter.getType()
			);

			List<FDSFilterContextContributor> fdsFilterContextContributors =
				fdsFilterContextContributorRegistry.
					getFDSFilterContextContributors(fdsFilter.getType());

			for (FDSFilterContextContributor fdsFilterContextContributor :
					fdsFilterContextContributors) {

				Map<String, Object> fdsFilterContext =
					fdsFilterContextContributor.getFDSFilterContext(
						fdsFilter, locale);

				if (fdsFilterContext == null) {
					continue;
				}

				for (Map.Entry<String, Object> entry :
						fdsFilterContext.entrySet()) {

					jsonObject.put(entry.getKey(), entry.getValue());
				}
			}

			jsonArray.put(jsonObject);
		}
	}

	private static final SystemFDSEntry _systemFDSEntry = new SystemFDSEntry() {

		public int getDefaultItemsPerPage() {
			return SystemFDSEntry.super.getDefaultItemsPerPage();
		}

		@Override
		public String getDescription() {
			return "";
		}

		public int[] getListOfItemsPerPage() {
			return SystemFDSEntry.super.getListOfItemsPerPage();
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public String getRESTApplication() {
			return "";
		}

		@Override
		public String getRESTEndpoint() {
			return "";
		}

		@Override
		public String getRESTSchema() {
			return "";
		}

		@Override
		public String getTitle() {
			return "";
		}

	};

}