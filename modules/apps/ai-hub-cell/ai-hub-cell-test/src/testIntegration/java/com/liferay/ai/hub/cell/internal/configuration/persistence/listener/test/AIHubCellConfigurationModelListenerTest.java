/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.internal.configuration.persistence.listener.test;

import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Joao Victor
 */
@RunWith(Arquillian.class)
public class AIHubCellConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_locale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);
	}

	@After
	public void tearDown() throws Exception {
		LocaleThreadLocal.setThemeDisplayLocale(_locale);

		if (!_configurationSaved) {
			return;
		}

		ConfigurationTestUtil.deleteConfiguration(
			AIHubCellConfiguration.class.getName());

		OAuth2Application oAuth2Application = _fetchOAuth2Application();

		if (_homePageURL == null) {
			_oAuth2ApplicationLocalService.deleteOAuth2Application(
				oAuth2Application);

			return;
		}

		oAuth2Application.setHomePageURL(_homePageURL);

		_oAuth2ApplicationLocalService.updateOAuth2Application(
			oAuth2Application);
	}

	@FeatureFlag("LPD-62272")
	@Test
	public void testOnAfterSave() throws Exception {
		OAuth2Application oAuth2Application = _fetchOAuth2Application();

		if (oAuth2Application != null) {
			_homePageURL = oAuth2Application.getHomePageURL();
		}

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		String serviceURL1 = StringBundler.concat(
			"http://", company.getVirtualHostname(), ":",
			RandomTestUtil.randomInt(1000, 4999));

		_configurationSaved = true;
		_saveConfiguration(serviceURL1);

		oAuth2Application = _fetchOAuth2Application();

		Assert.assertEquals(serviceURL1, oAuth2Application.getHomePageURL());

		String serviceURL2 = StringBundler.concat(
			"http://", company.getVirtualHostname(), ":",
			RandomTestUtil.randomInt(5000, 9999));

		_saveConfiguration(serviceURL2);

		OAuth2Application updatedOAuth2Application = _fetchOAuth2Application();

		Assert.assertEquals(
			oAuth2Application.getClientId(),
			updatedOAuth2Application.getClientId());
		Assert.assertEquals(
			oAuth2Application.getClientSecret(),
			updatedOAuth2Application.getClientSecret());
		Assert.assertEquals(
			serviceURL2, updatedOAuth2Application.getHomePageURL());

		_group = GroupTestUtil.addGroup();

		String siteHostname =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) + ".com";

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false,
			TreeMapBuilder.put(
				siteHostname, StringPool.BLANK
			).build());

		_saveConfiguration("http://" + siteHostname);

		updatedOAuth2Application = _fetchOAuth2Application();

		Assert.assertEquals(
			serviceURL2, updatedOAuth2Application.getHomePageURL());
	}

	@Test
	public void testOnBeforeSave() throws Exception {
		AssertUtils.assertFailure(
			ConfigurationModelListenerException.class,
			StringBundler.concat(
				"The listener ",
				_configurationModelListener.getClass(
				).getName(),
				" was unable to save configuration ",
				AIHubCellConfiguration.class.getName(), ": ",
				_language.get(
					LocaleUtil.US,
					"please-enter-a-service-url-that-does-not-end-with-a-" +
						"slash")),
			() -> _configurationModelListener.onBeforeSave(
				RandomTestUtil.randomString(),
				HashMapDictionaryBuilder.<String, Object>put(
					"serviceURL", "https://example.com/"
				).build()));
	}

	private OAuth2Application _fetchOAuth2Application() throws Exception {
		return _oAuth2ApplicationLocalService.
			fetchOAuth2ApplicationByExternalReferenceCode(
				"AI-HUB-CELL", TestPropsValues.getCompanyId());
	}

	private void _saveConfiguration(String serviceURL) throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			AIHubCellConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"serviceURL", serviceURL
			).build());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "model.class.name=com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration"
	)
	private ConfigurationModelListener _configurationModelListener;

	private boolean _configurationSaved;

	@DeleteAfterTestRun
	private Group _group;

	private String _homePageURL;

	@Inject
	private Language _language;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	private Locale _locale;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

}