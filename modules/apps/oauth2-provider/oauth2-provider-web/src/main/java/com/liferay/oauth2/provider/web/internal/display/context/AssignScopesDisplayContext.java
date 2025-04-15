/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.display.context;

import com.liferay.document.library.util.DLURLHelper;
import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.scope.liferay.spi.ApplicationDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.spi.ScopeDescriptorLocator;
import com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.oauth2.provider.web.internal.AssignableScopes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Stian Sigvartsen
 */
public class AssignScopesDisplayContext
	extends OAuth2AdminPortletDisplayContext {

	public AssignScopesDisplayContext(
			ApplicationDescriptorLocator applicationDescriptorLocator,
			DLURLHelper dlURLHelper,
			OAuth2ApplicationScopeAliasesLocalService
				oAuth2ApplicationScopeAliasesLocalService,
			OAuth2ApplicationService oAuth2ApplicationService,
			OAuth2ProviderConfiguration oAuth2ProviderConfiguration,
			OAuth2ScopeGrantLocalService oAuth2ScopeGrantLocalService,
			PortletRequest portletRequest,
			ScopeDescriptorLocator scopeDescriptorLocator,
			ScopeLocator scopeLocator, ThemeDisplay themeDisplay)
		throws PortalException {

		super(
			dlURLHelper, oAuth2ApplicationScopeAliasesLocalService,
			oAuth2ApplicationService, oAuth2ProviderConfiguration,
			portletRequest, themeDisplay);

		_applicationDescriptorLocator = applicationDescriptorLocator;

		_companyId = themeDisplay.getCompanyId();
		_locale = themeDisplay.getLocale();

		OAuth2Application oAuth2Application = getOAuth2Application();

		Map<String, AssignableScopes> assignedScopeAliasesAssignableScopes =
			_getAssignableScopesByScopeAlias(
				oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
				applicationDescriptorLocator, oAuth2ScopeGrantLocalService,
				scopeDescriptorLocator, scopeLocator, themeDisplay);

		Set<String> scopeAliases = new HashSet<>(
			scopeLocator.getScopeAliases(themeDisplay.getCompanyId()));

		scopeAliases.addAll(assignedScopeAliasesAssignableScopes.keySet());

		for (String scopeAlias : scopeAliases) {
			AssignableScopes assignableScopes = new AssignableScopes(
				applicationDescriptorLocator, _locale, scopeDescriptorLocator);

			Collection<LiferayOAuth2Scope> liferayOAuth2Scopes =
				scopeLocator.getLiferayOAuth2Scopes(
					themeDisplay.getCompanyId(), scopeAlias);

			AssignableScopes assignedAssignableScopes = null;
			Relations relations = null;
			Set<String> applicationNames = null;

			if (liferayOAuth2Scopes.isEmpty()) {
				assignedAssignableScopes =
					assignedScopeAliasesAssignableScopes.remove(scopeAlias);

				relations = _getRelations(null, assignedAssignableScopes);

				assignableScopes = assignedAssignableScopes;
				applicationNames =
					assignedAssignableScopes.getApplicationNames();
			}
			else {
				assignableScopes.addLiferayOAuth2Scopes(liferayOAuth2Scopes);

				relations = _getRelations(scopeAlias, assignableScopes);

				assignedAssignableScopes =
					assignedScopeAliasesAssignableScopes.remove(scopeAlias);

				applicationNames = assignableScopes.getApplicationNames();
			}

			boolean indexAsGlobalAssignableScopes = false;

			if (applicationNames.size() > 1) {
				indexAsGlobalAssignableScopes = true;
			}

			if (assignedAssignableScopes != null) {
				if (relations._assignedAssignableScopes != null) {
					relations._assignedAssignableScopes.add(
						assignedAssignableScopes);
				}
				else {
					relations._assignedAssignableScopes =
						assignedAssignableScopes;
				}

				if (relations._assignedScopeAliases == null) {
					relations._assignedScopeAliases = new HashSet<>();
				}

				relations._assignedScopeAliases.add(scopeAlias);

				Set<String> assignedApplicationNames =
					assignedAssignableScopes.getApplicationNames();

				indexAsGlobalAssignableScopes =
					indexAsGlobalAssignableScopes ||
					(assignedApplicationNames.size() > 1);
			}

			_indexAssignableScopes(
				assignableScopes, applicationNames,
				indexAsGlobalAssignableScopes);
		}
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

		applicationNames.addAll(
			_globalAssignableScopesByApplicationName.keySet());
		applicationNames.addAll(
			_localAssignableScopesByApplicationName.keySet());

		return applicationNames;
	}

	public Map<String, String> getApplicationNamesDescriptions() {
		Map<String, String> applicationNamesDescriptions = new HashMap<>();

		for (String applicationName : getApplicationNames()) {
			applicationNamesDescriptions.put(
				applicationName, getApplicationDescription(applicationName));
		}

		return applicationNamesDescriptions;
	}

	public String getApplicationScopeDescription(
		String applicationName, AssignableScopes assignableScopes,
		String delimiter) {

		return StringUtil.merge(
			TransformUtil.transform(
				new TreeSet<>(
					assignableScopes.getApplicationScopeDescription(
						_companyId, applicationName)),
				HtmlUtil::escape),
			delimiter);
	}

	public Map<AssignableScopes, Relations> getAssignableScopesRelations(
		String applicationName) {

		Set<AssignableScopes> localAssignableScopes =
			_localAssignableScopesByApplicationName.get(applicationName);

		Map<AssignableScopes, Relations> localRelations = new HashMap<>();

		if (localAssignableScopes != null) {
			localRelations.putAll(
				getAssignableScopesRelations(localAssignableScopes));
		}

		Set<AssignableScopes> globalAssignableScopes =
			_globalAssignableScopesByApplicationName.get(applicationName);

		if (globalAssignableScopes == null) {
			return localRelations;
		}

		Map<AssignableScopes, Relations> assignableScopesRelations =
			new HashMap<>(localRelations);

		for (AssignableScopes assignableScopes : globalAssignableScopes) {
			AssignableScopes applicationAssignableScopes =
				assignableScopes.getApplicationAssignableScopes(
					applicationName);
			boolean unassignable = true;

			for (Map.Entry<AssignableScopes, Relations> entry :
					localRelations.entrySet()) {

				if (assignableScopes.contains(entry.getKey())) {
					Relations relations = entry.getValue();

					relations._globalAssignableScopes.add(assignableScopes);
				}

				if (!unassignable) {
					continue;
				}

				applicationAssignableScopes =
					applicationAssignableScopes.subtract(entry.getKey());

				Set<LiferayOAuth2Scope> liferayOAuth2Scopes =
					applicationAssignableScopes.getLiferayOAuth2Scopes();

				if (liferayOAuth2Scopes.isEmpty()) {
					unassignable = false;
				}
			}

			if (!unassignable) {
				continue;
			}

			Relations relations = assignableScopesRelations.computeIfAbsent(
				applicationAssignableScopes, a -> new Relations());

			relations._globalAssignableScopes.add(assignableScopes);
		}

		return _normalize(assignableScopesRelations);
	}

	public Map<AssignableScopes, Relations>
		getGlobalAssignableScopesRelations() {

		Map<AssignableScopes, Relations> assignableScopesRelations =
			new HashMap<>();

		for (Set<AssignableScopes> assignableScopess :
				_globalAssignableScopesByApplicationName.values()) {

			assignableScopesRelations.putAll(
				HashMapBuilder.put(
					assignableScopess, _assignableScopesRelations::get
				).build());
		}

		return assignableScopesRelations;
	}

	public List<Map.Entry<String, String>>
		getSortedApplicationNamesDescriptions() {

		Map<String, String> applicationNamesDescriptions =
			getApplicationNamesDescriptions();

		List<Map.Entry<String, String>> applicationsNamesDescriptionsList =
			new ArrayList<>(applicationNamesDescriptions.entrySet());

		applicationsNamesDescriptionsList.sort(
			Comparator.comparing(Map.Entry::getValue));

		return applicationsNamesDescriptionsList;
	}

	public class Relations {

		public Relations() {
			_scopeAliases = new HashSet<>();
		}

		public Relations(Set<String> scopeAliases) {
			_scopeAliases = new HashSet<>(scopeAliases);
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if ((object == null) || (getClass() != object.getClass())) {
				return false;
			}

			Relations relations = (Relations)object;

			if (Objects.equals(
					_globalAssignableScopes,
					relations._globalAssignableScopes) &&
				Objects.equals(_scopeAliases, relations._scopeAliases)) {

				return true;
			}

			return false;
		}

		public AssignableScopes getAssignedAssignableScopes() {
			return _assignedAssignableScopes;
		}

		public Set<String> getAssignedScopeAliases() {
			if (_assignedScopeAliases == null) {
				return Collections.emptySet();
			}

			return _assignedScopeAliases;
		}

		public Set<String> getGlobalScopeAliases() {
			Set<String> scopeAliases = new HashSet<>();

			for (AssignableScopes assignableScopes : _globalAssignableScopes) {
				Relations relations = _assignableScopesRelations.get(
					assignableScopes);

				scopeAliases.addAll(relations.getScopeAliases());
			}

			return scopeAliases;
		}

		public Set<String> getScopeAliases() {
			return _scopeAliases;
		}

		@Override
		public int hashCode() {
			return Objects.hash(_globalAssignableScopes, _scopeAliases);
		}

		@Override
		public String toString() {
			return StringBundler.concat(
				"[", StringUtil.merge(getScopeAliases()), "][",
				StringUtil.merge(getGlobalScopeAliases()), "]");
		}

		private AssignableScopes _assignedAssignableScopes;
		private Set<String> _assignedScopeAliases;
		private final Set<AssignableScopes> _globalAssignableScopes =
			new HashSet<>();
		private final Set<String> _scopeAliases;

	}

	protected Map<AssignableScopes, Relations> getAssignableScopesRelations(
		Set<AssignableScopes> assignableScopes) {

		return HashMapBuilder.put(
			assignableScopes, _assignableScopesRelations::get
		).build();
	}

	private Map<String, AssignableScopes> _getAssignableScopesByScopeAlias(
		long oAuth2ApplicationScopeAliasesId,
		ApplicationDescriptorLocator applicationDescriptorLocator,
		OAuth2ScopeGrantLocalService oAuth2ScopeGrantLocalService,
		ScopeDescriptorLocator scopeDescriptorLocator,
		ScopeLocator scopeLocator, ThemeDisplay themeDisplay) {

		Collection<OAuth2ScopeGrant> oAuth2ScopeGrants =
			oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
				oAuth2ApplicationScopeAliasesId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Map<String, AssignableScopes> scopeAliasesAssignableScopes =
			new HashMap<>();

		for (OAuth2ScopeGrant oAuth2ScopeGrant : oAuth2ScopeGrants) {
			for (String scopeAlias : oAuth2ScopeGrant.getScopeAliasesList()) {
				AssignableScopes assignableScopes =
					scopeAliasesAssignableScopes.computeIfAbsent(
						scopeAlias,
						x -> new AssignableScopes(
							applicationDescriptorLocator, _locale,
							scopeDescriptorLocator));

				assignableScopes.addLiferayOAuth2Scope(
					scopeLocator.getLiferayOAuth2Scope(
						themeDisplay.getCompanyId(),
						oAuth2ScopeGrant.getApplicationName(),
						oAuth2ScopeGrant.getScope()));
			}
		}

		return scopeAliasesAssignableScopes;
	}

	private Relations _getRelations(
		String scopeAlias, AssignableScopes assignableScopes) {

		return _assignableScopesRelations.compute(
			assignableScopes,
			(key, relations) -> {
				if (relations != null) {
					if (Validator.isNotNull(scopeAlias)) {
						relations._scopeAliases.add(scopeAlias);
					}

					return relations;
				}

				if (Validator.isNotNull(scopeAlias)) {
					return new Relations(Collections.singleton(scopeAlias));
				}

				return new Relations(Collections.emptySet());
			});
	}

	private void _indexAssignableScopes(
		AssignableScopes assignableScopes, Set<String> assignedApplicationNames,
		boolean indexAsGlobalAssignableScopes) {

		if ((assignableScopes == null) || (assignedApplicationNames == null)) {
			return;
		}

		if (indexAsGlobalAssignableScopes) {
			for (String applicationName : assignedApplicationNames) {
				Set<AssignableScopes> assignableScopesSet =
					_globalAssignableScopesByApplicationName.computeIfAbsent(
						applicationName, a -> new HashSet<>());

				assignableScopesSet.add(assignableScopes);
			}
		}
		else {
			Iterator<String> iterator = assignedApplicationNames.iterator();

			if (!iterator.hasNext()) {
				return;
			}

			String applicationName = iterator.next();

			Set<AssignableScopes> assignableScopesSet =
				_localAssignableScopesByApplicationName.computeIfAbsent(
					applicationName, a -> new HashSet<>());

			assignableScopesSet.add(assignableScopes);
		}
	}

	private <K, V> Map<V, K> _invertMap(Map<K, V> map) {
		Map<V, K> ret = new HashMap<>();

		for (Map.Entry<K, V> entry : map.entrySet()) {
			ret.put(entry.getValue(), entry.getKey());
		}

		return ret;
	}

	private Map<AssignableScopes, Relations> _normalize(
		Map<AssignableScopes, Relations> assignableScopesRelations) {

		Map<AssignableScopes, Relations> combinedAssignableScopesRelations =
			new HashMap<>();

		for (Map.Entry<AssignableScopes, Relations>
				assignableScopesRelationsEntry :
					assignableScopesRelations.entrySet()) {

			Relations relations = assignableScopesRelationsEntry.getValue();

			AssignableScopes assignableScopes =
				assignableScopesRelationsEntry.getKey();

			// Preserve assignable scopes that are assigned an alias

			if (SetUtil.isNotEmpty(relations.getAssignedScopeAliases()) ||
				SetUtil.isNotEmpty(relations.getScopeAliases())) {

				combinedAssignableScopesRelations.put(
					assignableScopes, relations);

				continue;
			}

			// Reduce other assignable scopes down to individual
			// application scopes but keep the global assignable scopes
			// relations of each original assignable scopes

			Set<AssignableScopes> applicationScopedAssignableScopesSet =
				assignableScopes.splitByApplicationScopes();

			for (AssignableScopes applicationScopedAssignableScopes :
					applicationScopedAssignableScopesSet) {

				Relations combinedRelations =
					combinedAssignableScopesRelations.computeIfAbsent(
						applicationScopedAssignableScopes,
						__ -> new Relations());

				combinedRelations._globalAssignableScopes.addAll(
					relations._globalAssignableScopes);
			}
		}

		// Merge those with identical global assignable scopes relations

		HashMap<Relations, AssignableScopes> relationsAssignableScopes =
			new HashMap<>();

		for (Map.Entry<AssignableScopes, Relations> entry :
				combinedAssignableScopesRelations.entrySet()) {

			relationsAssignableScopes.compute(
				entry.getValue(),
				(key, existingValue) -> {
					if (existingValue != null) {
						return existingValue.add(entry.getKey());
					}

					return entry.getKey();
				});
		}

		return _invertMap(relationsAssignableScopes);
	}

	private final ApplicationDescriptorLocator _applicationDescriptorLocator;
	private final Map<AssignableScopes, Relations> _assignableScopesRelations =
		new HashMap<>();
	private final long _companyId;
	private final Map<String, Set<AssignableScopes>>
		_globalAssignableScopesByApplicationName = new HashMap<>();
	private final Map<String, Set<AssignableScopes>>
		_localAssignableScopesByApplicationName = new HashMap<>();
	private final Locale _locale;

}