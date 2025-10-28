/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.item.renderer.InfoItemRendererRegistry;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.servlet.ServletRequest;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Yuri Monteiro
 */
@RunWith(Arquillian.class)
public class ObjectEntryRowInfoItemRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectFieldName = StringUtil.randomId();

		_objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, false, true, false,
				true, false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), _objectFieldName)),
				Collections.emptyList());

		_objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				_objectDefinition.getUserId(),
				_objectDefinition.getObjectDefinitionId());
	}

	@Test
	public void testRender() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group group1 = GroupTestUtil.addGroup();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(group1.getCompanyId()));

		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(group1.getGroupId());
		themeDisplay.setSiteGroupId(group1.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setContextPath("/object-web");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		InfoItemRenderer<ObjectEntry> infoItemRenderer =
			(InfoItemRenderer<ObjectEntry>)
				_infoItemRendererRegistry.getInfoItemRenderer(
					StringBundler.concat(
						"com.liferay.object.web.internal.info.item.renderer.",
						"ObjectEntryRowInfoItemRenderer_",
						_objectDefinition.getCompanyId(), StringPool.UNDERLINE,
						_objectDefinition.getName()));

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			group1.getGroupId(), _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_objectFieldName, RandomTestUtil.randomString()
			).build());

		infoItemRenderer.render(
			objectEntry1, mockHttpServletRequest, mockHttpServletResponse);

		_assertObjectEntryValues(objectEntry1, mockHttpServletRequest);

		Group group2 = GroupTestUtil.addGroup();

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			group2.getGroupId(), _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_objectFieldName, RandomTestUtil.randomString()
			).build());

		infoItemRenderer.render(
			objectEntry2, mockHttpServletRequest, mockHttpServletResponse);

		_assertObjectEntryValues(objectEntry2, mockHttpServletRequest);
	}

	private void _assertObjectEntryValues(
		ObjectEntry objectEntry, ServletRequest servletRequest) {

		Map<String, Serializable> objectEntryValues = objectEntry.getValues();
		Map<String, Serializable> values =
			(Map<String, Serializable>)servletRequest.getAttribute(
				ObjectWebKeys.OBJECT_ENTRY_VALUES);

		Assert.assertEquals(
			objectEntryValues.get(_objectFieldName),
			values.get(_objectFieldName));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private InfoItemRendererRegistry _infoItemRendererRegistry;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private String _objectFieldName;

}