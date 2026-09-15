/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.IndividualSegmentMembershipChangeSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class IndividualSegmentMembershipChange
	implements Cloneable, Serializable {

	public static IndividualSegmentMembershipChange toDTO(String json) {
		return IndividualSegmentMembershipChangeSerDes.toDTO(json);
	}

	public Date getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}

	public void setDateChanged(
		UnsafeSupplier<Date, Exception> dateChangedUnsafeSupplier) {

		try {
			dateChanged = dateChangedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateChanged;

	public Date getDateFirst() {
		return dateFirst;
	}

	public void setDateFirst(Date dateFirst) {
		this.dateFirst = dateFirst;
	}

	public void setDateFirst(
		UnsafeSupplier<Date, Exception> dateFirstUnsafeSupplier) {

		try {
			dateFirst = dateFirstUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateFirst;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String id;

	public String getIndividualEmail() {
		return individualEmail;
	}

	public void setIndividualEmail(String individualEmail) {
		this.individualEmail = individualEmail;
	}

	public void setIndividualEmail(
		UnsafeSupplier<String, Exception> individualEmailUnsafeSupplier) {

		try {
			individualEmail = individualEmailUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String individualEmail;

	public String getIndividualId() {
		return individualId;
	}

	public void setIndividualId(String individualId) {
		this.individualId = individualId;
	}

	public void setIndividualId(
		UnsafeSupplier<String, Exception> individualIdUnsafeSupplier) {

		try {
			individualId = individualIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String individualId;

	public String getIndividualName() {
		return individualName;
	}

	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}

	public void setIndividualName(
		UnsafeSupplier<String, Exception> individualNameUnsafeSupplier) {

		try {
			individualName = individualNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String individualName;

	public String getIndividualSegmentId() {
		return individualSegmentId;
	}

	public void setIndividualSegmentId(String individualSegmentId) {
		this.individualSegmentId = individualSegmentId;
	}

	public void setIndividualSegmentId(
		UnsafeSupplier<String, Exception> individualSegmentIdUnsafeSupplier) {

		try {
			individualSegmentId = individualSegmentIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String individualSegmentId;

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void setOperation(
		UnsafeSupplier<String, Exception> operationUnsafeSupplier) {

		try {
			operation = operationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String operation;

	@Override
	public IndividualSegmentMembershipChange clone()
		throws CloneNotSupportedException {

		return (IndividualSegmentMembershipChange)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof IndividualSegmentMembershipChange)) {
			return false;
		}

		IndividualSegmentMembershipChange individualSegmentMembershipChange =
			(IndividualSegmentMembershipChange)object;

		return Objects.equals(
			toString(), individualSegmentMembershipChange.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return IndividualSegmentMembershipChangeSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1701606938