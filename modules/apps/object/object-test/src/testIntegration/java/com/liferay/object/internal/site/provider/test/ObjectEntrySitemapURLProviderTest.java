/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.site.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.provider.SitemapURLProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author João Victor Alves
 */
@RunWith(Arquillian.class)
public class ObjectEntrySitemapURLProviderTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FriendlyURLResolverRegistryUtil.removeURLSeparators();

		_companyObjectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
		_depotObjectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_DEPOT);

		_group = GroupTestUtil.addGroup();

		LayoutTestUtil.addTypePortletLayout(_group);

		_layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		_siteObjectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);
		_themeDisplay = _getThemeDisplay(_group, _layoutSet);
	}

	@Test
	public void testGetModifiedDate() throws Exception {
		_testGetModifiedDate(0, _companyObjectDefinition);
		_testGetModifiedDate(_depotEntry.getGroupId(), _depotObjectDefinition);
		_testGetModifiedDate(_group.getGroupId(), _siteObjectDefinition);
	}

	@Test
	public void testIsInclude() throws Exception {
		ObjectDefinition systemObjectDefinition =
			ObjectDefinitionTestUtil.publishSystemObjectDefinition();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(
						systemObjectDefinition)) {

			Assert.assertFalse(
				_objectEntrySitemapURLProvider.isInclude(
					TestPropsValues.getCompanyId(), _group.getGroupId()));

			_objectDefinitionLocalService.updateSystemObjectDefinition(
				systemObjectDefinition.getExternalReferenceCode(),
				systemObjectDefinition.getObjectDefinitionId(),
				systemObjectDefinition.getObjectFolderId(),
				systemObjectDefinition.getTitleObjectFieldId(),
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_SITEMAPABLE
					).value(
						StringPool.TRUE
					).build()),
				Collections.emptyList(), Collections.emptyList());

			Assert.assertTrue(
				_objectEntrySitemapURLProvider.isInclude(
					TestPropsValues.getCompanyId(), _group.getGroupId()));
		}
	}

	@Test
	public void testVisitCMSObjectDefinition() throws Exception {
		_testVisitCMSObjectDefinition(
			(layout, objectDefinition, objectEntry) -> {
				Group group = _depotEntry.getGroup();

				_assertRootElement(
					true, group.getFriendlyURL(), layout, objectDefinition,
					objectEntry);
			});

		_testVisitCMSObjectDefinition(
			(layout, objectDefinition, objectEntry) -> {
				Group group = _depotEntry.getGroup();

				_assertRootElement(
					true, group.getFriendlyURL(), _layoutSet, objectDefinition,
					objectEntry, _themeDisplay);
			});
	}

	@Test
	public void testVisitLayout() throws Exception {
		_testVisitLayout(0, _companyObjectDefinition);
		_testVisitLayout(_depotEntry.getGroupId(), _depotObjectDefinition);
		_testVisitLayout(_group.getGroupId(), _siteObjectDefinition);
	}

	@Test
	public void testVisitLayoutSet() throws Exception {
		_testVisitLayoutSet(0, _companyObjectDefinition);
		_testVisitLayoutSet(_depotEntry.getGroupId(), _depotObjectDefinition);
		_testVisitLayoutSet(_group.getGroupId(), _siteObjectDefinition);
	}

	private LayoutPageTemplateEntry _addDisplayPageTemplate(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		return DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			groupId, _portal.getClassNameId(objectDefinition.getClassName()),
			null, true, WorkflowConstants.STATUS_APPROVED);
	}

	private ObjectEntry _addObjectEntry(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			Collections.singletonMap(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertRootElement(
			boolean expectedHasContent, Layout layout,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		_assertRootElement(
			expectedHasContent, StringPool.BLANK, layout, objectDefinition,
			objectEntry);
	}

	private void _assertRootElement(
			boolean expectedHasContent, LayoutSet layoutSet,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ThemeDisplay themeDisplay)
		throws Exception {

		_assertRootElement(
			expectedHasContent, StringPool.BLANK, layoutSet, objectDefinition,
			objectEntry, themeDisplay);
	}

	private void _assertRootElement(
			boolean expectedHasContent, String friendlyURL, Layout layout,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		Element rootElement = _getRootElement();

		_objectEntrySitemapURLProvider.visitLayout(
			rootElement, layout.getUuid(), _layoutSet, _themeDisplay);

		Assert.assertEquals(expectedHasContent, rootElement.hasContent());

		if (!expectedHasContent) {
			return;
		}

		_assertRootElements(
			friendlyURL, objectDefinition, objectEntry, rootElement.elements());
	}

	private void _assertRootElement(
			boolean expectedHasContent, String friendlyURL, LayoutSet layoutSet,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ThemeDisplay themeDisplay)
		throws Exception {

		Element rootElement = _getRootElement();

		_objectEntrySitemapURLProvider.visitLayoutSet(
			rootElement, layoutSet, themeDisplay);

		Assert.assertEquals(expectedHasContent, rootElement.hasContent());

		if (!expectedHasContent) {
			return;
		}

		_assertRootElements(
			friendlyURL, objectDefinition, objectEntry, rootElement.elements());
	}

	private void _assertRootElements(
		String friendlyURL, ObjectDefinition objectDefinition,
		ObjectEntry objectEntry, List<Element> rootElements) {

		Set<Locale> availableLocales = _language.getAvailableLocales();

		String[] availableLanguageIds = TransformUtil.transform(
			objectDefinition.getAvailableLanguageIds(),
			availableLanguageId -> {
				if (availableLocales.contains(
						LocaleUtil.fromLanguageId(availableLanguageId))) {

					return availableLanguageId;
				}

				return null;
			},
			String.class);

		Assert.assertEquals(
			rootElements.toString(), availableLanguageIds.length,
			rootElements.size());

		String objectEntryFriendlyURL = StringUtil.toLowerCase(
			StringBundler.concat(
				StringPool.SLASH, objectDefinition.getFriendlyURLSeparator(),
				friendlyURL, StringPool.SLASH,
				objectEntry.getExternalReferenceCode()));

		for (Element rootElement : rootElements) {
			String objectEntryLocalizedURL = rootElement.elementText("loc");

			Assert.assertNotNull(objectEntryLocalizedURL);
			Assert.assertTrue(
				objectEntryLocalizedURL.endsWith(objectEntryFriendlyURL));
		}
	}

	private CompanyConfigurationTemporarySwapper
			_getCompanyConfigurationTemporarySwapper(
				ObjectDefinition objectDefinition)
		throws Exception {

		return new CompanyConfigurationTemporarySwapper(
			TestPropsValues.getCompanyId(), _PID_SITEMAP_COMPANY_CONFIGURATION,
			HashMapDictionaryBuilder.<String, Object>put(
				"companySitemapObjectDefinitionIds",
				new String[] {
					String.valueOf(objectDefinition.getObjectDefinitionId())
				}
			).build());
	}

	private Element _getRootElement() {
		Document document = _saxReader.createDocument();

		document.setXMLEncoding("UTF-8");

		Element rootElement = document.addElement(
			"urlset", "http://www.sitemaps.org/schemas/sitemap/0.9");

		rootElement.addAttribute(
			"xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.addAttribute(
			"xsi:schemaLocation",
			"http://www.w3.org/1999/xhtml " +
				"http://www.w3.org/2002/08/xhtml/xhtml1-strict.xsd");
		rootElement.addAttribute("xmlns:xhtml", "http://www.w3.org/1999/xhtml");

		return rootElement;
	}

	private ThemeDisplay _getThemeDisplay(Group group, LayoutSet layoutSet)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = CompanyLocalServiceUtil.getCompany(
			group.getCompanyId());

		themeDisplay.setCompany(company);

		themeDisplay.setLanguageId(group.getDefaultLanguageId());
		themeDisplay.setLayoutSet(layoutSet);
		themeDisplay.setLocale(
			LocaleUtil.fromLanguageId(group.getDefaultLanguageId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setPortalDomain(company.getVirtualHostname());
		themeDisplay.setPortalURL(company.getPortalURL(group.getGroupId()));
		themeDisplay.setRequest(new MockHttpServletRequest());
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
		themeDisplay.setSignedIn(true);
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private ObjectDefinition _publishObjectDefinition(
			long objectFolderId, String scope)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						_OBJECT_FIELD_NAME
					).objectFieldSettings(
						Collections.emptyList()
					).build()),
				objectFolderId, scope, TestPropsValues.getUserId());

		if (StringUtil.equals(scope, ObjectDefinitionConstants.SCOPE_DEPOT)) {
			_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				StringPool.TRUE);
		}

		return objectDefinition;
	}

	private ObjectDefinition _publishObjectDefinition(String scope)
		throws Exception {

		return _publishObjectDefinition(0, scope);
	}

	private void _testGetModifiedDate(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(
						objectDefinition)) {

			_addObjectEntry(groupId, objectDefinition);

			if (StringUtil.equals(
					objectDefinition.getScope(),
					ObjectDefinitionConstants.SCOPE_DEPOT)) {

				Assert.assertNull(
					_objectEntrySitemapURLProvider.getModifiedDate(
						TestPropsValues.getCompanyId(), _group.getGroupId()));

				_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
					_depotEntry.getDepotEntryId(), _group.getGroupId());
			}

			Assert.assertNotNull(
				_objectEntrySitemapURLProvider.getModifiedDate(
					TestPropsValues.getCompanyId(), _group.getGroupId()));
		}
	}

	private void _testVisitCMSObjectDefinition(
			UnsafeTriConsumer<Layout, ObjectDefinition, ObjectEntry, Exception>
				unsafeTriConsumer)
		throws Exception {

		ObjectFolder objectFolder =
			_objectFolderLocalService.getOrAddEmptyObjectFolder(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId());

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			objectFolder.getObjectFolderId(),
			ObjectDefinitionConstants.SCOPE_DEPOT);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(
						objectDefinition)) {

			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_depotEntry.getDepotEntryId(), _group.getGroupId());

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_addDisplayPageTemplate(_group.getGroupId(), objectDefinition);

			ObjectEntry objectEntry = _addObjectEntry(
				_depotEntry.getGroupId(), objectDefinition);

			unsafeTriConsumer.accept(
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				objectDefinition, objectEntry);
		}
	}

	private void _testVisitLayout(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(
						objectDefinition)) {

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_addDisplayPageTemplate(_group.getGroupId(), objectDefinition);

			Layout layout = _layoutLocalService.getLayout(
				layoutPageTemplateEntry.getPlid());

			ObjectEntry objectEntry = _addObjectEntry(
				groupId, objectDefinition);

			if (StringUtil.equals(
					objectDefinition.getScope(),
					ObjectDefinitionConstants.SCOPE_DEPOT)) {

				_assertRootElement(
					false, layout, objectDefinition, objectEntry);

				_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
					_depotEntry.getDepotEntryId(), _group.getGroupId());
			}

			_assertRootElement(true, layout, objectDefinition, objectEntry);

			Role role = _roleLocalService.getRole(
				_group.getCompanyId(), RoleConstants.GUEST);

			_resourcePermissionLocalService.setResourcePermissions(
				_group.getCompanyId(), objectDefinition.getClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry.getObjectEntryId()),
				role.getRoleId(), new String[] {ActionKeys.VIEW});

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_userLocalService.getGuestUser(
						objectDefinition.getCompanyId()))) {

				_assertRootElement(true, layout, objectDefinition, objectEntry);
			}

			_updateLayoutSEOEntry(true, layout);

			_assertRootElement(false, layout, objectDefinition, objectEntry);

			_updateLayoutSEOEntry(false, layout);

			_updateObjectDefinition(false, objectDefinition);

			_assertRootElement(false, layout, objectDefinition, objectEntry);

			_updateObjectDefinition(true, objectDefinition);
		}
	}

	private void _testVisitLayoutSet(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(
						objectDefinition)) {

			_addDisplayPageTemplate(_group.getGroupId(), objectDefinition);

			ObjectEntry objectEntry = _addObjectEntry(
				groupId, objectDefinition);

			if (StringUtil.equals(
					objectDefinition.getScope(),
					ObjectDefinitionConstants.SCOPE_DEPOT)) {

				_assertRootElement(
					false, _layoutSet, objectDefinition, objectEntry,
					_themeDisplay);

				_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
					_depotEntry.getDepotEntryId(), _group.getGroupId());
			}

			if (StringUtil.equals(
					objectDefinition.getScope(),
					ObjectDefinitionConstants.SCOPE_SITE)) {

				Group childGroup = GroupTestUtil.addGroup(groupId);

				LayoutTestUtil.addTypePortletLayout(childGroup);

				_addDisplayPageTemplate(
					childGroup.getGroupId(), objectDefinition);

				ObjectEntry childObjectEntry = _addObjectEntry(
					childGroup.getGroupId(), objectDefinition);

				LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
					childGroup.getGroupId(), false);

				_assertRootElement(
					true, layoutSet, objectDefinition, childObjectEntry,
					_getThemeDisplay(childGroup, layoutSet));
			}

			_assertRootElement(
				true, _layoutSet, objectDefinition, objectEntry, _themeDisplay);

			_updateObjectDefinition(false, objectDefinition);

			_assertRootElement(
				false, _layoutSet, objectDefinition, objectEntry,
				_themeDisplay);

			_updateObjectDefinition(true, objectDefinition);
		}
	}

	private void _updateLayoutSEOEntry(
			boolean canonicalURLEnabled, Layout layout)
		throws Exception {

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			layout.isPrivateLayout(), layout.getLayoutId(), canonicalURLEnabled,
			new HashMap<>(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _updateObjectDefinition(
			boolean active, ObjectDefinition objectDefinition)
		throws Exception {

		objectDefinition = _objectDefinitionLocalService.getObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		objectDefinition.setActive(active);

		_objectDefinitionLocalService.updateObjectDefinition(objectDefinition);
	}

	private static final String _OBJECT_FIELD_NAME = StringUtil.randomId();

	private static final String _PID_SITEMAP_COMPANY_CONFIGURATION =
		"com.liferay.site.internal.configuration.SitemapCompanyConfiguration";

	private ObjectDefinition _companyObjectDefinition;
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private ObjectDefinition _depotObjectDefinition;
	private Group _group;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	private LayoutSet _layoutSet;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.site.provider.ObjectEntrySitemapURLProvider",
		type = SitemapURLProvider.class
	)
	private SitemapURLProvider _objectEntrySitemapURLProvider;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SAXReader _saxReader;

	private ObjectDefinition _siteObjectDefinition;
	private ThemeDisplay _themeDisplay;

	@Inject
	private UserLocalService _userLocalService;

}