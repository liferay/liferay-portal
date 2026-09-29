/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.thread.local;

import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Stefano Motta
 */
public class DSRRoomThreadLocal {

	public static long[] getFileEntryIds() {
		return _fileEntryIds.get();
	}

	public static long getObjectEntryId() {
		return _objectEntryId.get();
	}

	public static void setFileEntryIds(long[] fileEntryIds) {
		_fileEntryIds.set(fileEntryIds);
	}

	public static void setObjectEntryId(long objectEntryId) {
		_objectEntryId.set(objectEntryId);
	}

	private static final ThreadLocal<long[]> _fileEntryIds =
		new CentralizedThreadLocal<>(
			DSRRoomThreadLocal.class + "._fileEntryIds", () -> new long[0]);
	private static final ThreadLocal<Long> _objectEntryId =
		new CentralizedThreadLocal<>(
			DSRRoomThreadLocal.class + "._objectEntryId", () -> 0L);

}