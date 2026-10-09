/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v4_0_0;

import com.liferay.layout.util.CollectionPaginationUtil;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.List;

/**
 * @author Rubén Pulido
 */
public class LayoutPageTemplateStructureRelUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeLayoutPageTemplateStructureRel();
	}

	private JSONObject _getItemsJSONObject(String data) {
		try {
			JSONObject layoutStructureJSONObject =
				JSONFactoryUtil.createJSONObject(data);

			return layoutStructureJSONObject.getJSONObject("items");
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return JSONFactoryUtil.createJSONObject();
	}

	private String _getPaginationType(
		CollectionStyledLayoutStructureItem collectionStyledLayoutStructureItem,
		JSONObject itemsJSONObject) {

		JSONObject itemJSONObject = itemsJSONObject.getJSONObject(
			collectionStyledLayoutStructureItem.getItemId());

		JSONObject configJSONObject = itemJSONObject.getJSONObject("config");

		return configJSONObject.getString("paginationType");
	}

	private String _upgradeLayoutData(String data) {
		JSONObject itemsJSONObject = _getItemsJSONObject(data);

		LayoutStructure layoutStructure = LayoutStructure.of(data);

		List<LayoutStructureItem> layoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		for (LayoutStructureItem layoutStructureItem : layoutStructureItems) {
			if (!(layoutStructureItem instanceof
					CollectionStyledLayoutStructureItem)) {

				continue;
			}

			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem =
					(CollectionStyledLayoutStructureItem)layoutStructureItem;

			collectionStyledLayoutStructureItem.setDisplayAllItems(false);

			String paginationType = _getPaginationType(
				collectionStyledLayoutStructureItem, itemsJSONObject);

			if (CollectionPaginationUtil.isPaginationEnabled(paginationType)) {
				collectionStyledLayoutStructureItem.setDisplayAllPages(
					collectionStyledLayoutStructureItem.isShowAllItems());

				int numberOfItemsPerPage =
					collectionStyledLayoutStructureItem.
						getNumberOfItemsPerPage();

				if (numberOfItemsPerPage > 0) {
					int numberOfItems =
						collectionStyledLayoutStructureItem.getNumberOfItems();

					collectionStyledLayoutStructureItem.setNumberOfPages(
						(int)Math.ceil(
							numberOfItems / (double)numberOfItemsPerPage));
				}
			}
			else {
				paginationType = CollectionPaginationUtil.PAGINATION_TYPE_NONE;
			}

			collectionStyledLayoutStructureItem.setPaginationType(
				paginationType);
		}

		JSONObject jsonObject = layoutStructure.toJSONObject();

		return jsonObject.toString();
	}

	private void _upgradeLayoutPageTemplateStructureRel() throws Exception {
		try (Statement s = connection.createStatement();

			ResultSet resultSet = s.executeQuery(
				"select ctCollectionId, lPageTemplateStructureRelId, data_ " +
					"from LayoutPageTemplateStructureRel");

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update LayoutPageTemplateStructureRel set data_ = ? " +
						"where ctCollectionId = ? and " +
							"lPageTemplateStructureRelId = ?")) {

			while (resultSet.next()) {
				long layoutPageTemplateStructureRelId = resultSet.getLong(
					"lPageTemplateStructureRelId");

				long ctCollectionId = resultSet.getLong("ctCollectionId");

				String data = resultSet.getString("data_");

				preparedStatement.setString(1, _upgradeLayoutData(data));

				preparedStatement.setLong(2, ctCollectionId);
				preparedStatement.setLong(3, layoutPageTemplateStructureRelId);

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateStructureRelUpgradeProcess.class);

}