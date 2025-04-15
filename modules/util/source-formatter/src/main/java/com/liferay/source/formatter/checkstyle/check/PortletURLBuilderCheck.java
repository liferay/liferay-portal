/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public class PortletURLBuilderCheck extends BaseBuilderCheck {

	@Override
	protected boolean allowNullValues() {
		return true;
	}

	@Override
	protected List<BaseBuilderCheck.BuilderInformation>
		doGetBuilderInformationList() {

		return ListUtil.fromArray(
			new BaseBuilderCheck.BuilderInformation(
				"PortletURL", "PortletURLBuilder", "setActionName",
				"setBackURL", "setCMD", "setGlobalParameter", "setKeywords",
				"setMVCPath", "setMVCRenderCommandName", "setNavigation",
				"setParameter", "setParameters", "setPortletMode",
				"setPortletResource", "setProperty", "setRedirect", "setSecure",
				"setTabs1", "setTabs2", "setWindowState"));
	}

	@Override
	protected String getAssignClassName(DetailAST assignDetailAST) {
		for (BaseBuilderCheck.BuilderInformation builderInformation :
				getBuilderInformationList()) {

			for (String className : getNames(assignDetailAST, true)) {
				if (Objects.equals(
						builderInformation.getBuilderClassName(), className)) {

					return null;
				}
			}
		}

		DetailAST parentDetailAST = assignDetailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.VARIABLE_DEF) {
			return getTypeName(parentDetailAST, false);
		}

		String variableName = getName(assignDetailAST);

		if (variableName != null) {
			return getVariableTypeName(assignDetailAST, variableName, false);
		}

		return null;
	}

	@Override
	protected List<String> getAvoidCastStringMethodNames() {
		return ListUtil.fromArray(
			"setGlobalParameter", "setParameter", "setRedirect");
	}

	@Override
	protected Map<String, String[][]> getReservedKeywordsMap() {
		return HashMapBuilder.put(
			"setParameter", _RESERVED_KEYWORDS
		).build();
	}

	@Override
	protected List<String> getSupportsFunctionMethodNames() {
		return ListUtil.fromArray(
			"setActionName", "setCMD", "setGlobalParameter", "setKeywords",
			"setMVCPath", "setMVCRenderCommandName", "setNavigation",
			"setParameter", "setPortletResource", "setRedirect", "setTabs1",
			"setTabs2");
	}

	@Override
	protected boolean isSupportsNestedMethodCalls() {
		return true;
	}

	private static final String[][] _RESERVED_KEYWORDS = {
		{"ActionRequest.ACTION_NAME", "setActionName"},
		{"Constants.CMD", "setCMD"}, {"backURL", "setBackURL"},
		{"cmd", "setCMD"}, {"jakarta.portlet.action", "setActionName"},
		{"keywords", "setKeywords"}, {"mvcPath", "setMVCPath"},
		{"mvcRenderCommandName", "setMVCRenderCommandName"},
		{"navigation", "setNavigation"}, {"p_p_mode", "setPortletMode"},
		{"p_p_state", "setWindowState"},
		{"portletResource", "setPortletResource"}, {"redirect", "setRedirect"},
		{"tabs1", "setTabs1"}, {"tabs2", "setTabs2"}
	};

}