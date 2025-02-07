/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.graphql.query.v1_0;

import com.liferay.object.admin.rest.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFieldSetting;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.dto.v1_0.ObjectLayout;
import com.liferay.object.admin.rest.dto.v1_0.ObjectLayoutTab;
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
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Query {

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

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectAction(objectActionId: ___){actions, active, conditionExpression, dateCreated, dateModified, description, errorMessage, externalReferenceCode, id, label, name, objectActionExecutorKey, objectActionTriggerKey, parameters, status, system}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectAction objectAction(
			@GraphQLName("objectActionId") Long objectActionId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource -> objectActionResource.getObjectAction(
				objectActionId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionByExternalReferenceCodeObjectActions(externalReferenceCode: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectActionPage
			objectDefinitionByExternalReferenceCodeObjectActions(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource -> new ObjectActionPage(
				objectActionResource.
					getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
						externalReferenceCode, search,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							objectActionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionObjectActions(objectDefinitionId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectActionPage objectDefinitionObjectActions(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectActionResource -> new ObjectActionPage(
				objectActionResource.getObjectDefinitionObjectActionsPage(
					objectDefinitionId, search, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						objectActionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitions(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectDefinitionPage objectDefinitions(
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource -> new ObjectDefinitionPage(
				objectDefinitionResource.getObjectDefinitionsPage(
					search,
					_aggregationBiFunction.apply(
						objectDefinitionResource, aggregations),
					_filterBiFunction.apply(
						objectDefinitionResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						objectDefinitionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionByExternalReferenceCode(externalReferenceCode: ___){accountEntryRestricted, accountEntryRestrictedObjectFieldName, actions, active, className, dateCreated, dateModified, defaultLanguageId, enableCategorization, enableComments, enableFriendlyURLCustomization, enableIndexSearch, enableLocalization, enableObjectEntryDraft, enableObjectEntryHistory, externalReferenceCode, id, label, modifiable, name, objectActions, objectDefinitionSettings, objectFields, objectFolderExternalReferenceCode, objectLayouts, objectRelationships, objectValidationRules, objectViews, panelAppOrder, panelCategoryKey, parameterRequired, pluralLabel, portlet, restContextPath, rootObjectDefinitionExternalReferenceCode, scope, status, storageType, system, titleObjectFieldName}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectDefinition objectDefinitionByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.
					getObjectDefinitionByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinition(objectDefinitionId: ___){accountEntryRestricted, accountEntryRestrictedObjectFieldName, actions, active, className, dateCreated, dateModified, defaultLanguageId, enableCategorization, enableComments, enableFriendlyURLCustomization, enableIndexSearch, enableLocalization, enableObjectEntryDraft, enableObjectEntryHistory, externalReferenceCode, id, label, modifiable, name, objectActions, objectDefinitionSettings, objectFields, objectFolderExternalReferenceCode, objectLayouts, objectRelationships, objectValidationRules, objectViews, panelAppOrder, panelCategoryKey, parameterRequired, pluralLabel, portlet, restContextPath, rootObjectDefinitionExternalReferenceCode, scope, status, storageType, system, titleObjectFieldName}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectDefinition objectDefinition(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectDefinitionResource ->
				objectDefinitionResource.getObjectDefinition(
					objectDefinitionId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionByExternalReferenceCodeObjectFields(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectFieldPage objectDefinitionByExternalReferenceCodeObjectFields(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource -> new ObjectFieldPage(
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(
							objectFieldResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							objectFieldResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionObjectFields(filter: ___, objectDefinitionId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectFieldPage objectDefinitionObjectFields(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource -> new ObjectFieldPage(
				objectFieldResource.getObjectDefinitionObjectFieldsPage(
					objectDefinitionId, search,
					_filterBiFunction.apply(objectFieldResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(objectFieldResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectField(objectFieldId: ___){DBType, actions, businessType, defaultValue, externalReferenceCode, id, indexed, indexedAsKeyword, indexedLanguageId, label, listTypeDefinitionExternalReferenceCode, listTypeDefinitionId, localized, name, objectDefinitionExternalReferenceCode1, objectFieldSettings, objectRelationshipExternalReferenceCode, readOnly, readOnlyConditionExpression, relationshipType, required, state, system, type, unique}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectField objectField(
			@GraphQLName("objectFieldId") Long objectFieldId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFieldResource -> objectFieldResource.getObjectField(
				objectFieldId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectFolders(page: ___, pageSize: ___, search: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectFolderPage objectFolders(
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource -> new ObjectFolderPage(
				objectFolderResource.getObjectFoldersPage(
					search, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectFolderByExternalReferenceCode(externalReferenceCode: ___){actions, dateCreated, dateModified, externalReferenceCode, id, label, name, objectFolderItems}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectFolder objectFolderByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource ->
				objectFolderResource.getObjectFolderByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectFolder(objectFolderId: ___){actions, dateCreated, dateModified, externalReferenceCode, id, label, name, objectFolderItems}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectFolder objectFolder(
			@GraphQLName("objectFolderId") Long objectFolderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectFolderResource -> objectFolderResource.getObjectFolder(
				objectFolderId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionByExternalReferenceCodeObjectLayouts(externalReferenceCode: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectLayoutPage
			objectDefinitionByExternalReferenceCodeObjectLayouts(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectLayoutResource -> new ObjectLayoutPage(
				objectLayoutResource.
					getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
						externalReferenceCode, search,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							objectLayoutResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionObjectLayouts(objectDefinitionId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectLayoutPage objectDefinitionObjectLayouts(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectLayoutResource -> new ObjectLayoutPage(
				objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
					objectDefinitionId, search, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						objectLayoutResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectLayout(objectLayoutId: ___){actions, dateCreated, dateModified, defaultObjectLayout, id, name, objectDefinitionExternalReferenceCode, objectDefinitionId, objectLayoutTabs}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectLayout objectLayout(
			@GraphQLName("objectLayoutId") Long objectLayoutId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectLayoutResource -> objectLayoutResource.getObjectLayout(
				objectLayoutId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionByExternalReferenceCodeObjectRelationships(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectRelationshipPage
			objectDefinitionByExternalReferenceCodeObjectRelationships(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource -> new ObjectRelationshipPage(
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(
							objectRelationshipResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							objectRelationshipResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionObjectRelationships(filter: ___, objectDefinitionId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectRelationshipPage objectDefinitionObjectRelationships(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource -> new ObjectRelationshipPage(
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId, search,
						_filterBiFunction.apply(
							objectRelationshipResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							objectRelationshipResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectRelationship(objectRelationshipId: ___){actions, deletionType, edge, externalReferenceCode, id, label, name, objectDefinitionExternalReferenceCode1, objectDefinitionExternalReferenceCode2, objectDefinitionId1, objectDefinitionId2, objectDefinitionModifiable2, objectDefinitionName2, objectDefinitionScope2, objectDefinitionSystem2, objectField, parameterObjectFieldId, parameterObjectFieldName, reverse, system, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectRelationship objectRelationship(
			@GraphQLName("objectRelationshipId") Long objectRelationshipId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectRelationshipResource ->
				objectRelationshipResource.getObjectRelationship(
					objectRelationshipId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionByExternalReferenceCodeObjectValidationRules(externalReferenceCode: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectValidationRulePage
			objectDefinitionByExternalReferenceCodeObjectValidationRules(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource -> new ObjectValidationRulePage(
				objectValidationRuleResource.
					getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
						externalReferenceCode, search,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							objectValidationRuleResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionObjectValidationRules(objectDefinitionId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectValidationRulePage objectDefinitionObjectValidationRules(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource -> new ObjectValidationRulePage(
				objectValidationRuleResource.
					getObjectDefinitionObjectValidationRulesPage(
						objectDefinitionId, search,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							objectValidationRuleResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectValidationRule(objectValidationRuleId: ___){actions, active, dateCreated, dateModified, engine, engineLabel, errorLabel, externalReferenceCode, id, name, objectDefinitionExternalReferenceCode, objectDefinitionId, objectValidationRuleSettings, outputType, script, system}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectValidationRule objectValidationRule(
			@GraphQLName("objectValidationRuleId") Long objectValidationRuleId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectValidationRuleResource ->
				objectValidationRuleResource.getObjectValidationRule(
					objectValidationRuleId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionByExternalReferenceCodeObjectViews(externalReferenceCode: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectViewPage objectDefinitionByExternalReferenceCodeObjectViews(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource -> new ObjectViewPage(
				objectViewResource.
					getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
						externalReferenceCode, search,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							objectViewResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectDefinitionObjectViews(objectDefinitionId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectViewPage objectDefinitionObjectViews(
			@GraphQLName("objectDefinitionId") Long objectDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource -> new ObjectViewPage(
				objectViewResource.getObjectDefinitionObjectViewsPage(
					objectDefinitionId, search, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(objectViewResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectView(objectViewId: ___){actions, dateCreated, dateModified, defaultObjectView, id, name, objectDefinitionExternalReferenceCode, objectDefinitionId, objectViewColumns, objectViewFilterColumns, objectViewSortColumns}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectView objectView(@GraphQLName("objectViewId") Long objectViewId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectViewResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectViewResource -> objectViewResource.getObjectView(
				objectViewId));
	}

	@GraphQLTypeExtension(ObjectLayoutTab.class)
	public class GetObjectRelationshipTypeExtension {

		public GetObjectRelationshipTypeExtension(
			ObjectLayoutTab objectLayoutTab) {

			_objectLayoutTab = objectLayoutTab;
		}

		@GraphQLField
		public ObjectRelationship objectRelationship() throws Exception {
			return _applyComponentServiceObjects(
				_objectRelationshipResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectRelationshipResource ->
					objectRelationshipResource.getObjectRelationship(
						_objectLayoutTab.getObjectRelationshipId()));
		}

		private ObjectLayoutTab _objectLayoutTab;

	}

	@GraphQLTypeExtension(ObjectView.class)
	public class GetObjectDefinitionTypeExtension {

		public GetObjectDefinitionTypeExtension(ObjectView objectView) {
			_objectView = objectView;
		}

		@GraphQLField
		public ObjectDefinition objectDefinition() throws Exception {
			return _applyComponentServiceObjects(
				_objectDefinitionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectDefinitionResource ->
					objectDefinitionResource.getObjectDefinition(
						_objectView.getObjectDefinitionId()));
		}

		private ObjectView _objectView;

	}

	@GraphQLTypeExtension(ObjectDefinition.class)
	public class
		GetObjectDefinitionByExternalReferenceCodeObjectViewsPageTypeExtension {

		public GetObjectDefinitionByExternalReferenceCodeObjectViewsPageTypeExtension(
			ObjectDefinition objectDefinition) {

			_objectDefinition = objectDefinition;
		}

		@GraphQLField
		public ObjectViewPage byExternalReferenceCodeObjectViews(
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_objectViewResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectViewResource -> new ObjectViewPage(
					objectViewResource.
						getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
							_objectDefinition.getExternalReferenceCode(),
							search, Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								objectViewResource, sortsString))));
		}

		private ObjectDefinition _objectDefinition;

	}

	@GraphQLTypeExtension(ObjectFieldSetting.class)
	public class GetObjectFieldTypeExtension {

		public GetObjectFieldTypeExtension(
			ObjectFieldSetting objectFieldSetting) {

			_objectFieldSetting = objectFieldSetting;
		}

		@GraphQLField
		public ObjectField objectField() throws Exception {
			return _applyComponentServiceObjects(
				_objectFieldResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectFieldResource -> objectFieldResource.getObjectField(
					_objectFieldSetting.getObjectFieldId()));
		}

		private ObjectFieldSetting _objectFieldSetting;

	}

	@GraphQLTypeExtension(ObjectDefinition.class)
	public class
		GetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageTypeExtension {

		public GetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageTypeExtension(
			ObjectDefinition objectDefinition) {

			_objectDefinition = objectDefinition;
		}

		@GraphQLField
		public ObjectRelationshipPage
				byExternalReferenceCodeObjectRelationships(
					@GraphQLName("search") String search,
					@GraphQLName("filter") String filterString,
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page,
					@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_objectRelationshipResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectRelationshipResource -> new ObjectRelationshipPage(
					objectRelationshipResource.
						getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
							_objectDefinition.getExternalReferenceCode(),
							search,
							_filterBiFunction.apply(
								objectRelationshipResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								objectRelationshipResource, sortsString))));
		}

		private ObjectDefinition _objectDefinition;

	}

	@GraphQLTypeExtension(ObjectDefinition.class)
	public class GetObjectFolderByExternalReferenceCodeTypeExtension {

		public GetObjectFolderByExternalReferenceCodeTypeExtension(
			ObjectDefinition objectDefinition) {

			_objectDefinition = objectDefinition;
		}

		@GraphQLField
		public ObjectFolder objectFolderByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_objectFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectFolderResource ->
					objectFolderResource.getObjectFolderByExternalReferenceCode(
						_objectDefinition.getExternalReferenceCode()));
		}

		private ObjectDefinition _objectDefinition;

	}

	@GraphQLTypeExtension(ObjectDefinition.class)
	public class
		GetObjectDefinitionByExternalReferenceCodeObjectActionsPageTypeExtension {

		public GetObjectDefinitionByExternalReferenceCodeObjectActionsPageTypeExtension(
			ObjectDefinition objectDefinition) {

			_objectDefinition = objectDefinition;
		}

		@GraphQLField
		public ObjectActionPage byExternalReferenceCodeObjectActions(
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_objectActionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectActionResource -> new ObjectActionPage(
					objectActionResource.
						getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
							_objectDefinition.getExternalReferenceCode(),
							search, Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								objectActionResource, sortsString))));
		}

		private ObjectDefinition _objectDefinition;

	}

	@GraphQLTypeExtension(ObjectDefinition.class)
	public class
		GetObjectDefinitionByExternalReferenceCodeObjectFieldsPageTypeExtension {

		public GetObjectDefinitionByExternalReferenceCodeObjectFieldsPageTypeExtension(
			ObjectDefinition objectDefinition) {

			_objectDefinition = objectDefinition;
		}

		@GraphQLField
		public ObjectFieldPage byExternalReferenceCodeObjectFields(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_objectFieldResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectFieldResource -> new ObjectFieldPage(
					objectFieldResource.
						getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
							_objectDefinition.getExternalReferenceCode(),
							search,
							_filterBiFunction.apply(
								objectFieldResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								objectFieldResource, sortsString))));
		}

		private ObjectDefinition _objectDefinition;

	}

	@GraphQLTypeExtension(ObjectDefinition.class)
	public class
		GetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageTypeExtension {

		public GetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageTypeExtension(
			ObjectDefinition objectDefinition) {

			_objectDefinition = objectDefinition;
		}

		@GraphQLField
		public ObjectLayoutPage byExternalReferenceCodeObjectLayouts(
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_objectLayoutResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectLayoutResource -> new ObjectLayoutPage(
					objectLayoutResource.
						getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
							_objectDefinition.getExternalReferenceCode(),
							search, Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								objectLayoutResource, sortsString))));
		}

		private ObjectDefinition _objectDefinition;

	}

	@GraphQLTypeExtension(ObjectFolder.class)
	public class GetObjectDefinitionByExternalReferenceCodeTypeExtension {

		public GetObjectDefinitionByExternalReferenceCodeTypeExtension(
			ObjectFolder objectFolder) {

			_objectFolder = objectFolder;
		}

		@GraphQLField
		public ObjectDefinition objectDefinitionByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_objectDefinitionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectDefinitionResource ->
					objectDefinitionResource.
						getObjectDefinitionByExternalReferenceCode(
							_objectFolder.getExternalReferenceCode()));
		}

		private ObjectFolder _objectFolder;

	}

	@GraphQLTypeExtension(ObjectDefinition.class)
	public class
		GetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageTypeExtension {

		public GetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageTypeExtension(
			ObjectDefinition objectDefinition) {

			_objectDefinition = objectDefinition;
		}

		@GraphQLField
		public ObjectValidationRulePage
				byExternalReferenceCodeObjectValidationRules(
					@GraphQLName("search") String search,
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page,
					@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_objectValidationRuleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectValidationRuleResource -> new ObjectValidationRulePage(
					objectValidationRuleResource.
						getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
							_objectDefinition.getExternalReferenceCode(),
							search, Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								objectValidationRuleResource, sortsString))));
		}

		private ObjectDefinition _objectDefinition;

	}

	@GraphQLName("ObjectActionPage")
	public class ObjectActionPage {

		public ObjectActionPage(Page objectActionPage) {
			actions = objectActionPage.getActions();

			facets = objectActionPage.getFacets();

			items = objectActionPage.getItems();
			lastPage = objectActionPage.getLastPage();
			page = objectActionPage.getPage();
			pageSize = objectActionPage.getPageSize();
			totalCount = objectActionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ObjectAction> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ObjectDefinitionPage")
	public class ObjectDefinitionPage {

		public ObjectDefinitionPage(Page objectDefinitionPage) {
			actions = objectDefinitionPage.getActions();

			facets = objectDefinitionPage.getFacets();

			items = objectDefinitionPage.getItems();
			lastPage = objectDefinitionPage.getLastPage();
			page = objectDefinitionPage.getPage();
			pageSize = objectDefinitionPage.getPageSize();
			totalCount = objectDefinitionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ObjectDefinition> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ObjectFieldPage")
	public class ObjectFieldPage {

		public ObjectFieldPage(Page objectFieldPage) {
			actions = objectFieldPage.getActions();

			facets = objectFieldPage.getFacets();

			items = objectFieldPage.getItems();
			lastPage = objectFieldPage.getLastPage();
			page = objectFieldPage.getPage();
			pageSize = objectFieldPage.getPageSize();
			totalCount = objectFieldPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ObjectField> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ObjectFolderPage")
	public class ObjectFolderPage {

		public ObjectFolderPage(Page objectFolderPage) {
			actions = objectFolderPage.getActions();

			facets = objectFolderPage.getFacets();

			items = objectFolderPage.getItems();
			lastPage = objectFolderPage.getLastPage();
			page = objectFolderPage.getPage();
			pageSize = objectFolderPage.getPageSize();
			totalCount = objectFolderPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ObjectFolder> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ObjectLayoutPage")
	public class ObjectLayoutPage {

		public ObjectLayoutPage(Page objectLayoutPage) {
			actions = objectLayoutPage.getActions();

			facets = objectLayoutPage.getFacets();

			items = objectLayoutPage.getItems();
			lastPage = objectLayoutPage.getLastPage();
			page = objectLayoutPage.getPage();
			pageSize = objectLayoutPage.getPageSize();
			totalCount = objectLayoutPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ObjectLayout> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ObjectRelationshipPage")
	public class ObjectRelationshipPage {

		public ObjectRelationshipPage(Page objectRelationshipPage) {
			actions = objectRelationshipPage.getActions();

			facets = objectRelationshipPage.getFacets();

			items = objectRelationshipPage.getItems();
			lastPage = objectRelationshipPage.getLastPage();
			page = objectRelationshipPage.getPage();
			pageSize = objectRelationshipPage.getPageSize();
			totalCount = objectRelationshipPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ObjectRelationship> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ObjectValidationRulePage")
	public class ObjectValidationRulePage {

		public ObjectValidationRulePage(Page objectValidationRulePage) {
			actions = objectValidationRulePage.getActions();

			facets = objectValidationRulePage.getFacets();

			items = objectValidationRulePage.getItems();
			lastPage = objectValidationRulePage.getLastPage();
			page = objectValidationRulePage.getPage();
			pageSize = objectValidationRulePage.getPageSize();
			totalCount = objectValidationRulePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ObjectValidationRule> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ObjectViewPage")
	public class ObjectViewPage {

		public ObjectViewPage(Page objectViewPage) {
			actions = objectViewPage.getActions();

			facets = objectViewPage.getFacets();

			items = objectViewPage.getItems();
			lastPage = objectViewPage.getLastPage();
			page = objectViewPage.getPage();
			pageSize = objectViewPage.getPageSize();
			totalCount = objectViewPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ObjectView> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

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
	private BiFunction<Object, List<String>, Aggregation>
		_aggregationBiFunction;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction<Object, String, Filter> _filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, Sort[]> _sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}