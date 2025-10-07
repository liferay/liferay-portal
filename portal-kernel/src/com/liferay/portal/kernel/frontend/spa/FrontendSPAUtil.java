/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.frontend.spa;

import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Bryce Osterhaus
 */
public class FrontendSPAUtil {

	public static boolean isEnabled(long companyId) {
		FrontendSPA frontendSPA = _frontendSPASnapshot.get();

		return frontendSPA.isEnabled(companyId);
	}

	private static final Snapshot<FrontendSPA> _frontendSPASnapshot =
		new Snapshot<>(FrontendSPAUtil.class, FrontendSPA.class);

}