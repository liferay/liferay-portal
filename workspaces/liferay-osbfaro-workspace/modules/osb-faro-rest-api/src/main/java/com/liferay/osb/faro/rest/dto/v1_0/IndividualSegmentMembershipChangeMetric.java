/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "How an individual segment's membership changed over the selected date range, bucketed by day. `individuals` and `knownIndividuals` are the segment's size: `value` is the size at the end of the range and `previousValue` the size at the end of the preceding range of the same length. `addedIndividuals` and `removedIndividuals` are how much the segment grew and shrank, summed over the range. Those two are derived from the change in size between consecutive days, so on any one day only one of them can be non-zero: a day on which 10 individuals joined and 10 left reports zero for both, and a day on which 12 joined and 10 left reports 2 added and 0 removed. Read them as net movement, not as counts of individuals entering and leaving. For the current member list use `getWorkspaceGroupIndividualSegmentMembershipsPage`.",
	value = "IndividualSegmentMembershipChangeMetric"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "How an individual segment's membership changed over the selected date range, bucketed by day. `individuals` and `knownIndividuals` are the segment's size: `value` is the size at the end of the range and `previousValue` the size at the end of the preceding range of the same length. `addedIndividuals` and `removedIndividuals` are how much the segment grew and shrank, summed over the range. Those two are derived from the change in size between consecutive days, so on any one day only one of them can be non-zero: a day on which 10 individuals joined and 10 left reports zero for both, and a day on which 12 joined and 10 left reports 2 added and 0 removed. Read them as net movement, not as counts of individuals entering and leaving. For the current member list use `getWorkspaceGroupIndividualSegmentMembershipsPage`."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "IndividualSegmentMembershipChangeMetric")
public class IndividualSegmentMembershipChangeMetric implements Serializable {

	public static IndividualSegmentMembershipChangeMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(
			IndividualSegmentMembershipChangeMetric.class, json);
	}

	public static IndividualSegmentMembershipChangeMetric unsafeToDTO(
		String json) {

		return ObjectMapperUtil.unsafeReadValue(
			IndividualSegmentMembershipChangeMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric getAddedIndividuals() {
		if (_addedIndividualsSupplier != null) {
			addedIndividuals = _addedIndividualsSupplier.get();

			_addedIndividualsSupplier = null;
		}

		return addedIndividuals;
	}

	public void setAddedIndividuals(Metric addedIndividuals) {
		this.addedIndividuals = addedIndividuals;

		_addedIndividualsSupplier = null;
	}

	@JsonIgnore
	public void setAddedIndividuals(
		UnsafeSupplier<Metric, Exception> addedIndividualsUnsafeSupplier) {

		_addedIndividualsSupplier = () -> {
			try {
				return addedIndividualsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Metric addedIndividuals;

	@JsonIgnore
	private Supplier<Metric> _addedIndividualsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric getIndividuals() {
		if (_individualsSupplier != null) {
			individuals = _individualsSupplier.get();

			_individualsSupplier = null;
		}

		return individuals;
	}

	public void setIndividuals(Metric individuals) {
		this.individuals = individuals;

		_individualsSupplier = null;
	}

	@JsonIgnore
	public void setIndividuals(
		UnsafeSupplier<Metric, Exception> individualsUnsafeSupplier) {

		_individualsSupplier = () -> {
			try {
				return individualsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Metric individuals;

	@JsonIgnore
	private Supplier<Metric> _individualsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric getKnownIndividuals() {
		if (_knownIndividualsSupplier != null) {
			knownIndividuals = _knownIndividualsSupplier.get();

			_knownIndividualsSupplier = null;
		}

		return knownIndividuals;
	}

	public void setKnownIndividuals(Metric knownIndividuals) {
		this.knownIndividuals = knownIndividuals;

		_knownIndividualsSupplier = null;
	}

	@JsonIgnore
	public void setKnownIndividuals(
		UnsafeSupplier<Metric, Exception> knownIndividualsUnsafeSupplier) {

		_knownIndividualsSupplier = () -> {
			try {
				return knownIndividualsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Metric knownIndividuals;

	@JsonIgnore
	private Supplier<Metric> _knownIndividualsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric getRemovedIndividuals() {
		if (_removedIndividualsSupplier != null) {
			removedIndividuals = _removedIndividualsSupplier.get();

			_removedIndividualsSupplier = null;
		}

		return removedIndividuals;
	}

	public void setRemovedIndividuals(Metric removedIndividuals) {
		this.removedIndividuals = removedIndividuals;

		_removedIndividualsSupplier = null;
	}

	@JsonIgnore
	public void setRemovedIndividuals(
		UnsafeSupplier<Metric, Exception> removedIndividualsUnsafeSupplier) {

		_removedIndividualsSupplier = () -> {
			try {
				return removedIndividualsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Metric removedIndividuals;

	@JsonIgnore
	private Supplier<Metric> _removedIndividualsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof IndividualSegmentMembershipChangeMetric)) {
			return false;
		}

		IndividualSegmentMembershipChangeMetric
			individualSegmentMembershipChangeMetric =
				(IndividualSegmentMembershipChangeMetric)object;

		return Objects.equals(
			toString(), individualSegmentMembershipChangeMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Metric addedIndividuals = getAddedIndividuals();

		if (addedIndividuals != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addedIndividuals\": ");

			sb.append(String.valueOf(addedIndividuals));
		}

		Metric individuals = getIndividuals();

		if (individuals != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individuals\": ");

			sb.append(String.valueOf(individuals));
		}

		Metric knownIndividuals = getKnownIndividuals();

		if (knownIndividuals != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"knownIndividuals\": ");

			sb.append(String.valueOf(knownIndividuals));
		}

		Metric removedIndividuals = getRemovedIndividuals();

		if (removedIndividuals != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"removedIndividuals\": ");

			sb.append(String.valueOf(removedIndividuals));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembershipChangeMetric",
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
// LIFERAY-REST-BUILDER-HASH:1208748557