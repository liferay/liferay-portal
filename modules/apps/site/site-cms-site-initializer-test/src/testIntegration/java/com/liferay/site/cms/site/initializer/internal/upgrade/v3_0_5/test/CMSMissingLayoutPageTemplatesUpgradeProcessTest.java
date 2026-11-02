/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_5.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jan Brychta
 */
@RunWith(Arquillian.class)
public class CMSMissingLayoutPageTemplatesUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany(true);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyLocalService.deleteCompany(_company);
	}

	@Test
	public void testUpgrade() throws Exception {
		Group group = _groupLocalService.getGroup(
			_company.getCompanyId(), GroupConstants.CMS);

		Layout preexistingLayout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutPageTemplateEntry
			existingContentEditorMasterLayoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						group.getGroupId(), "content-editor-master");

		if (existingContentEditorMasterLayoutPageTemplateEntry != null) {
			_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
				existingContentEditorMasterLayoutPageTemplateEntry);
		}

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				group.getGroupId(), "content-editor-master"));

		LayoutPageTemplateEntry displayPageLayoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				group.getGroupId(),
				_portal.getClassNameId(RandomTestUtil.randomString()), null,
				true, WorkflowConstants.STATUS_APPROVED);

		Layout layout = _layoutLocalService.getLayout(
			displayPageLayoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertTrue(
			Validator.isNull(
				draftLayout.getMasterLayoutPageTemplateEntryERC()));

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
				_company.getCompanyId())) {

			upgradeProcess.upgrade();
		}

		Assert.assertNotNull(
			_layoutLocalService.fetchLayout(preexistingLayout.getPlid()));

		LayoutPageTemplateEntry contentEditorMasterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				group.getGroupId(), "content-editor-master");

		Assert.assertNotNull(contentEditorMasterLayoutPageTemplateEntry);

		Layout updatedDraftLayout = _layoutLocalService.fetchLayout(
			draftLayout.getPlid());

		Assert.assertEquals(
			contentEditorMasterLayoutPageTemplateEntry.
				getExternalReferenceCode(),
			updatedDraftLayout.getMasterLayoutPageTemplateEntryERC());
	}

	private static final String _CLASS_NAME =
		"com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_5." +
			"CMSMissingLayoutPageTemplatesUpgradeProcess";

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.upgrade.registry.SiteCMSSiteInitializerUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}