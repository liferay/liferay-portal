/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer.categorization.inputs.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class CategoriesInputFragmentRendererTest
	extends BaseInputFragmentRendererTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			group.getGroupId());

		_assetCategory = AssetTestUtil.addCategory(
			group.getGroupId(), assetVocabulary.getVocabularyId());
	}

	@Test
	public void testGetGroupIdsForCompanyScopeObject() throws Exception {
		objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, "First Name",
					"firstName")),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		fragmentEntryLink = addFragmentEntryLink(objectDefinition);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(objectDefinition);

		FragmentRenderer fragmentRenderer = getFragmentRenderer();

		fragmentRenderer.render(
			new DefaultFragmentRendererContext(fragmentEntryLink),
			mockHttpServletRequest, new MockHttpServletResponse());

		Map<String, Object> data =
			(Map<String, Object>)mockHttpServletRequest.getAttribute(
				"liferay-asset:asset-categories-selector:data");

		List<Long> groupIds = (List<Long>)data.get("groupIds");

		Assert.assertEquals(groupIds.toString(), 1, groupIds.size());
		Assert.assertFalse(groupIds.contains(group.getGroupId()));

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group globalGroup = company.getGroup();

		Assert.assertTrue(groupIds.contains(globalGroup.getGroupId()));
	}

	@Test
	public void testGetGroupIdsForSiteScopeObject() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(objectDefinition);

		FragmentRenderer fragmentRenderer = getFragmentRenderer();

		fragmentRenderer.render(
			new DefaultFragmentRendererContext(fragmentEntryLink),
			mockHttpServletRequest, new MockHttpServletResponse());

		Map<String, Object> data =
			(Map<String, Object>)mockHttpServletRequest.getAttribute(
				"liferay-asset:asset-categories-selector:data");

		List<Long> groupIds = (List<Long>)data.get("groupIds");

		Assert.assertEquals(groupIds.toString(), 2, groupIds.size());
		Assert.assertTrue(groupIds.contains(group.getGroupId()));

		Company company = companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group globalGroup = company.getGroup();

		Assert.assertTrue(groupIds.contains(globalGroup.getGroupId()));
	}

	@Override
	protected ObjectEntry addObjectEntry() throws Exception {
		long groupId = 0;

		if (Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_SITE)) {

			groupId = group.getGroupId();
		}

		return _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), groupId,
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId(),
				new long[] {_assetCategory.getCategoryId()}));
	}

	@Override
	protected void assertRender(
		int expectedResult, HttpServletRequest httpServletRequest) {

		Map<String, Object> data =
			(Map<String, Object>)httpServletRequest.getAttribute(
				"liferay-asset:asset-categories-selector:data");

		List<Map<String, Object>> vocabularies =
			(List<Map<String, Object>>)data.get("vocabularies");

		Map<String, Object> vocabulary = vocabularies.get(0);

		List<Map<String, Object>> selectedItems =
			(List<Map<String, Object>>)vocabulary.get("selectedItems");

		if (expectedResult <= 0) {
			Assert.assertNull(selectedItems);

			return;
		}

		Assert.assertEquals(
			selectedItems.toString(), expectedResult, selectedItems.size());

		Map<String, Object> selectedItem = selectedItems.get(0);

		Assert.assertEquals(
			String.valueOf(_assetCategory.getCategoryId()),
			selectedItem.get("value"));
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _categoriesInputFragmentRenderer;
	}

	@Override
	protected String getRenderKey() {
		return "com.liferay.fragment.renderer.categorization.inputs.internal." +
			"CategoriesInputFragmentRenderer";
	}

	private AssetCategory _assetCategory;

	@Inject(
		filter = "component.name=com.liferay.fragment.renderer.categorization.inputs.internal.CategoriesInputFragmentRenderer",
		type = FragmentRenderer.class
	)
	private FragmentRenderer _categoriesInputFragmentRenderer;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}