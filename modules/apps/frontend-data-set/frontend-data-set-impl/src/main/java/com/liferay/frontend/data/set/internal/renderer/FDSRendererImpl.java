/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.renderer;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.data.set.renderer.FDSRenderer;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 */
@Component(service = FDSRenderer.class)
public class FDSRendererImpl implements FDSRenderer {

	public String getFDSAPIURL(
		String fdsName, HttpServletRequest httpServletRequest,
		boolean interpolate, JSONObject tokenResolutionsJSONObject) {

		FDSSerializer fdsSerializer = _getFDSSerializer(
			fdsName, httpServletRequest);

		if (fdsSerializer != null) {
			String fdsAPIURL = fdsSerializer.serializeAPIURL(
				fdsName, httpServletRequest, interpolate,
				tokenResolutionsJSONObject);

			String additionalAPIURLParameters =
				fdsSerializer.serializeAdditionalAPIURLParameters(
					fdsName, httpServletRequest, interpolate,
					tokenResolutionsJSONObject);

			if (fdsAPIURL.contains(StringPool.QUESTION) &&
				Validator.isNotNull(additionalAPIURLParameters)) {

				return fdsAPIURL + StringPool.AMPERSAND +
					additionalAPIURLParameters;
			}

			if (Validator.isNotNull(additionalAPIURLParameters)) {
				return fdsAPIURL + StringPool.QUESTION +
					additionalAPIURLParameters;
			}

			return fdsAPIURL;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"No frontend data set serializer is associated with " +
					fdsName);
		}

		return StringPool.BLANK;
	}

	@Override
	public void render(
		Map<String, Object> baseProps, String componentId, String fdsName,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, boolean inline,
		String propsTransformer, Writer writer) {

		Map<String, Object> props = new HashMap<>();

		if (baseProps != null) {
			props.putAll(baseProps);
		}

		FDSSerializer fdsSerializer = _getFDSSerializer(
			fdsName, httpServletRequest);

		if (fdsSerializer == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No frontend data set serializer is associated with " +
						fdsName);
			}
		}
		else {
			props.putAll(
				HashMapBuilder.<String, Object>put(
					"additionalAPIURLParameters",
					() -> {
						String additionalAPIURLParameters =
							fdsSerializer.serializeAdditionalAPIURLParameters(
								fdsName, httpServletRequest, true,
								(JSONObject)props.get("tokenResolutions"));

						if (Validator.isNull(additionalAPIURLParameters)) {
							return null;
						}

						return additionalAPIURLParameters;
					}
				).put(
					"apiURL",
					() -> {
						String apiURL = fdsSerializer.serializeAPIURL(
							fdsName, httpServletRequest, true,
							(JSONObject)props.get("tokenResolutions"));

						if (Validator.isNull(apiURL)) {
							return null;
						}

						return apiURL;
					}
				).put(
					"bulkActions",
					() -> {
						List<FDSActionDropdownItem> fdsActionDropdownItems =
							fdsSerializer.serializeBulkActions(
								fdsName, httpServletRequest);

						if (ListUtil.isEmpty(fdsActionDropdownItems)) {
							return null;
						}

						return fdsActionDropdownItems;
					}
				).put(
					"creationMenu",
					() -> {
						CreationMenu creationMenu =
							fdsSerializer.serializeCreationMenu(
								fdsName, httpServletRequest);

						if ((creationMenu == null) || creationMenu.isEmpty()) {
							return null;
						}

						return creationMenu;
					}
				).put(
					"currentURL", _portal.getCurrentURL(httpServletRequest)
				).put(
					"filters",
					() -> {
						JSONArray filtersJSONArray =
							fdsSerializer.serializeFilters(
								fdsName, httpServletRequest);

						if (JSONUtil.isEmpty(filtersJSONArray)) {
							return null;
						}

						return filtersJSONArray;
					}
				).put(
					"itemsActions",
					() -> {
						List<FDSActionDropdownItem> fdsActionDropdownItems =
							fdsSerializer.serializeItemsActions(
								fdsName, httpServletRequest);

						if (ListUtil.isEmpty(fdsActionDropdownItems)) {
							return null;
						}

						return fdsActionDropdownItems;
					}
				).put(
					"pagination",
					() -> {
						JSONObject paginationJSONObject =
							fdsSerializer.serializePagination(
								fdsName, httpServletRequest);

						if (JSONUtil.isEmpty(paginationJSONObject)) {
							return null;
						}

						return paginationJSONObject;
					}
				).put(
					"sorts",
					() -> {
						List<FDSSortItem> fdsSortItems =
							fdsSerializer.serializeSorts(
								fdsName, httpServletRequest);

						if (ListUtil.isEmpty(fdsSortItems)) {
							return null;
						}

						return fdsSortItems;
					}
				).put(
					"views",
					() -> {
						JSONArray viewsJSONArray = fdsSerializer.serializeViews(
							fdsName, httpServletRequest);

						if (JSONUtil.isEmpty(viewsJSONArray)) {
							return null;
						}

						return viewsJSONArray;
					}
				).build());

			String tempPropsTransformer =
				fdsSerializer.serializePropsTransformer(
					fdsName, httpServletRequest);

			if (Validator.isNotNull(tempPropsTransformer)) {
				propsTransformer = tempPropsTransformer;
			}
		}

		try {
			_reactRenderer.renderReact(
				new ComponentDescriptor(
					"{FrontendDataSet} from frontend-data-set-web", componentId,
					null, inline, propsTransformer),
				props, httpServletRequest, writer);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to render frontend data set " + fdsName, exception);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, FDSSerializer.class,
			"frontend.data.set.serializer.type");
	}

	private FDSSerializer _getFDSSerializer(
		String fdsName, HttpServletRequest httpServletRequest) {

		for (String type : FDSSerializer.FDS_TYPES) {
			FDSSerializer fdsSerializer = _serviceTrackerMap.getService(type);

			if ((fdsSerializer != null) &&
				fdsSerializer.isAvailable(fdsName, httpServletRequest)) {

				return fdsSerializer;
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FDSRendererImpl.class);

	private BundleContext _bundleContext;

	@Reference
	private Portal _portal;

	@Reference
	private ReactRenderer _reactRenderer;

	private ServiceTrackerMap<String, FDSSerializer> _serviceTrackerMap;

}