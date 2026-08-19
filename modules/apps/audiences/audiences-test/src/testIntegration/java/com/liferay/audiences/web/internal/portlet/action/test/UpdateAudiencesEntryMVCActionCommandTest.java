/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
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

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class UpdateAudiencesEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-102224")
	public void testProcessAction() throws Exception {
		_testProcessActionWithExistingAudiencesEntry();
		_testProcessActionWithRegisteredCustomAttribute();
		_testProcessActionWithUnregisteredCustomAttribute();
	}

	private void _assertInvalidCustomAttributeError(JSONObject jsonObject) {
		JSONObject errorJSONObject = jsonObject.getJSONObject("error");

		Assert.assertEquals(
			_language.get(
				LocaleUtil.US, "you-have-entered-an-invalid-custom-attribute"),
			errorJSONObject.getString("other"));
	}

	private String _getCriteriaJSON(JSONObject ruleJSONObject) {
		return JSONUtil.put(
			"conjunction", "AND"
		).put(
			"rules", JSONUtil.putAll(ruleJSONObject)
		).toString();
	}

	private JSONObject _getRuleJSONObject(String attribute) {
		return JSONUtil.put(
			"attribute", attribute
		).put(
			"operator", "eq"
		).put(
			"value", true
		);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private JSONObject _processAction(
			long audiencesEntryId, String externalReferenceCode, String json,
			String name)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayPortletActionRequest.setParameter(
			"audiencesEntryId", String.valueOf(audiencesEntryId));
		mockLiferayPortletActionRequest.setParameter(
			"externalReferenceCode", externalReferenceCode);
		mockLiferayPortletActionRequest.setParameter("json", json);
		mockLiferayPortletActionRequest.setParameter("name", name);

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		return _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());
	}

	private void _testProcessActionWithExistingAudiencesEntry()
		throws Exception {

		String json = _getCriteriaJSON(
			_getRuleJSONObject(_REGISTERED_CUSTOM_ATTRIBUTE));

		AudiencesEntry audiencesEntry =
			_audiencesEntryLocalService.addAudiencesEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				json, RandomTestUtil.randomString());

		JSONObject jsonObject = _processAction(
			audiencesEntry.getAudiencesEntryId(),
			audiencesEntry.getExternalReferenceCode(),
			_getCriteriaJSON(
				_getRuleJSONObject(_UNREGISTERED_CUSTOM_ATTRIBUTE)),
			audiencesEntry.getName());

		_assertInvalidCustomAttributeError(jsonObject);

		audiencesEntry = _audiencesEntryLocalService.getAudiencesEntry(
			audiencesEntry.getAudiencesEntryId());

		Assert.assertEquals(json, audiencesEntry.getJSON());
	}

	private void _testProcessActionWithRegisteredCustomAttribute()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();
		String json = _getCriteriaJSON(
			_getRuleJSONObject(_REGISTERED_CUSTOM_ATTRIBUTE));
		String name = RandomTestUtil.randomString();

		JSONObject jsonObject = _processAction(
			0, externalReferenceCode, json, name);

		Assert.assertEquals(jsonObject.toString(), 0, jsonObject.length());

		AudiencesEntry audiencesEntry =
			_audiencesEntryLocalService.
				getAudiencesEntryByExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId());

		Assert.assertEquals(json, audiencesEntry.getJSON());
		Assert.assertEquals(name, audiencesEntry.getName());
	}

	private void _testProcessActionWithUnregisteredCustomAttribute()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		JSONObject jsonObject = _processAction(
			0, externalReferenceCode,
			_getCriteriaJSON(
				_getRuleJSONObject(_UNREGISTERED_CUSTOM_ATTRIBUTE)),
			RandomTestUtil.randomString());

		_assertInvalidCustomAttributeError(jsonObject);

		Assert.assertNull(
			_audiencesEntryLocalService.
				fetchAudiencesEntryByExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId()));

		jsonObject = _processAction(
			0, RandomTestUtil.randomString(),
			_getCriteriaJSON(
				JSONUtil.put(
					"conjunction", "OR"
				).put(
					"rules",
					JSONUtil.putAll(
						JSONUtil.put(
							"conjunction", "AND"
						).put(
							"rules",
							JSONUtil.putAll(
								_getRuleJSONObject(
									_UNREGISTERED_CUSTOM_ATTRIBUTE))
						))
				)),
			RandomTestUtil.randomString());

		_assertInvalidCustomAttributeError(jsonObject);
	}

	private static final String _REGISTERED_CUSTOM_ATTRIBUTE =
		"custom:/o/frontend-js-audiences-web/__liferay__" +
			"/custom-attributes.js#signed_in";

	private static final String _UNREGISTERED_CUSTOM_ATTRIBUTE =
		"custom:data:text/javascript,export function run(){return true}#run";

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	@Inject(filter = "mvc.command.name=/audiences/update_audiences_entry")
	private MVCActionCommand _mvcActionCommand;

}