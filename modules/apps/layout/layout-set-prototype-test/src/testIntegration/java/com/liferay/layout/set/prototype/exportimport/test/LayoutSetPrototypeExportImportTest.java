/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.test.util.lar.BasePortletExportImportTestCase;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.set.prototype.constants.LayoutSetPrototypePortletKeys;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.sites.kernel.util.Sites;

import java.io.File;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class LayoutSetPrototypeExportImportTest
	extends BasePortletExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	public String getNamespace() {
		return "layout_set_prototypes";
	}

	@Override
	public String getPortletId() {
		return LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE;
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	@TestInfo("LPS-98548")
	public void testDeleteLayoutSetPrototypeImportedToAnotherCompany()
		throws Exception {

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototypes();

		LayoutSetPrototype exportedLayoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		LayoutTestUtil.addTypePortletLayout(
			exportedLayoutSetPrototype.getGroup(), true);

		_sites.updateLayoutSetPrototypesLinks(
			group, exportedLayoutSetPrototype.getLayoutSetPrototypeId(), 0,
			true, false);

		User user = TestPropsValues.getUser();

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_PORTLET_LOCAL,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportPortletSettingsMap(
							user, layout.getPlid(), layout.getGroupId(),
							getPortletId(), getExportParameterMap(),
							StringPool.BLANK));

		larFile = ExportImportLocalServiceUtil.exportPortletInfoAsFile(
			exportImportConfiguration);

		Company company = CompanyTestUtil.addCompany();

		User companyAdminUser = UserTestUtil.addCompanyAdminUser(company);

		Group companyGroup = GroupTestUtil.addGroupToCompany(
			company.getCompanyId());

		Layout companyLayout = LayoutTestUtil.addTypePortletLayout(
			companyGroup);

		exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				updateExportImportConfiguration(
					companyAdminUser.getUserId(),
					exportImportConfiguration.getExportImportConfigurationId(),
					StringPool.BLANK, StringPool.BLANK,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportPortletSettingsMap(
							companyAdminUser, companyLayout.getPlid(),
							companyGroup.getGroupId(), getPortletId(),
							getImportParameterMap()),
					new ServiceContext());

		ExportImportLocalServiceUtil.importPortletInfo(
			exportImportConfiguration, larFile);

		LayoutSetPrototype importedLayoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.
				getLayoutSetPrototypeByUuidAndCompanyId(
					exportedLayoutSetPrototype.getUuid(),
					company.getCompanyId());

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
			importedLayoutSetPrototype);

		Assert.assertNull(
			LayoutSetPrototypeLocalServiceUtil.
				fetchLayoutSetPrototypeByUuidAndCompanyId(
					exportedLayoutSetPrototype.getUuid(),
					company.getCompanyId()));

		Assert.assertNotNull(
			LayoutSetPrototypeLocalServiceUtil.
				fetchLayoutSetPrototypeByUuidAndCompanyId(
					exportedLayoutSetPrototype.getUuid(),
					exportedLayoutSetPrototype.getCompanyId()));

		LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			group.getGroupId(), false);

		Assert.assertEquals(
			exportedLayoutSetPrototype.getUuid(),
			layoutSet.getLayoutSetPrototypeUuid());

		GroupLocalServiceUtil.deleteGroup(group);

		group = null;

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
			exportedLayoutSetPrototype);
	}

	@Override
	@Test
	public void testExportImportAssetLinks() throws Exception {
	}

	@Test
	public void testExportImportLayoutSetPrototype() throws Exception {
		exportImportLayoutSetPrototype(false);
	}

	@Test
	@TestInfo("LPD-83275")
	public void testExportImportLayoutSetPrototypeWithCleanImport()
		throws Exception {

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototypes();

		LayoutSetPrototype exportedLayoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		Group exportedLayoutSetPrototypeGroup =
			exportedLayoutSetPrototype.getGroup();

		LayoutTestUtil.addTypePortletLayout(
			exportedLayoutSetPrototypeGroup, true);

		long companyId = exportedLayoutSetPrototype.getCompanyId();
		int privateLayoutsPageCount =
			exportedLayoutSetPrototypeGroup.getPrivateLayoutsPageCount();
		String uuid = exportedLayoutSetPrototype.getUuid();

		User user = TestPropsValues.getUser();

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_PORTLET_LOCAL,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportPortletSettingsMap(
							user, layout.getPlid(), layout.getGroupId(),
							getPortletId(), getExportParameterMap(),
							StringPool.BLANK));

		File larFile = ExportImportLocalServiceUtil.exportPortletInfoAsFile(
			exportImportConfiguration);

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
			exportedLayoutSetPrototype);

		importedLayout = LayoutTestUtil.addTypePortletLayout(importedGroup);

		exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				updateExportImportConfiguration(
					user.getUserId(),
					exportImportConfiguration.getExportImportConfigurationId(),
					StringPool.BLANK, StringPool.BLANK,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportPortletSettingsMap(
							user, importedLayout.getPlid(),
							importedGroup.getGroupId(), getPortletId(),
							getImportParameterMap()),
					new ServiceContext());

		ExportImportLocalServiceUtil.importPortletInfo(
			exportImportConfiguration, larFile);

		LayoutSetPrototype importedLayoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.
				getLayoutSetPrototypeByUuidAndCompanyId(uuid, companyId);

		Group importedLayoutSetPrototypeGroup =
			importedLayoutSetPrototype.getGroup();

		Assert.assertEquals(
			privateLayoutsPageCount,
			importedLayoutSetPrototypeGroup.getPrivateLayoutsPageCount());

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
			importedLayoutSetPrototype);
	}

	@Test
	@TestInfo("LPS-86501")
	public void testExportImportLayoutSetPrototypeWithContentPageFragmentEditableValues()
		throws Exception {

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototypes();

		LayoutSetPrototype exportedLayoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		Layout contentLayout = LayoutTestUtil.addTypeContentLayout(
			exportedLayoutSetPrototype.getGroup(), true, false);

		Layout draftLayout = contentLayout.fetchDraftLayout();

		String editableId = RandomTestUtil.randomString();
		String editableValue = RandomTestUtil.randomString();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(editableId, JSONUtil.put("en_US", editableValue))
			).toString(),
			draftLayout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, contentLayout);

		long companyId = exportedLayoutSetPrototype.getCompanyId();
		String uuid = exportedLayoutSetPrototype.getUuid();

		User user = TestPropsValues.getUser();

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_PORTLET_LOCAL,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportPortletSettingsMap(
							user, layout.getPlid(), layout.getGroupId(),
							getPortletId(), getExportParameterMap(),
							StringPool.BLANK));

		File larFile = ExportImportLocalServiceUtil.exportPortletInfoAsFile(
			exportImportConfiguration);

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
			exportedLayoutSetPrototype);

		importedLayout = LayoutTestUtil.addTypePortletLayout(importedGroup);

		exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				updateExportImportConfiguration(
					user.getUserId(),
					exportImportConfiguration.getExportImportConfigurationId(),
					StringPool.BLANK, StringPool.BLANK,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportPortletSettingsMap(
							user, importedLayout.getPlid(),
							importedGroup.getGroupId(), getPortletId(),
							getImportParameterMap()),
					new ServiceContext());

		ExportImportLocalServiceUtil.importPortletInfo(
			exportImportConfiguration, larFile);

		LayoutSetPrototype importedLayoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.
				getLayoutSetPrototypeByUuidAndCompanyId(uuid, companyId);

		Group importedLayoutSetPrototypeGroup =
			importedLayoutSetPrototype.getGroup();

		Layout importedContentLayout =
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				contentLayout.getExternalReferenceCode(),
				importedLayoutSetPrototypeGroup.getGroupId());

		FragmentEntryLink importedFragmentEntryLink = _getFragmentEntryLink(
			importedContentLayout);

		JSONObject editableValuesJSONObject = JSONFactoryUtil.createJSONObject(
			importedFragmentEntryLink.getEditableValues());

		JSONObject editableProcessorJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		JSONObject editableJSONObject =
			editableProcessorJSONObject.getJSONObject(editableId);

		Assert.assertEquals(
			editableValuesJSONObject.toString(), editableValue,
			editableJSONObject.getString("en_US"));

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
			importedLayoutSetPrototype);
	}

	@Test
	public void testExportImportLayoutSetPrototypeWithLayoutPrototype()
		throws Exception {

		exportImportLayoutSetPrototype(true);
	}

	protected void exportImportLayoutSetPrototype(boolean layoutPrototype)
		throws Exception {

		// Exclude default site templates

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototypes();

		LayoutSetPrototype exportedLayoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		Group exportedLayoutSetPrototypeGroup =
			exportedLayoutSetPrototype.getGroup();

		if (layoutPrototype) {
			LayoutPrototype exportedLayoutPrototype =
				LayoutTestUtil.addLayoutPrototype(
					RandomTestUtil.randomString());

			LayoutTestUtil.addTypePortletLayout(
				exportedLayoutSetPrototypeGroup, true, exportedLayoutPrototype,
				true);
		}
		else {
			LayoutTestUtil.addTypePortletLayout(
				exportedLayoutSetPrototypeGroup, true);
		}

		exportImportPortlet(LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE);

		LayoutSetPrototype importedLayoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.
				getLayoutSetPrototypeByUuidAndCompanyId(
					exportedLayoutSetPrototype.getUuid(),
					exportedLayoutSetPrototype.getCompanyId());

		Group importedLayoutSetPrototypeGroup =
			importedLayoutSetPrototype.getGroup();

		Assert.assertEquals(
			exportedLayoutSetPrototypeGroup.getPrivateLayoutsPageCount(),
			importedLayoutSetPrototypeGroup.getPrivateLayoutsPageCount());

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
			exportedLayoutSetPrototype);
	}

	@Override
	protected Map<String, String[]> getExportParameterMap() throws Exception {
		Map<String, String[]> parameterMap = super.getExportParameterMap();

		addParameter(parameterMap, "page-templates", true);

		return parameterMap;
	}

	@Override
	protected Map<String, String[]> getImportParameterMap() throws Exception {
		Map<String, String[]> parameterMap = super.getExportParameterMap();

		addParameter(parameterMap, "page-templates", true);

		return parameterMap;
	}

	@Override
	protected void testExportImportDisplayStyle(long groupId, String scopeType)
		throws Exception {
	}

	private FragmentEntryLink _getFragmentEntryLink(Layout layout) {
		Layout draftLayout = layout.fetchDraftLayout();

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layout.getGroupId(), draftLayout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		return fragmentEntryLinks.get(0);
	}

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private Sites _sites;

}