/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.util;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Matija Petanjek
 */
public class ItemIndexThreadLocal {

	public static void add(int itemIndex) {
		Queue<Integer> indexQueue = _indexQueue.get();

		indexQueue.add(itemIndex);
	}

	public static void clear() {
		Queue<Integer> indexQueue = _indexQueue.get();

		indexQueue.clear();
	}

	public static int get() {
		Queue<Integer> indexQueue = _indexQueue.get();

		return indexQueue.peek();
	}

	public static SafeCloseable pushIndexQueueWithSafeCloseable() {
		return _indexQueue.setWithSafeCloseable(new LinkedList<>());
	}

	public static int remove() {
		Queue<Integer> indexQueue = _indexQueue.get();

		return indexQueue.remove();
	}

	private static final CentralizedThreadLocal<Queue<Integer>> _indexQueue =
		new CentralizedThreadLocal<>(
			ItemIndexThreadLocal.class + "._indexQueue");

}