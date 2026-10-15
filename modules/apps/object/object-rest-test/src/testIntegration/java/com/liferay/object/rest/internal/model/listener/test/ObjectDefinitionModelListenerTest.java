/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tejesh Boggavarapu
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testOnAfterRemove() throws Exception {
		_testOnAfterRemoveDeletesOAuth2ScopeGrants();
		_testOnAfterRemoveKeepsOAuth2Authorization();
	}

	private OAuth2Application _addOAuth2Application(
			String... objectDefinitionScopeAliases)
		throws Exception {

		List<String> scopeAliases = new ArrayList<>(
			Arrays.asList(objectDefinitionScopeAliases));

		scopeAliases.add("Liferay.Headless.Admin.User.everything");

		User user = TestPropsValues.getUser();

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.addOAuth2Application(
				user.getCompanyId(), user.getUserId(), user.getFullName(),
				List.of(GrantType.CLIENT_CREDENTIALS), "client_secret_post",
				user.getUserId(),
				OAuth2SecureRandomGenerator.generateClientId(),
				ClientProfile.HEADLESS_SERVER.id(),
				OAuth2SecureRandomGenerator.generateClientSecret(), null,
				List.of(), "https://" + RandomTestUtil.randomString(), 0, null,
				RandomTestUtil.randomString(), null, List.of(), false,
				scopeAliases, false, new ServiceContext());

		_oAuth2Applications.add(oAuth2Application);

		return oAuth2Application;
	}

	private ObjectDefinition _addObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						RandomTestUtil.randomString(),
						"x" + RandomTestUtil.randomString(), false)));

		_objectDefinitions.add(objectDefinition);

		return objectDefinition;
	}

	private String _getScopeAlias(ObjectDefinition objectDefinition) {
		return objectDefinition.getOSGiJaxRsName() + ".everything";
	}

	private Set<String> _getScopeAliases(
		Collection<OAuth2ScopeGrant> oAuth2ScopeGrants) {

		Set<String> scopeAliases = new HashSet<>();

		for (OAuth2ScopeGrant oAuth2ScopeGrant : oAuth2ScopeGrants) {
			scopeAliases.addAll(oAuth2ScopeGrant.getScopeAliasesList());
		}

		return scopeAliases;
	}

	private Set<String> _getScopeAliases(OAuth2Application oAuth2Application) {
		return new HashSet<>(
			_oAuth2ApplicationScopeAliasesLocalService.getScopeAliasesList(
				oAuth2Application.getOAuth2ApplicationScopeAliasesId()));
	}

	private void _testOnAfterRemoveDeletesOAuth2ScopeGrants() throws Exception {
		ObjectDefinition objectDefinition = _addObjectDefinition();

		String scopeAlias = _getScopeAlias(objectDefinition);

		String otherObjectDefinitionScopeAlias = _getScopeAlias(
			_addObjectDefinition());

		OAuth2Application oAuth2Application = _addOAuth2Application(
			scopeAlias, otherObjectDefinitionScopeAlias);

		Assert.assertEquals(
			Set.of(
				scopeAlias, otherObjectDefinitionScopeAlias,
				"Liferay.Headless.Admin.User.everything"),
			_getScopeAliases(oAuth2Application));

		OAuth2ScopeGrant oAuth2ScopeGrant = null;

		for (OAuth2ScopeGrant curOAuth2ScopeGrant :
				_oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
					oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			List<String> scopeAliasesList =
				curOAuth2ScopeGrant.getScopeAliasesList();

			if (scopeAliasesList.contains(scopeAlias)) {
				oAuth2ScopeGrant = curOAuth2ScopeGrant;

				break;
			}
		}

		OAuth2ScopeGrant otherBundleOAuth2ScopeGrant =
			_oAuth2ScopeGrantLocalService.createOAuth2ScopeGrant(
				oAuth2ScopeGrant.getCompanyId(), RandomTestUtil.randomLong(),
				oAuth2ScopeGrant.getApplicationName(),
				RandomTestUtil.randomString(), oAuth2ScopeGrant.getScope(),
				oAuth2ScopeGrant.getScopeAliasesList());

		_oAuth2ScopeGrants.add(otherBundleOAuth2ScopeGrant);

		OAuth2ScopeGrant otherCompanyOAuth2ScopeGrant =
			_oAuth2ScopeGrantLocalService.createOAuth2ScopeGrant(
				RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
				oAuth2ScopeGrant.getApplicationName(),
				oAuth2ScopeGrant.getBundleSymbolicName(),
				oAuth2ScopeGrant.getScope(),
				oAuth2ScopeGrant.getScopeAliasesList());

		_oAuth2ScopeGrants.add(otherCompanyOAuth2ScopeGrant);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		Assert.assertEquals(
			Set.of(
				otherObjectDefinitionScopeAlias,
				"Liferay.Headless.Admin.User.everything"),
			_getScopeAliases(oAuth2Application));
		Assert.assertNotNull(
			_oAuth2ScopeGrantLocalService.fetchOAuth2ScopeGrant(
				otherBundleOAuth2ScopeGrant.getOAuth2ScopeGrantId()));
		Assert.assertNotNull(
			_oAuth2ScopeGrantLocalService.fetchOAuth2ScopeGrant(
				otherCompanyOAuth2ScopeGrant.getOAuth2ScopeGrantId()));
	}

	private void _testOnAfterRemoveKeepsOAuth2Authorization() throws Exception {
		User user = TestPropsValues.getUser();

		ObjectDefinition objectDefinition = _addObjectDefinition();

		String scopeAlias = _getScopeAlias(objectDefinition);

		OAuth2Application oAuth2Application = _addOAuth2Application(scopeAlias);

		Date date = new Date();

		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.addOAuth2Authorization(
				user.getCompanyId(), user.getUserId(), user.getFullName(),
				oAuth2Application.getOAuth2ApplicationId(),
				oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
				RandomTestUtil.randomString(), date,
				new Date(date.getTime() + Time.HOUR), List.of(), null, null,
				RandomTestUtil.randomString(), date,
				new Date(date.getTime() + Time.DAY));

		List<OAuth2ScopeGrant> oAuth2ScopeGrants = new ArrayList<>(
			_oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
				oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

		_oAuth2ScopeGrantLocalService.addOAuth2AuthorizationOAuth2ScopeGrants(
			oAuth2Authorization.getOAuth2AuthorizationId(), oAuth2ScopeGrants);

		Assert.assertEquals(
			Set.of(scopeAlias, "Liferay.Headless.Admin.User.everything"),
			_getScopeAliases(
				_oAuth2ScopeGrantLocalService.
					getOAuth2AuthorizationOAuth2ScopeGrants(
						oAuth2Authorization.getOAuth2AuthorizationId())));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		Assert.assertNotNull(
			_oAuth2AuthorizationLocalService.fetchOAuth2Authorization(
				oAuth2Authorization.getOAuth2AuthorizationId()));
		Assert.assertEquals(
			Set.of("Liferay.Headless.Admin.User.everything"),
			_getScopeAliases(
				_oAuth2ScopeGrantLocalService.
					getOAuth2AuthorizationOAuth2ScopeGrants(
						oAuth2Authorization.getOAuth2AuthorizationId())));
	}

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Inject
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

	@DeleteAfterTestRun
	private final List<OAuth2Application> _oAuth2Applications =
		new ArrayList<>();

	@Inject
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	@Inject
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	@DeleteAfterTestRun
	private final List<OAuth2ScopeGrant> _oAuth2ScopeGrants = new ArrayList<>();

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

}