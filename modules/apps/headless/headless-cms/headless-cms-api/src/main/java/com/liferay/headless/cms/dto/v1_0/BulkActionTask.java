/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
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
@GraphQLName("BulkActionTask")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "BulkActionTask")
public class BulkActionTask implements Serializable {

	public static BulkActionTask toDTO(String json) {
		return ObjectMapperUtil.readValue(BulkActionTask.class, json);
	}

	public static BulkActionTask unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(BulkActionTask.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getActionName() {
		if (_actionNameSupplier != null) {
			actionName = _actionNameSupplier.get();

			_actionNameSupplier = null;
		}

		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;

		_actionNameSupplier = null;
	}

	@JsonIgnore
	public void setActionName(
		UnsafeSupplier<String, Exception> actionNameUnsafeSupplier) {

		_actionNameSupplier = () -> {
			try {
				return actionNameUnsafeSupplier.get();
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
	protected String actionName;

	@JsonIgnore
	private Supplier<String> _actionNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAuthor() {
		if (_authorSupplier != null) {
			author = _authorSupplier.get();

			_authorSupplier = null;
		}

		return author;
	}

	public void setAuthor(String author) {
		this.author = author;

		_authorSupplier = null;
	}

	@JsonIgnore
	public void setAuthor(
		UnsafeSupplier<String, Exception> authorUnsafeSupplier) {

		_authorSupplier = () -> {
			try {
				return authorUnsafeSupplier.get();
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
	protected String author;

	@JsonIgnore
	private Supplier<String> _authorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getCompletionDate() {
		if (_completionDateSupplier != null) {
			completionDate = _completionDateSupplier.get();

			_completionDateSupplier = null;
		}

		return completionDate;
	}

	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;

		_completionDateSupplier = null;
	}

	@JsonIgnore
	public void setCompletionDate(
		UnsafeSupplier<Date, Exception> completionDateUnsafeSupplier) {

		_completionDateSupplier = () -> {
			try {
				return completionDateUnsafeSupplier.get();
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
	protected Date completionDate;

	@JsonIgnore
	private Supplier<Date> _completionDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getCreatedDate() {
		if (_createdDateSupplier != null) {
			createdDate = _createdDateSupplier.get();

			_createdDateSupplier = null;
		}

		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;

		_createdDateSupplier = null;
	}

	@JsonIgnore
	public void setCreatedDate(
		UnsafeSupplier<Date, Exception> createdDateUnsafeSupplier) {

		_createdDateSupplier = () -> {
			try {
				return createdDateUnsafeSupplier.get();
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
	protected Date createdDate;

	@JsonIgnore
	private Supplier<Date> _createdDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExecuteStatus() {
		if (_executeStatusSupplier != null) {
			executeStatus = _executeStatusSupplier.get();

			_executeStatusSupplier = null;
		}

		return executeStatus;
	}

	public void setExecuteStatus(String executeStatus) {
		this.executeStatus = executeStatus;

		_executeStatusSupplier = null;
	}

	@JsonIgnore
	public void setExecuteStatus(
		UnsafeSupplier<String, Exception> executeStatusUnsafeSupplier) {

		_executeStatusSupplier = () -> {
			try {
				return executeStatusUnsafeSupplier.get();
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
	protected String executeStatus;

	@JsonIgnore
	private Supplier<String> _executeStatusSupplier;

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
	public Integer getNumberOfItems() {
		if (_numberOfItemsSupplier != null) {
			numberOfItems = _numberOfItemsSupplier.get();

			_numberOfItemsSupplier = null;
		}

		return numberOfItems;
	}

	public void setNumberOfItems(Integer numberOfItems) {
		this.numberOfItems = numberOfItems;

		_numberOfItemsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfItems(
		UnsafeSupplier<Integer, Exception> numberOfItemsUnsafeSupplier) {

		_numberOfItemsSupplier = () -> {
			try {
				return numberOfItemsUnsafeSupplier.get();
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
	protected Integer numberOfItems;

	@JsonIgnore
	private Supplier<Integer> _numberOfItemsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	public void setType(String type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String type;

	@JsonIgnore
	private Supplier<String> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BulkActionTask)) {
			return false;
		}

		BulkActionTask bulkActionTask = (BulkActionTask)object;

		return Objects.equals(toString(), bulkActionTask.toString());
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

		String actionName = getActionName();

		if (actionName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionName\": ");

			sb.append("\"");

			sb.append(_escape(actionName));

			sb.append("\"");
		}

		String author = getAuthor();

		if (author != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"author\": ");

			sb.append("\"");

			sb.append(_escape(author));

			sb.append("\"");
		}

		Date completionDate = getCompletionDate();

		if (completionDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completionDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(completionDate));

			sb.append("\"");
		}

		Date createdDate = getCreatedDate();

		if (createdDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createdDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(createdDate));

			sb.append("\"");
		}

		String executeStatus = getExecuteStatus();

		if (executeStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"executeStatus\": ");

			sb.append("\"");

			sb.append(_escape(executeStatus));

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

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Integer numberOfItems = getNumberOfItems();

		if (numberOfItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfItems\": ");

			sb.append(numberOfItems);
		}

		String type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(type));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.cms.dto.v1_0.BulkActionTask",
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