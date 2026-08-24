/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.index.contributor;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionTable;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldTable;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectFolderTable;
import com.liferay.object.model.bag.ObjectFieldBag;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectEntryModelIndexerWriterContributor
	extends ModelIndexerWriterContributor<ObjectEntry> {

	public ObjectEntryModelIndexerWriterContributor(
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFolderLocalService objectFolderLocalService) {

		super(objectEntryLocalService::getIndexableActionableDynamicQuery);

		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectFolderLocalService = objectFolderLocalService;

		_companyId = objectDefinition.getCompanyId();
		_objectDefinitionId = objectDefinition.getObjectDefinitionId();
	}

	@Override
	public void customize(
		IndexableActionableDynamicQuery indexableActionableDynamicQuery,
		IndexerDocumentBuilder indexerDocumentBuilder) {

		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property objectDefinitionIdProperty =
					PropertyFactoryUtil.forName("objectDefinitionId");

				dynamicQuery.add(
					objectDefinitionIdProperty.eq(_objectDefinitionId));

				dynamicQuery.add(
					RestrictionsFactoryUtil.eqProperty(
						"objectEntryId", "headObjectEntryId"));
			});
		indexableActionableDynamicQuery.setCacheKeySuffix(
			String.valueOf(_objectDefinitionId));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_objectDefinitionId);

		_initObjectDefinition(objectDefinition);

		indexableActionableDynamicQuery.setPerformActionMethod(
			(ObjectEntry objectEntry) -> {
				objectEntry.setObjectDefinition(objectDefinition);

				return indexerDocumentBuilder.getDocument(objectEntry);
			});
	}

	@Override
	public IndexerWriterMode getIndexerWriterMode(ObjectEntry objectEntry) {
		if (objectEntry.isHead()) {
			return IndexerWriterMode.UPDATE;
		}

		return IndexerWriterMode.DELETE;
	}

	@Override
	public boolean shouldRun(long companyId) {
		if (ReindexCacheThreadLocal.isFullMode()) {
			return false;
		}

		if (_companyId == companyId) {
			return true;
		}

		return false;
	}

	private void _initObjectDefinition(ObjectDefinition objectDefinition) {
		Map<Long, ObjectFolder> objectFolders =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				ObjectEntryModelIndexerWriterContributor.class.getName() +
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
				ObjectEntryModelIndexerWriterContributor.class.getName() +
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

	private final long _companyId;
	private final Long _objectDefinitionId;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectFolderLocalService _objectFolderLocalService;

}