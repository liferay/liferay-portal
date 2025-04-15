/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.client.dto.v1_0;

import com.liferay.headless.batch.engine.client.function.UnsafeSupplier;
import com.liferay.headless.batch.engine.client.serdes.v1_0.ImportTaskSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Ivica Cardic
 * @generated
 */
@Generated("")
public class ImportTask implements Cloneable, Serializable {

	public static ImportTask toDTO(String json) {
		return ImportTaskSerDes.toDTO(json);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setClassName(
		UnsafeSupplier<String, Exception> classNameUnsafeSupplier) {

		try {
			className = classNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String className;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setContentType(
		UnsafeSupplier<String, Exception> contentTypeUnsafeSupplier) {

		try {
			contentType = contentTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String contentType;

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setEndTime(
		UnsafeSupplier<Date, Exception> endTimeUnsafeSupplier) {

		try {
			endTime = endTimeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date endTime;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setErrorMessage(
		UnsafeSupplier<String, Exception> errorMessageUnsafeSupplier) {

		try {
			errorMessage = errorMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String errorMessage;

	public ExecuteStatus getExecuteStatus() {
		return executeStatus;
	}

	public String getExecuteStatusAsString() {
		if (executeStatus == null) {
			return null;
		}

		return executeStatus.toString();
	}

	public void setExecuteStatus(ExecuteStatus executeStatus) {
		this.executeStatus = executeStatus;
	}

	public void setExecuteStatus(
		UnsafeSupplier<ExecuteStatus, Exception> executeStatusUnsafeSupplier) {

		try {
			executeStatus = executeStatusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ExecuteStatus executeStatus;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public FailedItem[] getFailedItems() {
		return failedItems;
	}

	public void setFailedItems(FailedItem[] failedItems) {
		this.failedItems = failedItems;
	}

	public void setFailedItems(
		UnsafeSupplier<FailedItem[], Exception> failedItemsUnsafeSupplier) {

		try {
			failedItems = failedItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FailedItem[] failedItems;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public ImportStrategy getImportStrategy() {
		return importStrategy;
	}

	public String getImportStrategyAsString() {
		if (importStrategy == null) {
			return null;
		}

		return importStrategy.toString();
	}

	public void setImportStrategy(ImportStrategy importStrategy) {
		this.importStrategy = importStrategy;
	}

	public void setImportStrategy(
		UnsafeSupplier<ImportStrategy, Exception>
			importStrategyUnsafeSupplier) {

		try {
			importStrategy = importStrategyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ImportStrategy importStrategy;

	public Operation getOperation() {
		return operation;
	}

	public String getOperationAsString() {
		if (operation == null) {
			return null;
		}

		return operation.toString();
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public void setOperation(
		UnsafeSupplier<Operation, Exception> operationUnsafeSupplier) {

		try {
			operation = operationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Operation operation;

	public Integer getProcessedItemsCount() {
		return processedItemsCount;
	}

	public void setProcessedItemsCount(Integer processedItemsCount) {
		this.processedItemsCount = processedItemsCount;
	}

	public void setProcessedItemsCount(
		UnsafeSupplier<Integer, Exception> processedItemsCountUnsafeSupplier) {

		try {
			processedItemsCount = processedItemsCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer processedItemsCount;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setStartTime(
		UnsafeSupplier<Date, Exception> startTimeUnsafeSupplier) {

		try {
			startTime = startTimeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date startTime;

	public Integer getTotalItemsCount() {
		return totalItemsCount;
	}

	public void setTotalItemsCount(Integer totalItemsCount) {
		this.totalItemsCount = totalItemsCount;
	}

	public void setTotalItemsCount(
		UnsafeSupplier<Integer, Exception> totalItemsCountUnsafeSupplier) {

		try {
			totalItemsCount = totalItemsCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer totalItemsCount;

	@Override
	public ImportTask clone() throws CloneNotSupportedException {
		return (ImportTask)super.clone();
	}

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
		return ImportTaskSerDes.toJSON(this);
	}

	public static enum ExecuteStatus {

		COMPLETED("COMPLETED"), FAILED("FAILED"), INITIAL("INITIAL"),
		STARTED("STARTED");

		public static ExecuteStatus create(String value) {
			for (ExecuteStatus executeStatus : values()) {
				if (Objects.equals(executeStatus.getValue(), value) ||
					Objects.equals(executeStatus.name(), value)) {

					return executeStatus;
				}
			}

			return null;
		}

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

	public static enum ImportStrategy {

		ON_ERROR_CONTINUE("ON_ERROR_CONTINUE"), ON_ERROR_FAIL("ON_ERROR_FAIL");

		public static ImportStrategy create(String value) {
			for (ImportStrategy importStrategy : values()) {
				if (Objects.equals(importStrategy.getValue(), value) ||
					Objects.equals(importStrategy.name(), value)) {

					return importStrategy;
				}
			}

			return null;
		}

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

	public static enum Operation {

		CREATE("CREATE"), DELETE("DELETE"), UPDATE("UPDATE");

		public static Operation create(String value) {
			for (Operation operation : values()) {
				if (Objects.equals(operation.getValue(), value) ||
					Objects.equals(operation.name(), value)) {

					return operation;
				}
			}

			return null;
		}

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

}