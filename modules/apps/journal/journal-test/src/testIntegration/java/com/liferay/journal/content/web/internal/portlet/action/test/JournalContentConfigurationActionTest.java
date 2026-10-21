/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class JournalContentConfigurationActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testProcessAction() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				LayoutTestUtil.addPortletToLayout(
					_layout, JournalContentPortletKeys.JOURNAL_CONTENT));

		Map<String, String[]> portletPreferencesMap = _processAction(
			journalArticle1, mockLiferayPortletActionRequest);

		Assert.assertArrayEquals(
			new String[] {journalArticle1.getExternalReferenceCode()},
			portletPreferencesMap.get("articleExternalReferenceCode"));
		Assert.assertArrayEquals(
			new String[] {StringPool.BLANK},
			portletPreferencesMap.get("groupExternalReferenceCode"));

		_companyJournalArticle = JournalTestUtil.addArticle(
			_company.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		portletPreferencesMap = _processAction(
			_companyJournalArticle, mockLiferayPortletActionRequest);

		Group companyGroup = _company.getGroup();

		Assert.assertArrayEquals(
			new String[] {_companyJournalArticle.getExternalReferenceCode()},
			portletPreferencesMap.get("articleExternalReferenceCode"));
		Assert.assertArrayEquals(
			new String[] {companyGroup.getExternalReferenceCode()},
			portletPreferencesMap.get("groupExternalReferenceCode"));

		portletPreferencesMap = _processAction(
			journalArticle1, mockLiferayPortletActionRequest);

		Assert.assertArrayEquals(
			new String[] {journalArticle1.getExternalReferenceCode()},
			portletPreferencesMap.get("articleExternalReferenceCode"));
		Assert.assertArrayEquals(
			new String[] {StringPool.BLANK},
			portletPreferencesMap.get("groupExternalReferenceCode"));
	}

	@Test
	public void testProcessActionWhenArticleIsFromDepotEntry()
		throws Exception {

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), _group.getGroupId());

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_depotEntry.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				LayoutTestUtil.addPortletToLayout(
					_layout, JournalContentPortletKeys.JOURNAL_CONTENT));

		Map<String, String[]> portletPreferencesMap = _processAction(
			journalArticle1, mockLiferayPortletActionRequest);

		Group depotGroup = _depotEntry.getGroup();

		Assert.assertArrayEquals(
			new String[] {journalArticle1.getExternalReferenceCode()},
			portletPreferencesMap.get("articleExternalReferenceCode"));
		Assert.assertArrayEquals(
			new String[] {depotGroup.getExternalReferenceCode()},
			portletPreferencesMap.get("groupExternalReferenceCode"));

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		portletPreferencesMap = _processAction(
			journalArticle2, mockLiferayPortletActionRequest);

		Assert.assertArrayEquals(
			new String[] {journalArticle2.getExternalReferenceCode()},
			portletPreferencesMap.get("articleExternalReferenceCode"));
		Assert.assertArrayEquals(
			new String[] {StringPool.BLANK},
			portletPreferencesMap.get("groupExternalReferenceCode"));
	}

	@Test
	public void testProcessActionWhenArticleIsNotSelected() throws Exception {
		_companyJournalArticle = JournalTestUtil.addArticle(
			_company.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				LayoutTestUtil.addPortletToLayout(
					_layout, JournalContentPortletKeys.JOURNAL_CONTENT));

		Map<String, String[]> portletPreferencesMap = _processAction(
			_companyJournalArticle, mockLiferayPortletActionRequest);

		Group companyGroup = _company.getGroup();

		Assert.assertArrayEquals(
			new String[] {companyGroup.getExternalReferenceCode()},
			portletPreferencesMap.get("groupExternalReferenceCode"));

		portletPreferencesMap = _processAction(
			null, mockLiferayPortletActionRequest);

		Assert.assertArrayEquals(
			new String[] {StringPool.BLANK},
			portletPreferencesMap.get("articleExternalReferenceCode"));
		Assert.assertArrayEquals(
			new String[] {StringPool.BLANK},
			portletPreferencesMap.get("groupExternalReferenceCode"));
	}

	@Test
	public void testProcessActionWhenScopeIsCompany() throws Exception {
		_companyJournalArticle = JournalTestUtil.addArticle(
			_company.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				LayoutTestUtil.addPortletToLayout(
					_layout, JournalContentPortletKeys.JOURNAL_CONTENT,
					HashMapBuilder.put(
						"lfrScopeType", new String[] {"company"}
					).build()));

		Map<String, String[]> portletPreferencesMap = _processAction(
			_companyJournalArticle, mockLiferayPortletActionRequest);

		Group companyGroup = _company.getGroup();

		Assert.assertArrayEquals(
			new String[] {_companyJournalArticle.getExternalReferenceCode()},
			portletPreferencesMap.get("articleExternalReferenceCode"));
		Assert.assertArrayEquals(
			new String[] {companyGroup.getExternalReferenceCode()},
			portletPreferencesMap.get("groupExternalReferenceCode"));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		portletPreferencesMap = _processAction(
			journalArticle, mockLiferayPortletActionRequest);

		Assert.assertArrayEquals(
			new String[] {journalArticle.getExternalReferenceCode()},
			portletPreferencesMap.get("articleExternalReferenceCode"));
		Assert.assertArrayEquals(
			new String[] {_group.getExternalReferenceCode()},
			portletPreferencesMap.get("groupExternalReferenceCode"));
	}

	@Test
	public void testProcessActionWhenScopeIsLayout() throws Exception {
		Group scopeGroup = GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), _layout);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		JournalArticle journalArticle1 =
			JournalTestUtil.addArticleWithXMLContent(
				scopeGroup.getGroupId(),
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureKey(), null);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				LayoutTestUtil.addPortletToLayout(
					_layout, JournalContentPortletKeys.JOURNAL_CONTENT,
					HashMapBuilder.put(
						"lfrScopeLayoutUuid", new String[] {_layout.getUuid()}
					).put(
						"lfrScopeType", new String[] {"layout"}
					).build()));

		Map<String, String[]> portletPreferencesMap = _processAction(
			journalArticle1, mockLiferayPortletActionRequest);

		Assert.assertArrayEquals(
			new String[] {journalArticle1.getExternalReferenceCode()},
			portletPreferencesMap.get("articleExternalReferenceCode"));
		Assert.assertArrayEquals(
			new String[] {scopeGroup.getExternalReferenceCode()},
			portletPreferencesMap.get("groupExternalReferenceCode"));

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		portletPreferencesMap = _processAction(
			journalArticle2, mockLiferayPortletActionRequest);

		Assert.assertArrayEquals(
			new String[] {journalArticle2.getExternalReferenceCode()},
			portletPreferencesMap.get("articleExternalReferenceCode"));
		Assert.assertArrayEquals(
			new String[] {_group.getExternalReferenceCode()},
			portletPreferencesMap.get("groupExternalReferenceCode"));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String portletId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayPortletActionRequest.setParameter(
			"portletResource", portletId);

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(_layout);
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private Map<String, String[]> _processAction(
			JournalArticle journalArticle,
			MockLiferayPortletActionRequest mockLiferayPortletActionRequest)
		throws Exception {

		long assetEntryId = 0;

		if (journalArticle != null) {
			AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getResourcePrimKey());

			assetEntryId = assetEntry.getEntryId();
		}

		mockLiferayPortletActionRequest.setParameter(
			"preferences--assetEntryId--", String.valueOf(assetEntryId));

		_configurationAction.processAction(
			PortletConfigFactoryUtil.create(
				PortletLocalServiceUtil.getPortletById(
					JournalContentPortletKeys.JOURNAL_CONTENT + "_INSTANCE_" +
						RandomTestUtil.randomString()),
				null),
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		return (Map<String, String[]>)
			mockLiferayPortletActionRequest.getAttribute(
				WebKeys.PORTLET_PREFERENCES_MAP);
	}

	private Company _company;

	@DeleteAfterTestRun
	private JournalArticle _companyJournalArticle;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "jakarta.portlet.name=" + JournalContentPortletKeys.JOURNAL_CONTENT
	)
	private ConfigurationAction _configurationAction;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private Portal _portal;

}