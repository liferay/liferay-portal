/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.view.list;

import java.util.List;

/**
 * @author Mylena Monte
 */
public class FDSListSchema {

	public List<FDSListSchemaLabelField> getFDSListSchemaLabelFieldsList() {
		return _fdsListSchemaLabelFieldsList;
	}

	public void setFDSListSchemaLabelFieldsList(
		List<FDSListSchemaLabelField> fdsListSchemaLabelFieldsList) {

		_fdsListSchemaLabelFieldsList = fdsListSchemaLabelFieldsList;
	}

	private List<FDSListSchemaLabelField> _fdsListSchemaLabelFieldsList;

}