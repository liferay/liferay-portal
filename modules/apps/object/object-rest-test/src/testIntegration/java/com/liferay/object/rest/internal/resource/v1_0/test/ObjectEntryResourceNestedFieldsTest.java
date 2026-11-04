/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.rest.test.util.UserAccountTestUtil;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectEntryServiceWrapper;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Edge;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.Tree;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.spring.hibernate.PortalTransactionManager;
import com.liferay.portal.spring.hibernate.PortletTransactionManager;
import com.liferay.portal.spring.transaction.TransactionExecutor;
import com.liferay.portal.spring.transaction.TransactionInterceptor;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.vulcan.resource.NestedFieldsContextResource;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Feature;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.SessionFactory;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * @author Luis Miguel Barcos
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceNestedFieldsTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testCustomizeNestedFieldsContextWithRootModelHierarchy()
		throws Exception {

		Tree objectDefinitionTree = TreeTestUtil.createObjectDefinitionTree(
			objectDefinitionLocalService, objectRelationshipLocalService, true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA", "AB"}
			).put(
				"AA", new String[] {"AAA", "AAB"}
			).put(
				"AB", new String[0]
			).put(
				"AAA", new String[0]
			).put(
				"AAB", new String[0]
			).build());

		Node objectDefinitionRootNode = objectDefinitionTree.getRootNode();

		NestedFieldsContextResource nestedFieldsContextResource =
			(NestedFieldsContextResource)getObjectEntryResource(
				objectDefinitionLocalService.getObjectDefinition(
					objectDefinitionRootNode.getPrimaryKey()),
				TestPropsValues.getUser());

		String nestedField1 = RandomTestUtil.randomString();
		String nestedField2 = RandomTestUtil.randomString();
		String nestedField3 = RandomTestUtil.randomString();

		List<String> nestedFields = Arrays.asList(
			nestedField1, nestedField2, nestedField3, "rootModelHierarchy");

		NestedFieldsContext nestedFieldsContext =
			nestedFieldsContextResource.customizeNestedFieldsContext(
				new NestedFieldsContext(1, nestedFields));

		Set<String> expectedNestedFields = new HashSet<>(nestedFields);

		expectedNestedFields.addAll(
			_getObjectRelationshipNames(objectDefinitionRootNode));

		Assert.assertEquals(
			expectedNestedFields,
			new HashSet<>(nestedFieldsContext.getNestedFields()));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AB", "C_AAA", "C_AAB"},
			objectEntryLocalService, objectRelationshipLocalService);
	}

	@Test
	public void testCustomizeNestedFieldsContextWithRootModelHierarchyNoHierarchy()
		throws Exception {

		String nestedField1 = RandomTestUtil.randomString();
		String nestedField2 = RandomTestUtil.randomString();
		String nestedField3 = RandomTestUtil.randomString();

		NestedFieldsContextResource nestedFieldsContextResource =
			(NestedFieldsContextResource)getObjectEntryResource(
				getObjectDefinition1(), TestPropsValues.getUser());

		NestedFieldsContext nestedFieldsContext =
			nestedFieldsContextResource.customizeNestedFieldsContext(
				new NestedFieldsContext(
					1,
					Arrays.asList(
						nestedField1, nestedField2, nestedField3,
						"rootModelHierarchy")));

		Assert.assertEquals(
			SetUtil.fromArray(
				nestedField1, nestedField2, nestedField3, "rootModelHierarchy"),
			new HashSet<>(nestedFieldsContext.getNestedFields()));
	}

	@Test
	public void testCustomizeNestedFieldsContextWithoutRootModelHierarchy()
		throws Exception {

		String nestedField1 = RandomTestUtil.randomString();
		String nestedField2 = RandomTestUtil.randomString();
		String nestedField3 = RandomTestUtil.randomString();

		NestedFieldsContextResource nestedFieldsContextResource =
			(NestedFieldsContextResource)getObjectEntryResource(
				getObjectDefinition1(), TestPropsValues.getUser());

		NestedFieldsContext nestedFieldsContext =
			nestedFieldsContextResource.customizeNestedFieldsContext(
				new NestedFieldsContext(
					1,
					Arrays.asList(nestedField1, nestedField2, nestedField3)));

		Assert.assertEquals(
			SetUtil.fromArray(nestedField1, nestedField2, nestedField3),
			new HashSet<>(nestedFieldsContext.getNestedFields()));
	}

	@Test
	public void testGetNestedFieldDetailsInRelationshipsWithCustomObjectDefinition()
		throws Exception {

		// Many to many relationship

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			getObjectDefinition1().getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry1.getPrimaryKey()), role1.getRoleId(),
			new String[] {ActionKeys.DELETE, ActionKeys.PERMISSIONS});

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			getObjectDefinition2().getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry2.getPrimaryKey()), role2.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testGetNestedFieldDetailsInRelationships(
			objectRelationship2.getName(), null, objectRelationship2.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.MANY_TO_MANY);
		_testGetNestedFieldDetailsInRelationships(
			objectRelationship2.getName(), 3, objectRelationship2.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.MANY_TO_MANY);
		_testGetNestedFieldDetailsInRelationships(
			objectRelationship2.getName(), 5, objectRelationship2.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.MANY_TO_MANY);
		_testGetNestedFieldDetailsInRelationships(
			objectRelationship2.getName(), 6, objectRelationship2.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.MANY_TO_MANY);

		// Many to many relationship with permissions

		_testGetNestedFieldDetailsInRelationships(
			objectRelationship2.getName(), 3, objectRelationship2.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			new JSONArray[] {
				_getPermissionsJSONArray(
					new String[] {ActionKeys.DELETE, ActionKeys.PERMISSIONS},
					getObjectDefinition1(), role1),
				_getPermissionsJSONArray(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					getObjectDefinition2(), role2),
				_getPermissionsJSONArray(
					new String[] {ActionKeys.DELETE, ActionKeys.PERMISSIONS},
					getObjectDefinition1(), role1),
				_getPermissionsJSONArray(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					getObjectDefinition2(), role2)
			},
			Type.MANY_TO_MANY);

		// Many to one relationship

		objectRelationship3 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		String relationshipFieldName = String.format(
			"r_%s_%s", objectRelationship3.getName(),
			getObjectDefinition1().getPKObjectFieldName());

		String relationshipFieldNameNestedFieldName = StringUtil.removeLast(
			relationshipFieldName, "Id");

		_testGetNestedFieldDetailsInRelationships(
			relationshipFieldNameNestedFieldName, null, relationshipFieldName,
			getObjectDefinition2(),
			new String[][] {
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)}
			},
			Type.MANY_TO_ONE);
		_testGetNestedFieldDetailsInRelationships(
			relationshipFieldNameNestedFieldName, null,
			StringUtil.removeLast(relationshipFieldName, "Id"),
			getObjectDefinition2(),
			new String[][] {
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)}
			},
			Type.MANY_TO_ONE);

		String relatedObjectDefinitionName = StringUtil.removeFirst(
			StringUtil.removeLast(
				getObjectDefinition1().getPKObjectFieldName(), "Id"),
			"c_");

		_testGetNestedFieldDetailsInRelationships(
			relationshipFieldNameNestedFieldName, null,
			relatedObjectDefinitionName, getObjectDefinition2(),
			new String[][] {
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)}
			},
			Type.MANY_TO_ONE);
		_testGetNestedFieldDetailsInRelationships(
			relationshipFieldNameNestedFieldName, null,
			RandomTestUtil.randomString() + relatedObjectDefinitionName,
			getObjectDefinition2(),
			new String[][] {
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)}
			},
			Type.MANY_TO_ONE);

		_testGetNestedFieldDetailsInRelationships(
			relationshipFieldNameNestedFieldName, null,
			objectRelationship3.getName(), getObjectDefinition2(),
			new String[][] {
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)}
			},
			Type.MANY_TO_ONE);
		_testGetNestedFieldDetailsInRelationships(
			objectRelationship3.getName(), null, objectRelationship3.getName(),
			getObjectDefinition2(),
			new String[][] {
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)}
			},
			Type.MANY_TO_ONE);

		// Many to one relationship with permissions

		_testGetNestedFieldDetailsInRelationships(
			relationshipFieldNameNestedFieldName, null, relationshipFieldName,
			getObjectDefinition2(),
			new String[][] {
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)}
			},
			new JSONArray[] {
				_getPermissionsJSONArray(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					getObjectDefinition2(), role2),
				_getPermissionsJSONArray(
					new String[] {ActionKeys.DELETE, ActionKeys.PERMISSIONS},
					getObjectDefinition1(), role1)
			},
			Type.MANY_TO_ONE);

		// One to many relationship

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testGetNestedFieldDetailsInRelationships(
			objectRelationship1.getName(), null, objectRelationship1.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.ONE_TO_MANY);
		_testGetNestedFieldDetailsInRelationships(
			objectRelationship1.getName(), 3, objectRelationship1.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.ONE_TO_MANY);
		_testGetNestedFieldDetailsInRelationships(
			objectRelationship1.getName(), 5, objectRelationship1.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.ONE_TO_MANY);
		_testGetNestedFieldDetailsInRelationships(
			objectRelationship1.getName(), 6, objectRelationship1.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.ONE_TO_MANY);

		// One to many relationship with permissions

		_testGetNestedFieldDetailsInRelationships(
			objectRelationship1.getName(), 3, objectRelationship1.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			new JSONArray[] {
				_getPermissionsJSONArray(
					new String[] {ActionKeys.DELETE, ActionKeys.PERMISSIONS},
					getObjectDefinition1(), role1),
				_getPermissionsJSONArray(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					getObjectDefinition2(), role2),
				_getPermissionsJSONArray(
					new String[] {ActionKeys.DELETE, ActionKeys.PERMISSIONS},
					getObjectDefinition1(), role1),
				_getPermissionsJSONArray(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					getObjectDefinition2(), role2)
			},
			Type.ONE_TO_MANY);

		// Transactions

		try (TransactionCounter transactionCounter = new TransactionCounter(
				objectEntryLocalService, _objectEntryService)) {

			_testGetNestedFieldDetailsInRelationships(
				objectRelationship1.getName(), 6, objectRelationship1.getName(),
				getObjectDefinition1(),
				new String[][] {
					{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
					{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
					{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
					{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
					{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
					{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
				},
				Type.ONE_TO_MANY);

			Assert.assertEquals(0, transactionCounter.getCount());
		}
	}

	@Test
	public void testGetNestedFieldDetailsInRelationshipsWithSystemObjectDefinition()
		throws Exception {

		// TODO LPS-17875 and LPS-185883

		// With fields, many to many and one to many relationships

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			getObjectDefinition1().getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry1.getPrimaryKey()), role1.getRoleId(),
			new String[] {ActionKeys.DELETE, ActionKeys.PERMISSIONS});

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);
		objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition3(), OBJECT_FIELD_NAME_3, OBJECT_FIELD_VALUE_3);

		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			getObjectDefinition3().getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry3.getPrimaryKey()), role2.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		objectEntry4 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition4(), OBJECT_FIELD_NAME_TEXT,
			OBJECT_FIELD_VALUE_4);

		userAccountJSONObject = UserAccountTestUtil.addUserAccountJSONObject(
			systemObjectDefinitionManager,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)
			).build());

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			systemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		userAccountJSONObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				jaxRsApplicationDescriptor.getRESTContextPath(), "/",
				userAccountJSONObject.get("id"), "?nestedFields=permissions"),
			Http.Method.GET);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), userSystemObjectDefinition,
			objectEntry1.getPrimaryKey(), userAccountJSONObject.getLong("id"),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			userSystemObjectDefinition, getObjectDefinition2(),
			userAccountJSONObject.getLong("id"), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		objectRelationship3 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition2(), getObjectDefinition3(),
			objectEntry2.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		objectRelationship4 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition3(), userSystemObjectDefinition,
			objectEntry3.getPrimaryKey(), userAccountJSONObject.getLong("id"),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		objectRelationship5 = addObjectRelationshipAndRelateObjectEntries(
			userSystemObjectDefinition, getObjectDefinition4(),
			userAccountJSONObject.getLong("id"), objectEntry4.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		int nestedFieldDepth = 5;

		String endpoint = StringBundler.concat(
			getObjectDefinition1().getRESTContextPath(), "?fields=",
			objectRelationship1.getName(), ".", objectRelationship2.getName(),
			".", objectRelationship3.getName(), ".",
			objectRelationship4.getName(), ".", objectRelationship5.getName(),
			".", OBJECT_FIELD_NAME_TEXT, "&nestedFields=",
			objectRelationship1.getName(), ",", objectRelationship2.getName(),
			",", objectRelationship3.getName(), ",",
			objectRelationship4.getName(), ",", objectRelationship5.getName(),
			"&nestedFieldsDepth=", nestedFieldDepth);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, endpoint, Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		_assertNestedFieldsFieldsInRelationships(
			0, nestedFieldDepth, itemsJSONArray.getJSONObject(0),
			new String[] {
				objectRelationship1.getName(), objectRelationship2.getName(),
				objectRelationship3.getName(), objectRelationship4.getName(),
				objectRelationship5.getName()
			},
			new String[][] {
				{"", "", Boolean.TRUE.toString()},
				{"", "", Boolean.TRUE.toString()},
				{"", "", Boolean.TRUE.toString()},
				{"", "", Boolean.TRUE.toString()},
				{"", "", Boolean.TRUE.toString()},
				{
					OBJECT_FIELD_NAME_TEXT,
					String.valueOf(OBJECT_FIELD_VALUE_4),
					Boolean.TRUE.toString()
				}
			},
			new Type[] {
				Type.ONE_TO_MANY, Type.MANY_TO_MANY, Type.ONE_TO_MANY,
				Type.MANY_TO_MANY, Type.ONE_TO_MANY
			});

		// Without fields, many to many relationship

		_testGetNestedFieldDetailsInRelationships(
			objectRelationship4.getName(), null, objectRelationship4.getName(),
			getObjectDefinition3(),
			new String[][] {
				{OBJECT_FIELD_NAME_3, String.valueOf(OBJECT_FIELD_VALUE_3)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.MANY_TO_MANY);
		_testGetNestedFieldDetailsInRelationships(
			objectRelationship4.getName(), 3, objectRelationship4.getName(),
			getObjectDefinition3(),
			new String[][] {
				{OBJECT_FIELD_NAME_3, String.valueOf(OBJECT_FIELD_VALUE_3)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_3, String.valueOf(OBJECT_FIELD_VALUE_3)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.MANY_TO_MANY);
		_testGetNestedFieldDetailsInRelationships(
			objectRelationship4.getName(), 5, objectRelationship4.getName(),
			getObjectDefinition3(),
			new String[][] {
				{OBJECT_FIELD_NAME_3, String.valueOf(OBJECT_FIELD_VALUE_3)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_3, String.valueOf(OBJECT_FIELD_VALUE_3)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_3, String.valueOf(OBJECT_FIELD_VALUE_3)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.MANY_TO_MANY);
		_testGetNestedFieldDetailsInRelationships(
			objectRelationship4.getName(), 6, objectRelationship4.getName(),
			getObjectDefinition3(),
			new String[][] {
				{OBJECT_FIELD_NAME_3, String.valueOf(OBJECT_FIELD_VALUE_3)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_3, String.valueOf(OBJECT_FIELD_VALUE_3)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_3, String.valueOf(OBJECT_FIELD_VALUE_3)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.MANY_TO_MANY);

		// Without fields, many to many relationship with permissions

		_testGetNestedFieldDetailsInRelationships(
			objectRelationship4.getName(), 3, objectRelationship4.getName(),
			getObjectDefinition3(),
			new String[][] {
				{OBJECT_FIELD_NAME_3, String.valueOf(OBJECT_FIELD_VALUE_3)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)},
				{OBJECT_FIELD_NAME_3, String.valueOf(OBJECT_FIELD_VALUE_3)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			new JSONArray[] {
				_getPermissionsJSONArray(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					getObjectDefinition3(), role2),
				(JSONArray)userAccountJSONObject.get("permissions"),
				_getPermissionsJSONArray(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					getObjectDefinition3(), role2),
				(JSONArray)userAccountJSONObject.get("permissions")
			},
			Type.MANY_TO_MANY);

		// Without fields, one to many relationship

		_testGetNestedFieldDetailsInRelationships(
			objectRelationship1.getName(), null, objectRelationship1.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			Type.ONE_TO_MANY);

		// Without fields, one to many relationship with permissions

		_testGetNestedFieldDetailsInRelationships(
			objectRelationship1.getName(), null, objectRelationship1.getName(),
			getObjectDefinition1(),
			new String[][] {
				{OBJECT_FIELD_NAME_1, String.valueOf(OBJECT_FIELD_VALUE_1)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			new JSONArray[] {
				_getPermissionsJSONArray(
					new String[] {ActionKeys.DELETE, ActionKeys.PERMISSIONS},
					getObjectDefinition1(), role1),
				(JSONArray)userAccountJSONObject.get("permissions")
			},
			Type.MANY_TO_MANY);

		// Without fields, many to one relationship

		String relationshipFieldName = String.format(
			"r_%s_%s", objectRelationship5.getName(),
			userSystemObjectDefinition.getPKObjectFieldName());

		String relationshipFieldNameNestedFieldName = StringUtil.removeLast(
			relationshipFieldName, "Id");

		_assertNestedFieldsInRelationships(
			0, 1,
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition4().getRESTContextPath(), "/",
					objectEntry4.getObjectEntryId(), "?nestedFields=",
					relationshipFieldName),
				Http.Method.GET),
			relationshipFieldNameNestedFieldName,
			new String[][] {
				{OBJECT_FIELD_NAME_TEXT, String.valueOf(OBJECT_FIELD_VALUE_4)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			null, Type.MANY_TO_ONE);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition4().getRESTContextPath(), "/",
				objectEntry4.getObjectEntryId(), "?nestedFields=",
				objectRelationship5.getName()),
			Http.Method.GET);

		_assertNestedFieldsInRelationships(
			0, 1, jsonObject, relationshipFieldNameNestedFieldName,
			new String[][] {
				{OBJECT_FIELD_NAME_TEXT, String.valueOf(OBJECT_FIELD_VALUE_4)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			null, Type.MANY_TO_ONE);
		_assertNestedFieldsInRelationships(
			0, 1, jsonObject, objectRelationship5.getName(),
			new String[][] {
				{OBJECT_FIELD_NAME_TEXT, String.valueOf(OBJECT_FIELD_VALUE_4)},
				{OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)}
			},
			null, Type.MANY_TO_ONE);
	}

	@Test
	@TestInfo("LPD-71250")
	public void testGetNestedFieldDetailsInRelationshipsWithoutPermission()
		throws Exception {

		// Many to many relationship, custom and system object definitions

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		userAccountJSONObject = UserAccountTestUtil.addUserAccountJSONObject(
			systemObjectDefinitionManager, Collections.emptyMap());

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			userSystemObjectDefinition, getObjectDefinition1(),
			userAccountJSONObject.getLong("id"), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testGetNestedFieldDetailsInRelationshipsWithoutPermission();

		// Many to many relationship, custom object definitions

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_testGetNestedFieldDetailsInRelationshipsWithoutPermission();

		// Many to one relationship, custom and system object definitions

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			userSystemObjectDefinition, getObjectDefinition1(),
			userAccountJSONObject.getLong("id"), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testGetNestedFieldDetailsInRelationshipsWithoutPermission();

		// Many to one relationship, custom object definitions

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			getObjectDefinition2(), getObjectDefinition1(),
			objectEntry2.getPrimaryKey(), objectEntry1.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testGetNestedFieldDetailsInRelationshipsWithoutPermission();

		// One to many relationship, custom and system object definitions

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			getObjectDefinition1(), userSystemObjectDefinition,
			objectEntry1.getPrimaryKey(), userAccountJSONObject.getLong("id"),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testGetNestedFieldDetailsInRelationshipsWithoutPermission();

		// One to many relationship, custom object definitions

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testGetNestedFieldDetailsInRelationshipsWithoutPermission();
	}

	@Test
	public void testGetObjectEntriesSystemProperties() throws Exception {
		ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_TEXT,
			NEW_OBJECT_FIELD_VALUE_1);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, getObjectDefinition1().getRESTContextPath(), Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(
			itemsJSONArray.toString(), 1, itemsJSONArray.length());

		JSONObject objectEntryJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertNull(
			objectEntryJSONObject.getJSONObject("systemProperties"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() +
				"?nestedFields=systemProperties.objectDefinitionBrief",
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(
			itemsJSONArray.toString(), 1, itemsJSONArray.length());

		objectEntryJSONObject = itemsJSONArray.getJSONObject(0);

		JSONObject systemPropertiesJSONObject =
			objectEntryJSONObject.getJSONObject("systemProperties");

		JSONObject objectDefinitionBriefJSONObject =
			systemPropertiesJSONObject.getJSONObject("objectDefinitionBrief");

		Assert.assertEquals(
			getObjectDefinition1().getExternalReferenceCode(),
			objectDefinitionBriefJSONObject.getString("externalReferenceCode"));
		Assert.assertEquals(
			getObjectDefinition1().getLabel(LocaleUtil.getDefault()),
			objectDefinitionBriefJSONObject.getString("label"));
		Assert.assertEquals(
			getObjectDefinition1().getObjectFolderExternalReferenceCode(),
			objectDefinitionBriefJSONObject.getString(
				"objectFolderExternalReferenceCode"));
	}

	@Test
	public void testGetObjectEntryUnsafeSuppliers() throws Exception {
		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_BOOLEAN, RandomTestUtil.randomBoolean()
			).put(
				OBJECT_FIELD_NAME_DATE,
				dateFormat.format(RandomTestUtil.nextDate())
			).build());
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_TEXT,
			RandomTestUtil.randomString());
		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		Bundle bundle = FrameworkUtil.getBundle(ObjectEntryResourceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Map<String, Long> invocations = new HashMap<>();

		ServiceRegistration<Feature> serviceRegistration =
			bundleContext.registerService(
				Feature.class,
				featureContext -> {
					featureContext.register(
						(ContainerResponseFilter)
							(containerRequestContext,
							 containerResponseContext) -> {

								com.liferay.object.rest.dto.v1_0.ObjectEntry
									objectEntry =
										(com.liferay.object.rest.dto.v1_0.
											ObjectEntry)
												containerResponseContext.
													getEntity();

								_registerUnsafeSupplierInvocations(
									invocations, objectEntry, null);
							},
						Priorities.USER + 999);

					return false;
				},
				HashMapDictionaryBuilder.<String, Object>put(
					"osgi.jaxrs.application.select",
					"(osgi.jaxrs.extension.select=\\(osgi.jaxrs.name=Liferay." +
						"Vulcan\\))"
				).put(
					"osgi.jaxrs.extension", true
				).put(
					"osgi.jaxrs.name", "Liferay.Vulcan)"
				).build());

		try {
			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/",
					objectEntry1.getExternalReferenceCode(), "?fields=",
					OBJECT_FIELD_NAME_BOOLEAN, ",", OBJECT_FIELD_NAME_TEXT),
				Http.Method.GET);

			Assert.assertTrue(jsonObject.has(OBJECT_FIELD_NAME_BOOLEAN));
			Assert.assertFalse(jsonObject.has(OBJECT_FIELD_NAME_DATE));
			Assert.assertTrue(jsonObject.has(OBJECT_FIELD_NAME_TEXT));
			Assert.assertFalse(jsonObject.has(objectRelationship1.getName()));

			_assertInvocations(invocations, true, OBJECT_FIELD_NAME_BOOLEAN);
			_assertInvocations(invocations, false, OBJECT_FIELD_NAME_DATE);
			_assertInvocations(invocations, true, OBJECT_FIELD_NAME_TEXT);
			_assertInvocations(
				invocations, false, objectRelationship1.getName());
			_assertInvocations(
				invocations, false,
				objectRelationship1.getName() + "." + OBJECT_FIELD_NAME_TEXT);

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null,
				getObjectDefinition1().getRESTContextPath() +
					"/by-external-reference-code/" +
						objectEntry1.getExternalReferenceCode(),
				Http.Method.GET);

			Assert.assertTrue(jsonObject.has(OBJECT_FIELD_NAME_BOOLEAN));
			Assert.assertTrue(jsonObject.has(OBJECT_FIELD_NAME_DATE));
			Assert.assertTrue(jsonObject.has(OBJECT_FIELD_NAME_TEXT));
			Assert.assertFalse(jsonObject.has(objectRelationship1.getName()));

			_assertInvocations(invocations, true, OBJECT_FIELD_NAME_BOOLEAN);
			_assertInvocations(invocations, true, OBJECT_FIELD_NAME_DATE);
			_assertInvocations(invocations, true, OBJECT_FIELD_NAME_TEXT);
			_assertInvocations(
				invocations, false, objectRelationship1.getName());
			_assertInvocations(
				invocations, false,
				objectRelationship1.getName() + "." + OBJECT_FIELD_NAME_TEXT);

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/",
					objectEntry1.getExternalReferenceCode(), "?nestedFields=",
					objectRelationship1.getName()),
				Http.Method.GET);

			Assert.assertTrue(jsonObject.has(OBJECT_FIELD_NAME_BOOLEAN));
			Assert.assertTrue(jsonObject.has(OBJECT_FIELD_NAME_DATE));
			Assert.assertTrue(jsonObject.has(OBJECT_FIELD_NAME_TEXT));
			Assert.assertTrue(jsonObject.has(objectRelationship1.getName()));
			Assert.assertFalse(
				Validator.isNull(
					JSONUtil.getValueAsString(
						jsonObject,
						"JSONArray/" + objectRelationship1.getName(),
						"Object/0", "Object/" + OBJECT_FIELD_NAME_TEXT)));

			_assertInvocations(invocations, true, OBJECT_FIELD_NAME_BOOLEAN);
			_assertInvocations(invocations, true, OBJECT_FIELD_NAME_DATE);
			_assertInvocations(invocations, true, OBJECT_FIELD_NAME_TEXT);
			_assertInvocations(
				invocations, true, objectRelationship1.getName());
			_assertInvocations(
				invocations, true,
				objectRelationship1.getName() + "." + OBJECT_FIELD_NAME_TEXT);

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/",
					objectEntry1.getExternalReferenceCode(), "?nestedFields=",
					objectRelationship1.getName(), "&restrictFields=",
					objectRelationship1.getName(), ".", OBJECT_FIELD_NAME_TEXT),
				Http.Method.GET);

			Assert.assertTrue(jsonObject.has(OBJECT_FIELD_NAME_BOOLEAN));
			Assert.assertTrue(jsonObject.has(OBJECT_FIELD_NAME_DATE));
			Assert.assertTrue(jsonObject.has(OBJECT_FIELD_NAME_TEXT));
			Assert.assertTrue(jsonObject.has(objectRelationship1.getName()));
			Assert.assertTrue(
				Validator.isNull(
					JSONUtil.getValueAsString(
						jsonObject,
						"JSONArray/" + objectRelationship1.getName(),
						"Object/0", "Object/" + OBJECT_FIELD_NAME_TEXT)));

			_assertInvocations(invocations, true, OBJECT_FIELD_NAME_BOOLEAN);
			_assertInvocations(invocations, true, OBJECT_FIELD_NAME_DATE);
			_assertInvocations(invocations, true, OBJECT_FIELD_NAME_TEXT);
			_assertInvocations(
				invocations, true, objectRelationship1.getName());
			_assertInvocations(
				invocations, false,
				objectRelationship1.getName() + "." + OBJECT_FIELD_NAME_TEXT);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetObjectEntryWithAuditEvents() throws Exception {
		ListTypeDefinition listTypeDefinition =
			listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				false,
				Arrays.asList(
					ListTypeEntryUtil.createListTypeEntry(
						"listTypeEntryKey1",
						Collections.singletonMap(
							LocaleUtil.US, "List Type Entry Key 1")),
					ListTypeEntryUtil.createListTypeEntry(
						"listTypeEntryKey2",
						Collections.singletonMap(
							LocaleUtil.US, "List Type Entry Key 2"))),
				new ServiceContext());

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, null, true, false,
				true, false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
						ObjectFieldConstants.DB_TYPE_BOOLEAN, true, false, null,
						"Author of Gospel", "authorOfGospel", false),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						"Email Address", "emailAddress", false),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
						ObjectFieldConstants.DB_TYPE_DOUBLE, true, false, null,
						"Height", "height", false),
					ObjectFieldUtil.createObjectField(
						listTypeDefinition.getListTypeDefinitionId(),
						ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, null,
						ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
						"List Type Entry Key", "listTypeEntryKey", false,
						false),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
						ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
						"Upload", "upload",
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_ACCEPTED_FILE_EXTENSIONS
							).value(
								"txt"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_FILE_SOURCE
							).value(
								ObjectFieldSettingConstants.
									VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
							).value(
								String.valueOf(MAX_FILE_SIZE_VALUE)
							).build()),
						false)),
				Collections.emptyList(), new ServiceContext());

		objectDefinition.setEnableObjectEntryHistory(true);

		objectDefinitionLocalService.updateObjectDefinition(objectDefinition);

		objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		NestedFieldsContext originalNestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.portal.security.audit.router.configuration." +
						"PersistentAuditMessageProcessorConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", true
					).put(
						"flushInterval", 1
					).build())) {

			ObjectEntry serviceBuilderObjectEntry =
				objectEntryLocalService.addObjectEntry(
					0, TestPropsValues.getUserId(),
					objectDefinition.getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null,
					HashMapBuilder.<String, Serializable>put(
						"authorOfGospel", true
					).put(
						"emailAddress", "john@liferay.com"
					).put(
						"height", 110.1
					).put(
						"listTypeEntryKey", "listTypeEntryKey1"
					).put(
						"upload",
						() -> {
							FileEntry fileEntry = addTempFileEntry(
								DLTestUtil.randomTextFileBytes(),
								objectDefinition, "Old Testament");

							return fileEntry.getFileEntryId();
						}
					).build(),
					ServiceContextTestUtil.getServiceContext());

			long objectEntryId = serviceBuilderObjectEntry.getObjectEntryId();

			objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId,
				serviceBuilderObjectEntry.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					"authorOfGospel", false
				).put(
					"emailAddress", "peter@liferay.com"
				).put(
					"height", 120.2
				).put(
					"listTypeEntryKey", "listTypeEntryKey2"
				).put(
					"upload",
					() -> {
						FileEntry fileEntry = addTempFileEntry(
							DLTestUtil.randomTextFileBytes(), objectDefinition,
							"New Testament");

						return fileEntry.getFileEntryId();
					}
				).build(),
				ServiceContextTestUtil.getServiceContext());

			User user = UserTestUtil.addUser();

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));
			PrincipalThreadLocal.setName(user.getUserId());

			// Add permissions to get object entry with audit events

			_addModelResourcePermissions(
				new String[] {
					ActionKeys.VIEW, ObjectActionKeys.OBJECT_ENTRY_HISTORY
				},
				objectDefinition.getClassName(), objectEntryId,
				user.getUserId());

			// Get object entry with null context URI info

			ObjectEntryResource objectEntryResource = getObjectEntryResource(
				objectDefinition, user);

			com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
				objectEntryResource.getObjectEntry(objectEntryId);

			Assert.assertNull(objectEntry.getAuditEvents());

			// Get object entry with no nested fields

			NestedFieldsContextThreadLocal.setNestedFieldsContext(null);

			objectEntry = objectEntryResource.getObjectEntry(objectEntryId);

			Assert.assertNull(objectEntry.getAuditEvents());

			// Get object entry with nested fields but without "auditEvents"

			NestedFieldsContextThreadLocal.setNestedFieldsContext(
				_getNestedFieldsContext(RandomTestUtil.randomString()));

			objectEntry = objectEntryResource.getObjectEntry(objectEntryId);

			Assert.assertNull(objectEntry.getAuditEvents());

			// Get object entry with "auditEvents" nested fields

			NestedFieldsContextThreadLocal.setNestedFieldsContext(
				_getNestedFieldsContext("auditEvents"));

			objectEntry = objectEntryResource.getObjectEntry(objectEntryId);

			JSONAssert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						"auditFieldChanges",
						JSONUtil.putAll(
							JSONUtil.put(
								"name", "authorOfGospel"
							).put(
								"newValue", true
							),
							JSONUtil.put(
								"name", "emailAddress"
							).put(
								"newValue", "john@liferay.com"
							),
							JSONUtil.put(
								"name", "height"
							).put(
								"newValue", 110.1
							),
							JSONUtil.put(
								"name", "listTypeEntryKey"
							).put(
								"newValue",
								JSONUtil.put(
									"key", "listTypeEntryKey1"
								).put(
									"name", "List Type Entry Key 1"
								)
							),
							JSONUtil.put(
								"name", "upload"
							).put(
								"newValue",
								JSONUtil.put("title", "Old Testament")
							))
					).put(
						"eventType", "ADD"
					),
					JSONUtil.put(
						"auditFieldChanges",
						JSONUtil.putAll(
							JSONUtil.put(
								"name", "authorOfGospel"
							).put(
								"newValue", false
							).put(
								"oldValue", true
							),
							JSONUtil.put(
								"name", "emailAddress"
							).put(
								"newValue", "peter@liferay.com"
							).put(
								"oldValue", "john@liferay.com"
							),
							JSONUtil.put(
								"name", "height"
							).put(
								"newValue", 120.2
							).put(
								"oldValue", 110.1
							),
							JSONUtil.put(
								"name", "listTypeEntryKey"
							).put(
								"newValue",
								JSONUtil.put(
									"key", "listTypeEntryKey2"
								).put(
									"name", "List Type Entry Key 2"
								)
							).put(
								"oldValue",
								JSONUtil.put(
									"key", "listTypeEntryKey1"
								).put(
									"name", "List Type Entry Key 1"
								)
							),
							JSONUtil.put(
								"name", "upload"
							).put(
								"newValue",
								JSONUtil.put("title", "New Testament")
							).put(
								"oldValue",
								JSONUtil.put("title", "Old Testament")
							))
					).put(
						"eventType", "UPDATE"
					)
				).toString(),
				String.valueOf(
					JSONFactoryUtil.createJSONArray(
						JSONFactoryUtil.looseSerializeDeep(
							objectEntry.getAuditEvents()))),
				false);

			// Get object entry without object entry history permission

			_addModelResourcePermissions(
				new String[] {ActionKeys.VIEW}, objectDefinition.getClassName(),
				objectEntryId, user.getUserId());

			objectEntry = objectEntryResource.getObjectEntry(objectEntryId);

			Assert.assertNull(objectEntry.getAuditEvents());
		}
		finally {
			NestedFieldsContextThreadLocal.setNestedFieldsContext(
				originalNestedFieldsContext);
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		listTypeDefinitionLocalService.deleteListTypeDefinition(
			listTypeDefinition);
	}

	@Test
	@TestInfo({"LPD-55420", "LPD-60419", "LPD-60423"})
	public void testGetObjectEntryWithDifferentScopeAndNestedFields()
		throws Exception {

		// Company scope

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1,
			RandomTestUtil.randomString());

		Group siteGroup1 = GroupTestUtil.addGroup();

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			siteGroup1.getGroupId(), getSiteScopedObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).build());

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getSiteScopedObjectDefinition1(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		Group siteGroup2 = GroupTestUtil.addGroup();

		ObjectEntry objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			siteGroup2.getGroupId(), getSiteScopedObjectDefinition1(),
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).build());

		ObjectRelationshipTestUtil.relateObjectEntries(
			objectEntry1.getPrimaryKey(), objectEntry3.getPrimaryKey(),
			objectRelationship1, TestPropsValues.getUserId());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"externalReferenceCode", objectEntry1.getExternalReferenceCode()
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode",
						objectEntry2.getExternalReferenceCode()),
					JSONUtil.put(
						"externalReferenceCode",
						objectEntry3.getExternalReferenceCode()))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				getEndpoint(
					objectEntry1.getObjectEntryId(), objectRelationship1,
					getObjectDefinition1()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		// Site scope

		ObjectEntry objectEntry4 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2,
			RandomTestUtil.randomString());

		objectRelationship2 = addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry4.getPrimaryKey(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"externalReferenceCode", objectEntry2.getExternalReferenceCode()
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode",
						objectEntry1.getExternalReferenceCode()
					).put(
						objectRelationship2.getName(),
						JSONUtil.putAll(
							JSONUtil.put(
								"externalReferenceCode",
								objectEntry4.getExternalReferenceCode()))
					))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getEndpoint(
						objectEntry2.getObjectEntryId(), objectRelationship1,
						getSiteScopedObjectDefinition1()),
					",", objectRelationship2.getName(), "&nestedFieldsDepth=2"),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testGetObjectEntryWithExcludedNestedFields() throws Exception {
		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);
		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_TEXT,
			RandomTestUtil.randomString());
		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		AtomicInteger atomicInteger = new AtomicInteger();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				ServiceWrapper.class,
				new ObjectEntryServiceWrapper(_objectEntryService) {

					@Override
					public List<ObjectEntry> getManyToManyObjectEntries(
							long groupId, long objectRelationshipId,
							long primaryKey, boolean related, boolean reverse,
							String search, int start, int end)
						throws PortalException {

						atomicInteger.incrementAndGet();

						return super.getManyToManyObjectEntries(
							groupId, objectRelationshipId, primaryKey, related,
							reverse, search, start, end);
					}

				},
				HashMapDictionaryBuilder.<String, Object>put(
					"service.ranking", Integer.MAX_VALUE
				).put(
					"service.wrapper.class", ObjectEntryService.class.getName()
				).build());

		try {
			String endpoint = getEndpoint(
				objectEntry1.getObjectEntryId(), objectRelationship1,
				getObjectDefinition1());

			// Nested field excluded by fields

			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				null, endpoint + "&fields=" + OBJECT_FIELD_NAME_1,
				Http.Method.GET);

			Assert.assertFalse(jsonObject.has(objectRelationship1.getName()));

			Assert.assertEquals(0, atomicInteger.get());

			// Nested field excluded by restrictFields

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null,
				endpoint + "&restrictFields=" + objectRelationship1.getName(),
				Http.Method.GET);

			Assert.assertFalse(jsonObject.has(objectRelationship1.getName()));

			Assert.assertEquals(0, atomicInteger.get());

			// Nested field included

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null, endpoint, Http.Method.GET);

			JSONArray jsonArray = jsonObject.getJSONArray(
				objectRelationship1.getName());

			Assert.assertEquals(1, jsonArray.length());

			Assert.assertEquals(1, atomicInteger.get());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetObjectEntryWithRootModelHierarchy() throws Exception {
		try {
			Tree objectDefinitionTree = TreeTestUtil.createObjectDefinitionTree(
				objectDefinitionLocalService, objectRelationshipLocalService,
				true,
				LinkedHashMapBuilder.put(
					"A", new String[] {"AA", "AB"}
				).put(
					"AA", new String[] {"AAA", "AAB"}
				).put(
					"AB", new String[0]
				).put(
					"AAA", new String[0]
				).put(
					"AAB", new String[0]
				).build());

			Node objectDefinitionRootNode = objectDefinitionTree.getRootNode();

			Tree objectEntryTree = TreeTestUtil.createObjectEntryTree(
				"1", objectDefinitionLocalService, objectEntryLocalService,
				objectFieldLocalService, objectRelationshipLocalService,
				objectDefinitionRootNode.getPrimaryKey());

			JSONObject objectEntryAJSONObject = getObjectEntryJSONObject(
				null, "rootModelHierarchy",
				objectDefinitionLocalService.getObjectDefinition(
					objectDefinitionRootNode.getPrimaryKey()));

			_assertRelatedObjectEntryExternalReferenceCode(
				_getChildNodeEdge(0, objectEntryTree.getRootNode()), "AA1",
				objectEntryAJSONObject);
			_assertRelatedObjectEntryExternalReferenceCode(
				_getChildNodeEdge(1, objectEntryTree.getRootNode()), "AB1",
				objectEntryAJSONObject);

			JSONObject objectEntryAAJSONObject = _getRelatedJSONObject(
				objectEntryAJSONObject,
				_getObjectRelationshipName(
					_getChildNodeEdge(0, objectEntryTree.getRootNode())),
				Type.ONE_TO_MANY);

			_assertRelatedObjectEntryExternalReferenceCode(
				_getChildNodeEdge(
					0, _getChildNode(0, objectEntryTree.getRootNode())),
				"AAA1", objectEntryAAJSONObject);
			_assertRelatedObjectEntryExternalReferenceCode(
				_getChildNodeEdge(
					1, _getChildNode(0, objectEntryTree.getRootNode())),
				"AAB1", objectEntryAAJSONObject);
		}
		finally {
			TreeTestUtil.deleteObjectDefinitionHierarchy(
				objectDefinitionLocalService,
				new String[] {"C_A", "C_AA", "C_AB", "C_AAAA", "C_AAB"},
				objectEntryLocalService, objectRelationshipLocalService);
		}
	}

	private void _addModelResourcePermissions(
			String[] actionIds, String className, long objectEntryId,
			long userId)
		throws Exception {

		resourcePermissionLocalService.addModelResourcePermissions(
			TestPropsValues.getCompanyId(), testGroupId, userId, className,
			String.valueOf(objectEntryId),
			ModelPermissionsFactory.create(
				HashMapBuilder.put(
					RoleConstants.USER, actionIds
				).build(),
				className));
	}

	private void _assertInvocations(
		Map<String, Long> invocations, boolean invoked, String name) {

		if (invoked) {
			Assert.assertEquals(
				name + " should have been computed once", 1,
				(long)invocations.getOrDefault(name, 1L));

			invocations.put(name, 0L);
		}
		else {
			Assert.assertEquals(
				name + " should not have been computed", 0,
				(long)invocations.getOrDefault(name, 0L));
		}
	}

	private void _assertNestedFieldsFieldsInRelationships(
		int currentDepth, int depth, JSONObject jsonObject,
		String[] nestedFieldNames,
		String[][] objectFieldNamesAndObjectFieldValues, Type[] types) {

		if (objectFieldNamesAndObjectFieldValues[currentDepth][0] == null) {
			Assert.assertNull(jsonObject);
		}
		else {
			String notPresent;

			if (objectFieldNamesAndObjectFieldValues[currentDepth][1].equals(
					jsonObject.getString(
						objectFieldNamesAndObjectFieldValues[currentDepth]
							[0]))) {

				notPresent = "true";
			}
			else {
				notPresent = "false";
			}

			Assert.assertEquals(
				"Incorrect presence of field " +
					objectFieldNamesAndObjectFieldValues[currentDepth][0],
				objectFieldNamesAndObjectFieldValues[currentDepth][2],
				notPresent);
		}

		if ((currentDepth == depth) ||
			(currentDepth ==
				PropsValues.OBJECT_NESTED_FIELDS_MAX_QUERY_DEPTH)) {

			Assert.assertEquals(
				Arrays.toString(objectFieldNamesAndObjectFieldValues),
				currentDepth + 1, objectFieldNamesAndObjectFieldValues.length);

			return;
		}

		_assertNestedFieldsFieldsInRelationships(
			currentDepth + 1, depth,
			_getRelatedJSONObject(
				jsonObject, nestedFieldNames[currentDepth],
				types[currentDepth]),
			nestedFieldNames, objectFieldNamesAndObjectFieldValues, types);
	}

	private void _assertNestedFieldsInRelationships(
		int currentDepth, int depth, JSONObject jsonObject,
		String nestedFieldName, String[][] objectFieldNamesAndObjectFieldValues,
		JSONArray[] permissionsJSONArrays, Type type) {

		if (objectFieldNamesAndObjectFieldValues[currentDepth][0] == null) {
			Assert.assertNull(jsonObject);
		}
		else {
			Assert.assertEquals(
				objectFieldNamesAndObjectFieldValues[currentDepth][1],
				jsonObject.getString(
					objectFieldNamesAndObjectFieldValues[currentDepth][0]));
		}

		if ((permissionsJSONArrays == null) ||
			(permissionsJSONArrays[currentDepth] == null)) {

			Assert.assertNull(jsonObject.getJSONArray("permissions"));
		}
		else {
			JSONAssert.assertEquals(
				String.valueOf(permissionsJSONArrays[currentDepth]),
				String.valueOf(jsonObject.getJSONArray("permissions")),
				JSONCompareMode.LENIENT);
		}

		if ((currentDepth == depth) ||
			(currentDepth ==
				PropsValues.OBJECT_NESTED_FIELDS_MAX_QUERY_DEPTH)) {

			Assert.assertEquals(
				Arrays.toString(objectFieldNamesAndObjectFieldValues),
				currentDepth + 1, objectFieldNamesAndObjectFieldValues.length);
			Assert.assertNull(jsonObject.get(nestedFieldName));

			return;
		}

		_assertNestedFieldsInRelationships(
			currentDepth + 1, depth,
			_getRelatedJSONObject(jsonObject, nestedFieldName, type),
			nestedFieldName, objectFieldNamesAndObjectFieldValues,
			permissionsJSONArrays, _getReverseType(type));
	}

	private void _assertRelatedObjectEntryExternalReferenceCode(
			Edge edge, String expectedExternalReferenceCode,
			JSONObject jsonObject)
		throws Exception {

		String objectRelationshipName = _getObjectRelationshipName(edge);

		Assert.assertTrue(jsonObject.has(objectRelationshipName));

		JSONObject relatedJSONObject = _getRelatedJSONObject(
			jsonObject, objectRelationshipName, Type.ONE_TO_MANY);

		Assert.assertEquals(
			expectedExternalReferenceCode,
			relatedJSONObject.getString("externalReferenceCode"));
	}

	private Node _getChildNode(int index, Node node) {
		List<Node> childNodes = node.getChildNodes();

		return childNodes.get(index);
	}

	private Edge _getChildNodeEdge(int index, Node node) {
		Node childNode = _getChildNode(index, node);

		return childNode.getEdge();
	}

	private NestedFieldsContext _getNestedFieldsContext(String nestedFields) {
		return new NestedFieldsContext(
			1, null, ListUtil.fromString(nestedFields, StringPool.COMMA), null,
			null, null);
	}

	private String _getObjectRelationshipName(Edge edge) throws Exception {
		ObjectRelationship objectRelationship =
			objectRelationshipLocalService.getObjectRelationship(
				edge.getObjectRelationshipId());

		return objectRelationship.getName();
	}

	private Set<String> _getObjectRelationshipNames(Node node)
		throws Exception {

		Set<String> objectRelationshipNames = new HashSet<>();

		Edge edge = node.getEdge();

		if (edge != null) {
			ObjectRelationship objectRelationship =
				objectRelationshipLocalService.getObjectRelationship(
					edge.getObjectRelationshipId());

			objectRelationshipNames.add(objectRelationship.getName());
		}

		for (Node childNode : node.getChildNodes()) {
			objectRelationshipNames.addAll(
				_getObjectRelationshipNames(childNode));
		}

		return objectRelationshipNames;
	}

	private JSONArray _getPermissionsJSONArray(
		String[] actionIds, ObjectDefinition objectDefinition, Role role) {

		return JSONUtil.putAll(
			getOwnerPermissionsJSONObject(objectDefinition),
			getPermissionsJSONObject(actionIds, role.getName()));
	}

	private JSONObject _getRelatedJSONObject(
		JSONObject jsonObject, String nestedFieldName, Type type) {

		if (type == Type.MANY_TO_ONE) {
			JSONObject nestedJSONObject = jsonObject.getJSONObject(
				nestedFieldName);

			Assert.assertNotNull(
				"Missing field " + nestedFieldName, nestedJSONObject);

			return jsonObject.getJSONObject(nestedFieldName);
		}

		JSONArray jsonArray = jsonObject.getJSONArray(nestedFieldName);

		Assert.assertNotNull("Missing field " + nestedFieldName, jsonArray);

		Assert.assertEquals(1, jsonArray.length());

		return jsonArray.getJSONObject(0);
	}

	private Type _getReverseType(Type type) {
		if (type == Type.MANY_TO_ONE) {
			return Type.ONE_TO_MANY;
		}
		else if (type == Type.ONE_TO_MANY) {
			return Type.MANY_TO_ONE;
		}

		return Type.MANY_TO_MANY;
	}

	private void _registerUnsafeSupplierInvocations(
		Map<String, Long> invocations,
		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry,
		String relationshipName) {

		Map<String, Object> properties = objectEntry.getProperties();

		properties.replaceAll(
			(name, value) -> (UnsafeSupplier<Object, Exception>)() -> {
				String key = name;

				if (relationshipName != null) {
					key = relationshipName + "." + name;
				}

				invocations.compute(
					key,
					(__, invocation) -> GetterUtil.get(invocation, 0L) + 1);

				if (StringUtil.equals(name, objectRelationship1.getName())) {
					com.liferay.object.rest.dto.v1_0.ObjectEntry[]
						objectEntries =
							(com.liferay.object.rest.dto.v1_0.ObjectEntry[])
								value;

					_registerUnsafeSupplierInvocations(
						invocations, objectEntries[0],
						objectRelationship1.getName());
				}

				return value;
			});

		objectEntry.setProperties(properties);
	}

	private void _testGetNestedFieldDetailsInRelationships(
			String expectedFieldName, Integer nestedFieldDepth,
			String nestedFieldName, ObjectDefinition objectDefinition,
			String[][] objectFieldNamesAndObjectFieldValues,
			JSONArray[] permissionsJSONArrays, Type type)
		throws Exception {

		_assertNestedFieldsInRelationships(
			0, GetterUtil.getInteger(nestedFieldDepth, 1),
			getObjectEntryJSONObject(
				nestedFieldDepth, nestedFieldName + ",permissions",
				objectDefinition),
			expectedFieldName, objectFieldNamesAndObjectFieldValues,
			permissionsJSONArrays, type);
	}

	private void _testGetNestedFieldDetailsInRelationships(
			String expectedFieldName, Integer nestedFieldDepth,
			String nestedFieldName, ObjectDefinition objectDefinition,
			String[][] objectFieldNamesAndObjectFieldValues, Type type)
		throws Exception {

		_assertNestedFieldsInRelationships(
			0, GetterUtil.getInteger(nestedFieldDepth, 1),
			getObjectEntryJSONObject(
				nestedFieldDepth, nestedFieldName, objectDefinition),
			expectedFieldName, objectFieldNamesAndObjectFieldValues, null,
			type);
	}

	private void _testGetNestedFieldDetailsInRelationshipsWithoutPermission()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			getObjectDefinition1().getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry1.getPrimaryKey()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		String password = RandomTestUtil.randomString();

		User user = addUser(RandomTestUtil.randomString(), password);

		userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		HTTPTestUtil.customize(
		).withCredentials(
			user.getEmailAddress(), password
		).apply(
			() -> {
				JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
					null,
					getEndpoint(
						objectEntry1.getObjectEntryId(), objectRelationship1,
						getObjectDefinition1()),
					Http.Method.GET);

				Assert.assertTrue(
					JSONUtil.isEmpty(
						jsonObject.getJSONArray(
							objectRelationship1.getName())));
				Assert.assertTrue(
					JSONUtil.isEmpty(
						jsonObject.getJSONObject(
							objectRelationship1.getName())));
			}
		);
	}

	@Inject
	private ObjectEntryService _objectEntryService;

	private static class TransactionCounter implements AutoCloseable {

		public TransactionCounter(Object... services) {
			for (Object service : services) {
				TransactionInterceptor transactionInterceptor =
					ReflectionTestUtil.getFieldValue(
						ProxyUtil.fetchInvocationHandler(
							service, AopInvocationHandler.class),
						"_transactionInterceptor");

				TransactionExecutor transactionExecutor =
					ReflectionTestUtil.getFieldValue(
						transactionInterceptor, "_transactionExecutor");

				PortletTransactionManager portletTransactionManager =
					ReflectionTestUtil.getFieldValue(
						transactionExecutor, "_platformTransactionManager");

				_autoCloseables.push(
					ReflectionTestUtil.setFieldValueWithAutoCloseable(
						portletTransactionManager, "_portalTransactionManager",
						new PortalTransactionManager(
							InfrastructureUtil.getDataSource(),
							(SessionFactory)
								InfrastructureUtil.getSessionFactory()) {

							@Override
							protected void doCommit(
								DefaultTransactionStatus
									defaultTransactionStatus) {

								super.doCommit(defaultTransactionStatus);

								_atomicInteger.incrementAndGet();
							}

						}));
			}
		}

		@Override
		public void close() throws Exception {
			while (!_autoCloseables.isEmpty()) {
				AutoCloseable autoCloseable = _autoCloseables.pop();

				autoCloseable.close();
			}
		}

		public int getCount() {
			return _atomicInteger.get();
		}

		private final AtomicInteger _atomicInteger = new AtomicInteger(0);
		private final Stack<AutoCloseable> _autoCloseables = new Stack<>();

	}

}