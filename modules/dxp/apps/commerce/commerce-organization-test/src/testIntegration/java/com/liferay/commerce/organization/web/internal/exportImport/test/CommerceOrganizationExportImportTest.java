/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.organization.web.internal.exportImport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.organization.constants.CommerceOrganizationPortletKeys;
import com.liferay.exportimport.test.util.lar.BasePortletExportImportTestCase;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
 * @author Fabio Monaco
 */
@RunWith(Arquillian.class)
public class CommerceOrganizationExportImportTest
	extends BasePortletExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	public String getPortletId() throws Exception {
		return PortletIdCodec.encode(
			CommerceOrganizationPortletKeys.COMMERCE_ORGANIZATION,
			RandomTestUtil.randomString());
	}

	@Before
	@Override
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		super.setUp();
	}

	@Override
	@Test
	public void testExportImportAssetLinks() throws Exception {
	}

	@Test
	public void testExportImportOrganizationWithDifferentGroup()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"rootOrganizationExternalReferenceCode",
				() -> new String[] {organization.getExternalReferenceCode()}
			).build());

		Organization importedOrganization =
			_organizationLocalService.fetchOrganizationByExternalReferenceCode(
				portletPreferences.getValue(
					"rootOrganizationExternalReferenceCode", null),
				organization.getCompanyId());

		Assert.assertNotEquals(layout.getGroupId(), importedGroup.getGroupId());
		Assert.assertEquals(
			importedOrganization.getExternalReferenceCode(),
			portletPreferences.getValue(
				"rootOrganizationExternalReferenceCode", null));
	}

	@Test
	public void testExportImportOrganizationWithSameGroup() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"rootOrganizationExternalReferenceCode",
				() -> new String[] {organization.getExternalReferenceCode()}
			).build());

		Organization importedOrganization =
			_organizationLocalService.fetchOrganizationByExternalReferenceCode(
				organization.getExternalReferenceCode(),
				organization.getCompanyId());

		Assert.assertEquals(organization, importedOrganization);

		String importedRootOrganizationExternalReferenceCode =
			portletPreferences.getValue(
				"rootOrganizationExternalReferenceCode", null);

		Assert.assertNotNull(importedRootOrganizationExternalReferenceCode);

		Assert.assertEquals(
			importedOrganization.getExternalReferenceCode(),
			importedRootOrganizationExternalReferenceCode);
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}