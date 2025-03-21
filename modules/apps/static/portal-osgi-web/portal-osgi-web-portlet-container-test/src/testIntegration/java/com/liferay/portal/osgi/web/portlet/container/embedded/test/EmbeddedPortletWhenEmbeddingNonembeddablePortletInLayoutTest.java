/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.embedded.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.osgi.web.portlet.container.test.BasePortletContainerTestCase;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.util.Dictionary;
import java.util.Hashtable;
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
public class EmbeddedPortletWhenEmbeddingNonembeddablePortletInLayoutTest
	extends BasePortletContainerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_layoutTypePortlet = (LayoutTypePortlet)layout.getLayoutType();

		_layoutStaticPortletsAll = PropsValues.LAYOUT_STATIC_PORTLETS_ALL;

		_testNonembeddedPortlet = new TestNonembeddedPortlet();

		Dictionary<String, Object> properties = new Hashtable<>();

		setUpPortlet(
			_testNonembeddedPortlet, properties,
			_testNonembeddedPortlet.getPortletId(), false);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		StringBundler sb = new StringBundler(_layoutStaticPortletsAll.length);

		for (String portletId : _layoutStaticPortletsAll) {
			sb.append(portletId);
		}

		PropsUtil.set(PropsKeys.LAYOUT_STATIC_PORTLETS_ALL, sb.toString());

		super.tearDown();
	}

	@Test
	public void testShouldNotReturnItFromAllPortlets() throws Exception {
		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
			_testNonembeddedPortlet.getPortletId(), _testNonembeddedPortlet,
			null);

		List<Portlet> allPortlets = _layoutTypePortlet.getAllPortlets();

		Assert.assertFalse(
			allPortlets.toString(),
			allPortlets.contains(_testNonembeddedPortlet));
	}

	@Test
	public void testShouldNotReturnItFromEmbeddedPortlets() throws Exception {
		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
			_testNonembeddedPortlet.getPortletId(), _testNonembeddedPortlet,
			null);

		List<Portlet> embeddedPortlets =
			_layoutTypePortlet.getEmbeddedPortlets();

		Assert.assertFalse(
			embeddedPortlets.toString(),
			embeddedPortlets.contains(_testNonembeddedPortlet));
	}

	@Test
	public void testShouldNotReturnItFromExplicitlyAddedPortlets()
		throws Exception {

		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
			_testNonembeddedPortlet.getPortletId(), _testNonembeddedPortlet,
			null);

		List<Portlet> explicitlyAddedPortlets =
			_layoutTypePortlet.getExplicitlyAddedPortlets();

		Assert.assertFalse(
			explicitlyAddedPortlets.toString(),
			explicitlyAddedPortlets.contains(_testNonembeddedPortlet));
	}

	@Test
	public void testShouldReturnItsConfiguration() throws Exception {
		String defaultPreferences =
			"<portlet-preferences><preference><name>testName</name><value>" +
				"testValue1</value><value>testValue2</value>" +
					"</preference></portlet-preferences>";

		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
			_testNonembeddedPortlet.getPortletId(), _testNonembeddedPortlet,
			defaultPreferences);

		List<PortletPreferences> portletPreferences =
			_portletPreferencesLocalService.getPortletPreferences(
				layout.getPlid(), _testNonembeddedPortlet.getPortletId());

		Assert.assertEquals(
			portletPreferences.toString(), 1, portletPreferences.size());

		PortletPreferences embeddedPortletPreferences = portletPreferences.get(
			0);

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				embeddedPortletPreferences);

		Assert.assertArrayEquals(
			new String[] {"testValue1", "testValue2"},
			jxPortletPreferences.getValues("testName", null));
	}

	private static String[] _layoutStaticPortletsAll;
	private static LayoutTypePortlet _layoutTypePortlet;

	@Inject
	private static PortletPreferencesLocalService
		_portletPreferencesLocalService;

	@Inject
	private static PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	private TestNonembeddedPortlet _testNonembeddedPortlet;

}