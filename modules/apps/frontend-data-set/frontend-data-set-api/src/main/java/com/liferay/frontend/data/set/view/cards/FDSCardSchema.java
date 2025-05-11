/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.view.cards;

import java.util.List;

/**
 * @author Mikel Lorza
 */
public class FDSCardSchema {

	public List<FDSCardSchemaLabelField> getFDSCardSchemaLabelFieldsList() {
		return _fdsCardSchemaLabelFieldsList;
	}

	public void setFDSCardSchemaLabelFieldsList(
		List<FDSCardSchemaLabelField> fdsCardSchemaLabelFieldsList) {

		_fdsCardSchemaLabelFieldsList = fdsCardSchemaLabelFieldsList;
	}

	private List<FDSCardSchemaLabelField> _fdsCardSchemaLabelFieldsList;

}