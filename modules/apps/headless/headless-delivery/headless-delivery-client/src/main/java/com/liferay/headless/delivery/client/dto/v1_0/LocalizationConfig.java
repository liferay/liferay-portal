/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.LocalizationConfigSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class LocalizationConfig implements Cloneable, Serializable {

	public static LocalizationConfig toDTO(String json) {
		return LocalizationConfigSerDes.toDTO(json);
	}

	public FragmentInlineValue getUnlocalizedFieldsMessage() {
		return unlocalizedFieldsMessage;
	}

	public void setUnlocalizedFieldsMessage(
		FragmentInlineValue unlocalizedFieldsMessage) {

		this.unlocalizedFieldsMessage = unlocalizedFieldsMessage;
	}

	public void setUnlocalizedFieldsMessage(
		UnsafeSupplier<FragmentInlineValue, Exception>
			unlocalizedFieldsMessageUnsafeSupplier) {

		try {
			unlocalizedFieldsMessage =
				unlocalizedFieldsMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentInlineValue unlocalizedFieldsMessage;

	public UnlocalizedFieldsState getUnlocalizedFieldsState() {
		return unlocalizedFieldsState;
	}

	public String getUnlocalizedFieldsStateAsString() {
		if (unlocalizedFieldsState == null) {
			return null;
		}

		return unlocalizedFieldsState.toString();
	}

	public void setUnlocalizedFieldsState(
		UnlocalizedFieldsState unlocalizedFieldsState) {

		this.unlocalizedFieldsState = unlocalizedFieldsState;
	}

	public void setUnlocalizedFieldsState(
		UnsafeSupplier<UnlocalizedFieldsState, Exception>
			unlocalizedFieldsStateUnsafeSupplier) {

		try {
			unlocalizedFieldsState = unlocalizedFieldsStateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UnlocalizedFieldsState unlocalizedFieldsState;

	@Override
	public LocalizationConfig clone() throws CloneNotSupportedException {
		return (LocalizationConfig)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LocalizationConfig)) {
			return false;
		}

		LocalizationConfig localizationConfig = (LocalizationConfig)object;

		return Objects.equals(toString(), localizationConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return LocalizationConfigSerDes.toJSON(this);
	}

	public static enum UnlocalizedFieldsState {

		DISABLED("Disabled"), READ_ONLY("ReadOnly");

		public static UnlocalizedFieldsState create(String value) {
			for (UnlocalizedFieldsState unlocalizedFieldsState : values()) {
				if (Objects.equals(unlocalizedFieldsState.getValue(), value) ||
					Objects.equals(unlocalizedFieldsState.name(), value)) {

					return unlocalizedFieldsState;
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

		private UnlocalizedFieldsState(String value) {
			_value = value;
		}

		private final String _value;

	}

}