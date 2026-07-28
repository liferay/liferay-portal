/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v1_1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ricardo Couso
 */
public class PortletPreferencesUpgradeProcess extends UpgradeProcess {

	public PortletPreferencesUpgradeProcess(
		LayoutLocalService layoutLocalService) {

		_layoutLocalService = layoutLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_computeControlPanelPlids();

		if (_groupControlPanelPlids.isEmpty()) {
			return;
		}

		_upgradePortletPreferences();

		_deleteGroupControlPanelLayouts();
	}

	private void _computeControlPanelPlids() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select Layout.plid, Group_.groupKey from Layout inner ",
					"join Group_ on Layout.groupId = Group_.groupId where ",
					"Layout.type_ = ?"))) {

			preparedStatement.setString(1, LayoutConstants.TYPE_CONTROL_PANEL);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String groupKey = resultSet.getString("groupKey");

					long plid = resultSet.getLong("plid");

					Layout layout = _layoutLocalService.getLayout(plid);

					if (groupKey.equals(GroupConstants.CONTROL_PANEL)) {
						_companyControlPanelPlids.put(
							layout.getCompanyId(), plid);
					}
					else {
						_groupControlPanelPlids.put(layout.getGroupId(), plid);
					}
				}
			}
		}
	}

	private void _deleteGroupControlPanelLayouts() throws Exception {
		for (Long groupControlPanelLayoutPlid :
				_groupControlPanelPlids.values()) {

			try {
				_layoutLocalService.deleteLayout(groupControlPanelLayoutPlid);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private Map<Long, Long> _getPortletPreferencesMap(
			long companyId, long groupId, String namespace)
		throws Exception {

		Map<Long, Long> portletPreferencesMap = new HashMap<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select PortletPreferences.portletPreferencesId, ",
					"PortletPreferences.plid from PortletPreferences inner ",
					"join Layout on PortletPreferences.plid = Layout.plid ",
					"where PortletPreferences.portletId like ",
					"CONCAT('%_INSTANCE_', ?) and (Layout.groupId = ? or ",
					"PortletPreferences.plid = ?)"))) {

			preparedStatement.setString(1, namespace);
			preparedStatement.setLong(2, groupId);
			preparedStatement.setLong(
				3,
				_companyControlPanelPlids.getOrDefault(
					companyId, LayoutConstants.DEFAULT_PLID));

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				long portletPreferencesId = resultSet.getLong(
					"portletPreferencesId");
				long portletPreferencesPlid = resultSet.getLong("plid");

				portletPreferencesMap.put(
					portletPreferencesId, portletPreferencesPlid);
			}
		}

		return portletPreferencesMap;
	}

	private void _upgradePortletPreferences() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select groupId, companyId, classPK, namespace from " +
					"FragmentEntryLink");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"delete from PortletPreferences where " +
						"portletPreferencesId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update PortletPreferences set plid = ? where " +
						"portletPreferencesId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long groupId = resultSet.getLong("groupId");
				long companyId = resultSet.getLong("companyId");
				long classPK = resultSet.getLong("classPK");
				String namespace = resultSet.getString("namespace");

				try {
					Map<Long, Long> portletPreferencesMap =
						_getPortletPreferencesMap(
							companyId, groupId, namespace);

					if (portletPreferencesMap.isEmpty()) {
						continue;
					}

					List<Long> companyPortletPreferencesIds = new ArrayList<>();
					List<Long> groupPortletPreferencesIds = new ArrayList<>();
					List<Long> layoutPortletPreferencesIds = new ArrayList<>();

					long companyControlPanelPlid =
						_companyControlPanelPlids.getOrDefault(
							companyId, LayoutConstants.DEFAULT_PLID);
					long groupControlPanelPlid =
						_groupControlPanelPlids.getOrDefault(
							groupId, LayoutConstants.DEFAULT_PLID);
					long layoutPlid = classPK;

					for (Map.Entry<Long, Long> entry :
							portletPreferencesMap.entrySet()) {

						Long portletPreferencesId = entry.getKey();
						Long portletPreferencesPlid = entry.getValue();

						if (portletPreferencesPlid == companyControlPanelPlid) {
							companyPortletPreferencesIds.add(
								portletPreferencesId);
						}
						else if (portletPreferencesPlid ==
									groupControlPanelPlid) {

							groupPortletPreferencesIds.add(
								portletPreferencesId);
						}
						else if (portletPreferencesPlid == layoutPlid) {
							layoutPortletPreferencesIds.add(
								portletPreferencesId);
						}
					}

					if (ListUtil.isNotEmpty(groupPortletPreferencesIds)) {
						for (Long companyPortletPreferencesId :
								companyPortletPreferencesIds) {

							preparedStatement2.setLong(
								1, companyPortletPreferencesId);
							preparedStatement2.addBatch();
						}

						for (Long layoutPortletPreferencesId :
								layoutPortletPreferencesIds) {

							preparedStatement2.setLong(
								1, layoutPortletPreferencesId);
							preparedStatement2.addBatch();
						}

						for (Long groupPortletPreferencesId :
								groupPortletPreferencesIds) {

							preparedStatement3.setLong(1, classPK);
							preparedStatement3.setLong(
								2, groupPortletPreferencesId);

							preparedStatement3.addBatch();
						}
					}
					else if (ListUtil.isNotEmpty(
								companyPortletPreferencesIds)) {

						for (Long layoutPortletPreferencesId :
								layoutPortletPreferencesIds) {

							preparedStatement2.setLong(
								1, layoutPortletPreferencesId);
							preparedStatement2.addBatch();
						}

						for (Long companyPortletPreferencesId :
								companyPortletPreferencesIds) {

							preparedStatement3.setLong(1, classPK);
							preparedStatement3.setLong(
								2, companyPortletPreferencesId);

							preparedStatement3.addBatch();
						}
					}
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPreferencesUpgradeProcess.class);

	private final Map<Long, Long> _companyControlPanelPlids = new HashMap<>();
	private final Map<Long, Long> _groupControlPanelPlids = new HashMap<>();
	private final LayoutLocalService _layoutLocalService;

}