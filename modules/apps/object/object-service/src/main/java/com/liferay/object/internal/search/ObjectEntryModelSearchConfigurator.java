/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search;

import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.text.DLFileEntryTextProvider;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.internal.search.spi.model.index.contributor.ObjectEntryModelDocumentContributor;
import com.liferay.object.internal.search.spi.model.result.contributor.ObjectEntryModelSummaryContributor;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionTable;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldTable;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectFolderTable;
import com.liferay.object.model.bag.ObjectFieldBag;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingDocumentContributor;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.registrar.ModelSearchConfigurator;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = ModelSearchConfigurator.class)
public class ObjectEntryModelSearchConfigurator
	implements ModelSearchConfigurator<ObjectEntry> {

	@Override
	public String getClassName() {
		return ObjectEntry.class.getName();
	}

	@Override
	public ModelIndexerWriterContributor<ObjectEntry>
		getModelIndexerWriterContributor() {

		return _modelIndexerWriterContributor;
	}

	@Override
	public ModelSummaryContributor getModelSummaryContributor() {
		return _modelSummaryContributor;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_modelIndexerWriterContributor =
			new ObjectEntryFullReindexModelIndexerWriterContributor();

		_modelDocumentContributorServiceRegistration =
			bundleContext.registerService(
				(Class<ModelDocumentContributor<?>>)
					(Class<?>)ModelDocumentContributor.class,
				new ObjectEntryModelDocumentContributor(
					_accountEntryOrganizationRelLocalService,
					_dlFileEntryLocalService, _dlFileEntryTextProvider,
					_objectEntryFolderLocalService,
					_objectFieldBusinessTypeRegistry,
					_textEmbeddingDocumentContributor),
				MapUtil.singletonDictionary(
					"indexer.class.name", ObjectEntry.class.getName()));

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, IndexerDocumentBuilder.class, "indexer.class.name");
	}

	@Deactivate
	protected void deactivate() {
		if (_modelDocumentContributorServiceRegistration != null) {
			_modelDocumentContributorServiceRegistration.unregister();
		}

		if (_serviceTrackerMap != null) {
			_serviceTrackerMap.close();
		}
	}

	private void _initObjectDefinition(ObjectDefinition objectDefinition) {
		Map<Long, ObjectFolder> objectFolders =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				ObjectEntryModelSearchConfigurator.class.getName() +
					"#ObjectFolder",
				count -> {
					Map<Long, ObjectFolder> localObjectFolders =
						new HashMap<>();

					for (ObjectFolder objectFolder :
							_objectFolderLocalService.
								<List<ObjectFolder>>dslQuery(
									DSLQueryFactoryUtil.select(
										ObjectFolderTable.INSTANCE
									).from(
										ObjectFolderTable.INSTANCE
									).innerJoinON(
										ObjectDefinitionTable.INSTANCE,
										ObjectFolderTable.INSTANCE.
											objectFolderId.eq(
												ObjectDefinitionTable.INSTANCE.
													objectFolderId)
									).where(
										ObjectDefinitionTable.INSTANCE.
											enableIndexSearch.eq(true)
									),
									false)) {

						localObjectFolders.put(
							objectFolder.getObjectFolderId(), objectFolder);
					}

					return localObjectFolders;
				});

		if (objectFolders != null) {
			objectDefinition.setObjectFolder(
				objectFolders.get(objectDefinition.getObjectFolderId()));
		}

		Map<Long, List<ObjectField>> objectFieldsMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				ObjectEntryModelSearchConfigurator.class.getName() +
					"#ObjectFields",
				count -> {
					Map<Long, List<ObjectField>> localObjectFieldsMap =
						new HashMap<>();

					for (ObjectField objectField :
							_objectFieldLocalService.
								<List<ObjectField>>dslQuery(
									DSLQueryFactoryUtil.select(
										ObjectFieldTable.INSTANCE
									).from(
										ObjectFieldTable.INSTANCE
									),
									false)) {

						List<ObjectField> objectFields =
							localObjectFieldsMap.computeIfAbsent(
								objectField.getObjectDefinitionId(),
								key -> new ArrayList<>());

						objectFields.add(objectField);
					}

					return localObjectFieldsMap;
				});

		if (objectFieldsMap != null) {
			objectDefinition.setObjectFieldBag(
				new ObjectFieldBag(
					objectDefinition.isModifiableAndSystem(),
					objectFieldsMap.getOrDefault(
						objectDefinition.getObjectDefinitionId(),
						Collections.emptyList())));
		}
	}

	private Map<Long, ObjectDefinition> _loadObjectDefinitionsMap() {
		Map<Long, ObjectDefinition> objectDefinitionsMap = new HashMap<>();

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.<List<ObjectDefinition>>dslQuery(
					DSLQueryFactoryUtil.select(
						ObjectDefinitionTable.INSTANCE
					).from(
						ObjectDefinitionTable.INSTANCE
					).where(
						ObjectDefinitionTable.INSTANCE.enableIndexSearch.eq(
							true)
					),
					false)) {

			_initObjectDefinition(objectDefinition);

			objectDefinitionsMap.put(
				objectDefinition.getObjectDefinitionId(), objectDefinition);
		}

		return objectDefinitionsMap;
	}

	@Reference
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileEntryTextProvider _dlFileEntryTextProvider;

	private ServiceRegistration<ModelDocumentContributor<?>>
		_modelDocumentContributorServiceRegistration;
	private ModelIndexerWriterContributor<ObjectEntry>
		_modelIndexerWriterContributor;
	private final ModelSummaryContributor _modelSummaryContributor =
		new ObjectEntryModelSummaryContributor();

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	private ServiceTrackerMap<String, IndexerDocumentBuilder>
		_serviceTrackerMap;

	@Reference
	private TextEmbeddingDocumentContributor _textEmbeddingDocumentContributor;

	private class ObjectEntryFullReindexModelIndexerWriterContributor
		extends ModelIndexerWriterContributor<ObjectEntry> {

		@Override
		public void customize(
			IndexableActionableDynamicQuery indexableActionableDynamicQuery,
			IndexerDocumentBuilder indexerDocumentBuilder) {

			AtomicReference<Map<Long, ObjectDefinition>>
				objectDefinitionsMapReference = new AtomicReference<>();

			indexableActionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> dynamicQuery.add(
					RestrictionsFactoryUtil.eqProperty(
						"objectEntryId", "headObjectEntryId")));
			indexableActionableDynamicQuery.setPerformActionMethod(
				(ObjectEntry objectEntry) -> {
					Map<Long, ObjectDefinition> objectDefinitionsMap =
						objectDefinitionsMapReference.get();

					if (objectDefinitionsMap == null) {
						objectDefinitionsMap = _loadObjectDefinitionsMap();

						objectDefinitionsMapReference.set(objectDefinitionsMap);
					}

					ObjectDefinition objectDefinition =
						objectDefinitionsMap.get(
							objectEntry.getObjectDefinitionId());

					if (objectDefinition == null) {
						return null;
					}

					objectEntry.setObjectDefinition(objectDefinition);

					IndexerDocumentBuilder
						objectDefinitionIndexerDocumentBuilder =
							_serviceTrackerMap.getService(
								objectDefinition.getClassName());

					if (objectDefinitionIndexerDocumentBuilder == null) {
						return indexerDocumentBuilder.getDocument(objectEntry);
					}

					return objectDefinitionIndexerDocumentBuilder.getDocument(
						objectEntry);
				});
		}

		@Override
		public IndexerWriterMode getIndexerWriterMode(ObjectEntry objectEntry) {
			if (!objectEntry.isHead()) {
				return IndexerWriterMode.DELETE;
			}

			if (!ReindexCacheThreadLocal.isFullMode()) {
				return IndexerWriterMode.SKIP;
			}

			return IndexerWriterMode.UPDATE;
		}

		@Override
		public boolean shouldRun(long companyId) {
			return ReindexCacheThreadLocal.isFullMode();
		}

		private ObjectEntryFullReindexModelIndexerWriterContributor() {
			super(_objectEntryLocalService::getIndexableActionableDynamicQuery);
		}

	}

}