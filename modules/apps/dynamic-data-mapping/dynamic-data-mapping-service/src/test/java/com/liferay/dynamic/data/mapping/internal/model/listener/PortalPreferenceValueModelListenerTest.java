/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.model.listener;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Pedro Leite
 */
public class PortalPreferenceValueModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_portalPreferenceValueModelListener =
			new PortalPreferenceValueModelListener();

		ReflectionTestUtil.setFieldValue(
			_portalPreferenceValueModelListener, "_groupLocalService",
			_groupLocalService);
		ReflectionTestUtil.setFieldValue(
			_portalPreferenceValueModelListener, "_layoutLocalService",
			_layoutLocalService);
		ReflectionTestUtil.setFieldValue(
			_portalPreferenceValueModelListener, "_layoutSetLocalService",
			_layoutSetLocalService);
	}

	@Test
	public void testOnAfterUpdate() {
		Group group = Mockito.mock(Group.class);
		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_groupLocalService.fetchGroup(companyId, GroupConstants.FORMS)
		).thenReturn(
			group
		);

		Layout layout = Mockito.mock(Layout.class);
		LayoutSet layoutSet = Mockito.mock(LayoutSet.class);

		Mockito.when(
			layout.getLayoutSet()
		).thenReturn(
			layoutSet
		);

		Mockito.when(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				groupId, false, "/shared")
		).thenReturn(
			layout
		);

		String value = RandomTestUtil.randomString();

		_portalPreferenceValueModelListener.onAfterUpdate(
			null, _mockPortalPreferenceValue(companyId, value));

		Mockito.verify(
			layoutSet
		).setThemeId(
			value
		);

		Mockito.verify(
			_layoutSetLocalService
		).updateLayoutSet(
			layoutSet
		);
	}

	private PortalPreferenceValue _mockPortalPreferenceValue(
		long companyId, String value) {

		PortalPreferenceValue portalPreferenceValue = Mockito.mock(
			PortalPreferenceValue.class);

		Mockito.when(
			portalPreferenceValue.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			portalPreferenceValue.getKey()
		).thenReturn(
			PropsKeys.DEFAULT_REGULAR_THEME_ID
		);

		Mockito.when(
			portalPreferenceValue.getValue()
		).thenReturn(
			value
		);

		return portalPreferenceValue;
	}

	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final LayoutLocalService _layoutLocalService = Mockito.mock(
		LayoutLocalService.class);
	private final LayoutSetLocalService _layoutSetLocalService = Mockito.mock(
		LayoutSetLocalService.class);
	private PortalPreferenceValueModelListener
		_portalPreferenceValueModelListener;

}