/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentInstancePageElementDefinitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FragmentInstancePageElementDefinition
	extends PageElementDefinition implements Cloneable, Serializable {

	public static FragmentInstancePageElementDefinition toDTO(String json) {
		return FragmentInstancePageElementDefinitionSerDes.toDTO(json);
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public void setConfiguration(
		UnsafeSupplier<String, Exception> configurationUnsafeSupplier) {

		try {
			configuration = configurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String configuration;

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public void setCss(UnsafeSupplier<String, Exception> cssUnsafeSupplier) {
		try {
			css = cssUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String css;

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

	public Date getDatePropagated() {
		return datePropagated;
	}

	public void setDatePropagated(Date datePropagated) {
		this.datePropagated = datePropagated;
	}

	public void setDatePropagated(
		UnsafeSupplier<Date, Exception> datePropagatedUnsafeSupplier) {

		try {
			datePropagated = datePropagatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date datePropagated;

	public String getDraftFragmentInstanceExternalReferenceCode() {
		return draftFragmentInstanceExternalReferenceCode;
	}

	public void setDraftFragmentInstanceExternalReferenceCode(
		String draftFragmentInstanceExternalReferenceCode) {

		this.draftFragmentInstanceExternalReferenceCode =
			draftFragmentInstanceExternalReferenceCode;
	}

	public void setDraftFragmentInstanceExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			draftFragmentInstanceExternalReferenceCodeUnsafeSupplier) {

		try {
			draftFragmentInstanceExternalReferenceCode =
				draftFragmentInstanceExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String draftFragmentInstanceExternalReferenceCode;

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

	public String getFragmentInstanceExternalReferenceCode() {
		return fragmentInstanceExternalReferenceCode;
	}

	public void setFragmentInstanceExternalReferenceCode(
		String fragmentInstanceExternalReferenceCode) {

		this.fragmentInstanceExternalReferenceCode =
			fragmentInstanceExternalReferenceCode;
	}

	public void setFragmentInstanceExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			fragmentInstanceExternalReferenceCodeUnsafeSupplier) {

		try {
			fragmentInstanceExternalReferenceCode =
				fragmentInstanceExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fragmentInstanceExternalReferenceCode;

	public FragmentReference getFragmentReference() {
		return fragmentReference;
	}

	public void setFragmentReference(FragmentReference fragmentReference) {
		this.fragmentReference = fragmentReference;
	}

	public void setFragmentReference(
		UnsafeSupplier<FragmentReference, Exception>
			fragmentReferenceUnsafeSupplier) {

		try {
			fragmentReference = fragmentReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentReference fragmentReference;

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

	public FragmentType getFragmentType() {
		return fragmentType;
	}

	public String getFragmentTypeAsString() {
		if (fragmentType == null) {
			return null;
		}

		return fragmentType.toString();
	}

	public void setFragmentType(FragmentType fragmentType) {
		this.fragmentType = fragmentType;
	}

	public void setFragmentType(
		UnsafeSupplier<FragmentType, Exception> fragmentTypeUnsafeSupplier) {

		try {
			fragmentType = fragmentTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentType fragmentType;

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

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public void setHtml(UnsafeSupplier<String, Exception> htmlUnsafeSupplier) {
		try {
			html = htmlUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String html;

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

	public String getJs() {
		return js;
	}

	public void setJs(String js) {
		this.js = js;
	}

	public void setJs(UnsafeSupplier<String, Exception> jsUnsafeSupplier) {
		try {
			js = jsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String js;

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

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setNamespace(
		UnsafeSupplier<String, Exception> namespaceUnsafeSupplier) {

		try {
			namespace = namespaceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String namespace;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setUuid(UnsafeSupplier<String, Exception> uuidUnsafeSupplier) {
		try {
			uuid = uuidUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String uuid;

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
	public FragmentInstancePageElementDefinition clone()
		throws CloneNotSupportedException {

		return (FragmentInstancePageElementDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentInstancePageElementDefinition)) {
			return false;
		}

		FragmentInstancePageElementDefinition
			fragmentInstancePageElementDefinition =
				(FragmentInstancePageElementDefinition)object;

		return Objects.equals(
			toString(), fragmentInstancePageElementDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentInstancePageElementDefinitionSerDes.toJSON(this);
	}

	public static enum FragmentType {

		BASIC("Basic"), FORM("Form");

		public static FragmentType create(String value) {
			for (FragmentType fragmentType : values()) {
				if (Objects.equals(fragmentType.getValue(), value) ||
					Objects.equals(fragmentType.name(), value)) {

					return fragmentType;
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

		private FragmentType(String value) {
			_value = value;
		}

		private final String _value;

	}

}