/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.security.audit.storage.model.AuditEvent;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing AuditEvent in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AuditEventCacheModel
	implements CacheModel<AuditEvent>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AuditEventCacheModel)) {
			return false;
		}

		AuditEventCacheModel auditEventCacheModel =
			(AuditEventCacheModel)object;

		if (auditEventId == auditEventCacheModel.auditEventId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, auditEventId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(65);

		sb.append("{auditEventId=");
		sb.append(auditEventId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", accountEntryId=");
		sb.append(accountEntryId);
		sb.append(", additionalInfo=");
		sb.append(additionalInfo);
		sb.append(", className=");
		sb.append(className);
		sb.append(", classPK=");
		sb.append(classPK);
		sb.append(", clientHost=");
		sb.append(clientHost);
		sb.append(", clientIP=");
		sb.append(clientIP);
		sb.append(", contextName=");
		sb.append(contextName);
		sb.append(", correlationId=");
		sb.append(correlationId);
		sb.append(", eventType=");
		sb.append(eventType);
		sb.append(", httpMethod=");
		sb.append(httpMethod);
		sb.append(", impersonated=");
		sb.append(impersonated);
		sb.append(", impersonatedUserEmailAddress=");
		sb.append(impersonatedUserEmailAddress);
		sb.append(", impersonatedUserId=");
		sb.append(impersonatedUserId);
		sb.append(", impersonatedUserName=");
		sb.append(impersonatedUserName);
		sb.append(", message=");
		sb.append(message);
		sb.append(", objectName=");
		sb.append(objectName);
		sb.append(", requestId=");
		sb.append(requestId);
		sb.append(", requestIdGenerated=");
		sb.append(requestIdGenerated);
		sb.append(", resourceAction=");
		sb.append(resourceAction);
		sb.append(", resourceType=");
		sb.append(resourceType);
		sb.append(", roles=");
		sb.append(roles);
		sb.append(", serverName=");
		sb.append(serverName);
		sb.append(", serverPort=");
		sb.append(serverPort);
		sb.append(", sessionID=");
		sb.append(sessionID);
		sb.append(", userAgent=");
		sb.append(userAgent);
		sb.append(", userEmailAddress=");
		sb.append(userEmailAddress);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public AuditEvent toEntityModel() {
		AuditEventImpl auditEventImpl = new AuditEventImpl();

		auditEventImpl.setAuditEventId(auditEventId);
		auditEventImpl.setGroupId(groupId);
		auditEventImpl.setCompanyId(companyId);
		auditEventImpl.setUserId(userId);

		if (userName == null) {
			auditEventImpl.setUserName("");
		}
		else {
			auditEventImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			auditEventImpl.setCreateDate(null);
		}
		else {
			auditEventImpl.setCreateDate(new Date(createDate));
		}

		auditEventImpl.setAccountEntryId(accountEntryId);

		if (additionalInfo == null) {
			auditEventImpl.setAdditionalInfo("");
		}
		else {
			auditEventImpl.setAdditionalInfo(additionalInfo);
		}

		if (className == null) {
			auditEventImpl.setClassName("");
		}
		else {
			auditEventImpl.setClassName(className);
		}

		if (classPK == null) {
			auditEventImpl.setClassPK("");
		}
		else {
			auditEventImpl.setClassPK(classPK);
		}

		if (clientHost == null) {
			auditEventImpl.setClientHost("");
		}
		else {
			auditEventImpl.setClientHost(clientHost);
		}

		if (clientIP == null) {
			auditEventImpl.setClientIP("");
		}
		else {
			auditEventImpl.setClientIP(clientIP);
		}

		if (contextName == null) {
			auditEventImpl.setContextName("");
		}
		else {
			auditEventImpl.setContextName(contextName);
		}

		if (correlationId == null) {
			auditEventImpl.setCorrelationId("");
		}
		else {
			auditEventImpl.setCorrelationId(correlationId);
		}

		if (eventType == null) {
			auditEventImpl.setEventType("");
		}
		else {
			auditEventImpl.setEventType(eventType);
		}

		if (httpMethod == null) {
			auditEventImpl.setHttpMethod("");
		}
		else {
			auditEventImpl.setHttpMethod(httpMethod);
		}

		auditEventImpl.setImpersonated(impersonated);

		if (impersonatedUserEmailAddress == null) {
			auditEventImpl.setImpersonatedUserEmailAddress("");
		}
		else {
			auditEventImpl.setImpersonatedUserEmailAddress(
				impersonatedUserEmailAddress);
		}

		auditEventImpl.setImpersonatedUserId(impersonatedUserId);

		if (impersonatedUserName == null) {
			auditEventImpl.setImpersonatedUserName("");
		}
		else {
			auditEventImpl.setImpersonatedUserName(impersonatedUserName);
		}

		if (message == null) {
			auditEventImpl.setMessage("");
		}
		else {
			auditEventImpl.setMessage(message);
		}

		if (objectName == null) {
			auditEventImpl.setObjectName("");
		}
		else {
			auditEventImpl.setObjectName(objectName);
		}

		if (requestId == null) {
			auditEventImpl.setRequestId("");
		}
		else {
			auditEventImpl.setRequestId(requestId);
		}

		auditEventImpl.setRequestIdGenerated(requestIdGenerated);

		if (resourceAction == null) {
			auditEventImpl.setResourceAction("");
		}
		else {
			auditEventImpl.setResourceAction(resourceAction);
		}

		if (resourceType == null) {
			auditEventImpl.setResourceType("");
		}
		else {
			auditEventImpl.setResourceType(resourceType);
		}

		if (roles == null) {
			auditEventImpl.setRoles("");
		}
		else {
			auditEventImpl.setRoles(roles);
		}

		if (serverName == null) {
			auditEventImpl.setServerName("");
		}
		else {
			auditEventImpl.setServerName(serverName);
		}

		auditEventImpl.setServerPort(serverPort);

		if (sessionID == null) {
			auditEventImpl.setSessionID("");
		}
		else {
			auditEventImpl.setSessionID(sessionID);
		}

		if (userAgent == null) {
			auditEventImpl.setUserAgent("");
		}
		else {
			auditEventImpl.setUserAgent(userAgent);
		}

		if (userEmailAddress == null) {
			auditEventImpl.setUserEmailAddress("");
		}
		else {
			auditEventImpl.setUserEmailAddress(userEmailAddress);
		}

		auditEventImpl.resetOriginalValues();

		return auditEventImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		auditEventId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();

		accountEntryId = objectInput.readLong();
		additionalInfo = (String)objectInput.readObject();
		className = objectInput.readUTF();
		classPK = objectInput.readUTF();
		clientHost = objectInput.readUTF();
		clientIP = objectInput.readUTF();
		contextName = objectInput.readUTF();
		correlationId = objectInput.readUTF();
		eventType = objectInput.readUTF();
		httpMethod = objectInput.readUTF();

		impersonated = objectInput.readBoolean();
		impersonatedUserEmailAddress = objectInput.readUTF();

		impersonatedUserId = objectInput.readLong();
		impersonatedUserName = objectInput.readUTF();
		message = objectInput.readUTF();
		objectName = objectInput.readUTF();
		requestId = objectInput.readUTF();

		requestIdGenerated = objectInput.readBoolean();
		resourceAction = objectInput.readUTF();
		resourceType = objectInput.readUTF();
		roles = (String)objectInput.readObject();
		serverName = objectInput.readUTF();

		serverPort = objectInput.readInt();
		sessionID = objectInput.readUTF();
		userAgent = objectInput.readUTF();
		userEmailAddress = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(auditEventId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);

		objectOutput.writeLong(accountEntryId);

		if (additionalInfo == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(additionalInfo);
		}

		if (className == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(className);
		}

		if (classPK == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(classPK);
		}

		if (clientHost == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(clientHost);
		}

		if (clientIP == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(clientIP);
		}

		if (contextName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(contextName);
		}

		if (correlationId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(correlationId);
		}

		if (eventType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(eventType);
		}

		if (httpMethod == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(httpMethod);
		}

		objectOutput.writeBoolean(impersonated);

		if (impersonatedUserEmailAddress == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(impersonatedUserEmailAddress);
		}

		objectOutput.writeLong(impersonatedUserId);

		if (impersonatedUserName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(impersonatedUserName);
		}

		if (message == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(message);
		}

		if (objectName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(objectName);
		}

		if (requestId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(requestId);
		}

		objectOutput.writeBoolean(requestIdGenerated);

		if (resourceAction == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(resourceAction);
		}

		if (resourceType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(resourceType);
		}

		if (roles == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(roles);
		}

		if (serverName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(serverName);
		}

		objectOutput.writeInt(serverPort);

		if (sessionID == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(sessionID);
		}

		if (userAgent == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userAgent);
		}

		if (userEmailAddress == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userEmailAddress);
		}
	}

	public long auditEventId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long accountEntryId;
	public String additionalInfo;
	public String className;
	public String classPK;
	public String clientHost;
	public String clientIP;
	public String contextName;
	public String correlationId;
	public String eventType;
	public String httpMethod;
	public boolean impersonated;
	public String impersonatedUserEmailAddress;
	public long impersonatedUserId;
	public String impersonatedUserName;
	public String message;
	public String objectName;
	public String requestId;
	public boolean requestIdGenerated;
	public String resourceAction;
	public String resourceType;
	public String roles;
	public String serverName;
	public int serverPort;
	public String sessionID;
	public String userAgent;
	public String userEmailAddress;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1646860033