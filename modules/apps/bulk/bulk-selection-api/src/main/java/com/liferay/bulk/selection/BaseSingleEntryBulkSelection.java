/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.selection;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseSingleEntryBulkSelection<T>
	implements BulkSelection<T> {

	public BaseSingleEntryBulkSelection(
		long entryId, Map<String, String[]> parameterMap) {

		_entryId = entryId;
		_parameterMap = parameterMap;
	}

	@Override
	public <E extends PortalException> void forEach(
			UnsafeConsumer<T, E> unsafeConsumer)
		throws PortalException {

		unsafeConsumer.accept(getEntry());
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _parameterMap;
	}

	@Override
	public long getSize() {
		return 1;
	}

	@Override
	public Serializable serialize() {
		return String.valueOf(_entryId);
	}

	protected abstract T getEntry() throws PortalException;

	protected abstract String getEntryName() throws PortalException;

	private final long _entryId;
	private final Map<String, String[]> _parameterMap;

}