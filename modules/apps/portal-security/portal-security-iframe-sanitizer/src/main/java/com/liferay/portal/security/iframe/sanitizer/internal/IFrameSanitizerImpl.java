/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.iframe.sanitizer.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.iframe.sanitizer.configuration.IFrameConfiguration;
import com.liferay.portal.security.iframe.sanitizer.internal.configuration.helper.IFrameConfigurationHelper;

import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.portal.security.iframe.sanitizer.configuration.IFrameConfiguration",
	property = "sanitizer.order:Integer=10", service = Sanitizer.class
)
public class IFrameSanitizerImpl implements Sanitizer {

	@Override
	public String sanitize(
		long companyId, long groupId, long userId, String className,
		long classPK, String contentType, String[] modes, String content,
		Map<String, Object> options) {

		IFrameConfiguration companyIFrameConfiguration =
			_iFrameConfigurationHelper.getCompanyIFrameConfiguration(companyId);

		if (!companyIFrameConfiguration.enabled() ||
			Validator.isNull(content) || Validator.isNull(contentType) ||
			!contentType.equals(ContentTypes.TEXT_HTML) ||
			_isWhitelisted(
				_iFrameConfigurationHelper.getCompanyBlacklist(companyId),
				className, classPK,
				_iFrameConfigurationHelper.getCompanyWhitelist(companyId))) {

			return content;
		}

		Document document = _getDocument(content);

		for (Element iFrameElement : document.getElementsByTag("iframe")) {
			if (companyIFrameConfiguration.removeIFrameTags()) {
				iFrameElement.remove();
			}
			else {
				iFrameElement.attr(
					"sandbox",
					StringUtil.merge(
						companyIFrameConfiguration.sandboxAttributeValues(),
						StringPool.SPACE));
			}
		}

		Element bodyElement = document.body();

		StringBundler sb = new StringBundler(bodyElement.childNodeSize());

		for (Node childNode : bodyElement.childNodes()) {
			sb.append(childNode.toString());
		}

		return sb.toString();
	}

	private Document _getDocument(String content) {
		Document document = Jsoup.parseBodyFragment(content);

		document.outputSettings(
			new Document.OutputSettings() {
				{
					prettyPrint(false);
				}
			});

		return document;
	}

	private boolean _isWhitelisted(
		Set<String> blacklist, String className, long classPK,
		Set<String> whitelist) {

		String classNameAndClassPK = className + StringPool.POUND + classPK;

		for (String blacklistItem : blacklist) {
			if (blacklistItem.equals(StringPool.STAR) ||
				classNameAndClassPK.startsWith(blacklistItem)) {

				return false;
			}
		}

		for (String whitelistItem : whitelist) {
			if (whitelistItem.equals(StringPool.STAR) ||
				classNameAndClassPK.startsWith(whitelistItem)) {

				return true;
			}
		}

		return false;
	}

	@Reference
	private IFrameConfigurationHelper _iFrameConfigurationHelper;

}