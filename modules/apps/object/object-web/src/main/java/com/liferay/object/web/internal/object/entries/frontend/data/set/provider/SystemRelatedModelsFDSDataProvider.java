/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.frontend.data.set.provider;

import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.object.entries.frontend.data.set.data.model.RelatedModel;
import com.liferay.object.entry.util.ObjectEntryDTOConverterUtil;
import com.liferay.object.entry.util.ObjectEntryValuesUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.web.internal.object.entries.constants.ObjectEntriesFDSNames;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + ObjectEntriesFDSNames.SYSTEM_RELATED_MODELS,
	service = FDSDataProvider.class
)
public class SystemRelatedModelsFDSDataProvider
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

		long objectEntryId = ParamUtil.getLong(
			httpServletRequest, "objectEntryId");

		return TransformUtil.transform(
			(List<BaseModel<?>>)objectRelatedModelsProvider.getRelatedModels(
				objectScopeProvider.getGroupId(httpServletRequest),
				objectRelationshipId, objectEntryId, fdsKeywords.getKeywords(),
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition()),
			relatedModel -> {
				ObjectField titleObjectField =
					_objectFieldLocalService.fetchObjectField(
						objectDefinition.getTitleObjectFieldId());

				if (titleObjectField == null) {
					titleObjectField =
						_objectFieldLocalService.fetchObjectField(
							objectDefinition.getObjectDefinitionId(), "id");
				}

				User user = _userLocalService.getUser(
					PrincipalThreadLocal.getUserId());

				Map<String, Object> values =
					ObjectEntryDTOConverterUtil.toValues(
						relatedModel, _dtoConverterRegistry,
						objectDefinition.getName(),
						_systemObjectDefinitionManagerRegistry, user);

				Object titleFieldValue =
					ObjectEntryValuesUtil.getTitleFieldValue(
						titleObjectField.getBusinessType(),
						relatedModel.getModelAttributes(), titleObjectField,
						user, values);

				if (titleFieldValue == null) {
					titleFieldValue = StringPool.BLANK;
				}

				return new RelatedModel(
					objectDefinition.getClassName(),
					GetterUtil.getLong(values.get("id")),
					titleFieldValue.toString(), true);
			});
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
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private UserLocalService _userLocalService;

}