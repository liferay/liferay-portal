/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.util;

import com.liferay.change.tracking.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Jaime Leon
 */
public class SiteTemplateSyncUtil {

	public static boolean isPublicationsEnabled(long companyId) {
		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-104837")) {
			return false;
		}

		CTSettingsConfigurationHelper ctSettingsConfigurationHelper =
			_ctSettingsConfigurationHelperSnapshot.get();

		if (ctSettingsConfigurationHelper == null) {
			return false;
		}

		return ctSettingsConfigurationHelper.isEnabled(companyId);
	}

	private static final Snapshot<CTSettingsConfigurationHelper>
		_ctSettingsConfigurationHelperSnapshot = new Snapshot<>(
			SiteTemplateSyncUtil.class, CTSettingsConfigurationHelper.class);

}