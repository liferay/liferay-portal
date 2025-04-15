/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn.taglib.servlet.taglib;

import com.liferay.learn.LearnMessageUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.jsp.JspWriter;

/**
 * @author Brian Wing Shun Chan
 */
public class JSONTag extends IncludeTag {

	public String getResource() {
		return _resource;
	}

	public String getVar() {
		return _var;
	}

	@Override
	public int processEndTag() throws Exception {
		JSONObject jsonObject = LearnMessageUtil.getJSONObject(_resource);

		if (Validator.isNotNull(_var)) {
			pageContext.setAttribute(_var, jsonObject.toString());
		}
		else {
			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write(jsonObject.toString());
		}

		return EVAL_PAGE;
	}

	public void setResource(String resource) {
		_resource = resource;
	}

	public void setVar(String var) {
		_var = var;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_resource = null;
		_var = null;
	}

	private String _resource;
	private String _var;

}