/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal;

import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.spi.ApplicationDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.spi.ScopeDescriptorLocator;
import com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.petra.string.StringUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * @author Stian Sigvartsen
 */
public class AssignableScopes {

	public AssignableScopes(
		ApplicationDescriptorLocator applicationDescriptorLocator,
		Locale locale, ScopeDescriptorLocator scopeDescriptorLocator) {

		this(
			applicationDescriptorLocator, new HashSet<>(), locale,
			scopeDescriptorLocator);
	}

	public AssignableScopes(
		ApplicationDescriptorLocator applicationDescriptorLocator,
		Set<LiferayOAuth2Scope> liferayOAuth2Scopes, Locale locale,
		ScopeDescriptorLocator scopeDescriptorLocator) {

		_applicationDescriptorLocator = applicationDescriptorLocator;
		_liferayOAuth2Scopes = liferayOAuth2Scopes;
		_locale = locale;
		_scopeDescriptorLocator = scopeDescriptorLocator;
	}

	public AssignableScopes add(AssignableScopes assignableScopes) {
		Set<LiferayOAuth2Scope> liferayOAuth2Scopes = new HashSet<>();

		liferayOAuth2Scopes.addAll(assignableScopes.getLiferayOAuth2Scopes());
		liferayOAuth2Scopes.addAll(_liferayOAuth2Scopes);

		return new AssignableScopes(
			_applicationDescriptorLocator, liferayOAuth2Scopes, _locale,
			_scopeDescriptorLocator);
	}

	public void addLiferayOAuth2Scope(LiferayOAuth2Scope liferayOAuth2Scope) {
		if (liferayOAuth2Scope != null) {
			_liferayOAuth2Scopes.add(liferayOAuth2Scope);
		}
	}

	public void addLiferayOAuth2Scopes(
		Collection<LiferayOAuth2Scope> liferayOAuth2Scopes) {

		if (liferayOAuth2Scopes == null) {
			return;
		}

		liferayOAuth2Scopes.forEach(this::addLiferayOAuth2Scope);
	}

	public boolean contains(AssignableScopes assignableScopes) {
		return _liferayOAuth2Scopes.containsAll(
			assignableScopes.getLiferayOAuth2Scopes());
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssignableScopes)) {
			return false;
		}

		AssignableScopes assignableScopes = (AssignableScopes)object;

		return Objects.equals(
			_liferayOAuth2Scopes, assignableScopes.getLiferayOAuth2Scopes());
	}

	public AssignableScopes getApplicationAssignableScopes(
		String applicationName) {

		Set<LiferayOAuth2Scope> liferayOAuth2Scopes = new HashSet<>();

		for (LiferayOAuth2Scope liferayOAuth2Scope : _liferayOAuth2Scopes) {
			if (!Objects.equals(
					applicationName, liferayOAuth2Scope.getApplicationName())) {

				continue;
			}

			liferayOAuth2Scopes.add(liferayOAuth2Scope);
		}

		return new AssignableScopes(
			_applicationDescriptorLocator, liferayOAuth2Scopes, _locale,
			_scopeDescriptorLocator);
	}

	public String getApplicationDescription(String applicationName) {
		ApplicationDescriptor applicationDescriptor =
			_applicationDescriptorLocator.getApplicationDescriptor(
				applicationName);

		if (applicationDescriptor == null) {
			return applicationName;
		}

		return applicationDescriptor.describeApplication(_locale);
	}

	public Set<String> getApplicationNames() {
		Set<String> applicationNames = new HashSet<>();

		for (LiferayOAuth2Scope liferayOAuth2Scope : _liferayOAuth2Scopes) {
			applicationNames.add(liferayOAuth2Scope.getApplicationName());
		}

		return applicationNames;
	}

	public Set<String> getApplicationScopeDescription(
		long companyId, String applicationName) {

		Set<String> scopeDescriptions = new HashSet<>();

		for (LiferayOAuth2Scope liferayOAuth2Scope : _liferayOAuth2Scopes) {
			if (!Objects.equals(
					applicationName, liferayOAuth2Scope.getApplicationName())) {

				continue;
			}

			scopeDescriptions.add(
				getScopeDescription(companyId, liferayOAuth2Scope));
		}

		return scopeDescriptions;
	}

	public Set<LiferayOAuth2Scope> getLiferayOAuth2Scopes() {
		return _liferayOAuth2Scopes;
	}

	public String getScopeDescription(
		long companyId, LiferayOAuth2Scope liferayOAuth2Scope) {

		ScopeDescriptor scopeDescriptor =
			_scopeDescriptorLocator.getScopeDescriptor(
				companyId, liferayOAuth2Scope.getApplicationName());

		if (scopeDescriptor == null) {
			return liferayOAuth2Scope.getScope();
		}

		return scopeDescriptor.describeScope(
			liferayOAuth2Scope.getScope(), _locale);
	}

	@Override
	public int hashCode() {
		return _liferayOAuth2Scopes.hashCode();
	}

	public Set<AssignableScopes> splitByApplicationScopes() {
		Set<AssignableScopes> assignableScopess = new HashSet<>();

		for (LiferayOAuth2Scope liferayOAuth2Scope : _liferayOAuth2Scopes) {
			assignableScopess.add(
				new AssignableScopes(
					_applicationDescriptorLocator,
					Collections.singleton(liferayOAuth2Scope), _locale,
					_scopeDescriptorLocator));
		}

		return assignableScopess;
	}

	public AssignableScopes subtract(AssignableScopes assignableScopes) {
		Set<LiferayOAuth2Scope> liferayOAuth2Scopes = new HashSet<>(
			_liferayOAuth2Scopes);

		liferayOAuth2Scopes.removeAll(
			assignableScopes.getLiferayOAuth2Scopes());

		return new AssignableScopes(
			_applicationDescriptorLocator, liferayOAuth2Scopes, _locale,
			_scopeDescriptorLocator);
	}

	@Override
	public String toString() {
		return StringUtil.merge(_liferayOAuth2Scopes, " + ");
	}

	private final ApplicationDescriptorLocator _applicationDescriptorLocator;
	private final Set<LiferayOAuth2Scope> _liferayOAuth2Scopes;
	private final Locale _locale;
	private final ScopeDescriptorLocator _scopeDescriptorLocator;

}