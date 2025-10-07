/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
@GraphQLName("TextStylesConfig")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TextStylesConfig")
public class TextStylesConfig implements Serializable {

	public static TextStylesConfig toDTO(String json) {
		return ObjectMapperUtil.readValue(TextStylesConfig.class, json);
	}

	public static TextStylesConfig unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(TextStylesConfig.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("alignment")
	@Valid
	public Alignment getAlignment() {
		if (_alignmentSupplier != null) {
			alignment = _alignmentSupplier.get();

			_alignmentSupplier = null;
		}

		return alignment;
	}

	@JsonIgnore
	public String getAlignmentAsString() {
		Alignment alignment = getAlignment();

		if (alignment == null) {
			return null;
		}

		return alignment.toString();
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;

		_alignmentSupplier = null;
	}

	@JsonIgnore
	public void setAlignment(
		UnsafeSupplier<Alignment, Exception> alignmentUnsafeSupplier) {

		_alignmentSupplier = () -> {
			try {
				return alignmentUnsafeSupplier.get();
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
	protected Alignment alignment;

	@JsonIgnore
	private Supplier<Alignment> _alignmentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getBold() {
		if (_boldSupplier != null) {
			bold = _boldSupplier.get();

			_boldSupplier = null;
		}

		return bold;
	}

	public void setBold(Boolean bold) {
		this.bold = bold;

		_boldSupplier = null;
	}

	@JsonIgnore
	public void setBold(UnsafeSupplier<Boolean, Exception> boldUnsafeSupplier) {
		_boldSupplier = () -> {
			try {
				return boldUnsafeSupplier.get();
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
	protected Boolean bold;

	@JsonIgnore
	private Supplier<Boolean> _boldSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "#FF0D0D")
	public String getColor() {
		if (_colorSupplier != null) {
			color = _colorSupplier.get();

			_colorSupplier = null;
		}

		return color;
	}

	public void setColor(String color) {
		this.color = color;

		_colorSupplier = null;
	}

	@JsonIgnore
	public void setColor(
		UnsafeSupplier<String, Exception> colorUnsafeSupplier) {

		_colorSupplier = () -> {
			try {
				return colorUnsafeSupplier.get();
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
	protected String color;

	@JsonIgnore
	private Supplier<String> _colorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("font")
	@Valid
	public Font getFont() {
		if (_fontSupplier != null) {
			font = _fontSupplier.get();

			_fontSupplier = null;
		}

		return font;
	}

	@JsonIgnore
	public String getFontAsString() {
		Font font = getFont();

		if (font == null) {
			return null;
		}

		return font.toString();
	}

	public void setFont(Font font) {
		this.font = font;

		_fontSupplier = null;
	}

	@JsonIgnore
	public void setFont(UnsafeSupplier<Font, Exception> fontUnsafeSupplier) {
		_fontSupplier = () -> {
			try {
				return fontUnsafeSupplier.get();
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
	protected Font font;

	@JsonIgnore
	private Supplier<Font> _fontSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getItalic() {
		if (_italicSupplier != null) {
			italic = _italicSupplier.get();

			_italicSupplier = null;
		}

		return italic;
	}

	public void setItalic(Boolean italic) {
		this.italic = italic;

		_italicSupplier = null;
	}

	@JsonIgnore
	public void setItalic(
		UnsafeSupplier<Boolean, Exception> italicUnsafeSupplier) {

		_italicSupplier = () -> {
			try {
				return italicUnsafeSupplier.get();
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
	protected Boolean italic;

	@JsonIgnore
	private Supplier<Boolean> _italicSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLetterSpacing() {
		if (_letterSpacingSupplier != null) {
			letterSpacing = _letterSpacingSupplier.get();

			_letterSpacingSupplier = null;
		}

		return letterSpacing;
	}

	public void setLetterSpacing(String letterSpacing) {
		this.letterSpacing = letterSpacing;

		_letterSpacingSupplier = null;
	}

	@JsonIgnore
	public void setLetterSpacing(
		UnsafeSupplier<String, Exception> letterSpacingUnsafeSupplier) {

		_letterSpacingSupplier = () -> {
			try {
				return letterSpacingUnsafeSupplier.get();
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
	protected String letterSpacing;

	@JsonIgnore
	private Supplier<String> _letterSpacingSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLineHeight() {
		if (_lineHeightSupplier != null) {
			lineHeight = _lineHeightSupplier.get();

			_lineHeightSupplier = null;
		}

		return lineHeight;
	}

	public void setLineHeight(String lineHeight) {
		this.lineHeight = lineHeight;

		_lineHeightSupplier = null;
	}

	@JsonIgnore
	public void setLineHeight(
		UnsafeSupplier<String, Exception> lineHeightUnsafeSupplier) {

		_lineHeightSupplier = () -> {
			try {
				return lineHeightUnsafeSupplier.get();
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
	protected String lineHeight;

	@JsonIgnore
	private Supplier<String> _lineHeightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSize() {
		if (_sizeSupplier != null) {
			size = _sizeSupplier.get();

			_sizeSupplier = null;
		}

		return size;
	}

	public void setSize(String size) {
		this.size = size;

		_sizeSupplier = null;
	}

	@JsonIgnore
	public void setSize(UnsafeSupplier<String, Exception> sizeUnsafeSupplier) {
		_sizeSupplier = () -> {
			try {
				return sizeUnsafeSupplier.get();
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
	protected String size;

	@JsonIgnore
	private Supplier<String> _sizeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("textDecoration")
	@Valid
	public TextDecoration getTextDecoration() {
		if (_textDecorationSupplier != null) {
			textDecoration = _textDecorationSupplier.get();

			_textDecorationSupplier = null;
		}

		return textDecoration;
	}

	@JsonIgnore
	public String getTextDecorationAsString() {
		TextDecoration textDecoration = getTextDecoration();

		if (textDecoration == null) {
			return null;
		}

		return textDecoration.toString();
	}

	public void setTextDecoration(TextDecoration textDecoration) {
		this.textDecoration = textDecoration;

		_textDecorationSupplier = null;
	}

	@JsonIgnore
	public void setTextDecoration(
		UnsafeSupplier<TextDecoration, Exception>
			textDecorationUnsafeSupplier) {

		_textDecorationSupplier = () -> {
			try {
				return textDecorationUnsafeSupplier.get();
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
	protected TextDecoration textDecoration;

	@JsonIgnore
	private Supplier<TextDecoration> _textDecorationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getWordSpacing() {
		if (_wordSpacingSupplier != null) {
			wordSpacing = _wordSpacingSupplier.get();

			_wordSpacingSupplier = null;
		}

		return wordSpacing;
	}

	public void setWordSpacing(String wordSpacing) {
		this.wordSpacing = wordSpacing;

		_wordSpacingSupplier = null;
	}

	@JsonIgnore
	public void setWordSpacing(
		UnsafeSupplier<String, Exception> wordSpacingUnsafeSupplier) {

		_wordSpacingSupplier = () -> {
			try {
				return wordSpacingUnsafeSupplier.get();
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
	protected String wordSpacing;

	@JsonIgnore
	private Supplier<String> _wordSpacingSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TextStylesConfig)) {
			return false;
		}

		TextStylesConfig textStylesConfig = (TextStylesConfig)object;

		return Objects.equals(toString(), textStylesConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Alignment alignment = getAlignment();

		if (alignment != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"alignment\": ");

			sb.append("\"");

			sb.append(alignment);

			sb.append("\"");
		}

		Boolean bold = getBold();

		if (bold != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bold\": ");

			sb.append(bold);
		}

		String color = getColor();

		if (color != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"color\": ");

			sb.append("\"");

			sb.append(_escape(color));

			sb.append("\"");
		}

		Font font = getFont();

		if (font != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"font\": ");

			sb.append("\"");

			sb.append(font);

			sb.append("\"");
		}

		Boolean italic = getItalic();

		if (italic != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"italic\": ");

			sb.append(italic);
		}

		String letterSpacing = getLetterSpacing();

		if (letterSpacing != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"letterSpacing\": ");

			sb.append("\"");

			sb.append(_escape(letterSpacing));

			sb.append("\"");
		}

		String lineHeight = getLineHeight();

		if (lineHeight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lineHeight\": ");

			sb.append("\"");

			sb.append(_escape(lineHeight));

			sb.append("\"");
		}

		String size = getSize();

		if (size != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append("\"");

			sb.append(_escape(size));

			sb.append("\"");
		}

		TextDecoration textDecoration = getTextDecoration();

		if (textDecoration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"textDecoration\": ");

			sb.append("\"");

			sb.append(textDecoration);

			sb.append("\"");
		}

		String wordSpacing = getWordSpacing();

		if (wordSpacing != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"wordSpacing\": ");

			sb.append("\"");

			sb.append(_escape(wordSpacing));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.TextStylesConfig",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Alignment")
	public static enum Alignment {

		CENTER("Center"), JUSTIFY("Justify"), LEFT("Left"), RIGHT("Right");

		@JsonCreator
		public static Alignment create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Alignment alignment : values()) {
				if (Objects.equals(alignment.getValue(), value)) {
					return alignment;
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

		private Alignment(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Font")
	public static enum Font {

		ARIAL("Arial"), GEORGIA("Georgia"), TAHOMA("Tahoma"),
		TIMES_NEW_ROMAN("TimesNewRoman"), TREBUCHET_MS("TrebuchetMS"),
		VERDANA("Verdana");

		@JsonCreator
		public static Font create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Font font : values()) {
				if (Objects.equals(font.getValue(), value)) {
					return font;
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

		private Font(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("TextDecoration")
	public static enum TextDecoration {

		NONE("None"), OVERLINE("Overline"), STRIKETHROUGH("Strikethrough"),
		UNDERLINE("Underline");

		@JsonCreator
		public static TextDecoration create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (TextDecoration textDecoration : values()) {
				if (Objects.equals(textDecoration.getValue(), value)) {
					return textDecoration;
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

		private TextDecoration(String value) {
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