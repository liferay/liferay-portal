/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;

import java.util.List;
import java.util.Map;

/**
 * @author Luis Ortiz
 */
public class LayoutDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		DataCleanupPreupgradeProcess layoutRelatedDataCleanupPreupgradeProcess =
			_getLayoutRelatedDataDataCleanupPreupgradeProcess();

		DataCleanupPreupgradeProcess layoutSelfDataCleanupPreupgradeProcess =
			_getLayoutSelfDataDataCleanupPreupgradeProcess();

		Map<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
			dataCleanupPreupgradeProcessesMap =
				LinkedHashMapBuilder.
					<DataCleanupPreupgradeProcess,
					 List<DataCleanupPreupgradeProcess>>put(
						_getLayoutLayoutPageTemplateStructureDataCleanupPreupgradeProcess(),
						dependsOn(layoutRelatedDataCleanupPreupgradeProcess)
					).put(
						layoutRelatedDataCleanupPreupgradeProcess,
						dependsOn(layoutSelfDataCleanupPreupgradeProcess)
					).put(
						layoutSelfDataCleanupPreupgradeProcess, dependsOn()
					).build();

		List<DataCleanupPreupgradeProcess> dataCleanupPreupgradeProcesses =
			getSortedDataCleanupPreupgradeProcesses(
				dataCleanupPreupgradeProcessesMap);

		for (DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess :
				dataCleanupPreupgradeProcesses) {

			dataCleanupPreupgradeProcess.upgrade();
		}
	}

	private DataCleanupPreupgradeProcess
		_getLayoutLayoutPageTemplateStructureDataCleanupPreupgradeProcess() {

		return new DataCleanupPreupgradeProcess(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "layoutPageTemplateStructureId",
				"LayoutPageTemplateStructureRel",
				"layoutPageTemplateStructureId",
				"LayoutPageTemplateStructure"));
	}

	private DataCleanupPreupgradeProcess
			_getLayoutRelatedDataDataCleanupPreupgradeProcess()
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		DataCleanupPreupgradeProcess
			layoutRevisionDataCleanupPreupgradeProcess =
				new DataCleanupPreupgradeProcess();

		if (dbInspector.hasTable("LayoutRevision")) {
			layoutRevisionDataCleanupPreupgradeProcess =
				new FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
					"not exists (select 1 from (select layoutRevisionId from " +
						"LayoutRevision) temp where temp.layoutRevisionId = " +
							"[$SOURCE_TABLE_ALIAS$].plid)",
					new String[0], "plid", new String[] {"plid"}, "Layout");
		}

		return new DataCleanupPreupgradeProcess(
			layoutRevisionDataCleanupPreupgradeProcess,
			new FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
				StringBundler.concat(
					"[$SOURCE_TABLE_ALIAS$].classNameId in (select ",
					"classNameId from ClassName_ where value = '",
					Layout.class.getName(), "' or value like '",
					Layout.class.getName(), "-%')"),
				new String[] {"classNameId"}, "classPK", new String[] {"plid"},
				"Layout"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null,
				StringBundler.concat(
					"[$SOURCE_TABLE_ALIAS$].scope = ",
					ResourceConstants.SCOPE_INDIVIDUAL, " and ",
					"[$SOURCE_TABLE_ALIAS$].name = '", Layout.class.getName(),
					"'"),
				"primKeyId", "ResourcePermission", "plid", "Layout"),
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				SQLTransformer.transform(
					StringBundler.concat(
						"CAST_TEXT([$TARGET_TABLE_ALIAS$].plid) = SUBSTR(",
						"[$SOURCE_TABLE_ALIAS$].primKey, 1, INSTR(",
						"[$SOURCE_TABLE_ALIAS$].primKey, '",
						PortletConstants.LAYOUT_SEPARATOR, "') - 1)")),
				StringBundler.concat(
					"[$SOURCE_TABLE_ALIAS$].scope = ",
					ResourceConstants.SCOPE_INDIVIDUAL, " and ",
					"[$SOURCE_TABLE_ALIAS$].name = '", Layout.class.getName(),
					"' and [$SOURCE_TABLE_ALIAS$].primKey like '%",
					PortletConstants.LAYOUT_SEPARATOR, "%'",
					dbInspector.hasColumn("ResourcePermission", "primKeyId") ?
						" and [$SOURCE_TABLE_ALIAS$].primKeyId = 0" : ""),
				"primKey", "ResourcePermission", "plid", "Layout"));
	}

	private DataCleanupPreupgradeProcess
		_getLayoutSelfDataDataCleanupPreupgradeProcess() {

		return new DataCleanupPreupgradeProcess(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null,
				StringBundler.concat(
					"[$SOURCE_TABLE_ALIAS$].classNameId = (select classNameId ",
					"from ClassName_ where value = '", Layout.class.getName(),
					"')"),
				"classPK", "Layout", "plid", "Layout"));
	}

}