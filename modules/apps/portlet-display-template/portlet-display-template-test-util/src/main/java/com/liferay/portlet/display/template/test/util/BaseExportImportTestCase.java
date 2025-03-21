/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.test.util;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.exportimport.test.util.lar.BasePortletExportImportTestCase;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.display.template.PortletDisplayTemplate;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lourdes Fernández Besada
 */
public abstract class BaseExportImportTestCase
	extends BasePortletExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	@TestInfo("LPD-33733")
	public void testExportImportDisplayStyleFromDifferentGroup()
		throws Exception {

		super.testExportImportDisplayStyleFromDifferentGroup();

		long classNameId = PortalUtil.getClassNameId(
			getClassName(group.getCompanyId()));

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			group.getGroupId(), classNameId, 0,
			PortalUtil.getClassNameId(PortletDisplayTemplate.class.getName()),
			TemplateConstants.LANG_TYPE_FTL, RandomTestUtil.randomString(),
			PortalUtil.getSiteDefaultLocale(group));

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"displayStyle",
				new String[] {
					PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
						ddmTemplate.getTemplateKey()
				}
			).build());

		Assert.assertNotNull(
			DDMTemplateLocalServiceUtil.getTemplate(
				importedGroup.getGroupId(), classNameId,
				ddmTemplate.getTemplateKey()));

		Assert.assertEquals(
			PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
				ddmTemplate.getTemplateKey(),
			portletPreferences.getValue("displayStyle", null));
		Assert.assertNull(
			portletPreferences.getValue(
				"displayStyleGroupExternalReferenceCode", null));
	}

	@Override
	@Test
	public void testExportImportDisplayStyleFromGlobalScope() throws Exception {
		super.testExportImportDisplayStyleFromGlobalScope();

		Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
			TestPropsValues.getCompanyId());

		long classNameId = PortalUtil.getClassNameId(
			getClassName(group.getCompanyId()));

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			companyGroup.getGroupId(), classNameId, 0,
			PortalUtil.getClassNameId(PortletDisplayTemplate.class.getName()),
			TemplateConstants.LANG_TYPE_FTL, RandomTestUtil.randomString(),
			PortalUtil.getSiteDefaultLocale(companyGroup));

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"displayStyle",
				new String[] {
					PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
						ddmTemplate.getTemplateKey()
				}
			).put(
				"displayStyleGroupExternalReferenceCode",
				new String[] {companyGroup.getExternalReferenceCode()}
			).build());

		Assert.assertNull(
			DDMTemplateLocalServiceUtil.fetchTemplate(
				importedGroup.getGroupId(), classNameId,
				ddmTemplate.getTemplateKey()));

		Assert.assertEquals(
			PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
				ddmTemplate.getTemplateKey(),
			portletPreferences.getValue("displayStyle", null));
		Assert.assertEquals(
			companyGroup.getExternalReferenceCode(),
			portletPreferences.getValue(
				"displayStyleGroupExternalReferenceCode", null));
	}

	protected String getClassName(long companyId) throws Exception {
		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			companyId, getPortletId());

		TemplateHandler templateHandler = portlet.getTemplateHandlerInstance();

		if (templateHandler == null) {
			throw new UnsupportedOperationException();
		}

		return templateHandler.getClassName();
	}

	@Override
	protected void testExportImportDisplayStyle(
			long displayStyleGroupId, String scopeType)
		throws Exception {

		Group displayStyleGroup = GroupLocalServiceUtil.getGroup(
			displayStyleGroupId);

		long classNameId = PortalUtil.getClassNameId(
			getClassName(group.getCompanyId()));

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			displayStyleGroup.getGroupId(), classNameId, 0,
			PortalUtil.getClassNameId(PortletDisplayTemplate.class.getName()),
			TemplateConstants.LANG_TYPE_FTL, RandomTestUtil.randomString(),
			PortalUtil.getSiteDefaultLocale(displayStyleGroup));

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"displayStyle",
				new String[] {
					PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
						ddmTemplate.getTemplateKey()
				}
			).put(
				"displayStyleGroupExternalReferenceCode",
				() -> {
					if (displayStyleGroup.getGroupId() == layout.getGroupId()) {
						return null;
					}

					return new String[] {
						displayStyleGroup.getExternalReferenceCode()
					};
				}
			).build());

		Assert.assertEquals(
			PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
				ddmTemplate.getTemplateKey(),
			portletPreferences.getValue("displayStyle", null));

		DDMTemplate importedDDMTemplate =
			DDMTemplateLocalServiceUtil.fetchTemplate(
				layout.getGroupId(), classNameId, ddmTemplate.getTemplateKey());

		String importedDisplayStyleGroupExternalReferenceCode =
			portletPreferences.getValue(
				"displayStyleGroupExternalReferenceCode", null);

		if (displayStyleGroup.getGroupId() != layout.getGroupId()) {
			Assert.assertNull(importedDDMTemplate);

			Assert.assertEquals(
				displayStyleGroup.getExternalReferenceCode(),
				importedDisplayStyleGroupExternalReferenceCode);
		}
		else {
			Assert.assertNotNull(importedDDMTemplate);
			Assert.assertNull(importedDisplayStyleGroupExternalReferenceCode);
		}
	}

}