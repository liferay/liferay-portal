/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.dto.v1_0;

import com.liferay.headless.admin.content.client.function.UnsafeSupplier;
import com.liferay.headless.admin.content.client.serdes.v1_0.PageDefinitionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PageDefinition implements Cloneable, Serializable {

	public static PageDefinition toDTO(String json) {
		return PageDefinitionSerDes.toDTO(json);
	}

	public PageElement getPageElement() {
		return pageElement;
	}

	public void setPageElement(PageElement pageElement) {
		this.pageElement = pageElement;
	}

	public void setPageElement(
		UnsafeSupplier<PageElement, Exception> pageElementUnsafeSupplier) {

		try {
			pageElement = pageElementUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PageElement pageElement;

	public PageRule[] getPageRules() {
		return pageRules;
	}

	public void setPageRules(PageRule[] pageRules) {
		this.pageRules = pageRules;
	}

	public void setPageRules(
		UnsafeSupplier<PageRule[], Exception> pageRulesUnsafeSupplier) {

		try {
			pageRules = pageRulesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PageRule[] pageRules;

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public void setSettings(
		UnsafeSupplier<Settings, Exception> settingsUnsafeSupplier) {

		try {
			settings = settingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Settings settings;

	public Double getVersion() {
		return version;
	}

	public void setVersion(Double version) {
		this.version = version;
	}

	public void setVersion(
		UnsafeSupplier<Double, Exception> versionUnsafeSupplier) {

		try {
			version = versionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double version;

	@Override
	public PageDefinition clone() throws CloneNotSupportedException {
		return (PageDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageDefinition)) {
			return false;
		}

		PageDefinition pageDefinition = (PageDefinition)object;

		return Objects.equals(toString(), pageDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageDefinitionSerDes.toJSON(this);
	}

}