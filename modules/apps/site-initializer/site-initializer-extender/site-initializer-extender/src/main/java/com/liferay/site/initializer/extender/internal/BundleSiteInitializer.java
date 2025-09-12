/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.initializer.extender.internal;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.util.comparator.ClassNameModelResourceComparator;
import com.liferay.asset.util.AssetRendererFactoryWrapper;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.client.extension.util.CETUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.fragment.importer.FragmentsImportStrategy;
import com.liferay.fragment.importer.FragmentsImporter;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeEntryResource;
import com.liferay.headless.admin.taxonomy.dto.v1_0.Keyword;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.resource.v1_0.KeywordResource;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.AccountRole;
import com.liferay.headless.admin.user.dto.v1_0.Organization;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.resource.v1_0.AccountResource;
import com.liferay.headless.admin.user.resource.v1_0.AccountRoleResource;
import com.liferay.headless.admin.user.resource.v1_0.OrganizationResource;
import com.liferay.headless.admin.user.resource.v1_0.UserAccountResource;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowDefinition;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowDefinitionResource;
import com.liferay.headless.delivery.dto.v1_0.BlogPosting;
import com.liferay.headless.delivery.dto.v1_0.Document;
import com.liferay.headless.delivery.dto.v1_0.DocumentFolder;
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseArticle;
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseFolder;
import com.liferay.headless.delivery.dto.v1_0.StructuredContentFolder;
import com.liferay.headless.delivery.resource.v1_0.BlogPostingResource;
import com.liferay.headless.delivery.resource.v1_0.DocumentFolderResource;
import com.liferay.headless.delivery.resource.v1_0.DocumentResource;
import com.liferay.headless.delivery.resource.v1_0.KnowledgeBaseArticleResource;
import com.liferay.headless.delivery.resource.v1_0.KnowledgeBaseFolderResource;
import com.liferay.headless.delivery.resource.v1_0.StructuredContentFolderResource;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.utility.page.converter.LayoutUtilityPageEntryTypeConverter;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.notification.rest.dto.v1_0.NotificationTemplate;
import com.liferay.notification.rest.resource.v1_0.NotificationTemplateResource;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.dto.v1_0.util.ObjectActionUtil;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFieldResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFolderResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectRelationshipResource;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.settings.ArchivedSettingsFactory;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;
import com.liferay.portal.servlet.filters.threadlocal.ThreadLocalFilterThreadLocal;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;
import com.liferay.site.exception.InitializationException;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.extender.CommerceSiteInitializer;
import com.liferay.site.initializer.extender.OSBSiteInitializer;
import com.liferay.site.initializer.extender.SiteInitializerUtil;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;
import com.liferay.style.book.zip.processor.StyleBookEntryZipProcessor;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;
import com.liferay.wiki.model.WikiPage;

import jakarta.servlet.ServletContext;

import java.io.InputStream;
import java.io.Serializable;

import java.net.URL;
import java.net.URLConnection;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Brian Wing Shun Chan
 */
public class BundleSiteInitializer implements SiteInitializer {

	public BundleSiteInitializer(
		AccountEntryLocalService accountEntryLocalService,
		AccountEntryOrganizationRelLocalService
			accountEntryOrganizationRelLocalService,
		AccountGroupLocalService accountGroupLocalService,
		AccountGroupRelService accountGroupRelService,
		AccountResource.Factory accountResourceFactory,
		AccountRoleLocalService accountRoleLocalService,
		AccountRoleResource.Factory accountRoleResourceFactory,
		ArchivedSettingsFactory archivedSettingsFactory,
		AssetCategoryLocalService assetCategoryLocalService,
		AssetEntryLocalService assetEntryLocalService,
		AssetLinkLocalService assetLinkLocalService,
		AssetListEntryLocalService assetListEntryLocalService,
		BlogPostingResource.Factory blogPostingResourceFactory,
		CETManager cetManager,
		ClientExtensionEntryLocalService clientExtensionEntryLocalService,
		CompanyLocalService companyLocalService,
		ConfigurationProvider configurationProvider,
		DataDefinitionResource.Factory dataDefinitionResourceFactory,
		DDMStructureLocalService ddmStructureLocalService,
		DDMTemplateLocalService ddmTemplateLocalService,
		DefaultDDMStructureHelper defaultDDMStructureHelper,
		DepotEntryGroupRelLocalService depotEntryGroupRelLocalService,
		DepotEntryLocalService depotEntryLocalService,
		DLFileEntryTypeLocalService dlFileEntryTypeLocalService,
		DLURLHelper dlURLHelper,
		DocumentFolderResource.Factory documentFolderResourceFactory,
		DocumentResource.Factory documentResourceFactory,
		ExpandoValueLocalService expandoValueLocalService,
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		FragmentsImporter fragmentsImporter,
		GroupLocalService groupLocalService,
		JournalArticleLocalService journalArticleLocalService,
		JSONFactory jsonFactory, KeywordResource.Factory keywordResourceFactory,
		KnowledgeBaseArticleResource.Factory
			knowledgeBaseArticleResourceFactory,
		KnowledgeBaseFolderResource.Factory knowledgeBaseFolderResourceFactory,
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutsImporter layoutsImporter,
		LayoutPageTemplateStructureLocalService
			layoutPageTemplateStructureLocalService,
		LayoutPageTemplateStructureRelLocalService
			layoutPageTemplateStructureRelLocalService,
		LayoutSetLocalService layoutSetLocalService,
		LayoutUtilityPageEntryLocalService layoutUtilityPageEntryLocalService,
		ListTypeDefinitionResource listTypeDefinitionResource,
		ListTypeDefinitionResource.Factory listTypeDefinitionResourceFactory,
		ListTypeEntryLocalService listTypeEntryLocalService,
		ListTypeEntryResource listTypeEntryResource,
		ListTypeEntryResource.Factory listTypeEntryResourceFactory,
		MenuAccessConfigurationManager menuAccessConfigurationManager,
		NotificationTemplateResource.Factory
			notificationTemplateResourceFactory,
		ObjectActionLocalService objectActionLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectDefinitionResource.Factory objectDefinitionResourceFactory,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryManager objectEntryManager,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFieldResource.Factory objectFieldResourceFactory,
		ObjectFolderResource.Factory objectFolderResourceFactory,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ObjectRelationshipResource.Factory objectRelationshipResourceFactory,
		OrganizationLocalService organizationLocalService,
		OrganizationResource.Factory organizationResourceFactory,
		PLOEntryLocalService ploEntryLocalService, Portal portal,
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService,
		SAPEntryLocalService sapEntryLocalService,
		SegmentsEntryLocalService segmentsEntryLocalService,
		SegmentsExperienceLocalService segmentsExperienceLocalService,
		Bundle siteBundle, Bundle siteInitializerExtenderBundle,
		SiteNavigationMenuItemLocalService siteNavigationMenuItemLocalService,
		SiteNavigationMenuItemTypeRegistry siteNavigationMenuItemTypeRegistry,
		SiteNavigationMenuLocalService siteNavigationMenuLocalService,
		StructuredContentFolderResource.Factory
			structuredContentFolderResourceFactory,
		StyleBookEntryZipProcessor styleBookEntryZipProcessor,
		TaxonomyCategoryResource.Factory taxonomyCategoryResourceFactory,
		TaxonomyVocabularyResource.Factory taxonomyVocabularyResourceFactory,
		TemplateEntryLocalService templateEntryLocalService,
		ThemeLocalService themeLocalService,
		UserAccountResource.Factory userAccountResourceFactory,
		UserGroupLocalService userGroupLocalService,
		UserLocalService userLocalService,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService,
		WorkflowDefinitionResource.Factory workflowDefinitionResourceFactory,
		ZipWriterFactory zipWriterFactory) {

		_accountEntryLocalService = accountEntryLocalService;
		_accountEntryOrganizationRelLocalService =
			accountEntryOrganizationRelLocalService;
		_accountGroupLocalService = accountGroupLocalService;
		_accountGroupRelService = accountGroupRelService;
		_accountResourceFactory = accountResourceFactory;
		_accountRoleLocalService = accountRoleLocalService;
		_accountRoleResourceFactory = accountRoleResourceFactory;
		_archivedSettingsFactory = archivedSettingsFactory;
		_assetCategoryLocalService = assetCategoryLocalService;
		_assetEntryLocalService = assetEntryLocalService;
		_assetLinkLocalService = assetLinkLocalService;
		_assetListEntryLocalService = assetListEntryLocalService;
		_blogPostingResourceFactory = blogPostingResourceFactory;
		_cetManager = cetManager;
		_clientExtensionEntryLocalService = clientExtensionEntryLocalService;
		_companyLocalService = companyLocalService;
		_configurationProvider = configurationProvider;
		_dataDefinitionResourceFactory = dataDefinitionResourceFactory;
		_ddmStructureLocalService = ddmStructureLocalService;
		_ddmTemplateLocalService = ddmTemplateLocalService;
		_defaultDDMStructureHelper = defaultDDMStructureHelper;
		_depotEntryGroupRelLocalService = depotEntryGroupRelLocalService;
		_depotEntryLocalService = depotEntryLocalService;
		_dlFileEntryTypeLocalService = dlFileEntryTypeLocalService;
		_dlURLHelper = dlURLHelper;
		_documentFolderResourceFactory = documentFolderResourceFactory;
		_documentResourceFactory = documentResourceFactory;
		_expandoValueLocalService = expandoValueLocalService;
		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_fragmentsImporter = fragmentsImporter;
		_groupLocalService = groupLocalService;
		_journalArticleLocalService = journalArticleLocalService;
		_jsonFactory = jsonFactory;
		_keywordResourceFactory = keywordResourceFactory;
		_knowledgeBaseArticleResourceFactory =
			knowledgeBaseArticleResourceFactory;
		_knowledgeBaseFolderResourceFactory =
			knowledgeBaseFolderResourceFactory;
		_layoutLocalService = layoutLocalService;
		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
		_layoutsImporter = layoutsImporter;
		_layoutPageTemplateStructureLocalService =
			layoutPageTemplateStructureLocalService;
		_layoutPageTemplateStructureRelLocalService =
			layoutPageTemplateStructureRelLocalService;
		_layoutSetLocalService = layoutSetLocalService;
		_layoutUtilityPageEntryLocalService =
			layoutUtilityPageEntryLocalService;
		_listTypeDefinitionResource = listTypeDefinitionResource;
		_listTypeDefinitionResourceFactory = listTypeDefinitionResourceFactory;
		_listTypeEntryLocalService = listTypeEntryLocalService;
		_listTypeEntryResource = listTypeEntryResource;
		_listTypeEntryResourceFactory = listTypeEntryResourceFactory;
		_menuAccessConfigurationManager = menuAccessConfigurationManager;
		_notificationTemplateResourceFactory =
			notificationTemplateResourceFactory;
		_objectActionLocalService = objectActionLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectDefinitionResourceFactory = objectDefinitionResourceFactory;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryManager = objectEntryManager;
		_objectFieldLocalService = objectFieldLocalService;
		_objectFieldResourceFactory = objectFieldResourceFactory;
		_objectFolderResourceFactory = objectFolderResourceFactory;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_objectRelationshipResourceFactory = objectRelationshipResourceFactory;
		_organizationLocalService = organizationLocalService;
		_organizationResourceFactory = organizationResourceFactory;
		_ploEntryLocalService = ploEntryLocalService;
		_portal = portal;
		_resourceActionLocalService = resourceActionLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
		_sapEntryLocalService = sapEntryLocalService;
		_segmentsEntryLocalService = segmentsEntryLocalService;
		_segmentsExperienceLocalService = segmentsExperienceLocalService;
		_siteBundle = siteBundle;
		_siteInitializerExtenderBundle = siteInitializerExtenderBundle;
		_siteNavigationMenuItemLocalService =
			siteNavigationMenuItemLocalService;
		_siteNavigationMenuItemTypeRegistry =
			siteNavigationMenuItemTypeRegistry;
		_siteNavigationMenuLocalService = siteNavigationMenuLocalService;
		_structuredContentFolderResourceFactory =
			structuredContentFolderResourceFactory;
		_styleBookEntryZipProcessor = styleBookEntryZipProcessor;
		_taxonomyCategoryResourceFactory = taxonomyCategoryResourceFactory;
		_taxonomyVocabularyResourceFactory = taxonomyVocabularyResourceFactory;
		_templateEntryLocalService = templateEntryLocalService;
		_themeLocalService = themeLocalService;
		_userAccountResourceFactory = userAccountResourceFactory;
		_userGroupLocalService = userGroupLocalService;
		_userLocalService = userLocalService;
		_workflowDefinitionLinkLocalService =
			workflowDefinitionLinkLocalService;
		_workflowDefinitionResourceFactory = workflowDefinitionResourceFactory;
		_zipWriterFactory = zipWriterFactory;

		BundleWiring bundleWiring = _siteBundle.adapt(BundleWiring.class);

		_classLoader = bundleWiring.getClassLoader();

		_classNameIdStringUtilReplaceValues =
			_getClassNameIdStringUtilReplaceValues();
		_releaseInfoStringUtilReplaceValues =
			_getReleaseInfoStringUtilReplaceValues();
	}

	@Override
	public String getDescription(Locale locale) {
		Dictionary<String, String> headers = _siteBundle.getHeaders(
			StringPool.BLANK);

		return GetterUtil.getString(
			headers.get("Liferay-Site-Initializer-Description"));
	}

	@Override
	public String getKey() {
		return _siteBundle.getSymbolicName();
	}

	@Override
	public String getName(Locale locale) {
		Dictionary<String, String> headers = _siteBundle.getHeaders(
			StringPool.BLANK);

		return GetterUtil.getString(
			headers.get("Liferay-Site-Initializer-Name"),
			headers.get("Bundle-Name"));
	}

	@Override
	public String getThumbnailSrc() {
		return _servletContext.getContextPath() + "/thumbnail.png";
	}

	@Override
	public void initialize(long groupId) throws InitializationException {
		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce site initializer " +
					_commerceSiteInitializerSnapshot.get());
			_log.debug(
				"OSB site initializer " + _osbSiteInitializerSnapshot.get());
		}

		if (ThreadLocalFilterThreadLocal.isFilterInvoked()) {
			Set<String> initializedGroupIdAndKeys =
				_initializedGroupIdAndKeys.get();

			if (!initializedGroupIdAndKeys.add(
					StringBundler.concat(
						groupId, StringPool.POUND, getKey()))) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Skip already initialized ", getKey(),
							" for group ", groupId));
				}

				return;
			}
		}

		long startTime = System.currentTimeMillis();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Initializing ", getKey(), " for group ", groupId));
		}

		try {
			_initialize(groupId);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new InitializationException(exception);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Initialized ", getKey(), " for group ", groupId, " in ",
					System.currentTimeMillis() - startTime, " ms"));
		}
	}

	@Override
	public boolean isActive(long companyId) {
		Dictionary<String, String> headers = _siteBundle.getHeaders(
			StringPool.BLANK);

		String featureFlagKey = headers.get(
			"Liferay-Site-Initializer-Feature-Flag");

		if (Validator.isNotNull(featureFlagKey) &&
			!FeatureFlagManagerUtil.isEnabled(featureFlagKey)) {

			return false;
		}

		return true;
	}

	protected void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private void _addAccountGroupAssignments(ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/account-group-assignments.json",
			_servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray accountsJSONArray = jsonObject.getJSONArray("accounts");

			if (JSONUtil.isEmpty(accountsJSONArray)) {
				continue;
			}

			List<AccountEntry> accountEntries = new ArrayList<>();

			for (int j = 0; j < accountsJSONArray.length(); j++) {
				accountEntries.add(
					_accountEntryLocalService.
						getAccountEntryByExternalReferenceCode(
							accountsJSONArray.getString(j),
							serviceContext.getCompanyId()));
			}

			if (ListUtil.isEmpty(accountEntries)) {
				continue;
			}

			AccountGroup accountGroup =
				_accountGroupLocalService.
					fetchAccountGroupByExternalReferenceCode(
						jsonObject.getString(
							"accountGroupExternalReferenceCode"),
						serviceContext.getCompanyId());

			if (accountGroup == null) {
				continue;
			}

			for (AccountEntry accountEntry : accountEntries) {
				AccountGroupRel accountGroupRel =
					_accountGroupRelService.fetchAccountGroupRel(
						accountGroup.getAccountGroupId(),
						AccountEntry.class.getName(),
						accountEntry.getAccountEntryId());

				if (accountGroupRel == null) {
					_accountGroupRelService.addAccountGroupRel(
						accountGroup.getAccountGroupId(),
						AccountEntry.class.getName(),
						accountEntry.getAccountEntryId());
				}
			}
		}
	}

	private void _addAccountGroups(ServiceContext serviceContext)
		throws Exception {

		CommerceSiteInitializer commerceSiteInitializer =
			_commerceSiteInitializerSnapshot.get();

		if (commerceSiteInitializer == null) {
			return;
		}

		commerceSiteInitializer.addAccountGroups(
			serviceContext, _servletContext);
	}

	private void _addAccounts(ServiceContext serviceContext) throws Exception {
		String json = SiteInitializerUtil.read(
			"/site-initializer/accounts.json", _servletContext);

		if (json == null) {
			return;
		}

		AccountResource.Builder builder = _accountResourceFactory.create();

		AccountResource accountResource = builder.user(
			serviceContext.fetchUser()
		).build();

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			Account account = Account.toDTO(
				String.valueOf(jsonArray.getJSONObject(i)));

			if (account == null) {
				_log.error("Unable to transform account from JSON: " + json);

				continue;
			}

			accountResource.putAccountByExternalReferenceCode(
				account.getExternalReferenceCode(), account);
		}
	}

	private void _addAccountsOrganizations(ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/accounts-organizations.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray organizationJSONArray = jsonObject.getJSONArray(
				"organizations");

			if (JSONUtil.isEmpty(organizationJSONArray)) {
				continue;
			}

			List<Long> organizationIds = new ArrayList<>();

			for (int j = 0; j < organizationJSONArray.length(); j++) {
				com.liferay.portal.kernel.model.Organization organization =
					_organizationLocalService.
						getOrganizationByExternalReferenceCode(
							organizationJSONArray.getString(j),
							serviceContext.getCompanyId());

				organizationIds.add(organization.getOrganizationId());
			}

			if (ListUtil.isEmpty(organizationIds)) {
				continue;
			}

			AccountEntry accountEntry =
				_accountEntryLocalService.
					getAccountEntryByExternalReferenceCode(
						jsonObject.getString("accountExternalReferenceCode"),
						serviceContext.getCompanyId());

			if (accountEntry == null) {
				continue;
			}

			for (Long organizationId : organizationIds) {
				if (_accountEntryOrganizationRelLocalService.
						hasAccountEntryOrganizationRel(
							accountEntry.getAccountEntryId(), organizationId)) {

					continue;
				}

				_accountEntryOrganizationRelLocalService.
					addAccountEntryOrganizationRel(
						accountEntry.getAccountEntryId(), organizationId);
			}
		}
	}

	private void _addAssetListEntries(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/asset-list-entries.json", _servletContext);

		if (json == null) {
			return;
		}

		for (DLFileEntryType dlFileEntryType :
				_dlFileEntryTypeLocalService.getDLFileEntryTypes(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			stringUtilReplaceValues.put(
				"DOCUMENT_FILE_ENTRY_TYPE_ID:" +
					dlFileEntryType.getFileEntryTypeKey(),
				String.valueOf(dlFileEntryType.getFileEntryTypeId()));
		}

		JSONArray assetListJSONArray = _jsonFactory.createJSONArray(
			_replace(json, stringUtilReplaceValues));

		for (int i = 0; i < assetListJSONArray.length(); i++) {
			JSONObject assetListJSONObject = assetListJSONArray.getJSONObject(
				i);

			_addOrUpdateAssetListEntry(assetListJSONObject, serviceContext);
		}

		List<AssetListEntry> assetListEntries =
			_assetListEntryLocalService.getAssetListEntries(
				serviceContext.getScopeGroupId());

		for (AssetListEntry assetListEntry : assetListEntries) {
			String assetListEntryKeyUppercase = StringUtil.toUpperCase(
				assetListEntry.getAssetListEntryKey());

			stringUtilReplaceValues.put(
				"ASSET_LIST_ENTRY_ID:" + assetListEntryKeyUppercase,
				String.valueOf(assetListEntry.getAssetListEntryId()));
		}
	}

	private void _addCPDefinitions(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		CommerceSiteInitializer commerceSiteInitializer =
			_commerceSiteInitializerSnapshot.get();

		if (commerceSiteInitializer == null) {
			return;
		}

		commerceSiteInitializer.addCPDefinitions(
			_siteBundle, serviceContext, _servletContext,
			stringUtilReplaceValues);
	}

	private void _addExpandoValues(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/expando-values.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			_replace(json, stringUtilReplaceValues));

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object data = jsonObject.get("data");

			if (data instanceof JSONObject) {
				Map<Locale, Object> map = new HashMap<>();

				JSONObject dataJSONObject = (JSONObject)data;

				Map<String, Object> dataJSONObjectMap = dataJSONObject.toMap();

				for (Map.Entry<String, Object> entry :
						dataJSONObjectMap.entrySet()) {

					Object value = entry.getValue();

					if (!(value instanceof List)) {
						map.put(
							LocaleUtil.fromLanguageId(entry.getKey()), value);

						continue;
					}

					List<?> values = (List<?>)value;

					map.put(
						LocaleUtil.fromLanguageId(entry.getKey()),
						values.toArray(new String[0]));
				}

				data = map;
			}

			_expandoValueLocalService.addValue(
				serviceContext.getCompanyId(),
				jsonObject.getString("className"), "CUSTOM_FIELDS",
				jsonObject.getString("columnName"),
				jsonObject.getLong("classPk"), data);
		}
	}

	private void _addFragmentEntries(
			Bundle bundle, long groupId, String parentResourcePath,
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Enumeration<URL> enumeration = bundle.findEntries(
			parentResourcePath, StringPool.STAR, true);

		if (enumeration == null) {
			return;
		}

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			String fileName = url.getFile();

			if (fileName.endsWith("/")) {
				continue;
			}

			if (StringUtil.endsWith(
					fileName, "fragment-composition-definition.json")) {

				String json = URLUtil.toString(url);

				json = _replace(
					SiteInitializerUtil.replace(json, serviceContext),
					stringUtilReplaceValues);

				zipWriter.addEntry(
					_removeFirst(fileName, parentResourcePath), json);
			}
			else {
				try (InputStream inputStream = url.openStream()) {
					zipWriter.addEntry(
						_removeFirst(fileName, parentResourcePath),
						inputStream);
				}
			}
		}

		_fragmentsImporter.importFragmentEntries(
			serviceContext.getUserId(), groupId, 0, zipWriter.getFile(),
			FragmentsImportStrategy.OVERWRITE, false);
	}

	private void _addFragmentEntries(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Group group = _groupLocalService.getCompanyGroup(
			serviceContext.getCompanyId());

		_addFragmentEntries(
			_siteBundle, group.getGroupId(),
			"/site-initializer/fragments/company", serviceContext,
			stringUtilReplaceValues);

		_addFragmentEntries(
			_siteBundle, serviceContext.getScopeGroupId(),
			"/site-initializer/fragments/group", serviceContext,
			stringUtilReplaceValues);

		if (_dialectThemeDetected) {
			_addFragmentEntries(
				_siteInitializerExtenderBundle,
				serviceContext.getScopeGroupId(),
				"/site-initializer/fragments/group", serviceContext,
				stringUtilReplaceValues);
		}
	}

	private void _addKeywords(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		_addKeywords(
			"ASSET_LIBRARY", "/site-initializer/keywords/asset-libraries",
			serviceContext, stringUtilReplaceValues);
		_addKeywords(
			"SITE", "/site-initializer/keywords/group", serviceContext,
			stringUtilReplaceValues);
	}

	private void _addKeywords(
			String replaceKey, String resourcePath,
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		String json = SiteInitializerUtil.read(
			resourcePath + "/keywords.json", _servletContext);

		if (json == null) {
			return;
		}

		KeywordResource.Builder keywordResourceBuilder =
			_keywordResourceFactory.create();

		KeywordResource keywordResource = keywordResourceBuilder.user(
			serviceContext.fetchUser()
		).build();

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			long groupId = 0;

			if (jsonObject.has("assetLibraryName")) {
				Group group = _groupLocalService.fetchGroup(
					serviceContext.getCompanyId(),
					jsonObject.getString("assetLibraryName"));

				if (group == null) {
					_log.error(
						"Unable to get asset library " +
							jsonObject.getString("assetLibraryName"));

					break;
				}

				groupId = group.getGroupId();
			}

			JSONArray keywordsJSONArray = jsonObject.getJSONArray("keywords");

			for (int j = 0; j < keywordsJSONArray.length(); j++) {
				Keyword keyword = Keyword.toDTO(
					String.valueOf(keywordsJSONArray.getJSONObject(j)));

				if (keyword == null) {
					_log.error(
						"Unable to transform keyword from JSON: " + json);

					continue;
				}

				Keyword existingKeyword = null;

				if (groupId != 0) {
					existingKeyword =
						keywordResource.getAssetLibraryKeywordsPage(
							groupId, null, null,
							keywordResource.toFilter(
								"name eq '" + keyword.getName() + "'"),
							null, null
						).fetchFirstItem();
				}
				else {
					existingKeyword = keywordResource.getSiteKeywordsPage(
						groupId, null, null,
						keywordResource.toFilter(
							"name eq '" + keyword.getName() + "'"),
						null, null
					).fetchFirstItem();

					groupId = serviceContext.getScopeGroupId();
				}

				if (existingKeyword != null) {
					continue;
				}

				keyword = keywordResource.postAssetLibraryKeyword(
					groupId, keyword);

				stringUtilReplaceValues.put(
					replaceKey + "_KEYWORD_ID:" + keyword.getName(),
					String.valueOf(keyword.getId()));
			}
		}
	}

	private void _addLayoutPageTemplates(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Enumeration<URL> enumeration = _siteBundle.findEntries(
			"/site-initializer/layout-page-templates", StringPool.STAR, true);

		if (enumeration == null) {
			return;
		}

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			String fileName = url.getFile();

			if (fileName.endsWith("/")) {
				continue;
			}

			String urlPath = url.getPath();

			if (StringUtil.endsWith(urlPath, "display-page-template.json") ||
				StringUtil.endsWith(urlPath, "page-definition.json")) {

				String json = URLUtil.toString(url);

				json = _replace(
					SiteInitializerUtil.replace(json, serviceContext),
					stringUtilReplaceValues);

				String css = _replace(
					SiteInitializerUtil.read(
						FileUtil.getPath(urlPath) + "/css.css",
						_servletContext),
					stringUtilReplaceValues);

				if (Validator.isNotNull(css)) {
					JSONObject jsonObject = _jsonFactory.createJSONObject(json);

					JSONObject settingsJSONObject = jsonObject.getJSONObject(
						"settings");

					settingsJSONObject.put("css", css);

					jsonObject.put("settings", settingsJSONObject);

					json = jsonObject.toString();
				}

				zipWriter.addEntry(
					_removeFirst(
						urlPath, "/site-initializer/layout-page-templates"),
					json);
			}
			else {
				try (InputStream inputStream = url.openStream()) {
					zipWriter.addEntry(
						_removeFirst(
							urlPath, "/site-initializer/layout-page-templates"),
						inputStream);
				}
			}
		}

		_layoutsImporter.importFile(
			serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			zipWriter.getFile(), LayoutsImportStrategy.OVERWRITE, true);
	}

	private void _addLayoutUtilityPageEntries(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Enumeration<URL> enumeration = _siteBundle.findEntries(
			"/site-initializer/layout-utility-page-entries", StringPool.STAR,
			true);

		if (enumeration == null) {
			return;
		}

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			String fileName = url.getFile();

			if (fileName.endsWith("/")) {
				continue;
			}

			String urlPath = url.getPath();

			if (StringUtil.endsWith(urlPath, "page-definition.json")) {
				String json = URLUtil.toString(url);

				json = _replace(
					SiteInitializerUtil.replace(json, serviceContext),
					stringUtilReplaceValues);

				String css = _replace(
					SiteInitializerUtil.read(
						FileUtil.getPath(urlPath) + "/css.css",
						_servletContext),
					stringUtilReplaceValues);

				if (Validator.isNotNull(css)) {
					JSONObject jsonObject = _jsonFactory.createJSONObject(json);

					JSONObject settingsJSONObject = jsonObject.getJSONObject(
						"settings");

					settingsJSONObject.put("css", css);

					jsonObject.put("settings", settingsJSONObject);

					json = jsonObject.toString();
				}

				zipWriter.addEntry(
					_removeFirst(
						urlPath,
						"/site-initializer/layout-utility-page-entries"),
					json);
			}
			else {
				try (InputStream inputStream = url.openStream()) {
					zipWriter.addEntry(
						_removeFirst(
							urlPath,
							"/site-initializer/layout-utility-page-entries"),
						inputStream);
				}
			}
		}

		_layoutsImporter.importFile(
			serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			zipWriter.getFile(), LayoutsImportStrategy.OVERWRITE, true);

		_setDefaultLayoutUtilityPageEntries(serviceContext);
	}

	private void _addObjectDefinitions(
			Map<String, ObjectDefinition>
				accountEntryRestrictedObjectDefinitions,
			List<Long> objectDefinitionIds, ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		List<com.liferay.object.model.ObjectDefinition>
			serviceBuilderObjectDefinitions =
				_objectDefinitionLocalService.getObjectDefinitions(
					serviceContext.getCompanyId(), true,
					WorkflowConstants.STATUS_APPROVED);

		for (com.liferay.object.model.ObjectDefinition
				serviceBuilderObjectDefinition :
					serviceBuilderObjectDefinitions) {

			_replaceObjectDefinitionValues(
				serviceBuilderObjectDefinition.getClassName(),
				serviceBuilderObjectDefinition.getShortName(),
				serviceBuilderObjectDefinition.getObjectDefinitionId(),
				stringUtilReplaceValues);
		}

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/object-definitions");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		ObjectDefinitionResource.Builder objectDefinitionResourceBuilder =
			_objectDefinitionResourceFactory.create();

		ObjectDefinitionResource objectDefinitionResource =
			objectDefinitionResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		for (String resourcePath : resourcePaths) {
			String json = SiteInitializerUtil.read(
				resourcePath, _servletContext);

			json = _replace(json, stringUtilReplaceValues);

			ObjectDefinition objectDefinition = ObjectDefinition.toDTO(json);

			if (objectDefinition == null) {
				_log.error(
					"Unable to transform object definition from JSON: " + json);

				continue;
			}

			Page<ObjectDefinition> objectDefinitionsPage =
				objectDefinitionResource.getObjectDefinitionsPage(
					null, null,
					objectDefinitionResource.toFilter(
						StringBundler.concat(
							"name eq '", objectDefinition.getName(), "'")),
					null, null);

			ObjectDefinition existingObjectDefinition =
				objectDefinitionsPage.fetchFirstItem();

			if (existingObjectDefinition == null) {
				if (GetterUtil.getBoolean(
						objectDefinition.getAccountEntryRestricted())) {

					accountEntryRestrictedObjectDefinitions.put(
						objectDefinition.getName(), objectDefinition);
				}

				objectDefinition =
					objectDefinitionResource.postObjectDefinition(
						objectDefinition);

				objectDefinitionIds.add(objectDefinition.getId());
			}
			else {
				objectDefinition =
					objectDefinitionResource.patchObjectDefinition(
						existingObjectDefinition.getId(), objectDefinition);
			}

			_replaceObjectDefinitionValues(
				objectDefinition.getClassName(), objectDefinition.getName(),
				objectDefinition.getId(), stringUtilReplaceValues);
		}
	}

	private void _addOrganizationUser(
			JSONArray jsonArray, ServiceContext serviceContext, long userId)
		throws Exception {

		if (JSONUtil.isEmpty(jsonArray)) {
			return;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			long organizationId = _organizationLocalService.getOrganizationId(
				serviceContext.getCompanyId(), jsonObject.getString("name"));

			if (organizationId <= 0) {
				continue;
			}

			_userLocalService.addOrganizationUser(organizationId, userId);
		}
	}

	private void _addOrKnowledgeBaseObjects(
			boolean folder, long parentKnowledgeBaseObjectId,
			String parentResourcePath, ServiceContext serviceContext)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			parentResourcePath);

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			if (!resourcePath.endsWith(".metadata.json")) {
				continue;
			}

			String json = SiteInitializerUtil.read(
				resourcePath, _servletContext);

			if (json == null) {
				continue;
			}

			JSONObject jsonObject = _jsonFactory.createJSONObject(json);

			if (jsonObject.has("articleBody")) {
				_addOrUpdateKnowledgeBaseArticle(
					folder, jsonObject, parentKnowledgeBaseObjectId,
					resourcePath.substring(
						0, resourcePath.indexOf(".metadata.json")),
					serviceContext);
			}
			else {
				_addOrUpdateKnowledgeBaseFolder(
					jsonObject, parentKnowledgeBaseObjectId,
					resourcePath.substring(
						0, resourcePath.indexOf(".metadata.json")),
					serviceContext);
			}
		}
	}

	private void _addOrUpdateAccountEntryRestrictions(
			Map<String, ObjectDefinition>
				accountEntryRestrictedObjectDefinitions,
			ServiceContext serviceContext)
		throws Exception {

		for (Map.Entry<String, ObjectDefinition> entry :
				accountEntryRestrictedObjectDefinitions.entrySet()) {

			com.liferay.object.model.ObjectDefinition
				serviceBuilderObjectDefinition =
					_objectDefinitionLocalService.fetchObjectDefinition(
						serviceContext.getCompanyId(), "C_" + entry.getKey());

			com.liferay.object.model.ObjectField serviceBuilderObjectField =
				_objectFieldLocalService.fetchObjectField(
					serviceBuilderObjectDefinition.getObjectDefinitionId(),
					entry.getValue(
					).getAccountEntryRestrictedObjectFieldName());

			if (serviceBuilderObjectDefinition.isDefaultStorageType()) {
				_objectDefinitionLocalService.enableAccountEntryRestricted(
					_objectRelationshipLocalService.
						fetchObjectRelationshipByObjectFieldId2(
							serviceBuilderObjectField.getObjectFieldId()));
			}
			else {
				_objectDefinitionLocalService.
					enableAccountEntryRestrictedForNondefaultStorageType(
						serviceBuilderObjectField);
			}
		}
	}

	private void _addOrUpdateAssetLinkEntries(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/asset-link-entries.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			_replace(json, stringUtilReplaceValues));

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			AssetEntry assetEntry1 = _assetEntryLocalService.fetchEntry(
				_portal.getClassNameId(jsonObject.getString("classNameId")),
				jsonObject.getLong("classPK"));

			if (assetEntry1 == null) {
				continue;
			}

			JSONArray assetEntriesJSONArray = jsonObject.getJSONArray(
				"assetEntries");

			for (int j = 0; j < assetEntriesJSONArray.length(); j++) {
				JSONObject assetEntryJSONObject =
					assetEntriesJSONArray.getJSONObject(j);

				AssetEntry assetEntry2 = _assetEntryLocalService.fetchEntry(
					_portal.getClassNameId(
						assetEntryJSONObject.getString("classNameId")),
					assetEntryJSONObject.getLong("classPK"));

				if (assetEntry2 == null) {
					continue;
				}

				_assetLinkLocalService.updateLink(
					serviceContext.getUserId(), assetEntry1.getEntryId(),
					assetEntry2.getEntryId(), AssetLinkConstants.TYPE_RELATED,
					0);
			}
		}
	}

	private void _addOrUpdateAssetListEntry(
			JSONObject assetListJSONObject, ServiceContext serviceContext)
		throws Exception {

		AssetListEntry assetListEntry = null;

		String assetListEntryKey = StringUtil.toLowerCase(
			_replace(assetListJSONObject.getString("title"), " ", "-"));

		for (AssetListEntry curAssetListEntry :
				_assetListEntryLocalService.getAssetListEntries(
					serviceContext.getScopeGroupId())) {

			if (Objects.equals(
					curAssetListEntry.getAssetListEntryKey(),
					assetListEntryKey)) {

				assetListEntry = curAssetListEntry;

				break;
			}
		}

		JSONObject unicodePropertiesJSONObject =
			assetListJSONObject.getJSONObject("unicodeProperties");

		List<String> classNameIdStrings = new ArrayList<>();

		List<Long> classNameIds = ListUtil.fromArray(
			AssetRendererFactoryRegistryUtil.getIndexableClassNameIds(
				serviceContext.getCompanyId(), true));

		classNameIds = ListUtil.sort(
			classNameIds,
			new ClassNameModelResourceComparator(
				true, serviceContext.getLocale()));

		classNameIds.forEach(
			classNameId -> classNameIdStrings.add(classNameId.toString()));

		String assetRendererFactoryName = _getAssetRendererFactoryName(
			unicodePropertiesJSONObject.getString("classNameIds"));

		Map<String, String> map = HashMapBuilder.put(
			"anyAssetType",
			String.valueOf(
				_portal.getClassNameId(
					unicodePropertiesJSONObject.getString("classNameIds")))
		).put(
			"anyClassType" + assetRendererFactoryName,
			assetListJSONObject.getString("assetEntrySubtypeId")
		).put(
			"classNameIds", StringUtil.merge(classNameIdStrings, ",")
		).put(
			"groupIds", String.valueOf(serviceContext.getScopeGroupId())
		).build();

		Object[] filterByObjects = JSONUtil.toObjectArray(
			unicodePropertiesJSONObject.getJSONArray("filterBy"));

		for (Object filterByObject : filterByObjects) {
			JSONObject filterByJSONObject = (JSONObject)filterByObject;

			map.put(
				filterByJSONObject.getString("key"),
				filterByJSONObject.getString("value"));
		}

		Object[] orderByObjects = JSONUtil.toObjectArray(
			unicodePropertiesJSONObject.getJSONArray("orderBy"));

		for (Object orderByObject : orderByObjects) {
			JSONObject orderByJSONObject = (JSONObject)orderByObject;

			map.put(
				orderByJSONObject.getString("key"),
				orderByJSONObject.getString("value"));
		}

		String[] assetTagNames = JSONUtil.toStringArray(
			assetListJSONObject.getJSONArray("assetTagNames"));

		for (int i = 0; i < assetTagNames.length; i++) {
			map.put("queryValues" + i, assetTagNames[i]);

			Object[] queryObjects = JSONUtil.toObjectArray(
				unicodePropertiesJSONObject.getJSONArray("query"));

			for (Object queryObject : queryObjects) {
				JSONObject queryJSONObject = (JSONObject)queryObject;

				map.put(
					queryJSONObject.getString("key"),
					queryJSONObject.getString("value"));
			}
		}

		if (assetListEntry == null) {
			_assetListEntryLocalService.addDynamicAssetListEntry(
				assetListJSONObject.getString("externalReferenceCode"),
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				assetListJSONObject.getString("title"),
				UnicodePropertiesBuilder.create(
					map, true
				).buildString(),
				serviceContext);
		}
		else {
			_assetListEntryLocalService.updateAssetListEntry(
				assetListEntry.getAssetListEntryId(),
				assetListJSONObject.getString("title"));
		}
	}

	private void _addOrUpdateBlogPostings(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/blog-postings.json", _servletContext);

		if (json == null) {
			return;
		}

		BlogPostingResource.Builder blogPostingResourceBuilder =
			_blogPostingResourceFactory.create();

		BlogPostingResource blogPostingResource =
			blogPostingResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			_replace(json, stringUtilReplaceValues));

		for (int i = 0; i < jsonArray.length(); i++) {
			BlogPosting blogPosting = BlogPosting.toDTO(
				String.valueOf(jsonArray.getJSONObject(i)));

			if (blogPosting == null) {
				_log.error(
					"Unable to transform blog posting from JSON: " + json);

				continue;
			}

			blogPosting =
				blogPostingResource.putSiteBlogPostingByExternalReferenceCode(
					serviceContext.getScopeGroupId(),
					blogPosting.getExternalReferenceCode(), blogPosting);

			stringUtilReplaceValues.put(
				"BLOG_POSTING_ID:" + blogPosting.getExternalReferenceCode(),
				String.valueOf(blogPosting.getId()));
		}
	}

	private void _addOrUpdateClientExtensionEntries(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		List<CET> cets = _cetManager.getCETs(
			serviceContext.getCompanyId(), null, null,
			Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null);

		for (CET cet : cets) {
			stringUtilReplaceValues.put(
				"CLIENT_EXTENSION_ENTRY_ERC:" + cet.getExternalReferenceCode(),
				StringBundler.concat(
					"com_liferay_client_extension_web_internal_portlet_",
					"ClientExtensionEntryPortlet_", cet.getCompanyId(), "_",
					CETUtil.normalizeExternalReferenceCodeForPortletId(
						cet.getExternalReferenceCode())));
		}

		String json = SiteInitializerUtil.read(
			"/site-initializer/client-extension-entries.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			StringBundler sb = new StringBundler();

			JSONObject propertiesJSONObject = jsonObject.getJSONObject(
				"properties");

			if (propertiesJSONObject != null) {
				for (String key : propertiesJSONObject.keySet()) {
					sb.append(key);
					sb.append(StringPool.EQUAL);
					sb.append(propertiesJSONObject.getString(key));
					sb.append(StringPool.NEW_LINE);
				}
			}

			_clientExtensionEntryLocalService.addOrUpdateClientExtensionEntry(
				jsonObject.getString("externalReferenceCode"),
				serviceContext.getUserId(), StringPool.BLANK,
				SiteInitializerUtil.toMap(jsonObject.getString("name_i18n")),
				sb.toString(), StringPool.BLANK,
				ClientExtensionEntryConstants.TYPE_CUSTOM_ELEMENT,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"cssURLs",
					_replace(
						StringUtil.merge(
							JSONUtil.toStringArray(
								jsonObject.getJSONArray("cssURLs")),
							StringPool.NEW_LINE),
						stringUtilReplaceValues)
				).put(
					"friendlyURLMapping", StringPool.BLANK
				).put(
					"htmlElementName", jsonObject.getString("htmlElementName")
				).put(
					"instanceable", jsonObject.getBoolean("instanceable")
				).put(
					"portletCategoryName",
					jsonObject.getString("portletCategoryName")
				).put(
					"urls",
					_replace(
						StringUtil.merge(
							JSONUtil.toStringArray(
								jsonObject.getJSONArray("elementURLs")),
							StringPool.NEW_LINE),
						stringUtilReplaceValues)
				).put(
					"useESM", jsonObject.getBoolean("useESM", false)
				).buildString());

			stringUtilReplaceValues.put(
				"CLIENT_EXTENSION_ENTRY_ERC:" +
					jsonObject.getString("externalReferenceCode"),
				StringBundler.concat(
					"com_liferay_client_extension_web_internal_portlet_",
					"ClientExtensionEntryPortlet_",
					serviceContext.getCompanyId(), "_",
					CETUtil.normalizeExternalReferenceCodeForPortletId(
						jsonObject.getString("externalReferenceCode"))));
		}
	}

	private void _addOrUpdateDataDefinitions(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		List<DDMStructure> ddmStructures =
			_ddmStructureLocalService.getStructures(
				serviceContext.getScopeGroupId());

		for (DDMStructure ddmStructure : ddmStructures) {
			stringUtilReplaceValues.put(
				"DDM_STRUCTURE_ID:" + ddmStructure.getStructureKey(),
				String.valueOf(ddmStructure.getStructureId()));
		}

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/data-definitions");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		for (String resourcePath : resourcePaths) {
			String json = _replace(
				SiteInitializerUtil.read(resourcePath, _servletContext),
				stringUtilReplaceValues);

			DataDefinition dataDefinition = DataDefinition.toDTO(json);

			if (dataDefinition == null) {
				_log.error(
					"Unable to transform data definition from JSON: " + json);

				continue;
			}

			try {
				DataDefinition existingDataDefinition =
					dataDefinitionResource.
						getSiteDataDefinitionByContentTypeByDataDefinitionKey(
							serviceContext.getScopeGroupId(),
							dataDefinition.getContentType(),
							dataDefinition.getDataDefinitionKey());

				dataDefinition = dataDefinitionResource.putDataDefinition(
					existingDataDefinition.getId(), dataDefinition);
			}
			catch (NoSuchStructureException noSuchStructureException) {
				dataDefinition =
					dataDefinitionResource.postSiteDataDefinitionByContentType(
						serviceContext.getScopeGroupId(),
						dataDefinition.getContentType(), dataDefinition);
			}

			stringUtilReplaceValues.put(
				"DATA_DEFINITION_ID:" + dataDefinition.getDataDefinitionKey(),
				String.valueOf(dataDefinition.getId()));
		}
	}

	private void _addOrUpdateDDMStructures(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/ddm-structures");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			_defaultDDMStructureHelper.addOrUpdateDDMStructures(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				_portal.getClassNameId(JournalArticle.class), _classLoader,
				resourcePath, serviceContext);
		}

		List<DDMStructure> ddmStructures =
			_ddmStructureLocalService.getStructures(
				serviceContext.getScopeGroupId());

		for (DDMStructure ddmStructure : ddmStructures) {
			stringUtilReplaceValues.put(
				"DDM_STRUCTURE_ID:" + ddmStructure.getStructureKey(),
				String.valueOf(ddmStructure.getStructureId()));
		}
	}

	private void _addOrUpdateDDMTemplates(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		List<DDMTemplate> ddmTemplates =
			_ddmTemplateLocalService.getDDMTemplates(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (DDMTemplate ddmTemplate : ddmTemplates) {
			TemplateEntry templateEntry =
				_templateEntryLocalService.fetchTemplateEntryByDDMTemplateId(
					ddmTemplate.getTemplateId());

			if (templateEntry != null) {
				stringUtilReplaceValues.put(
					"TEMPLATE_ENTRY_ID:" +
						ddmTemplate.getName(LocaleUtil.getSiteDefault()),
					String.valueOf(templateEntry.getTemplateEntryId()));
			}

			stringUtilReplaceValues.put(
				"DDM_TEMPLATE_ID:" +
					ddmTemplate.getName(LocaleUtil.getSiteDefault()),
				String.valueOf(ddmTemplate.getTemplateId()));
		}

		Enumeration<URL> enumeration = _siteBundle.findEntries(
			"/site-initializer/ddm-templates", "ddm-template.json", true);

		if (enumeration == null) {
			return;
		}

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				_replace(URLUtil.toString(url), stringUtilReplaceValues));

			long resourceClassNameId = _portal.getClassNameId(
				jsonObject.getString(
					"resourceClassName", JournalArticle.class.getName()));

			long ddmStructureId = 0;

			String ddmStructureKey = jsonObject.getString("ddmStructureKey");

			if (Validator.isNotNull(ddmStructureKey)) {
				DDMStructure ddmStructure =
					_ddmStructureLocalService.fetchStructure(
						serviceContext.getScopeGroupId(), resourceClassNameId,
						ddmStructureKey);

				ddmStructureId = ddmStructure.getStructureId();
			}

			DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
				serviceContext.getScopeGroupId(),
				_portal.getClassNameId(
					jsonObject.getString(
						"className", DDMStructure.class.getName())),
				jsonObject.getString("ddmTemplateKey"));

			if (ddmTemplate == null) {
				ddmTemplate = _ddmTemplateLocalService.addTemplate(
					null, serviceContext.getUserId(),
					serviceContext.getScopeGroupId(),
					_portal.getClassNameId(
						jsonObject.getString(
							"className", DDMStructure.class.getName())),
					ddmStructureId, resourceClassNameId,
					jsonObject.getString("ddmTemplateKey"),
					HashMapBuilder.put(
						LocaleUtil.getSiteDefault(),
						jsonObject.getString("name")
					).build(),
					null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, null,
					TemplateConstants.LANG_TYPE_FTL,
					SiteInitializerUtil.read(
						_siteBundle, "ddm-template.ftl", url),
					false, false, null, null, serviceContext);

				if (Objects.equals(
						jsonObject.getString("className"),
						TemplateEntry.class.getName())) {

					TemplateEntry templateEntry =
						_templateEntryLocalService.addTemplateEntry(
							null, serviceContext.getUserId(),
							serviceContext.getScopeGroupId(),
							ddmTemplate.getTemplateId(),
							jsonObject.getString("infoItemClassName"),
							jsonObject.getString("infoItemKey"),
							serviceContext);

					String templateEntryIdKey =
						"TEMPLATE_ENTRY_ID:" +
							ddmTemplate.getName(LocaleUtil.getSiteDefault());
					String templateEntryIdValue = String.valueOf(
						templateEntry.getTemplateEntryId());

					stringUtilReplaceValues.put(
						templateEntryIdKey, templateEntryIdValue);
				}
			}
			else {
				_ddmTemplateLocalService.updateTemplate(
					serviceContext.getUserId(), ddmTemplate.getTemplateId(),
					ddmStructureId,
					HashMapBuilder.put(
						LocaleUtil.getSiteDefault(),
						jsonObject.getString("name")
					).build(),
					null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, null,
					TemplateConstants.LANG_TYPE_FTL,
					SiteInitializerUtil.read(
						_siteBundle, "ddm-template.ftl", url),
					false, false, null, null, serviceContext);
			}

			stringUtilReplaceValues.put(
				"DDM_TEMPLATE_ID:" +
					ddmTemplate.getName(LocaleUtil.getSiteDefault()),
				String.valueOf(ddmTemplate.getTemplateId()));
		}
	}

	private void _addOrUpdateDepotEntries(ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/depot-entries.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Group group = _groupLocalService.fetchGroup(
				serviceContext.getCompanyId(),
				SiteInitializerUtil.toMap(
					jsonObject.getString("name_i18n")
				).get(
					LocaleUtil.getSiteDefault()
				));

			DepotEntry depotEntry = null;

			if (group == null) {
				depotEntry = _depotEntryLocalService.addDepotEntry(
					SiteInitializerUtil.toMap(
						jsonObject.getString("name_i18n")),
					SiteInitializerUtil.toMap(
						jsonObject.getString("description_i18n")),
					_getDepotEntryType(jsonObject.getString("type")),
					serviceContext);
			}

			UnicodeProperties unicodeProperties = new UnicodeProperties(true);

			JSONArray typeSettingsJSONArray = jsonObject.getJSONArray(
				"typeSettings");

			if (typeSettingsJSONArray != null) {
				for (int j = 0; j < typeSettingsJSONArray.length(); j++) {
					JSONObject propertyJSONObject =
						typeSettingsJSONArray.getJSONObject(j);

					unicodeProperties.put(
						propertyJSONObject.getString("key"),
						propertyJSONObject.getString("value"));
				}
			}

			JSONObject depotAppCustomizationJSONObject =
				jsonObject.getJSONObject("depotAppCustomization");

			_depotEntryLocalService.updateDepotEntry(
				(group != null) ? group.getClassPK() :
					depotEntry.getDepotEntryId(),
				SiteInitializerUtil.toMap(jsonObject.getString("name_i18n")),
				SiteInitializerUtil.toMap(
					jsonObject.getString("description_i18n")),
				HashMapBuilder.put(
					PortletKeys.ASSET_LIST,
					GetterUtil.getBoolean(
						depotAppCustomizationJSONObject.getBoolean(
							PortletKeys.ASSET_LIST),
						true)
				).put(
					PortletKeys.DOCUMENT_LIBRARY_ADMIN,
					GetterUtil.getBoolean(
						depotAppCustomizationJSONObject.getBoolean(
							PortletKeys.DOCUMENT_LIBRARY_ADMIN),
						true)
				).put(
					PortletKeys.JOURNAL,
					GetterUtil.getBoolean(
						depotAppCustomizationJSONObject.getBoolean(
							PortletKeys.JOURNAL),
						true)
				).put(
					PortletKeys.TRANSLATION,
					GetterUtil.getBoolean(
						depotAppCustomizationJSONObject.getBoolean(
							PortletKeys.TRANSLATION),
						true)
				).build(),
				unicodeProperties, serviceContext);

			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				(group != null) ? group.getClassPK() :
					depotEntry.getDepotEntryId(),
				serviceContext.getScopeGroupId());
		}
	}

	private Long _addOrUpdateDocumentFolder(
			Long documentFolderId, long groupId, String resourcePath,
			ServiceContext serviceContext)
		throws Exception {

		DocumentFolderResource.Builder documentFolderResourceBuilder =
			_documentFolderResourceFactory.create();

		DocumentFolderResource documentFolderResource =
			documentFolderResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		DocumentFolder documentFolder = null;

		resourcePath = resourcePath.substring(0, resourcePath.length() - 1);

		String json = SiteInitializerUtil.read(
			resourcePath + ".metadata.json", _servletContext);

		if (json != null) {
			documentFolder = DocumentFolder.toDTO(json);
		}
		else {
			documentFolder = DocumentFolder.toDTO(
				JSONUtil.put(
					"name", FileUtil.getShortFileName(resourcePath)
				).put(
					"viewableBy", "Anyone"
				).toString());
		}

		Page<DocumentFolder> documentFoldersPage =
			documentFolderResource.getSiteDocumentFoldersPage(
				groupId, true, null, null,
				documentFolderResource.toFilter(
					StringBundler.concat(
						"name eq '", documentFolder.getName(), "'")),
				null, null);

		DocumentFolder existingDocumentFolder =
			documentFoldersPage.fetchFirstItem();

		if (existingDocumentFolder == null) {
			if (documentFolderId != null) {
				documentFolder =
					documentFolderResource.postDocumentFolderDocumentFolder(
						documentFolderId, documentFolder);
			}
			else {
				documentFolder = documentFolderResource.postSiteDocumentFolder(
					groupId, documentFolder);
			}
		}
		else {
			documentFolder = documentFolderResource.putDocumentFolder(
				existingDocumentFolder.getId(), documentFolder);
		}

		return documentFolder.getId();
	}

	private void _addOrUpdateDocuments(
			Long documentFolderId, long groupId, String parentResourcePath,
			ServiceContext serviceContext,
			SiteNavigationMenuItemSettingsBuilder
				siteNavigationMenuItemSettingsBuilder,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			parentResourcePath);

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		DocumentResource.Builder documentResourceBuilder =
			_documentResourceFactory.create();

		DocumentResource documentResource = documentResourceBuilder.user(
			serviceContext.fetchUser()
		).build();

		for (String resourcePath : resourcePaths) {
			if (resourcePath.endsWith("/")) {
				_addOrUpdateDocuments(
					_addOrUpdateDocumentFolder(
						documentFolderId, groupId, resourcePath,
						serviceContext),
					groupId, resourcePath, serviceContext,
					siteNavigationMenuItemSettingsBuilder,
					stringUtilReplaceValues);

				continue;
			}

			if (resourcePath.endsWith(".gitkeep") ||
				resourcePath.endsWith(".metadata.json")) {

				continue;
			}

			String fileName = FileUtil.getShortFileName(resourcePath);

			URL url = _servletContext.getResource(resourcePath);

			URLConnection urlConnection = url.openConnection();

			Map<String, String> values = new HashMap<>();

			String json = SiteInitializerUtil.read(
				resourcePath + ".metadata.json", _servletContext);

			if (json != null) {
				values = Collections.singletonMap("document", json);
			}
			else {
				values = Collections.singletonMap(
					"document",
					JSONUtil.put(
						"viewableBy", "Anyone"
					).toString());
			}

			Document document = null;

			if (documentFolderId != null) {
				Page<Document> documentsPage =
					documentResource.getDocumentFolderDocumentsPage(
						documentFolderId, false, null, null,
						documentResource.toFilter(
							StringBundler.concat("title eq '", fileName, "'")),
						null, null);

				Document existingDocument = documentsPage.fetchFirstItem();

				if (existingDocument == null) {
					document = documentResource.postDocumentFolderDocument(
						documentFolderId,
						MultipartBody.of(
							Collections.singletonMap(
								"file",
								new BinaryFile(
									MimeTypesUtil.getContentType(fileName),
									fileName, urlConnection.getInputStream(),
									urlConnection.getContentLength())),
							__ -> _objectMapper, values));
				}
				else {
					document = documentResource.putDocument(
						existingDocument.getId(),
						MultipartBody.of(
							Collections.singletonMap(
								"file",
								new BinaryFile(
									MimeTypesUtil.getContentType(fileName),
									fileName, urlConnection.getInputStream(),
									urlConnection.getContentLength())),
							__ -> _objectMapper, values));
				}
			}
			else {
				Page<Document> documentsPage =
					documentResource.getSiteDocumentsPage(
						groupId, false, null, null,
						documentResource.toFilter(
							StringBundler.concat("title eq '", fileName, "'")),
						null, null);

				Document existingDocument = documentsPage.fetchFirstItem();

				if (existingDocument == null) {
					document = documentResource.postSiteDocument(
						groupId,
						MultipartBody.of(
							Collections.singletonMap(
								"file",
								new BinaryFile(
									MimeTypesUtil.getContentType(fileName),
									fileName, urlConnection.getInputStream(),
									urlConnection.getContentLength())),
							__ -> _objectMapper, values));
				}
				else {
					document = documentResource.putDocument(
						existingDocument.getId(),
						MultipartBody.of(
							Collections.singletonMap(
								"file",
								new BinaryFile(
									MimeTypesUtil.getContentType(fileName),
									fileName, urlConnection.getInputStream(),
									urlConnection.getContentLength())),
							__ -> _objectMapper, values));
				}
			}

			String key = resourcePath;

			FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
				document.getId());

			stringUtilReplaceValues.put(
				"DOCUMENT_FILE_ENTRY_ID:" + key,
				String.valueOf(fileEntry.getFileEntryId()));

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				_jsonFactory.looseSerialize(fileEntry));

			jsonObject.put("alt", StringPool.BLANK);

			stringUtilReplaceValues.put(
				"DOCUMENT_JSON:" + key, jsonObject.toString());

			stringUtilReplaceValues.put(
				"DOCUMENT_URL:" + key,
				_dlURLHelper.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK, false, false));

			long fileEntryTypeId = 0;

			if (fileEntry.getModel() instanceof DLFileEntry) {
				DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

				DLFileEntryType dlFileEntryType =
					dlFileEntry.getDLFileEntryType();

				fileEntryTypeId = dlFileEntryType.getFileEntryTypeId();
			}

			String fileEntryTypeIdString = String.valueOf(fileEntryTypeId);

			siteNavigationMenuItemSettingsBuilder.put(
				key,
				new SiteNavigationMenuItemSetting() {
					{
						className = FileEntry.class.getName();
						classPK = String.valueOf(fileEntry.getFileEntryId());
						classTypeId = fileEntryTypeIdString;
						title = fileEntry.getTitle();
						type = ResourceActionsUtil.getModelResource(
							serviceContext.getLocale(),
							FileEntry.class.getName());
					}
				});
		}
	}

	private void _addOrUpdateDocuments(
			ServiceContext serviceContext,
			SiteNavigationMenuItemSettingsBuilder
				siteNavigationMenuItemSettingsBuilder,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Group group = _groupLocalService.getCompanyGroup(
			serviceContext.getCompanyId());

		_addOrUpdateDocuments(
			null, group.getGroupId(), "/site-initializer/documents/company",
			serviceContext, siteNavigationMenuItemSettingsBuilder,
			stringUtilReplaceValues);

		_addOrUpdateDocuments(
			null, serviceContext.getScopeGroupId(),
			"/site-initializer/documents/group", serviceContext,
			siteNavigationMenuItemSettingsBuilder, stringUtilReplaceValues);
	}

	private void _addOrUpdateExpandoColumns(ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/expando-columns.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			ExpandoBridge expandoBridge =
				ExpandoBridgeFactoryUtil.getExpandoBridge(
					serviceContext.getCompanyId(),
					jsonObject.getString("modelResource"));

			if (expandoBridge == null) {
				continue;
			}

			if (expandoBridge.getAttribute(jsonObject.getString("name")) !=
					null) {

				expandoBridge.setAttributeDefault(
					jsonObject.getString("name"),
					_getExpandoAttributeValue(jsonObject));
			}
			else {
				expandoBridge.addAttribute(
					jsonObject.getString("name"), jsonObject.getInt("dataType"),
					_getExpandoAttributeValue(jsonObject));
			}

			if (jsonObject.has("properties")) {
				UnicodeProperties unicodeProperties = new UnicodeProperties(
					true);

				JSONObject propertiesJSONObject = jsonObject.getJSONObject(
					"properties");

				Map<String, Object> map = propertiesJSONObject.toMap();

				for (Map.Entry<String, Object> entry : map.entrySet()) {
					unicodeProperties.setProperty(
						TextFormatter.format(entry.getKey(), TextFormatter.K),
						String.valueOf(entry.getValue()));
				}

				expandoBridge.setAttributeProperties(
					jsonObject.getString("name"), unicodeProperties);
			}
		}
	}

	private void _addOrUpdateJournalArticles(
			Long documentFolderId, String parentResourcePath,
			ServiceContext serviceContext,
			SiteNavigationMenuItemSettingsBuilder
				siteNavigationMenuItemSettingsBuilder,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			parentResourcePath);

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			parentResourcePath = resourcePath.substring(
				0, resourcePath.length() - 1);

			if (resourcePath.endsWith("/")) {
				_addOrUpdateJournalArticles(
					_addOrUpdateStructuredContentFolders(
						documentFolderId, parentResourcePath, serviceContext),
					resourcePath, serviceContext,
					siteNavigationMenuItemSettingsBuilder,
					stringUtilReplaceValues);

				continue;
			}

			if (resourcePath.endsWith(".gitkeep") ||
				resourcePath.endsWith(".metadata.json") ||
				resourcePath.endsWith(".xml")) {

				continue;
			}

			long journalFolderId =
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID;

			if (documentFolderId != null) {
				journalFolderId = documentFolderId;
			}

			String json = SiteInitializerUtil.read(
				resourcePath, _servletContext);

			JSONObject jsonObject = _jsonFactory.createJSONObject(json);

			String articleId = jsonObject.getString("articleId");

			if (Validator.isNull(articleId)) {
				articleId = FileUtil.stripExtension(
					FileUtil.getShortFileName(resourcePath));
			}

			Map<Locale, String> titleMap = Collections.singletonMap(
				LocaleUtil.getSiteDefault(), jsonObject.getString("name"));

			String ddmStructureKey = jsonObject.getString("ddmStructureKey");

			DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
				serviceContext.getScopeGroupId(),
				_portal.getClassNameId(JournalArticle.class), ddmStructureKey,
				true);

			Calendar calendar = CalendarFactoryUtil.getCalendar(
				serviceContext.getTimeZone());

			serviceContext.setAssetCategoryIds(
				_getAssetCategoryIds(
					serviceContext.getScopeGroupId(),
					JSONUtil.toStringArray(
						jsonObject.getJSONArray("assetCategoryERCs"))));
			serviceContext.setAssetTagNames(
				JSONUtil.toStringArray(
					jsonObject.getJSONArray("assetTagNames")));

			JournalArticle journalArticle =
				_journalArticleLocalService.fetchArticle(
					serviceContext.getScopeGroupId(), articleId);

			if (journalArticle == null) {
				journalArticle = _journalArticleLocalService.addArticle(
					null, serviceContext.getUserId(),
					serviceContext.getScopeGroupId(), journalFolderId,
					JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0, articleId,
					false, 1, titleMap, null, titleMap,
					_replace(
						SiteInitializerUtil.read(
							_replace(resourcePath, ".json", ".xml"),
							_servletContext),
						stringUtilReplaceValues),
					ddmStructure.getStructureId(),
					jsonObject.getString("ddmTemplateKey"), null,
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, 0, 0, 0,
					0, 0, true, true, false, 0, 0, null, null, null, null,
					serviceContext);
			}
			else {
				journalArticle = _journalArticleLocalService.updateArticle(
					serviceContext.getUserId(),
					serviceContext.getScopeGroupId(), journalFolderId,
					articleId, journalArticle.getVersion(), titleMap, null,
					titleMap,
					_replace(
						SiteInitializerUtil.read(
							_replace(resourcePath, ".json", ".xml"),
							_servletContext),
						stringUtilReplaceValues),
					jsonObject.getString("ddmTemplateKey"), null,
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, 0, 0, 0,
					0, 0, true, true, false, 0, 0, null, null, null, null,
					serviceContext);
			}

			JournalArticle finalJournalArticle = journalArticle;

			serviceContext.setAssetCategoryIds(null);
			serviceContext.setAssetTagNames(null);

			siteNavigationMenuItemSettingsBuilder.put(
				resourcePath,
				new SiteNavigationMenuItemSetting() {
					{
						className = JournalArticle.class.getName();
						classPK = String.valueOf(
							finalJournalArticle.getResourcePrimKey());
						classTypeId = String.valueOf(
							ddmStructure.getStructureId());
						title = finalJournalArticle.getTitle(
							serviceContext.getLocale());
						type = ResourceActionsUtil.getModelResource(
							serviceContext.getLocale(),
							JournalArticle.class.getName());
					}
				});
		}
	}

	private void _addOrUpdateJournalArticles(
			ServiceContext serviceContext,
			SiteNavigationMenuItemSettingsBuilder
				siteNavigationMenuItemSettingsBuilder,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		stringUtilReplaceValues.put(
			"LOCALE_DEFAULT",
			LocaleUtil.toLanguageId(
				_portal.getSiteDefaultLocale(
					serviceContext.getScopeGroupId())));

		_addOrUpdateJournalArticles(
			null, "/site-initializer/journal-articles", serviceContext,
			siteNavigationMenuItemSettingsBuilder, stringUtilReplaceValues);
	}

	private KnowledgeBaseArticle _addOrUpdateKnowledgeBaseArticle(
			boolean folder, JSONObject jsonObject,
			long parentKnowledgeBaseObjectId, ServiceContext serviceContext)
		throws Exception {

		KnowledgeBaseArticleResource.Builder
			knowledgeBaseArticleResourceBuilder =
				_knowledgeBaseArticleResourceFactory.create();

		KnowledgeBaseArticleResource knowledgeBaseArticleResource =
			knowledgeBaseArticleResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		KnowledgeBaseArticle knowledgeBaseArticle = KnowledgeBaseArticle.toDTO(
			jsonObject.toString());

		if (!folder) {
			knowledgeBaseArticle.setParentKnowledgeBaseArticleId(
				() -> parentKnowledgeBaseObjectId);
		}
		else {
			knowledgeBaseArticle.setParentKnowledgeBaseFolderId(
				() -> parentKnowledgeBaseObjectId);
		}

		return knowledgeBaseArticleResource.
			putSiteKnowledgeBaseArticleByExternalReferenceCode(
				serviceContext.getScopeGroupId(),
				knowledgeBaseArticle.getExternalReferenceCode(),
				knowledgeBaseArticle);
	}

	private void _addOrUpdateKnowledgeBaseArticle(
			boolean folder, JSONObject jsonObject,
			long parentKnowledgeBaseObjectId, String resourcePath,
			ServiceContext serviceContext)
		throws Exception {

		KnowledgeBaseArticle knowledgeBaseArticle =
			_addOrUpdateKnowledgeBaseArticle(
				folder, jsonObject, parentKnowledgeBaseObjectId,
				serviceContext);

		_addOrKnowledgeBaseObjects(
			false, knowledgeBaseArticle.getId(), resourcePath, serviceContext);
	}

	private void _addOrUpdateKnowledgeBaseArticles(
			ServiceContext serviceContext)
		throws Exception {

		_addOrKnowledgeBaseObjects(
			true, 0, "/site-initializer/knowledge-base-articles",
			serviceContext);
	}

	private KnowledgeBaseFolder _addOrUpdateKnowledgeBaseFolder(
			JSONObject jsonObject, long parentKnowledgeBaseObjectId,
			ServiceContext serviceContext)
		throws Exception {

		KnowledgeBaseFolderResource.Builder knowledgeBaseFolderResourceBuilder =
			_knowledgeBaseFolderResourceFactory.create();

		KnowledgeBaseFolderResource knowledgeBaseFolderResource =
			knowledgeBaseFolderResourceBuilder.httpServletRequest(
				serviceContext.getRequest()
			).user(
				serviceContext.fetchUser()
			).build();

		KnowledgeBaseFolder knowledgeBaseFolder = KnowledgeBaseFolder.toDTO(
			jsonObject.toString());

		knowledgeBaseFolder.setParentKnowledgeBaseFolderId(
			() -> parentKnowledgeBaseObjectId);

		return knowledgeBaseFolderResource.
			putSiteKnowledgeBaseFolderByExternalReferenceCode(
				serviceContext.getScopeGroupId(),
				knowledgeBaseFolder.getExternalReferenceCode(),
				knowledgeBaseFolder);
	}

	private void _addOrUpdateKnowledgeBaseFolder(
			JSONObject jsonObject, long parentKnowledgeBaseObjectId,
			String resourcePath, ServiceContext serviceContext)
		throws Exception {

		KnowledgeBaseFolder knowledgeBaseFolder =
			_addOrUpdateKnowledgeBaseFolder(
				jsonObject, parentKnowledgeBaseObjectId, serviceContext);

		_addOrKnowledgeBaseObjects(
			true, knowledgeBaseFolder.getId(), resourcePath, serviceContext);
	}

	private Map<String, Layout> _addOrUpdateLayout(
			long parentLayoutId, String parentResourcePath,
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		JSONObject pageJSONObject = _jsonFactory.createJSONObject(
			_replace(
				SiteInitializerUtil.read(
					parentResourcePath + "page.json", _servletContext),
				stringUtilReplaceValues));

		Map<Locale, String> nameMap = new HashMap<>(
			SiteInitializerUtil.toMap(pageJSONObject.getString("name_i18n")));

		Locale siteDefaultLocale = _portal.getSiteDefaultLocale(
			serviceContext.getScopeGroupId());

		if (!nameMap.containsKey(siteDefaultLocale)) {
			nameMap.put(siteDefaultLocale, pageJSONObject.getString("name"));
		}

		String type = StringUtil.toLowerCase(pageJSONObject.getString("type"));

		if (Objects.equals(type, "link_to_layout")) {
			type = LayoutConstants.TYPE_LINK_TO_LAYOUT;
		}
		else if (Objects.equals(type, "url")) {
			type = LayoutConstants.TYPE_URL;
		}
		else if (Objects.equals(type, "widget")) {
			type = LayoutConstants.TYPE_PORTLET;
		}

		Map<Locale, String> friendlyURLMap = new HashMap<>(
			SiteInitializerUtil.toMap(
				pageJSONObject.getString("friendlyURL_i18n")));

		if (!friendlyURLMap.containsKey(siteDefaultLocale)) {
			friendlyURLMap.put(
				siteDefaultLocale, pageJSONObject.getString("friendlyURL"));
		}

		UnicodeProperties unicodeProperties = new UnicodeProperties(true);

		JSONArray typeSettingsJSONArray = pageJSONObject.getJSONArray(
			"typeSettings");

		if (typeSettingsJSONArray != null) {
			for (int i = 0; i < typeSettingsJSONArray.length(); i++) {
				JSONObject propertyJSONObject =
					typeSettingsJSONArray.getJSONObject(i);

				unicodeProperties.put(
					propertyJSONObject.getString("key"),
					propertyJSONObject.getString("value"));
			}
		}

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			serviceContext.getScopeGroupId(),
			pageJSONObject.getBoolean("private"),
			pageJSONObject.getString("friendlyURL"));

		if ((layout != null) && !Objects.equals(layout.getType(), type)) {
			_layoutLocalService.deleteLayout(layout);

			layout = null;
		}

		if (layout != null) {
			_layoutLocalService.updateLayout(
				serviceContext.getScopeGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId(), parentLayoutId, nameMap,
				SiteInitializerUtil.toMap(
					pageJSONObject.getString("title_i18n")),
				SiteInitializerUtil.toMap(
					pageJSONObject.getString("description_i18n")),
				SiteInitializerUtil.toMap(
					pageJSONObject.getString("keywords_i18n")),
				SiteInitializerUtil.toMap(
					pageJSONObject.getString("robots_i18n")),
				type, pageJSONObject.getBoolean("hidden"),
				layout.getFriendlyURLMap(), layout.getIconImage(), null,
				layout.getStyleBookEntryId(),
				pageJSONObject.getLong("faviconFileEntryId"),
				layout.getMasterLayoutPlid(), serviceContext);
			_layoutLocalService.updateLayout(
				serviceContext.getScopeGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId(), unicodeProperties.toString());
		}
		else {
			layout = _layoutLocalService.addLayout(
				null, serviceContext.getUserId(),
				serviceContext.getScopeGroupId(),
				pageJSONObject.getBoolean("private"), parentLayoutId, nameMap,
				SiteInitializerUtil.toMap(
					pageJSONObject.getString("title_i18n")),
				SiteInitializerUtil.toMap(
					pageJSONObject.getString("description_i18n")),
				SiteInitializerUtil.toMap(
					pageJSONObject.getString("keywords_i18n")),
				SiteInitializerUtil.toMap(
					pageJSONObject.getString("robots_i18n")),
				type, unicodeProperties.toString(),
				pageJSONObject.getBoolean("hidden"),
				pageJSONObject.getBoolean("system"), friendlyURLMap,
				serviceContext);
		}

		_setResourcePermissions(
			layout.getCompanyId(), layout.getModelClassName(),
			pageJSONObject.getJSONArray("permissions"),
			String.valueOf(layout.getPlid()));

		if (pageJSONObject.has("priority")) {
			layout = _layoutLocalService.updatePriority(
				layout.getPlid(), pageJSONObject.getInt("priority"));
		}

		stringUtilReplaceValues.put(
			"LAYOUT_ID:" + layout.getName(LocaleUtil.getSiteDefault()),
			String.valueOf(layout.getLayoutId()));

		Map<String, Layout> layoutsMap = HashMapBuilder.<String, Layout>put(
			parentResourcePath, layout
		).build();

		String layoutTemplateId = StringUtil.toLowerCase(
			pageJSONObject.getString("layoutTemplateId"));

		if (Validator.isNotNull(layoutTemplateId)) {
			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			layoutTypePortlet.setLayoutTemplateId(0, layoutTemplateId, false);
		}

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			parentResourcePath);

		if (SetUtil.isEmpty(resourcePaths)) {
			return layoutsMap;
		}

		Set<String> sortedResourcePaths = new TreeSet<>(
			new NaturalOrderStringComparator());

		sortedResourcePaths.addAll(resourcePaths);

		resourcePaths = sortedResourcePaths;

		for (String resourcePath : resourcePaths) {
			if (resourcePath.endsWith("/")) {
				layoutsMap.putAll(
					_addOrUpdateLayout(
						layout.getLayoutId(), resourcePath, serviceContext,
						stringUtilReplaceValues));
			}
		}

		return layoutsMap;
	}

	private void _addOrUpdateLayoutContent(
			Layout layout, String resourcePath, long segmentsExperienceId,
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		JSONObject pageJSONObject = _jsonFactory.createJSONObject(
			SiteInitializerUtil.read(
				resourcePath + "page.json", _servletContext));

		String type = StringUtil.toLowerCase(pageJSONObject.getString("type"));

		if (Objects.equals(type, "url")) {
			return;
		}
		else if (Objects.equals(type, "widget")) {
			type = LayoutConstants.TYPE_PORTLET;
		}

		String json = SiteInitializerUtil.read(
			resourcePath + "page-definition.json", _servletContext);

		if (json == null) {
			return;
		}

		json = _replace(
			SiteInitializerUtil.replace(json, serviceContext),
			stringUtilReplaceValues);

		JSONObject pageDefinitionJSONObject = _jsonFactory.createJSONObject(
			json);

		if (!Objects.equals(type, LayoutConstants.TYPE_CONTENT) &&
			!Objects.equals(type, LayoutConstants.TYPE_UTILITY)) {

			return;
		}

		Layout draftLayout = layout.fetchDraftLayout();

		JSONObject pageElementJSONObject =
			pageDefinitionJSONObject.getJSONObject("pageElement");

		if ((pageElementJSONObject != null) &&
			Objects.equals(pageElementJSONObject.getString("type"), "Root")) {

			JSONArray jsonArray = pageElementJSONObject.getJSONArray(
				"pageElements");

			if (!JSONUtil.isEmpty(jsonArray)) {
				LayoutPageTemplateStructure layoutPageTemplateStructure =
					_layoutPageTemplateStructureLocalService.
						fetchLayoutPageTemplateStructure(
							draftLayout.getGroupId(), draftLayout.getPlid());

				LayoutStructure layoutStructure = new LayoutStructure();

				layoutStructure.addRootLayoutStructureItem();

				if (segmentsExperienceId == 0) {
					segmentsExperienceId =
						_segmentsExperienceLocalService.
							fetchDefaultSegmentsExperienceId(
								draftLayout.getPlid());
				}

				if (Validator.isNull(
						layoutPageTemplateStructure.getData(
							segmentsExperienceId))) {

					_layoutPageTemplateStructureRelLocalService.
						addLayoutPageTemplateStructureRel(
							serviceContext.getUserId(),
							serviceContext.getScopeGroupId(),
							layoutPageTemplateStructure.
								getLayoutPageTemplateStructureId(),
							segmentsExperienceId, layoutStructure.toString(),
							serviceContext);
				}
				else {
					_layoutPageTemplateStructureRelLocalService.
						updateLayoutPageTemplateStructureRel(
							layoutPageTemplateStructure.
								getLayoutPageTemplateStructureId(),
							segmentsExperienceId, layoutStructure.toString());

					_fragmentEntryLinkLocalService.
						deleteLayoutPageTemplateEntryFragmentEntryLinks(
							draftLayout.getGroupId(),
							new long[] {segmentsExperienceId},
							draftLayout.getPlid());
				}

				for (int i = 0; i < jsonArray.length(); i++) {
					_layoutsImporter.importPageElement(
						serviceContext.getUserId(), draftLayout,
						layoutStructure, layoutStructure.getMainItemId(),
						jsonArray.getString(i), i, true, segmentsExperienceId);
				}
			}
		}

		JSONObject settingsJSONObject = pageDefinitionJSONObject.getJSONObject(
			"settings");

		if (settingsJSONObject != null) {
			draftLayout = _updateDraftLayout(draftLayout, settingsJSONObject);
		}

		layout = _layoutLocalService.copyLayoutContent(draftLayout, layout);

		_layoutLocalService.updateStatus(
			layout.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);
		_layoutLocalService.updateStatus(
			layout.getUserId(), layout.getPlid(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void _addOrUpdateLayouts(
			Map<String, Layout> layoutsMap, ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/layouts");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		List<Layout> layouts = _layoutLocalService.getLayouts(
			serviceContext.getScopeGroupId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		for (Layout layout : layouts) {
			stringUtilReplaceValues.put(
				"LAYOUT_ID:" + layout.getName(LocaleUtil.getSiteDefault()),
				String.valueOf(layout.getLayoutId()));
		}

		Set<String> sortedResourcePaths = new TreeSet<>(
			new NaturalOrderStringComparator());

		sortedResourcePaths.addAll(resourcePaths);

		resourcePaths = sortedResourcePaths;

		for (String resourcePath : resourcePaths) {
			if (resourcePath.endsWith("/")) {
				layoutsMap.putAll(
					_addOrUpdateLayout(
						LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, resourcePath,
						serviceContext, stringUtilReplaceValues));
			}
		}
	}

	private void _addOrUpdateLayoutsContent(
			Map<String, Layout> layouts, ServiceContext serviceContext,
			Map<String, SiteNavigationMenuItemSetting>
				siteNavigationMenuItemSettings,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		for (Map.Entry<String, Layout> entry : layouts.entrySet()) {
			_addOrUpdateLayoutContent(
				entry.getValue(), entry.getKey(), 0, serviceContext,
				stringUtilReplaceValues);
		}

		_addOrUpdateSiteNavigationMenus(
			serviceContext, siteNavigationMenuItemSettings,
			stringUtilReplaceValues);
	}

	private void _addOrUpdateListTypeDefinitions(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/list-type-definitions");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		ListTypeDefinitionResource.Builder listTypeDefinitionResourceBuilder =
			_listTypeDefinitionResourceFactory.create();

		ListTypeDefinitionResource listTypeDefinitionResource =
			listTypeDefinitionResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		for (String resourcePath : resourcePaths) {
			if (resourcePath.endsWith(".list-type-entries.json")) {
				continue;
			}

			String json = SiteInitializerUtil.read(
				resourcePath, _servletContext);

			ListTypeDefinition listTypeDefinition = ListTypeDefinition.toDTO(
				json);

			if (listTypeDefinition == null) {
				_log.error(
					"Unable to transform list type definition from JSON: " +
						json);

				continue;
			}

			ListTypeDefinition existingListTypeDefinition =
				listTypeDefinitionResource.
					getListTypeDefinitionByExternalReferenceCode(
						listTypeDefinition.getExternalReferenceCode());

			if (existingListTypeDefinition == null) {
				listTypeDefinition =
					listTypeDefinitionResource.postListTypeDefinition(
						listTypeDefinition);
			}
			else {
				listTypeDefinition =
					listTypeDefinitionResource.patchListTypeDefinition(
						existingListTypeDefinition.getId(), listTypeDefinition);
			}

			stringUtilReplaceValues.put(
				"LIST_TYPE_DEFINITION_ID:" + listTypeDefinition.getName(),
				String.valueOf(listTypeDefinition.getId()));

			String listTypeEntriesJSON = SiteInitializerUtil.read(
				_replace(resourcePath, ".json", ".list-type-entries.json"),
				_servletContext);

			if (listTypeEntriesJSON == null) {
				continue;
			}

			JSONArray jsonArray = _jsonFactory.createJSONArray(
				listTypeEntriesJSON);

			ListTypeEntryResource.Builder listTypeEntryResourceBuilder =
				_listTypeEntryResourceFactory.create();

			ListTypeEntryResource listTypeEntryResource =
				listTypeEntryResourceBuilder.user(
					serviceContext.fetchUser()
				).build();

			for (int i = 0; i < jsonArray.length(); i++) {
				ListTypeEntry listTypeEntry = ListTypeEntry.toDTO(
					String.valueOf(jsonArray.getJSONObject(i)));

				com.liferay.list.type.model.ListTypeEntry
					serviceBuilderListTypeEntry =
						_listTypeEntryLocalService.fetchListTypeEntry(
							listTypeDefinition.getId(), listTypeEntry.getKey());

				if (serviceBuilderListTypeEntry == null) {
					listTypeEntryResource.postListTypeDefinitionListTypeEntry(
						listTypeDefinition.getId(), listTypeEntry);
				}
				else {
					listTypeEntryResource.putListTypeEntry(
						serviceBuilderListTypeEntry.getListTypeEntryId(),
						listTypeEntry);
				}
			}
		}
	}

	private void _addOrUpdateNotificationTemplate(
			String resourcePath, ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		String json = SiteInitializerUtil.read(
			resourcePath + "notification-template.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONObject bodyJSONObject = _jsonFactory.createJSONObject();

		Enumeration<URL> enumeration = _siteBundle.findEntries(
			resourcePath, "*.html", true);

		if (enumeration == null) {
			return;
		}

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			bodyJSONObject.put(
				FileUtil.getShortFileName(
					FileUtil.stripExtension(url.getPath())),
				_replace(
					SiteInitializerUtil.replace(
						URLUtil.toString(url), serviceContext),
					stringUtilReplaceValues));
		}

		JSONObject notificationTemplateJSONObject =
			_jsonFactory.createJSONObject(json);

		notificationTemplateJSONObject.put("body", bodyJSONObject);

		NotificationTemplate notificationTemplate = NotificationTemplate.toDTO(
			notificationTemplateJSONObject.toString());

		NotificationTemplateResource.Builder
			notificationTemplateResourceBuilder =
				_notificationTemplateResourceFactory.create();

		NotificationTemplateResource notificationTemplateResource =
			notificationTemplateResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		NotificationTemplate existingNotificationTemplate =
			notificationTemplateResource.
				getNotificationTemplateByExternalReferenceCode(
					notificationTemplate.getExternalReferenceCode());

		if (existingNotificationTemplate == null) {
			notificationTemplate =
				notificationTemplateResource.postNotificationTemplate(
					notificationTemplate);
		}
		else {
			notificationTemplate =
				notificationTemplateResource.
					putNotificationTemplateByExternalReferenceCode(
						notificationTemplate.getExternalReferenceCode(),
						notificationTemplate);
		}

		json = SiteInitializerUtil.read(
			resourcePath + "notification-template.object-actions.json",
			_servletContext);

		if (json == null) {
			return;
		}

		json = _replace(json, stringUtilReplaceValues);

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		Map<String, Long> parametersMap = HashMapBuilder.put(
			"notificationTemplateId", notificationTemplate.getId()
		).build();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			_objectActionLocalService.addOrUpdateObjectAction(
				jsonObject.getString("externalReferenceCode"), 0,
				serviceContext.getUserId(),
				jsonObject.getLong("objectDefinitionId"),
				jsonObject.getBoolean("active"),
				jsonObject.getString("conditionExpression"),
				jsonObject.getString("description"),
				SiteInitializerUtil.toMap(jsonObject.getString("errorMessage")),
				SiteInitializerUtil.toMap(jsonObject.getString("label")),
				jsonObject.getString("name"),
				jsonObject.getString("objectActionExecutorKey"),
				jsonObject.getString("objectActionTriggerKey"),
				ObjectActionUtil.toParametersUnicodeProperties(parametersMap),
				jsonObject.getBoolean("system"));
		}
	}

	private void _addOrUpdateNotificationTemplates(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/notification-templates");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			_addOrUpdateNotificationTemplate(
				resourcePath, serviceContext, stringUtilReplaceValues);
		}
	}

	private void _addOrUpdateObjectActions(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/object-actions");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			String json = SiteInitializerUtil.read(
				resourcePath, _servletContext);

			json = _replace(json, stringUtilReplaceValues);

			if (json == null) {
				continue;
			}

			JSONObject jsonObject = _jsonFactory.createJSONObject(json);

			JSONArray jsonArray = jsonObject.getJSONArray("object-actions");

			if (JSONUtil.isEmpty(jsonArray)) {
				continue;
			}

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject objectActionJSONObject = jsonArray.getJSONObject(i);

				JSONObject parametersJSONObject =
					objectActionJSONObject.getJSONObject("parameters");

				_objectActionLocalService.addOrUpdateObjectAction(
					objectActionJSONObject.getString("externalReferenceCode"),
					0, serviceContext.getUserId(),
					jsonObject.getLong("objectDefinitionId"),
					objectActionJSONObject.getBoolean("active"),
					objectActionJSONObject.getString("conditionExpression"),
					objectActionJSONObject.getString("description"),
					SiteInitializerUtil.toMap(
						objectActionJSONObject.getString("errorMessage")),
					SiteInitializerUtil.toMap(
						objectActionJSONObject.getString("label")),
					objectActionJSONObject.getString("name"),
					objectActionJSONObject.getString("objectActionExecutorKey"),
					objectActionJSONObject.getString("objectActionTriggerKey"),
					ObjectActionUtil.toParametersUnicodeProperties(
						parametersJSONObject.toMap()),
					objectActionJSONObject.getBoolean("system"));
			}
		}
	}

	private void _addOrUpdateObjectEntries(
			ServiceContext serviceContext,
			SiteNavigationMenuItemSettingsBuilder
				siteNavigationMenuItemSettingsBuilder,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/object-entries");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		Set<String> sortedResourcePaths = new TreeSet<>(
			new NaturalOrderStringComparator());

		sortedResourcePaths.addAll(resourcePaths);

		resourcePaths = sortedResourcePaths;

		for (String resourcePath : resourcePaths) {
			String json = SiteInitializerUtil.read(
				resourcePath, _servletContext);

			if (json == null) {
				continue;
			}

			json = _replace(json, stringUtilReplaceValues);

			JSONObject jsonObject = _jsonFactory.createJSONObject(json);

			com.liferay.object.model.ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					serviceContext.getCompanyId(),
					"C_" + jsonObject.getString("objectDefinitionName"));

			if (objectDefinition == null) {
				continue;
			}

			JSONArray jsonArray = jsonObject.getJSONArray("object-entries");

			if (JSONUtil.isEmpty(jsonArray)) {
				continue;
			}

			DefaultDTOConverterContext defaultDTOConverterContext =
				new DefaultDTOConverterContext(
					false, null, null, null, null, LocaleUtil.getSiteDefault(),
					null, serviceContext.fetchUser());

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject objectEntryJSONObject = jsonArray.getJSONObject(i);

				String externalReferenceCode = objectEntryJSONObject.getString(
					"externalReferenceCode");

				ObjectEntry objectEntry = ObjectEntry.toDTO(
					JSONUtil.toString(objectEntryJSONObject));

				objectEntry = _objectEntryManager.updateObjectEntry(
					serviceContext.getCompanyId(), defaultDTOConverterContext,
					externalReferenceCode, objectDefinition, objectEntry,
					String.valueOf(serviceContext.getScopeGroupId()));

				if (Validator.isNotNull(externalReferenceCode)) {
					stringUtilReplaceValues.put(
						StringBundler.concat(
							objectDefinition.getShortName(), "#",
							externalReferenceCode),
						String.valueOf(objectEntry.getId()));
				}

				String objectEntrySiteInitializerKey =
					objectEntryJSONObject.getString(
						"objectEntrySiteInitializerKey");

				if (Validator.isNull(objectEntrySiteInitializerKey)) {
					continue;
				}

				com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
					_objectEntryLocalService.getObjectEntry(
						objectEntry.getId());

				siteNavigationMenuItemSettingsBuilder.put(
					objectEntrySiteInitializerKey,
					new SiteNavigationMenuItemSetting() {
						{
							className =
								serviceBuilderObjectEntry.getModelClassName();
							classPK = String.valueOf(
								serviceBuilderObjectEntry.getObjectEntryId());
							title = StringBundler.concat(
								objectDefinition.getName(), StringPool.SPACE,
								serviceBuilderObjectEntry.getObjectEntryId());
						}
					});
			}
		}
	}

	private void _addOrUpdateObjectFields(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/object-fields");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		ObjectFieldResource.Builder objectFieldResourceBuilder =
			_objectFieldResourceFactory.create();

		ObjectFieldResource objectFieldResource =
			objectFieldResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		for (String resourcePath : resourcePaths) {
			String json = SiteInitializerUtil.read(
				resourcePath, _servletContext);

			json = _replace(json, stringUtilReplaceValues);

			JSONObject jsonObject = _jsonFactory.createJSONObject(json);

			JSONArray jsonArray = jsonObject.getJSONArray("object-fields");

			if (JSONUtil.isEmpty(jsonArray)) {
				continue;
			}

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject objectFieldJSONObject = jsonArray.getJSONObject(i);

				ObjectField objectField = ObjectField.toDTO(
					JSONUtil.toString(objectFieldJSONObject));

				if (objectField == null) {
					_log.error(
						"Unable to transform object field from JSON: " + json);

					continue;
				}

				com.liferay.object.model.ObjectField existingObjectField =
					_objectFieldLocalService.fetchObjectField(
						jsonObject.getLong("objectDefinitionId"),
						objectField.getName());

				if (existingObjectField == null) {
					objectFieldResource.postObjectDefinitionObjectField(
						jsonObject.getLong("objectDefinitionId"), objectField);
				}
				else {
					objectFieldResource.putObjectField(
						existingObjectField.getObjectFieldId(), objectField);
				}
			}
		}
	}

	private void _addOrUpdateObjectFolders(ServiceContext serviceContext)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/object-folders");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		ObjectFolderResource.Builder objectFoldResourceBuilder =
			_objectFolderResourceFactory.create();

		ObjectFolderResource objectFolderResource =
			objectFoldResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		for (String resourcePath : resourcePaths) {
			String json = SiteInitializerUtil.read(
				resourcePath, _servletContext);

			ObjectFolder objectFolder = ObjectFolder.toDTO(json);

			if (objectFolder == null) {
				_log.error(
					"Unable to transform object folder from JSON: " + json);

				continue;
			}

			objectFolderResource.putObjectFolderByExternalReferenceCode(
				objectFolder.getExternalReferenceCode(), objectFolder);
		}
	}

	private void _addOrUpdateObjectRelationships(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/object-relationships");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		ObjectRelationshipResource.Builder objectRelationshipResourceBuilder =
			_objectRelationshipResourceFactory.create();

		ObjectRelationshipResource objectRelationshipResource =
			objectRelationshipResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		for (String resourcePath : resourcePaths) {
			String json = SiteInitializerUtil.read(
				resourcePath, _servletContext);

			json = _replace(json, stringUtilReplaceValues);

			ObjectRelationship objectRelationship = ObjectRelationship.toDTO(
				json);

			if (objectRelationship == null) {
				_log.error(
					"Unable to transform object relationship from JSON: " +
						json);

				continue;
			}

			com.liferay.object.model.ObjectRelationship
				existingObjectRelationship =
					_objectRelationshipLocalService.
						fetchObjectRelationshipByObjectDefinitionId1(
							objectRelationship.getObjectDefinitionId1(),
							objectRelationship.getName());

			if (existingObjectRelationship == null) {
				objectRelationshipResource.
					postObjectDefinitionObjectRelationship(
						objectRelationship.getObjectDefinitionId1(),
						objectRelationship);
			}
			else {
				objectRelationshipResource.putObjectRelationship(
					existingObjectRelationship.getObjectRelationshipId(),
					objectRelationship);
			}
		}
	}

	private void _addOrUpdateOrganization(
			JSONObject jsonObject, Organization parentOrganization,
			ServiceContext serviceContext)
		throws Exception {

		Organization organization = Organization.toDTO(jsonObject.toString());

		if (organization == null) {
			_log.error(
				"Unable to transform organization from JSON: " + jsonObject);

			return;
		}

		organization.setParentOrganization(() -> parentOrganization);

		OrganizationResource.Builder organizationResourceBuilder =
			_organizationResourceFactory.create();

		OrganizationResource organizationResource =
			organizationResourceBuilder.user(
				serviceContext.fetchUser()
			).httpServletRequest(
				serviceContext.getRequest()
			).build();

		organization =
			organizationResource.putOrganizationByExternalReferenceCode(
				organization.getExternalReferenceCode(), organization);

		JSONArray jsonArray = jsonObject.getJSONArray("childOrganizations");

		if (JSONUtil.isEmpty(jsonArray)) {
			return;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			_addOrUpdateOrganization(
				jsonArray.getJSONObject(i), organization, serviceContext);
		}
	}

	private void _addOrUpdateOrganizations(ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/organizations.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			_addOrUpdateOrganization(
				jsonArray.getJSONObject(i), null, serviceContext);
		}
	}

	private void _addOrUpdateResourcePermissions(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/resource-permissions.json", _servletContext);

		if (json == null) {
			return;
		}

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntries(
				serviceContext.getScopeGroupId());

		for (LayoutPageTemplateEntry layoutPageTemplateEntry :
				layoutPageTemplateEntries) {

			stringUtilReplaceValues.put(
				"LAYOUT_PAGE_TEMPLATE_ENTRY_ID:" +
					layoutPageTemplateEntry.getName(),
				String.valueOf(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			_replace(json, stringUtilReplaceValues));

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String[] actionIds = ArrayUtil.toStringArray(
				jsonObject.getJSONArray("actionIds"));
			String[] resourceActionIds = TransformUtil.transformToArray(
				_resourceActionLocalService.getResourceActions(
					jsonObject.getString("resourceName")),
				ResourceAction -> ResourceAction.getActionId(), String.class);

			if (!ArrayUtil.containsAll(resourceActionIds, actionIds)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"No resource action found with name ",
							jsonObject.getString("resourceName"),
							" and action IDs ",
							ArrayUtil.toString(actionIds, "")));
				}

				continue;
			}

			Role role = _roleLocalService.fetchRole(
				serviceContext.getCompanyId(),
				jsonObject.getString("roleName"));

			if (role == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No role found with name " +
							jsonObject.getString("roleName"));
				}

				continue;
			}

			int scope = jsonObject.getInt("scope");

			if (scope == ResourceConstants.SCOPE_COMPANY) {
				jsonObject.put(
					"primKey", String.valueOf(serviceContext.getCompanyId()));
			}
			else if (scope == ResourceConstants.SCOPE_GROUP) {
				jsonObject.put(
					"primKey",
					String.valueOf(serviceContext.getScopeGroupId()));
			}

			_resourcePermissionLocalService.setResourcePermissions(
				serviceContext.getCompanyId(),
				jsonObject.getString("resourceName"), scope,
				jsonObject.getString("primKey"), role.getRoleId(), actionIds);
		}
	}

	private void _addOrUpdateRoles(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		List<Role> roles = _roleLocalService.getRoles(
			serviceContext.getCompanyId());

		for (Role role : roles) {
			stringUtilReplaceValues.put(
				"ROLE_ID:" + role.getName(), String.valueOf(role.getRoleId()));
		}

		String json = SiteInitializerUtil.read(
			"/site-initializer/roles.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Role role = _roleLocalService.fetchRole(
				serviceContext.getCompanyId(), jsonObject.getString("name"));

			if (role == null) {
				if (jsonObject.getInt("type") == RoleConstants.TYPE_ACCOUNT) {
					com.liferay.account.model.AccountRole accountRole =
						_accountRoleLocalService.addAccountRole(
							null, serviceContext.getUserId(),
							AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
							jsonObject.getString("name"),
							SiteInitializerUtil.toMap(
								jsonObject.getString("name_i18n")),
							SiteInitializerUtil.toMap(
								jsonObject.getString("description")));

					role = accountRole.getRole();
				}
				else {
					role = _roleLocalService.addRole(
						null, serviceContext.getUserId(), null, 0,
						jsonObject.getString("name"),
						SiteInitializerUtil.toMap(
							jsonObject.getString("name_i18n")),
						SiteInitializerUtil.toMap(
							jsonObject.getString("description")),
						jsonObject.getInt("type"),
						jsonObject.getString("subtype"), serviceContext);
				}
			}
			else {
				role = _roleLocalService.updateRole(
					jsonObject.getString("externalReferenceCode"),
					role.getRoleId(), jsonObject.getString("name"),
					SiteInitializerUtil.toMap(
						jsonObject.getString("name_i18n")),
					SiteInitializerUtil.toMap(
						jsonObject.getString("description")),
					jsonObject.getString("subtype"), serviceContext);
			}

			stringUtilReplaceValues.put(
				"ROLE_ID:" + role.getName(), String.valueOf(role.getRoleId()));

			JSONArray actionsJSONArray = jsonObject.getJSONArray("actions");

			if (JSONUtil.isEmpty(actionsJSONArray) || (role == null)) {
				continue;
			}

			for (int j = 0; j < actionsJSONArray.length(); j++) {
				JSONObject actionsJSONObject = actionsJSONArray.getJSONObject(
					j);

				String resource = actionsJSONObject.getString("resource");
				int scope = actionsJSONObject.getInt("scope");
				String actionId = actionsJSONObject.getString("actionId");

				if (scope == ResourceConstants.SCOPE_COMPANY) {
					_resourcePermissionLocalService.addResourcePermission(
						serviceContext.getCompanyId(), resource, scope,
						String.valueOf(role.getCompanyId()), role.getRoleId(),
						actionId);
				}
				else if (scope == ResourceConstants.SCOPE_GROUP) {
					_resourcePermissionLocalService.removeResourcePermissions(
						serviceContext.getCompanyId(), resource,
						ResourceConstants.SCOPE_GROUP, role.getRoleId(),
						actionId);

					_resourcePermissionLocalService.addResourcePermission(
						serviceContext.getCompanyId(), resource,
						ResourceConstants.SCOPE_GROUP,
						String.valueOf(serviceContext.getScopeGroupId()),
						role.getRoleId(), actionId);
				}
				else if (scope == ResourceConstants.SCOPE_GROUP_TEMPLATE) {
					_resourcePermissionLocalService.addResourcePermission(
						serviceContext.getCompanyId(), resource,
						ResourceConstants.SCOPE_GROUP_TEMPLATE,
						String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
						role.getRoleId(), actionId);
				}
			}
		}
	}

	private void _addOrUpdateSAPEntries(ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/sap-entries.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
				serviceContext.getCompanyId(), jsonObject.getString("name"));

			if (sapEntry == null) {
				_sapEntryLocalService.addSAPEntry(
					serviceContext.getUserId(),
					StringUtil.merge(
						JSONUtil.toStringArray(
							jsonObject.getJSONArray(
								"allowedServiceSignatures")),
						StringPool.NEW_LINE),
					jsonObject.getBoolean("defaultSAPEntry", true),
					jsonObject.getBoolean("enabled", true),
					jsonObject.getString("name"),
					SiteInitializerUtil.toMap(
						jsonObject.getString("title_i18n")),
					serviceContext);
			}
			else {
				_sapEntryLocalService.updateSAPEntry(
					sapEntry.getSapEntryId(),
					StringUtil.merge(
						JSONUtil.toStringArray(
							jsonObject.getJSONArray(
								"allowedServiceSignatures")),
						StringPool.NEW_LINE),
					jsonObject.getBoolean("defaultSAPEntry", true),
					jsonObject.getBoolean("enabled", true),
					jsonObject.getString("name"),
					SiteInitializerUtil.toMap(
						jsonObject.getString("title_i18n")),
					serviceContext);
			}
		}
	}

	private void _addOrUpdateSegmentsEntries(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/segments-entries.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			_replace(json, stringUtilReplaceValues));

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			SegmentsEntry segmentsEntry =
				_segmentsEntryLocalService.fetchSegmentsEntry(
					serviceContext.getScopeGroupId(),
					jsonObject.getString("segmentsEntryKey"));

			if (segmentsEntry == null) {
				segmentsEntry = _segmentsEntryLocalService.addSegmentsEntry(
					jsonObject.getString("segmentsEntryKey"),
					SiteInitializerUtil.toMap(
						jsonObject.getString("name_i18n")),
					null, jsonObject.getBoolean("active", true),
					jsonObject.get(
						"criteria"
					).toString(),
					serviceContext);
			}
			else {
				segmentsEntry = _segmentsEntryLocalService.updateSegmentsEntry(
					segmentsEntry.getSegmentsEntryId(),
					jsonObject.getString("segmentsEntryKey"),
					SiteInitializerUtil.toMap(
						jsonObject.getString("name_i18n")),
					null, jsonObject.getBoolean("active", true),
					jsonObject.get(
						"criteria"
					).toString(),
					serviceContext);
			}

			stringUtilReplaceValues.put(
				"SEGMENTS_ENTRY_ID:" + segmentsEntry.getSegmentsEntryKey(),
				String.valueOf(segmentsEntry.getSegmentsEntryId()));
		}
	}

	private void _addOrUpdateSiteNavigationMenu(
			JSONObject jsonObject, ServiceContext serviceContext,
			Map<String, SiteNavigationMenuItemSetting>
				siteNavigationMenuItemSettings,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					jsonObject.getString("externalReferenceCode"),
					serviceContext.getScopeGroupId());

		if (siteNavigationMenu == null) {
			siteNavigationMenu =
				_siteNavigationMenuLocalService.addSiteNavigationMenu(
					jsonObject.getString("externalReferenceCode"),
					serviceContext.getUserId(),
					serviceContext.getScopeGroupId(),
					jsonObject.getString("name"), jsonObject.getInt("typeSite"),
					serviceContext);
		}
		else {
			_siteNavigationMenuLocalService.updateSiteNavigationMenu(
				serviceContext.getUserId(),
				siteNavigationMenu.getSiteNavigationMenuId(),
				serviceContext.getScopeGroupId(), jsonObject.getString("name"),
				jsonObject.getInt("typeSite"), jsonObject.getBoolean("auto"));
		}

		_addOrUpdateSiteNavigationMenuItems(
			jsonObject, siteNavigationMenu, 0, serviceContext,
			siteNavigationMenuItemSettings, stringUtilReplaceValues);
	}

	private void _addOrUpdateSiteNavigationMenuItems(
			JSONObject jsonObject, SiteNavigationMenu siteNavigationMenu,
			long parentSiteNavigationMenuItemId, ServiceContext serviceContext,
			Map<String, SiteNavigationMenuItemSetting>
				siteNavigationMenuItemSettings,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		for (Object object :
				JSONUtil.toObjectArray(jsonObject.getJSONArray("menuItems"))) {

			JSONObject menuItemJSONObject = (JSONObject)object;

			String type = menuItemJSONObject.getString("type");

			String typeSettings = null;

			if (type.equals(SiteNavigationMenuItemTypeConstants.LAYOUT)) {
				Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
					serviceContext.getScopeGroupId(),
					menuItemJSONObject.getBoolean("privateLayout"),
					menuItemJSONObject.getString("friendlyURL"));

				if (layout == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"No layout found with friendly URL " +
								menuItemJSONObject.getString("friendlyURL"));
					}

					continue;
				}

				SiteNavigationMenuItemType siteNavigationMenuItemType =
					_siteNavigationMenuItemTypeRegistry.
						getSiteNavigationMenuItemType(
							SiteNavigationMenuItemTypeConstants.LAYOUT);

				typeSettings =
					siteNavigationMenuItemType.getTypeSettingsFromLayout(
						layout);

				boolean useCustomName = menuItemJSONObject.getBoolean(
					"useCustomName");

				if (useCustomName) {
					UnicodePropertiesBuilder.UnicodePropertiesWrapper
						unicodePropertiesWrapper =
							_getNavigationMenuItemUnicodePropertiesWrapper(
								menuItemJSONObject);

					if (unicodePropertiesWrapper != null) {
						typeSettings = unicodePropertiesWrapper.load(
							typeSettings
						).put(
							"useCustomName", useCustomName
						).buildString();
					}
				}
			}
			else if (type.equals(SiteNavigationMenuItemTypeConstants.NODE)) {
				UnicodePropertiesBuilder.UnicodePropertiesWrapper
					unicodePropertiesWrapper =
						_getNavigationMenuItemUnicodePropertiesWrapper(
							menuItemJSONObject);

				if (unicodePropertiesWrapper == null) {
					continue;
				}

				typeSettings = unicodePropertiesWrapper.put(
					"title", menuItemJSONObject.getString("title")
				).buildString();
			}
			else if (type.equals(SiteNavigationMenuItemTypeConstants.URL)) {
				UnicodePropertiesBuilder.UnicodePropertiesWrapper
					unicodePropertiesWrapper =
						_getNavigationMenuItemUnicodePropertiesWrapper(
							menuItemJSONObject);

				if (unicodePropertiesWrapper == null) {
					continue;
				}

				typeSettings = unicodePropertiesWrapper.put(
					"url", menuItemJSONObject.getString("url")
				).put(
					"useNewTab", menuItemJSONObject.getString("useNewTab")
				).buildString();
			}
			else if (type.equals("display-page")) {
				String key = menuItemJSONObject.getString("key");

				if (Validator.isNull(key)) {
					continue;
				}

				SiteNavigationMenuItemSetting siteNavigationMenuItemSetting =
					siteNavigationMenuItemSettings.get(key);

				if (siteNavigationMenuItemSetting == null) {
					continue;
				}

				type = siteNavigationMenuItemSetting.className;

				typeSettings = UnicodePropertiesBuilder.create(
					true
				).put(
					"className", siteNavigationMenuItemSetting.className
				).put(
					"classNameId",
					String.valueOf(
						_portal.getClassNameId(
							siteNavigationMenuItemSetting.className))
				).put(
					"classPK",
					String.valueOf(siteNavigationMenuItemSetting.classPK)
				).put(
					"classTypeId", siteNavigationMenuItemSetting.classTypeId
				).put(
					"title", siteNavigationMenuItemSetting.title
				).put(
					"type", siteNavigationMenuItemSetting.type
				).buildString();
			}

			SiteNavigationMenuItem siteNavigationMenuItem =
				_siteNavigationMenuItemLocalService.
					addOrUpdateSiteNavigationMenuItem(
						menuItemJSONObject.getString("externalReferenceCode"),
						serviceContext.getUserId(),
						serviceContext.getScopeGroupId(),
						siteNavigationMenu.getSiteNavigationMenuId(),
						parentSiteNavigationMenuItemId, type, typeSettings,
						serviceContext);

			stringUtilReplaceValues.put(
				"SITE_NAVIGATION_MENU_ITEM_ID:" +
					siteNavigationMenuItem.getExternalReferenceCode(),
				String.valueOf(
					siteNavigationMenuItem.getSiteNavigationMenuItemId()));

			_addOrUpdateSiteNavigationMenuItems(
				menuItemJSONObject, siteNavigationMenu,
				siteNavigationMenuItem.getSiteNavigationMenuItemId(),
				serviceContext, siteNavigationMenuItemSettings,
				stringUtilReplaceValues);
		}
	}

	private void _addOrUpdateSiteNavigationMenus(
			ServiceContext serviceContext,
			Map<String, SiteNavigationMenuItemSetting>
				siteNavigationMenuItemSettings,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/site-navigation-menus.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			_replace(
				SiteInitializerUtil.replace(json, serviceContext),
				stringUtilReplaceValues));

		for (int i = 0; i < jsonArray.length(); i++) {
			_addOrUpdateSiteNavigationMenu(
				jsonArray.getJSONObject(i), serviceContext,
				siteNavigationMenuItemSettings, stringUtilReplaceValues);
		}
	}

	private Long _addOrUpdateStructuredContentFolders(
			Long documentFolderId, String parentResourcePath,
			ServiceContext serviceContext)
		throws Exception {

		StructuredContentFolderResource.Builder
			structuredContentFolderResourceBuilder =
				_structuredContentFolderResourceFactory.create();

		StructuredContentFolderResource structuredContentFolderResource =
			structuredContentFolderResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		String json = SiteInitializerUtil.read(
			parentResourcePath + ".metadata.json", _servletContext);

		if (json == null) {
			json = JSONUtil.put(
				"name", FileUtil.getShortFileName(parentResourcePath)
			).toString();
		}

		StructuredContentFolder structuredContentFolder =
			StructuredContentFolder.toDTO(json);

		structuredContentFolder.setParentStructuredContentFolderId(
			() -> documentFolderId);

		structuredContentFolder =
			structuredContentFolderResource.
				putSiteStructuredContentFolderByExternalReferenceCode(
					serviceContext.getScopeGroupId(),
					structuredContentFolder.getExternalReferenceCode(),
					structuredContentFolder);

		return structuredContentFolder.getId();
	}

	private void _addOrUpdateSXPBlueprint(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		OSBSiteInitializer osbSiteInitializer =
			_osbSiteInitializerSnapshot.get();

		if (osbSiteInitializer == null) {
			return;
		}

		osbSiteInitializer.addOrUpdateSXPBlueprint(
			_getClassNameIdStringUtilReplaceValues(),
			_releaseInfoStringUtilReplaceValues, serviceContext,
			_servletContext, stringUtilReplaceValues);
	}

	private TaxonomyCategory _addOrUpdateTaxonomyCategoryTaxonomyCategory(
			String parentTaxonomyCategoryId, ServiceContext serviceContext,
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		TaxonomyCategoryResource.Builder taxonomyCategoryResourceBuilder =
			_taxonomyCategoryResourceFactory.create();

		TaxonomyCategoryResource taxonomyCategoryResource =
			taxonomyCategoryResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		Page<TaxonomyCategory> taxonomyCategoryPage =
			taxonomyCategoryResource.getTaxonomyCategoryTaxonomyCategoriesPage(
				parentTaxonomyCategoryId, "", null,
				taxonomyCategoryResource.toFilter(
					StringBundler.concat(
						"name eq '", taxonomyCategory.getName(), "'")),
				null, null);

		TaxonomyCategory existingTaxonomyCategory =
			taxonomyCategoryPage.fetchFirstItem();

		if (existingTaxonomyCategory == null) {
			taxonomyCategory =
				taxonomyCategoryResource.postTaxonomyCategoryTaxonomyCategory(
					parentTaxonomyCategoryId, taxonomyCategory);
		}
		else {
			taxonomyCategory = taxonomyCategoryResource.patchTaxonomyCategory(
				existingTaxonomyCategory.getId(), taxonomyCategory);
		}

		return taxonomyCategory;
	}

	private void _addOrUpdateTaxonomyVocabularies(
			long groupId, String parentResourcePath,
			ServiceContext serviceContext,
			SiteNavigationMenuItemSettingsBuilder
				siteNavigationMenuItemSettingsBuilder,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			parentResourcePath);

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		TaxonomyVocabularyResource.Builder taxonomyVocabularyResourceBuilder =
			_taxonomyVocabularyResourceFactory.create();

		TaxonomyVocabularyResource taxonomyVocabularyResource =
			taxonomyVocabularyResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		for (String resourcePath : resourcePaths) {
			if (resourcePath.endsWith("/")) {
				continue;
			}

			String json = SiteInitializerUtil.read(
				resourcePath, _servletContext);

			TaxonomyVocabulary taxonomyVocabulary = TaxonomyVocabulary.toDTO(
				SiteInitializerUtil.replace(json, serviceContext));

			if (taxonomyVocabulary == null) {
				_log.error(
					"Unable to transform taxonomy vocabulary from JSON: " +
						json);

				continue;
			}

			Page<TaxonomyVocabulary> taxonomyVocabularyPage =
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					groupId, "", null,
					taxonomyVocabularyResource.toFilter(
						StringBundler.concat(
							"name eq '", taxonomyVocabulary.getName(), "'")),
					null, null);

			TaxonomyVocabulary existingTaxonomyVocabulary =
				taxonomyVocabularyPage.fetchFirstItem();

			if (existingTaxonomyVocabulary == null) {
				taxonomyVocabulary =
					taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
						groupId, taxonomyVocabulary);
			}
			else {
				taxonomyVocabulary =
					taxonomyVocabularyResource.patchTaxonomyVocabulary(
						existingTaxonomyVocabulary.getId(), taxonomyVocabulary);
			}

			stringUtilReplaceValues.put(
				"TAXONOMY_VOCABULARY_ID:" + taxonomyVocabulary.getName(),
				String.valueOf(taxonomyVocabulary.getId()));

			_addTaxonomyCategories(
				StringUtil.replaceLast(resourcePath, ".json", "/"), null,
				serviceContext, siteNavigationMenuItemSettingsBuilder,
				stringUtilReplaceValues, taxonomyVocabulary.getId());
		}
	}

	private void _addOrUpdateTaxonomyVocabularies(
			ServiceContext serviceContext,
			SiteNavigationMenuItemSettingsBuilder
				siteNavigationMenuItemSettingsBuilder,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Group group = _groupLocalService.getCompanyGroup(
			serviceContext.getCompanyId());

		_addOrUpdateTaxonomyVocabularies(
			group.getGroupId(),
			"/site-initializer/taxonomy-vocabularies/company", serviceContext,
			siteNavigationMenuItemSettingsBuilder, stringUtilReplaceValues);

		_addOrUpdateTaxonomyVocabularies(
			serviceContext.getScopeGroupId(),
			"/site-initializer/taxonomy-vocabularies/group", serviceContext,
			siteNavigationMenuItemSettingsBuilder, stringUtilReplaceValues);
	}

	private TaxonomyCategory _addOrUpdateTaxonomyVocabularyTaxonomyCategory(
			ServiceContext serviceContext, TaxonomyCategory taxonomyCategory,
			long vocabularyId)
		throws Exception {

		TaxonomyCategoryResource.Builder taxonomyCategoryResourceBuilder =
			_taxonomyCategoryResourceFactory.create();

		TaxonomyCategoryResource taxonomyCategoryResource =
			taxonomyCategoryResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		Page<TaxonomyCategory> taxonomyCategoryPage =
			taxonomyCategoryResource.
				getTaxonomyVocabularyTaxonomyCategoriesPage(
					vocabularyId, null, "", null,
					taxonomyCategoryResource.toFilter(
						StringBundler.concat(
							"name eq '", taxonomyCategory.getName(), "'")),
					null, null);

		TaxonomyCategory existingTaxonomyCategory =
			taxonomyCategoryPage.fetchFirstItem();

		if (existingTaxonomyCategory == null) {
			taxonomyCategory =
				taxonomyCategoryResource.postTaxonomyVocabularyTaxonomyCategory(
					vocabularyId, taxonomyCategory);
		}
		else {
			taxonomyCategory = taxonomyCategoryResource.patchTaxonomyCategory(
				existingTaxonomyCategory.getId(), taxonomyCategory);
		}

		return taxonomyCategory;
	}

	private void _addOrUpdateUserGroups(ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/user-groups.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			UserGroup userGroup = _userGroupLocalService.addOrUpdateUserGroup(
				jsonObject.getString("externalReferenceCode"),
				serviceContext.getUserId(), serviceContext.getCompanyId(),
				jsonObject.getString("name"),
				jsonObject.getString("description"), serviceContext);

			_userGroupLocalService.addGroupUserGroup(
				serviceContext.getScopeGroupId(), userGroup);
		}
	}

	private void _addPortletSettings(ServiceContext serviceContext)
		throws Exception {

		CommerceSiteInitializer commerceSiteInitializer =
			_commerceSiteInitializerSnapshot.get();

		if (commerceSiteInitializer == null) {
			return;
		}

		commerceSiteInitializer.addPortletSettings(
			_classLoader, serviceContext, _servletContext);
	}

	private void _addRolesAssignments(ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/roles-assignments.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray groupsJSONArray = jsonObject.getJSONArray("groups");

			if (JSONUtil.isEmpty(groupsJSONArray)) {
				continue;
			}

			List<Long> groupIds = new ArrayList<>();

			for (int j = 0; j < groupsJSONArray.length(); j++) {
				JSONObject groupJSONObject = groupsJSONArray.getJSONObject(j);

				String groupType = groupJSONObject.getString("groupType");

				if (StringUtil.equals(groupType, "Organization")) {
					com.liferay.portal.kernel.model.Organization organization =
						_organizationLocalService.fetchOrganization(
							serviceContext.getCompanyId(),
							groupJSONObject.getString("groupName"));

					if (organization == null) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"No organization found with name " +
									groupJSONObject.getString("groupName"));
						}

						continue;
					}

					Group group = _groupLocalService.getOrganizationGroup(
						serviceContext.getCompanyId(),
						organization.getOrganizationId());

					groupIds.add(group.getGroupId());
				}
				else if (StringUtil.equals(groupType, "Site")) {
					groupIds.add(serviceContext.getScopeGroupId());
				}
				else if (StringUtil.equals(groupType, "User")) {
					User user = _userLocalService.fetchUserByScreenName(
						serviceContext.getCompanyId(),
						groupJSONObject.getString("groupName"));

					if (user == null) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"No user found with screen name " +
									groupJSONObject.getString("groupName"));
						}

						continue;
					}

					Group group = _groupLocalService.getUserGroup(
						serviceContext.getCompanyId(), user.getUserId());

					groupIds.add(group.getGroupId());
				}
				else if (StringUtil.equals(groupType, "UserGroups")) {
					UserGroup userGroup = _userGroupLocalService.fetchUserGroup(
						serviceContext.getCompanyId(),
						groupJSONObject.getString("groupName"));

					if (userGroup == null) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"No user group found with name " +
									groupJSONObject.getString("groupName"));
						}

						continue;
					}

					Group group = _groupLocalService.getUserGroupGroup(
						serviceContext.getCompanyId(),
						userGroup.getUserGroupId());

					groupIds.add(group.getGroupId());
				}
			}

			if (ListUtil.isEmpty(groupIds)) {
				continue;
			}

			Role role = _roleLocalService.fetchRole(
				serviceContext.getCompanyId(),
				jsonObject.getString("roleName"));

			if (role == null) {
				if (_log.isWarnEnabled()) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"No role found with name " +
								jsonObject.getString("roleName"));
					}
				}

				continue;
			}

			_groupLocalService.setRoleGroups(
				role.getRoleId(), ArrayUtil.toLongArray(groupIds));
		}
	}

	private void _addSegmentsExperiences(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> parentResourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/segments-experiences");

		if (SetUtil.isEmpty(parentResourcePaths)) {
			return;
		}

		for (String parentResourcePath : parentResourcePaths) {
			String json = SiteInitializerUtil.read(
				parentResourcePath + "segments-experiences.json",
				_servletContext);

			if (json == null) {
				return;
			}

			json = _replace(json, stringUtilReplaceValues);

			JSONObject jsonObject = _jsonFactory.createJSONObject(json);

			Layout layout = _layoutLocalService.getLayoutByFriendlyURL(
				serviceContext.getScopeGroupId(), false,
				jsonObject.getString("friendlyURL"));

			Layout draftLayout = layout.fetchDraftLayout();

			UnicodeProperties unicodeProperties = new UnicodeProperties(true);

			JSONObject propertiesJSONObject = jsonObject.getJSONObject(
				"typeSettings");

			if (propertiesJSONObject != null) {
				Map<String, String> map = JSONUtil.toStringMap(
					propertiesJSONObject);

				unicodeProperties.putAll(map);
			}

			SegmentsExperience segmentsExperience =
				_segmentsExperienceLocalService.appendSegmentsExperience(
					serviceContext.getUserId(),
					serviceContext.getScopeGroupId(),
					jsonObject.getLong("segmentsEntryId"),
					draftLayout.getPlid(),
					SiteInitializerUtil.toMap(
						jsonObject.getString("name_i18n")),
					jsonObject.getBoolean("active", true), unicodeProperties,
					serviceContext);

			Set<String> resourcePaths = _servletContext.getResourcePaths(
				parentResourcePath);

			for (String resourcePath : resourcePaths) {
				if (resourcePath.endsWith("/")) {
					_addOrUpdateLayoutContent(
						layout, resourcePath,
						segmentsExperience.getSegmentsExperienceId(),
						serviceContext, stringUtilReplaceValues);
				}
			}
		}
	}

	private void _addSiteConfiguration(ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/site-configuration.json", _servletContext);

		if (json == null) {
			return;
		}

		Group group = _groupLocalService.getGroup(
			serviceContext.getScopeGroupId());

		JSONObject jsonObject = _jsonFactory.createJSONObject(json);

		group.setType(jsonObject.getInt("typeSite"));
		group.setManualMembership(jsonObject.getBoolean("manualMembership"));
		group.setMembershipRestriction(
			jsonObject.getInt("membershipRestriction"));

		_groupLocalService.updateGroup(group);

		JSONArray accessToControlMenuRoleNamesJSONArray =
			jsonObject.getJSONArray("accessToControlMenuRoleNames");

		if (accessToControlMenuRoleNamesJSONArray == null) {
			_menuAccessConfigurationManager.updateMenuAccessConfiguration(
				serviceContext.getScopeGroupId(), new String[0],
				jsonObject.getBoolean("showControlMenuByRole"));

			return;
		}

		List<Long> roleIds = new ArrayList<>();

		for (int i = 0; i < accessToControlMenuRoleNamesJSONArray.length();
			 i++) {

			Role role = _roleLocalService.fetchRole(
				serviceContext.getCompanyId(),
				accessToControlMenuRoleNamesJSONArray.getString(i));

			if (role == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No role found with name " +
							accessToControlMenuRoleNamesJSONArray.getString(i));
				}

				continue;
			}

			roleIds.add(role.getRoleId());
		}

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			serviceContext.getScopeGroupId(), ArrayUtil.toStringArray(roleIds),
			jsonObject.getBoolean("showControlMenuByRole"));
	}

	private void _addSiteSettings(ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/site-settings.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			JSONObject propertiesJSONObject = jsonObject.getJSONObject(
				"properties");

			Iterator<String> iterator = propertiesJSONObject.keys();

			while (iterator.hasNext()) {
				String key = iterator.next();

				properties.put(key, propertiesJSONObject.getString(key));
			}

			_configurationProvider.saveGroupConfiguration(
				serviceContext.getScopeGroupId(), jsonObject.getString("pid"),
				properties);
		}
	}

	private void _addStyleBookEntries(ServiceContext serviceContext)
		throws Exception {

		Enumeration<URL> enumeration = _siteBundle.findEntries(
			"/site-initializer/style-books", StringPool.STAR, true);

		if (enumeration == null) {
			return;
		}

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			String fileName = url.getFile();

			if (fileName.endsWith("/")) {
				continue;
			}

			try (InputStream inputStream = url.openStream()) {
				zipWriter.addEntry(
					_removeFirst(fileName, "/site-initializer/style-books/"),
					inputStream);
			}
		}

		_styleBookEntryZipProcessor.importStyleBookEntries(
			serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			zipWriter.getFile(), true);
	}

	private void _addTaxonomyCategories(
			String parentResourcePath, String parentTaxonomyCategoryId,
			ServiceContext serviceContext,
			SiteNavigationMenuItemSettingsBuilder
				siteNavigationMenuItemSettingsBuilder,
			Map<String, String> stringUtilReplaceValues,
			long taxonomyVocabularyId)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			parentResourcePath);

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		for (String resourcePath : resourcePaths) {
			if (resourcePath.endsWith("/")) {
				continue;
			}

			String json = SiteInitializerUtil.read(
				resourcePath, _servletContext);

			json = _replace(
				SiteInitializerUtil.replace(json, serviceContext),
				stringUtilReplaceValues);

			TaxonomyCategory taxonomyCategory = TaxonomyCategory.toDTO(json);

			if (taxonomyCategory == null) {
				_log.error(
					"Unable to transform taxonomy category from JSON: " + json);

				continue;
			}

			if (parentTaxonomyCategoryId == null) {
				taxonomyCategory =
					_addOrUpdateTaxonomyVocabularyTaxonomyCategory(
						serviceContext, taxonomyCategory, taxonomyVocabularyId);
			}
			else {
				taxonomyCategory = _addOrUpdateTaxonomyCategoryTaxonomyCategory(
					parentTaxonomyCategoryId, serviceContext, taxonomyCategory);
			}

			TaxonomyCategory finalTaxonomyCategory = taxonomyCategory;

			String key = resourcePath;

			stringUtilReplaceValues.put(
				"TAXONOMY_CATEGORY_ID:" + key,
				String.valueOf(finalTaxonomyCategory.getId()));

			siteNavigationMenuItemSettingsBuilder.put(
				resourcePath,
				new SiteNavigationMenuItemSetting() {
					{
						className = AssetCategory.class.getName();
						classPK = finalTaxonomyCategory.getId();
						title = finalTaxonomyCategory.getName();
					}
				});

			_addTaxonomyCategories(
				StringUtil.replaceLast(resourcePath, ".json", "/"),
				taxonomyCategory.getId(), serviceContext,
				siteNavigationMenuItemSettingsBuilder, stringUtilReplaceValues,
				taxonomyVocabularyId);
		}
	}

	private void _addUserAccounts(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/user-accounts.json", _servletContext);

		if (json == null) {
			return;
		}

		UserAccountResource.Builder userAccountResourceBuilder =
			_userAccountResourceFactory.create();

		UserAccountResource userAccountResource =
			userAccountResourceBuilder.user(
				serviceContext.fetchUser()
			).httpServletRequest(
				serviceContext.getRequest()
			).build();

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			_replace(json, stringUtilReplaceValues));

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			List<Group> oldGroups = new ArrayList<>();

			long imageId = jsonObject.getLong("imageId");
			long userId = 0;

			UserAccount userAccount = UserAccount.toDTO(
				String.valueOf(jsonObject));

			User user = _userLocalService.fetchUserByEmailAddress(
				serviceContext.getCompanyId(), userAccount.getEmailAddress());

			if (user == null) {
				userAccount =
					userAccountResource.putUserAccountByExternalReferenceCode(
						jsonObject.getString("externalReferenceCode"),
						userAccount);

				userId = userAccount.getId();
			}
			else {
				userId = user.getUserId();

				oldGroups = user.getSiteGroups();
			}

			oldGroups.add(serviceContext.getScopeGroup());

			_userLocalService.updateGroups(
				userId, ListUtil.toLongArray(oldGroups, GroupModel::getGroupId),
				serviceContext);

			if (imageId > 0) {
				FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
					imageId);

				_userLocalService.updatePortrait(
					userId, FileUtil.getBytes(fileEntry.getContentStream()));
			}

			if (jsonObject.has("organizationBriefs")) {
				_addOrganizationUser(
					jsonObject.getJSONArray("organizationBriefs"),
					serviceContext, userId);
			}

			if (jsonObject.has("userGroupBriefs")) {
				_addUserGroupsUser(
					jsonObject.getJSONArray("userGroupBriefs"), serviceContext,
					userId);
			}

			if (jsonObject.has("accountBriefs")) {
				JSONArray accountBriefsJSONArray = jsonObject.getJSONArray(
					"accountBriefs");

				for (int j = 0; j < accountBriefsJSONArray.length(); j++) {
					JSONObject accountBriefsJSONObject =
						accountBriefsJSONArray.getJSONObject(j);

					userAccountResource.
						postAccountUserAccountByExternalReferenceCodeByEmailAddress(
							accountBriefsJSONObject.getString(
								"externalReferenceCode"),
							userAccount.getEmailAddress());

					_associateUserAccounts(
						accountBriefsJSONObject,
						jsonObject.getString("emailAddress"), serviceContext);
				}
			}

			userAccount = userAccountResource.getUserAccountByEmailAddress(
				userAccount.getEmailAddress());

			userAccount.setStatus(() -> UserAccount.Status.INACTIVE);

			userAccountResource.patchUserAccount(
				userAccount.getId(), userAccount);
		}
	}

	private void _addUserGroupsUser(
			JSONArray jsonArray, ServiceContext serviceContext, long userId)
		throws Exception {

		if (JSONUtil.isEmpty(jsonArray)) {
			return;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			UserGroup userGroup = _userGroupLocalService.fetchUserGroup(
				serviceContext.getCompanyId(), jsonObject.getString("name"));

			if (userGroup == null) {
				continue;
			}

			_userLocalService.addUserGroupUser(
				userGroup.getUserGroupId(), userId);
		}
	}

	private void _addUserRoles(ServiceContext serviceContext) throws Exception {
		String json = SiteInitializerUtil.read(
			"/site-initializer/user-roles.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray rolesJSONArray = jsonObject.getJSONArray("roles");

			if (JSONUtil.isEmpty(rolesJSONArray)) {
				continue;
			}

			List<Role> roles = new ArrayList<>();

			for (int j = 0; j < rolesJSONArray.length(); j++) {
				roles.add(
					_roleLocalService.getRole(
						serviceContext.getCompanyId(),
						rolesJSONArray.getString(j)));
			}

			if (ListUtil.isNotEmpty(roles)) {
				User user = _userLocalService.fetchUserByEmailAddress(
					serviceContext.getCompanyId(),
					jsonObject.getString("emailAddress"));

				_roleLocalService.addUserRoles(user.getUserId(), roles);
			}
		}
	}

	private void _addWorkflowDefinitions(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		Set<String> resourcePaths = _servletContext.getResourcePaths(
			"/site-initializer/workflow-definitions");

		if (SetUtil.isEmpty(resourcePaths)) {
			return;
		}

		WorkflowDefinitionResource.Builder workflowDefinitionResourceBuilder =
			_workflowDefinitionResourceFactory.create();

		WorkflowDefinitionResource workflowDefinitionResource =
			workflowDefinitionResourceBuilder.user(
				serviceContext.fetchUser()
			).build();

		for (String resourcePath : resourcePaths) {
			JSONObject workflowDefinitionJSONObject =
				_jsonFactory.createJSONObject(
					SiteInitializerUtil.read(
						resourcePath + "workflow-definition.json",
						_servletContext));

			workflowDefinitionJSONObject.put(
				"content",
				_replace(
					SiteInitializerUtil.read(
						resourcePath + "workflow-definition.xml",
						_servletContext),
					stringUtilReplaceValues));

			WorkflowDefinition workflowDefinition =
				workflowDefinitionResource.postWorkflowDefinitionDeploy(
					WorkflowDefinition.toDTO(
						workflowDefinitionJSONObject.toString()));

			String propertiesJSON = SiteInitializerUtil.read(
				resourcePath + "workflow-definition.properties.json",
				_servletContext);

			if (propertiesJSON == null) {
				continue;
			}

			JSONArray propertiesJSONArray = _jsonFactory.createJSONArray(
				propertiesJSON);

			for (int i = 0; i < propertiesJSONArray.length(); i++) {
				JSONObject propertiesJSONObject =
					propertiesJSONArray.getJSONObject(i);

				long groupId = 0;

				if (StringUtil.equals(
						propertiesJSONObject.getString("scope"), "site")) {

					groupId = serviceContext.getScopeGroupId();
				}

				String className = propertiesJSONObject.getString("className");

				if (StringUtil.equals(
						className,
						com.liferay.object.model.ObjectDefinition.class.
							getName())) {

					com.liferay.object.model.ObjectDefinition objectDefinition =
						_objectDefinitionLocalService.fetchObjectDefinition(
							serviceContext.getCompanyId(),
							propertiesJSONObject.getString("assetName"));

					if (objectDefinition == null) {
						continue;
					}

					className = StringBundler.concat(
						className, "#",
						objectDefinition.getObjectDefinitionId());
				}

				long typePK = 0;

				CommerceSiteInitializer commerceSiteInitializer =
					_commerceSiteInitializerSnapshot.get();

				if ((commerceSiteInitializer != null) &&
					StringUtil.equals(
						className,
						commerceSiteInitializer.getCommerceOrderClassName())) {

					groupId = commerceSiteInitializer.getCommerceChannelGroupId(
						groupId);

					typePK = propertiesJSONObject.getLong("typePK");
				}

				_workflowDefinitionLinkLocalService.
					updateWorkflowDefinitionLink(
						serviceContext.getUserId(),
						serviceContext.getCompanyId(), groupId, className, 0,
						typePK,
						StringBundler.concat(
							workflowDefinition.getName(), "@",
							workflowDefinition.getVersion()));
			}
		}
	}

	private void _associateUserAccounts(
			JSONObject accountBriefsJSONObject, String emailAddress,
			ServiceContext serviceContext)
		throws Exception {

		if (!accountBriefsJSONObject.has("roleBriefs")) {
			return;
		}

		JSONArray jsonArray = accountBriefsJSONObject.getJSONArray(
			"roleBriefs");

		if (JSONUtil.isEmpty(jsonArray)) {
			return;
		}

		AccountRoleResource.Builder builder =
			_accountRoleResourceFactory.create();

		AccountRoleResource accountRoleResource = builder.user(
			serviceContext.fetchUser()
		).build();

		for (int i = 0; i < jsonArray.length(); i++) {
			Page<AccountRole> accountRolePage =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						accountBriefsJSONObject.getString(
							"externalReferenceCode"),
						null,
						accountRoleResource.toFilter(
							StringBundler.concat(
								"name eq '", jsonArray.getString(i), "'")),
						null, null);

			AccountRole accountRole = accountRolePage.fetchFirstItem();

			if (accountRole == null) {
				continue;
			}

			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
					accountBriefsJSONObject.getString("externalReferenceCode"),
					accountRole.getId(), emailAddress);
		}
	}

	private Map<R, List<R>> _createRMap(ServiceContext serviceContext) {
		Map<String, ObjectDefinition> accountEntryRestrictedObjectDefinitions =
			new HashMap<>();
		Map<String, Layout> layoutsMap = new HashMap<>();
		List<Long> objectDefinitionIds = new ArrayList<>();
		SiteNavigationMenuItemSettingsBuilder
			siteNavigationMenuItemSettingsBuilder =
				new SiteNavigationMenuItemSettingsBuilder();
		Map<String, String> stringUtilReplaceValues = new HashMap<>();

		R addAccountGroupAssignmentsR = new R(
			"addAccountGroupAssignments",
			() -> _addAccountGroupAssignments(serviceContext));
		R addAccountGroupsR = new R(
			"addAccountGroups", () -> _addAccountGroups(serviceContext));
		R addAccountsR = new R(
			"addAccounts", () -> _addAccounts(serviceContext));
		R addAccountsOrganizationsR = new R(
			"addAccountsOrganizations",
			() -> _addAccountsOrganizations(serviceContext));
		R addAssetListEntriesR = new R(
			"addAssetListEntries",
			() -> _addAssetListEntries(
				serviceContext, stringUtilReplaceValues));
		R addCPDefinitionsR = new R(
			"addCPDefinitions",
			() -> _addCPDefinitions(serviceContext, stringUtilReplaceValues));
		R addExpandoValuesR = new R(
			"addExpandoValues",
			() -> _addExpandoValues(serviceContext, stringUtilReplaceValues));
		R addFragmentEntriesR = new R(
			"addFragmentEntries",
			() -> _addFragmentEntries(serviceContext, stringUtilReplaceValues));
		R addKeywordsR = new R(
			"addKeywords",
			() -> _addKeywords(serviceContext, stringUtilReplaceValues));
		R addLayoutPageTemplatesR = new R(
			"addLayoutPageTemplates",
			() -> _addLayoutPageTemplates(
				serviceContext, stringUtilReplaceValues));
		R addLayoutUtilityPageEntriesR = new R(
			"addLayoutUtilityPageEntries",
			() -> _addLayoutUtilityPageEntries(
				serviceContext, stringUtilReplaceValues));
		R addObjectDefinitionsR = new R(
			"addObjectDefinitions",
			() -> _addObjectDefinitions(
				accountEntryRestrictedObjectDefinitions, objectDefinitionIds,
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateAccountEntryRestrictionsR = new R(
			"addOrUpdateAccountEntryRestrictions",
			() -> _addOrUpdateAccountEntryRestrictions(
				accountEntryRestrictedObjectDefinitions, serviceContext));
		R addOrUpdateAssetLinkEntriesR = new R(
			"addOrUpdateAssetLinkEntries",
			() -> _addOrUpdateAssetLinkEntries(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateBlogPostingsR = new R(
			"addOrUpdateBlogPostings",
			() -> _addOrUpdateBlogPostings(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateClientExtensionEntriesR = new R(
			"addOrUpdateClientExtensionEntries",
			() -> _addOrUpdateClientExtensionEntries(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateDataDefinitionsR = new R(
			"addOrUpdateDataDefinitions",
			() -> _addOrUpdateDataDefinitions(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateDDMStructuresR = new R(
			"addOrUpdateDDMStructures",
			() -> _addOrUpdateDDMStructures(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateDDMTemplatesR = new R(
			"addOrUpdateDDMTemplates",
			() -> _addOrUpdateDDMTemplates(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateDepotEntriesR = new R(
			"addOrUpdateDepotEntries",
			() -> _addOrUpdateDepotEntries(serviceContext));
		R addOrUpdateDocumentsR = new R(
			"addOrUpdateDocuments",
			() -> _addOrUpdateDocuments(
				serviceContext, siteNavigationMenuItemSettingsBuilder,
				stringUtilReplaceValues));
		R addOrUpdateExpandoColumnsR = new R(
			"addOrUpdateExpandoColumns",
			() -> _addOrUpdateExpandoColumns(serviceContext));
		R addOrUpdateJournalArticlesR = new R(
			"addOrUpdateJournalArticles",
			() -> _addOrUpdateJournalArticles(
				serviceContext, siteNavigationMenuItemSettingsBuilder,
				stringUtilReplaceValues));
		R addOrUpdateKnowledgeBaseArticlesR = new R(
			"addOrUpdateKnowledgeBaseArticles",
			() -> _addOrUpdateKnowledgeBaseArticles(serviceContext));
		R addOrUpdateLayoutsR = new R(
			"addOrUpdateLayouts",
			() -> _addOrUpdateLayouts(
				layoutsMap, serviceContext, stringUtilReplaceValues));
		R addOrUpdateLayoutsContentR = new R(
			"addOrUpdateLayoutsContent",
			() -> _addOrUpdateLayoutsContent(
				layoutsMap, serviceContext,
				siteNavigationMenuItemSettingsBuilder.build(),
				stringUtilReplaceValues));
		R addOrUpdateListTypeDefinitionsR = new R(
			"addOrUpdateListTypeDefinitions",
			() -> _addOrUpdateListTypeDefinitions(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateNotificationTemplatesR = new R(
			"addOrUpdateNotificationTemplates",
			() -> _addOrUpdateNotificationTemplates(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateObjectActionsR = new R(
			"addOrUpdateObjectActions",
			() -> _addOrUpdateObjectActions(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateObjectEntriesR = new R(
			"addOrUpdateObjectEntries",
			() -> _addOrUpdateObjectEntries(
				serviceContext, siteNavigationMenuItemSettingsBuilder,
				stringUtilReplaceValues));
		R addOrUpdateObjectFieldsR = new R(
			"addOrUpdateObjectFields",
			() -> _addOrUpdateObjectFields(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateObjectFoldersR = new R(
			"addOrUpdateObjectFolders",
			() -> _addOrUpdateObjectFolders(serviceContext));
		R addOrUpdateObjectRelationshipsR = new R(
			"addOrUpdateObjectRelationships",
			() -> _addOrUpdateObjectRelationships(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateOrganizationsR = new R(
			"addOrUpdateOrganizations",
			() -> _addOrUpdateOrganizations(serviceContext));
		R addOrUpdateResourcePermissionsR = new R(
			"addOrUpdateResourcePermissions",
			() -> _addOrUpdateResourcePermissions(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateRolesR = new R(
			"addOrUpdateRoles",
			() -> _addOrUpdateRoles(serviceContext, stringUtilReplaceValues));
		R addOrUpdateSAPEntriesR = new R(
			"addOrUpdateSAPEntries",
			() -> _addOrUpdateSAPEntries(serviceContext));
		R addOrUpdateSegmentsEntriesR = new R(
			"addOrUpdateSegmentsEntries",
			() -> _addOrUpdateSegmentsEntries(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateSXPBlueprintR = new R(
			"addOrUpdateSXPBlueprint",
			() -> _addOrUpdateSXPBlueprint(
				serviceContext, stringUtilReplaceValues));
		R addOrUpdateTaxonomyVocabulariesR = new R(
			"addOrUpdateTaxonomyVocabularies",
			() -> _addOrUpdateTaxonomyVocabularies(
				serviceContext, siteNavigationMenuItemSettingsBuilder,
				stringUtilReplaceValues));
		R addOrUpdateUserGroupsR = new R(
			"addOrUpdateUserGroups",
			() -> _addOrUpdateUserGroups(serviceContext));
		R addPortletSettingsR = new R(
			"addPortletSettings", () -> _addPortletSettings(serviceContext));
		R addRolesAssignmentsR = new R(
			"addRolesAssignments", () -> _addRolesAssignments(serviceContext));
		R addSegmentsExperiencesR = new R(
			"addSegmentsExperiences",
			() -> _addSegmentsExperiences(
				serviceContext, stringUtilReplaceValues));
		R addSiteConfigurationR = new R(
			"addSiteConfiguration",
			() -> _addSiteConfiguration(serviceContext));
		R addSiteSettingsR = new R(
			"addSiteSettings", () -> _addSiteSettings(serviceContext));
		R addStyleBookEntriesR = new R(
			"addStyleBookEntries", () -> _addStyleBookEntries(serviceContext));
		R addUserRolesR = new R(
			"addUserRoles", () -> _addUserRoles(serviceContext));
		R addUserAccountsR = new R(
			"addUserAccounts",
			() -> _addUserAccounts(serviceContext, stringUtilReplaceValues));
		R addWorkflowDefinitionsR = new R(
			"addWorkflowDefinitions",
			() -> _addWorkflowDefinitions(
				serviceContext, stringUtilReplaceValues));
		R publishObjectDefinitionsR = new R(
			"publishObjectDefinitions",
			() -> _publishObjectDefinitions(
				objectDefinitionIds, serviceContext));
		R setPLOEntriesR = new R(
			"setPLOEntries", () -> _setPLOEntries(serviceContext));
		R updateLayoutSetsR = new R(
			"updateLayoutSets",
			() -> _updateLayoutSets(serviceContext, stringUtilReplaceValues));

		return HashMapBuilder.<R, List<R>>put(
			addAccountGroupAssignmentsR,
			_dependsOn(addAccountGroupsR, addAccountsR)
		).put(
			addAccountGroupsR, _dependsOn()
		).put(
			addAccountsOrganizationsR,
			_dependsOn(addAccountsR, addOrUpdateOrganizationsR)
		).put(
			addAccountsR, _dependsOn(addOrUpdateExpandoColumnsR)
		).put(
			addAssetListEntriesR,
			_dependsOn(addOrUpdateDDMStructuresR, publishObjectDefinitionsR)
		).put(
			addCPDefinitionsR,
			_dependsOn(addOrUpdateLayoutsR, addOrUpdateObjectEntriesR)
		).put(
			addExpandoValuesR,
			_dependsOn(
				addOrUpdateBlogPostingsR, addOrUpdateJournalArticlesR,
				addOrUpdateKnowledgeBaseArticlesR, addOrUpdateLayoutsContentR,
				addOrUpdateSegmentsEntriesR, addOrUpdateUserGroupsR)
		).put(
			addFragmentEntriesR,
			_dependsOn(addOrUpdateDocumentsR, updateLayoutSetsR)
		).put(
			addKeywordsR, _dependsOn(addOrUpdateDepotEntriesR)
		).put(
			addLayoutPageTemplatesR,
			_dependsOn(
				addOrUpdateBlogPostingsR, addCPDefinitionsR,
				addOrUpdateClientExtensionEntriesR, addOrUpdateJournalArticlesR,
				addOrUpdateSXPBlueprintR)
		).put(
			addLayoutUtilityPageEntriesR,
			_dependsOn(
				addOrUpdateBlogPostingsR, addCPDefinitionsR,
				addOrUpdateClientExtensionEntriesR, addOrUpdateJournalArticlesR,
				addOrUpdateSXPBlueprintR)
		).put(
			addObjectDefinitionsR,
			_dependsOn(
				addOrUpdateListTypeDefinitionsR, addOrUpdateObjectFoldersR,
				addUserAccountsR)
		).put(
			addOrUpdateAccountEntryRestrictionsR,
			_dependsOn(publishObjectDefinitionsR)
		).put(
			addOrUpdateAssetLinkEntriesR,
			_dependsOn(addOrUpdateBlogPostingsR, addOrUpdateDDMStructuresR)
		).put(
			addOrUpdateBlogPostingsR,
			_dependsOn(addKeywordsR, addOrUpdateDocumentsR)
		).put(
			addOrUpdateClientExtensionEntriesR,
			_dependsOn(addOrUpdateDocumentsR)
		).put(
			addOrUpdateDataDefinitionsR, _dependsOn()
		).put(
			addOrUpdateDDMStructuresR, _dependsOn()
		).put(
			addOrUpdateDDMTemplatesR,
			_dependsOn(
				addOrUpdateDataDefinitionsR, addOrUpdateDDMStructuresR,
				publishObjectDefinitionsR)
		).put(
			addOrUpdateDepotEntriesR, _dependsOn()
		).put(
			addOrUpdateDocumentsR,
			_dependsOn(
				addOrUpdateExpandoColumnsR, addOrUpdateTaxonomyVocabulariesR)
		).put(
			addOrUpdateExpandoColumnsR, _dependsOn()
		).put(
			addOrUpdateJournalArticlesR,
			_dependsOn(addOrUpdateDDMTemplatesR, addOrUpdateDocumentsR)
		).put(
			addOrUpdateKnowledgeBaseArticlesR,
			_dependsOn(addOrUpdateExpandoColumnsR)
		).put(
			addOrUpdateLayoutsContentR, _dependsOn(addLayoutPageTemplatesR)
		).put(
			addOrUpdateLayoutsR, _dependsOn(addOrUpdateRolesR)
		).put(
			addOrUpdateListTypeDefinitionsR, _dependsOn()
		).put(
			addOrUpdateNotificationTemplatesR,
			_dependsOn(publishObjectDefinitionsR)
		).put(
			addOrUpdateObjectActionsR,
			_dependsOn(addOrUpdateAccountEntryRestrictionsR)
		).put(
			addOrUpdateObjectEntriesR,
			_dependsOn(addOrUpdateObjectActionsR, addOrUpdateDocumentsR)
		).put(
			addOrUpdateObjectFieldsR,
			_dependsOn(addOrUpdateObjectRelationshipsR)
		).put(
			addOrUpdateObjectFoldersR, _dependsOn()
		).put(
			addOrUpdateObjectRelationshipsR, _dependsOn(addObjectDefinitionsR)
		).put(
			addOrUpdateOrganizationsR, _dependsOn(addOrUpdateExpandoColumnsR)
		).put(
			addOrUpdateResourcePermissionsR,
			_dependsOn(addSegmentsExperiencesR, addWorkflowDefinitionsR)
		).put(
			addOrUpdateRolesR, _dependsOn()
		).put(
			addOrUpdateSAPEntriesR, _dependsOn()
		).put(
			addOrUpdateSegmentsEntriesR,
			_dependsOn(addOrUpdateRolesR, addUserAccountsR)
		).put(
			addOrUpdateSXPBlueprintR,
			_dependsOn(addOrUpdateTaxonomyVocabulariesR)
		).put(
			addOrUpdateTaxonomyVocabulariesR,
			_dependsOn(addOrUpdateDDMStructuresR)
		).put(
			addOrUpdateUserGroupsR, _dependsOn()
		).put(
			addPortletSettingsR, _dependsOn(addOrUpdateTaxonomyVocabulariesR)
		).put(
			addRolesAssignmentsR,
			_dependsOn(
				addOrUpdateRolesR, addOrUpdateUserGroupsR, addUserAccountsR)
		).put(
			addSegmentsExperiencesR,
			_dependsOn(addOrUpdateLayoutsContentR, addOrUpdateSegmentsEntriesR)
		).put(
			addSiteConfigurationR, _dependsOn(addOrUpdateRolesR)
		).put(
			addSiteSettingsR, _dependsOn()
		).put(
			addStyleBookEntriesR, _dependsOn()
		).put(
			addUserAccountsR,
			_dependsOn(
				addAccountsR, addKeywordsR, addOrUpdateDocumentsR,
				addOrUpdateOrganizationsR, addOrUpdateTaxonomyVocabulariesR)
		).put(
			addUserRolesR, _dependsOn(addOrUpdateRolesR, addUserAccountsR)
		).put(
			addWorkflowDefinitionsR, _dependsOn(addOrUpdateRolesR)
		).put(
			publishObjectDefinitionsR, _dependsOn(addOrUpdateObjectFieldsR)
		).put(
			setPLOEntriesR, _dependsOn()
		).put(
			updateLayoutSetsR, _dependsOn(addOrUpdateLayoutsR)
		).build();
	}

	private List<R> _dependsOn(R... rArray) {
		return ListUtil.fromArray(rArray);
	}

	private long[] _getAssetCategoryIds(
		long groupId, String[] externalReferenceCodes) {

		List<Long> assetCategoryIds = new ArrayList<>();

		for (String externalReferenceCode : externalReferenceCodes) {
			AssetCategory assetCategory =
				_assetCategoryLocalService.
					fetchAssetCategoryByExternalReferenceCode(
						externalReferenceCode, groupId);

			if (assetCategory != null) {
				assetCategoryIds.add(assetCategory.getCategoryId());
			}
		}

		return ArrayUtil.toLongArray(assetCategoryIds);
	}

	private String _getAssetRendererFactoryName(String assetEntryType) {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				assetEntryType);

		if ((assetRendererFactory == null) ||
			!assetRendererFactory.isSupportsClassTypes()) {

			return StringPool.BLANK;
		}

		Class<?> clazz = assetRendererFactory.getClass();

		if (assetRendererFactory instanceof AssetRendererFactoryWrapper) {
			AssetRendererFactoryWrapper<?> assetRendererFactoryWrapper =
				(AssetRendererFactoryWrapper<?>)assetRendererFactory;

			clazz = assetRendererFactoryWrapper.getWrappedClass();
		}

		String className = clazz.getName();

		int pos = className.lastIndexOf(StringPool.PERIOD);

		return className.substring(pos + 1);
	}

	private Map<String, String> _getClassNameIdStringUtilReplaceValues() {
		Map<String, String> map = new HashMap<>();

		Class<?>[] classes = {
			BlogsEntry.class, com.liferay.calendar.model.Calendar.class,
			DDLRecord.class, DDMStructure.class, DLFileEntry.class,
			DLFolder.class, JournalArticle.class, KBArticle.class,
			MBMessage.class, WikiPage.class
		};

		for (Class<?> clazz : classes) {
			map.put(
				"CLASS_NAME_ID:" + clazz.getName(),
				String.valueOf(_portal.getClassNameId(clazz)));
		}

		return map;
	}

	private int _getDepotEntryType(String assetLibraryTypeString) {
		if (Validator.isNull(assetLibraryTypeString) ||
			StringUtil.equalsIgnoreCase(
				assetLibraryTypeString, "AssetLibrary")) {

			return DepotConstants.TYPE_ASSET_LIBRARY;
		}
		else if (StringUtil.equalsIgnoreCase(assetLibraryTypeString, "Space")) {
			return DepotConstants.TYPE_SPACE;
		}

		throw new IllegalArgumentException(
			"Asset library type " + assetLibraryTypeString +
				" must be \"AssetLibrary\" or \"Space\"");
	}

	private Serializable _getExpandoAttributeValue(JSONObject jsonObject)
		throws Exception {

		if (jsonObject.getInt("dataType") == ExpandoColumnConstants.BOOLEAN) {
			return jsonObject.getBoolean("defaultValue");
		}
		else if (jsonObject.getInt("dataType") == ExpandoColumnConstants.DATE) {
			if (Validator.isNull(jsonObject.getString("defaultValue"))) {
				return null;
			}

			DateFormat dateFormat = DateUtil.getISOFormat(
				jsonObject.getString("defaultValue"));

			return dateFormat.parse(jsonObject.getString("defaultValue"));
		}
		else if (jsonObject.getInt("dataType") ==
					ExpandoColumnConstants.DOUBLE) {

			return jsonObject.getDouble("defaultValue");
		}
		else if (jsonObject.getInt("dataType") ==
					ExpandoColumnConstants.DOUBLE_ARRAY) {

			return JSONUtil.toDoubleArray(
				jsonObject.getJSONArray("defaultValue"));
		}
		else if (jsonObject.getInt("dataType") ==
					ExpandoColumnConstants.FLOAT) {

			return jsonObject.getDouble("defaultValue");
		}
		else if (jsonObject.getInt("dataType") ==
					ExpandoColumnConstants.FLOAT_ARRAY) {

			return JSONUtil.toFloatArray(
				jsonObject.getJSONArray("defaultValue"));
		}
		else if (jsonObject.getInt("dataType") ==
					ExpandoColumnConstants.INTEGER) {

			return jsonObject.getInt("defaultValue");
		}
		else if (jsonObject.getInt("dataType") ==
					ExpandoColumnConstants.INTEGER_ARRAY) {

			return JSONUtil.toIntegerArray(
				jsonObject.getJSONArray("defaultValue"));
		}
		else if (jsonObject.getInt("dataType") == ExpandoColumnConstants.LONG) {
			return jsonObject.getLong("defaultValue");
		}
		else if (jsonObject.getInt("dataType") ==
					ExpandoColumnConstants.LONG_ARRAY) {

			return JSONUtil.toLongArray(
				jsonObject.getJSONArray("defaultValue"));
		}
		else if (jsonObject.getInt("dataType") ==
					ExpandoColumnConstants.NUMBER) {

			return jsonObject.getDouble("defaultValue");
		}
		else if (jsonObject.getInt("dataType") ==
					ExpandoColumnConstants.NUMBER_ARRAY) {

			return JSONUtil.toIntegerArray(
				jsonObject.getJSONArray("defaultValue"));
		}
		else if (jsonObject.getInt("dataType") ==
					ExpandoColumnConstants.STRING) {

			return jsonObject.getString("defaultValue");
		}
		else if (jsonObject.getInt("dataType") ==
					ExpandoColumnConstants.STRING_ARRAY) {

			return JSONUtil.toStringArray(
				jsonObject.getJSONArray("defaultValue"));
		}

		return (Serializable)jsonObject.get("defaultValue");
	}

	private UnicodePropertiesBuilder.UnicodePropertiesWrapper
		_getNavigationMenuItemUnicodePropertiesWrapper(
			JSONObject menuItemJSONObject) {

		JSONObject nameI18nJSONObject = menuItemJSONObject.getJSONObject(
			"name_i18n");

		if (nameI18nJSONObject == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Missing \"name_i18n\" in " + menuItemJSONObject);
			}

			return null;
		}

		UnicodePropertiesBuilder.UnicodePropertiesWrapper
			unicodePropertiesWrapper = UnicodePropertiesBuilder.put(
				"defaultLanguageId",
				LocaleUtil.toLanguageId(LocaleUtil.getDefault()));

		for (String key : nameI18nJSONObject.keySet()) {
			unicodePropertiesWrapper.put(
				"name_" + key, nameI18nJSONObject.getString(key));
		}

		return unicodePropertiesWrapper;
	}

	private Map<String, String> _getReleaseInfoStringUtilReplaceValues() {
		Map<String, String> map = new HashMap<>();

		Object[] entries = {
			"BUILD_DATE", ReleaseInfo.getBuildDate(), "BUILD_NUMBER",
			ReleaseInfo.getBuildNumber(), "CODE_NAME",
			ReleaseInfo.getCodeName(), "NAME", ReleaseInfo.getName(),
			"PARENT_BUILD_NUMBER", ReleaseInfo.getParentBuildNumber(),
			"RELEASE_INFO",
			_replace(
				ReleaseInfo.getReleaseInfo(), StringPool.OPEN_PARENTHESIS,
				"<br>("),
			"SERVER_INFO", ReleaseInfo.getServerInfo(), "VENDOR",
			ReleaseInfo.getVendor(), "VERSION", ReleaseInfo.getVersion(),
			"VERSION_DISPLAY_NAME", ReleaseInfo.getVersionDisplayName()
		};

		for (int i = 0; i < entries.length; i += 2) {
			String entryKey = String.valueOf(entries[i]);
			String entryValue = String.valueOf(entries[i + 1]);

			map.put("RELEASE_INFO:" + entryKey, entryValue);
		}

		return map;
	}

	private String _getThemeId(
		long companyId, String defaultThemeId, String themeName) {

		List<Theme> themes = ListUtil.filter(
			_themeLocalService.getThemes(companyId),
			theme -> Objects.equals(theme.getName(), themeName));

		if (ListUtil.isNotEmpty(themes)) {
			Theme theme = themes.get(0);

			if (Objects.equals(theme.getName(), "Dialect")) {
				_dialectThemeDetected = true;
			}

			return theme.getThemeId();
		}

		return defaultThemeId;
	}

	private void _initialize(long groupId) throws Exception {
		User user = _userLocalService.getUser(PrincipalThreadLocal.getUserId());

		ServiceContext serviceContextThreadLocal =
			ServiceContextThreadLocal.getServiceContext();

		ServiceContext serviceContext =
			(ServiceContext)serviceContextThreadLocal.clone();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCompanyId(user.getCompanyId());

		Company company = _companyLocalService.getCompany(
			serviceContext.getCompanyId());

		serviceContext.setPortalURL(
			company.getPortalURL(serviceContext.getScopeGroupId()));

		serviceContext.setScopeGroupId(groupId);
		serviceContext.setTimeZone(user.getTimeZone());
		serviceContext.setUserId(user.getUserId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		List<R> rList = new ArrayList<>();
		Map<R, List<R>> rMap = _createRMap(serviceContext);

		while (rList.size() != rMap.size()) {
			int size = rList.size();

			for (Map.Entry<R, List<R>> entry : rMap.entrySet()) {
				R r = entry.getKey();

				if (rList.contains(r) || !rList.containsAll(entry.getValue())) {
					continue;
				}

				long startTime = System.currentTimeMillis();

				r._unsafeRunnable.run();

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Invoking ", r._name, " took ",
							System.currentTimeMillis() - startTime, " ms"));
				}

				rList.add(r);
			}

			if (size == rList.size()) {
				throw new InitializationException("Circular dependency");
			}
		}

		_updateGroupSiteInitializerKey(groupId);
	}

	private void _publishObjectDefinitions(
			List<Long> objectDefinitinIds, ServiceContext serviceContext)
		throws Exception {

		if (ListUtil.isEmpty(objectDefinitinIds)) {
			return;
		}

		ObjectDefinitionResource.Builder builder =
			_objectDefinitionResourceFactory.create();

		ObjectDefinitionResource objectDefinitionResource = builder.user(
			serviceContext.fetchUser()
		).build();

		for (Long objectDefinitionId : objectDefinitinIds) {
			objectDefinitionResource.postObjectDefinitionPublish(
				objectDefinitionId);
		}
	}

	private String _removeFirst(String s, String oldSub) {
		int index = s.indexOf(oldSub);

		return s.substring(index + oldSub.length());
	}

	private String _replace(
		String s, Map<String, String> stringUtilReplaceValues) {

		return SiteInitializerUtil.replace(
			_classNameIdStringUtilReplaceValues,
			_releaseInfoStringUtilReplaceValues, s, stringUtilReplaceValues);
	}

	private String _replace(String s, String oldSub, String newSub) {
		return StringUtil.replace(s, oldSub, newSub);
	}

	private void _replaceObjectDefinitionValues(
		String className, String name, long objectDefinitionId,
		Map<String, String> stringUtilReplaceValues) {

		stringUtilReplaceValues.put(
			"OBJECT_DEFINITION_CLASS_NAME:" + name, className);
		stringUtilReplaceValues.put(
			"OBJECT_DEFINITION_ID:" + name, String.valueOf(objectDefinitionId));

		if (!className.contains(StringPool.POUND)) {
			return;
		}

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectDefinitionId);

		stringUtilReplaceValues.put(
			"OBJECT_DEFINITION_PORTLET_ID:" + name,
			serviceBuilderObjectDefinition.getPortletId());
	}

	private void _setDefaultLayoutUtilityPageEntries(
			ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/layout-utility-page-entries" +
				"/default-utility-page-entries.json",
			_servletContext);

		if (json == null) {
			return;
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(json);

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String type = iterator.next();

			String name = jsonObject.getString(type);

			LayoutUtilityPageEntry layoutUtilityPageEntry =
				_layoutUtilityPageEntryLocalService.fetchLayoutUtilityPageEntry(
					serviceContext.getScopeGroupId(), name,
					LayoutUtilityPageEntryTypeConverter.convertToInternalValue(
						type));

			if (layoutUtilityPageEntry != null) {
				_layoutUtilityPageEntryLocalService.
					setDefaultLayoutUtilityPageEntry(
						layoutUtilityPageEntry.getLayoutUtilityPageEntryId());
			}
		}
	}

	private void _setPLOEntries(ServiceContext serviceContext)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/plo-entries.json", _servletContext);

		if (json == null) {
			return;
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			_ploEntryLocalService.setPLOEntries(
				serviceContext.getCompanyId(), serviceContext.getUserId(),
				jsonObject.getString("key"),
				SiteInitializerUtil.toMap(jsonObject.getString("value")));
		}
	}

	private void _setResourcePermissions(
			long companyId, String name, JSONArray permissionsJSONArray,
			String primKey)
		throws Exception {

		if (permissionsJSONArray == null) {
			return;
		}

		for (int i = 0; i < permissionsJSONArray.length(); i++) {
			JSONObject permissionsJSONObject =
				permissionsJSONArray.getJSONObject(i);

			int scope = permissionsJSONObject.getInt("scope");

			String roleName = permissionsJSONObject.getString("roleName");

			Role role = _roleLocalService.getRole(companyId, roleName);

			String[] actionIds = new String[0];

			JSONArray actionIdsJSONArray = permissionsJSONObject.getJSONArray(
				"actionIds");

			if (actionIdsJSONArray != null) {
				for (int j = 0; j < actionIdsJSONArray.length(); j++) {
					actionIds = ArrayUtil.append(
						actionIds, actionIdsJSONArray.getString(j));
				}
			}

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, name, scope, primKey, role.getRoleId(), actionIds);
		}
	}

	private Layout _updateDraftLayout(
			Layout draftLayout, JSONObject settingsJSONObject)
		throws Exception {

		UnicodeProperties unicodeProperties =
			draftLayout.getTypeSettingsProperties();

		Set<Map.Entry<String, String>> set = unicodeProperties.entrySet();

		set.removeIf(
			entry -> StringUtil.startsWith(entry.getKey(), "lfr-theme:"));

		JSONObject themeSettingsJSONObject = settingsJSONObject.getJSONObject(
			"themeSettings");

		if (themeSettingsJSONObject != null) {
			for (String key : themeSettingsJSONObject.keySet()) {
				unicodeProperties.put(
					key, themeSettingsJSONObject.getString(key));
			}

			draftLayout = _layoutLocalService.updateLayout(
				draftLayout.getGroupId(), draftLayout.isPrivateLayout(),
				draftLayout.getLayoutId(), unicodeProperties.toString());

			draftLayout.setTypeSettingsProperties(unicodeProperties);
		}

		draftLayout = _layoutLocalService.updateLookAndFeel(
			draftLayout.getGroupId(), draftLayout.isPrivateLayout(),
			draftLayout.getLayoutId(),
			_getThemeId(
				draftLayout.getCompanyId(), draftLayout.getThemeId(),
				settingsJSONObject.getString("themeName")),
			settingsJSONObject.getString(
				"colorSchemeName", draftLayout.getColorSchemeId()),
			settingsJSONObject.getString("css", draftLayout.getCss()));

		JSONObject masterPageJSONObject = settingsJSONObject.getJSONObject(
			"masterPage");

		if (masterPageJSONObject != null) {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						draftLayout.getGroupId(),
						masterPageJSONObject.getString("key"));

			if (layoutPageTemplateEntry != null) {
				draftLayout = _layoutLocalService.updateMasterLayoutPlid(
					draftLayout.getGroupId(), draftLayout.isPrivateLayout(),
					draftLayout.getLayoutId(),
					layoutPageTemplateEntry.getPlid());
			}
		}

		return draftLayout;
	}

	private void _updateGroupSiteInitializerKey(long groupId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled("LPS-165482")) {
			return;
		}

		Group group = _groupLocalService.getGroup(groupId);

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"siteInitializerKey", getKey());

		_groupLocalService.updateGroup(
			group.getGroupId(), typeSettingsUnicodeProperties.toString());
	}

	private void _updateLayoutSet(
			boolean privateLayout, ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			serviceContext.getScopeGroupId(), privateLayout);

		String resourcePath = "/site-initializer/layout-set";

		if (privateLayout) {
			resourcePath += "/private";
		}
		else {
			resourcePath += "/public";
		}

		String metadataJSON = SiteInitializerUtil.read(
			resourcePath + "/metadata.json", _servletContext);

		JSONObject metadataJSONObject = _jsonFactory.createJSONObject(
			(metadataJSON == null) ? "{}" : metadataJSON);

		String css = _replace(
			SiteInitializerUtil.read(
				resourcePath + "/css.css", _servletContext),
			stringUtilReplaceValues);

		_layoutSetLocalService.updateLookAndFeel(
			serviceContext.getScopeGroupId(), privateLayout,
			_getThemeId(
				serviceContext.getCompanyId(), StringPool.BLANK,
				metadataJSONObject.getString("themeName")),
			layoutSet.getColorSchemeId(), css);

		URL url = _servletContext.getResource(resourcePath + "/logo.png");

		if (url != null) {
			_layoutSetLocalService.updateLogo(
				serviceContext.getScopeGroupId(), privateLayout, true,
				URLUtil.toByteArray(url));
		}

		JSONObject settingsJSONObject = metadataJSONObject.getJSONObject(
			"settings");

		if (settingsJSONObject == null) {
			return;
		}

		String js = SiteInitializerUtil.read(
			resourcePath + "/js.js", _servletContext);

		if (Validator.isNotNull(js)) {
			settingsJSONObject.put("javascript", js);
		}

		UnicodeProperties unicodeProperties = layoutSet.getSettingsProperties();

		for (String key : settingsJSONObject.keySet()) {
			unicodeProperties.put(key, settingsJSONObject.getString(key));
		}

		_layoutSetLocalService.updateSettings(
			serviceContext.getScopeGroupId(), privateLayout,
			unicodeProperties.toString());
	}

	private void _updateLayoutSets(
			ServiceContext serviceContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		_updateLayoutSet(false, serviceContext, stringUtilReplaceValues);
		_updateLayoutSet(true, serviceContext, stringUtilReplaceValues);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BundleSiteInitializer.class);

	private static final Snapshot<CommerceSiteInitializer>
		_commerceSiteInitializerSnapshot = new Snapshot<>(
			BundleSiteInitializer.class, CommerceSiteInitializer.class);
	private static final ThreadLocal<Set<String>> _initializedGroupIdAndKeys =
		new CentralizedThreadLocal<>(
			BundleSiteInitializer.class + "._initializedGroupIdAndKeys",
			HashSet::new);
	private static final ObjectMapper _objectMapper = new ObjectMapper();
	private static final Snapshot<OSBSiteInitializer>
		_osbSiteInitializerSnapshot = new Snapshot<>(
			BundleSiteInitializer.class, OSBSiteInitializer.class);

	private final AccountEntryLocalService _accountEntryLocalService;
	private final AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;
	private final AccountGroupLocalService _accountGroupLocalService;
	private final AccountGroupRelService _accountGroupRelService;
	private final AccountResource.Factory _accountResourceFactory;
	private final AccountRoleLocalService _accountRoleLocalService;
	private final AccountRoleResource.Factory _accountRoleResourceFactory;
	private final ArchivedSettingsFactory _archivedSettingsFactory;
	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final AssetEntryLocalService _assetEntryLocalService;
	private final AssetLinkLocalService _assetLinkLocalService;
	private final AssetListEntryLocalService _assetListEntryLocalService;
	private final BlogPostingResource.Factory _blogPostingResourceFactory;
	private final CETManager _cetManager;
	private final ClassLoader _classLoader;
	private final Map<String, String> _classNameIdStringUtilReplaceValues;
	private final ClientExtensionEntryLocalService
		_clientExtensionEntryLocalService;
	private final CompanyLocalService _companyLocalService;
	private final ConfigurationProvider _configurationProvider;
	private final DataDefinitionResource.Factory _dataDefinitionResourceFactory;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final DDMTemplateLocalService _ddmTemplateLocalService;
	private final DefaultDDMStructureHelper _defaultDDMStructureHelper;
	private final DepotEntryGroupRelLocalService
		_depotEntryGroupRelLocalService;
	private final DepotEntryLocalService _depotEntryLocalService;
	private boolean _dialectThemeDetected;
	private final DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;
	private final DLURLHelper _dlURLHelper;
	private final DocumentFolderResource.Factory _documentFolderResourceFactory;
	private final DocumentResource.Factory _documentResourceFactory;
	private final ExpandoValueLocalService _expandoValueLocalService;
	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final FragmentsImporter _fragmentsImporter;
	private final GroupLocalService _groupLocalService;
	private final JournalArticleLocalService _journalArticleLocalService;
	private final JSONFactory _jsonFactory;
	private final KeywordResource.Factory _keywordResourceFactory;
	private final KnowledgeBaseArticleResource.Factory
		_knowledgeBaseArticleResourceFactory;
	private final KnowledgeBaseFolderResource.Factory
		_knowledgeBaseFolderResourceFactory;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;
	private final LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;
	private final LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;
	private final LayoutSetLocalService _layoutSetLocalService;
	private final LayoutsImporter _layoutsImporter;
	private final LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;
	private final ListTypeDefinitionResource _listTypeDefinitionResource;
	private final ListTypeDefinitionResource.Factory
		_listTypeDefinitionResourceFactory;
	private final ListTypeEntryLocalService _listTypeEntryLocalService;
	private final ListTypeEntryResource _listTypeEntryResource;
	private final ListTypeEntryResource.Factory _listTypeEntryResourceFactory;
	private final MenuAccessConfigurationManager
		_menuAccessConfigurationManager;
	private final NotificationTemplateResource.Factory
		_notificationTemplateResourceFactory;
	private final ObjectActionLocalService _objectActionLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectDefinitionResource.Factory
		_objectDefinitionResourceFactory;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryManager _objectEntryManager;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectFieldResource.Factory _objectFieldResourceFactory;
	private final ObjectFolderResource.Factory _objectFolderResourceFactory;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final ObjectRelationshipResource.Factory
		_objectRelationshipResourceFactory;
	private final OrganizationLocalService _organizationLocalService;
	private final OrganizationResource.Factory _organizationResourceFactory;
	private final PLOEntryLocalService _ploEntryLocalService;
	private final Portal _portal;
	private final Map<String, String> _releaseInfoStringUtilReplaceValues;
	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;
	private final SAPEntryLocalService _sapEntryLocalService;
	private final SegmentsEntryLocalService _segmentsEntryLocalService;
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService;
	private ServletContext _servletContext;
	private final Bundle _siteBundle;
	private final Bundle _siteInitializerExtenderBundle;
	private final SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;
	private final SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;
	private final SiteNavigationMenuLocalService
		_siteNavigationMenuLocalService;
	private final StructuredContentFolderResource.Factory
		_structuredContentFolderResourceFactory;
	private final StyleBookEntryZipProcessor _styleBookEntryZipProcessor;
	private final TaxonomyCategoryResource.Factory
		_taxonomyCategoryResourceFactory;
	private final TaxonomyVocabularyResource.Factory
		_taxonomyVocabularyResourceFactory;
	private final TemplateEntryLocalService _templateEntryLocalService;
	private final ThemeLocalService _themeLocalService;
	private final UserAccountResource.Factory _userAccountResourceFactory;
	private final UserGroupLocalService _userGroupLocalService;
	private final UserLocalService _userLocalService;
	private final WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;
	private final WorkflowDefinitionResource.Factory
		_workflowDefinitionResourceFactory;
	private final ZipWriterFactory _zipWriterFactory;

	private class R {

		public R(String name, UnsafeRunnable<Exception> unsafeRunnable) {
			_name = name;
			_unsafeRunnable = unsafeRunnable;
		}

		private final String _name;
		private final UnsafeRunnable<Exception> _unsafeRunnable;

	}

	private class SiteNavigationMenuItemSetting {

		public String className;
		public String classPK;
		public String classTypeId = StringPool.BLANK;
		public String title;
		public String type = StringPool.BLANK;

	}

	private class SiteNavigationMenuItemSettingsBuilder {

		public Map<String, SiteNavigationMenuItemSetting> build() {
			return _siteNavigationMenuItemSettings;
		}

		public void put(
			String key,
			SiteNavigationMenuItemSetting siteNavigationMenuItemSetting) {

			_siteNavigationMenuItemSettings.put(
				key, siteNavigationMenuItemSetting);
		}

		private Map<String, SiteNavigationMenuItemSetting>
			_siteNavigationMenuItemSettings = new HashMap<>();

	}

}