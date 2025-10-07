/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.asset.model.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class DepotAssetRendererFactoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "name"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "description"
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetAssetRendererDLFileEntryWithRelatedGroup()
		throws Exception {

		ServiceContextThreadLocal.pushServiceContext(_getServiceContext());

		try {
			DepotEntryGroupRel depotEntryGroupRel =
				_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
					_depotEntry.getDepotEntryId(), _group.getGroupId());

			byte[] bytes = TestDataConstants.TEST_BYTE_ARRAY;

			DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
				null, TestPropsValues.getUserId(), _depotEntry.getGroupId(),
				_depotEntry.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString() + ".txt",
				MimeTypesUtil.getExtensionContentType("txt"),
				StringUtil.randomString(), null, StringPool.BLANK,
				StringPool.BLANK,
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
				null, null, new UnsyncByteArrayInputStream(bytes), bytes.length,
				null, null, null,
				ServiceContextTestUtil.getServiceContext(
					_depotEntry.getGroupId()));

			AssetRendererFactory<DLFileEntry> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
					DLFileEntry.class);

			AssetRenderer<DLFileEntry> assetRenderer =
				assetRendererFactory.getAssetRenderer(
					dlFileEntry.getPrimaryKey());

			Assert.assertNotNull(assetRenderer);

			_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
				depotEntryGroupRel);

			assetRenderer = assetRendererFactory.getAssetRenderer(
				dlFileEntry.getPrimaryKey());

			Assert.assertNull(assetRenderer);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetAssetRendererJournalArticleWithInvalidGroup()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_depotEntry.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			Collections.emptyMap());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(RandomTestUtil.randomLong());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			AssetRendererFactory<JournalArticle> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
					JournalArticle.class);

			Assert.assertNull(
				assetRendererFactory.getAssetRenderer(
					journalArticle.getResourcePrimKey()));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetAssetRendererJournalArticleWithRelatedGroup()
		throws Exception {

		ServiceContext serviceContext = _getServiceContext();

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			DepotEntryGroupRel depotEntryGroupRel =
				_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
					_depotEntry.getDepotEntryId(), _group.getGroupId());

			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_depotEntry.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				serviceContext);

			AssetRendererFactory<JournalArticle> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
					JournalArticle.class);

			AssetRenderer<JournalArticle> assetRenderer =
				assetRendererFactory.getAssetRenderer(
					journalArticle.getResourcePrimKey());

			Assert.assertNotNull(assetRenderer);

			_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
				depotEntryGroupRel);

			assetRenderer = assetRendererFactory.getAssetRenderer(
				journalArticle.getResourcePrimKey());

			Assert.assertNull(assetRenderer);

			Assert.assertEquals(
				_group.getGroupId(),
				serviceContext.getAttribute("scopeGroupId"));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private ServiceContext _getServiceContext() throws Exception {
		User user = TestPropsValues.getUser();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, user.getUserId());

		serviceContext.setLanguageId(LocaleUtil.toLanguageId(user.getLocale()));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		return serviceContext;
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

}