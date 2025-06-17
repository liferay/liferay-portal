/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;

import java.util.regex.Pattern;

/**
 * @author Peter Shin
 */
public class ExcludeSyntaxPattern {

	public ExcludeSyntaxPattern(
		ExcludeSyntax excludeSyntax, String excludePattern) {

		_excludeSyntax = excludeSyntax;

		if (excludeSyntax == ExcludeSyntax.REGEX) {
			_excludePattern = StringUtil.replace(
				excludePattern, CharPool.SLASH, Pattern.quote(File.separator));
		}
		else {
			_excludePattern = excludePattern;
		}
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ExcludeSyntaxPattern)) {
			return false;
		}

		ExcludeSyntaxPattern excludeSyntaxPattern =
			(ExcludeSyntaxPattern)object;

		ExcludeSyntax excludeSyntax = excludeSyntaxPattern.getExcludeSyntax();

		if (!excludeSyntax.equals(_excludeSyntax)) {
			return false;
		}

		String excludePattern = excludeSyntaxPattern.getExcludePattern();

		return excludePattern.equals(_excludePattern);
	}

	public String getExcludePattern() {
		return _excludePattern;
	}

	public ExcludeSyntax getExcludeSyntax() {
		return _excludeSyntax;
	}

	@Override
	public int hashCode() {
		String s = _excludeSyntax.getValue();

		s = s.concat(_excludePattern);

		return s.hashCode();
	}

	private final String _excludePattern;
	private final ExcludeSyntax _excludeSyntax;

}