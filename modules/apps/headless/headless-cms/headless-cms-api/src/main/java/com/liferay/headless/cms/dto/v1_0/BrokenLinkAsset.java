/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
@GraphQLName("BrokenLinkAsset")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "BrokenLinkAsset")
public class BrokenLinkAsset implements Serializable {

	public static BrokenLinkAsset toDTO(String json) {
		return ObjectMapperUtil.readValue(BrokenLinkAsset.class, json);
	}

	public static BrokenLinkAsset unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(BrokenLinkAsset.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getBrokenLinkTitle() {
		if (_brokenLinkTitleSupplier != null) {
			brokenLinkTitle = _brokenLinkTitleSupplier.get();

			_brokenLinkTitleSupplier = null;
		}

		return brokenLinkTitle;
	}

	public void setBrokenLinkTitle(String brokenLinkTitle) {
		this.brokenLinkTitle = brokenLinkTitle;

		_brokenLinkTitleSupplier = null;
	}

	@JsonIgnore
	public void setBrokenLinkTitle(
		UnsafeSupplier<String, Exception> brokenLinkTitleUnsafeSupplier) {

		_brokenLinkTitleSupplier = () -> {
			try {
				return brokenLinkTitleUnsafeSupplier.get();
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
	protected String brokenLinkTitle;

	@JsonIgnore
	private Supplier<String> _brokenLinkTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getBrokenLinksCount() {
		if (_brokenLinksCountSupplier != null) {
			brokenLinksCount = _brokenLinksCountSupplier.get();

			_brokenLinksCountSupplier = null;
		}

		return brokenLinksCount;
	}

	public void setBrokenLinksCount(Long brokenLinksCount) {
		this.brokenLinksCount = brokenLinksCount;

		_brokenLinksCountSupplier = null;
	}

	@JsonIgnore
	public void setBrokenLinksCount(
		UnsafeSupplier<Long, Exception> brokenLinksCountUnsafeSupplier) {

		_brokenLinksCountSupplier = () -> {
			try {
				return brokenLinksCountUnsafeSupplier.get();
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
	protected Long brokenLinksCount;

	@JsonIgnore
	private Supplier<Long> _brokenLinksCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getHref() {
		if (_hrefSupplier != null) {
			href = _hrefSupplier.get();

			_hrefSupplier = null;
		}

		return href;
	}

	public void setHref(String href) {
		this.href = href;

		_hrefSupplier = null;
	}

	@JsonIgnore
	public void setHref(UnsafeSupplier<String, Exception> hrefUnsafeSupplier) {
		_hrefSupplier = () -> {
			try {
				return hrefUnsafeSupplier.get();
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
	protected String href;

	@JsonIgnore
	private Supplier<String> _hrefSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
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
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectDefinitionExternalReferenceCode() {
		if (_objectDefinitionExternalReferenceCodeSupplier != null) {
			objectDefinitionExternalReferenceCode =
				_objectDefinitionExternalReferenceCodeSupplier.get();

			_objectDefinitionExternalReferenceCodeSupplier = null;
		}

		return objectDefinitionExternalReferenceCode;
	}

	public void setObjectDefinitionExternalReferenceCode(
		String objectDefinitionExternalReferenceCode) {

		this.objectDefinitionExternalReferenceCode =
			objectDefinitionExternalReferenceCode;

		_objectDefinitionExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCodeUnsafeSupplier) {

		_objectDefinitionExternalReferenceCodeSupplier = () -> {
			try {
				return objectDefinitionExternalReferenceCodeUnsafeSupplier.
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String objectDefinitionExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _objectDefinitionExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTitle() {
		if (_titleSupplier != null) {
			title = _titleSupplier.get();

			_titleSupplier = null;
		}

		return title;
	}

	public void setTitle(String title) {
		this.title = title;

		_titleSupplier = null;
	}

	@JsonIgnore
	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		_titleSupplier = () -> {
			try {
				return titleUnsafeSupplier.get();
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
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BrokenLinkAsset)) {
			return false;
		}

		BrokenLinkAsset brokenLinkAsset = (BrokenLinkAsset)object;

		return Objects.equals(toString(), brokenLinkAsset.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String brokenLinkTitle = getBrokenLinkTitle();

		if (brokenLinkTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"brokenLinkTitle\": ");

			sb.append("\"");

			sb.append(_escape(brokenLinkTitle));

			sb.append("\"");
		}

		Long brokenLinksCount = getBrokenLinksCount();

		if (brokenLinksCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"brokenLinksCount\": ");

			sb.append(brokenLinksCount);
		}

		String href = getHref();

		if (href != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"href\": ");

			sb.append("\"");

			sb.append(_escape(href));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String objectDefinitionExternalReferenceCode =
			getObjectDefinitionExternalReferenceCode();

		if (objectDefinitionExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectDefinitionExternalReferenceCode));

			sb.append("\"");
		}

		String title = getTitle();

		if (title != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(title));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.cms.dto.v1_0.BrokenLinkAsset",
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
// LIFERAY-REST-BUILDER-HASH:581079945