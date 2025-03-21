/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.struts;

import com.liferay.document.library.web.internal.portlet.action.helper.GetFileActionHelper;
import com.liferay.portal.kernel.struts.StrutsAction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Charles May
 * @author Bruno Farache
 */
@Component(
	property = "path=/document_library/get_file", service = StrutsAction.class
)
public class GetFileStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		_getFileActionHelper.processRequest(
			httpServletRequest, httpServletResponse);

		return null;
	}

	private final GetFileActionHelper _getFileActionHelper =
		new GetFileActionHelper();

}