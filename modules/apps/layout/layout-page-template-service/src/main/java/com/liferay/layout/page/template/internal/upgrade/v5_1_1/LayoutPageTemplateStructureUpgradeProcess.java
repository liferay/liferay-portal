/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v5_1_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Márk Gulácsy
 */
public class LayoutPageTemplateStructureUpgradeProcess extends UpgradeProcess {

	public LayoutPageTemplateStructureUpgradeProcess(
		LayoutLocalService layoutLocalService,
		UserLocalService userLocalService) {

		_layoutLocalService = layoutLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_deleteOrphanLayoutPageTemplateStructures();
		_processLayoutPageTemplateStructuresOfWidgetLayouts();
	}

	private void _deleteOrphanLayoutPageTemplateStructures() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					"select layoutPageTemplateStructureId from " +
						"LayoutPageTemplateStructure where classPK not in " +
							"(select plid from Layout)");
			PreparedStatement deletePreparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"delete from LayoutPageTemplateStructureRel where " +
						"layoutPageTemplateStructureId = ?")) {

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					deletePreparedStatement.setLong(
						1, resultSet.getLong("layoutPageTemplateStructureId"));

					deletePreparedStatement.addBatch();
				}
			}

			deletePreparedStatement.executeBatch();
		}

		runSQL(
			"delete from LayoutPageTemplateStructure where classPK not in " +
				"(select plid from Layout)");
	}

	private void _processLayoutPageTemplateStructuresOfWidgetLayouts()
		throws Exception {

		List<Long> plids = new ArrayList<>();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select LayoutPageTemplateStructure.ctCollectionId, ",
					"LayoutPageTemplateStructure.",
					"layoutPageTemplateStructureId, ",
					"LayoutPageTemplateStructure.classPK from ",
					"LayoutPageTemplateStructure inner join Layout on ",
					"LayoutPageTemplateStructure.ctCollectionId = ",
					"Layout.ctCollectionId and ",
					"LayoutPageTemplateStructure.classPK = Layout.plid and ",
					"Layout.type_ = ?"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"delete from LayoutPageTemplateStructure where " +
						"ctCollectionId = ? and " +
							"layoutPageTemplateStructureId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"delete from LayoutPageTemplateStructureRel where " +
						"ctCollectionId = ? and " +
							"layoutPageTemplateStructureId = ?")) {

			preparedStatement1.setString(1, LayoutConstants.TYPE_PORTLET);

			ResultSet resultSet = preparedStatement1.executeQuery();

			while (resultSet.next()) {
				plids.add(resultSet.getLong(3));

				long ctCollectionId = resultSet.getLong(1);

				preparedStatement2.setLong(1, ctCollectionId);

				long layoutPageTemplateStructureId = resultSet.getLong(2);

				preparedStatement2.setLong(2, layoutPageTemplateStructureId);

				preparedStatement2.addBatch();

				preparedStatement3.setLong(1, ctCollectionId);
				preparedStatement3.setLong(2, layoutPageTemplateStructureId);

				preparedStatement3.addBatch();
			}

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();
		}

		ServiceContext serviceContext = new ServiceContext();

		for (long plid : plids) {
			Layout layout = _layoutLocalService.fetchLayout(plid);

			if (layout.getStatus() != WorkflowConstants.STATUS_DRAFT) {
				continue;
			}

			User user = _userLocalService.fetchUser(layout.getUserId());

			if (user == null) {
				user = _userLocalService.getGuestUser(layout.getCompanyId());
			}

			_layoutLocalService.updateStatus(
				user.getUserId(), plid, WorkflowConstants.STATUS_APPROVED,
				serviceContext);
		}
	}

	private final LayoutLocalService _layoutLocalService;
	private final UserLocalService _userLocalService;

}