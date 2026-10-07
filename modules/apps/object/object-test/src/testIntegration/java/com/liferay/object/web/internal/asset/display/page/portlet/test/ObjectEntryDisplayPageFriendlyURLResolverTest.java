/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.asset.display.page.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURLComposite;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectEntryDisplayPageFriendlyURLResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			"Test",
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					_OBJECT_FIELD_NAME
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY,
			TestPropsValues.getUserId());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			_objectDefinition.getObjectDefinitionId(), _OBJECT_FIELD_NAME);

		_objectDefinition =
			_objectDefinitionLocalService.updateTitleObjectFieldId(
				_objectDefinition.getObjectDefinitionId(),
				objectField.getObjectFieldId());

		_friendlyURLResolver =
			FriendlyURLResolverRegistryUtil.
				getFriendlyURLResolverByDefaultURLSeparator("/c_test/");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_objectDefinitionLocalService.deleteObjectDefinition(_objectDefinition);
	}

	@Test
	@TestInfo("LPD-106977")
	public void testGetActualURL() throws Exception {
		_group = GroupTestUtil.addGroup();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate();

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		ObjectEntry objectEntry = _addObjectEntry();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(TestPropsValues.getUser());

		Assert.assertEquals(
			_portal.getLayoutActualURL(layout, Portal.PATH_MAIN),
			_getActualURL(mockHttpServletRequest, objectEntry));

		ListMergeable<String> titleListMergeable =
			(ListMergeable<String>)mockHttpServletRequest.getAttribute(
				WebKeys.PAGE_TITLE);

		Assert.assertEquals(
			objectEntry.getTitleValue(),
			titleListMergeable.mergeToString(StringPool.SPACE));

		_assertDisplayPageAttributes(true, mockHttpServletRequest);

		_user = UserTestUtil.addUser();

		mockHttpServletRequest = _getMockHttpServletRequest(_user);

		Assert.assertEquals(
			_portal.getLayoutActualURL(layout, Portal.PATH_MAIN),
			_getActualURL(mockHttpServletRequest, objectEntry));

		_assertDisplayPageAttributes(false, mockHttpServletRequest);
	}

	@Test
	public void testGetDefaultURLSeparator() {
		Assert.assertEquals(
			"/c_test/", _friendlyURLResolver.getDefaultURLSeparator());
	}

	@Test
	public void testGetKey() {
		Assert.assertEquals(
			StringUtil.replace(
				_objectDefinition.getClassName(), CharPool.POUND,
				CharPool.PERIOD),
			_friendlyURLResolver.getKey());
	}

	@Test
	@TestInfo("LPD-106977")
	public void testGetLayoutFriendlyURLComposite() throws Exception {
		_group = GroupTestUtil.addGroup();

		_addDisplayPageTemplate();

		ObjectEntry objectEntry = _addObjectEntry();

		LayoutFriendlyURLComposite layoutFriendlyURLComposite =
			_getLayoutFriendlyURLComposite(
				objectEntry, TestPropsValues.getUser());

		Assert.assertEquals(
			"/c_test/" + StringUtil.toLowerCase(objectEntry.getTitleValue()),
			layoutFriendlyURLComposite.getFriendlyURL());
		Assert.assertTrue(layoutFriendlyURLComposite.isRedirect());

		_user = UserTestUtil.addUser();

		layoutFriendlyURLComposite = _getLayoutFriendlyURLComposite(
			objectEntry, _user);

		Assert.assertEquals(
			_getFriendlyURL(objectEntry),
			layoutFriendlyURLComposite.getFriendlyURL());
		Assert.assertFalse(layoutFriendlyURLComposite.isRedirect());
	}

	@Test
	public void testGetURLSeparator() {
		Assert.assertEquals("/c_test/", _friendlyURLResolver.getURLSeparator());
	}

	@Test
	public void testIsURLSeparatorConfigurable() {
		Assert.assertFalse(_friendlyURLResolver.isURLSeparatorConfigurable());
	}

	private LayoutPageTemplateEntry _addDisplayPageTemplate() throws Exception {
		return DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(_objectDefinition.getClassName()), null,
			true, WorkflowConstants.STATUS_APPROVED);
	}

	private ObjectEntry _addObjectEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).build(),
			serviceContext);
	}

	private void _assertDisplayPageAttributes(
		boolean expected, MockHttpServletRequest mockHttpServletRequest) {

		for (String name : _DISPLAY_PAGE_ATTRIBUTE_NAMES) {
			Assert.assertEquals(
				name, expected,
				mockHttpServletRequest.getAttribute(name) != null);
		}
	}

	private String _getActualURL(
			MockHttpServletRequest mockHttpServletRequest,
			ObjectEntry objectEntry)
		throws Exception {

		return _friendlyURLResolver.getActualURL(
			TestPropsValues.getCompanyId(), _group.getGroupId(), false,
			Portal.PATH_MAIN, _getFriendlyURL(objectEntry),
			Collections.emptyMap(),
			HashMapBuilder.<String, Object>put(
				"request", mockHttpServletRequest
			).build());
	}

	private String _getFriendlyURL(ObjectEntry objectEntry) {
		return "/c_test/" + objectEntry.getObjectEntryId();
	}

	private LayoutFriendlyURLComposite _getLayoutFriendlyURLComposite(
			ObjectEntry objectEntry, User user)
		throws Exception {

		return _friendlyURLResolver.getLayoutFriendlyURLComposite(
			TestPropsValues.getCompanyId(), _group.getGroupId(), false,
			_getFriendlyURL(objectEntry), Collections.emptyMap(),
			HashMapBuilder.<String, Object>put(
				"request", _getMockHttpServletRequest(user)
			).build());
	}

	private MockHttpServletRequest _getMockHttpServletRequest(User user) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.USER, user);

		return mockHttpServletRequest;
	}

	private static final String[] _DISPLAY_PAGE_ATTRIBUTE_NAMES = {
		LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
		LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_PROVIDER,
		WebKeys.LAYOUT_ASSET_ENTRY, WebKeys.PAGE_DESCRIPTION,
		WebKeys.PAGE_KEYWORDS, WebKeys.PAGE_TITLE
	};

	private static final String _OBJECT_FIELD_NAME = StringUtil.randomId();

	private static FriendlyURLResolver _friendlyURLResolver;
	private static ObjectDefinition _objectDefinition;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private static ObjectFieldLocalService _objectFieldLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	@DeleteAfterTestRun
	private User _user;

}