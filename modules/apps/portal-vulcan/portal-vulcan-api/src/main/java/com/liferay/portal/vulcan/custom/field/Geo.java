/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.custom.field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.function.Supplier;

/**
 * @author Carlos Correa
 */
@GraphQLName(
	description = "A point determined by latitude and longitude.", value = "Geo"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Geo")
public class Geo implements Serializable {

	public static Geo toDTO(String json) {
		return ObjectMapperUtil.readValue(Geo.class, json);
	}

	public static Geo unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Geo.class, json);
	}

	@Schema(description = "The latitude of a point in space.")
	public Double getLatitude() {
		if (_latitudeSupplier != null) {
			latitude = _latitudeSupplier.get();

			_latitudeSupplier = null;
		}

		return latitude;
	}

	@Schema(description = "The longitude of a point in space.")
	public Double getLongitude() {
		if (_longitudeSupplier != null) {
			longitude = _longitudeSupplier.get();

			_longitudeSupplier = null;
		}

		return longitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;

		_latitudeSupplier = null;
	}

	@JsonIgnore
	public void setLatitude(
		UnsafeSupplier<Double, Exception> latitudeUnsafeSupplier) {

		_latitudeSupplier = () -> {
			try {
				return latitudeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;

		_longitudeSupplier = null;
	}

	@JsonIgnore
	public void setLongitude(
		UnsafeSupplier<Double, Exception> longitudeUnsafeSupplier) {

		_longitudeSupplier = () -> {
			try {
				return longitudeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Schema(
		accessMode = Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.vulcan.custom.field.Geo",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLField(description = "The latitude of a point in space.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double latitude;

	@GraphQLField(description = "The longitude of a point in space.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double longitude;

	@JsonIgnore
	private Supplier<Double> _latitudeSupplier;

	@JsonIgnore
	private Supplier<Double> _longitudeSupplier;

}