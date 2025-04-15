/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A definition of a page fragment instance.",
	value = "PageFragmentInstanceDefinition"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageFragmentInstanceDefinition")
public class PageFragmentInstanceDefinition implements Serializable {

	public static PageFragmentInstanceDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(
			PageFragmentInstanceDefinition.class, json);
	}

	public static PageFragmentInstanceDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PageFragmentInstanceDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of CSS classes that are applied to the fragment instance."
	)
	public String[] getCssClasses() {
		if (_cssClassesSupplier != null) {
			cssClasses = _cssClassesSupplier.get();

			_cssClassesSupplier = null;
		}

		return cssClasses;
	}

	public void setCssClasses(String[] cssClasses) {
		this.cssClasses = cssClasses;

		_cssClassesSupplier = null;
	}

	@JsonIgnore
	public void setCssClasses(
		UnsafeSupplier<String[], Exception> cssClassesUnsafeSupplier) {

		_cssClassesSupplier = () -> {
			try {
				return cssClassesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A list of CSS classes that are applied to the fragment instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] cssClasses;

	@JsonIgnore
	private Supplier<String[]> _cssClassesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Custom CSS that is applied on the fragment instance."
	)
	public String getCustomCSS() {
		if (_customCSSSupplier != null) {
			customCSS = _customCSSSupplier.get();

			_customCSSSupplier = null;
		}

		return customCSS;
	}

	public void setCustomCSS(String customCSS) {
		this.customCSS = customCSS;

		_customCSSSupplier = null;
	}

	@JsonIgnore
	public void setCustomCSS(
		UnsafeSupplier<String, Exception> customCSSUnsafeSupplier) {

		_customCSSSupplier = () -> {
			try {
				return customCSSUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Custom CSS that is applied on the fragment instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String customCSS;

	@JsonIgnore
	private Supplier<String> _customCSSSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The custom CSS viewports of the fragment instance."
	)
	@Valid
	public CustomCSSViewport[] getCustomCSSViewports() {
		if (_customCSSViewportsSupplier != null) {
			customCSSViewports = _customCSSViewportsSupplier.get();

			_customCSSViewportsSupplier = null;
		}

		return customCSSViewports;
	}

	public void setCustomCSSViewports(CustomCSSViewport[] customCSSViewports) {
		this.customCSSViewports = customCSSViewports;

		_customCSSViewportsSupplier = null;
	}

	@JsonIgnore
	public void setCustomCSSViewports(
		UnsafeSupplier<CustomCSSViewport[], Exception>
			customCSSViewportsUnsafeSupplier) {

		_customCSSViewportsSupplier = () -> {
			try {
				return customCSSViewportsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The custom CSS viewports of the fragment instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected CustomCSSViewport[] customCSSViewports;

	@JsonIgnore
	private Supplier<CustomCSSViewport[]> _customCSSViewportsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment instance's most recent propagation date."
	)
	public Date getDatePropagated() {
		if (_datePropagatedSupplier != null) {
			datePropagated = _datePropagatedSupplier.get();

			_datePropagatedSupplier = null;
		}

		return datePropagated;
	}

	public void setDatePropagated(Date datePropagated) {
		this.datePropagated = datePropagated;

		_datePropagatedSupplier = null;
	}

	@JsonIgnore
	public void setDatePropagated(
		UnsafeSupplier<Date, Exception> datePropagatedUnsafeSupplier) {

		_datePropagatedSupplier = () -> {
			try {
				return datePropagatedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The fragment instance's most recent propagation date."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date datePropagated;

	@JsonIgnore
	private Supplier<Date> _datePropagatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment instance's most recent publication date."
	)
	public Date getDatePublished() {
		if (_datePublishedSupplier != null) {
			datePublished = _datePublishedSupplier.get();

			_datePublishedSupplier = null;
		}

		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;

		_datePublishedSupplier = null;
	}

	@JsonIgnore
	public void setDatePublished(
		UnsafeSupplier<Date, Exception> datePublishedUnsafeSupplier) {

		_datePublishedSupplier = () -> {
			try {
				return datePublishedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The fragment instance's most recent publication date."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date datePublished;

	@JsonIgnore
	private Supplier<Date> _datePublishedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external reference code of the corresponding page element in the draft of the page. Available only in the published page specification."
	)
	public String getDraftPageElementExternalReferenceCode() {
		if (_draftPageElementExternalReferenceCodeSupplier != null) {
			draftPageElementExternalReferenceCode =
				_draftPageElementExternalReferenceCodeSupplier.get();

			_draftPageElementExternalReferenceCodeSupplier = null;
		}

		return draftPageElementExternalReferenceCode;
	}

	public void setDraftPageElementExternalReferenceCode(
		String draftPageElementExternalReferenceCode) {

		this.draftPageElementExternalReferenceCode =
			draftPageElementExternalReferenceCode;

		_draftPageElementExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setDraftPageElementExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			draftPageElementExternalReferenceCodeUnsafeSupplier) {

		_draftPageElementExternalReferenceCodeSupplier = () -> {
			try {
				return draftPageElementExternalReferenceCodeUnsafeSupplier.
					get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The external reference code of the corresponding page element in the draft of the page. Available only in the published page specification."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String draftPageElementExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _draftPageElementExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The configuration values of the fragment instance."
	)
	@Valid
	public Map<String, Object> getFragmentConfig() {
		if (_fragmentConfigSupplier != null) {
			fragmentConfig = _fragmentConfigSupplier.get();

			_fragmentConfigSupplier = null;
		}

		return fragmentConfig;
	}

	public void setFragmentConfig(Map<String, Object> fragmentConfig) {
		this.fragmentConfig = fragmentConfig;

		_fragmentConfigSupplier = null;
	}

	@JsonIgnore
	public void setFragmentConfig(
		UnsafeSupplier<Map<String, Object>, Exception>
			fragmentConfigUnsafeSupplier) {

		_fragmentConfigSupplier = () -> {
			try {
				return fragmentConfigUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The configuration values of the fragment instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, Object> fragmentConfig;

	@JsonIgnore
	private Supplier<Map<String, Object>> _fragmentConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment field values of the the fragment instance."
	)
	@Valid
	public FragmentField[] getFragmentFields() {
		if (_fragmentFieldsSupplier != null) {
			fragmentFields = _fragmentFieldsSupplier.get();

			_fragmentFieldsSupplier = null;
		}

		return fragmentFields;
	}

	public void setFragmentFields(FragmentField[] fragmentFields) {
		this.fragmentFields = fragmentFields;

		_fragmentFieldsSupplier = null;
	}

	@JsonIgnore
	public void setFragmentFields(
		UnsafeSupplier<FragmentField[], Exception>
			fragmentFieldsUnsafeSupplier) {

		_fragmentFieldsSupplier = () -> {
			try {
				return fragmentFieldsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The fragment field values of the the fragment instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentField[] fragmentFields;

	@JsonIgnore
	private Supplier<FragmentField[]> _fragmentFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "An external reference to the fragment."
	)
	@Valid
	public Object getFragmentReference() {
		if (_fragmentReferenceSupplier != null) {
			fragmentReference = _fragmentReferenceSupplier.get();

			_fragmentReferenceSupplier = null;
		}

		return fragmentReference;
	}

	public void setFragmentReference(Object fragmentReference) {
		this.fragmentReference = fragmentReference;

		_fragmentReferenceSupplier = null;
	}

	@JsonIgnore
	public void setFragmentReference(
		UnsafeSupplier<Object, Exception> fragmentReferenceUnsafeSupplier) {

		_fragmentReferenceSupplier = () -> {
			try {
				return fragmentReferenceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "An external reference to the fragment.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object fragmentReference;

	@JsonIgnore
	private Supplier<Object> _fragmentReferenceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment style of the page fragment instance."
	)
	@Valid
	public FragmentStyle getFragmentStyle() {
		if (_fragmentStyleSupplier != null) {
			fragmentStyle = _fragmentStyleSupplier.get();

			_fragmentStyleSupplier = null;
		}

		return fragmentStyle;
	}

	public void setFragmentStyle(FragmentStyle fragmentStyle) {
		this.fragmentStyle = fragmentStyle;

		_fragmentStyleSupplier = null;
	}

	@JsonIgnore
	public void setFragmentStyle(
		UnsafeSupplier<FragmentStyle, Exception> fragmentStyleUnsafeSupplier) {

		_fragmentStyleSupplier = () -> {
			try {
				return fragmentStyleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The fragment style of the page fragment instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentStyle fragmentStyle;

	@JsonIgnore
	private Supplier<FragmentStyle> _fragmentStyleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of fragment viewports of the page fragment instance."
	)
	@Valid
	public FragmentViewport[] getFragmentViewports() {
		if (_fragmentViewportsSupplier != null) {
			fragmentViewports = _fragmentViewportsSupplier.get();

			_fragmentViewportsSupplier = null;
		}

		return fragmentViewports;
	}

	public void setFragmentViewports(FragmentViewport[] fragmentViewports) {
		this.fragmentViewports = fragmentViewports;

		_fragmentViewportsSupplier = null;
	}

	@JsonIgnore
	public void setFragmentViewports(
		UnsafeSupplier<FragmentViewport[], Exception>
			fragmentViewportsUnsafeSupplier) {

		_fragmentViewportsSupplier = () -> {
			try {
				return fragmentViewportsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A list of fragment viewports of the page fragment instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentViewport[] fragmentViewports;

	@JsonIgnore
	private Supplier<FragmentViewport[]> _fragmentViewportsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the page fragment instance is indexed or not."
	)
	public Boolean getIndexed() {
		if (_indexedSupplier != null) {
			indexed = _indexedSupplier.get();

			_indexedSupplier = null;
		}

		return indexed;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;

		_indexedSupplier = null;
	}

	@JsonIgnore
	public void setIndexed(
		UnsafeSupplier<Boolean, Exception> indexedUnsafeSupplier) {

		_indexedSupplier = () -> {
			try {
				return indexedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A flag that indicates whether the page fragment instance is indexed or not."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean indexed;

	@JsonIgnore
	private Supplier<Boolean> _indexedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The custom name of a fragment instance."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The custom name of a fragment instance.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment instance's namespace."
	)
	public String getNamespace() {
		if (_namespaceSupplier != null) {
			namespace = _namespaceSupplier.get();

			_namespaceSupplier = null;
		}

		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;

		_namespaceSupplier = null;
	}

	@JsonIgnore
	public void setNamespace(
		UnsafeSupplier<String, Exception> namespaceUnsafeSupplier) {

		_namespaceSupplier = () -> {
			try {
				return namespaceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment instance's namespace.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String namespace;

	@JsonIgnore
	private Supplier<String> _namespaceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A valid external identifier to reference this fragment instance."
	)
	public String getUuid() {
		if (_uuidSupplier != null) {
			uuid = _uuidSupplier.get();

			_uuidSupplier = null;
		}

		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;

		_uuidSupplier = null;
	}

	@JsonIgnore
	public void setUuid(UnsafeSupplier<String, Exception> uuidUnsafeSupplier) {
		_uuidSupplier = () -> {
			try {
				return uuidUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A valid external identifier to reference this fragment instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String uuid;

	@JsonIgnore
	private Supplier<String> _uuidSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of widget instances within the fragment instance."
	)
	@Valid
	public WidgetInstance[] getWidgetInstances() {
		if (_widgetInstancesSupplier != null) {
			widgetInstances = _widgetInstancesSupplier.get();

			_widgetInstancesSupplier = null;
		}

		return widgetInstances;
	}

	public void setWidgetInstances(WidgetInstance[] widgetInstances) {
		this.widgetInstances = widgetInstances;

		_widgetInstancesSupplier = null;
	}

	@JsonIgnore
	public void setWidgetInstances(
		UnsafeSupplier<WidgetInstance[], Exception>
			widgetInstancesUnsafeSupplier) {

		_widgetInstancesSupplier = () -> {
			try {
				return widgetInstancesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A list of widget instances within the fragment instance."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected WidgetInstance[] widgetInstances;

	@JsonIgnore
	private Supplier<WidgetInstance[]> _widgetInstancesSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		String[] cssClasses = getCssClasses();

		if (cssClasses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0; i < cssClasses.length; i++) {
				sb.append("\"");

				sb.append(_escape(cssClasses[i]));

				sb.append("\"");

				if ((i + 1) < cssClasses.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String customCSS = getCustomCSS();

		if (customCSS != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(customCSS));

			sb.append("\"");
		}

		CustomCSSViewport[] customCSSViewports = getCustomCSSViewports();

		if (customCSSViewports != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSViewports\": ");

			sb.append("[");

			for (int i = 0; i < customCSSViewports.length; i++) {
				sb.append(String.valueOf(customCSSViewports[i]));

				if ((i + 1) < customCSSViewports.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Date datePropagated = getDatePropagated();

		if (datePropagated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePropagated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(datePropagated));

			sb.append("\"");
		}

		Date datePublished = getDatePublished();

		if (datePublished != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(datePublished));

			sb.append("\"");
		}

		String draftPageElementExternalReferenceCode =
			getDraftPageElementExternalReferenceCode();

		if (draftPageElementExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"draftPageElementExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(draftPageElementExternalReferenceCode));

			sb.append("\"");
		}

		Map<String, Object> fragmentConfig = getFragmentConfig();

		if (fragmentConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentConfig\": ");

			sb.append(_toJSON(fragmentConfig));
		}

		FragmentField[] fragmentFields = getFragmentFields();

		if (fragmentFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentFields\": ");

			sb.append("[");

			for (int i = 0; i < fragmentFields.length; i++) {
				sb.append(String.valueOf(fragmentFields[i]));

				if ((i + 1) < fragmentFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Object fragmentReference = getFragmentReference();

		if (fragmentReference != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentReference\": ");

			if (fragmentReference instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)fragmentReference));
			}
			else if (fragmentReference instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)fragmentReference));
				sb.append("\"");
			}
			else {
				sb.append(fragmentReference);
			}
		}

		FragmentStyle fragmentStyle = getFragmentStyle();

		if (fragmentStyle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(String.valueOf(fragmentStyle));
		}

		FragmentViewport[] fragmentViewports = getFragmentViewports();

		if (fragmentViewports != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0; i < fragmentViewports.length; i++) {
				sb.append(String.valueOf(fragmentViewports[i]));

				if ((i + 1) < fragmentViewports.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean indexed = getIndexed();

		if (indexed != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(indexed);
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		String namespace = getNamespace();

		if (namespace != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"namespace\": ");

			sb.append("\"");

			sb.append(_escape(namespace));

			sb.append("\"");
		}

		String uuid = getUuid();

		if (uuid != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(uuid));

			sb.append("\"");
		}

		WidgetInstance[] widgetInstances = getWidgetInstances();

		if (widgetInstances != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstances\": ");

			sb.append("[");

			for (int i = 0; i < widgetInstances.length; i++) {
				sb.append(String.valueOf(widgetInstances[i]));

				if ((i + 1) < widgetInstances.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.PageFragmentInstanceDefinition",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}