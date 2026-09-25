/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.listener;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Víctor Galán
 */
public interface AudiencesEntryGroupRelListener {

	public void onUpdateAudiencesEntryGroupRels(
			long companyId, String audienceEntryERC, String[] groupERCs)
		throws PortalException;

}