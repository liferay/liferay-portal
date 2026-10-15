/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.link;

import com.liferay.object.model.ObjectEntry;

/**
 * @author Stefano Motta
 */
public class PIMLinkRelatedEntry {

	public PIMLinkRelatedEntry(
		ObjectEntry objectEntry, boolean primary, String type) {

		_objectEntry = objectEntry;
		_primary = primary;
		_type = type;
	}

	public ObjectEntry getObjectEntry() {
		return _objectEntry;
	}

	public String getType() {
		return _type;
	}

	public boolean isPrimary() {
		return _primary;
	}

	private final ObjectEntry _objectEntry;
	private final boolean _primary;
	private final String _type;

}