/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.dto.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.function.UnsafeSupplier;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.NodeMetricSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class NodeMetric implements Cloneable, Serializable {

	public static NodeMetric toDTO(String json) {
		return NodeMetricSerDes.toDTO(json);
	}

	public Long getBreachedInstanceCount() {
		return breachedInstanceCount;
	}

	public void setBreachedInstanceCount(Long breachedInstanceCount) {
		this.breachedInstanceCount = breachedInstanceCount;
	}

	public void setBreachedInstanceCount(
		UnsafeSupplier<Long, Exception> breachedInstanceCountUnsafeSupplier) {

		try {
			breachedInstanceCount = breachedInstanceCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long breachedInstanceCount;

	public Double getBreachedInstancePercentage() {
		return breachedInstancePercentage;
	}

	public void setBreachedInstancePercentage(
		Double breachedInstancePercentage) {

		this.breachedInstancePercentage = breachedInstancePercentage;
	}

	public void setBreachedInstancePercentage(
		UnsafeSupplier<Double, Exception>
			breachedInstancePercentageUnsafeSupplier) {

		try {
			breachedInstancePercentage =
				breachedInstancePercentageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double breachedInstancePercentage;

	public Long getDurationAvg() {
		return durationAvg;
	}

	public void setDurationAvg(Long durationAvg) {
		this.durationAvg = durationAvg;
	}

	public void setDurationAvg(
		UnsafeSupplier<Long, Exception> durationAvgUnsafeSupplier) {

		try {
			durationAvg = durationAvgUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long durationAvg;

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

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public void setNode(UnsafeSupplier<Node, Exception> nodeUnsafeSupplier) {
		try {
			node = nodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Node node;

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

	@Override
	public NodeMetric clone() throws CloneNotSupportedException {
		return (NodeMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NodeMetric)) {
			return false;
		}

		NodeMetric nodeMetric = (NodeMetric)object;

		return Objects.equals(toString(), nodeMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return NodeMetricSerDes.toJSON(this);
	}

}