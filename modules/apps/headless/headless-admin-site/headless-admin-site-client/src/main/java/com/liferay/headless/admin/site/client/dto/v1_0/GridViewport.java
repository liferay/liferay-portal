/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.GridViewportSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class GridViewport implements Cloneable, Serializable {

	public static GridViewport toDTO(String json) {
		return GridViewportSerDes.toDTO(json);
	}

	public String getCustomCSS() {
		return customCSS;
	}

	public void setCustomCSS(String customCSS) {
		this.customCSS = customCSS;
	}

	public void setCustomCSS(
		UnsafeSupplier<String, Exception> customCSSUnsafeSupplier) {

		try {
			customCSS = customCSSUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String customCSS;

	public FragmentViewportStyle getFragmentViewportStyle() {
		return fragmentViewportStyle;
	}

	public void setFragmentViewportStyle(
		FragmentViewportStyle fragmentViewportStyle) {

		this.fragmentViewportStyle = fragmentViewportStyle;
	}

	public void setFragmentViewportStyle(
		UnsafeSupplier<FragmentViewportStyle, Exception>
			fragmentViewportStyleUnsafeSupplier) {

		try {
			fragmentViewportStyle = fragmentViewportStyleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentViewportStyle fragmentViewportStyle;

	public GridViewportDefinition getGridViewportDefinition() {
		return gridViewportDefinition;
	}

	public void setGridViewportDefinition(
		GridViewportDefinition gridViewportDefinition) {

		this.gridViewportDefinition = gridViewportDefinition;
	}

	public void setGridViewportDefinition(
		UnsafeSupplier<GridViewportDefinition, Exception>
			gridViewportDefinitionUnsafeSupplier) {

		try {
			gridViewportDefinition = gridViewportDefinitionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected GridViewportDefinition gridViewportDefinition;

	public Id getId() {
		return id;
	}

	public String getIdAsString() {
		if (id == null) {
			return null;
		}

		return id.toString();
	}

	public void setId(Id id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Id, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Id id;

	@Override
	public GridViewport clone() throws CloneNotSupportedException {
		return (GridViewport)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GridViewport)) {
			return false;
		}

		GridViewport gridViewport = (GridViewport)object;

		return Objects.equals(toString(), gridViewport.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return GridViewportSerDes.toJSON(this);
	}

	public static enum Id {

		LANDSCAPE_MOBILE("LandscapeMobile"), PORTRAIT_MOBILE("PortraitMobile"),
		TABLET("Tablet");

		public static Id create(String value) {
			for (Id id : values()) {
				if (Objects.equals(id.getValue(), value) ||
					Objects.equals(id.name(), value)) {

					return id;
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

		private Id(String value) {
			_value = value;
		}

		private final String _value;

	}

}