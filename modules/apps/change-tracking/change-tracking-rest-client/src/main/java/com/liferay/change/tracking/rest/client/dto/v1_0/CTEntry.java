/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.client.dto.v1_0;

import com.liferay.change.tracking.rest.client.function.UnsafeSupplier;
import com.liferay.change.tracking.rest.client.serdes.v1_0.CTEntrySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author David Truong
 * @generated
 */
@Generated("")
public class CTEntry implements Cloneable, Serializable {

	public static CTEntry toDTO(String json) {
		return CTEntrySerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public void setChangeType(
		UnsafeSupplier<String, Exception> changeTypeUnsafeSupplier) {

		try {
			changeType = changeTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String changeType;

	public Long getCtCollectionId() {
		return ctCollectionId;
	}

	public void setCtCollectionId(Long ctCollectionId) {
		this.ctCollectionId = ctCollectionId;
	}

	public void setCtCollectionId(
		UnsafeSupplier<Long, Exception> ctCollectionIdUnsafeSupplier) {

		try {
			ctCollectionId = ctCollectionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long ctCollectionId;

	public String getCtCollectionName() {
		return ctCollectionName;
	}

	public void setCtCollectionName(String ctCollectionName) {
		this.ctCollectionName = ctCollectionName;
	}

	public void setCtCollectionName(
		UnsafeSupplier<String, Exception> ctCollectionNameUnsafeSupplier) {

		try {
			ctCollectionName = ctCollectionNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String ctCollectionName;

	public Status getCtCollectionStatus() {
		return ctCollectionStatus;
	}

	public void setCtCollectionStatus(Status ctCollectionStatus) {
		this.ctCollectionStatus = ctCollectionStatus;
	}

	public void setCtCollectionStatus(
		UnsafeSupplier<Status, Exception> ctCollectionStatusUnsafeSupplier) {

		try {
			ctCollectionStatus = ctCollectionStatusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Status ctCollectionStatus;

	public Date getCtCollectionStatusDate() {
		return ctCollectionStatusDate;
	}

	public void setCtCollectionStatusDate(Date ctCollectionStatusDate) {
		this.ctCollectionStatusDate = ctCollectionStatusDate;
	}

	public void setCtCollectionStatusDate(
		UnsafeSupplier<Date, Exception> ctCollectionStatusDateUnsafeSupplier) {

		try {
			ctCollectionStatusDate = ctCollectionStatusDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date ctCollectionStatusDate;

	public String getCtCollectionStatusUserName() {
		return ctCollectionStatusUserName;
	}

	public void setCtCollectionStatusUserName(
		String ctCollectionStatusUserName) {

		this.ctCollectionStatusUserName = ctCollectionStatusUserName;
	}

	public void setCtCollectionStatusUserName(
		UnsafeSupplier<String, Exception>
			ctCollectionStatusUserNameUnsafeSupplier) {

		try {
			ctCollectionStatusUserName =
				ctCollectionStatusUserNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String ctCollectionStatusUserName;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public Boolean getHideable() {
		return hideable;
	}

	public void setHideable(Boolean hideable) {
		this.hideable = hideable;
	}

	public void setHideable(
		UnsafeSupplier<Boolean, Exception> hideableUnsafeSupplier) {

		try {
			hideable = hideableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean hideable;

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

	public Long getModelClassNameId() {
		return modelClassNameId;
	}

	public void setModelClassNameId(Long modelClassNameId) {
		this.modelClassNameId = modelClassNameId;
	}

	public void setModelClassNameId(
		UnsafeSupplier<Long, Exception> modelClassNameIdUnsafeSupplier) {

		try {
			modelClassNameId = modelClassNameIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long modelClassNameId;

	public Long getModelClassPK() {
		return modelClassPK;
	}

	public void setModelClassPK(Long modelClassPK) {
		this.modelClassPK = modelClassPK;
	}

	public void setModelClassPK(
		UnsafeSupplier<Long, Exception> modelClassPKUnsafeSupplier) {

		try {
			modelClassPK = modelClassPKUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long modelClassPK;

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public void setOwnerId(
		UnsafeSupplier<Long, Exception> ownerIdUnsafeSupplier) {

		try {
			ownerId = ownerIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long ownerId;

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public void setOwnerName(
		UnsafeSupplier<String, Exception> ownerNameUnsafeSupplier) {

		try {
			ownerName = ownerNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String ownerName;

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		try {
			siteId = siteIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long siteId;

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public void setSiteName(
		UnsafeSupplier<String, Exception> siteNameUnsafeSupplier) {

		try {
			siteName = siteNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String siteName;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<Status, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Status status;

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public void setStatusMessage(
		UnsafeSupplier<String, Exception> statusMessageUnsafeSupplier) {

		try {
			statusMessage = statusMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String statusMessage;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String title;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setTypeName(
		UnsafeSupplier<String, Exception> typeNameUnsafeSupplier) {

		try {
			typeName = typeNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String typeName;

	@Override
	public CTEntry clone() throws CloneNotSupportedException {
		return (CTEntry)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CTEntry)) {
			return false;
		}

		CTEntry ctEntry = (CTEntry)object;

		return Objects.equals(toString(), ctEntry.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CTEntrySerDes.toJSON(this);
	}

}