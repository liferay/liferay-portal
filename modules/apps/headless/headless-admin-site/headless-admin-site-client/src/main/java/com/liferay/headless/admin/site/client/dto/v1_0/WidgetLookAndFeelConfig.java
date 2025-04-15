/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.WidgetLookAndFeelConfigSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class WidgetLookAndFeelConfig implements Cloneable, Serializable {

	public static WidgetLookAndFeelConfig toDTO(String json) {
		return WidgetLookAndFeelConfigSerDes.toDTO(json);
	}

	public Object getAdvancedStylingConfig() {
		return advancedStylingConfig;
	}

	public void setAdvancedStylingConfig(Object advancedStylingConfig) {
		this.advancedStylingConfig = advancedStylingConfig;
	}

	public void setAdvancedStylingConfig(
		UnsafeSupplier<Object, Exception> advancedStylingConfigUnsafeSupplier) {

		try {
			advancedStylingConfig = advancedStylingConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object advancedStylingConfig;

	public Object getBackgroundStylesConfig() {
		return backgroundStylesConfig;
	}

	public void setBackgroundStylesConfig(Object backgroundStylesConfig) {
		this.backgroundStylesConfig = backgroundStylesConfig;
	}

	public void setBackgroundStylesConfig(
		UnsafeSupplier<Object, Exception>
			backgroundStylesConfigUnsafeSupplier) {

		try {
			backgroundStylesConfig = backgroundStylesConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object backgroundStylesConfig;

	public Object getBorderStylesConfig() {
		return borderStylesConfig;
	}

	public void setBorderStylesConfig(Object borderStylesConfig) {
		this.borderStylesConfig = borderStylesConfig;
	}

	public void setBorderStylesConfig(
		UnsafeSupplier<Object, Exception> borderStylesConfigUnsafeSupplier) {

		try {
			borderStylesConfig = borderStylesConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object borderStylesConfig;

	public Object getGeneralConfig() {
		return generalConfig;
	}

	public void setGeneralConfig(Object generalConfig) {
		this.generalConfig = generalConfig;
	}

	public void setGeneralConfig(
		UnsafeSupplier<Object, Exception> generalConfigUnsafeSupplier) {

		try {
			generalConfig = generalConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object generalConfig;

	public Object getMarginAndPaddingConfig() {
		return marginAndPaddingConfig;
	}

	public void setMarginAndPaddingConfig(Object marginAndPaddingConfig) {
		this.marginAndPaddingConfig = marginAndPaddingConfig;
	}

	public void setMarginAndPaddingConfig(
		UnsafeSupplier<Object, Exception>
			marginAndPaddingConfigUnsafeSupplier) {

		try {
			marginAndPaddingConfig = marginAndPaddingConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object marginAndPaddingConfig;

	public Object getTextStylesConfig() {
		return textStylesConfig;
	}

	public void setTextStylesConfig(Object textStylesConfig) {
		this.textStylesConfig = textStylesConfig;
	}

	public void setTextStylesConfig(
		UnsafeSupplier<Object, Exception> textStylesConfigUnsafeSupplier) {

		try {
			textStylesConfig = textStylesConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object textStylesConfig;

	@Override
	public WidgetLookAndFeelConfig clone() throws CloneNotSupportedException {
		return (WidgetLookAndFeelConfig)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetLookAndFeelConfig)) {
			return false;
		}

		WidgetLookAndFeelConfig widgetLookAndFeelConfig =
			(WidgetLookAndFeelConfig)object;

		return Objects.equals(toString(), widgetLookAndFeelConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WidgetLookAndFeelConfigSerDes.toJSON(this);
	}

}