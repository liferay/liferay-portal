/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.transformer;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.templateparser.BaseTransformerListener;
import com.liferay.portal.kernel.templateparser.TransformerListener;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.util.PropsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
	service = TransformerListener.class
)
public class RegexTransformerListener extends BaseTransformerListener {

	@Override
	public String onOutput(
		String output, String languageId, Map<String, String> tokens) {

		if (_log.isDebugEnabled()) {
			_log.debug("onOutput");
		}

		return replace(output);
	}

	@Override
	public String onScript(
		String script, Document document, String languageId,
		Map<String, String> tokens) {

		if (_log.isDebugEnabled()) {
			_log.debug("onScript");
		}

		return replace(script);
	}

	@Activate
	protected void activate() {
		_patterns = new ArrayList<>();
		_replacements = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			String regex = PropsUtil.get(
				"journal.transformer.regex.pattern." + i);
			String replacement = PropsUtil.get(
				"journal.transformer.regex.replacement." + i);

			if (Validator.isNull(regex) || Validator.isNull(replacement)) {
				break;
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Pattern ", regex, " will be replaced with ",
						replacement));
			}

			_patterns.add(Pattern.compile(regex));
			_replacements.add(replacement);
		}
	}

	protected String replace(String s) {
		if (s == null) {
			return s;
		}

		for (int i = 0; i < _patterns.size(); i++) {
			Pattern pattern = _patterns.get(i);
			String replacement = _replacements.get(i);

			Matcher matcher = pattern.matcher(s);

			s = matcher.replaceAll(replacement);
		}

		return s;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RegexTransformerListener.class);

	private List<Pattern> _patterns;
	private List<String> _replacements;

}