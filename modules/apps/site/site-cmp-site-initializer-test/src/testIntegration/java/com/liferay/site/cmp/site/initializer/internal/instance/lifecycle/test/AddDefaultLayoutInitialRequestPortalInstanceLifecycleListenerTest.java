/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.instance.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class AddDefaultLayoutInitialRequestPortalInstanceLifecycleListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testPortalInstanceRegistered() throws Exception {
		Assume.assumeTrue(LicenseManagerUtil.isAppEnabled(App.CMP));

		Company company = CompanyTestUtil.addCompany(false);

		_portalInstanceLifecycleListener.portalInstanceRegistered(company);

		Group group = _groupLocalService.fetchGroup(
			company.getCompanyId(), GroupConstants.CMS);

		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/dashboard"));

		ObjectDefinition cmpObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", company.getCompanyId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchDefaultLayoutPageTemplateEntry(
					group.getGroupId(),
					_portal.getClassNameId(cmpObjectDefinition.getClassName()),
					0);

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry);

		CompanyTestUtil.resetCompanyLocales(
			company.getCompanyId(),
			ListUtil.fromArray(LocaleUtil.ITALY, LocaleUtil.US),
			LocaleUtil.ITALY);

		_portalInstanceLifecycleListener.portalInstanceRegistered(company);

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/planning");

		Assert.assertEquals(
			"Planning", layout.getName(LocaleUtil.ITALY, false));

		layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/projects");

		Assert.assertEquals(
			"Progetti", layout.getName(LocaleUtil.ITALY, false));

		layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/tasks");

		Assert.assertEquals(
			"Attività", layout.getName(LocaleUtil.ITALY, false));

		AssetCategory assetCategory =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					"L_CMP_FUNNEL_STAGE_AWARENESS", group.getGroupId());

		Assert.assertEquals(
			"Awareness", assetCategory.getTitle(LocaleUtil.ITALY, false));
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.instance.lifecycle.AddDefaultLayoutInitialRequestPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

}