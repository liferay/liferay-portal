/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.controller.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.controller.PortletImportController;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class PortletImportControllerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_portletDataContext =
			PortletDataContextFactoryUtil.createPreparePortletDataContext(
				_group.getCompanyId(), _group.getGroupId(), null, null);
	}

	@Test
	public void testImportServicePortletPreferences() throws Exception {
		_testImportServicePortletPreferencesCompanyOwner();
		_testImportServicePortletPreferencesGroupOwner();
		_testImportServicePortletPreferencesUserOwner();
	}

	private void _importServicePortletPreferences(
			long ownerId, int ownerType, String serviceName)
		throws Exception {

		Element serviceElement = SAXReaderUtil.createElement(
			"portlet-preferences");

		serviceElement.addAttribute("owner-id", String.valueOf(ownerId));
		serviceElement.addAttribute("owner-type", String.valueOf(ownerType));
		serviceElement.addAttribute("service-name", serviceName);

		Element preferenceElement = serviceElement.addElement("preference");

		Element nameElement = preferenceElement.addElement("name");

		nameElement.addText("name");

		Element valueElement = preferenceElement.addElement("value");

		valueElement.addText("value");

		_portletImportController.importServicePortletPreferences(
			_portletDataContext, serviceElement);
	}

	private void _testImportServicePortletPreferencesCompanyOwner()
		throws Exception {

		String serviceName = RandomTestUtil.randomString();

		_importServicePortletPreferences(
			_group.getCompanyId(), PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			serviceName);

		Assert.assertNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				_group.getCompanyId(), PortletKeys.PREFS_OWNER_TYPE_COMPANY,
				PortletKeys.PREFS_PLID_SHARED, serviceName));
	}

	private void _testImportServicePortletPreferencesGroupOwner()
		throws Exception {

		String serviceName = RandomTestUtil.randomString();

		_importServicePortletPreferences(
			RandomTestUtil.randomLong(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
			serviceName);

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getStrictPreferences(
				_group.getCompanyId(), _group.getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_GROUP,
				PortletKeys.PREFS_PLID_SHARED, serviceName);

		Assert.assertEquals(
			"value", portletPreferences.getValue("name", StringPool.BLANK));
	}

	private void _testImportServicePortletPreferencesUserOwner()
		throws Exception {

		String serviceName = RandomTestUtil.randomString();

		_importServicePortletPreferences(
			TestPropsValues.getUserId(), PortletKeys.PREFS_OWNER_TYPE_USER,
			serviceName);

		Assert.assertNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				TestPropsValues.getUserId(), PortletKeys.PREFS_OWNER_TYPE_USER,
				PortletKeys.PREFS_PLID_SHARED, serviceName));
	}

	@DeleteAfterTestRun
	private Group _group;

	private PortletDataContext _portletDataContext;

	@Inject
	private PortletImportController _portletImportController;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}