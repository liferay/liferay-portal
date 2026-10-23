/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.jsonws.internal.service.access.policy.scope;

import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Tomas Polesovsky
 */
public class SAPEntryScopeDescriptorFinder
	implements ScopeDescriptor, ScopeFinder {

	public SAPEntryScopeDescriptorFinder(
		Supplier<List<SAPEntryScope>> sapEntryScopesSupplier,
		ScopeDescriptor defaultScopeDescriptor) {

		_sapEntryScopesSupplier = sapEntryScopesSupplier;
		_defaultScopeDescriptor = defaultScopeDescriptor;
	}

	@Override
	public String describeScope(String scope, Locale locale) {
		_initialize();

		SAPEntryScope sapEntryScope = _sapEntryScopes.get(scope);

		if (sapEntryScope == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get SAP entry scope " + scope);
			}

			return _defaultScopeDescriptor.describeScope(scope, locale);
		}

		return sapEntryScope.getTitle(locale);
	}

	@Override
	public Collection<String> findScopes() {
		_initialize();

		return new HashSet<>(_scopes);
	}

	public Collection<SAPEntryScope> getSAPEntryScopes() {
		_initialize();

		return _sapEntryScopes.values();
	}

	private void _initialize() {
		if (_initialized) {
			return;
		}

		synchronized (this) {
			if (_initialized) {
				return;
			}

			for (SAPEntryScope sapEntryScope : _sapEntryScopesSupplier.get()) {
				SAPEntry sapEntry = sapEntryScope.getSAPEntry();

				if (sapEntry.isEnabled()) {
					_scopes.add(sapEntryScope.getScope());
				}

				_sapEntryScopes.put(sapEntryScope.getScope(), sapEntryScope);
			}

			_initialized = true;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SAPEntryScopeDescriptorFinder.class);

	private final ScopeDescriptor _defaultScopeDescriptor;
	private volatile boolean _initialized;
	private final Map<String, SAPEntryScope> _sapEntryScopes = new HashMap<>();
	private final Supplier<List<SAPEntryScope>> _sapEntryScopesSupplier;
	private final Set<String> _scopes = new HashSet<>();

}