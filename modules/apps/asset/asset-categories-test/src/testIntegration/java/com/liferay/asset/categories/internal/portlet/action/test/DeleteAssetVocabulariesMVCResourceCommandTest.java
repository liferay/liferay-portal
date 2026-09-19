/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Diego hu
 */
@RunWith(Arquillian.class)
public class DeleteAssetVocabulariesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_assetVocabulary = AssetTestUtil.addVocabulary(_group.getGroupId());
	}

	@Test
	public void testServeResourceWithPermission() throws Exception {
		User user = UserTestUtil.addCompanyAdminUser(
			_companyLocalService.getCompany(_group.getCompanyId()));

		JSONObject jsonObject = _serveResource(
			_getMockLiferayResourceRequest(), user);

		Assert.assertTrue(jsonObject.getBoolean("success"));
	}

	@Test
	public void testServeResourceWithoutPermission() throws Exception {
		User user = UserTestUtil.addUser();

		JSONObject jsonObject = _serveResource(
			_getMockLiferayResourceRequest(), user);

		Assert.assertEquals(
			_language.get(
				LocaleUtil.US, "one-or-more-entries-could-not-be-deleted"),
			jsonObject.get("errorMessage"));
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest() {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"rowIds", String.valueOf(_assetVocabulary.getVocabularyId()));

		mockLiferayResourceRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		return mockLiferayResourceRequest;
	}

	private JSONObject _serveResource(
			MockLiferayResourceRequest mockLiferayResourceRequest, User user)
		throws Exception {

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			_mvcResourceCommand.serveResource(
				mockLiferayResourceRequest, mockLiferayResourceResponse);

			ByteArrayOutputStream byteArrayOutputStream =
				(ByteArrayOutputStream)
					mockLiferayResourceResponse.getPortletOutputStream();

			return JSONFactoryUtil.createJSONObject(
				byteArrayOutputStream.toString());
		}
		finally {
			UserLocalServiceUtil.deleteUser(user);
		}
	}

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyService _assetVocabularyService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject(
		filter = "mvc.command.name=/asset_categories_admin/delete_asset_vocabularies"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private Portal _portal;

}