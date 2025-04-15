/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.admin.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.AssetTagNameException;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class AssetTagsAdminPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDeleteTag() throws Exception {
		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			"tagId", String.valueOf(assetTag.getTagId()));

		_invoke("deleteTag", mockLiferayPortletActionRequest);

		Assert.assertNull(
			_assetTagLocalService.fetchAssetTag(assetTag.getTagId()));
	}

	@Test
	public void testEditTagAddTag() throws Exception {
		String name = RandomTestUtil.randomString();

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter("name", name);

		_invoke("editTag", mockLiferayPortletActionRequest);

		Assert.assertNotNull(
			_assetTagLocalService.fetchTag(_group.getGroupId(), name));
	}

	@Test
	public void testEditTagUpdateTag() throws Exception {
		String name = RandomTestUtil.randomString();

		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), name,
			ServiceContextTestUtil.getServiceContext());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		String newName = RandomTestUtil.randomString();

		mockLiferayPortletActionRequest.setParameter("name", newName);

		mockLiferayPortletActionRequest.setParameter(
			"tagId", String.valueOf(assetTag.getTagId()));

		_invoke("editTag", mockLiferayPortletActionRequest);

		assetTag = _assetTagLocalService.getAssetTag(assetTag.getTagId());

		Assert.assertEquals(newName, assetTag.getName());
	}

	@Test(expected = AssetTagNameException.class)
	public void testEditTagWithEmptyName() throws Exception {
		_invoke("editTag", _getMockLiferayPortletActionRequest());
	}

	@Test
	public void testMergeTag() throws Exception {
		AssetTag mergeAssetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
		AssetTag targetAssetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.setParameter(
			"mergeTagNames", new String[] {mergeAssetTag.getName()});
		mockLiferayPortletActionRequest.setParameter(
			"targetTagName", targetAssetTag.getName());

		_invoke("mergeTag", mockLiferayPortletActionRequest);

		Assert.assertNull(
			_assetTagLocalService.fetchAssetTag(mergeAssetTag.getTagId()));
		Assert.assertNotNull(
			_assetTagLocalService.fetchAssetTag(targetAssetTag.getTagId()));
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));

		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _invoke(
		String deleteTag,
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest) {

		ReflectionTestUtil.invoke(
			_portlet, deleteTag,
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest, new MockActionResponse());
	}

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.asset.tags.admin.web.internal.portlet.AssetTagsAdminPortlet"
	)
	private Portlet _portlet;

}