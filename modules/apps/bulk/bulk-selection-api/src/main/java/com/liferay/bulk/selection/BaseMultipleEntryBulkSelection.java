/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.selection;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseMultipleEntryBulkSelection<T>
	implements BulkSelection<T> {

	public BaseMultipleEntryBulkSelection(
		long[] entryIds, Map<String, String[]> parameterMap) {

		_entryIds = entryIds;
		_parameterMap = parameterMap;
	}

	@Override
	public <E extends PortalException> void forEach(
			UnsafeConsumer<T, E> unsafeConsumer)
		throws PortalException {

		for (long entryId : _entryIds) {
			unsafeConsumer.accept(fetchEntry(entryId));
		}
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _parameterMap;
	}

	@Override
	public long getSize() {
		return _entryIds.length;
	}

	@Override
	public Serializable serialize() {
		return StringUtil.merge(_entryIds, StringPool.COMMA);
	}

	protected abstract T fetchEntry(long entryId);

	private final long[] _entryIds;
	private final Map<String, String[]> _parameterMap;

}