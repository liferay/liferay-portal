/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
package com.liferay.oauth.client.persistence.exception;

import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;

/**
 * @author Brian Wing Shun Chan
 */
public class DuplicateOAuthClientPRLocalMetadataExternalReferenceCodeException extends DuplicateExternalReferenceCodeException {

	public DuplicateOAuthClientPRLocalMetadataExternalReferenceCodeException() {
	}

	public DuplicateOAuthClientPRLocalMetadataExternalReferenceCodeException(String msg) {
		super(msg);
	}

	public DuplicateOAuthClientPRLocalMetadataExternalReferenceCodeException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public DuplicateOAuthClientPRLocalMetadataExternalReferenceCodeException(Throwable throwable) {
		super(throwable);
	}

}