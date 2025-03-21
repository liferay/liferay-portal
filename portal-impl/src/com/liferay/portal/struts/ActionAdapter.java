/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.struts;

import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Mika Koivisto
 */
public class ActionAdapter implements Action {

	public ActionAdapter(StrutsAction strutsAction) {
		_strutsAction = strutsAction;
	}

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String forward = _strutsAction.execute(
			httpServletRequest, httpServletResponse);

		if (Validator.isNull(forward)) {
			return null;
		}

		ActionForward actionForward = actionMapping.getActionForward(forward);

		if (actionForward == null) {
			actionForward = new ActionForward(null, forward);
		}

		return actionForward;
	}

	private final StrutsAction _strutsAction;

}