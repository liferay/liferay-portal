/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.initializer.extender.internal;

import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.fragment.importer.FragmentsImporter;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeEntryResource;
import com.liferay.headless.admin.taxonomy.resource.v1_0.KeywordResource;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.headless.admin.user.resource.v1_0.AccountResource;
import com.liferay.headless.admin.user.resource.v1_0.AccountRoleResource;
import com.liferay.headless.admin.user.resource.v1_0.OrganizationResource;
import com.liferay.headless.admin.user.resource.v1_0.UserAccountResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowDefinitionResource;
import com.liferay.headless.delivery.resource.v1_0.BlogPostingResource;
import com.liferay.headless.delivery.resource.v1_0.DocumentFolderResource;
import com.liferay.headless.delivery.resource.v1_0.DocumentResource;
import com.liferay.headless.delivery.resource.v1_0.KnowledgeBaseArticleResource;
import com.liferay.headless.delivery.resource.v1_0.KnowledgeBaseFolderResource;
import com.liferay.headless.delivery.resource.v1_0.StructuredContentFolderResource;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.notification.rest.resource.v1_0.NotificationTemplateResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFieldResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFolderResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectRelationshipResource;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.settings.ArchivedSettingsFactory;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;
import com.liferay.portal.util.PropsValues;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;
import com.liferay.site.initializer.extender.internal.file.backed.osgi.FileBackedBundleDelegate;
import com.liferay.site.initializer.extender.internal.file.backed.servlet.FileBackedServletContextDelegate;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;
import com.liferay.style.book.zip.processor.StyleBookEntryZipProcessor;
import com.liferay.template.service.TemplateEntryLocalService;

import jakarta.servlet.ServletContext;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.dm.DependencyManager;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = SiteInitializerExtender.class)
public class SiteInitializerExtender
	implements BundleTrackerCustomizer<SiteInitializerExtension> {

	@Override
	public SiteInitializerExtension addingBundle(
		Bundle bundle, BundleEvent bundleEvent) {

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		List<BundleCapability> bundleCapabilities =
			bundleWiring.getCapabilities("liferay.site.initializer");

		if (ListUtil.isEmpty(bundleCapabilities)) {
			return null;
		}

		SiteInitializerExtension siteInitializerExtension =
			new SiteInitializerExtension(
				_accountEntryLocalService,
				_accountEntryOrganizationRelLocalService,
				_accountGroupLocalService, _accountGroupRelService,
				_accountResourceFactory, _accountRoleLocalService,
				_accountRoleResourceFactory, _archivedSettingsFactory,
				_assetCategoryLocalService, _assetEntryLocalService,
				_assetLinkLocalService, _assetListEntryLocalService,
				_blogPostingResourceFactory, _cetManager,
				_clientExtensionEntryLocalService, _companyLocalService,
				_configurationProvider, _dataDefinitionResourceFactory,
				_ddmStructureLocalService, _ddmTemplateLocalService,
				_defaultDDMStructureHelper, _dependencyManager,
				_depotEntryGroupRelLocalService, _depotEntryLocalService,
				_dlFileEntryTypeLocalService, _dlURLHelper,
				_documentFolderResourceFactory, _documentResourceFactory,
				_expandoValueLocalService, _fragmentEntryLinkLocalService,
				_fragmentsImporter, _groupLocalService,
				_journalArticleLocalService, _jsonFactory,
				_keywordResourceFactory, _knowledgeBaseArticleResourceFactory,
				_knowledgeBaseFolderResourceFactory, _layoutLocalService,
				_layoutPageTemplateEntryLocalService,
				_layoutPageTemplateStructureLocalService,
				_layoutPageTemplateStructureRelLocalService,
				_layoutSetLocalService, _layoutsImporter,
				_layoutUtilityPageEntryLocalService,
				_listTypeDefinitionResource, _listTypeDefinitionResourceFactory,
				_listTypeEntryLocalService, _listTypeEntryResource,
				_listTypeEntryResourceFactory, _menuAccessConfigurationManager,
				_notificationTemplateResourceFactory, _objectActionLocalService,
				_objectDefinitionLocalService, _objectDefinitionResourceFactory,
				_objectEntryLocalService, _objectEntryManager,
				_objectFieldLocalService, _objectFieldResourceFactory,
				_objectFolderResourceFactory, _objectRelationshipLocalService,
				_objectRelationshipResourceFactory, _organizationLocalService,
				_organizationResourceFactory, _ploEntryLocalService, _portal,
				_resourceActionLocalService, _resourcePermissionLocalService,
				_roleLocalService, _sapEntryLocalService,
				_segmentsEntryLocalService, _segmentsExperienceLocalService,
				null, bundle, _bundleContext.getBundle(),
				_siteNavigationMenuItemLocalService,
				_siteNavigationMenuItemTypeRegistry,
				_siteNavigationMenuLocalService,
				_structuredContentFolderResourceFactory,
				_styleBookEntryZipProcessor, _taxonomyCategoryResourceFactory,
				_taxonomyVocabularyResourceFactory, _templateEntryLocalService,
				_themeLocalService, _userAccountResourceFactory,
				_userGroupLocalService, _userLocalService,
				_workflowDefinitionLinkLocalService,
				_workflowDefinitionResourceFactory, _zipWriterFactory);

		siteInitializerExtension.start();

		return siteInitializerExtension;
	}

	public File getFile(String fileKey) {
		return _files.get(fileKey);
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		SiteInitializerExtension siteInitializerExtension) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		SiteInitializerExtension siteInitializerExtension) {

		siteInitializerExtension.destroy();
	}

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		_bundleContext = bundleContext;

		_dependencyManager = new DependencyManager(bundleContext);

		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE, this);

		_bundleTracker.open();

		File siteInitializersDirectoryFile = new File(
			PropsValues.LIFERAY_HOME, "site-initializers");

		if (siteInitializersDirectoryFile.isDirectory()) {
			for (File file : siteInitializersDirectoryFile.listFiles()) {
				_addFile(file);
			}
		}
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();

		_files.clear();

		for (SiteInitializerExtension siteInitializerExtension :
				_fileSiteInitializerExtensions) {

			siteInitializerExtension.destroy();
		}

		_fileSiteInitializerExtensions.clear();
	}

	private void _addFile(File file) throws Exception {
		if (!file.isDirectory()) {
			return;
		}

		String fileKey = StringUtil.randomString(16);

		_files.put(fileKey, file);

		String symbolicName = "Liferay Site Initializer - File - " + fileKey;

		SiteInitializerExtension siteInitializerExtension =
			new SiteInitializerExtension(
				_accountEntryLocalService,
				_accountEntryOrganizationRelLocalService,
				_accountGroupLocalService, _accountGroupRelService,
				_accountResourceFactory, _accountRoleLocalService,
				_accountRoleResourceFactory, _archivedSettingsFactory,
				_assetCategoryLocalService, _assetEntryLocalService,
				_assetLinkLocalService, _assetListEntryLocalService,
				_blogPostingResourceFactory, _cetManager,
				_clientExtensionEntryLocalService, _companyLocalService,
				_configurationProvider, _dataDefinitionResourceFactory,
				_ddmStructureLocalService, _ddmTemplateLocalService,
				_defaultDDMStructureHelper, _dependencyManager,
				_depotEntryGroupRelLocalService, _depotEntryLocalService,
				_dlFileEntryTypeLocalService, _dlURLHelper,
				_documentFolderResourceFactory, _documentResourceFactory,
				_expandoValueLocalService, _fragmentEntryLinkLocalService,
				_fragmentsImporter, _groupLocalService,
				_journalArticleLocalService, _jsonFactory,
				_keywordResourceFactory, _knowledgeBaseArticleResourceFactory,
				_knowledgeBaseFolderResourceFactory, _layoutLocalService,
				_layoutPageTemplateEntryLocalService,
				_layoutPageTemplateStructureLocalService,
				_layoutPageTemplateStructureRelLocalService,
				_layoutSetLocalService, _layoutsImporter,
				_layoutUtilityPageEntryLocalService,
				_listTypeDefinitionResource, _listTypeDefinitionResourceFactory,
				_listTypeEntryLocalService, _listTypeEntryResource,
				_listTypeEntryResourceFactory, _menuAccessConfigurationManager,
				_notificationTemplateResourceFactory, _objectActionLocalService,
				_objectDefinitionLocalService, _objectDefinitionResourceFactory,
				_objectEntryLocalService, _objectEntryManager,
				_objectFieldLocalService, _objectFieldResourceFactory,
				_objectFolderResourceFactory, _objectRelationshipLocalService,
				_objectRelationshipResourceFactory, _organizationLocalService,
				_organizationResourceFactory, _ploEntryLocalService, _portal,
				_resourceActionLocalService, _resourcePermissionLocalService,
				_roleLocalService, _sapEntryLocalService,
				_segmentsEntryLocalService, _segmentsExperienceLocalService,
				ProxyUtil.newDelegateProxyInstance(
					ServletContext.class.getClassLoader(), ServletContext.class,
					new FileBackedServletContextDelegate(
						file, fileKey, symbolicName),
					null),
				ProxyUtil.newDelegateProxyInstance(
					Bundle.class.getClassLoader(), Bundle.class,
					new FileBackedBundleDelegate(
						_bundleContext, file, _jsonFactory, symbolicName),
					null),
				_bundleContext.getBundle(), _siteNavigationMenuItemLocalService,
				_siteNavigationMenuItemTypeRegistry,
				_siteNavigationMenuLocalService,
				_structuredContentFolderResourceFactory,
				_styleBookEntryZipProcessor, _taxonomyCategoryResourceFactory,
				_taxonomyVocabularyResourceFactory, _templateEntryLocalService,
				_themeLocalService, _userAccountResourceFactory,
				_userGroupLocalService, _userLocalService,
				_workflowDefinitionLinkLocalService,
				_workflowDefinitionResourceFactory, _zipWriterFactory);

		siteInitializerExtension.start();

		_fileSiteInitializerExtensions.add(siteInitializerExtension);
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private AccountGroupRelService _accountGroupRelService;

	@Reference
	private AccountResource.Factory _accountResourceFactory;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private AccountRoleResource.Factory _accountRoleResourceFactory;

	@Reference
	private ArchivedSettingsFactory _archivedSettingsFactory;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference
	private BlogPostingResource.Factory _blogPostingResourceFactory;

	private BundleContext _bundleContext;
	private BundleTracker<?> _bundleTracker;

	@Reference
	private CETManager _cetManager;

	@Reference
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DefaultDDMStructureHelper _defaultDDMStructureHelper;

	private DependencyManager _dependencyManager;

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private DocumentFolderResource.Factory _documentFolderResourceFactory;

	@Reference
	private DocumentResource.Factory _documentResourceFactory;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	private final Map<String, File> _files = new HashMap<>();
	private final List<SiteInitializerExtension>
		_fileSiteInitializerExtensions = new ArrayList<>();

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentsImporter _fragmentsImporter;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private KeywordResource.Factory _keywordResourceFactory;

	@Reference
	private KnowledgeBaseArticleResource.Factory
		_knowledgeBaseArticleResourceFactory;

	@Reference
	private KnowledgeBaseFolderResource.Factory
		_knowledgeBaseFolderResourceFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LayoutsImporter _layoutsImporter;

	@Reference
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Reference
	private ListTypeDefinitionResource _listTypeDefinitionResource;

	@Reference
	private ListTypeDefinitionResource.Factory
		_listTypeDefinitionResourceFactory;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ListTypeEntryResource _listTypeEntryResource;

	@Reference
	private ListTypeEntryResource.Factory _listTypeEntryResourceFactory;

	@Reference
	private MenuAccessConfigurationManager _menuAccessConfigurationManager;

	@Reference
	private NotificationTemplateResource.Factory
		_notificationTemplateResourceFactory;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFieldResource.Factory _objectFieldResourceFactory;

	@Reference
	private ObjectFolderResource.Factory _objectFolderResourceFactory;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectRelationshipResource.Factory
		_objectRelationshipResourceFactory;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private OrganizationResource.Factory _organizationResourceFactory;

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Reference
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

	@Reference
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@Reference
	private StructuredContentFolderResource.Factory
		_structuredContentFolderResourceFactory;

	@Reference
	private StyleBookEntryZipProcessor _styleBookEntryZipProcessor;

	@Reference
	private TaxonomyCategoryResource.Factory _taxonomyCategoryResourceFactory;

	@Reference
	private TaxonomyVocabularyResource.Factory
		_taxonomyVocabularyResourceFactory;

	@Reference
	private TemplateEntryLocalService _templateEntryLocalService;

	@Reference
	private ThemeLocalService _themeLocalService;

	@Reference
	private UserAccountResource.Factory _userAccountResourceFactory;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Reference
	private WorkflowDefinitionResource.Factory
		_workflowDefinitionResourceFactory;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}