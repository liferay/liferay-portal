/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A list of services the organization provides. This follows the [`Service`](https://www.schema.org/Service) specification.",
	value = "Service"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Service")
public class Service implements Serializable {

	public static Service toDTO(String json) {
		return ObjectMapperUtil.readValue(Service.class, json);
	}

	public static Service unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Service.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of hours when the organization is open. This follows the [`OpeningHoursSpecification`](https://www.schema.org/OpeningHoursSpecification) specification."
	)
	@Valid
	public HoursAvailable[] getHoursAvailable() {
		if (_hoursAvailableSupplier != null) {
			hoursAvailable = _hoursAvailableSupplier.get();

			_hoursAvailableSupplier = null;
		}

		return hoursAvailable;
	}

	public void setHoursAvailable(HoursAvailable[] hoursAvailable) {
		this.hoursAvailable = hoursAvailable;

		_hoursAvailableSupplier = null;
	}

	@JsonIgnore
	public void setHoursAvailable(
		UnsafeSupplier<HoursAvailable[], Exception>
			hoursAvailableUnsafeSupplier) {

		_hoursAvailableSupplier = () -> {
			try {
				return hoursAvailableUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A list of hours when the organization is open. This follows the [`OpeningHoursSpecification`](https://www.schema.org/OpeningHoursSpecification) specification."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected HoursAvailable[] hoursAvailable;

	@JsonIgnore
	private Supplier<HoursAvailable[]> _hoursAvailableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of service the organization provides."
	)
	public String getServiceType() {
		if (_serviceTypeSupplier != null) {
			serviceType = _serviceTypeSupplier.get();

			_serviceTypeSupplier = null;
		}

		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;

		_serviceTypeSupplier = null;
	}

	@JsonIgnore
	public void setServiceType(
		UnsafeSupplier<String, Exception> serviceTypeUnsafeSupplier) {

		_serviceTypeSupplier = () -> {
			try {
				return serviceTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The type of service the organization provides."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String serviceType;

	@JsonIgnore
	private Supplier<String> _serviceTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Service)) {
			return false;
		}

		Service service = (Service)object;

		return Objects.equals(toString(), service.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		HoursAvailable[] hoursAvailable = getHoursAvailable();

		if (hoursAvailable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hoursAvailable\": ");

			sb.append("[");

			for (int i = 0; i < hoursAvailable.length; i++) {
				sb.append(String.valueOf(hoursAvailable[i]));

				if ((i + 1) < hoursAvailable.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String serviceType = getServiceType();

		if (serviceType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"serviceType\": ");

			sb.append("\"");

			sb.append(_escape(serviceType));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.Service",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}