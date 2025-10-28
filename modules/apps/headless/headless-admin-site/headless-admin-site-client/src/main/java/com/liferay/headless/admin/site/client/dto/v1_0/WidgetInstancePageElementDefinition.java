/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.WidgetInstancePageElementDefinitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class WidgetInstancePageElementDefinition
	extends PageElementDefinition implements Cloneable, Serializable {

	public static WidgetInstancePageElementDefinition toDTO(String json) {
		return WidgetInstancePageElementDefinitionSerDes.toDTO(json);
	}

	public String[] getCssClasses() {
		return cssClasses;
	}

	public void setCssClasses(String[] cssClasses) {
		this.cssClasses = cssClasses;
	}

	public void setCssClasses(
		UnsafeSupplier<String[], Exception> cssClassesUnsafeSupplier) {

		try {
			cssClasses = cssClassesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] cssClasses;

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

	public String getDraftWidgetInstanceExternalReferenceCode() {
		return draftWidgetInstanceExternalReferenceCode;
	}

	public void setDraftWidgetInstanceExternalReferenceCode(
		String draftWidgetInstanceExternalReferenceCode) {

		this.draftWidgetInstanceExternalReferenceCode =
			draftWidgetInstanceExternalReferenceCode;
	}

	public void setDraftWidgetInstanceExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			draftWidgetInstanceExternalReferenceCodeUnsafeSupplier) {

		try {
			draftWidgetInstanceExternalReferenceCode =
				draftWidgetInstanceExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String draftWidgetInstanceExternalReferenceCode;

	public FragmentStyle getFragmentStyle() {
		return fragmentStyle;
	}

	public void setFragmentStyle(FragmentStyle fragmentStyle) {
		this.fragmentStyle = fragmentStyle;
	}

	public void setFragmentStyle(
		UnsafeSupplier<FragmentStyle, Exception> fragmentStyleUnsafeSupplier) {

		try {
			fragmentStyle = fragmentStyleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentStyle fragmentStyle;

	public FragmentViewport[] getFragmentViewports() {
		return fragmentViewports;
	}

	public void setFragmentViewports(FragmentViewport[] fragmentViewports) {
		this.fragmentViewports = fragmentViewports;
	}

	public void setFragmentViewports(
		UnsafeSupplier<FragmentViewport[], Exception>
			fragmentViewportsUnsafeSupplier) {

		try {
			fragmentViewports = fragmentViewportsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentViewport[] fragmentViewports;

	public Boolean getIndexed() {
		return indexed;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;
	}

	public void setIndexed(
		UnsafeSupplier<Boolean, Exception> indexedUnsafeSupplier) {

		try {
			indexed = indexedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean indexed;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public WidgetInstance getWidgetInstance() {
		return widgetInstance;
	}

	public void setWidgetInstance(WidgetInstance widgetInstance) {
		this.widgetInstance = widgetInstance;
	}

	public void setWidgetInstance(
		UnsafeSupplier<WidgetInstance, Exception>
			widgetInstanceUnsafeSupplier) {

		try {
			widgetInstance = widgetInstanceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected WidgetInstance widgetInstance;

	public String getWidgetInstanceExternalReferenceCode() {
		return widgetInstanceExternalReferenceCode;
	}

	public void setWidgetInstanceExternalReferenceCode(
		String widgetInstanceExternalReferenceCode) {

		this.widgetInstanceExternalReferenceCode =
			widgetInstanceExternalReferenceCode;
	}

	public void setWidgetInstanceExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			widgetInstanceExternalReferenceCodeUnsafeSupplier) {

		try {
			widgetInstanceExternalReferenceCode =
				widgetInstanceExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String widgetInstanceExternalReferenceCode;

	@Override
	public WidgetInstancePageElementDefinition clone()
		throws CloneNotSupportedException {

		return (WidgetInstancePageElementDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetInstancePageElementDefinition)) {
			return false;
		}

		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition =
				(WidgetInstancePageElementDefinition)object;

		return Objects.equals(
			toString(), widgetInstancePageElementDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WidgetInstancePageElementDefinitionSerDes.toJSON(this);
	}

}