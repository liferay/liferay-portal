/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.deployer;

import com.liferay.application.list.PanelApp;
import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.util.AssetHelper;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.exception.InvalidFileException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.friendly.url.info.item.updater.InfoItemFriendlyURLUpdater;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.item.action.executor.InfoItemActionExecutor;
import com.liferay.info.item.capability.InfoItemCapability;
import com.liferay.info.item.creator.InfoItemCreator;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.info.item.provider.InfoItemActionDetailsProvider;
import com.liferay.info.item.provider.InfoItemCapabilitiesProvider;
import com.liferay.info.item.provider.InfoItemCategorizationProvider;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.info.item.provider.InfoItemScopeProvider;
import com.liferay.info.item.provider.InfoItemStatusProvider;
import com.liferay.info.item.provider.RelatedInfoItemProvider;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.item.renderer.InfoItemRendererRegistry;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.info.list.renderer.InfoListRenderer;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.info.staging.InfoStagingClassMapper;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.page.template.info.item.capability.DisplayPageInfoItemCapability;
import com.liferay.layout.page.template.info.item.capability.EditPageInfoItemCapability;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.configuration.ObjectConfiguration;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.definition.security.permission.resource.ObjectDefinitionPortletResourcePermissionRegistryUtil;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.deployer.ObjectDefinitionDeployer;
import com.liferay.object.display.context.ObjectEntryDisplayContextFactory;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.field.filter.parser.ObjectFieldFilterContributorRegistry;
import com.liferay.object.info.field.converter.ObjectFieldInfoFieldConverter;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.rest.context.path.RESTContextPathResolverRegistry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectRelationshipService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.web.internal.asset.display.page.portlet.ObjectEntryDisplayPageFriendlyURLResolver;
import com.liferay.object.web.internal.asset.model.ObjectEntryAssetRendererFactory;
import com.liferay.object.web.internal.info.collection.provider.ObjectEntrySingleFormVariationInfoCollectionProvider;
import com.liferay.object.web.internal.info.item.action.ObjectEntryInfoItemActionExecutor;
import com.liferay.object.web.internal.info.item.creator.ObjectEntryInfoItemCreator;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemActionDetailsProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemCapabilitiesProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemCategorizationProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemDetailsProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemFieldValuesProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemFormProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemFriendlyURLProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemLanguagesProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemObjectProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemPermissionProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemScopeProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryInfoItemStatusProvider;
import com.liferay.object.web.internal.info.item.provider.ObjectEntryRelatedInfoItemProvider;
import com.liferay.object.web.internal.info.item.renderer.ObjectEntryRowInfoItemRenderer;
import com.liferay.object.web.internal.info.item.updater.ObjectEntryInfoItemFieldValuesUpdater;
import com.liferay.object.web.internal.info.item.updater.ObjectEntryInfoItemFriendlyURLUpdater;
import com.liferay.object.web.internal.info.list.renderer.ObjectEntryTableInfoListRenderer;
import com.liferay.object.web.internal.info.permission.provider.ObjectEntryInfoPermissionProvider;
import com.liferay.object.web.internal.info.staging.ObjectEntryInfoStagingClassMapper;
import com.liferay.object.web.internal.item.selector.ObjectEntryItemSelectorView;
import com.liferay.object.web.internal.layout.display.page.ObjectEntryLayoutDisplayPageProvider;
import com.liferay.object.web.internal.notifications.ObjectUserNotificationsDefinition;
import com.liferay.object.web.internal.notifications.ObjectUserNotificationsHandler;
import com.liferay.object.web.internal.object.entries.application.list.ObjectEntriesPanelApp;
import com.liferay.object.web.internal.object.entries.frontend.data.set.filter.factory.ObjectFieldFDSFilterFactoryRegistry;
import com.liferay.object.web.internal.object.entries.frontend.data.set.view.table.ObjectEntriesTableFDSView;
import com.liferay.object.web.internal.object.entries.portlet.ObjectEntriesPortlet;
import com.liferay.object.web.internal.object.entries.portlet.action.DeleteAttachmentMVCActionCommand;
import com.liferay.object.web.internal.object.entries.portlet.action.EditObjectEntryMVCActionCommand;
import com.liferay.object.web.internal.object.entries.portlet.action.EditObjectEntryMVCRenderCommand;
import com.liferay.object.web.internal.object.entries.portlet.action.EditObjectEntryRelatedModelMVCActionCommand;
import com.liferay.object.web.internal.object.entries.portlet.action.ExpireObjectEntryMVCActionCommand;
import com.liferay.object.web.internal.object.entries.portlet.action.UploadAttachmentMVCActionCommand;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.template.info.item.capability.TemplateInfoItemCapability;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	configurationPid = "com.liferay.object.configuration.ObjectConfiguration",
	service = ObjectDefinitionDeployer.class
)
public class ObjectDefinitionDeployerImpl implements ObjectDefinitionDeployer {

	@Override
	public List<ServiceRegistration<?>> deploy(
		ObjectDefinition objectDefinition) {

		if (objectDefinition.isUnmodifiableSystemObject()) {
			return Collections.emptyList();
		}

		ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter =
			new ObjectFieldInfoFieldConverter(
				_ddmExpressionFactory, _listTypeEntryLocalService,
				_objectConfiguration, _objectDefinitionLocalService,
				_objectFieldLocalService, _objectFieldSettingLocalService,
				_objectRelationshipLocalService, _objectScopeProviderRegistry,
				_objectStateFlowLocalService, _objectStateLocalService, _portal,
				_restContextPathResolverRegistry, _userLocalService);

		InfoItemFormProvider<ObjectEntry> infoItemFormProvider =
			new ObjectEntryInfoItemFormProvider(
				_displayPageInfoItemFieldSetProvider, objectDefinition,
				_infoItemFieldReaderFieldSetProvider,
				_listTypeEntryLocalService, _objectActionLocalService,
				_objectDefinitionLocalService, objectFieldInfoFieldConverter,
				_objectFieldLocalService, _objectFieldSettingLocalService,
				_objectRelationshipLocalService, _objectScopeProviderRegistry,
				_restContextPathResolverRegistry,
				_templateInfoItemFieldSetProvider, _userLocalService);

		InfoItemFriendlyURLProvider infoItemFriendlyURLProvider =
			new ObjectEntryInfoItemFriendlyURLProvider(
				_friendlyURLEntryLocalService, objectDefinition, _portal);

		PortletResourcePermission portletResourcePermission =
			_getPortletResourcePermission(objectDefinition.getResourceName());

		InfoPermissionProvider infoPermissionProvider =
			new ObjectEntryInfoPermissionProvider(
				objectDefinition, _portletLocalService,
				portletResourcePermission);

		List<ServiceRegistration<?>> serviceRegistrations = ListUtil.fromArray(
			_bundleContext.registerService(
				AssetRendererFactory.class,
				new ObjectEntryAssetRendererFactory(
					_assetDisplayPageFriendlyURLProvider,
					_depotEntryLocalService, objectDefinition,
					_objectEntryDisplayContextFactory, _objectEntryLocalService,
					_objectEntryService, _servletContext),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"jakarta.portlet.name", objectDefinition.getPortletId()
				).build()),
			_bundleContext.registerService(
				FDSView.class,
				new ObjectEntriesTableFDSView(
					_fdsTableSchemaBuilderFactory, objectDefinition,
					_objectDefinitionLocalService, _objectFieldLocalService,
					_objectRelationshipLocalService, _objectViewLocalService,
					_userLocalService),
				HashMapDictionaryBuilder.put(
					"frontend.data.set.name", objectDefinition.getPortletId()
				).build()),
			_bundleContext.registerService(
				InfoCollectionProvider.class,
				new ObjectEntrySingleFormVariationInfoCollectionProvider(
					_assetCategoryLocalService, _assetTagLocalService,
					_assetVocabularyLocalService, _groupLocalService,
					_listTypeEntryLocalService, objectDefinition,
					_objectEntryLocalService, _objectEntryManagerRegistry,
					_objectFieldLocalService, _objectLayoutLocalService,
					_objectScopeProviderRegistry),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemActionDetailsProvider.class,
				new ObjectEntryInfoItemActionDetailsProvider(
					infoItemFormProvider, _objectActionLocalService,
					objectDefinition),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemActionExecutor.class,
				new ObjectEntryInfoItemActionExecutor(
					infoItemFormProvider, _objectActionLocalService,
					objectDefinition, _objectEntryManagerRegistry),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemCapabilitiesProvider.class,
				new ObjectEntryInfoItemCapabilitiesProvider(
					_displayPageInfoItemCapability, _editPageInfoItemCapability,
					_templateInfoItemCapability),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemCategorizationProvider.class,
				new ObjectEntryInfoItemCategorizationProvider(objectDefinition),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemStatusProvider.class,
				new ObjectEntryInfoItemStatusProvider(objectDefinition),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemCreator.class,
				new ObjectEntryInfoItemCreator(
					infoItemFormProvider, objectDefinition,
					_objectEntryLocalService, _objectEntryManagerRegistry,
					_objectScopeProviderRegistry),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemDetailsProvider.class,
				new ObjectEntryInfoItemDetailsProvider(objectDefinition),
				HashMapDictionaryBuilder.<String, Object>put(
					Constants.SERVICE_RANKING, 10
				).put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemFieldValuesProvider.class,
				new ObjectEntryInfoItemFieldValuesProvider(
					_displayPageInfoItemFieldSetProvider, _dlAppLocalService,
					_dlURLHelper, _friendlyURLEntryLocalService,
					_infoItemFieldReaderFieldSetProvider,
					_listTypeEntryLocalService, _objectActionLocalService,
					objectDefinition, _objectDefinitionLocalService,
					objectFieldInfoFieldConverter, _objectEntryLocalService,
					_objectEntryManagerRegistry, _objectFieldLocalService,
					_objectRelationshipLocalService,
					_objectScopeProviderRegistry, _portal,
					_templateInfoItemFieldSetProvider, _userLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemFieldValuesUpdater.class,
				new ObjectEntryInfoItemFieldValuesUpdater(
					infoItemFormProvider, objectDefinition,
					_objectEntryManagerRegistry, _objectScopeProviderRegistry),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemFormProvider.class, infoItemFormProvider,
				HashMapDictionaryBuilder.<String, Object>put(
					Constants.SERVICE_RANKING, 10
				).put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemFriendlyURLProvider.class, infoItemFriendlyURLProvider,
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemObjectProvider.class,
				new ObjectEntryInfoItemObjectProvider(
					_groupLocalService, objectDefinition,
					_objectEntryLocalService, _objectEntryManagerRegistry,
					_userLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					Constants.SERVICE_RANKING, 100
				).put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"info.item.identifier",
					new String[] {
						"com.liferay.info.item.ClassPKInfoItemIdentifier",
						"com.liferay.info.item.ERCInfoItemIdentifier"
					}
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemPermissionProvider.class,
				new ObjectEntryInfoItemPermissionProvider(
					objectDefinition,
					_objectEntryManagerRegistry.getObjectEntryManager(
						objectDefinition.getStorageType()),
					_objectEntryService),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoItemRenderer.class,
				new ObjectEntryRowInfoItemRenderer(
					_assetDisplayPageFriendlyURLProvider, objectDefinition,
					_objectEntryManagerRegistry.getObjectEntryManager(
						objectDefinition.getStorageType()),
					_objectFieldLocalService, _objectScopeProviderRegistry,
					_servletContext),
				HashMapDictionaryBuilder.<String, Object>put(
					Constants.SERVICE_RANKING, 100
				).put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).put(
					"osgi.web.symbolicname", "com.liferay.object.web"
				).build()),
			_bundleContext.registerService(
				InfoItemScopeProvider.class,
				new ObjectEntryInfoItemScopeProvider(
					objectDefinition, _objectScopeProviderRegistry),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoListRenderer.class,
				new ObjectEntryTableInfoListRenderer(
					_infoItemRendererRegistry, objectDefinition,
					_objectFieldLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoPermissionProvider.class, infoPermissionProvider,
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				InfoStagingClassMapper.class,
				new ObjectEntryInfoStagingClassMapper(objectDefinition),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				ItemSelectorView.class,
				new ObjectEntryItemSelectorView(
					_groupLocalService, infoPermissionProvider,
					_itemSelectorViewDescriptorRenderer, objectDefinition,
					_objectEntryManagerRegistry.getObjectEntryManager(
						objectDefinition.getStorageType()),
					_objectRelatedModelsProviderRegistry,
					_objectScopeProviderRegistry, _portal),
				HashMapDictionaryBuilder.<String, Object>put(
					"item.selector.view.order", 500
				).build()),
			_bundleContext.registerService(
				LayoutDisplayPageProvider.class,
				new ObjectEntryLayoutDisplayPageProvider(
					_assetHelper, infoItemFriendlyURLProvider, objectDefinition,
					_objectDefinitionLocalService, _objectEntryLocalService,
					_objectEntryManagerRegistry.getObjectEntryManager(
						objectDefinition.getStorageType()),
					_objectRelationshipLocalService, _userLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				Portlet.class,
				new ObjectEntriesPortlet(
					_objectActionLocalService,
					objectDefinition.getObjectDefinitionId(),
					_objectDefinitionLocalService,
					_objectFieldFDSFilterFactoryRegistry,
					_objectFieldLocalService, _objectScopeProviderRegistry,
					_objectViewLocalService, _portal,
					portletResourcePermission),
				HashMapDictionaryBuilder.<String, Object>put(
					"com.liferay.portlet.company",
					objectDefinition.getCompanyId()
				).put(
					"com.liferay.portlet.display-category",
					() -> {
						if (objectDefinition.isPortlet()) {
							return "category.object";
						}

						return "category.hidden";
					}
				).put(
					"jakarta.portlet.display-name",
					objectDefinition.getPluralLabel(LocaleUtil.getSiteDefault())
				).put(
					"jakarta.portlet.init-param.view-template",
					"/object_entries/view_object_entries.jsp"
				).put(
					"jakarta.portlet.name", objectDefinition.getPortletId()
				).put(
					"jakarta.portlet.version", "3.0"
				).build()),
			_bundleContext.registerService(
				MVCActionCommand.class,
				new DeleteAttachmentMVCActionCommand(
					_dlFileEntryLocalService, objectDefinition),
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", objectDefinition.getPortletId()
				).put(
					"mvc.command.name", "/object_entries/delete_attachment"
				).build()),
			_bundleContext.registerService(
				MVCActionCommand.class,
				new EditObjectEntryMVCActionCommand(
					_objectDefinitionLocalService, _objectEntryService,
					_objectRelatedModelsProviderRegistry,
					_objectRelationshipLocalService,
					_objectScopeProviderRegistry, _portal),
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", objectDefinition.getPortletId()
				).put(
					"mvc.command.name", "/object_entries/edit_object_entry"
				).build()),
			_bundleContext.registerService(
				MVCActionCommand.class,
				new EditObjectEntryRelatedModelMVCActionCommand(
					_objectDefinitionLocalService,
					_objectRelationshipLocalService,
					_objectRelationshipService),
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", objectDefinition.getPortletId()
				).put(
					"mvc.command.name",
					"/object_entries/edit_object_entry_related_model"
				).build()),
			_bundleContext.registerService(
				MVCActionCommand.class,
				new ExpireObjectEntryMVCActionCommand(
					_objectEntryLocalService, _objectEntryService),
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", objectDefinition.getPortletId()
				).put(
					"mvc.command.name", "/object_entries/expire_object_entry"
				).build()),
			_bundleContext.registerService(
				MVCActionCommand.class,
				new UploadAttachmentMVCActionCommand(
					_attachmentUploadFileEntryHandler,
					_attachmentUploadResponseHandler, _uploadHandler),
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", objectDefinition.getPortletId()
				).put(
					"mvc.command.name", "/object_entries/upload_attachment"
				).build()),
			_bundleContext.registerService(
				MVCRenderCommand.class,
				new EditObjectEntryMVCRenderCommand(
					_objectEntryDisplayContextFactory, _portal),
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", objectDefinition.getPortletId()
				).put(
					"mvc.command.name", "/object_entries/edit_object_entry"
				).build()),
			_bundleContext.registerService(
				RelatedInfoItemProvider.class,
				new ObjectEntryRelatedInfoItemProvider(
					objectDefinition, _objectDefinitionLocalService,
					_objectRelationshipLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				UserNotificationDefinition.class,
				new ObjectUserNotificationsDefinition(
					objectDefinition.getPortletId(),
					_portal.getClassNameId(objectDefinition.getClassName()),
					UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY),
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", objectDefinition.getPortletId()
				).build()),
			_bundleContext.registerService(
				UserNotificationHandler.class,
				new ObjectUserNotificationsHandler(
					_assetDisplayPageFriendlyURLProvider, objectDefinition),
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", objectDefinition.getPortletId()
				).build()));

		if (FeatureFlagManagerUtil.isEnabled(
				objectDefinition.getCompanyId(), "LPD-21926")) {

			if (Validator.isNotNull(
					objectDefinition.getFriendlyURLSeparator()) &&
				!ObjectDefinitionUtil.isDefaultFriendlyURLSeparator(
					objectDefinition.getFriendlyURLSeparator())) {

				ObjectEntryDisplayPageFriendlyURLResolver
					objectEntryDisplayPageFriendlyURLResolver =
						(ObjectEntryDisplayPageFriendlyURLResolver)
							_friendlyURLResolverComponentServiceObjects.
								getService();

				objectEntryDisplayPageFriendlyURLResolver.setObjectDefinition(
					objectDefinition);

				serviceRegistrations.add(
					_bundleContext.registerService(
						FriendlyURLResolver.class,
						objectEntryDisplayPageFriendlyURLResolver,
						HashMapDictionaryBuilder.<String, Object>put(
							"company.id", objectDefinition.getCompanyId()
						).put(
							"item.class.name", objectDefinition.getClassName()
						).build()));
			}

			Collections.addAll(
				serviceRegistrations,
				_bundleContext.registerService(
					InfoItemFriendlyURLUpdater.class,
					new ObjectEntryInfoItemFriendlyURLUpdater(
						_friendlyURLEntryLocalService),
					HashMapDictionaryBuilder.<String, Object>put(
						"company.id", objectDefinition.getCompanyId()
					).put(
						"item.class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					InfoItemLanguagesProvider.class,
					new ObjectEntryInfoItemLanguagesProvider(),
					HashMapDictionaryBuilder.<String, Object>put(
						"company.id", objectDefinition.getCompanyId()
					).put(
						"item.class.name", objectDefinition.getClassName()
					).build()));
		}

		// Register ObjectEntriesPanelApp after ObjectEntriesPortlet. See
		// LPS-140379.

		serviceRegistrations.add(
			_bundleContext.registerService(
				PanelApp.class,
				new ObjectEntriesPanelApp(
					objectDefinition,
					() -> {
						com.liferay.portal.kernel.model.Portlet portlet =
							_portletLocalService.getPortletById(
								objectDefinition.getCompanyId(),
								objectDefinition.getPortletId());

						portlet.setControlPanelEntryCategory(
							objectDefinition.getPanelCategoryKey());

						return portlet;
					}),
				HashMapDictionaryBuilder.<String, Object>put(
					"panel.app.order:Integer",
					objectDefinition.getPanelAppOrder()
				).put(
					"panel.category.key", objectDefinition.getPanelCategoryKey()
				).build()));

		return serviceRegistrations;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_bundleContext = bundleContext;

		_objectFieldFDSFilterFactoryRegistry =
			new ObjectFieldFDSFilterFactoryRegistry(
				_language, _objectFieldFilterContributorRegistry,
				_objectFieldLocalService);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_objectConfiguration = ConfigurableUtil.createConfigurable(
			ObjectConfiguration.class, properties);
	}

	private PortletResourcePermission _getPortletResourcePermission(
		String resourceName) {

		PortletResourcePermission portletResourcePermission =
			ObjectDefinitionPortletResourcePermissionRegistryUtil.getService(
				resourceName);

		if (portletResourcePermission == null) {
			throw new IllegalArgumentException(
				"No portlet resource permission found with resource name " +
					resourceName);
		}

		return portletResourcePermission;
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private AttachmentManager _attachmentManager;

	private final AttachmentUploadFileEntryHandler
		_attachmentUploadFileEntryHandler =
			new AttachmentUploadFileEntryHandler();
	private final AttachmentUploadResponseHandler
		_attachmentUploadResponseHandler =
			new AttachmentUploadResponseHandler();
	private BundleContext _bundleContext;

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference(target = "(upload.response.handler.system.default=true)")
	private UploadResponseHandler _defaultUploadResponseHandler;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference(
		target = "(info.item.capability.key=" + DisplayPageInfoItemCapability.KEY + ")"
	)
	private InfoItemCapability _displayPageInfoItemCapability;

	@Reference
	private DisplayPageInfoItemFieldSetProvider
		_displayPageInfoItemFieldSetProvider;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference(
		target = "(info.item.capability.key=" + EditPageInfoItemCapability.KEY + ")"
	)
	private InfoItemCapability _editPageInfoItemCapability;

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<FriendlyURLResolver>
		_friendlyURLResolverComponentServiceObjects;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoItemFieldReaderFieldSetProvider
		_infoItemFieldReaderFieldSetProvider;

	@Reference
	private InfoItemRendererRegistry _infoItemRendererRegistry;

	@Reference
	private ItemSelectorViewDescriptorRenderer<InfoItemItemSelectorCriterion>
		_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private MimeTypes _mimeTypes;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	private volatile ObjectConfiguration _objectConfiguration;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryDisplayContextFactory _objectEntryDisplayContextFactory;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectEntryService _objectEntryService;

	private ObjectFieldFDSFilterFactoryRegistry
		_objectFieldFDSFilterFactoryRegistry;

	@Reference
	private ObjectFieldFilterContributorRegistry
		_objectFieldFilterContributorRegistry;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ObjectLayoutLocalService _objectLayoutLocalService;

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectRelationshipService _objectRelationshipService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Reference
	private ObjectStateFlowLocalService _objectStateFlowLocalService;

	@Reference
	private ObjectStateLocalService _objectStateLocalService;

	@Reference
	private ObjectViewLocalService _objectViewLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RESTContextPathResolverRegistry _restContextPathResolverRegistry;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.object.web)")
	private ServletContext _servletContext;

	@Reference(
		target = "(info.item.capability.key=" + TemplateInfoItemCapability.KEY + ")"
	)
	private InfoItemCapability _templateInfoItemCapability;

	@Reference
	private TemplateInfoItemFieldSetProvider _templateInfoItemFieldSetProvider;

	@Reference
	private UploadHandler _uploadHandler;

	@Reference
	private UserLocalService _userLocalService;

	private class AttachmentUploadFileEntryHandler
		implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			long objectFieldId = ParamUtil.getLong(
				uploadPortletRequest, "objectFieldId");

			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				objectFieldId);

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					objectField.getObjectDefinitionId());

			PortletResourcePermission portletResourcePermission =
				ObjectDefinitionPortletResourcePermissionRegistryUtil.
					getService(objectDefinition.getResourceName());

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			long groupId = _getGroupId(objectDefinition, themeDisplay);

			portletResourcePermission.check(
				themeDisplay.getPermissionChecker(), groupId,
				ObjectActionKeys.ADD_OBJECT_ENTRY);

			String fileName = uploadPortletRequest.getFileName("file");

			_attachmentManager.validateFileExtension(fileName, objectFieldId);

			File file = null;

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					"file")) {

				file = FileUtil.createTempFile(inputStream);

				if (file == null) {
					throw new InvalidFileException(
						"File is null for " + fileName);
				}

				_attachmentManager.validateFileSize(
					fileName, file.length(), objectFieldId,
					themeDisplay.isSignedIn());

				FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
					groupId, themeDisplay.getUserId(),
					objectDefinition.getPortletId(),
					TempFileEntryUtil.getTempFileName(fileName), file,
					_mimeTypes.getContentType(file, fileName));

				_resourcePermissionLocalService.removeResourcePermission(
					themeDisplay.getCompanyId(), DLFileEntry.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(tempFileEntry.getFileEntryId()),
					_roleLocalService.getRole(
						themeDisplay.getCompanyId(), RoleConstants.GUEST
					).getRoleId(),
					ActionKeys.DOWNLOAD);

				return tempFileEntry;
			}
			finally {
				if (file != null) {
					FileUtil.delete(file);
				}
			}
		}

		private long _getGroupId(
				ObjectDefinition objectDefinition, ThemeDisplay themeDisplay)
			throws PortalException {

			long groupId = themeDisplay.getScopeGroupId();

			if (Objects.equals(
					ObjectDefinitionConstants.SCOPE_COMPANY,
					objectDefinition.getScope())) {

				Company company = themeDisplay.getCompany();

				groupId = company.getGroupId();
			}

			return groupId;
		}

	}

	private class AttachmentUploadResponseHandler
		implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

			JSONObject jsonObject = _defaultUploadResponseHandler.onFailure(
				portletRequest, portalException);

			String errorMessage = StringPool.BLANK;

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (portalException instanceof FileExtensionException) {
				errorMessage = themeDisplay.translate(
					"please-enter-a-file-with-a-valid-extension-x",
					StringUtil.merge(
						_attachmentManager.getAcceptedFileExtensions(
							ParamUtil.getLong(portletRequest, "objectFieldId")),
						StringPool.COMMA_AND_SPACE));
			}
			else if (portalException instanceof FileSizeException) {
				errorMessage = themeDisplay.translate(
					"please-enter-a-file-with-a-valid-file-size-no-larger-" +
						"than-x",
					_language.formatStorageSize(
						_attachmentManager.getMaximumFileSize(
							ParamUtil.getLong(portletRequest, "objectFieldId"),
							themeDisplay.isSignedIn()),
						themeDisplay.getLocale()));
			}
			else if (portalException instanceof InvalidFileException) {
				errorMessage = themeDisplay.translate(
					"please-enter-a-valid-file");
			}
			else {
				errorMessage = themeDisplay.translate(
					"an-unexpected-error-occurred-while-uploading-your-file");
			}

			return jsonObject.put(
				"error", JSONUtil.put("message", errorMessage));
		}

		@Override
		public JSONObject onSuccess(
				UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
			throws PortalException {

			return JSONUtil.put(
				"file",
				JSONUtil.put(
					"contentURL",
					_dlURLHelper.getPreviewURL(
						fileEntry, fileEntry.getFileVersion(), null, "")
				).put(
					"fileEntryId", fileEntry.getFileEntryId()
				).put(
					"title",
					TempFileEntryUtil.getOriginalTempFileName(
						fileEntry.getFileName())
				)
			).put(
				"success", Boolean.TRUE
			);
		}

	}

}