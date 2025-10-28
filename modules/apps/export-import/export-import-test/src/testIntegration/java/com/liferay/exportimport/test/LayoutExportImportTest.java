/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.exception.LARTypeException;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.test.util.lar.BaseExportImportTestCase;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.staging.configuration.StagingConfiguration;

import java.io.InputStream;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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
public class LayoutExportImportTest extends BaseExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testDeleteMissingLayouts() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypePortletLayout(group);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(group);

		long[] layoutIds = ExportImportHelperUtil.getLayoutIds(
			_layoutLocalService.getLayouts(group.getGroupId(), false));

		exportImportLayouts(layoutIds, getImportParameterMap());

		Assert.assertEquals(
			_layoutLocalService.getLayoutsCount(group, false),
			_layoutLocalService.getLayoutsCount(importedGroup, false));

		LayoutTestUtil.addTypePortletLayout(importedGroup);

		Map<String, String[]> parameterMap = getImportParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS,
			new String[] {Boolean.TRUE.toString()});

		layoutIds = new long[] {layout1.getLayoutId()};

		exportImportLayouts(layoutIds, getImportParameterMap());

		Assert.assertEquals(
			_layoutLocalService.getLayoutsCount(group, false),
			_layoutLocalService.getLayoutsCount(importedGroup, false));

		Layout importedLayout1 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout1.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotNull(importedLayout1);

		Layout importedLayout2 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout2.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotNull(importedLayout2);
	}

	@Test
	public void testExportImportCompanyGroupInvalidLARType() throws Exception {

		// Import a layout set to a company layout set

		Group originalImportedGroup = importedGroup;
		Group originalGroup = group;

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		importedGroup = company.getGroup();

		long[] layoutIds = new long[0];

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}
		finally {
			importedGroup = originalImportedGroup;
		}

		// Import a company layout set to a layout set

		group = company.getGroup();
		importedGroup = originalGroup;

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}
		finally {
			importedGroup = originalImportedGroup;
			group = originalGroup;
		}
	}

	@Test
	public void testExportImportLayoutFromMasterLayoutPageTemplateAndDraftLayoutMappingOnImportSide()
		throws Exception {

		// This line is needed to reproduce LPD-18967

		LayoutTestUtil.addTypePortletLayout(group, true);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), group.getGroupId(), 0, null,
				"Test Master Page",
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Layout masterPageTemplateLayout = _layoutLocalService.getLayout(
			masterLayoutPageTemplateEntry.getPlid());

		Layout masterPageTemplateDraftLayout =
			masterPageTemplateLayout.fetchDraftLayout();

		Layout contentLayout = LayoutTestUtil.addTypeContentLayout(
			group, "Test Page From Master Layout Page Template");

		_fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), group.getGroupId(), null,
			RandomTestUtil.randomString(), null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				masterPageTemplateDraftLayout.getPlid()),
			masterPageTemplateDraftLayout.getPlid(), StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringUtil.replace(
				_getContent(
					"fragment_entry_link_editable_values_with_configuration." +
						"json"),
				new String[] {
					"$GROUP_ID", "$LAYOUT_ID", "$LAYOUT_UUID", "$TITLE"
				},
				new String[] {
					String.valueOf(group.getGroupId()),
					String.valueOf(contentLayout.getLayoutId()),
					contentLayout.getUuid(), contentLayout.getName("en_US")
				}),
			StringPool.BLANK, 0, StringPool.BLANK,
			FragmentConstants.TYPE_COMPONENT,
			ServiceContextTestUtil.getServiceContext());

		exportImportLayouts(
			new long[] {contentLayout.getLayoutId()}, getImportParameterMap());

		Layout importedLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			contentLayout.getUuid(), importedGroup.getGroupId(), false);

		Layout importedDraftLayout = importedLayout.fetchDraftLayout();

		Assert.assertTrue(importedDraftLayout.isDraftLayout());
		Assert.assertEquals(
			importedLayout.getName(), importedDraftLayout.getName());

		Layout importedMasterPageTemplateLayout =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				masterPageTemplateLayout.getUuid(), importedGroup.getGroupId(),
				true);

		Layout importedDraftLayoutOfMasterPageTemplate =
			importedMasterPageTemplateLayout.fetchDraftLayout();

		Assert.assertTrue(
			importedDraftLayoutOfMasterPageTemplate.isDraftLayout());
		Assert.assertEquals(
			importedMasterPageTemplateLayout.getName(),
			importedDraftLayoutOfMasterPageTemplate.getName());
	}

	@Test
	public void testExportImportLayoutPrototypeInvalidLARType()
		throws Exception {

		// Import a layout prototype to a layout set

		LayoutPrototype layoutPrototype = LayoutTestUtil.addLayoutPrototype(
			RandomTestUtil.randomString());

		group = layoutPrototype.getGroup();

		importedGroup = GroupTestUtil.addGroup();

		long[] layoutIds = new long[0];

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}

		// Import a layout prototype to a layout set pototype

		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		importedGroup = layoutSetPrototype.getGroup();

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}
		finally {
			_layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
				layoutSetPrototype);

			importedGroup = null;
		}
	}

	@Test
	public void testExportImportLayouts() throws Exception {
		LayoutTestUtil.addTypePortletLayout(group);

		exportImportLayouts(
			ExportImportHelperUtil.getLayoutIds(
				_layoutLocalService.getLayouts(group.getGroupId(), false)),
			getImportParameterMap());

		Assert.assertEquals(
			_layoutLocalService.getLayoutsCount(group, false),
			_layoutLocalService.getLayoutsCount(importedGroup, false));
	}

	@Test
	public void testExportImportLayoutSetInvalidLARType() throws Exception {

		// Import a layout set to a layout prototype

		LayoutPrototype layoutPrototype = LayoutTestUtil.addLayoutPrototype(
			RandomTestUtil.randomString());

		importedGroup = layoutPrototype.getGroup();

		long[] layoutIds = new long[0];

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}

		// Import a layout set to a layout set prototype

		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		importedGroup = layoutSetPrototype.getGroup();

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.fail();
		}
		catch (LARTypeException larTypeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(larTypeException);
			}
		}
		finally {
			_layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
				layoutSetPrototype);

			importedGroup = null;
		}
	}

	@Test
	public void testExportImportLayoutSetPrototypeInvalidLARType()
		throws Exception {

		// Import a layout set prototype to a layout set

		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		try {
			group = layoutSetPrototype.getGroup();
			importedGroup = GroupTestUtil.addGroup();

			long[] layoutIds = new long[0];

			try {
				exportImportLayouts(layoutIds, getImportParameterMap(), true);

				Assert.fail();
			}
			catch (LARTypeException larTypeException) {
				if (_log.isDebugEnabled()) {
					_log.debug(larTypeException);
				}
			}

			// Import a layout set prototype to a layout prototyope

			LayoutPrototype layoutPrototype = LayoutTestUtil.addLayoutPrototype(
				RandomTestUtil.randomString());

			importedGroup = layoutPrototype.getGroup();

			try {
				exportImportLayouts(layoutIds, getImportParameterMap(), true);

				Assert.fail();
			}
			catch (LARTypeException larTypeException) {
				if (_log.isDebugEnabled()) {
					_log.debug(larTypeException);
				}
			}
		}
		finally {
			_layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
				layoutSetPrototype);

			group = null;
		}
	}

	@Test
	public void testExportImportLayoutsInvalidAvailableLocales()
		throws Exception {

		testAvailableLocales(
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN),
			Arrays.asList(LocaleUtil.US, LocaleUtil.GERMANY), true);
	}

	@Test
	public void testExportImportLayoutsPriorities() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypePortletLayout(group);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(group);
		Layout layout3 = LayoutTestUtil.addTypePortletLayout(group);

		int priority = layout1.getPriority();

		layout1.setPriority(layout3.getPriority());

		layout3.setPriority(priority);

		layout1 = _layoutLocalService.updateLayout(layout1);
		layout3 = _layoutLocalService.updateLayout(layout3);

		long[] layoutIds = {layout1.getLayoutId(), layout2.getLayoutId()};

		exportImportLayouts(layoutIds, getImportParameterMap());

		Layout importedLayout1 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout1.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotEquals(
			layout1.getPriority(), importedLayout1.getPriority());

		Layout importedLayout2 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout2.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotEquals(
			layout2.getPriority(), importedLayout2.getPriority());

		exportImportLayouts(
			ExportImportHelperUtil.getLayoutIds(
				_layoutLocalService.getLayouts(group.getGroupId(), false)),
			getImportParameterMap());

		importedLayout1 = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout1.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			layout1.getPriority(), importedLayout1.getPriority());

		importedLayout2 = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout2.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			layout2.getPriority(), importedLayout2.getPriority());

		Layout importedLayout3 =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout3.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			layout3.getPriority(), importedLayout3.getPriority());
	}

	@Test
	public void testExportImportLayoutsValidAvailableLocales()
		throws Exception {

		testAvailableLocales(
			Arrays.asList(LocaleUtil.US, LocaleUtil.US),
			Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN, LocaleUtil.US),
			false);
	}

	@Test
	public void testExportImportSelectedLayouts() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		long[] layoutIds = {layout.getLayoutId()};

		exportImportLayouts(layoutIds, getImportParameterMap());

		Assert.assertEquals(
			layoutIds.length,
			_layoutLocalService.getLayoutsCount(importedGroup, false));

		importedLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotNull(importedLayout);
	}

	@Test
	public void testExportImportUnselectedChildLayouts() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			group, layout.getPlid());

		Map<Long, Boolean> selectedLayouts = HashMapBuilder.put(
			LayoutConstants.DEFAULT_PLID, true
		).put(
			layout.getPlid(), false
		).build();

		List<Layout> layouts = _layoutLocalService.getLayouts(
			group.getGroupId(), false);

		Map<String, String[]> exportParameterMap = getExportParameterMap();

		exportParameterMap.put(Constants.CMD, new String[] {Constants.EXPORT});

		exportLayouts(
			ExportImportHelperUtil.getLayoutIds(selectedLayouts),
			exportParameterMap);

		importLayouts(getImportParameterMap());

		Assert.assertNotEquals(
			layouts.size(),
			_layoutLocalService.getLayoutsCount(importedGroup, false));

		importedLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			childLayout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNull(importedLayout);
	}

	@Test
	public void testFriendlyURLCollision() throws Exception {
		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getDefault());

		Layout layoutA = LayoutTestUtil.addTypePortletLayout(group);

		String friendlyURLA = layoutA.getFriendlyURL();

		layoutA = _layoutLocalService.updateFriendlyURL(
			layoutA.getUserId(), layoutA.getPlid(), friendlyURLA + "-de", "de");

		Layout layoutB = LayoutTestUtil.addTypePortletLayout(group);

		String friendlyURLB = layoutB.getFriendlyURL();

		layoutB = _layoutLocalService.updateFriendlyURL(
			layoutB.getUserId(), layoutB.getPlid(), friendlyURLB + "-de", "de");

		long[] layoutIds = {layoutA.getLayoutId(), layoutB.getLayoutId()};

		exportImportLayouts(layoutIds, getImportParameterMap());

		layoutA = _layoutLocalService.updateFriendlyURL(
			layoutA.getUserId(), layoutA.getPlid(), "/temp", defaultLanguageId);

		layoutA = _layoutLocalService.updateFriendlyURL(
			layoutA.getUserId(), layoutA.getPlid(), "/temp-de", "de");

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				group.getGroupId(),
				_layoutFriendlyURLEntryHelper.getClassNameId(
					layoutA.isPrivateLayout()),
				friendlyURLA);

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			friendlyURLEntry.getFriendlyURLEntryId());

		layoutB = _layoutLocalService.updateFriendlyURL(
			layoutB.getUserId(), layoutB.getPlid(), friendlyURLA,
			defaultLanguageId);

		_layoutLocalService.updateFriendlyURL(
			layoutB.getUserId(), layoutB.getPlid(), friendlyURLA + "-de", "de");

		friendlyURLEntry = _friendlyURLEntryLocalService.fetchFriendlyURLEntry(
			group.getGroupId(),
			_layoutFriendlyURLEntryHelper.getClassNameId(
				layoutB.isPrivateLayout()),
			friendlyURLB);

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			friendlyURLEntry.getFriendlyURLEntryId());

		layoutA = _layoutLocalService.updateFriendlyURL(
			layoutA.getUserId(), layoutA.getPlid(), friendlyURLB,
			defaultLanguageId);

		_layoutLocalService.updateFriendlyURL(
			layoutA.getUserId(), layoutA.getPlid(), friendlyURLB + "-de", "de");

		exportImportLayouts(layoutIds, getImportParameterMap());

		_assertNewFriendlyURL(layoutA, friendlyURLB);
		_assertNewFriendlyURL(layoutB, friendlyURLA);
	}

	@FeatureFlag("LPS-199086")
	@Test
	public void testLayoutExportImportWithChildLayoutReferencedWithButtonAndChildLayoutHasParentLayout()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			StagingConfiguration.class, CompanyThreadLocal.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"publishParentLayoutsByDefault", true
			).build());

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		Layout parentLayout = LayoutTestUtil.addTypeContentLayout(group);

		Layout childLayout = LayoutTestUtil.addTypeContentLayout(
			group, parentLayout.getPlid());

		_fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), group.getGroupId(), null,
			RandomTestUtil.randomString(), null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()),
			layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK,
			StringUtil.replace(
				_getContent(
					"fragment_entry_link_editable_values_with_configuration." +
						"json"),
				new String[] {
					"$GROUP_ID", "$LAYOUT_ID", "$LAYOUT_UUID", "$TITLE"
				},
				new String[] {
					String.valueOf(childLayout.getGroupId()),
					String.valueOf(childLayout.getLayoutId()),
					childLayout.getUuid(), childLayout.getName("en_US")
				}),
			StringPool.BLANK, 0, StringPool.BLANK,
			FragmentConstants.TYPE_COMPONENT,
			ServiceContextTestUtil.getServiceContext());

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		Layout importedChildLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				childLayout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotNull(importedChildLayout);

		Layout importedParentLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				parentLayout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotNull(importedParentLayout);

		Layout importedLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				layout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotNull(importedLayout);
	}

	@FeatureFlag("LPS-199086")
	@Test
	@TestInfo("LPD-6808: AC9-AC10")
	public void testLayoutExportImportWithModifiedContentAndExistingParentAndChildLayoutsOnImportSide()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			StagingConfiguration.class, CompanyThreadLocal.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"publishParentLayoutsByDefault", true
			).build());

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			group, layout.getPlid());

		Map<Long, Boolean> selectedLayouts = HashMapBuilder.put(
			LayoutConstants.DEFAULT_PLID, true
		).put(
			layout.getPlid(), false
		).put(
			childLayout.getPlid(), false
		).build();

		Map<String, String[]> exportParameterMap = getExportParameterMap();

		exportParameterMap.put(Constants.CMD, new String[] {Constants.EXPORT});

		exportLayouts(
			ExportImportHelperUtil.getLayoutIds(selectedLayouts),
			exportParameterMap);

		importLayouts(exportParameterMap, false);

		Layout importedParentLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				layout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			0,
			_getLayoutPortletIds(
				importedParentLayout.getPlid()
			).size());

		Layout importedChildLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				childLayout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			0,
			_getLayoutPortletIds(
				importedChildLayout.getPlid()
			).size());

		LayoutTestUtil.addPortletToLayout(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT);
		LayoutTestUtil.addPortletToLayout(
			childLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		selectedLayouts = HashMapBuilder.put(
			LayoutConstants.DEFAULT_PLID, true
		).put(
			childLayout.getPlid(), false
		).build();

		exportLayouts(
			ExportImportHelperUtil.getLayoutIds(selectedLayouts),
			exportParameterMap);

		importLayouts(exportParameterMap, false);

		Assert.assertEquals(
			1,
			_getLayoutPortletIds(
				importedParentLayout.getPlid()
			).size());
		Assert.assertEquals(
			1,
			_getLayoutPortletIds(
				importedChildLayout.getPlid()
			).size());
	}

	@FeatureFlag("LPS-199086")
	@Test
	@TestInfo("LPD-6808: AC9-AC11")
	public void testLayoutExportImportWithModifiedContentAndNonexistentParentAndChildLayoutsOnImportSide()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			StagingConfiguration.class, CompanyThreadLocal.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"publishParentLayoutsByDefault", true
			).build());

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			group, layout.getPlid());

		Map<Long, Boolean> selectedLayouts = HashMapBuilder.put(
			LayoutConstants.DEFAULT_PLID, true
		).put(
			childLayout.getPlid(), false
		).build();

		Map<String, String[]> exportParameterMap = getExportParameterMap();

		exportParameterMap.put(Constants.CMD, new String[] {Constants.EXPORT});

		exportLayouts(
			ExportImportHelperUtil.getLayoutIds(selectedLayouts),
			exportParameterMap);

		importLayouts(exportParameterMap, false);

		Layout importedParentLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				layout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotNull(importedParentLayout);

		Layout importedChildLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				childLayout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertNotNull(importedChildLayout);
	}

	@FeatureFlag("LPS-199086")
	@Test
	@TestInfo("LPD-6808: AC12-AC13")
	public void testLayoutExportImportWithUncheckedConfigurationAndModifiedContentAndExistingParentAndChildLayoutsOnImportSide()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			StagingConfiguration.class, CompanyThreadLocal.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"publishParentLayoutsByDefault", false
			).build());

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			group, layout.getPlid());

		Map<Long, Boolean> selectedLayouts = HashMapBuilder.put(
			LayoutConstants.DEFAULT_PLID, true
		).put(
			layout.getPlid(), false
		).put(
			childLayout.getPlid(), false
		).build();

		Map<String, String[]> exportParameterMap = getExportParameterMap();

		exportParameterMap.put(Constants.CMD, new String[] {Constants.EXPORT});

		exportLayouts(
			ExportImportHelperUtil.getLayoutIds(selectedLayouts),
			exportParameterMap);

		importLayouts(exportParameterMap, false);

		Layout importedParentLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				layout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			0,
			_getLayoutPortletIds(
				importedParentLayout.getPlid()
			).size());

		Layout importedChildLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				childLayout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			0,
			_getLayoutPortletIds(
				importedChildLayout.getPlid()
			).size());

		LayoutTestUtil.addPortletToLayout(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT);
		LayoutTestUtil.addPortletToLayout(
			childLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		selectedLayouts = HashMapBuilder.put(
			LayoutConstants.DEFAULT_PLID, true
		).put(
			childLayout.getPlid(), false
		).build();

		exportLayouts(
			ExportImportHelperUtil.getLayoutIds(selectedLayouts),
			exportParameterMap);

		importLayouts(exportParameterMap, false);

		Assert.assertEquals(
			0,
			_getLayoutPortletIds(
				importedParentLayout.getPlid()
			).size());
		Assert.assertEquals(
			1,
			_getLayoutPortletIds(
				importedChildLayout.getPlid()
			).size());
	}

	@FeatureFlag("LPS-199086")
	@Test
	@TestInfo("LPD-6808: AC12-AC14")
	public void testLayoutExportImportWithUncheckedConfigurationAndModifiedContentAndNonexistentParentAndChildLayoutsOnImportSide()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			StagingConfiguration.class, CompanyThreadLocal.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"publishParentLayoutsByDefault", false
			).build());

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutTestUtil.addPortletToLayout(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			group, layout.getPlid());

		LayoutTestUtil.addPortletToLayout(
			childLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		Map<Long, Boolean> selectedLayouts = HashMapBuilder.put(
			LayoutConstants.DEFAULT_PLID, true
		).put(
			childLayout.getPlid(), false
		).build();

		Map<String, String[]> exportParameterMap = getExportParameterMap();

		exportParameterMap.put(Constants.CMD, new String[] {Constants.EXPORT});

		exportLayouts(
			ExportImportHelperUtil.getLayoutIds(selectedLayouts),
			exportParameterMap);

		try {
			importLayouts(exportParameterMap, true);
		}
		catch (PortletDataException portletDataException) {
			Assert.assertEquals(
				PortletDataException.MISSING_REFERENCE,
				portletDataException.getType());
		}
	}

	@Test
	@TestInfo("LPD-6808")
	public void testPublishParentLayoutsByDefaultConfigurationPublishParentLayoutFirstThenChildLayout()
		throws Exception {

		_testPublishParentLayoutsByDefaultConfigurationPublishParentLayoutFirstThenChildLayout(
			false);
		_testPublishParentLayoutsByDefaultConfigurationPublishParentLayoutFirstThenChildLayout(
			true);
	}

	protected void testAvailableLocales(
			Collection<Locale> sourceAvailableLocales,
			Collection<Locale> targetAvailableLocales, boolean expectFailure)
		throws Exception {

		group = GroupTestUtil.updateDisplaySettings(
			group.getGroupId(), sourceAvailableLocales, null);
		importedGroup = GroupTestUtil.updateDisplaySettings(
			importedGroup.getGroupId(), targetAvailableLocales, null);

		LayoutTestUtil.addTypePortletLayout(group);

		long[] layoutIds = new long[0];

		try {
			exportImportLayouts(layoutIds, getImportParameterMap(), true);

			Assert.assertFalse(expectFailure);
		}
		catch (LocaleException localeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(localeException);
			}

			Assert.assertTrue(expectFailure);
		}
	}

	private void _assertNewFriendlyURL(Layout layout, String friendlyURL)
		throws Exception {

		Layout importedLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			layout.getUuid(), importedGroup.getGroupId(),
			layout.isPrivateLayout());

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				importedGroup.getGroupId(),
				_layoutFriendlyURLEntryHelper.getClassNameId(
					importedLayout.isPrivateLayout()),
				friendlyURL + "-1");

		Assert.assertEquals(
			importedLayout.getPlid(), friendlyURLEntry.getClassPK());
	}

	private String _getContent(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		Scanner scanner = new Scanner(inputStream);

		scanner.useDelimiter("\\Z");

		return scanner.next();
	}

	private Set<String> _getLayoutPortletIds(long plid) {
		Set<String> layoutPortletIds = new HashSet<>();

		List<PortletPreferences> portletPreferencesList =
			PortletPreferencesLocalServiceUtil.getPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid);

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			layoutPortletIds.add(portletPreferences.getPortletId());
		}

		return layoutPortletIds;
	}

	private void
			_testPublishParentLayoutsByDefaultConfigurationPublishParentLayoutFirstThenChildLayout(
				boolean publishParentLayoutsByDefault)
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			StagingConfiguration.class, CompanyThreadLocal.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"publishParentLayoutsByDefault", publishParentLayoutsByDefault
			).build());

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutTestUtil.addPortletToLayout(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			group, layout.getPlid());

		LayoutTestUtil.addPortletToLayout(
			childLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		Map<Long, Boolean> selectedLayouts = HashMapBuilder.put(
			LayoutConstants.DEFAULT_PLID, true
		).put(
			layout.getPlid(), false
		).build();

		Map<String, String[]> exportParameterMap = getExportParameterMap();

		exportParameterMap.put(Constants.CMD, new String[] {Constants.EXPORT});

		exportLayouts(
			ExportImportHelperUtil.getLayoutIds(selectedLayouts),
			exportParameterMap);

		importLayouts(exportParameterMap, false);

		selectedLayouts = HashMapBuilder.put(
			LayoutConstants.DEFAULT_PLID, true
		).put(
			childLayout.getPlid(), false
		).build();

		exportLayouts(
			ExportImportHelperUtil.getLayoutIds(selectedLayouts),
			exportParameterMap);

		importLayouts(exportParameterMap, false);

		Layout importedParentLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				layout.getUuid(), importedGroup.getGroupId(), false);

		Layout importedChildLayout =
			LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				childLayout.getUuid(), importedGroup.getGroupId(), false);

		Assert.assertEquals(
			importedChildLayout.getParentLayoutId(),
			importedParentLayout.getLayoutId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutExportImportTest.class);

	@Inject
	private static ConfigurationProvider _configurationProvider;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}