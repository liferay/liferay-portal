/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v6_1_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.exportimport.changeset.Changeset;
import com.liferay.exportimport.changeset.portlet.action.ExportImportChangesetMVCActionCommandHelper;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalContentSearch;
import com.liferay.journal.service.JournalContentSearchLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class JournalArticleLayoutClassedModelUsageUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey());

		_journalArticleClassNameId = _classNameLocalService.getClassNameId(
			JournalArticle.class.getName());
		_privateLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), true);
		_publicLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false);
	}

	@Test
	public void testUpgrade() throws Exception {
		Map<Layout, List<String>> expectedLayoutPortletIdsMap =
			_addPortletsToLayouts();

		_runUpgrade();

		_assertLayoutPortletIds(expectedLayoutPortletIdsMap);
	}

	@Test
	public void testUpgradeExistingDefaultLayoutClassedModelUsage()
		throws Exception {

		_addPortletsToLayouts();

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			_journalArticle.getGroupId(), StringPool.BLANK,
			_journalArticleClassNameId, _journalArticle.getResourcePrimKey(),
			StringPool.BLANK, 0, 0, new ServiceContext());

		_runUpgrade();

		_assertLayoutPortletIds(Collections.emptyMap());
	}

	@Test
	public void testUpgradeManualAssetPublisherSelection() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry1 = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle1.getResourcePrimKey());

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry2 = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle2.getResourcePrimKey());

		String[] assetEntryXml = {
			assetEntry1.getClassUuid(), _assetEntry.getClassUuid(),
			assetEntry2.getClassUuid()
		};

		String expectedPrivateLayoutPortletId =
			_addAssetPublisherPortletToLayout(
				_privateLayout, "manual", assetEntryXml);

		_assertAssetPublisherPortletPreferencesCount(1, true);

		String expectedPublicLayoutPortletId =
			_addAssetPublisherPortletToLayout(
				_publicLayout, "manual", assetEntryXml);

		_assertAssetPublisherPortletPreferencesCount(1, false);
		_assertLayoutClassedModelUsagesCount(
			_journalArticle.getResourcePrimKey(), 0);
		_assertLayoutClassedModelUsagesCount(
			journalArticle1.getResourcePrimKey(), 0);
		_assertLayoutClassedModelUsagesCount(
			journalArticle2.getResourcePrimKey(), 0);

		_runUpgrade();

		_assertLayoutClassedModelUsagesCount(
			_journalArticle.getResourcePrimKey(), 2);
		_assertLayoutClassedModelUsagesCount(
			journalArticle1.getResourcePrimKey(), 2);
		_assertLayoutClassedModelUsagesCount(
			journalArticle2.getResourcePrimKey(), 2);

		long portletClassNameId = _classNameLocalService.getClassNameId(
			Portlet.class.getName());

		_assertLayoutClassedModelUsage(
			expectedPublicLayoutPortletId, portletClassNameId, _publicLayout,
			_journalArticle.getResourcePrimKey(),
			journalArticle1.getResourcePrimKey(),
			journalArticle2.getResourcePrimKey());
		_assertLayoutClassedModelUsage(
			expectedPrivateLayoutPortletId, portletClassNameId, _privateLayout,
			_journalArticle.getResourcePrimKey(),
			journalArticle1.getResourcePrimKey(),
			journalArticle2.getResourcePrimKey());
	}

	@Test
	public void testUpgradeManualAssetPublisherSelectionLocalStagingEnabled()
		throws Exception {

		try {
			_pushServiceContext(_group, _publicLayout);

			_stagingLocalService.enableLocalStaging(
				TestPropsValues.getUserId(), _group, false, false,
				ServiceContextThreadLocal.getServiceContext());

			Group stagingGroup = _group.getStagingGroup();

			_assertAssetPublisherPortletPreferencesCount(
				_group.getCompanyId(), 0, _group.getGroupId(), false);
			_assertAssetPublisherPortletPreferencesCount(
				stagingGroup.getCompanyId(), 0, stagingGroup.getGroupId(),
				false);

			Layout stagingLayout = _getStagingLayout(stagingGroup.getGroupId());

			String[] assetEntryXml = {_assetEntry.getClassUuid()};

			String expectedPortletId = _addAssetPublisherPortletToLayout(
				stagingLayout, "manual", assetEntryXml);

			_assertAssetPublisherPortletPreferencesCount(
				_group.getCompanyId(), 0, _group.getGroupId(), false);
			_assertAssetPublisherPortletPreferencesCount(
				stagingGroup.getCompanyId(), 1, stagingGroup.getGroupId(),
				false);

			_assertLayoutClassedModelUsagesCount(
				_journalArticle.getResourcePrimKey(), 0);

			_publishToLive(stagingLayout);

			_assertAssetPublisherPortletPreferencesCount(
				_group.getCompanyId(), 1, _group.getGroupId(), false);
			_assertAssetPublisherPortletPreferencesCount(
				stagingGroup.getCompanyId(), 1, stagingGroup.getGroupId(),
				false);

			_assertLayoutClassedModelUsagesCount(
				_journalArticle.getResourcePrimKey(), 0);

			_runUpgrade();

			_assertLayoutClassedModelUsagesCount(
				_journalArticle.getResourcePrimKey(), 2);

			Assert.assertEquals(
				1,
				_getLayoutClassedModelUsagesCount(
					_journalArticle.getResourcePrimKey(),
					stagingGroup.getGroupId()));

			Assert.assertEquals(
				1,
				_getLayoutClassedModelUsagesCount(
					_journalArticle.getResourcePrimKey(), _group.getGroupId()));

			long portletClassNameId = _classNameLocalService.getClassNameId(
				Portlet.class.getName());

			_assertLayoutClassedModelUsage(
				expectedPortletId, portletClassNameId, stagingLayout,
				_journalArticle.getResourcePrimKey());

			_assertLayoutClassedModelUsage(
				expectedPortletId, portletClassNameId, _publicLayout,
				_journalArticle.getResourcePrimKey());

			_publishToLive(stagingLayout);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private String _addAssetPublisherPortletToLayout(
			Layout layout, String selectionStyle, String[] assetEntryClassUuids)
		throws Exception {

		List<String> assetEntryXMLs = new ArrayList<>();

		for (String assetEntryClassUuid : assetEntryClassUuids) {
			assetEntryXMLs.add(_getAssetEntryXml(assetEntryClassUuid));
		}

		return LayoutTestUtil.addPortletToLayout(
			layout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			HashMapBuilder.put(
				"assetEntryXml", assetEntryXMLs.toArray(new String[0])
			).put(
				"selectionStyle", new String[] {selectionStyle}
			).build());
	}

	private String _addJournalContentPortletToLayout(
			JournalArticle journalArticle, Layout layout)
		throws Exception {

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		return _addJournalContentPortletToLayout(
			layout,
			HashMapBuilder.put(
				"articleId", new String[] {journalArticle.getArticleId()}
			).put(
				"assetEntryId",
				new String[] {String.valueOf(assetEntry.getEntryId())}
			).put(
				"groupId",
				new String[] {String.valueOf(assetEntry.getGroupId())}
			).build());
	}

	private String _addJournalContentPortletToLayout(
			Layout layout, Map<String, String[]> preferenceMap)
		throws Exception {

		int count = _getJournalContentPortletPreferencesCount(
			layout.isPrivateLayout());

		String portletId = LayoutTestUtil.addPortletToLayout(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT, preferenceMap);

		Assert.assertEquals(
			count + 1,
			_getJournalContentPortletPreferencesCount(
				layout.isPrivateLayout()));

		return portletId;
	}

	private String _addJournalContentSearch(
			String articleId, long groupId, Layout layout)
		throws Exception {

		String portletId = _addJournalContentPortletToLayout(
			layout,
			HashMapBuilder.put(
				"articleId", new String[] {articleId}
			).put(
				"groupId", new String[] {String.valueOf(groupId)}
			).build());

		_journalContentSearchLocalService.updateContentSearch(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			portletId, articleId, true);

		return portletId;
	}

	private Map<Layout, List<String>> _addPortletsToLayouts() throws Exception {
		_addAssetPublisherPortletToLayout(
			_publicLayout, "dynamic",
			new String[] {_assetEntry.getClassUuid()});
		_addJournalContentSearch(
			RandomTestUtil.randomString(), _journalArticle.getGroupId(),
			_publicLayout);

		JournalArticle draftJournalArticle =
			JournalTestUtil.addArticleWithWorkflow(_group.getGroupId(), false);

		_addJournalContentPortletToLayout(draftJournalArticle, _publicLayout);

		List<String> expectedPrivateLayoutPortletIds = new ArrayList<>();

		expectedPrivateLayoutPortletIds.add(
			_addAssetPublisherPortletToLayout(
				_privateLayout, "manual",
				new String[] {_assetEntry.getClassUuid()}));
		expectedPrivateLayoutPortletIds.add(
			_addJournalContentSearch(
				_journalArticle.getArticleId(), _journalArticle.getGroupId(),
				_privateLayout));
		expectedPrivateLayoutPortletIds.add(
			_addJournalContentPortletToLayout(_journalArticle, _privateLayout));

		_assertAssetPublisherPortletPreferencesCount(1, true);

		List<String> expectedPublicLayoutPortletIds = new ArrayList<>();

		expectedPublicLayoutPortletIds.add(
			_addAssetPublisherPortletToLayout(
				_publicLayout, "manual",
				new String[] {_assetEntry.getClassUuid()}));
		expectedPublicLayoutPortletIds.add(
			_addJournalContentSearch(
				_journalArticle.getArticleId(), _journalArticle.getGroupId(),
				_publicLayout));
		expectedPublicLayoutPortletIds.add(
			_addJournalContentPortletToLayout(_journalArticle, _publicLayout));

		_assertAssetPublisherPortletPreferencesCount(2, false);
		_assertJournalContentSearchesCount(_journalArticle.getArticleId(), 2);
		_assertLayoutClassedModelUsagesCount(
			_journalArticle.getResourcePrimKey(), 0);

		return HashMapBuilder.<Layout, List<String>>put(
			_privateLayout, expectedPrivateLayoutPortletIds
		).put(
			_publicLayout, expectedPublicLayoutPortletIds
		).build();
	}

	private void _assertAssetPublisherPortletPreferencesCount(
		int count, boolean privateLayout) {

		_assertAssetPublisherPortletPreferencesCount(
			_group.getCompanyId(), count, _group.getGroupId(), privateLayout);
	}

	private void _assertAssetPublisherPortletPreferencesCount(
		long companyId, int count, long groupId, boolean privateLayout) {

		List<PortletPreferences> portletPreferences =
			_portletPreferencesLocalService.getPortletPreferences(
				companyId, groupId, PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				AssetPublisherPortletKeys.ASSET_PUBLISHER, privateLayout);

		Assert.assertEquals(
			portletPreferences.toString(), count, portletPreferences.size());
	}

	private void _assertJournalContentSearchesCount(
		String articleId, int count) {

		List<JournalContentSearch> articleContentSearches =
			_journalContentSearchLocalService.getArticleContentSearches(
				articleId);

		Assert.assertEquals(
			articleContentSearches.toString(), count,
			articleContentSearches.size());
	}

	private void _assertLayoutClassedModelUsage(
		String containerKey, long containerType, Layout layout,
		long... classPKs) {

		for (long classPK : classPKs) {
			LayoutClassedModelUsage layoutClassedModelUsage =
				_layoutClassedModelUsageLocalService.
					fetchLayoutClassedModelUsage(
						layout.getGroupId(), StringPool.BLANK,
						_journalArticleClassNameId, classPK, containerKey,
						containerType, layout.getPlid());

			Assert.assertNotNull(layoutClassedModelUsage);
			Assert.assertEquals(
				layout.getGroupId(), layoutClassedModelUsage.getGroupId());
		}
	}

	private void _assertLayoutClassedModelUsagesCount(long classPK, int count) {
		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.getLayoutClassedModelUsages(
				_journalArticleClassNameId, classPK);

		Assert.assertEquals(
			layoutClassedModelUsages.toString(), count,
			layoutClassedModelUsages.size());
	}

	private void _assertLayoutPortletIds(
		Map<Layout, List<String>> expectedLayoutPortletIdsMap) {

		int count = 0;

		long portletClassNameId = _classNameLocalService.getClassNameId(
			Portlet.class.getName());

		for (Map.Entry<Layout, List<String>> layoutPortletIdsEntry :
				expectedLayoutPortletIdsMap.entrySet()) {

			Layout layout = layoutPortletIdsEntry.getKey();

			for (String expectedPortletId : layoutPortletIdsEntry.getValue()) {
				_assertLayoutClassedModelUsage(
					expectedPortletId, portletClassNameId, layout,
					_journalArticle.getResourcePrimKey());
				count++;
			}
		}

		_assertLayoutClassedModelUsagesCount(
			_journalArticle.getResourcePrimKey(), count);
	}

	private String _getAssetEntryXml(String assetEntryUuid) throws Exception {
		Document document = SAXReaderUtil.createDocument(StringPool.UTF8);

		Element assetEntryElement = document.addElement("asset-entry");

		assetEntryElement.addElement("asset-entry-type");

		Element assetEntryUuidElement = assetEntryElement.addElement(
			"asset-entry-uuid");

		assetEntryUuidElement.addText(assetEntryUuid);

		return document.formattedString(StringPool.BLANK);
	}

	private int _getJournalContentPortletPreferencesCount(
		boolean privateLayout) {

		List<PortletPreferences> portletPreferences =
			_portletPreferencesLocalService.getPortletPreferences(
				_group.getCompanyId(), _group.getGroupId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				JournalContentPortletKeys.JOURNAL_CONTENT, privateLayout);

		return portletPreferences.size();
	}

	private int _getLayoutClassedModelUsagesCount(long classPK, long groupId) {
		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.getLayoutClassedModelUsages(
				_journalArticleClassNameId, classPK);

		int count = 0;

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				layoutClassedModelUsages) {

			if (layoutClassedModelUsage.getGroupId() == groupId) {
				count++;
			}
		}

		return count;
	}

	private Layout _getStagingLayout(long stagingGroupId) {
		List<Layout> stagingLayouts = _layoutLocalService.getLayouts(
			stagingGroupId, false);

		Assert.assertEquals(
			stagingLayouts.toString(), 1, stagingLayouts.size());

		Layout stagingLayout = stagingLayouts.get(0);

		Assert.assertEquals(_publicLayout.getUuid(), stagingLayout.getUuid());

		return stagingLayout;
	}

	private void _publishToLive(Layout layout) throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, LayoutAdminPortletKeys.GROUP_PAGES);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, serviceContext.getThemeDisplay());

		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(layout.getGroupId()));

		Changeset.Builder builder = Changeset.create();

		Changeset changeset = builder.addStagedModel(
			() -> layout
		).addMultipleStagedModel(
			Collections::emptyList
		).build();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.background.task.internal.messaging." +
					"BackgroundTaskMessageListener",
				LoggerTestUtil.ERROR)) {

			_exportImportChangesetMVCActionCommandHelper.publish(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse(), changeset);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(
				ListUtil.toString(
					logEntries,
					new Accessor<LogEntry, String>() {

						@Override
						public String get(LogEntry logEntry) {
							Throwable throwable = logEntry.getThrowable();

							while (throwable.getCause() != null) {
								throwable = throwable.getCause();
							}

							return throwable.getMessage();
						}

						@Override
						public Class<String> getAttributeClass() {
							return String.class;
						}

						@Override
						public Class<LogEntry> getTypeClass() {
							return LogEntry.class;
						}

					}),
				ListUtil.isEmpty(logEntries));
		}
	}

	private void _pushServiceContext(Group group, Layout layout)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(group.getCompanyId()), group,
			layout);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.journal.internal.upgrade.v6_1_1." +
			"JournalArticleLayoutClassedModelUsageUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.journal.internal.upgrade.registry.JournalServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	private AssetEntry _assetEntry;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ExportImportChangesetMVCActionCommandHelper
		_exportImportChangesetMVCActionCommandHelper;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle;
	private long _journalArticleClassNameId;

	@Inject
	private JournalContentSearchLocalService _journalContentSearchLocalService;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	private Layout _privateLayout;
	private Layout _publicLayout;

	@Inject
	private StagingLocalService _stagingLocalService;

}