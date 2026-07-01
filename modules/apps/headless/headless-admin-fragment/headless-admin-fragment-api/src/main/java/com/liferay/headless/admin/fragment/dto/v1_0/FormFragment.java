/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.headless.admin.fragment.constant.v1_0.FieldType;
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
@GraphQLName(description = "A form fragment entry.", value = "FormFragment")
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A form fragment entry."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FormFragment")
public class FormFragment extends Fragment implements Serializable {

	public static FormFragment toDTO(String json) {
		return ObjectMapperUtil.readValue(FormFragment.class, json);
	}

	public static FormFragment unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FormFragment.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The input field types the form fragment supports."
	)
	@Valid
	public FieldType[] getFieldTypes() {
		if (_fieldTypesSupplier != null) {
			fieldTypes = _fieldTypesSupplier.get();

			_fieldTypesSupplier = null;
		}

		return fieldTypes;
	}

	public void setFieldTypes(FieldType[] fieldTypes) {
		this.fieldTypes = fieldTypes;

		_fieldTypesSupplier = null;
	}

	@JsonIgnore
	public void setFieldTypes(
		UnsafeSupplier<FieldType[], Exception> fieldTypesUnsafeSupplier) {

		_fieldTypesSupplier = () -> {
			try {
				return fieldTypesUnsafeSupplier.get();
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
		description = "The input field types the form fragment supports."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FieldType[] fieldTypes;

	@JsonIgnore
	private Supplier<FieldType[]> _fieldTypesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormFragment)) {
			return false;
		}

		FormFragment formFragment = (FormFragment)object;

		return Objects.equals(toString(), formFragment.toString());
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

		FieldType[] fieldTypes = getFieldTypes();

		if (fieldTypes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldTypes\": ");

			sb.append("[");

			for (int i = 0; i < fieldTypes.length; i++) {
				sb.append(fieldTypes[i]);

				if ((i + 1) < fieldTypes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

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
		defaultValue = "com.liferay.headless.admin.fragment.dto.v1_0.FormFragment",
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
// LIFERAY-REST-BUILDER-HASH:-180614183