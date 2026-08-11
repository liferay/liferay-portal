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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

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
@GraphQLName("PublishPreview")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PublishPreview")
public class PublishPreview implements Serializable {

	public static PublishPreview toDTO(String json) {
		return ObjectMapperUtil.readValue(PublishPreview.class, json);
	}

	public static PublishPreview unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PublishPreview.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getAdditionCount() {
		if (_additionCountSupplier != null) {
			additionCount = _additionCountSupplier.get();

			_additionCountSupplier = null;
		}

		return additionCount;
	}

	public void setAdditionCount(Long additionCount) {
		this.additionCount = additionCount;

		_additionCountSupplier = null;
	}

	@JsonIgnore
	public void setAdditionCount(
		UnsafeSupplier<Long, Exception> additionCountUnsafeSupplier) {

		_additionCountSupplier = () -> {
			try {
				return additionCountUnsafeSupplier.get();
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
	protected Long additionCount;

	@JsonIgnore
	private Supplier<Long> _additionCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getDeletionCount() {
		if (_deletionCountSupplier != null) {
			deletionCount = _deletionCountSupplier.get();

			_deletionCountSupplier = null;
		}

		return deletionCount;
	}

	public void setDeletionCount(Long deletionCount) {
		this.deletionCount = deletionCount;

		_deletionCountSupplier = null;
	}

	@JsonIgnore
	public void setDeletionCount(
		UnsafeSupplier<Long, Exception> deletionCountUnsafeSupplier) {

		_deletionCountSupplier = () -> {
			try {
				return deletionCountUnsafeSupplier.get();
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
	protected Long deletionCount;

	@JsonIgnore
	private Supplier<Long> _deletionCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public PreviewPortletDataHandlerSection[]
		getPreviewPortletDataHandlerSections() {

		if (_previewPortletDataHandlerSectionsSupplier != null) {
			previewPortletDataHandlerSections =
				_previewPortletDataHandlerSectionsSupplier.get();

			_previewPortletDataHandlerSectionsSupplier = null;
		}

		return previewPortletDataHandlerSections;
	}

	public void setPreviewPortletDataHandlerSections(
		PreviewPortletDataHandlerSection[] previewPortletDataHandlerSections) {

		this.previewPortletDataHandlerSections =
			previewPortletDataHandlerSections;

		_previewPortletDataHandlerSectionsSupplier = null;
	}

	@JsonIgnore
	public void setPreviewPortletDataHandlerSections(
		UnsafeSupplier<PreviewPortletDataHandlerSection[], Exception>
			previewPortletDataHandlerSectionsUnsafeSupplier) {

		_previewPortletDataHandlerSectionsSupplier = () -> {
			try {
				return previewPortletDataHandlerSectionsUnsafeSupplier.get();
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
	protected PreviewPortletDataHandlerSection[]
		previewPortletDataHandlerSections;

	@JsonIgnore
	private Supplier<PreviewPortletDataHandlerSection[]>
		_previewPortletDataHandlerSectionsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PublishPreview)) {
			return false;
		}

		PublishPreview publishPreview = (PublishPreview)object;

		return Objects.equals(toString(), publishPreview.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long additionCount = getAdditionCount();

		if (additionCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionCount\": ");

			sb.append(additionCount);
		}

		Long deletionCount = getDeletionCount();

		if (deletionCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionCount\": ");

			sb.append(deletionCount);
		}

		PreviewPortletDataHandlerSection[] previewPortletDataHandlerSections =
			getPreviewPortletDataHandlerSections();

		if (previewPortletDataHandlerSections != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previewPortletDataHandlerSections\": ");

			sb.append("[");

			for (int i = 0; i < previewPortletDataHandlerSections.length; i++) {
				sb.append(String.valueOf(previewPortletDataHandlerSections[i]));

				if ((i + 1) < previewPortletDataHandlerSections.length) {
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
		defaultValue = "com.liferay.exportimport.rest.dto.v1_0.PublishPreview",
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
// LIFERAY-REST-BUILDER-HASH:464014803