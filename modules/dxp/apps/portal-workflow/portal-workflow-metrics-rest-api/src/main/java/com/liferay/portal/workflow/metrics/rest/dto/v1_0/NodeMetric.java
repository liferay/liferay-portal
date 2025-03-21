/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.dto.v1_0;

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
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "https://www.schema.org/NodeMetric", value = "NodeMetric"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "NodeMetric")
public class NodeMetric implements Serializable {

	public static NodeMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(NodeMetric.class, json);
	}

	public static NodeMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(NodeMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getBreachedInstanceCount() {
		if (_breachedInstanceCountSupplier != null) {
			breachedInstanceCount = _breachedInstanceCountSupplier.get();

			_breachedInstanceCountSupplier = null;
		}

		return breachedInstanceCount;
	}

	public void setBreachedInstanceCount(Long breachedInstanceCount) {
		this.breachedInstanceCount = breachedInstanceCount;

		_breachedInstanceCountSupplier = null;
	}

	@JsonIgnore
	public void setBreachedInstanceCount(
		UnsafeSupplier<Long, Exception> breachedInstanceCountUnsafeSupplier) {

		_breachedInstanceCountSupplier = () -> {
			try {
				return breachedInstanceCountUnsafeSupplier.get();
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
	protected Long breachedInstanceCount;

	@JsonIgnore
	private Supplier<Long> _breachedInstanceCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getBreachedInstancePercentage() {
		if (_breachedInstancePercentageSupplier != null) {
			breachedInstancePercentage =
				_breachedInstancePercentageSupplier.get();

			_breachedInstancePercentageSupplier = null;
		}

		return breachedInstancePercentage;
	}

	public void setBreachedInstancePercentage(
		Double breachedInstancePercentage) {

		this.breachedInstancePercentage = breachedInstancePercentage;

		_breachedInstancePercentageSupplier = null;
	}

	@JsonIgnore
	public void setBreachedInstancePercentage(
		UnsafeSupplier<Double, Exception>
			breachedInstancePercentageUnsafeSupplier) {

		_breachedInstancePercentageSupplier = () -> {
			try {
				return breachedInstancePercentageUnsafeSupplier.get();
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
	protected Double breachedInstancePercentage;

	@JsonIgnore
	private Supplier<Double> _breachedInstancePercentageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getDurationAvg() {
		if (_durationAvgSupplier != null) {
			durationAvg = _durationAvgSupplier.get();

			_durationAvgSupplier = null;
		}

		return durationAvg;
	}

	public void setDurationAvg(Long durationAvg) {
		this.durationAvg = durationAvg;

		_durationAvgSupplier = null;
	}

	@JsonIgnore
	public void setDurationAvg(
		UnsafeSupplier<Long, Exception> durationAvgUnsafeSupplier) {

		_durationAvgSupplier = () -> {
			try {
				return durationAvgUnsafeSupplier.get();
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
	protected Long durationAvg;

	@JsonIgnore
	private Supplier<Long> _durationAvgSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getInstanceCount() {
		if (_instanceCountSupplier != null) {
			instanceCount = _instanceCountSupplier.get();

			_instanceCountSupplier = null;
		}

		return instanceCount;
	}

	public void setInstanceCount(Long instanceCount) {
		this.instanceCount = instanceCount;

		_instanceCountSupplier = null;
	}

	@JsonIgnore
	public void setInstanceCount(
		UnsafeSupplier<Long, Exception> instanceCountUnsafeSupplier) {

		_instanceCountSupplier = () -> {
			try {
				return instanceCountUnsafeSupplier.get();
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
	protected Long instanceCount;

	@JsonIgnore
	private Supplier<Long> _instanceCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Node getNode() {
		if (_nodeSupplier != null) {
			node = _nodeSupplier.get();

			_nodeSupplier = null;
		}

		return node;
	}

	public void setNode(Node node) {
		this.node = node;

		_nodeSupplier = null;
	}

	@JsonIgnore
	public void setNode(UnsafeSupplier<Node, Exception> nodeUnsafeSupplier) {
		_nodeSupplier = () -> {
			try {
				return nodeUnsafeSupplier.get();
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
	protected Node node;

	@JsonIgnore
	private Supplier<Node> _nodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getOnTimeInstanceCount() {
		if (_onTimeInstanceCountSupplier != null) {
			onTimeInstanceCount = _onTimeInstanceCountSupplier.get();

			_onTimeInstanceCountSupplier = null;
		}

		return onTimeInstanceCount;
	}

	public void setOnTimeInstanceCount(Long onTimeInstanceCount) {
		this.onTimeInstanceCount = onTimeInstanceCount;

		_onTimeInstanceCountSupplier = null;
	}

	@JsonIgnore
	public void setOnTimeInstanceCount(
		UnsafeSupplier<Long, Exception> onTimeInstanceCountUnsafeSupplier) {

		_onTimeInstanceCountSupplier = () -> {
			try {
				return onTimeInstanceCountUnsafeSupplier.get();
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
	protected Long onTimeInstanceCount;

	@JsonIgnore
	private Supplier<Long> _onTimeInstanceCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getOverdueInstanceCount() {
		if (_overdueInstanceCountSupplier != null) {
			overdueInstanceCount = _overdueInstanceCountSupplier.get();

			_overdueInstanceCountSupplier = null;
		}

		return overdueInstanceCount;
	}

	public void setOverdueInstanceCount(Long overdueInstanceCount) {
		this.overdueInstanceCount = overdueInstanceCount;

		_overdueInstanceCountSupplier = null;
	}

	@JsonIgnore
	public void setOverdueInstanceCount(
		UnsafeSupplier<Long, Exception> overdueInstanceCountUnsafeSupplier) {

		_overdueInstanceCountSupplier = () -> {
			try {
				return overdueInstanceCountUnsafeSupplier.get();
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
	protected Long overdueInstanceCount;

	@JsonIgnore
	private Supplier<Long> _overdueInstanceCountSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NodeMetric)) {
			return false;
		}

		NodeMetric nodeMetric = (NodeMetric)object;

		return Objects.equals(toString(), nodeMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long breachedInstanceCount = getBreachedInstanceCount();

		if (breachedInstanceCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"breachedInstanceCount\": ");

			sb.append(breachedInstanceCount);
		}

		Double breachedInstancePercentage = getBreachedInstancePercentage();

		if (breachedInstancePercentage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"breachedInstancePercentage\": ");

			sb.append(breachedInstancePercentage);
		}

		Long durationAvg = getDurationAvg();

		if (durationAvg != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"durationAvg\": ");

			sb.append(durationAvg);
		}

		Long instanceCount = getInstanceCount();

		if (instanceCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"instanceCount\": ");

			sb.append(instanceCount);
		}

		Node node = getNode();

		if (node != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"node\": ");

			sb.append(String.valueOf(node));
		}

		Long onTimeInstanceCount = getOnTimeInstanceCount();

		if (onTimeInstanceCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"onTimeInstanceCount\": ");

			sb.append(onTimeInstanceCount);
		}

		Long overdueInstanceCount = getOverdueInstanceCount();

		if (overdueInstanceCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"overdueInstanceCount\": ");

			sb.append(overdueInstanceCount);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.workflow.metrics.rest.dto.v1_0.NodeMetric",
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