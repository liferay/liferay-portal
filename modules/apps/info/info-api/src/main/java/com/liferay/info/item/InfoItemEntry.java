/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Víctor Galán
 */
public class InfoItemEntry implements Serializable {

	public InfoItemEntry(
		String externalReferenceCode, String parentExternalReferenceCode) {

		_externalReferenceCode = externalReferenceCode;
		_parentExternalReferenceCode = parentExternalReferenceCode;
	}

	public void addInfoItemEntry(
		InfoItemEntry infoItemEntry, String relationshipName) {

		List<InfoItemEntry> infoItemEntries =
			_infoItemEntriesMap.computeIfAbsent(
				relationshipName, key -> new ArrayList<>());

		infoItemEntries.add(infoItemEntry);
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public List<InfoItemEntry> getInfoItemEntries(String relationshipName) {
		return _infoItemEntriesMap.getOrDefault(
			relationshipName, Collections.emptyList());
	}

	public String getParentExternalReferenceCode() {
		return _parentExternalReferenceCode;
	}

	public Object getValue(String infoFieldUniqueId) {
		return _values.get(infoFieldUniqueId);
	}

	public void putValue(String infoFieldUniqueId, Object value) {
		_values.put(infoFieldUniqueId, value);
	}

	private final String _externalReferenceCode;
	private final Map<String, List<InfoItemEntry>> _infoItemEntriesMap =
		new LinkedHashMap<>();
	private final String _parentExternalReferenceCode;
	private final Map<String, Object> _values = new HashMap<>();

}