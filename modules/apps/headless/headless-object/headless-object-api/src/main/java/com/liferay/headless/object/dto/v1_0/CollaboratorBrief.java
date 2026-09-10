/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.dto.v1_0;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Alicia García
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Brief of the collaborator record for the user making the request, present only when this asset is shared with them.",
	value = "CollaboratorBrief"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Brief of the collaborator record for the user making the request, present only when this asset is shared with them."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "CollaboratorBrief")
public class CollaboratorBrief implements Serializable {

	public static CollaboratorBrief toDTO(String json) {
		return ObjectMapperUtil.readValue(CollaboratorBrief.class, json);
	}

	public static CollaboratorBrief unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(CollaboratorBrief.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The sharing actions granted to the user making the request (e.g. VIEW, UPDATE)."
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

	@GraphQLField(
		description = "The sharing actions granted to the user making the request (e.g. VIEW, UPDATE)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] actionIds;

	@JsonIgnore
	private Supplier<String[]> _actionIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The date when the sharing access expires, or null if it never expires."
	)
	public Date getDateExpired() {
		if (_dateExpiredSupplier != null) {
			dateExpired = _dateExpiredSupplier.get();

			_dateExpiredSupplier = null;
		}

		return dateExpired;
	}

	public void setDateExpired(Date dateExpired) {
		this.dateExpired = dateExpired;

		_dateExpiredSupplier = null;
	}

	@JsonIgnore
	public void setDateExpired(
		UnsafeSupplier<Date, Exception> dateExpiredUnsafeSupplier) {

		_dateExpiredSupplier = () -> {
			try {
				return dateExpiredUnsafeSupplier.get();
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
		description = "The date when the sharing access expires, or null if it never expires."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateExpired;

	@JsonIgnore
	private Supplier<Date> _dateExpiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the user making the request can reshare this asset with others."
	)
	public Boolean getShare() {
		if (_shareSupplier != null) {
			share = _shareSupplier.get();

			_shareSupplier = null;
		}

		return share;
	}

	public void setShare(Boolean share) {
		this.share = share;

		_shareSupplier = null;
	}

	@JsonIgnore
	public void setShare(
		UnsafeSupplier<Boolean, Exception> shareUnsafeSupplier) {

		_shareSupplier = () -> {
			try {
				return shareUnsafeSupplier.get();
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
		description = "Whether the user making the request can reshare this asset with others."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean share;

	@JsonIgnore
	private Supplier<Boolean> _shareSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CollaboratorBrief)) {
			return false;
		}

		CollaboratorBrief collaboratorBrief = (CollaboratorBrief)object;

		return Objects.equals(toString(), collaboratorBrief.toString());
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

		Date dateExpired = getDateExpired();

		if (dateExpired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateExpired\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateExpired));

			sb.append("\"");
		}

		Boolean share = getShare();

		if (share != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"share\": ");

			sb.append(share);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.object.dto.v1_0.CollaboratorBrief",
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
// LIFERAY-REST-BUILDER-HASH:-1045672764