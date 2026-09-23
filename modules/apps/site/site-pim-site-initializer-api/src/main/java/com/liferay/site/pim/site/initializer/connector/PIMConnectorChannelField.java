/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.connector;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class PIMConnectorChannelField {

	public PIMConnectorChannelField(
		String label, boolean multiple, String name, boolean required) {

		_label = label;
		_multiple = multiple;
		_name = name;
		_required = required;
	}

	public String getLabel() {
		return _label;
	}

	public String getName() {
		return _name;
	}

	public boolean isMultiple() {
		return _multiple;
	}

	public boolean isRequired() {
		return _required;
	}

	private final String _label;
	private final boolean _multiple;
	private final String _name;
	private final boolean _required;

}