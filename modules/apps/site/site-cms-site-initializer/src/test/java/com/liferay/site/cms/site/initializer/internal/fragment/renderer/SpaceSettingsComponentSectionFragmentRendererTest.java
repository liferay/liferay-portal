/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.model.DepotEntry;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jan Brychta
 */
public class SpaceSettingsComponentSectionFragmentRendererTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_language);

		ReflectionTestUtil.setFieldValue(
			_spaceSettingsComponentSectionFragmentRenderer,
			"_depotEntryModelResourcePermission",
			_depotEntryModelResourcePermission);
		ReflectionTestUtil.setFieldValue(
			_spaceSettingsComponentSectionFragmentRenderer,
			"_groupLocalService", _groupLocalService);
		ReflectionTestUtil.setFieldValue(
			_spaceSettingsComponentSectionFragmentRenderer, "_jsonFactory",
			_jsonFactory);
	}

	@Test(expected = PrincipalException.class)
	public void testGetPropsWhenUserDoesNotHavePermission() throws Exception {
		Mockito.doThrow(
			new PrincipalException()
		).when(
			_depotEntryModelResourcePermission
		).check(
			Mockito.any(), Mockito.eq(_DEPOT_ENTRY_ID),
			Mockito.eq(ActionKeys.UPDATE)
		);

		_getProps();
	}

	@Test
	public void testGetPropsWhenUserHasPermission() throws Exception {
		Mockito.verify(
			_depotEntryModelResourcePermission
		).check(
			Mockito.any(), Mockito.eq(_DEPOT_ENTRY_ID),
			Mockito.eq(ActionKeys.UPDATE)
		);

		Mockito.when(
			_group.getExternalReferenceCode()
		).thenReturn(
			_EXTERNAL_REFERENCE_CODE
		);

		Mockito.when(
			_groupLocalService.getGroup(_GROUP_ID)
		).thenReturn(
			_group
		);

		Mockito.when(
			_jsonFactory.createJSONArray()
		).thenReturn(
			Mockito.mock(JSONArray.class)
		);

		Mockito.when(
			_language.getAvailableLocales(Mockito.anyLong())
		).thenReturn(
			Collections.emptySet()
		);

		Map<String, Object> props = _getProps();

		Assert.assertEquals(
			_EXTERNAL_REFERENCE_CODE, props.get("externalReferenceCode"));
		Assert.assertEquals(_GROUP_ID, props.get("groupId"));
	}

	private Map<String, Object> _getProps() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		DepotEntry infoItemDepotEntry = Mockito.mock(DepotEntry.class);

		Mockito.when(
			infoItemDepotEntry.getDepotEntryId()
		).thenReturn(
			_DEPOT_ENTRY_ID
		);

		Mockito.when(
			infoItemDepotEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		mockHttpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, infoItemDepotEntry);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getPermissionChecker()
		).thenReturn(
			Mockito.mock(PermissionChecker.class)
		);

		Mockito.when(
			themeDisplay.getScopeGroupId()
		).thenReturn(
			_GROUP_ID
		);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return _spaceSettingsComponentSectionFragmentRenderer.getProps(
			null, mockHttpServletRequest);
	}

	private static final long _DEPOT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final String _EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	@Mock
	private ModelResourcePermission<DepotEntry>
		_depotEntryModelResourcePermission;

	@Mock
	private Group _group;

	@Mock
	private GroupLocalService _groupLocalService;

	@Mock
	private JSONFactory _jsonFactory;

	@Mock
	private Language _language;

	private final SpaceSettingsComponentSectionFragmentRenderer
		_spaceSettingsComponentSectionFragmentRenderer =
			new SpaceSettingsComponentSectionFragmentRenderer();

}