/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentViewportStyleSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FragmentViewportStyle implements Cloneable, Serializable {

	public static FragmentViewportStyle toDTO(String json) {
		return FragmentViewportStyleSerDes.toDTO(json);
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setBackgroundColor(
		UnsafeSupplier<String, Exception> backgroundColorUnsafeSupplier) {

		try {
			backgroundColor = backgroundColorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String backgroundColor;

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

	public String getBorderRadius() {
		return borderRadius;
	}

	public void setBorderRadius(String borderRadius) {
		this.borderRadius = borderRadius;
	}

	public void setBorderRadius(
		UnsafeSupplier<String, Exception> borderRadiusUnsafeSupplier) {

		try {
			borderRadius = borderRadiusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String borderRadius;

	public String getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(String borderWidth) {
		this.borderWidth = borderWidth;
	}

	public void setBorderWidth(
		UnsafeSupplier<String, Exception> borderWidthUnsafeSupplier) {

		try {
			borderWidth = borderWidthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String borderWidth;

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public void setFontFamily(
		UnsafeSupplier<String, Exception> fontFamilyUnsafeSupplier) {

		try {
			fontFamily = fontFamilyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fontFamily;

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public void setFontSize(
		UnsafeSupplier<String, Exception> fontSizeUnsafeSupplier) {

		try {
			fontSize = fontSizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fontSize;

	public String getFontWeight() {
		return fontWeight;
	}

	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}

	public void setFontWeight(
		UnsafeSupplier<String, Exception> fontWeightUnsafeSupplier) {

		try {
			fontWeight = fontWeightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fontWeight;

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void setHeight(
		UnsafeSupplier<String, Exception> heightUnsafeSupplier) {

		try {
			height = heightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String height;

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public void setHidden(
		UnsafeSupplier<Boolean, Exception> hiddenUnsafeSupplier) {

		try {
			hidden = hiddenUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean hidden;

	public String getMarginBottom() {
		return marginBottom;
	}

	public void setMarginBottom(String marginBottom) {
		this.marginBottom = marginBottom;
	}

	public void setMarginBottom(
		UnsafeSupplier<String, Exception> marginBottomUnsafeSupplier) {

		try {
			marginBottom = marginBottomUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String marginBottom;

	public String getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(String marginLeft) {
		this.marginLeft = marginLeft;
	}

	public void setMarginLeft(
		UnsafeSupplier<String, Exception> marginLeftUnsafeSupplier) {

		try {
			marginLeft = marginLeftUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String marginLeft;

	public String getMarginRight() {
		return marginRight;
	}

	public void setMarginRight(String marginRight) {
		this.marginRight = marginRight;
	}

	public void setMarginRight(
		UnsafeSupplier<String, Exception> marginRightUnsafeSupplier) {

		try {
			marginRight = marginRightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String marginRight;

	public String getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(String marginTop) {
		this.marginTop = marginTop;
	}

	public void setMarginTop(
		UnsafeSupplier<String, Exception> marginTopUnsafeSupplier) {

		try {
			marginTop = marginTopUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String marginTop;

	public String getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(String maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setMaxHeight(
		UnsafeSupplier<String, Exception> maxHeightUnsafeSupplier) {

		try {
			maxHeight = maxHeightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String maxHeight;

	public String getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(String maxWidth) {
		this.maxWidth = maxWidth;
	}

	public void setMaxWidth(
		UnsafeSupplier<String, Exception> maxWidthUnsafeSupplier) {

		try {
			maxWidth = maxWidthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String maxWidth;

	public String getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(String minHeight) {
		this.minHeight = minHeight;
	}

	public void setMinHeight(
		UnsafeSupplier<String, Exception> minHeightUnsafeSupplier) {

		try {
			minHeight = minHeightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String minHeight;

	public String getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(String minWidth) {
		this.minWidth = minWidth;
	}

	public void setMinWidth(
		UnsafeSupplier<String, Exception> minWidthUnsafeSupplier) {

		try {
			minWidth = minWidthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String minWidth;

	public String getOpacity() {
		return opacity;
	}

	public void setOpacity(String opacity) {
		this.opacity = opacity;
	}

	public void setOpacity(
		UnsafeSupplier<String, Exception> opacityUnsafeSupplier) {

		try {
			opacity = opacityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String opacity;

	public String getOverflow() {
		return overflow;
	}

	public void setOverflow(String overflow) {
		this.overflow = overflow;
	}

	public void setOverflow(
		UnsafeSupplier<String, Exception> overflowUnsafeSupplier) {

		try {
			overflow = overflowUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String overflow;

	public String getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(String paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	public void setPaddingBottom(
		UnsafeSupplier<String, Exception> paddingBottomUnsafeSupplier) {

		try {
			paddingBottom = paddingBottomUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String paddingBottom;

	public String getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(String paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public void setPaddingLeft(
		UnsafeSupplier<String, Exception> paddingLeftUnsafeSupplier) {

		try {
			paddingLeft = paddingLeftUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String paddingLeft;

	public String getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(String paddingRight) {
		this.paddingRight = paddingRight;
	}

	public void setPaddingRight(
		UnsafeSupplier<String, Exception> paddingRightUnsafeSupplier) {

		try {
			paddingRight = paddingRightUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String paddingRight;

	public String getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(String paddingTop) {
		this.paddingTop = paddingTop;
	}

	public void setPaddingTop(
		UnsafeSupplier<String, Exception> paddingTopUnsafeSupplier) {

		try {
			paddingTop = paddingTopUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String paddingTop;

	public String getShadow() {
		return shadow;
	}

	public void setShadow(String shadow) {
		this.shadow = shadow;
	}

	public void setShadow(
		UnsafeSupplier<String, Exception> shadowUnsafeSupplier) {

		try {
			shadow = shadowUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String shadow;

	public String getTextAlign() {
		return textAlign;
	}

	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}

	public void setTextAlign(
		UnsafeSupplier<String, Exception> textAlignUnsafeSupplier) {

		try {
			textAlign = textAlignUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String textAlign;

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	public void setTextColor(
		UnsafeSupplier<String, Exception> textColorUnsafeSupplier) {

		try {
			textColor = textColorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String textColor;

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setWidth(
		UnsafeSupplier<String, Exception> widthUnsafeSupplier) {

		try {
			width = widthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String width;

	@Override
	public FragmentViewportStyle clone() throws CloneNotSupportedException {
		return (FragmentViewportStyle)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentViewportStyle)) {
			return false;
		}

		FragmentViewportStyle fragmentViewportStyle =
			(FragmentViewportStyle)object;

		return Objects.equals(toString(), fragmentViewportStyle.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentViewportStyleSerDes.toJSON(this);
	}

}