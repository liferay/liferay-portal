/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.system.info.collection.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.system.SystemObjectEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Map;

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
public class SystemObjectEntrySingleFormVariationInfoCollectionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(_getServiceContext());
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetCollectionInfoPage() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(),
				Organization.class.getSimpleName());

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(objectDefinition.getName());

		long organizationId1 = systemObjectDefinitionManager.addBaseModel(
			false, TestPropsValues.getUser(),
			HashMapBuilder.<String, Object>put(
				"comment", "Comment 1"
			).put(
				"name", "Name 1"
			).build());
		long organizationId2 = systemObjectDefinitionManager.addBaseModel(
			false, TestPropsValues.getUser(),
			HashMapBuilder.<String, Object>put(
				"comment", "Comment 2"
			).put(
				"name", "Name 2"
			).build());

		InfoCollectionProvider<SystemObjectEntry> infoCollectionProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoCollectionProvider.class,
				objectDefinition.getClassName() + StringPool.POUND +
					objectDefinition.getObjectDefinitionId());

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setPagination(Pagination.of(1, 0));

		InfoPage<SystemObjectEntry> infoPage =
			infoCollectionProvider.getCollectionInfoPage(collectionQuery);

		Assert.assertEquals(2, infoPage.getTotalCount());

		_assertSystemObjectEntry(
			organizationId1, "Comment 1", "Name 1", infoPage);

		collectionQuery.setPagination(Pagination.of(2, 1));

		_assertSystemObjectEntry(
			organizationId2, "Comment 2", "Name 2",
			infoCollectionProvider.getCollectionInfoPage(collectionQuery));
	}

	private void _assertSystemObjectEntry(
		long expectedClassPK, String expectedComment, String expectedName,
		InfoPage<SystemObjectEntry> infoPage) {

		List<SystemObjectEntry> systemObjectEntries =
			(List<SystemObjectEntry>)infoPage.getPageItems();

		Assert.assertEquals(
			systemObjectEntries.toString(), 1, systemObjectEntries.size());

		SystemObjectEntry systemObjectEntry = systemObjectEntries.get(0);

		Assert.assertEquals(expectedClassPK, systemObjectEntry.getClassPK());

		Map<String, Object> values = systemObjectEntry.getValues();

		Assert.assertEquals(expectedComment, values.get("comment"));
		Assert.assertEquals(expectedName, values.get("name"));
	}

	private ServiceContext _getServiceContext() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		return serviceContext;
	}

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}