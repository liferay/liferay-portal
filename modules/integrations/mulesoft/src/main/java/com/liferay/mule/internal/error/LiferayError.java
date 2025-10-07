/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.error;

import java.util.Optional;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.error.MuleErrors;

/**
 * @author Matija Petanjek
 */
public enum LiferayError implements ErrorTypeDefinition<LiferayError> {

	BAD_REQUEST(MuleErrors.CONNECTIVITY, 400), BATCH_EXPORT_FAILED,
	BATCH_IMPORT_FAILED, CONNECTION_TIMEOUT(MuleErrors.CONNECTIVITY), EXECUTION,
	INVALID_OAS_DOCUMENT(EXECUTION),
	NOT_ACCEPTABLE(MuleErrors.CONNECTIVITY, 406),
	NOT_ALLOWED(MuleErrors.CONNECTIVITY, 405),
	NOT_FOUND(MuleErrors.CONNECTIVITY, 404),
	NOT_IMPLEMENTED(MuleErrors.CONNECTIVITY, 501),
	OAUTH2_ERROR(MuleErrors.CONNECTIVITY),
	SERVER_ERROR(MuleErrors.CONNECTIVITY, 500),
	UNAUTHORIZED(MuleErrors.CONNECTIVITY, 401),
	UNSUPPORTED_MEDIA_TYPE(MuleErrors.CONNECTIVITY, 415);

	public static LiferayError fromStatus(int status) {
		for (LiferayError liferayError : values()) {
			if (liferayError.status == status) {
				return liferayError;
			}
		}

		throw new IllegalArgumentException(
			"No error defined for status: " + status);
	}

	@Override
	public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
		return Optional.ofNullable(parent);
	}

	private LiferayError() {
	}

	private LiferayError(ErrorTypeDefinition<?> parent) {
		this.parent = parent;
	}

	private LiferayError(ErrorTypeDefinition<?> parent, int status) {
		this.parent = parent;
		this.status = status;
	}

	private ErrorTypeDefinition<?> parent;
	private int status;

}