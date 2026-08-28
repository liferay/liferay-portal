/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.rest.client.dto.v1_0;

import com.liferay.portal.security.audit.rest.client.function.UnsafeSupplier;
import com.liferay.portal.security.audit.rest.client.serdes.v1_0.AuditEventSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class AuditEvent implements Cloneable, Serializable {

	public static AuditEvent toDTO(String json) {
		return AuditEventSerDes.toDTO(json);
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public void setAccountId(
		UnsafeSupplier<Long, Exception> accountIdUnsafeSupplier) {

		try {
			accountId = accountIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long accountId;

	public Map<String, ?> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Map<String, ?> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public void setAdditionalInfo(
		UnsafeSupplier<Map<String, ?>, Exception>
			additionalInfoUnsafeSupplier) {

		try {
			additionalInfo = additionalInfoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ?> additionalInfo;

	public String getClientHost() {
		return clientHost;
	}

	public void setClientHost(String clientHost) {
		this.clientHost = clientHost;
	}

	public void setClientHost(
		UnsafeSupplier<String, Exception> clientHostUnsafeSupplier) {

		try {
			clientHost = clientHostUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String clientHost;

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public void setClientIP(
		UnsafeSupplier<String, Exception> clientIPUnsafeSupplier) {

		try {
			clientIP = clientIPUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String clientIP;

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public void setContextName(
		UnsafeSupplier<String, Exception> contextNameUnsafeSupplier) {

		try {
			contextName = contextNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String contextName;

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public void setCorrelationId(
		UnsafeSupplier<String, Exception> correlationIdUnsafeSupplier) {

		try {
			correlationId = correlationIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String correlationId;

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

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public void setEntityId(
		UnsafeSupplier<Long, Exception> entityIdUnsafeSupplier) {

		try {
			entityId = entityIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long entityId;

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public void setEntityType(
		UnsafeSupplier<String, Exception> entityTypeUnsafeSupplier) {

		try {
			entityType = entityTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String entityType;

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public void setEventType(
		UnsafeSupplier<String, Exception> eventTypeUnsafeSupplier) {

		try {
			eventType = eventTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String eventType;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public void setGroupId(
		UnsafeSupplier<Long, Exception> groupIdUnsafeSupplier) {

		try {
			groupId = groupIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long groupId;

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public void setHttpMethod(
		UnsafeSupplier<String, Exception> httpMethodUnsafeSupplier) {

		try {
			httpMethod = httpMethodUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String httpMethod;

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

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public void setObjectName(
		UnsafeSupplier<String, Exception> objectNameUnsafeSupplier) {

		try {
			objectName = objectNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectName;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setRequestId(
		UnsafeSupplier<String, Exception> requestIdUnsafeSupplier) {

		try {
			requestId = requestIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String requestId;

	public Boolean getRequestIdGenerated() {
		return requestIdGenerated;
	}

	public void setRequestIdGenerated(Boolean requestIdGenerated) {
		this.requestIdGenerated = requestIdGenerated;
	}

	public void setRequestIdGenerated(
		UnsafeSupplier<Boolean, Exception> requestIdGeneratedUnsafeSupplier) {

		try {
			requestIdGenerated = requestIdGeneratedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean requestIdGenerated;

	public String getResourceAction() {
		return resourceAction;
	}

	public void setResourceAction(String resourceAction) {
		this.resourceAction = resourceAction;
	}

	public void setResourceAction(
		UnsafeSupplier<String, Exception> resourceActionUnsafeSupplier) {

		try {
			resourceAction = resourceActionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String resourceAction;

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public void setResourceType(
		UnsafeSupplier<String, Exception> resourceTypeUnsafeSupplier) {

		try {
			resourceType = resourceTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String resourceType;

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public void setRoles(
		UnsafeSupplier<String, Exception> rolesUnsafeSupplier) {

		try {
			roles = rolesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String roles;

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setServerName(
		UnsafeSupplier<String, Exception> serverNameUnsafeSupplier) {

		try {
			serverName = serverNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String serverName;

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setUserAgent(
		UnsafeSupplier<String, Exception> userAgentUnsafeSupplier) {

		try {
			userAgent = userAgentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String userAgent;

	@Override
	public AuditEvent clone() throws CloneNotSupportedException {
		return (AuditEvent)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AuditEvent)) {
			return false;
		}

		AuditEvent auditEvent = (AuditEvent)object;

		return Objects.equals(toString(), auditEvent.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AuditEventSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-477295016