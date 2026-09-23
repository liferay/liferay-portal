/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.model;

import com.liferay.portal.kernel.language.LanguageUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author Stefano Motta
 */
public class PIMConnectorChannelFieldDisplay {

	public PIMConnectorChannelFieldDisplay(
		String channelField, String href, Locale locale, boolean required,
		List<String> sourceAttributes) {

		_channelField = channelField;
		_href = href;
		_locale = locale;
		_required = required;
		_sourceAttributes = sourceAttributes;
	}

	public String getChannelField() {
		return _channelField;
	}

	public String getHref() {
		return _href;
	}

	public List<String> getSourceAttributes() {
		return _sourceAttributes;
	}

	public Status getStatus() {
		if (isMapped()) {
			return new Status("success", LanguageUtil.get(_locale, "mapped"));
		}

		if (_required) {
			return new Status(
				"danger", LanguageUtil.get(_locale, "required-not-mapped"));
		}

		return new Status("secondary", LanguageUtil.get(_locale, "not-mapped"));
	}

	public boolean isMapped() {
		return !_sourceAttributes.isEmpty();
	}

	public boolean isRequired() {
		return _required;
	}

	public static class Status {

		public Status(String displayStyle, String label) {
			_displayStyle = displayStyle;
			_label = label;
		}

		public String getDisplayStyle() {
			return _displayStyle;
		}

		public String getLabel() {
			return _label;
		}

		private final String _displayStyle;
		private final String _label;

	}

	private final String _channelField;
	private final String _href;
	private final Locale _locale;
	private final boolean _required;
	private final List<String> _sourceAttributes;

}