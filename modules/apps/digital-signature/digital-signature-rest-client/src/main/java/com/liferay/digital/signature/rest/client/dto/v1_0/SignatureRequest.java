/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.client.dto.v1_0;

import com.liferay.digital.signature.rest.client.function.UnsafeSupplier;
import com.liferay.digital.signature.rest.client.serdes.v1_0.SignatureRequestSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class SignatureRequest implements Cloneable, Serializable {

	public static SignatureRequest toDTO(String json) {
		return SignatureRequestSerDes.toDTO(json);
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

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator creator;

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

	public String getEmailMessage() {
		return emailMessage;
	}

	public void setEmailMessage(String emailMessage) {
		this.emailMessage = emailMessage;
	}

	public void setEmailMessage(
		UnsafeSupplier<String, Exception> emailMessageUnsafeSupplier) {

		try {
			emailMessage = emailMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String emailMessage;

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public void setEmailSubject(
		UnsafeSupplier<String, Exception> emailSubjectUnsafeSupplier) {

		try {
			emailSubject = emailSubjectUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String emailSubject;

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public void setExpirationDate(
		UnsafeSupplier<Date, Exception> expirationDateUnsafeSupplier) {

		try {
			expirationDate = expirationDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date expirationDate;

	public Integer getExpireAfter() {
		return expireAfter;
	}

	public void setExpireAfter(Integer expireAfter) {
		this.expireAfter = expireAfter;
	}

	public void setExpireAfter(
		UnsafeSupplier<Integer, Exception> expireAfterUnsafeSupplier) {

		try {
			expireAfter = expireAfterUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer expireAfter;

	public FileEntryBrief[] getFileEntryBriefs() {
		return fileEntryBriefs;
	}

	public void setFileEntryBriefs(FileEntryBrief[] fileEntryBriefs) {
		this.fileEntryBriefs = fileEntryBriefs;
	}

	public void setFileEntryBriefs(
		UnsafeSupplier<FileEntryBrief[], Exception>
			fileEntryBriefsUnsafeSupplier) {

		try {
			fileEntryBriefs = fileEntryBriefsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FileEntryBrief[] fileEntryBriefs;

	public Long[] getFileEntryIds() {
		return fileEntryIds;
	}

	public void setFileEntryIds(Long[] fileEntryIds) {
		this.fileEntryIds = fileEntryIds;
	}

	public void setFileEntryIds(
		UnsafeSupplier<Long[], Exception> fileEntryIdsUnsafeSupplier) {

		try {
			fileEntryIds = fileEntryIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] fileEntryIds;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public Boolean getSendNotifications() {
		return sendNotifications;
	}

	public void setSendNotifications(Boolean sendNotifications) {
		this.sendNotifications = sendNotifications;
	}

	public void setSendNotifications(
		UnsafeSupplier<Boolean, Exception> sendNotificationsUnsafeSupplier) {

		try {
			sendNotifications = sendNotificationsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean sendNotifications;

	public SignatureRequestRecipient[] getSignatureRequestRecipients() {
		return signatureRequestRecipients;
	}

	public void setSignatureRequestRecipients(
		SignatureRequestRecipient[] signatureRequestRecipients) {

		this.signatureRequestRecipients = signatureRequestRecipients;
	}

	public void setSignatureRequestRecipients(
		UnsafeSupplier<SignatureRequestRecipient[], Exception>
			signatureRequestRecipientsUnsafeSupplier) {

		try {
			signatureRequestRecipients =
				signatureRequestRecipientsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SignatureRequestRecipient[] signatureRequestRecipients;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<String, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String status;

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public void setStatusDate(
		UnsafeSupplier<Date, Exception> statusDateUnsafeSupplier) {

		try {
			statusDate = statusDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date statusDate;

	@Override
	public SignatureRequest clone() throws CloneNotSupportedException {
		return (SignatureRequest)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SignatureRequest)) {
			return false;
		}

		SignatureRequest signatureRequest = (SignatureRequest)object;

		return Objects.equals(toString(), signatureRequest.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SignatureRequestSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1516081372