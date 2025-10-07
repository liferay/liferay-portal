/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.initializer.extender.internal.test;

import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.type.CustomElementCET;
import com.liferay.client.extension.type.factory.CETFactory;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.service.CommerceNotificationTemplateLocalService;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPOptionCategoryLocalService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CPSpecificationOptionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotAppCustomization;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotAppCustomizationLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.headless.admin.taxonomy.dto.v1_0.Keyword;
import com.liferay.headless.admin.taxonomy.resource.v1_0.KeywordResource;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.dto.v1_0.Organization;
import com.liferay.headless.admin.user.dto.v1_0.OrganizationBrief;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.dto.v1_0.UserGroupBrief;
import com.liferay.headless.admin.user.resource.v1_0.AccountResource;
import com.liferay.headless.admin.user.resource.v1_0.OrganizationResource;
import com.liferay.headless.admin.user.resource.v1_0.UserAccountResource;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowDefinition;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowDefinitionResource;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AdminAccountGroup;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AdminAccountGroupResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductSpecificationResource;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderType;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderTypeResource;
import com.liferay.headless.delivery.dto.v1_0.BlogPosting;
import com.liferay.headless.delivery.dto.v1_0.SitePage;
import com.liferay.headless.delivery.resource.v1_0.BlogPostingResource;
import com.liferay.headless.delivery.resource.v1_0.SitePageResource;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.knowledge.base.util.comparator.KBArticlePriorityComparator;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.notification.rest.dto.v1_0.NotificationTemplate;
import com.liferay.notification.rest.resource.v1_0.NotificationTemplateResource;
import com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.resource.v1_0.ObjectRelationshipResource;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SettingsLocator;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.security.script.management.test.rule.ScriptManagementConfigurationTestRule;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.search.experiences.rest.dto.v1_0.GeneralConfiguration;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;
import com.liferay.search.experiences.rest.resource.v1_0.SXPBlueprintResource;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerFactory;
import com.liferay.site.initializer.SiteInitializerRegistry;
import com.liferay.site.initializer.SiteInitializerSerializer;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;

import jakarta.servlet.ServletContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class BundleSiteInitializerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			ScriptManagementConfigurationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUser());
		mockHttpServletRequest.setParameter(
			"currentURL", "http://www.liferay.com");

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();

		GroupLocalServiceUtil.deleteGroup(_group);

		// TODO We should not need to delete the object definition manually
		// because of DataGuardTestRule. However,
		// ObjectDefinitionLocalServiceImpl#deleteObjectDefinition checks
		// for PortalRunMode#isTestMode which is not returning true when the
		// DataGuardTestRule runs.

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_serviceContext.getCompanyId(), "C_TestObjectDefinition1");

		if (objectDefinition1 != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition1.getObjectDefinitionId());
		}

		ObjectDefinition objectDefinition2 =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_serviceContext.getCompanyId(), "C_TestObjectDefinition2");

		if (objectDefinition2 != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition2.getObjectDefinitionId());
		}

		ObjectDefinition objectDefinition3 =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_serviceContext.getCompanyId(), "C_TestObjectDefinition3");

		if (objectDefinition3 != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition3.getObjectDefinitionId());
		}

		ObjectDefinition objectDefinition4 =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_serviceContext.getCompanyId(), "C_TestObjectDefinition4");

		if (objectDefinition4 != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition4.getObjectDefinitionId());
		}

		ObjectDefinition objectDefinition5 =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_serviceContext.getCompanyId(), "C_TestObjectDefinition5");

		if (objectDefinition5 != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition5.getObjectDefinitionId());
		}
	}

	@Test
	public void testInitializeFromBundle() throws Exception {
		Bundle bundle1 = _getBundle(
			"/com.liferay.site.initializer.extender.test.bundle.1.jar");
		Bundle bundle2 = _getBundle(
			"/com.liferay.site.initializer.extender.test.bundle.2.jar");

		try {
			_test1(
				_siteInitializerRegistry.getSiteInitializer(
					bundle1.getSymbolicName()));
			_test2(
				_siteInitializerRegistry.getSiteInitializer(
					bundle2.getSymbolicName()));
		}
		finally {
			bundle1.uninstall();
			bundle2.uninstall();
		}
	}

	@Test
	public void testInitializeFromFile() throws Exception {
		File tempDir1 = _getTempDir(
			"/com.liferay.site.initializer.extender.test.bundle.1.jar");
		File tempDir2 = _getTempDir(
			"/com.liferay.site.initializer.extender.test.bundle.2.jar");

		try {
			_test1(
				_siteInitializerFactory.create(
					new File(tempDir1, "site-initializer"), null));
			_test2(
				_siteInitializerFactory.create(
					new File(tempDir2, "site-initializer"), null));
		}
		finally {
			FileUtil.deltree(tempDir1);
			FileUtil.deltree(tempDir2);
		}
	}

	@FeatureFlag("LPD-19870")
	@Test
	public void testSerialize() throws Exception {
		File tempDir1 = _getTempDir(
			"/com.liferay.site.initializer.extender.test.bundle.1.jar");

		try {
			SiteInitializer siteInitializer = _siteInitializerFactory.create(
				new File(tempDir1, "site-initializer"), null);

			siteInitializer.initialize(_group.getGroupId());

			File file = _siteInitializerSerializer.serialize(
				_group.getGroupId());

			Assert.assertNotNull(file);

			File tempFolderFile = FileUtil.createTempFolder();

			if (_log.isInfoEnabled()) {
				_log.info("Temp folder " + tempFolderFile);
			}

			FileUtil.unzip(file, tempFolderFile);
		}
		finally {
			FileUtil.deltree(tempDir1);
		}
	}

	private void _assertAccountGroupAssignments(
		AdminAccountGroup accountGroup, int accountGroupAssignmentsCount) {

		Assert.assertEquals(
			accountGroupAssignmentsCount,
			_accountGroupRelLocalService.
				getAccountGroupRelsCountByAccountGroupId(accountGroup.getId()));
	}

	private void _assertAccountGroups1() throws Exception {
		AdminAccountGroupResource.Builder accountGroupResourceBuilder =
			_adminAccountGroupResourcefactory.create();

		AdminAccountGroupResource accountGroupResource =
			accountGroupResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		AdminAccountGroup accountGroup =
			accountGroupResource.getAccountGroupByExternalReferenceCode(
				"TESTACCOUNTGROUP1");

		Assert.assertNotNull(accountGroup);
		Assert.assertEquals("Test Account Group 1", accountGroup.getName());

		_assertAccountGroupAssignments(accountGroup, 1);

		accountGroup =
			accountGroupResource.getAccountGroupByExternalReferenceCode(
				"TESTACCOUNTGROUP2");

		Assert.assertNotNull(accountGroup);
		Assert.assertEquals("Test Account Group 2", accountGroup.getName());

		_assertAccountGroupAssignments(accountGroup, 1);

		accountGroup =
			accountGroupResource.getAccountGroupByExternalReferenceCode(
				"TESTACCOUNTGROUP3");

		Assert.assertNotNull(accountGroup);
		Assert.assertEquals("Test Account Group 3", accountGroup.getName());

		_assertAccountGroupAssignments(accountGroup, 0);
	}

	private void _assertAccountGroups2() throws Exception {
		AdminAccountGroupResource.Builder accountGroupResourceBuilder =
			_adminAccountGroupResourcefactory.create();

		AdminAccountGroupResource accountGroupResource =
			accountGroupResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		AdminAccountGroup accountGroup =
			accountGroupResource.getAccountGroupByExternalReferenceCode(
				"TESTACCOUNTGROUP1");

		Assert.assertNotNull(accountGroup);
		Assert.assertEquals("Test Account Group 1", accountGroup.getName());

		_assertAccountGroupAssignments(accountGroup, 1);

		accountGroup =
			accountGroupResource.getAccountGroupByExternalReferenceCode(
				"TESTACCOUNTGROUP2");

		Assert.assertNotNull(accountGroup);
		Assert.assertEquals(
			"Test Account Group 2 Update", accountGroup.getName());

		_assertAccountGroupAssignments(accountGroup, 1);

		accountGroup =
			accountGroupResource.getAccountGroupByExternalReferenceCode(
				"TESTACCOUNTGROUP3");

		Assert.assertNotNull(accountGroup);
		Assert.assertEquals("Test Account Group 3", accountGroup.getName());

		_assertAccountGroupAssignments(accountGroup, 2);

		accountGroup =
			accountGroupResource.getAccountGroupByExternalReferenceCode(
				"TESTACCOUNTGROUP4");

		Assert.assertNotNull(accountGroup);
		Assert.assertEquals("Test Account Group 4", accountGroup.getName());

		_assertAccountGroupAssignments(accountGroup, 0);
	}

	private void _assertAccountOrganizationRelsCount(
			Organization organization, int organizationAssignmentsCount)
		throws Exception {

		Assert.assertEquals(
			organizationAssignmentsCount,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					GetterUtil.getLong(organization.getId())));
	}

	private void _assertAccounts1() throws Exception {
		AccountResource.Builder accountResourceBuilder =
			_accountResourceFactory.create();

		AccountResource accountResource = accountResourceBuilder.user(
			_serviceContext.fetchUser()
		).build();

		Account account1 = accountResource.getAccountByExternalReferenceCode(
			"TESTACCOUNT1");

		Assert.assertNotNull(account1);
		Assert.assertEquals("Test Account 1", account1.getName());
		Assert.assertEquals("business", account1.getTypeAsString());

		Account account2 = accountResource.getAccountByExternalReferenceCode(
			"TESTACCOUNT2");

		Assert.assertNotNull(account2);
		Assert.assertEquals("Test Account 2", account2.getName());
		Assert.assertEquals("guest", account2.getTypeAsString());

		Account account3 = accountResource.getAccountByExternalReferenceCode(
			"TESTACCOUNT3");

		Assert.assertNotNull(account3);
		Assert.assertEquals("Test Account 3", account3.getName());
		Assert.assertEquals("person", account3.getTypeAsString());
	}

	private void _assertAccounts2() throws Exception {
		AccountResource.Builder accountResourceBuilder =
			_accountResourceFactory.create();

		AccountResource accountResource = accountResourceBuilder.user(
			_serviceContext.fetchUser()
		).build();

		Account account1 = accountResource.getAccountByExternalReferenceCode(
			"TESTACCOUNT1");

		Assert.assertNotNull(account1);
		Assert.assertEquals("Test Account 1", account1.getName());
		Assert.assertEquals("business", account1.getTypeAsString());

		Account account2 = accountResource.getAccountByExternalReferenceCode(
			"TESTACCOUNT2");

		Assert.assertNotNull(account2);
		Assert.assertEquals("Test Account Guest", account2.getName());
		Assert.assertEquals("guest", account2.getTypeAsString());

		Account account3 = accountResource.getAccountByExternalReferenceCode(
			"TESTACCOUNT3");

		Assert.assertNotNull(account3);
		Assert.assertEquals("Test Account 3", account3.getName());
		Assert.assertEquals("person", account3.getTypeAsString());

		Account account4 = accountResource.getAccountByExternalReferenceCode(
			"TESTACCOUNT4");

		Assert.assertNotNull(account4);
		Assert.assertEquals("Test Account 4", account4.getName());
		Assert.assertEquals("person", account4.getTypeAsString());
	}

	private void _assertAssetCategories() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		AssetCategory testAssetCategory1 =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					"TESTASSETCATEGORY1", companyGroup.getGroupId());

		Assert.assertNotNull(testAssetCategory1);
		Assert.assertEquals(
			"Test Asset Category 1", testAssetCategory1.getName());

		AssetCategory testAssetCategory2 =
			_assetCategoryLocalService.fetchCategory(
				companyGroup.getGroupId(), testAssetCategory1.getCategoryId(),
				"Test Asset Category 2", testAssetCategory1.getVocabularyId());

		Assert.assertNotNull(testAssetCategory2);
		Assert.assertEquals(
			"TESTASSETCATEGORY2",
			testAssetCategory2.getExternalReferenceCode());

		AssetCategory testAssetCategory3 =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					"TESTASSETCATEGORY3", _group.getGroupId());

		Assert.assertNotNull(testAssetCategory3);
		Assert.assertEquals(
			"Test Asset Category 3", testAssetCategory3.getName());

		AssetCategory testAssetCategory4 =
			_assetCategoryLocalService.fetchCategory(
				_group.getGroupId(), testAssetCategory3.getCategoryId(),
				"Test Asset Category 4", testAssetCategory3.getVocabularyId());

		Assert.assertNotNull(testAssetCategory4);
		Assert.assertEquals(
			"TESTASSETCATEGORY4",
			testAssetCategory4.getExternalReferenceCode());
	}

	private void _assertAssetLinkEntries(
		long blogPostingId, long assetLinkEntriesCount) {

		List<AssetLink> links = _assetLinkLocalService.getLinks(
			_portal.getClassNameId(BlogsEntry.class), blogPostingId);

		Assert.assertNotNull(links);
		Assert.assertEquals(
			links.toString(), assetLinkEntriesCount, links.size());
	}

	private void _assertAssetListEntries() {
		List<AssetListEntry> assetListEntries =
			_assetListEntryLocalService.getAssetListEntries(
				_group.getGroupId());

		Assert.assertEquals(
			assetListEntries.toString(), 4, assetListEntries.size());

		AssetListEntry assetListEntry = assetListEntries.get(0);

		Assert.assertEquals(
			"Test Asset List Entry 1", assetListEntry.getTitle());
		Assert.assertEquals(
			"com.liferay.journal.model.JournalArticle",
			assetListEntry.getAssetEntryType());

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			_assetListEntrySegmentsEntryRelLocalService.
				fetchAssetListEntrySegmentsEntryRel(
					assetListEntry.getAssetListEntryId(), 0);

		String typeSettings = assetListEntrySegmentsEntryRel.getTypeSettings();

		Assert.assertTrue(typeSettings.contains("queryValues0=Test Keyword"));

		assetListEntry = assetListEntries.get(1);

		Assert.assertEquals(
			"Test Asset List Entry 2", assetListEntry.getTitle());
		Assert.assertEquals(
			"com.liferay.journal.model.JournalArticle",
			assetListEntry.getAssetEntryType());

		assetListEntry = assetListEntries.get(2);

		Assert.assertEquals(
			"Test Asset List Entry 3", assetListEntry.getTitle());
		Assert.assertEquals(
			"com.liferay.document.library.kernel.model.DLFileEntry",
			assetListEntry.getAssetEntryType());

		assetListEntry = assetListEntries.get(3);

		Assert.assertEquals(
			"Test Asset List Entry 4", assetListEntry.getTitle());
		Assert.assertTrue(
			StringUtil.startsWith(
				assetListEntry.getAssetEntryType(),
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION));
	}

	private void _assertAssetVocabularies() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		AssetVocabulary testAssetVocabulary1 =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				companyGroup.getGroupId(), "Test Asset Vocabulary 1");

		Assert.assertNotNull(testAssetVocabulary1);
		Assert.assertEquals(
			"TESTASSETVOCABULARY1",
			testAssetVocabulary1.getExternalReferenceCode());

		AssetVocabulary testAssetVocabulary2 =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				_group.getGroupId(), "Test Asset Vocabulary 2");

		Assert.assertNotNull(testAssetVocabulary2);
		Assert.assertEquals(
			"TESTASSETVOCABULARY2",
			testAssetVocabulary2.getExternalReferenceCode());

		_assertAssetCategories();
	}

	private void _assertBlogPostings1() throws Exception {
		BlogPostingResource.Builder blogPostingResourceBuilder =
			_blogPostingResourceFactory.create();

		BlogPostingResource blogPostingResource =
			blogPostingResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		BlogPosting blogPosting =
			blogPostingResource.getSiteBlogPostingByExternalReferenceCode(
				_group.getGroupId(), "TESTBLOGPOSTING1");

		Assert.assertEquals("Test Headline 1", blogPosting.getHeadline());
		Assert.assertTrue(blogPosting.getImage() != null);

		_assertAssetLinkEntries(blogPosting.getId(), 2);

		blogPosting =
			blogPostingResource.getSiteBlogPostingByExternalReferenceCode(
				_group.getGroupId(), "TESTBLOGPOSTING2");

		Assert.assertEquals("Test Headline 2", blogPosting.getHeadline());
		Assert.assertTrue(blogPosting.getImage() == null);

		_assertAssetLinkEntries(blogPosting.getId(), 2);
	}

	private void _assertBlogPostings2() throws Exception {
		BlogPostingResource.Builder blogPostingResourceBuilder =
			_blogPostingResourceFactory.create();

		BlogPostingResource blogPostingResource =
			blogPostingResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		BlogPosting blogPosting =
			blogPostingResource.getSiteBlogPostingByExternalReferenceCode(
				_group.getGroupId(), "TESTBLOGPOSTING1");

		Assert.assertEquals("Test Headline 1", blogPosting.getHeadline());
		Assert.assertTrue(blogPosting.getImage() != null);

		_assertAssetLinkEntries(blogPosting.getId(), 2);

		blogPosting =
			blogPostingResource.getSiteBlogPostingByExternalReferenceCode(
				_group.getGroupId(), "TESTBLOGPOSTING2");

		Assert.assertEquals(
			"Test Headline 2 Update", blogPosting.getHeadline());
		Assert.assertTrue(blogPosting.getImage() != null);

		_assertAssetLinkEntries(blogPosting.getId(), 4);

		blogPosting =
			blogPostingResource.getSiteBlogPostingByExternalReferenceCode(
				_group.getGroupId(), "TESTBLOGPOSTING3");

		Assert.assertEquals("Test Headline 3", blogPosting.getHeadline());
		Assert.assertTrue(blogPosting.getImage() == null);

		_assertAssetLinkEntries(blogPosting.getId(), 2);
	}

	private void _assertClientExtension() throws Exception {
		ClientExtensionEntry clientExtensionEntry =
			_clientExtensionEntryLocalService.
				fetchClientExtensionEntryByExternalReferenceCode(
					"TESTCLIENTEXTENSION1", _group.getCompanyId());

		Assert.assertNotNull(clientExtensionEntry);

		CustomElementCET customElementCET =
			(CustomElementCET)_cetFactory.create(clientExtensionEntry, true);

		Assert.assertEquals(
			"liferay-test-remote-app", customElementCET.getHTMLElementName());
		Assert.assertEquals(
			"category.client-extensions",
			customElementCET.getPortletCategoryName());
	}

	private void _assertCommerceCatalogs1() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.
				fetchCommerceCatalogByExternalReferenceCode(
					"TESTCOMMERCECATALOG1", _group.getCompanyId());

		Assert.assertNotNull(commerceCatalog);
		Assert.assertEquals(_group.getGroupKey(), commerceCatalog.getName());

		commerceCatalog =
			_commerceCatalogLocalService.
				fetchCommerceCatalogByExternalReferenceCode(
					"TESTCOMMERCECATALOG2", _group.getCompanyId());

		Assert.assertNotNull(commerceCatalog);
		Assert.assertEquals(
			"Test Commerce Catalog 2", commerceCatalog.getName());

		commerceCatalog =
			_commerceCatalogLocalService.
				fetchCommerceCatalogByExternalReferenceCode(
					"TESTCOMMERCECATALOG3", _group.getCompanyId());

		Assert.assertNotNull(commerceCatalog);
		Assert.assertEquals(_group.getGroupKey(), commerceCatalog.getName());

		Company company = _companyLocalService.getCompany(
			_serviceContext.getCompanyId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), _group.getGroupKey());

		Assert.assertNotNull(assetVocabulary);

		_assertCPDefinition();
		_assertCPOption();
	}

	private void _assertCommerceCatalogs2() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.
				fetchCommerceCatalogByExternalReferenceCode(
					"TESTCOMMERCECATALOG1", _group.getCompanyId());

		Assert.assertNotNull(commerceCatalog);
		Assert.assertEquals(
			"Test Commerce Catalog 1", commerceCatalog.getName());

		commerceCatalog =
			_commerceCatalogLocalService.
				fetchCommerceCatalogByExternalReferenceCode(
					"TESTCOMMERCECATALOG2", _group.getCompanyId());

		Assert.assertNotNull(commerceCatalog);
		Assert.assertEquals(
			"Test Commerce Catalog 2 Update", commerceCatalog.getName());

		commerceCatalog =
			_commerceCatalogLocalService.
				fetchCommerceCatalogByExternalReferenceCode(
					"TESTCOMMERCECATALOG3", _group.getCompanyId());

		Assert.assertNotNull(commerceCatalog);
		Assert.assertEquals(
			"Test Commerce Catalog 3", commerceCatalog.getName());
	}

	private void _assertCommerceChannel1() throws Exception {
		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				_group.getGroupId());

		Assert.assertNotNull(commerceChannel);
		Assert.assertEquals(
			"TESTCOMMERCECHANNEL1", commerceChannel.getExternalReferenceCode());
		Assert.assertEquals("Test Commerce Channel", commerceChannel.getName());
		Assert.assertEquals("site", commerceChannel.getType());
		Assert.assertEquals("USD", commerceChannel.getCommerceCurrencyCode());

		_assertCommerceChannelConfiguration1(commerceChannel);
		_assertCommerceNotificationTemplate(commerceChannel);
		_assertDefaultCPDisplayLayout1(commerceChannel);
	}

	private void _assertCommerceChannel2() throws Exception {
		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				_group.getGroupId());

		Assert.assertNotNull(commerceChannel);
		Assert.assertEquals(
			"TESTCOMMERCECHANNEL1", commerceChannel.getExternalReferenceCode());
		Assert.assertEquals(
			"Test Commerce Channel Update", commerceChannel.getName());
		Assert.assertEquals("site", commerceChannel.getType());
		Assert.assertEquals("EUR", commerceChannel.getCommerceCurrencyCode());

		_assertCommerceChannelConfiguration2(commerceChannel);
		_assertDefaultCPDisplayLayout2(commerceChannel);
	}

	private void _assertCommerceChannelConfiguration1(
			CommerceChannel commerceChannel)
		throws Exception {

		SettingsLocator settingsLocator2 = new GroupServiceSettingsLocator(
			commerceChannel.getGroupId(),
			CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			settingsLocator2);

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		Assert.assertEquals(
			"2", modifiableSettings.getValue("commerceSiteType", null));

		SettingsLocator settingsLocator1 = new GroupServiceSettingsLocator(
			commerceChannel.getGroupId(),
			CommerceConstants.SERVICE_NAME_COMMERCE_ORDER);

		settings = FallbackKeysSettingsUtil.getSettings(settingsLocator1);

		modifiableSettings = settings.getModifiableSettings();

		Assert.assertEquals(
			"true",
			modifiableSettings.getValue(
				"checkoutRequestedDeliveryDateEnabled", null));
		Assert.assertEquals(
			"false", modifiableSettings.getValue("guestCheckoutEnabled", null));
		Assert.assertEquals(
			"true", modifiableSettings.getValue("hideShippingPriceZero", null));
		Assert.assertEquals(
			"true",
			modifiableSettings.getValue("showPurchaseOrderNumber", null));

		settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				commerceChannel.getGroupId(),
				CommerceConstants.SERVICE_NAME_COMMERCE_ORDER_FIELDS));

		modifiableSettings = settings.getModifiableSettings();

		Assert.assertEquals(
			"3", modifiableSettings.getValue("accountCartMaxAllowed", null));
	}

	private void _assertCommerceChannelConfiguration2(
			CommerceChannel commerceChannel)
		throws Exception {

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				commerceChannel.getGroupId(),
				CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		Assert.assertEquals(
			"1", modifiableSettings.getValue("commerceSiteType", null));

		settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				commerceChannel.getGroupId(),
				CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		modifiableSettings = settings.getModifiableSettings();

		Assert.assertEquals(
			"true",
			modifiableSettings.getValue(
				"checkoutRequestedDeliveryDateEnabled", null));
		Assert.assertEquals(
			"true", modifiableSettings.getValue("guestCheckoutEnabled", null));
		Assert.assertEquals(
			"false",
			modifiableSettings.getValue("hideShippingPriceZero", null));
		Assert.assertEquals(
			"true",
			modifiableSettings.getValue("showPurchaseOrderNumber", null));

		SettingsLocator settingsLocator = new GroupServiceSettingsLocator(
			commerceChannel.getGroupId(),
			CommerceConstants.SERVICE_NAME_COMMERCE_ORDER_FIELDS);

		settings = FallbackKeysSettingsUtil.getSettings(settingsLocator);

		modifiableSettings = settings.getModifiableSettings();

		Assert.assertEquals(
			"5", modifiableSettings.getValue("accountCartMaxAllowed", null));
	}

	private void _assertCommerceInventoryWarehouse() {
		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseLocalService.
				fetchCommerceInventoryWarehouseByExternalReferenceCode(
					"TESTCOMMERCEINVENTORYWAREHOUSE1", _group.getCompanyId());

		Assert.assertNotNull(commerceInventoryWarehouse);
		Assert.assertEquals(
			"Test Commerce Warehouse",
			commerceInventoryWarehouse.getName(LocaleUtil.getSiteDefault()));
	}

	private void _assertCommerceNotificationTemplate(
			CommerceChannel commerceChannel)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_group.getCompanyId(), "C_TestObjectDefinition1");

		Assert.assertNotNull(objectDefinition);

		List<CommerceNotificationTemplate> commerceNotificationTemplates =
			_commerceNotificationTemplateLocalService.
				getCommerceNotificationTemplates(
					commerceChannel.getGroupId(),
					objectDefinition.getClassName() + "#create", true);

		CommerceNotificationTemplate commerceNotificationTemplate =
			commerceNotificationTemplates.get(0);

		Assert.assertNotNull(commerceNotificationTemplate);
		Assert.assertEquals(
			"Test Commerce Notification Template",
			commerceNotificationTemplate.getName());
	}

	private void _assertCommerceOrderType1() throws Exception {
		OrderTypeResource.Builder builder = _orderTypeResourceFactory.create();

		OrderTypeResource orderTypeResource = builder.user(
			_serviceContext.fetchUser()
		).build();

		OrderType orderType =
			orderTypeResource.getOrderTypeByExternalReferenceCode(
				"TESTCOMMERCEORDERTYPE1");

		Assert.assertNotNull(orderType);

		Map<String, String> orderTypeName = orderType.getName();

		Assert.assertEquals(
			"Test Commerce Order Type 1", orderTypeName.get("en_US"));

		Assert.assertFalse(orderType.getActive());

		orderType = orderTypeResource.getOrderTypeByExternalReferenceCode(
			"TESTCOMMERCEORDERTYPE2");

		Assert.assertNotNull(orderType);

		orderTypeName = orderType.getName();

		Assert.assertEquals(
			"Test Commerce Order Type 2", orderTypeName.get("en_US"));

		Assert.assertTrue(orderType.getActive());
	}

	private void _assertCommerceOrderType2() throws Exception {
		OrderTypeResource.Builder builder = _orderTypeResourceFactory.create();

		OrderTypeResource orderTypeResource = builder.user(
			_serviceContext.fetchUser()
		).build();

		OrderType orderType =
			orderTypeResource.getOrderTypeByExternalReferenceCode(
				"TESTCOMMERCEORDERTYPE1");

		Assert.assertNotNull(orderType);

		Map<String, String> orderTypeName = orderType.getName();

		Assert.assertEquals(
			"Test Commerce Order Type 1", orderTypeName.get("en_US"));

		Assert.assertFalse(orderType.getActive());

		orderType = orderTypeResource.getOrderTypeByExternalReferenceCode(
			"TESTCOMMERCEORDERTYPE2");

		Assert.assertNotNull(orderType);

		orderTypeName = orderType.getName();

		Assert.assertEquals(
			"Test Commerce Order Type 2 Update", orderTypeName.get("en_US"));

		Assert.assertTrue(orderType.getActive());

		orderType = orderTypeResource.getOrderTypeByExternalReferenceCode(
			"TESTCOMMERCEORDERTYPE3");

		Assert.assertNotNull(orderType);

		orderTypeName = orderType.getName();

		Assert.assertEquals(
			"Test Commerce Order Type 3", orderTypeName.get("en_US"));

		Assert.assertFalse(orderType.getActive());
	}

	private void _assertCommerceSpecificationProducts1() throws Exception {
		CPSpecificationOption cpSpecificationOption =
			_cpSpecificationOptionLocalService.fetchCPSpecificationOption(
				_serviceContext.getCompanyId(), "test-product-specification-1");

		Assert.assertNotNull(cpSpecificationOption);
		Assert.assertFalse(cpSpecificationOption.getCPOptionCategoryId() > 0);
	}

	private void _assertCommerceSpecificationProducts2() throws Exception {
		CPSpecificationOption cpSpecificationOption =
			_cpSpecificationOptionLocalService.fetchCPSpecificationOption(
				_serviceContext.getCompanyId(), "test-product-specification-1");

		Assert.assertNotNull(cpSpecificationOption);
		Assert.assertTrue(cpSpecificationOption.getCPOptionCategoryId() > 0);
	}

	private void _assertCPDefinition() throws Exception {
		CPDefinition cpDefinition =
			_cpDefinitionLocalService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					"TESTCOMMERCEPRODUCT1", _group.getCompanyId());

		Assert.assertNotNull(cpDefinition);
		Assert.assertEquals("Test Commerce Product", cpDefinition.getName());

		_assertCPDefinitionSpecificationOptionValue(cpDefinition, 1);

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpDefinitionLocalService.getDefaultImageCPAttachmentFileEntry(
				cpDefinition.getCPDefinitionId());

		Assert.assertNotNull(cpAttachmentFileEntry);

		FileEntry fileEntry = cpAttachmentFileEntry.fetchFileEntry();

		Assert.assertEquals(
			"test_commerce_product.png", fileEntry.getFileName());
	}

	private void _assertCPDefinitionSpecificationOptionValue(
			CPDefinition cpDefinition, int cpDefinitionValuesCount)
		throws Exception {

		Assert.assertEquals(
			cpDefinitionValuesCount,
			_cpDefinitionSpecificationOptionValueLocalService.
				getCPDefinitionSpecificationOptionValuesCount(
					cpDefinition.getCPDefinitionId(), null));
	}

	private void _assertCPInstanceProperties() throws Exception {
		CPDefinition cpDefinition =
			_cpDefinitionLocalService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					"TESTCOMMERCEPRODUCT1", _group.getCompanyId());

		CPInstance cpInstance1 = _cpInstanceLocalService.getCPInstance(
			cpDefinition.getCPDefinitionId(), "TEST VALUE 1");

		Assert.assertNotNull(cpInstance1);

		BigDecimal actualPrice = cpInstance1.getPrice();

		Assert.assertEquals(60.0, actualPrice.doubleValue(), 0.0001);

		BigDecimal actualPromoPrice = cpInstance1.getPromoPrice();

		Assert.assertEquals(25.0, actualPromoPrice.doubleValue(), 0.0001);

		CPInstance cpInstance2 = _cpInstanceLocalService.getCPInstance(
			cpDefinition.getCPDefinitionId(), "TEST VALUE 2");

		Assert.assertNotNull(cpInstance2);
		Assert.assertTrue(cpInstance2.isSubscriptionEnabled());
	}

	private void _assertCPOption() throws Exception {
		CPOption cpOption1 = _cpOptionLocalService.fetchCPOption(
			_group.getCompanyId(), "test-option-1");

		Assert.assertNotNull(cpOption1);
		Assert.assertEquals(
			"Test Option 1", cpOption1.getName(LocaleUtil.getSiteDefault()));

		CPOption cpOption2 = _cpOptionLocalService.fetchCPOption(
			_group.getCompanyId(), "test-option-2");

		Assert.assertNotNull(cpOption2);
		Assert.assertEquals(
			"Test Option 2", cpOption2.getName(LocaleUtil.getSiteDefault()));

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					"TESTCOMMERCEPRODUCT1", _group.getCompanyId());

		Assert.assertNotNull(cpDefinition);

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			cpDefinition.getCPDefinitionOptionRels();

		Assert.assertEquals(
			cpDefinitionOptionRels.toString(), 2,
			cpDefinitionOptionRels.size());
	}

	private void _assertCPOptionCategory() throws Exception {
		CPOptionCategory cpOptionCategory =
			_cpOptionCategoryLocalService.fetchCPOptionCategory(
				_serviceContext.getCompanyId(),
				"test-commerce-specification-key-1");

		Assert.assertNotNull(cpOptionCategory);
		Assert.assertEquals(1.0, cpOptionCategory.getPriority(), 0);
		Assert.assertEquals(
			"Test Commerce Specification 1",
			cpOptionCategory.getTitle(LocaleUtil.getSiteDefault()));
	}

	private void _assertDataDefinition1() throws Exception {
		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		DataDefinition dataDefinition =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeByDataDefinitionKey(
					_serviceContext.getScopeGroupId(), "journal",
					"test-data-definition-1");

		Map<String, Object> description = dataDefinition.getDescription();

		Assert.assertEquals(
			"Test Data Definition Description 1", description.get("en_US"));

		Map<String, Object> name = dataDefinition.getName();

		Assert.assertEquals("Test Data Definition Name 1", name.get("en_US"));

		dataDefinition =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeByDataDefinitionKey(
					_serviceContext.getScopeGroupId(), "journal",
					"test-data-definition-2");

		description = dataDefinition.getDescription();

		Assert.assertEquals(
			"Test Data Definition Description 2", description.get("en_US"));

		name = dataDefinition.getName();

		Assert.assertEquals("Test Data Definition Name 2", name.get("en_US"));
	}

	private void _assertDataDefinition2() throws Exception {
		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		DataDefinition dataDefinition =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeByDataDefinitionKey(
					_serviceContext.getScopeGroupId(), "journal",
					"test-data-definition-1");

		Map<String, Object> description = dataDefinition.getDescription();

		Assert.assertEquals(
			"Test Data Definition Description 1", description.get("en_US"));

		Map<String, Object> name = dataDefinition.getName();

		Assert.assertEquals("Test Data Definition Name 1", name.get("en_US"));

		dataDefinition =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeByDataDefinitionKey(
					_serviceContext.getScopeGroupId(), "journal",
					"test-data-definition-2");

		description = dataDefinition.getDescription();

		Assert.assertEquals(
			"Test Data Definition Description 2 Update",
			description.get("en_US"));

		name = dataDefinition.getName();

		Assert.assertEquals(
			"Test Data Definition Name 2 Update", name.get("en_US"));
	}

	private void _assertDDMStructure() {
		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			_group.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			"TEST DDM STRUCTURE NAME");

		Assert.assertNotNull(ddmStructure);
		Assert.assertTrue(ddmStructure.hasField("aField"));
	}

	private void _assertDDMTemplate1() {
		DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(DDMStructure.class.getName()),
			"TEST DDM TEMPLATE KEY 1");

		Assert.assertNotNull(ddmTemplate);
		Assert.assertEquals(
			"Test DDM Template Name 1",
			ddmTemplate.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("${aField.getData()}", ddmTemplate.getScript());

		ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(TemplateEntry.class.getName()),
			"TEST INFORMATION DDM TEMPLATE KEY");

		Assert.assertNotNull(ddmTemplate);
		Assert.assertEquals(
			"Test Information DDM Template Name",
			ddmTemplate.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("${entries?size}", ddmTemplate.getScript());

		TemplateEntry templateEntry =
			_templateEntryLocalService.fetchTemplateEntryByDDMTemplateId(
				ddmTemplate.getTemplateId());

		String infoItemClassName = templateEntry.getInfoItemClassName();

		Assert.assertFalse(
			infoItemClassName.contains(
				"[$OBJECT_DEFINITION_CLASS_NAME:TestObjectDefinition1$]"));

		ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(AssetEntry.class.getName()),
			"TEST WIDGET DDM TEMPLATE KEY");

		Assert.assertNotNull(ddmTemplate);
		Assert.assertEquals(
			"Test Widget DDM Template Name",
			ddmTemplate.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("${entries?size}", ddmTemplate.getScript());
	}

	private void _assertDDMTemplate2() {
		DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(DDMStructure.class.getName()),
			"TEST DDM TEMPLATE KEY 1");

		Assert.assertNotNull(ddmTemplate);
		Assert.assertEquals(
			"Test DDM Template Name 1 Update",
			ddmTemplate.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("${entries?size}", ddmTemplate.getScript());

		ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(DDMStructure.class.getName()),
			"TEST DDM TEMPLATE KEY 2");

		Assert.assertNotNull(ddmTemplate);
		Assert.assertEquals(
			"Test DDM Template Name 2",
			ddmTemplate.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("${aField.getData()}", ddmTemplate.getScript());

		ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(TemplateEntry.class.getName()),
			"TEST INFORMATION DDM TEMPLATE KEY");

		Assert.assertNotNull(ddmTemplate);
		Assert.assertEquals(
			"Test Information DDM Template Name",
			ddmTemplate.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("${entries?size}", ddmTemplate.getScript());

		TemplateEntry templateEntry =
			_templateEntryLocalService.fetchTemplateEntryByDDMTemplateId(
				ddmTemplate.getTemplateId());

		String infoItemClassName = templateEntry.getInfoItemClassName();

		Assert.assertFalse(
			infoItemClassName.contains(
				"[$OBJECT_DEFINITION_CLASS_NAME:TestObjectDefinition1$]"));

		ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(AssetEntry.class.getName()),
			"TEST WIDGET DDM TEMPLATE KEY");

		Assert.assertNotNull(ddmTemplate);
		Assert.assertEquals(
			"Test Widget DDM Template Name",
			ddmTemplate.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("${entries?size}", ddmTemplate.getScript());
	}

	private void _assertDefaultCPDisplayLayout1(CommerceChannel commerceChannel)
		throws Exception {

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				commerceChannel.getGroupId(),
				CPConstants.RESOURCE_NAME_CP_DISPLAY_LAYOUT));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		String productLayoutUuid = modifiableSettings.getValue(
			"productLayoutUuid", null);

		Assert.assertNotNull(productLayoutUuid);

		Layout publicLayout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/test-public-layout");

		Assert.assertEquals(productLayoutUuid, publicLayout.getUuid());
	}

	private void _assertDefaultCPDisplayLayout2(CommerceChannel commerceChannel)
		throws Exception {

		SettingsLocator settingsLocator = new GroupServiceSettingsLocator(
			commerceChannel.getGroupId(),
			CPConstants.RESOURCE_NAME_CP_DISPLAY_LAYOUT);

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			settingsLocator);

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		String productLayoutUuid = modifiableSettings.getValue(
			"productLayoutUuid", null);

		Assert.assertNotNull(productLayoutUuid);

		Layout publicLayout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/test-public-child-layout");

		Assert.assertEquals(productLayoutUuid, publicLayout.getUuid());
	}

	private void _assertDepotEntries1() throws Exception {
		List<DepotEntry> depotEntries =
			_depotEntryLocalService.getGroupConnectedDepotEntries(
				_group.getGroupId(), DepotConstants.TYPE_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(depotEntries.toString(), 2, depotEntries.size());

		List<DepotAppCustomization> depotAppCustomizations =
			_depotAppCustomizationLocalService.getDepotAppCustomizations(
				depotEntries.get(
					0
				).getDepotEntryId());

		Assert.assertTrue(
			depotAppCustomizations.get(
				0
			).getEnabled());

		depotAppCustomizations =
			_depotAppCustomizationLocalService.getDepotAppCustomizations(
				depotEntries.get(
					1
				).getDepotEntryId());

		Assert.assertFalse(
			depotAppCustomizations.get(
				0
			).getEnabled());
	}

	private void _assertDepotEntries2() throws Exception {
		List<DepotEntry> depotEntries =
			_depotEntryLocalService.getGroupConnectedDepotEntries(
				_group.getGroupId(), DepotConstants.TYPE_ANY, -1, -1);

		Assert.assertEquals(depotEntries.toString(), 3, depotEntries.size());

		List<DepotAppCustomization> depotAppCustomizations =
			_depotAppCustomizationLocalService.getDepotAppCustomizations(
				depotEntries.get(
					0
				).getDepotEntryId());

		Assert.assertFalse(
			depotAppCustomizations.get(
				0
			).getEnabled());

		depotAppCustomizations =
			_depotAppCustomizationLocalService.getDepotAppCustomizations(
				depotEntries.get(
					1
				).getDepotEntryId());

		Assert.assertFalse(
			depotAppCustomizations.get(
				0
			).getEnabled());

		depotAppCustomizations =
			_depotAppCustomizationLocalService.getDepotAppCustomizations(
				depotEntries.get(
					2
				).getDepotEntryId());

		Assert.assertTrue(
			depotAppCustomizations.get(
				0
			).getEnabled());
	}

	private void _assertDLFileEntry1() throws Exception {
		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getFileEntry(
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"Table of Contents.md");

		String string = new String(
			StreamUtil.toByteArray(
				_dlFileEntryLocalService.getFileAsStream(
					dlFileEntry.getFileEntryId(), dlFileEntry.getVersion())));

		Assert.assertTrue(string.contains("## Old Testament"));
		Assert.assertTrue(string.contains("1. Genesis"));
		Assert.assertTrue(string.contains("## New Testament"));
		Assert.assertTrue(string.contains("1. Revelation"));
	}

	private void _assertDLFileEntry2() throws Exception {
		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getFileEntry(
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"Table of Contents.md");

		String string = new String(
			StreamUtil.toByteArray(
				_dlFileEntryLocalService.getFileAsStream(
					dlFileEntry.getFileEntryId(), dlFileEntry.getVersion())));

		Assert.assertTrue(string.contains("## Old Testament"));
		Assert.assertTrue(string.contains("1. Genesis"));
		Assert.assertTrue(string.contains("## New Testament"));
		Assert.assertTrue(string.contains("1. Revelation"));
		Assert.assertTrue(string.contains("## Content Update"));
		Assert.assertTrue(string.contains("1. Test Update"));

		DLFolder dlFolder = _dlFolderLocalService.fetchFolder(
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"Old Testament");

		Assert.assertNotNull(dlFolder);

		dlFileEntry = _dlFileEntryLocalService.getFileEntry(
			_group.getGroupId(), dlFolder.getFolderId(), "Genesis.txt");

		Assert.assertNotNull(dlFileEntry);

		string = new String(
			StreamUtil.toByteArray(
				_dlFileEntryLocalService.getFileAsStream(
					dlFileEntry.getFileEntryId(), dlFileEntry.getVersion())));

		Assert.assertTrue(string.isEmpty());
	}

	private void _assertExpandoColumns1() {
		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			_serviceContext.getCompanyId(),
			"com.liferay.commerce.product.model.CPDefinition");

		Assert.assertEquals(
			1.5, expandoBridge.getAttributeDefault("Test Expando Column 1"));
		Assert.assertEquals(
			"Test Default Value",
			expandoBridge.getAttributeDefault("Test Expando Column 2"));
		Assert.assertNull(expandoBridge.getAttribute("Test Expando Column 3"));

		UnicodeProperties unicodeProperties =
			expandoBridge.getAttributeProperties("Test Expando Column 1");

		Assert.assertTrue(unicodeProperties.isEmpty());

		unicodeProperties = expandoBridge.getAttributeProperties(
			"Test Expando Column 2");

		Assert.assertTrue(unicodeProperties.isEmpty());

		expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			_serviceContext.getCompanyId(),
			"com.liferay.commerce.model.CommerceOrderItem");

		Assert.assertTrue(
			ArrayUtil.containsAll(
				new double[] {2.0, 2.5},
				(double[])expandoBridge.getAttributeDefault(
					"Test Expando Column 3")));

		unicodeProperties = expandoBridge.getAttributeProperties(
			"Test Expando Column 3");

		Assert.assertFalse(unicodeProperties.isEmpty());
		Assert.assertFalse(
			GetterUtil.getBoolean(unicodeProperties.getProperty("secret")));

		expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			_serviceContext.getCompanyId(),
			"com.liferay.portal.kernel.model.User");

		Assert.assertTrue(
			ArrayUtil.containsAll(
				new int[] {100, 200},
				(int[])expandoBridge.getAttributeDefault(
					"Test Expando Column 4")));

		unicodeProperties = expandoBridge.getAttributeProperties(
			"Test Expando Column 4");

		Assert.assertTrue(unicodeProperties.isEmpty());
	}

	private void _assertExpandoColumns2() {
		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			_serviceContext.getCompanyId(),
			"com.liferay.commerce.product.model.CPDefinition");

		Assert.assertNotNull(expandoBridge);
		Assert.assertEquals(
			1.5, expandoBridge.getAttributeDefault("Test Expando Column 1"));
		Assert.assertEquals(
			"Test Default Value Update",
			expandoBridge.getAttributeDefault("Test Expando Column 2"));
		Assert.assertNull(expandoBridge.getAttribute("Test Expando Column 3"));

		UnicodeProperties unicodeProperties =
			expandoBridge.getAttributeProperties("Test Expando Column 1");

		Assert.assertTrue(unicodeProperties.isEmpty());

		unicodeProperties = expandoBridge.getAttributeProperties(
			"Test Expando Column 2");

		Assert.assertTrue(unicodeProperties.isEmpty());

		expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			_serviceContext.getCompanyId(),
			"com.liferay.commerce.model.CommerceOrderItem");

		Assert.assertTrue(
			ArrayUtil.containsAll(
				new double[] {2.0, 2.5, 3.0},
				(double[])expandoBridge.getAttributeDefault(
					"Test Expando Column 3")));

		unicodeProperties = expandoBridge.getAttributeProperties(
			"Test Expando Column 3");

		Assert.assertFalse(unicodeProperties.isEmpty());
		Assert.assertTrue(
			GetterUtil.getBoolean(unicodeProperties.getProperty("secret")));

		expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			_serviceContext.getCompanyId(),
			"com.liferay.portal.kernel.model.User");

		Assert.assertTrue(
			ArrayUtil.containsAll(
				new int[] {100, 250, 300},
				(int[])expandoBridge.getAttributeDefault(
					"Test Expando Column 4")));
		Assert.assertTrue(
			ArrayUtil.containsAll(
				new String[] {"value1", "value2"},
				(String[])expandoBridge.getAttributeDefault(
					"Test Expando Column 5")));

		unicodeProperties = expandoBridge.getAttributeProperties(
			"Test Expando Column 4");

		Assert.assertTrue(unicodeProperties.isEmpty());

		unicodeProperties = expandoBridge.getAttributeProperties(
			"Test Expando Column 5");

		Assert.assertTrue(unicodeProperties.isEmpty());
	}

	private void _assertExpandoValues1() throws Exception {
		CPDefinition cpDefinition =
			_cpDefinitionLocalService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					"TESTCOMMERCEPRODUCT1", _group.getCompanyId());

		ExpandoBridge expandoBridge = cpDefinition.getExpandoBridge();

		Assert.assertEquals(
			0.1, expandoBridge.getAttribute("Test Expando Column 1"));
		Assert.assertEquals(
			"Test Value", expandoBridge.getAttribute("Test Expando Column 2"));
	}

	private void _assertExpandoValues2() throws Exception {
		CPDefinition cpDefinition =
			_cpDefinitionLocalService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					"TESTCOMMERCEPRODUCT1", _group.getCompanyId());

		ExpandoBridge expandoBridge = cpDefinition.getExpandoBridge();

		Assert.assertEquals(
			0.1, expandoBridge.getAttribute("Test Expando Column 1"));
		Assert.assertEquals(
			"Test Value Update",
			expandoBridge.getAttribute("Test Expando Column 2"));

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					"TESTSITENAVIGATIONMENUITEM1", _group.getGroupId());

		expandoBridge = siteNavigationMenuItem.getExpandoBridge();

		Map<Locale, String[]> expandoBridgeAttribute =
			(Map<Locale, String[]>)expandoBridge.getAttribute(
				"Test Expando Column 6");

		Assert.assertTrue(
			ArrayUtil.containsAll(
				new String[] {"Test Value 1", "Test Value 2"},
				expandoBridgeAttribute.get(LocaleUtil.getSiteDefault())));
	}

	private void _assertFragmentEntries1() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			_serviceContext.getCompanyId());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(
				companyGroup.getGroupId(), "test-fragment-entry-1");

		Assert.assertNotNull(fragmentEntry);
		Assert.assertEquals("Test Fragment Entry 1", fragmentEntry.getName());

		fragmentEntry = _fragmentEntryLocalService.fetchFragmentEntry(
			_group.getGroupId(), "test-fragment-entry-2");

		Assert.assertNotNull(fragmentEntry);
		Assert.assertEquals("Test Fragment Entry 2", fragmentEntry.getName());

		fragmentEntry = _fragmentEntryLocalService.fetchFragmentEntry(
			_group.getGroupId(), "dialect-button");

		Assert.assertNull(fragmentEntry);
	}

	private void _assertFragmentEntries2() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			_serviceContext.getCompanyId());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(
				companyGroup.getGroupId(), "test-fragment-entry-1");

		Assert.assertNotNull(fragmentEntry);
		Assert.assertEquals("Test Fragment Entry 1", fragmentEntry.getName());

		fragmentEntry = _fragmentEntryLocalService.fetchFragmentEntry(
			_group.getGroupId(), "test-fragment-entry-2");

		Assert.assertNotNull(fragmentEntry);
		Assert.assertEquals("Test Fragment Entry 2", fragmentEntry.getName());

		fragmentEntry = _fragmentEntryLocalService.fetchFragmentEntry(
			_group.getGroupId(), "dialect-button");

		Assert.assertNotNull(fragmentEntry);
		Assert.assertEquals("Dialect Button", fragmentEntry.getName());
	}

	private void _assertJournalArticles1() throws Exception {
		JournalArticle journalArticle1 =
			_journalArticleLocalService.fetchArticle(
				_group.getGroupId(), "TEST-JOURNAL-ARTICLE-1");

		Assert.assertNotNull(journalArticle1);
		Assert.assertEquals(
			"TEST DDM TEMPLATE KEY 1", journalArticle1.getDDMTemplateKey());
		Assert.assertEquals(
			"Test Journal Article 1", journalArticle1.getTitle());

		JournalArticle journalArticle2 =
			_journalArticleLocalService.fetchArticle(
				_group.getGroupId(), "TEST-JOURNAL-ARTICLE-2");

		Assert.assertNotNull(journalArticle2);
		Assert.assertEquals(
			"TEST DDM TEMPLATE KEY 1", journalArticle2.getDDMTemplateKey());
		Assert.assertEquals(
			"Test Journal Article 2", journalArticle2.getTitle());

		List<JournalFolder> journalFolders = _journalFolderService.getFolders(
			_group.getGroupId());

		Assert.assertEquals(
			journalFolders.toString(), 2, journalFolders.size());

		JournalFolder journalFolder1 = journalFolders.get(0);

		Assert.assertEquals(
			"TESTJOURNALFOLDER1", journalFolder1.getExternalReferenceCode());
		Assert.assertEquals("Test Journal Folder 1", journalFolder1.getName());

		JournalFolder journalFolder2 = journalFolders.get(1);

		Assert.assertEquals(
			"TESTJOURNALFOLDER2", journalFolder2.getExternalReferenceCode());
		Assert.assertEquals("Test Journal Folder 2", journalFolder2.getName());
	}

	private void _assertJournalArticles2() throws Exception {
		JournalArticle journalArticle1 =
			_journalArticleLocalService.fetchArticle(
				_group.getGroupId(), "TEST-JOURNAL-ARTICLE-1");

		Assert.assertNotNull(journalArticle1);
		Assert.assertEquals(
			"TEST DDM TEMPLATE KEY 1", journalArticle1.getDDMTemplateKey());
		Assert.assertEquals(
			"Test Journal Article 1", journalArticle1.getTitle());

		JournalArticle journalArticle2 =
			_journalArticleLocalService.fetchArticle(
				_group.getGroupId(), "TEST-JOURNAL-ARTICLE-2");

		Assert.assertNotNull(journalArticle2);
		Assert.assertEquals(
			"TEST DDM TEMPLATE KEY 1", journalArticle2.getDDMTemplateKey());
		Assert.assertEquals(
			"Test Journal Article 2 Update", journalArticle2.getTitle());

		JournalArticle journalArticle3 =
			_journalArticleLocalService.fetchArticle(
				_group.getGroupId(), "TEST-JOURNAL-ARTICLE-3");

		Assert.assertNotNull(journalArticle3);
		Assert.assertTrue(
			Validator.isNull(journalArticle3.getDDMTemplateKey()));
		Assert.assertEquals(
			"Test Journal Article 3", journalArticle3.getTitle());
		Assert.assertEquals(
			LocaleUtil.toLanguageId(
				_portal.getSiteDefaultLocale(_group.getGroupId())),
			journalArticle3.getDefaultLanguageId());

		List<JournalFolder> journalFolders = _journalFolderService.getFolders(
			_group.getGroupId());

		Assert.assertEquals(
			journalFolders.toString(), 3, journalFolders.size());

		JournalFolder journalFolder1 = journalFolders.get(0);

		Assert.assertEquals(
			"TESTJOURNALFOLDER1", journalFolder1.getExternalReferenceCode());
		Assert.assertEquals("Test Journal Folder 1", journalFolder1.getName());

		JournalFolder journalFolder2 = journalFolders.get(1);

		Assert.assertEquals(
			"TESTJOURNALFOLDER2", journalFolder2.getExternalReferenceCode());
		Assert.assertEquals(
			"Test Journal Folder 2 Update", journalFolder2.getName());

		JournalFolder journalFolder3 = journalFolders.get(2);

		Assert.assertEquals(
			"TESTJOURNALFOLDER3", journalFolder3.getExternalReferenceCode());
		Assert.assertEquals("Test Journal Folder 3", journalFolder3.getName());
	}

	private void _assertKBArticles() throws Exception {
		KBFolder kbFolder = _kbFolderLocalService.getKBFolderByUrlTitle(
			_group.getGroupId(), KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"test-kb-folder-name");

		Assert.assertEquals(
			"TESTKBFOLDER1", kbFolder.getExternalReferenceCode());
		Assert.assertEquals("Test KB Folder Name", kbFolder.getName());

		List<KBArticle> kbFolderKBArticles =
			_kbArticleLocalService.getKBArticles(
				_group.getGroupId(), kbFolder.getKbFolderId(),
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				KBArticlePriorityComparator.getInstance(true));

		Assert.assertEquals(
			kbFolderKBArticles.toString(), 1, kbFolderKBArticles.size());

		KBArticle kbArticle1 = kbFolderKBArticles.get(0);

		Assert.assertEquals(
			"TESTKBARTICLE1", kbArticle1.getExternalReferenceCode());
		Assert.assertEquals("Test KB Article 1 Title", kbArticle1.getTitle());
		Assert.assertEquals(
			"This is the body for Test KB Article 1.", kbArticle1.getContent());

		List<KBArticle> kbArticleKBArticles =
			_kbArticleLocalService.getKBArticles(
				_group.getGroupId(), kbArticle1.getResourcePrimKey(),
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				KBArticlePriorityComparator.getInstance(true));

		Assert.assertEquals(
			kbArticleKBArticles.toString(), 2, kbArticleKBArticles.size());

		KBArticle kbArticle2 = kbArticleKBArticles.get(0);

		Assert.assertEquals(
			"TESTKBARTICLE2", kbArticle2.getExternalReferenceCode());
		Assert.assertEquals("Test KB Article 2 Title", kbArticle2.getTitle());
		Assert.assertEquals(
			"This is the body for Test KB Article 2.", kbArticle2.getContent());

		KBArticle kbArticle3 = kbArticleKBArticles.get(1);

		Assert.assertEquals(
			"TESTKBARTICLE3", kbArticle3.getExternalReferenceCode());
		Assert.assertEquals("Test KB Article 3 Title", kbArticle3.getTitle());
		Assert.assertEquals(
			"This is the body for Test KB Article 3.", kbArticle3.getContent());
	}

	private void _assertKeywords1() throws Exception {
		KeywordResource.Builder keywordResourceBuilder =
			_keywordResourceFactory.create();

		KeywordResource keywordResource = keywordResourceBuilder.user(
			_serviceContext.fetchUser()
		).build();

		Page<Keyword> keywordsPage = keywordResource.getSiteKeywordsPage(
			_group.getGroupId(), null, null, null, null, null);

		Assert.assertEquals(
			keywordsPage.toString(), 2, keywordsPage.getTotalCount());

		Group group = _groupLocalService.fetchGroup(
			_serviceContext.getCompanyId(), "Test Depot Entry 1");

		keywordsPage = keywordResource.getAssetLibraryKeywordsPage(
			group.getGroupId(), null, null, null, null, null);

		Assert.assertEquals(
			keywordsPage.toString(), 2, keywordsPage.getTotalCount());

		group = _groupLocalService.fetchGroup(
			_serviceContext.getCompanyId(), "Test Depot Entry 2");

		keywordsPage = keywordResource.getAssetLibraryKeywordsPage(
			group.getGroupId(), null, null, null, null, null);

		Assert.assertEquals(
			keywordsPage.toString(), 1, keywordsPage.getTotalCount());
	}

	private void _assertKeywords2() throws Exception {
		KeywordResource.Builder keywordResourceBuilder =
			_keywordResourceFactory.create();

		KeywordResource keywordResource = keywordResourceBuilder.user(
			_serviceContext.fetchUser()
		).build();

		Page<Keyword> keywordsPage = keywordResource.getSiteKeywordsPage(
			_group.getGroupId(), null, null, null, null, null);

		Assert.assertEquals(
			keywordsPage.toString(), 3, keywordsPage.getTotalCount());

		Group group = _groupLocalService.fetchGroup(
			_serviceContext.getCompanyId(), "Test Depot Entry 1");

		keywordsPage = keywordResource.getAssetLibraryKeywordsPage(
			group.getGroupId(), null, null, null, null, null);

		Assert.assertEquals(
			keywordsPage.toString(), 2, keywordsPage.getTotalCount());

		group = _groupLocalService.fetchGroup(
			_serviceContext.getCompanyId(), "Test Depot Entry 2");

		keywordsPage = keywordResource.getAssetLibraryKeywordsPage(
			group.getGroupId(), null, null, null, null, null);

		Assert.assertEquals(
			keywordsPage.toString(), 2, keywordsPage.getTotalCount());
	}

	private void _assertLayoutPageTemplateEntries() throws Exception {

		// Test Display Page Template

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				"Test Display Page Template",
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);

		Assert.assertNotNull(layoutPageTemplateEntry);
		Assert.assertEquals(
			"Test Display Page Template", layoutPageTemplateEntry.getName());

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		Assert.assertNotNull(editableValuesJSONObject);

		JSONObject editableFragmentEntryProcessorJSONObject =
			editableValuesJSONObject.getJSONObject(
				"com.liferay.fragment.entry.processor.editable." +
					"EditableFragmentEntryProcessor");

		Assert.assertNotNull(editableFragmentEntryProcessorJSONObject);

		JSONObject linkJSONObject =
			editableFragmentEntryProcessorJSONObject.getJSONObject("link");

		Assert.assertNotNull(linkJSONObject);

		JSONObject configJSONObject = linkJSONObject.getJSONObject("config");

		Assert.assertNotNull(configJSONObject);

		JSONObject layoutJSONObject = configJSONObject.getJSONObject("layout");

		Assert.assertNotNull(layoutJSONObject);
		Assert.assertEquals(
			_group.getGroupId(), layoutJSONObject.getLong("groupId"));
		Assert.assertFalse(layoutJSONObject.getBoolean("privateLayout"));
		Assert.assertEquals("/home", layoutJSONObject.getString("value"));

		// Test Master Page

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				"Test Master Page",
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

		Assert.assertNotNull(layoutPageTemplateEntry);
		Assert.assertEquals(
			"Test Master Page", layoutPageTemplateEntry.getName());
	}

	private void _assertLayouts1() throws Exception {
		_assertPrivateLayouts1();
		_assertPublicLayouts1();
	}

	private void _assertLayouts2() throws Exception {
		_assertPrivateLayouts2();
		_assertPublicLayouts2();
	}

	private void _assertLayoutSets1() throws Exception {
		LayoutSet privateLayoutSet = _layoutSetLocalService.fetchLayoutSet(
			_group.getGroupId(), true);

		Assert.assertNotNull(privateLayoutSet);

		Theme privateTheme = privateLayoutSet.getTheme();

		Assert.assertEquals("Classic", privateTheme.getName());

		UnicodeProperties privateLayoutSetUnicodeProperties =
			privateLayoutSet.getSettingsProperties();

		Assert.assertTrue(
			GetterUtil.getBoolean(
				privateLayoutSetUnicodeProperties.getProperty(
					"lfr-theme:regular:show-footer")));
		Assert.assertTrue(
			GetterUtil.getBoolean(
				privateLayoutSetUnicodeProperties.getProperty(
					"lfr-theme:regular:show-header")));

		LayoutSet publicLayoutSet = _layoutSetLocalService.fetchLayoutSet(
			_group.getGroupId(), false);

		Assert.assertNotNull(publicLayoutSet);

		Theme publicTheme = publicLayoutSet.getTheme();

		Assert.assertEquals("Classic", publicTheme.getName());

		UnicodeProperties publicLayoutSetUnicodeProperties =
			publicLayoutSet.getSettingsProperties();

		Assert.assertFalse(
			GetterUtil.getBoolean(
				publicLayoutSetUnicodeProperties.getProperty(
					"lfr-theme:regular:show-footer")));
		Assert.assertFalse(
			GetterUtil.getBoolean(
				publicLayoutSetUnicodeProperties.getProperty(
					"lfr-theme:regular:show-header")));
	}

	private void _assertLayoutSets2() throws Exception {
		LayoutSet privateLayoutSet = _layoutSetLocalService.fetchLayoutSet(
			_group.getGroupId(), true);

		Assert.assertNotNull(privateLayoutSet);

		Theme privateTheme = privateLayoutSet.getTheme();

		Assert.assertEquals("Dialect", privateTheme.getName());

		UnicodeProperties privateLayoutSetUnicodeProperties =
			privateLayoutSet.getSettingsProperties();

		Assert.assertTrue(
			GetterUtil.getBoolean(
				privateLayoutSetUnicodeProperties.getProperty(
					"lfr-theme:regular:show-footer")));
		Assert.assertFalse(
			GetterUtil.getBoolean(
				privateLayoutSetUnicodeProperties.getProperty(
					"lfr-theme:regular:show-header")));

		LayoutSet publicLayoutSet = _layoutSetLocalService.fetchLayoutSet(
			_group.getGroupId(), false);

		Assert.assertNotNull(publicLayoutSet);

		Theme publicTheme = publicLayoutSet.getTheme();

		Assert.assertEquals("Dialect", publicTheme.getName());

		UnicodeProperties publicLayoutSetUnicodeProperties =
			publicLayoutSet.getSettingsProperties();

		Assert.assertFalse(
			GetterUtil.getBoolean(
				publicLayoutSetUnicodeProperties.getProperty(
					"lfr-theme:regular:show-footer")));
		Assert.assertTrue(
			GetterUtil.getBoolean(
				publicLayoutSetUnicodeProperties.getProperty(
					"lfr-theme:regular:show-header")));
	}

	private void _assertLayoutStructureItems(
		LayoutPageTemplateStructure layoutPageTemplateStructure,
		int layoutStructureItemsCount, long segmentsExperienceId) {

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		List<LayoutStructureItem> layoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		Assert.assertEquals(
			layoutStructureItems.toString(), layoutStructureItemsCount,
			layoutStructureItems.size());
	}

	private void _assertLayoutUtilityPageEntries() {
		LayoutUtilityPageEntry defaultLayoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				fetchDefaultLayoutUtilityPageEntry(
					_group.getGroupId(),
					LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND);

		Assert.assertNotNull(defaultLayoutUtilityPageEntry);
		Assert.assertEquals(
			"Test Default Layout Utility Page Entry",
			defaultLayoutUtilityPageEntry.getName());

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				fetchLayoutUtilityPageEntryByExternalReferenceCode(
					"test-layout-utility-page-entry", _group.getGroupId());

		Assert.assertNotNull(layoutUtilityPageEntry);
		Assert.assertEquals(
			"Test Layout Utility Page Entry", layoutUtilityPageEntry.getName());
		Assert.assertEquals(
			LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND,
			layoutUtilityPageEntry.getType());
		Assert.assertFalse(
			layoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry());
	}

	private void _assertListTypeDefinitions1() throws Exception {
		ListTypeDefinitionResource.Builder listTypeDefinitionResourceBuilder =
			_listTypeDefinitionResourceFactory.create();

		ListTypeDefinitionResource listTypeDefinitionResource =
			listTypeDefinitionResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		ListTypeDefinition listTypeDefinition1 =
			listTypeDefinitionResource.
				getListTypeDefinitionByExternalReferenceCode(
					"TESTLISTTYPEDEFINITION1");

		Assert.assertEquals(
			"Test List Type Definition 1", listTypeDefinition1.getName());

		ListTypeEntry[] listTypeEntries1 =
			listTypeDefinition1.getListTypeEntries();

		ListTypeEntry listTypeEntry1 = listTypeEntries1[0];

		Assert.assertEquals("testlisttypeentry1", listTypeEntry1.getKey());
		Assert.assertEquals("Test List Type Entry 1", listTypeEntry1.getName());

		ListTypeEntry listTypeEntry2 = listTypeEntries1[1];

		Assert.assertEquals("testlisttypeentry2", listTypeEntry2.getKey());
		Assert.assertEquals("Test List Type Entry 2", listTypeEntry2.getName());

		ListTypeDefinition listTypeDefinition2 =
			listTypeDefinitionResource.
				getListTypeDefinitionByExternalReferenceCode(
					"TESTLISTTYPEDEFINITION2");

		Assert.assertEquals(
			"Test List Type Definition 2", listTypeDefinition2.getName());

		ListTypeEntry[] listTypeEntries2 =
			listTypeDefinition2.getListTypeEntries();

		ListTypeEntry listTypeEntry3 = listTypeEntries2[0];

		Assert.assertEquals("testlisttypeentry3", listTypeEntry3.getKey());
		Assert.assertEquals("Test List Type Entry 3", listTypeEntry3.getName());

		ListTypeEntry listTypeEntry4 = listTypeEntries2[1];

		Assert.assertEquals("testlisttypeentry4", listTypeEntry4.getKey());
		Assert.assertEquals("Test List Type Entry 4", listTypeEntry4.getName());
	}

	private void _assertListTypeDefinitions2() throws Exception {
		ListTypeDefinitionResource.Builder listTypeDefinitionResourceBuilder =
			_listTypeDefinitionResourceFactory.create();

		ListTypeDefinitionResource listTypeDefinitionResource =
			listTypeDefinitionResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		ListTypeDefinition listTypeDefinition1 =
			listTypeDefinitionResource.
				getListTypeDefinitionByExternalReferenceCode(
					"TESTLISTTYPEDEFINITION1");

		Assert.assertEquals(
			"Test List Type Definition 1 Update",
			listTypeDefinition1.getName());

		ListTypeEntry[] listTypeEntries1 =
			listTypeDefinition1.getListTypeEntries();

		ListTypeEntry listTypeEntry1 = listTypeEntries1[0];

		Assert.assertEquals("testlisttypeentry1", listTypeEntry1.getKey());
		Assert.assertEquals(
			"Test List Type Entry 1 Update", listTypeEntry1.getName());

		ListTypeEntry listTypeEntry2 = listTypeEntries1[1];

		Assert.assertEquals("testlisttypeentry2", listTypeEntry2.getKey());
		Assert.assertEquals(
			"Test List Type Entry 2 Update", listTypeEntry2.getName());

		ListTypeDefinition listTypeDefinition2 =
			listTypeDefinitionResource.
				getListTypeDefinitionByExternalReferenceCode(
					"TESTLISTTYPEDEFINITION2");

		Assert.assertEquals(
			"Test List Type Definition 2", listTypeDefinition2.getName());

		ListTypeEntry[] listTypeEntries2 =
			listTypeDefinition2.getListTypeEntries();

		ListTypeEntry listTypeEntry3 = listTypeEntries2[0];

		Assert.assertEquals("testlisttypeentry3", listTypeEntry3.getKey());
		Assert.assertEquals("Test List Type Entry 3", listTypeEntry3.getName());

		ListTypeEntry listTypeEntry4 = listTypeEntries2[1];

		Assert.assertEquals("testlisttypeentry4", listTypeEntry4.getKey());
		Assert.assertEquals("Test List Type Entry 4", listTypeEntry4.getName());

		ListTypeDefinition listTypeDefinition3 =
			listTypeDefinitionResource.
				getListTypeDefinitionByExternalReferenceCode(
					"TESTLISTTYPEDEFINITION3");

		Assert.assertEquals(
			"Test List Type Definition 3", listTypeDefinition3.getName());

		ListTypeEntry[] listTypeEntries3 =
			listTypeDefinition3.getListTypeEntries();

		ListTypeEntry listTypeEntry5 = listTypeEntries3[0];

		Assert.assertEquals("testlisttypeentry5", listTypeEntry5.getKey());
		Assert.assertEquals("Test List Type Entry 5", listTypeEntry5.getName());

		ListTypeEntry listTypeEntry6 = listTypeEntries3[1];

		Assert.assertEquals("testlisttypeentry6", listTypeEntry6.getKey());
		Assert.assertEquals("Test List Type Entry 6", listTypeEntry6.getName());
	}

	private void _assertNotificationTemplate1() throws Exception {
		NotificationTemplateResource.Builder
			notificationTemplateResourceBuilder =
				_notificationTemplateResourceFactory.create();

		NotificationTemplateResource notificationTemplateResource =
			notificationTemplateResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		NotificationTemplate notificationTemplate =
			notificationTemplateResource.
				getNotificationTemplateByExternalReferenceCode(
					"TESTNOTIFICATIONTEMPLATE1");

		Assert.assertNotNull(notificationTemplate);

		Map<String, String> bodyMap = notificationTemplate.getBody();

		Assert.assertEquals(
			StringBundler.concat(
				"<p>\n\tThis is a template email for Test Notification ",
				"Template 1.\n\tCompany ID: ", _group.getCompanyId(),
				"\n\tGroup Friendly URL: ", _group.getFriendlyURL(),
				"\n\tGroup ID: ", _serviceContext.getScopeGroupId(),
				"\n\tGroup Key: ", _group.getGroupKey(), "\n\tPortal URL: ",
				_serviceContext.getPortalURL(), "\n</p>"),
			bodyMap.get("en_US"));

		Assert.assertEquals(
			"Test Notification Template 1", notificationTemplate.getName());

		Map<String, String> subjectMap = notificationTemplate.getSubject();

		Assert.assertTrue(
			Objects.equals(
				subjectMap.get("en_US"),
				StringUtil.getTitleCase(subjectMap.get("en_US"), true, "DXP")));

		notificationTemplate =
			notificationTemplateResource.
				getNotificationTemplateByExternalReferenceCode(
					"TESTNOTIFICATIONTEMPLATE2");

		Assert.assertNotNull(notificationTemplate);

		bodyMap = notificationTemplate.getBody();

		Assert.assertEquals(
			"<p>\n\tThis is a template email for Test Notification Template " +
				"2.\n</p>",
			bodyMap.get("en_US"));

		Assert.assertEquals(
			"Test Notification Template 2", notificationTemplate.getName());

		subjectMap = notificationTemplate.getSubject();

		Assert.assertTrue(
			Objects.equals(
				subjectMap.get("en_US"),
				StringUtil.getTitleCase(subjectMap.get("en_US"), true, "DXP")));
	}

	private void _assertNotificationTemplate2() throws Exception {
		NotificationTemplateResource.Builder
			notificationTemplateResourceBuilder =
				_notificationTemplateResourceFactory.create();

		NotificationTemplateResource notificationTemplateResource =
			notificationTemplateResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		NotificationTemplate notificationTemplate =
			notificationTemplateResource.
				getNotificationTemplateByExternalReferenceCode(
					"TESTNOTIFICATIONTEMPLATE1");

		Assert.assertNotNull(notificationTemplate);

		Map<String, String> bodyMap = notificationTemplate.getBody();

		Assert.assertEquals(
			"<p>\n\tThis is a template email for Test Notification Template " +
				"1.\n</p>",
			bodyMap.get("en_US"));

		Assert.assertEquals(
			"Test Notification Template 1", notificationTemplate.getName());

		Map<String, String> subjectMap = notificationTemplate.getSubject();

		Assert.assertTrue(
			Objects.equals(
				subjectMap.get("en_US"),
				StringUtil.getTitleCase(subjectMap.get("en_US"), true, "DXP")));

		notificationTemplate =
			notificationTemplateResource.
				getNotificationTemplateByExternalReferenceCode(
					"TESTNOTIFICATIONTEMPLATE2");

		Assert.assertNotNull(notificationTemplate);

		bodyMap = notificationTemplate.getBody();

		Assert.assertEquals(
			"<p>\n\tThis is a template email for Test Notification Template " +
				"2 Update.\n</p>",
			bodyMap.get("en_US"));

		Assert.assertEquals(
			"Test Notification Template 2 Update",
			notificationTemplate.getName());

		subjectMap = notificationTemplate.getSubject();

		Assert.assertTrue(
			Objects.equals(
				subjectMap.get("en_US"),
				StringUtil.getTitleCase(subjectMap.get("en_US"), true, "DXP")));

		notificationTemplate =
			notificationTemplateResource.
				getNotificationTemplateByExternalReferenceCode(
					"TESTNOTIFICATIONTEMPLATE3");

		Assert.assertNotNull(notificationTemplate);

		bodyMap = notificationTemplate.getBody();

		Assert.assertEquals(
			"<p>\n\tThis is a template email for Test Notification Template " +
				"3.\n</p>",
			bodyMap.get("en_US"));

		Assert.assertEquals(
			"Test Notification Template 3", notificationTemplate.getName());

		subjectMap = notificationTemplate.getSubject();

		Assert.assertTrue(
			Objects.equals(
				subjectMap.get("en_US"),
				StringUtil.getTitleCase(subjectMap.get("en_US"), true, "DXP")));
	}

	private void _assertObjectActions(
		int objectActionsCount, ObjectDefinition objectDefinition) {

		List<ObjectAction> objectActions =
			_objectActionLocalService.getObjectActions(
				objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(
			objectActions.toString(), objectActionsCount, objectActions.size());

		for (ObjectAction objectAction : objectActions) {
			String objectActionExecutorKey =
				objectAction.getObjectActionExecutorKey();

			UnicodeProperties parametersUnicodeProperties =
				objectAction.getParametersUnicodeProperties();

			if (Objects.equals(
					objectActionExecutorKey,
					ObjectActionExecutorConstants.KEY_GROOVY)) {

				String script = parametersUnicodeProperties.get("script");

				Assert.assertNotNull(script);
			}
			else if (Objects.equals(
						objectActionExecutorKey,
						ObjectActionExecutorConstants.KEY_WEBHOOK)) {

				String secret = parametersUnicodeProperties.get("secret");
				String url = parametersUnicodeProperties.get("url");

				Assert.assertNotNull(secret);
				Assert.assertNotNull(url);
			}
		}
	}

	private void _assertObjectDefinitions1() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_group.getCompanyId(), "C_TestObjectDefinition1");

		Assert.assertFalse(objectDefinition.isAccountEntryRestricted());
		Assert.assertFalse(objectDefinition.isSystem());
		Assert.assertEquals(
			objectDefinition.getStatus(), WorkflowConstants.STATUS_APPROVED);

		_assertObjectActions(4, objectDefinition);
		_assertObjectEntries(_group.getGroupId(), objectDefinition, 0);
		_assertObjectFields(objectDefinition, 13);
		_assertObjectRelationships1(objectDefinition, _serviceContext);

		objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			_group.getCompanyId(), "C_TestObjectDefinition2");

		Assert.assertFalse(objectDefinition.isAccountEntryRestricted());
		Assert.assertFalse(objectDefinition.isSystem());
		Assert.assertEquals(
			objectDefinition.getStatus(), WorkflowConstants.STATUS_APPROVED);

		_assertObjectActions(2, objectDefinition);
		_assertObjectEntries(_group.getGroupId(), objectDefinition, 0);
		_assertObjectFields(objectDefinition, 11);

		objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			_group.getCompanyId(), "C_TestObjectDefinition3");

		Assert.assertEquals(
			objectDefinition.getScope(),
			ObjectDefinitionConstants.SCOPE_COMPANY);
		Assert.assertFalse(objectDefinition.isSystem());
		Assert.assertEquals(
			objectDefinition.getStatus(), WorkflowConstants.STATUS_APPROVED);

		_assertObjectActions(0, objectDefinition);
		_assertObjectEntries(0, objectDefinition, 5);
		_assertObjectFields(objectDefinition, 10);

		objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			_group.getCompanyId(), "C_TestObjectDefinition4");

		Assert.assertTrue(objectDefinition.isAccountEntryRestricted());
		Assert.assertTrue(
			objectDefinition.getAccountEntryRestrictedObjectFieldId() > 0);
		Assert.assertFalse(objectDefinition.isSystem());
		Assert.assertEquals(
			objectDefinition.getStatus(), WorkflowConstants.STATUS_APPROVED);
	}

	private void _assertObjectDefinitions2() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_group.getCompanyId(), "C_TestObjectDefinition1");

		Assert.assertFalse(objectDefinition.isAccountEntryRestricted());
		Assert.assertFalse(objectDefinition.isSystem());
		Assert.assertEquals(
			objectDefinition.getStatus(), WorkflowConstants.STATUS_APPROVED);

		_assertObjectActions(4, objectDefinition);
		_assertObjectEntries(_group.getGroupId(), objectDefinition, 0);
		_assertObjectFields(objectDefinition, 13);
		_assertObjectRelationships2(objectDefinition, _serviceContext);

		objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			_group.getCompanyId(), "C_TestObjectDefinition2");

		Assert.assertFalse(objectDefinition.isAccountEntryRestricted());
		Assert.assertEquals(
			"Test Object Definition 2 Update",
			objectDefinition.getLabel(LocaleUtil.getSiteDefault()));
		Assert.assertEquals(
			"Test Object Definitions 2 Update",
			objectDefinition.getPluralLabel(LocaleUtil.getSiteDefault()));
		Assert.assertFalse(objectDefinition.isSystem());
		Assert.assertEquals(
			objectDefinition.getStatus(), WorkflowConstants.STATUS_APPROVED);

		_assertObjectActions(2, objectDefinition);
		_assertObjectEntries(_group.getGroupId(), objectDefinition, 0);
		_assertObjectFields(objectDefinition, 11);

		objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			_group.getCompanyId(), "C_TestObjectDefinition3");

		Assert.assertFalse(objectDefinition.isAccountEntryRestricted());
		Assert.assertEquals(
			objectDefinition.getScope(),
			ObjectDefinitionConstants.SCOPE_COMPANY);
		Assert.assertFalse(objectDefinition.isSystem());
		Assert.assertEquals(
			objectDefinition.getStatus(), WorkflowConstants.STATUS_APPROVED);

		_assertObjectActions(0, objectDefinition);
		_assertObjectEntries(0, objectDefinition, 5);
		_assertObjectFields(objectDefinition, 10);

		objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			_group.getCompanyId(), "C_TestObjectDefinition4");

		Assert.assertTrue(objectDefinition.isAccountEntryRestricted());
		Assert.assertTrue(
			objectDefinition.getAccountEntryRestrictedObjectFieldId() > 0);
		Assert.assertTrue(objectDefinition.isDefaultStorageType());
		Assert.assertFalse(objectDefinition.isSystem());
		Assert.assertEquals(
			objectDefinition.getStatus(), WorkflowConstants.STATUS_APPROVED);

		objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			_group.getCompanyId(), "C_TestObjectDefinition5");

		Assert.assertTrue(objectDefinition.isAccountEntryRestricted());
		Assert.assertTrue(
			objectDefinition.getAccountEntryRestrictedObjectFieldId() > 0);
		Assert.assertTrue(objectDefinition.isDefaultStorageType());
		Assert.assertFalse(objectDefinition.isSystem());
		Assert.assertEquals(
			objectDefinition.getStatus(), WorkflowConstants.STATUS_APPROVED);
	}

	private void _assertObjectEntries(
			long groupId, ObjectDefinition objectDefinition,
			int objectEntriesCount)
		throws Exception {

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				groupId, objectDefinition.getObjectDefinitionId(), -1, -1);

		Assert.assertNotNull(objectEntries);
		Assert.assertEquals(
			objectEntries.toString(), objectEntriesCount, objectEntries.size());

		for (ObjectEntry objectEntry : objectEntries) {
			Map<String, Serializable> objectEntryValues =
				objectEntry.getValues();

			for (Map.Entry<String, Serializable> entry :
					objectEntryValues.entrySet()) {

				Assert.assertTrue(Validator.isNotNull(entry.getValue()));
			}
		}
	}

	private void _assertObjectFields(
		ObjectDefinition objectDefinition, int objectFieldsCount) {

		Assert.assertEquals(
			objectFieldsCount,
			_objectFieldLocalService.getObjectFieldsCount(
				objectDefinition.getObjectDefinitionId()));
	}

	private void _assertObjectFolders1() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				"TESTOBJECTFOLDER1", _group.getCompanyId());

		Assert.assertEquals(
			"Test Object Folder 1",
			objectFolder.getLabel(LocaleUtil.getSiteDefault()));

		objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				"TESTOBJECTFOLDER2", _group.getCompanyId());

		Assert.assertEquals(
			"Test Object Folder 2",
			objectFolder.getLabel(LocaleUtil.getSiteDefault()));
	}

	private void _assertObjectFolders2() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				"TESTOBJECTFOLDER1", _group.getCompanyId());

		Assert.assertEquals(
			"Test Object Folder 1 Update",
			objectFolder.getLabel(LocaleUtil.getSiteDefault()));

		objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				"TESTOBJECTFOLDER2", _group.getCompanyId());

		Assert.assertEquals(
			"Test Object Folder 2 Update",
			objectFolder.getLabel(LocaleUtil.getSiteDefault()));
	}

	private void _assertObjectRelationships1(
			ObjectDefinition objectDefinition, ServiceContext serviceContext)
		throws Exception {

		ObjectRelationshipResource.Builder objectRelationshipResourceBuilder =
			_objectRelationshipResourceFactory.create();

		ObjectRelationshipResource objectRelationshipResource =
			objectRelationshipResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(), "User");

		Page<ObjectRelationship> page1 =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					objectDefinition1.getObjectDefinitionId(), null,
					objectRelationshipResource.toFilter("name eq 'testOR1'"),
					null, null);

		ObjectRelationship existingObjectRelationship1 = page1.fetchFirstItem();

		Assert.assertEquals(
			"TestObjectDefinition1",
			existingObjectRelationship1.getObjectDefinitionName2());

		ObjectRelationship.Type objectRelationshipType1 =
			existingObjectRelationship1.getType();

		Assert.assertEquals("oneToMany", objectRelationshipType1.toString());

		Page<ObjectRelationship> page2 =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					objectDefinition.getObjectDefinitionId(), null,
					objectRelationshipResource.toFilter("name eq 'testOR2'"),
					null, null);

		ObjectRelationship existingObjectRelationship2 = page2.fetchFirstItem();

		Assert.assertEquals(
			"TestObjectDefinition2",
			existingObjectRelationship2.getObjectDefinitionName2());

		ObjectRelationship.DeletionType objectRelationshipDeletionType2 =
			existingObjectRelationship2.getDeletionType();

		Assert.assertEquals(
			"cascade", objectRelationshipDeletionType2.toString());

		ObjectRelationship.Type objectRelationshipType2 =
			existingObjectRelationship2.getType();

		Assert.assertEquals("oneToMany", objectRelationshipType2.toString());

		objectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(), "AccountEntry");

		Page<ObjectRelationship> page3 =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					objectDefinition.getObjectDefinitionId(), null,
					objectRelationshipResource.toFilter(
						"name eq 'accountEntryToTestObjectDefinition4'"),
					null, null);

		ObjectRelationship existingObjectRelationship3 = page3.fetchFirstItem();

		Assert.assertEquals(
			"TestObjectDefinition4",
			existingObjectRelationship3.getObjectDefinitionName2());

		ObjectRelationship.Type objectRelationshipType3 =
			existingObjectRelationship3.getType();

		Assert.assertEquals("oneToMany", objectRelationshipType3.toString());
	}

	private void _assertObjectRelationships2(
			ObjectDefinition objectDefinition, ServiceContext serviceContext)
		throws Exception {

		ObjectRelationshipResource.Builder objectRelationshipResourceBuilder =
			_objectRelationshipResourceFactory.create();

		ObjectRelationshipResource objectRelationshipResource =
			objectRelationshipResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(), "User");

		Page<ObjectRelationship> page1 =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					objectDefinition1.getObjectDefinitionId(), null,
					objectRelationshipResource.toFilter("name eq 'testOR1'"),
					null, null);

		ObjectRelationship existingObjectRelationship1 = page1.fetchFirstItem();

		Assert.assertEquals(
			"TestObjectDefinition1",
			existingObjectRelationship1.getObjectDefinitionName2());

		ObjectRelationship.Type objectRelationshipType1 =
			existingObjectRelationship1.getType();

		Assert.assertEquals("oneToMany", objectRelationshipType1.toString());

		Page<ObjectRelationship> page2 =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					objectDefinition.getObjectDefinitionId(), null,
					objectRelationshipResource.toFilter("name eq 'testOR2'"),
					null, null);

		ObjectRelationship existingObjectRelationship2 = page2.fetchFirstItem();

		Assert.assertEquals(
			"TestObjectDefinition2",
			existingObjectRelationship2.getObjectDefinitionName2());

		ObjectRelationship.DeletionType objectRelationshipDeletionType2 =
			existingObjectRelationship2.getDeletionType();

		Assert.assertEquals(
			"prevent", objectRelationshipDeletionType2.toString());

		ObjectRelationship.Type objectRelationshipType2 =
			existingObjectRelationship2.getType();

		Assert.assertEquals("oneToMany", objectRelationshipType2.toString());

		objectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(), "AccountEntry");

		Page<ObjectRelationship> page3 =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					objectDefinition.getObjectDefinitionId(), null,
					objectRelationshipResource.toFilter(
						"name eq 'accountEntryToTestObjectDefinition4'"),
					null, null);

		ObjectRelationship existingObjectRelationship3 = page3.fetchFirstItem();

		Assert.assertEquals(
			"TestObjectDefinition4",
			existingObjectRelationship3.getObjectDefinitionName2());

		ObjectRelationship.Type objectRelationshipType3 =
			existingObjectRelationship3.getType();

		Assert.assertEquals("oneToMany", objectRelationshipType3.toString());

		Page<ObjectRelationship> page4 =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					objectDefinition.getObjectDefinitionId(), null,
					objectRelationshipResource.toFilter(
						"name eq 'accountEntryToTestObjectDefinition5'"),
					null, null);

		ObjectRelationship existingObjectRelationship4 = page4.fetchFirstItem();

		Assert.assertEquals(
			"TestObjectDefinition5",
			existingObjectRelationship4.getObjectDefinitionName2());

		ObjectRelationship.Type objectRelationshipType4 =
			existingObjectRelationship4.getType();

		Assert.assertEquals("oneToMany", objectRelationshipType4.toString());
	}

	private void _assertOrganizations1() throws Exception {
		OrganizationResource.Builder organizationResourceBuilder =
			_organizationResourceFactory.create();

		OrganizationResource organizationResource =
			organizationResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		Organization organization =
			organizationResource.getOrganizationByExternalReferenceCode(
				"TESTORGANIZATION1");

		Assert.assertNotNull(organization);
		Assert.assertEquals("Test Organization 1", organization.getName());

		_assertAccountOrganizationRelsCount(organization, 1);

		organization =
			organizationResource.getOrganizationByExternalReferenceCode(
				"TESTORGANIZATION2");

		Assert.assertNotNull(organization);
		Assert.assertEquals("Test Organization 2", organization.getName());

		_assertAccountOrganizationRelsCount(organization, 1);

		organization =
			organizationResource.getOrganizationByExternalReferenceCode(
				"TESTORGANIZATION3");

		Assert.assertNotNull(organization);
		Assert.assertEquals("Test Organization 3", organization.getName());

		_assertAccountOrganizationRelsCount(organization, 0);
	}

	private void _assertOrganizations2() throws Exception {
		OrganizationResource.Builder organizationResourceBuilder =
			_organizationResourceFactory.create();

		OrganizationResource organizationResource =
			organizationResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		Organization organization =
			organizationResource.getOrganizationByExternalReferenceCode(
				"TESTORGANIZATION1");

		Assert.assertNotNull(organization);
		Assert.assertEquals("Test Organization 1", organization.getName());

		_assertAccountOrganizationRelsCount(organization, 1);

		organization =
			organizationResource.getOrganizationByExternalReferenceCode(
				"TESTORGANIZATION2");

		Assert.assertNotNull(organization);
		Assert.assertEquals(
			"Test Organization 2 Update", organization.getName());

		_assertAccountOrganizationRelsCount(organization, 2);

		organization =
			organizationResource.getOrganizationByExternalReferenceCode(
				"TESTORGANIZATION3");

		Assert.assertNotNull(organization);
		Assert.assertEquals("Test Organization 3", organization.getName());

		_assertAccountOrganizationRelsCount(organization, 1);

		organization =
			organizationResource.getOrganizationByExternalReferenceCode(
				"TESTORGANIZATION4");

		Assert.assertNotNull(organization);
		Assert.assertEquals("Test Organization 4", organization.getName());

		_assertAccountOrganizationRelsCount(organization, 0);
	}

	private void _assertPermissions() throws Exception {
		_assertRoles();

		_assertResourcePermission1();
	}

	private void _assertPLOEntries1() {
		PLOEntry ploEntry1 = _ploEntryLocalService.fetchPLOEntry(
			_group.getCompanyId(), "test-portal-language-override-1", "en_US");

		Assert.assertEquals(
			"Test Portal Language Override 1", ploEntry1.getValue());

		PLOEntry ploEntry2 = _ploEntryLocalService.fetchPLOEntry(
			_group.getCompanyId(), "test-portal-language-override-2", "en_US");

		Assert.assertEquals(
			"Test Portal Language Override 2", ploEntry2.getValue());
	}

	private void _assertPLOEntries2() {
		PLOEntry ploEntry1 = _ploEntryLocalService.fetchPLOEntry(
			_group.getCompanyId(), "test-portal-language-override-1", "en_US");

		Assert.assertEquals(
			"Test Portal Language Override 1 Update", ploEntry1.getValue());

		PLOEntry ploEntry2 = _ploEntryLocalService.fetchPLOEntry(
			_group.getCompanyId(), "test-portal-language-override-2", "en_US");

		Assert.assertEquals(
			"Test Portal Language Override 2 Update", ploEntry2.getValue());
	}

	private void _assertPortletSettings() {
		DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			_group.getGroupId(),
			_portal.getClassNameId("com.liferay.portal.kernel.theme.NavItem"),
			"TEST-PORTLET-SETTINGS-1");

		Assert.assertNotNull(ddmTemplate);
		Assert.assertEquals(
			"TEST PORTLET SETTINGS 1",
			ddmTemplate.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("${aField.getData()}", ddmTemplate.getScript());
	}

	private void _assertPrivateLayouts1() {
		List<Layout> privateLayouts = _layoutLocalService.getLayouts(
			_group.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		Assert.assertEquals(
			privateLayouts.toString(), 1, privateLayouts.size());

		Layout privateLayout = privateLayouts.get(0);

		Assert.assertEquals(
			"Test Private Layout",
			privateLayout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("content", privateLayout.getType());
		Assert.assertTrue(privateLayout.isHidden());

		List<Layout> privateChildLayouts = privateLayout.getAllChildren();

		Assert.assertEquals(
			privateChildLayouts.toString(), 1, privateChildLayouts.size());

		Layout privateChildLayout = privateChildLayouts.get(0);

		Assert.assertEquals(
			"Test Private Child Layout 1",
			privateChildLayout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("portlet", privateChildLayout.getType());
	}

	private void _assertPrivateLayouts2() {
		List<Layout> privateLayouts = _layoutLocalService.getLayouts(
			_group.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		Assert.assertEquals(
			privateLayouts.toString(), 1, privateLayouts.size());

		Layout privateLayout = privateLayouts.get(0);

		Assert.assertEquals(
			"Test Private Layout",
			privateLayout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("content", privateLayout.getType());
		Assert.assertTrue(privateLayout.isHidden());

		List<Layout> privateChildLayouts = privateLayout.getAllChildren();

		Assert.assertEquals(
			privateChildLayouts.toString(), 2, privateChildLayouts.size());

		Layout privateChildLayout = privateChildLayouts.get(0);

		Assert.assertEquals(
			"Test Private Child Layout 1 Update",
			privateChildLayout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("content", privateChildLayout.getType());

		privateChildLayout = privateChildLayouts.get(1);

		Assert.assertEquals(
			"Test Private Child Layout 2",
			privateChildLayout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("content", privateChildLayout.getType());
	}

	private void _assertPublicLayouts1() throws Exception {
		int publicLayoutsCount = _layoutLocalService.getLayoutsCount(
			_group, false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		Assert.assertEquals(6, publicLayoutsCount);

		Layout layout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/test-public-layout");

		Assert.assertEquals(
			"Test Public Layout", layout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("content", layout.getType());
		Assert.assertFalse(layout.isHidden());

		List<Layout> layouts = layout.getAllChildren();

		Assert.assertEquals(layouts.toString(), 1, layouts.size());

		layout = layouts.get(0);

		Assert.assertEquals(
			"Test Public Child Layout",
			layout.getName(LocaleUtil.getSiteDefault()));

		layout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/test-public-permissions-layout");

		Role role = _roleLocalService.getRole(
			layout.getCompanyId(), RoleConstants.GUEST);

		boolean hasGuestViewPermission =
			_resourcePermissionLocalService.hasResourcePermission(
				layout.getCompanyId(), layout.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()), role.getRoleId(),
				ActionKeys.VIEW);

		Assert.assertFalse(hasGuestViewPermission);

		role = _roleLocalService.getRole(
			layout.getCompanyId(), RoleConstants.SITE_MEMBER);

		boolean hasSiteMemberViewPermission =
			_resourcePermissionLocalService.hasResourcePermission(
				layout.getCompanyId(), layout.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()), role.getRoleId(),
				ActionKeys.VIEW);

		Assert.assertTrue(hasSiteMemberViewPermission);

		boolean hasSiteMemberUpdateLayoutContentPermission =
			_resourcePermissionLocalService.hasResourcePermission(
				layout.getCompanyId(), layout.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()), role.getRoleId(),
				ActionKeys.UPDATE_LAYOUT_CONTENT);

		Assert.assertTrue(hasSiteMemberUpdateLayoutContentPermission);

		role = _roleLocalService.getRole(layout.getCompanyId(), "Test Role 4");

		boolean hasTestRole4ViewPermission =
			_resourcePermissionLocalService.hasResourcePermission(
				layout.getCompanyId(), layout.getModelClassName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(layout.getPlid()), role.getRoleId(),
				ActionKeys.VIEW);

		Assert.assertTrue(hasTestRole4ViewPermission);

		layout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/home");

		Assert.assertEquals(
			PropsUtil.get("default.guest.public.layout.name"),
			layout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertNotEquals(
			PropsUtil.get("default.user.private.layout.name"),
			layout.getName(LocaleUtil.SPAIN));
		Assert.assertEquals(
			PropsUtil.get("default.guest.public.layout.friendly.url"),
			layout.getFriendlyURL(LocaleUtil.getSiteDefault()));

		SitePageResource.Builder sitePageResourceBuilder =
			_sitePageResourceFactory.create();

		SitePageResource sitePageResource =
			sitePageResourceBuilder.httpServletRequest(
				_serviceContext.getRequest()
			).httpServletResponse(
				new MockHttpServletResponse()
			).user(
				_serviceContext.fetchUser()
			).build();

		SitePage sitePage = sitePageResource.getSiteSitePage(
			_group.getGroupId(), "test-objects-layout");

		String pageDefinitionString = String.valueOf(
			sitePage.getPageDefinition());

		Assert.assertFalse(
			pageDefinitionString.contains(
				"[$TestObjectDefinition3#Test_Object_Entry_1$]"));
		Assert.assertFalse(
			pageDefinitionString.contains(
				"[$OBJECT_DEFINITION_CLASS_NAME:TestObjectDefinition3$]"));
		Assert.assertFalse(
			pageDefinitionString.contains(
				"[$OBJECT_DEFINITION_PORTLET_ID:TestObjectDefinition3$]"));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_group.getCompanyId(), "C_TestObjectDefinition3");

		Assert.assertTrue(
			pageDefinitionString.contains(
				StringBundler.concat(
					ObjectPortletKeys.OBJECT_DEFINITIONS, StringPool.UNDERLINE,
					StringUtil.split(
						objectDefinition.getClassName(), StringPool.POUND)
						[1])));

		layout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/test-url-layout");

		Assert.assertEquals(
			"Test URL Layout", layout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("url", layout.getType());
		Assert.assertEquals(
			"url=/test-public-layout\n", layout.getTypeSettings());
		Assert.assertFalse(layout.isHidden());

		layout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/test-link-to-layout");

		Assert.assertEquals(
			"Test Link to Layout", layout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("link_to_layout", layout.getType());
		Assert.assertFalse(layout.isHidden());

		sitePage = sitePageResource.getSiteSitePage(
			_group.getGroupId(), "test-public-layout");

		pageDefinitionString = String.valueOf(sitePage.getPageDefinition());

		Assert.assertFalse(pageDefinitionString.contains("[$CLASS_NAME_ID:"));
	}

	private void _assertPublicLayouts2() throws Exception {
		int publicLayoutsCount = _layoutLocalService.getLayoutsCount(
			_group, false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		Assert.assertEquals(6, publicLayoutsCount);

		Layout layout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/test-public-layout");

		Assert.assertEquals(
			"Test Public Layout Update",
			layout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("content", layout.getType());
		Assert.assertFalse(layout.isHidden());

		List<Layout> layouts = layout.getAllChildren();

		Assert.assertEquals(layouts.toString(), 1, layouts.size());

		layout = layouts.get(0);

		Assert.assertEquals(
			"Test Public Child Layout",
			layout.getName(LocaleUtil.getSiteDefault()));

		layout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/test-public-permissions-layout");

		Role role = _roleLocalService.getRole(
			layout.getCompanyId(), RoleConstants.GUEST);

		boolean hasGuestViewPermission =
			_resourcePermissionLocalService.hasResourcePermission(
				layout.getCompanyId(), layout.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()), role.getRoleId(),
				ActionKeys.VIEW);

		Assert.assertTrue(hasGuestViewPermission);

		role = _roleLocalService.getRole(
			layout.getCompanyId(), RoleConstants.SITE_MEMBER);

		boolean hasSiteMemberViewPermission =
			_resourcePermissionLocalService.hasResourcePermission(
				layout.getCompanyId(), layout.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()), role.getRoleId(),
				ActionKeys.VIEW);

		Assert.assertFalse(hasSiteMemberViewPermission);

		boolean hasSiteMemberUpdateLayoutContentPermission =
			_resourcePermissionLocalService.hasResourcePermission(
				layout.getCompanyId(), layout.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()), role.getRoleId(),
				ActionKeys.UPDATE_LAYOUT_CONTENT);

		Assert.assertTrue(hasSiteMemberUpdateLayoutContentPermission);

		role = _roleLocalService.getRole(layout.getCompanyId(), "Test Role 4");

		boolean hasTestRole4ViewPermission =
			_resourcePermissionLocalService.hasResourcePermission(
				layout.getCompanyId(), layout.getModelClassName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(layout.getPlid()), role.getRoleId(),
				ActionKeys.VIEW);

		Assert.assertTrue(hasTestRole4ViewPermission);

		layout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/home");

		Assert.assertEquals(
			PropsUtil.get("default.guest.public.layout.name"),
			layout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertNotEquals(
			PropsUtil.get("default.user.private.layout.name"),
			layout.getName(LocaleUtil.SPAIN));
		Assert.assertEquals(
			PropsUtil.get("default.guest.public.layout.friendly.url"),
			layout.getFriendlyURL(LocaleUtil.getSiteDefault()));

		SitePageResource.Builder sitePageResourceBuilder =
			_sitePageResourceFactory.create();

		SitePageResource sitePageResource =
			sitePageResourceBuilder.httpServletRequest(
				_serviceContext.getRequest()
			).httpServletResponse(
				new MockHttpServletResponse()
			).user(
				_serviceContext.fetchUser()
			).build();

		SitePage sitePage = sitePageResource.getSiteSitePage(
			_group.getGroupId(), "test-objects-layout");

		String pageDefinitionString = String.valueOf(
			sitePage.getPageDefinition());

		Assert.assertFalse(
			pageDefinitionString.contains(
				"[$TestObjectDefinition3#Test_Object_Entry_1$]"));
		Assert.assertFalse(
			pageDefinitionString.contains(
				"[$OBJECT_DEFINITION_CLASS_NAME:TestObjectDefinition1$]"));
		Assert.assertFalse(
			pageDefinitionString.contains(
				"[$OBJECT_DEFINITION_CLASS_NAME:TestObjectDefinition3$]"));
		Assert.assertFalse(
			pageDefinitionString.contains(
				"[$OBJECT_DEFINITION_PORTLET_ID:TestObjectDefinition3$]"));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_group.getCompanyId(), "C_TestObjectDefinition3");

		Assert.assertTrue(
			pageDefinitionString.contains(
				StringBundler.concat(
					ObjectPortletKeys.OBJECT_DEFINITIONS, StringPool.UNDERLINE,
					StringUtil.split(
						objectDefinition.getClassName(), StringPool.POUND)
						[1])));

		layout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/test-url-layout");

		Assert.assertEquals(
			"Test URL Layout Update",
			layout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("url", layout.getType());
		Assert.assertEquals(
			"url=/test-public-child-layout\n", layout.getTypeSettings());
		Assert.assertFalse(layout.isHidden());

		layout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, "/test-link-to-layout");

		Assert.assertEquals(
			"Test Link to Layout Update",
			layout.getName(LocaleUtil.getSiteDefault()));
		Assert.assertEquals("link_to_layout", layout.getType());
		Assert.assertFalse(layout.isHidden());
	}

	private void _assertResourceAction(
			String[] actionIds, ResourcePermission resourcePermission)
		throws Exception {

		long resourceActionBitwiseValue = 0;

		for (String actionId : actionIds) {
			ResourceAction resourceAction =
				_resourceActionLocalService.getResourceAction(
					resourcePermission.getName(), actionId);

			Assert.assertNotNull(resourceAction);

			resourceActionBitwiseValue += resourceAction.getBitwiseValue();
		}

		Assert.assertEquals(
			resourceActionBitwiseValue, resourcePermission.getActionIds());
	}

	private void _assertResourcePermission1() throws Exception {
		Role role = _roleLocalService.fetchRole(
			_group.getCompanyId(), "Test Role 1");

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_group.getCompanyId(), "com.liferay.commerce.product",
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_group.getCompanyId()), role.getRoleId());

		Assert.assertNotNull(resourcePermission);

		_assertResourceAction(new String[] {"VIEW_PRICE"}, resourcePermission);

		role = _roleLocalService.fetchRole(
			_group.getCompanyId(), "Test Role 2");

		resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_group.getCompanyId(), "com.liferay.commerce.product",
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(_group.getGroupId()), role.getRoleId());

		Assert.assertNotNull(resourcePermission);

		_assertResourceAction(new String[] {"VIEW_PRICE"}, resourcePermission);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_group.getCompanyId(), "C_TestObjectDefinition3");

		resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_group.getCompanyId(), objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_group.getCompanyId()), role.getRoleId());

		Assert.assertNotNull(resourcePermission);

		_assertResourceAction(
			new String[] {"DELETE", "UPDATE", "VIEW"}, resourcePermission);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				"Test Display Page Template",
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);

		role = _roleLocalService.fetchRole(
			_group.getCompanyId(), "Test Role 4");

		String className =
			"com.liferay.layout.page.template.model.LayoutPageTemplateEntry";

		resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_group.getCompanyId(), className,
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layoutPageTemplateEntry.getPrimaryKey()),
				role.getRoleId());

		Assert.assertNotNull(resourcePermission);

		_assertResourceAction(
			new String[] {"UPDATE", "VIEW"}, resourcePermission);
	}

	private void _assertResourcePermission2() throws Exception {
		Role role = _roleLocalService.fetchRole(
			_group.getCompanyId(), "Test Role 1");

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_group.getCompanyId(), "com.liferay.commerce.product",
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_group.getCompanyId()), role.getRoleId());

		Assert.assertNotNull(resourcePermission);

		_assertResourceAction(
			new String[] {"PERMISSIONS", "VIEW_PRICE"}, resourcePermission);

		role = _roleLocalService.fetchRole(
			_group.getCompanyId(), "Test Role 2");

		resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_group.getCompanyId(), "com.liferay.commerce.product",
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(_group.getGroupId()), role.getRoleId());

		Assert.assertNotNull(resourcePermission);

		_assertResourceAction(new String[] {"VIEW_PRICE"}, resourcePermission);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_group.getCompanyId(), "C_TestObjectDefinition3");

		resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_group.getCompanyId(), objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_group.getCompanyId()), role.getRoleId());

		Assert.assertNotNull(resourcePermission);
		Assert.assertFalse(resourcePermission.isViewActionId());

		_assertResourceAction(
			new String[] {"DELETE", "UPDATE"}, resourcePermission);

		role = _roleLocalService.fetchRole(
			_group.getCompanyId(), "Test Role 4");

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				"Test Display Page Template",
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);

		String className =
			"com.liferay.layout.page.template.model.LayoutPageTemplateEntry";

		resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_group.getCompanyId(), className,
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layoutPageTemplateEntry.getPrimaryKey()),
				role.getRoleId());

		Assert.assertNotNull(resourcePermission);

		_assertResourceAction(
			new String[] {"UPDATE", "VIEW"}, resourcePermission);

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				"Test Master Page",
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

		resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_group.getCompanyId(), className,
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layoutPageTemplateEntry.getPrimaryKey()),
				role.getRoleId());

		Assert.assertNotNull(resourcePermission);

		_assertResourceAction(
			new String[] {"UPDATE", "VIEW"}, resourcePermission);
	}

	private void _assertRoles() {
		Role role1 = _roleLocalService.fetchRole(
			_group.getCompanyId(), "Test Role 1");

		Assert.assertNotNull(role1);
		Assert.assertEquals(1, role1.getType());

		_assertRolesAssignments(3, role1.getRoleId());

		Role role2 = _roleLocalService.fetchRole(
			_group.getCompanyId(), "Test Role 2");

		Assert.assertNotNull(role2);
		Assert.assertEquals(6, role2.getType());

		_assertRolesAssignments(2, role2.getRoleId());

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_group.getCompanyId(), "com.liferay.portal.kernel.model.Role",
				1, String.valueOf(_group.getCompanyId()), role2.getRoleId());

		Assert.assertEquals(1, resourcePermission.getScope());
		Assert.assertTrue(resourcePermission.isViewActionId());

		Role role3 = _roleLocalService.fetchRole(
			_group.getCompanyId(), "Test Role 3");

		Assert.assertNotNull(role3);
		Assert.assertEquals(1, role3.getType());

		_assertRolesAssignments(1, role3.getRoleId());

		Role role4 = _roleLocalService.fetchRole(
			_group.getCompanyId(), "Test Role 4");

		Assert.assertNotNull(role4);
		Assert.assertEquals(2, role4.getType());

		_assertRolesAssignments(0, role4.getRoleId());
	}

	private void _assertRolesAssignments(
		int roleAssignmentsCount, long roleId) {

		List<Group> groups = _groupLocalService.getRoleGroups(roleId);

		Assert.assertNotNull(groups);
		Assert.assertEquals(
			groups.toString(), roleAssignmentsCount, groups.size());
	}

	private void _assertSAPEntries() {
		SAPEntry sapEntry1 = _sapEntryLocalService.fetchSAPEntry(
			_group.getCompanyId(), "TEST_SAP_ENTRY_1");

		Assert.assertNotNull(sapEntry1);
		Assert.assertTrue(sapEntry1.isDefaultSAPEntry());
		Assert.assertTrue(sapEntry1.isEnabled());

		List<String> allowedServiceSignatures1 =
			sapEntry1.getAllowedServiceSignaturesList();

		Assert.assertEquals(
			allowedServiceSignatures1.toString(), 3,
			allowedServiceSignatures1.size());

		SAPEntry sapEntry2 = _sapEntryLocalService.fetchSAPEntry(
			_group.getCompanyId(), "TEST_SAP_ENTRY_2");

		Assert.assertNotNull(sapEntry2);
		Assert.assertFalse(sapEntry2.isDefaultSAPEntry());
		Assert.assertTrue(sapEntry2.isEnabled());

		List<String> allowedServiceSignatures2 =
			sapEntry2.getAllowedServiceSignaturesList();

		Assert.assertEquals(
			allowedServiceSignatures2.toString(), 5,
			allowedServiceSignatures2.size());
	}

	private void _assertSearchableAssetTypes(
		String[] className,
		com.liferay.search.experiences.rest.dto.v1_0.Configuration
			configuration) {

		GeneralConfiguration generalConfiguration =
			configuration.getGeneralConfiguration();

		Assert.assertTrue(
			ArrayUtil.containsAll(
				generalConfiguration.getSearchableAssetTypes(), className));
	}

	private void _assertSegmentsEntries() {
		Assert.assertEquals(
			2,
			_segmentsEntryLocalService.getSegmentsEntriesCount(
				_group.getGroupId()));

		SegmentsEntry segmentsEntry1 =
			_segmentsEntryLocalService.fetchSegmentsEntry(
				_group.getGroupId(), "TEST-SEGMENTS-ENTRY-1");

		Assert.assertNotNull(segmentsEntry1);
		Assert.assertEquals(
			"Test Segments Entry 1",
			segmentsEntry1.getName(LocaleUtil.getSiteDefault()));
		Assert.assertTrue(segmentsEntry1.isActive());
		Assert.assertFalse(
			segmentsEntry1.getCriteria(
			).contains(
				"[$ROLE_ID:Test Role 1$]"
			));

		SegmentsEntry segmentsEntry2 =
			_segmentsEntryLocalService.fetchSegmentsEntry(
				_group.getGroupId(), "TEST-SEGMENTS-ENTRY-2");

		Assert.assertNotNull(segmentsEntry2);
		Assert.assertEquals(
			"Test Segments Entry 2",
			segmentsEntry2.getName(LocaleUtil.getSiteDefault()));
		Assert.assertFalse(segmentsEntry2.isActive());
		Assert.assertFalse(
			segmentsEntry2.getCriteria(
			).contains(
				"[$ROLE_ID:Test Role 2$]"
			));

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			_group.getGroupId(), false, "/test-public-layout");

		LayoutPageTemplateStructure publishLayoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		List<SegmentsExperience> publishLayoutSegmentsExperiences =
			_segmentsExperienceLocalService.getSegmentsExperiences(
				_group.getGroupId(),
				new long[] {
					segmentsEntry1.getSegmentsEntryId(),
					segmentsEntry2.getSegmentsEntryId()
				},
				layout.getPlid(), true);

		Assert.assertEquals(
			publishLayoutSegmentsExperiences.toString(), 2,
			publishLayoutSegmentsExperiences.size());

		SegmentsExperience publishLayoutSegmentsExperience1 =
			publishLayoutSegmentsExperiences.get(0);

		_assertLayoutStructureItems(
			publishLayoutPageTemplateStructure, 3,
			publishLayoutSegmentsExperience1.getSegmentsExperienceId());

		SegmentsExperience publishLayoutSegmentsExperience2 =
			publishLayoutSegmentsExperiences.get(1);

		_assertLayoutStructureItems(
			publishLayoutPageTemplateStructure, 3,
			publishLayoutSegmentsExperience2.getSegmentsExperienceId());

		Layout draftLayout = layout.fetchDraftLayout();

		LayoutPageTemplateStructure draftLayoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					draftLayout.getGroupId(), draftLayout.getPlid());

		List<SegmentsExperience> draftLayoutSegmentsExperiences =
			_segmentsExperienceLocalService.getSegmentsExperiences(
				_group.getGroupId(),
				new long[] {
					segmentsEntry1.getSegmentsEntryId(),
					segmentsEntry2.getSegmentsEntryId()
				},
				draftLayout.getPlid(), true);

		Assert.assertEquals(
			draftLayoutSegmentsExperiences.toString(), 2,
			draftLayoutSegmentsExperiences.size());

		SegmentsExperience draftLayoutSegmentsExperience1 =
			draftLayoutSegmentsExperiences.get(0);

		_assertLayoutStructureItems(
			draftLayoutPageTemplateStructure, 3,
			draftLayoutSegmentsExperience1.getSegmentsExperienceId());

		SegmentsExperience draftLayoutSegmentsExperience2 =
			draftLayoutSegmentsExperiences.get(1);

		_assertLayoutStructureItems(
			draftLayoutPageTemplateStructure, 3,
			draftLayoutSegmentsExperience2.getSegmentsExperienceId());
	}

	private void _assertSiteConfiguration1() throws Exception {
		Assert.assertEquals(
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			_group.getMembershipRestriction());
		Assert.assertEquals(GroupConstants.TYPE_SITE_OPEN, _group.getType());
		Assert.assertTrue(_group.isManualMembership());

		Assert.assertEquals(
			2,
			_menuAccessConfigurationManager.getAccessToControlMenuRoleIds(
				_group.getGroupId()).length);
		Assert.assertTrue(
			_menuAccessConfigurationManager.isShowControlMenuByRole(
				_group.getGroupId()));
	}

	private void _assertSiteConfiguration2() throws Exception {
		Assert.assertEquals(
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			_group.getMembershipRestriction());
		Assert.assertEquals(GroupConstants.TYPE_SITE_OPEN, _group.getType());
		Assert.assertTrue(_group.isManualMembership());

		Assert.assertEquals(
			0,
			_menuAccessConfigurationManager.getAccessToControlMenuRoleIds(
				_group.getGroupId()).length);
		Assert.assertFalse(
			_menuAccessConfigurationManager.isShowControlMenuByRole(
				_group.getGroupId()));
	}

	private void _assertSiteNavigationMenu1() {
		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					"SITENAVIGATIONMENU1", _group.getGroupId());

		Assert.assertNotNull(siteNavigationMenu);

		Assert.assertEquals(
			"Test Site Navigation Menu", siteNavigationMenu.getName());

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
				siteNavigationMenu.getSiteNavigationMenuId(), 0);

		Assert.assertEquals(
			siteNavigationMenuItems.toString(), 7,
			siteNavigationMenuItems.size());

		SiteNavigationMenuItem siteNavigationMenuItem1 =
			siteNavigationMenuItems.get(0);

		Assert.assertEquals(
			SiteNavigationMenuItemTypeConstants.LAYOUT,
			siteNavigationMenuItem1.getType());

		String typeSettings = siteNavigationMenuItem1.getTypeSettings();

		Assert.assertTrue(typeSettings.contains("displayIcon=home"));
		Assert.assertFalse(typeSettings.contains("useCustomName"));

		SiteNavigationMenuItem siteNavigationMenuItem2 =
			siteNavigationMenuItems.get(1);

		Assert.assertEquals(
			SiteNavigationMenuItemTypeConstants.URL,
			siteNavigationMenuItem2.getType());

		typeSettings = siteNavigationMenuItem2.getTypeSettings();

		Assert.assertTrue(typeSettings.contains("Test URL"));

		SiteNavigationMenuItem siteNavigationMenuItem3 =
			siteNavigationMenuItems.get(2);

		Assert.assertEquals(
			SiteNavigationMenuItemTypeConstants.NODE,
			siteNavigationMenuItem3.getType());

		typeSettings = siteNavigationMenuItem3.getTypeSettings();

		Assert.assertTrue(typeSettings.contains("Other Links"));

		SiteNavigationMenuItem siteNavigationMenuItem4 =
			siteNavigationMenuItems.get(3);

		Assert.assertEquals(
			AssetCategory.class.getName(), siteNavigationMenuItem4.getType());

		SiteNavigationMenuItem siteNavigationMenuItem5 =
			siteNavigationMenuItems.get(4);

		Assert.assertEquals(
			JournalArticle.class.getName(), siteNavigationMenuItem5.getType());

		SiteNavigationMenuItem siteNavigationMenuItem6 =
			siteNavigationMenuItems.get(5);

		Assert.assertEquals(
			FileEntry.class.getName(), siteNavigationMenuItem6.getType());

		SiteNavigationMenuItem siteNavigationMenuItem7 =
			siteNavigationMenuItems.get(6);

		String type = siteNavigationMenuItem7.getType();

		Assert.assertTrue(
			type.startsWith(
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION));
	}

	private void _assertSiteNavigationMenu2() {
		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					"SITENAVIGATIONMENU1", _group.getGroupId());

		Assert.assertNotNull(siteNavigationMenu);

		Assert.assertEquals(
			"Test Site Navigation Menu Update", siteNavigationMenu.getName());

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
				siteNavigationMenu.getSiteNavigationMenuId(), 0);

		Assert.assertEquals(
			siteNavigationMenuItems.toString(), 8,
			siteNavigationMenuItems.size());

		SiteNavigationMenuItem siteNavigationMenuItem1 =
			siteNavigationMenuItems.get(0);

		Assert.assertEquals(
			SiteNavigationMenuItemTypeConstants.LAYOUT,
			siteNavigationMenuItem1.getType());

		String typeSettings = siteNavigationMenuItem1.getTypeSettings();

		Assert.assertTrue(typeSettings.contains("Test Layout"));
		Assert.assertTrue(typeSettings.contains("useCustomName"));

		SiteNavigationMenuItem siteNavigationMenuItem2 =
			siteNavigationMenuItems.get(1);

		Assert.assertEquals(
			SiteNavigationMenuItemTypeConstants.URL,
			siteNavigationMenuItem2.getType());

		typeSettings = siteNavigationMenuItem2.getTypeSettings();

		Assert.assertTrue(typeSettings.contains("Test URL"));

		SiteNavigationMenuItem siteNavigationMenuItem3 =
			siteNavigationMenuItems.get(2);

		Assert.assertEquals(
			SiteNavigationMenuItemTypeConstants.NODE,
			siteNavigationMenuItem3.getType());

		typeSettings = siteNavigationMenuItem3.getTypeSettings();

		Assert.assertTrue(typeSettings.contains("Other Links Update"));

		SiteNavigationMenuItem siteNavigationMenuItem4 =
			siteNavigationMenuItems.get(3);

		Assert.assertEquals(
			AssetCategory.class.getName(), siteNavigationMenuItem4.getType());

		SiteNavigationMenuItem siteNavigationMenuItem5 =
			siteNavigationMenuItems.get(4);

		Assert.assertEquals(
			JournalArticle.class.getName(), siteNavigationMenuItem5.getType());

		SiteNavigationMenuItem siteNavigationMenuItem6 =
			siteNavigationMenuItems.get(5);

		Assert.assertEquals(
			FileEntry.class.getName(), siteNavigationMenuItem6.getType());

		SiteNavigationMenuItem siteNavigationMenuItem7 =
			siteNavigationMenuItems.get(6);

		String type = siteNavigationMenuItem7.getType();

		Assert.assertTrue(
			type.startsWith(
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION));

		SiteNavigationMenuItem siteNavigationMenuItem8 =
			siteNavigationMenuItems.get(7);

		Assert.assertEquals(
			SiteNavigationMenuItemTypeConstants.LAYOUT,
			siteNavigationMenuItem8.getType());
	}

	private void _assertSiteSettings() throws Exception {
		Configuration configuration = _getFactoryConfiguration(
			"test.pid.scoped", ExtendedObjectClassDefinition.Scope.GROUP,
			_group.getGroupId());

		Assert.assertNotNull(configuration);

		Dictionary<String, Object> properties = configuration.getProperties();

		Assert.assertNotNull(properties);
		Assert.assertEquals("value1", properties.get("key1"));
		Assert.assertEquals("value2", properties.get("key2"));
	}

	private void _assertStyleBookEntry() {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.fetchStyleBookEntry(
				_group.getGroupId(), "test-style-book");

		Assert.assertNotNull(styleBookEntry);

		String frontendTokensValues = styleBookEntry.getFrontendTokensValues();

		Assert.assertTrue(
			frontendTokensValues.contains("blockquote-small-color"));
	}

	private void _assertSXPBlueprint1() throws Exception {
		SXPBlueprintResource.Builder sxpBlueprintResourceBuilder =
			_sxpBlueprintResourceFactory.create();

		SXPBlueprintResource sxpBlueprintResource =
			sxpBlueprintResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		SXPBlueprint sxpBlueprint =
			sxpBlueprintResource.getSXPBlueprintByExternalReferenceCode(
				"TESTSXPBLUEPRINT1");

		Assert.assertNotNull(sxpBlueprint);
		_assertSearchableAssetTypes(
			new String[] {"com.liferay.journal.model.JournalArticle"},
			sxpBlueprint.getConfiguration());
		Assert.assertEquals("Test SXBlueprint 1", sxpBlueprint.getTitle());

		sxpBlueprint =
			sxpBlueprintResource.getSXPBlueprintByExternalReferenceCode(
				"TESTSXPBLUEPRINT2");

		Assert.assertNotNull(sxpBlueprint);
		Assert.assertFalse(
			sxpBlueprint.toString(
			).contains(
				"[$TAXONOMY_CATEGORY_ID:/site-initializer/taxonomy-" +
					"vocabularies/company/test-asset-vocabulary-1/test-asset-" +
						"category-1.json$]"
			));
		_assertSearchableAssetTypes(
			new String[] {
				"com.liferay.document.library.kernel.model.DLFileEntry"
			},
			sxpBlueprint.getConfiguration());
		Assert.assertEquals("Test SXBlueprint 2", sxpBlueprint.getTitle());
	}

	private void _assertSXPBlueprint2() throws Exception {
		SXPBlueprintResource.Builder sxpBlueprintResourceBuilder =
			_sxpBlueprintResourceFactory.create();

		SXPBlueprintResource sxpBlueprintResource =
			sxpBlueprintResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		SXPBlueprint sxpBlueprint =
			sxpBlueprintResource.getSXPBlueprintByExternalReferenceCode(
				"TESTSXPBLUEPRINT1");

		Assert.assertNotNull(sxpBlueprint);
		_assertSearchableAssetTypes(
			new String[] {"com.liferay.journal.model.JournalArticle"},
			sxpBlueprint.getConfiguration());
		Assert.assertEquals("Test SXBlueprint 1", sxpBlueprint.getTitle());

		sxpBlueprint =
			sxpBlueprintResource.getSXPBlueprintByExternalReferenceCode(
				"TESTSXPBLUEPRINT2");

		Assert.assertNotNull(sxpBlueprint);
		_assertSearchableAssetTypes(
			new String[] {
				"com.liferay.document.library.kernel.model.DLFileEntry",
				"com.liferay.journal.model.JournalArticle"
			},
			sxpBlueprint.getConfiguration());
		Assert.assertEquals(
			"Test SXBlueprint 2 Update", sxpBlueprint.getTitle());

		sxpBlueprint =
			sxpBlueprintResource.getSXPBlueprintByExternalReferenceCode(
				"TESTSXPBLUEPRINT3");

		Assert.assertNotNull(sxpBlueprint);
		_assertSearchableAssetTypes(
			new String[] {"com.liferay.portal.kernel.model.User"},
			sxpBlueprint.getConfiguration());
		Assert.assertEquals("Test SXBlueprint 3", sxpBlueprint.getTitle());
	}

	private void _assertUserAccounts1() throws Exception {
		UserAccountResource.Builder userAccountResourceBuilder =
			_userAccountResourceFactory.create();

		UserAccountResource userAccountResource =
			userAccountResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		UserAccount userAccount =
			userAccountResource.getUserAccountByEmailAddress(
				"test.user1@liferay.com");

		Assert.assertNotNull(userAccount);

		AccountBrief[] accountBriefs = userAccount.getAccountBriefs();

		Assert.assertEquals(
			Arrays.toString(accountBriefs), 1, accountBriefs.length);

		Assert.assertEquals(
			"testalternatename1", userAccount.getAlternateName());
		Assert.assertEquals(
			UserAccount.Status.INACTIVE, userAccount.getStatus());

		OrganizationBrief[] organizationBriefs =
			userAccount.getOrganizationBriefs();

		Assert.assertEquals(
			Arrays.toString(organizationBriefs), 1, organizationBriefs.length);

		UserGroupBrief[] userGroupBriefs = userAccount.getUserGroupBriefs();

		Assert.assertEquals(
			Arrays.toString(userGroupBriefs), 2, userGroupBriefs.length);

		Assert.assertTrue(userAccount.getImageId() > 0);

		_assertUserSiteGroups(userAccount.getId());

		userAccount = userAccountResource.getUserAccountByEmailAddress(
			"test.user2@liferay.com");

		Assert.assertNotNull(userAccount);

		accountBriefs = userAccount.getAccountBriefs();

		Assert.assertEquals(
			Arrays.toString(accountBriefs), 1, accountBriefs.length);

		Assert.assertEquals(
			"testalternatename2", userAccount.getAlternateName());
		Assert.assertEquals(
			UserAccount.Status.INACTIVE, userAccount.getStatus());

		organizationBriefs = userAccount.getOrganizationBriefs();

		Assert.assertEquals(
			Arrays.toString(organizationBriefs), 1, organizationBriefs.length);

		userGroupBriefs = userAccount.getUserGroupBriefs();

		Assert.assertEquals(
			Arrays.toString(userGroupBriefs), 1, userGroupBriefs.length);

		Assert.assertTrue(userAccount.getImageId() == 0);

		_assertUserSiteGroups(userAccount.getId());

		userAccount = userAccountResource.getUserAccountByEmailAddress(
			"test.user3@liferay.com");

		accountBriefs = userAccount.getAccountBriefs();

		Assert.assertEquals(
			Arrays.toString(accountBriefs), 0, accountBriefs.length);

		Assert.assertEquals(
			"testalternatename3", userAccount.getAlternateName());
		Assert.assertEquals(
			UserAccount.Status.INACTIVE, userAccount.getStatus());
	}

	private void _assertUserAccounts2() throws Exception {
		UserAccountResource.Builder userAccountResourceBuilder =
			_userAccountResourceFactory.create();

		UserAccountResource userAccountResource =
			userAccountResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		UserAccount userAccount =
			userAccountResource.getUserAccountByEmailAddress(
				"test.user1@liferay.com");

		Assert.assertNotNull(userAccount);

		AccountBrief[] accountBriefs = userAccount.getAccountBriefs();

		Assert.assertEquals(
			Arrays.toString(accountBriefs), 1, accountBriefs.length);

		Assert.assertEquals(
			"testalternatename1", userAccount.getAlternateName());
		Assert.assertEquals(
			"test.user1@liferay.com", userAccount.getEmailAddress());

		OrganizationBrief[] organizationBriefs =
			userAccount.getOrganizationBriefs();

		Assert.assertEquals(
			Arrays.toString(organizationBriefs), 1, organizationBriefs.length);

		UserGroupBrief[] userGroupBriefs = userAccount.getUserGroupBriefs();

		Assert.assertEquals(
			Arrays.toString(userGroupBriefs), 2, userGroupBriefs.length);

		Assert.assertTrue(userAccount.getImageId() > 0);

		_assertUserSiteGroups(userAccount.getId());

		userAccount = userAccountResource.getUserAccountByEmailAddress(
			"test.user2@liferay.com");

		Assert.assertNotNull(userAccount);

		accountBriefs = userAccount.getAccountBriefs();

		Assert.assertEquals(
			Arrays.toString(accountBriefs), 2, accountBriefs.length);

		Assert.assertEquals(
			"testalternatename2", userAccount.getAlternateName());
		Assert.assertEquals(
			UserAccount.Status.INACTIVE, userAccount.getStatus());

		organizationBriefs = userAccount.getOrganizationBriefs();

		Assert.assertEquals(
			Arrays.toString(organizationBriefs), 2, organizationBriefs.length);

		userGroupBriefs = userAccount.getUserGroupBriefs();

		Assert.assertEquals(
			Arrays.toString(userGroupBriefs), 2, userGroupBriefs.length);

		Assert.assertTrue(userAccount.getImageId() >= 0);

		_assertUserSiteGroups(userAccount.getId());

		userAccount = userAccountResource.getUserAccountByEmailAddress(
			"test.user3@liferay.com");

		Assert.assertNotNull(userAccount);

		accountBriefs = userAccount.getAccountBriefs();

		Assert.assertEquals(
			Arrays.toString(accountBriefs), 2, accountBriefs.length);

		Assert.assertEquals(
			"testalternatename3", userAccount.getAlternateName());
		Assert.assertEquals(
			UserAccount.Status.INACTIVE, userAccount.getStatus());

		organizationBriefs = userAccount.getOrganizationBriefs();

		Assert.assertEquals(
			Arrays.toString(organizationBriefs), 0, organizationBriefs.length);

		userGroupBriefs = userAccount.getUserGroupBriefs();

		Assert.assertEquals(
			Arrays.toString(userGroupBriefs), 0, userGroupBriefs.length);

		Assert.assertTrue(userAccount.getImageId() == 0);

		_assertUserSiteGroups(userAccount.getId());
	}

	private void _assertUserGroups() {
		List<UserGroup> userGroups = _userGroupLocalService.getGroupUserGroups(
			_group.getGroupId());

		Assert.assertEquals(userGroups.toString(), 2, userGroups.size());

		UserGroup userGroup1 =
			_userGroupLocalService.fetchUserGroupByExternalReferenceCode(
				"TESTUSERGROUP1", _group.getCompanyId());

		Assert.assertNotNull(userGroup1);
		Assert.assertTrue(userGroups.contains(userGroup1));

		UserGroup userGroup2 =
			_userGroupLocalService.fetchUserGroupByExternalReferenceCode(
				"TESTUSERGROUP2", _group.getCompanyId());

		Assert.assertNotNull(userGroup2);
		Assert.assertTrue(userGroups.contains(userGroup2));
	}

	private void _assertUserRoles() throws Exception {
		User user = _userLocalService.fetchUserByEmailAddress(
			_group.getCompanyId(), "test.user1@liferay.com");

		List<Role> roles = user.getRoles();

		Assert.assertEquals(roles.toString(), 3, roles.size());

		Role role = roles.get(0);

		Assert.assertEquals(RoleConstants.USER, role.getName());

		role = roles.get(1);

		Assert.assertEquals("Test Role 1", role.getName());

		role = roles.get(2);

		Assert.assertEquals("Test Role 2", role.getName());

		user = _userLocalService.fetchUserByEmailAddress(
			_group.getCompanyId(), "test.user2@liferay.com");

		roles = user.getRoles();

		Assert.assertEquals(roles.toString(), 2, roles.size());

		role = roles.get(0);

		Assert.assertEquals(RoleConstants.USER, role.getName());

		role = roles.get(1);

		Assert.assertEquals("Test Role 3", role.getName());
	}

	private void _assertUserSiteGroups(long userId) throws Exception {
		User user = _userLocalService.getUser(userId);

		List<Group> groups = user.getSiteGroups();

		Assert.assertEquals(groups.toString(), 1, groups.size());

		Group group = groups.get(0);

		Assert.assertEquals(_group.getGroupId(), group.getGroupId());
	}

	private void _assertWorkflowDefinitions() throws Exception {
		WorkflowDefinitionResource.Builder workflowDefinitionResourceBuilder =
			_workflowDefinitionResourceFactory.create();

		WorkflowDefinitionResource workflowDefinitionResource =
			workflowDefinitionResourceBuilder.user(
				_serviceContext.fetchUser()
			).build();

		WorkflowDefinition workflowDefinitionTest1 =
			workflowDefinitionResource.getWorkflowDefinitionByName(
				"Test Workflow Definition 1", null, 1);

		Assert.assertNotNull(workflowDefinitionTest1);
		Assert.assertEquals(
			"Test Workflow Definition 1", workflowDefinitionTest1.getName());
		Assert.assertEquals(
			"Test Workflow Definition 1", workflowDefinitionTest1.getTitle());
		Assert.assertEquals(
			"This is the description for Test Workflow Definition 1.",
			workflowDefinitionTest1.getDescription());

		WorkflowDefinitionLink workflowDefinitionLink1 =
			_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
				_group.getCompanyId(), 0, "com.liferay.blogs.model.BlogsEntry",
				0, 0);

		Assert.assertNotNull(workflowDefinitionLink1);
		Assert.assertEquals(
			"Test Workflow Definition 1",
			workflowDefinitionLink1.getWorkflowDefinitionName());

		WorkflowDefinition workflowDefinitionTest2 =
			workflowDefinitionResource.getWorkflowDefinitionByName(
				"Test Workflow Definition 2", null, 1);

		Assert.assertNotNull(workflowDefinitionTest2);
		Assert.assertEquals(
			"Test Workflow Definition 2", workflowDefinitionTest2.getName());
		Assert.assertEquals(
			"Test Workflow Definition 2", workflowDefinitionTest2.getTitle());
		Assert.assertEquals(
			"This is the description for Test Workflow Definition 2.",
			workflowDefinitionTest2.getDescription());

		WorkflowDefinitionLink workflowDefinitionLink2 =
			_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
				_group.getCompanyId(), _group.getGroupId(),
				"com.liferay.search.experiences.model.SXPBlueprint", 0, 0);

		Assert.assertNotNull(workflowDefinitionLink2);
		Assert.assertEquals(
			"Test Workflow Definition 2",
			workflowDefinitionLink2.getWorkflowDefinitionName());
	}

	private Bundle _getBundle(String location) throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			BundleSiteInitializerTest.class);

		try (InputStream inputStream =
				BundleSiteInitializerTest.class.getResourceAsStream(location)) {

			BundleContext bundleContext = bundle.getBundleContext();

			bundle = bundleContext.installBundle(location, inputStream);
		}

		bundle.start();

		return bundle;
	}

	private Configuration _getFactoryConfiguration(
			String factoryPid, ExtendedObjectClassDefinition.Scope scope,
			Serializable scopePK)
		throws Exception {

		try {
			String filterString = StringBundler.concat(
				"(&(service.factoryPid=", factoryPid, ")(",
				scope.getPropertyKey(), "=", scopePK, "))");

			Configuration[] configurations =
				_configurationAdmin.listConfigurations(filterString);

			if (configurations != null) {
				return configurations[0];
			}

			return null;
		}
		catch (InvalidSyntaxException | IOException exception) {
			throw new ConfigurationException(
				"Unable to retrieve factory configuration " + factoryPid,
				exception);
		}
	}

	private File _getTempDir(String location) throws Exception {
		File tempFile = FileUtil.createTempFile();

		FileUtil.write(
			tempFile,
			BundleSiteInitializerTest.class.getResourceAsStream(location));

		File tempDir1 = FileUtil.createTempFolder();

		FileUtil.unzip(tempFile, tempDir1);

		tempFile.delete();

		return tempDir1;
	}

	private void _test1(SiteInitializer siteInitializer) throws Exception {
		siteInitializer.initialize(_group.getGroupId());

		_assertAccountGroups1();
		_assertAccounts1();
		_assertAssetListEntries();
		_assertAssetVocabularies();
		_assertBlogPostings1();
		_assertClientExtension();
		_assertCommerceCatalogs1();
		_assertCommerceChannel1();
		_assertCommerceInventoryWarehouse();
		_assertCommerceOrderType1();
		_assertCommerceSpecificationProducts1();
		_assertCPDefinition();
		_assertCPInstanceProperties();
		_assertCPOptionCategory();
		_assertDataDefinition1();
		_assertDDMStructure();
		_assertDDMTemplate1();
		_assertDepotEntries1();
		_assertDLFileEntry1();
		_assertExpandoColumns1();
		_assertExpandoValues1();
		_assertFragmentEntries1();
		_assertJournalArticles1();
		_assertKBArticles();
		_assertKeywords1();
		_assertLayoutPageTemplateEntries();
		_assertLayoutSets1();
		_assertLayouts1();
		_assertLayoutUtilityPageEntries();
		_assertListTypeDefinitions1();
		_assertNotificationTemplate1();
		_assertObjectDefinitions1();
		_assertObjectFolders1();
		_assertOrganizations1();
		_assertPermissions();
		_assertPLOEntries1();
		_assertPortletSettings();
		_assertSAPEntries();
		_assertSegmentsEntries();
		_assertSiteConfiguration1();
		_assertSiteSettings();
		_assertSiteNavigationMenu1();
		_assertStyleBookEntry();
		_assertSXPBlueprint1();
		_assertUserAccounts1();
		_assertUserGroups();
		_assertUserRoles();
		_assertWorkflowDefinitions();
	}

	private void _test2(SiteInitializer siteInitializer) throws Exception {
		siteInitializer.initialize(_group.getGroupId());

		_assertAccountGroups2();
		_assertAccounts2();
		_assertBlogPostings2();
		_assertCommerceCatalogs2();
		_assertCommerceChannel2();
		_assertCommerceOrderType2();
		_assertCommerceSpecificationProducts2();
		_assertDataDefinition2();
		_assertDDMTemplate2();
		_assertDepotEntries2();
		_assertDLFileEntry2();
		_assertExpandoColumns2();
		_assertExpandoValues2();
		_assertFragmentEntries2();
		_assertJournalArticles2();
		_assertKeywords2();
		_assertLayouts2();
		_assertLayoutSets2();
		_assertListTypeDefinitions2();
		_assertNotificationTemplate2();
		_assertObjectDefinitions2();
		_assertObjectFolders2();
		_assertOrganizations2();
		_assertPLOEntries2();
		_assertResourcePermission2();
		_assertSiteConfiguration2();
		_assertSiteNavigationMenu2();
		_assertSXPBlueprint2();
		_assertUserAccounts2();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BundleSiteInitializerTest.class);

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	@Inject
	private static PLOEntryLocalService _ploEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Inject
	private AccountResource.Factory _accountResourceFactory;

	@Inject
	private AdminAccountGroupResource.Factory _adminAccountGroupResourcefactory;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetLinkLocalService _assetLinkLocalService;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private BlogPostingResource.Factory _blogPostingResourceFactory;

	@Inject
	private CETFactory _cetFactory;

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	@Inject
	private CommerceNotificationTemplateLocalService
		_commerceNotificationTemplateLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	@Inject
	private CPOptionCategoryLocalService _cpOptionCategoryLocalService;

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@Inject
	private CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Inject
	private DepotAppCustomizationLocalService
		_depotAppCustomizationLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalFolderService _journalFolderService;

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	@Inject
	private KBFolderLocalService _kbFolderLocalService;

	@Inject
	private KeywordResource.Factory _keywordResourceFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private ListTypeDefinitionResource.Factory
		_listTypeDefinitionResourceFactory;

	@Inject
	private MenuAccessConfigurationManager _menuAccessConfigurationManager;

	@Inject
	private NotificationTemplateResource.Factory
		_notificationTemplateResourceFactory;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipResource.Factory
		_objectRelationshipResourceFactory;

	@Inject
	private OrderTypeResource.Factory _orderTypeResourceFactory;

	@Inject
	private OrganizationResource.Factory _organizationResourceFactory;

	@Inject
	private Portal _portal;

	@Inject
	private ProductSpecificationResource.Factory
		_productSpecificationResourceFactory;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SAPEntryLocalService _sapEntryLocalService;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private ServletContext _servletContext;

	@Inject
	private SiteInitializerFactory _siteInitializerFactory;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private SiteInitializerSerializer _siteInitializerSerializer;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@Inject
	private SitePageResource.Factory _sitePageResourceFactory;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject
	private SXPBlueprintResource.Factory _sxpBlueprintResourceFactory;

	@Inject
	private TemplateEntryLocalService _templateEntryLocalService;

	@Inject
	private UserAccountResource.Factory _userAccountResourceFactory;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowDefinitionResource.Factory
		_workflowDefinitionResourceFactory;

}