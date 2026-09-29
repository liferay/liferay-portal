/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.map.geocoder.Geocoder;
import com.liferay.map.geocoder.GeocoderRegistry;
import com.liferay.map.geocoder.GeocoderResult;
import com.liferay.map.util.MapProviderHelperUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.Coordinates;
import com.liferay.object.rest.dto.v1_0.Location;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.io.Serializable;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_LOCATION,
	service = ObjectFieldBusinessType.class
)
public class LocationObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_CLOB;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.LOCATION;
	}

	@Override
	public Serializable getDTOValue(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ObjectField objectField, Serializable serializable)
		throws Exception {

		if (serializable instanceof Location) {
			return serializable;
		}

		if (!(serializable instanceof Map)) {
			return null;
		}

		Map<String, Serializable> locationMap =
			(Map<String, Serializable>)serializable;

		Map<String, Serializable> coordinatesMap =
			(Map<String, Serializable>)locationMap.get("coordinates");

		if (MapUtil.isNotEmpty(coordinatesMap)) {
			return Location.toDTO(_jsonFactory.looseSerializeDeep(locationMap));
		}

		return new Location() {
			{
				setAddress(() -> MapUtil.getString(locationMap, "address"));
				setCoordinates(
					() -> new Coordinates() {
						{
							setLatitude(
								() -> MapUtil.getDouble(
									locationMap, "latitude"));
							setLongitude(
								() -> MapUtil.getDouble(
									locationMap, "longitude"));
						}
					});
			}
		};
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			locale, "store-addresses-and-precise-geographic-coordinates");
	}

	@Override
	public Object getDisplayContextValue(
			ObjectField objectField, long userId, Map<String, Object> values)
		throws PortalException {

		if (objectField.isLocalized()) {
			Map<String, Object> localizedValues = super.getLocalizedValues(
				null, objectField, userId, values);

			if (localizedValues == null) {
				return null;
			}

			for (Map.Entry<String, Object> entry : localizedValues.entrySet()) {
				localizedValues.put(
					entry.getKey(), String.valueOf(entry.getValue()));
			}

			return localizedValues;
		}

		return values.get(objectField.getName());
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "location");
	}

	@Override
	public Map<String, Object> getLocalizedValues(
			Long groupId, ObjectField objectField, Long userId,
			Map<String, Object> values)
		throws PortalException {

		Map<String, Object> localizedValues = super.getLocalizedValues(
			groupId, objectField, userId, values);

		if (localizedValues == null) {
			return null;
		}

		for (Map.Entry<String, Object> entry : localizedValues.entrySet()) {
			localizedValues.put(
				entry.getKey(),
				_getValue(groupId, objectField, entry.getValue()));
		}

		return localizedValues;
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_LOCATION;
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.TEXT;
	}

	@Override
	public Object getValue(
			Long groupId, ObjectField objectField, long userId,
			Map<String, Object> values)
		throws PortalException {

		return _getValue(
			groupId, objectField,
			super.getValue(groupId, objectField, userId, values));
	}

	@Override
	public boolean isVisible(ObjectDefinition objectDefinition) {
		return FeatureFlagManagerUtil.isEnabled(
			objectDefinition.getCompanyId(), "LPD-11388");
	}

	private Object _getValue(
			Long groupId, ObjectField objectField, Object value)
		throws PortalException {

		if (value == null) {
			return null;
		}

		try {
			if (value instanceof Location location) {
				return _getValue(
					location.getAddress(), objectField.getCompanyId(),
					location.getCoordinates(), GetterUtil.getLong(groupId));
			}
			else if (value instanceof Map) {
				Map<String, Serializable> locationMap =
					(Map<String, Serializable>)value;

				return _getValue(
					MapUtil.getString(locationMap, "address"),
					objectField.getCompanyId(), locationMap.get("coordinates"),
					GetterUtil.getLong(groupId));
			}
			else if (value instanceof String) {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					(String)value);

				return _getValue(
					jsonObject.getString("address"), objectField.getCompanyId(),
					jsonObject.getJSONObject("coordinates"),
					GetterUtil.getLong(groupId));
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new ObjectEntryValuesException.InvalidValue(
				objectField.getName());
		}

		return null;
	}

	private Object _getValue(
			String address, long companyId, Object coordinates, long groupId)
		throws Exception {

		Geocoder geocoder = _geocoderRegistry.getGeocoder(
			GetterUtil.getString(
				MapProviderHelperUtil.getMapProviderKey(
					_groupLocalService, companyId, groupId),
				"OpenStreetMap"));

		if (geocoder == null) {
			throw new UnsupportedOperationException();
		}

		Double latitude = null;
		Double longitude = null;

		if (coordinates != null) {
			if (coordinates instanceof Coordinates dtoCoordinates) {
				latitude = dtoCoordinates.getLatitude();
				longitude = dtoCoordinates.getLongitude();
			}
			else if (coordinates instanceof JSONObject coordinatesJSONObject) {
				latitude = coordinatesJSONObject.getDouble("latitude");
				longitude = coordinatesJSONObject.getDouble("longitude");
			}
			else if (coordinates instanceof Map) {
				Map<String, Serializable> coordinatesMap =
					(Map<String, Serializable>)coordinates;

				if (MapUtil.isNotEmpty(coordinatesMap)) {
					latitude = MapUtil.getDouble(coordinatesMap, "latitude");
					longitude = MapUtil.getDouble(coordinatesMap, "longitude");
				}
			}
		}

		if (Validator.isNull(address) && (latitude == null) &&
			(longitude == null)) {

			return Collections.emptyMap();
		}

		GeocoderResult geocoderResult = null;

		if ((latitude != null) && (longitude != null)) {
			geocoderResult = geocoder.reverseGeocode(
				companyId, groupId, latitude, longitude);
		}
		else {
			geocoderResult = geocoder.geocode(address, companyId, groupId);
		}

		if ((geocoderResult == null) ||
			Validator.isNull(geocoderResult.getAddress())) {

			throw new IllegalArgumentException("Unable to resolve location");
		}

		return HashMapBuilder.<String, Serializable>put(
			"address", geocoderResult.getAddress()
		).put(
			"latitude", geocoderResult.getLatitude()
		).put(
			"longitude", geocoderResult.getLongitude()
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LocationObjectFieldBusinessType.class);

	@Reference
	private GeocoderRegistry _geocoderRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}