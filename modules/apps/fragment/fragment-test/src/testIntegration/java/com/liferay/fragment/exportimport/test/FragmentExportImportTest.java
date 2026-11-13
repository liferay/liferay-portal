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
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
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
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
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
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Locale;
import java.util.Set;

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
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_configuration = JSONUtil.put(
			"fieldSets",
			JSONUtil.put(
				JSONUtil.put(
					"customComponentModule",
					"{SimpleInputField} from @liferay/fragment-impl/api"
				).put(
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

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());

		_fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(),
				_serviceContext.getScopeGroupId(), null,
				RandomTestUtil.randomString(), StringPool.BLANK, false,
				_serviceContext);
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

			FragmentEntry fragmentEntry = _addFragmentEntry(
				_fragmentCollection, false, _serviceContext);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.exportimport.internal.lifecycle." +
						"LoggerExportImportLifecycleListener",
					LoggerTestUtil.ERROR)) {

				_stagingLocalService.enableLocalStaging(
					TestPropsValues.getUserId(), _group, false, false,
					_serviceContext);

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
			}

			Group stagingGroup = _group.getStagingGroup();

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
	@TestInfo("LPD-115198")
	public void testExportImportLayoutsWithValidation() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		FragmentEntry fragmentEntry = _addFragmentEntry(
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(), RandomTestUtil.randomString(),
				StringPool.BLANK, serviceContext),
			RandomTestUtil.randomString(), serviceContext);

		Layout guestGroupLayout = _getGuestGroupLayout();

		_testExportImportLayoutsWithValidation(
			fragmentEntry, guestGroupLayout.getPlid());

		Layout importedGroupLayout = LayoutTestUtil.addTypePortletLayout(
			importedGroup);

		_testExportImportLayoutsWithValidation(
			fragmentEntry, importedGroupLayout.getPlid());

		Role role = _roleLocalService.getRole(
			importedGroupLayout.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.removeResourcePermission(
			importedGroupLayout.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(importedGroupLayout.getPlid()), role.getRoleId(),
			ActionKeys.VIEW);

		_testExportImportLayoutsWithValidation(
			fragmentEntry, guestGroupLayout.getPlid());
	}

	@Test
	@TestInfo("LPD-57728")
	public void testExportImportMarketplaceFragmentEntries() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(), RandomTestUtil.randomString(),
				StringPool.BLANK, serviceContext);

		FragmentEntry fragmentEntry = _addFragmentEntry(
			fragmentCollection, false, serviceContext);

		FragmentEntry marketplaceFragmentEntry = _addFragmentEntry(
			fragmentCollection, true, serviceContext);

		exportImportPortlet(FragmentPortletKeys.FRAGMENT, false);

		FragmentCollection importedFragmentCollection =
			_fragmentCollectionLocalService.
				getFragmentCollectionByUuidAndGroupId(
					fragmentCollection.getUuid(), importedGroup.getGroupId());

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				importedFragmentCollection.getFragmentCollectionId());

		Assert.assertEquals(
			fragmentEntries.toString(), 1, fragmentEntries.size());

		FragmentEntry actualFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntry.getUuid(), actualFragmentEntry.getUuid());

		Assert.assertNull(
			_fragmentEntryLinkLocalService.
				fetchFragmentEntryLinkByUuidAndGroupId(
					marketplaceFragmentEntry.getUuid(),
					importedGroup.getGroupId()));
	}

	@Test
	@TestInfo("LPD-93455")
	public void testExportImportPortletWithValidation() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		FragmentEntry fragmentEntry = _addFragmentEntry(
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(), RandomTestUtil.randomString(),
				StringPool.BLANK, serviceContext),
			RandomTestUtil.randomString(), serviceContext);

		_testExportImportPortletWithValidation(fragmentEntry);
		_testExportImportPortletWithValidation(fragmentEntry);
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

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					group, TestPropsValues.getUserId());

			FragmentCollection fragmentCollection =
				_fragmentCollectionLocalService.addFragmentCollection(
					null, TestPropsValues.getUserId(),
					serviceContext.getScopeGroupId(),
					RandomTestUtil.randomString(), StringPool.BLANK,
					serviceContext);

			FragmentEntry fragmentEntry = _addFragmentEntry(
				fragmentCollection, false, serviceContext);

			exportImportPortlet(FragmentPortletKeys.FRAGMENT, false);

			FragmentEntry importedGroupFragmentEntry =
				_fragmentEntryLocalService.getFragmentEntryByUuidAndGroupId(
					fragmentEntry.getUuid(), importedGroup.getGroupId());

			Assert.assertEquals(
				_configuration, importedGroupFragmentEntry.getConfiguration());
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
					importedGroupFragmentEntry.getExternalReferenceCode(), null,
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

	private FragmentEntry _addFragmentEntry(
			FragmentCollection fragmentCollection, boolean marketplace,
			ServiceContext serviceContext)
		throws Exception {

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			fragmentCollection.getFragmentCollectionId(), null,
			RandomTestUtil.randomString(), StringPool.BLANK,
			"Original HTML Fragment" + _HTML, StringPool.BLANK, false,
			_configuration, null, 0, marketplace, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private FragmentEntry _addFragmentEntry(
			FragmentCollection fragmentCollection, String html,
			ServiceContext serviceContext)
		throws Exception {

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			fragmentCollection.getFragmentCollectionId(), null,
			RandomTestUtil.randomString(), StringPool.BLANK, html,
			StringPool.BLANK, false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void _assertContains(String text, String... strings) {
		for (String string : strings) {
			Assert.assertTrue(string, string.contains(text));
		}
	}

	private void _assertFragmentEntryContentException(Exception exception) {
		Throwable throwable = exception;

		while (throwable != null) {
			if (throwable instanceof FragmentEntryContentException) {
				return;
			}

			throwable = throwable.getCause();
		}

		throw new AssertionError(exception);
	}

	private Layout _getGuestGroupLayout() throws Exception {
		Group group = _groupLocalService.getGroup(
			importedGroup.getCompanyId(), GroupConstants.GUEST);

		return _layoutLocalService.getLayoutByFriendlyURL(
			group.getGroupId(), false,
			PropsValues.DEFAULT_GUEST_PUBLIC_LAYOUT_FRIENDLY_URL);
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

	private String _getRESTClientHTML() {
		return StringBundler.concat(
			"[#assign restGetSample = restClient.get(",
			"\"/o/headless-delivery/v1.0/sites/0/site-pages\")]\n",
			"[#if (restGetSample.items)??]${restGetSample.items?size}",
			"[/#if]\n<h1>", RandomTestUtil.randomString(), "</h1>");
	}

	private void _testExportImportLayoutsWithValidation(
			FragmentEntry fragmentEntry, long plid)
		throws Exception {

		fragmentEntry = _updateFragmentEntry(
			fragmentEntry,
			StringBundler.concat(
				"[#if themeDisplay.getPlid() != ", plid,
				"]${invalidPlid}[/#if][#if themeDisplay.getScopeGroupId() != ",
				importedGroup.getGroupId(), "]${invalidScopeGroupId}[/#if]"));

		exportImportLayouts(new long[0], getImportParameterMap());

		FragmentEntry importedGroupFragmentEntry =
			_fragmentEntryLocalService.getFragmentEntryByUuidAndGroupId(
				fragmentEntry.getUuid(), importedGroup.getGroupId());

		Assert.assertEquals(
			fragmentEntry.getHtml(), importedGroupFragmentEntry.getHtml());
	}

	private void _testExportImportPortletWithValidation(
			FragmentEntry fragmentEntry)
		throws Exception {

		fragmentEntry = _updateFragmentEntry(fragmentEntry, "${");

		_testExportImportPortletWithValidationWithFragmentEntryContentException(
			fragmentEntry);

		fragmentEntry = _updateFragmentEntry(
			fragmentEntry, _getRESTClientHTML());

		exportImportPortlet(FragmentPortletKeys.FRAGMENT, false);

		FragmentEntry importedGroupFragmentEntry =
			_fragmentEntryLocalService.getFragmentEntryByUuidAndGroupId(
				fragmentEntry.getUuid(), importedGroup.getGroupId());

		Assert.assertEquals(
			fragmentEntry.getHtml(), importedGroupFragmentEntry.getHtml());
	}

	private void _testExportImportPortletWithValidationWithFragmentEntryContentException() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.exportimport.internal.lifecycle." +
					"LoggerExportImportLifecycleListener",
				LoggerTestUtil.ERROR)) {

			try {
				exportImportPortlet(FragmentPortletKeys.FRAGMENT, false);

				Assert.fail();
			}
			catch (Exception exception) {
				_assertFragmentEntryContentException(exception);
			}

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertFalse(logEntries.toString(), logEntries.isEmpty());
		}
	}

	private void
		_testExportImportPortletWithValidationWithFragmentEntryContentException(
			FragmentEntry fragmentEntry) {

		String expectedHTML = null;

		FragmentEntry importedGroupFragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntryByUuidAndGroupId(
				fragmentEntry.getUuid(), importedGroup.getGroupId());

		if (importedGroupFragmentEntry != null) {
			expectedHTML = importedGroupFragmentEntry.getHtml();
		}

		_testExportImportPortletWithValidationWithFragmentEntryContentException();

		importedGroupFragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntryByUuidAndGroupId(
				fragmentEntry.getUuid(), importedGroup.getGroupId());

		if (expectedHTML == null) {
			Assert.assertNull(importedGroupFragmentEntry);

			return;
		}

		Assert.assertEquals(expectedHTML, importedGroupFragmentEntry.getHtml());
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

	private FragmentEntry _updateFragmentEntry(
			FragmentEntry fragmentEntry, String html)
		throws Exception {

		try (AutoCloseable autoCloseable =
				new CompanyConfigurationTemporarySwapper(
					_group.getCompanyId(),
					"com.liferay.fragment.entry.processor.freemarker." +
						"internal.configuration." +
							"FreeMarkerFragmentEntryProcessorConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"enable.freemarker", false
					).build())) {

			fragmentEntry.setHtml(html);

			fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
				fragmentEntry);

			ThreadLocal<Set<String>> validHTMLs =
				ReflectionTestUtil.getFieldValue(
					_fragmentEntryProcessorRegistry, "_validHTMLs");

			validHTMLs.remove();

			return fragmentEntry;
		}
	}

	private static final String _HTML = StringBundler.concat(
		"[#list 0..configuration.numberOfSlides-1 as i]\n",
		"<div class=\"js-slide js-slide${i+1}\">\n",
		"\t<lfr-drop-zone data-lfr-drop-zone-id=\"${i+1}\" ",
		"data-lfr-priority=\"${i+1}\"></lfr-drop-zone>\n", "</div>\n[/#list]");

	private static String _configuration;
	private static FragmentCollection _fragmentCollection;

	@Inject
	private static FragmentCollectionLocalService
		_fragmentCollectionLocalService;

	private static Group _group;
	private static ServiceContext _serviceContext;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Inject(filter = "jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT)
	private PortletDataHandler _fragmentPortletDataHandler;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutContentProvider _layoutContentProvider;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

}