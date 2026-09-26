/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.audit;

/**
 * @author Rafael Praxedes
 */
public class AuditRequestContext implements Cloneable {

	@Override
	public AuditRequestContext clone() {
		try {
			return (AuditRequestContext)super.clone();
		}
		catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new RuntimeException(cloneNotSupportedException);
		}
	}

	public String getClientHost() {
		return _clientHost;
	}

	public String getClientIP() {
		return _clientIP;
	}

	public String getCorrelationId() {
		return _correlationId;
	}

	public String getQueryString() {
		return _queryString;
	}

	public String getRealUserEmailAddress() {
		return _realUserEmailAddress;
	}

	public long getRealUserId() {
		return _realUserId;
	}

	public String getRealUserLogin() {
		return _realUserLogin;
	}

	public String getRequestId() {
		return _requestId;
	}

	public String getRequestURL() {
		return _requestURL;
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

	public boolean isRequestIdGenerated() {
		return _requestIdGenerated;
	}

	public void setClientHost(String clientHost) {
		_clientHost = clientHost;
	}

	public void setClientIP(String clientIP) {
		_clientIP = clientIP;
	}

	public void setCorrelationId(String correlationId) {
		_correlationId = correlationId;
	}

	public void setQueryString(String queryString) {
		_queryString = queryString;
	}

	public void setRealUserEmailAddress(String realUserEmailAddress) {
		_realUserEmailAddress = realUserEmailAddress;
	}

	public void setRealUserId(long realUserId) {
		_realUserId = realUserId;
	}

	public void setRealUserLogin(String realUserLogin) {
		_realUserLogin = realUserLogin;
	}

	public void setRequestId(String requestId) {
		_requestId = requestId;
	}

	public void setRequestIdGenerated(boolean requestIdGenerated) {
		_requestIdGenerated = requestIdGenerated;
	}

	public void setRequestURL(String requestURL) {
		_requestURL = requestURL;
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

	private String _clientHost;
	private String _clientIP;
	private String _correlationId;
	private String _queryString;
	private String _realUserEmailAddress;
	private long _realUserId;
	private String _realUserLogin;
	private String _requestId;
	private boolean _requestIdGenerated;
	private String _requestURL;
	private String _serverName;
	private int _serverPort;
	private String _sessionID;

}