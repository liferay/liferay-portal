/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@GraphQLName(description = "A fragment style.", value = "FragmentStyle")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FragmentStyle")
public class FragmentStyle implements Serializable {

	public static FragmentStyle toDTO(String json) {
		return ObjectMapperUtil.readValue(FragmentStyle.class, json);
	}

	public static FragmentStyle unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FragmentStyle.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's background color."
	)
	public String getBackgroundColor() {
		if (_backgroundColorSupplier != null) {
			backgroundColor = _backgroundColorSupplier.get();

			_backgroundColorSupplier = null;
		}

		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;

		_backgroundColorSupplier = null;
	}

	@JsonIgnore
	public void setBackgroundColor(
		UnsafeSupplier<String, Exception> backgroundColorUnsafeSupplier) {

		_backgroundColorSupplier = () -> {
			try {
				return backgroundColorUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's background color.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String backgroundColor;

	@JsonIgnore
	private Supplier<String> _backgroundColorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's background image."
	)
	@Valid
	public FragmentImage getBackgroundFragmentImage() {
		if (_backgroundFragmentImageSupplier != null) {
			backgroundFragmentImage = _backgroundFragmentImageSupplier.get();

			_backgroundFragmentImageSupplier = null;
		}

		return backgroundFragmentImage;
	}

	public void setBackgroundFragmentImage(
		FragmentImage backgroundFragmentImage) {

		this.backgroundFragmentImage = backgroundFragmentImage;

		_backgroundFragmentImageSupplier = null;
	}

	@JsonIgnore
	public void setBackgroundFragmentImage(
		UnsafeSupplier<FragmentImage, Exception>
			backgroundFragmentImageUnsafeSupplier) {

		_backgroundFragmentImageSupplier = () -> {
			try {
				return backgroundFragmentImageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's background image.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentImage backgroundFragmentImage;

	@JsonIgnore
	private Supplier<FragmentImage> _backgroundFragmentImageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's border color."
	)
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

	@GraphQLField(description = "The fragment's border color.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String borderColor;

	@JsonIgnore
	private Supplier<String> _borderColorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's background radius."
	)
	public String getBorderRadius() {
		if (_borderRadiusSupplier != null) {
			borderRadius = _borderRadiusSupplier.get();

			_borderRadiusSupplier = null;
		}

		return borderRadius;
	}

	public void setBorderRadius(String borderRadius) {
		this.borderRadius = borderRadius;

		_borderRadiusSupplier = null;
	}

	@JsonIgnore
	public void setBorderRadius(
		UnsafeSupplier<String, Exception> borderRadiusUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's background radius.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String borderRadius;

	@JsonIgnore
	private Supplier<String> _borderRadiusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's border width."
	)
	public String getBorderWidth() {
		if (_borderWidthSupplier != null) {
			borderWidth = _borderWidthSupplier.get();

			_borderWidthSupplier = null;
		}

		return borderWidth;
	}

	public void setBorderWidth(String borderWidth) {
		this.borderWidth = borderWidth;

		_borderWidthSupplier = null;
	}

	@JsonIgnore
	public void setBorderWidth(
		UnsafeSupplier<String, Exception> borderWidthUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's border width.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String borderWidth;

	@JsonIgnore
	private Supplier<String> _borderWidthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's font family."
	)
	public String getFontFamily() {
		if (_fontFamilySupplier != null) {
			fontFamily = _fontFamilySupplier.get();

			_fontFamilySupplier = null;
		}

		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;

		_fontFamilySupplier = null;
	}

	@JsonIgnore
	public void setFontFamily(
		UnsafeSupplier<String, Exception> fontFamilyUnsafeSupplier) {

		_fontFamilySupplier = () -> {
			try {
				return fontFamilyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's font family.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String fontFamily;

	@JsonIgnore
	private Supplier<String> _fontFamilySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's font size."
	)
	public String getFontSize() {
		if (_fontSizeSupplier != null) {
			fontSize = _fontSizeSupplier.get();

			_fontSizeSupplier = null;
		}

		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;

		_fontSizeSupplier = null;
	}

	@JsonIgnore
	public void setFontSize(
		UnsafeSupplier<String, Exception> fontSizeUnsafeSupplier) {

		_fontSizeSupplier = () -> {
			try {
				return fontSizeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's font size.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String fontSize;

	@JsonIgnore
	private Supplier<String> _fontSizeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's font weight."
	)
	public String getFontWeight() {
		if (_fontWeightSupplier != null) {
			fontWeight = _fontWeightSupplier.get();

			_fontWeightSupplier = null;
		}

		return fontWeight;
	}

	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;

		_fontWeightSupplier = null;
	}

	@JsonIgnore
	public void setFontWeight(
		UnsafeSupplier<String, Exception> fontWeightUnsafeSupplier) {

		_fontWeightSupplier = () -> {
			try {
				return fontWeightUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's font weight.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String fontWeight;

	@JsonIgnore
	private Supplier<String> _fontWeightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's height."
	)
	public String getHeight() {
		if (_heightSupplier != null) {
			height = _heightSupplier.get();

			_heightSupplier = null;
		}

		return height;
	}

	public void setHeight(String height) {
		this.height = height;

		_heightSupplier = null;
	}

	@JsonIgnore
	public void setHeight(
		UnsafeSupplier<String, Exception> heightUnsafeSupplier) {

		_heightSupplier = () -> {
			try {
				return heightUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's height.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String height;

	@JsonIgnore
	private Supplier<String> _heightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Specifies if the fragment is hidden to the user."
	)
	public Boolean getHidden() {
		if (_hiddenSupplier != null) {
			hidden = _hiddenSupplier.get();

			_hiddenSupplier = null;
		}

		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;

		_hiddenSupplier = null;
	}

	@JsonIgnore
	public void setHidden(
		UnsafeSupplier<Boolean, Exception> hiddenUnsafeSupplier) {

		_hiddenSupplier = () -> {
			try {
				return hiddenUnsafeSupplier.get();
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
		description = "Specifies if the fragment is hidden to the user."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean hidden;

	@JsonIgnore
	private Supplier<Boolean> _hiddenSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's margin bottom."
	)
	public String getMarginBottom() {
		if (_marginBottomSupplier != null) {
			marginBottom = _marginBottomSupplier.get();

			_marginBottomSupplier = null;
		}

		return marginBottom;
	}

	public void setMarginBottom(String marginBottom) {
		this.marginBottom = marginBottom;

		_marginBottomSupplier = null;
	}

	@JsonIgnore
	public void setMarginBottom(
		UnsafeSupplier<String, Exception> marginBottomUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's margin bottom.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String marginBottom;

	@JsonIgnore
	private Supplier<String> _marginBottomSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's margin left."
	)
	public String getMarginLeft() {
		if (_marginLeftSupplier != null) {
			marginLeft = _marginLeftSupplier.get();

			_marginLeftSupplier = null;
		}

		return marginLeft;
	}

	public void setMarginLeft(String marginLeft) {
		this.marginLeft = marginLeft;

		_marginLeftSupplier = null;
	}

	@JsonIgnore
	public void setMarginLeft(
		UnsafeSupplier<String, Exception> marginLeftUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's margin left.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String marginLeft;

	@JsonIgnore
	private Supplier<String> _marginLeftSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's margin right."
	)
	public String getMarginRight() {
		if (_marginRightSupplier != null) {
			marginRight = _marginRightSupplier.get();

			_marginRightSupplier = null;
		}

		return marginRight;
	}

	public void setMarginRight(String marginRight) {
		this.marginRight = marginRight;

		_marginRightSupplier = null;
	}

	@JsonIgnore
	public void setMarginRight(
		UnsafeSupplier<String, Exception> marginRightUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's margin right.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String marginRight;

	@JsonIgnore
	private Supplier<String> _marginRightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's margin top."
	)
	public String getMarginTop() {
		if (_marginTopSupplier != null) {
			marginTop = _marginTopSupplier.get();

			_marginTopSupplier = null;
		}

		return marginTop;
	}

	public void setMarginTop(String marginTop) {
		this.marginTop = marginTop;

		_marginTopSupplier = null;
	}

	@JsonIgnore
	public void setMarginTop(
		UnsafeSupplier<String, Exception> marginTopUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's margin top.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String marginTop;

	@JsonIgnore
	private Supplier<String> _marginTopSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's max height."
	)
	public String getMaxHeight() {
		if (_maxHeightSupplier != null) {
			maxHeight = _maxHeightSupplier.get();

			_maxHeightSupplier = null;
		}

		return maxHeight;
	}

	public void setMaxHeight(String maxHeight) {
		this.maxHeight = maxHeight;

		_maxHeightSupplier = null;
	}

	@JsonIgnore
	public void setMaxHeight(
		UnsafeSupplier<String, Exception> maxHeightUnsafeSupplier) {

		_maxHeightSupplier = () -> {
			try {
				return maxHeightUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's max height.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String maxHeight;

	@JsonIgnore
	private Supplier<String> _maxHeightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's max width."
	)
	public String getMaxWidth() {
		if (_maxWidthSupplier != null) {
			maxWidth = _maxWidthSupplier.get();

			_maxWidthSupplier = null;
		}

		return maxWidth;
	}

	public void setMaxWidth(String maxWidth) {
		this.maxWidth = maxWidth;

		_maxWidthSupplier = null;
	}

	@JsonIgnore
	public void setMaxWidth(
		UnsafeSupplier<String, Exception> maxWidthUnsafeSupplier) {

		_maxWidthSupplier = () -> {
			try {
				return maxWidthUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's max width.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String maxWidth;

	@JsonIgnore
	private Supplier<String> _maxWidthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's min height."
	)
	public String getMinHeight() {
		if (_minHeightSupplier != null) {
			minHeight = _minHeightSupplier.get();

			_minHeightSupplier = null;
		}

		return minHeight;
	}

	public void setMinHeight(String minHeight) {
		this.minHeight = minHeight;

		_minHeightSupplier = null;
	}

	@JsonIgnore
	public void setMinHeight(
		UnsafeSupplier<String, Exception> minHeightUnsafeSupplier) {

		_minHeightSupplier = () -> {
			try {
				return minHeightUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's min height.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String minHeight;

	@JsonIgnore
	private Supplier<String> _minHeightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's min width."
	)
	public String getMinWidth() {
		if (_minWidthSupplier != null) {
			minWidth = _minWidthSupplier.get();

			_minWidthSupplier = null;
		}

		return minWidth;
	}

	public void setMinWidth(String minWidth) {
		this.minWidth = minWidth;

		_minWidthSupplier = null;
	}

	@JsonIgnore
	public void setMinWidth(
		UnsafeSupplier<String, Exception> minWidthUnsafeSupplier) {

		_minWidthSupplier = () -> {
			try {
				return minWidthUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's min width.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String minWidth;

	@JsonIgnore
	private Supplier<String> _minWidthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's opacity."
	)
	public String getOpacity() {
		if (_opacitySupplier != null) {
			opacity = _opacitySupplier.get();

			_opacitySupplier = null;
		}

		return opacity;
	}

	public void setOpacity(String opacity) {
		this.opacity = opacity;

		_opacitySupplier = null;
	}

	@JsonIgnore
	public void setOpacity(
		UnsafeSupplier<String, Exception> opacityUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's opacity.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String opacity;

	@JsonIgnore
	private Supplier<String> _opacitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's overflow behavior."
	)
	public String getOverflow() {
		if (_overflowSupplier != null) {
			overflow = _overflowSupplier.get();

			_overflowSupplier = null;
		}

		return overflow;
	}

	public void setOverflow(String overflow) {
		this.overflow = overflow;

		_overflowSupplier = null;
	}

	@JsonIgnore
	public void setOverflow(
		UnsafeSupplier<String, Exception> overflowUnsafeSupplier) {

		_overflowSupplier = () -> {
			try {
				return overflowUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's overflow behavior.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String overflow;

	@JsonIgnore
	private Supplier<String> _overflowSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's padding bottom."
	)
	public String getPaddingBottom() {
		if (_paddingBottomSupplier != null) {
			paddingBottom = _paddingBottomSupplier.get();

			_paddingBottomSupplier = null;
		}

		return paddingBottom;
	}

	public void setPaddingBottom(String paddingBottom) {
		this.paddingBottom = paddingBottom;

		_paddingBottomSupplier = null;
	}

	@JsonIgnore
	public void setPaddingBottom(
		UnsafeSupplier<String, Exception> paddingBottomUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's padding bottom.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String paddingBottom;

	@JsonIgnore
	private Supplier<String> _paddingBottomSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's padding left."
	)
	public String getPaddingLeft() {
		if (_paddingLeftSupplier != null) {
			paddingLeft = _paddingLeftSupplier.get();

			_paddingLeftSupplier = null;
		}

		return paddingLeft;
	}

	public void setPaddingLeft(String paddingLeft) {
		this.paddingLeft = paddingLeft;

		_paddingLeftSupplier = null;
	}

	@JsonIgnore
	public void setPaddingLeft(
		UnsafeSupplier<String, Exception> paddingLeftUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's padding left.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String paddingLeft;

	@JsonIgnore
	private Supplier<String> _paddingLeftSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's padding right."
	)
	public String getPaddingRight() {
		if (_paddingRightSupplier != null) {
			paddingRight = _paddingRightSupplier.get();

			_paddingRightSupplier = null;
		}

		return paddingRight;
	}

	public void setPaddingRight(String paddingRight) {
		this.paddingRight = paddingRight;

		_paddingRightSupplier = null;
	}

	@JsonIgnore
	public void setPaddingRight(
		UnsafeSupplier<String, Exception> paddingRightUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's padding right.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String paddingRight;

	@JsonIgnore
	private Supplier<String> _paddingRightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's padding top."
	)
	public String getPaddingTop() {
		if (_paddingTopSupplier != null) {
			paddingTop = _paddingTopSupplier.get();

			_paddingTopSupplier = null;
		}

		return paddingTop;
	}

	public void setPaddingTop(String paddingTop) {
		this.paddingTop = paddingTop;

		_paddingTopSupplier = null;
	}

	@JsonIgnore
	public void setPaddingTop(
		UnsafeSupplier<String, Exception> paddingTopUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's padding top.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String paddingTop;

	@JsonIgnore
	private Supplier<String> _paddingTopSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's shadow effect."
	)
	public String getShadow() {
		if (_shadowSupplier != null) {
			shadow = _shadowSupplier.get();

			_shadowSupplier = null;
		}

		return shadow;
	}

	public void setShadow(String shadow) {
		this.shadow = shadow;

		_shadowSupplier = null;
	}

	@JsonIgnore
	public void setShadow(
		UnsafeSupplier<String, Exception> shadowUnsafeSupplier) {

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

	@GraphQLField(description = "The fragment's shadow effect.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String shadow;

	@JsonIgnore
	private Supplier<String> _shadowSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's text align."
	)
	public String getTextAlign() {
		if (_textAlignSupplier != null) {
			textAlign = _textAlignSupplier.get();

			_textAlignSupplier = null;
		}

		return textAlign;
	}

	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;

		_textAlignSupplier = null;
	}

	@JsonIgnore
	public void setTextAlign(
		UnsafeSupplier<String, Exception> textAlignUnsafeSupplier) {

		_textAlignSupplier = () -> {
			try {
				return textAlignUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's text align.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String textAlign;

	@JsonIgnore
	private Supplier<String> _textAlignSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's text color."
	)
	public String getTextColor() {
		if (_textColorSupplier != null) {
			textColor = _textColorSupplier.get();

			_textColorSupplier = null;
		}

		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;

		_textColorSupplier = null;
	}

	@JsonIgnore
	public void setTextColor(
		UnsafeSupplier<String, Exception> textColorUnsafeSupplier) {

		_textColorSupplier = () -> {
			try {
				return textColorUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's text color.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String textColor;

	@JsonIgnore
	private Supplier<String> _textColorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's width."
	)
	public String getWidth() {
		if (_widthSupplier != null) {
			width = _widthSupplier.get();

			_widthSupplier = null;
		}

		return width;
	}

	public void setWidth(String width) {
		this.width = width;

		_widthSupplier = null;
	}

	@JsonIgnore
	public void setWidth(
		UnsafeSupplier<String, Exception> widthUnsafeSupplier) {

		_widthSupplier = () -> {
			try {
				return widthUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's width.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String width;

	@JsonIgnore
	private Supplier<String> _widthSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentStyle)) {
			return false;
		}

		FragmentStyle fragmentStyle = (FragmentStyle)object;

		return Objects.equals(toString(), fragmentStyle.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String backgroundColor = getBackgroundColor();

		if (backgroundColor != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundColor\": ");

			sb.append("\"");

			sb.append(_escape(backgroundColor));

			sb.append("\"");
		}

		FragmentImage backgroundFragmentImage = getBackgroundFragmentImage();

		if (backgroundFragmentImage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundFragmentImage\": ");

			sb.append(String.valueOf(backgroundFragmentImage));
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

		String borderRadius = getBorderRadius();

		if (borderRadius != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderRadius\": ");

			sb.append("\"");

			sb.append(_escape(borderRadius));

			sb.append("\"");
		}

		String borderWidth = getBorderWidth();

		if (borderWidth != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderWidth\": ");

			sb.append("\"");

			sb.append(_escape(borderWidth));

			sb.append("\"");
		}

		String fontFamily = getFontFamily();

		if (fontFamily != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fontFamily\": ");

			sb.append("\"");

			sb.append(_escape(fontFamily));

			sb.append("\"");
		}

		String fontSize = getFontSize();

		if (fontSize != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fontSize\": ");

			sb.append("\"");

			sb.append(_escape(fontSize));

			sb.append("\"");
		}

		String fontWeight = getFontWeight();

		if (fontWeight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fontWeight\": ");

			sb.append("\"");

			sb.append(_escape(fontWeight));

			sb.append("\"");
		}

		String height = getHeight();

		if (height != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"height\": ");

			sb.append("\"");

			sb.append(_escape(height));

			sb.append("\"");
		}

		Boolean hidden = getHidden();

		if (hidden != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hidden\": ");

			sb.append(hidden);
		}

		String marginBottom = getMarginBottom();

		if (marginBottom != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginBottom\": ");

			sb.append("\"");

			sb.append(_escape(marginBottom));

			sb.append("\"");
		}

		String marginLeft = getMarginLeft();

		if (marginLeft != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginLeft\": ");

			sb.append("\"");

			sb.append(_escape(marginLeft));

			sb.append("\"");
		}

		String marginRight = getMarginRight();

		if (marginRight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginRight\": ");

			sb.append("\"");

			sb.append(_escape(marginRight));

			sb.append("\"");
		}

		String marginTop = getMarginTop();

		if (marginTop != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginTop\": ");

			sb.append("\"");

			sb.append(_escape(marginTop));

			sb.append("\"");
		}

		String maxHeight = getMaxHeight();

		if (maxHeight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxHeight\": ");

			sb.append("\"");

			sb.append(_escape(maxHeight));

			sb.append("\"");
		}

		String maxWidth = getMaxWidth();

		if (maxWidth != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxWidth\": ");

			sb.append("\"");

			sb.append(_escape(maxWidth));

			sb.append("\"");
		}

		String minHeight = getMinHeight();

		if (minHeight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minHeight\": ");

			sb.append("\"");

			sb.append(_escape(minHeight));

			sb.append("\"");
		}

		String minWidth = getMinWidth();

		if (minWidth != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minWidth\": ");

			sb.append("\"");

			sb.append(_escape(minWidth));

			sb.append("\"");
		}

		String opacity = getOpacity();

		if (opacity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"opacity\": ");

			sb.append("\"");

			sb.append(_escape(opacity));

			sb.append("\"");
		}

		String overflow = getOverflow();

		if (overflow != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"overflow\": ");

			sb.append("\"");

			sb.append(_escape(overflow));

			sb.append("\"");
		}

		String paddingBottom = getPaddingBottom();

		if (paddingBottom != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingBottom\": ");

			sb.append("\"");

			sb.append(_escape(paddingBottom));

			sb.append("\"");
		}

		String paddingLeft = getPaddingLeft();

		if (paddingLeft != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingLeft\": ");

			sb.append("\"");

			sb.append(_escape(paddingLeft));

			sb.append("\"");
		}

		String paddingRight = getPaddingRight();

		if (paddingRight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingRight\": ");

			sb.append("\"");

			sb.append(_escape(paddingRight));

			sb.append("\"");
		}

		String paddingTop = getPaddingTop();

		if (paddingTop != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingTop\": ");

			sb.append("\"");

			sb.append(_escape(paddingTop));

			sb.append("\"");
		}

		String shadow = getShadow();

		if (shadow != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shadow\": ");

			sb.append("\"");

			sb.append(_escape(shadow));

			sb.append("\"");
		}

		String textAlign = getTextAlign();

		if (textAlign != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"textAlign\": ");

			sb.append("\"");

			sb.append(_escape(textAlign));

			sb.append("\"");
		}

		String textColor = getTextColor();

		if (textColor != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"textColor\": ");

			sb.append("\"");

			sb.append(_escape(textColor));

			sb.append("\"");
		}

		String width = getWidth();

		if (width != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"width\": ");

			sb.append("\"");

			sb.append(_escape(width));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.FragmentStyle",
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