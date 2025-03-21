/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.dto.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.function.UnsafeSupplier;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.ProcessMetricSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class ProcessMetric implements Cloneable, Serializable {

	public static ProcessMetric toDTO(String json) {
		return ProcessMetricSerDes.toDTO(json);
	}

	public Long getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(Long instanceCount) {
		this.instanceCount = instanceCount;
	}

	public void setInstanceCount(
		UnsafeSupplier<Long, Exception> instanceCountUnsafeSupplier) {

		try {
			instanceCount = instanceCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long instanceCount;

	public Long getOnTimeInstanceCount() {
		return onTimeInstanceCount;
	}

	public void setOnTimeInstanceCount(Long onTimeInstanceCount) {
		this.onTimeInstanceCount = onTimeInstanceCount;
	}

	public void setOnTimeInstanceCount(
		UnsafeSupplier<Long, Exception> onTimeInstanceCountUnsafeSupplier) {

		try {
			onTimeInstanceCount = onTimeInstanceCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long onTimeInstanceCount;

	public Long getOverdueInstanceCount() {
		return overdueInstanceCount;
	}

	public void setOverdueInstanceCount(Long overdueInstanceCount) {
		this.overdueInstanceCount = overdueInstanceCount;
	}

	public void setOverdueInstanceCount(
		UnsafeSupplier<Long, Exception> overdueInstanceCountUnsafeSupplier) {

		try {
			overdueInstanceCount = overdueInstanceCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long overdueInstanceCount;

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public void setProcess(
		UnsafeSupplier<Process, Exception> processUnsafeSupplier) {

		try {
			process = processUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Process process;

	public Long getUntrackedInstanceCount() {
		return untrackedInstanceCount;
	}

	public void setUntrackedInstanceCount(Long untrackedInstanceCount) {
		this.untrackedInstanceCount = untrackedInstanceCount;
	}

	public void setUntrackedInstanceCount(
		UnsafeSupplier<Long, Exception> untrackedInstanceCountUnsafeSupplier) {

		try {
			untrackedInstanceCount = untrackedInstanceCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long untrackedInstanceCount;

	@Override
	public ProcessMetric clone() throws CloneNotSupportedException {
		return (ProcessMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProcessMetric)) {
			return false;
		}

		ProcessMetric processMetric = (ProcessMetric)object;

		return Objects.equals(toString(), processMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProcessMetricSerDes.toJSON(this);
	}

}