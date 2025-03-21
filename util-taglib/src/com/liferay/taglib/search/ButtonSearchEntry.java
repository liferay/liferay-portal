/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyHTMLRewriterUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

/**
 * @author Brian Wing Shun Chan
 */
public class ButtonSearchEntry extends TextSearchEntry {

	@Override
	public Object clone() {
		ButtonSearchEntry buttonSearchEntry = new ButtonSearchEntry();

		BeanPropertiesUtil.copyProperties(this, buttonSearchEntry);

		return buttonSearchEntry;
	}

	@Override
	public void print(
			Writer writer, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		writer.write(
			ContentSecurityPolicyHTMLRewriterUtil.rewriteInlineAttributes(
				StringBundler.concat(
					"<input type=\"button\" value=\"", getName(),
					"\" onClick=\"", getHref(), "\">"),
				httpServletRequest, false));
	}

}