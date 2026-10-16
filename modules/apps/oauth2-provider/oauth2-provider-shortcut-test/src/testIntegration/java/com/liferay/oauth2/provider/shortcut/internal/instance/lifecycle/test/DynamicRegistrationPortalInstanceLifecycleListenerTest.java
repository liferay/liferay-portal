/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.shortcut.internal.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.constants.OAuth2ApplicationConstants;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge García Jiménez
 */
@RunWith(Arquillian.class)
public class DynamicRegistrationPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testPortalInstanceRegistered() throws Exception {
		User user = _userLocalService.getUserByScreenName(
			TestPropsValues.getCompanyId(),
			UserConstants.SCREEN_NAME_DEFAULT_SERVICE_ACCOUNT);

		OAuth2Application dynamicallyRegisteredOAuth2Application =
			_addOAuth2Application(
				user, user.getScreenName() + "_dynamic_registered",
				GrantType.AUTHORIZATION_CODE, GrantType.CLIENT_CREDENTIALS);

		OAuth2Authorization dynamicallyRegisteredOAuth2Authorization =
			_addOAuth2Authorization(
				dynamicallyRegisteredOAuth2Application, user);

		OAuth2Authorization oAuth2Authorization = _addOAuth2Authorization(
			dynamicallyRegisteredOAuth2Application, TestPropsValues.getUser());

		OAuth2Application dynamicRegistratorOAuth2Application =
			_getDynamicRegistratorOAuth2Application();

		dynamicRegistratorOAuth2Application =
			_oAuth2ApplicationLocalService.updateScopeAliases(
				user.getUserId(), user.getScreenName(),
				dynamicRegistratorOAuth2Application.getOAuth2ApplicationId(),
				Collections.singletonList(RandomTestUtil.randomString()));

		OAuth2Authorization dynamicRegistratorOAuth2Authorization =
			_addOAuth2Authorization(dynamicRegistratorOAuth2Application, user);

		OAuth2Application oAuth2Application = _addOAuth2Application(
			user, user.getScreenName(), GrantType.CLIENT_CREDENTIALS);

		_portalInstanceLifecycleListener.portalInstanceRegistered(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Assert.assertNotNull(
			_oAuth2AuthorizationLocalService.fetchOAuth2Authorization(
				oAuth2Authorization.getOAuth2AuthorizationId()));
		Assert.assertNull(
			_oAuth2AuthorizationLocalService.fetchOAuth2Authorization(
				dynamicallyRegisteredOAuth2Authorization.
					getOAuth2AuthorizationId()));
		Assert.assertNull(
			_oAuth2AuthorizationLocalService.fetchOAuth2Authorization(
				dynamicRegistratorOAuth2Authorization.
					getOAuth2AuthorizationId()));

		dynamicallyRegisteredOAuth2Application =
			_oAuth2ApplicationLocalService.getOAuth2Application(
				dynamicallyRegisteredOAuth2Application.
					getOAuth2ApplicationId());

		Assert.assertEquals(
			Collections.singletonList(GrantType.AUTHORIZATION_CODE),
			dynamicallyRegisteredOAuth2Application.getAllowedGrantTypesList());

		dynamicRegistratorOAuth2Application =
			_oAuth2ApplicationLocalService.getOAuth2Application(
				dynamicRegistratorOAuth2Application.getOAuth2ApplicationId());

		Assert.assertEquals(
			0,
			dynamicRegistratorOAuth2Application.
				getOAuth2ApplicationScopeAliasesId());

		oAuth2Application = _oAuth2ApplicationLocalService.getOAuth2Application(
			oAuth2Application.getOAuth2ApplicationId());

		Assert.assertEquals(
			Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
			oAuth2Application.getAllowedGrantTypesList());
	}

	private OAuth2Application _addOAuth2Application(
			User user, String userName, GrantType... grantTypes)
		throws Exception {

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.addOAuth2Application(
				user.getCompanyId(), user.getUserId(), userName,
				Arrays.asList(grantTypes), "client_secret_post",
				user.getUserId(),
				OAuth2SecureRandomGenerator.generateClientId(),
				ClientProfile.WEB_APPLICATION.id(),
				OAuth2SecureRandomGenerator.generateClientSecret(), null, null,
				null, 0, null, RandomTestUtil.randomString(), null,
				Collections.singletonList(
					"https://" + RandomTestUtil.randomString()),
				false, false, null, new ServiceContext());

		_oAuth2Applications.add(oAuth2Application);

		return oAuth2Application;
	}

	private OAuth2Authorization _addOAuth2Authorization(
		OAuth2Application oAuth2Application, User user) {

		return _oAuth2AuthorizationLocalService.addOAuth2Authorization(
			oAuth2Application.getCompanyId(), user.getUserId(),
			user.getScreenName(), oAuth2Application.getOAuth2ApplicationId(),
			oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
			RandomTestUtil.randomString(), new Date(),
			new Date(System.currentTimeMillis() + Time.HOUR), null, null, null,
			null, null, null);
	}

	private OAuth2Application _getDynamicRegistratorOAuth2Application()
		throws Exception {

		DynamicQuery dynamicQuery =
			_oAuth2ApplicationLocalService.dynamicQuery();

		Property companyIdProperty = PropertyFactoryUtil.forName("companyId");

		dynamicQuery.add(companyIdProperty.eq(TestPropsValues.getCompanyId()));

		Property nameProperty = PropertyFactoryUtil.forName("name");

		dynamicQuery.add(
			nameProperty.eq(
				OAuth2ApplicationConstants.NAME_DYNAMIC_REGISTRATOR));

		List<OAuth2Application> oAuth2Applications =
			_oAuth2ApplicationLocalService.dynamicQuery(dynamicQuery);

		return oAuth2Applications.get(0);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@DeleteAfterTestRun
	private final List<OAuth2Application> _oAuth2Applications =
		new ArrayList<>();

	@Inject
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	@Inject(
		filter = "component.name=com.liferay.oauth2.provider.shortcut.internal.instance.lifecycle.DynamicRegistrationPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

	@Inject
	private UserLocalService _userLocalService;

}