/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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

		if (!_map.containsKey(bucket)) {
			List<PatcherFix> patcherFixes = new ArrayList<PatcherFix>();

			patcherFixes.add(patcherFix);

			_map.put(bucket, patcherFixes);
		}
		else {
			List<PatcherFix> patcherFixes = _map.get(bucket);

			patcherFixes.add(patcherFix);
		}
	}

	public PatcherFix getPatcherFix() {
		while (_currentBucket > 0) {
			if (_map.containsKey(_currentBucket)) {
				List<PatcherFix> patcherFixes = _map.get(_currentBucket);

				if (patcherFixes.size() > 0) {
					return patcherFixes.remove(0);
				}
			}

			_currentBucket--;
		}

		return null;
	}

	private int _currentBucket;
	private Map<Integer, List<PatcherFix>> _map =
		new HashMap<Integer, List<PatcherFix>>();

}