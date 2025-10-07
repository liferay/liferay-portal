/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.deployer;

import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.notification.handler.NotificationHandler;
import com.liferay.notification.term.evaluator.NotificationTermEvaluator;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.definition.security.permission.resource.util.ObjectDefinitionResourcePermissionUtil;
import com.liferay.object.definition.tree.util.ObjectDefinitionTreeUtil;
import com.liferay.object.deployer.ObjectDefinitionDeployer;
import com.liferay.object.internal.defaultpermissions.resource.ObjectEntryPortalDefaultPermissionsModelResource;
import com.liferay.object.internal.layout.tab.screen.navigation.category.ObjectLayoutTabScreenNavigationCategory;
import com.liferay.object.internal.notification.handler.ObjectDefinitionNotificationHandler;
import com.liferay.object.internal.notification.term.contributor.ObjectDefinitionNotificationTermEvaluator;
import com.liferay.object.internal.related.models.ObjectEntry1to1ObjectRelatedModelsProviderImpl;
import com.liferay.object.internal.related.models.ObjectEntry1toMObjectRelatedModelsPredicateProviderImpl;
import com.liferay.object.internal.related.models.ObjectEntry1toMObjectRelatedModelsProviderImpl;
import com.liferay.object.internal.related.models.ObjectEntryMtoMObjectRelatedModelsPredicateProviderImpl;
import com.liferay.object.internal.related.models.ObjectEntryMtoMObjectRelatedModelsProviderImpl;
import com.liferay.object.internal.rest.context.path.RESTContextPathResolverImpl;
import com.liferay.object.internal.search.spi.model.index.contributor.ObjectEntryModelDocumentContributor;
import com.liferay.object.internal.search.spi.model.index.contributor.ObjectEntryModelIndexerWriterContributor;
import com.liferay.object.internal.search.spi.model.query.contributor.ObjectEntryKeywordQueryContributor;
import com.liferay.object.internal.search.spi.model.query.contributor.ObjectEntryModelPreFilterContributor;
import com.liferay.object.internal.search.spi.model.result.contributor.ObjectEntryModelSummaryContributor;
import com.liferay.object.internal.security.permission.ObjectEntrySharingPermissionChecker;
import com.liferay.object.internal.security.permission.resource.ObjectEntryModelResourcePermission;
import com.liferay.object.internal.security.permission.resource.ObjectEntryPortletResourcePermissionLogic;
import com.liferay.object.internal.trash.ObjectEntryTrashHandler;
import com.liferay.object.internal.uad.anonymizer.ObjectEntryUADAnonymizer;
import com.liferay.object.internal.uad.display.ObjectEntryUADDisplay;
import com.liferay.object.internal.uad.exporter.ObjectEntryUADExporter;
import com.liferay.object.internal.workflow.ObjectEntryWorkflowHandler;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsPredicateProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistryUtil;
import com.liferay.object.rest.context.path.RESTContextPathResolver;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectLayoutTabLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermissionFactory;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.localization.SearchLocalizationHelper;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingDocumentContributor;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchConfigurator;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.sharing.security.permission.SharingPermissionChecker;
import com.liferay.sharing.security.permission.resource.SharingModelResourcePermissionConfigurator;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.exporter.UADExporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Brian Wing Shun Chan
 * @author Marco Leo
 */
public class ObjectDefinitionDeployerImpl implements ObjectDefinitionDeployer {

	public ObjectDefinitionDeployerImpl(
		AccountEntryLocalService accountEntryLocalService,
		AccountEntryOrganizationRelLocalService
			accountEntryOrganizationRelLocalService,
		AssetEntryLocalService assetEntryLocalService,
		BundleContext bundleContext,
		DynamicQueryBatchIndexingActionableFactory
			dynamicQueryBatchIndexingActionableFactory,
		GroupLocalService groupLocalService,
		KaleoDefinitionLocalService kaleoDefinitionLocalService,
		ListTypeLocalService listTypeLocalService,
		ObjectActionLocalService objectActionLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryService objectEntryService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFolderLocalService objectFolderLocalService,
		ObjectLayoutLocalService objectLayoutLocalService,
		ObjectLayoutTabLocalService objectLayoutTabLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ObjectScopeProviderRegistry objectScopeProviderRegistry,
		ObjectViewLocalService objectViewLocalService,
		OrganizationLocalService organizationLocalService,
		PLOEntryLocalService ploEntryLocalService, Portal portal,
		PortletLocalService portletLocalService,
		ResourceActions resourceActions, UserLocalService userLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		SearchLocalizationHelper searchLocalizationHelper,
		SharingModelResourcePermissionConfigurator
			sharingModelResourcePermissionConfigurator,
		SystemEventLocalService systemEventLocalService,
		TextEmbeddingDocumentContributor textEmbeddingDocumentContributor,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService,
		ModelPreFilterContributor workflowStatusModelPreFilterContributor,
		UserGroupRoleLocalService userGroupRoleLocalService) {

		_accountEntryLocalService = accountEntryLocalService;
		_accountEntryOrganizationRelLocalService =
			accountEntryOrganizationRelLocalService;
		_assetEntryLocalService = assetEntryLocalService;
		_bundleContext = bundleContext;
		_dynamicQueryBatchIndexingActionableFactory =
			dynamicQueryBatchIndexingActionableFactory;
		_groupLocalService = groupLocalService;
		_kaleoDefinitionLocalService = kaleoDefinitionLocalService;
		_listTypeLocalService = listTypeLocalService;
		_objectActionLocalService = objectActionLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectDefinitionSettingLocalService =
			objectDefinitionSettingLocalService;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryService = objectEntryService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectFolderLocalService = objectFolderLocalService;
		_objectLayoutLocalService = objectLayoutLocalService;
		_objectLayoutTabLocalService = objectLayoutTabLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
		_objectViewLocalService = objectViewLocalService;
		_organizationLocalService = organizationLocalService;
		_ploEntryLocalService = ploEntryLocalService;
		_portal = portal;
		_portletLocalService = portletLocalService;
		_resourceActions = resourceActions;
		_userLocalService = userLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_searchLocalizationHelper = searchLocalizationHelper;
		_sharingModelResourcePermissionConfigurator =
			sharingModelResourcePermissionConfigurator;
		_systemEventLocalService = systemEventLocalService;
		_textEmbeddingDocumentContributor = textEmbeddingDocumentContributor;
		_workflowDefinitionLinkLocalService =
			workflowDefinitionLinkLocalService;
		_workflowStatusModelPreFilterContributor =
			workflowStatusModelPreFilterContributor;
		_userGroupRoleLocalService = userGroupRoleLocalService;

		_objectDefinitionTreeFactory = new ObjectDefinitionTreeFactory(
			_objectDefinitionLocalService, _objectRelationshipLocalService);
	}

	@Override
	public Map<String, List<ServiceRegistration<?>>> deploy(
		long companyId, List<ObjectDefinition> objectDefinitions) {

		Map<String, List<ServiceRegistration<?>>> serviceRegistrationsMap =
			new ConcurrentHashMap<>();

		Map<Long, List<ObjectAction>> objectActionsMap =
			_objectActionLocalService.getObjectActionsMap(
				companyId, true, ObjectActionTriggerConstants.KEY_STANDALONE);

		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-34594")) {
			ObjectDefinitionTreeUtil.populateRootObjectDefinitionIds(
				objectDefinitions,
				_objectDefinitionSettingLocalService.
					getObjectDefinitionSettingsMap(
						companyId,
						ObjectDefinitionSettingConstants.
							NAME_ROOT_OBJECT_DEFINITION_IDS));
		}

		Map<Long, List<ObjectLayout>> objectLayoutsMap =
			_objectLayoutLocalService.getObjectLayoutsMap(companyId);
		Map<Long, List<ObjectRelationship>> objectRelationshipsMap =
			_objectRelationshipLocalService.getObjectRelationshipsMap(
				companyId);

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			long objectDefinitionId = objectDefinition.getObjectDefinitionId();

			serviceRegistrationsMap.put(
				DBPartitionUtil.getPartitionKey(objectDefinitionId),
				_deploy(
					objectDefinition,
					objectLayoutsMap.getOrDefault(
						objectDefinitionId, Collections.emptyList()),
					objectRelationshipsMap,
					objectActionsMap.getOrDefault(
						objectDefinitionId, Collections.emptyList())));
		}

		return serviceRegistrationsMap;
	}

	@Override
	public List<ServiceRegistration<?>> deploy(
		ObjectDefinition objectDefinition) {

		return _deploy(objectDefinition, null, null, null);
	}

	@Override
	public synchronized void undeploy(ObjectDefinition objectDefinition) {
		_unregister(_getServiceRegistrationKey(objectDefinition, null));

		for (String serviceRegistrationKey : _serviceRegistrations.keySet()) {
			if (serviceRegistrationKey.startsWith(
					_getServiceRegistrationKey(objectDefinition, null) +
						StringPool.POUND)) {

				_unregister(serviceRegistrationKey);
			}
		}
	}

	private List<ServiceRegistration<?>> _deploy(
		ObjectDefinition objectDefinition, List<ObjectLayout> objectLayouts,
		Map<Long, List<ObjectRelationship>> objectRelationshipsMap,
		List<ObjectAction> standaloneObjectActions) {

		if (objectDefinition.isUnmodifiableSystemObject()) {
			return Collections.emptyList();
		}

		try {
			ObjectDefinitionResourcePermissionUtil.populateResourceActions(
				_objectActionLocalService, objectDefinition,
				_portletLocalService, _resourceActions,
				standaloneObjectActions);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}

		List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();

		if (objectDefinition.isEnableIndexSearch()) {
			ObjectEntryModelIndexerWriterContributor
				objectEntryModelIndexerWriterContributor =
					new ObjectEntryModelIndexerWriterContributor(
						_dynamicQueryBatchIndexingActionableFactory,
						objectDefinition.getObjectDefinitionId(),
						_objectEntryLocalService);
			ObjectEntryModelSummaryContributor
				objectEntryModelSummaryContributor =
					new ObjectEntryModelSummaryContributor();

			Collections.addAll(
				serviceRegistrations,
				_bundleContext.registerService(
					KeywordQueryContributor.class,
					new ObjectEntryKeywordQueryContributor(
						objectDefinition, _objectFieldLocalService,
						_objectViewLocalService, _searchLocalizationHelper),
					HashMapDictionaryBuilder.<String, Object>put(
						"component.name",
						ObjectEntryKeywordQueryContributor.class.getName()
					).put(
						"indexer.class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					ModelDocumentContributor.class,
					new ObjectEntryModelDocumentContributor(
						_accountEntryOrganizationRelLocalService,
						objectDefinition.getClassName(),
						_objectDefinitionLocalService,
						_objectEntryFolderLocalService,
						_objectEntryLocalService, _objectFieldLocalService,
						_objectFolderLocalService,
						_textEmbeddingDocumentContributor),
					HashMapDictionaryBuilder.<String, Object>put(
						"indexer.class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					ModelPreFilterContributor.class,
					new ObjectEntryModelPreFilterContributor(
						_workflowStatusModelPreFilterContributor),
					HashMapDictionaryBuilder.<String, Object>put(
						"indexer.class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					ModelSearchConfigurator.class,
					new ModelSearchConfigurator<ObjectEntry>() {

						@Override
						public String getClassName() {
							return objectDefinition.getClassName();
						}

						@Override
						public long getCompanyId() {
							return objectDefinition.getCompanyId();
						}

						@Override
						public ModelIndexerWriterContributor<ObjectEntry>
							getModelIndexerWriterContributor() {

							return objectEntryModelIndexerWriterContributor;
						}

						@Override
						public ModelSummaryContributor
							getModelSummaryContributor() {

							return objectEntryModelSummaryContributor;
						}

					},
					null));
		}

		Collections.addAll(
			serviceRegistrations,
			_bundleContext.registerService(
				NotificationHandler.class,
				new ObjectDefinitionNotificationHandler(objectDefinition),
				HashMapDictionaryBuilder.<String, Object>put(
					"class.name", objectDefinition.getClassName()
				).build()),
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
				).build()),
			_bundleContext.registerService(
				ObjectRelatedModelsPredicateProvider.class,
				new ObjectEntry1toMObjectRelatedModelsPredicateProviderImpl(
					objectDefinition, _objectFieldLocalService),
				null),
			_bundleContext.registerService(
				ObjectRelatedModelsPredicateProvider.class,
				new ObjectEntryMtoMObjectRelatedModelsPredicateProviderImpl(
					objectDefinition, _objectFieldLocalService),
				null),
			_bundleContext.registerService(
				PersistedModelLocalService.class, _objectEntryLocalService,
				MapUtil.singletonDictionary(
					"model.class.name", objectDefinition.getClassName())),
			_bundleContext.registerService(
				PortalDefaultPermissionsModelResource.class,
				new ObjectEntryPortalDefaultPermissionsModelResource(
					objectDefinition.getClassName(),
					objectDefinition.getLabel(), _getScope(objectDefinition)),
				MapUtil.singletonDictionary(
					"portal.default.permissions.model.resource.key",
					objectDefinition.getClassName())),
			_bundleContext.registerService(
				RESTContextPathResolver.class,
				new RESTContextPathResolverImpl(
					objectDefinition,
					_objectScopeProviderRegistry.getObjectScopeProvider(
						objectDefinition.getScope()),
					false),
				HashMapDictionaryBuilder.<String, Object>put(
					"model.class.name", objectDefinition.getClassName()
				).build()),
			_bundleContext.registerService(
				UADAnonymizer.class,
				new ObjectEntryUADAnonymizer(
					_assetEntryLocalService, objectDefinition,
					_objectEntryLocalService, _resourcePermissionLocalService),
				null),
			_bundleContext.registerService(
				UADDisplay.class,
				new ObjectEntryUADDisplay(
					_groupLocalService, objectDefinition,
					_objectEntryLocalService, _objectScopeProviderRegistry,
					_portal),
				null),
			_bundleContext.registerService(
				UADExporter.class,
				new ObjectEntryUADExporter(
					objectDefinition, _objectEntryLocalService),
				null),
			ObjectRelatedModelsProviderRegistryUtil.register(
				_bundleContext, objectDefinition,
				new ObjectEntryMtoMObjectRelatedModelsProviderImpl(
					objectDefinition, _objectEntryService,
					_objectRelationshipLocalService)),
			ObjectRelatedModelsProviderRegistryUtil.register(
				_bundleContext, objectDefinition,
				new ObjectEntry1toMObjectRelatedModelsProviderImpl(
					objectDefinition, _objectEntryService,
					_objectFieldLocalService, _objectRelationshipLocalService)),
			ObjectRelatedModelsProviderRegistryUtil.register(
				_bundleContext, objectDefinition,
				new ObjectEntry1to1ObjectRelatedModelsProviderImpl(
					objectDefinition, _objectEntryService,
					_objectFieldLocalService,
					_objectRelationshipLocalService)));

		ConsumerSupplier<ModelResourcePermissionLogic<ObjectEntry>>
			consumerSupplier = new ConsumerSupplier<>();
		PortletResourcePermission portletResourcePermission =
			PortletResourcePermissionFactory.create(
				objectDefinition.getResourceName(),
				new ObjectEntryPortletResourcePermissionLogic(
					_accountEntryLocalService, _groupLocalService,
					_objectDefinitionLocalService, _organizationLocalService));

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			new ObjectEntryModelResourcePermission(
				_accountEntryLocalService,
				_accountEntryOrganizationRelLocalService, _groupLocalService,
				objectDefinition.getClassName(), _objectActionLocalService,
				_objectDefinitionLocalService, _objectEntryLocalService,
				consumerSupplier, _objectFieldLocalService,
				portletResourcePermission, _resourcePermissionLocalService,
				_userGroupRoleLocalService);

		serviceRegistrations.add(
			_bundleContext.registerService(
				ModelResourcePermission.class, modelResourcePermission,
				HashMapDictionaryBuilder.<String, Object>put(
					"com.liferay.object", "true"
				).put(
					"model.class.name", objectDefinition.getClassName()
				).build()));

		serviceRegistrations.add(
			_bundleContext.registerService(
				PortletResourcePermission.class, portletResourcePermission,
				HashMapDictionaryBuilder.<String, Object>put(
					"com.liferay.object", "true"
				).put(
					"resource.name", objectDefinition.getResourceName()
				).build()));

		_sharingModelResourcePermissionConfigurator.configure(
			modelResourcePermission, consumerSupplier);

		serviceRegistrations.add(
			_bundleContext.registerService(
				SharingPermissionChecker.class,
				new ObjectEntrySharingPermissionChecker(
					modelResourcePermission),
				HashMapDictionaryBuilder.<String, Object>put(
					"com.liferay.object", "true"
				).put(
					"model.class.name", objectDefinition.getClassName()
				).build()));

		serviceRegistrations.add(
			_bundleContext.registerService(
				TrashHandler.class,
				new ObjectEntryTrashHandler(
					objectDefinition, _objectDefinitionLocalService,
					_objectEntryService, _systemEventLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"model.class.name", objectDefinition.getClassName()
				).build()));
		serviceRegistrations.add(
			_bundleContext.registerService(
				WorkflowHandler.class,
				new ObjectEntryWorkflowHandler(
					_kaleoDefinitionLocalService, objectDefinition,
					_objectDefinitionLocalService, _objectEntryLocalService,
					_workflowDefinitionLinkLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"model.class.name", objectDefinition.getClassName()
				).build()));

		ObjectLayout objectLayout = null;

		if (objectLayouts == null) {
			objectLayout = _objectLayoutLocalService.fetchDefaultObjectLayout(
				objectDefinition.getObjectDefinitionId());
		}
		else if (!objectLayouts.isEmpty()) {
			objectLayout = objectLayouts.get(0);
		}

		if (objectLayout != null) {
			_objectLayoutTabLocalService.
				registerObjectLayoutTabScreenNavigationCategories(
					objectDefinition, objectLayout.getObjectLayoutTabs());
		}

		List<ObjectRelationship> objectRelationships = null;

		if (objectRelationshipsMap != null) {
			objectRelationships = objectRelationshipsMap.getOrDefault(
				objectDefinition.getObjectDefinitionId(),
				Collections.emptyList());
		}

		_objectRelationshipLocalService.
			registerObjectRelationshipsRelatedInfoCollectionProviders(
				objectDefinition, _objectDefinitionLocalService,
				objectRelationships);

		try {
			if (ArrayUtil.isNotEmpty(
					objectDefinition.getRootObjectDefinitionIds())) {

				_registerRootObjectLayoutTabScreenNavigationCategories(
					objectDefinition.getObjectDefinitionId());
			}
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}

		return serviceRegistrations;
	}

	private String _getScope(ObjectDefinition objectDefinition) {
		if (Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			return ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				toString();
		}

		return ExtendedObjectClassDefinition.Scope.GROUP.toString();
	}

	private String _getServiceRegistrationKey(
		ObjectDefinition objectDefinition,
		ObjectRelationship objectRelationship) {

		String serviceRegistrationKey = StringBundler.concat(
			"ROOT_OBJECT_LAYOUT_TAB#", objectDefinition.getCompanyId(),
			StringPool.POUND, objectDefinition.getObjectDefinitionId());

		if (objectRelationship == null) {
			return serviceRegistrationKey;
		}

		return StringBundler.concat(
			serviceRegistrationKey, StringPool.POUND,
			objectRelationship.getObjectRelationshipId());
	}

	private void _registerRootObjectLayoutTabScreenNavigationCategories(
			long objectDefinitionId)
		throws PortalException {

		Tree tree = _objectDefinitionTreeFactory.create(
			false, true, objectDefinitionId);

		Iterator<Node> iterator = tree.iterator();

		while (iterator.hasNext()) {
			Node node = iterator.next();

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					node.getPrimaryKey());

			List<ObjectRelationship> objectRelationships =
				_objectRelationshipLocalService.getObjectRelationships(
					objectDefinition.getObjectDefinitionId());

			for (ObjectRelationship objectRelationship : objectRelationships) {
				_registerRootObjectLayoutTabScreenNavigationCategory(
					objectDefinition, objectRelationship);
			}

			_registerRootObjectLayoutTabScreenNavigationCategory(
				objectDefinition, null);
		}
	}

	private void _registerRootObjectLayoutTabScreenNavigationCategory(
		ObjectDefinition objectDefinition,
		ObjectRelationship objectRelationship) {

		_serviceRegistrations.computeIfAbsent(
			_getServiceRegistrationKey(objectDefinition, objectRelationship),
			serviceRegistrationKey -> _bundleContext.registerService(
				new String[] {
					ScreenNavigationCategory.class.getName(),
					ScreenNavigationEntry.class.getName()
				},
				new ObjectLayoutTabScreenNavigationCategory(
					objectDefinition, null, objectRelationship),
				null));
	}

	private void _unregister(String serviceRegistrationKey) {
		ServiceRegistration<?> serviceRegistration =
			_serviceRegistrations.remove(serviceRegistrationKey);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private final AccountEntryLocalService _accountEntryLocalService;
	private final AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;
	private final AssetEntryLocalService _assetEntryLocalService;
	private final BundleContext _bundleContext;
	private final DynamicQueryBatchIndexingActionableFactory
		_dynamicQueryBatchIndexingActionableFactory;
	private final GroupLocalService _groupLocalService;
	private final KaleoDefinitionLocalService _kaleoDefinitionLocalService;
	private final ListTypeLocalService _listTypeLocalService;
	private final ObjectActionLocalService _objectActionLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;
	private final ObjectDefinitionTreeFactory _objectDefinitionTreeFactory;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryService _objectEntryService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectFolderLocalService _objectFolderLocalService;
	private final ObjectLayoutLocalService _objectLayoutLocalService;
	private final ObjectLayoutTabLocalService _objectLayoutTabLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;
	private final ObjectViewLocalService _objectViewLocalService;
	private final OrganizationLocalService _organizationLocalService;
	private final PLOEntryLocalService _ploEntryLocalService;
	private final Portal _portal;
	private final PortletLocalService _portletLocalService;
	private final ResourceActions _resourceActions;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final SearchLocalizationHelper _searchLocalizationHelper;
	private final Map<String, ServiceRegistration<?>> _serviceRegistrations =
		new ConcurrentHashMap<>();
	private final SharingModelResourcePermissionConfigurator
		_sharingModelResourcePermissionConfigurator;
	private final SystemEventLocalService _systemEventLocalService;
	private final TextEmbeddingDocumentContributor
		_textEmbeddingDocumentContributor;
	private final UserGroupRoleLocalService _userGroupRoleLocalService;
	private final UserLocalService _userLocalService;
	private final WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;
	private final ModelPreFilterContributor
		_workflowStatusModelPreFilterContributor;

	private static class ConsumerSupplier<T>
		implements Consumer<T>, Supplier<T> {

		@Override
		public void accept(T t) {
			_t = t;
		}

		@Override
		public T get() {
			return _t;
		}

		private T _t;

	}

}