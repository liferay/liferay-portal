/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Joao Victor Alves
 */
@RunWith(Arquillian.class)
public class CProductModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_group.getGroupId());

		CProduct cProduct = cpDefinition.getCProduct();

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
				null, TestPropsValues.getUserId(),
				siteNavigationMenu.getGroupId(),
				siteNavigationMenu.getSiteNavigationMenuId(), 0,
				CPDefinition.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", cpDefinition.getModelClassName()
				).put(
					"classNameId",
					_classNameLocalService.getClassNameId(
						CPDefinition.class.getName())
				).put(
					"classPK", cpDefinition.getCPDefinitionId()
				).put(
					"externalReferenceCode", cProduct.getExternalReferenceCode()
				).put(
					"title", cpDefinition.getName()
				).put(
					"type", "Product"
				).buildString(),
				ServiceContextTestUtil.getServiceContext(
					siteNavigationMenu.getGroupId()));

		String newExternalReferenceCode = RandomTestUtil.randomString();

		cProduct = _cProductLocalService.updateCProductExternalReferenceCode(
			newExternalReferenceCode, cProduct.getCProductId());

		siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItem(
				siteNavigationMenuItem.getSiteNavigationMenuItemId());

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.fastLoad(
			siteNavigationMenuItem.getTypeSettings()
		).build();

		Assert.assertEquals(
			newExternalReferenceCode, cProduct.getExternalReferenceCode());

		Assert.assertEquals(
			unicodeProperties.getProperty("externalReferenceCode"),
			cProduct.getExternalReferenceCode());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CProductLocalService _cProductLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}