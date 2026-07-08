/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.quota;

import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.ai.hub.quota.Usage;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.PortalRunMode;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "service.ranking:Integer=-1", service = QuotaManager.class
)
public class DummyQuotaManager implements QuotaManager {

	@Override
	public String acquireAgentInstancePermit(long userId)
		throws PortalException {

		return null;
	}

	@Override
	public void addQuotas(long accountEntryId, long companyId, long userId) {
	}

	@Override
	public void checkTokensUsage(long companyId, long userId) {
		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-97531") ||
			PortalRunMode.isTestMode()) {

			return;
		}

		throw new UnsupportedOperationException(
			"You have exceeded your token quota");
	}

	@Override
	public void releaseAgentInstancePermit(String permit) {
	}

	@Override
	public void updateUsage(long companyId, Usage usage, long userId) {
	}

}