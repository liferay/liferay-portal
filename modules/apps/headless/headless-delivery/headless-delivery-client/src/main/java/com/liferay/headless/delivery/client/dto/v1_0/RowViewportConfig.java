/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.RowViewportConfigSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class RowViewportConfig implements Cloneable, Serializable {

	public static RowViewportConfig toDTO(String json) {
		return RowViewportConfigSerDes.toDTO(json);
	}

	public LandscapeMobile getLandscapeMobile() {
		return landscapeMobile;
	}

	public void setLandscapeMobile(LandscapeMobile landscapeMobile) {
		this.landscapeMobile = landscapeMobile;
	}

	public void setLandscapeMobile(
		UnsafeSupplier<LandscapeMobile, Exception>
			landscapeMobileUnsafeSupplier) {

		try {
			landscapeMobile = landscapeMobileUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected LandscapeMobile landscapeMobile;

	public PortraitMobile getPortraitMobile() {
		return portraitMobile;
	}

	public void setPortraitMobile(PortraitMobile portraitMobile) {
		this.portraitMobile = portraitMobile;
	}

	public void setPortraitMobile(
		UnsafeSupplier<PortraitMobile, Exception>
			portraitMobileUnsafeSupplier) {

		try {
			portraitMobile = portraitMobileUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PortraitMobile portraitMobile;

	public Tablet getTablet() {
		return tablet;
	}

	public void setTablet(Tablet tablet) {
		this.tablet = tablet;
	}

	public void setTablet(
		UnsafeSupplier<Tablet, Exception> tabletUnsafeSupplier) {

		try {
			tablet = tabletUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Tablet tablet;

	@Override
	public RowViewportConfig clone() throws CloneNotSupportedException {
		return (RowViewportConfig)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RowViewportConfig)) {
			return false;
		}

		RowViewportConfig rowViewportConfig = (RowViewportConfig)object;

		return Objects.equals(toString(), rowViewportConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return RowViewportConfigSerDes.toJSON(this);
	}

}