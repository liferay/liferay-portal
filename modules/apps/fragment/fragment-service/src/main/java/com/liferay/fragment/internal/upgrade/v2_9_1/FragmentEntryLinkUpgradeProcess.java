/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v2_9_1;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentEntryLinkUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, fragmentEntryLinkId, editableValues " +
					"from FragmentEntryLink");

			ResultSet resultSet = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update FragmentEntryLink set editableValues = ? where " +
						"ctCollectionId = ? and fragmentEntryLinkId = ?")) {

			while (resultSet.next()) {
				long ctCollectionId = resultSet.getLong("ctCollectionId");
				long fragmentEntryLinkId = resultSet.getLong(
					"fragmentEntryLinkId");

				String editableValues = resultSet.getString("editableValues");

				preparedStatement2.setString(
					1, _stripEmptyFragmentEntryProcessor(editableValues));

				preparedStatement2.setLong(2, ctCollectionId);
				preparedStatement2.setLong(3, fragmentEntryLinkId);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private String _stripEmptyFragmentEntryProcessor(String editableValues) {
		try {
			JSONObject editableValuesJSONObject =
				JSONFactoryUtil.createJSONObject(editableValues);

			for (String fragmentEntryProcessorKey :
					_FRAGMENT_ENTRY_PROCESSOR_KEYS) {

				JSONObject fragmentEntryProcessorJSONObject =
					editableValuesJSONObject.getJSONObject(
						fragmentEntryProcessorKey);

				if ((fragmentEntryProcessorJSONObject == null) ||
					(fragmentEntryProcessorJSONObject.length() != 0)) {

					continue;
				}

				editableValuesJSONObject.remove(fragmentEntryProcessorKey);
			}

			return editableValuesJSONObject.toString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return editableValues;
	}

	private static final String[] _FRAGMENT_ENTRY_PROCESSOR_KEYS = {
		FragmentEntryProcessorConstants.
			KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
		FragmentEntryProcessorConstants.KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
		FragmentEntryProcessorConstants.KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR
	};

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryLinkUpgradeProcess.class);

}