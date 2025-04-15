/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.layout.display.page.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.friendly.url.configuration.FriendlyURLSeparatorCompanyConfiguration;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.PortletPreferences;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class JournalArticleLayoutDisplayPageProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderJournalArticleWithExpiredArticleVersionInfoItemReference()
		throws Exception {

		JournalArticle originalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalTestUtil.updateArticle(originalArticle);

		JournalTestUtil.expireArticle(
			originalArticle.getGroupId(), originalArticle,
			originalArticle.getVersion());

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			originalArticle.getResourcePrimKey());

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			new ClassPKInfoItemIdentifier(assetEntry.getClassPK());

		classPKInfoItemIdentifier.setVersion(
			String.valueOf(originalArticle.getVersion()));

		Assert.assertNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					assetEntry.getClassName(), classPKInfoItemIdentifier)));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderJournalArticleWithExpiredJournalArticle()
		throws Exception {

		_journalArticle.setStatus(WorkflowConstants.STATUS_EXPIRED);

		Assert.assertNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_journalArticle));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderJournalArticleWithInvalidInfoItemReference() {
		Assert.assertNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					JournalArticle.class.getName(),
					_journalArticle.getResourcePrimKey() + 1)));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderJournalArticleWithInvalidUrlTitle() {
		Assert.assertNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_group.getGroupId(), RandomTestUtil.randomString()));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderJournalArticleWithJournalArticle() {
		Assert.assertNotNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_journalArticle));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderJournalArticleWithPendingJournalArticle()
		throws Exception {

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		_journalArticle.setStatus(WorkflowConstants.STATUS_PENDING);

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			Assert.assertNotNull(
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					_journalArticle));

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(
					UserLocalServiceUtil.getGuestUser(
						_journalArticle.getCompanyId())));

			Assert.assertNull(
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					_journalArticle));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderJournalArticleWithScheduledJournalArticle()
		throws Exception {

		_journalArticle.setStatus(WorkflowConstants.STATUS_SCHEDULED);

		Assert.assertNotNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_journalArticle));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderJournalArticleWithTrashedJournalArticle()
		throws Exception {

		_journalArticle.setStatus(WorkflowConstants.STATUS_IN_TRASH);

		Assert.assertNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_journalArticle));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderJournalArticleWithValidInfoItemReference() {
		Assert.assertNotNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					JournalArticle.class.getName(),
					_journalArticle.getResourcePrimKey())));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderJournalArticleWithValidUrlTitle() {
		Assert.assertNotNull(
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_group.getGroupId(), _journalArticle.getUrlTitle()));
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderParentJournalArticleContentSharingWithChildrenDisabled()
		throws Exception {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			_group.getCompanyId());

		String originalSitesContentSharingWithChildrenEnabledValue =
			portletPreferences.getValue(
				PropsKeys.SITES_CONTENT_SHARING_WITH_CHILDREN_ENABLED, null);

		try {
			portletPreferences.setValue(
				PropsKeys.SITES_CONTENT_SHARING_WITH_CHILDREN_ENABLED,
				String.valueOf(Sites.CONTENT_SHARING_WITH_CHILDREN_DISABLED));

			portletPreferences.store();

			Group childGroup = GroupTestUtil.addGroupToCompany(
				_group.getCompanyId(), _group.getGroupId());

			Assert.assertNull(
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					childGroup.getGroupId(), _journalArticle.getUrlTitle()));
		}
		finally {
			portletPreferences.setValue(
				PropsKeys.SITES_CONTENT_SHARING_WITH_CHILDREN_ENABLED,
				originalSitesContentSharingWithChildrenEnabledValue);

			portletPreferences.store();
		}
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderParentJournalArticleContentSharingWithChildrenEnabledByDefault()
		throws Exception {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			_group.getCompanyId());

		String originalSitesContentSharingWithChildrenEnabledValue =
			portletPreferences.getValue(
				PropsKeys.SITES_CONTENT_SHARING_WITH_CHILDREN_ENABLED, null);

		try {
			portletPreferences.setValue(
				PropsKeys.SITES_CONTENT_SHARING_WITH_CHILDREN_ENABLED,
				String.valueOf(
					Sites.CONTENT_SHARING_WITH_CHILDREN_ENABLED_BY_DEFAULT));

			portletPreferences.store();

			Group childGroup = GroupTestUtil.addGroupToCompany(
				_group.getCompanyId(), _group.getGroupId());

			Assert.assertNotNull(
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					childGroup.getGroupId(), _journalArticle.getUrlTitle()));
		}
		finally {
			portletPreferences.setValue(
				PropsKeys.SITES_CONTENT_SHARING_WITH_CHILDREN_ENABLED,
				originalSitesContentSharingWithChildrenEnabledValue);

			portletPreferences.store();
		}
	}

	@Test
	public void testGetURLSeparator() {
		Assert.assertEquals(
			FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE,
			_layoutDisplayPageProvider.getURLSeparator());
	}

	@Test
	public void testGetURLSeparatorWithConfiguredURLSeparator()
		throws Exception {

		String journalArticleFriendlyURLSeparator = "/journal-test1/";

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_group.getCompanyId(),
						FriendlyURLSeparatorCompanyConfiguration.class.
							getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"friendlyURLSeparatorsJSON",
							JSONUtil.put(
								JournalArticle.class.getName(),
								journalArticleFriendlyURLSeparator)
						).build())) {

			Assert.assertEquals(
				journalArticleFriendlyURLSeparator,
				_layoutDisplayPageProvider.getURLSeparator());
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.layout.display.page.JournalArticleLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<JournalArticle>
		_layoutDisplayPageProvider;

}