/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.liferay.osb.faro.engine.client.model.AcquisitionParameter;

/**
 * @author Caio Pinheiro
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class AcquisitionParameterDisplay {

	public AcquisitionParameterDisplay() {
	}

	public AcquisitionParameterDisplay(
		AcquisitionParameter acquisitionParameter) {

		_fieldName = acquisitionParameter.getFieldName();
		_name = acquisitionParameter.getName();
		_standard = acquisitionParameter.getStandard();
		_type = acquisitionParameter.getType();
	}

	private String _fieldName;
	private String _name;
	private Boolean _standard;
	private String _type;

}