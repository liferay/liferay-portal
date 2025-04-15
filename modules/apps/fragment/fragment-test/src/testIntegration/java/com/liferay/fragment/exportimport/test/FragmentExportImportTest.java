/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.test.util.lar.BasePortletExportImportTestCase;
import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.content.LayoutContentProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class FragmentExportImportTest extends BasePortletExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_configuration = JSONUtil.put(
			"fieldSets",
			JSONUtil.put(
				JSONUtil.put(
					"fields",
					JSONUtil.put(
						JSONUtil.put(
							"dataType", "int"
						).put(
							"defaultValue", "4"
						).put(
							"label", "number-of-slides"
						).put(
							"name", "numberOfSlides"
						).put(
							"type", "text"
						).put(
							"typeOptions",
							JSONUtil.put(
								"validation",
								JSONUtil.put(
									"max", 4
								).put(
									"min", 1
								).put(
									"type", "number"
								))
						))
				).put(
					"label", "Configuration"
				))
		).toString();
	}

	@Override
	public String getNamespace() {
		return _fragmentPortletDataHandler.getNamespace();
	}

	@Override
	public String getPortletId() {
		return FragmentPortletKeys.FRAGMENT;
	}

	@Test
	@TestInfo("LPD-40051")
	public void testEnableLocalStagingWithPropagationEnabled()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						FragmentServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"propagateChanges", true
						).build())) {

			Group group = GroupTestUtil.addGroup();

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					group, TestPropsValues.getUserId());

			FragmentEntry fragmentEntry = _addFragmentEntry(serviceContext);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.exportimport.internal.lifecycle." +
						"LoggerExportImportLifecycleListener",
					LoggerTestUtil.ERROR)) {

				_stagingLocalService.enableLocalStaging(
					TestPropsValues.getUserId(), group, false, false,
					serviceContext);

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
			}

			Group stagingGroup = group.getStagingGroup();

			FragmentEntry importedGroupFragmentEntry =
				_fragmentEntryLocalService.getFragmentEntryByUuidAndGroupId(
					fragmentEntry.getUuid(), stagingGroup.getGroupId());

			_assertContains(
				"Original HTML Fragment", importedGroupFragmentEntry.getHtml());
		}
	}

	@Override
	@Test
	public void testExportImportAssetLinks() throws Exception {
	}

	@Test
	public void testImportUpdateFragmentEntryWithPropagationEnabled()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						FragmentServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"propagateChanges", true
						).build())) {

			FragmentEntry fragmentEntry = _addFragmentEntry(
				ServiceContextTestUtil.getServiceContext(
					group, TestPropsValues.getUserId()));

			exportImportPortlet(FragmentPortletKeys.FRAGMENT, false);

			FragmentEntry importedGroupFragmentEntry =
				_fragmentEntryLocalService.getFragmentEntryByUuidAndGroupId(
					fragmentEntry.getUuid(), importedGroup.getGroupId());

			_assertContains(
				"Original HTML Fragment", importedGroupFragmentEntry.getHtml());

			Layout importedGroupLayout = LayoutTestUtil.addTypeContentLayout(
				importedGroup);

			Layout importedDraftGroupLayout =
				importedGroupLayout.fetchDraftLayout();

			Assert.assertNotNull(importedDraftGroupLayout);

			FragmentEntryLink fragmentEntryLink =
				ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
					null, importedGroupFragmentEntry.getCss(),
					importedGroupFragmentEntry.getConfiguration(),
					importedGroupFragmentEntry.getFragmentEntryId(),
					importedGroupFragmentEntry.getHtml(),
					importedGroupFragmentEntry.getJs(),
					importedDraftGroupLayout,
					importedGroupFragmentEntry.getFragmentEntryKey(),
					importedGroupFragmentEntry.getType(), null, 0,
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(
							importedDraftGroupLayout.getPlid()));

			_assertContains(
				"Original HTML Fragment", fragmentEntryLink.getHtml());

			ContentLayoutTestUtil.publishLayout(
				_layoutLocalService.getLayout(
					importedDraftGroupLayout.getPlid()),
				importedGroupLayout);

			importedGroupLayout = _layoutLocalService.getLayout(
				importedGroupLayout.getPlid());

			Locale locale = _portal.getSiteDefaultLocale(importedGroup);

			_assertContains(
				"Original HTML Fragment",
				_getLayoutContent(importedGroupLayout, locale));

			fragmentEntry = _updateFragmentEntry(fragmentEntry);

			_assertContains("Updated HTML Fragment", fragmentEntry.getHtml());

			exportImportPortlet(FragmentPortletKeys.FRAGMENT, false);

			importedGroupFragmentEntry =
				_fragmentEntryLocalService.getFragmentEntry(
					fragmentEntry.getFragmentEntryId());

			fragmentEntryLink =
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentEntryLink.getFragmentEntryLinkId());

			_assertContains(
				"Updated HTML Fragment", importedGroupFragmentEntry.getHtml(),
				fragmentEntryLink.getHtml(),
				_getLayoutContent(importedGroupLayout, locale));
		}
	}

	private FragmentEntry _addFragmentEntry(ServiceContext serviceContext)
		throws Exception {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(), RandomTestUtil.randomString(),
				StringPool.BLANK, serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			fragmentCollection.getFragmentCollectionId(), null,
			RandomTestUtil.randomString(), StringPool.BLANK,
			"Original HTML Fragment" + _HTML, StringPool.BLANK, false,
			_configuration, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void _assertContains(String text, String... strings) {
		for (String string : strings) {
			Assert.assertTrue(
				string, StringUtil.contains(string, text, StringPool.BLANK));
		}
	}

	private String _getLayoutContent(Layout layout, Locale locale)
		throws Exception {

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			return _layoutContentProvider.getLayoutContent(
				themeDisplay.getRequest(), themeDisplay.getResponse(), layout,
				locale);
		}
	}

	private FragmentEntry _updateFragmentEntry(FragmentEntry fragmentEntry)
		throws Exception {

		return _fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			fragmentEntry.getCss(), "Updated HTML Fragment" + _HTML,
			fragmentEntry.getJs(), fragmentEntry.isCacheable(),
			fragmentEntry.getConfiguration(), fragmentEntry.getIcon(),
			fragmentEntry.getPreviewFileEntryId(), fragmentEntry.isReadOnly(),
			fragmentEntry.getTypeOptions(), fragmentEntry.getStatus());
	}

	private static final String _HTML = StringBundler.concat(
		"[#list 0..configuration.numberOfSlides-1 as i]\n",
		"<div class=\"js-slide js-slide${i+1}\">\n",
		"\t<lfr-drop-zone data-lfr-drop-zone-id=\"${i+1}\" ",
		"data-lfr-priority=\"${i+1}\"></lfr-drop-zone>\n", "</div>\n[/#list]");

	private static String _configuration;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject(filter = "jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT)
	private PortletDataHandler _fragmentPortletDataHandler;

	@Inject
	private LayoutContentProvider _layoutContentProvider;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

}