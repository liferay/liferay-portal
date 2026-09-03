/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.liferay.osb.faro.engine.client.model.AssetSummaryCMPProject;

/**
 * @author Marcos Martins
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetSummaryCMPProjectDisplay {

	public AssetSummaryCMPProjectDisplay(
		AssetSummaryCMPProject assetSummaryCMPProject) {

		_id = assetSummaryCMPProject.getId();
		_name = assetSummaryCMPProject.getName();
	}

	private final String _id;
	private final String _name;

}