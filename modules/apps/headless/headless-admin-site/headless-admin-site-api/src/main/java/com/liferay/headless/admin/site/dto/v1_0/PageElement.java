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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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
import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(description = "A page element.", value = "PageElement")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageElement")
public class PageElement implements Serializable {

	public static PageElement toDTO(String json) {
		return ObjectMapperUtil.readValue(PageElement.class, json);
	}

	public static PageElement unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PageElement.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page element's definition."
	)
	@Valid
	public Object getDefinition() {
		if (_definitionSupplier != null) {
			definition = _definitionSupplier.get();

			_definitionSupplier = null;
		}

		return definition;
	}

	public void setDefinition(Object definition) {
		this.definition = definition;

		_definitionSupplier = null;
	}

	@JsonIgnore
	public void setDefinition(
		UnsafeSupplier<Object, Exception> definitionUnsafeSupplier) {

		_definitionSupplier = () -> {
			try {
				return definitionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page element's definition.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object definition;

	@JsonIgnore
	private Supplier<Object> _definitionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page element's external reference code. Unique within the site."
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

	@GraphQLField(
		description = "The page element's external reference code. Unique within the site."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the page elements this page element has."
	)
	@Valid
	public PageElement[] getPageElements() {
		if (_pageElementsSupplier != null) {
			pageElements = _pageElementsSupplier.get();

			_pageElementsSupplier = null;
		}

		return pageElements;
	}

	public void setPageElements(PageElement[] pageElements) {
		this.pageElements = pageElements;

		_pageElementsSupplier = null;
	}

	@JsonIgnore
	public void setPageElements(
		UnsafeSupplier<PageElement[], Exception> pageElementsUnsafeSupplier) {

		_pageElementsSupplier = () -> {
			try {
				return pageElementsUnsafeSupplier.get();
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
		description = "A list of the page elements this page element has."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PageElement[] pageElements;

	@JsonIgnore
	private Supplier<PageElement[]> _pageElementsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The parent's page element's external reference code. Unique within the site."
	)
	public String getParentExternalReferenceCode() {
		if (_parentExternalReferenceCodeSupplier != null) {
			parentExternalReferenceCode =
				_parentExternalReferenceCodeSupplier.get();

			_parentExternalReferenceCodeSupplier = null;
		}

		return parentExternalReferenceCode;
	}

	public void setParentExternalReferenceCode(
		String parentExternalReferenceCode) {

		this.parentExternalReferenceCode = parentExternalReferenceCode;

		_parentExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setParentExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentExternalReferenceCodeUnsafeSupplier) {

		_parentExternalReferenceCodeSupplier = () -> {
			try {
				return parentExternalReferenceCodeUnsafeSupplier.get();
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
		description = "The parent's page element's external reference code. Unique within the site."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String parentExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _parentExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The 0-based position this page element occupies with respect to its siblings (0 for first child, 1 for second child, ...). If not specified when creating a page element the page element will be added at the last valid position."
	)
	public Integer getPosition() {
		if (_positionSupplier != null) {
			position = _positionSupplier.get();

			_positionSupplier = null;
		}

		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;

		_positionSupplier = null;
	}

	@JsonIgnore
	public void setPosition(
		UnsafeSupplier<Integer, Exception> positionUnsafeSupplier) {

		_positionSupplier = () -> {
			try {
				return positionUnsafeSupplier.get();
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
		description = "The 0-based position this page element occupies with respect to its siblings (0 for first child, 1 for second child, ...). If not specified when creating a page element the page element will be added at the last valid position."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer position;

	@JsonIgnore
	private Supplier<Integer> _positionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page element's type (collection, collection item, column, container, drop zone, form, form step, form step container, fragment, fragment composition, fragment drop zone, row, widget or widget section)."
	)
	@JsonGetter("type")
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
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
		description = "The page element's type (collection, collection item, column, container, drop zone, form, form step, form step container, fragment, fragment composition, fragment drop zone, row, widget or widget section)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageElement)) {
			return false;
		}

		PageElement pageElement = (PageElement)object;

		return Objects.equals(toString(), pageElement.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object definition = getDefinition();

		if (definition != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"definition\": ");

			if (definition instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)definition));
			}
			else if (definition instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)definition));
				sb.append("\"");
			}
			else {
				sb.append(definition);
			}
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

		PageElement[] pageElements = getPageElements();

		if (pageElements != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageElements\": ");

			sb.append("[");

			for (int i = 0; i < pageElements.length; i++) {
				sb.append(String.valueOf(pageElements[i]));

				if ((i + 1) < pageElements.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String parentExternalReferenceCode = getParentExternalReferenceCode();

		if (parentExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(parentExternalReferenceCode));

			sb.append("\"");
		}

		Integer position = getPosition();

		if (position != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"position\": ");

			sb.append(position);
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(type);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.PageElement",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		COLLECTION("Collection"), COLLECTION_ITEM("CollectionItem"),
		COLUMN("Column"), CONTAINER("Container"), DROP_ZONE("DropZone"),
		FORM("Form"), FORM_STEP("FormStep"),
		FORM_STEP_CONTAINER("FormStepContainer"), FRAGMENT("Fragment"),
		FRAGMENT_COMPOSITION("FragmentComposition"),
		FRAGMENT_DROP_ZONE("FragmentDropZone"), ROW("Row"), WIDGET("Widget");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
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

		private Type(String value) {
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