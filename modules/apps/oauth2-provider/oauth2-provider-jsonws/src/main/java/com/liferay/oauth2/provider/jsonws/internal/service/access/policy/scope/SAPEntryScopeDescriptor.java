/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.jsonws.internal.service.access.policy.scope;

import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Locale;

/**
 * @author Shuyang Zhou
 */
public class SAPEntryScopeDescriptor implements ScopeDescriptor {

	public SAPEntryScopeDescriptor(
		ScopeDescriptor defaultScopeDescriptor,
		SAPEntryScopeDescriptorFinder sapEntryScopeDescriptorFinder) {

		_defaultScopeDescriptor = defaultScopeDescriptor;
		_sapEntryScopeDescriptorFinder = sapEntryScopeDescriptorFinder;
	}

	@Override
	public String describeScope(String scope, Locale locale) {
		SAPEntryScope sapEntryScope =
			_sapEntryScopeDescriptorFinder.getSAPEntryScope(scope);

		if (sapEntryScope == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get SAP entry scope " + scope);
			}

			return _defaultScopeDescriptor.describeScope(scope, locale);
		}

		return sapEntryScope.getTitle(locale);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SAPEntryScopeDescriptor.class);

	private final ScopeDescriptor _defaultScopeDescriptor;
	private final SAPEntryScopeDescriptorFinder _sapEntryScopeDescriptorFinder;

}