/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.shortcut.internal.spi.scope.finder;

import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Shinn Lok
 */
public class OAuth2ProviderShortcutScopeFinder implements ScopeFinder {

	public OAuth2ProviderShortcutScopeFinder(
		SAPEntryLocalService sapEntryLocalService) {

		_sapEntryLocalService = sapEntryLocalService;
	}

	@Override
	public Collection<String> findScopes() {
		Long companyId = CompanyThreadLocal.getCompanyId();

		if (companyId == null) {
			return _scopeAliasesList;
		}

		return TransformUtil.transform(
			_scopeAliasesList,
			scopeAlias -> {
				SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
					companyId, "OAUTH2_" + scopeAlias);

				if ((sapEntry != null) && sapEntry.isEnabled()) {
					return scopeAlias;
				}

				return null;
			});
	}

	private static final List<String> _scopeAliasesList = Arrays.asList(
		"analytics.read", "analytics.write");

	private final SAPEntryLocalService _sapEntryLocalService;

}