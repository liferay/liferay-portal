/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.graphql.servlet.v1_0;

import com.liferay.object.admin.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.object.admin.rest.internal.graphql.query.v1_0.Query;
import com.liferay.object.admin.rest.internal.resource.v1_0.ObjectActionResourceImpl;
import com.liferay.object.admin.rest.internal.resource.v1_0.ObjectDefinitionResourceImpl;
import com.liferay.object.admin.rest.internal.resource.v1_0.ObjectFieldResourceImpl;
import com.liferay.object.admin.rest.internal.resource.v1_0.ObjectFolderResourceImpl;
import com.liferay.object.admin.rest.internal.resource.v1_0.ObjectLayoutResourceImpl;
import com.liferay.object.admin.rest.internal.resource.v1_0.ObjectRelationshipResourceImpl;
import com.liferay.object.admin.rest.internal.resource.v1_0.ObjectValidationRuleResourceImpl;
import com.liferay.object.admin.rest.internal.resource.v1_0.ObjectViewResourceImpl;
import com.liferay.object.admin.rest.resource.v1_0.ObjectActionResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFieldResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFolderResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectLayoutResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectRelationshipResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectValidationRuleResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectViewResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Javier Gamarra
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setObjectActionResourceComponentServiceObjects(
			_objectActionResourceComponentServiceObjects);
		Mutation.setObjectDefinitionResourceComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects);
		Mutation.setObjectFieldResourceComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects);
		Mutation.setObjectFolderResourceComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects);
		Mutation.setObjectLayoutResourceComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects);
		Mutation.setObjectRelationshipResourceComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects);
		Mutation.setObjectValidationRuleResourceComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects);
		Mutation.setObjectViewResourceComponentServiceObjects(
			_objectViewResourceComponentServiceObjects);

		Query.setObjectActionResourceComponentServiceObjects(
			_objectActionResourceComponentServiceObjects);
		Query.setObjectDefinitionResourceComponentServiceObjects(
			_objectDefinitionResourceComponentServiceObjects);
		Query.setObjectFieldResourceComponentServiceObjects(
			_objectFieldResourceComponentServiceObjects);
		Query.setObjectFolderResourceComponentServiceObjects(
			_objectFolderResourceComponentServiceObjects);
		Query.setObjectLayoutResourceComponentServiceObjects(
			_objectLayoutResourceComponentServiceObjects);
		Query.setObjectRelationshipResourceComponentServiceObjects(
			_objectRelationshipResourceComponentServiceObjects);
		Query.setObjectValidationRuleResourceComponentServiceObjects(
			_objectValidationRuleResourceComponentServiceObjects);
		Query.setObjectViewResourceComponentServiceObjects(
			_objectViewResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Object.Admin.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/object-admin-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#deleteObjectAction",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class,
							"deleteObjectAction"));
					put(
						"mutation#deleteObjectActionBatch",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class,
							"deleteObjectActionBatch"));
					put(
						"mutation#patchObjectAction",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class,
							"patchObjectAction"));
					put(
						"mutation#updateObjectAction",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class, "putObjectAction"));
					put(
						"mutation#updateObjectActionBatch",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class,
							"putObjectActionBatch"));
					put(
						"mutation#createObjectDefinitionByExternalReferenceCodeObjectAction",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class,
							"postObjectDefinitionByExternalReferenceCodeObjectAction"));
					put(
						"mutation#createObjectDefinitionObjectActionsPageExportBatch",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class,
							"postObjectDefinitionObjectActionsPageExportBatch"));
					put(
						"mutation#createObjectDefinitionObjectAction",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class,
							"postObjectDefinitionObjectAction"));
					put(
						"mutation#createObjectDefinitionObjectActionBatch",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class,
							"postObjectDefinitionObjectActionBatch"));
					put(
						"mutation#createObjectDefinitionsPageExportBatch",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"postObjectDefinitionsPageExportBatch"));
					put(
						"mutation#createObjectDefinition",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"postObjectDefinition"));
					put(
						"mutation#createObjectDefinitionBatch",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"postObjectDefinitionBatch"));
					put(
						"mutation#updateObjectDefinitionByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"putObjectDefinitionByExternalReferenceCode"));
					put(
						"mutation#deleteObjectDefinition",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"deleteObjectDefinition"));
					put(
						"mutation#deleteObjectDefinitionBatch",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"deleteObjectDefinitionBatch"));
					put(
						"mutation#patchObjectDefinition",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"patchObjectDefinition"));
					put(
						"mutation#updateObjectDefinition",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"putObjectDefinition"));
					put(
						"mutation#updateObjectDefinitionBatch",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"putObjectDefinitionBatch"));
					put(
						"mutation#createObjectDefinitionPublish",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"postObjectDefinitionPublish"));
					put(
						"mutation#createObjectDefinitionByExternalReferenceCodeObjectField",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class,
							"postObjectDefinitionByExternalReferenceCodeObjectField"));
					put(
						"mutation#createObjectDefinitionObjectFieldsPageExportBatch",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class,
							"postObjectDefinitionObjectFieldsPageExportBatch"));
					put(
						"mutation#createObjectDefinitionObjectField",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class,
							"postObjectDefinitionObjectField"));
					put(
						"mutation#createObjectDefinitionObjectFieldBatch",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class,
							"postObjectDefinitionObjectFieldBatch"));
					put(
						"mutation#deleteObjectField",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class,
							"deleteObjectField"));
					put(
						"mutation#deleteObjectFieldBatch",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class,
							"deleteObjectFieldBatch"));
					put(
						"mutation#patchObjectField",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class, "patchObjectField"));
					put(
						"mutation#updateObjectField",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class, "putObjectField"));
					put(
						"mutation#updateObjectFieldBatch",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class,
							"putObjectFieldBatch"));
					put(
						"mutation#createObjectFoldersPageExportBatch",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class,
							"postObjectFoldersPageExportBatch"));
					put(
						"mutation#createObjectFolder",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class,
							"postObjectFolder"));
					put(
						"mutation#createObjectFolderBatch",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class,
							"postObjectFolderBatch"));
					put(
						"mutation#updateObjectFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class,
							"putObjectFolderByExternalReferenceCode"));
					put(
						"mutation#deleteObjectFolder",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class,
							"deleteObjectFolder"));
					put(
						"mutation#deleteObjectFolderBatch",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class,
							"deleteObjectFolderBatch"));
					put(
						"mutation#patchObjectFolder",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class,
							"patchObjectFolder"));
					put(
						"mutation#updateObjectFolder",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class, "putObjectFolder"));
					put(
						"mutation#updateObjectFolderBatch",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class,
							"putObjectFolderBatch"));
					put(
						"mutation#createObjectDefinitionByExternalReferenceCodeObjectLayout",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class,
							"postObjectDefinitionByExternalReferenceCodeObjectLayout"));
					put(
						"mutation#createObjectDefinitionObjectLayoutsPageExportBatch",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class,
							"postObjectDefinitionObjectLayoutsPageExportBatch"));
					put(
						"mutation#createObjectDefinitionObjectLayout",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class,
							"postObjectDefinitionObjectLayout"));
					put(
						"mutation#createObjectDefinitionObjectLayoutBatch",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class,
							"postObjectDefinitionObjectLayoutBatch"));
					put(
						"mutation#deleteObjectLayout",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class,
							"deleteObjectLayout"));
					put(
						"mutation#deleteObjectLayoutBatch",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class,
							"deleteObjectLayoutBatch"));
					put(
						"mutation#updateObjectLayout",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class, "putObjectLayout"));
					put(
						"mutation#updateObjectLayoutBatch",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class,
							"putObjectLayoutBatch"));
					put(
						"mutation#createObjectDefinitionByExternalReferenceCodeObjectRelationship",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"postObjectDefinitionByExternalReferenceCodeObjectRelationship"));
					put(
						"mutation#createObjectDefinitionObjectRelationshipsPageExportBatch",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"postObjectDefinitionObjectRelationshipsPageExportBatch"));
					put(
						"mutation#createObjectDefinitionObjectRelationship",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"postObjectDefinitionObjectRelationship"));
					put(
						"mutation#createObjectDefinitionObjectRelationshipBatch",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"postObjectDefinitionObjectRelationshipBatch"));
					put(
						"mutation#updateObjectRelationshipByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"putObjectRelationshipByExternalReferenceCode"));
					put(
						"mutation#deleteObjectRelationship",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"deleteObjectRelationship"));
					put(
						"mutation#deleteObjectRelationshipBatch",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"deleteObjectRelationshipBatch"));
					put(
						"mutation#updateObjectRelationship",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"putObjectRelationship"));
					put(
						"mutation#updateObjectRelationshipBatch",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"putObjectRelationshipBatch"));
					put(
						"mutation#createObjectDefinitionByExternalReferenceCodeObjectValidationRule",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"postObjectDefinitionByExternalReferenceCodeObjectValidationRule"));
					put(
						"mutation#createObjectDefinitionObjectValidationRulesPageExportBatch",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"postObjectDefinitionObjectValidationRulesPageExportBatch"));
					put(
						"mutation#createObjectDefinitionObjectValidationRule",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"postObjectDefinitionObjectValidationRule"));
					put(
						"mutation#createObjectDefinitionObjectValidationRuleBatch",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"postObjectDefinitionObjectValidationRuleBatch"));
					put(
						"mutation#deleteObjectValidationRule",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"deleteObjectValidationRule"));
					put(
						"mutation#deleteObjectValidationRuleBatch",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"deleteObjectValidationRuleBatch"));
					put(
						"mutation#patchObjectValidationRule",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"patchObjectValidationRule"));
					put(
						"mutation#updateObjectValidationRule",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"putObjectValidationRule"));
					put(
						"mutation#updateObjectValidationRuleBatch",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"putObjectValidationRuleBatch"));
					put(
						"mutation#createObjectDefinitionByExternalReferenceCodeObjectView",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class,
							"postObjectDefinitionByExternalReferenceCodeObjectView"));
					put(
						"mutation#createObjectDefinitionObjectViewsPageExportBatch",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class,
							"postObjectDefinitionObjectViewsPageExportBatch"));
					put(
						"mutation#createObjectDefinitionObjectView",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class,
							"postObjectDefinitionObjectView"));
					put(
						"mutation#createObjectDefinitionObjectViewBatch",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class,
							"postObjectDefinitionObjectViewBatch"));
					put(
						"mutation#deleteObjectView",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class, "deleteObjectView"));
					put(
						"mutation#deleteObjectViewBatch",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class,
							"deleteObjectViewBatch"));
					put(
						"mutation#updateObjectView",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class, "putObjectView"));
					put(
						"mutation#updateObjectViewBatch",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class,
							"putObjectViewBatch"));
					put(
						"mutation#createObjectViewCopy",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class,
							"postObjectViewCopy"));

					put(
						"query#objectAction",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class, "getObjectAction"));
					put(
						"query#objectDefinitionByExternalReferenceCodeObjectActions",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectActionsPage"));
					put(
						"query#objectDefinitionObjectActions",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class,
							"getObjectDefinitionObjectActionsPage"));
					put(
						"query#objectDefinitions",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"getObjectDefinitionsPage"));
					put(
						"query#objectDefinitionByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCode"));
					put(
						"query#objectDefinition",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"getObjectDefinition"));
					put(
						"query#objectDefinitionByExternalReferenceCodeObjectFields",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectFieldsPage"));
					put(
						"query#objectDefinitionObjectFields",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class,
							"getObjectDefinitionObjectFieldsPage"));
					put(
						"query#objectField",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class, "getObjectField"));
					put(
						"query#objectFolders",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class,
							"getObjectFoldersPage"));
					put(
						"query#objectFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class,
							"getObjectFolderByExternalReferenceCode"));
					put(
						"query#objectFolder",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class, "getObjectFolder"));
					put(
						"query#objectDefinitionByExternalReferenceCodeObjectLayouts",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage"));
					put(
						"query#objectDefinitionObjectLayouts",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class,
							"getObjectDefinitionObjectLayoutsPage"));
					put(
						"query#objectLayout",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class, "getObjectLayout"));
					put(
						"query#objectDefinitionByExternalReferenceCodeObjectRelationships",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage"));
					put(
						"query#objectDefinitionObjectRelationships",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"getObjectDefinitionObjectRelationshipsPage"));
					put(
						"query#objectRelationship",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"getObjectRelationship"));
					put(
						"query#objectDefinitionByExternalReferenceCodeObjectValidationRules",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage"));
					put(
						"query#objectDefinitionObjectValidationRules",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"getObjectDefinitionObjectValidationRulesPage"));
					put(
						"query#objectValidationRule",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"getObjectValidationRule"));
					put(
						"query#objectDefinitionByExternalReferenceCodeObjectViews",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectViewsPage"));
					put(
						"query#objectDefinitionObjectViews",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class,
							"getObjectDefinitionObjectViewsPage"));
					put(
						"query#objectView",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class, "getObjectView"));

					put(
						"query#ObjectLayoutTab.objectRelationship",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"getObjectRelationship"));
					put(
						"query#ObjectView.objectDefinition",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"getObjectDefinition"));
					put(
						"query#ObjectDefinition.byExternalReferenceCodeObjectViews",
						new ObjectValuePair<>(
							ObjectViewResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectViewsPage"));
					put(
						"query#ObjectFieldSetting.objectField",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class, "getObjectField"));
					put(
						"query#ObjectDefinition.byExternalReferenceCodeObjectRelationships",
						new ObjectValuePair<>(
							ObjectRelationshipResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage"));
					put(
						"query#ObjectDefinition.objectFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectFolderResourceImpl.class,
							"getObjectFolderByExternalReferenceCode"));
					put(
						"query#ObjectDefinition.byExternalReferenceCodeObjectActions",
						new ObjectValuePair<>(
							ObjectActionResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectActionsPage"));
					put(
						"query#ObjectDefinition.byExternalReferenceCodeObjectFields",
						new ObjectValuePair<>(
							ObjectFieldResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectFieldsPage"));
					put(
						"query#ObjectDefinition.byExternalReferenceCodeObjectLayouts",
						new ObjectValuePair<>(
							ObjectLayoutResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage"));
					put(
						"query#ObjectFolder.objectDefinitionByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectDefinitionResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCode"));
					put(
						"query#ObjectDefinition.byExternalReferenceCodeObjectValidationRules",
						new ObjectValuePair<>(
							ObjectValidationRuleResourceImpl.class,
							"getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ObjectActionResource>
		_objectActionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ObjectDefinitionResource>
		_objectDefinitionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ObjectFieldResource>
		_objectFieldResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ObjectFolderResource>
		_objectFolderResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ObjectLayoutResource>
		_objectLayoutResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ObjectRelationshipResource>
		_objectRelationshipResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ObjectValidationRuleResource>
		_objectValidationRuleResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ObjectViewResource>
		_objectViewResourceComponentServiceObjects;

}