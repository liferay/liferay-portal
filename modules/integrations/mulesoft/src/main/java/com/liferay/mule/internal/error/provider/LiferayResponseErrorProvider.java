/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.error.provider;

import com.liferay.mule.internal.error.LiferayError;

import java.util.HashSet;
import java.util.Set;

import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

/**
 * @author Matija Petanjek
 */
public class LiferayResponseErrorProvider implements ErrorTypeProvider {

	@Override
	public Set<ErrorTypeDefinition> getErrorTypes() {
		Set<ErrorTypeDefinition> errors = new HashSet<>();

		errors.add(LiferayError.BAD_REQUEST);
		errors.add(LiferayError.BATCH_EXPORT_FAILED);
		errors.add(LiferayError.BATCH_IMPORT_FAILED);
		errors.add(LiferayError.CONNECTION_TIMEOUT);
		errors.add(LiferayError.EXECUTION);
		errors.add(LiferayError.INVALID_OAS_DOCUMENT);
		errors.add(LiferayError.NOT_ACCEPTABLE);
		errors.add(LiferayError.NOT_ALLOWED);
		errors.add(LiferayError.NOT_FOUND);
		errors.add(LiferayError.NOT_IMPLEMENTED);
		errors.add(LiferayError.OAUTH2_ERROR);
		errors.add(LiferayError.SERVER_ERROR);
		errors.add(LiferayError.UNAUTHORIZED);
		errors.add(LiferayError.UNSUPPORTED_MEDIA_TYPE);

		return errors;
	}

}