/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName("PreviewSite")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PreviewSite")
public class PreviewSite implements Serializable {

	public static PreviewSite toDTO(String json) {
		return ObjectMapperUtil.readValue(PreviewSite.class, json);
	}

	public static PreviewSite unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PreviewSite.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getChildSitesCount() {
		if (_childSitesCountSupplier != null) {
			childSitesCount = _childSitesCountSupplier.get();

			_childSitesCountSupplier = null;
		}

		return childSitesCount;
	}

	public void setChildSitesCount(Integer childSitesCount) {
		this.childSitesCount = childSitesCount;

		_childSitesCountSupplier = null;
	}

	@JsonIgnore
	public void setChildSitesCount(
		UnsafeSupplier<Integer, Exception> childSitesCountUnsafeSupplier) {

		_childSitesCountSupplier = () -> {
			try {
				return childSitesCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer childSitesCount;

	@JsonIgnore
	private Supplier<Integer> _childSitesCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDescriptiveName() {
		if (_descriptiveNameSupplier != null) {
			descriptiveName = _descriptiveNameSupplier.get();

			_descriptiveNameSupplier = null;
		}

		return descriptiveName;
	}

	public void setDescriptiveName(String descriptiveName) {
		this.descriptiveName = descriptiveName;

		_descriptiveNameSupplier = null;
	}

	@JsonIgnore
	public void setDescriptiveName(
		UnsafeSupplier<String, Exception> descriptiveNameUnsafeSupplier) {

		_descriptiveNameSupplier = () -> {
			try {
				return descriptiveNameUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String descriptiveName;

	@JsonIgnore
	private Supplier<String> _descriptiveNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the instance being imported into already has a site of the same external reference code. Set only when previewing a file to import."
	)
	public Boolean getExistsInInstance() {
		if (_existsInInstanceSupplier != null) {
			existsInInstance = _existsInInstanceSupplier.get();

			_existsInInstanceSupplier = null;
		}

		return existsInInstance;
	}

	public void setExistsInInstance(Boolean existsInInstance) {
		this.existsInInstance = existsInInstance;

		_existsInInstanceSupplier = null;
	}

	@JsonIgnore
	public void setExistsInInstance(
		UnsafeSupplier<Boolean, Exception> existsInInstanceUnsafeSupplier) {

		_existsInInstanceSupplier = () -> {
			try {
				return existsInInstanceUnsafeSupplier.get();
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
		description = "Whether the instance being imported into already has a site of the same external reference code. Set only when previewing a file to import."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean existsInInstance;

	@JsonIgnore
	private Supplier<Boolean> _existsInInstanceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Site's path in the site hierarchy."
	)
	public String getPath() {
		if (_pathSupplier != null) {
			path = _pathSupplier.get();

			_pathSupplier = null;
		}

		return path;
	}

	public void setPath(String path) {
		this.path = path;

		_pathSupplier = null;
	}

	@JsonIgnore
	public void setPath(UnsafeSupplier<String, Exception> pathUnsafeSupplier) {
		_pathSupplier = () -> {
			try {
				return pathUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Site's path in the site hierarchy.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String path;

	@JsonIgnore
	private Supplier<String> _pathSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PreviewSite)) {
			return false;
		}

		PreviewSite previewSite = (PreviewSite)object;

		return Objects.equals(toString(), previewSite.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Integer childSitesCount = getChildSitesCount();

		if (childSitesCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"childSitesCount\": ");

			sb.append(childSitesCount);
		}

		String descriptiveName = getDescriptiveName();

		if (descriptiveName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName\": ");

			sb.append("\"");

			sb.append(_escape(descriptiveName));

			sb.append("\"");
		}

		Boolean existsInInstance = getExistsInInstance();

		if (existsInInstance != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"existsInInstance\": ");

			sb.append(existsInInstance);
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		String path = getPath();

		if (path != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"path\": ");

			sb.append("\"");

			sb.append(_escape(path));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.exportimport.rest.dto.v1_0.PreviewSite",
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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-662573593