/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.manager;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface DSDocumentManager {

	public byte[] getDSDocumentAsBytes(
		long companyId, long groupId, String dsDocumentId, String dsEnvelopeId);

	public byte[] getDSDocumentsAsBytes(
		long companyId, long groupId, String dsEnvelopeId);

}