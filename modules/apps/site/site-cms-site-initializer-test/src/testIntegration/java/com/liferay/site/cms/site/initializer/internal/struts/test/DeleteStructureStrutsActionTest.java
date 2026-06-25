/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class DeleteStructureStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-77022")
	public void testExecute() throws Exception {
		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			objectDefinition1.getObjectDefinitionId(),
			objectDefinition2.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringUtil.randomId(), false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectDefinition objectDefinition3 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			objectDefinition2.getObjectDefinitionId(),
			objectDefinition3.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringUtil.randomId(), false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"objectDefinitionId",
			String.valueOf(objectDefinition1.getObjectDefinitionId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_deleteStructureStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(0, jsonObject.length());

		Assert.assertNull(
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinition1.getObjectDefinitionId()));
		Assert.assertNull(
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinition2.getObjectDefinitionId()));
		Assert.assertNull(
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinition3.getObjectDefinitionId()));
	}

	@Test
	@TestInfo("LPD-77022")
	public void testExecuteWithObjectRelationships() throws Exception {
		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			objectDefinition1.getObjectDefinitionId(),
			objectDefinition2.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringUtil.randomId(), false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectDefinition objectDefinition3 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			objectDefinition2.getObjectDefinitionId(),
			objectDefinition3.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringUtil.randomId(), false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"objectDefinitionId",
			String.valueOf(objectDefinition1.getObjectDefinitionId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_deleteStructureStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(jsonObject.toString(), 0, jsonObject.length());

		Assert.assertNull(
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinition1.getObjectDefinitionId()));
		Assert.assertNull(
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinition2.getObjectDefinitionId()));
		Assert.assertNull(
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinition3.getObjectDefinitionId()));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "path=/cms/delete-structure")
	private StrutsAction _deleteStructureStrutsAction;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}