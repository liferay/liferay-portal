/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.dto.v1_0;

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
	description = "Represents a site's basic information.", value = "SiteBrief"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SiteBrief")
public class SiteBrief implements Serializable {

	public static SiteBrief toDTO(String json) {
		return ObjectMapperUtil.readValue(SiteBrief.class, json);
	}

	public static SiteBrief unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SiteBrief.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The site's descriptive name."
	)
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

	@GraphQLField(description = "The site's descriptive name.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String descriptiveName;

	@JsonIgnore
	private Supplier<String> _descriptiveNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getDescriptiveName_i18n() {
		if (_descriptiveName_i18nSupplier != null) {
			descriptiveName_i18n = _descriptiveName_i18nSupplier.get();

			_descriptiveName_i18nSupplier = null;
		}

		return descriptiveName_i18n;
	}

	public void setDescriptiveName_i18n(
		Map<String, String> descriptiveName_i18n) {

		this.descriptiveName_i18n = descriptiveName_i18n;

		_descriptiveName_i18nSupplier = null;
	}

	@JsonIgnore
	public void setDescriptiveName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			descriptiveName_i18nUnsafeSupplier) {

		_descriptiveName_i18nSupplier = () -> {
			try {
				return descriptiveName_i18nUnsafeSupplier.get();
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
	protected Map<String, String> descriptiveName_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _descriptiveName_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The site's external reference code.",
		example = "AB-34098-789-N"
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

	@GraphQLField(description = "The site's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(description = "The site's ID.")
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

	@GraphQLField(description = "The site's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The site's name."
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

	@GraphQLField(description = "The site's name.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getName_i18n() {
		if (_name_i18nSupplier != null) {
			name_i18n = _name_i18nSupplier.get();

			_name_i18nSupplier = null;
		}

		return name_i18n;
	}

	public void setName_i18n(Map<String, String> name_i18n) {
		this.name_i18n = name_i18n;

		_name_i18nSupplier = null;
	}

	@JsonIgnore
	public void setName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			name_i18nUnsafeSupplier) {

		_name_i18nSupplier = () -> {
			try {
				return name_i18nUnsafeSupplier.get();
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
	protected Map<String, String> name_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _name_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the user's roles."
	)
	@Valid
	public RoleBrief[] getRoleBriefs() {
		if (_roleBriefsSupplier != null) {
			roleBriefs = _roleBriefsSupplier.get();

			_roleBriefsSupplier = null;
		}

		return roleBriefs;
	}

	public void setRoleBriefs(RoleBrief[] roleBriefs) {
		this.roleBriefs = roleBriefs;

		_roleBriefsSupplier = null;
	}

	@JsonIgnore
	public void setRoleBriefs(
		UnsafeSupplier<RoleBrief[], Exception> roleBriefsUnsafeSupplier) {

		_roleBriefsSupplier = () -> {
			try {
				return roleBriefsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of the user's roles.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected RoleBrief[] roleBriefs;

	@JsonIgnore
	private Supplier<RoleBrief[]> _roleBriefsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SiteBrief)) {
			return false;
		}

		SiteBrief siteBrief = (SiteBrief)object;

		return Objects.equals(toString(), siteBrief.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

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

		Map<String, String> descriptiveName_i18n = getDescriptiveName_i18n();

		if (descriptiveName_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName_i18n\": ");

			sb.append(_toJSON(descriptiveName_i18n));
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

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
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

		Map<String, String> name_i18n = getName_i18n();

		if (name_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(name_i18n));
		}

		RoleBrief[] roleBriefs = getRoleBriefs();

		if (roleBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleBriefs\": ");

			sb.append("[");

			for (int i = 0; i < roleBriefs.length; i++) {
				sb.append(String.valueOf(roleBriefs[i]));

				if ((i + 1) < roleBriefs.length) {
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
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.SiteBrief",
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