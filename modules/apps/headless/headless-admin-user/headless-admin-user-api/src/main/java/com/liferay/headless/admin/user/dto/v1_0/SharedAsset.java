/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(description = "Represents a shared asset.", value = "SharedAsset")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SharedAsset")
public class SharedAsset implements Serializable {

	public static SharedAsset toDTO(String json) {
		return ObjectMapperUtil.readValue(SharedAsset.class, json);
	}

	public static SharedAsset unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SharedAsset.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The shared asset actions."
	)
	public String[] getActionIds() {
		if (_actionIdsSupplier != null) {
			actionIds = _actionIdsSupplier.get();

			_actionIdsSupplier = null;
		}

		return actionIds;
	}

	public void setActionIds(String[] actionIds) {
		this.actionIds = actionIds;

		_actionIdsSupplier = null;
	}

	@JsonIgnore
	public void setActionIds(
		UnsafeSupplier<String[], Exception> actionIdsUnsafeSupplier) {

		_actionIdsSupplier = () -> {
			try {
				return actionIdsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The shared asset actions.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] actionIds;

	@JsonIgnore
	private Supplier<String[]> _actionIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Block of actions allowed by the user making the request."
	)
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
		description = "Block of actions allowed by the user making the request."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The shared asset type."
	)
	public String getAssetType() {
		if (_assetTypeSupplier != null) {
			assetType = _assetTypeSupplier.get();

			_assetTypeSupplier = null;
		}

		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;

		_assetTypeSupplier = null;
	}

	@JsonIgnore
	public void setAssetType(
		UnsafeSupplier<String, Exception> assetTypeUnsafeSupplier) {

		_assetTypeSupplier = () -> {
			try {
				return assetTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The shared asset type.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String assetType;

	@JsonIgnore
	private Supplier<String> _assetTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getClassName() {
		if (_classNameSupplier != null) {
			className = _classNameSupplier.get();

			_classNameSupplier = null;
		}

		return className;
	}

	public void setClassName(String className) {
		this.className = className;

		_classNameSupplier = null;
	}

	@JsonIgnore
	public void setClassName(
		UnsafeSupplier<String, Exception> classNameUnsafeSupplier) {

		_classNameSupplier = () -> {
			try {
				return classNameUnsafeSupplier.get();
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
	protected String className;

	@JsonIgnore
	private Supplier<String> _classNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getClassPK() {
		if (_classPKSupplier != null) {
			classPK = _classPKSupplier.get();

			_classPKSupplier = null;
		}

		return classPK;
	}

	public void setClassPK(Long classPK) {
		this.classPK = classPK;

		_classPKSupplier = null;
	}

	@JsonIgnore
	public void setClassPK(
		UnsafeSupplier<Long, Exception> classPKUnsafeSupplier) {

		_classPKSupplier = () -> {
			try {
				return classPKUnsafeSupplier.get();
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
	protected Long classPK;

	@JsonIgnore
	private Supplier<Long> _classPKSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The shared asset creator."
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

	@GraphQLField(description = "The shared asset creator.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The shared asset creation date."
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

	@GraphQLField(description = "The shared asset creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The shared asset modified date."
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

	@GraphQLField(description = "The shared asset modified date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The shared asset external reference code."
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

	@GraphQLField(description = "The shared asset external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Optional field with the embedded file, can be embedded with nestedFields"
	)
	@Valid
	public FileEntry getFile() {
		if (_fileSupplier != null) {
			file = _fileSupplier.get();

			_fileSupplier = null;
		}

		return file;
	}

	public void setFile(FileEntry file) {
		this.file = file;

		_fileSupplier = null;
	}

	@JsonIgnore
	public void setFile(
		UnsafeSupplier<FileEntry, Exception> fileUnsafeSupplier) {

		_fileSupplier = () -> {
			try {
				return fileUnsafeSupplier.get();
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
		description = "Optional field with the embedded file, can be embedded with nestedFields"
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected FileEntry file;

	@JsonIgnore
	private Supplier<FileEntry> _fileSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The shared asset file type icon."
	)
	public String getFileTypeIcon() {
		if (_fileTypeIconSupplier != null) {
			fileTypeIcon = _fileTypeIconSupplier.get();

			_fileTypeIconSupplier = null;
		}

		return fileTypeIcon;
	}

	public void setFileTypeIcon(String fileTypeIcon) {
		this.fileTypeIcon = fileTypeIcon;

		_fileTypeIconSupplier = null;
	}

	@JsonIgnore
	public void setFileTypeIcon(
		UnsafeSupplier<String, Exception> fileTypeIconUnsafeSupplier) {

		_fileTypeIconSupplier = () -> {
			try {
				return fileTypeIconUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The shared asset file type icon.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String fileTypeIcon;

	@JsonIgnore
	private Supplier<String> _fileTypeIconSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The shared asset file type icon color."
	)
	public String getFileTypeIconColor() {
		if (_fileTypeIconColorSupplier != null) {
			fileTypeIconColor = _fileTypeIconColorSupplier.get();

			_fileTypeIconColorSupplier = null;
		}

		return fileTypeIconColor;
	}

	public void setFileTypeIconColor(String fileTypeIconColor) {
		this.fileTypeIconColor = fileTypeIconColor;

		_fileTypeIconColorSupplier = null;
	}

	@JsonIgnore
	public void setFileTypeIconColor(
		UnsafeSupplier<String, Exception> fileTypeIconColorUnsafeSupplier) {

		_fileTypeIconColorSupplier = () -> {
			try {
				return fileTypeIconColorUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The shared asset file type icon color.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String fileTypeIconColor;

	@JsonIgnore
	private Supplier<String> _fileTypeIconColorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The shared asset ID."
	)
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

	@GraphQLField(description = "The shared asset ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the shared asset is shareable or not"
	)
	public Boolean getShareable() {
		if (_shareableSupplier != null) {
			shareable = _shareableSupplier.get();

			_shareableSupplier = null;
		}

		return shareable;
	}

	public void setShareable(Boolean shareable) {
		this.shareable = shareable;

		_shareableSupplier = null;
	}

	@JsonIgnore
	public void setShareable(
		UnsafeSupplier<Boolean, Exception> shareableUnsafeSupplier) {

		_shareableSupplier = () -> {
			try {
				return shareableUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Whether the shared asset is shareable or not")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean shareable;

	@JsonIgnore
	private Supplier<Boolean> _shareableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the site to which this shared asset is scoped."
	)
	public String getSiteName() {
		if (_siteNameSupplier != null) {
			siteName = _siteNameSupplier.get();

			_siteNameSupplier = null;
		}

		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;

		_siteNameSupplier = null;
	}

	@JsonIgnore
	public void setSiteName(
		UnsafeSupplier<String, Exception> siteNameUnsafeSupplier) {

		_siteNameSupplier = () -> {
			try {
				return siteNameUnsafeSupplier.get();
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
		description = "The name of the site to which this shared asset is scoped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String siteName;

	@JsonIgnore
	private Supplier<String> _siteNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The shared asset title."
	)
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

	@GraphQLField(description = "The shared asset title.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SharedAsset)) {
			return false;
		}

		SharedAsset sharedAsset = (SharedAsset)object;

		return Objects.equals(toString(), sharedAsset.toString());
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

		String[] actionIds = getActionIds();

		if (actionIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionIds\": ");

			sb.append("[");

			for (int i = 0; i < actionIds.length; i++) {
				sb.append("\"");

				sb.append(_escape(actionIds[i]));

				sb.append("\"");

				if ((i + 1) < actionIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		String assetType = getAssetType();

		if (assetType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType\": ");

			sb.append("\"");

			sb.append(_escape(assetType));

			sb.append("\"");
		}

		String className = getClassName();

		if (className != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(className));

			sb.append("\"");
		}

		Long classPK = getClassPK();

		if (classPK != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classPK\": ");

			sb.append(classPK);
		}

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(creator));
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

		FileEntry file = getFile();

		if (file != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"file\": ");

			sb.append(String.valueOf(file));
		}

		String fileTypeIcon = getFileTypeIcon();

		if (fileTypeIcon != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileTypeIcon\": ");

			sb.append("\"");

			sb.append(_escape(fileTypeIcon));

			sb.append("\"");
		}

		String fileTypeIconColor = getFileTypeIconColor();

		if (fileTypeIconColor != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileTypeIconColor\": ");

			sb.append("\"");

			sb.append(_escape(fileTypeIconColor));

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

		Boolean shareable = getShareable();

		if (shareable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shareable\": ");

			sb.append(shareable);
		}

		String siteName = getSiteName();

		if (siteName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteName\": ");

			sb.append("\"");

			sb.append(_escape(siteName));

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
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.SharedAsset",
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