/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.preferences.internal.change.tracking.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseTableReferenceDefinitionTestCase;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Gislayne Vitorino
 */
@RunWith(Arquillian.class)
public class PortletPreferenceValueTableReferenceDefinitionTest
	extends BaseTableReferenceDefinitionTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				TestPropsValues.getCompanyId(), group.getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, TestPropsValues.getPlid(),
				RandomTestUtil.randomString(), null,
				PortletConstants.DEFAULT_PREFERENCES);
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		PortletPreferenceValue portletPreferenceValue =
			_portletPreferenceValueLocalService.createPortletPreferenceValue(
				RandomTestUtil.randomLong());

		portletPreferenceValue.setCompanyId(TestPropsValues.getCompanyId());
		portletPreferenceValue.setPortletPreferencesId(
			_portletPreferences.getPortletPreferencesId());
		portletPreferenceValue.setIndex(RandomTestUtil.nextInt());
		portletPreferenceValue.setLargeValue(RandomTestUtil.randomString());
		portletPreferenceValue.setName(RandomTestUtil.randomString());
		portletPreferenceValue.setReadOnly(RandomTestUtil.randomBoolean());
		portletPreferenceValue.setValue(RandomTestUtil.randomString());

		return _portletPreferenceValueLocalService.addPortletPreferenceValue(
			portletPreferenceValue);
	}

	@Inject
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	private PortletPreferences _portletPreferences;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}