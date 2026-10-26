/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@RunWith(Arquillian.class)
public class OrphanPortletsDisplayContextTest {

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
	@TestInfo("LPD-110311")
	public void testGetOrphanPortlets() throws Exception {
		_testGetOrphanPortletsWithMasterLayoutPortlet();
		_testGetOrphanPortletsWithOrphanPortletAndMasterLayoutPortlet();
		_testGetOrphanPortletsWithTypeContentLayout();
		_testGetOrphanPortletsWithTypePortletLayout();
		_testGetOrphanPortletsWithTypePortletLayoutAndMasterLayoutPortlet();
	}

	private void _addPortletPreferences(Layout layout, String portletId)
		throws Exception {

		_portletPreferencesLocalService.addPortletPreferences(
			layout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(), portletId,
			null, null);
	}

	private void _assertPortletPreferences(Layout layout, String portletId) {
		Assert.assertNotNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId));
	}

	private List<Portlet> _getOrphanPortlets(Layout layout) throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(layout.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(
			mockLiferayPortletRenderRequest.getHttpServletRequest());
		themeDisplay.setScopeGroupId(layout.getGroupId());
		themeDisplay.setSiteGroupId(layout.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter(
			"selPlid", String.valueOf(layout.getPlid()));

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.layout.admin.web.internal.display.context." +
					"OrphanPortletsDisplayContext"),
			"getOrphanPortlets", new Class<?>[0]);
	}

	private String _getPortletId(Layout layout) {
		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layout.getGroupId(), layout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		List<String> portletIds =
			_portletRegistry.getFragmentEntryLinkPortletIds(
				fragmentEntryLinks.get(0));

		Assert.assertEquals(portletIds.toString(), 1, portletIds.size());

		return portletIds.get(0);
	}

	private void _testGetOrphanPortletsWithMasterLayoutPortlet()
		throws Exception {

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		Layout masterLayout = _layoutLocalService.getLayout(
			masterLayoutPageTemplateEntry.getPlid());

		Layout masterDraftLayout = masterLayout.fetchDraftLayout();

		ContentLayoutTestUtil.addPortletToLayout(
			masterDraftLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		ContentLayoutTestUtil.publishLayout(masterDraftLayout, masterLayout);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_group, false, false,
			masterLayoutPageTemplateEntry.getExternalReferenceCode());

		_addPortletPreferences(layout, _getPortletId(masterLayout));

		List<Portlet> orphanPortlets = _getOrphanPortlets(layout);

		Assert.assertTrue(orphanPortlets.isEmpty());
	}

	private void _testGetOrphanPortletsWithOrphanPortletAndMasterLayoutPortlet()
		throws Exception {

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		Layout masterLayout = _layoutLocalService.getLayout(
			masterLayoutPageTemplateEntry.getPlid());

		Layout masterDraftLayout = masterLayout.fetchDraftLayout();

		ContentLayoutTestUtil.addPortletToLayout(
			masterDraftLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		ContentLayoutTestUtil.publishLayout(masterDraftLayout, masterLayout);

		Assert.assertNotEquals(
			JournalContentPortletKeys.JOURNAL_CONTENT,
			_getPortletId(masterLayout));

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_group, false, false,
			masterLayoutPageTemplateEntry.getExternalReferenceCode());

		_addPortletPreferences(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT);

		List<Portlet> orphanPortlets = _getOrphanPortlets(layout);

		Assert.assertEquals(
			orphanPortlets.toString(), 1, orphanPortlets.size());

		Portlet portlet = orphanPortlets.get(0);

		Assert.assertEquals(
			JournalContentPortletKeys.JOURNAL_CONTENT, portlet.getPortletId());
	}

	private void _testGetOrphanPortletsWithTypeContentLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addPortletToLayout(
			draftLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_assertPortletPreferences(layout, _getPortletId(layout));

		List<Portlet> orphanPortlets = _getOrphanPortlets(layout);

		Assert.assertTrue(orphanPortlets.isEmpty());
	}

	private void _testGetOrphanPortletsWithTypePortletLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		String portletId = LayoutTestUtil.addPortletToLayout(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT);

		layout = _layoutLocalService.getLayout(layout.getPlid());

		_assertPortletPreferences(layout, portletId);

		List<Portlet> orphanPortlets = _getOrphanPortlets(layout);

		Assert.assertTrue(orphanPortlets.isEmpty());
	}

	private void _testGetOrphanPortletsWithTypePortletLayoutAndMasterLayoutPortlet()
		throws Exception {

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		Layout masterLayout = _layoutLocalService.getLayout(
			masterLayoutPageTemplateEntry.getPlid());

		Layout masterDraftLayout = masterLayout.fetchDraftLayout();

		ContentLayoutTestUtil.addPortletToLayout(
			masterDraftLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		ContentLayoutTestUtil.publishLayout(masterDraftLayout, masterLayout);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		layout = _layoutLocalService.updateMasterLayoutPageTemplateEntryERC(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			masterLayoutPageTemplateEntry.getExternalReferenceCode());

		_addPortletPreferences(layout, _getPortletId(masterLayout));

		List<Portlet> orphanPortlets = _getOrphanPortlets(layout);

		Assert.assertTrue(orphanPortlets.isEmpty());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(
		filter = "component.name=com.liferay.layout.admin.web.internal.portlet.action.ViewOrphanPortletsMVCRenderCommand"
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletRegistry _portletRegistry;

}