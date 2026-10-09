/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v2_13_1;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Portal;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author Rubén Pulido
 */
public class FragmentEntryLinkUpgradeProcess extends UpgradeProcess {

	public FragmentEntryLinkUpgradeProcess(
		JSONFactory jsonFactory, Portal portal) {

		_jsonFactory = jsonFactory;
		_portal = portal;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_doUpgrade();
		}
	}

	private void _doUpgrade() throws Exception {
		long dlFileEntryClassNameId = _portal.getClassNameId(
			DLFileEntry.class.getName());
		long fileEntryClassNameId = _portal.getClassNameId(
			FileEntry.class.getName());

		String sql = StringBundler.concat(
			"select FragmentEntryLink.ctCollectionId, ",
			"FragmentEntryLink.fragmentEntryLinkId, ",
			"FragmentEntryLink.editableValues from FragmentEntryLink where ",
			"editableValues like '%\"classNameId\":%\"", dlFileEntryClassNameId,
			"\"%'");

		processConcurrently(
			SQLTransformer.transform(sql),
			"update FragmentEntryLink set editableValues = ? where " +
				"ctCollectionId = ? and fragmentEntryLinkId = ?",
			resultSet -> new Object[] {
				resultSet.getLong("ctCollectionId"),
				resultSet.getLong("fragmentEntryLinkId"),
				GetterUtil.getString(resultSet.getString("editableValues"))
			},
			(values, preparedStatement) -> {
				long ctCollectionId = (Long)values[0];
				long fragmentEntryLinkId = (Long)values[1];

				String editableValues = (String)values[2];

				try {
					JSONObject editableValuesJSONObject =
						_jsonFactory.createJSONObject(editableValues);

					boolean modified =
						_processBackgroundFragmentEntryProcessorJSONObject(
							dlFileEntryClassNameId, fileEntryClassNameId,
							editableValuesJSONObject.getJSONObject(
								FragmentEntryProcessorConstants.
									KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR));

					if (_processEditableFragmentEntryProcessorJSONObject(
							dlFileEntryClassNameId, fileEntryClassNameId,
							editableValuesJSONObject.getJSONObject(
								FragmentEntryProcessorConstants.
									KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR))) {

						modified = true;
					}

					if (!modified) {
						return;
					}

					preparedStatement.setString(
						1, editableValuesJSONObject.toString());
					preparedStatement.setLong(2, ctCollectionId);
					preparedStatement.setLong(3, fragmentEntryLinkId);

					preparedStatement.addBatch();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			},
			null);
	}

	private boolean _processBackgroundFragmentEntryProcessorJSONObject(
		long dlFileEntryClassNameId, long fileEntryClassNameId,
		JSONObject jsonObject) {

		if (jsonObject == null) {
			return false;
		}

		boolean modified = false;

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String backgroundElementId = iterator.next();

			JSONObject backgroundElementJSONObject = jsonObject.getJSONObject(
				backgroundElementId);

			if (backgroundElementJSONObject == null) {
				continue;
			}

			Iterator<String> curIterator = backgroundElementJSONObject.keys();

			while (curIterator.hasNext()) {
				String backgroundElementItemId = curIterator.next();

				if (Objects.equals(backgroundElementItemId, "config")) {
					continue;
				}

				JSONObject backgroundElementItemJSONObject =
					backgroundElementJSONObject.getJSONObject(
						backgroundElementItemId);

				if (backgroundElementItemJSONObject == null) {
					continue;
				}

				String classNameId = backgroundElementItemJSONObject.getString(
					"classNameId");

				if (!Objects.equals(
						classNameId, String.valueOf(dlFileEntryClassNameId))) {

					continue;
				}

				backgroundElementItemJSONObject.put(
					"classNameId", String.valueOf(fileEntryClassNameId));

				modified = true;
			}
		}

		return modified;
	}

	private boolean _processEditableFragmentEntryProcessorJSONObject(
		long dlFileEntryClassNameId, long fileEntryClassNameId,
		JSONObject jsonObject) {

		if (jsonObject == null) {
			return false;
		}

		boolean modified = false;

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String editableElementId = iterator.next();

			JSONObject editableElementJSONObject = jsonObject.getJSONObject(
				editableElementId);

			if (editableElementJSONObject == null) {
				continue;
			}

			JSONObject configJSONObject =
				editableElementJSONObject.getJSONObject("config");

			if (configJSONObject == null) {
				continue;
			}

			String classNameId = configJSONObject.getString("classNameId");

			if (!Objects.equals(
					classNameId, String.valueOf(dlFileEntryClassNameId))) {

				continue;
			}

			configJSONObject.put(
				"classNameId", String.valueOf(fileEntryClassNameId));

			modified = true;
		}

		return modified;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryLinkUpgradeProcess.class);

	private final JSONFactory _jsonFactory;
	private final Portal _portal;

}