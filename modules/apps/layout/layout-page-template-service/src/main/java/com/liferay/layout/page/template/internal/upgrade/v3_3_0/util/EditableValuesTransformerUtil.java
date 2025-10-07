/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v3_3_0.util;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class EditableValuesTransformerUtil {

	public static String getEditableValues(
		String editableValues, long segmentsExperienceId) {

		JSONObject newEditableValuesJSONObject =
			JSONFactoryUtil.createJSONObject();

		try {
			JSONObject editableValuesJSONObject =
				JSONFactoryUtil.createJSONObject(editableValues);

			Iterator<String> keysIterator = editableValuesJSONObject.keys();

			while (keysIterator.hasNext()) {
				String editableProcessorKey = keysIterator.next();

				Object editableProcessorObject = editableValuesJSONObject.get(
					editableProcessorKey);

				if (!(editableProcessorObject instanceof JSONObject)) {
					newEditableValuesJSONObject.put(
						editableProcessorKey, editableProcessorObject);

					continue;
				}

				JSONObject editableProcessorJSONObject =
					(JSONObject)editableProcessorObject;

				if (Objects.equals(
						editableProcessorKey,
						FragmentEntryProcessorConstants.
							KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR)) {

					editableProcessorJSONObject =
						_getFreeMarkerFragmentEntryProcessorJSONObject(
							editableProcessorJSONObject, segmentsExperienceId);
				}
				else if (editableProcessorJSONObject.length() > 0) {
					editableProcessorJSONObject =
						_getFragmentEntryProcessorJSONObject(
							editableProcessorJSONObject, segmentsExperienceId);
				}

				if (editableProcessorJSONObject.length() <= 0) {
					continue;
				}

				newEditableValuesJSONObject.put(
					editableProcessorKey, editableProcessorJSONObject);
			}
		}
		catch (JSONException jsonException) {
			if (_log.isWarnEnabled()) {
				_log.warn(jsonException);
			}
		}

		return newEditableValuesJSONObject.toString();
	}

	private static JSONObject _getFragmentEntryProcessorJSONObject(
		JSONObject editableProcessorJSONObject, long segmentsExperienceId) {

		JSONObject newEditableProcessorJSONObject =
			JSONFactoryUtil.createJSONObject();

		Iterator<String> editableKeysIterator =
			editableProcessorJSONObject.keys();

		while (editableKeysIterator.hasNext()) {
			String editableKey = editableKeysIterator.next();

			JSONObject editableJSONObject =
				editableProcessorJSONObject.getJSONObject(editableKey);

			if (editableJSONObject == null) {
				newEditableProcessorJSONObject.put(
					editableKey, JSONFactoryUtil.createJSONObject());

				continue;
			}

			JSONObject newEditableJSONObject =
				JSONFactoryUtil.createJSONObject();

			boolean processedSegmentsExperienceId = false;

			Iterator<String> valueKeysIterator = editableJSONObject.keys();

			while (valueKeysIterator.hasNext()) {
				String valueKey = valueKeysIterator.next();

				if (Objects.equals(
						valueKey, _ID_PREFIX + segmentsExperienceId)) {

					JSONObject valueJSONObject =
						editableJSONObject.getJSONObject(valueKey);

					if (valueJSONObject == null) {
						continue;
					}

					Iterator<String> segmentedValueKeysIterator =
						valueJSONObject.keys();

					while (segmentedValueKeysIterator.hasNext()) {
						String segmentedValueKey =
							segmentedValueKeysIterator.next();

						newEditableJSONObject.put(
							segmentedValueKey,
							valueJSONObject.get(segmentedValueKey));
					}

					processedSegmentsExperienceId = true;
				}
				else if (!valueKey.startsWith(_ID_PREFIX)) {
					if (processedSegmentsExperienceId &&
						newEditableJSONObject.has(valueKey)) {

						continue;
					}

					newEditableJSONObject.put(
						valueKey, editableJSONObject.get(valueKey));
				}
			}

			newEditableProcessorJSONObject.put(
				editableKey, newEditableJSONObject);
		}

		return newEditableProcessorJSONObject;
	}

	private static JSONObject _getFreeMarkerFragmentEntryProcessorJSONObject(
		JSONObject jsonObject, long segmentsExperienceId) {

		if (!jsonObject.has(_ID_PREFIX + segmentsExperienceId)) {
			JSONObject newJSONObject = JSONFactoryUtil.createJSONObject();

			Iterator<String> valueKeysIterator = jsonObject.keys();

			while (valueKeysIterator.hasNext()) {
				String valueKey = valueKeysIterator.next();

				if (!valueKey.startsWith(_ID_PREFIX)) {
					newJSONObject.put(valueKey, jsonObject.get(valueKey));
				}
			}

			return newJSONObject;
		}

		return jsonObject.getJSONObject(_ID_PREFIX + segmentsExperienceId);
	}

	private static final String _ID_PREFIX = "segments-experience-id-";

	private static final Log _log = LogFactoryUtil.getLog(
		EditableValuesTransformerUtil.class);

}