/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.sanitizer.internal;

import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * @author Sergio González
 */
@Component(property = "sanitizer.order:Integer=20", service = Sanitizer.class)
public class CommentSanitizerImpl implements Sanitizer {

	public CommentSanitizerImpl() {
		_commentAllowedContent = new CommentAllowedContent(
			PropsValues.DISCUSSION_COMMENTS_ALLOWED_CONTENT);
	}

	@Override
	public String sanitize(
		long companyId, long groupId, long userId, String className,
		long classPK, String contentType, String[] modes, String content,
		Map<String, Object> options) {

		if (MapUtil.isEmpty(options)) {
			return content;
		}

		boolean discussion = GetterUtil.getBoolean(options.get("discussion"));

		if (!discussion || !contentType.equals(ContentTypes.TEXT_HTML)) {
			return content;
		}

		return sanitize(content);
	}

	protected String sanitize(String html) {
		HtmlPolicyBuilder htmlPolicyBuilder = new HtmlPolicyBuilder();

		htmlPolicyBuilder.allowStandardUrlProtocols();

		Map<String, String[]> attributeNames =
			_commentAllowedContent.getAttributeNames();

		for (Map.Entry<String, String[]> entry : attributeNames.entrySet()) {
			String elementName = entry.getKey();
			String[] attributesNames = entry.getValue();

			if (attributesNames != null) {
				HtmlPolicyBuilder.AttributeBuilder attributeBuilder =
					htmlPolicyBuilder.allowAttributes(attributesNames);

				attributeBuilder.onElements(elementName);
			}

			htmlPolicyBuilder.allowElements(elementName);
		}

		PolicyFactory policyFactory = htmlPolicyBuilder.toFactory();

		return policyFactory.sanitize(html);
	}

	private final CommentAllowedContent _commentAllowedContent;

}