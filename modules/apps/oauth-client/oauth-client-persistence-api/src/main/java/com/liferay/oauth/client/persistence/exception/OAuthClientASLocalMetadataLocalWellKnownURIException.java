/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class OAuthClientASLocalMetadataLocalWellKnownURIException
	extends PortalException {

	public static class MustBeValidHTTPSURL
		extends OAuthClientASLocalMetadataLocalWellKnownURIException {

		public MustBeValidHTTPSURL(String msg) {
			super(msg);
		}

		public MustBeValidHTTPSURL(String msg, Throwable throwable) {
			super(msg, throwable);
		}

	}

	public static class MustNotExceedMaximumLength
		extends OAuthClientASLocalMetadataLocalWellKnownURIException {

		public MustNotExceedMaximumLength(String msg) {
			super(msg);
		}

	}

	public static class MustProduceValidURI
		extends OAuthClientASLocalMetadataLocalWellKnownURIException {

		public MustProduceValidURI(Throwable throwable) {
			super(throwable);
		}

	}

	private OAuthClientASLocalMetadataLocalWellKnownURIException(String msg) {
		super(msg);
	}

	private OAuthClientASLocalMetadataLocalWellKnownURIException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	private OAuthClientASLocalMetadataLocalWellKnownURIException(
		Throwable throwable) {

		super(throwable);
	}

}