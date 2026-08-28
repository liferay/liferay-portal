/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link AuditEvent}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AuditEvent
 * @generated
 */
public class AuditEventWrapper
	extends BaseModelWrapper<AuditEvent>
	implements AuditEvent, ModelWrapper<AuditEvent> {

	public AuditEventWrapper(AuditEvent auditEvent) {
		super(auditEvent);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("auditEventId", getAuditEventId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("accountEntryId", getAccountEntryId());
		attributes.put("additionalInfo", getAdditionalInfo());
		attributes.put("className", getClassName());
		attributes.put("classPK", getClassPK());
		attributes.put("clientHost", getClientHost());
		attributes.put("clientIP", getClientIP());
		attributes.put("contextName", getContextName());
		attributes.put("correlationId", getCorrelationId());
		attributes.put("eventType", getEventType());
		attributes.put("httpMethod", getHttpMethod());
		attributes.put("impersonated", isImpersonated());
		attributes.put(
			"impersonatedUserEmailAddress", getImpersonatedUserEmailAddress());
		attributes.put("impersonatedUserId", getImpersonatedUserId());
		attributes.put("impersonatedUserName", getImpersonatedUserName());
		attributes.put("message", getMessage());
		attributes.put("objectName", getObjectName());
		attributes.put("requestId", getRequestId());
		attributes.put("requestIdGenerated", isRequestIdGenerated());
		attributes.put("resourceAction", getResourceAction());
		attributes.put("resourceType", getResourceType());
		attributes.put("roles", getRoles());
		attributes.put("serverName", getServerName());
		attributes.put("serverPort", getServerPort());
		attributes.put("sessionID", getSessionID());
		attributes.put("userAgent", getUserAgent());
		attributes.put("userEmailAddress", getUserEmailAddress());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long auditEventId = (Long)attributes.get("auditEventId");

		if (auditEventId != null) {
			setAuditEventId(auditEventId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Long accountEntryId = (Long)attributes.get("accountEntryId");

		if (accountEntryId != null) {
			setAccountEntryId(accountEntryId);
		}

		String additionalInfo = (String)attributes.get("additionalInfo");

		if (additionalInfo != null) {
			setAdditionalInfo(additionalInfo);
		}

		String className = (String)attributes.get("className");

		if (className != null) {
			setClassName(className);
		}

		String classPK = (String)attributes.get("classPK");

		if (classPK != null) {
			setClassPK(classPK);
		}

		String clientHost = (String)attributes.get("clientHost");

		if (clientHost != null) {
			setClientHost(clientHost);
		}

		String clientIP = (String)attributes.get("clientIP");

		if (clientIP != null) {
			setClientIP(clientIP);
		}

		String contextName = (String)attributes.get("contextName");

		if (contextName != null) {
			setContextName(contextName);
		}

		String correlationId = (String)attributes.get("correlationId");

		if (correlationId != null) {
			setCorrelationId(correlationId);
		}

		String eventType = (String)attributes.get("eventType");

		if (eventType != null) {
			setEventType(eventType);
		}

		String httpMethod = (String)attributes.get("httpMethod");

		if (httpMethod != null) {
			setHttpMethod(httpMethod);
		}

		Boolean impersonated = (Boolean)attributes.get("impersonated");

		if (impersonated != null) {
			setImpersonated(impersonated);
		}

		String impersonatedUserEmailAddress = (String)attributes.get(
			"impersonatedUserEmailAddress");

		if (impersonatedUserEmailAddress != null) {
			setImpersonatedUserEmailAddress(impersonatedUserEmailAddress);
		}

		Long impersonatedUserId = (Long)attributes.get("impersonatedUserId");

		if (impersonatedUserId != null) {
			setImpersonatedUserId(impersonatedUserId);
		}

		String impersonatedUserName = (String)attributes.get(
			"impersonatedUserName");

		if (impersonatedUserName != null) {
			setImpersonatedUserName(impersonatedUserName);
		}

		String message = (String)attributes.get("message");

		if (message != null) {
			setMessage(message);
		}

		String objectName = (String)attributes.get("objectName");

		if (objectName != null) {
			setObjectName(objectName);
		}

		String requestId = (String)attributes.get("requestId");

		if (requestId != null) {
			setRequestId(requestId);
		}

		Boolean requestIdGenerated = (Boolean)attributes.get(
			"requestIdGenerated");

		if (requestIdGenerated != null) {
			setRequestIdGenerated(requestIdGenerated);
		}

		String resourceAction = (String)attributes.get("resourceAction");

		if (resourceAction != null) {
			setResourceAction(resourceAction);
		}

		String resourceType = (String)attributes.get("resourceType");

		if (resourceType != null) {
			setResourceType(resourceType);
		}

		String roles = (String)attributes.get("roles");

		if (roles != null) {
			setRoles(roles);
		}

		String serverName = (String)attributes.get("serverName");

		if (serverName != null) {
			setServerName(serverName);
		}

		Integer serverPort = (Integer)attributes.get("serverPort");

		if (serverPort != null) {
			setServerPort(serverPort);
		}

		String sessionID = (String)attributes.get("sessionID");

		if (sessionID != null) {
			setSessionID(sessionID);
		}

		String userAgent = (String)attributes.get("userAgent");

		if (userAgent != null) {
			setUserAgent(userAgent);
		}

		String userEmailAddress = (String)attributes.get("userEmailAddress");

		if (userEmailAddress != null) {
			setUserEmailAddress(userEmailAddress);
		}
	}

	@Override
	public AuditEvent cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the account entry ID of this audit event.
	 *
	 * @return the account entry ID of this audit event
	 */
	@Override
	public long getAccountEntryId() {
		return model.getAccountEntryId();
	}

	/**
	 * Returns the additional info of this audit event.
	 *
	 * @return the additional info of this audit event
	 */
	@Override
	public String getAdditionalInfo() {
		return model.getAdditionalInfo();
	}

	/**
	 * Returns the audit event ID of this audit event.
	 *
	 * @return the audit event ID of this audit event
	 */
	@Override
	public long getAuditEventId() {
		return model.getAuditEventId();
	}

	/**
	 * Returns the class name of this audit event.
	 *
	 * @return the class name of this audit event
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class pk of this audit event.
	 *
	 * @return the class pk of this audit event
	 */
	@Override
	public String getClassPK() {
		return model.getClassPK();
	}

	/**
	 * Returns the client host of this audit event.
	 *
	 * @return the client host of this audit event
	 */
	@Override
	public String getClientHost() {
		return model.getClientHost();
	}

	/**
	 * Returns the client ip of this audit event.
	 *
	 * @return the client ip of this audit event
	 */
	@Override
	public String getClientIP() {
		return model.getClientIP();
	}

	/**
	 * Returns the company ID of this audit event.
	 *
	 * @return the company ID of this audit event
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the context name of this audit event.
	 *
	 * @return the context name of this audit event
	 */
	@Override
	public String getContextName() {
		return model.getContextName();
	}

	/**
	 * Returns the correlation ID of this audit event.
	 *
	 * @return the correlation ID of this audit event
	 */
	@Override
	public String getCorrelationId() {
		return model.getCorrelationId();
	}

	/**
	 * Returns the create date of this audit event.
	 *
	 * @return the create date of this audit event
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the event type of this audit event.
	 *
	 * @return the event type of this audit event
	 */
	@Override
	public String getEventType() {
		return model.getEventType();
	}

	/**
	 * Returns the group ID of this audit event.
	 *
	 * @return the group ID of this audit event
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the http method of this audit event.
	 *
	 * @return the http method of this audit event
	 */
	@Override
	public String getHttpMethod() {
		return model.getHttpMethod();
	}

	/**
	 * Returns the impersonated of this audit event.
	 *
	 * @return the impersonated of this audit event
	 */
	@Override
	public boolean getImpersonated() {
		return model.getImpersonated();
	}

	/**
	 * Returns the impersonated user email address of this audit event.
	 *
	 * @return the impersonated user email address of this audit event
	 */
	@Override
	public String getImpersonatedUserEmailAddress() {
		return model.getImpersonatedUserEmailAddress();
	}

	/**
	 * Returns the impersonated user ID of this audit event.
	 *
	 * @return the impersonated user ID of this audit event
	 */
	@Override
	public long getImpersonatedUserId() {
		return model.getImpersonatedUserId();
	}

	/**
	 * Returns the impersonated user name of this audit event.
	 *
	 * @return the impersonated user name of this audit event
	 */
	@Override
	public String getImpersonatedUserName() {
		return model.getImpersonatedUserName();
	}

	/**
	 * Returns the impersonated user uuid of this audit event.
	 *
	 * @return the impersonated user uuid of this audit event
	 */
	@Override
	public String getImpersonatedUserUuid() {
		return model.getImpersonatedUserUuid();
	}

	/**
	 * Returns the message of this audit event.
	 *
	 * @return the message of this audit event
	 */
	@Override
	public String getMessage() {
		return model.getMessage();
	}

	/**
	 * Returns the object name of this audit event.
	 *
	 * @return the object name of this audit event
	 */
	@Override
	public String getObjectName() {
		return model.getObjectName();
	}

	/**
	 * Returns the primary key of this audit event.
	 *
	 * @return the primary key of this audit event
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the request ID of this audit event.
	 *
	 * @return the request ID of this audit event
	 */
	@Override
	public String getRequestId() {
		return model.getRequestId();
	}

	/**
	 * Returns the request ID generated of this audit event.
	 *
	 * @return the request ID generated of this audit event
	 */
	@Override
	public boolean getRequestIdGenerated() {
		return model.getRequestIdGenerated();
	}

	/**
	 * Returns the resource action of this audit event.
	 *
	 * @return the resource action of this audit event
	 */
	@Override
	public String getResourceAction() {
		return model.getResourceAction();
	}

	/**
	 * Returns the resource type of this audit event.
	 *
	 * @return the resource type of this audit event
	 */
	@Override
	public String getResourceType() {
		return model.getResourceType();
	}

	/**
	 * Returns the roles of this audit event.
	 *
	 * @return the roles of this audit event
	 */
	@Override
	public String getRoles() {
		return model.getRoles();
	}

	/**
	 * Returns the server name of this audit event.
	 *
	 * @return the server name of this audit event
	 */
	@Override
	public String getServerName() {
		return model.getServerName();
	}

	/**
	 * Returns the server port of this audit event.
	 *
	 * @return the server port of this audit event
	 */
	@Override
	public int getServerPort() {
		return model.getServerPort();
	}

	/**
	 * Returns the session ID of this audit event.
	 *
	 * @return the session ID of this audit event
	 */
	@Override
	public String getSessionID() {
		return model.getSessionID();
	}

	/**
	 * Returns the user agent of this audit event.
	 *
	 * @return the user agent of this audit event
	 */
	@Override
	public String getUserAgent() {
		return model.getUserAgent();
	}

	/**
	 * Returns the user email address of this audit event.
	 *
	 * @return the user email address of this audit event
	 */
	@Override
	public String getUserEmailAddress() {
		return model.getUserEmailAddress();
	}

	/**
	 * Returns the user ID of this audit event.
	 *
	 * @return the user ID of this audit event
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this audit event.
	 *
	 * @return the user name of this audit event
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this audit event.
	 *
	 * @return the user uuid of this audit event
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this audit event is impersonated.
	 *
	 * @return <code>true</code> if this audit event is impersonated; <code>false</code> otherwise
	 */
	@Override
	public boolean isImpersonated() {
		return model.isImpersonated();
	}

	/**
	 * Returns <code>true</code> if this audit event is request ID generated.
	 *
	 * @return <code>true</code> if this audit event is request ID generated; <code>false</code> otherwise
	 */
	@Override
	public boolean isRequestIdGenerated() {
		return model.isRequestIdGenerated();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the account entry ID of this audit event.
	 *
	 * @param accountEntryId the account entry ID of this audit event
	 */
	@Override
	public void setAccountEntryId(long accountEntryId) {
		model.setAccountEntryId(accountEntryId);
	}

	/**
	 * Sets the additional info of this audit event.
	 *
	 * @param additionalInfo the additional info of this audit event
	 */
	@Override
	public void setAdditionalInfo(String additionalInfo) {
		model.setAdditionalInfo(additionalInfo);
	}

	/**
	 * Sets the audit event ID of this audit event.
	 *
	 * @param auditEventId the audit event ID of this audit event
	 */
	@Override
	public void setAuditEventId(long auditEventId) {
		model.setAuditEventId(auditEventId);
	}

	/**
	 * Sets the class name of this audit event.
	 *
	 * @param className the class name of this audit event
	 */
	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class pk of this audit event.
	 *
	 * @param classPK the class pk of this audit event
	 */
	@Override
	public void setClassPK(String classPK) {
		model.setClassPK(classPK);
	}

	/**
	 * Sets the client host of this audit event.
	 *
	 * @param clientHost the client host of this audit event
	 */
	@Override
	public void setClientHost(String clientHost) {
		model.setClientHost(clientHost);
	}

	/**
	 * Sets the client ip of this audit event.
	 *
	 * @param clientIP the client ip of this audit event
	 */
	@Override
	public void setClientIP(String clientIP) {
		model.setClientIP(clientIP);
	}

	/**
	 * Sets the company ID of this audit event.
	 *
	 * @param companyId the company ID of this audit event
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the context name of this audit event.
	 *
	 * @param contextName the context name of this audit event
	 */
	@Override
	public void setContextName(String contextName) {
		model.setContextName(contextName);
	}

	/**
	 * Sets the correlation ID of this audit event.
	 *
	 * @param correlationId the correlation ID of this audit event
	 */
	@Override
	public void setCorrelationId(String correlationId) {
		model.setCorrelationId(correlationId);
	}

	/**
	 * Sets the create date of this audit event.
	 *
	 * @param createDate the create date of this audit event
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the event type of this audit event.
	 *
	 * @param eventType the event type of this audit event
	 */
	@Override
	public void setEventType(String eventType) {
		model.setEventType(eventType);
	}

	/**
	 * Sets the group ID of this audit event.
	 *
	 * @param groupId the group ID of this audit event
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the http method of this audit event.
	 *
	 * @param httpMethod the http method of this audit event
	 */
	@Override
	public void setHttpMethod(String httpMethod) {
		model.setHttpMethod(httpMethod);
	}

	/**
	 * Sets whether this audit event is impersonated.
	 *
	 * @param impersonated the impersonated of this audit event
	 */
	@Override
	public void setImpersonated(boolean impersonated) {
		model.setImpersonated(impersonated);
	}

	/**
	 * Sets the impersonated user email address of this audit event.
	 *
	 * @param impersonatedUserEmailAddress the impersonated user email address of this audit event
	 */
	@Override
	public void setImpersonatedUserEmailAddress(
		String impersonatedUserEmailAddress) {

		model.setImpersonatedUserEmailAddress(impersonatedUserEmailAddress);
	}

	/**
	 * Sets the impersonated user ID of this audit event.
	 *
	 * @param impersonatedUserId the impersonated user ID of this audit event
	 */
	@Override
	public void setImpersonatedUserId(long impersonatedUserId) {
		model.setImpersonatedUserId(impersonatedUserId);
	}

	/**
	 * Sets the impersonated user name of this audit event.
	 *
	 * @param impersonatedUserName the impersonated user name of this audit event
	 */
	@Override
	public void setImpersonatedUserName(String impersonatedUserName) {
		model.setImpersonatedUserName(impersonatedUserName);
	}

	/**
	 * Sets the impersonated user uuid of this audit event.
	 *
	 * @param impersonatedUserUuid the impersonated user uuid of this audit event
	 */
	@Override
	public void setImpersonatedUserUuid(String impersonatedUserUuid) {
		model.setImpersonatedUserUuid(impersonatedUserUuid);
	}

	/**
	 * Sets the message of this audit event.
	 *
	 * @param message the message of this audit event
	 */
	@Override
	public void setMessage(String message) {
		model.setMessage(message);
	}

	/**
	 * Sets the object name of this audit event.
	 *
	 * @param objectName the object name of this audit event
	 */
	@Override
	public void setObjectName(String objectName) {
		model.setObjectName(objectName);
	}

	/**
	 * Sets the primary key of this audit event.
	 *
	 * @param primaryKey the primary key of this audit event
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the request ID of this audit event.
	 *
	 * @param requestId the request ID of this audit event
	 */
	@Override
	public void setRequestId(String requestId) {
		model.setRequestId(requestId);
	}

	/**
	 * Sets whether this audit event is request ID generated.
	 *
	 * @param requestIdGenerated the request ID generated of this audit event
	 */
	@Override
	public void setRequestIdGenerated(boolean requestIdGenerated) {
		model.setRequestIdGenerated(requestIdGenerated);
	}

	/**
	 * Sets the resource action of this audit event.
	 *
	 * @param resourceAction the resource action of this audit event
	 */
	@Override
	public void setResourceAction(String resourceAction) {
		model.setResourceAction(resourceAction);
	}

	/**
	 * Sets the resource type of this audit event.
	 *
	 * @param resourceType the resource type of this audit event
	 */
	@Override
	public void setResourceType(String resourceType) {
		model.setResourceType(resourceType);
	}

	/**
	 * Sets the roles of this audit event.
	 *
	 * @param roles the roles of this audit event
	 */
	@Override
	public void setRoles(String roles) {
		model.setRoles(roles);
	}

	/**
	 * Sets the server name of this audit event.
	 *
	 * @param serverName the server name of this audit event
	 */
	@Override
	public void setServerName(String serverName) {
		model.setServerName(serverName);
	}

	/**
	 * Sets the server port of this audit event.
	 *
	 * @param serverPort the server port of this audit event
	 */
	@Override
	public void setServerPort(int serverPort) {
		model.setServerPort(serverPort);
	}

	/**
	 * Sets the session ID of this audit event.
	 *
	 * @param sessionID the session ID of this audit event
	 */
	@Override
	public void setSessionID(String sessionID) {
		model.setSessionID(sessionID);
	}

	/**
	 * Sets the user agent of this audit event.
	 *
	 * @param userAgent the user agent of this audit event
	 */
	@Override
	public void setUserAgent(String userAgent) {
		model.setUserAgent(userAgent);
	}

	/**
	 * Sets the user email address of this audit event.
	 *
	 * @param userEmailAddress the user email address of this audit event
	 */
	@Override
	public void setUserEmailAddress(String userEmailAddress) {
		model.setUserEmailAddress(userEmailAddress);
	}

	/**
	 * Sets the user ID of this audit event.
	 *
	 * @param userId the user ID of this audit event
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this audit event.
	 *
	 * @param userName the user name of this audit event
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this audit event.
	 *
	 * @param userUuid the user uuid of this audit event
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected AuditEventWrapper wrap(AuditEvent auditEvent) {
		return new AuditEventWrapper(auditEvent);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-304411144