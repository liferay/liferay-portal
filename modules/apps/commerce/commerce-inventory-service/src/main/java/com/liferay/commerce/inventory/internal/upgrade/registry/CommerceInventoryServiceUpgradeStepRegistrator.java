/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.internal.upgrade.registry;

import com.liferay.commerce.inventory.internal.upgrade.v2_0_0.CommerceInventoryAuditUpgradeProcess;
import com.liferay.commerce.inventory.internal.upgrade.v2_11_5.CommercePermissionUpgradeProcess;
import com.liferay.commerce.inventory.internal.upgrade.v2_1_0.MVCCUpgradeProcess;
import com.liferay.commerce.inventory.internal.upgrade.v2_6_0.util.CommerceInventoryWarehouseRelTable;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryAuditModelImpl;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityModelImpl;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseItemModelImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseUuidUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(service = UpgradeStepRegistrator.class)
public class CommerceInventoryServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		if (_log.isInfoEnabled()) {
			_log.info("Commerce inventory upgrade step registrator started");
		}

		registry.register(
			"1.0.0", "1.1.0",
			UpgradeProcessFactory.addColumns(
				"CIWarehouseItem", "externalReferenceCode VARCHAR(75)"));

		registry.register("1.1.0", "1.2.0", new DummyUpgradeProcess());

		registry.register(
			"1.2.0", "2.0.0", new CommerceInventoryAuditUpgradeProcess());

		registry.register("2.0.0", "2.1.0", new MVCCUpgradeProcess());

		registry.register(
			"2.1.0", "2.2.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"CIAudit", "CIBookedQuantity", "CIReplenishmentItem",
						"CIWarehouse", "CIWarehouseGroupRel", "CIWarehouseItem"
					};
				}

			});

		registry.register(
			"2.2.0", "2.3.0",
			new BaseUuidUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"CIWarehouse", "CIWarehouseItem"};
				}

			});

		registry.register(
			"2.3.0", "2.3.1",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"CIWarehouse", "CIWarehouseItem"};
				}

			});

		registry.register(
			"2.3.1", "2.4.0",
			new BaseUuidUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"CIReplenishmentItem"};
				}

			});

		registry.register(
			"2.4.0", "2.4.1",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"CIReplenishmentItem"};
				}

			});

		registry.register(
			"2.4.1", "2.5.0",
			new com.liferay.commerce.inventory.internal.upgrade.v2_5_0.
				CommerceInventoryWarehouseUpgradeProcess(
					_resourceActionLocalService));

		registry.register(
			"2.5.0", "2.5.1",
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"delete from CIReplenishmentItem where ",
					"CIReplenishmentItem.sku not in (select ",
					"CIWarehouseItem.sku from CIWarehouseItem where ",
					"CIReplenishmentItem.companyId = ",
					"CIWarehouseItem.companyId and ",
					"CIReplenishmentItem.commerceInventoryWarehouseId = ",
					"CIWarehouseItem.commerceInventoryWarehouseId)")));

		registry.register(
			"2.5.1", "2.5.2", CommerceInventoryWarehouseRelTable.create());

		registry.register(
			"2.5.2", "2.6.0",
			new com.liferay.commerce.inventory.internal.upgrade.v2_6_0.
				CommerceInventoryWarehouseUpgradeProcess());

		registry.register(
			"2.6.0", "2.6.1",
			new com.liferay.commerce.inventory.internal.upgrade.v2_6_1.
				CommercePermissionUpgradeProcess(
					_resourcePermissionLocalService, _roleLocalService));

		registry.register(
			"2.6.1", "2.7.0",
			UpgradeProcessFactory.addColumns(
				CommerceInventoryAuditModelImpl.TABLE_NAME,
				"unitOfMeasureKey VARCHAR(75) null"),
			UpgradeProcessFactory.addColumns(
				CommerceInventoryBookedQuantityModelImpl.TABLE_NAME,
				"unitOfMeasureKey VARCHAR(75) null"),
			UpgradeProcessFactory.addColumns(
				CommerceInventoryReplenishmentItemModelImpl.TABLE_NAME,
				"unitOfMeasureKey VARCHAR(75) null"),
			UpgradeProcessFactory.addColumns(
				CommerceInventoryWarehouseItemModelImpl.TABLE_NAME,
				"unitOfMeasureKey VARCHAR(75) null"));

		registry.register(
			"2.7.0", "2.8.0",
			UpgradeProcessFactory.alterColumnType(
				"CIAudit", "quantity", "BIGDECIMAL null"));

		registry.register(
			"2.8.0", "2.9.0",
			UpgradeProcessFactory.alterColumnType(
				"CIWarehouseItem", "quantity", "BIGDECIMAL null"),
			UpgradeProcessFactory.alterColumnType(
				"CIWarehouseItem", "reservedQuantity", "BIGDECIMAL null"));

		registry.register(
			"2.9.0", "2.10.0",
			UpgradeProcessFactory.alterColumnType(
				CommerceInventoryReplenishmentItemModelImpl.TABLE_NAME,
				"quantity", "BIGDECIMAL null"));

		registry.register(
			"2.10.0", "2.11.0",
			UpgradeProcessFactory.alterColumnType(
				CommerceInventoryBookedQuantityModelImpl.TABLE_NAME, "quantity",
				"BIGDECIMAL null"));

		registry.register("2.11.0", "2.11.1", new DummyUpgradeStep());

		registry.register(
			"2.11.1", "2.11.2",
			new com.liferay.commerce.inventory.internal.upgrade.v2_11_2.
				CommercePermissionUpgradeProcess(
					_resourceActionLocalService,
					_resourcePermissionLocalService));

		registry.register("2.11.2", "2.11.3", new DummyUpgradeStep());

		registry.register(
			"2.11.3", "2.11.4",
			UpgradeProcessFactory.dropColumns(
				"CIWarehouseGroupRel", "mvccVersion"));

		registry.register(
			"2.11.4", "2.11.5",
			new CommercePermissionUpgradeProcess(
				_resourceActionLocalService, _resourcePermissionLocalService));

		if (_log.isInfoEnabled()) {
			_log.info("Commerce inventory upgrade step registrator finished");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceInventoryServiceUpgradeStepRegistrator.class);

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}