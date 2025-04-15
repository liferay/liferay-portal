/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.content.security.policy;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Iván Zaera Avellón
 */
public interface ContentSecurityPolicyHTMLRewriter {

	/**
	 * Transform a fragment of HTML extracting all inline event handlers (e.g.:
	 * onclick, onfocus, etc.) to a &lt;script> node and inline styles (e.g.:
	 * style="color: red;...") to a &lt;style> node so that restrictive
	 * content security policies like <code>script-src-attr 'none';</code> do
	 * not prevent their execution.
	 * @param html the HTML to transform
	 * @param httpServletRequest needed to obtain the content security policy nonce to use in the &lt;script> node
	 * @param recursive whether to rewrite all nodes or only the top level ones
	 * @return the transformed HTML
	 * @review
	 */
	public String rewriteInlineAttributes(
		String html, HttpServletRequest httpServletRequest, boolean recursive);

}