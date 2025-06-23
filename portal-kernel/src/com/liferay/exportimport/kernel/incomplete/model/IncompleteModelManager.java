/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.incomplete.model;

import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;

import java.util.function.BiFunction;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Carlos Correa
 */
@ProviderType
public interface IncompleteModelManager {

	public <T, E extends Exception> T getOrAddIncompleteModel(
			Class<T> clazz, long companyId, String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			UnsafeSupplier<T, E> incompleteModelUnsafeSupplier)
		throws E;

	public <T, E extends Exception> T getOrAddIncompleteModel(
			Class<T> clazz, String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			long groupId, UnsafeSupplier<T, E> incompleteModelUnsafeSupplier)
		throws E;

	public boolean isIncompleteModel();

}