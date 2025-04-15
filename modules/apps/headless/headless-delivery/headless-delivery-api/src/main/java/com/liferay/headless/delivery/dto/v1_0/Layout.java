/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(description = "the page section's layout.", value = "Layout")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Layout")
public class Layout implements Serializable {

	public static Layout toDTO(String json) {
		return ObjectMapperUtil.readValue(Layout.class, json);
	}

	public static Layout unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Layout.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
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

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Align align;

	@JsonIgnore
	private Supplier<Align> _alignSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public String getBorderColor() {
		if (_borderColorSupplier != null) {
			borderColor = _borderColorSupplier.get();

			_borderColorSupplier = null;
		}

		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;

		_borderColorSupplier = null;
	}

	@JsonIgnore
	public void setBorderColor(
		UnsafeSupplier<String, Exception> borderColorUnsafeSupplier) {

		_borderColorSupplier = () -> {
			try {
				return borderColorUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String borderColor;

	@JsonIgnore
	private Supplier<String> _borderColorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	@JsonGetter("borderRadius")
	@Valid
	public BorderRadius getBorderRadius() {
		if (_borderRadiusSupplier != null) {
			borderRadius = _borderRadiusSupplier.get();

			_borderRadiusSupplier = null;
		}

		return borderRadius;
	}

	@JsonIgnore
	public String getBorderRadiusAsString() {
		BorderRadius borderRadius = getBorderRadius();

		if (borderRadius == null) {
			return null;
		}

		return borderRadius.toString();
	}

	public void setBorderRadius(BorderRadius borderRadius) {
		this.borderRadius = borderRadius;

		_borderRadiusSupplier = null;
	}

	@JsonIgnore
	public void setBorderRadius(
		UnsafeSupplier<BorderRadius, Exception> borderRadiusUnsafeSupplier) {

		_borderRadiusSupplier = () -> {
			try {
				return borderRadiusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected BorderRadius borderRadius;

	@JsonIgnore
	private Supplier<BorderRadius> _borderRadiusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public Integer getBorderWidth() {
		if (_borderWidthSupplier != null) {
			borderWidth = _borderWidthSupplier.get();

			_borderWidthSupplier = null;
		}

		return borderWidth;
	}

	public void setBorderWidth(Integer borderWidth) {
		this.borderWidth = borderWidth;

		_borderWidthSupplier = null;
	}

	@JsonIgnore
	public void setBorderWidth(
		UnsafeSupplier<Integer, Exception> borderWidthUnsafeSupplier) {

		_borderWidthSupplier = () -> {
			try {
				return borderWidthUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer borderWidth;

	@JsonIgnore
	private Supplier<Integer> _borderWidthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The container's type (fixed or fluid)."
	)
	@JsonGetter("containerType")
	@Valid
	public ContainerType getContainerType() {
		if (_containerTypeSupplier != null) {
			containerType = _containerTypeSupplier.get();

			_containerTypeSupplier = null;
		}

		return containerType;
	}

	@JsonIgnore
	public String getContainerTypeAsString() {
		ContainerType containerType = getContainerType();

		if (containerType == null) {
			return null;
		}

		return containerType.toString();
	}

	public void setContainerType(ContainerType containerType) {
		this.containerType = containerType;

		_containerTypeSupplier = null;
	}

	@JsonIgnore
	public void setContainerType(
		UnsafeSupplier<ContainerType, Exception> containerTypeUnsafeSupplier) {

		_containerTypeSupplier = () -> {
			try {
				return containerTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The container's type (fixed or fluid).")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ContainerType containerType;

	@JsonIgnore
	private Supplier<ContainerType> _containerTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
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

	@Deprecated
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

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
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

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Justify justify;

	@JsonIgnore
	private Supplier<Justify> _justifySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public Integer getMarginBottom() {
		if (_marginBottomSupplier != null) {
			marginBottom = _marginBottomSupplier.get();

			_marginBottomSupplier = null;
		}

		return marginBottom;
	}

	public void setMarginBottom(Integer marginBottom) {
		this.marginBottom = marginBottom;

		_marginBottomSupplier = null;
	}

	@JsonIgnore
	public void setMarginBottom(
		UnsafeSupplier<Integer, Exception> marginBottomUnsafeSupplier) {

		_marginBottomSupplier = () -> {
			try {
				return marginBottomUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer marginBottom;

	@JsonIgnore
	private Supplier<Integer> _marginBottomSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public Integer getMarginLeft() {
		if (_marginLeftSupplier != null) {
			marginLeft = _marginLeftSupplier.get();

			_marginLeftSupplier = null;
		}

		return marginLeft;
	}

	public void setMarginLeft(Integer marginLeft) {
		this.marginLeft = marginLeft;

		_marginLeftSupplier = null;
	}

	@JsonIgnore
	public void setMarginLeft(
		UnsafeSupplier<Integer, Exception> marginLeftUnsafeSupplier) {

		_marginLeftSupplier = () -> {
			try {
				return marginLeftUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer marginLeft;

	@JsonIgnore
	private Supplier<Integer> _marginLeftSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public Integer getMarginRight() {
		if (_marginRightSupplier != null) {
			marginRight = _marginRightSupplier.get();

			_marginRightSupplier = null;
		}

		return marginRight;
	}

	public void setMarginRight(Integer marginRight) {
		this.marginRight = marginRight;

		_marginRightSupplier = null;
	}

	@JsonIgnore
	public void setMarginRight(
		UnsafeSupplier<Integer, Exception> marginRightUnsafeSupplier) {

		_marginRightSupplier = () -> {
			try {
				return marginRightUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer marginRight;

	@JsonIgnore
	private Supplier<Integer> _marginRightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public Integer getMarginTop() {
		if (_marginTopSupplier != null) {
			marginTop = _marginTopSupplier.get();

			_marginTopSupplier = null;
		}

		return marginTop;
	}

	public void setMarginTop(Integer marginTop) {
		this.marginTop = marginTop;

		_marginTopSupplier = null;
	}

	@JsonIgnore
	public void setMarginTop(
		UnsafeSupplier<Integer, Exception> marginTopUnsafeSupplier) {

		_marginTopSupplier = () -> {
			try {
				return marginTopUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer marginTop;

	@JsonIgnore
	private Supplier<Integer> _marginTopSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public Integer getOpacity() {
		if (_opacitySupplier != null) {
			opacity = _opacitySupplier.get();

			_opacitySupplier = null;
		}

		return opacity;
	}

	public void setOpacity(Integer opacity) {
		this.opacity = opacity;

		_opacitySupplier = null;
	}

	@JsonIgnore
	public void setOpacity(
		UnsafeSupplier<Integer, Exception> opacityUnsafeSupplier) {

		_opacitySupplier = () -> {
			try {
				return opacityUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer opacity;

	@JsonIgnore
	private Supplier<Integer> _opacitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public Integer getPaddingBottom() {
		if (_paddingBottomSupplier != null) {
			paddingBottom = _paddingBottomSupplier.get();

			_paddingBottomSupplier = null;
		}

		return paddingBottom;
	}

	public void setPaddingBottom(Integer paddingBottom) {
		this.paddingBottom = paddingBottom;

		_paddingBottomSupplier = null;
	}

	@JsonIgnore
	public void setPaddingBottom(
		UnsafeSupplier<Integer, Exception> paddingBottomUnsafeSupplier) {

		_paddingBottomSupplier = () -> {
			try {
				return paddingBottomUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer paddingBottom;

	@JsonIgnore
	private Supplier<Integer> _paddingBottomSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public Integer getPaddingHorizontal() {
		if (_paddingHorizontalSupplier != null) {
			paddingHorizontal = _paddingHorizontalSupplier.get();

			_paddingHorizontalSupplier = null;
		}

		return paddingHorizontal;
	}

	public void setPaddingHorizontal(Integer paddingHorizontal) {
		this.paddingHorizontal = paddingHorizontal;

		_paddingHorizontalSupplier = null;
	}

	@JsonIgnore
	public void setPaddingHorizontal(
		UnsafeSupplier<Integer, Exception> paddingHorizontalUnsafeSupplier) {

		_paddingHorizontalSupplier = () -> {
			try {
				return paddingHorizontalUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer paddingHorizontal;

	@JsonIgnore
	private Supplier<Integer> _paddingHorizontalSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public Integer getPaddingLeft() {
		if (_paddingLeftSupplier != null) {
			paddingLeft = _paddingLeftSupplier.get();

			_paddingLeftSupplier = null;
		}

		return paddingLeft;
	}

	public void setPaddingLeft(Integer paddingLeft) {
		this.paddingLeft = paddingLeft;

		_paddingLeftSupplier = null;
	}

	@JsonIgnore
	public void setPaddingLeft(
		UnsafeSupplier<Integer, Exception> paddingLeftUnsafeSupplier) {

		_paddingLeftSupplier = () -> {
			try {
				return paddingLeftUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer paddingLeft;

	@JsonIgnore
	private Supplier<Integer> _paddingLeftSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public Integer getPaddingRight() {
		if (_paddingRightSupplier != null) {
			paddingRight = _paddingRightSupplier.get();

			_paddingRightSupplier = null;
		}

		return paddingRight;
	}

	public void setPaddingRight(Integer paddingRight) {
		this.paddingRight = paddingRight;

		_paddingRightSupplier = null;
	}

	@JsonIgnore
	public void setPaddingRight(
		UnsafeSupplier<Integer, Exception> paddingRightUnsafeSupplier) {

		_paddingRightSupplier = () -> {
			try {
				return paddingRightUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer paddingRight;

	@JsonIgnore
	private Supplier<Integer> _paddingRightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	public Integer getPaddingTop() {
		if (_paddingTopSupplier != null) {
			paddingTop = _paddingTopSupplier.get();

			_paddingTopSupplier = null;
		}

		return paddingTop;
	}

	public void setPaddingTop(Integer paddingTop) {
		this.paddingTop = paddingTop;

		_paddingTopSupplier = null;
	}

	@JsonIgnore
	public void setPaddingTop(
		UnsafeSupplier<Integer, Exception> paddingTopUnsafeSupplier) {

		_paddingTopSupplier = () -> {
			try {
				return paddingTopUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer paddingTop;

	@JsonIgnore
	private Supplier<Integer> _paddingTopSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	@JsonGetter("shadow")
	@Valid
	public Shadow getShadow() {
		if (_shadowSupplier != null) {
			shadow = _shadowSupplier.get();

			_shadowSupplier = null;
		}

		return shadow;
	}

	@JsonIgnore
	public String getShadowAsString() {
		Shadow shadow = getShadow();

		if (shadow == null) {
			return null;
		}

		return shadow.toString();
	}

	public void setShadow(Shadow shadow) {
		this.shadow = shadow;

		_shadowSupplier = null;
	}

	@JsonIgnore
	public void setShadow(
		UnsafeSupplier<Shadow, Exception> shadowUnsafeSupplier) {

		_shadowSupplier = () -> {
			try {
				return shadowUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Shadow shadow;

	@JsonIgnore
	private Supplier<Shadow> _shadowSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The width's type (fixed or fluid)."
	)
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

	@GraphQLField(description = "The width's type (fixed or fluid).")
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

		String borderColor = getBorderColor();

		if (borderColor != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderColor\": ");

			sb.append("\"");

			sb.append(_escape(borderColor));

			sb.append("\"");
		}

		BorderRadius borderRadius = getBorderRadius();

		if (borderRadius != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderRadius\": ");

			sb.append("\"");

			sb.append(borderRadius);

			sb.append("\"");
		}

		Integer borderWidth = getBorderWidth();

		if (borderWidth != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderWidth\": ");

			sb.append(borderWidth);
		}

		ContainerType containerType = getContainerType();

		if (containerType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"containerType\": ");

			sb.append("\"");

			sb.append(containerType);

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

		Integer marginBottom = getMarginBottom();

		if (marginBottom != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginBottom\": ");

			sb.append(marginBottom);
		}

		Integer marginLeft = getMarginLeft();

		if (marginLeft != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginLeft\": ");

			sb.append(marginLeft);
		}

		Integer marginRight = getMarginRight();

		if (marginRight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginRight\": ");

			sb.append(marginRight);
		}

		Integer marginTop = getMarginTop();

		if (marginTop != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginTop\": ");

			sb.append(marginTop);
		}

		Integer opacity = getOpacity();

		if (opacity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"opacity\": ");

			sb.append(opacity);
		}

		Integer paddingBottom = getPaddingBottom();

		if (paddingBottom != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingBottom\": ");

			sb.append(paddingBottom);
		}

		Integer paddingHorizontal = getPaddingHorizontal();

		if (paddingHorizontal != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingHorizontal\": ");

			sb.append(paddingHorizontal);
		}

		Integer paddingLeft = getPaddingLeft();

		if (paddingLeft != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingLeft\": ");

			sb.append(paddingLeft);
		}

		Integer paddingRight = getPaddingRight();

		if (paddingRight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingRight\": ");

			sb.append(paddingRight);
		}

		Integer paddingTop = getPaddingTop();

		if (paddingTop != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingTop\": ");

			sb.append(paddingTop);
		}

		Shadow shadow = getShadow();

		if (shadow != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shadow\": ");

			sb.append("\"");

			sb.append(shadow);

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
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.Layout",
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

	@GraphQLName("BorderRadius")
	public static enum BorderRadius {

		CIRCLE("Circle"), LARGE("Large"), NONE("None"), PILL("Pill"),
		REGULAR("Regular");

		@JsonCreator
		public static BorderRadius create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (BorderRadius borderRadius : values()) {
				if (Objects.equals(borderRadius.getValue(), value)) {
					return borderRadius;
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

		private BorderRadius(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("ContainerType")
	public static enum ContainerType {

		FIXED("Fixed"), FLUID("Fluid");

		@JsonCreator
		public static ContainerType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ContainerType containerType : values()) {
				if (Objects.equals(containerType.getValue(), value)) {
					return containerType;
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

		private ContainerType(String value) {
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

	@GraphQLName("Shadow")
	public static enum Shadow {

		DEFAULT("Default"), LARGE("Large"), NONE("None"), REGULAR("Regular"),
		SMALL("Small");

		@JsonCreator
		public static Shadow create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Shadow shadow : values()) {
				if (Objects.equals(shadow.getValue(), value)) {
					return shadow;
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

		private Shadow(String value) {
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