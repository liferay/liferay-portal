/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.model;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author José Abelenda
 */
public class DSRecipientViewDefinition {

	public Map<String, Object> getAdditionalProps() {
		return additionalProps;
	}

	public String getAuthenticationMethod() {
		return authenticationMethod;
	}

	public String getDSClientUserId() {
		return dsClientUserId;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setAdditionalProps(Map<String, Object> additionalProps) {
		this.additionalProps = additionalProps;
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

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = JSONUtil.put(
			"authenticationMethod", getAuthenticationMethod()
		).put(
			"clientUserId", getDSClientUserId()
		).put(
			"email", getEmailAddress()
		).put(
			"returnUrl", getReturnURL()
		).put(
			"userName", getUserName()
		);

		for (Map.Entry<String, Object> entry : additionalProps.entrySet()) {
			Object value = entry.getValue();

			if (value instanceof Object[]) {
				jsonObject.put(
					entry.getKey(), JSONUtil.putAll((Object[])value));
			}
			else {
				jsonObject.put(entry.getKey(), value);
			}
		}

		return jsonObject;
	}

	@Override
	public String toString() {
		return toJSONObject().toString();
	}

	protected Map<String, Object> additionalProps = new LinkedHashMap<>();
	protected String authenticationMethod;
	protected String dsClientUserId;
	protected String emailAddress;
	protected String returnURL;
	protected String userName;

}