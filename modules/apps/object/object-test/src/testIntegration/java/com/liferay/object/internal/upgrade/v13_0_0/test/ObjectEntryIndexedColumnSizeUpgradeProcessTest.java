/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.BaseIndexedColumnSizeUpgradeProcessTestCase;

import org.junit.runner.RunWith;

/**
 * @author Roselaine Marques
 */
@RunWith(Arquillian.class)
public class ObjectEntryIndexedColumnSizeUpgradeProcessTest
	extends BaseIndexedColumnSizeUpgradeProcessTestCase {

	@Override
	protected String getColumnName() {
		return "externalReferenceCode";
	}

	@Override
	protected String getIndexName() {
		return "IX_11E61545";
	}

	@Override
	protected String getInsertSQL(
		String columnName, String columnValue, long id, String tableName) {

		return StringBundler.concat(
			"insert into ", tableName, " (mvccVersion, ",
			getPrimaryKeyColumnName(), ", ", columnName, ") values (0, ", id,
			", '", columnValue, "')");
	}

	@Override
	protected int getNewColumnLength() {
		return 500;
	}

	@Override
	protected int getOldColumnLength() {
		return 1000;
	}

	@Override
	protected String getPrimaryKeyColumnName() {
		return "objectEntryId";
	}

	@Override
	protected String getTableName() {
		return "ObjectEntry";
	}

	@Override
	protected String getUpgradeProcessClassName() {
		return "com.liferay.object.internal.upgrade.v13_0_0." +
			"ObjectEntryIndexedColumnSizeUpgradeProcess";
	}

	@Override
	protected UpgradeStepRegistrator getUpgradeStepRegistrator() {
		return _upgradeStepRegistrator;
	}

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}