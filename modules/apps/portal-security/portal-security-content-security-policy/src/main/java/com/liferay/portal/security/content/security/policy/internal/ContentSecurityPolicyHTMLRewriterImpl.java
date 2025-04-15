/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.content.security.policy.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyHTMLRewriter;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProvider;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = ContentSecurityPolicyHTMLRewriter.class)
public class ContentSecurityPolicyHTMLRewriterImpl
	implements ContentSecurityPolicyHTMLRewriter {

	@Override
	public String rewriteInlineAttributes(
		String html, HttpServletRequest httpServletRequest, boolean recursive) {

		String nonce = _contentSecurityPolicyNonceProvider.getNonce(
			httpServletRequest);

		if (Validator.isBlank(nonce)) {
			return html;
		}

		StringBundler scriptSB = new StringBundler();
		StringBundler styleSB = new StringBundler();

		Document document = Jsoup.parse(html);

		Element bodyElement = document.body();

		boolean containsBody = _containsBody(html);

		if (containsBody) {
			_extractInlineHandlers(bodyElement, recursive, scriptSB);
			_extractInlineStyles(bodyElement, recursive, styleSB);
		}
		else {
			for (Element childElement : bodyElement.children()) {
				_extractInlineHandlers(childElement, recursive, scriptSB);
				_extractInlineStyles(childElement, recursive, styleSB);
			}
		}

		if ((scriptSB.length() == 0) && (styleSB.length() == 0)) {
			return html;
		}

		if (scriptSB.length() != 0) {
			Element element = new Element("script");

			element.attr("nonce", nonce);
			element.html(scriptSB.toString());

			bodyElement.appendChild(element);
		}

		if (styleSB.length() != 0) {
			Element element = new Element("style");

			element.attr("nonce", nonce);
			element.html(styleSB.toString());

			bodyElement.prependChild(element);
		}

		if (containsBody) {
			return bodyElement.outerHtml();
		}

		return bodyElement.html();
	}

	private boolean _containsBody(String html) {
		html = StringUtil.toLowerCase(html.trim());

		return html.startsWith("<body");
	}

	private void _extractInlineHandlers(
		Element element, boolean recursive, StringBundler sb) {

		String id = element.attr("id");
		List<String> keys = new ArrayList<>();

		for (Attribute attribute : element.attributes()) {
			String key = attribute.getKey();

			String lowerCaseKey = StringUtil.toLowerCase(key);

			if (!lowerCaseKey.startsWith("on")) {
				continue;
			}

			if (Objects.equals(element.nodeName(), "body")) {
				sb.append("document.body.");
			}
			else {
				if (Validator.isBlank(id)) {
					id = StringUtil.randomString(8);
				}

				sb.append("document.getElementById('");
				sb.append(id);
				sb.append("').");
			}

			sb.append(key);
			sb.append(" = function(event) {");
			sb.append(element.attr(key));
			sb.append("};");

			keys.add(key);
		}

		if (!Validator.isBlank(id)) {
			element.attr("id", id);
		}

		for (String key : keys) {
			element.removeAttr(key);
		}

		if (recursive) {
			for (Element childElement : element.children()) {
				_extractInlineHandlers(childElement, true, sb);
			}
		}
	}

	private void _extractInlineStyles(
		Element element, boolean recursive, StringBundler sb) {

		String style = element.attr("style");

		if (!Validator.isBlank(style)) {
			String id = element.attr("id");

			if (Validator.isBlank(id)) {
				id = StringUtil.randomId(8);

				element.attr("id", id);
			}

			sb.append("#");
			sb.append(id);
			sb.append("{");
			sb.append(style);
			sb.append("}");

			element.removeAttr("style");
		}

		if (recursive) {
			for (Element childElement : element.children()) {
				_extractInlineStyles(childElement, recursive, sb);
			}
		}
	}

	@Reference
	private ContentSecurityPolicyNonceProvider
		_contentSecurityPolicyNonceProvider;

}