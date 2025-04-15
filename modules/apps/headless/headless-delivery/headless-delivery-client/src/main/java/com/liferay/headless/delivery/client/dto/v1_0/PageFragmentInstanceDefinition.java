/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.PageFragmentInstanceDefinitionSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PageFragmentInstanceDefinition implements Cloneable, Serializable {

	public static PageFragmentInstanceDefinition toDTO(String json) {
		return PageFragmentInstanceDefinitionSerDes.toDTO(json);
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

	public CustomCSSViewport[] getCustomCSSViewports() {
		return customCSSViewports;
	}

	public void setCustomCSSViewports(CustomCSSViewport[] customCSSViewports) {
		this.customCSSViewports = customCSSViewports;
	}

	public void setCustomCSSViewports(
		UnsafeSupplier<CustomCSSViewport[], Exception>
			customCSSViewportsUnsafeSupplier) {

		try {
			customCSSViewports = customCSSViewportsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CustomCSSViewport[] customCSSViewports;

	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

	public void setFragment(
		UnsafeSupplier<Fragment, Exception> fragmentUnsafeSupplier) {

		try {
			fragment = fragmentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Fragment fragment;

	public Map<String, Object> getFragmentConfig() {
		return fragmentConfig;
	}

	public void setFragmentConfig(Map<String, Object> fragmentConfig) {
		this.fragmentConfig = fragmentConfig;
	}

	public void setFragmentConfig(
		UnsafeSupplier<Map<String, Object>, Exception>
			fragmentConfigUnsafeSupplier) {

		try {
			fragmentConfig = fragmentConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> fragmentConfig;

	public FragmentField[] getFragmentFields() {
		return fragmentFields;
	}

	public void setFragmentFields(FragmentField[] fragmentFields) {
		this.fragmentFields = fragmentFields;
	}

	public void setFragmentFields(
		UnsafeSupplier<FragmentField[], Exception>
			fragmentFieldsUnsafeSupplier) {

		try {
			fragmentFields = fragmentFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentField[] fragmentFields;

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

	public WidgetInstance[] getWidgetInstances() {
		return widgetInstances;
	}

	public void setWidgetInstances(WidgetInstance[] widgetInstances) {
		this.widgetInstances = widgetInstances;
	}

	public void setWidgetInstances(
		UnsafeSupplier<WidgetInstance[], Exception>
			widgetInstancesUnsafeSupplier) {

		try {
			widgetInstances = widgetInstancesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected WidgetInstance[] widgetInstances;

	@Override
	public PageFragmentInstanceDefinition clone()
		throws CloneNotSupportedException {

		return (PageFragmentInstanceDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageFragmentInstanceDefinition)) {
			return false;
		}

		PageFragmentInstanceDefinition pageFragmentInstanceDefinition =
			(PageFragmentInstanceDefinition)object;

		return Objects.equals(
			toString(), pageFragmentInstanceDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageFragmentInstanceDefinitionSerDes.toJSON(this);
	}

}