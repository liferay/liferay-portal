/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.TextStylesConfigSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class TextStylesConfig implements Cloneable, Serializable {

	public static TextStylesConfig toDTO(String json) {
		return TextStylesConfigSerDes.toDTO(json);
	}

	public Alignment getAlignment() {
		return alignment;
	}

	public String getAlignmentAsString() {
		if (alignment == null) {
			return null;
		}

		return alignment.toString();
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	public void setAlignment(
		UnsafeSupplier<Alignment, Exception> alignmentUnsafeSupplier) {

		try {
			alignment = alignmentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Alignment alignment;

	public Boolean getBold() {
		return bold;
	}

	public void setBold(Boolean bold) {
		this.bold = bold;
	}

	public void setBold(UnsafeSupplier<Boolean, Exception> boldUnsafeSupplier) {
		try {
			bold = boldUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean bold;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setColor(
		UnsafeSupplier<String, Exception> colorUnsafeSupplier) {

		try {
			color = colorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String color;

	public Font getFont() {
		return font;
	}

	public String getFontAsString() {
		if (font == null) {
			return null;
		}

		return font.toString();
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void setFont(UnsafeSupplier<Font, Exception> fontUnsafeSupplier) {
		try {
			font = fontUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Font font;

	public Boolean getItalic() {
		return italic;
	}

	public void setItalic(Boolean italic) {
		this.italic = italic;
	}

	public void setItalic(
		UnsafeSupplier<Boolean, Exception> italicUnsafeSupplier) {

		try {
			italic = italicUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean italic;

	public String getLetterSpacing() {
		return letterSpacing;
	}

	public void setLetterSpacing(String letterSpacing) {
		this.letterSpacing = letterSpacing;
	}

	public void setLetterSpacing(
		UnsafeSupplier<String, Exception> letterSpacingUnsafeSupplier) {

		try {
			letterSpacing = letterSpacingUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String letterSpacing;

	public String getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(String lineHeight) {
		this.lineHeight = lineHeight;
	}

	public void setLineHeight(
		UnsafeSupplier<String, Exception> lineHeightUnsafeSupplier) {

		try {
			lineHeight = lineHeightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String lineHeight;

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setSize(UnsafeSupplier<String, Exception> sizeUnsafeSupplier) {
		try {
			size = sizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String size;

	public TextDecoration getTextDecoration() {
		return textDecoration;
	}

	public String getTextDecorationAsString() {
		if (textDecoration == null) {
			return null;
		}

		return textDecoration.toString();
	}

	public void setTextDecoration(TextDecoration textDecoration) {
		this.textDecoration = textDecoration;
	}

	public void setTextDecoration(
		UnsafeSupplier<TextDecoration, Exception>
			textDecorationUnsafeSupplier) {

		try {
			textDecoration = textDecorationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TextDecoration textDecoration;

	public String getWordSpacing() {
		return wordSpacing;
	}

	public void setWordSpacing(String wordSpacing) {
		this.wordSpacing = wordSpacing;
	}

	public void setWordSpacing(
		UnsafeSupplier<String, Exception> wordSpacingUnsafeSupplier) {

		try {
			wordSpacing = wordSpacingUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String wordSpacing;

	@Override
	public TextStylesConfig clone() throws CloneNotSupportedException {
		return (TextStylesConfig)super.clone();
	}

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
		return TextStylesConfigSerDes.toJSON(this);
	}

	public static enum Alignment {

		CENTER("Center"), JUSTIFY("Justify"), LEFT("Left"), RIGHT("Right");

		public static Alignment create(String value) {
			for (Alignment alignment : values()) {
				if (Objects.equals(alignment.getValue(), value) ||
					Objects.equals(alignment.name(), value)) {

					return alignment;
				}
			}

			return null;
		}

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

	public static enum Font {

		ARIAL("Arial"), GEORGIA("Georgia"), TAHOMA("Tahoma"),
		TIMES_NEW_ROMAN("TimesNewRoman"), TREBUCHET_MS("TrebuchetMS"),
		VERDANA("Verdana");

		public static Font create(String value) {
			for (Font font : values()) {
				if (Objects.equals(font.getValue(), value) ||
					Objects.equals(font.name(), value)) {

					return font;
				}
			}

			return null;
		}

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

	public static enum TextDecoration {

		NONE("None"), OVERLINE("Overline"), STRIKETHROUGH("Strikethrough"),
		UNDERLINE("Underline");

		public static TextDecoration create(String value) {
			for (TextDecoration textDecoration : values()) {
				if (Objects.equals(textDecoration.getValue(), value) ||
					Objects.equals(textDecoration.name(), value)) {

					return textDecoration;
				}
			}

			return null;
		}

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

}