/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.site.staticexport.StaticSiteExportReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportReportImpl implements StaticSiteExportReport {

	public void addLayoutFailure(String message, String url) {
		_layoutFailures.add(new FailureImpl(message, url));
	}

	public void addResourceFailure(String message, String url) {
		_resourceFailures.add(new FailureImpl(message, url));
	}

	@Override
	public List<Failure> getLayoutFailures() {
		return Collections.unmodifiableList(_layoutFailures);
	}

	@Override
	public List<Failure> getResourceFailures() {
		return Collections.unmodifiableList(_resourceFailures);
	}

	private final List<Failure> _layoutFailures = new ArrayList<>();
	private final List<Failure> _resourceFailures = new ArrayList<>();

	private static class FailureImpl implements Failure {

		public FailureImpl(String message, String url) {
			_message = message;
			_url = url;
		}

		@Override
		public String getMessage() {
			return _message;
		}

		@Override
		public String getURL() {
			return _url;
		}

		private final String _message;
		private final String _url;

	}

}