/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.model.PatcherFix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zsolt Balogh
 */
public class PatcherFixRadix {

	public void addPatcherFix(int bucket, PatcherFix patcherFix) {
		if (_currentBucket < bucket) {
			_currentBucket = bucket;
		}

		List<PatcherFix> patcherFixes = _map.computeIfAbsent(
			bucket, key -> new ArrayList<>());

		patcherFixes.add(patcherFix);
	}

	public PatcherFix getPatcherFix() {
		while (_currentBucket > 0) {
			List<PatcherFix> patcherFixes = _map.get(_currentBucket);

			if ((patcherFixes != null) && !patcherFixes.isEmpty()) {
				return patcherFixes.remove(0);
			}

			_currentBucket--;
		}

		return null;
	}

	private int _currentBucket;
	private final Map<Integer, List<PatcherFix>> _map = new HashMap<>();

}