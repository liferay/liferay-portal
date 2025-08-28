/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.graphql.mutation.v1_0;

import com.liferay.object.admin.rest.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.dto.v1_0.ObjectLayout;
import com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.dto.v1_0.ObjectView;
import com.liferay.object.admin.rest.resource.v1_0.ObjectActionResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFieldResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFolderResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectLayoutResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectRelationshipResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectValidationRuleResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectViewResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setObjectActionResourceComponentServiceObjects(
		ComponentServiceObjects<ObjectActionResource>
			objectActionResourceComponentServiceObjects) {

		_objectActionResourceComponentServiceObjects =
			objectActionResourceComponentServiceObjects;
	}

	public static void setObjectDefinitionResourceComponentServiceObjects(
		ComponentServiceObjects<ObjectDefinitionResource>
			objectDefinitionResourceComponentServiceObjects) {

		_objectDefinitionResourceComponentServiceObjects =
			objectDefinitionResourceComponentServiceObjects;
	}

	public static void setObjectFieldResourceComponentServiceObjects(
		ComponentServiceObjects<ObjectFieldResource>
			objectFieldResourceComponentServiceObjects) {

		_objectFieldResourceComponentServiceObjects =
			objectFieldResourceComponentServiceObjects;
	}

	public static void setObjectFolderResourceComponentServiceObjects(
		ComponentServiceObjects<ObjectFolderResource>
			objectFolderResourceComponentServiceObjects) {

		_objectFolderResourceComponentServiceObjects =
			objectFolderResourceComponentServiceObjects;
	}

	public static void setObjectLayoutResourceComponentServiceObjects(
		ComponentServiceObjects<ObjectLayoutResource>
			objectLayoutResourceComponentServiceObjects) {

		_objectLayoutResourceComponentServiceObjects =
			objectLayoutResourceComponentServiceObjects;
	}

	public static void setObjectRelationshipResourceComponentServiceObjects(
		ComponentServiceObjects<ObjectRelationshipResource>
			objectRelationshipResourceComponentServiceObjects) {

		_objectRelationshipResourceComponentServiceObjects =
			objectRelationshipResourceComponentServiceObjects;
	}

	public static void setObjectValidationRuleResourceComponentServiceObjects(
		ComponentServiceObjects<ObjectValidationRuleResource>
			objectValidationRuleResourceComponentServiceObjects) {

		_objectValidationRuleResourceComponentServiceObjects =
			objectValidationRuleResourceComponentServiceObjects;
	}

	public static void setObjectViewResourceComponentServiceObjects(
		ComponentServiceObjects<ObjectViewResource>
			objectViewResourceComponentServiceObjects) {

		_objectViewResourceComponentServiceObjects =
			objectViewResourceComponentServiceObjects;
	}

	@GraphQLField
	public boolean deleteObjectAction(
			@GraphQLName("objectActionId") Long objectActionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource -> objectActionResource.deleteObjectAction(
				objectActionId));

		return true;
	}

	@GraphQLField
	public Response deleteObjectActionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource ->
				objectActionResource.deleteObjectActionBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ObjectAction patchObjectAction(
			@GraphQLName("objectActionId") Long objectActionId,
			@GraphQLName("objectAction") ObjectAction objectAction)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource -> objectActionResource.patchObjectAction(
				objectActionId, objectAction));
	}

	@GraphQLField
	public ObjectAction
			createObjectDefinitionByExternalReferenceCodeObjectAction(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("objectAction") ObjectAction objectAction)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource ->
				objectActionResource.
					postObjectDefinitionByExternalReferenceCodeObjectAction(
						externalReferenceCode, objectAction));
	}

	@GraphQLField
	public ObjectAction createObjectDefinitionObjectAction(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("objectAction") ObjectAction objectAction)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource ->
				objectActionResource.postObjectDefinitionObjectAction(
					objectDefinitionId, objectAction));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectActionBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource ->
				objectActionResource.postObjectDefinitionObjectActionBatch(
					objectDefinitionId, callbackURL, object));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectActionsPageExportBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource ->
				objectActionResource.
					postObjectDefinitionObjectActionsPageExportBatch(
						objectDefinitionId, search,
						_sortsBiFunction.apply(
							objectActionResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ObjectAction updateObjectAction(
			@GraphQLName("objectActionId") Long objectActionId,
			@GraphQLName("objectAction") ObjectAction objectAction)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource -> objectActionResource.putObjectAction(
				objectActionId, objectAction));
	}

	@GraphQLField
	public Response updateObjectActionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource -> objectActionResource.putObjectActionBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteObjectDefinition(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.deleteObjectDefinition(
					objectDefinitionId));

		return true;
	}

	@GraphQLField
	public Response deleteObjectDefinitionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.deleteObjectDefinitionBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ObjectDefinition patchObjectDefinition(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("objectDefinition") ObjectDefinition objectDefinition)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.patchObjectDefinition(
					objectDefinitionId, objectDefinition));
	}

	@GraphQLField
	public ObjectDefinition createObjectDefinition(
			@GraphQLName("accumulateOnValidation") Boolean
				accumulateOnValidation,
			@GraphQLName("objectDefinition") ObjectDefinition objectDefinition)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.postObjectDefinition(
					accumulateOnValidation, objectDefinition));
	}

	@GraphQLField
	public Response createObjectDefinitionBatch(
			@GraphQLName("accumulateOnValidation") Boolean
				accumulateOnValidation,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.postObjectDefinitionBatch(
					accumulateOnValidation, callbackURL, object));
	}

	@GraphQLField
	public ObjectDefinition createObjectDefinitionPublish(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.postObjectDefinitionPublish(
					objectDefinitionId));
	}

	@GraphQLField
	public Response createObjectDefinitionsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.postObjectDefinitionsPageExportBatch(
					search,
					_filterBiFunction.apply(
						objectDefinitionResource, filterString),
					_sortsBiFunction.apply(
						objectDefinitionResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ObjectDefinition updateObjectDefinition(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("objectDefinition") ObjectDefinition objectDefinition)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.putObjectDefinition(
					objectDefinitionId, objectDefinition));
	}

	@GraphQLField
	public Response updateObjectDefinitionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.putObjectDefinitionBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ObjectDefinition updateObjectDefinitionByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("accumulateOnValidation") Boolean
				accumulateOnValidation,
			@GraphQLName("objectDefinition") ObjectDefinition objectDefinition)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.
					putObjectDefinitionByExternalReferenceCode(
						externalReferenceCode, accumulateOnValidation,
						objectDefinition));
	}

	@GraphQLField
	public boolean deleteObjectField(
			@GraphQLName("objectFieldId") Long objectFieldId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource -> objectFieldResource.deleteObjectField(
				objectFieldId));

		return true;
	}

	@GraphQLField
	public Response deleteObjectFieldBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource -> objectFieldResource.deleteObjectFieldBatch(
				callbackURL, object));
	}

	@GraphQLField
	public ObjectField patchObjectField(
			@GraphQLName("objectFieldId") Long objectFieldId,
			@GraphQLName("objectField") ObjectField objectField)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource -> objectFieldResource.patchObjectField(
				objectFieldId, objectField));
	}

	@GraphQLField
	public ObjectField createObjectDefinitionByExternalReferenceCodeObjectField(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("objectField") ObjectField objectField)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource ->
				objectFieldResource.
					postObjectDefinitionByExternalReferenceCodeObjectField(
						externalReferenceCode, objectField));
	}

	@GraphQLField
	public ObjectField createObjectDefinitionObjectField(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("objectField") ObjectField objectField)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource ->
				objectFieldResource.postObjectDefinitionObjectField(
					objectDefinitionId, objectField));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectFieldBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource ->
				objectFieldResource.postObjectDefinitionObjectFieldBatch(
					objectDefinitionId, callbackURL, object));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectFieldsPageExportBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource ->
				objectFieldResource.
					postObjectDefinitionObjectFieldsPageExportBatch(
						objectDefinitionId, search,
						_filterBiFunction.apply(
							objectFieldResource, filterString),
						_sortsBiFunction.apply(
							objectFieldResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ObjectField updateObjectField(
			@GraphQLName("objectFieldId") Long objectFieldId,
			@GraphQLName("objectField") ObjectField objectField)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource -> objectFieldResource.putObjectField(
				objectFieldId, objectField));
	}

	@GraphQLField
	public Response updateObjectFieldBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource -> objectFieldResource.putObjectFieldBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteObjectFolder(
			@GraphQLName("objectFolderId") Long objectFolderId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource -> objectFolderResource.deleteObjectFolder(
				objectFolderId));

		return true;
	}

	@GraphQLField
	public Response deleteObjectFolderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource ->
				objectFolderResource.deleteObjectFolderBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ObjectFolder patchObjectFolder(
			@GraphQLName("objectFolderId") Long objectFolderId,
			@GraphQLName("objectFolder") ObjectFolder objectFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource -> objectFolderResource.patchObjectFolder(
				objectFolderId, objectFolder));
	}

	@GraphQLField
	public ObjectFolder createObjectFolder(
			@GraphQLName("objectFolder") ObjectFolder objectFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource -> objectFolderResource.postObjectFolder(
				objectFolder));
	}

	@GraphQLField
	public Response createObjectFolderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource -> objectFolderResource.postObjectFolderBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response createObjectFoldersPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource ->
				objectFolderResource.postObjectFoldersPageExportBatch(
					search, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ObjectFolder updateObjectFolder(
			@GraphQLName("objectFolderId") Long objectFolderId,
			@GraphQLName("objectFolder") ObjectFolder objectFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource -> objectFolderResource.putObjectFolder(
				objectFolderId, objectFolder));
	}

	@GraphQLField
	public Response updateObjectFolderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource -> objectFolderResource.putObjectFolderBatch(
				callbackURL, object));
	}

	@GraphQLField
	public ObjectFolder updateObjectFolderByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("objectFolder") ObjectFolder objectFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource ->
				objectFolderResource.putObjectFolderByExternalReferenceCode(
					externalReferenceCode, objectFolder));
	}

	@GraphQLField
	public boolean deleteObjectLayout(
			@GraphQLName("objectLayoutId") Long objectLayoutId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectLayoutResource -> objectLayoutResource.deleteObjectLayout(
				objectLayoutId));

		return true;
	}

	@GraphQLField
	public Response deleteObjectLayoutBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectLayoutResource ->
				objectLayoutResource.deleteObjectLayoutBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ObjectLayout
			createObjectDefinitionByExternalReferenceCodeObjectLayout(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("objectLayout") ObjectLayout objectLayout)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectLayoutResource ->
				objectLayoutResource.
					postObjectDefinitionByExternalReferenceCodeObjectLayout(
						externalReferenceCode, objectLayout));
	}

	@GraphQLField
	public ObjectLayout createObjectDefinitionObjectLayout(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("objectLayout") ObjectLayout objectLayout)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectLayoutResource ->
				objectLayoutResource.postObjectDefinitionObjectLayout(
					objectDefinitionId, objectLayout));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectLayoutBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectLayoutResource ->
				objectLayoutResource.postObjectDefinitionObjectLayoutBatch(
					objectDefinitionId, callbackURL, object));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectLayoutsPageExportBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectLayoutResource ->
				objectLayoutResource.
					postObjectDefinitionObjectLayoutsPageExportBatch(
						objectDefinitionId, search,
						_sortsBiFunction.apply(
							objectLayoutResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ObjectLayout updateObjectLayout(
			@GraphQLName("objectLayoutId") Long objectLayoutId,
			@GraphQLName("objectLayout") ObjectLayout objectLayout)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectLayoutResource -> objectLayoutResource.putObjectLayout(
				objectLayoutId, objectLayout));
	}

	@GraphQLField
	public Response updateObjectLayoutBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectLayoutResource -> objectLayoutResource.putObjectLayoutBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteObjectRelationship(
			@GraphQLName("objectRelationshipId") Long objectRelationshipId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource ->
				objectRelationshipResource.deleteObjectRelationship(
					objectRelationshipId));

		return true;
	}

	@GraphQLField
	public Response deleteObjectRelationshipBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource ->
				objectRelationshipResource.deleteObjectRelationshipBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ObjectRelationship
			createObjectDefinitionByExternalReferenceCodeObjectRelationship(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("objectRelationship") ObjectRelationship
					objectRelationship)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource ->
				objectRelationshipResource.
					postObjectDefinitionByExternalReferenceCodeObjectRelationship(
						externalReferenceCode, objectRelationship));
	}

	@GraphQLField
	public ObjectRelationship createObjectDefinitionObjectRelationship(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("objectRelationship") ObjectRelationship
				objectRelationship)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource ->
				objectRelationshipResource.
					postObjectDefinitionObjectRelationship(
						objectDefinitionId, objectRelationship));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectRelationshipBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource ->
				objectRelationshipResource.
					postObjectDefinitionObjectRelationshipBatch(
						objectDefinitionId, callbackURL, object));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectRelationshipsPageExportBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource ->
				objectRelationshipResource.
					postObjectDefinitionObjectRelationshipsPageExportBatch(
						objectDefinitionId, search,
						_filterBiFunction.apply(
							objectRelationshipResource, filterString),
						_sortsBiFunction.apply(
							objectRelationshipResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ObjectRelationship updateObjectRelationship(
			@GraphQLName("objectRelationshipId") Long objectRelationshipId,
			@GraphQLName("objectRelationship") ObjectRelationship
				objectRelationship)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource ->
				objectRelationshipResource.putObjectRelationship(
					objectRelationshipId, objectRelationship));
	}

	@GraphQLField
	public Response updateObjectRelationshipBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource ->
				objectRelationshipResource.putObjectRelationshipBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ObjectRelationship updateObjectRelationshipByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("objectRelationship") ObjectRelationship
				objectRelationship)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource ->
				objectRelationshipResource.
					putObjectRelationshipByExternalReferenceCode(
						externalReferenceCode, objectRelationship));
	}

	@GraphQLField
	public boolean deleteObjectValidationRule(
			@GraphQLName("objectValidationRuleId") Long objectValidationRuleId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource ->
				objectValidationRuleResource.deleteObjectValidationRule(
					objectValidationRuleId));

		return true;
	}

	@GraphQLField
	public Response deleteObjectValidationRuleBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource ->
				objectValidationRuleResource.deleteObjectValidationRuleBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ObjectValidationRule patchObjectValidationRule(
			@GraphQLName("objectValidationRuleId") Long objectValidationRuleId,
			@GraphQLName("objectValidationRule") ObjectValidationRule
				objectValidationRule)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource ->
				objectValidationRuleResource.patchObjectValidationRule(
					objectValidationRuleId, objectValidationRule));
	}

	@GraphQLField
	public ObjectValidationRule
			createObjectDefinitionByExternalReferenceCodeObjectValidationRule(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("objectValidationRule") ObjectValidationRule
					objectValidationRule)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource ->
				objectValidationRuleResource.
					postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
						externalReferenceCode, objectValidationRule));
	}

	@GraphQLField
	public ObjectValidationRule createObjectDefinitionObjectValidationRule(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("objectValidationRule") ObjectValidationRule
				objectValidationRule)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource ->
				objectValidationRuleResource.
					postObjectDefinitionObjectValidationRule(
						objectDefinitionId, objectValidationRule));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectValidationRuleBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource ->
				objectValidationRuleResource.
					postObjectDefinitionObjectValidationRuleBatch(
						objectDefinitionId, callbackURL, object));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectValidationRulesPageExportBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource ->
				objectValidationRuleResource.
					postObjectDefinitionObjectValidationRulesPageExportBatch(
						objectDefinitionId, search,
						_sortsBiFunction.apply(
							objectValidationRuleResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ObjectValidationRule updateObjectValidationRule(
			@GraphQLName("objectValidationRuleId") Long objectValidationRuleId,
			@GraphQLName("objectValidationRule") ObjectValidationRule
				objectValidationRule)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource ->
				objectValidationRuleResource.putObjectValidationRule(
					objectValidationRuleId, objectValidationRule));
	}

	@GraphQLField
	public Response updateObjectValidationRuleBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource ->
				objectValidationRuleResource.putObjectValidationRuleBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteObjectView(
			@GraphQLName("objectViewId") Long objectViewId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource -> objectViewResource.deleteObjectView(
				objectViewId));

		return true;
	}

	@GraphQLField
	public Response deleteObjectViewBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource -> objectViewResource.deleteObjectViewBatch(
				callbackURL, object));
	}

	@GraphQLField
	public ObjectView createObjectDefinitionByExternalReferenceCodeObjectView(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("objectView") ObjectView objectView)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource ->
				objectViewResource.
					postObjectDefinitionByExternalReferenceCodeObjectView(
						externalReferenceCode, objectView));
	}

	@GraphQLField
	public ObjectView createObjectDefinitionObjectView(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("objectView") ObjectView objectView)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource ->
				objectViewResource.postObjectDefinitionObjectView(
					objectDefinitionId, objectView));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectViewBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource ->
				objectViewResource.postObjectDefinitionObjectViewBatch(
					objectDefinitionId, callbackURL, object));
	}

	@GraphQLField
	public Response createObjectDefinitionObjectViewsPageExportBatch(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource ->
				objectViewResource.
					postObjectDefinitionObjectViewsPageExportBatch(
						objectDefinitionId, search,
						_sortsBiFunction.apply(objectViewResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ObjectView createObjectViewCopy(
			@GraphQLName("objectViewId") Long objectViewId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource -> objectViewResource.postObjectViewCopy(
				objectViewId));
	}

	@GraphQLField
	public ObjectView updateObjectView(
			@GraphQLName("objectViewId") Long objectViewId,
			@GraphQLName("objectView") ObjectView objectView)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource -> objectViewResource.putObjectView(
				objectViewId, objectView));
	}

	@GraphQLField
	public Response updateObjectViewBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource -> objectViewResource.putObjectViewBatch(
				callbackURL, object));
	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			ObjectActionResource objectActionResource)
		throws Exception {

		objectActionResource.setContextAcceptLanguage(_acceptLanguage);
		objectActionResource.setContextCompany(_company);
		objectActionResource.setContextHttpServletRequest(_httpServletRequest);
		objectActionResource.setContextHttpServletResponse(
			_httpServletResponse);
		objectActionResource.setContextUriInfo(_uriInfo);
		objectActionResource.setContextUser(_user);
		objectActionResource.setGroupLocalService(_groupLocalService);
		objectActionResource.setRoleLocalService(_roleLocalService);

		objectActionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		objectActionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ObjectDefinitionResource objectDefinitionResource)
		throws Exception {

		objectDefinitionResource.setContextAcceptLanguage(_acceptLanguage);
		objectDefinitionResource.setContextCompany(_company);
		objectDefinitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		objectDefinitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		objectDefinitionResource.setContextUriInfo(_uriInfo);
		objectDefinitionResource.setContextUser(_user);
		objectDefinitionResource.setGroupLocalService(_groupLocalService);
		objectDefinitionResource.setRoleLocalService(_roleLocalService);

		objectDefinitionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		objectDefinitionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ObjectFieldResource objectFieldResource)
		throws Exception {

		objectFieldResource.setContextAcceptLanguage(_acceptLanguage);
		objectFieldResource.setContextCompany(_company);
		objectFieldResource.setContextHttpServletRequest(_httpServletRequest);
		objectFieldResource.setContextHttpServletResponse(_httpServletResponse);
		objectFieldResource.setContextUriInfo(_uriInfo);
		objectFieldResource.setContextUser(_user);
		objectFieldResource.setGroupLocalService(_groupLocalService);
		objectFieldResource.setRoleLocalService(_roleLocalService);

		objectFieldResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		objectFieldResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ObjectFolderResource objectFolderResource)
		throws Exception {

		objectFolderResource.setContextAcceptLanguage(_acceptLanguage);
		objectFolderResource.setContextCompany(_company);
		objectFolderResource.setContextHttpServletRequest(_httpServletRequest);
		objectFolderResource.setContextHttpServletResponse(
			_httpServletResponse);
		objectFolderResource.setContextUriInfo(_uriInfo);
		objectFolderResource.setContextUser(_user);
		objectFolderResource.setGroupLocalService(_groupLocalService);
		objectFolderResource.setRoleLocalService(_roleLocalService);

		objectFolderResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		objectFolderResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ObjectLayoutResource objectLayoutResource)
		throws Exception {

		objectLayoutResource.setContextAcceptLanguage(_acceptLanguage);
		objectLayoutResource.setContextCompany(_company);
		objectLayoutResource.setContextHttpServletRequest(_httpServletRequest);
		objectLayoutResource.setContextHttpServletResponse(
			_httpServletResponse);
		objectLayoutResource.setContextUriInfo(_uriInfo);
		objectLayoutResource.setContextUser(_user);
		objectLayoutResource.setGroupLocalService(_groupLocalService);
		objectLayoutResource.setRoleLocalService(_roleLocalService);

		objectLayoutResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		objectLayoutResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ObjectRelationshipResource objectRelationshipResource)
		throws Exception {

		objectRelationshipResource.setContextAcceptLanguage(_acceptLanguage);
		objectRelationshipResource.setContextCompany(_company);
		objectRelationshipResource.setContextHttpServletRequest(
			_httpServletRequest);
		objectRelationshipResource.setContextHttpServletResponse(
			_httpServletResponse);
		objectRelationshipResource.setContextUriInfo(_uriInfo);
		objectRelationshipResource.setContextUser(_user);
		objectRelationshipResource.setGroupLocalService(_groupLocalService);
		objectRelationshipResource.setRoleLocalService(_roleLocalService);

		objectRelationshipResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		objectRelationshipResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ObjectValidationRuleResource objectValidationRuleResource)
		throws Exception {

		objectValidationRuleResource.setContextAcceptLanguage(_acceptLanguage);
		objectValidationRuleResource.setContextCompany(_company);
		objectValidationRuleResource.setContextHttpServletRequest(
			_httpServletRequest);
		objectValidationRuleResource.setContextHttpServletResponse(
			_httpServletResponse);
		objectValidationRuleResource.setContextUriInfo(_uriInfo);
		objectValidationRuleResource.setContextUser(_user);
		objectValidationRuleResource.setGroupLocalService(_groupLocalService);
		objectValidationRuleResource.setRoleLocalService(_roleLocalService);

		objectValidationRuleResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		objectValidationRuleResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(ObjectViewResource objectViewResource)
		throws Exception {

		objectViewResource.setContextAcceptLanguage(_acceptLanguage);
		objectViewResource.setContextCompany(_company);
		objectViewResource.setContextHttpServletRequest(_httpServletRequest);
		objectViewResource.setContextHttpServletResponse(_httpServletResponse);
		objectViewResource.setContextUriInfo(_uriInfo);
		objectViewResource.setContextUser(_user);
		objectViewResource.setGroupLocalService(_groupLocalService);
		objectViewResource.setRoleLocalService(_roleLocalService);

		objectViewResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		objectViewResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<ObjectActionResource>
		_objectActionResourceComponentServiceObjects;
	private static ComponentServiceObjects<ObjectDefinitionResource>
		_objectDefinitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<ObjectFieldResource>
		_objectFieldResourceComponentServiceObjects;
	private static ComponentServiceObjects<ObjectFolderResource>
		_objectFolderResourceComponentServiceObjects;
	private static ComponentServiceObjects<ObjectLayoutResource>
		_objectLayoutResourceComponentServiceObjects;
	private static ComponentServiceObjects<ObjectRelationshipResource>
		_objectRelationshipResourceComponentServiceObjects;
	private static ComponentServiceObjects<ObjectValidationRuleResource>
		_objectValidationRuleResourceComponentServiceObjects;
	private static ComponentServiceObjects<ObjectViewResource>
		_objectViewResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}