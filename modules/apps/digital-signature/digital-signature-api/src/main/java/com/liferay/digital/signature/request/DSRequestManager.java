/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.request;

import com.liferay.digital.signature.model.DSEnvelope;

import java.util.Collection;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Kim
 */
@ProviderType
public interface DSRequestManager {

	public void addDSRequest(
		long companyId, long groupId, long userId, DSEnvelope dsEnvelope,
		long[] fileEntryIds);

	public Map<Long, String> getRequestStatusesByFileEntryId(
		long companyId, Collection<Long> fileEntryIds);

	public void updateDSRequest(
		long companyId, long groupId, String providerRequestId);

}