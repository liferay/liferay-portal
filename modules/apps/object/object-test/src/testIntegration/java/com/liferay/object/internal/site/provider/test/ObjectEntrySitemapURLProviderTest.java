/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.site.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
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
import com.liferay.portal.kernel.model.User;
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

		_initThemeDisplay();

		_objectDefinition = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectField"
				).objectFieldSettings(
					Collections.emptyList()
				).build()));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId());

		String objectDefinitionId = String.valueOf(
			_objectDefinition.getObjectDefinitionId());

		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId(),
				_PID_SITEMAP_COMPANY_CONFIGURATION,
				HashMapDictionaryBuilder.<String, Object>put(
					"companySitemapObjectDefinitionIds",
					new String[] {objectDefinitionId}
				).build());

		LayoutTestUtil.addTypePortletLayout(_group);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyConfigurationTemporarySwapper.close();
	}

	@Test
	public void testIsIncludeWithSystemObjectDefinition() throws Exception {
		_systemObjectDefinition =
			ObjectDefinitionTestUtil.publishSystemObjectDefinition();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"companySitemapObjectDefinitionIds",
							new String[] {
								String.valueOf(
									_systemObjectDefinition.
										getObjectDefinitionId())
							}
						).build())) {

			Assert.assertFalse(
				_objectEntrySitemapURLProvider.isInclude(
					TestPropsValues.getCompanyId(), _group.getGroupId()));

			// Sitemapable system object definition

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

		_assertRootElement(layout, _objectDefinition, objectEntry, rootElement);

		// Inactive object definition

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

		// Layout excluded from the sitemap

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
		Element rootElement = _getRootElement();

		ObjectEntry objectEntry = _addObjectEntry();

		_addObjectEntry();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate();

		Role role = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), _objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getObjectEntryId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		User guestUser = _userLocalService.getGuestUser(_group.getCompanyId());

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(guestUser));

			_assertRootElement(
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				_objectDefinition, objectEntry, rootElement);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
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

			// Inactive object definition

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

	private static void _initThemeDisplay() throws Exception {
		_themeDisplay = new ThemeDisplay();

		Company company = CompanyLocalServiceUtil.getCompany(
			_group.getCompanyId());

		_themeDisplay.setCompany(company);

		_themeDisplay.setLanguageId(_group.getDefaultLanguageId());
		_themeDisplay.setLayoutSet(_layoutSet);
		_themeDisplay.setLocale(
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
		_themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		_themeDisplay.setPortalDomain(company.getVirtualHostname());
		_themeDisplay.setPortalURL(company.getPortalURL(_group.getGroupId()));
		_themeDisplay.setRequest(new MockHttpServletRequest());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
		_themeDisplay.setSignedIn(true);
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(TestPropsValues.getUser());
	}

	private LayoutPageTemplateEntry _addDisplayPageTemplate() throws Exception {
		return DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(_objectDefinition.getClassName()), null,
			true, WorkflowConstants.STATUS_APPROVED);
	}

	private ObjectEntry _addObjectEntry() throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textObjectField", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
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
				_objectDefinition.getObjectDefinitionId());

		objectDefinition.setActive(active);

		_objectDefinitionLocalService.updateObjectDefinition(objectDefinition);
	}

	private static final String _PID_SITEMAP_COMPANY_CONFIGURATION =
		"com.liferay.site.internal.configuration.SitemapCompanyConfiguration";

	private static CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;
	private static Group _group;
	private static LayoutSet _layoutSet;

	@Inject
	private static LayoutSetLocalService _layoutSetLocalService;

	private static ObjectDefinition _objectDefinition;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	private static ThemeDisplay _themeDisplay;

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