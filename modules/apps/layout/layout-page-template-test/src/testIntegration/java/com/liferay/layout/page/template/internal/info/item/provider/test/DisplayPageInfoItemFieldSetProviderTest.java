/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.friendly.url.test.util.configuration.manager.FriendlyURLSeparatorConfigurationManagerTemporarySwapper;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.type.WebURL;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
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
public class DisplayPageInfoItemFieldSetProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), null,
			LocaleUtil.getSiteDefault(), null, false, false, serviceContext);

		_classNameId = _portal.getClassNameId(JournalArticle.class.getName());

		_layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), _classNameId,
				_journalArticle.getDDMStructureKey(), true, null,
				"layoutPageTemplateEntry", WorkflowConstants.STATUS_APPROVED);

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				TestPropsValues.getUserId(), _group.getGroupId(), _classNameId,
				_journalArticle.getResourcePrimKey(),
				_layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				AssetDisplayPageConstants.TYPE_SPECIFIC, serviceContext);

		_layout = _layoutLocalService.getLayout(
			assetDisplayPageEntry.getPlid());

		_setUpThemeDisplay();
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo("LPD-104243")
	public void testGetInfoFieldSet() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		_testGetInfoFieldSet();

		_testGetInfoFieldSetWithDisconnectedDesignLibrary();
		_testGetInfoFieldSetWithConnectedDesignLibrary();
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo("LPD-104243")
	public void testGetInfoFieldValues() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		_assertInfoFieldValues(
			FriendlyURLResolverConstants.URL_SEPARATOR_X_CUSTOM_ASSET);

		_testGetInfoFieldValuesWithConnectedDesignLibrary();
	}

	@Test
	public void testGetInfoFieldValuesWithConfiguredURLSeparator()
		throws Exception {

		String customAssetFriendlyURLSeparator = "/custom-asset-test1";

		try (FriendlyURLSeparatorConfigurationManagerTemporarySwapper
				friendlyURLSeparatorConfigurationManagerTemporarySwapper =
					new FriendlyURLSeparatorConfigurationManagerTemporarySwapper(
						_group.getCompanyId(),
						JSONUtil.put(
							JournalArticle.class.getName(), "/journal-test1/"
						).put(
							"custom-asset-display-page",
							customAssetFriendlyURLSeparator + StringPool.SLASH
						).toString())) {

			_assertInfoFieldValues(customAssetFriendlyURLSeparator);
		}
	}

	@Test
	public void testGetInfoFieldValuesWithDoAsUserId() throws Exception {
		_themeDisplay.setDoAsUserId(RandomTestUtil.randomString());

		_assertInfoFieldValues(
			FriendlyURLResolverConstants.URL_SEPARATOR_X_CUSTOM_ASSET);
	}

	@Test
	@TestInfo("LPS-191986")
	public void testGetInfoFieldValuesWithDraftLayoutPageTemplateEntry()
		throws Exception {

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			_journalArticle.getDDMStructureKey(), false,
			WorkflowConstants.STATUS_DRAFT);

		InfoItemReference infoItemReference = new InfoItemReference(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey());

		List<InfoFieldValue<Object>> infoFieldValues =
			_displayPageInfoItemFieldSetProvider.getInfoFieldValues(
				infoItemReference,
				String.valueOf(_journalArticle.getDDMStructureId()),
				JournalArticle.class.getSimpleName(), _journalArticle,
				_themeDisplay);

		Assert.assertEquals(
			infoFieldValues.toString(), 3, infoFieldValues.size());

		InfoFieldSet infoFieldSet =
			_displayPageInfoItemFieldSetProvider.getInfoFieldSet(
				JournalArticle.class.getName(),
				String.valueOf(_journalArticle.getDDMStructureId()),
				RandomTestUtil.randomString(), _group.getGroupId());

		List<InfoField<?>> infoFields = infoFieldSet.getAllInfoFields();

		Assert.assertEquals(infoFields.toString(), 2, infoFields.size());
	}

	private Group _addConnectedDesignLibraryGroup() throws Exception {
		DepotEntry depotEntry = _addDesignLibraryDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		return depotEntry.getGroup();
	}

	private DepotEntry _addDesignLibraryDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _assertInfoField(
		String externalUniqueId, InfoField infoField, String name,
		String uniqueId) {

		Assert.assertEquals(externalUniqueId, infoField.getExternalUniqueId());
		Assert.assertEquals(name, infoField.getName());
		Assert.assertEquals(uniqueId, infoField.getUniqueId());
	}

	private void _assertInfoFieldValue(
			String externalUniqueId, InfoFieldValue<Object> infoFieldValue,
			String name, String uniqueId,
			UnsafeConsumer<Object, Exception> unsafeConsumer)
		throws Exception {

		_assertInfoField(
			externalUniqueId, infoFieldValue.getInfoField(), name, uniqueId);

		unsafeConsumer.accept(
			infoFieldValue.getValue(LocaleUtil.getSiteDefault()));
	}

	private void _assertInfoFieldValues(String customAssetURLSeparator)
		throws Exception {

		List<InfoFieldValue<Object>> sortedInfoFieldValues =
			_getSortedInfoFieldValues();

		Assert.assertEquals(
			sortedInfoFieldValues.toString(), 3, sortedInfoFieldValues.size());

		InfoItemReference infoItemReference = new InfoItemReference(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey());

		_assertInfoFieldValue(
			JournalArticle.class.getSimpleName() + "_displayPageURL",
			sortedInfoFieldValues.get(0), "displayPageURL",
			JournalArticle.class.getSimpleName() + "_displayPageURL",
			object -> Assert.assertEquals(
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					infoItemReference, _journalArticle, _themeDisplay),
				object));

		_assertInfoFieldValue(
			LayoutPageTemplateEntry.class.getSimpleName() + "__ERC__" +
				_layoutPageTemplateEntry.getExternalReferenceCode(),
			sortedInfoFieldValues.get(1), _layoutPageTemplateEntry.getName(),
			LayoutPageTemplateEntry.class.getSimpleName() +
				StringPool.UNDERLINE +
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			object -> _assertInfoFieldValueWebURL(
				customAssetURLSeparator, _layout, object));
		_assertInfoFieldValue(
			LayoutPageTemplateEntry.class.getSimpleName() + "__ERC__" +
				_layoutPageTemplateEntry.getExternalReferenceCode(),
			sortedInfoFieldValues.get(2), _layoutPageTemplateEntry.getName(),
			LayoutPageTemplateEntry.class.getSimpleName() +
				StringPool.UNDERLINE +
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryKey(),
			object -> _assertInfoFieldValueWebURL(
				customAssetURLSeparator, _layout, object));
	}

	private void _assertInfoFieldValueWebURL(
			String customAssetURLSeparator, Layout layout, Object object)
		throws Exception {

		Assert.assertTrue(object instanceof WebURL);

		WebURL webURL = (WebURL)object;

		Assert.assertEquals(
			_portal.addPreservedParameters(
				_themeDisplay,
				StringBundler.concat(
					_portal.getGroupFriendlyURL(
						_group.getPublicLayoutSet(), _themeDisplay, false,
						false),
					customAssetURLSeparator,
					layout.getFriendlyURL(LocaleUtil.getSiteDefault()),
					StringPool.SLASH, _classNameId, StringPool.SLASH,
					_journalArticle.getResourcePrimKey())),
			webURL.getURL());
	}

	private List<InfoField<?>> _getSortedInfoFields() {
		InfoFieldSet infoFieldSet =
			_displayPageInfoItemFieldSetProvider.getInfoFieldSet(
				JournalArticle.class.getName(),
				String.valueOf(_journalArticle.getDDMStructureId()),
				"LayoutPageTemplateEntry", _group.getGroupId());

		List<InfoField<?>> infoFields = infoFieldSet.getAllInfoFields();

		return ListUtil.sort(
			infoFields, Comparator.comparing(InfoField::getName));
	}

	private List<InfoFieldValue<Object>> _getSortedInfoFieldValues()
		throws Exception {

		InfoItemReference infoItemReference = new InfoItemReference(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey());

		return ListUtil.sort(
			_displayPageInfoItemFieldSetProvider.getInfoFieldValues(
				infoItemReference,
				String.valueOf(_journalArticle.getDDMStructureId()),
				JournalArticle.class.getSimpleName(), _journalArticle,
				_themeDisplay),
			Comparator.comparing(
				infoFieldValue -> {
					InfoField infoField = infoFieldValue.getInfoField();

					return infoField.getName();
				}));
	}

	private void _setUpThemeDisplay() throws Exception {
		_themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
			_group, _layout);

		_themeDisplay.setPortalURL(
			"http://localhost:" + PortalUtil.getPortalServerPort(false));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_group.getGroupId(),
				String.valueOf(
					_journalArticle.getUrlTitle(LocaleUtil.getSiteDefault()))));
		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_themeDisplay.setRequest(mockHttpServletRequest);

		_themeDisplay.setServerName("localhost");
		_themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
	}

	private void _testGetInfoFieldSet() {
		List<InfoField<?>> sortedInfoFields = _getSortedInfoFields();

		Assert.assertEquals(
			sortedInfoFields.toString(), 2, sortedInfoFields.size());

		_assertInfoField(
			"LayoutPageTemplateEntry_displayPageURL", sortedInfoFields.get(0),
			"displayPageURL", "LayoutPageTemplateEntry_displayPageURL");
		_assertInfoField(
			"LayoutPageTemplateEntry__ERC__" +
				_layoutPageTemplateEntry.getExternalReferenceCode(),
			sortedInfoFields.get(1), _layoutPageTemplateEntry.getName(),
			"LayoutPageTemplateEntry_" +
				_layoutPageTemplateEntry.getLayoutPageTemplateEntryId());
	}

	private void _testGetInfoFieldSetWithConnectedDesignLibrary()
		throws Exception {

		Group designLibraryGroup = _addConnectedDesignLibraryGroup();

		LayoutPageTemplateEntry designLibraryLayoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				designLibraryGroup.getGroupId(), _classNameId,
				_journalArticle.getDDMStructureKey(), false, null,
				"designLibraryLayoutPageTemplateEntry",
				WorkflowConstants.STATUS_APPROVED);

		long layoutPageTemplateEntryId =
			designLibraryLayoutPageTemplateEntry.getLayoutPageTemplateEntryId();

		List<InfoField<?>> sortedInfoFields = _getSortedInfoFields();

		Assert.assertEquals(
			sortedInfoFields.toString(), 3, sortedInfoFields.size());

		_assertInfoField(
			"LayoutPageTemplateEntry__ERC__" +
				designLibraryLayoutPageTemplateEntry.getExternalReferenceCode(),
			sortedInfoFields.get(0),
			designLibraryLayoutPageTemplateEntry.getName(),
			"LayoutPageTemplateEntry_" + layoutPageTemplateEntryId);
	}

	private void _testGetInfoFieldSetWithDisconnectedDesignLibrary()
		throws Exception {

		DepotEntry depotEntry = _addDesignLibraryDepotEntry();

		Group designLibraryGroup = depotEntry.getGroup();

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			designLibraryGroup.getGroupId(), _classNameId,
			_journalArticle.getDDMStructureKey(), false, null,
			"designLibraryLayoutPageTemplateEntry",
			WorkflowConstants.STATUS_APPROVED);

		List<InfoField<?>> sortedInfoFields = _getSortedInfoFields();

		Assert.assertEquals(
			sortedInfoFields.toString(), 2, sortedInfoFields.size());
	}

	private void _testGetInfoFieldValuesWithConnectedDesignLibrary()
		throws Exception {

		Group designLibraryGroup = _addConnectedDesignLibraryGroup();

		LayoutPageTemplateEntry designLibraryLayoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				designLibraryGroup.getGroupId(), _classNameId,
				_journalArticle.getDDMStructureKey(), false, null,
				"designLibraryLayoutPageTemplateEntry",
				WorkflowConstants.STATUS_APPROVED);

		Layout designLibraryLayout = _layoutLocalService.getLayout(
			designLibraryLayoutPageTemplateEntry.getPlid());

		List<InfoFieldValue<Object>> sortedInfoFieldValues =
			_getSortedInfoFieldValues();

		Assert.assertEquals(
			sortedInfoFieldValues.toString(), 5, sortedInfoFieldValues.size());

		long layoutPageTemplateEntryId =
			designLibraryLayoutPageTemplateEntry.getLayoutPageTemplateEntryId();

		_assertInfoFieldValue(
			"LayoutPageTemplateEntry__ERC__" +
				designLibraryLayoutPageTemplateEntry.getExternalReferenceCode(),
			sortedInfoFieldValues.get(0),
			designLibraryLayoutPageTemplateEntry.getName(),
			"LayoutPageTemplateEntry_" + layoutPageTemplateEntryId,
			object -> _assertInfoFieldValueWebURL(
				FriendlyURLResolverConstants.URL_SEPARATOR_X_CUSTOM_ASSET,
				designLibraryLayout, object));

		String layoutPageTemplateEntryKey =
			designLibraryLayoutPageTemplateEntry.
				getLayoutPageTemplateEntryKey();

		_assertInfoFieldValue(
			"LayoutPageTemplateEntry__ERC__" +
				designLibraryLayoutPageTemplateEntry.getExternalReferenceCode(),
			sortedInfoFieldValues.get(1),
			designLibraryLayoutPageTemplateEntry.getName(),
			"LayoutPageTemplateEntry_" + layoutPageTemplateEntryKey,
			object -> _assertInfoFieldValueWebURL(
				FriendlyURLResolverConstants.URL_SEPARATOR_X_CUSTOM_ASSET,
				designLibraryLayout, object));
	}

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	private long _classNameId;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DisplayPageInfoItemFieldSetProvider
		_displayPageInfoItemFieldSetProvider;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle;
	private Layout _layout;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.layout.display.page.JournalArticleLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<JournalArticle>
		_layoutDisplayPageProvider;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private LayoutPageTemplateEntry _layoutPageTemplateEntry;

	@Inject
	private Portal _portal;

	private ThemeDisplay _themeDisplay;

}