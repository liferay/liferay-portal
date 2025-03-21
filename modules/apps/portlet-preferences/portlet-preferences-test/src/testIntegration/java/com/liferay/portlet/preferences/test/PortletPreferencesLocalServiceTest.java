/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.preferences.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.deploy.hot.ServiceBag;
import com.liferay.portal.kernel.bean.ClassLoaderBeanHandler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceWrapper;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CompanyProviderClassTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.PortletAppImpl;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.StrictPortletPreferencesImpl;

import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class PortletPreferencesLocalServiceTest
	extends BasePortletPreferencesTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule() {
			{
				skipTestRule(CompanyProviderClassTestRule.INSTANCE);
			}
		};

	@BeforeClass
	public static void setUpClass() {
		_portletApp.setServletContext(
			(ServletContext)ProxyUtil.newProxyInstance(
				ServletContext.class.getClassLoader(),
				new Class<?>[] {ServletContext.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "getServletContextName")) {

						return StringPool.BLANK;
					}

					return null;
				}));
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		testPortlet.setPortletApp(_portletApp);
	}

	@After
	public void tearDown() {
		portletLocalService.destroyPortlet(testPortlet);
	}

	@Test
	public void testAddPortletPreferencesWithCompanyThreadLocal()
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			PortletPreferences portletPreferences =
				portletPreferencesLocalService.addPortletPreferences(
					0, PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
					testPortlet.getPortletId(), testPortlet, null);

			Assert.assertEquals(
				(long)CompanyThreadLocal.getCompanyId(),
				portletPreferences.getCompanyId());
		}
	}

	@Test
	public void testAddPortletPreferencesWithCompanyThreadLocalSystem()
		throws Exception {

		PortletPreferences portletPreferences =
			portletPreferencesLocalService.addPortletPreferences(
				TestPropsValues.getCompanyId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId(), testPortlet, null);

		Assert.assertEquals(
			TestPropsValues.getCompanyId(), portletPreferences.getCompanyId());
	}

	@Test
	public void testAddPortletPreferencesWithDefaultMultipleXML()
		throws Exception {

		PortletPreferences portletPreferences = addLayoutPortletPreferences(
			testLayout, testPortlet,
			getPortletPreferencesXML(_NAME, _MULTIPLE_VALUES));

		PortletPreferencesImpl portletPreferencesImpl =
			_toPortletPreferencesImpl(portletPreferences);

		assertOwner(testLayout, portletPreferencesImpl);

		assertValues(portletPreferences, _NAME, _MULTIPLE_VALUES);
	}

	@Test
	public void testAddPortletPreferencesWithDefaultNullXML() throws Exception {
		PortletPreferences portletPreferences = addLayoutPortletPreferences(
			testLayout, testPortlet, null);

		PortletPreferencesImpl portletPreferencesImpl =
			_toPortletPreferencesImpl(portletPreferences);

		assertOwner(testLayout, portletPreferencesImpl);
		assertEmptyPortletPreferencesMap(portletPreferencesImpl);

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			fetchLayoutJxPortletPreferences(testLayout, testPortlet);

		assertOwner(testLayout, (PortletPreferencesImpl)jxPortletPreferences);
		assertEmptyPortletPreferencesMap(jxPortletPreferences);
	}

	@Test
	public void testAddPortletPreferencesWithDefaultNullXMLAndNullPortlet()
		throws Exception {

		PortletPreferences portletPreferences =
			portletPreferencesLocalService.addPortletPreferences(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId(), null, null);

		PortletPreferencesImpl portletPreferencesImpl =
			_toPortletPreferencesImpl(portletPreferences);

		assertOwner(testLayout, portletPreferencesImpl);
		assertEmptyPortletPreferencesMap(portletPreferencesImpl);
	}

	@Test
	public void testAddPortletPreferencesWithDefaultSingleXML()
		throws Exception {

		PortletPreferences portletPreferences = addLayoutPortletPreferences(
			testLayout, testPortlet,
			getPortletPreferencesXML(_NAME, _SINGLE_VALUE));

		PortletPreferencesImpl portletPreferencesImpl =
			_toPortletPreferencesImpl(portletPreferences);

		assertOwner(testLayout, portletPreferencesImpl);

		assertValues(portletPreferences, _NAME, _SINGLE_VALUE);
	}

	@Test
	public void testAddPortletPreferencesWithPortlet() throws Exception {
		testPortlet.setDefaultPreferences(
			getPortletPreferencesXML(_NAME, _SINGLE_VALUE));

		PortletPreferences portletPreferences = addLayoutPortletPreferences(
			testLayout, testPortlet);

		PortletPreferencesImpl portletPreferencesImpl =
			_toPortletPreferencesImpl(portletPreferences);

		assertOwner(testLayout, portletPreferencesImpl);

		assertValues(portletPreferences, _NAME, _SINGLE_VALUE);
	}

	@Test
	public void testDeleteGroupPortletPreferencesByPlid() {
		PortletPreferences portletPreferences = addGroupPortletPreferences(
			testLayout, testPortlet);

		portletPreferencesLocalService.deletePortletPreferences(
			testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
			testLayout.getPlid());

		Assert.assertNull(
			portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testDeleteGroupPortletPreferencesByPlidAndPortletId()
		throws Exception {

		PortletPreferences portletPreferences = addGroupPortletPreferences(
			testLayout, testPortlet);

		portletPreferencesLocalService.deletePortletPreferences(
			testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
			testLayout.getPlid(), testPortlet.getPortletId());

		Assert.assertNull(
			portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testDeleteLayoutPortletPreferencesByPlid() throws Exception {
		PortletPreferences portletPreferences = addLayoutPortletPreferences(
			testLayout, testPortlet);

		portletPreferencesLocalService.deletePortletPreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid());

		Assert.assertNull(
			portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testDeleteLayoutPortletPreferencesByPlidAndPortletId()
		throws Exception {

		PortletPreferences portletPreferences = addLayoutPortletPreferences(
			testLayout, testPortlet);

		portletPreferencesLocalService.deletePortletPreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
			testPortlet.getPortletId());

		Assert.assertNull(
			portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testDeleteOriginalGroupPortletPreferencesByPlid()
		throws Exception {

		addGroupPortletPreferences(testLayout, testPortlet);

		Layout layout = _createNewLayout(false);

		PortletPreferences portletPreferences = addGroupPortletPreferences(
			layout,
			portletLocalService.getPortletById(
				layout.getCompanyId(), String.valueOf(_PORTLET_ID + 1)));

		portletPreferencesLocalService.deletePortletPreferences(
			testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
			testLayout.getPlid());

		Assert.assertNotNull(
			portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testDeleteOriginalGroupPortletPreferencesByPlidAndPortletId()
		throws Exception {

		addGroupPortletPreferences(testLayout, testPortlet);

		Layout layout = _createNewLayout(false);

		PortletPreferences portletPreferences = addGroupPortletPreferences(
			layout,
			portletLocalService.getPortletById(
				layout.getCompanyId(), String.valueOf(_PORTLET_ID + 1)));

		portletPreferencesLocalService.deletePortletPreferences(
			testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
			testLayout.getPlid(), testPortlet.getPortletId());

		Assert.assertNotNull(
			portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testDeleteOriginalLayoutPortletPreferencesByPlid()
		throws Exception {

		addLayoutPortletPreferences(testLayout, testPortlet);

		Layout layout = _createNewLayout(false);

		PortletPreferences portletPreferences2 = addLayoutPortletPreferences(
			layout,
			portletLocalService.getPortletById(
				layout.getCompanyId(), String.valueOf(_PORTLET_ID + 1)));

		portletPreferencesLocalService.deletePortletPreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid());

		Assert.assertNotNull(
			portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences2.getPortletPreferencesId()));
	}

	@Test
	public void testDeleteOriginalLayoutPortletPreferencesByPlidAndPortletId()
		throws Exception {

		addLayoutPortletPreferences(testLayout, testPortlet);

		PortletPreferences portletPreferences = addLayoutPortletPreferences(
			testLayout,
			portletLocalService.getPortletById(
				testLayout.getCompanyId(), String.valueOf(_PORTLET_ID + 1)));

		portletPreferencesLocalService.deletePortletPreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
			testPortlet.getPortletId());

		Assert.assertNotNull(
			portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testDeleteOriginalPortletPreferencesByPlid() throws Exception {
		addLayoutPortletPreferences(testLayout, testPortlet);

		PortletPreferences portletPreferences = addLayoutPortletPreferences(
			testLayout,
			portletLocalService.getPortletById(
				testLayout.getCompanyId(), String.valueOf(_PORTLET_ID + 1)));

		portletPreferencesLocalService.deletePortletPreferencesByPlid(
			testLayout.getPlid());

		Assert.assertNull(
			portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testDeletePortletPreferencesByPlid() throws Exception {
		PortletPreferences portletPreferences = addLayoutPortletPreferences(
			testLayout, testPortlet);

		portletPreferencesLocalService.deletePortletPreferencesByPlid(
			testLayout.getPlid());

		Assert.assertNull(
			portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testDeletePortletPreferencesByPortletPreferencesId()
		throws Exception {

		PortletPreferences portletPreferences = addLayoutPortletPreferences(
			testLayout, testPortlet);

		portletPreferencesLocalService.deletePortletPreferences(
			portletPreferences.getPortletPreferencesId());

		Assert.assertNull(
			portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testFetchLayoutJxPortletPreferences() throws Exception {
		PortletPreferencesImpl portletPreferencesImpl =
			(PortletPreferencesImpl)fetchLayoutJxPortletPreferences(
				testLayout, testPortlet);

		Assert.assertNull(portletPreferencesImpl);
	}

	@Test
	public void testFetchNonexistentPreferences() throws Exception {
		addLayoutPortletPreferences(
			testLayout, testPortlet,
			getPortletPreferencesXML(_NAME, _SINGLE_VALUE));

		portletPreferencesLocalService.deletePortletPreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
			testPortlet.getPortletId());

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.fetchPreferences(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId());

		Assert.assertNull(jxPortletPreferences);
	}

	@Test
	public void testFetchPreferences() throws Exception {
		addLayoutPortletPreferences(
			testLayout, testPortlet,
			getPortletPreferencesXML(_NAME, _SINGLE_VALUE));

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.fetchPreferences(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId());

		assertValues(jxPortletPreferences, _NAME, _SINGLE_VALUE);
	}

	@Test
	public void testFetchPreferencesByPortletPreferencesIds() throws Exception {
		addLayoutPortletPreferences(
			testLayout, testPortlet,
			getPortletPreferencesXML(_NAME, _SINGLE_VALUE));

		PortletPreferencesIds portletPreferencesIds = new PortletPreferencesIds(
			testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
			testPortlet.getPortletId());

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.fetchPreferences(
				portletPreferencesIds);

		assertValues(jxPortletPreferences, _NAME, _SINGLE_VALUE);
	}

	@Test
	public void testGetAllPortletPreferences() throws Exception {
		List<PortletPreferences> initialPortletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences();

		addLayoutPortletPreferences(testLayout, testPortlet);

		List<PortletPreferences> currentPortletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences();

		Assert.assertEquals(
			currentPortletPreferencesList.toString(),
			initialPortletPreferencesList.size() + 1,
			currentPortletPreferencesList.size());
	}

	@Test
	public void testGetGroupPortletPreferencesByCompanyIdAndGroupIdAndPortletId()
		throws Exception {

		addGroupPortletPreferences(testLayout, testPortlet);

		addGroupPortletPreferences(_createNewLayout(false), testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				testLayout.getCompanyId(), testLayout.getGroupId(),
				testLayout.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testPortlet.getPortletId(), false);

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());

		PortletPreferences portletPreferences = portletPreferencesList.get(0);

		Assert.assertEquals(testLayout.getPlid(), portletPreferences.getPlid());
	}

	@Test
	public void testGetGroupPortletPreferencesByOwnerAndPlid() {
		addGroupPortletPreferences(testLayout, testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testLayout.getPlid());

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());
	}

	@Test
	public void testGetGroupPortletPreferencesByOwnerAndPlidAndPortletId()
		throws Exception {

		addGroupPortletPreferences(testLayout, testPortlet);

		PortletPreferences portletPreferences =
			portletPreferencesLocalService.getPortletPreferences(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testLayout.getPlid(), testPortlet.getPortletId());

		Assert.assertEquals(
			portletPreferences.getPortletId(), testPortlet.getPortletId());

		assertOwner(testGroup, _toPortletPreferencesImpl(portletPreferences));
	}

	@Test
	public void testGetGroupPortletPreferencesByPlidAndPortletId()
		throws Exception {

		addGroupPortletPreferences(testLayout, testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_TYPE_GROUP, testLayout.getPlid(),
				testPortlet.getPortletId());

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());

		PortletPreferencesImpl portletPreferencesImpl =
			_toPortletPreferencesImpl(portletPreferencesList.get(0));

		assertOwner(testLayout.getGroup(), portletPreferencesImpl);
	}

	@Test
	public void testGetGroupPortletPreferencesCountByOwnerAndNotPlidAndPortlet() {
		Assert.assertEquals(
			0,
			portletPreferencesLocalService.getPortletPreferencesCount(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP, -1,
				testPortlet, false));

		portletPreferencesLocalService.addPortletPreferences(
			testPortlet.getCompanyId(), testGroup.getGroupId(),
			PortletKeys.PREFS_OWNER_TYPE_GROUP, -1, testPortlet.getPortletId(),
			testPortlet, null);

		Assert.assertEquals(
			1,
			portletPreferencesLocalService.getPortletPreferencesCount(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP, -1,
				testPortlet, false));
	}

	@Test
	public void testGetGroupPortletPreferencesCountByOwnerAndPlidAndPortlet() {
		Assert.assertEquals(
			0,
			portletPreferencesLocalService.getPortletPreferencesCount(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testLayout.getPlid(), testPortlet, false));

		addGroupPortletPreferences(testLayout, testPortlet);

		Assert.assertEquals(
			1,
			portletPreferencesLocalService.getPortletPreferencesCount(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testLayout.getPlid(), testPortlet, false));
	}

	@Test
	public void testGetGroupPortletPreferencesCountByOwnerAndPlidAndPortletExcludeDefault() {
		Assert.assertEquals(
			0,
			portletPreferencesLocalService.getPortletPreferencesCount(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testLayout.getPlid(), testPortlet, true));

		addGroupPortletPreferences(testLayout, testPortlet);

		Assert.assertEquals(
			0,
			portletPreferencesLocalService.getPortletPreferencesCount(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testLayout.getPlid(), testPortlet, true));
	}

	@Test
	public void testGetGroupPortletPreferencesCountByOwnerAndPortletId() {
		Assert.assertEquals(
			0,
			portletPreferencesLocalService.getPortletPreferencesCount(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testPortlet.getPortletId(), false));

		addGroupPortletPreferences(testLayout, testPortlet);

		Assert.assertEquals(
			1,
			portletPreferencesLocalService.getPortletPreferencesCount(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testPortlet.getPortletId(), false));
	}

	@Test
	public void testGetGroupPortletPreferencesCountByOwnerAndPortletIdExcludeDefault() {
		Assert.assertEquals(
			0,
			portletPreferencesLocalService.getPortletPreferencesCount(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testPortlet.getPortletId(), true));

		addGroupPortletPreferences(testLayout, testPortlet);

		Assert.assertEquals(
			0,
			portletPreferencesLocalService.getPortletPreferencesCount(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testPortlet.getPortletId(), true));
	}

	@Test
	public void testGetGroupPreferencesByOwnerAndPlidAndPortletIdNotAutoAdded() {
		String singleValuePortletPreferencesXML = getPortletPreferencesXML(
			_NAME, _SINGLE_VALUE);

		addGroupPortletPreferences(
			testLayout, testPortlet, singleValuePortletPreferencesXML);

		String multipleValuesPortletPreferencesXML = getPortletPreferencesXML(
			_NAME, _MULTIPLE_VALUES);

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				testGroup.getCompanyId(), testGroup.getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_GROUP, testLayout.getPlid(),
				testPortlet.getPortletId(),
				multipleValuesPortletPreferencesXML);

		assertValues(jxPortletPreferences, _NAME, _SINGLE_VALUE);
		assertOwner(
			testLayout.getGroup(),
			(PortletPreferencesImpl)jxPortletPreferences);
	}

	@Test
	public void testGetGroupPreferencesByOwnerAndPlidAndPortletIdWithoutDefaultAutoAdded() {
		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				testGroup.getCompanyId(), testGroup.getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_GROUP, testLayout.getPlid(),
				testPortlet.getPortletId());

		assertEmptyPortletPreferencesMap(jxPortletPreferences);
		assertOwner(
			testLayout.getGroup(),
			(PortletPreferencesImpl)jxPortletPreferences);
	}

	@Test
	public void testGetGroupPreferencesByPortletPreferencesIds() {
		addGroupPortletPreferences(
			testLayout, testPortlet,
			getPortletPreferencesXML(_NAME, _SINGLE_VALUE));

		PortletPreferencesIds portletPreferencesIds = new PortletPreferencesIds(
			testGroup.getCompanyId(), testGroup.getGroupId(),
			PortletKeys.PREFS_OWNER_TYPE_GROUP, testLayout.getPlid(),
			testPortlet.getPortletId());

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				portletPreferencesIds);

		assertValues(jxPortletPreferences, _NAME, _SINGLE_VALUE);
		assertOwner(
			testLayout.getGroup(),
			(PortletPreferencesImpl)jxPortletPreferences);
	}

	@Test
	public void testGetGroupreferencesByOwnerAndPlidAndPortletIdWithDefaultXMLAutoAdded() {
		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				testGroup.getCompanyId(), testGroup.getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_GROUP, testLayout.getPlid(),
				testPortlet.getPortletId(),
				getPortletPreferencesXML(_NAME, _SINGLE_VALUE));

		assertValues(jxPortletPreferences, _NAME, _SINGLE_VALUE);
		assertOwner(
			testLayout.getGroup(),
			(PortletPreferencesImpl)jxPortletPreferences);
	}

	@Test
	public void testGetLayoutPortletPreferencesByCompanyIdAndGroupIdAndPortletId()
		throws Exception {

		addLayoutPortletPreferences(testLayout, testPortlet);

		addLayoutPortletPreferences(
			LayoutTestUtil.addTypePortletLayout(testLayout.getGroup()),
			testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				testLayout.getCompanyId(), testLayout.getGroupId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testPortlet.getPortletId(),
				false);

		Assert.assertEquals(
			portletPreferencesList.toString(), 2,
			portletPreferencesList.size());

		PortletPreferences portletPreferences = portletPreferencesList.get(0);

		Assert.assertEquals(
			testPortlet.getPortletId(), portletPreferences.getPortletId());
	}

	@Test
	public void testGetLayoutPortletPreferencesByPlidAndPortletId()
		throws Exception {

		addLayoutPortletPreferences(testLayout, testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId());

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());

		PortletPreferencesImpl portletPreferencesImpl =
			_toPortletPreferencesImpl(portletPreferencesList.get(0));

		assertOwner(testLayout, portletPreferencesImpl);
	}

	@Test
	public void testGetLayoutPortletPreferencesCountByPlidAndPortletId()
		throws Exception {

		Assert.assertEquals(
			0,
			portletPreferencesLocalService.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId()));

		addLayoutPortletPreferences(testLayout, testPortlet);

		portletLocalService.deployPortlet(testPortlet);

		Assert.assertEquals(
			1,
			portletPreferencesLocalService.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId()));
	}

	@Test
	public void testGetLayoutPortletPreferencesCountByPortletId()
		throws Exception {

		Assert.assertEquals(
			0,
			portletPreferencesLocalService.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				testPortlet.getPortletId()));

		addLayoutPortletPreferences(testLayout, testPortlet);

		addLayoutPortletPreferences(
			LayoutTestUtil.addTypePortletLayout(testGroup), testPortlet);

		Assert.assertEquals(
			2,
			portletPreferencesLocalService.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				testPortlet.getPortletId()));
	}

	@Test
	public void testGetLayoutPreferencesByOwnerAndPlidAndPortletIdNotAutoAdded()
		throws Exception {

		String singleValuePortletPreferencesXML = getPortletPreferencesXML(
			_NAME, _SINGLE_VALUE);

		addLayoutPortletPreferences(
			testLayout, testPortlet, singleValuePortletPreferencesXML);

		String multipleValuesPortletPreferencesXML = getPortletPreferencesXML(
			_NAME, _MULTIPLE_VALUES);

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId(),
				multipleValuesPortletPreferencesXML);

		assertValues(jxPortletPreferences, _NAME, _SINGLE_VALUE);
		assertOwner(testLayout, (PortletPreferencesImpl)jxPortletPreferences);
	}

	@Test
	public void testGetLayoutPreferencesByOwnerAndPlidAndPortletIdWithDefaultXMLAutoAdded() {
		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId(),
				getPortletPreferencesXML(_NAME, _SINGLE_VALUE));

		assertValues(jxPortletPreferences, _NAME, _SINGLE_VALUE);
		assertOwner(testLayout, (PortletPreferencesImpl)jxPortletPreferences);
	}

	@Test
	public void testGetLayoutPreferencesByOwnerAndPlidAndPortletIdWithoutDefaultAutoAdded() {
		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId());

		assertEmptyPortletPreferencesMap(jxPortletPreferences);
		assertOwner(testLayout, (PortletPreferencesImpl)jxPortletPreferences);
	}

	@Test
	public void testGetLayoutPreferencesByPortletPreferencesIds()
		throws Exception {

		addLayoutPortletPreferences(
			testLayout, testPortlet,
			getPortletPreferencesXML(_NAME, _SINGLE_VALUE));

		PortletPreferencesIds portletPreferencesIds = new PortletPreferencesIds(
			testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
			testPortlet.getPortletId());

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				portletPreferencesIds);

		assertValues(jxPortletPreferences, _NAME, _SINGLE_VALUE);
		assertOwner(testLayout, (PortletPreferencesImpl)jxPortletPreferences);
	}

	@Test
	public void testGetLayoutPrivatePortletPreferences() throws Exception {
		Layout layout = _createNewLayout(true);

		addGroupPortletPreferences(layout, testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				layout.getCompanyId(), layout.getGroupId(), layout.getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_GROUP, testPortlet.getPortletId(),
				true);

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());
	}

	@Test
	public void testGetNotLayoutPrivatePortletPreferences() throws Exception {
		Layout layout = _createNewLayout(false);

		addGroupPortletPreferences(layout, testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				layout.getCompanyId(), layout.getGroupId(), layout.getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_GROUP, testPortlet.getPortletId(),
				false);

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());
	}

	@Test
	public void testGetNotStrictPortletPreferences() throws Exception {
		replaceService();

		try {
			jakarta.portlet.PortletPreferences jxPortletPreferences =
				portletPreferencesLocalService.getStrictPreferences(
					testLayout.getCompanyId(),
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
					testPortlet.getPortletId());

			assertEmptyPortletPreferencesMap(jxPortletPreferences);
		}
		finally {
			resetService();
		}
	}

	@Test
	public void testGetOriginalGroupPortletPreferencesByCompanyIdAndGroupIdAndPortletId()
		throws Exception {

		addGroupPortletPreferences(testLayout, testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				testLayout.getCompanyId(), testLayout.getGroupId(),
				testLayout.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testPortlet.getPortletId(), false);

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());

		addGroupPortletPreferences(_createNewLayout(false), testPortlet);

		portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				testLayout.getCompanyId(), testLayout.getGroupId(),
				testLayout.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testPortlet.getPortletId(), false);

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());
	}

	@Test
	public void testGetOriginalGroupPortletPreferencesByOwnerAndPlid() {
		addGroupPortletPreferences(testLayout, testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testLayout.getPlid());

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());

		addGroupPortletPreferences(
			testLayout,
			portletLocalService.getPortletById(
				testLayout.getCompanyId(), String.valueOf(_PORTLET_ID + 1)));

		portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testLayout.getPlid());

		Assert.assertEquals(
			portletPreferencesList.toString(), 2,
			portletPreferencesList.size());
	}

	@Test
	public void testGetOriginalGroupPortletPreferencesByOwnerAndPlidAndPortletId()
		throws Exception {

		addGroupPortletPreferences(testLayout, testPortlet);

		PortletPreferences portletPreferences =
			portletPreferencesLocalService.getPortletPreferences(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testLayout.getPlid(), testPortlet.getPortletId());

		Assert.assertEquals(
			portletPreferences.getPortletId(), testPortlet.getPortletId());

		Layout layout = _createNewLayout(false);

		Portlet portlet1 = portletLocalService.getPortletById(
			layout.getCompanyId(), String.valueOf(_PORTLET_ID + 1));

		addGroupPortletPreferences(layout, portlet1);

		Portlet portlet2 = portletLocalService.getPortletById(
			layout.getCompanyId(), String.valueOf(_PORTLET_ID + 2));

		addGroupPortletPreferences(layout, portlet2);

		portletPreferences =
			portletPreferencesLocalService.getPortletPreferences(
				testGroup.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
				testLayout.getPlid(), testPortlet.getPortletId());

		Assert.assertEquals(
			portletPreferences.getPortletId(), testPortlet.getPortletId());
	}

	@Test
	public void testGetOriginalGroupPortletPreferencesByPlidAndPortletId()
		throws Exception {

		addGroupPortletPreferences(testLayout, testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_TYPE_GROUP, testLayout.getPlid(),
				testPortlet.getPortletId());

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());

		addGroupPortletPreferences(_createNewLayout(false), testPortlet);

		portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_TYPE_GROUP, testLayout.getPlid(),
				testPortlet.getPortletId());

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());
	}

	@Test
	public void testGetOriginalLayoutPortletPreferencesByPlidAndPortletId()
		throws Exception {

		addLayoutPortletPreferences(testLayout, testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId());

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());

		PortletPreferencesImpl portletPreferencesImpl =
			_toPortletPreferencesImpl(portletPreferencesList.get(0));

		assertOwner(testLayout, portletPreferencesImpl);

		addLayoutPortletPreferences(_createNewLayout(false), testPortlet);

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());
	}

	@Test
	public void testGetOriginalLayoutPortletPreferencesCountByPlidAndPortletId()
		throws Exception {

		Assert.assertEquals(
			0,
			portletPreferencesLocalService.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId()));

		addLayoutPortletPreferences(testLayout, testPortlet);

		portletLocalService.deployPortlet(testPortlet);

		Assert.assertEquals(
			1,
			portletPreferencesLocalService.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId()));

		addGroupPortletPreferences(_createNewLayout(false), testPortlet);

		Assert.assertEquals(
			1,
			portletPreferencesLocalService.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId()));
	}

	@Test
	public void testGetOriginalLayoutPortletPreferencesCountByPortletId()
		throws Exception {

		Assert.assertEquals(
			0,
			portletPreferencesLocalService.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				testPortlet.getPortletId()));

		addLayoutPortletPreferences(testLayout, testPortlet);

		addLayoutPortletPreferences(
			LayoutTestUtil.addTypePortletLayout(testGroup), testPortlet);

		Assert.assertEquals(
			2,
			portletPreferencesLocalService.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				testPortlet.getPortletId()));

		addGroupPortletPreferences(testLayout, testPortlet);

		Assert.assertEquals(
			2,
			portletPreferencesLocalService.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				testPortlet.getPortletId()));
	}

	@Test
	public void testGetPortletPreferencesByPlid() throws Exception {
		addLayoutPortletPreferences(testLayout, testPortlet);

		Layout layout = LayoutTestUtil.addTypePortletLayout(testGroup);

		Portlet portlet1 = portletLocalService.getPortletById(
			layout.getCompanyId(), String.valueOf(_PORTLET_ID + 1));

		addLayoutPortletPreferences(layout, portlet1);

		Portlet portlet2 = portletLocalService.getPortletById(
			layout.getCompanyId(), String.valueOf(_PORTLET_ID + 2));

		addLayoutPortletPreferences(layout, portlet2);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferencesByPlid(
				layout.getPlid());

		Assert.assertEquals(
			portletPreferencesList.toString(), 2,
			portletPreferencesList.size());
	}

	@Test
	public void testGetPortletPreferencesByPlidAndPortletId() throws Exception {
		addLayoutPortletPreferences(testLayout, testPortlet);

		addLayoutPortletPreferences(
			LayoutTestUtil.addTypePortletLayout(testGroup), testPortlet);

		List<PortletPreferences> portletPreferencesList =
			portletPreferencesLocalService.getPortletPreferences(
				testLayout.getPlid(), testPortlet.getPortletId());

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());

		PortletPreferences portletPreferences = portletPreferencesList.get(0);

		Assert.assertEquals(testLayout.getPlid(), portletPreferences.getPlid());
		Assert.assertEquals(
			testPortlet.getPortletId(), portletPreferences.getPortletId());
	}

	@Test
	public void testGetStrictPreferences() {
		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getStrictPreferences(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId());

		assertStrictPortletPreferences(jxPortletPreferences);
	}

	@Test
	public void testGetStrictPreferencesByPortletPreferencesIds() {
		PortletPreferencesIds portletPreferencesIds = new PortletPreferencesIds(
			testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
			testPortlet.getPortletId());

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getStrictPreferences(
				portletPreferencesIds);

		assertStrictPortletPreferences(jxPortletPreferences);
	}

	@Test
	public void testUpdatePortletPreferences() throws Exception {
		String singleValuePortletPreferencesXML = getPortletPreferencesXML(
			_NAME, _SINGLE_VALUE);

		PortletPreferences portletPreferences = addLayoutPortletPreferences(
			testLayout, testPortlet, singleValuePortletPreferencesXML);

		String multipleValuesPortletPreferencesAsXML = getPortletPreferencesXML(
			_NAME, _MULTIPLE_VALUES);

		portletPreferencesLocalService.updatePreferences(
			portletPreferences.getOwnerId(), portletPreferences.getOwnerType(),
			portletPreferences.getPlid(), portletPreferences.getPortletId(),
			multipleValuesPortletPreferencesAsXML);

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId());

		assertValues(jxPortletPreferences, _NAME, _MULTIPLE_VALUES);
	}

	@Test
	public void testUpdatePreferences() throws Exception {
		String singleValuePortletPreferencesXML = getPortletPreferencesXML(
			_NAME, _SINGLE_VALUE);

		addLayoutPortletPreferences(
			testLayout, testPortlet, singleValuePortletPreferencesXML);

		String multipleValuesPortletPreferencesAsXML = getPortletPreferencesXML(
			_NAME, _MULTIPLE_VALUES);

		portletPreferencesLocalService.updatePreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
			testPortlet.getPortletId(), multipleValuesPortletPreferencesAsXML);

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId());

		assertValues(jxPortletPreferences, _NAME, _MULTIPLE_VALUES);
	}

	@Test
	public void testUpdatePreferencesAutoAdd() {
		portletPreferencesLocalService.updatePreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
			testPortlet.getPortletId(),
			getPortletPreferencesXML(_NAME, _SINGLE_VALUE));

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId());

		assertValues(jxPortletPreferences, _NAME, _SINGLE_VALUE);
	}

	@Test
	public void testUpdatePreferencesByJxPortletPreferences() throws Exception {
		String singleValuePortletPreferences = getPortletPreferencesXML(
			_NAME, _SINGLE_VALUE);

		addLayoutPortletPreferences(
			testLayout, testPortlet, singleValuePortletPreferences);

		String multipleValuesPortletPreferencesXML = getPortletPreferencesXML(
			_NAME, _MULTIPLE_VALUES);

		jakarta.portlet.PortletPreferences initialJxPortletPreferences =
			portletPreferencesFactory.fromXML(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId(),
				multipleValuesPortletPreferencesXML);

		portletPreferencesLocalService.updatePreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
			testPortlet.getPortletId(), initialJxPortletPreferences);

		jakarta.portlet.PortletPreferences currentJxPortletPreferences =
			portletPreferencesLocalService.getPreferences(
				testLayout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, testLayout.getPlid(),
				testPortlet.getPortletId());

		assertValues(currentJxPortletPreferences, _NAME, _MULTIPLE_VALUES);
	}

	protected void assertEmptyPortletPreferencesMap(
		jakarta.portlet.PortletPreferences jxPortletPreferences) {

		PortletPreferencesImpl portletPreferencesImpl =
			(PortletPreferencesImpl)jxPortletPreferences;

		Map<String, String[]> portletPreferencesMap =
			portletPreferencesImpl.getMap();

		Assert.assertTrue(
			portletPreferencesMap.toString(), portletPreferencesMap.isEmpty());
	}

	protected void assertOwner(
		Group group, PortletPreferencesImpl portletPreferencesImpl) {

		Assert.assertEquals(
			group.getGroupId(), portletPreferencesImpl.getOwnerId());
		Assert.assertEquals(
			PortletKeys.PREFS_OWNER_TYPE_GROUP,
			portletPreferencesImpl.getOwnerType());
	}

	protected void assertOwner(
		Layout layout, PortletPreferencesImpl portletPreferencesImpl) {

		Assert.assertEquals(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			portletPreferencesImpl.getOwnerId());
		Assert.assertEquals(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
			portletPreferencesImpl.getOwnerType());
		Assert.assertEquals(layout.getPlid(), portletPreferencesImpl.getPlid());
	}

	protected void assertStrictPortletPreferences(
		jakarta.portlet.PortletPreferences jxPortletPreferences) {

		StrictPortletPreferencesImpl strictPortletPreferencesImpl =
			(StrictPortletPreferencesImpl)jxPortletPreferences;

		Map<String, String[]> strictPortletPreferencesMap =
			strictPortletPreferencesImpl.getMap();

		Assert.assertTrue(
			strictPortletPreferencesMap.toString(),
			strictPortletPreferencesMap.isEmpty());
	}

	protected void assertValues(
			PortletPreferences portletPreferences, String name, String[] values)
		throws Exception {

		PortletPreferencesImpl portletPreferencesImpl =
			_toPortletPreferencesImpl(portletPreferences);

		assertValues(portletPreferencesImpl, name, values);
	}

	protected void assertValues(
		jakarta.portlet.PortletPreferences jxPortletPreferences, String name,
		String[] values) {

		PortletPreferencesImpl portletPreferencesImpl =
			(PortletPreferencesImpl)jxPortletPreferences;

		Map<String, String[]> portletPreferencesMap =
			portletPreferencesImpl.getMap();

		Assert.assertFalse(
			portletPreferencesMap.toString(), portletPreferencesMap.isEmpty());
		Assert.assertArrayEquals(values, portletPreferencesMap.get(name));
	}

	@Override
	protected String getPortletId() {
		return String.valueOf(_PORTLET_ID);
	}

	protected void replaceService() {
		AopInvocationHandler aopInvocationHandler =
			ProxyUtil.fetchInvocationHandler(
				portletPreferencesLocalService, AopInvocationHandler.class);

		Object previousService = aopInvocationHandler.getTarget();

		ServiceWrapper<PortletPreferencesLocalService> serviceWrapper =
			new TestPortletPreferencesLocalServiceWrapper(
				(PortletPreferencesLocalService)previousService);

		_serviceBag = new ServiceBag<>(
			aopInvocationHandler, PortletPreferencesLocalService.class,
			serviceWrapper, null, null);
	}

	protected void resetService() throws Exception {
		_serviceBag.replace();
	}

	private Layout _createNewLayout(boolean privateLayout) throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return LayoutTestUtil.addTypePortletLayout(group, privateLayout);
	}

	private PortletPreferencesImpl _toPortletPreferencesImpl(
			PortletPreferences portletPreferences)
		throws Exception {

		return (PortletPreferencesImpl)
			portletPreferencesLocalService.getPreferences(
				TestPropsValues.getCompanyId(), portletPreferences.getOwnerId(),
				portletPreferences.getOwnerType(), portletPreferences.getPlid(),
				portletPreferences.getPortletId());
	}

	private static final String[] _MULTIPLE_VALUES = {"value1", "value2"};

	private static final String _NAME = "name";

	private static final int _PORTLET_ID = 1000;

	private static final String[] _SINGLE_VALUE = {"value"};

	private static final PortletApp _portletApp = new PortletAppImpl(
		StringPool.CONTENT);

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	private ServiceBag<PortletPreferencesLocalService> _serviceBag;

	private static class TestPortletPreferencesLocalServiceWrapper
		extends PortletPreferencesLocalServiceWrapper {

		@Override
		public jakarta.portlet.PortletPreferences getStrictPreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId) {

			ClassLoaderBeanHandler classLoaderBeanHandler =
				(ClassLoaderBeanHandler)ProxyUtil.getInvocationHandler(
					getWrappedService());

			PortletPreferencesLocalService portletPreferencesLocalService =
				(PortletPreferencesLocalService)
					classLoaderBeanHandler.getBean();

			return portletPreferencesLocalService.getPreferences(
				companyId, ownerId, ownerType, plid, portletId);
		}

		private TestPortletPreferencesLocalServiceWrapper(
			PortletPreferencesLocalService portletPreferencesLocalService) {

			super(portletPreferencesLocalService);
		}

	}

}