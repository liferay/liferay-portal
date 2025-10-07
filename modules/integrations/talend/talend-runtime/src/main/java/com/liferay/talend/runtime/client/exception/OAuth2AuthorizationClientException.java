/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.client.exception;

/**
 * @author Igor Beslic
 */
public class OAuth2AuthorizationClientException extends ClientException {

	public OAuth2AuthorizationClientException(String message) {
		super(message);
	}

	public OAuth2AuthorizationClientException(String message, int httpStatus) {
		super(message, httpStatus);
	}

}