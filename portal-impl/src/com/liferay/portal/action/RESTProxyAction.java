/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author David Truong
 * @author Gavin Wan
 * @author Samuel Kong
 */
public class RESTProxyAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String url = ParamUtil.getString(httpServletRequest, "url");

		if (!validate(url)) {
			return null;
		}

		Http.Options options = new Http.Options();

		int pos = url.indexOf(CharPool.QUESTION);

		if (pos != -1) {
			options.setBody(
				url.substring(pos + 1),
				ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED,
				StringPool.UTF8);
			options.setLocation(url.substring(0, pos));
		}
		else {
			options.setLocation(url);
		}

		options.setPost(true);

		String content = HttpUtil.URLtoString(options);

		ServletResponseUtil.write(httpServletResponse, content);

		return null;
	}

	protected boolean validate(String url) {
		if (Validator.isNull(url) || !HttpComponentsUtil.hasDomain(url)) {
			return false;
		}

		if (PropsValues.REST_PROXY_URL_PREFIXES_ALLOWED.length == 0) {
			return true;
		}

		for (String urlPrefix : PropsValues.REST_PROXY_URL_PREFIXES_ALLOWED) {
			if (StringUtil.startsWith(url, urlPrefix)) {
				return true;
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("URL " + url + " is not allowed");
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RESTProxyAction.class);

}