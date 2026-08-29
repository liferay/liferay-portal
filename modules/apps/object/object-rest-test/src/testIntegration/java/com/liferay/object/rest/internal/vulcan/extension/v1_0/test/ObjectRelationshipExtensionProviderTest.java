/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.vulcan.extension.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.extension.ExtensionProvider;
import com.liferay.portal.vulcan.extension.PropertyDefinition;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class ObjectRelationshipExtensionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectDefinition = _publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					"Text", "String", true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME, false)));

		_objectEntry = _addObjectEntry(_OBJECT_FIELD_VALUE);

		_userSystemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager("User");

		ObjectDefinition userSystemObjectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(),
				_userSystemObjectDefinitionManager.getName());

		_user = TestPropsValues.getUser();

		_objectRelationship =
			ObjectRelationshipLocalServiceUtil.addObjectRelationship(
				null, _user.getUserId(),
				_objectDefinition.getObjectDefinitionId(),
				userSystemObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		ObjectRelationshipLocalServiceUtil.
			addObjectRelationshipMappingTableValues(
				_user.getUserId(),
				_objectRelationship.getObjectRelationshipId(),
				_objectEntry.getPrimaryKey(), _user.getUserId(),
				ServiceContextTestUtil.getServiceContext());

		_originalNestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();
	}

	@After
	public void tearDown() throws Exception {
		ObjectRelationshipLocalServiceUtil.
			deleteObjectRelationshipMappingTableValues(
				_objectRelationship.getObjectRelationshipId(),
				_objectEntry.getPrimaryKey(), _user.getUserId());

		ObjectRelationshipLocalServiceUtil.deleteObjectRelationship(
			_objectRelationship);

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			_originalNestedFieldsContext);
	}

	@Test
	public void testGetExtendedProperties() throws Exception {
		_testGetExtendedPropertiesWithCommerceProduct();
		_testGetExtendedPropertiesWithUserAccount();
	}

	@Test
	public void testGetExtendedPropertyDefinitions() throws Exception {
		Map<String, PropertyDefinition> extendedPropertyDefinitions =
			_extensionProvider.getExtendedPropertyDefinitions(
				TestPropsValues.getCompanyId(), UserAccount.class.getName());

		Assert.assertEquals(
			extendedPropertyDefinitions.toString(), 1,
			extendedPropertyDefinitions.size());

		PropertyDefinition propertyDefinition = extendedPropertyDefinitions.get(
			_objectRelationship.getName());

		Assert.assertEquals(
			_objectRelationship.getName(),
			propertyDefinition.getPropertyName());
		Assert.assertEquals(
			PropertyDefinition.PropertyType.MULTIPLE_ELEMENT,
			propertyDefinition.getPropertyType());
	}

	@Test
	public void testIsApplicableExtension() throws Exception {
		Assert.assertFalse(
			_extensionProvider.isApplicableExtension(
				TestPropsValues.getCompanyId(),
				com.liferay.object.rest.dto.v1_0.ObjectEntry.class.getName() +
					"#" + _objectDefinition.getName()));
		Assert.assertTrue(
			_extensionProvider.isApplicableExtension(
				TestPropsValues.getCompanyId(), UserAccount.class.getName()));
	}

	private ObjectEntry _addObjectEntry(String objectFieldValue)
		throws Exception {

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, objectFieldValue
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private NestedFieldsContext _getNestedFieldsContext(
		String nestedFieldName) {

		return new NestedFieldsContext(
			1, null, Collections.singletonList(nestedFieldName), null, null,
			null);
	}

	private ObjectDefinition _publishObjectDefinition(
			List<ObjectField> objectFields)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, true, false, true,
				false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), objectFields, Collections.emptyList(),
				new ServiceContext());

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private void _testGetExtendedPropertiesWithCommerceProduct()
		throws Exception {

		CommerceCatalog commerceCatalog = CPTestUtil.getSystemCommerceCatalog(
			TestPropsValues.getCompanyId());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		ObjectDefinition cpDefinitionObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), CPDefinition.class.getName());

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					"Text", "String", true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME, false)));

		ObjectEntry objectEntry = ObjectEntryLocalServiceUtil.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, _OBJECT_FIELD_VALUE
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectRelationship objectRelationship =
			ObjectRelationshipLocalServiceUtil.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				cpDefinitionObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		ObjectRelationshipLocalServiceUtil.
			addObjectRelationshipMappingTableValues(
				TestPropsValues.getUserId(),
				objectRelationship.getObjectRelationshipId(),
				objectEntry.getPrimaryKey(), cpDefinition.getCProductId(),
				ServiceContextTestUtil.getServiceContext());

		NestedFieldsContext originalNestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();

		try {
			NestedFieldsContextThreadLocal.setNestedFieldsContext(
				_getNestedFieldsContext(objectRelationship.getName()));

			Map<String, Serializable> extendedProperties =
				_extensionProvider.getExtendedProperties(
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					Product.class.getName(),
					new Product() {
						{
							id = cpDefinition.getCPDefinitionId();
							productId = cpDefinition.getCProductId();
						}
					});

			Assert.assertEquals(
				extendedProperties.toString(), 1, extendedProperties.size());
			Assert.assertNotNull(
				extendedProperties.get(objectRelationship.getName()));
		}
		finally {
			ObjectRelationshipLocalServiceUtil.
				deleteObjectRelationshipMappingTableValues(
					objectRelationship.getObjectRelationshipId(),
					objectEntry.getPrimaryKey(), cpDefinition.getCProductId());

			ObjectRelationshipLocalServiceUtil.deleteObjectRelationship(
				objectRelationship);

			PersistedModelLocalService persistedModelLocalService =
				PersistedModelLocalServiceRegistryUtil.
					getPersistedModelLocalService(CPDefinition.class.getName());

			persistedModelLocalService.deletePersistedModel(cpDefinition);

			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition.getObjectDefinitionId());

			NestedFieldsContextThreadLocal.setNestedFieldsContext(
				originalNestedFieldsContext);
		}
	}

	private void _testGetExtendedPropertiesWithUserAccount() throws Exception {
		UserAccount userAccount = new UserAccount() {
			{
				id = _user.getUserId();
			}
		};

		NestedFieldsContextThreadLocal.setNestedFieldsContext(null);

		Map<String, Serializable> extendedProperties =
			_extensionProvider.getExtendedProperties(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				UserAccount.class.getName(), userAccount);

		Assert.assertNull(extendedProperties);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			_getNestedFieldsContext(RandomTestUtil.randomString()));

		extendedProperties = _extensionProvider.getExtendedProperties(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			UserAccount.class.getName(), userAccount);

		Assert.assertTrue(extendedProperties.isEmpty());

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			_getNestedFieldsContext(_objectRelationship.getName()));

		extendedProperties = _extensionProvider.getExtendedProperties(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			UserAccount.class.getName(), userAccount);

		Assert.assertEquals(
			extendedProperties.toString(), 1, extendedProperties.size());
		Assert.assertNotNull(
			extendedProperties.get(_objectRelationship.getName()));
	}

	private static final String _OBJECT_FIELD_NAME =
		"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_VALUE =
		RandomTestUtil.randomString();

	@Inject(
		filter = "component.name=com.liferay.object.rest.internal.vulcan.extension.v1_0.ObjectRelationshipExtensionProvider"
	)
	private ExtensionProvider _extensionProvider;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	private ObjectRelationship _objectRelationship;
	private NestedFieldsContext _originalNestedFieldsContext;

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	private User _user;
	private SystemObjectDefinitionManager _userSystemObjectDefinitionManager;

}