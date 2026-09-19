/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.io.exporter;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Leonardo Barros
 */
public final class DDMFormInstanceRecordWriterRequest {

	public List<Map<String, String>> getDDMFormFieldValues() {
		return _ddmFormFieldValues;
	}

	public Map<String, DDMFormField> getDDMFormFields() {
		return _ddmFormFields;
	}

	public Map<String, String> getDDMFormFieldsLabel() {
		return _ddmFormFieldsLabel;
	}

	public static class Builder {

		public static Builder newBuilder(
			Map<String, String> ddmFormFieldsLabel,
			List<Map<String, String>> ddmFormFieldValues) {

			return new Builder(ddmFormFieldsLabel, ddmFormFieldValues);
		}

		public DDMFormInstanceRecordWriterRequest build() {
			_formatLabels(
				_ddmFormInstanceRecordWriterRequest._ddmFormFieldsLabel);

			return _ddmFormInstanceRecordWriterRequest;
		}

		public Builder withDDMFormFields(
			Map<String, DDMFormField> ddmFormFields) {

			_ddmFormInstanceRecordWriterRequest._ddmFormFields = ddmFormFields;

			return this;
		}

		private Builder(
			Map<String, String> ddmFormFieldsLabel,
			List<Map<String, String>> ddmFormFieldValues) {

			_ddmFormInstanceRecordWriterRequest._ddmFormFieldsLabel =
				ddmFormFieldsLabel;

			_ddmFormInstanceRecordWriterRequest._ddmFormFieldValues =
				ddmFormFieldValues;
		}

		private String _formatLabelString(String fieldName, String label) {
			return StringBundler.concat(
				label, StringPool.SPACE, StringPool.OPEN_PARENTHESIS, fieldName,
				StringPool.CLOSE_PARENTHESIS);
		}

		private Map<String, String> _formatLabels(
			Map<String, String> ddmFormFieldsLabel) {

			Map<String, String> currentDDMFormFieldsLabel = new HashMap<>();

			ddmFormFieldsLabel.forEach(
				(fieldName, label) -> {
					if (!currentDDMFormFieldsLabel.containsKey(label)) {
						ddmFormFieldsLabel.put(fieldName, label);
					}
					else {
						String previousFieldName =
							currentDDMFormFieldsLabel.get(label);

						ddmFormFieldsLabel.put(
							previousFieldName,
							_formatLabelString(previousFieldName, label));

						ddmFormFieldsLabel.put(
							fieldName, _formatLabelString(fieldName, label));
					}

					currentDDMFormFieldsLabel.put(label, fieldName);
				});

			return ddmFormFieldsLabel;
		}

		private final DDMFormInstanceRecordWriterRequest
			_ddmFormInstanceRecordWriterRequest =
				new DDMFormInstanceRecordWriterRequest();

	}

	private DDMFormInstanceRecordWriterRequest() {
	}

	private List<Map<String, String>> _ddmFormFieldValues;
	private Map<String, DDMFormField> _ddmFormFields;
	private Map<String, String> _ddmFormFieldsLabel;

}