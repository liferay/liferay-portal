/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Petteri Karttunen
 */
public class BatchEngineImportTaskExecutorException extends PortalException {

	public BatchEngineImportTaskExecutorException(
		Object item, Throwable throwable) {

		super("Unable to convert item", throwable);

		_item = item;
	}

	public Object getItem() {
		return _item;
	}

	private final Object _item;

}