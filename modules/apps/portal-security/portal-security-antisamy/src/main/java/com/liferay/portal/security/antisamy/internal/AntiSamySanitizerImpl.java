/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.antisamy.internal;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Validator;

import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;

/**
 * @author Zsolt Balogh
 * @author Brian Wing Shun Chan
 */
public class AntiSamySanitizerImpl implements Sanitizer {

	public AntiSamySanitizerImpl(
		String[] blacklist, URL url, String[] whitelist) {

		_url = url;

		if (blacklist != null) {
			for (String blacklistItem : blacklist) {
				blacklistItem = blacklistItem.trim();

				if (!blacklistItem.isEmpty()) {
					blacklistItem = _stripTrailingStar(blacklistItem);

					_blacklist.add(blacklistItem);
				}
			}
		}

		if (whitelist != null) {
			for (String whitelistItem : whitelist) {
				whitelistItem = whitelistItem.trim();

				if (!whitelistItem.isEmpty()) {
					whitelistItem = _stripTrailingStar(whitelistItem);

					_whitelist.add(whitelistItem);
				}
			}
		}
	}

	public void addPolicy(String className, URL url) {
		try (InputStream inputStream = url.openStream()) {
			Policy policy = Policy.getInstance(inputStream);

			_policies.put(className, policy);
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				"Unable to initialize policy", exception);
		}
	}

	public void removePolicy(String className) {
		_policies.remove(className);
	}

	@Override
	public String sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String[] modes, String content,
			Map<String, Object> options)
		throws SanitizerException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat("Sanitizing ", className, "#", classPK));
		}

		if (Validator.isNull(content) || Validator.isNull(contentType) ||
			!contentType.equals(ContentTypes.TEXT_HTML) ||
			_isWhitelisted(className, classPK)) {

			return content;
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				AntiSamySanitizerImpl.class.getClassLoader())) {

			CleanResults cleanResults = null;

			AntiSamy antiSamy = new AntiSamy();

			if (_isConfigured(className, classPK)) {
				Policy policy = _policies.get(className);

				cleanResults = antiSamy.scan(content, policy, AntiSamy.SAX);
			}
			else {
				cleanResults = antiSamy.scan(
					content,
					_policyDCLSingleton.getSingleton(this::_getPolicy));
			}

			if (_log.isWarnEnabled()) {
				for (String errorMessage : cleanResults.getErrorMessages()) {
					_log.warn(errorMessage);
				}
			}

			return cleanResults.getCleanHTML();
		}
		catch (Exception exception) {
			_log.error("Unable to sanitize input", exception);

			throw new SanitizerException(exception);
		}
	}

	private Policy _getPolicy() {
		try (InputStream inputStream = _url.openStream()) {
			return Policy.getInstance(inputStream);
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				"Unable to initialize policy", exception);
		}
	}

	private boolean _isConfigured(String className, long classPK) {
		String classNameAndClassPK = className + StringPool.POUND + classPK;

		for (String policyClassName : _policies.keySet()) {
			if (classNameAndClassPK.startsWith(policyClassName)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isWhitelisted(String className, long classPK) {
		String classNameAndClassPK = className + StringPool.POUND + classPK;

		for (String blacklistItem : _blacklist) {
			if (blacklistItem.equals(StringPool.STAR) ||
				classNameAndClassPK.startsWith(blacklistItem)) {

				return false;
			}
		}

		for (String whitelistItem : _whitelist) {
			if (whitelistItem.equals(StringPool.STAR) ||
				classNameAndClassPK.startsWith(whitelistItem)) {

				return true;
			}
		}

		return false;
	}

	private String _stripTrailingStar(String item) {
		if (item.equals(StringPool.STAR)) {
			return item;
		}

		char c = item.charAt(item.length() - 1);

		if (c == CharPool.STAR) {
			return item.substring(0, item.length() - 1);
		}

		return item;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AntiSamySanitizerImpl.class);

	private final List<String> _blacklist = new ArrayList<>();
	private final Map<String, Policy> _policies = new HashMap<>();
	private final DCLSingleton<Policy> _policyDCLSingleton =
		new DCLSingleton<>();
	private final URL _url;
	private final List<String> _whitelist = new ArrayList<>();

}