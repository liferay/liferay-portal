/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.audit;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.Serializable;

import java.text.DateFormat;

import java.util.Date;

/**
 * @author Michael C. Han
 * @author Mika Koivisto
 * @author Bruno Farache
 * @author Stian Sigvartsen
 */
public class AuditMessage implements Serializable {

	public AuditMessage(
		long groupId, long companyId, long userId, String userName,
		Date timestampDate, JSONObject additionalInfoJSONObject,
		String className, String classPK, String eventType, String message) {

		this(
			groupId, companyId, userId, userName, timestampDate, 0,
			additionalInfoJSONObject, className, classPK, null, eventType,
			message);
	}

	public AuditMessage(
		long groupId, long companyId, long userId, String userName,
		Date timestampDate, long accountEntryId,
		JSONObject additionalInfoJSONObject, String className, String classPK,
		String contextName, String eventType, String message) {

		_groupId = groupId;
		_companyId = companyId;
		_userId = userId;
		_userName = userName;
		_timestampDate = (timestampDate != null) ? timestampDate : new Date();
		_accountEntryId = accountEntryId;
		_additionalInfoJSONObject =
			(additionalInfoJSONObject != null) ? additionalInfoJSONObject :
				JSONFactoryUtil.createJSONObject();
		_className = className;
		_classPK = classPK;
		_contextName = contextName;
		_eventType = eventType;
		_message = message;

		AuditRequestThreadLocal auditRequestThreadLocal =
			AuditRequestThreadLocal.getAuditThreadLocal();

		_clientHost = auditRequestThreadLocal.getClientHost();
		_clientIP = auditRequestThreadLocal.getClientIP();
		_serverName = auditRequestThreadLocal.getServerName();
		_serverPort = auditRequestThreadLocal.getServerPort();
		_sessionID = auditRequestThreadLocal.getSessionID();
		_userEmailAddress = auditRequestThreadLocal.getRealUserEmailAddress();

		long realUserId = auditRequestThreadLocal.getRealUserId();

		long doAsUserId = 0;

		if (PrincipalThreadLocal.getName() != null) {
			doAsUserId = GetterUtil.getLong(PrincipalThreadLocal.getName());
		}

		if ((realUserId > 0) && (doAsUserId != realUserId) &&
			!_additionalInfoJSONObject.has("doAsUserId")) {

			_additionalInfoJSONObject.put(
				"doAsUserEmailAddress",
				PortalUtil.getUserEmailAddress(doAsUserId)
			).put(
				"doAsUserId", String.valueOf(doAsUserId)
			).put(
				"doAsUserName",
				PortalUtil.getUserName(doAsUserId, StringPool.BLANK)
			);
		}

		if (userId == realUserId) {
			_userLogin = auditRequestThreadLocal.getRealUserLogin();
		}

		// LPS-172507

		else if ((realUserId > 0) && !PortalRunMode.isTestMode()) {
			_log.error(
				"Impersonated actions must be audited on the real user's ID");
		}
	}

	public AuditMessage(
		long companyId, long userId, String userName, Date timestampDate,
		JSONObject additionalInfoJSONObject, String className, String classPK,
		String eventType, String message) {

		this(
			0, companyId, userId, userName, timestampDate, 0,
			additionalInfoJSONObject, className, classPK, null, eventType,
			message);
	}

	public AuditMessage(
		long companyId, long userId, String userName,
		JSONObject additionalInfoJSONObject, String className, String classPK,
		String eventType, String message) {

		this(
			0, companyId, userId, userName, null, 0, additionalInfoJSONObject,
			className, classPK, null, eventType, message);
	}

	public AuditMessage(
		long companyId, long userId, String userName, String eventType) {

		this(
			0, companyId, userId, userName, null, 0, null, null, null, null,
			eventType, null);
	}

	public AuditMessage(
		long companyId, long userId, String userName, String className,
		String classPK, String eventType) {

		this(
			0, companyId, userId, userName, null, 0, null, className, classPK,
			null, eventType, null);
	}

	public AuditMessage(
		long companyId, long userId, String userName, String className,
		String classPK, String eventType, String message) {

		this(
			0, companyId, userId, userName, null, 0, null, className, classPK,
			null, eventType, message);
	}

	public AuditMessage(String message) throws JSONException {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(message);

		_accountEntryId = jsonObject.getLong(_ACCOUNT_ENTRY_ID);
		_additionalInfoJSONObject = jsonObject.getJSONObject(_ADDITIONAL_INFO);
		_className = jsonObject.getString(_CLASS_NAME);
		_classPK = jsonObject.getString(_CLASS_PK);

		if (jsonObject.has(_CLIENT_HOST)) {
			_clientHost = jsonObject.getString(_CLIENT_HOST);
		}

		if (jsonObject.has(_CLIENT_IP)) {
			_clientIP = jsonObject.getString(_CLIENT_IP);
		}

		_companyId = jsonObject.getLong(_COMPANY_ID);

		if (jsonObject.has(_CONTEXT_NAME)) {
			_contextName = jsonObject.getString(_CONTEXT_NAME);
		}

		if (jsonObject.has(_CORRELATION_ID)) {
			_correlationId = jsonObject.getString(_CORRELATION_ID);
		}

		_eventType = jsonObject.getString(_EVENT_TYPE);
		_groupId = jsonObject.getLong(_GROUP_ID);

		if (jsonObject.has(_HTTP_METHOD)) {
			_httpMethod = jsonObject.getString(_HTTP_METHOD);
		}

		if (jsonObject.has(_IMPERSONATED)) {
			_impersonated = jsonObject.getBoolean(_IMPERSONATED);
		}

		if (jsonObject.has(_IMPERSONATED_USER_EMAIL_ADDRESS)) {
			_impersonatedUserEmailAddress = jsonObject.getString(
				_IMPERSONATED_USER_EMAIL_ADDRESS);
		}

		if (jsonObject.has(_IMPERSONATED_USER_ID)) {
			_impersonatedUserId = jsonObject.getLong(_IMPERSONATED_USER_ID);
		}

		if (jsonObject.has(_IMPERSONATED_USER_NAME)) {
			_impersonatedUserName = jsonObject.getString(
				_IMPERSONATED_USER_NAME);
		}

		_message = jsonObject.getString(_MESSAGE);

		if (jsonObject.has(_OBJECT_NAME)) {
			_objectName = jsonObject.getString(_OBJECT_NAME);
		}

		if (jsonObject.has(_REQUEST_ID)) {
			_requestId = jsonObject.getString(_REQUEST_ID);
		}

		if (jsonObject.has(_REQUEST_ID_GENERATED)) {
			_requestIdGenerated = jsonObject.getBoolean(_REQUEST_ID_GENERATED);
		}

		if (jsonObject.has(_RESOURCE_ACTION)) {
			_resourceAction = jsonObject.getString(_RESOURCE_ACTION);
		}

		if (jsonObject.has(_RESOURCE_TYPE)) {
			_resourceType = jsonObject.getString(_RESOURCE_TYPE);
		}

		if (jsonObject.has(_ROLES)) {
			_roles = jsonObject.getString(_ROLES);
		}

		if (jsonObject.has(_SERVER_NAME)) {
			_serverName = jsonObject.getString(_SERVER_NAME);
		}

		if (jsonObject.has(_SERVER_PORT)) {
			_serverPort = jsonObject.getInt(_SERVER_PORT);
		}

		if (jsonObject.has(_SESSION_ID)) {
			_sessionID = jsonObject.getString(_SESSION_ID);
		}

		_timestampDate = GetterUtil.getDate(
			jsonObject.getString(_TIMESTAMP), _getDateFormat());

		if (jsonObject.has(_USER_AGENT)) {
			_userAgent = jsonObject.getString(_USER_AGENT);
		}

		_userEmailAddress = jsonObject.getString(_USER_EMAIL_ADDRESS);
		_userId = jsonObject.getLong(_USER_ID);
		_userLogin = jsonObject.getString(_USER_LOGIN);
		_userName = jsonObject.getString(_USER_NAME);
	}

	public long getAccountEntryId() {
		return _accountEntryId;
	}

	public JSONObject getAdditionalInfo() {
		return _additionalInfoJSONObject;
	}

	public String getClassName() {
		return _className;
	}

	public String getClassPK() {
		return _classPK;
	}

	public String getClientHost() {
		return _clientHost;
	}

	public String getClientIP() {
		return _clientIP;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public String getContextName() {
		return _contextName;
	}

	public String getCorrelationId() {
		return _correlationId;
	}

	public String getEventType() {
		return _eventType;
	}

	public long getGroupId() {
		return _groupId;
	}

	public String getHttpMethod() {
		return _httpMethod;
	}

	public String getImpersonatedUserEmailAddress() {
		return _impersonatedUserEmailAddress;
	}

	public long getImpersonatedUserId() {
		return _impersonatedUserId;
	}

	public String getImpersonatedUserName() {
		return _impersonatedUserName;
	}

	public String getMessage() {
		return _message;
	}

	public String getObjectName() {
		return _objectName;
	}

	public String getRequestId() {
		return _requestId;
	}

	public String getResourceAction() {
		return _resourceAction;
	}

	public String getResourceType() {
		return _resourceType;
	}

	public String getRoles() {
		return _roles;
	}

	public String getServerName() {
		return _serverName;
	}

	public int getServerPort() {
		return _serverPort;
	}

	public String getSessionID() {
		return _sessionID;
	}

	public Date getTimestampDate() {
		return _timestampDate;
	}

	public String getUserAgent() {
		return _userAgent;
	}

	public String getUserEmailAddress() {
		return _userEmailAddress;
	}

	public long getUserId() {
		return _userId;
	}

	public String getUserLogin() {
		return _userLogin;
	}

	public String getUserName() {
		return _userName;
	}

	public boolean isImpersonated() {
		return _impersonated;
	}

	public boolean isRequestIdGenerated() {
		return _requestIdGenerated;
	}

	public void setAccountEntryId(long accountEntryId) {
		_accountEntryId = accountEntryId;
	}

	public void setAdditionalInfo(JSONObject additionalInfoJSONObject) {
		_additionalInfoJSONObject = additionalInfoJSONObject;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = String.valueOf(classPK);
	}

	public void setClassPK(String classPK) {
		_classPK = classPK;
	}

	public void setClientHost(String clientHost) {
		_clientHost = clientHost;
	}

	public void setClientIP(String clientIP) {
		_clientIP = clientIP;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public void setContextName(String contextName) {
		_contextName = contextName;
	}

	public void setCorrelationId(String correlationId) {
		_correlationId = correlationId;
	}

	public void setEventType(String eventType) {
		_eventType = eventType;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setHttpMethod(String httpMethod) {
		_httpMethod = httpMethod;
	}

	public void setImpersonated(boolean impersonated) {
		_impersonated = impersonated;
	}

	public void setImpersonatedUserEmailAddress(
		String impersonatedUserEmailAddress) {

		_impersonatedUserEmailAddress = impersonatedUserEmailAddress;
	}

	public void setImpersonatedUserId(long impersonatedUserId) {
		_impersonatedUserId = impersonatedUserId;
	}

	public void setImpersonatedUserName(String impersonatedUserName) {
		_impersonatedUserName = impersonatedUserName;
	}

	public void setMessage(String message) {
		_message = message;
	}

	public void setObjectName(String objectName) {
		_objectName = objectName;
	}

	public void setRequestId(String requestId) {
		_requestId = requestId;
	}

	public void setRequestIdGenerated(boolean requestIdGenerated) {
		_requestIdGenerated = requestIdGenerated;
	}

	public void setResourceAction(String resourceAction) {
		_resourceAction = resourceAction;
	}

	public void setResourceType(String resourceType) {
		_resourceType = resourceType;
	}

	public void setRoles(String roles) {
		_roles = roles;
	}

	public void setServerName(String serverName) {
		_serverName = serverName;
	}

	public void setServerPort(int serverPort) {
		_serverPort = serverPort;
	}

	public void setSessionID(String sessionID) {
		_sessionID = sessionID;
	}

	public void setTimestampDate(Date timestampDate) {
		_timestampDate = timestampDate;
	}

	public void setUserAgent(String userAgent) {
		_userAgent = userAgent;
	}

	public void setUserEmailAddress(String userEmailAddress) {
		_userEmailAddress = userEmailAddress;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public void setUserLogin(String userLogin) {
		_userLogin = userLogin;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public JSONObject toJSONObject() {
		DateFormat dateFormat = _getDateFormat();

		return JSONUtil.put(
			_ACCOUNT_ENTRY_ID, _accountEntryId
		).put(
			_ADDITIONAL_INFO, _additionalInfoJSONObject
		).put(
			_CLASS_NAME, _className
		).put(
			_CLASS_PK, _classPK
		).put(
			_CLIENT_HOST, _clientHost
		).put(
			_CLIENT_IP, _clientIP
		).put(
			_COMPANY_ID, _companyId
		).put(
			_CONTEXT_NAME, _contextName
		).put(
			_CORRELATION_ID, _correlationId
		).put(
			_EVENT_TYPE, _eventType
		).put(
			_GROUP_ID, _groupId
		).put(
			_HTTP_METHOD, _httpMethod
		).put(
			_IMPERSONATED, _impersonated
		).put(
			_IMPERSONATED_USER_EMAIL_ADDRESS, _impersonatedUserEmailAddress
		).put(
			_IMPERSONATED_USER_ID, _impersonatedUserId
		).put(
			_IMPERSONATED_USER_NAME, _impersonatedUserName
		).put(
			_MESSAGE, _message
		).put(
			_OBJECT_NAME, _objectName
		).put(
			_REQUEST_ID, _requestId
		).put(
			_REQUEST_ID_GENERATED, _requestIdGenerated
		).put(
			_RESOURCE_ACTION, _resourceAction
		).put(
			_RESOURCE_TYPE, _resourceType
		).put(
			_ROLES, _roles
		).put(
			_SERVER_NAME, _serverName
		).put(
			_SERVER_PORT, _serverPort
		).put(
			_SESSION_ID, _sessionID
		).put(
			_TIMESTAMP,
			dateFormat.format(
				(_timestampDate != null) ? _timestampDate : new Date())
		).put(
			_USER_AGENT, _userAgent
		).put(
			_USER_EMAIL_ADDRESS, _userEmailAddress
		).put(
			_USER_ID, _userId
		).put(
			_USER_LOGIN, _userLogin
		).put(
			_USER_NAME, _userName
		);
	}

	private DateFormat _getDateFormat() {
		return DateFormatFactoryUtil.getSimpleDateFormat(_DATE_FORMAT);
	}

	private static final String _ACCOUNT_ENTRY_ID = "accountEntryId";

	private static final String _ADDITIONAL_INFO = "additionalInfo";

	private static final String _CLASS_NAME = "className";

	private static final String _CLASS_PK = "classPK";

	private static final String _CLIENT_HOST = "clientHost";

	private static final String _CLIENT_IP = "clientIP";

	private static final String _COMPANY_ID = "companyId";

	private static final String _CONTEXT_NAME = "contextName";

	private static final String _CORRELATION_ID = "correlationId";

	private static final String _DATE_FORMAT = "yyyyMMddkkmmssSSS";

	private static final String _EVENT_TYPE = "eventType";

	private static final String _GROUP_ID = "groupId";

	private static final String _HTTP_METHOD = "httpMethod";

	private static final String _IMPERSONATED = "impersonated";

	private static final String _IMPERSONATED_USER_EMAIL_ADDRESS =
		"impersonatedUserEmailAddress";

	private static final String _IMPERSONATED_USER_ID = "impersonatedUserId";

	private static final String _IMPERSONATED_USER_NAME =
		"impersonatedUserName";

	private static final String _MESSAGE = "message";

	private static final String _OBJECT_NAME = "objectName";

	private static final String _REQUEST_ID = "requestId";

	private static final String _REQUEST_ID_GENERATED = "requestIdGenerated";

	private static final String _RESOURCE_ACTION = "resourceAction";

	private static final String _RESOURCE_TYPE = "resourceType";

	private static final String _ROLES = "roles";

	private static final String _SERVER_NAME = "serverName";

	private static final String _SERVER_PORT = "serverPort";

	private static final String _SESSION_ID = "sessionID";

	private static final String _TIMESTAMP = "timestamp";

	private static final String _USER_AGENT = "userAgent";

	private static final String _USER_EMAIL_ADDRESS = "userEmailAddress";

	private static final String _USER_ID = "userId";

	private static final String _USER_LOGIN = "userLogin";

	private static final String _USER_NAME = "userName";

	private static final Log _log = LogFactoryUtil.getLog(AuditMessage.class);

	private long _accountEntryId;
	private JSONObject _additionalInfoJSONObject;
	private String _className;
	private String _classPK;
	private String _clientHost;
	private String _clientIP;
	private long _companyId = -1;
	private String _contextName;
	private String _correlationId;
	private String _eventType;
	private long _groupId = -1;
	private String _httpMethod;
	private boolean _impersonated;
	private String _impersonatedUserEmailAddress;
	private long _impersonatedUserId;
	private String _impersonatedUserName;
	private String _message;
	private String _objectName;
	private String _requestId;
	private boolean _requestIdGenerated;
	private String _resourceAction;
	private String _resourceType;
	private String _roles;
	private String _serverName;
	private int _serverPort;
	private String _sessionID;
	private Date _timestampDate;
	private String _userAgent;
	private String _userEmailAddress;
	private long _userId = -1;
	private String _userLogin;
	private String _userName;

}