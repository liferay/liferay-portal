/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.internal.feature.flag.test;

import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Joao Victor
 */
@FeatureFlag("LPD-62272")
@RunWith(Arquillian.class)
public class AIHubCellFeatureFlagListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_deleteOAuth2ApplicationAndSAPEntry();
	}

	@After
	public void tearDown() throws Exception {
		_deleteOAuth2ApplicationAndSAPEntry();
	}

	@Test
	public void testOnValue() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AIHubCellConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"serviceURL", ""
						).build())) {

			_deleteOAuth2ApplicationAndSAPEntry();

			_featureFlagListener.onValue(
				TestPropsValues.getCompanyId(), "LPD-62272", true);

			Assert.assertNull(_fetchOAuth2Application());
			Assert.assertNull(_fetchSAPEntry());
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AIHubCellConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"serviceURL",
							"https://" + RandomTestUtil.randomString()
						).build())) {

			_deleteOAuth2ApplicationAndSAPEntry();

			_featureFlagListener.onValue(
				TestPropsValues.getCompanyId(), "LPD-62272", true);

			Assert.assertNotNull(_fetchOAuth2Application());
			Assert.assertNotNull(_fetchSAPEntry());

			_featureFlagListener.onValue(
				TestPropsValues.getCompanyId(), "LPD-62272", false);

			Assert.assertNull(_fetchOAuth2Application());
			Assert.assertNull(_fetchSAPEntry());
		}
	}

	@Test
	public void testOnValueConcurrently() throws Exception {
		long companyId = TestPropsValues.getCompanyId();
		String serviceURL = "https://" + RandomTestUtil.randomString();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						companyId, AIHubCellConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"serviceURL", serviceURL
						).build())) {

			_deleteOAuth2ApplicationAndSAPEntry();

			CountDownLatch countDownLatch = new CountDownLatch(1);
			Dictionary<String, Object> properties =
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", companyId
				).put(
					"serviceURL", serviceURL
				).build();

			List<Callable<Void>> callables = new ArrayList<>();

			for (int i = 0; i < 5; i++) {
				callables.add(
					() -> {
						countDownLatch.await();

						_configurationModelListener.onAfterSave(
							AIHubCellConfiguration.class.getName(), properties);

						return null;
					});
				callables.add(
					() -> {
						countDownLatch.await();

						_featureFlagListener.onValue(
							companyId, "LPD-62272", true);

						return null;
					});
			}

			ExecutorService executorService = Executors.newFixedThreadPool(
				callables.size());

			try {
				List<Future<Void>> futures = TransformUtil.transform(
					callables, executorService::submit);

				countDownLatch.countDown();

				for (Future<Void> future : futures) {
					future.get();
				}
			}
			finally {
				executorService.shutdownNow();
			}

			Assert.assertEquals(
				1,
				ListUtil.count(
					_oAuth2ApplicationLocalService.getOAuth2Applications(
						companyId),
					oAuth2Application -> Objects.equals(
						oAuth2Application.getExternalReferenceCode(),
						"AI-HUB-CELL")));
			Assert.assertNotNull(_fetchSAPEntry());

			List<String> scopeAliasesList = _getScopeAliasesList();

			Set<String> scopeAliasesSet = SetUtil.fromList(scopeAliasesList);

			Collection<String> scopeAliases = _scopeLocator.getScopeAliases(
				companyId);

			Assert.assertEquals(
				scopeAliasesList.toString(),
				SetUtil.fromList(
					ListUtil.filter(
						Arrays.asList(
							"Liferay.Bulk.REST.everything.write",
							"Liferay.Headless.Admin.Fragment.everything",
							"Liferay.Headless.Admin.Taxonomy.everything.read",
							"Liferay.Headless.Admin.User.everything.read",
							"Liferay.Headless.Batch.Engine.everything",
							"Liferay.Headless.CMP.everything.read",
							"Liferay.Object.Admin.REST.everything.read",
							"Liferay.Portal.Search.REST.everything.read",
							"cmpprojectlink.everything"),
						scopeAliases::contains)),
				scopeAliasesSet);

			Assert.assertEquals(
				scopeAliasesList.toString(), scopeAliasesSet.size(),
				scopeAliasesList.size());
		}
	}

	@Test
	public void testOnValueWithLateScopeAliases() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			AIHubCellFeatureFlagListenerTest.class);

		bundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.bulk.rest.impl");

		Assert.assertNotNull(bundle);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AIHubCellConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"serviceURL",
							"https://" + RandomTestUtil.randomString()
						).build())) {

			_deleteOAuth2ApplicationAndSAPEntry();

			try {
				bundle.stop();

				_featureFlagListener.onValue(
					TestPropsValues.getCompanyId(), "LPD-62272", true);

				List<String> scopeAliasesList = _getScopeAliasesList();

				Assert.assertFalse(
					scopeAliasesList.contains(
						"Liferay.Bulk.REST.everything.write"));
			}
			finally {
				bundle.start();
			}

			IdempotentRetryAssert.retryAssert(
				10, TimeUnit.SECONDS,
				() -> {
					List<String> scopeAliasesList = _getScopeAliasesList();

					Assert.assertTrue(
						scopeAliasesList.contains(
							"Liferay.Bulk.REST.everything.write"));

					return null;
				});
		}
	}

	@Test
	public void testOnValueWithLocalServiceURL() throws Exception {
		User user = TestPropsValues.getUser();

		String clientId = RandomTestUtil.randomString();
		String clientSecret = RandomTestUtil.randomString();

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
				"AI-HUB-CELL", user.getUserId(), user.getScreenName(),
				Arrays.asList(GrantType.CLIENT_CREDENTIALS),
				"client_secret_post", user.getUserId(), clientId,
				ClientProfile.HEADLESS_SERVER.id(), clientSecret, null, null,
				null, 0, null, RandomTestUtil.randomString(), null,
				Arrays.asList(), false,
				Arrays.asList("Liferay.Portal.Search.REST.everything.read"),
				false, new ServiceContext());

		SAPEntry sapEntry = _sapEntryLocalService.addSAPEntry(
			user.getUserId(),
			StringBundler.concat(
				_SAP_ENTRY_ALLOWED_SERVICE_SIGNATURES,
				"\ncom.liferay.ai.hub.cell.rest.internal.resource.v1_0.",
				"AuthorizationTokenResourceImpl#postAuthorizationToken"),
			true, true, "AI_HUB_CELL_TOKEN",
			Collections.singletonMap(
				LocaleUtil.getDefault(), "AI_HUB_CELL_TOKEN"),
			new ServiceContext());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		String serviceURL = StringBundler.concat(
			"http://", company.getVirtualHostname(), ":",
			RandomTestUtil.randomInt(1000, 9999));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						company.getCompanyId(),
						AIHubCellConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"serviceURL", serviceURL
						).build())) {

			_featureFlagListener.onValue(
				company.getCompanyId(), "LPD-62272", true);
		}

		oAuth2Application = _oAuth2ApplicationLocalService.getOAuth2Application(
			oAuth2Application.getOAuth2ApplicationId());

		Assert.assertEquals(clientId, oAuth2Application.getClientId());
		Assert.assertEquals(
			clientSecret,
			_oAuth2ApplicationLocalService.getPlaintextClientSecret(
				oAuth2Application));
		Assert.assertEquals(serviceURL, oAuth2Application.getHomePageURL());

		Collection<String> scopeAliases = _scopeLocator.getScopeAliases(
			company.getCompanyId());

		Assert.assertEquals(
			SetUtil.fromList(
				ListUtil.filter(
					Arrays.asList(
						"Liferay.AI.Hub.REST.everything",
						"Liferay.AI.Hub.REST.everything.read",
						"Liferay.AI.Hub.REST.everything.write",
						"Liferay.Bulk.REST.everything.write",
						"Liferay.Headless.Admin.Taxonomy.everything.read",
						"Liferay.Headless.Admin.User.everything.read",
						"Liferay.Headless.Batch.Engine.everything",
						"Liferay.Headless.CMP.everything.read",
						"Liferay.Object.Admin.REST.everything.read",
						"Liferay.Portal.Search.REST.everything.read",
						"cmpprojectlink.everything"),
					scopeAliases::contains)),
			SetUtil.fromList(_getScopeAliasesList()));

		sapEntry = _sapEntryLocalService.getSAPEntry(sapEntry.getSapEntryId());

		Assert.assertEquals(
			Arrays.asList(_SAP_ENTRY_ALLOWED_SERVICE_SIGNATURES),
			sapEntry.getAllowedServiceSignaturesList());
	}

	private void _deleteOAuth2ApplicationAndSAPEntry() throws Exception {
		OAuth2Application oAuth2Application = _fetchOAuth2Application();

		if (oAuth2Application != null) {
			_oAuth2ApplicationLocalService.deleteOAuth2Application(
				oAuth2Application.getOAuth2ApplicationId());
		}

		SAPEntry sapEntry = _fetchSAPEntry();

		if (sapEntry != null) {
			_sapEntryLocalService.deleteSAPEntry(sapEntry);
		}
	}

	private OAuth2Application _fetchOAuth2Application() throws Exception {
		return _oAuth2ApplicationLocalService.
			fetchOAuth2ApplicationByExternalReferenceCode(
				"AI-HUB-CELL", TestPropsValues.getCompanyId());
	}

	private SAPEntry _fetchSAPEntry() throws Exception {
		return _sapEntryLocalService.fetchSAPEntry(
			TestPropsValues.getCompanyId(), "AI_HUB_CELL_TOKEN");
	}

	private List<String> _getScopeAliasesList() throws Exception {
		OAuth2Application oAuth2Application = _fetchOAuth2Application();

		return _oAuth2ApplicationScopeAliasesLocalService.getScopeAliasesList(
			oAuth2Application.getOAuth2ApplicationScopeAliasesId());
	}

	private static final String _SAP_ENTRY_ALLOWED_SERVICE_SIGNATURES =
		"com.liferay.portal.search.rest.internal.resource.v1_0." +
			"SearchResultResourceImpl#getSearchPage";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "model.class.name=com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration"
	)
	private ConfigurationModelListener _configurationModelListener;

	@Inject(
		filter = "component.name=com.liferay.ai.hub.cell.internal.feature.flag.AIHubCellFeatureFlagListener"
	)
	private FeatureFlagListener _featureFlagListener;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Inject
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

	@Inject
	private SAPEntryLocalService _sapEntryLocalService;

	@Inject
	private ScopeLocator _scopeLocator;

}