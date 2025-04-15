/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.LayoutSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class Layout implements Cloneable, Serializable {

	public static Layout toDTO(String json) {
		return LayoutSerDes.toDTO(json);
	}

	public Align getAlign() {
		return align;
	}

	public String getAlignAsString() {
		if (align == null) {
			return null;
		}

		return align.toString();
	}

	public void setAlign(Align align) {
		this.align = align;
	}

	public void setAlign(UnsafeSupplier<Align, Exception> alignUnsafeSupplier) {
		try {
			align = alignUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Align align;

	public ContentDisplay getContentDisplay() {
		return contentDisplay;
	}

	public String getContentDisplayAsString() {
		if (contentDisplay == null) {
			return null;
		}

		return contentDisplay.toString();
	}

	public void setContentDisplay(ContentDisplay contentDisplay) {
		this.contentDisplay = contentDisplay;
	}

	public void setContentDisplay(
		UnsafeSupplier<ContentDisplay, Exception>
			contentDisplayUnsafeSupplier) {

		try {
			contentDisplay = contentDisplayUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ContentDisplay contentDisplay;

	public FlexWrap getFlexWrap() {
		return flexWrap;
	}

	public String getFlexWrapAsString() {
		if (flexWrap == null) {
			return null;
		}

		return flexWrap.toString();
	}

	public void setFlexWrap(FlexWrap flexWrap) {
		this.flexWrap = flexWrap;
	}

	public void setFlexWrap(
		UnsafeSupplier<FlexWrap, Exception> flexWrapUnsafeSupplier) {

		try {
			flexWrap = flexWrapUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FlexWrap flexWrap;

	public Justify getJustify() {
		return justify;
	}

	public String getJustifyAsString() {
		if (justify == null) {
			return null;
		}

		return justify.toString();
	}

	public void setJustify(Justify justify) {
		this.justify = justify;
	}

	public void setJustify(
		UnsafeSupplier<Justify, Exception> justifyUnsafeSupplier) {

		try {
			justify = justifyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Justify justify;

	public WidthType getWidthType() {
		return widthType;
	}

	public String getWidthTypeAsString() {
		if (widthType == null) {
			return null;
		}

		return widthType.toString();
	}

	public void setWidthType(WidthType widthType) {
		this.widthType = widthType;
	}

	public void setWidthType(
		UnsafeSupplier<WidthType, Exception> widthTypeUnsafeSupplier) {

		try {
			widthType = widthTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected WidthType widthType;

	@Override
	public Layout clone() throws CloneNotSupportedException {
		return (Layout)super.clone();
	}

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
		return LayoutSerDes.toJSON(this);
	}

	public static enum Align {

		BASELINE("Baseline"), CENTER("Center"), END("End"), NONE("None"),
		START("Start"), STRETCH("Stretch");

		public static Align create(String value) {
			for (Align align : values()) {
				if (Objects.equals(align.getValue(), value) ||
					Objects.equals(align.name(), value)) {

					return align;
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

		private Align(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum ContentDisplay {

		BLOCK("Block"), FLEX_COLUMN("FlexColumn"), FLEX_ROW("FlexRow");

		public static ContentDisplay create(String value) {
			for (ContentDisplay contentDisplay : values()) {
				if (Objects.equals(contentDisplay.getValue(), value) ||
					Objects.equals(contentDisplay.name(), value)) {

					return contentDisplay;
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

		private ContentDisplay(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum FlexWrap {

		NO_WRAP("NoWrap"), WRAP("Wrap"), WRAP_REVERSE("WrapReverse");

		public static FlexWrap create(String value) {
			for (FlexWrap flexWrap : values()) {
				if (Objects.equals(flexWrap.getValue(), value) ||
					Objects.equals(flexWrap.name(), value)) {

					return flexWrap;
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

		private FlexWrap(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum Justify {

		CENTER("Center"), END("End"), NONE("None"), SPACE_AROUND("SpaceAround"),
		SPACE_BETWEEN("SpaceBetween"), START("Start");

		public static Justify create(String value) {
			for (Justify justify : values()) {
				if (Objects.equals(justify.getValue(), value) ||
					Objects.equals(justify.name(), value)) {

					return justify;
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

		private Justify(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum WidthType {

		FIXED("Fixed"), FLUID("Fluid");

		public static WidthType create(String value) {
			for (WidthType widthType : values()) {
				if (Objects.equals(widthType.getValue(), value) ||
					Objects.equals(widthType.name(), value)) {

					return widthType;
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

		private WidthType(String value) {
			_value = value;
		}

		private final String _value;

	}

}