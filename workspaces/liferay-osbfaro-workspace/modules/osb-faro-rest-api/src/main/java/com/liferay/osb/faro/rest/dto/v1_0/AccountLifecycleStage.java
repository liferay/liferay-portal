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
	description = "A stage of an account lifecycle. Each lifecycle has one stage per stage type, ordered by display order.",
	value = "AccountLifecycleStage"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A stage of an account lifecycle. Each lifecycle has one stage per stage type, ordered by display order."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AccountLifecycleStage")
public class AccountLifecycleStage implements Serializable {

	public static AccountLifecycleStage toDTO(String json) {
		return ObjectMapperUtil.readValue(AccountLifecycleStage.class, json);
	}

	public static AccountLifecycleStage unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			AccountLifecycleStage.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Description of the stage."
	)
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Description of the stage.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Position of the stage in the lifecycle, starting at 1."
	)
	public Integer getDisplayOrder() {
		if (_displayOrderSupplier != null) {
			displayOrder = _displayOrderSupplier.get();

			_displayOrderSupplier = null;
		}

		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;

		_displayOrderSupplier = null;
	}

	@JsonIgnore
	public void setDisplayOrder(
		UnsafeSupplier<Integer, Exception> displayOrderUnsafeSupplier) {

		_displayOrderSupplier = () -> {
			try {
				return displayOrderUnsafeSupplier.get();
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
		description = "Position of the stage in the lifecycle, starting at 1."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer displayOrder;

	@JsonIgnore
	private Supplier<Integer> _displayOrderSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(description = "Stage ID.")
	public String getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(String id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Stage ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Maximum number of days an account is expected to stay in the stage before it is considered stalled. Null when no limit is set."
	)
	public Integer getMaxDuration() {
		if (_maxDurationSupplier != null) {
			maxDuration = _maxDurationSupplier.get();

			_maxDurationSupplier = null;
		}

		return maxDuration;
	}

	public void setMaxDuration(Integer maxDuration) {
		this.maxDuration = maxDuration;

		_maxDurationSupplier = null;
	}

	@JsonIgnore
	public void setMaxDuration(
		UnsafeSupplier<Integer, Exception> maxDurationUnsafeSupplier) {

		_maxDurationSupplier = () -> {
			try {
				return maxDurationUnsafeSupplier.get();
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
		description = "Maximum number of days an account is expected to stay in the stage before it is considered stalled. Null when no limit is set."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer maxDuration;

	@JsonIgnore
	private Supplier<Integer> _maxDurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Stage type: AWARE, ENGAGED, PIPELINE, ONBOARDING, ESTABLISHED, or AT_RISK. Use it (or the description) as `fromLifecycleStage` or `toLifecycleStage` to filter stage transitions."
	)
	public String getStageType() {
		if (_stageTypeSupplier != null) {
			stageType = _stageTypeSupplier.get();

			_stageTypeSupplier = null;
		}

		return stageType;
	}

	public void setStageType(String stageType) {
		this.stageType = stageType;

		_stageTypeSupplier = null;
	}

	@JsonIgnore
	public void setStageType(
		UnsafeSupplier<String, Exception> stageTypeUnsafeSupplier) {

		_stageTypeSupplier = () -> {
			try {
				return stageTypeUnsafeSupplier.get();
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
		description = "Stage type: AWARE, ENGAGED, PIPELINE, ONBOARDING, ESTABLISHED, or AT_RISK. Use it (or the description) as `fromLifecycleStage` or `toLifecycleStage` to filter stage transitions."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String stageType;

	@JsonIgnore
	private Supplier<String> _stageTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountLifecycleStage)) {
			return false;
		}

		AccountLifecycleStage accountLifecycleStage =
			(AccountLifecycleStage)object;

		return Objects.equals(toString(), accountLifecycleStage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		Integer displayOrder = getDisplayOrder();

		if (displayOrder != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayOrder\": ");

			sb.append(displayOrder);
		}

		String id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(id));

			sb.append("\"");
		}

		Integer maxDuration = getMaxDuration();

		if (maxDuration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxDuration\": ");

			sb.append(maxDuration);
		}

		String stageType = getStageType();

		if (stageType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stageType\": ");

			sb.append("\"");

			sb.append(_escape(stageType));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStage",
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
// LIFERAY-REST-BUILDER-HASH:1000084595