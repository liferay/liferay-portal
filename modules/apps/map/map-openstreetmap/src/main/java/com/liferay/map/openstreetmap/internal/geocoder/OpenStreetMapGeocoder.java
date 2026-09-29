/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.openstreetmap.internal.geocoder;

import com.liferay.map.geocoder.Geocoder;
import com.liferay.map.geocoder.GeocoderResult;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.url.URLBuilder;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(property = "geocoder.key=OpenStreetMap", service = Geocoder.class)
public class OpenStreetMapGeocoder implements Geocoder {

	@Override
	public GeocoderResult geocode(String address, long companyId, long groupId)
		throws Exception {

		String json = _getJSON(
			URLBuilder.create(
				_SEARCH_URL
			).addParameter(
				"format", "json"
			).addParameter(
				"q", address
			).build());

		if (Validator.isNull(json)) {
			return null;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		if (JSONUtil.isEmpty(jsonArray)) {
			return null;
		}

		return _getGeocoderResult(jsonArray.getJSONObject(0));
	}

	@Override
	public GeocoderResult reverseGeocode(
			long companyId, long groupId, double latitude, double longitude)
		throws Exception {

		String json = _getJSON(
			URLBuilder.create(
				_REVERSE_URL
			).addParameter(
				"format", "json"
			).addParameter(
				"lat", String.valueOf(latitude)
			).addParameter(
				"lon", String.valueOf(longitude)
			).build());

		if (Validator.isNull(json)) {
			return null;
		}

		return _getGeocoderResult(_jsonFactory.createJSONObject(json));
	}

	private GeocoderResult _getGeocoderResult(JSONObject jsonObject) {
		if (JSONUtil.isEmpty(jsonObject)) {
			return null;
		}

		return new GeocoderResult(
			jsonObject.getString("display_name"),
			GetterUtil.getDouble(jsonObject.getString("lat")),
			GetterUtil.getDouble(jsonObject.getString("lon")));
	}

	private String _getJSON(String url) throws Exception {
		Http.Options options = new Http.Options();

		options.addHeader("User-Agent", "Liferay-Portal");
		options.setLocation(url);

		String json = _http.URLtoString(options);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() != HttpServletResponse.SC_OK) {
			return null;
		}

		return json;
	}

	private static final String _REVERSE_URL =
		"https://nominatim.openstreetmap.org/reverse";

	private static final String _SEARCH_URL =
		"https://nominatim.openstreetmap.org/search";

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}