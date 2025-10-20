/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.BasicLayoutSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class BasicLayout implements Cloneable, Serializable {

	public static BasicLayout toDTO(String json) {
		return BasicLayoutSerDes.toDTO(json);
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

	@Override
	public BasicLayout clone() throws CloneNotSupportedException {
		return (BasicLayout)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BasicLayout)) {
			return false;
		}

		BasicLayout basicLayout = (BasicLayout)object;

		return Objects.equals(toString(), basicLayout.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return BasicLayoutSerDes.toJSON(this);
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

}