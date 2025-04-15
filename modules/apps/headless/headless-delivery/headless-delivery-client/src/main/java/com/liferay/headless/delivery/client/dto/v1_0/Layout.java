/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.LayoutSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
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

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public void setBorderColor(
		UnsafeSupplier<String, Exception> borderColorUnsafeSupplier) {

		try {
			borderColor = borderColorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String borderColor;

	public BorderRadius getBorderRadius() {
		return borderRadius;
	}

	public String getBorderRadiusAsString() {
		if (borderRadius == null) {
			return null;
		}

		return borderRadius.toString();
	}

	public void setBorderRadius(BorderRadius borderRadius) {
		this.borderRadius = borderRadius;
	}

	public void setBorderRadius(
		UnsafeSupplier<BorderRadius, Exception> borderRadiusUnsafeSupplier) {

		try {
			borderRadius = borderRadiusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BorderRadius borderRadius;

	public Integer getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(Integer borderWidth) {
		this.borderWidth = borderWidth;
	}

	public void setBorderWidth(
		UnsafeSupplier<Integer, Exception> borderWidthUnsafeSupplier) {

		try {
			borderWidth = borderWidthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer borderWidth;

	public ContainerType getContainerType() {
		return containerType;
	}

	public String getContainerTypeAsString() {
		if (containerType == null) {
			return null;
		}

		return containerType.toString();
	}

	public void setContainerType(ContainerType containerType) {
		this.containerType = containerType;
	}

	public void setContainerType(
		UnsafeSupplier<ContainerType, Exception> containerTypeUnsafeSupplier) {

		try {
			containerType = containerTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ContainerType containerType;

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

	public Integer getMarginBottom() {
		return marginBottom;
	}

	public void setMarginBottom(Integer marginBottom) {
		this.marginBottom = marginBottom;
	}

	public void setMarginBottom(
		UnsafeSupplier<Integer, Exception> marginBottomUnsafeSupplier) {

		try {
			marginBottom = marginBottomUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer marginBottom;

	public Integer getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(Integer marginLeft) {
		this.marginLeft = marginLeft;
	}

	public void setMarginLeft(
		UnsafeSupplier<Integer, Exception> marginLeftUnsafeSupplier) {

		try {
			marginLeft = marginLeftUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer marginLeft;

	public Integer getMarginRight() {
		return marginRight;
	}

	public void setMarginRight(Integer marginRight) {
		this.marginRight = marginRight;
	}

	public void setMarginRight(
		UnsafeSupplier<Integer, Exception> marginRightUnsafeSupplier) {

		try {
			marginRight = marginRightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer marginRight;

	public Integer getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(Integer marginTop) {
		this.marginTop = marginTop;
	}

	public void setMarginTop(
		UnsafeSupplier<Integer, Exception> marginTopUnsafeSupplier) {

		try {
			marginTop = marginTopUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer marginTop;

	public Integer getOpacity() {
		return opacity;
	}

	public void setOpacity(Integer opacity) {
		this.opacity = opacity;
	}

	public void setOpacity(
		UnsafeSupplier<Integer, Exception> opacityUnsafeSupplier) {

		try {
			opacity = opacityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer opacity;

	public Integer getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(Integer paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	public void setPaddingBottom(
		UnsafeSupplier<Integer, Exception> paddingBottomUnsafeSupplier) {

		try {
			paddingBottom = paddingBottomUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer paddingBottom;

	public Integer getPaddingHorizontal() {
		return paddingHorizontal;
	}

	public void setPaddingHorizontal(Integer paddingHorizontal) {
		this.paddingHorizontal = paddingHorizontal;
	}

	public void setPaddingHorizontal(
		UnsafeSupplier<Integer, Exception> paddingHorizontalUnsafeSupplier) {

		try {
			paddingHorizontal = paddingHorizontalUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer paddingHorizontal;

	public Integer getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(Integer paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public void setPaddingLeft(
		UnsafeSupplier<Integer, Exception> paddingLeftUnsafeSupplier) {

		try {
			paddingLeft = paddingLeftUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer paddingLeft;

	public Integer getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(Integer paddingRight) {
		this.paddingRight = paddingRight;
	}

	public void setPaddingRight(
		UnsafeSupplier<Integer, Exception> paddingRightUnsafeSupplier) {

		try {
			paddingRight = paddingRightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer paddingRight;

	public Integer getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(Integer paddingTop) {
		this.paddingTop = paddingTop;
	}

	public void setPaddingTop(
		UnsafeSupplier<Integer, Exception> paddingTopUnsafeSupplier) {

		try {
			paddingTop = paddingTopUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer paddingTop;

	public Shadow getShadow() {
		return shadow;
	}

	public String getShadowAsString() {
		if (shadow == null) {
			return null;
		}

		return shadow.toString();
	}

	public void setShadow(Shadow shadow) {
		this.shadow = shadow;
	}

	public void setShadow(
		UnsafeSupplier<Shadow, Exception> shadowUnsafeSupplier) {

		try {
			shadow = shadowUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Shadow shadow;

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

	public static enum BorderRadius {

		CIRCLE("Circle"), LARGE("Large"), NONE("None"), PILL("Pill"),
		REGULAR("Regular");

		public static BorderRadius create(String value) {
			for (BorderRadius borderRadius : values()) {
				if (Objects.equals(borderRadius.getValue(), value) ||
					Objects.equals(borderRadius.name(), value)) {

					return borderRadius;
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

		private BorderRadius(String value) {
			_value = value;
		}

		private final String _value;

	}

	public static enum ContainerType {

		FIXED("Fixed"), FLUID("Fluid");

		public static ContainerType create(String value) {
			for (ContainerType containerType : values()) {
				if (Objects.equals(containerType.getValue(), value) ||
					Objects.equals(containerType.name(), value)) {

					return containerType;
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

		private ContainerType(String value) {
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

	public static enum Shadow {

		DEFAULT("Default"), LARGE("Large"), NONE("None"), REGULAR("Regular"),
		SMALL("Small");

		public static Shadow create(String value) {
			for (Shadow shadow : values()) {
				if (Objects.equals(shadow.getValue(), value) ||
					Objects.equals(shadow.name(), value)) {

					return shadow;
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

		private Shadow(String value) {
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