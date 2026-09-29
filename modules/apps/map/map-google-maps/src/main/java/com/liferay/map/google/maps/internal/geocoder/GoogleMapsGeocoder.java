/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.google.maps.internal.geocoder;

import com.liferay.map.geocoder.Geocoder;
import com.liferay.map.geocoder.GeocoderResult;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.url.URLBuilder;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.security.key.secret.SecretResolver;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(property = "geocoder.key=GoogleMaps", service = Geocoder.class)
public class GoogleMapsGeocoder implements Geocoder {

	@Override
	public GeocoderResult geocode(String address, long companyId, long groupId)
		throws Exception {

		return _getGeocoderResult(companyId, groupId, "address", address);
	}

	@Override
	public GeocoderResult reverseGeocode(
			long companyId, long groupId, double latitude, double longitude)
		throws Exception {

		return _getGeocoderResult(
			companyId, groupId, "latlng", latitude + "," + longitude);
	}

	private String _getAPIKey(long companyId, long groupId) {
		PortletPreferences companyPortletPreferences =
			PrefsPropsUtil.getPreferences(companyId);

		String googleMapsAPIKey = companyPortletPreferences.getValue(
			"googleMapsAPIKey", null);

		Group group = _groupLocalService.fetchGroup(groupId);

		if ((group != null) && !group.isControlPanel()) {
			googleMapsAPIKey = GetterUtil.getString(
				group.getTypeSettingsProperty("googleMapsAPIKey"),
				googleMapsAPIKey);
		}

		return _secretResolver.resolve(companyId, googleMapsAPIKey);
	}

	private GeocoderResult _getGeocoderResult(
			long companyId, long groupId, String parameterName,
			String parameterValue)
		throws Exception {

		Http.Options options = new Http.Options();

		options.setLocation(
			URLBuilder.create(
				_API_URL
			).addParameter(
				parameterName, parameterValue
			).addParameter(
				"key", _getAPIKey(companyId, groupId)
			).build());

		String json = _http.URLtoString(options);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() != HttpServletResponse.SC_OK) {
			return null;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(json);

		if (JSONUtil.isEmpty(jsonObject)) {
			return null;
		}

		JSONArray resultsJSONArray = jsonObject.getJSONArray("results");

		if (JSONUtil.isEmpty(resultsJSONArray)) {
			return null;
		}

		JSONObject resultJSONObject = resultsJSONArray.getJSONObject(0);

		if (JSONUtil.isEmpty(resultJSONObject)) {
			return null;
		}

		JSONObject geometryJSONObject = resultJSONObject.getJSONObject(
			"geometry");

		JSONObject locationJSONObject = geometryJSONObject.getJSONObject(
			"location");

		return new GeocoderResult(
			resultJSONObject.getString("formatted_address"),
			locationJSONObject.getDouble("lat"),
			locationJSONObject.getDouble("lng"));
	}

	private static final String _API_URL =
		"https://maps.googleapis.com/maps/api/geocode/json";

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SecretResolver _secretResolver;

}