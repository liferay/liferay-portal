/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.list.retriever.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.info.pagination.InfoPage;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.list.retriever.ClassedModelListObjectReference;
import com.liferay.layout.list.retriever.DefaultLayoutListRetrieverContext;
import com.liferay.layout.list.retriever.KeyListObjectReference;
import com.liferay.layout.list.retriever.LayoutListRetriever;
import com.liferay.layout.list.retriever.LayoutListRetrieverContext;
import com.liferay.layout.list.retriever.LayoutListRetrieverRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class LayoutListRetrieverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-64102")
	public void testAssetEntryListLayoutListRetriever() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();
		long classNameId = _portal.getClassNameId(JournalArticle.class);
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				externalReferenceCode, TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"anyAssetType", String.valueOf(classNameId)
				).buildString(),
				serviceContext);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), _portal.getSiteDefaultLocale(_group),
			true, true, serviceContext);

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		ServiceContext companyGroupServiceContext =
			ServiceContextTestUtil.getServiceContext(
				companyGroup.getGroupId(), TestPropsValues.getUserId());

		AssetListEntry companyGroupAssetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				externalReferenceCode, TestPropsValues.getUserId(),
				companyGroup.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"anyAssetType", String.valueOf(classNameId)
				).buildString(),
				companyGroupServiceContext);

		JournalArticle companyGroupJournalArticle = JournalTestUtil.addArticle(
			companyGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			_portal.getSiteDefaultLocale(companyGroup), true, true,
			companyGroupServiceContext);

		DefaultLayoutListRetrieverContext layoutListRetrieverContext =
			new DefaultLayoutListRetrieverContext();

		layoutListRetrieverContext.setScopeGroupId(_group.getGroupId());

		_testAssetEntryListLayoutListRetriever(
			JSONUtil.put("classPK", assetListEntry.getAssetListEntryId()),
			layoutListRetrieverContext, journalArticle.getResourcePrimKey());
		_testAssetEntryListLayoutListRetriever(
			JSONUtil.put(
				"classPK", companyGroupAssetListEntry.getAssetListEntryId()),
			layoutListRetrieverContext,
			companyGroupJournalArticle.getResourcePrimKey());
		_testAssetEntryListLayoutListRetriever(
			JSONUtil.put("externalReferenceCode", externalReferenceCode),
			layoutListRetrieverContext, journalArticle.getResourcePrimKey());
		_testAssetEntryListLayoutListRetriever(
			JSONUtil.put(
				"classPK", RandomTestUtil.randomLong()
			).put(
				"externalReferenceCode", externalReferenceCode
			),
			layoutListRetrieverContext);
		_testAssetEntryListLayoutListRetriever(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"scopeExternalReferenceCode",
				companyGroup.getExternalReferenceCode()
			),
			layoutListRetrieverContext,
			companyGroupJournalArticle.getResourcePrimKey());
	}

	@Test
	public void testAssetRelatedInfoItemCollectionProviderLayoutListRetriever()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(LayoutListRetrieverTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<RelatedInfoItemCollectionProvider<?, ?>>
			serviceRegistration = bundleContext.registerService(
				(Class<RelatedInfoItemCollectionProvider<?, ?>>)
					(Class<?>)RelatedInfoItemCollectionProvider.class,
				new AssetEntryRelatedInfoItemCollectionProvider(), null);

		LayoutListRetriever<?, KeyListObjectReference> layoutListRetriever =
			(LayoutListRetriever<?, KeyListObjectReference>)
				_layoutListRetrieverRegistry.getLayoutListRetriever(
					InfoListProviderItemSelectorReturnType.class.getName());

		KeyListObjectReference keyListObjectReference =
			new KeyListObjectReference(
				JSONUtil.put(
					"key",
					AssetEntryRelatedInfoItemCollectionProvider.class.
						getName()));

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			new byte[0], null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId(),
				new String[] {"tag1", "tag2"}));

		DefaultLayoutListRetrieverContext layoutListRetrieverContext =
			new DefaultLayoutListRetrieverContext();

		layoutListRetrieverContext.setContextObject(fileEntry);
		layoutListRetrieverContext.setScopeGroupId(_group.getGroupId());

		InfoPage<?> infoPage = layoutListRetriever.getInfoPage(
			keyListObjectReference, layoutListRetrieverContext);

		List<Object> list = (List<Object>)infoPage.getPageItems();

		Assert.assertEquals(list.toString(), 2, list.size());

		AssetTag assetTag1 = (AssetTag)list.get(0);

		Assert.assertEquals("tag1", assetTag1.getName());

		AssetTag assetTag2 = (AssetTag)list.get(1);

		Assert.assertEquals("tag2", assetTag2.getName());

		serviceRegistration.unregister();
	}

	@Test
	public void testInfoCollectionProviderLayoutListRetriever() {
		Bundle bundle = FrameworkUtil.getBundle(LayoutListRetrieverTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<InfoCollectionProvider<?>> serviceRegistration =
			bundleContext.registerService(
				(Class<InfoCollectionProvider<?>>)
					(Class<?>)InfoCollectionProvider.class,
				new TestInfoCollectionProvider(), null);

		LayoutListRetriever<?, KeyListObjectReference> layoutListRetriever =
			(LayoutListRetriever<?, KeyListObjectReference>)
				_layoutListRetrieverRegistry.getLayoutListRetriever(
					InfoListProviderItemSelectorReturnType.class.getName());

		KeyListObjectReference keyListObjectReference =
			new KeyListObjectReference(
				JSONUtil.put(
					"key", TestInfoCollectionProvider.class.getName()));

		InfoPage<?> infoPage = layoutListRetriever.getInfoPage(
			keyListObjectReference, new DefaultLayoutListRetrieverContext());

		List<Object> list = (List<Object>)infoPage.getPageItems();

		Assert.assertEquals(list.toString(), 1, list.size());
		Assert.assertEquals(
			TestInfoCollectionProvider.class.getName(), list.get(0));

		serviceRegistration.unregister();
	}

	private void _testAssetEntryListLayoutListRetriever(
		JSONObject jsonObject,
		LayoutListRetrieverContext layoutListRetrieverContext,
		long... resourcePrimKeys) {

		LayoutListRetriever<?, ClassedModelListObjectReference>
			layoutListRetriever =
				(LayoutListRetriever<?, ClassedModelListObjectReference>)
					_layoutListRetrieverRegistry.getLayoutListRetriever(
						InfoListItemSelectorReturnType.class.getName());

		InfoPage<?> infoPage = layoutListRetriever.getInfoPage(
			new ClassedModelListObjectReference(jsonObject),
			layoutListRetrieverContext);

		List<Object> list = (List<Object>)infoPage.getPageItems();

		Assert.assertEquals(
			list.toString(), resourcePrimKeys.length, list.size());

		for (int i = 0; i < resourcePrimKeys.length; i++) {
			JournalArticle journalArticle = (JournalArticle)list.get(i);

			Assert.assertEquals(
				resourcePrimKeys[i], journalArticle.getResourcePrimKey());
		}
	}

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutListRetrieverRegistry _layoutListRetrieverRegistry;

	@Inject
	private Portal _portal;

	private static class AssetEntryRelatedInfoItemCollectionProvider
		implements RelatedInfoItemCollectionProvider<AssetEntry, AssetTag> {

		@Override
		public InfoPage<AssetTag> getCollectionInfoPage(
			CollectionQuery collectionQuery) {

			Object relatedItem = collectionQuery.getRelatedItem();

			if (!(relatedItem instanceof AssetEntry)) {
				return InfoPage.of(
					Collections.emptyList(), collectionQuery.getPagination(),
					0);
			}

			AssetEntry assetEntry = (AssetEntry)relatedItem;

			return InfoPage.of(assetEntry.getTags());
		}

		@Override
		public String getLabel(Locale locale) {
			return StringPool.BLANK;
		}

	}

	private static class TestInfoCollectionProvider
		implements InfoCollectionProvider<String> {

		@Override
		public InfoPage<String> getCollectionInfoPage(
			CollectionQuery collectionQuery) {

			return InfoPage.of(
				Collections.singletonList(
					TestInfoCollectionProvider.class.getName()));
		}

		@Override
		public String getLabel(Locale locale) {
			return StringPool.BLANK;
		}

	}

}