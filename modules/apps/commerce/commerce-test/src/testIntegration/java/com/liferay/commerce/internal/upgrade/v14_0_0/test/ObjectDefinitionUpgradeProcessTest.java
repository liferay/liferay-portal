/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v14_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionTable;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		_originalSystemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(
					CPDefinition.class.getSimpleName());

		SystemObjectDefinitionManager newSystemObjectDefinitionManager =
			(SystemObjectDefinitionManager)ProxyUtil.newProxyInstance(
				SystemObjectDefinitionManager.class.getClassLoader(),
				new Class<?>[] {SystemObjectDefinitionManager.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "getPrimaryKeyColumn")) {

						return CPDefinitionTable.INSTANCE.CPDefinitionId;
					}

					if (Objects.equals(method.getName(), "getObjectFields")) {
						List<ObjectField> objectFields =
							(List<ObjectField>)method.invoke(
								_originalSystemObjectDefinitionManager, args);

						objectFields.set(
							4,
							new TextObjectFieldBuilder(
							).dbColumnName(
								"CPDefinitionId"
							).labelMap(
								LocalizedMapUtil.getLocalizedMap(
									RandomTestUtil.randomString())
							).name(
								"productId"
							).system(
								true
							).build());

						return objectFields;
					}

					return method.invoke(
						_originalSystemObjectDefinitionManager, args);
				});

		_serviceTrackerMap =
			(ServiceTrackerMap<String, SystemObjectDefinitionManager>)
				ReflectionTestUtil.getAndSetFieldValue(
					_systemObjectDefinitionManagerRegistry,
					"_serviceTrackerMap",
					ProxyUtil.newProxyInstance(
						ServiceTrackerMap.class.getClassLoader(),
						new Class<?>[] {ServiceTrackerMap.class},
						(proxy, method, args) -> {
							if (Objects.equals(
									method.getName(), "getService") &&
								Objects.equals(args[0], "CPDefinition")) {

								return newSystemObjectDefinitionManager;
							}

							return method.invoke(_serviceTrackerMap, args);
						}));

		_originalObjectDefinitionLocalService =
			(ObjectDefinitionLocalService)
				ReflectionTestUtil.getAndSetFieldValue(
					_portalInstanceLifecycleListener,
					"_objectDefinitionLocalService",
					ProxyUtil.newProxyInstance(
						ObjectDefinitionLocalService.class.getClassLoader(),
						new Class<?>[] {ObjectDefinitionLocalService.class},
						(proxy, method, args) -> {
							if (!Objects.equals(
									method.getName(),
									"addOrUpdateSystemObjectDefinition")) {

								return method.invoke(
									_originalObjectDefinitionLocalService,
									args);
							}

							SystemObjectDefinitionManager
								systemObjectDefinitionManager =
									(SystemObjectDefinitionManager)args[2];

							if (!Objects.equals(
									systemObjectDefinitionManager.getName(),
									"CPDefinition")) {

								return method.invoke(
									_originalObjectDefinitionLocalService,
									args);
							}

							args[2] = newSystemObjectDefinitionManager;

							return method.invoke(
								_originalObjectDefinitionLocalService, args);
						}));
	}

	@AfterClass
	public static void tearDownClass() {
		if (_originalObjectDefinitionLocalService != null) {
			ReflectionTestUtil.setFieldValue(
				_portalInstanceLifecycleListener,
				"_objectDefinitionLocalService",
				_originalObjectDefinitionLocalService);
		}

		if (_serviceTrackerMap != null) {
			ReflectionTestUtil.setFieldValue(
				_systemObjectDefinitionManagerRegistry, "_serviceTrackerMap",
				_serviceTrackerMap);
		}
	}

	@Before
	public void setUp() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_objectDefinition1 =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_COMMERCE_PRODUCT_DEFINITION", _company.getCompanyId());

		_user = UserTestUtil.getAdminUser(_company.getCompanyId());

		_objectDefinition2 = ObjectDefinitionTestUtil.publishObjectDefinition(
			true, ObjectDefinitionTestUtil.getRandomName(),
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					StringUtil.randomId()
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY, _user.getUserId());

		_objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, _objectDefinition1,
			_objectDefinition2,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			StringUtil.randomId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, _objectDefinition1,
			_objectDefinition2,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT);

		_objectField = _objectFieldLocalService.getObjectField(
			_objectRelationship2.getObjectFieldId2());
	}

	@Test
	public void testUpgrade() throws Exception {
		Assert.assertEquals(
			"CPDefinitionId",
			_objectDefinition1.getPKObjectFieldDBColumnName());
		Assert.assertEquals(
			"CPDefinitionId", _objectDefinition1.getPKObjectFieldName());

		String externalReferenceCode = RandomTestUtil.randomString();

		_originalSystemObjectDefinitionManager.addBaseModel(
			_user,
			HashMapBuilder.<String, Object>put(
				"catalogId",
				() -> {
					List<CommerceCatalog> commerceCatalogs =
						CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
							_company.getCompanyId(), true);

					CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

					return commerceCatalog.getCommerceCatalogId();
				}
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"productType", "simple"
			).build());

		BaseModel<?> baseModel =
			_originalSystemObjectDefinitionManager.
				fetchBaseModelByExternalReferenceCode(
					externalReferenceCode, _company.getCompanyId());

		Map<String, Object> modelAttributes = baseModel.getModelAttributes();

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, _user.getUserId(), _objectDefinition2.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				_objectField.getName(),
				(long)modelAttributes.get("publishedCPDefinitionId")
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_objectRelationshipLocalService.addObjectRelationshipMappingTableValues(
			_user.getUserId(), _objectRelationship1.getObjectRelationshipId(),
			(long)modelAttributes.get("publishedCPDefinitionId"),
			objectEntry.getObjectEntryId(),
			ServiceContextTestUtil.getServiceContext());

		_assertManyToManyObjectRelationshipFieldValue(
			"CPDefinitionId",
			(long)modelAttributes.get("publishedCPDefinitionId"),
			_objectRelationship1.getDBTableName());

		_assertOneToManyObjectRelationshipFieldValue(
			(long)modelAttributes.get("publishedCPDefinitionId"),
			_objectDefinition2, _objectField.getName(),
			objectEntry.getObjectEntryId());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}

		_objectDefinition1 =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_COMMERCE_PRODUCT_DEFINITION", _company.getCompanyId());

		Assert.assertEquals(
			"CProductId", _objectDefinition1.getPKObjectFieldDBColumnName());
		Assert.assertEquals(
			"CProductId", _objectDefinition1.getPKObjectFieldName());

		_assertManyToManyObjectRelationshipFieldValue(
			"CProductId", (long)modelAttributes.get("CProductId"),
			_objectRelationship1.getDBTableName());

		_objectDefinition2 = _objectDefinitionLocalService.getObjectDefinition(
			_objectDefinition2.getObjectDefinitionId());

		_objectField = _objectFieldLocalService.getObjectField(
			_objectRelationship2.getObjectFieldId2());

		_assertOneToManyObjectRelationshipFieldValue(
			(long)modelAttributes.get("CProductId"), _objectDefinition2,
			_objectField.getName(), objectEntry.getObjectEntryId());
	}

	private void _assertManyToManyObjectRelationshipFieldValue(
			String columnName, long primaryKey, String tableName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			Assert.assertTrue(dbInspector.hasColumn(tableName, columnName));

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(*) from ", tableName, " where ", columnName,
					" = ?"));

			preparedStatement.setLong(1, primaryKey);

			ResultSet resultSet = preparedStatement.executeQuery();

			Assert.assertNotNull(resultSet.next());

			Assert.assertEquals(1L, resultSet.getInt(1));
		}
	}

	private void _assertOneToManyObjectRelationshipFieldValue(
			long expectedValue, ObjectDefinition objectDefinition,
			String objectFieldName, long primaryKey)
		throws Exception {

		Map<String, Serializable> values =
			_objectEntryLocalService.
				getExtensionDynamicObjectDefinitionTableValues(
					objectDefinition, primaryKey);

		Assert.assertEquals(expectedValue, values.get(objectFieldName));
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.internal.upgrade.v14_0_0." +
			"ObjectDefinitionUpgradeProcess";

	private static ObjectDefinitionLocalService
		_originalObjectDefinitionLocalService;
	private static SystemObjectDefinitionManager
		_originalSystemObjectDefinitionManager;

	@Inject(
		filter = "component.name=com.liferay.object.internal.instance.lifecycle.SystemObjectDefinitionManagerPortalInstanceLifecycleListener"
	)
	private static PortalInstanceLifecycleListener
		_portalInstanceLifecycleListener;

	private static ServiceTrackerMap<String, SystemObjectDefinitionManager>
		_serviceTrackerMap;

	@Inject
	private static SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.internal.upgrade.registry.CommerceServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private Company _company;

	@Inject
	private MultiVMPool _multiVMPool;

	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ObjectField _objectField;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@DeleteAfterTestRun
	private ObjectRelationship _objectRelationship1;

	@DeleteAfterTestRun
	private ObjectRelationship _objectRelationship2;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private User _user;

}