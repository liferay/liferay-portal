/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.dto.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.function.UnsafeSupplier;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.TaskBulkSelectionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class TaskBulkSelection implements Cloneable, Serializable {

	public static TaskBulkSelection toDTO(String json) {
		return TaskBulkSelectionSerDes.toDTO(json);
	}

	public Long[] getAssigneeIds() {
		return assigneeIds;
	}

	public void setAssigneeIds(Long[] assigneeIds) {
		this.assigneeIds = assigneeIds;
	}

	public void setAssigneeIds(
		UnsafeSupplier<Long[], Exception> assigneeIdsUnsafeSupplier) {

		try {
			assigneeIds = assigneeIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] assigneeIds;

	public Long[] getInstanceIds() {
		return instanceIds;
	}

	public void setInstanceIds(Long[] instanceIds) {
		this.instanceIds = instanceIds;
	}

	public void setInstanceIds(
		UnsafeSupplier<Long[], Exception> instanceIdsUnsafeSupplier) {

		try {
			instanceIds = instanceIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] instanceIds;

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public void setProcessId(
		UnsafeSupplier<Long, Exception> processIdUnsafeSupplier) {

		try {
			processId = processIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long processId;

	public String[] getSlaStatuses() {
		return slaStatuses;
	}

	public void setSlaStatuses(String[] slaStatuses) {
		this.slaStatuses = slaStatuses;
	}

	public void setSlaStatuses(
		UnsafeSupplier<String[], Exception> slaStatusesUnsafeSupplier) {

		try {
			slaStatuses = slaStatusesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] slaStatuses;

	public String[] getTaskNames() {
		return taskNames;
	}

	public void setTaskNames(String[] taskNames) {
		this.taskNames = taskNames;
	}

	public void setTaskNames(
		UnsafeSupplier<String[], Exception> taskNamesUnsafeSupplier) {

		try {
			taskNames = taskNamesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] taskNames;

	@Override
	public TaskBulkSelection clone() throws CloneNotSupportedException {
		return (TaskBulkSelection)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TaskBulkSelection)) {
			return false;
		}

		TaskBulkSelection taskBulkSelection = (TaskBulkSelection)object;

		return Objects.equals(toString(), taskBulkSelection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TaskBulkSelectionSerDes.toJSON(this);
	}

}