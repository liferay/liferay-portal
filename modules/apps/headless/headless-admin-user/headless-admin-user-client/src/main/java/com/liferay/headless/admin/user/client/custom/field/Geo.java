/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.custom.field;

import com.liferay.headless.admin.user.client.function.UnsafeSupplier;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Geo {

	public static Geo toDTO(String json) {
		GeoJSONParser geoJSONParser = new GeoJSONParser();

		return geoJSONParser.parseToDTO(json);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Geo)) {
			return false;
		}

		Geo geo = (Geo)object;

		return Objects.equals(toString(), geo.toString());
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLatitude(
		UnsafeSupplier<Double, Exception> latitudeUnsafeSupplier) {

		try {
			latitude = latitudeUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public void setLongitude(
		UnsafeSupplier<Double, Exception> longitudeUnsafeSupplier) {

		try {
			longitude = longitudeUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public String toString() {
		return GeoJSONParser.toJSON(this);
	}

	protected Double latitude;
	protected Double longitude;

	private static class GeoJSONParser extends BaseJSONParser<Geo> {

		public static String toJSON(Geo geo) {
			if (geo == null) {
				return "null";
			}

			StringBuilder sb = new StringBuilder();

			sb.append("{");

			if (geo.getLatitude() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"latitude\": ");

				sb.append(geo.getLatitude());
			}

			if (geo.getLongitude() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"longitude\": ");

				sb.append(geo.getLongitude());
			}

			sb.append("}");

			return sb.toString();
		}

		@Override
		protected Geo createDTO() {
			return new Geo();
		}

		@Override
		protected Geo[] createDTOArray(int size) {
			return new Geo[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "latitude")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "longitude")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Geo geo, String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "latitude")) {
				if (jsonParserFieldValue != null) {
					geo.setLatitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "longitude")) {
				if (jsonParserFieldValue != null) {
					geo.setLongitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
		}

	}

}