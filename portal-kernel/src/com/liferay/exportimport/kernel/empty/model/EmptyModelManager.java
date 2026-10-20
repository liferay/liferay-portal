/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.empty.model;

import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Carlos Correa
 */
@ProviderType
public interface EmptyModelManager {

	public <T, E extends PortalException> T getOrAddEmptyModel(
			Class<T> clazz, long companyId,
			UnsafeSupplier<T, E> emptyModelUnsafeSupplier,
			String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			String modelNameLanguageKey)
		throws E;

	public <T, E extends Exception> T getOrAddEmptyModel(
			String className, Long companyId,
			UnsafeSupplier<T, E> emptyModelUnsafeSupplier,
			String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			long groupId, String modelNameLanguageKey)
		throws E;

	public boolean isEmptyModel();

	public void reportMissingReference(
		String className, String externalReferenceCode, long groupId);

	public void resolveMissingReference(
		String className, long companyId, String externalReferenceCode,
		long groupId);

	public int solveEmptyModel(
		String classExternalReferenceCode, String className, long companyId,
		long groupId, int status, Supplier<Integer> updatedModelStatusSupplier);

}