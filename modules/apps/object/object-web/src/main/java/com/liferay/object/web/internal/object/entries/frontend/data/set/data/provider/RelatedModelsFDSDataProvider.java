/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.frontend.data.set.data.provider;

import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.object.entries.frontend.data.set.data.model.RelatedModel;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.web.internal.object.entries.constants.ObjectEntriesFDSNames;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "fds.data.provider.key=" + ObjectEntriesFDSNames.RELATED_MODELS,
	service = FDSDataProvider.class
)
public class RelatedModelsFDSDataProvider
	implements FDSDataProvider<RelatedModel> {

	@Override
	public List<RelatedModel> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long objectRelationshipId = ParamUtil.getLong(
			httpServletRequest, "objectRelationshipId");

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		ObjectRelatedModelsProvider objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				objectDefinition.getClassName(),
				objectDefinition.getCompanyId(), objectRelationship.getType());

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		long groupId = objectScopeProvider.getGroupId(httpServletRequest);

		if (!objectScopeProvider.isValidGroupId(groupId)) {
			groupId = 0;
		}

		long objectEntryId = ParamUtil.getLong(
			httpServletRequest, "objectEntryId");

		return TransformUtil.transform(
			(List<ObjectEntry>)objectRelatedModelsProvider.getRelatedModels(
				groupId, objectRelationshipId, objectEntryId,
				fdsKeywords.getKeywords(), fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition()),
			objectEntry -> new RelatedModel(
				objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
				objectEntry.getTitleValue(), false));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long objectRelationshipId = ParamUtil.getLong(
			httpServletRequest, "objectRelationshipId");

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		ObjectRelatedModelsProvider objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				objectDefinition.getClassName(),
				objectDefinition.getCompanyId(), objectRelationship.getType());

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		long objectEntryId = ParamUtil.getLong(
			httpServletRequest, "objectEntryId");

		return objectRelatedModelsProvider.getRelatedModelsCount(
			objectScopeProvider.getGroupId(httpServletRequest),
			objectRelationshipId, objectEntryId, fdsKeywords.getKeywords());
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

}