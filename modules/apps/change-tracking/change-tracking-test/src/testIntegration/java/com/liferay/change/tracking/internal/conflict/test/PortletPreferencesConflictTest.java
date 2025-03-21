/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.conflict.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class PortletPreferencesConflictTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
		_ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group, false);

		_portlet = _portletLocalService.getPortletById(PortletKeys.LOGIN);
	}

	@Test
	public void testResolvePortletPreferencesConflictTest() throws Exception {
		jakarta.portlet.PortletPreferences jxPortletPreferences = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			PortletPreferences portletPreferences =
				_portletPreferencesLocalService.addPortletPreferences(
					TestPropsValues.getCompanyId(),
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
					_portlet.getPortletId(), _portlet, null);

			jxPortletPreferences =
				_portletPreferenceValueLocalService.getPreferences(
					portletPreferences);

			Assert.assertNotNull(jxPortletPreferences);

			jxPortletPreferences.setValue(
				_ctCollection1.getName(), RandomTestUtil.randomString());

			_portletPreferencesLocalService.updatePreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				_portlet.getPortletId(), jxPortletPreferences);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			PortletPreferences portletPreferences =
				_portletPreferencesLocalService.addPortletPreferences(
					TestPropsValues.getCompanyId(),
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
					_portlet.getPortletId(), _portlet, null);

			jxPortletPreferences =
				_portletPreferenceValueLocalService.getPreferences(
					portletPreferences);

			Assert.assertNotNull(jxPortletPreferences);

			jxPortletPreferences.setValue(
				_ctCollection2.getName(), RandomTestUtil.randomString());

			_portletPreferencesLocalService.updatePreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				_portlet.getPortletId(), jxPortletPreferences);
		}

		_ctProcessLocalService.addCTProcess(
			_ctCollection2.getUserId(), _ctCollection2.getCtCollectionId());

		jakarta.portlet.PortletPreferences publishedJavaxPortletPreferences =
			_portletPreferencesLocalService.getPreferences(
				TestPropsValues.getCompanyId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				_portlet.getPortletId());

		Assert.assertNotNull(
			publishedJavaxPortletPreferences.getValue(
				_ctCollection2.getName(), null));

		Assert.assertNull(
			publishedJavaxPortletPreferences.getValue(
				_ctCollection1.getName(), null));

		Map<Long, List<ConflictInfo>> conflictInfos =
			_ctCollectionLocalService.checkConflicts(_ctCollection1);

		for (List<ConflictInfo> conflictInfoLists : conflictInfos.values()) {
			for (ConflictInfo conflictInfo : conflictInfoLists) {
				Assert.assertTrue(conflictInfo.isResolved());
			}
		}

		_ctProcessLocalService.addCTProcess(
			_ctCollection1.getUserId(), _ctCollection1.getCtCollectionId());

		publishedJavaxPortletPreferences =
			_portletPreferencesLocalService.getPreferences(
				TestPropsValues.getCompanyId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				_portlet.getPortletId());

		Assert.assertNotNull(
			publishedJavaxPortletPreferences.getValue(
				_ctCollection1.getName(), null));
		Assert.assertNull(
			publishedJavaxPortletPreferences.getValue(
				_ctCollection2.getName(), null));
	}

	@Test
	public void testResolvePortletPreferenceValueConflictTest()
		throws Exception {

		String conflictValueName = RandomTestUtil.randomString();

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				TestPropsValues.getCompanyId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				_portlet.getPortletId(), _portlet, null);

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				portletPreferences);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			jxPortletPreferences.setValue(
				_ctCollection1.getName(), RandomTestUtil.randomString());

			jxPortletPreferences.setValue(
				conflictValueName, _ctCollection1.getName());

			portletPreferences =
				_portletPreferencesLocalService.updatePreferences(
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
					_portlet.getPortletId(), jxPortletPreferences);
		}

		jxPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				portletPreferences);

		Assert.assertNull(
			jxPortletPreferences.getValue(_ctCollection1.getName(), null));

		Assert.assertNull(
			jxPortletPreferences.getValue(conflictValueName, null));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			jxPortletPreferences.setValue(
				_ctCollection2.getName(), RandomTestUtil.randomString());

			jxPortletPreferences.setValue(
				conflictValueName, _ctCollection2.getName());

			_portletPreferencesLocalService.updatePreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				_portlet.getPortletId(), jxPortletPreferences);
		}

		_ctProcessLocalService.addCTProcess(
			_ctCollection2.getUserId(), _ctCollection2.getCtCollectionId());

		jakarta.portlet.PortletPreferences publishedJavaxPortletPreferences =
			_portletPreferencesLocalService.getPreferences(
				TestPropsValues.getCompanyId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				_portlet.getPortletId());

		Assert.assertNull(
			publishedJavaxPortletPreferences.getValue(
				_ctCollection1.getName(), null));
		Assert.assertNotNull(
			publishedJavaxPortletPreferences.getValue(
				_ctCollection2.getName(), null));
		Assert.assertEquals(
			publishedJavaxPortletPreferences.getValue(conflictValueName, null),
			_ctCollection2.getName());

		Map<Long, List<ConflictInfo>> conflictInfos =
			_ctCollectionLocalService.checkConflicts(_ctCollection1);

		for (List<ConflictInfo> conflictInfoLists : conflictInfos.values()) {
			for (ConflictInfo conflictInfo : conflictInfoLists) {
				Assert.assertTrue(conflictInfo.isResolved());
			}
		}

		_ctProcessLocalService.addCTProcess(
			_ctCollection1.getUserId(), _ctCollection1.getCtCollectionId());

		publishedJavaxPortletPreferences =
			_portletPreferencesLocalService.getPreferences(
				TestPropsValues.getCompanyId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				_portlet.getPortletId());

		Assert.assertNotNull(
			publishedJavaxPortletPreferences.getValue(
				_ctCollection1.getName(), null));
		Assert.assertNotNull(
			publishedJavaxPortletPreferences.getValue(
				_ctCollection2.getName(), null));
		Assert.assertEquals(
			publishedJavaxPortletPreferences.getValue(conflictValueName, null),
			_ctCollection1.getName());
	}

	@Inject
	private CounterLocalService _counterLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection1;

	@DeleteAfterTestRun
	private CTCollection _ctCollection2;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Layout _layout;

	@DeleteAfterTestRun
	private Portlet _portlet;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

}