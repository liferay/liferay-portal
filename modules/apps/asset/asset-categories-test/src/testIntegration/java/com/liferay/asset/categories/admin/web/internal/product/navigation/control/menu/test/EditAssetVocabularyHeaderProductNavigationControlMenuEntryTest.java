/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.product.navigation.control.menu.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;

import jakarta.portlet.Portlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Marco Galluzzi
 */
@RunWith(Arquillian.class)
public class EditAssetVocabularyHeaderProductNavigationControlMenuEntryTest {

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
	public void testIsShow() throws Exception {
		Assert.assertTrue(
			_productNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(
					_addAssetVocabulary(
						AssetVocabularyConstants.VISIBILITY_TYPE_EMPTY))));
	}

	@Test
	public void testIsShowWithDifferentPath() throws Exception {
		Assert.assertFalse(
			_productNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(
					_addAssetVocabulary(
						AssetVocabularyConstants.VISIBILITY_TYPE_EMPTY),
					"/view.jsp")));
	}

	@Test
	public void testIsShowWithoutAssetVocabulary() throws Exception {
		Assert.assertFalse(
			_productNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(null)));
	}

	private AssetVocabulary _addAssetVocabulary(int visibilityType)
		throws Exception {

		return _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, null, visibilityType,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			AssetVocabulary assetVocabulary)
		throws Exception {

		return _getMockHttpServletRequest(
			assetVocabulary, "/edit_asset_vocabulary.jsp");
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			AssetVocabulary assetVocabulary, String path)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		if (assetVocabulary != null) {
			mockLiferayPortletRenderRequest.addParameter(
				"vocabularyId",
				String.valueOf(assetVocabulary.getVocabularyId()));
		}

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);

		_portlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockHttpServletRequest;
	}

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.asset.categories.admin.web.internal.portlet.AssetCategoryAdminPortlet"
	)
	private Portlet _portlet;

	@Inject(
		filter = "component.name=com.liferay.asset.categories.admin.web.internal.product.navigation.control.menu.EditAssetVocabularyHeaderProductNavigationControlMenuEntry"
	)
	private ProductNavigationControlMenuEntry
		_productNavigationControlMenuEntry;

}