/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.impl;

import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.persistence.FragmentCollectionPersistence;
import com.liferay.fragment.service.persistence.FragmentEntryLinkPersistence;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Javier Moral
 */
public class FragmentEntryLinkLocalServiceImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_portal = PortalUtil.getPortal();

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(Mockito.mock(Portal.class));
	}

	@AfterClass
	public static void tearDownClass() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	@Test
	@TestInfo("LPD-109403")
	public void testUpdateLatestChanges() throws Exception {
		_testUpdateLatestChangesWithJBoss();
		_testUpdateLatestChangesWithTomcat();
		_testUpdateLatestChangesWithWildfly();
	}

	private FragmentEntryProcessorContext _getFragmentEntryProcessorContext()
		throws Exception {

		FragmentEntryLinkLocalServiceImpl fragmentEntryLinkLocalServiceImpl =
			new FragmentEntryLinkLocalServiceImpl();

		FragmentEntryLinkPersistence fragmentEntryLinkPersistence =
			Mockito.mock(FragmentEntryLinkPersistence.class);
		FragmentEntryProcessorRegistry fragmentEntryProcessorRegistry =
			Mockito.mock(FragmentEntryProcessorRegistry.class);

		ReflectionTestUtil.setFieldValue(
			fragmentEntryLinkLocalServiceImpl, "_fragmentCollectionPersistence",
			Mockito.mock(FragmentCollectionPersistence.class));
		ReflectionTestUtil.setFieldValue(
			fragmentEntryLinkLocalServiceImpl,
			"_fragmentEntryLinkListenerRegistry",
			Mockito.mock(FragmentEntryLinkListenerRegistry.class));
		ReflectionTestUtil.setFieldValue(
			fragmentEntryLinkLocalServiceImpl,
			"_fragmentEntryProcessorRegistry", fragmentEntryProcessorRegistry);
		ReflectionTestUtil.setFieldValue(
			fragmentEntryLinkLocalServiceImpl, "_jsonFactory",
			new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			fragmentEntryLinkLocalServiceImpl, "_layoutLocalService",
			Mockito.mock(LayoutLocalService.class));
		ReflectionTestUtil.setFieldValue(
			fragmentEntryLinkLocalServiceImpl, "fragmentEntryLinkPersistence",
			fragmentEntryLinkPersistence);

		FragmentEntry fragmentEntry = Mockito.mock(FragmentEntry.class);

		Mockito.when(
			fragmentEntry.getHtml()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		FragmentEntryLink clonedFragmentEntryLink = Mockito.mock(
			FragmentEntryLink.class);
		FragmentEntryLink fragmentEntryLink = Mockito.mock(
			FragmentEntryLink.class);

		Mockito.when(
			fragmentEntryLink.clone()
		).thenReturn(
			clonedFragmentEntryLink
		);

		Mockito.when(
			fragmentEntryLinkPersistence.update(fragmentEntryLink)
		).thenReturn(
			fragmentEntryLink
		);

		ServiceContext serviceContext = Mockito.mock(ServiceContext.class);

		Mockito.when(
			serviceContext.getRequest()
		).thenReturn(
			Mockito.mock(HttpServletRequest.class)
		);

		Mockito.when(
			serviceContext.getResponse()
		).thenReturn(
			Mockito.mock(HttpServletResponse.class)
		);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			fragmentEntryLinkLocalServiceImpl.updateLatestChanges(
				fragmentEntry, fragmentEntryLink);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		ArgumentCaptor<FragmentEntryProcessorContext> argumentCaptor =
			ArgumentCaptor.forClass(FragmentEntryProcessorContext.class);

		Mockito.verify(
			fragmentEntryProcessorRegistry
		).processFragmentEntryLinkHTML(
			Mockito.any(JSONObject.class), Mockito.eq(clonedFragmentEntryLink),
			argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private void _testUpdateLatestChangesWithJBoss() throws Exception {
		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isJBoss
			).thenReturn(
				true
			);

			FragmentEntryProcessorContext fragmentEntryProcessorContext =
				_getFragmentEntryProcessorContext();

			Assert.assertTrue(
				fragmentEntryProcessorContext.isDisablePortletRender());
		}
	}

	private void _testUpdateLatestChangesWithTomcat() throws Exception {
		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isTomcat
			).thenReturn(
				true
			);

			FragmentEntryProcessorContext fragmentEntryProcessorContext =
				_getFragmentEntryProcessorContext();

			Assert.assertFalse(
				fragmentEntryProcessorContext.isDisablePortletRender());
		}
	}

	private void _testUpdateLatestChangesWithWildfly() throws Exception {
		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isWildfly
			).thenReturn(
				true
			);

			FragmentEntryProcessorContext fragmentEntryProcessorContext =
				_getFragmentEntryProcessorContext();

			Assert.assertTrue(
				fragmentEntryProcessorContext.isDisablePortletRender());
		}
	}

	private static Portal _portal;

}