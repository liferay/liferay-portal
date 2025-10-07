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
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "the container page element's layout.", value = "Layout"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Layout")
public class Layout implements Serializable {

	public static Layout toDTO(String json) {
		return ObjectMapperUtil.readValue(Layout.class, json);
	}

	public static Layout unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Layout.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("align")
	@Valid
	public Align getAlign() {
		if (_alignSupplier != null) {
			align = _alignSupplier.get();

			_alignSupplier = null;
		}

		return align;
	}

	@JsonIgnore
	public String getAlignAsString() {
		Align align = getAlign();

		if (align == null) {
			return null;
		}

		return align.toString();
	}

	public void setAlign(Align align) {
		this.align = align;

		_alignSupplier = null;
	}

	@JsonIgnore
	public void setAlign(UnsafeSupplier<Align, Exception> alignUnsafeSupplier) {
		_alignSupplier = () -> {
			try {
				return alignUnsafeSupplier.get();
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
	protected Align align;

	@JsonIgnore
	private Supplier<Align> _alignSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("contentDisplay")
	@Valid
	public ContentDisplay getContentDisplay() {
		if (_contentDisplaySupplier != null) {
			contentDisplay = _contentDisplaySupplier.get();

			_contentDisplaySupplier = null;
		}

		return contentDisplay;
	}

	@JsonIgnore
	public String getContentDisplayAsString() {
		ContentDisplay contentDisplay = getContentDisplay();

		if (contentDisplay == null) {
			return null;
		}

		return contentDisplay.toString();
	}

	public void setContentDisplay(ContentDisplay contentDisplay) {
		this.contentDisplay = contentDisplay;

		_contentDisplaySupplier = null;
	}

	@JsonIgnore
	public void setContentDisplay(
		UnsafeSupplier<ContentDisplay, Exception>
			contentDisplayUnsafeSupplier) {

		_contentDisplaySupplier = () -> {
			try {
				return contentDisplayUnsafeSupplier.get();
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
	protected ContentDisplay contentDisplay;

	@JsonIgnore
	private Supplier<ContentDisplay> _contentDisplaySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("flexWrap")
	@Valid
	public FlexWrap getFlexWrap() {
		if (_flexWrapSupplier != null) {
			flexWrap = _flexWrapSupplier.get();

			_flexWrapSupplier = null;
		}

		return flexWrap;
	}

	@JsonIgnore
	public String getFlexWrapAsString() {
		FlexWrap flexWrap = getFlexWrap();

		if (flexWrap == null) {
			return null;
		}

		return flexWrap.toString();
	}

	public void setFlexWrap(FlexWrap flexWrap) {
		this.flexWrap = flexWrap;

		_flexWrapSupplier = null;
	}

	@JsonIgnore
	public void setFlexWrap(
		UnsafeSupplier<FlexWrap, Exception> flexWrapUnsafeSupplier) {

		_flexWrapSupplier = () -> {
			try {
				return flexWrapUnsafeSupplier.get();
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
	protected FlexWrap flexWrap;

	@JsonIgnore
	private Supplier<FlexWrap> _flexWrapSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("justify")
	@Valid
	public Justify getJustify() {
		if (_justifySupplier != null) {
			justify = _justifySupplier.get();

			_justifySupplier = null;
		}

		return justify;
	}

	@JsonIgnore
	public String getJustifyAsString() {
		Justify justify = getJustify();

		if (justify == null) {
			return null;
		}

		return justify.toString();
	}

	public void setJustify(Justify justify) {
		this.justify = justify;

		_justifySupplier = null;
	}

	@JsonIgnore
	public void setJustify(
		UnsafeSupplier<Justify, Exception> justifyUnsafeSupplier) {

		_justifySupplier = () -> {
			try {
				return justifyUnsafeSupplier.get();
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
	protected Justify justify;

	@JsonIgnore
	private Supplier<Justify> _justifySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("widthType")
	@Valid
	public WidthType getWidthType() {
		if (_widthTypeSupplier != null) {
			widthType = _widthTypeSupplier.get();

			_widthTypeSupplier = null;
		}

		return widthType;
	}

	@JsonIgnore
	public String getWidthTypeAsString() {
		WidthType widthType = getWidthType();

		if (widthType == null) {
			return null;
		}

		return widthType.toString();
	}

	public void setWidthType(WidthType widthType) {
		this.widthType = widthType;

		_widthTypeSupplier = null;
	}

	@JsonIgnore
	public void setWidthType(
		UnsafeSupplier<WidthType, Exception> widthTypeUnsafeSupplier) {

		_widthTypeSupplier = () -> {
			try {
				return widthTypeUnsafeSupplier.get();
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
	protected WidthType widthType;

	@JsonIgnore
	private Supplier<WidthType> _widthTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Layout)) {
			return false;
		}

		Layout layout = (Layout)object;

		return Objects.equals(toString(), layout.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Align align = getAlign();

		if (align != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"align\": ");

			sb.append("\"");

			sb.append(align);

			sb.append("\"");
		}

		ContentDisplay contentDisplay = getContentDisplay();

		if (contentDisplay != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentDisplay\": ");

			sb.append("\"");

			sb.append(contentDisplay);

			sb.append("\"");
		}

		FlexWrap flexWrap = getFlexWrap();

		if (flexWrap != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"flexWrap\": ");

			sb.append("\"");

			sb.append(flexWrap);

			sb.append("\"");
		}

		Justify justify = getJustify();

		if (justify != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"justify\": ");

			sb.append("\"");

			sb.append(justify);

			sb.append("\"");
		}

		WidthType widthType = getWidthType();

		if (widthType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widthType\": ");

			sb.append("\"");

			sb.append(widthType);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.Layout",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Align")
	public static enum Align {

		BASELINE("Baseline"), CENTER("Center"), END("End"), NONE("None"),
		START("Start"), STRETCH("Stretch");

		@JsonCreator
		public static Align create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Align align : values()) {
				if (Objects.equals(align.getValue(), value)) {
					return align;
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

		private Align(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("ContentDisplay")
	public static enum ContentDisplay {

		BLOCK("Block"), FLEX_COLUMN("FlexColumn"), FLEX_ROW("FlexRow");

		@JsonCreator
		public static ContentDisplay create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ContentDisplay contentDisplay : values()) {
				if (Objects.equals(contentDisplay.getValue(), value)) {
					return contentDisplay;
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

		private ContentDisplay(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("FlexWrap")
	public static enum FlexWrap {

		NO_WRAP("NoWrap"), WRAP("Wrap"), WRAP_REVERSE("WrapReverse");

		@JsonCreator
		public static FlexWrap create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (FlexWrap flexWrap : values()) {
				if (Objects.equals(flexWrap.getValue(), value)) {
					return flexWrap;
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

		private FlexWrap(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Justify")
	public static enum Justify {

		CENTER("Center"), END("End"), NONE("None"), SPACE_AROUND("SpaceAround"),
		SPACE_BETWEEN("SpaceBetween"), START("Start");

		@JsonCreator
		public static Justify create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Justify justify : values()) {
				if (Objects.equals(justify.getValue(), value)) {
					return justify;
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

		private Justify(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("WidthType")
	public static enum WidthType {

		FIXED("Fixed"), FLUID("Fluid");

		@JsonCreator
		public static WidthType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (WidthType widthType : values()) {
				if (Objects.equals(widthType.getValue(), value)) {
					return widthType;
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

		private WidthType(String value) {
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