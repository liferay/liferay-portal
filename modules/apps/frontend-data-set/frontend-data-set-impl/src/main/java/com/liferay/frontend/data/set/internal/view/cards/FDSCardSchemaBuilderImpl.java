/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.view.cards;

import com.liferay.frontend.data.set.view.FDSSchemaLabelField;
import com.liferay.frontend.data.set.view.cards.FDSCardSchema;
import com.liferay.frontend.data.set.view.cards.FDSCardSchemaBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mikel Lorza
 */
public class FDSCardSchemaBuilderImpl implements FDSCardSchemaBuilder {

	@Override
	public FDSCardSchemaBuilder add(FDSSchemaLabelField fdsSchemaLabelField) {
		_fdsSchemaLabelFieldsList.add(fdsSchemaLabelField);

		return this;
	}

	@Override
	public FDSCardSchemaBuilder add(
		String displayTypeKey, Map<String, String> displayTypeValues,
		String value) {

		FDSSchemaLabelField fdsSchemaLabelField = new FDSSchemaLabelField();

		fdsSchemaLabelField.setDisplayTypeKey(displayTypeKey);
		fdsSchemaLabelField.setDisplayTypeValues(displayTypeValues);
		fdsSchemaLabelField.setValue(value);

		_fdsSchemaLabelFieldsList.add(fdsSchemaLabelField);

		return this;
	}

	@Override
	public FDSCardSchemaBuilder add(String displayType, String value) {
		FDSSchemaLabelField fdsSchemaLabelField = new FDSSchemaLabelField();

		fdsSchemaLabelField.setDisplayType(displayType);
		fdsSchemaLabelField.setValue(value);

		_fdsSchemaLabelFieldsList.add(fdsSchemaLabelField);

		return this;
	}

	@Override
	public FDSCardSchema build() {
		_fdsCardSchema.setFDSSchemaLabelFieldsList(_fdsSchemaLabelFieldsList);

		return _fdsCardSchema;
	}

	private final FDSCardSchema _fdsCardSchema = new FDSCardSchema();
	private final List<FDSSchemaLabelField> _fdsSchemaLabelFieldsList =
		new ArrayList<>();

}