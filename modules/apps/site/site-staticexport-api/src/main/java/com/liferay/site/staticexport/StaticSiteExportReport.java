/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport;

import java.util.Collections;
import java.util.List;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportReport {

	public StaticSiteExportReport(
		List<Failure> layoutFailures, List<Failure> resourceFailures) {

		_layoutFailures = layoutFailures;
		_resourceFailures = resourceFailures;
	}

	public List<Failure> getLayoutFailures() {
		return Collections.unmodifiableList(_layoutFailures);
	}

	public List<Failure> getResourceFailures() {
		return Collections.unmodifiableList(_resourceFailures);
	}

	public static class Failure {

		public Failure(String message, String url) {
			_message = message;
			_url = url;
		}

		public String getMessage() {
			return _message;
		}

		public String getURL() {
			return _url;
		}

		private final String _message;
		private final String _url;

	}

	private final List<Failure> _layoutFailures;
	private final List<Failure> _resourceFailures;

}