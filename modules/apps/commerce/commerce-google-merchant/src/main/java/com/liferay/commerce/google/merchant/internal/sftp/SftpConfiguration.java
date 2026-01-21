/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.google.merchant.internal.sftp;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.commerce.google.merchant.internal.constants.CommerceGoogleMerchantConstants;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Thomas Stewart
 */
@ExtendedObjectClassDefinition(category = "google-merchant")
@Meta.OCD(
	id = "com.liferay.commerce.google.merchant.internal.sftp.SftpConfiguration",
	name = "sftp-upload-configuration-name"
)
public interface SftpConfiguration {

	@Meta.AD(name = "fingerprint", required = false)
	public String fingerprint();

	@Meta.AD(
		deflt = CommerceGoogleMerchantConstants.COMMERCE_GOOGLE_PARTNER_UPLOAD_URL,
		name = "host", required = false
	)
	public String host();

	@Meta.AD(name = "feed-password", required = false)
	public String password();

	@Meta.AD(deflt = "19321", name = "port", required = false)
	public int port();

	@Meta.AD(name = "feed-username", required = false)
	public String username();

}