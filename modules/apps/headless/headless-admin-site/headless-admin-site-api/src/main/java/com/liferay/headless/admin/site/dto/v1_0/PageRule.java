/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(description = "The definition of a page rule.", value = "PageRule")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageRule")
public class PageRule implements Serializable {

	public static PageRule toDTO(String json) {
		return ObjectMapperUtil.readValue(PageRule.class, json);
	}

	public static PageRule unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PageRule.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The custom name of a Page rule."
	)
	@JsonGetter("conditionType")
	@Valid
	public ConditionType getConditionType() {
		if (_conditionTypeSupplier != null) {
			conditionType = _conditionTypeSupplier.get();

			_conditionTypeSupplier = null;
		}

		return conditionType;
	}

	@JsonIgnore
	public String getConditionTypeAsString() {
		ConditionType conditionType = getConditionType();

		if (conditionType == null) {
			return null;
		}

		return conditionType.toString();
	}

	public void setConditionType(ConditionType conditionType) {
		this.conditionType = conditionType;

		_conditionTypeSupplier = null;
	}

	@JsonIgnore
	public void setConditionType(
		UnsafeSupplier<ConditionType, Exception> conditionTypeUnsafeSupplier) {

		_conditionTypeSupplier = () -> {
			try {
				return conditionTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The custom name of a Page rule.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ConditionType conditionType;

	@JsonIgnore
	private Supplier<ConditionType> _conditionTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page rule external reference code."
	)
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page rule external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The custom name of a page rule."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The custom name of a page rule.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of actions of a page rule."
	)
	@Valid
	public PageRuleAction[] getPageRuleActions() {
		if (_pageRuleActionsSupplier != null) {
			pageRuleActions = _pageRuleActionsSupplier.get();

			_pageRuleActionsSupplier = null;
		}

		return pageRuleActions;
	}

	public void setPageRuleActions(PageRuleAction[] pageRuleActions) {
		this.pageRuleActions = pageRuleActions;

		_pageRuleActionsSupplier = null;
	}

	@JsonIgnore
	public void setPageRuleActions(
		UnsafeSupplier<PageRuleAction[], Exception>
			pageRuleActionsUnsafeSupplier) {

		_pageRuleActionsSupplier = () -> {
			try {
				return pageRuleActionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of actions of a page rule.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PageRuleAction[] pageRuleActions;

	@JsonIgnore
	private Supplier<PageRuleAction[]> _pageRuleActionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of conditions of a page rule."
	)
	@Valid
	public PageRuleCondition[] getPageRuleConditions() {
		if (_pageRuleConditionsSupplier != null) {
			pageRuleConditions = _pageRuleConditionsSupplier.get();

			_pageRuleConditionsSupplier = null;
		}

		return pageRuleConditions;
	}

	public void setPageRuleConditions(PageRuleCondition[] pageRuleConditions) {
		this.pageRuleConditions = pageRuleConditions;

		_pageRuleConditionsSupplier = null;
	}

	@JsonIgnore
	public void setPageRuleConditions(
		UnsafeSupplier<PageRuleCondition[], Exception>
			pageRuleConditionsUnsafeSupplier) {

		_pageRuleConditionsSupplier = () -> {
			try {
				return pageRuleConditionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of conditions of a page rule.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PageRuleCondition[] pageRuleConditions;

	@JsonIgnore
	private Supplier<PageRuleCondition[]> _pageRuleConditionsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageRule)) {
			return false;
		}

		PageRule pageRule = (PageRule)object;

		return Objects.equals(toString(), pageRule.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		ConditionType conditionType = getConditionType();

		if (conditionType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"conditionType\": ");

			sb.append("\"");

			sb.append(conditionType);

			sb.append("\"");
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		PageRuleAction[] pageRuleActions = getPageRuleActions();

		if (pageRuleActions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageRuleActions\": ");

			sb.append("[");

			for (int i = 0; i < pageRuleActions.length; i++) {
				sb.append(String.valueOf(pageRuleActions[i]));

				if ((i + 1) < pageRuleActions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PageRuleCondition[] pageRuleConditions = getPageRuleConditions();

		if (pageRuleConditions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageRuleConditions\": ");

			sb.append("[");

			for (int i = 0; i < pageRuleConditions.length; i++) {
				sb.append(String.valueOf(pageRuleConditions[i]));

				if ((i + 1) < pageRuleConditions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.PageRule",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("ConditionType")
	public static enum ConditionType {

		ALL("All"), ANY("Any");

		@JsonCreator
		public static ConditionType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ConditionType conditionType : values()) {
				if (Objects.equals(conditionType.getValue(), value)) {
					return conditionType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private ConditionType(String value) {
			_value = value;
		}

		private final String _value;

	}

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