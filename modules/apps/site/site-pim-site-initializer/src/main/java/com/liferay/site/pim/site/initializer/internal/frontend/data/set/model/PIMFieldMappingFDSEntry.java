/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.model;

import com.liferay.petra.string.StringPool;

import java.util.List;

/**
 * @author Andrea Sbarra
 */
public class PIMFieldMappingFDSEntry {

	public PIMFieldMappingFDSEntry(
		String channelField, String href, boolean required,
		List<String> sourceAttributes) {

		_channelField = channelField;
		_href = href;
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

	public String getTransformationRule() {
		return StringPool.BLANK;
	}

	public boolean isMapped() {
		return !_sourceAttributes.isEmpty();
	}

	public boolean isRequired() {
		return _required;
	}

	private final String _channelField;
	private final String _href;
	private final boolean _required;
	private final List<String> _sourceAttributes;

}