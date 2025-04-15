/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.lar.PermissionImporter;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.DateRange;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletPreferences;

import java.lang.reflect.Constructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Máté Thurzó
 */
@RunWith(Arquillian.class)
public class ExportImportDateUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(ExportImportDateUtilTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		PermissionImporter permissionImporter = bundleContext.getService(
			bundleContext.getServiceReference(PermissionImporter.class));

		Class<?> clazz = permissionImporter.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		clazz = classLoader.loadClass(
			"com.liferay.exportimport.internal.lar.PortletDataContextImpl");

		_constructor = clazz.getConstructor(LockManager.class);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		_layoutSet = _layout.getLayoutSet();

		_portletPreferences = PortletPreferencesFactoryUtil.getPortletSetup(
			_layout, PortletKeys.EXPORT_IMPORT, null);
	}

	@Test
	public void testGetLastPublishDateFromLastPublishDate() throws Exception {
		PortletDataContext portletDataContext =
			(PortletDataContext)_constructor.newInstance((Object)null);

		portletDataContext.setGroupId(_group.getGroupId());

		Date portletDataContextLastPublishDate = new Date();

		updateLastPublishDate(
			portletDataContext,
			ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE,
			portletDataContextLastPublishDate);

		updateLastPublishDate(_portletPreferences, new Date());

		Assert.assertEquals(
			portletDataContextLastPublishDate,
			ExportImportDateUtil.getLastPublishDate(
				portletDataContext, _portletPreferences));
	}

	@Test
	public void testGetLastPublishDateNotFromLastPublishDate()
		throws Exception {

		PortletDataContext portletDataContext =
			(PortletDataContext)_constructor.newInstance((Object)null);

		portletDataContext.setGroupId(_group.getGroupId());

		Date portletDataContextLastPublishDate = new Date();

		updateLastPublishDate(
			portletDataContext, ExportImportDateUtil.RANGE_ALL,
			portletDataContextLastPublishDate);

		updateLastPublishDate(_portletPreferences, new Date());

		Assert.assertEquals(
			portletDataContextLastPublishDate,
			ExportImportDateUtil.getLastPublishDate(
				portletDataContext, _portletPreferences));
	}

	@Test
	public void testGetLastPublishDateWithoutPorltetLastPublishDate()
		throws Exception {

		PortletDataContext portletDataContext =
			(PortletDataContext)_constructor.newInstance((Object)null);

		portletDataContext.setGroupId(_group.getGroupId());

		updateLastPublishDate(
			portletDataContext,
			ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE, new Date());

		Assert.assertNull(
			ExportImportDateUtil.getLastPublishDate(
				portletDataContext, _portletPreferences));
	}

	@Test
	public void testGetLastPublishDateWithoutPortletDataContextLastPublishDate()
		throws Exception {

		PortletDataContext portletDataContext =
			(PortletDataContext)_constructor.newInstance((Object)null);

		portletDataContext.setGroupId(_group.getGroupId());

		updateLastPublishDate(
			portletDataContext,
			ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE, null);

		Date portletLastPublishDate = new Date();

		updateLastPublishDate(_portletPreferences, portletLastPublishDate);

		Assert.assertEquals(
			portletLastPublishDate,
			ExportImportDateUtil.getLastPublishDate(
				portletDataContext, _portletPreferences));
	}

	@Test
	public void testUpdateLastPublishDateFirstPublishLayoutSet()
		throws Exception {

		Date date = new Date();

		Date startDate = new Date(date.getTime() + Time.DAY);
		Date endDate = new Date(date.getTime() + Time.WEEK);

		DateRange dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			_layoutSet.getGroupId(), _layoutSet.isPrivateLayout(), dateRange,
			endDate);

		_layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			_layoutSet.getLayoutSetId());

		Date lastPublishDate = ExportImportDateUtil.getLastPublishDate(
			_layoutSet);

		// It should be null, since no update should have happened, because it
		// would result in a gap for contents

		Assert.assertNull(lastPublishDate);
	}

	@Test
	public void testUpdateLastPublishDateFirstPublishPortlet()
		throws Exception {

		Date date = new Date();

		Date startDate = new Date(date.getTime() + Time.DAY);
		Date endDate = new Date(date.getTime() + Time.WEEK);

		DateRange dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			PortletKeys.EXPORT_IMPORT, _portletPreferences, dateRange, endDate);

		Date lastPublishDate = ExportImportDateUtil.getLastPublishDate(
			_portletPreferences);

		// It should be null, since no update should have happened, because it
		// would result in a gap for contents

		Assert.assertNull(lastPublishDate);
	}

	@Test
	public void testUpdateLastPublishDateOverlappingRangeLayoutSet()
		throws Exception {

		Date date = new Date();

		updateLastPublishDate(_layoutSet, date);

		Date startDate = new Date(date.getTime() - Time.DAY);
		Date endDate = new Date(date.getTime() + Time.WEEK);

		DateRange dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			_layoutSet.getGroupId(), _layoutSet.isPrivateLayout(), dateRange,
			endDate);

		_layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			_layoutSet.getLayoutSetId());

		Date lastPublishDate = ExportImportDateUtil.getLastPublishDate(
			_layoutSet);

		Assert.assertEquals(endDate.getTime(), lastPublishDate.getTime());
	}

	@Test
	public void testUpdateLastPublishDateOverlappingRangePortlet()
		throws Exception {

		Date date = new Date();

		updateLastPublishDate(_portletPreferences, date);

		Date startDate = new Date(date.getTime() - Time.DAY);
		Date endDate = new Date(date.getTime() + Time.WEEK);

		DateRange dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			PortletKeys.EXPORT_IMPORT, _portletPreferences, dateRange, endDate);

		Date lastPublishDate = ExportImportDateUtil.getLastPublishDate(
			_portletPreferences);

		Assert.assertEquals(endDate.getTime(), lastPublishDate.getTime());
	}

	@Test
	public void testUpdateLastPublishDateRangeBeforeLastPublishDateLayoutSet()
		throws Exception {

		Date date = new Date();

		updateLastPublishDate(_layoutSet, date);

		Date startDate = new Date(date.getTime() - Time.WEEK);
		Date endDate = new Date(date.getTime() - Time.DAY);

		DateRange dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			_layoutSet.getGroupId(), _layoutSet.isPrivateLayout(), dateRange,
			endDate);

		_layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			_layoutSet.getLayoutSetId());

		Date lastPublishDate = ExportImportDateUtil.getLastPublishDate(
			_layoutSet);

		Assert.assertEquals(date.getTime(), lastPublishDate.getTime());
	}

	@Test
	public void testUpdateLastPublishDateRangeBeforeLastPublishDatePortlet()
		throws Exception {

		Date date = new Date();

		updateLastPublishDate(_portletPreferences, date);

		Date startDate = new Date(date.getTime() - Time.WEEK);
		Date endDate = new Date(date.getTime() - Time.DAY);

		DateRange dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			PortletKeys.EXPORT_IMPORT, _portletPreferences, dateRange, endDate);

		Date lastPublishDate = ExportImportDateUtil.getLastPublishDate(
			_portletPreferences);

		Assert.assertEquals(date.getTime(), lastPublishDate.getTime());
	}

	@Test
	public void testUpdateLastPublishDateWithGapLayoutSet() throws Exception {
		Date date = new Date();

		updateLastPublishDate(_layoutSet, date);

		Date startDate = new Date(date.getTime() + Time.DAY);
		Date endDate = new Date(date.getTime() + Time.WEEK);

		DateRange dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			_layoutSet.getGroupId(), _layoutSet.isPrivateLayout(), dateRange,
			endDate);

		_layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			_layoutSet.getLayoutSetId());

		Date lastPublishDate = ExportImportDateUtil.getLastPublishDate(
			_layoutSet);

		Assert.assertEquals(date.getTime(), lastPublishDate.getTime());
	}

	@Test
	public void testUpdateLastPublishDateWithGapPortlet() throws Exception {
		Date date = new Date();

		updateLastPublishDate(_portletPreferences, date);

		Date startDate = new Date(date.getTime() + Time.DAY);
		Date endDate = new Date(date.getTime() + Time.WEEK);

		DateRange dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			PortletKeys.EXPORT_IMPORT, _portletPreferences, dateRange, endDate);

		Date lastPublishDate = ExportImportDateUtil.getLastPublishDate(
			_portletPreferences);

		Assert.assertEquals(date.getTime(), lastPublishDate.getTime());
	}

	@Test
	public void testUpdateLastPublishDateWithoutExistingLastPublishDate()
		throws Exception {

		Assert.assertNull(
			ExportImportDateUtil.getLastPublishDate(_portletPreferences));

		Date date = new Date();

		DateRange dateRange = new DateRange(date, null);

		ExportImportDateUtil.updateLastPublishDate(
			PortletKeys.EXPORT_IMPORT, _portletPreferences, dateRange, date);

		Assert.assertNull(
			ExportImportDateUtil.getLastPublishDate(_portletPreferences));

		dateRange = new DateRange(null, date);

		ExportImportDateUtil.updateLastPublishDate(
			PortletKeys.EXPORT_IMPORT, _portletPreferences, dateRange, date);

		Assert.assertEquals(
			date, ExportImportDateUtil.getLastPublishDate(_portletPreferences));
	}

	@Test
	public void testUpdateLastPublishDateWithoutGapLayoutSet()
		throws Exception {

		Date date = new Date();

		updateLastPublishDate(_layoutSet, date);

		// Start date is exactly the last publish date

		Date startDate = new Date(date.getTime());
		Date endDate = new Date(date.getTime() + Time.WEEK);

		DateRange dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			_layoutSet.getGroupId(), _layoutSet.isPrivateLayout(), dateRange,
			endDate);

		_layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			_layoutSet.getLayoutSetId());

		Date lastPublishDate = ExportImportDateUtil.getLastPublishDate(
			_layoutSet);

		Assert.assertEquals(endDate.getTime(), lastPublishDate.getTime());

		updateLastPublishDate(_layoutSet, date);

		// End date is exactly the last publish date

		startDate = new Date(date.getTime() - Time.WEEK);
		endDate = new Date(date.getTime());

		dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			_layoutSet.getGroupId(), _layoutSet.isPrivateLayout(), dateRange,
			endDate);

		_layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			_layoutSet.getLayoutSetId());

		lastPublishDate = ExportImportDateUtil.getLastPublishDate(_layoutSet);

		Assert.assertEquals(endDate.getTime(), lastPublishDate.getTime());
	}

	@Test
	public void testUpdateLastPublishDateWithoutGapPortlet() throws Exception {
		Date date = new Date();

		updateLastPublishDate(_portletPreferences, date);

		// Start date is exactly the last publish date

		Date startDate = new Date(date.getTime());
		Date endDate = new Date(date.getTime() + Time.WEEK);

		DateRange dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			PortletKeys.EXPORT_IMPORT, _portletPreferences, dateRange, endDate);

		Date lastPublishDate = ExportImportDateUtil.getLastPublishDate(
			_portletPreferences);

		Assert.assertEquals(endDate.getTime(), lastPublishDate.getTime());

		updateLastPublishDate(_portletPreferences, date);

		// End date is exactly the last publish date

		startDate = new Date(date.getTime() - Time.WEEK);
		endDate = new Date(date.getTime());

		dateRange = new DateRange(startDate, endDate);

		ExportImportDateUtil.updateLastPublishDate(
			PortletKeys.EXPORT_IMPORT, _portletPreferences, dateRange, endDate);

		lastPublishDate = ExportImportDateUtil.getLastPublishDate(
			_portletPreferences);

		Assert.assertEquals(endDate.getTime(), lastPublishDate.getTime());
	}

	protected void updateLastPublishDate(
			LayoutSet layoutSet, Date lastPublishDate)
		throws Exception {

		UnicodeProperties settingsUnicodeProperties =
			layoutSet.getSettingsProperties();

		settingsUnicodeProperties.setProperty(
			"last-publish-date", String.valueOf(lastPublishDate.getTime()));

		LayoutSetLocalServiceUtil.updateSettings(
			layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
			settingsUnicodeProperties.toString());
	}

	protected void updateLastPublishDate(
			PortletDataContext portletDataContext, String range, Date startDate)
		throws Exception {

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		if (portletDataContext.getParameterMap() == null) {
			parameterMap = new HashMap<>();

			portletDataContext.setParameterMap(parameterMap);
		}

		parameterMap.put("range", new String[] {range});

		portletDataContext.setStartDate(startDate);
	}

	protected void updateLastPublishDate(
			PortletPreferences portletPreferences, Date lastPublishDate)
		throws Exception {

		portletPreferences.setValue(
			"last-publish-date", String.valueOf(lastPublishDate.getTime()));

		portletPreferences.store();
	}

	private static Constructor<?> _constructor;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private LayoutSet _layoutSet;
	private PortletPreferences _portletPreferences;

}