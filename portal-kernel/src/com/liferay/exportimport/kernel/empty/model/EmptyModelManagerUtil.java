/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.empty.model;

import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.function.BiFunction;

/**
 * @author Carlos Correa
 */
public class EmptyModelManagerUtil {

	public static <T, E extends PortalException> T getOrAddEmptyModel(
			Class<T> clazz, long companyId, String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			UnsafeSupplier<T, E> emptyModelUnsafeSupplier)
		throws E {

		EmptyModelManager emptyModelManager = _emptyModelManagerSnapshot.get();

		return emptyModelManager.getOrAddEmptyModel(
			clazz, companyId, emptyModelUnsafeSupplier, externalReferenceCode,
			fetchByExternalReferenceCodeBiFunction,
			getByExternalReferenceCodeUnsafeBiFunction);
	}

	public static <T, E extends PortalException> T getOrAddEmptyModel(
			Class<T> clazz, UnsafeSupplier<T, E> emptyModelUnsafeSupplier,
			String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			long groupId)
		throws E {

		EmptyModelManager emptyModelManager = _emptyModelManagerSnapshot.get();

		return emptyModelManager.getOrAddEmptyModel(
			clazz, emptyModelUnsafeSupplier, externalReferenceCode,
			fetchByExternalReferenceCodeBiFunction,
			getByExternalReferenceCodeUnsafeBiFunction, groupId);
	}

	public static boolean isEmptyModel() {
		EmptyModelManager emptyModelManager = _emptyModelManagerSnapshot.get();

		if (emptyModelManager == null) {
			return false;
		}

		return emptyModelManager.isEmptyModel();
	}

	private static final Snapshot<EmptyModelManager>
		_emptyModelManagerSnapshot = new Snapshot<>(
			EmptyModelManagerUtil.class, EmptyModelManager.class);

}