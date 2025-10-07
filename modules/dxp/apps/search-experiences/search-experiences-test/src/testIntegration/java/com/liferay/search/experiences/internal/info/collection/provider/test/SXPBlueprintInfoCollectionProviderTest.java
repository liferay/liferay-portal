/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.info.collection.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.pagination.InfoPage;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Joshua Cords
 */
@FeatureFlag("LPS-129412")
@RunWith(Arquillian.class)
public class SXPBlueprintInfoCollectionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPS-129412");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), false, "LPS-129412");
	}

	@Before
	public void setUp() throws Exception {
		_journalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_serviceContext = ServiceContextTestUtil.getServiceContext();

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		_serviceContext.setRequest(httpServletRequest);
	}

	@Test
	public void testGetCollectionInfoPage() throws Exception {
		SXPBlueprint sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_readJSON("configurationJSON"), null, null, StringPool.BLANK,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			_serviceContext);

		InfoCollectionProvider<JournalArticle> infoCollectionProvider =
			_infoItemServiceRegistry.getInfoItemService(
				InfoCollectionProvider.class,
				StringBundler.concat(
					SXPBlueprint.class.getName(), StringPool.UNDERLINE,
					sxpBlueprint.getCompanyId(), StringPool.UNDERLINE,
					sxpBlueprint.getExternalReferenceCode()));

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		InfoPage<JournalArticle> infoPage =
			infoCollectionProvider.getCollectionInfoPage(new CollectionQuery());

		ServiceContextThreadLocal.popServiceContext();

		List<? extends JournalArticle> journalArticles =
			infoPage.getPageItems();

		Assert.assertEquals(
			journalArticles.toString(), 1, journalArticles.size());

		JournalArticle journalArticle = journalArticles.get(0);

		Assert.assertEquals(
			_journalArticle.getPrimaryKey(), journalArticle.getPrimaryKey());
	}

	@Test
	public void testIsAvailable() throws Exception {
		SXPBlueprint sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_readJSON("configurationJSON"), null, null, StringPool.BLANK,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			_serviceContext);

		InfoCollectionProvider<JournalArticle> infoCollectionProvider =
			_infoItemServiceRegistry.getInfoItemService(
				InfoCollectionProvider.class,
				StringBundler.concat(
					SXPBlueprint.class.getName(), StringPool.UNDERLINE,
					sxpBlueprint.getCompanyId(), StringPool.UNDERLINE,
					sxpBlueprint.getExternalReferenceCode()));

		Assert.assertTrue(infoCollectionProvider.isAvailable());

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					RandomTestUtil.randomLong())) {

			Assert.assertFalse(infoCollectionProvider.isAvailable());
		}
	}

	private String _readJSON(String name) {
		return StringUtil.read(
			_clazz,
			StringBundler.concat(
				"dependencies/", _clazz.getSimpleName(), StringPool.PERIOD,
				name, ".json"));
	}

	private final Class<?> _clazz = getClass();

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@DeleteAfterTestRun
	private JournalArticle _journalArticle;

	private ServiceContext _serviceContext;

	@Inject
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

}