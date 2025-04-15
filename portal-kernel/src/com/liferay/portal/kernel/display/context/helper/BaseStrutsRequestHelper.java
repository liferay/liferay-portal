/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.display.context.helper;

import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Iván Zaera
 */
public abstract class BaseStrutsRequestHelper extends BaseRequestHelper {

	public BaseStrutsRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	public String getMVCRenderCommandName() {
		if (_mvcRenderCommandName == null) {
			_mvcRenderCommandName = ParamUtil.getString(
				getRequest(), "mvcRenderCommandName");
		}

		return _mvcRenderCommandName;
	}

	private String _mvcRenderCommandName;

}