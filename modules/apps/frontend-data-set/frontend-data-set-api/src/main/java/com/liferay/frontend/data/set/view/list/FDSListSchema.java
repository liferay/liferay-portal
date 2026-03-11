/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.view.list;

import com.liferay.frontend.data.set.view.FDSSchemaLabelField;

import java.util.List;

/**
 * @author Mylena Monte
 */
public class FDSListSchema {

	public List<FDSSchemaLabelField> getFDSSchemaLabelFieldsList() {
		return _fdsSchemaLabelFieldsList;
	}

	public void setFDSSchemaLabelFieldsList(
		List<FDSSchemaLabelField> fdsSchemaLabelFieldsList) {

		_fdsSchemaLabelFieldsList = fdsSchemaLabelFieldsList;
	}

	private List<FDSSchemaLabelField> _fdsSchemaLabelFieldsList;

}