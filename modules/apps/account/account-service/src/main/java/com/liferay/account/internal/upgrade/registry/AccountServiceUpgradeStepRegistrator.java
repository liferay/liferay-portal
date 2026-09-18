/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.upgrade.registry;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.account.internal.upgrade.v1_1_0.SchemaUpgradeProcess;
import com.liferay.account.internal.upgrade.v2_11_2.RoleResourceUpgradeProcess;
import com.liferay.account.internal.upgrade.v2_12_1.AccountEntryResourcePermissionUpgradeProcess;
import com.liferay.account.internal.upgrade.v2_4_0.AccountGroupResourceUpgradeProcess;
import com.liferay.account.internal.upgrade.v2_5_0.AccountRoleResourceUpgradeProcess;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseUuidUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = UpgradeStepRegistrator.class)
public class AccountServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.0.1",
			new com.liferay.account.internal.upgrade.v1_0_1.
				RoleUpgradeProcess());

		registry.register(
			"1.0.1", "1.0.2",
			new com.liferay.account.internal.upgrade.v1_0_2.
				RoleUpgradeProcess());

		registry.register(
			"1.0.2", "1.0.3",
			new com.liferay.account.internal.upgrade.v1_0_3.
				RoleUpgradeProcess());

		registry.register(
			"1.0.3", "1.0.4",
			new com.liferay.account.internal.upgrade.v1_1_0.
				AccountEntryUpgradeProcess());

		registry.register("1.0.4", "1.1.0", new SchemaUpgradeProcess());

		registry.register(
			"1.1.0", "1.1.1",
			new com.liferay.account.internal.upgrade.v1_1_1.
				AccountEntryUpgradeProcess());

		registry.register("1.1.1", "1.2.0", new DummyUpgradeStep());

		registry.register(
			"1.2.0", "1.2.1",
			new com.liferay.account.internal.upgrade.v1_2_1.
				RoleUpgradeProcess());

		registry.register(
			"1.2.1", "1.3.0",
			UpgradeProcessFactory.addColumns(
				"AccountEntry", "defaultBillingAddressId LONG",
				"defaultShippingAddressId LONG",
				"emailAddress VARCHAR(254) null",
				"taxExemptionCode VARCHAR(75) null"),
			UpgradeProcessFactory.addColumns(
				"AccountGroup", "defaultAccountGroup BOOLEAN"));

		registry.register(
			"1.3.0", "2.0.0",
			new com.liferay.account.internal.upgrade.v2_0_0.
				AccountGroupAccountEntryRelUpgradeProcess());

		registry.register(
			"2.0.0", "2.1.0",
			new com.liferay.account.internal.upgrade.v2_1_0.
				AccountGroupUpgradeProcess());

		registry.register(
			"2.1.0", "2.2.0",
			new com.liferay.account.internal.upgrade.v2_2_0.
				AccountGroupRelUpgradeProcess(_companyLocalService));

		registry.register(
			"2.2.0", "2.3.0",
			UpgradeProcessFactory.runSQL(
				"delete from ResourceAction where name = 'com.liferay.account'",
				"delete from ResourcePermission where name = " +
					"'com.liferay.account'"));

		registry.register(
			"2.3.0", "2.4.0",
			new AccountGroupResourceUpgradeProcess(_resourceLocalService));

		registry.register(
			"2.4.0", "2.5.0",
			new AccountRoleResourceUpgradeProcess(_resourceLocalService));

		registry.register(
			"2.5.0", "2.6.0",
			UpgradeProcessFactory.addColumns(
				"AccountEntry", "defaultDeliveryCTermEntryId LONG",
				"defaultPaymentCTermEntryId LONG"));

		registry.register(
			"2.6.0", "2.7.0",
			UpgradeProcessFactory.addColumns(
				"AccountEntry", "defaultCPaymentMethodKey VARCHAR(75)"));

		registry.register(
			"2.7.0", "2.7.1",
			UpgradeProcessFactory.runSQL(
				"delete from AccountEntryUserRel where accountEntryId = " +
					AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT));

		registry.register(
			"2.7.1", "2.8.0",
			new BaseUuidUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"AccountEntry", "AccountGroup"};
				}

			});

		registry.register(
			"2.8.0", "2.8.1",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"AccountEntry", "AccountGroup"};
				}

			});

		registry.register(
			"2.8.1", "2.9.0",
			UpgradeProcessFactory.addColumns(
				"AccountEntry", "restrictMembership BOOLEAN"));

		registry.register(
			"2.9.0", "2.10.0",
			UpgradeProcessFactory.addColumns(
				"AccountEntry", "statusByUserId LONG",
				"statusByUserName VARCHAR(75) null", "statusDate DATE null"));

		registry.register(
			"2.10.0", "2.10.1",
			new com.liferay.account.internal.upgrade.v2_10_1.
				AccountRoleResourceUpgradeProcess(
					_resourceActionLocalService,
					_resourcePermissionLocalService));

		registry.register(
			"2.10.1", "2.10.2",
			new com.liferay.account.internal.upgrade.v2_10_2.
				AccountRoleResourceUpgradeProcess(
					_resourceActionLocalService,
					_resourcePermissionLocalService));

		registry.register(
			"2.10.2", "2.10.3",
			UpgradeProcessFactory.alterColumnType(
				"AccountEntry", "name", "VARCHAR(250) null"));

		registry.register(
			"2.10.3", "2.10.4",
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"delete from ListType where type_ = '",
					AccountListTypeConstants.ACCOUNT_ENTRY_PHONE,
					"' and name = 'tool-free'")));

		registry.register(
			"2.10.4", "2.11.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"AccountRole"};
				}

			});

		registry.register(
			"2.11.0", "2.11.1",
			new com.liferay.account.internal.upgrade.v2_11_1.
				AccountRoleResourceUpgradeProcess());

		registry.register(
			"2.11.1", "2.11.2",
			new RoleResourceUpgradeProcess(
				_resourceActionLocalService, _resourcePermissionLocalService));

		registry.register(
			"2.11.2", "2.12.0",
			UpgradeProcessFactory.addColumns("AccountGroup", "status INTEGER"),
			UpgradeProcessFactory.runSQL("update AccountGroup set status = 0"));

		registry.register(
			"2.12.0", "2.12.1",
			new AccountEntryResourcePermissionUpgradeProcess(
				_resourceActionLocalService, _resourcePermissionLocalService));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}