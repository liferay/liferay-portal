/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.EnumTestEntitySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class EnumTestEntity implements Cloneable, Serializable {

	public static EnumTestEntity toDTO(String json) {
		return EnumTestEntitySerDes.toDTO(json);
	}

	public TestEnum getTestEnum() {
		return testEnum;
	}

	public String getTestEnumAsString() {
		if (testEnum == null) {
			return null;
		}

		return testEnum.toString();
	}

	public void setTestEnum(TestEnum testEnum) {
		this.testEnum = testEnum;
	}

	public void setTestEnum(
		UnsafeSupplier<TestEnum, Exception> testEnumUnsafeSupplier) {

		try {
			testEnum = testEnumUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TestEnum testEnum;

	@Override
	public EnumTestEntity clone() throws CloneNotSupportedException {
		return (EnumTestEntity)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EnumTestEntity)) {
			return false;
		}

		EnumTestEntity enumTestEntity = (EnumTestEntity)object;

		return Objects.equals(toString(), enumTestEntity.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return EnumTestEntitySerDes.toJSON(this);
	}

	public static enum TestEnum {

		NEGATIVE_1EM("-1em"), NEGATIVE_0_POINT_95EM("-0.95em"),
		POSITIVE_1EM("1em"), POSITIVE_0_POINT_95EM("0.95em");

		public static TestEnum create(String value) {
			for (TestEnum testEnum : values()) {
				if (Objects.equals(testEnum.getValue(), value) ||
					Objects.equals(testEnum.name(), value)) {

					return testEnum;
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

		private TestEnum(String value) {
			_value = value;
		}

		private final String _value;

	}

}