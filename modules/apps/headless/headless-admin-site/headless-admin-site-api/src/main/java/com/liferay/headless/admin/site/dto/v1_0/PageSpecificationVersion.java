/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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
	description = "A page specification version of a content page.",
	value = "PageSpecificationVersion"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A page specification version of a content page."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageSpecificationVersion")
public class PageSpecificationVersion implements Serializable {

	public static PageSpecificationVersion toDTO(String json) {
		return ObjectMapperUtil.readValue(PageSpecificationVersion.class, json);
	}

	public static PageSpecificationVersion unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PageSpecificationVersion.class, json);
	}

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
		description = "The page specification version's creator. It is not returned by default. It can be embedded via nestedFields."
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

	@GraphQLField(
		description = "The page specification version's creator. It is not returned by default. It can be embedded via nestedFields."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page specification version's creation date."
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

	@GraphQLField(
		description = "The page specification version's creation date."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time the page specification version changed."
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

	@GraphQLField(
		description = "The last time the page specification version changed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page specification version's external reference code."
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

	@GraphQLField(
		description = "The page specification version's external reference code."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page specification version's name."
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

	@GraphQLField(description = "The page specification version's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page specification version's page specification. It is not returned by default. It can be embedded via nestedFields."
	)
	@Valid
	public PageSpecification getPageSpecification() {
		if (_pageSpecificationSupplier != null) {
			pageSpecification = _pageSpecificationSupplier.get();

			_pageSpecificationSupplier = null;
		}

		return pageSpecification;
	}

	public void setPageSpecification(PageSpecification pageSpecification) {
		this.pageSpecification = pageSpecification;

		_pageSpecificationSupplier = null;
	}

	@JsonIgnore
	public void setPageSpecification(
		UnsafeSupplier<PageSpecification, Exception>
			pageSpecificationUnsafeSupplier) {

		_pageSpecificationSupplier = () -> {
			try {
				return pageSpecificationUnsafeSupplier.get();
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
		description = "The page specification version's page specification. It is not returned by default. It can be embedded via nestedFields."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PageSpecification pageSpecification;

	@JsonIgnore
	private Supplier<PageSpecification> _pageSpecificationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page specification version's page experiences. It is not returned by default. It can be embedded via nestedFields."
	)
	@Valid
	public PageSpecificationVersionPageExperience[]
		getPageSpecificationVersionPageExperiences() {

		if (_pageSpecificationVersionPageExperiencesSupplier != null) {
			pageSpecificationVersionPageExperiences =
				_pageSpecificationVersionPageExperiencesSupplier.get();

			_pageSpecificationVersionPageExperiencesSupplier = null;
		}

		return pageSpecificationVersionPageExperiences;
	}

	public void setPageSpecificationVersionPageExperiences(
		PageSpecificationVersionPageExperience[]
			pageSpecificationVersionPageExperiences) {

		this.pageSpecificationVersionPageExperiences =
			pageSpecificationVersionPageExperiences;

		_pageSpecificationVersionPageExperiencesSupplier = null;
	}

	@JsonIgnore
	public void setPageSpecificationVersionPageExperiences(
		UnsafeSupplier<PageSpecificationVersionPageExperience[], Exception>
			pageSpecificationVersionPageExperiencesUnsafeSupplier) {

		_pageSpecificationVersionPageExperiencesSupplier = () -> {
			try {
				return pageSpecificationVersionPageExperiencesUnsafeSupplier.
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

	@GraphQLField(
		description = "The page specification version's page experiences. It is not returned by default. It can be embedded via nestedFields."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PageSpecificationVersionPageExperience[]
		pageSpecificationVersionPageExperiences;

	@JsonIgnore
	private Supplier<PageSpecificationVersionPageExperience[]>
		_pageSpecificationVersionPageExperiencesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page specification version's status."
	)
	@JsonGetter("status")
	@Valid
	public Status getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	@JsonIgnore
	public String getStatusAsString() {
		Status status = getStatus();

		if (status == null) {
			return null;
		}

		return status.toString();
	}

	public void setStatus(Status status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Status, Exception> statusUnsafeSupplier) {

		_statusSupplier = () -> {
			try {
				return statusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page specification version's status.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Status status;

	@JsonIgnore
	private Supplier<Status> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page specification version's status date."
	)
	public Date getStatusDate() {
		if (_statusDateSupplier != null) {
			statusDate = _statusDateSupplier.get();

			_statusDateSupplier = null;
		}

		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;

		_statusDateSupplier = null;
	}

	@JsonIgnore
	public void setStatusDate(
		UnsafeSupplier<Date, Exception> statusDateUnsafeSupplier) {

		_statusDateSupplier = () -> {
			try {
				return statusDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page specification version's status date.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date statusDate;

	@JsonIgnore
	private Supplier<Date> _statusDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page specification version's version."
	)
	public Integer getVersion() {
		if (_versionSupplier != null) {
			version = _versionSupplier.get();

			_versionSupplier = null;
		}

		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;

		_versionSupplier = null;
	}

	@JsonIgnore
	public void setVersion(
		UnsafeSupplier<Integer, Exception> versionUnsafeSupplier) {

		_versionSupplier = () -> {
			try {
				return versionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page specification version's version.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer version;

	@JsonIgnore
	private Supplier<Integer> _versionSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageSpecificationVersion)) {
			return false;
		}

		PageSpecificationVersion pageSpecificationVersion =
			(PageSpecificationVersion)object;

		return Objects.equals(toString(), pageSpecificationVersion.toString());
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

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
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

		PageSpecification pageSpecification = getPageSpecification();

		if (pageSpecification != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSpecification\": ");

			sb.append(String.valueOf(pageSpecification));
		}

		PageSpecificationVersionPageExperience[]
			pageSpecificationVersionPageExperiences =
				getPageSpecificationVersionPageExperiences();

		if (pageSpecificationVersionPageExperiences != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSpecificationVersionPageExperiences\": ");

			sb.append("[");

			for (int i = 0; i < pageSpecificationVersionPageExperiences.length;
				 i++) {

				sb.append(
					String.valueOf(pageSpecificationVersionPageExperiences[i]));

				if ((i + 1) < pageSpecificationVersionPageExperiences.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Status status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");
			sb.append(status);
			sb.append("\"");
		}

		Date statusDate = getStatusDate();

		if (statusDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"statusDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(statusDate));

			sb.append("\"");
		}

		Integer version = getVersion();

		if (version != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append(version);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.PageSpecificationVersion",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Status")
	public static enum Status {

		APPROVED("Approved"), DRAFT("Draft");

		@JsonCreator
		public static Status create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Status status : values()) {
				if (Objects.equals(status.getValue(), value)) {
					return status;
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

		private Status(String value) {
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
// LIFERAY-REST-BUILDER-HASH:-674890779