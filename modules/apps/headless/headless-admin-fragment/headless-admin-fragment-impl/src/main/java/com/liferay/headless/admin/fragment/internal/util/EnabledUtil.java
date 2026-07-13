/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.util;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Company;

/**
 * @author Rubén Pulido
 */
public class EnabledUtil {

	public static void checkDesignLibrariesEnabled(Company company) {
		FeatureFlagManagerUtil.checkEnabled(
			company.getCompanyId(), "LPD-57283");
	}

	public static void checkEnabled(Company company) {
		FeatureFlagManagerUtil.checkEnabled(
			company.getCompanyId(), "LPD-39244");
	}

}