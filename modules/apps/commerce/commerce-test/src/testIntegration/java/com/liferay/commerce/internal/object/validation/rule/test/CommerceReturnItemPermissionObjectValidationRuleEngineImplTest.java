/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.validation.rule.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceReturnConstants;
import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CommerceReturnItemPermissionObjectValidationRuleEngineImplTest
	extends BaseObjectValidationRuleEngineImplTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		Assume.assumeTrue(FeatureFlagManagerUtil.isEnabled("LPD-10562"));

		super.setUp();

		CommerceShipment commerceShipment =
			_commerceShipmentLocalService.addCommerceShipment(
				commerceOrder.getCommerceOrderId(), serviceContext);

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
				_commerceInventoryWarehouseLocalService.
					getCommerceInventoryWarehouses(
						commerceOrder.getCommerceAccountId(),
						commerceChannel.getGroupId(),
						commerceOrderItem.getSku());

			CommerceInventoryWarehouse commerceInventoryWarehouse =
				commerceInventoryWarehouses.get(0);

			_commerceShipmentItemLocalService.addCommerceShipmentItem(
				null, commerceShipment.getCommerceShipmentId(),
				commerceOrderItem.getCommerceOrderItemId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				commerceOrderItem.getQuantity(), null, true, serviceContext);
		}

		_commerceShipmentLocalService.updateStatus(
			commerceShipment.getCommerceShipmentId(),
			CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED);

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder = _commerceOrderEngine.transitionCommerceOrder(
			commerceOrder, CommerceOrderConstants.ORDER_STATUS_COMPLETED,
			user.getUserId(), true);

		ObjectDefinition commerceReturnObjectDefinition = null;

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionLocalService.getObjectDefinitions(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			if (StringUtil.equals(
					objectDefinition.getExternalReferenceCode(),
					"L_COMMERCE_RETURN")) {

				commerceReturnObjectDefinition = objectDefinition;

				break;
			}
		}

		_objectEntry = _objectEntryLocalService.addObjectEntry(
			0, commerceReturnObjectDefinition.getUserId(),
			commerceReturnObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"r_accountToCommerceReturns_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_commerceOrderToCommerceReturns_commerceOrderId",
				commerceOrder.getCommerceOrderId()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testUpdateAuthorized() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		Map<String, Object> results = _objectValidationRuleEngine.execute(
			HashMapBuilder.<String, Object>put(
				"entryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"authorized", 1
					).put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).build()
				).build()
			).put(
				"originalEntryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"authorized", 0
					).put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).build()
				).build()
			).build(),
			null);

		Assert.assertFalse(
			GetterUtil.getBoolean(results.get("validationCriteriaMet")));

		_objectEntryLocalService.updateObjectEntry(
			_objectEntry.getUserId(), _objectEntry.getObjectEntryId(),
			_objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"r_accountToCommerceReturns_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_commerceOrderToCommerceReturns_commerceOrderId",
				commerceOrder.getCommerceOrderId()
			).put(
				"returnStatus", CommerceReturnConstants.RETURN_STATUS_PENDING
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testUpdateAuthorizeReturnWithoutReturningProducts()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		Map<String, Object> results = _objectValidationRuleEngine.execute(
			HashMapBuilder.<String, Object>put(
				"entryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"authorizeReturnWithoutReturningProducts", true
					).put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).build()
				).build()
			).put(
				"originalEntryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"authorizeReturnWithoutReturningProducts", false
					).put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).build()
				).build()
			).build(),
			null);

		Assert.assertFalse(
			GetterUtil.getBoolean(results.get("validationCriteriaMet")));
	}

	@Test
	public void testUpdateExternalReferenceCode() throws Exception {
		Map<String, Object> results = _objectValidationRuleEngine.execute(
			HashMapBuilder.<String, Object>put(
				"entryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", RandomTestUtil.randomString()
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).build()
				).build()
			).put(
				"originalEntryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", RandomTestUtil.randomString()
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).build()
				).build()
			).build(),
			null);

		Assert.assertFalse(
			GetterUtil.getBoolean(results.get("validationCriteriaMet")));
	}

	@Test
	public void testUpdateQuantity() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		Map<String, Object> results = _objectValidationRuleEngine.execute(
			HashMapBuilder.<String, Object>put(
				"entryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"quantity", 1
					).put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).build()
				).build()
			).put(
				"originalEntryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"quantity", 0
					).put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).build()
				).build()
			).build(),
			null);

		Assert.assertTrue(
			GetterUtil.getBoolean(results.get("validationCriteriaMet")));

		_objectEntryLocalService.updateObjectEntry(
			_objectEntry.getUserId(), _objectEntry.getObjectEntryId(),
			_objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"r_accountToCommerceReturns_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_commerceOrderToCommerceReturns_commerceOrderId",
				commerceOrder.getCommerceOrderId()
			).put(
				"returnStatus", CommerceReturnConstants.RETURN_STATUS_PENDING
			).build(),
			ServiceContextTestUtil.getServiceContext());

		results = _objectValidationRuleEngine.execute(
			HashMapBuilder.<String, Object>put(
				"entryDTO",
				HashMapBuilder.<String, Object>put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"externalReferenceCode", externalReferenceCode
					).put(
						"quantity", 1
					).put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).build()
				).build()
			).put(
				"originalEntryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"quantity", 0
					).put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).build()
				).build()
			).build(),
			null);

		Assert.assertFalse(
			GetterUtil.getBoolean(results.get("validationCriteriaMet")));
	}

	@Test
	public void testUpdateReceived() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		Map<String, Object> results = _objectValidationRuleEngine.execute(
			HashMapBuilder.<String, Object>put(
				"entryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).put(
						"received", 1
					).build()
				).build()
			).put(
				"originalEntryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).put(
						"received", 0
					).build()
				).build()
			).build(),
			null);

		Assert.assertFalse(
			GetterUtil.getBoolean(results.get("validationCriteriaMet")));
	}

	@Test
	public void testUpdateReturnReason() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		Map<String, Object> results = _objectValidationRuleEngine.execute(
			HashMapBuilder.<String, Object>put(
				"entryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).put(
						"returnReason", "productDefect"
					).build()
				).build()
			).put(
				"originalEntryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).put(
						"returnReason", "changeOfMind"
					).build()
				).build()
			).build(),
			null);

		Assert.assertTrue(
			GetterUtil.getBoolean(results.get("validationCriteriaMet")));

		_objectEntryLocalService.updateObjectEntry(
			_objectEntry.getUserId(), _objectEntry.getObjectEntryId(),
			_objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"r_accountToCommerceReturns_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_commerceOrderToCommerceReturns_commerceOrderId",
				commerceOrder.getCommerceOrderId()
			).put(
				"returnStatus", CommerceReturnConstants.RETURN_STATUS_PENDING
			).build(),
			ServiceContextTestUtil.getServiceContext());

		results = _objectValidationRuleEngine.execute(
			HashMapBuilder.<String, Object>put(
				"entryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).put(
						"returnReason", "changeOfMind"
					).build()
				).build()
			).put(
				"originalEntryDTO",
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId",
						_objectEntry.getObjectEntryId()
					).put(
						"returnReason", "productDefect"
					).build()
				).build()
			).build(),
			null);

		Assert.assertFalse(
			GetterUtil.getBoolean(results.get("validationCriteriaMet")));
	}

	@Inject
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	@Inject
	private CommerceOrderEngine _commerceOrderEngine;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Inject
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.internal.object.validation.rule.CommerceReturnItemPermissionObjectValidationRuleEngineImpl"
	)
	private ObjectValidationRuleEngine _objectValidationRuleEngine;

}