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

	public AdvancedStylingConfig getAdvancedStylingConfig() {
		return advancedStylingConfig;
	}

	public void setAdvancedStylingConfig(
		AdvancedStylingConfig advancedStylingConfig) {

		this.advancedStylingConfig = advancedStylingConfig;
	}

	public void setAdvancedStylingConfig(
		UnsafeSupplier<AdvancedStylingConfig, Exception>
			advancedStylingConfigUnsafeSupplier) {

		try {
			advancedStylingConfig = advancedStylingConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AdvancedStylingConfig advancedStylingConfig;

	public BackgroundStylesConfig getBackgroundStylesConfig() {
		return backgroundStylesConfig;
	}

	public void setBackgroundStylesConfig(
		BackgroundStylesConfig backgroundStylesConfig) {

		this.backgroundStylesConfig = backgroundStylesConfig;
	}

	public void setBackgroundStylesConfig(
		UnsafeSupplier<BackgroundStylesConfig, Exception>
			backgroundStylesConfigUnsafeSupplier) {

		try {
			backgroundStylesConfig = backgroundStylesConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BackgroundStylesConfig backgroundStylesConfig;

	public BorderStylesConfig getBorderStylesConfig() {
		return borderStylesConfig;
	}

	public void setBorderStylesConfig(BorderStylesConfig borderStylesConfig) {
		this.borderStylesConfig = borderStylesConfig;
	}

	public void setBorderStylesConfig(
		UnsafeSupplier<BorderStylesConfig, Exception>
			borderStylesConfigUnsafeSupplier) {

		try {
			borderStylesConfig = borderStylesConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BorderStylesConfig borderStylesConfig;

	public GeneralConfig getGeneralConfig() {
		return generalConfig;
	}

	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	public void setGeneralConfig(
		UnsafeSupplier<GeneralConfig, Exception> generalConfigUnsafeSupplier) {

		try {
			generalConfig = generalConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected GeneralConfig generalConfig;

	public MarginAndPaddingConfig getMarginAndPaddingConfig() {
		return marginAndPaddingConfig;
	}

	public void setMarginAndPaddingConfig(
		MarginAndPaddingConfig marginAndPaddingConfig) {

		this.marginAndPaddingConfig = marginAndPaddingConfig;
	}

	public void setMarginAndPaddingConfig(
		UnsafeSupplier<MarginAndPaddingConfig, Exception>
			marginAndPaddingConfigUnsafeSupplier) {

		try {
			marginAndPaddingConfig = marginAndPaddingConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MarginAndPaddingConfig marginAndPaddingConfig;

	public TextStylesConfig getTextStylesConfig() {
		return textStylesConfig;
	}

	public void setTextStylesConfig(TextStylesConfig textStylesConfig) {
		this.textStylesConfig = textStylesConfig;
	}

	public void setTextStylesConfig(
		UnsafeSupplier<TextStylesConfig, Exception>
			textStylesConfigUnsafeSupplier) {

		try {
			textStylesConfig = textStylesConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TextStylesConfig textStylesConfig;

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