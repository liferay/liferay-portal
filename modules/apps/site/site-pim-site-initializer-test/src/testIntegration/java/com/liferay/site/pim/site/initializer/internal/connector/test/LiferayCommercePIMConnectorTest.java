/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.depot.model.DepotEntry;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.exception.PIMConnectorException;
import com.liferay.site.pim.site.initializer.test.util.PIMBasicProductTestUtil;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tancredi Covioli
 */
@FeatureFlag("LPD-96666")
@RunWith(Arquillian.class)
public class LiferayCommercePIMConnectorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		PIMTestUtil.getOrAddGroup();

		_depotEntry = PIMTestUtil.addSpaceDepotEntry();
	}

	@Test
	public void testExecute() throws Exception {
		_testExecute();
		_testExecuteWithMissingCatalog();
	}

	private void _addPIMConnectorFieldMappingObjectEntry(
			String channelFieldName, ObjectEntry pimConnectorObjectEntry,
			ObjectField objectField, String sourceFieldName, String type,
			String value)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CONNECTOR_FIELD_MAPPING,
					TestPropsValues.getCompanyId());

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"channelFieldName", channelFieldName
			).put(
				objectField.getName(),
				pimConnectorObjectEntry.getObjectEntryId()
			).put(
				"sourceFieldName", sourceFieldName
			).put(
				"type", type
			).put(
				"value", value
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addPIMConnectorObjectEntry(
			String catalogExternalReferenceCode)
		throws Exception {

		User user = TestPropsValues.getUser();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CONNECTOR,
					user.getCompanyId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, user.getUserId(), objectDefinition.getObjectDefinitionId(), 0,
			null,
			HashMapBuilder.<String, Serializable>put(
				"active", false
			).put(
				"key", "liferay-commerce"
			).put(
				"name", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_objectEntries.add(objectEntry);

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByObjectDefinitionId1(
					objectDefinition.getObjectDefinitionId(),
					"pimConnectorToPIMConnectorFieldMappings");

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		_addPIMConnectorFieldMappingObjectEntry(
			"catalogExternalReferenceCode", objectEntry, objectField, null,
			"fixedValue", catalogExternalReferenceCode);
		_addPIMConnectorFieldMappingObjectEntry(
			"name", objectEntry, objectField, "title", "dynamicValue", null);
		_addPIMConnectorFieldMappingObjectEntry(
			"productOptions", objectEntry, objectField, "title", "dynamicValue",
			null);
		_addPIMConnectorFieldMappingObjectEntry(
			"productType", objectEntry, objectField, null, "fixedValue",
			"simple");
		_addPIMConnectorFieldMappingObjectEntry(
			"skus[].sku", objectEntry, objectField, "sku", "dynamicValue",
			null);

		PIMConnector pimConnector = _pimConnectorRegistry.getPIMConnector(
			"liferay-commerce");

		ObjectDefinition pimConnectorConfigurationObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					pimConnector.
						getPIMConnectorConfigurationObjectDefinitionExternalReferenceCode(),
					user.getCompanyId());

		_objectEntryLocalService.addOrUpdateObjectEntry(
			objectEntry.getExternalReferenceCode(), 0, user.getUserId(),
			pimConnectorConfigurationObjectDefinition.getObjectDefinitionId(),
			0, HashMapBuilder.<String, Serializable>put(
				"clientId", _oAuth2Application.getClientId()
			).put(
				"clientSecret", _oAuth2Application.getClientSecret()
			).put(
				"url",
				"http://localhost:" + PortalUtil.getPortalServerPort(false)
			).build(),
			ServiceContextTestUtil.getServiceContext());

		return objectEntry;
	}

	private JSONObject _execute(ObjectEntry pimConnectorObjectEntry)
		throws Exception {

		PIMConnector pimConnector = _pimConnectorRegistry.getPIMConnector(
			"liferay-commerce");

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			pimConnector.execute(pimConnectorObjectEntry));

		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskLocalService.
				getBatchEngineImportTaskByExternalReferenceCode(
					jsonObject.getString("batchId"),
					TestPropsValues.getCompanyId());

		for (int i = 0; i < 120; i++) {
			BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus =
				BatchEngineTaskExecuteStatus.valueOf(
					batchEngineImportTask.getExecuteStatus());

			if ((batchEngineTaskExecuteStatus ==
					BatchEngineTaskExecuteStatus.COMPLETED) ||
				(batchEngineTaskExecuteStatus ==
					BatchEngineTaskExecuteStatus.FAILED)) {

				break;
			}

			Thread.sleep(500);

			batchEngineImportTask =
				_batchEngineImportTaskLocalService.getBatchEngineImportTask(
					batchEngineImportTask.getBatchEngineImportTaskId());
		}

		Assert.assertEquals(
			BatchEngineTaskExecuteStatus.COMPLETED.name(),
			batchEngineImportTask.getExecuteStatus());

		return jsonObject;
	}

	private void _testExecute() throws Exception {
		User user = TestPropsValues.getUser();

		_oAuth2Application =
			_oAuth2ApplicationLocalService.addOAuth2Application(
				user.getCompanyId(), user.getUserId(), user.getFullName(),
				List.of(GrantType.CLIENT_CREDENTIALS), "client_secret_post",
				user.getUserId(),
				OAuth2SecureRandomGenerator.generateClientId(),
				ClientProfile.HEADLESS_SERVER.id(),
				OAuth2SecureRandomGenerator.generateClientSecret(), null,
				List.of(), "https://" + RandomTestUtil.randomString(), 0, null,
				RandomTestUtil.randomString(), null, List.of(), false,
				ListUtil.fromArray(
					"Liferay.Headless.Batch.Engine.everything",
					"Liferay.Headless.Commerce.Admin.Catalog.everything"),
				false, new ServiceContext());

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), "USD",
			LocaleUtil.toLanguageId(LocaleUtil.US),
			ServiceContextTestUtil.getServiceContext());

		ObjectEntry pimProductObjectEntry1 =
			PIMBasicProductTestUtil.addPIMBasicProductObjectEntry(
				_depotEntry.getGroupId());
		ObjectEntry pimProductObjectEntry2 =
			PIMBasicProductTestUtil.addPIMBasicProductObjectEntry(
				_depotEntry.getGroupId());

		ObjectEntry pimConnectorObjectEntry = _addPIMConnectorObjectEntry(
			_commerceCatalog.getExternalReferenceCode());

		JSONObject jsonObject = _execute(pimConnectorObjectEntry);

		JSONArray jsonArray = jsonObject.getJSONArray("log");

		Assert.assertEquals(jsonArray.toString(), 4, jsonArray.length());

		String log = jsonArray.toString();

		Assert.assertFalse(
			log, log.contains(_oAuth2Application.getClientSecret()));

		for (ObjectEntry pimProductObjectEntry :
				new ObjectEntry[] {
					pimProductObjectEntry1, pimProductObjectEntry2
				}) {

			CPDefinition cpDefinition =
				_cpDefinitionLocalService.
					fetchCPDefinitionByCProductExternalReferenceCode(
						pimProductObjectEntry.getExternalReferenceCode(),
						user.getCompanyId(), false);

			Assert.assertNotNull(cpDefinition);
			Assert.assertEquals(
				_commerceCatalog.getGroupId(), cpDefinition.getGroupId());
			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, cpDefinition.getStatus());

			CPInstance cpInstance =
				_cpInstanceLocalService.fetchCPInstanceByExternalReferenceCode(
					pimProductObjectEntry.getExternalReferenceCode(),
					user.getCompanyId());

			Assert.assertNotNull(cpInstance);
			Assert.assertEquals(
				cpDefinition.getCPDefinitionId(),
				cpInstance.getCPDefinitionId());
			Assert.assertTrue(cpInstance.isPublished());
			Assert.assertTrue(cpInstance.isPurchasable());
		}

		CPOption cpOption =
			_cpOptionLocalService.fetchCPOptionByExternalReferenceCode(
				"title", user.getCompanyId());

		Assert.assertNotNull(cpOption);
		Assert.assertTrue(cpOption.isSkuContributor());

		int count = _cpDefinitionLocalService.getCPDefinitionsCount(
			_commerceCatalog.getGroupId(), WorkflowConstants.STATUS_ANY);

		_execute(pimConnectorObjectEntry);

		Assert.assertEquals(
			count,
			_cpDefinitionLocalService.getCPDefinitionsCount(
				_commerceCatalog.getGroupId(), WorkflowConstants.STATUS_ANY));
	}

	private void _testExecuteWithMissingCatalog() throws Exception {
		ObjectEntry pimConnectorObjectEntry = _addPIMConnectorObjectEntry(
			RandomTestUtil.randomString());

		PIMConnector pimConnector = _pimConnectorRegistry.getPIMConnector(
			"liferay-commerce");

		try {
			pimConnector.execute(pimConnectorObjectEntry);

			Assert.fail();
		}
		catch (PIMConnectorException pimConnectorException) {
			Assert.assertEquals(
				"a-catalog-mapped-to-a-product-does-not-exist-in-the-remote-" +
					"instance",
				pimConnectorException.getMessage());

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				pimConnectorException.getOutput());

			JSONArray jsonArray = jsonObject.getJSONArray("log");

			Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());
		}
	}

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@DeleteAfterTestRun
	private OAuth2Application _oAuth2Application;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private final List<ObjectEntry> _objectEntries = new ArrayList<>();

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private PIMConnectorRegistry _pimConnectorRegistry;

}