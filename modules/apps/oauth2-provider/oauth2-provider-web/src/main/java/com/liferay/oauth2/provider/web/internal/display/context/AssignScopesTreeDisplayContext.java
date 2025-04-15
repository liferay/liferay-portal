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
import com.liferay.oauth2.provider.scope.liferay.spi.ScopeDescriptorLocator;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.matcher.ScopeMatcherFactory;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.oauth2.provider.web.internal.tree.Tree;
import com.liferay.oauth2.provider.web.internal.util.ScopeTreeUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.PortletRequest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Marta Medio
 */
public class AssignScopesTreeDisplayContext
	extends OAuth2AdminPortletDisplayContext {

	public AssignScopesTreeDisplayContext(
			DLURLHelper dlURLHelper,
			OAuth2ApplicationScopeAliasesLocalService
				oAuth2ApplicationScopeAliasesLocalService,
			OAuth2ApplicationService oAuth2ApplicationService,
			OAuth2ProviderConfiguration oAuth2ProviderConfiguration,
			OAuth2ScopeGrantLocalService oAuth2ScopeGrantLocalService,
			PortletRequest portletRequest,
			ScopeDescriptorLocator scopeDescriptorLocator,
			ScopeLocator scopeLocator, ScopeMatcherFactory scopeMatcherFactory,
			ThemeDisplay themeDisplay)
		throws PortalException {

		super(
			dlURLHelper, oAuth2ApplicationScopeAliasesLocalService,
			oAuth2ApplicationService, oAuth2ProviderConfiguration,
			portletRequest, themeDisplay);

		_scopeDescriptorLocator = scopeDescriptorLocator;
		_scopeLocator = scopeLocator;

		OAuth2Application oAuth2Application = getOAuth2Application();

		_assignedScopeAliases = getAssignedScopeAliases(
			oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
			oAuth2ScopeGrantLocalService);

		Set<String> scopeAliases = new LinkedHashSet<>(
			scopeLocator.getScopeAliases(themeDisplay.getCompanyId()));

		_assignedDeletedScopeAliases = _getAssignedDeletedScopeAliases(
			scopeAliases);

		scopeAliases.addAll(_assignedScopeAliases);

		_scopeAliasesDescriptionsMap = _getScopeAliasesDescriptionsMap(
			scopeAliases);

		_scopeAliasTreeNode = ScopeTreeUtil.getScopeAliasTreeNode(
			scopeAliases, scopeMatcherFactory);
	}

	public Set<String> getAssignedDeletedScopeAliases() {
		return _assignedDeletedScopeAliases;
	}

	public Set<String> getAssignedScopeAliases() {
		return _assignedScopeAliases;
	}

	public Map<String, String> getScopeAliasesDescriptionsMap() {
		return _scopeAliasesDescriptionsMap;
	}

	public Tree.Node<String> getScopeAliasTreeNode() {
		return _scopeAliasTreeNode;
	}

	protected Set<String> getAssignedScopeAliases(
		long oAuth2ApplicationScopeAliasesId,
		OAuth2ScopeGrantLocalService oAuth2ScopeGrantLocalService) {

		Set<String> assignedScopeAliases = new HashSet<>();

		for (OAuth2ScopeGrant oAuth2ScopeGrant :
				oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
					oAuth2ApplicationScopeAliasesId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			assignedScopeAliases.addAll(oAuth2ScopeGrant.getScopeAliasesList());
		}

		return assignedScopeAliases;
	}

	private Set<String> _getAssignedDeletedScopeAliases(
		Set<String> scopeAliases) {

		Set<String> assignedDeletedScopeAliases = new HashSet<>();

		for (String assignedScopeAlias : _assignedScopeAliases) {
			if (!scopeAliases.contains(assignedScopeAlias)) {
				assignedDeletedScopeAliases.add(assignedScopeAlias);
			}
		}

		return assignedDeletedScopeAliases;
	}

	private String _getDescription(String scopeAlias, Locale locale) {
		ScopeDescriptor scopeDescriptor =
			_scopeDescriptorLocator.getScopeDescriptor(
				themeDisplay.getCompanyId());

		if (scopeDescriptor != null) {
			String description = scopeDescriptor.describeScope(
				scopeAlias, locale);

			if (description != null) {
				return description;
			}
		}

		Set<String> descriptions = new LinkedHashSet<>();

		for (LiferayOAuth2Scope liferayOAuth2Scope :
				_scopeLocator.getLiferayOAuth2Scopes(
					themeDisplay.getCompanyId(), scopeAlias)) {

			ScopeDescriptor applicationScopeDescriptor =
				_scopeDescriptorLocator.getScopeDescriptor(
					themeDisplay.getCompanyId(),
					liferayOAuth2Scope.getApplicationName());

			descriptions.add(
				applicationScopeDescriptor.describeScope(
					liferayOAuth2Scope.getScope(), locale));
		}

		return StringUtil.merge(descriptions, StringPool.COMMA_AND_SPACE);
	}

	private Map<String, String> _getScopeAliasesDescriptionsMap(
		Set<String> scopeAliases) {

		Map<String, String> map = new HashMap<>();

		for (String scopeAlias : scopeAliases) {
			map.put(
				scopeAlias,
				_getDescription(scopeAlias, themeDisplay.getLocale()));
		}

		return map;
	}

	private final Set<String> _assignedDeletedScopeAliases;
	private final Set<String> _assignedScopeAliases;
	private final Map<String, String> _scopeAliasesDescriptionsMap;
	private final Tree.Node<String> _scopeAliasTreeNode;
	private final ScopeDescriptorLocator _scopeDescriptorLocator;
	private final ScopeLocator _scopeLocator;

}