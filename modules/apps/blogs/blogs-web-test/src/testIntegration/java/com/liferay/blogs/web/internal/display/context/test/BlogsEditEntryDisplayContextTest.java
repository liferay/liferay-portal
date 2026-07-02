/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
@Sync
public class BlogsEditEntryDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(), null,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, null, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			new ServiceContext());
	}

	@Test
	public void testGetAvailableFriendlyURLAssetCategoriesJSONArrayWhenCreatingBlogEntry()
		throws Exception {

		JSONArray availableFriendlyURLAssetCategoriesJSONArray =
			ReflectionTestUtil.invoke(
				_getBlogsViewEntryContentDisplayContext(0, null, null),
				"getAvailableFriendlyURLAssetCategoriesJSONArray",
				new Class<?>[0]);

		Assert.assertEquals(
			0, availableFriendlyURLAssetCategoriesJSONArray.length());
	}

	@Test
	public void testGetAvailableFriendlyURLAssetCategoriesJSONArrayWhenParameterHasValues()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_addAssetCategories(5);

		serviceContext.setAssetCategoryIds(_assetCategoryIds);

		_addCurrentFriendlyURLAssetCategories(0, serviceContext);

		BlogsEntry entry = _addBlogEntry("test", serviceContext);

		long[] availableFriendlyURLAssetCategoriesFieldValues = new long[0];

		availableFriendlyURLAssetCategoriesFieldValues = ArrayUtil.append(
			availableFriendlyURLAssetCategoriesFieldValues,
			_assetCategoryIds[3]);
		availableFriendlyURLAssetCategoriesFieldValues = ArrayUtil.append(
			availableFriendlyURLAssetCategoriesFieldValues,
			_assetCategoryIds[2]);

		JSONArray availableFriendlyURLAssetCategoriesJSONArray =
			ReflectionTestUtil.invoke(
				_getBlogsViewEntryContentDisplayContext(
					entry.getEntryId(),
					StringUtil.merge(
						availableFriendlyURLAssetCategoriesFieldValues,
						StringPool.COMMA),
					null),
				"getAvailableFriendlyURLAssetCategoriesJSONArray",
				new Class<?>[0]);

		Assert.assertEquals(
			2, availableFriendlyURLAssetCategoriesJSONArray.length());

		JSONObject jsonObject1 =
			(JSONObject)availableFriendlyURLAssetCategoriesJSONArray.get(0);

		Assert.assertEquals(
			_assetCategoryIds[3], GetterUtil.getLong(jsonObject1.get("value")));

		JSONObject jsonObject2 =
			(JSONObject)availableFriendlyURLAssetCategoriesJSONArray.get(1);

		Assert.assertEquals(
			_assetCategoryIds[2], GetterUtil.getLong(jsonObject2.get("value")));
	}

	@Test
	public void testGetAvailableFriendlyURLAssetCategoriesJSONArrayWhenTwoCurrentFriendlyURLAssetCategories()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_addAssetCategories(5);

		serviceContext.setAssetCategoryIds(_assetCategoryIds);

		_addCurrentFriendlyURLAssetCategories(2, serviceContext);

		BlogsEntry entry = _addBlogEntry("test", serviceContext);

		JSONArray availableFriendlyURLAssetCategoriesJSONArray =
			ReflectionTestUtil.invoke(
				_getBlogsViewEntryContentDisplayContext(
					entry.getEntryId(), null, null),
				"getAvailableFriendlyURLAssetCategoriesJSONArray",
				new Class<?>[0]);

		Assert.assertEquals(
			3, availableFriendlyURLAssetCategoriesJSONArray.length());
	}

	@Test
	public void testGetAvailableFriendlyURLAssetCategoriesJSONArrayWhenZeroCurrentFriendlyURLAssetCategories()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_addAssetCategories(5);

		serviceContext.setAssetCategoryIds(_assetCategoryIds);

		_addCurrentFriendlyURLAssetCategories(0, serviceContext);

		BlogsEntry entry = _addBlogEntry("test", serviceContext);

		JSONArray availableFriendlyURLAssetCategoriesJSONArray =
			ReflectionTestUtil.invoke(
				_getBlogsViewEntryContentDisplayContext(
					entry.getEntryId(), null, null),
				"getAvailableFriendlyURLAssetCategoriesJSONArray",
				new Class<?>[0]);

		Assert.assertEquals(
			5, availableFriendlyURLAssetCategoriesJSONArray.length());
	}

	@Test
	public void testGetCurrentFriendlyURLAssetCategoriesJSONArrayWhenCreatingBlogEntry()
		throws Exception {

		JSONArray currentFriendlyURLAssetCategoriesJSONArray =
			ReflectionTestUtil.invoke(
				_getBlogsViewEntryContentDisplayContext(0, null, null),
				"getCurrentFriendlyURLAssetCategoriesJSONArray",
				new Class<?>[0]);

		Assert.assertEquals(
			0, currentFriendlyURLAssetCategoriesJSONArray.length());
	}

	@Test
	public void testGetCurrentFriendlyURLAssetCategoriesJSONArrayWhenParameterHasValues()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_addAssetCategories(5);

		serviceContext.setAssetCategoryIds(_assetCategoryIds);

		_addCurrentFriendlyURLAssetCategories(0, serviceContext);

		BlogsEntry entry = _addBlogEntry("test", serviceContext);

		long[] currentFriendlyURLAssetCategoriesFieldValues = new long[0];

		currentFriendlyURLAssetCategoriesFieldValues = ArrayUtil.append(
			currentFriendlyURLAssetCategoriesFieldValues, _assetCategoryIds[3]);
		currentFriendlyURLAssetCategoriesFieldValues = ArrayUtil.append(
			currentFriendlyURLAssetCategoriesFieldValues, _assetCategoryIds[2]);

		JSONArray currentFriendlyURLAssetCategoriesJSONArray =
			ReflectionTestUtil.invoke(
				_getBlogsViewEntryContentDisplayContext(
					entry.getEntryId(), null,
					StringUtil.merge(
						currentFriendlyURLAssetCategoriesFieldValues,
						StringPool.COMMA)),
				"getCurrentFriendlyURLAssetCategoriesJSONArray",
				new Class<?>[0]);

		Assert.assertEquals(
			2, currentFriendlyURLAssetCategoriesJSONArray.length());

		JSONObject jsonObject1 =
			(JSONObject)currentFriendlyURLAssetCategoriesJSONArray.get(0);

		Assert.assertEquals(
			_assetCategoryIds[3], GetterUtil.getLong(jsonObject1.get("value")));

		JSONObject jsonObject2 =
			(JSONObject)currentFriendlyURLAssetCategoriesJSONArray.get(1);

		Assert.assertEquals(
			_assetCategoryIds[2], GetterUtil.getLong(jsonObject2.get("value")));
	}

	@Test
	public void testGetCurrentFriendlyURLAssetCategoriesJSONArrayWhenReverseCurrentFriendlyURLAssetCategories()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_addAssetCategories(5);

		serviceContext.setAssetCategoryIds(_assetCategoryIds);

		_addCurrentFriendlyURLAssetCategories(3, serviceContext);

		_reverseCurrentFriendlyURLAssetCategories(serviceContext);

		BlogsEntry entry = _addBlogEntry("test", serviceContext);

		JSONArray currentFriendlyURLAssetCategoriesJSONArray =
			ReflectionTestUtil.invoke(
				_getBlogsViewEntryContentDisplayContext(
					entry.getEntryId(), null, null),
				"getCurrentFriendlyURLAssetCategoriesJSONArray",
				new Class<?>[0]);

		Assert.assertEquals(
			3, currentFriendlyURLAssetCategoriesJSONArray.length());

		JSONObject jsonObject1 =
			(JSONObject)currentFriendlyURLAssetCategoriesJSONArray.get(0);

		Assert.assertEquals(
			_assetCategoryIds[2], GetterUtil.getLong(jsonObject1.get("value")));

		JSONObject jsonObject2 =
			(JSONObject)currentFriendlyURLAssetCategoriesJSONArray.get(1);

		Assert.assertEquals(
			_assetCategoryIds[1], GetterUtil.getLong(jsonObject2.get("value")));

		JSONObject jsonObject3 =
			(JSONObject)currentFriendlyURLAssetCategoriesJSONArray.get(2);

		Assert.assertEquals(
			_assetCategoryIds[0], GetterUtil.getLong(jsonObject3.get("value")));
	}

	@Test
	public void testGetCurrentFriendlyURLAssetCategoriesJSONArrayWhenTwoCurrentFriendlyURLAssetCategories()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_addAssetCategories(5);

		serviceContext.setAssetCategoryIds(_assetCategoryIds);

		_addCurrentFriendlyURLAssetCategories(2, serviceContext);

		BlogsEntry entry = _addBlogEntry("test", serviceContext);

		JSONArray currentFriendlyURLAssetCategoriesJSONArray =
			ReflectionTestUtil.invoke(
				_getBlogsViewEntryContentDisplayContext(
					entry.getEntryId(), null, null),
				"getCurrentFriendlyURLAssetCategoriesJSONArray",
				new Class<?>[0]);

		Assert.assertEquals(
			2, currentFriendlyURLAssetCategoriesJSONArray.length());
	}

	@Test
	public void testGetCurrentFriendlyURLAssetCategoriesJSONArrayWhenZeroCurrentFriendlyURLAssetCategories()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_addAssetCategories(5);

		serviceContext.setAssetCategoryIds(_assetCategoryIds);

		_addCurrentFriendlyURLAssetCategories(0, serviceContext);

		BlogsEntry entry = _addBlogEntry("test", serviceContext);

		JSONArray currentFriendlyURLAssetCategoriesJSONArray =
			ReflectionTestUtil.invoke(
				_getBlogsViewEntryContentDisplayContext(
					entry.getEntryId(), null, null),
				"getCurrentFriendlyURLAssetCategoriesJSONArray",
				new Class<?>[0]);

		Assert.assertEquals(
			0, currentFriendlyURLAssetCategoriesJSONArray.length());
	}

	private void _addAssetCategories(int assetCategoriesCount)
		throws Exception {

		for (int i = 0; i < assetCategoriesCount; i++) {
			AssetCategory assetCategory =
				_assetCategoryLocalService.addCategory(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
					HashMapBuilder.put(
						LocaleUtil.US, "cat" + i
					).build(),
					null, _assetVocabulary.getVocabularyId(), false, null,
					new ServiceContext());

			_assetCategoryIds = ArrayUtil.append(
				_assetCategoryIds, assetCategory.getCategoryId());
		}
	}

	private BlogsEntry _addBlogEntry(
			String title, ServiceContext serviceContext)
		throws Exception {

		return _blogsEntryService.addEntry(
			title, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), 1, 1, 1990, 1, 1, true, false,
			new String[0], RandomTestUtil.randomString(), null, null,
			serviceContext);
	}

	private void _addCurrentFriendlyURLAssetCategories(
		int assetCategoriesCount, ServiceContext serviceContext) {

		long[] friendlyURLAssetCategoryIds = new long[0];

		for (int i = 0; i < assetCategoriesCount; i++) {
			friendlyURLAssetCategoryIds = ArrayUtil.append(
				friendlyURLAssetCategoryIds, _assetCategoryIds[i]);
		}

		serviceContext.setAttribute(
			"friendlyURLAssetCategoryIds", friendlyURLAssetCategoryIds);
	}

	private Object _getBlogsViewEntryContentDisplayContext(
			long entryId, String assetCategoryIdsParamValue,
			String friendlyURLAssetCategoryIdsParamValue)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(
				_getMockHttpServletRequest(
					entryId, assetCategoryIdsParamValue,
					friendlyURLAssetCategoryIdsParamValue));

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest.getAttribute(
			"com.liferay.blogs.web.internal.display.context." +
				"BlogsEditEntryDisplayContext");
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			long entryId, String assetCategoryIdsParamValue,
			String friendlyURLAssetCategoryIdsParamValue)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (entryId != 0) {
			mockHttpServletRequest.setParameter(
				"entryId", String.valueOf(entryId));
		}

		if (Validator.isNotNull(assetCategoryIdsParamValue)) {
			mockHttpServletRequest.setParameter(
				"assetCategoryIds", assetCategoryIdsParamValue);
		}

		if (Validator.isNotNull(friendlyURLAssetCategoryIdsParamValue)) {
			mockHttpServletRequest.setParameter(
				"friendlyURLAssetCategoryIds",
				friendlyURLAssetCategoryIdsParamValue);
		}

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_layout.getGroupId());
		themeDisplay.setSiteGroupId(_layout.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _reverseCurrentFriendlyURLAssetCategories(
		ServiceContext serviceContext) {

		long[] friendlyURLAssetCategoryIds = GetterUtil.getLongValues(
			serviceContext.getAttribute("friendlyURLAssetCategoryIds"));

		ArrayUtil.reverse(friendlyURLAssetCategoryIds);

		serviceContext.setAttribute(
			"friendlyURLAssetCategoryIds", friendlyURLAssetCategoryIds);
	}

	private long[] _assetCategoryIds = new long[0];

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private BlogsEntryService _blogsEntryService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject(
		filter = "component.name=com.liferay.blogs.web.internal.portlet.action.EditEntryMVCRenderCommand"
	)
	private MVCRenderCommand _mvcRenderCommand;

}