/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.manager;

import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface DSEnvelopeManager {

	public DSEnvelope addDSEnvelope(
		long companyId, long groupId, DSEnvelope dsEnvelope);

	public void deleteDSEnvelopes(
			long companyId, long groupId, String... dsEnvelopeIds)
		throws Exception;

	public DSEnvelope getDSEnvelope(
		long companyId, long groupId, String dsEnvelopeId);

	public DSEnvelope getDSEnvelope(
		long companyId, long groupId, String dsEnvelopeId, String include);

	public Page<DSEnvelope> getDSEnvelopesPage(
		long companyId, long groupId, String fromDateString, String keywords,
		String order, Pagination pagination, String status);

	public Page<DSEnvelope> getDSEnvelopesPage(
		long companyId, long groupId, String fromDateString, String keywords,
		String order, Pagination pagination, String status,
		boolean includeDocuments);

}