/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.IndividualSegmentMembershipChangeMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class IndividualSegmentMembershipChangeMetric
	implements Cloneable, Serializable {

	public static IndividualSegmentMembershipChangeMetric toDTO(String json) {
		return IndividualSegmentMembershipChangeMetricSerDes.toDTO(json);
	}

	public Metric getAddedIndividuals() {
		return addedIndividuals;
	}

	public void setAddedIndividuals(Metric addedIndividuals) {
		this.addedIndividuals = addedIndividuals;
	}

	public void setAddedIndividuals(
		UnsafeSupplier<Metric, Exception> addedIndividualsUnsafeSupplier) {

		try {
			addedIndividuals = addedIndividualsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Metric addedIndividuals;

	public Metric getIndividuals() {
		return individuals;
	}

	public void setIndividuals(Metric individuals) {
		this.individuals = individuals;
	}

	public void setIndividuals(
		UnsafeSupplier<Metric, Exception> individualsUnsafeSupplier) {

		try {
			individuals = individualsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Metric individuals;

	public Metric getKnownIndividuals() {
		return knownIndividuals;
	}

	public void setKnownIndividuals(Metric knownIndividuals) {
		this.knownIndividuals = knownIndividuals;
	}

	public void setKnownIndividuals(
		UnsafeSupplier<Metric, Exception> knownIndividualsUnsafeSupplier) {

		try {
			knownIndividuals = knownIndividualsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Metric knownIndividuals;

	public Metric getRemovedIndividuals() {
		return removedIndividuals;
	}

	public void setRemovedIndividuals(Metric removedIndividuals) {
		this.removedIndividuals = removedIndividuals;
	}

	public void setRemovedIndividuals(
		UnsafeSupplier<Metric, Exception> removedIndividualsUnsafeSupplier) {

		try {
			removedIndividuals = removedIndividualsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Metric removedIndividuals;

	@Override
	public IndividualSegmentMembershipChangeMetric clone()
		throws CloneNotSupportedException {

		return (IndividualSegmentMembershipChangeMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof IndividualSegmentMembershipChangeMetric)) {
			return false;
		}

		IndividualSegmentMembershipChangeMetric
			individualSegmentMembershipChangeMetric =
				(IndividualSegmentMembershipChangeMetric)object;

		return Objects.equals(
			toString(), individualSegmentMembershipChangeMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return IndividualSegmentMembershipChangeMetricSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:948225403