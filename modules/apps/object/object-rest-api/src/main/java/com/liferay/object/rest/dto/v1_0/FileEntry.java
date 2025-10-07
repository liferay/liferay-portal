/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.dto.v1_0;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("FileEntry")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FileEntry")
public class FileEntry implements Serializable {

	public static FileEntry toDTO(String json) {
		return ObjectMapperUtil.readValue(FileEntry.class, json);
	}

	public static FileEntry unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FileEntry.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAlternativeText() {
		if (_alternativeTextSupplier != null) {
			alternativeText = _alternativeTextSupplier.get();

			_alternativeTextSupplier = null;
		}

		return alternativeText;
	}

	public void setAlternativeText(String alternativeText) {
		this.alternativeText = alternativeText;

		_alternativeTextSupplier = null;
	}

	@JsonIgnore
	public void setAlternativeText(
		UnsafeSupplier<String, Exception> alternativeTextUnsafeSupplier) {

		_alternativeTextSupplier = () -> {
			try {
				return alternativeTextUnsafeSupplier.get();
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
	protected String alternativeText;

	@JsonIgnore
	private Supplier<String> _alternativeTextSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "optional field with the content of the document in Base64, can be embedded with nestedFields (the format of the nested field must be `<attachment field name>.fileBase64`)"
	)
	public String getFileBase64() {
		if (_fileBase64Supplier != null) {
			fileBase64 = _fileBase64Supplier.get();

			_fileBase64Supplier = null;
		}

		return fileBase64;
	}

	public void setFileBase64(String fileBase64) {
		this.fileBase64 = fileBase64;

		_fileBase64Supplier = null;
	}

	@JsonIgnore
	public void setFileBase64(
		UnsafeSupplier<String, Exception> fileBase64UnsafeSupplier) {

		_fileBase64Supplier = () -> {
			try {
				return fileBase64UnsafeSupplier.get();
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
		description = "optional field with the content of the document in Base64, can be embedded with nestedFields (the format of the nested field must be `<attachment field name>.fileBase64`)"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String fileBase64;

	@JsonIgnore
	private Supplier<String> _fileBase64Supplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "optional field that specifies the source of the file to be downloaded, can be embedded with nestedFields (the format of the nested field must be `<attachment field name>.fileURL`)"
	)
	public String getFileURL() {
		if (_fileURLSupplier != null) {
			fileURL = _fileURLSupplier.get();

			_fileURLSupplier = null;
		}

		return fileURL;
	}

	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;

		_fileURLSupplier = null;
	}

	@JsonIgnore
	public void setFileURL(
		UnsafeSupplier<String, Exception> fileURLUnsafeSupplier) {

		_fileURLSupplier = () -> {
			try {
				return fileURLUnsafeSupplier.get();
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
		description = "optional field that specifies the source of the file to be downloaded, can be embedded with nestedFields (the format of the nested field must be `<attachment field name>.fileURL`)"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String fileURL;

	@JsonIgnore
	private Supplier<String> _fileURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Folder getFolder() {
		if (_folderSupplier != null) {
			folder = _folderSupplier.get();

			_folderSupplier = null;
		}

		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;

		_folderSupplier = null;
	}

	@JsonIgnore
	public void setFolder(
		UnsafeSupplier<Folder, Exception> folderUnsafeSupplier) {

		_folderSupplier = () -> {
			try {
				return folderUnsafeSupplier.get();
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
	protected Folder folder;

	@JsonIgnore
	private Supplier<Folder> _folderSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Link getLink() {
		if (_linkSupplier != null) {
			link = _linkSupplier.get();

			_linkSupplier = null;
		}

		return link;
	}

	public void setLink(Link link) {
		this.link = link;

		_linkSupplier = null;
	}

	@JsonIgnore
	public void setLink(UnsafeSupplier<Link, Exception> linkUnsafeSupplier) {
		_linkSupplier = () -> {
			try {
				return linkUnsafeSupplier.get();
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
	protected Link link;

	@JsonIgnore
	private Supplier<Link> _linkSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "optional field that specifies the metadata of the file, can be embedded with nestedFields (the format of the nested field must be `<attachment field name>.metadata`)"
	)
	@Valid
	public Map<String, Object> getMetadata() {
		if (_metadataSupplier != null) {
			metadata = _metadataSupplier.get();

			_metadataSupplier = null;
		}

		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;

		_metadataSupplier = null;
	}

	@JsonIgnore
	public void setMetadata(
		UnsafeSupplier<Map<String, Object>, Exception> metadataUnsafeSupplier) {

		_metadataSupplier = () -> {
			try {
				return metadataUnsafeSupplier.get();
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
		description = "optional field that specifies the metadata of the file, can be embedded with nestedFields (the format of the nested field must be `<attachment field name>.metadata`)"
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Object> metadata;

	@JsonIgnore
	private Supplier<Map<String, Object>> _metadataSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getMimeType() {
		if (_mimeTypeSupplier != null) {
			mimeType = _mimeTypeSupplier.get();

			_mimeTypeSupplier = null;
		}

		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;

		_mimeTypeSupplier = null;
	}

	@JsonIgnore
	public void setMimeType(
		UnsafeSupplier<String, Exception> mimeTypeUnsafeSupplier) {

		_mimeTypeSupplier = () -> {
			try {
				return mimeTypeUnsafeSupplier.get();
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
	protected String mimeType;

	@JsonIgnore
	private Supplier<String> _mimeTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "optional field that specifies the preview URL of the file to be used, can be embedded with nestedFields (the format of the nested field must be `<attachment field name>.previewURL`)"
	)
	public String getPreviewURL() {
		if (_previewURLSupplier != null) {
			previewURL = _previewURLSupplier.get();

			_previewURLSupplier = null;
		}

		return previewURL;
	}

	public void setPreviewURL(String previewURL) {
		this.previewURL = previewURL;

		_previewURLSupplier = null;
	}

	@JsonIgnore
	public void setPreviewURL(
		UnsafeSupplier<String, Exception> previewURLUnsafeSupplier) {

		_previewURLSupplier = () -> {
			try {
				return previewURLUnsafeSupplier.get();
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
		description = "optional field that specifies the preview URL of the file to be used, can be embedded with nestedFields (the format of the nested field must be `<attachment field name>.previewURL`)"
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String previewURL;

	@JsonIgnore
	private Supplier<String> _previewURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Scope getScope() {
		if (_scopeSupplier != null) {
			scope = _scopeSupplier.get();

			_scopeSupplier = null;
		}

		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;

		_scopeSupplier = null;
	}

	@JsonIgnore
	public void setScope(UnsafeSupplier<Scope, Exception> scopeUnsafeSupplier) {
		_scopeSupplier = () -> {
			try {
				return scopeUnsafeSupplier.get();
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
	protected Scope scope;

	@JsonIgnore
	private Supplier<Scope> _scopeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "optional field that specifies the thumbnail of the file to be used, can be embedded with nestedFields (the format of the nested field must be `<attachment field name>.thumbnailURL`)"
	)
	public String getThumbnailURL() {
		if (_thumbnailURLSupplier != null) {
			thumbnailURL = _thumbnailURLSupplier.get();

			_thumbnailURLSupplier = null;
		}

		return thumbnailURL;
	}

	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;

		_thumbnailURLSupplier = null;
	}

	@JsonIgnore
	public void setThumbnailURL(
		UnsafeSupplier<String, Exception> thumbnailURLUnsafeSupplier) {

		_thumbnailURLSupplier = () -> {
			try {
				return thumbnailURLUnsafeSupplier.get();
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
		description = "optional field that specifies the thumbnail of the file to be used, can be embedded with nestedFields (the format of the nested field must be `<attachment field name>.thumbnailURL`)"
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String thumbnailURL;

	@JsonIgnore
	private Supplier<String> _thumbnailURLSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FileEntry)) {
			return false;
		}

		FileEntry fileEntry = (FileEntry)object;

		return Objects.equals(toString(), fileEntry.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String alternativeText = getAlternativeText();

		if (alternativeText != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"alternativeText\": ");

			sb.append("\"");

			sb.append(_escape(alternativeText));

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

		String fileBase64 = getFileBase64();

		if (fileBase64 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileBase64\": ");

			sb.append("\"");

			sb.append(_escape(fileBase64));

			sb.append("\"");
		}

		String fileURL = getFileURL();

		if (fileURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileURL\": ");

			sb.append("\"");

			sb.append(_escape(fileURL));

			sb.append("\"");
		}

		Folder folder = getFolder();

		if (folder != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"folder\": ");

			sb.append(String.valueOf(folder));
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Link link = getLink();

		if (link != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"link\": ");

			sb.append(String.valueOf(link));
		}

		Map<String, Object> metadata = getMetadata();

		if (metadata != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metadata\": ");

			sb.append(_toJSON(metadata));
		}

		String mimeType = getMimeType();

		if (mimeType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mimeType\": ");

			sb.append("\"");

			sb.append(_escape(mimeType));

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

		String previewURL = getPreviewURL();

		if (previewURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previewURL\": ");

			sb.append("\"");

			sb.append(_escape(previewURL));

			sb.append("\"");
		}

		Scope scope = getScope();

		if (scope != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append(String.valueOf(scope));
		}

		String thumbnailURL = getThumbnailURL();

		if (thumbnailURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnailURL\": ");

			sb.append("\"");

			sb.append(_escape(thumbnailURL));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.rest.dto.v1_0.FileEntry",
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