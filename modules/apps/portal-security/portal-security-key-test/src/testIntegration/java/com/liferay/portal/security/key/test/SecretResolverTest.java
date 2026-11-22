/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.google.places.constants.GooglePlacesWebKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.BundleContextTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.SecretManager;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.secret.SecretProvider;
import com.liferay.portal.security.key.test.util.TestSecretProvider;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Pedro Victor Silvestre
 */
@RunWith(Arquillian.class)
public class SecretResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		BundleContext bundleContext = BundleContextTestUtil.getBundleContext(
			SecretResolverTest.class);

		_secretProviderServiceRegistration = bundleContext.registerService(
			SecretProvider.class, new TestSecretProvider(_SECRET_PROVIDER_ID),
			HashMapDictionaryBuilder.<String, Object>put(
				"secret.provider.id", _SECRET_PROVIDER_ID
			).build());

		ConfigurationTestUtil.saveConfiguration(
			_KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"companySecretProviderId", _SECRET_PROVIDER_ID
			).put(
				"systemSecretProviderId", _SECRET_PROVIDER_ID
			).build());
	}

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			_KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID);

		if (_secretProviderServiceRegistration != null) {
			_secretProviderServiceRegistration.unregister();
		}

		_companyLocalService.updatePreferences(
			TestPropsValues.getCompanyId(),
			UnicodePropertiesBuilder.create(
				true
			).put(
				_GOOGLE_MAPS_API_KEY, ""
			).put(
				GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY, ""
			).build());
	}

	@Test
	public void testResolve() throws Exception {
		long companyId = TestPropsValues.getCompanyId();
		String identifier = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		KeyReference keyReference = _putSecret(companyId, identifier, value);

		Assert.assertEquals(_SECRET_PROVIDER_ID, keyReference.getProviderId());
		Assert.assertEquals(identifier, keyReference.getIdentifier());
		Assert.assertEquals(KeyReference.Type.SECRET, keyReference.getType());
		Assert.assertEquals(
			value,
			_secretResolver.resolve(
				companyId,
				KeyReferenceUtil.toKeyReferenceString(keyReference)));
	}

	@Test
	public void testResolveUnderAnotherCompany() throws Exception {
		KeyReference keyReference = _putSecret(
			TestPropsValues.getCompanyId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		Assert.assertThrows(
			SecretException.class,
			() -> _secretResolver.resolve(
				CompanyConstants.SYSTEM,
				KeyReferenceUtil.toKeyReferenceString(keyReference)));
	}

	@Test
	public void testStore() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", true)) {

			_testStoreCompanyPreference(
				GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY);
			_testStoreCompanyPreference(_GOOGLE_MAPS_API_KEY);
			_testStoreGroupTypeSettings(
				GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY);
			_testStoreGroupTypeSettings(_GOOGLE_MAPS_API_KEY);
			_testStoreGroupTypeSettingsInheritedFromCompany();
			_testStoreIsIdempotent();
		}
	}

	private void _editCompany(String key, String value) throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(
			_layoutLocalService.getLayout(
				_portal.getControlPanelPlid(TestPropsValues.getCompanyId())));

		mockLiferayPortletActionRequest.addParameter(
			Constants.CMD, Constants.UPDATE);
		mockLiferayPortletActionRequest.addParameter(
			"settings--" + key + "--", value);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Assert.assertTrue(
			_editCompanyMVCActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()));
	}

	private void _editSiteSettings(String key, String value) throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"TypeSettingsProperties--" + key + "--", value);
		mockLiferayPortletActionRequest.addParameter(
			"liveGroupId", String.valueOf(_group.getGroupId()));

		Assert.assertTrue(
			_editSiteSettingsMVCActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()));
	}

	private MockLiferayPortletActionRequest
		_getMockLiferayPortletActionRequest() {

		return new MockLiferayPortletActionRequest() {

			@Override
			public String[] getParameterValues(String name) {
				Map<String, String[]> parameterMap = getParameterMap();

				return parameterMap.get(name);
			}

		};
	}

	private KeyReference _putSecret(
			long companyId, String identifier, String value)
		throws Exception {

		try (Secret secret = new Secret(
				new KeyReference(
					identifier, StringPool.STAR, KeyReference.Type.SECRET),
				value)) {

			return _secretManager.putSecret(companyId, secret);
		}
	}

	private void _testStoreCompanyPreference(String key) throws Exception {
		String value = RandomTestUtil.randomString();

		_editCompany(key, value);

		long companyId = TestPropsValues.getCompanyId();

		String storedValue = PrefsPropsUtil.getString(companyId, key);

		Assert.assertNotNull(KeyReferenceUtil.parseKeyReference(storedValue));

		Assert.assertEquals(
			value, _secretResolver.resolve(companyId, storedValue));
	}

	private void _testStoreGroupTypeSettings(String key) throws Exception {
		String value = RandomTestUtil.randomString();

		_editSiteSettings(key, value);

		Group group = _groupLocalService.getGroup(_group.getGroupId());

		String storedValue = group.getTypeSettingsProperty(key);

		Assert.assertNotNull(KeyReferenceUtil.parseKeyReference(storedValue));

		Assert.assertEquals(
			value, _secretResolver.resolve(group.getCompanyId(), storedValue));
	}

	private void _testStoreGroupTypeSettingsInheritedFromCompany()
		throws Exception {

		String value = RandomTestUtil.randomString();

		_editCompany(GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY, value);

		long companyId = TestPropsValues.getCompanyId();

		String storedValue = PrefsPropsUtil.getString(
			companyId, GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY);

		_editSiteSettings(
			GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY, storedValue);

		Group group = _groupLocalService.getGroup(_group.getGroupId());

		Assert.assertEquals(
			storedValue,
			group.getTypeSettingsProperty(
				GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY));

		Assert.assertEquals(
			value, _secretResolver.resolve(companyId, storedValue));
	}

	private void _testStoreIsIdempotent() throws Exception {
		_editCompany(
			GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY,
			RandomTestUtil.randomString());

		long companyId = TestPropsValues.getCompanyId();

		String storedValue = PrefsPropsUtil.getString(
			companyId, GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY);

		_editCompany(GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY, storedValue);

		Assert.assertEquals(
			storedValue,
			PrefsPropsUtil.getString(
				companyId, GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY));
	}

	private static final String _GOOGLE_MAPS_API_KEY = "googleMapsAPIKey";

	private static final String _KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID =
		"com.liferay.portal.security.key.internal.profile.configuration." +
			"KeyManagerCustomProfileConfiguration";

	private static final String _SECRET_PROVIDER_ID =
		RandomTestUtil.randomString();

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "mvc.command.name=/portal_settings/edit_company")
	private MVCActionCommand _editCompanyMVCActionCommand;

	@Inject(filter = "mvc.command.name=/site_admin/edit_site_settings")
	private MVCActionCommand _editSiteSettingsMVCActionCommand;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SecretManager _secretManager;

	private ServiceRegistration<SecretProvider>
		_secretProviderServiceRegistration;

	@Inject
	private SecretResolver _secretResolver;

}