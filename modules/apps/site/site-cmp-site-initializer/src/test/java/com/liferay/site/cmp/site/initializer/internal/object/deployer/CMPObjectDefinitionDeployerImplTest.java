/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.object.deployer;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.cmp.site.initializer.internal.util.SiteInitializerUtil;
import com.liferay.site.initializer.SiteInitializer;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Brian I. Kim
 */
public class CMPObjectDefinitionDeployerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDeployWhenObjectDefinitionIsCMP() {
		CMPObjectDefinitionDeployerImpl cmpObjectDefinitionDeployerImpl =
			new CMPObjectDefinitionDeployerImpl();

		SiteInitializer cmpSiteInitializer = Mockito.mock(
			SiteInitializer.class);
		SiteInitializer cmsSiteInitializer = Mockito.mock(
			SiteInitializer.class);

		ReflectionTestUtil.setFieldValue(
			cmpObjectDefinitionDeployerImpl, "_cmpSiteInitializer",
			cmpSiteInitializer);
		ReflectionTestUtil.setFieldValue(
			cmpObjectDefinitionDeployerImpl, "_cmsSiteInitializer",
			cmsSiteInitializer);

		long companyId = RandomTestUtil.randomLong();

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.isCMP()
		).thenReturn(
			true
		);

		Mockito.when(
			objectDefinition.getCompanyId()
		).thenReturn(
			companyId
		);

		try (MockedStatic<SiteInitializerUtil> siteInitializerUtilMockedStatic =
				Mockito.mockStatic(SiteInitializerUtil.class)) {

			List<ServiceRegistration<?>> serviceRegistrations =
				cmpObjectDefinitionDeployerImpl.deploy(objectDefinition);

			Assert.assertTrue(serviceRegistrations.isEmpty());

			siteInitializerUtilMockedStatic.verify(
				() -> SiteInitializerUtil.initialize(
					cmpSiteInitializer, cmsSiteInitializer, companyId));
		}
	}

	@Test
	public void testDeployWhenObjectDefinitionIsCMS() {
		CMPObjectDefinitionDeployerImpl cmpObjectDefinitionDeployerImpl =
			new CMPObjectDefinitionDeployerImpl();

		SiteInitializer cmpSiteInitializer = Mockito.mock(
			SiteInitializer.class);
		SiteInitializer cmsSiteInitializer = Mockito.mock(
			SiteInitializer.class);

		ReflectionTestUtil.setFieldValue(
			cmpObjectDefinitionDeployerImpl, "_cmpSiteInitializer",
			cmpSiteInitializer);
		ReflectionTestUtil.setFieldValue(
			cmpObjectDefinitionDeployerImpl, "_cmsSiteInitializer",
			cmsSiteInitializer);
		ReflectionTestUtil.setFieldValue(
			cmpObjectDefinitionDeployerImpl, "_bundleContext",
			Mockito.mock(BundleContext.class));
		ReflectionTestUtil.setFieldValue(
			cmpObjectDefinitionDeployerImpl, "_filterFactory",
			Mockito.mock(FilterFactory.class));
		ReflectionTestUtil.setFieldValue(
			cmpObjectDefinitionDeployerImpl, "_groupLocalService",
			Mockito.mock(GroupLocalService.class));
		ReflectionTestUtil.setFieldValue(
			cmpObjectDefinitionDeployerImpl, "_objectDefinitionLocalService",
			Mockito.mock(ObjectDefinitionLocalService.class));
		ReflectionTestUtil.setFieldValue(
			cmpObjectDefinitionDeployerImpl, "_objectEntryFolderLocalService",
			Mockito.mock(ObjectEntryFolderLocalService.class));
		ReflectionTestUtil.setFieldValue(
			cmpObjectDefinitionDeployerImpl, "_objectEntryLocalService",
			Mockito.mock(ObjectEntryLocalService.class));

		long companyId = RandomTestUtil.randomLong();

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.isCMS()
		).thenReturn(
			true
		);

		Mockito.when(
			objectDefinition.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			objectDefinition.getClassName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		try (MockedStatic<SiteInitializerUtil> siteInitializerUtilMockedStatic =
				Mockito.mockStatic(SiteInitializerUtil.class)) {

			cmpObjectDefinitionDeployerImpl.deploy(objectDefinition);

			siteInitializerUtilMockedStatic.verify(
				() -> SiteInitializerUtil.initialize(
					cmpSiteInitializer, cmsSiteInitializer, companyId));
		}
	}

	@Test
	public void testDeployWhenObjectDefinitionIsNotCMS() {
		CMPObjectDefinitionDeployerImpl cmpObjectDefinitionDeployerImpl =
			new CMPObjectDefinitionDeployerImpl();

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.isCMS()
		).thenReturn(
			false
		);

		try (MockedStatic<SiteInitializerUtil> siteInitializerUtilMockedStatic =
				Mockito.mockStatic(SiteInitializerUtil.class)) {

			List<ServiceRegistration<?>> serviceRegistrations =
				cmpObjectDefinitionDeployerImpl.deploy(objectDefinition);

			Assert.assertTrue(serviceRegistrations.isEmpty());

			siteInitializerUtilMockedStatic.verifyNoInteractions();
		}
	}

}