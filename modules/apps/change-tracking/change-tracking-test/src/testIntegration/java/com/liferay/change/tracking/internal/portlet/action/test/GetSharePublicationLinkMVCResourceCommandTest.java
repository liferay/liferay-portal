/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class GetSharePublicationLinkMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final TestRule testRule = new LiferayIntegrationTestRule();

	@Test
	public void testServeResource() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			_getMockLiferayResourceRequest(
				ctCollection.getCtCollectionId(), true,
				TestPropsValues.getUser()),
			mockLiferayResourceResponse);

		JSONObject jsonObject = _getJSONObject(mockLiferayResourceResponse);

		Assert.assertTrue(jsonObject.getBoolean("shareable"));
		Assert.assertTrue(jsonObject.has("sharePublicationLink"));

		ctCollection = _ctCollectionLocalService.getCTCollection(
			ctCollection.getCtCollectionId());

		Assert.assertTrue(ctCollection.isShareable());
	}

	@Test
	public void testServeResourceWithoutCSRFToken() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(
				ctCollection.getCtCollectionId(), true,
				TestPropsValues.getUser());

		mockLiferayResourceRequest.setParameter("p_auth", StringPool.BLANK);

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		JSONObject jsonObject = _getJSONObject(mockLiferayResourceResponse);

		Assert.assertTrue(jsonObject.has("errorMessage"));
		Assert.assertFalse(jsonObject.has("sharePublicationLink"));

		ctCollection = _ctCollectionLocalService.getCTCollection(
			ctCollection.getCtCollectionId());

		Assert.assertFalse(ctCollection.isShareable());
	}

	@Test
	public void testServeResourceWithoutInviteUsersPermission()
		throws Exception {

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		User user = UserTestUtil.addUser();

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			_getMockLiferayResourceRequest(
				ctCollection.getCtCollectionId(), true, user),
			mockLiferayResourceResponse);

		JSONObject jsonObject = _getJSONObject(mockLiferayResourceResponse);

		Assert.assertTrue(jsonObject.has("errorMessage"));
		Assert.assertFalse(jsonObject.has("sharePublicationLink"));

		ctCollection = _ctCollectionLocalService.getCTCollection(
			ctCollection.getCtCollectionId());

		Assert.assertFalse(ctCollection.isShareable());
	}

	private JSONObject _getJSONObject(
			MockLiferayResourceResponse mockLiferayResourceResponse)
		throws Exception {

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return JSONFactoryUtil.createJSONObject(
			new String(byteArrayOutputStream.toByteArray()));
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			long ctCollectionId, boolean shareable, User user)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, null);
		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(user));
		mockLiferayResourceRequest.setParameter(
			"ctCollectionId", String.valueOf(ctCollectionId));
		mockLiferayResourceRequest.setParameter(
			"p_auth",
			AuthTokenUtil.getToken(
				mockLiferayResourceRequest.getHttpServletRequest()));
		mockLiferayResourceRequest.setParameter(
			"shareable", String.valueOf(shareable));

		return mockLiferayResourceRequest;
	}

	private ThemeDisplay _getThemeDisplay(User user) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject(
		filter = "mvc.command.name=/change_tracking/get_share_publication_link"
	)
	private MVCResourceCommand _mvcResourceCommand;

}