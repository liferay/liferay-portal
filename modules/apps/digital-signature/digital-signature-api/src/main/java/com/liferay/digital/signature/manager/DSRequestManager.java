/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.manager;

import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian I. Kim
 */
@ProviderType
public interface DSRequestManager {

	public void addDSRequest(
			long companyId, long groupId, long userId, DSEnvelope dsEnvelope,
			long[] fileEntryIds)
		throws PortalException;

}