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
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
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
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.provider.SitemapURLProvider;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
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

	@BeforeClass
	public static void setUpClass() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		_companyObjectDefinition = _publishCustomObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_depotObjectDefinition = _publishCustomObjectDefinition(
			ObjectDefinitionConstants.SCOPE_DEPOT);

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			TestPropsValues.getUserId(),
			_depotObjectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			StringPool.TRUE);

		_siteObjectDefinition = _publishCustomObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		_themeDisplay = _getThemeDisplay(_group, _layoutSet);

		_companyConfigurationTemporarySwapper =
			_getCompanyConfigurationTemporarySwapper(_companyObjectDefinition);

		LayoutTestUtil.addTypePortletLayout(_group);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyConfigurationTemporarySwapper.close();
	}

	@Test
	public void testGetModifiedDateWithObjectEntryInConnectedDepotEntry()
		throws Exception {

		_depotEntry = _addDepotEntry();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(
						_depotObjectDefinition)) {

			_addObjectEntry(_depotEntry.getGroupId(), _depotObjectDefinition);

			Assert.assertNull(
				_objectEntrySitemapURLProvider.getModifiedDate(
					TestPropsValues.getCompanyId(), _group.getGroupId()));

			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_depotEntry.getDepotEntryId(), _group.getGroupId());

			Assert.assertNotNull(
				_objectEntrySitemapURLProvider.getModifiedDate(
					TestPropsValues.getCompanyId(), _group.getGroupId()));
		}
	}

	@Test
	public void testIsIncludeWithSystemObjectDefinition() throws Exception {
		_systemObjectDefinition =
			ObjectDefinitionTestUtil.publishSystemObjectDefinition();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(
						_systemObjectDefinition)) {

			Assert.assertFalse(
				_objectEntrySitemapURLProvider.isInclude(
					TestPropsValues.getCompanyId(), _group.getGroupId()));

			_objectDefinitionLocalService.updateSystemObjectDefinition(
				_systemObjectDefinition.getExternalReferenceCode(),
				_systemObjectDefinition.getObjectDefinitionId(),
				_systemObjectDefinition.getObjectFolderId(),
				_systemObjectDefinition.getTitleObjectFieldId(),
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
	public void testVisitLayout() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate();

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		ObjectEntry objectEntry = _addObjectEntry();

		Element rootElement = _getRootElement();

		_assertRootElement(
			layout, _companyObjectDefinition, objectEntry, rootElement);

		_updateObjectDefinition(false);

		try {
			rootElement = _getRootElement();

			_objectEntrySitemapURLProvider.visitLayout(
				rootElement, layout.getUuid(), _layoutSet, _themeDisplay);

			Assert.assertFalse(rootElement.hasContent());
		}
		finally {
			_updateObjectDefinition(true);
		}

		_updateLayoutSEOEntry(true, layout);

		try {
			rootElement = _getRootElement();

			_objectEntrySitemapURLProvider.visitLayout(
				rootElement, layout.getUuid(), _layoutSet, _themeDisplay);

			Assert.assertFalse(rootElement.hasContent());
		}
		finally {
			_updateLayoutSEOEntry(false, layout);
		}
	}

	@Test
	public void testVisitLayoutAsGuestUser() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		_addObjectEntry();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate();

		_assertRootElementAsGuestUser(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			_companyObjectDefinition, objectEntry);
	}

	@Test
	public void testVisitLayoutAsGuestUserWithObjectEntryInConnectedDepotEntry()
		throws Exception {

		_depotEntry = _addDepotEntry();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(
						_depotObjectDefinition)) {

			ObjectEntry objectEntry = _addObjectEntry(
				_depotEntry.getGroupId(), _depotObjectDefinition);

			_addObjectEntry(_depotEntry.getGroupId(), _depotObjectDefinition);

			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_depotEntry.getDepotEntryId(), _group.getGroupId());

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_addDisplayPageTemplate(_depotObjectDefinition);

			_assertRootElementAsGuestUser(
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				_depotObjectDefinition, objectEntry);
		}
	}

	@Test
	public void testVisitLayoutSet() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		try {
			_addDisplayPageTemplate();

			Element rootElement = _getRootElement();

			_objectEntrySitemapURLProvider.visitLayoutSet(
				rootElement, _layoutSet, _themeDisplay);

			Assert.assertTrue(rootElement.hasContent());

			_updateObjectDefinition(false);

			rootElement = _getRootElement();

			_objectEntrySitemapURLProvider.visitLayoutSet(
				rootElement, _layoutSet, _themeDisplay);

			Assert.assertFalse(rootElement.hasContent());
		}
		finally {
			_updateObjectDefinition(true);

			_objectEntryLocalService.deleteObjectEntry(objectEntry);
		}
	}

	@Test
	public void testVisitLayoutSetWithObjectEntryInParentSite()
		throws Exception {

		_childGroup = GroupTestUtil.addGroup(_group.getGroupId());

		LayoutTestUtil.addTypePortletLayout(_childGroup);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(
						_siteObjectDefinition)) {

			ObjectEntry childObjectEntry = _addObjectEntry(
				_childGroup.getGroupId(), _siteObjectDefinition);
			ObjectEntry parentObjectEntry = _addObjectEntry(
				_group.getGroupId(), _siteObjectDefinition);

			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_childGroup.getGroupId(),
				_portal.getClassNameId(_siteObjectDefinition.getClassName()),
				null, true, WorkflowConstants.STATUS_APPROVED);

			LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
				_childGroup.getGroupId(), false);

			Element rootElement = _getRootElement();

			_objectEntrySitemapURLProvider.visitLayoutSet(
				rootElement, layoutSet,
				_getThemeDisplay(_childGroup, layoutSet));

			List<String> objectEntryURLs = new ArrayList<>();

			for (Element element : rootElement.elements()) {
				objectEntryURLs.add(element.elementText("loc"));
			}

			Assert.assertTrue(
				objectEntryURLs.toString(),
				_containsObjectEntry(childObjectEntry, objectEntryURLs));
			Assert.assertFalse(
				objectEntryURLs.toString(),
				_containsObjectEntry(parentObjectEntry, objectEntryURLs));
		}
	}

	@Test
	public void testVisitLayoutWithObjectEntryInConnectedDepotEntry()
		throws Exception {

		_depotEntry = _addDepotEntry();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper(
						_depotObjectDefinition)) {

			ObjectEntry objectEntry = _addObjectEntry(
				_depotEntry.getGroupId(), _depotObjectDefinition);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_addDisplayPageTemplate(_depotObjectDefinition);

			Layout layout = _layoutLocalService.getLayout(
				layoutPageTemplateEntry.getPlid());

			Element rootElement = _getRootElement();

			_objectEntrySitemapURLProvider.visitLayout(
				rootElement, layout.getUuid(), _layoutSet, _themeDisplay);

			Assert.assertFalse(rootElement.hasContent());

			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_depotEntry.getDepotEntryId(), _group.getGroupId());

			rootElement = _getRootElement();

			_assertRootElement(
				layout, _depotObjectDefinition, objectEntry, rootElement);
		}
	}

	private static CompanyConfigurationTemporarySwapper
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

	private static ThemeDisplay _getThemeDisplay(
			Group group, LayoutSet layoutSet)
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

	private static ObjectDefinition _publishCustomObjectDefinition(String scope)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						_OBJECT_FIELD_NAME
					).objectFieldSettings(
						Collections.emptyList()
					).build()),
				scope);

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		return objectDefinition;
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	private LayoutPageTemplateEntry _addDisplayPageTemplate() throws Exception {
		return _addDisplayPageTemplate(_companyObjectDefinition);
	}

	private LayoutPageTemplateEntry _addDisplayPageTemplate(
			ObjectDefinition objectDefinition)
		throws Exception {

		return DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(objectDefinition.getClassName()), null, true,
			WorkflowConstants.STATUS_APPROVED);
	}

	private ObjectEntry _addObjectEntry() throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_companyObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addObjectEntry(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private void _assertRootElement(
			Layout layout, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, Element rootElement)
		throws Exception {

		_objectEntrySitemapURLProvider.visitLayout(
			rootElement, layout.getUuid(), _layoutSet, _themeDisplay);

		Assert.assertTrue(rootElement.hasContent());

		String[] availableLanguageIds = _getAvailableLanguageIds(
			objectDefinition);

		List<Element> elements = rootElement.elements();

		Assert.assertEquals(
			elements.toString(), availableLanguageIds.length, elements.size());

		String objectEntryFriendlyURL = StringUtil.toLowerCase(
			StringBundler.concat(
				StringPool.SLASH, _group.getGroupKey(),
				FriendlyURLResolverConstants.URL_SEPARATOR_OBJECT_ENTRY,
				objectEntry.getObjectEntryId()));

		for (Element element : elements) {
			String objectEntryLocalizedURL = element.elementText("loc");

			Assert.assertNotNull(objectEntryLocalizedURL);
			Assert.assertTrue(
				objectEntryLocalizedURL.endsWith(objectEntryFriendlyURL));
		}
	}

	private void _assertRootElementAsGuestUser(
			Layout layout, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		Role role = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getObjectEntryId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(
					_userLocalService.getGuestUser(_group.getCompanyId())));

			_assertRootElement(
				layout, objectDefinition, objectEntry, _getRootElement());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	private boolean _containsObjectEntry(
		ObjectEntry objectEntry, List<String> objectEntryURLs) {

		for (String objectEntryURL : objectEntryURLs) {
			if (objectEntryURL.endsWith(
					String.valueOf(objectEntry.getObjectEntryId()))) {

				return true;
			}
		}

		return false;
	}

	private String[] _getAvailableLanguageIds(
		ObjectDefinition objectDefinition) {

		Set<Locale> siteAvailableLocales = _language.getAvailableLocales(
			_group.getGroupId());

		if (SetUtil.isEmpty(siteAvailableLocales)) {
			return new String[0];
		}

		List<String> availableLanguageIds = new ArrayList<>();

		for (String availableLanguageId :
				objectDefinition.getAvailableLanguageIds()) {

			if (siteAvailableLocales.contains(
					LocaleUtil.fromLanguageId(availableLanguageId))) {

				availableLanguageIds.add(availableLanguageId);
			}
		}

		return ArrayUtil.toStringArray(availableLanguageIds);
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

	private void _updateLayoutSEOEntry(
			boolean canonicalURLEnabled, Layout layout)
		throws Exception {

		_layoutSEOEntryLocalService.updateLayoutSEOEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			layout.isPrivateLayout(), layout.getLayoutId(), canonicalURLEnabled,
			new HashMap<>(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _updateObjectDefinition(boolean active) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				_companyObjectDefinition.getObjectDefinitionId());

		objectDefinition.setActive(active);

		_objectDefinitionLocalService.updateObjectDefinition(objectDefinition);
	}

	private static final String _OBJECT_FIELD_NAME = StringUtil.randomId();

	private static final String _PID_SITEMAP_COMPANY_CONFIGURATION =
		"com.liferay.site.internal.configuration.SitemapCompanyConfiguration";

	private static CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;
	private static ObjectDefinition _companyObjectDefinition;
	private static ObjectDefinition _depotObjectDefinition;
	private static Group _group;
	private static LayoutSet _layoutSet;

	@Inject
	private static LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private static ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	private static ObjectDefinition _siteObjectDefinition;
	private static ThemeDisplay _themeDisplay;

	@DeleteAfterTestRun
	private Group _childGroup;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.site.provider.ObjectEntrySitemapURLProvider",
		type = SitemapURLProvider.class
	)
	private SitemapURLProvider _objectEntrySitemapURLProvider;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SAXReader _saxReader;

	@DeleteAfterTestRun
	private ObjectDefinition _systemObjectDefinition;

	@Inject
	private UserLocalService _userLocalService;

}