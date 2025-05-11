/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.view.cards;

import com.liferay.frontend.data.set.view.cards.FDSCardSchema;
import com.liferay.frontend.data.set.view.cards.FDSCardSchemaBuilder;
import com.liferay.frontend.data.set.view.cards.FDSCardSchemaLabelField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mikel Lorza
 */
public class FDSCardSchemaBuilderImpl implements FDSCardSchemaBuilder {

	@Override
	public FDSCardSchemaBuilder add(
		FDSCardSchemaLabelField fdsCardSchemaLabelField) {

		_fdsCardSchemaLabelFieldsList.add(fdsCardSchemaLabelField);

		return this;
	}

	@Override
	public FDSCardSchemaBuilder add(
		String displayTypeKey, Map<String, String> displayTypeValues,
		String value) {

		FDSCardSchemaLabelField fdsCardSchemaLabelField =
			new FDSCardSchemaLabelField();

		fdsCardSchemaLabelField.setDisplayTypeKey(displayTypeKey);
		fdsCardSchemaLabelField.setDisplayTypeValues(displayTypeValues);
		fdsCardSchemaLabelField.setValue(value);

		_fdsCardSchemaLabelFieldsList.add(fdsCardSchemaLabelField);

		return this;
	}

	@Override
	public FDSCardSchemaBuilder add(String displayType, String value) {
		FDSCardSchemaLabelField fdsCardSchemaLabelField =
			new FDSCardSchemaLabelField();

		fdsCardSchemaLabelField.setDisplayType(displayType);
		fdsCardSchemaLabelField.setValue(value);

		_fdsCardSchemaLabelFieldsList.add(fdsCardSchemaLabelField);

		return this;
	}

	@Override
	public FDSCardSchema build() {
		_fdsCardSchema.setFDSCardSchemaLabelFieldsList(
			_fdsCardSchemaLabelFieldsList);

		return _fdsCardSchema;
	}

	private final FDSCardSchema _fdsCardSchema = new FDSCardSchema();
	private final List<FDSCardSchemaLabelField> _fdsCardSchemaLabelFieldsList =
		new ArrayList<>();

}