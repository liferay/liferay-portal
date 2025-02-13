/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.info.collection.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectEntrySingleFormVariationInfoCollectionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		ServiceContextThreadLocal.pushServiceContext(_getServiceContext());
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddCollectionDisplayToLayout() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		InfoCollectionProvider<ObjectEntry> infoCollectionProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoCollectionProvider.class, objectDefinition.getClassName());

		ContentLayoutTestUtil.addCollectionDisplayToLayout(
			JSONUtil.put(
				"itemSubtype",
				() -> {
					SingleFormVariationInfoCollectionProvider<?>
						singleFormVariationInfoCollectionProvider =
							(SingleFormVariationInfoCollectionProvider<?>)
								infoCollectionProvider;

					return singleFormVariationInfoCollectionProvider.
						getFormVariationKey();
				}
			).put(
				"itemType", infoCollectionProvider.getCollectionItemClassName()
			).put(
				"key", infoCollectionProvider.getKey()
			).put(
				"title",
				infoCollectionProvider.getLabel(LocaleUtil.getDefault())
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			),
			_layout, _layoutStructureProvider, null, null, 0,
			_segmentsExperienceId);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(), _segmentsExperienceId);

		List<CollectionStyledLayoutStructureItem>
			collectionStyledLayoutStructureItems =
				layoutStructure.getCollectionStyledLayoutStructureItems();

		Assert.assertEquals(
			collectionStyledLayoutStructureItems.toString(), 1,
			collectionStyledLayoutStructureItems.size());

		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem =
				collectionStyledLayoutStructureItems.get(0);

		JSONObject collectionJSONObject =
			collectionStyledLayoutStructureItem.getCollectionJSONObject();

		Assert.assertEquals(
			objectDefinition.getObjectDefinitionId(),
			collectionJSONObject.getLong("itemSubtype"));
		Assert.assertEquals(
			objectDefinition.getClassName(),
			collectionJSONObject.getString("itemType"));
		Assert.assertEquals(
			objectDefinition.getClassName(),
			collectionJSONObject.getString("key"));
		Assert.assertEquals(
			objectDefinition.getPluralLabel(LocaleUtil.getDefault()),
			collectionJSONObject.getString("title"));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testGetCollectionInfoPageDisplayAllItems() throws Exception {
		_testGetCollectionInfoPageDisplayAllItems(false);
		_testGetCollectionInfoPageDisplayAllItems(true);
	}

	private ServiceContext _getServiceContext() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		return serviceContext;
	}

	private void _testGetCollectionInfoPageDisplayAllItems(
			boolean enableIndexSearch)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false,
				enableIndexSearch, false, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectFieldName"
					).build()));

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		try {
			int expectedObjectEntriesSize = RandomTestUtil.randomInt(1, 10);

			for (int i = 0; i < expectedObjectEntriesSize; i++) {
				_objectEntryLocalService.addObjectEntry(
					TestPropsValues.getUserId(), 0,
					objectDefinition.getObjectDefinitionId(), null,
					HashMapBuilder.<String, Serializable>put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build(),
					ServiceContextTestUtil.getServiceContext());
			}

			InfoCollectionProvider<ObjectEntry> infoCollectionProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoCollectionProvider.class,
					objectDefinition.getClassName());

			Assert.assertEquals(
				objectDefinition.getClassName(),
				infoCollectionProvider.getKey());

			CollectionQuery collectionQuery = new CollectionQuery();

			collectionQuery.setPagination(Pagination.of(-1, -1));

			InfoPage<ObjectEntry> infoPage =
				infoCollectionProvider.getCollectionInfoPage(collectionQuery);

			Assert.assertEquals(
				expectedObjectEntriesSize, infoPage.getTotalCount());

			List<ObjectEntry> objectEntries =
				(List<ObjectEntry>)infoPage.getPageItems();

			Assert.assertEquals(
				objectEntries.toString(), expectedObjectEntriesSize,
				objectEntries.size());
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private Layout _layout;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}