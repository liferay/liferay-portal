/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.item.selector.web.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.item.selector.DDMTemplateItemSelectorCriterion;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;

/**
 * @author Ankita Malik
 */
@RunWith(Arquillian.class)
public class DDMTemplateItemSelectorViewDescriptorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		_originalName = PrincipalThreadLocal.getName();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		PrincipalThreadLocal.setName(TestPropsValues.getUserId());
	}

	@After
	public void tearDown() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testSearchContainerExcludesCrossSiteTemplatesForAssetLibraryStructure()
		throws Exception {

		Group group1 = GroupTestUtil.addGroup();
		Group group2 = GroupTestUtil.addGroup();

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), group1.getGroupId());
		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), group2.getGroupId());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			depotEntry.getGroupId(), JournalArticle.class.getName());

		DDMTemplate group1Template = DDMTemplateTestUtil.addTemplate(
			group1.getGroupId(), ddmStructure.getStructureId(),
			ddmStructure.getClassNameId(), TemplateConstants.LANG_TYPE_FTL,
			_SCRIPT, LocaleUtil.getSiteDefault());
		DDMTemplate group2Template = DDMTemplateTestUtil.addTemplate(
			group2.getGroupId(), ddmStructure.getStructureId(),
			ddmStructure.getClassNameId(), TemplateConstants.LANG_TYPE_FTL,
			_SCRIPT, LocaleUtil.getSiteDefault());
		DDMTemplate depotTemplate = DDMTemplateTestUtil.addTemplate(
			depotEntry.getGroupId(), ddmStructure.getStructureId(),
			ddmStructure.getClassNameId(), TemplateConstants.LANG_TYPE_FTL,
			_SCRIPT, LocaleUtil.getSiteDefault());

		DDMTemplateItemSelectorCriterion ddmTemplateItemSelectorCriterion =
			new DDMTemplateItemSelectorCriterion();

		ddmTemplateItemSelectorCriterion.setClassNameId(
			PortalUtil.getClassNameId(JournalArticle.class.getName()));
		ddmTemplateItemSelectorCriterion.setDDMStructureId(
			ddmStructure.getStructureId());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setScopeGroupId(group1.getGroupId());
		themeDisplay.setSiteGroupId(group1.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(mockLiferayResourceRequest);

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST, mockLiferayResourceRequest);
		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		Bundle bundle = BundleUtil.getBundle(
			SystemBundleUtil.getBundleContext(),
			"com.liferay.dynamic.data.mapping.item.selector.web");

		Class<?> clazz = bundle.loadClass(
			"com.liferay.dynamic.data.mapping.item.selector.web.internal." +
				"DDMTemplateItemSelectorViewDescriptor");

		Constructor<?> constructor = clazz.getConstructor(
			DDMStructureLocalService.class,
			DDMTemplateItemSelectorCriterion.class, HttpServletRequest.class,
			PortletURL.class);

		Object ddmTemplateItemSelectorViewDescriptor = constructor.newInstance(
			_ddmStructureLocalService, ddmTemplateItemSelectorCriterion,
			httpServletRequest, new MockLiferayPortletURL());

		SearchContainer<DDMTemplate> searchContainer =
			ReflectionTestUtil.invoke(
				ddmTemplateItemSelectorViewDescriptor, "getSearchContainer",
				new Class<?>[0]);

		List<DDMTemplate> ddmTemplates = searchContainer.getResults();

		Assert.assertTrue(
			ddmTemplates.toString(), ddmTemplates.contains(group1Template));
		Assert.assertTrue(
			ddmTemplates.toString(), ddmTemplates.contains(depotTemplate));
		Assert.assertFalse(
			ddmTemplates.toString(), ddmTemplates.contains(group2Template));
	}

	private static final String _SCRIPT = "${variable}";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

}