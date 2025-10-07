/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.helper.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.info.pagination.InfoPage;
import com.liferay.layout.helper.LayoutWarningMessageHelper;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.CollectionPaginationUtil;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class LayoutWarningMessageHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_collectionStyledLayoutStructureItem =
			_addCollectionStyledLayoutStructureItem(_group);
	}

	@Test
	public void testGetWarningMessage() throws Exception {
		_testGetWarningMessage(
			true, PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA - 1);
		_testGetWarningMessage(
			false, PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA + 1);
	}

	private CollectionStyledLayoutStructureItem
			_addCollectionStyledLayoutStructureItem(Group group)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					group.getGroupId(), layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem =
				(CollectionStyledLayoutStructureItem)
					layoutStructure.addCollectionStyledLayoutStructureItem(
						layoutStructure.getMainItemId(), 0);

		collectionStyledLayoutStructureItem.setCollectionJSONObject(
			JSONUtil.put(
				"itemType", RandomTestUtil.randomString()
			).put(
				"key", TestInfoCollectionProvider.class.getName()
			).put(
				"title", RandomTestUtil.randomString()
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			));

		return collectionStyledLayoutStructureItem;
	}

	private void _assertBlankCollectionWarningMessage(
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem,
			String mode)
		throws Exception {

		JSONObject collectionWarningMessageJSONObject =
			_getCollectionWarningMessageJSONObject(
				collectionStyledLayoutStructureItem, mode);

		Assert.assertFalse(
			collectionWarningMessageJSONObject.has("description"));
	}

	private void _assertBlankCollectionWarningMessages(
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem,
			List<String> modes, List<String> paginationTypes)
		throws Exception {

		for (String mode : modes) {
			for (String paginationType : paginationTypes) {
				collectionStyledLayoutStructureItem.setPaginationType(
					paginationType);

				_assertBlankCollectionWarningMessage(
					collectionStyledLayoutStructureItem, mode);
			}
		}
	}

	private void _assertCollectionWarningMessages(
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem)
		throws Exception {

		_assertBlankCollectionWarningMessages(
			collectionStyledLayoutStructureItem,
			Arrays.asList(Constants.EDIT, Constants.VIEW),
			Arrays.asList(
				CollectionPaginationUtil.PAGINATION_TYPE_NUMERIC,
				CollectionPaginationUtil.PAGINATION_TYPE_REGULAR,
				CollectionPaginationUtil.PAGINATION_TYPE_SIMPLE));

		collectionStyledLayoutStructureItem.setPaginationType(
			CollectionPaginationUtil.PAGINATION_TYPE_NONE);

		_assertDefaultEditCollectionWarningMessage(
			collectionStyledLayoutStructureItem);
		_assertDefaultViewCollectionWarningMessage(
			collectionStyledLayoutStructureItem);
	}

	private void _assertDefaultEditCollectionWarningMessage(
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem)
		throws Exception {

		JSONObject actualCollectionWarningMessageJSONObject =
			_getCollectionWarningMessageJSONObject(
				collectionStyledLayoutStructureItem, Constants.EDIT);

		Assert.assertEquals(
			_language.format(
				LocaleUtil.getDefault(),
				"this-setting-can-affect-page-performance-severely-if-the-" +
					"number-of-collection-items-is-above-x.-we-strongly-" +
						"recommend-using-pagination-instead",
				PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA),
			actualCollectionWarningMessageJSONObject.getString("description"));
	}

	private void _assertDefaultViewCollectionWarningMessage(
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem)
		throws Exception {

		JSONObject actualCollectionWarningMessageJSONObject =
			_getCollectionWarningMessageJSONObject(
				collectionStyledLayoutStructureItem, Constants.VIEW);

		Assert.assertEquals(
			_language.format(
				LocaleUtil.getDefault(),
				"this-setting-can-affect-page-performance-severely-if-the-" +
					"number-of-collection-items-is-above-x.-we-strongly-" +
						"recommend-using-pagination-instead",
				PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA),
			actualCollectionWarningMessageJSONObject.getString("description"));
	}

	private JSONObject _getCollectionWarningMessageJSONObject(
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem,
			String mode)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("p_l_mode", mode);

		return _layoutWarningMessageHelper.
			getCollectionWarningMessageJSONObject(
				collectionStyledLayoutStructureItem, mockHttpServletRequest);
	}

	private ServiceRegistration<InfoCollectionProvider<?>>
		_getServiceRegistration(int totalCount) {

		Bundle bundle = FrameworkUtil.getBundle(
			LayoutWarningMessageHelperTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			(Class<InfoCollectionProvider<?>>)
				(Class<?>)InfoCollectionProvider.class,
			new TestInfoCollectionProvider(totalCount), null);
	}

	private void _testGetWarningMessage(boolean displayAllItems, int totalCount)
		throws Exception {

		ServiceRegistration<InfoCollectionProvider<?>> serviceRegistration =
			_getServiceRegistration(totalCount);

		_collectionStyledLayoutStructureItem.setDisplayAllItems(
			displayAllItems);
		_collectionStyledLayoutStructureItem.setNumberOfItems(totalCount);

		_assertCollectionWarningMessages(_collectionStyledLayoutStructureItem);

		serviceRegistration.unregister();
	}

	private CollectionStyledLayoutStructureItem
		_collectionStyledLayoutStructureItem;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutWarningMessageHelper _layoutWarningMessageHelper;

	private static class TestInfoCollectionProvider
		implements InfoCollectionProvider<String> {

		public TestInfoCollectionProvider(int totalCount) {
			_totalCount = totalCount;
		}

		@Override
		public InfoPage<String> getCollectionInfoPage(
			CollectionQuery collectionQuery) {

			return InfoPage.of(Collections.emptyList(), null, _totalCount);
		}

		@Override
		public String getLabel(Locale locale) {
			return StringPool.BLANK;
		}

		private final int _totalCount;

	}

}