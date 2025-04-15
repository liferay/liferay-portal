/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

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
 * @author Ivica Cardic
 * @generated
 */
@Generated("")
@GraphQLName("ImportTask")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ImportTask")
public class ImportTask implements Serializable {

	public static ImportTask toDTO(String json) {
		return ObjectMapperUtil.readValue(ImportTask.class, json);
	}

	public static ImportTask unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ImportTask.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The item class name for which data will be processed in batch.",
		example = "com.liferay.headless.delivery.dto.v1_0.BlogPosting"
	)
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

	@GraphQLField(
		description = "The item class name for which data will be processed in batch."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String className;

	@JsonIgnore
	private Supplier<String> _classNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The file content type.", example = "JSON"
	)
	public String getContentType() {
		if (_contentTypeSupplier != null) {
			contentType = _contentTypeSupplier.get();

			_contentTypeSupplier = null;
		}

		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;

		_contentTypeSupplier = null;
	}

	@JsonIgnore
	public void setContentType(
		UnsafeSupplier<String, Exception> contentTypeUnsafeSupplier) {

		_contentTypeSupplier = () -> {
			try {
				return contentTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The file content type.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String contentType;

	@JsonIgnore
	private Supplier<String> _contentTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The end time of import task operation.",
		example = "2019-27-09'T'08:33:33'Z'"
	)
	public Date getEndTime() {
		if (_endTimeSupplier != null) {
			endTime = _endTimeSupplier.get();

			_endTimeSupplier = null;
		}

		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;

		_endTimeSupplier = null;
	}

	@JsonIgnore
	public void setEndTime(
		UnsafeSupplier<Date, Exception> endTimeUnsafeSupplier) {

		_endTimeSupplier = () -> {
			try {
				return endTimeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The end time of import task operation.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date endTime;

	@JsonIgnore
	private Supplier<Date> _endTimeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The error message in case of import task's failed execution.",
		example = "File import failed"
	)
	public String getErrorMessage() {
		if (_errorMessageSupplier != null) {
			errorMessage = _errorMessageSupplier.get();

			_errorMessageSupplier = null;
		}

		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;

		_errorMessageSupplier = null;
	}

	@JsonIgnore
	public void setErrorMessage(
		UnsafeSupplier<String, Exception> errorMessageUnsafeSupplier) {

		_errorMessageSupplier = () -> {
			try {
				return errorMessageUnsafeSupplier.get();
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
		description = "The error message in case of import task's failed execution."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String errorMessage;

	@JsonIgnore
	private Supplier<String> _errorMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The status of import task's execution.",
		example = "INITIALIZED"
	)
	@JsonGetter("executeStatus")
	@Valid
	public ExecuteStatus getExecuteStatus() {
		if (_executeStatusSupplier != null) {
			executeStatus = _executeStatusSupplier.get();

			_executeStatusSupplier = null;
		}

		return executeStatus;
	}

	@JsonIgnore
	public String getExecuteStatusAsString() {
		ExecuteStatus executeStatus = getExecuteStatus();

		if (executeStatus == null) {
			return null;
		}

		return executeStatus.toString();
	}

	public void setExecuteStatus(ExecuteStatus executeStatus) {
		this.executeStatus = executeStatus;

		_executeStatusSupplier = null;
	}

	@JsonIgnore
	public void setExecuteStatus(
		UnsafeSupplier<ExecuteStatus, Exception> executeStatusUnsafeSupplier) {

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

	@GraphQLField(description = "The status of import task's execution.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ExecuteStatus executeStatus;

	@JsonIgnore
	private Supplier<ExecuteStatus> _executeStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The optional external key of this account."
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

	@GraphQLField(description = "The optional external key of this account.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public FailedItem[] getFailedItems() {
		if (_failedItemsSupplier != null) {
			failedItems = _failedItemsSupplier.get();

			_failedItemsSupplier = null;
		}

		return failedItems;
	}

	public void setFailedItems(FailedItem[] failedItems) {
		this.failedItems = failedItems;

		_failedItemsSupplier = null;
	}

	@JsonIgnore
	public void setFailedItems(
		UnsafeSupplier<FailedItem[], Exception> failedItemsUnsafeSupplier) {

		_failedItemsSupplier = () -> {
			try {
				return failedItemsUnsafeSupplier.get();
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
	protected FailedItem[] failedItems;

	@JsonIgnore
	private Supplier<FailedItem[]> _failedItemsSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The task's ID.", example = "30130"
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

	@GraphQLField(description = "The task's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Defines if import task will fail when error occurs or continue importing rest of the items."
	)
	@JsonGetter("importStrategy")
	@Valid
	public ImportStrategy getImportStrategy() {
		if (_importStrategySupplier != null) {
			importStrategy = _importStrategySupplier.get();

			_importStrategySupplier = null;
		}

		return importStrategy;
	}

	@JsonIgnore
	public String getImportStrategyAsString() {
		ImportStrategy importStrategy = getImportStrategy();

		if (importStrategy == null) {
			return null;
		}

		return importStrategy.toString();
	}

	public void setImportStrategy(ImportStrategy importStrategy) {
		this.importStrategy = importStrategy;

		_importStrategySupplier = null;
	}

	@JsonIgnore
	public void setImportStrategy(
		UnsafeSupplier<ImportStrategy, Exception>
			importStrategyUnsafeSupplier) {

		_importStrategySupplier = () -> {
			try {
				return importStrategyUnsafeSupplier.get();
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
		description = "Defines if import task will fail when error occurs or continue importing rest of the items."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ImportStrategy importStrategy;

	@JsonIgnore
	private Supplier<ImportStrategy> _importStrategySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The operation of import task.", example = "CREATE"
	)
	@JsonGetter("operation")
	@Valid
	public Operation getOperation() {
		if (_operationSupplier != null) {
			operation = _operationSupplier.get();

			_operationSupplier = null;
		}

		return operation;
	}

	@JsonIgnore
	public String getOperationAsString() {
		Operation operation = getOperation();

		if (operation == null) {
			return null;
		}

		return operation.toString();
	}

	public void setOperation(Operation operation) {
		this.operation = operation;

		_operationSupplier = null;
	}

	@JsonIgnore
	public void setOperation(
		UnsafeSupplier<Operation, Exception> operationUnsafeSupplier) {

		_operationSupplier = () -> {
			try {
				return operationUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The operation of import task.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Operation operation;

	@JsonIgnore
	private Supplier<Operation> _operationSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of items processed by import task opeartion.",
		example = "100"
	)
	public Integer getProcessedItemsCount() {
		if (_processedItemsCountSupplier != null) {
			processedItemsCount = _processedItemsCountSupplier.get();

			_processedItemsCountSupplier = null;
		}

		return processedItemsCount;
	}

	public void setProcessedItemsCount(Integer processedItemsCount) {
		this.processedItemsCount = processedItemsCount;

		_processedItemsCountSupplier = null;
	}

	@JsonIgnore
	public void setProcessedItemsCount(
		UnsafeSupplier<Integer, Exception> processedItemsCountUnsafeSupplier) {

		_processedItemsCountSupplier = () -> {
			try {
				return processedItemsCountUnsafeSupplier.get();
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
		description = "Number of items processed by import task opeartion."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer processedItemsCount;

	@JsonIgnore
	private Supplier<Integer> _processedItemsCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The start time of import task operation.",
		example = "2019-27-09'T'08:23:33'Z'"
	)
	public Date getStartTime() {
		if (_startTimeSupplier != null) {
			startTime = _startTimeSupplier.get();

			_startTimeSupplier = null;
		}

		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;

		_startTimeSupplier = null;
	}

	@JsonIgnore
	public void setStartTime(
		UnsafeSupplier<Date, Exception> startTimeUnsafeSupplier) {

		_startTimeSupplier = () -> {
			try {
				return startTimeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The start time of import task operation.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date startTime;

	@JsonIgnore
	private Supplier<Date> _startTimeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Total number of items that will be processed by import task operation.",
		example = "1000"
	)
	public Integer getTotalItemsCount() {
		if (_totalItemsCountSupplier != null) {
			totalItemsCount = _totalItemsCountSupplier.get();

			_totalItemsCountSupplier = null;
		}

		return totalItemsCount;
	}

	public void setTotalItemsCount(Integer totalItemsCount) {
		this.totalItemsCount = totalItemsCount;

		_totalItemsCountSupplier = null;
	}

	@JsonIgnore
	public void setTotalItemsCount(
		UnsafeSupplier<Integer, Exception> totalItemsCountUnsafeSupplier) {

		_totalItemsCountSupplier = () -> {
			try {
				return totalItemsCountUnsafeSupplier.get();
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
		description = "Total number of items that will be processed by import task operation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer totalItemsCount;

	@JsonIgnore
	private Supplier<Integer> _totalItemsCountSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ImportTask)) {
			return false;
		}

		ImportTask importTask = (ImportTask)object;

		return Objects.equals(toString(), importTask.toString());
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

		String contentType = getContentType();

		if (contentType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(contentType));

			sb.append("\"");
		}

		Date endTime = getEndTime();

		if (endTime != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endTime\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(endTime));

			sb.append("\"");
		}

		String errorMessage = getErrorMessage();

		if (errorMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append("\"");

			sb.append(_escape(errorMessage));

			sb.append("\"");
		}

		ExecuteStatus executeStatus = getExecuteStatus();

		if (executeStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"executeStatus\": ");

			sb.append("\"");

			sb.append(executeStatus);

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

		FailedItem[] failedItems = getFailedItems();

		if (failedItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"failedItems\": ");

			sb.append("[");

			for (int i = 0; i < failedItems.length; i++) {
				sb.append(String.valueOf(failedItems[i]));

				if ((i + 1) < failedItems.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		ImportStrategy importStrategy = getImportStrategy();

		if (importStrategy != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"importStrategy\": ");

			sb.append("\"");

			sb.append(importStrategy);

			sb.append("\"");
		}

		Operation operation = getOperation();

		if (operation != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"operation\": ");

			sb.append("\"");

			sb.append(operation);

			sb.append("\"");
		}

		Integer processedItemsCount = getProcessedItemsCount();

		if (processedItemsCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"processedItemsCount\": ");

			sb.append(processedItemsCount);
		}

		Date startTime = getStartTime();

		if (startTime != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startTime\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(startTime));

			sb.append("\"");
		}

		Integer totalItemsCount = getTotalItemsCount();

		if (totalItemsCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalItemsCount\": ");

			sb.append(totalItemsCount);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.batch.engine.dto.v1_0.ImportTask",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("ExecuteStatus")
	public static enum ExecuteStatus {

		COMPLETED("COMPLETED"), FAILED("FAILED"), INITIAL("INITIAL"),
		STARTED("STARTED");

		@JsonCreator
		public static ExecuteStatus create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ExecuteStatus executeStatus : values()) {
				if (Objects.equals(executeStatus.getValue(), value)) {
					return executeStatus;
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

		private ExecuteStatus(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("ImportStrategy")
	public static enum ImportStrategy {

		ON_ERROR_CONTINUE("ON_ERROR_CONTINUE"), ON_ERROR_FAIL("ON_ERROR_FAIL");

		@JsonCreator
		public static ImportStrategy create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ImportStrategy importStrategy : values()) {
				if (Objects.equals(importStrategy.getValue(), value)) {
					return importStrategy;
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

		private ImportStrategy(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Operation")
	public static enum Operation {

		CREATE("CREATE"), DELETE("DELETE"), UPDATE("UPDATE");

		@JsonCreator
		public static Operation create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Operation operation : values()) {
				if (Objects.equals(operation.getValue(), value)) {
					return operation;
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

		private Operation(String value) {
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