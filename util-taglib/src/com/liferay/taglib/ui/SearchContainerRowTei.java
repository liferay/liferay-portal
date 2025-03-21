/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

/**
 * @author Raymond Augé
 */
public class SearchContainerRowTei extends TagExtraInfo {

	@Override
	public VariableInfo[] getVariableInfo(TagData tagData) {
		String className = tagData.getAttributeString("className");

		String indexVar = tagData.getAttributeString("indexVar");

		if (Validator.isNull(indexVar)) {
			indexVar = SearchContainerRowTag.DEFAULT_INDEX_VAR;
		}

		String modelVar = tagData.getAttributeString("modelVar");

		if (Validator.isNull(modelVar)) {
			modelVar = SearchContainerRowTag.DEFAULT_MODEL_VAR;
		}

		String rowVar = tagData.getAttributeString("rowVar");

		if (Validator.isNull(rowVar)) {
			rowVar = SearchContainerRowTag.DEFAULT_ROW_VAR;
		}

		return new VariableInfo[] {
			new VariableInfo(
				indexVar, Integer.class.getName(), true, VariableInfo.NESTED),
			new VariableInfo(modelVar, className, true, VariableInfo.NESTED),
			new VariableInfo(
				rowVar, ResultRow.class.getName(), true, VariableInfo.NESTED)
		};
	}

}