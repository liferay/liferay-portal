/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.incomplete.model;

import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.function.BiFunction;

/**
 * @author Carlos Correa
 */
public class IncompleteModelManagerUtil {

	public static <T, E extends Exception> T getOrAddIncompleteModel(
			Class<T> clazz, long companyId, String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			UnsafeSupplier<T, E> incompleteModelUnsafeSupplier)
		throws E {

		IncompleteModelManager incompleteModelManager =
			_incompleteModelManagerSnapshot.get();

		return incompleteModelManager.getOrAddIncompleteModel(
			clazz, companyId, externalReferenceCode,
			fetchByExternalReferenceCodeBiFunction,
			getByExternalReferenceCodeUnsafeBiFunction,
			incompleteModelUnsafeSupplier);
	}

	public static <T, E extends Exception> T getOrAddIncompleteModel(
			Class<T> clazz, String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			long groupId, UnsafeSupplier<T, E> incompleteModelUnsafeSupplier)
		throws E {

		IncompleteModelManager incompleteModelManager =
			_incompleteModelManagerSnapshot.get();

		return incompleteModelManager.getOrAddIncompleteModel(
			clazz, externalReferenceCode,
			fetchByExternalReferenceCodeBiFunction,
			getByExternalReferenceCodeUnsafeBiFunction, groupId,
			incompleteModelUnsafeSupplier);
	}

	public static boolean isIncompleteModel() {
		IncompleteModelManager incompleteModelManager =
			_incompleteModelManagerSnapshot.get();

		if (incompleteModelManager == null) {
			return false;
		}

		return incompleteModelManager.isIncompleteModel();
	}

	private static final Snapshot<IncompleteModelManager>
		_incompleteModelManagerSnapshot = new Snapshot<>(
			IncompleteModelManagerUtil.class, IncompleteModelManager.class);

}