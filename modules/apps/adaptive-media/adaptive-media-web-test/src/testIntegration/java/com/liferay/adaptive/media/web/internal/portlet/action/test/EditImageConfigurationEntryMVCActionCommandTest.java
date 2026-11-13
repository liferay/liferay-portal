/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.portlet.action.test;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class EditImageConfigurationEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_uuids = _getUUIDs();
	}

	@After
	public void tearDown() throws Exception {
		for (String uuid : _getUUIDs()) {
			if (_uuids.contains(uuid)) {
				continue;
			}

			_amImageConfigurationHelper.forceDeleteAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), uuid);
		}
	}

	@Test
	@TestInfo("LPD-115336")
	public void testProcessActionWithAddHighResolution() throws Exception {
		JSONObject jsonObject = _processAction(
			"true", "Low resolution variant", "480", "320", "Low Resolution",
			"low-resolution");

		Assert.assertEquals(
			"Low Resolution and Low Resolution-2x were saved successfully.",
			jsonObject.getString("message"));
		Assert.assertTrue(jsonObject.getBoolean("success"));

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "low-resolution");

		Assert.assertEquals(
			"Low resolution variant",
			amImageConfigurationEntry.getDescription());
		Assert.assertEquals(
			"Low Resolution", amImageConfigurationEntry.getName());

		Map<String, String> properties =
			amImageConfigurationEntry.getProperties();

		Assert.assertEquals("480", properties.get("max-height"));
		Assert.assertEquals("320", properties.get("max-width"));

		AMImageConfigurationEntry highResolutionAMImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "low-resolution-2x");

		Assert.assertEquals(
			"2x Low resolution variant",
			highResolutionAMImageConfigurationEntry.getDescription());
		Assert.assertEquals(
			"Low Resolution-2x",
			highResolutionAMImageConfigurationEntry.getName());

		Map<String, String> highResolutionProperties =
			highResolutionAMImageConfigurationEntry.getProperties();

		Assert.assertEquals("960", highResolutionProperties.get("max-height"));
		Assert.assertEquals("640", highResolutionProperties.get("max-width"));
	}

	@Test
	@TestInfo("LPD-115336")
	public void testProcessActionWithDuplicateUUID() throws Exception {
		JSONObject jsonObject = _processAction(
			"false", "", "350", "450", "Image Variant With Custom ID",
			"custom-id");

		Assert.assertTrue(jsonObject.getBoolean("success"));

		jsonObject = _processAction(
			"false", "", "350", "450", RandomTestUtil.randomString(),
			"custom-id");

		Assert.assertEquals(
			"A configuration with this ID already exists. Enter a unique ID.",
			jsonObject.getString("message"));
		Assert.assertFalse(jsonObject.getBoolean("success"));

		AMImageConfigurationEntry amImageConfigurationEntry =
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(), "custom-id");

		Assert.assertEquals(
			"Image Variant With Custom ID",
			amImageConfigurationEntry.getName());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private Set<String> _getUUIDs() throws Exception {
		Set<String> uuids = new HashSet<>();

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				_amImageConfigurationHelper.getAMImageConfigurationEntries(
					TestPropsValues.getCompanyId(),
					amImageConfigurationEntry -> true)) {

			uuids.add(amImageConfigurationEntry.getUUID());
		}

		return uuids;
	}

	private JSONObject _processAction(
			String addHighResolution, String description, String maxHeight,
			String maxWidth, String name, String newUUID)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayPortletActionRequest.setParameter(
			"addHighResolution", addHighResolution);
		mockLiferayPortletActionRequest.setParameter(
			"description", description);
		mockLiferayPortletActionRequest.setParameter("maxHeight", maxHeight);
		mockLiferayPortletActionRequest.setParameter("maxWidth", maxWidth);
		mockLiferayPortletActionRequest.setParameter("name", name);
		mockLiferayPortletActionRequest.setParameter("newUuid", newUUID);

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		return JSONFactoryUtil.createJSONObject(
			mockHttpServletResponse.getContentAsString());
	}

	@Inject
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "mvc.command.name=/adaptive_media/edit_image_configuration_entry"
	)
	private MVCActionCommand _mvcActionCommand;

	private Set<String> _uuids;

}