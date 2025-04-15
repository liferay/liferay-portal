/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

import java.util.List;

/**
 * @author Raymond Augé
 */
public class SearchContainerResultsTei extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData tagData) {
		String resultsVar = tagData.getAttributeString("resultsVar");

		if (Validator.isNull(resultsVar)) {
			resultsVar = SearchContainer.DEFAULT_RESULTS_VAR;
		}

		String totalVar = tagData.getAttributeString("totalVar");

		if (Validator.isNull(totalVar)) {
			totalVar = SearchContainer.DEFAULT_DEPRECATED_TOTAL_VAR;
		}

		return new VariableInfo[] {
			new VariableInfo(
				resultsVar, List.class.getName(), true, VariableInfo.AT_BEGIN),
			new VariableInfo(
				totalVar, Integer.class.getName(), true, VariableInfo.AT_BEGIN)
		};
	}

}