/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Specific settings related to the display page",
	value = "DisplayPageTemplateSettings"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DisplayPageTemplateSettings")
public class DisplayPageTemplateSettings implements Serializable {

	public static DisplayPageTemplateSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(
			DisplayPageTemplateSettings.class, json);
	}

	public static DisplayPageTemplateSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			DisplayPageTemplateSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ContentAssociation getContentAssociation() {
		if (_contentAssociationSupplier != null) {
			contentAssociation = _contentAssociationSupplier.get();

			_contentAssociationSupplier = null;
		}

		return contentAssociation;
	}

	public void setContentAssociation(ContentAssociation contentAssociation) {
		this.contentAssociation = contentAssociation;

		_contentAssociationSupplier = null;
	}

	@JsonIgnore
	public void setContentAssociation(
		UnsafeSupplier<ContentAssociation, Exception>
			contentAssociationUnsafeSupplier) {

		_contentAssociationSupplier = () -> {
			try {
				return contentAssociationUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ContentAssociation contentAssociation;

	@JsonIgnore
	private Supplier<ContentAssociation> _contentAssociationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public OpenGraphSettingsMapping getOpenGraphSettingsMapping() {
		if (_openGraphSettingsMappingSupplier != null) {
			openGraphSettingsMapping = _openGraphSettingsMappingSupplier.get();

			_openGraphSettingsMappingSupplier = null;
		}

		return openGraphSettingsMapping;
	}

	public void setOpenGraphSettingsMapping(
		OpenGraphSettingsMapping openGraphSettingsMapping) {

		this.openGraphSettingsMapping = openGraphSettingsMapping;

		_openGraphSettingsMappingSupplier = null;
	}

	@JsonIgnore
	public void setOpenGraphSettingsMapping(
		UnsafeSupplier<OpenGraphSettingsMapping, Exception>
			openGraphSettingsMappingUnsafeSupplier) {

		_openGraphSettingsMappingSupplier = () -> {
			try {
				return openGraphSettingsMappingUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected OpenGraphSettingsMapping openGraphSettingsMapping;

	@JsonIgnore
	private Supplier<OpenGraphSettingsMapping>
		_openGraphSettingsMappingSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SEOSettingsMapping getSeoSettingsMapping() {
		if (_seoSettingsMappingSupplier != null) {
			seoSettingsMapping = _seoSettingsMappingSupplier.get();

			_seoSettingsMappingSupplier = null;
		}

		return seoSettingsMapping;
	}

	public void setSeoSettingsMapping(SEOSettingsMapping seoSettingsMapping) {
		this.seoSettingsMapping = seoSettingsMapping;

		_seoSettingsMappingSupplier = null;
	}

	@JsonIgnore
	public void setSeoSettingsMapping(
		UnsafeSupplier<SEOSettingsMapping, Exception>
			seoSettingsMappingUnsafeSupplier) {

		_seoSettingsMappingSupplier = () -> {
			try {
				return seoSettingsMappingUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SEOSettingsMapping seoSettingsMapping;

	@JsonIgnore
	private Supplier<SEOSettingsMapping> _seoSettingsMappingSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DisplayPageTemplateSettings)) {
			return false;
		}

		DisplayPageTemplateSettings displayPageTemplateSettings =
			(DisplayPageTemplateSettings)object;

		return Objects.equals(
			toString(), displayPageTemplateSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		ContentAssociation contentAssociation = getContentAssociation();

		if (contentAssociation != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentAssociation\": ");

			sb.append(String.valueOf(contentAssociation));
		}

		OpenGraphSettingsMapping openGraphSettingsMapping =
			getOpenGraphSettingsMapping();

		if (openGraphSettingsMapping != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"openGraphSettingsMapping\": ");

			sb.append(String.valueOf(openGraphSettingsMapping));
		}

		SEOSettingsMapping seoSettingsMapping = getSeoSettingsMapping();

		if (seoSettingsMapping != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seoSettingsMapping\": ");

			sb.append(String.valueOf(seoSettingsMapping));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.content.dto.v1_0.DisplayPageTemplateSettings",
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