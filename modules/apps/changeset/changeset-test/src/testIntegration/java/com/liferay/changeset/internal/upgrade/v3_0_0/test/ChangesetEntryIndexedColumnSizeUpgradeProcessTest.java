/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.internal.upgrade.v3_0_0.test;

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
public class ChangesetEntryIndexedColumnSizeUpgradeProcessTest
	extends BaseIndexedColumnSizeUpgradeProcessTestCase {

	@Override
	protected String getColumnName() {
		return "classExternalReferenceCode";
	}

	@Override
	protected String getIndexName() {
		return "IX_71B99FC2";
	}

	@Override
	protected String getInsertSQL(
		String columnName, String columnValue, long id, String tableName) {

		return StringBundler.concat(
			"insert into ", tableName, " (", getPrimaryKeyColumnName(), ", ",
			columnName, ") values (", id, ", '", columnValue, "')");
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
		return "changesetEntryId";
	}

	@Override
	protected String getTableName() {
		return "ChangesetEntry";
	}

	@Override
	protected String getUpgradeProcessClassName() {
		return "com.liferay.changeset.internal.upgrade.v3_0_0." +
			"ChangesetEntryIndexedColumnSizeUpgradeProcess";
	}

	@Override
	protected UpgradeStepRegistrator getUpgradeStepRegistrator() {
		return _upgradeStepRegistrator;
	}

	@Inject(
		filter = "component.name=com.liferay.changeset.internal.upgrade.registry.ChangesetServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}