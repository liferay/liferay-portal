/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.model;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;

/**
 * @author José Abelenda
 */
public class DSRecipientViewDefinition {

	public String getAuthenticationMethod() {
		return authenticationMethod;
	}

	public String getDSClientUserId() {
		return dsClientUserId;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String[] getFrameAncestors() {
		return frameAncestors;
	}

	public String[] getMessageOrigins() {
		return messageOrigins;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setAuthenticationMethod(String authenticationMethod) {
		this.authenticationMethod = authenticationMethod;
	}

	public void setDSClientUserId(String dsClientUserId) {
		this.dsClientUserId = dsClientUserId;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setFrameAncestors(String[] frameAncestors) {
		this.frameAncestors = frameAncestors;
	}

	public void setMessageOrigins(String[] messageOrigins) {
		this.messageOrigins = messageOrigins;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public JSONObject toJSONObject() {
		return JSONUtil.put(
			"authenticationMethod", getAuthenticationMethod()
		).put(
			"clientUserId", getDSClientUserId()
		).put(
			"email", getEmailAddress()
		).put(
			"frameAncestors",
			() -> {
				if (ArrayUtil.isEmpty(frameAncestors)) {
					return null;
				}

				return JSONUtil.putAll(frameAncestors);
			}
		).put(
			"messageOrigins",
			() -> {
				if (ArrayUtil.isEmpty(messageOrigins)) {
					return null;
				}

				return JSONUtil.putAll(messageOrigins);
			}
		).put(
			"returnUrl", getReturnURL()
		).put(
			"userName", getUserName()
		);
	}

	@Override
	public String toString() {
		return toJSONObject().toString();
	}

	protected String authenticationMethod;
	protected String dsClientUserId;
	protected String emailAddress;
	protected String[] frameAncestors;
	protected String[] messageOrigins;
	protected String returnURL;
	protected String userName;

}