/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.embedded.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class EmbeddedPortletWhenEmbeddingEmbeddablePortletInLayoutTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		_layoutTypePortlet = (LayoutTypePortlet)_layout.getLayoutType();

		_layoutStaticPortletsAll = PropsValues.LAYOUT_STATIC_PORTLETS_ALL;
	}

	@After
	public void tearDown() {
		StringBundler sb = new StringBundler(_layoutStaticPortletsAll.length);

		for (String portletId : _layoutStaticPortletsAll) {
			sb.append(portletId);
		}

		PropsUtil.set(PropsKeys.LAYOUT_STATIC_PORTLETS_ALL, sb.toString());
	}

	@Test
	public void testShouldNotReturnItFromExplicitlyAddedPortlets()
		throws Exception {

		Portlet portlet = _portletLocalService.getPortletById(
			PortletKeys.LOGIN);

		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
			portlet.getPortletId(), portlet, null);

		List<Portlet> explicitlyAddedPortlets =
			_layoutTypePortlet.getExplicitlyAddedPortlets();

		Assert.assertFalse(
			explicitlyAddedPortlets.toString(),
			explicitlyAddedPortlets.contains(portlet));
	}

	@Test
	public void testShouldReturnItFromAllPortlets() throws Exception {
		Portlet portlet = _portletLocalService.getPortletById(
			PortletKeys.LOGIN);

		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
			portlet.getPortletId(), portlet, null);

		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), _layout.getGroupId(),
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, PortletKeys.PREFS_PLID_SHARED,
			portlet.getPortletId(), portlet, null);

		List<Portlet> allPortlets = _layoutTypePortlet.getAllPortlets();

		Assert.assertTrue(
			allPortlets.toString(), allPortlets.contains(portlet));
	}

	@Test
	public void testShouldReturnItFromEmbeddedPortlets() throws Exception {
		Portlet portlet = _portletLocalService.getPortletById(
			PortletKeys.LOGIN);

		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
			portlet.getPortletId(), portlet, null);

		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), _layout.getGroupId(),
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, PortletKeys.PREFS_PLID_SHARED,
			portlet.getPortletId(), portlet, null);

		List<Portlet> embeddedPortlets =
			_layoutTypePortlet.getEmbeddedPortlets();

		Assert.assertTrue(
			embeddedPortlets.toString(), embeddedPortlets.contains(portlet));
	}

	@Test
	public void testShouldReturnItsConfiguration() throws Exception {
		Portlet portlet = _portletLocalService.getPortletById(
			PortletKeys.LOGIN);

		String defaultPreferences =
			"<portlet-preferences><preference><name>testName</name><value>" +
				"testValue</value></preference></portlet-preferences>";

		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
			portlet.getPortletId(), portlet, defaultPreferences);

		List<PortletPreferences> portletPreferences =
			_portletPreferencesLocalService.getPortletPreferences(
				_layout.getPlid(), portlet.getPortletId());

		Assert.assertEquals(
			portletPreferences.toString(), 1, portletPreferences.size());

		PortletPreferences embeddedPortletPreferences = portletPreferences.get(
			0);

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				embeddedPortletPreferences);

		Assert.assertArrayEquals(
			new String[] {"testValue"},
			jxPortletPreferences.getValues("testName", null));
	}

	@DeleteAfterTestRun
	private static Group _group;

	private static Layout _layout;
	private static String[] _layoutStaticPortletsAll;
	private static LayoutTypePortlet _layoutTypePortlet;

	@Inject
	private static PortletLocalService _portletLocalService;

	@Inject
	private static PortletPreferencesLocalService
		_portletPreferencesLocalService;

	@Inject
	private static PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

}