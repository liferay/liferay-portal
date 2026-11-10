/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.servlet.taglib;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectEntryServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
public class CommentsTagTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		String className = RandomTestUtil.randomString();

		Mockito.when(
			_group.getClassName()
		).thenReturn(
			className
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroup(Mockito.anyLong())
		).thenReturn(
			_group
		);

		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			className
		);

		_objectDefinitionLocalServiceUtilMockedStatic.when(
			() ->
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByExternalReferenceCode(
						Mockito.eq("L_DSR_ROOM"), Mockito.anyLong())
		).thenReturn(
			_objectDefinition
		);

		_objectEntryServiceUtilMockedStatic.when(
			() -> ObjectEntryServiceUtil.getModelResourcePermission(
				Mockito.anyLong())
		).thenReturn(
			_modelResourcePermission
		);

		_editorConfigurationFactoryUtilMockedStatic.when(
			() -> EditorConfigurationFactoryUtil.getEditorConfiguration(
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())
		).thenReturn(
			_editorConfiguration
		);
	}

	@After
	public void tearDown() {
		_editorConfigurationFactoryUtilMockedStatic.close();
		_featureFlagManagerUtilMockedStatic.close();
		_groupLocalServiceUtilMockedStatic.close();
		_objectDefinitionLocalServiceUtilMockedStatic.close();
		_objectEntryLocalServiceUtilMockedStatic.close();
		_objectEntryServiceUtilMockedStatic.close();
	}

	@Test
	public void testSetAttributes() {

		// Feature flag disabled

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		Assert.assertNotNull(
			mockHttpServletRequest.getAttribute(
				"liferay-digital-sales-room-web:comments:getCommentsURL"));
		Assert.assertNull(
			mockHttpServletRequest.getAttribute(
				"liferay-digital-sales-room-web:comments:getMentionUsersURL"));

		// Feature flag enabled

		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				Mockito.anyLong(), Mockito.eq("LPD-104086"))
		).thenReturn(
			true
		);

		Mockito.when(
			_group.getClassPK()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_themeDisplay.getPathMain()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_themeDisplay.getPortalURL()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		mockHttpServletRequest = _getMockHttpServletRequest();

		Assert.assertEquals(
			StringBundler.concat(
				_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/get_content_item_mention_users?className=",
				_objectDefinition.getClassName(), "&classPK=",
				_group.getClassPK()),
			mockHttpServletRequest.getAttribute(
				"liferay-digital-sales-room-web:comments:getMentionUsersURL"));
	}

	private MockHttpServletRequest _getMockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		CommentsTag commentsTag = new CommentsTag();

		commentsTag.setGroupId(RandomTestUtil.randomLong());

		commentsTag.setAttributes(mockHttpServletRequest);

		return mockHttpServletRequest;
	}

	private final EditorConfiguration _editorConfiguration = Mockito.mock(
		EditorConfiguration.class);
	private final MockedStatic<EditorConfigurationFactoryUtil>
		_editorConfigurationFactoryUtilMockedStatic = Mockito.mockStatic(
			EditorConfigurationFactoryUtil.class);
	private final MockedStatic<FeatureFlagManagerUtil>
		_featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
			FeatureFlagManagerUtil.class);
	private final Group _group = Mockito.mock(Group.class);
	private final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private final ModelResourcePermission<?> _modelResourcePermission =
		Mockito.mock(ModelResourcePermission.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final MockedStatic<ObjectDefinitionLocalServiceUtil>
		_objectDefinitionLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectDefinitionLocalServiceUtil.class);
	private final MockedStatic<ObjectEntryLocalServiceUtil>
		_objectEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectEntryLocalServiceUtil.class);
	private final MockedStatic<ObjectEntryServiceUtil>
		_objectEntryServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectEntryServiceUtil.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}