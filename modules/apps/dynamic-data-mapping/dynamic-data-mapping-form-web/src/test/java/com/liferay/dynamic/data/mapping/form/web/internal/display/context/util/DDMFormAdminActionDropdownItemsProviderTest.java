/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context.util;

import com.liferay.dynamic.data.mapping.form.web.internal.display.context.helper.FormInstancePermissionCheckerHelper;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownGroupItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.hamcrest.collection.IsMapContaining;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Carolina Barbosa
 */
public class DDMFormAdminActionDropdownItemsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_setUpJSONFactoryUtil();
		_setUpLanguageUtil();
		_setUpPortletProviderUtil();
	}

	@AfterClass
	public static void tearDownClass() {
		_portletProviderUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		_setUpDDMFormAdminActionDropdownItemsProvider(true);
	}

	@Test
	public void testGetActionDropdownItems() {
		List<DropdownItem> dropdownItems =
			_ddmFormAdminActionDropdownItemsProvider.getActionDropdownItems();

		List<DropdownItem> dropdownGroupItems = _getDropdownGroupItems(
			dropdownItems, 0);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 3, dropdownGroupItems.size());

		_assertActionDropdownItemEdit(dropdownGroupItems.get(0));
		_assertActionDropdownItemViewEntries(dropdownGroupItems.get(1));
		_assertActionDropdownItemShare(dropdownGroupItems.get(2));

		dropdownGroupItems = _getDropdownGroupItems(dropdownItems, 1);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 2, dropdownGroupItems.size());

		_assertActionDropdownItemDuplicate(dropdownGroupItems.get(0));
		_assertActionDropdownItemExport(dropdownGroupItems.get(1));

		dropdownGroupItems = _getDropdownGroupItems(dropdownItems, 2);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 1, dropdownGroupItems.size());

		_assertActionDropdownItemPermissions(dropdownGroupItems.get(0));

		dropdownGroupItems = _getDropdownGroupItems(dropdownItems, 3);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 1, dropdownGroupItems.size());

		_assertActionDropdownItemDelete(dropdownGroupItems.get(0));
	}

	@Test
	public void testGetActionDropdownItemsNotShowingDelete() throws Exception {
		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowDeleteIcon(
				_ddmFormInstance)
		).thenReturn(
			false
		);

		List<DropdownItem> dropdownGroupItems = _getDropdownGroupItems(
			_ddmFormAdminActionDropdownItemsProvider.getActionDropdownItems(),
			3);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 0, dropdownGroupItems.size());
	}

	@Test
	public void testGetActionDropdownItemsNotShowingDuplicate()
		throws Exception {

		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowDuplicateIcon()
		).thenReturn(
			false
		);

		List<DropdownItem> dropdownGroupItems = _getDropdownGroupItems(
			_ddmFormAdminActionDropdownItemsProvider.getActionDropdownItems(),
			1);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 1, dropdownGroupItems.size());
	}

	@Test
	public void testGetActionDropdownItemsNotShowingEdit() throws Exception {
		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowEditIcon(
				_ddmFormInstance)
		).thenReturn(
			false
		);

		List<DropdownItem> dropdownGroupItems = _getDropdownGroupItems(
			_ddmFormAdminActionDropdownItemsProvider.getActionDropdownItems(),
			0);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 2, dropdownGroupItems.size());
	}

	@Test
	public void testGetActionDropdownItemsNotShowingExport() throws Exception {
		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowExportIcon(
				_ddmFormInstance)
		).thenReturn(
			false
		);

		List<DropdownItem> dropdownGroupItems = _getDropdownGroupItems(
			_ddmFormAdminActionDropdownItemsProvider.getActionDropdownItems(),
			1);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 1, dropdownGroupItems.size());
	}

	@Test
	public void testGetActionDropdownItemsNotShowingPermissions()
		throws Exception {

		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowPermissionsIcon(
				_ddmFormInstance)
		).thenReturn(
			false
		);

		List<DropdownItem> dropdownGroupItems = _getDropdownGroupItems(
			_ddmFormAdminActionDropdownItemsProvider.getActionDropdownItems(),
			2);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 0, dropdownGroupItems.size());
	}

	@Test
	public void testGetActionDropdownItemsNotShowingShare1() throws Exception {
		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowShareIcon(
				_ddmFormInstance)
		).thenReturn(
			false
		);

		List<DropdownItem> dropdownGroupItems = _getDropdownGroupItems(
			_ddmFormAdminActionDropdownItemsProvider.getActionDropdownItems(),
			0);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 2, dropdownGroupItems.size());
	}

	@Test
	public void testGetActionDropdownItemsNotShowingShare2() throws Exception {
		_setUpDDMFormAdminActionDropdownItemsProvider(false);

		List<DropdownItem> dropdownGroupItems = _getDropdownGroupItems(
			_ddmFormAdminActionDropdownItemsProvider.getActionDropdownItems(),
			0);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 2, dropdownGroupItems.size());
	}

	@Test
	public void testGetActionDropdownItemsNotShowingViewEntries()
		throws Exception {

		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowViewEntriesIcon(
				_ddmFormInstance)
		).thenReturn(
			false
		);

		List<DropdownItem> dropdownGroupItems = _getDropdownGroupItems(
			_ddmFormAdminActionDropdownItemsProvider.getActionDropdownItems(),
			0);

		Assert.assertEquals(
			dropdownGroupItems.toString(), 2, dropdownGroupItems.size());
	}

	private static void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private static void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArguments()[1]
		);

		languageUtil.setLanguage(language);
	}

	private static void _setUpPortletProviderUtil() throws Exception {
		_portletProviderUtilMockedStatic = Mockito.mockStatic(
			PortletProviderUtil.class);

		Mockito.when(
			PortletProviderUtil.getPortletURL(
				Mockito.any(HttpServletRequest.class),
				Mockito.any(String.class),
				Mockito.eq(PortletProvider.Action.VIEW))
		).thenReturn(
			new MockLiferayPortletURL()
		);
	}

	private void _assertActionDropdownItemDelete(DropdownItem dropdownItem) {
		Assert.assertThat(
			(Map<String, Object>)dropdownItem.get("data"),
			IsMapContaining.hasEntry("action", "delete"));
		Assert.assertThat(
			(Map<String, Object>)dropdownItem.get("data"),
			IsMapContaining.hasKey("deleteFormInstanceURL"));
		Assert.assertNull(dropdownItem.get("disabled"));
		Assert.assertEquals("times-circle", dropdownItem.get("icon"));
		Assert.assertEquals("delete", dropdownItem.get("label"));
	}

	private void _assertActionDropdownItemDuplicate(DropdownItem dropdownItem) {
		Assert.assertEquals(
			_INVALID_DDM_FORM_INSTANCE, dropdownItem.get("disabled"));
		Assert.assertTrue(Validator.isNotNull(dropdownItem.get("href")));
		Assert.assertEquals("copy", dropdownItem.get("icon"));
		Assert.assertEquals("make-a-copy", dropdownItem.get("label"));
	}

	private void _assertActionDropdownItemEdit(DropdownItem dropdownItem) {
		Assert.assertEquals(
			_INVALID_DDM_FORM_INSTANCE, dropdownItem.get("disabled"));
		Assert.assertTrue(Validator.isNotNull(dropdownItem.get("href")));
		Assert.assertEquals("pencil", dropdownItem.get("icon"));
		Assert.assertEquals("edit", dropdownItem.get("label"));
	}

	private void _assertActionDropdownItemExport(DropdownItem dropdownItem) {
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"action", "exportForm"
			).put(
				"exportFormURL", _EXPORT_FORM_URL
			).build(),
			dropdownItem.get("data"));
		Assert.assertEquals(
			_INVALID_DDM_FORM_INSTANCE, dropdownItem.get("disabled"));
		Assert.assertEquals("export-entries", dropdownItem.get("label"));
	}

	private void _assertActionDropdownItemPermissions(
		DropdownItem dropdownItem) {

		Assert.assertThat(
			(Map<String, Object>)dropdownItem.get("data"),
			IsMapContaining.hasEntry("action", "permissions"));
		Assert.assertThat(
			(Map<String, Object>)dropdownItem.get("data"),
			IsMapContaining.hasKey("permissionsFormInstanceURL"));
		Assert.assertThat(
			(Map<String, Object>)dropdownItem.get("data"),
			IsMapContaining.hasEntry("useDialog", "true"));
		Assert.assertNull(dropdownItem.get("disabled"));
		Assert.assertEquals("permissions", dropdownItem.get("label"));
	}

	private void _assertActionDropdownItemShare(DropdownItem dropdownItem) {
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"action", "shareForm"
			).put(
				"autocompleteUserURL", _AUTOCOMPLETE_USER_URL
			).put(
				"localizedName", JSONUtil.put("en_US", "Test US")
			).put(
				"portletNamespace", _PORTLET_NAMESPACE
			).put(
				"shareFormInstanceURL", _SHARE_FORM_INSTANCE_URL
			).put(
				"url", _PUBLISHED_FORM_URL
			).build(
			).toString(),
			String.valueOf(dropdownItem.get("data")));
		Assert.assertEquals(
			_INVALID_DDM_FORM_INSTANCE, dropdownItem.get("disabled"));
		Assert.assertEquals("share", dropdownItem.get("icon"));
		Assert.assertEquals("share", dropdownItem.get("label"));
	}

	private void _assertActionDropdownItemViewEntries(
		DropdownItem dropdownItem) {

		Assert.assertEquals(
			_INVALID_DDM_FORM_INSTANCE, dropdownItem.get("disabled"));
		Assert.assertTrue(Validator.isNotNull(dropdownItem.get("href")));
		Assert.assertEquals("list-ul", dropdownItem.get("icon"));
		Assert.assertEquals("view-entries", dropdownItem.get("label"));
	}

	private List<DropdownItem> _getDropdownGroupItems(
		List<DropdownItem> dropdownItems, int groupIndex) {

		DropdownGroupItem dropdownGroupItem =
			(DropdownGroupItem)dropdownItems.get(groupIndex);

		Assert.assertTrue((boolean)dropdownGroupItem.get("separator"));

		return (List<DropdownItem>)dropdownGroupItem.get("items");
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setURLCurrent(_CURRENT_URL);

		return themeDisplay;
	}

	private void _mockDDMFormInstance() {
		Mockito.when(
			_ddmFormInstance.getFormInstanceId()
		).thenReturn(
			_FORM_INSTANCE_ID
		);
	}

	private void _mockFormInstancePermissionCheckerHelper() throws Exception {
		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowDeleteIcon(
				_ddmFormInstance)
		).thenReturn(
			true
		);

		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowDuplicateIcon()
		).thenReturn(
			true
		);

		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowEditIcon(
				_ddmFormInstance)
		).thenReturn(
			true
		);

		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowExportIcon(
				_ddmFormInstance)
		).thenReturn(
			true
		);

		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowPermissionsIcon(
				_ddmFormInstance)
		).thenReturn(
			true
		);

		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowShareIcon(
				_ddmFormInstance)
		).thenReturn(
			true
		);

		Mockito.when(
			_formInstancePermissionCheckerHelper.isShowViewEntriesIcon(
				_ddmFormInstance)
		).thenReturn(
			true
		);
	}

	private HttpServletRequest _mockHttpServletRequest() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return httpServletRequest;
	}

	private void _setUpDDMFormAdminActionDropdownItemsProvider(
			boolean formPublished)
		throws Exception {

		_mockDDMFormInstance();
		_mockFormInstancePermissionCheckerHelper();

		_ddmFormAdminActionDropdownItemsProvider =
			new DDMFormAdminActionDropdownItemsProvider(
				_AUTOCOMPLETE_USER_URL, _ddmFormInstance, _EXPORT_FORM_URL,
				_formInstancePermissionCheckerHelper, formPublished,
				_mockHttpServletRequest(), _INVALID_DDM_FORM_INSTANCE,
				JSONUtil.put("en_US", "Test US"), _PUBLISHED_FORM_URL,
				new TestMockLiferayPortletRenderResponse(), _SCOPE_GROUP_ID,
				_SHARE_FORM_INSTANCE_URL);
	}

	private static final String _AUTOCOMPLETE_USER_URL =
		RandomTestUtil.randomString();

	private static final String _CURRENT_URL = RandomTestUtil.randomString();

	private static final String _EXPORT_FORM_URL =
		RandomTestUtil.randomString();

	private static final long _FORM_INSTANCE_ID = RandomTestUtil.randomLong();

	private static final boolean _INVALID_DDM_FORM_INSTANCE =
		RandomTestUtil.randomBoolean();

	private static final String _PORTLET_NAMESPACE =
		RandomTestUtil.randomString();

	private static final String _PUBLISHED_FORM_URL =
		RandomTestUtil.randomString();

	private static final long _SCOPE_GROUP_ID = RandomTestUtil.randomLong();

	private static final String _SHARE_FORM_INSTANCE_URL =
		RandomTestUtil.randomString();

	private static MockedStatic<PortletProviderUtil>
		_portletProviderUtilMockedStatic;

	private DDMFormAdminActionDropdownItemsProvider
		_ddmFormAdminActionDropdownItemsProvider;
	private final DDMFormInstance _ddmFormInstance = Mockito.mock(
		DDMFormInstance.class);
	private final FormInstancePermissionCheckerHelper
		_formInstancePermissionCheckerHelper = Mockito.mock(
			FormInstancePermissionCheckerHelper.class);

	private static class TestMockLiferayPortletRenderResponse
		extends MockLiferayPortletRenderResponse {

		@Override
		public String getNamespace() {
			return _PORTLET_NAMESPACE;
		}

	}

}