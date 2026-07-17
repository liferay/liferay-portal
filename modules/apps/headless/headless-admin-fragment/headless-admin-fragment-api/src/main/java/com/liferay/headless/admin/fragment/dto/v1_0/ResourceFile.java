/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@GraphQLName(
	description = "A resource file of a fragment set.", value = "ResourceFile"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A resource file of a fragment set."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ResourceFile")
public class ResourceFile implements Serializable {

	public static ResourceFile toDTO(String json) {
		return ObjectMapperUtil.readValue(ResourceFile.class, json);
	}

	public static ResourceFile unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ResourceFile.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource file's creator."
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

	@GraphQLField(description = "The resource file's creator.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource file's creation date."
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

	@GraphQLField(description = "The resource file's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time the resource file changed."
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

	@GraphQLField(description = "The last time the resource file changed.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource file's external reference code."
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

	@GraphQLField(description = "The resource file's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource file's binary content."
	)
	@Valid
	public FileURLReference getFileURLReference() {
		if (_fileURLReferenceSupplier != null) {
			fileURLReference = _fileURLReferenceSupplier.get();

			_fileURLReferenceSupplier = null;
		}

		return fileURLReference;
	}

	public void setFileURLReference(FileURLReference fileURLReference) {
		this.fileURLReference = fileURLReference;

		_fileURLReferenceSupplier = null;
	}

	@JsonIgnore
	public void setFileURLReference(
		UnsafeSupplier<FileURLReference, Exception>
			fileURLReferenceUnsafeSupplier) {

		_fileURLReferenceSupplier = () -> {
			try {
				return fileURLReferenceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The resource file's binary content.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FileURLReference fileURLReference;

	@JsonIgnore
	private Supplier<FileURLReference> _fileURLReferenceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource file's fragment set. On read, returned only when `nestedFields=fragmentSet` is requested. On write, used only during LAR import, where it is created if it does not exist."
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

	@GraphQLField(
		description = "The resource file's fragment set. On read, returned only when `nestedFields=fragmentSet` is requested. On write, used only during LAR import, where it is created if it does not exist."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentSet fragmentSet;

	@JsonIgnore
	private Supplier<FragmentSet> _fragmentSetSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external reference code of the resource file's fragment set, used to reference an existing fragment set. Takes precedence over `fragmentSet` when both are set."
	)
	public String getFragmentSetExternalReferenceCode() {
		if (_fragmentSetExternalReferenceCodeSupplier != null) {
			fragmentSetExternalReferenceCode =
				_fragmentSetExternalReferenceCodeSupplier.get();

			_fragmentSetExternalReferenceCodeSupplier = null;
		}

		return fragmentSetExternalReferenceCode;
	}

	public void setFragmentSetExternalReferenceCode(
		String fragmentSetExternalReferenceCode) {

		this.fragmentSetExternalReferenceCode =
			fragmentSetExternalReferenceCode;

		_fragmentSetExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setFragmentSetExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			fragmentSetExternalReferenceCodeUnsafeSupplier) {

		_fragmentSetExternalReferenceCodeSupplier = () -> {
			try {
				return fragmentSetExternalReferenceCodeUnsafeSupplier.get();
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
		description = "The external reference code of the resource file's fragment set, used to reference an existing fragment set. Takes precedence over `fragmentSet` when both are set."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String fragmentSetExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _fragmentSetExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource file's name."
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

	@GraphQLField(description = "The resource file's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource file's resource folder. On read, returned only when `nestedFields=resourceFolder` is requested. On write, used only during LAR import, where it is created if it does not exist."
	)
	@Valid
	public ResourceFolder getResourceFolder() {
		if (_resourceFolderSupplier != null) {
			resourceFolder = _resourceFolderSupplier.get();

			_resourceFolderSupplier = null;
		}

		return resourceFolder;
	}

	public void setResourceFolder(ResourceFolder resourceFolder) {
		this.resourceFolder = resourceFolder;

		_resourceFolderSupplier = null;
	}

	@JsonIgnore
	public void setResourceFolder(
		UnsafeSupplier<ResourceFolder, Exception>
			resourceFolderUnsafeSupplier) {

		_resourceFolderSupplier = () -> {
			try {
				return resourceFolderUnsafeSupplier.get();
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
		description = "The resource file's resource folder. On read, returned only when `nestedFields=resourceFolder` is requested. On write, used only during LAR import, where it is created if it does not exist."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ResourceFolder resourceFolder;

	@JsonIgnore
	private Supplier<ResourceFolder> _resourceFolderSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external reference code of the resource file's resource folder, used to reference an existing resource folder. Takes precedence over `resourceFolder` when both are set."
	)
	public String getResourceFolderExternalReferenceCode() {
		if (_resourceFolderExternalReferenceCodeSupplier != null) {
			resourceFolderExternalReferenceCode =
				_resourceFolderExternalReferenceCodeSupplier.get();

			_resourceFolderExternalReferenceCodeSupplier = null;
		}

		return resourceFolderExternalReferenceCode;
	}

	public void setResourceFolderExternalReferenceCode(
		String resourceFolderExternalReferenceCode) {

		this.resourceFolderExternalReferenceCode =
			resourceFolderExternalReferenceCode;

		_resourceFolderExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setResourceFolderExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			resourceFolderExternalReferenceCodeUnsafeSupplier) {

		_resourceFolderExternalReferenceCodeSupplier = () -> {
			try {
				return resourceFolderExternalReferenceCodeUnsafeSupplier.get();
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
		description = "The external reference code of the resource file's resource folder, used to reference an existing resource folder. Takes precedence over `resourceFolder` when both are set."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String resourceFolderExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _resourceFolderExternalReferenceCodeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ResourceFile)) {
			return false;
		}

		ResourceFile resourceFile = (ResourceFile)object;

		return Objects.equals(toString(), resourceFile.toString());
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

		FileURLReference fileURLReference = getFileURLReference();

		if (fileURLReference != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileURLReference\": ");

			sb.append(String.valueOf(fileURLReference));
		}

		FragmentSet fragmentSet = getFragmentSet();

		if (fragmentSet != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentSet\": ");

			sb.append(String.valueOf(fragmentSet));
		}

		String fragmentSetExternalReferenceCode =
			getFragmentSetExternalReferenceCode();

		if (fragmentSetExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentSetExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(fragmentSetExternalReferenceCode));

			sb.append("\"");
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

		ResourceFolder resourceFolder = getResourceFolder();

		if (resourceFolder != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resourceFolder\": ");

			sb.append(String.valueOf(resourceFolder));
		}

		String resourceFolderExternalReferenceCode =
			getResourceFolderExternalReferenceCode();

		if (resourceFolderExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resourceFolderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(resourceFolderExternalReferenceCode));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.fragment.dto.v1_0.ResourceFile",
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
// LIFERAY-REST-BUILDER-HASH:700611756