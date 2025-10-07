/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.instance.lifecycle;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.notification.handler.NotificationHandler;
import com.liferay.notification.term.evaluator.NotificationTermEvaluator;
import com.liferay.object.configuration.ObjectConfiguration;
import com.liferay.object.info.field.converter.ObjectFieldInfoFieldConverter;
import com.liferay.object.internal.item.selector.SystemObjectEntryItemSelectorView;
import com.liferay.object.internal.notification.handler.ObjectDefinitionNotificationHandler;
import com.liferay.object.internal.notification.term.contributor.ObjectDefinitionNotificationTermEvaluator;
import com.liferay.object.internal.related.models.SystemObject1toMObjectRelatedModelsProviderImpl;
import com.liferay.object.internal.related.models.SystemObjectMtoMObjectRelatedModelsProviderImpl;
import com.liferay.object.internal.rest.context.path.RESTContextPathResolverImpl;
import com.liferay.object.internal.system.info.collection.provider.SystemObjectEntrySingleFormVariationInfoCollectionProvider;
import com.liferay.object.internal.system.info.item.provider.SystemObjectEntryInfoItemDetailsProvider;
import com.liferay.object.internal.system.info.item.provider.SystemObjectEntryInfoItemFieldValuesProvider;
import com.liferay.object.internal.system.info.item.provider.SystemObjectEntryInfoItemFormProvider;
import com.liferay.object.internal.system.info.item.provider.SystemObjectEntryInfoItemObjectProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistryUtil;
import com.liferay.object.rest.context.path.RESTContextPathResolver;
import com.liferay.object.rest.context.path.RESTContextPathResolverRegistry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.EveryNodeEveryStartup;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.extension.ExtensionProviderRegistry;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(
	configurationPid = "com.liferay.object.configuration.ObjectConfiguration",
	service = PortalInstanceLifecycleListener.class
)
public class SystemObjectDefinitionManagerPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener
	implements EveryNodeEveryStartup {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Registered portal instance " + company);
		}

		for (SystemObjectDefinitionManager systemObjectDefinitionManager :
				_serviceTrackerList) {

			_apply(company.getCompanyId(), systemObjectDefinitionManager);
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		if (_log.isDebugEnabled()) {
			_log.debug("Activate " + bundleContext);
		}

		modified(properties);

		_bundleContext = bundleContext;

		_opening.set(Boolean.TRUE);

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, SystemObjectDefinitionManager.class, null,
			new EagerServiceTrackerCustomizer
				<SystemObjectDefinitionManager,
				 SystemObjectDefinitionManager>() {

				@Override
				public SystemObjectDefinitionManager addingService(
					ServiceReference<SystemObjectDefinitionManager>
						serviceReference) {

					SystemObjectDefinitionManager
						systemObjectDefinitionManager =
							bundleContext.getService(serviceReference);

					if (_log.isDebugEnabled()) {
						_log.debug(
							"Adding service " + systemObjectDefinitionManager);
					}

					if (!_opening.get()) {
						_companyLocalService.forEachCompanyId(
							companyId -> _apply(
								companyId, systemObjectDefinitionManager));
					}

					return systemObjectDefinitionManager;
				}

				@Override
				public void modifiedService(
					ServiceReference<SystemObjectDefinitionManager>
						serviceReference,
					SystemObjectDefinitionManager
						systemObjectDefinitionManager) {
				}

				@Override
				public void removedService(
					ServiceReference<SystemObjectDefinitionManager>
						serviceReference,
					SystemObjectDefinitionManager
						systemObjectDefinitionManager) {

					bundleContext.ungetService(serviceReference);
				}

			});

		_opening.set(Boolean.FALSE);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_objectConfiguration = ConfigurableUtil.createConfigurable(
			ObjectConfiguration.class, properties);
	}

	private void _apply(
		long companyId,
		SystemObjectDefinitionManager systemObjectDefinitionManager) {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Applying ", systemObjectDefinitionManager, " to company ",
					companyId));
		}

		try {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					companyId, systemObjectDefinitionManager.getName());

			if ((objectDefinition == null) ||
				(objectDefinition.getVersion() !=
					systemObjectDefinitionManager.getVersion())) {

				ObjectFolder objectFolder =
					_objectFolderLocalService.getOrAddDefaultObjectFolder(
						companyId);

				objectDefinition =
					_objectDefinitionLocalService.
						addOrUpdateSystemObjectDefinition(
							companyId, objectFolder.getObjectFolderId(),
							systemObjectDefinitionManager);
			}

			String itemClassName =
				objectDefinition.getClassName() + StringPool.POUND +
					objectDefinition.getObjectDefinitionId();

			_bundleContext.registerService(
				InfoCollectionProvider.class,
				new SystemObjectEntrySingleFormVariationInfoCollectionProvider(
					itemClassName, objectDefinition,
					systemObjectDefinitionManager),
				HashMapDictionaryBuilder.<String, Object>put(
					"class.name", objectDefinition.getClassName()
				).put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", itemClassName
				).build());
			_bundleContext.registerService(
				InfoItemDetailsProvider.class,
				new SystemObjectEntryInfoItemDetailsProvider(
					itemClassName, objectDefinition),
				HashMapDictionaryBuilder.<String, Object>put(
					Constants.SERVICE_RANKING, 10
				).put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", itemClassName
				).build());

			ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter =
				new ObjectFieldInfoFieldConverter(
					_ddmExpressionFactory, _listTypeEntryLocalService,
					_objectConfiguration, _objectDefinitionLocalService,
					_objectFieldLocalService, _objectFieldSettingLocalService,
					_objectRelationshipLocalService,
					_objectScopeProviderRegistry, _objectStateFlowLocalService,
					_objectStateLocalService, _portal,
					_restContextPathResolverRegistry, _userLocalService);

			_bundleContext.registerService(
				InfoItemFieldValuesProvider.class,
				new SystemObjectEntryInfoItemFieldValuesProvider(
					_displayPageInfoItemFieldSetProvider, _dlAppLocalService,
					_dlURLHelper, _dtoConverterRegistry,
					_extensionProviderRegistry, _friendlyURLEntryLocalService,
					_infoItemFieldReaderFieldSetProvider, itemClassName,
					_listTypeEntryLocalService, _objectActionLocalService,
					objectDefinition, _objectDefinitionLocalService,
					_objectEntryLocalService, _objectEntryManagerRegistry,
					objectFieldInfoFieldConverter, _objectFieldLocalService,
					_objectRelationshipLocalService,
					_objectScopeProviderRegistry, _portal,
					systemObjectDefinitionManager,
					_templateInfoItemFieldSetProvider),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", itemClassName
				).build());
			_bundleContext.registerService(
				InfoItemFormProvider.class,
				new SystemObjectEntryInfoItemFormProvider(
					_displayPageInfoItemFieldSetProvider,
					_infoItemFieldReaderFieldSetProvider, itemClassName,
					_objectActionLocalService, objectDefinition,
					_objectDefinitionLocalService,
					objectFieldInfoFieldConverter, _objectFieldLocalService,
					_objectRelationshipLocalService,
					_templateInfoItemFieldSetProvider),
				HashMapDictionaryBuilder.<String, Object>put(
					Constants.SERVICE_RANKING, 10
				).put(
					"company.id", objectDefinition.getCompanyId()
				).put(
					"item.class.name", itemClassName
				).build());

			_bundleContext.registerService(
				InfoItemObjectProvider.class,
				new SystemObjectEntryInfoItemObjectProvider(
					_dtoConverterRegistry, systemObjectDefinitionManager),
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
					"item.class.name", itemClassName
				).build());

			_bundleContext.registerService(
				ItemSelectorView.class,
				new SystemObjectEntryItemSelectorView(
					_dtoConverterRegistry, _itemSelector,
					_itemSelectorViewDescriptorRenderer, objectDefinition,
					_objectDefinitionLocalService, _objectFieldLocalService,
					_objectRelatedModelsProviderRegistry, _portal,
					_systemObjectDefinitionManagerRegistry, _userLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"item.selector.view.order", 500
				).build());
			_bundleContext.registerService(
				NotificationTermEvaluator.class,
				new ObjectDefinitionNotificationTermEvaluator(
					_listTypeLocalService, objectDefinition,
					_objectDefinitionLocalService,
					_objectEntryFolderLocalService, _objectEntryLocalService,
					_objectFieldLocalService, _objectRelationshipLocalService,
					_userLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"class.name", objectDefinition.getClassName()
				).build());
			_bundleContext.registerService(
				NotificationHandler.class,
				new ObjectDefinitionNotificationHandler(objectDefinition),
				HashMapDictionaryBuilder.<String, Object>put(
					"class.name", objectDefinition.getClassName()
				).build());

			JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
				systemObjectDefinitionManager.getJaxRsApplicationDescriptor();

			_bundleContext.registerService(
				RESTContextPathResolver.class,
				new RESTContextPathResolverImpl(
					"/o/" + jaxRsApplicationDescriptor.getRESTContextPath(),
					_objectScopeProviderRegistry.getObjectScopeProvider(
						objectDefinition.getScope()),
					true),
				HashMapDictionaryBuilder.<String, Object>put(
					"model.class.name", objectDefinition.getClassName()
				).build());

			ObjectRelatedModelsProviderRegistryUtil.register(
				_bundleContext, objectDefinition,
				new SystemObjectMtoMObjectRelatedModelsProviderImpl(
					objectDefinition, _objectDefinitionLocalService,
					_objectFieldLocalService, _objectRelationshipLocalService,
					systemObjectDefinitionManager,
					_systemObjectDefinitionManagerRegistry));
			ObjectRelatedModelsProviderRegistryUtil.register(
				_bundleContext, objectDefinition,
				new SystemObject1toMObjectRelatedModelsProviderImpl(
					objectDefinition, _objectDefinitionLocalService,
					_objectEntryLocalService, _objectFieldLocalService,
					_objectRelationshipLocalService,
					systemObjectDefinitionManager,
					_systemObjectDefinitionManagerRegistry));
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SystemObjectDefinitionManagerPortalInstanceLifecycleListener.class);

	private static final ThreadLocal<Boolean> _opening =
		new CentralizedThreadLocal<>(
			SystemObjectDefinitionManagerPortalInstanceLifecycleListener.class.
				getName() + "._opening",
			() -> Boolean.FALSE);

	private BundleContext _bundleContext;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference
	private DisplayPageInfoItemFieldSetProvider
		_displayPageInfoItemFieldSetProvider;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExtensionProviderRegistry _extensionProviderRegistry;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private InfoItemFieldReaderFieldSetProvider
		_infoItemFieldReaderFieldSetProvider;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private ItemSelectorViewDescriptorRenderer<InfoItemItemSelectorCriterion>
		_itemSelectorViewDescriptorRenderer;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	private volatile ObjectConfiguration _objectConfiguration;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Reference
	private ObjectStateFlowLocalService _objectStateFlowLocalService;

	@Reference
	private ObjectStateLocalService _objectStateLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.object.service)(release.schema.version>=1.0.0))"
	)
	private Release _release;

	@Reference
	private RESTContextPathResolverRegistry _restContextPathResolverRegistry;

	private ServiceTrackerList<SystemObjectDefinitionManager>
		_serviceTrackerList;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private TemplateInfoItemFieldSetProvider _templateInfoItemFieldSetProvider;

	@Reference
	private UserLocalService _userLocalService;

}