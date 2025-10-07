package com.liferay.testray.rest.dto.v1_0;

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

import javax.annotation.Generated;

import javax.validation.Valid;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Nilton Vieira
 * @generated
 */
@Generated("")
@GraphQLName("TestrayIssueMetric")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TestrayIssueMetric")
public class TestrayIssueMetric implements Serializable {

	public static TestrayIssueMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(TestrayIssueMetric.class, json);
	}

	public static TestrayIssueMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(TestrayIssueMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTestrayIssueKey() {
		if (_testrayIssueKeySupplier != null) {
			testrayIssueKey = _testrayIssueKeySupplier.get();

			_testrayIssueKeySupplier = null;
		}

		return testrayIssueKey;
	}

	public void setTestrayIssueKey(String testrayIssueKey) {
		this.testrayIssueKey = testrayIssueKey;

		_testrayIssueKeySupplier = null;
	}

	@JsonIgnore
	public void setTestrayIssueKey(
		UnsafeSupplier<String, Exception> testrayIssueKeyUnsafeSupplier) {

		_testrayIssueKeySupplier = () -> {
			try {
				return testrayIssueKeyUnsafeSupplier.get();
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
	protected String testrayIssueKey;

	@JsonIgnore
	private Supplier<String> _testrayIssueKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTestrayIssueTitle() {
		if (_testrayIssueTitleSupplier != null) {
			testrayIssueTitle = _testrayIssueTitleSupplier.get();

			_testrayIssueTitleSupplier = null;
		}

		return testrayIssueTitle;
	}

	public void setTestrayIssueTitle(String testrayIssueTitle) {
		this.testrayIssueTitle = testrayIssueTitle;

		_testrayIssueTitleSupplier = null;
	}

	@JsonIgnore
	public void setTestrayIssueTitle(
		UnsafeSupplier<String, Exception> testrayIssueTitleUnsafeSupplier) {

		_testrayIssueTitleSupplier = () -> {
			try {
				return testrayIssueTitleUnsafeSupplier.get();
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
	protected String testrayIssueTitle;

	@JsonIgnore
	private Supplier<String> _testrayIssueTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTestrayIssueType() {
		if (_testrayIssueTypeSupplier != null) {
			testrayIssueType = _testrayIssueTypeSupplier.get();

			_testrayIssueTypeSupplier = null;
		}

		return testrayIssueType;
	}

	public void setTestrayIssueType(String testrayIssueType) {
		this.testrayIssueType = testrayIssueType;

		_testrayIssueTypeSupplier = null;
	}

	@JsonIgnore
	public void setTestrayIssueType(
		UnsafeSupplier<String, Exception> testrayIssueTypeUnsafeSupplier) {

		_testrayIssueTypeSupplier = () -> {
			try {
				return testrayIssueTypeUnsafeSupplier.get();
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
	protected String testrayIssueType;

	@JsonIgnore
	private Supplier<String> _testrayIssueTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public TestrayStatusMetric getTestrayStatusMetric() {
		if (_testrayStatusMetricSupplier != null) {
			testrayStatusMetric = _testrayStatusMetricSupplier.get();

			_testrayStatusMetricSupplier = null;
		}

		return testrayStatusMetric;
	}

	public void setTestrayStatusMetric(
		TestrayStatusMetric testrayStatusMetric) {

		this.testrayStatusMetric = testrayStatusMetric;

		_testrayStatusMetricSupplier = null;
	}

	@JsonIgnore
	public void setTestrayStatusMetric(
		UnsafeSupplier<TestrayStatusMetric, Exception>
			testrayStatusMetricUnsafeSupplier) {

		_testrayStatusMetricSupplier = () -> {
			try {
				return testrayStatusMetricUnsafeSupplier.get();
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
	protected TestrayStatusMetric testrayStatusMetric;

	@JsonIgnore
	private Supplier<TestrayStatusMetric> _testrayStatusMetricSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TestrayIssueMetric)) {
			return false;
		}

		TestrayIssueMetric testrayIssueMetric = (TestrayIssueMetric)object;

		return Objects.equals(toString(), testrayIssueMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String testrayIssueKey = getTestrayIssueKey();

		if (testrayIssueKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"testrayIssueKey\": ");

			sb.append("\"");

			sb.append(_escape(testrayIssueKey));

			sb.append("\"");
		}

		String testrayIssueTitle = getTestrayIssueTitle();

		if (testrayIssueTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"testrayIssueTitle\": ");

			sb.append("\"");

			sb.append(_escape(testrayIssueTitle));

			sb.append("\"");
		}

		String testrayIssueType = getTestrayIssueType();

		if (testrayIssueType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"testrayIssueType\": ");

			sb.append("\"");

			sb.append(_escape(testrayIssueType));

			sb.append("\"");
		}

		TestrayStatusMetric testrayStatusMetric = getTestrayStatusMetric();

		if (testrayStatusMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"testrayStatusMetric\": ");

			sb.append(String.valueOf(testrayStatusMetric));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.testray.rest.dto.v1_0.TestrayIssueMetric",
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