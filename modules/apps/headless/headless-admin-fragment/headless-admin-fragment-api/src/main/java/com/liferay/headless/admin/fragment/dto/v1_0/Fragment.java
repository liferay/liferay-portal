/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.headless.admin.site.dto.v1_0.ThumbnailURLReference;
import com.liferay.headless.admin.user.dto.v1_0.Creator;
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
@GraphQLName(description = "A fragment entry.", value = "Fragment")
@io.swagger.v3.oas.annotations.media.Schema(description = "A fragment entry.")
@JsonFilter("Liferay.Vulcan")
@JsonSubTypes(
	{
		@JsonSubTypes.Type(name = "BasicFragment", value = BasicFragment.class),
		@JsonSubTypes.Type(name = "FormFragment", value = FormFragment.class)
	}
)
@JsonTypeInfo(
	include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type",
	use = JsonTypeInfo.Id.NAME, visible = true
)
@XmlRootElement(name = "Fragment")
public abstract class Fragment implements Serializable {

	public static Fragment toDTO(String json) {
		return ObjectMapperUtil.readValue(Fragment.class, json);
	}

	public static Fragment unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Fragment.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the fragment output is cacheable."
	)
	public Boolean getCacheable() {
		if (_cacheableSupplier != null) {
			cacheable = _cacheableSupplier.get();

			_cacheableSupplier = null;
		}

		return cacheable;
	}

	public void setCacheable(Boolean cacheable) {
		this.cacheable = cacheable;

		_cacheableSupplier = null;
	}

	@JsonIgnore
	public void setCacheable(
		UnsafeSupplier<Boolean, Exception> cacheableUnsafeSupplier) {

		_cacheableSupplier = () -> {
			try {
				return cacheableUnsafeSupplier.get();
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
		description = "A flag that indicates whether the fragment output is cacheable."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean cacheable;

	@JsonIgnore
	private Supplier<Boolean> _cacheableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's creator."
	)
	@Valid
	public Creator getCreator() {
		if (_creatorSupplier != null) {
			creator = _creatorSupplier.get();

			_creatorSupplier = null;
		}

		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;

		_creatorSupplier = null;
	}

	@JsonIgnore
	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		_creatorSupplier = () -> {
			try {
				return creatorUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's creator.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's creation date."
	)
	public Date getDateCreated() {
		if (_dateCreatedSupplier != null) {
			dateCreated = _dateCreatedSupplier.get();

			_dateCreatedSupplier = null;
		}

		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;

		_dateCreatedSupplier = null;
	}

	@JsonIgnore
	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		_dateCreatedSupplier = () -> {
			try {
				return dateCreatedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time the fragment changed."
	)
	public Date getDateModified() {
		if (_dateModifiedSupplier != null) {
			dateModified = _dateModifiedSupplier.get();

			_dateModifiedSupplier = null;
		}

		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;

		_dateModifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		_dateModifiedSupplier = () -> {
			try {
				return dateModifiedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The last time the fragment changed.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's external reference code."
	)
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

	@GraphQLField(description = "The fragment's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's set."
	)
	@Valid
	public FragmentSet getFragmentSet() {
		if (_fragmentSetSupplier != null) {
			fragmentSet = _fragmentSetSupplier.get();

			_fragmentSetSupplier = null;
		}

		return fragmentSet;
	}

	public void setFragmentSet(FragmentSet fragmentSet) {
		this.fragmentSet = fragmentSet;

		_fragmentSetSupplier = null;
	}

	@JsonIgnore
	public void setFragmentSet(
		UnsafeSupplier<FragmentSet, Exception> fragmentSetUnsafeSupplier) {

		_fragmentSetSupplier = () -> {
			try {
				return fragmentSetUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's set.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentSet fragmentSet;

	@JsonIgnore
	private Supplier<FragmentSet> _fragmentSetSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's versions. A fragment can have up to 2 versions, one draft and one published (approved)."
	)
	@Valid
	public FragmentVersion[] getFragmentVersions() {
		if (_fragmentVersionsSupplier != null) {
			fragmentVersions = _fragmentVersionsSupplier.get();

			_fragmentVersionsSupplier = null;
		}

		return fragmentVersions;
	}

	public void setFragmentVersions(FragmentVersion[] fragmentVersions) {
		this.fragmentVersions = fragmentVersions;

		_fragmentVersionsSupplier = null;
	}

	@JsonIgnore
	public void setFragmentVersions(
		UnsafeSupplier<FragmentVersion[], Exception>
			fragmentVersionsUnsafeSupplier) {

		_fragmentVersionsSupplier = () -> {
			try {
				return fragmentVersionsUnsafeSupplier.get();
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
		description = "The fragment's versions. A fragment can have up to 2 versions, one draft and one published (approved)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentVersion[] fragmentVersions;

	@JsonIgnore
	private Supplier<FragmentVersion[]> _fragmentVersionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's icon identifier."
	)
	public String getIcon() {
		if (_iconSupplier != null) {
			icon = _iconSupplier.get();

			_iconSupplier = null;
		}

		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;

		_iconSupplier = null;
	}

	@JsonIgnore
	public void setIcon(UnsafeSupplier<String, Exception> iconUnsafeSupplier) {
		_iconSupplier = () -> {
			try {
				return iconUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's icon identifier.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String icon;

	@JsonIgnore
	private Supplier<String> _iconSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's unique key within the group."
	)
	public String getKey() {
		if (_keySupplier != null) {
			key = _keySupplier.get();

			_keySupplier = null;
		}

		return key;
	}

	public void setKey(String key) {
		this.key = key;

		_keySupplier = null;
	}

	@JsonIgnore
	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		_keySupplier = () -> {
			try {
				return keyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's unique key within the group.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String key;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the fragment is from the marketplace."
	)
	public Boolean getMarketplace() {
		if (_marketplaceSupplier != null) {
			marketplace = _marketplaceSupplier.get();

			_marketplaceSupplier = null;
		}

		return marketplace;
	}

	public void setMarketplace(Boolean marketplace) {
		this.marketplace = marketplace;

		_marketplaceSupplier = null;
	}

	@JsonIgnore
	public void setMarketplace(
		UnsafeSupplier<Boolean, Exception> marketplaceUnsafeSupplier) {

		_marketplaceSupplier = () -> {
			try {
				return marketplaceUnsafeSupplier.get();
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
		description = "A flag that indicates whether the fragment is from the marketplace."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean marketplace;

	@JsonIgnore
	private Supplier<Boolean> _marketplaceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's name."
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

	@GraphQLField(description = "The fragment's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the fragment is read-only."
	)
	public Boolean getReadOnly() {
		if (_readOnlySupplier != null) {
			readOnly = _readOnlySupplier.get();

			_readOnlySupplier = null;
		}

		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;

		_readOnlySupplier = null;
	}

	@JsonIgnore
	public void setReadOnly(
		UnsafeSupplier<Boolean, Exception> readOnlyUnsafeSupplier) {

		_readOnlySupplier = () -> {
			try {
				return readOnlyUnsafeSupplier.get();
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
		description = "A flag that indicates whether the fragment is read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean readOnly;

	@JsonIgnore
	private Supplier<Boolean> _readOnlySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's thumbnail. On read, returned only when `nestedFields=thumbnailURLReference` is requested. On POST or PUT, omitting `thumbnailURLReference` clears any previously bound preview."
	)
	@Valid
	public ThumbnailURLReference getThumbnailURLReference() {
		if (_thumbnailURLReferenceSupplier != null) {
			thumbnailURLReference = _thumbnailURLReferenceSupplier.get();

			_thumbnailURLReferenceSupplier = null;
		}

		return thumbnailURLReference;
	}

	public void setThumbnailURLReference(
		ThumbnailURLReference thumbnailURLReference) {

		this.thumbnailURLReference = thumbnailURLReference;

		_thumbnailURLReferenceSupplier = null;
	}

	@JsonIgnore
	public void setThumbnailURLReference(
		UnsafeSupplier<ThumbnailURLReference, Exception>
			thumbnailURLReferenceUnsafeSupplier) {

		_thumbnailURLReferenceSupplier = () -> {
			try {
				return thumbnailURLReferenceUnsafeSupplier.get();
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
		description = "The fragment's thumbnail. On read, returned only when `nestedFields=thumbnailURLReference` is requested. On POST or PUT, omitting `thumbnailURLReference` clears any previously bound preview."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ThumbnailURLReference thumbnailURLReference;

	@JsonIgnore
	private Supplier<ThumbnailURLReference> _thumbnailURLReferenceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment's type."
	)
	@JsonGetter("type")
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment's type.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Fragment)) {
			return false;
		}

		Fragment fragment = (Fragment)object;

		return Objects.equals(toString(), fragment.toString());
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

		Boolean cacheable = getCacheable();

		if (cacheable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cacheable\": ");

			sb.append(cacheable);
		}

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(creator);
		}

		Date dateCreated = getDateCreated();

		if (dateCreated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCreated));

			sb.append("\"");
		}

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

			sb.append("\"");
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

		FragmentSet fragmentSet = getFragmentSet();

		if (fragmentSet != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentSet\": ");

			sb.append(String.valueOf(fragmentSet));
		}

		FragmentVersion[] fragmentVersions = getFragmentVersions();

		if (fragmentVersions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentVersions\": ");

			sb.append("[");

			for (int i = 0; i < fragmentVersions.length; i++) {
				sb.append(String.valueOf(fragmentVersions[i]));

				if ((i + 1) < fragmentVersions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String icon = getIcon();

		if (icon != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"icon\": ");

			sb.append("\"");

			sb.append(_escape(icon));

			sb.append("\"");
		}

		String key = getKey();

		if (key != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(key));

			sb.append("\"");
		}

		Boolean marketplace = getMarketplace();

		if (marketplace != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marketplace\": ");

			sb.append(marketplace);
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

		Boolean readOnly = getReadOnly();

		if (readOnly != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(readOnly);
		}

		ThumbnailURLReference thumbnailURLReference =
			getThumbnailURLReference();

		if (thumbnailURLReference != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnailURLReference\": ");

			sb.append(thumbnailURLReference);
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(type);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.fragment.dto.v1_0.Fragment",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		BASIC_FRAGMENT("BasicFragment"), FORM_FRAGMENT("FormFragment");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

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
// LIFERAY-REST-BUILDER-HASH:-1466352401