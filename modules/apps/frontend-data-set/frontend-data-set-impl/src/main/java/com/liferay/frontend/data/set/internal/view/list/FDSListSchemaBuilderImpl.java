/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.view.list;

import com.liferay.frontend.data.set.view.list.FDSListSchema;
import com.liferay.frontend.data.set.view.list.FDSListSchemaBuilder;
import com.liferay.frontend.data.set.view.list.FDSListSchemaLabelField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mylena Monte
 */
public class FDSListSchemaBuilderImpl implements FDSListSchemaBuilder {

	@Override
	public FDSListSchemaBuilder add(
		FDSListSchemaLabelField fdsListSchemaLabelField) {

		_fdsListSchemaLabelFieldsList.add(fdsListSchemaLabelField);

		return this;
	}

	@Override
	public FDSListSchemaBuilder add(
		String displayTypeKey, Map<String, String> displayTypeValues,
		String value) {

		FDSListSchemaLabelField fdsListSchemaLabelField =
			new FDSListSchemaLabelField();

		fdsListSchemaLabelField.setDisplayTypeKey(displayTypeKey);
		fdsListSchemaLabelField.setDisplayTypeValues(displayTypeValues);
		fdsListSchemaLabelField.setValue(value);

		_fdsListSchemaLabelFieldsList.add(fdsListSchemaLabelField);

		return this;
	}

	@Override
	public FDSListSchemaBuilder add(String displayType, String value) {
		FDSListSchemaLabelField fdsListSchemaLabelField =
			new FDSListSchemaLabelField();

		fdsListSchemaLabelField.setDisplayType(displayType);
		fdsListSchemaLabelField.setValue(value);

		_fdsListSchemaLabelFieldsList.add(fdsListSchemaLabelField);

		return this;
	}

	@Override
	public FDSListSchema build() {
		_fdsListSchema.setFDSListSchemaLabelFieldsList(
			_fdsListSchemaLabelFieldsList);

		return _fdsListSchema;
	}

	private final FDSListSchema _fdsListSchema = new FDSListSchema();
	private final List<FDSListSchemaLabelField> _fdsListSchemaLabelFieldsList =
		new ArrayList<>();

}