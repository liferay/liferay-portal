/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.fields;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Carlos Correa
 */
public class NestedFieldsSupplier<T> {

	public static void addNestedField(String nestedField) {
		NestedFieldsContext nestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();

		if (nestedFieldsContext != null) {
			nestedFieldsContext.addNestedField(nestedField);
		}
	}

	public static <T> T supply(
			String nestedField,
			UnsafeFunction<String, T, Exception> unsafeFunction)
		throws Exception {

		NestedFieldsContext nestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();

		if (!_mustProcessNestedFields(nestedFieldsContext)) {
			return null;
		}

		List<String> nestedFields = nestedFieldsContext.getNestedFields();

		if (!nestedFields.contains(nestedField)) {
			return null;
		}

		nestedFieldsContext.incrementCurrentDepth();

		try {
			return unsafeFunction.apply(nestedField);
		}
		finally {
			nestedFieldsContext.decrementCurrentDepth();
		}
	}

	public static <T> Map<String, T> supply(
			UnsafeFunction<String, T, Exception> unsafeFunction)
		throws Exception {

		NestedFieldsContext nestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();

		if (!_mustProcessNestedFields(nestedFieldsContext)) {
			return null;
		}

		Map<String, T> nestedFieldValues = new HashMap<>();

		nestedFieldsContext.incrementCurrentDepth();

		for (String nestedField : nestedFieldsContext.getNestedFields()) {
			T value = unsafeFunction.apply(nestedField);

			if (value != null) {
				nestedFieldValues.put(nestedField, value);
			}
		}

		nestedFieldsContext.decrementCurrentDepth();

		return nestedFieldValues;
	}

	public static <T> UnsafeSupplier<T, Exception> supplyScopedUnsafeSupplier(
		String nestedField, UnsafeSupplier<T, Exception> unsafeSupplier) {

		NestedFieldsContext oldNestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();

		if (oldNestedFieldsContext == null) {
			return () -> null;
		}

		NestedFieldsContext nestedFieldsContext = _createNestedFieldsContext(
			nestedField, oldNestedFieldsContext);

		return () -> {
			try (SafeCloseable safeCloseable =
					NestedFieldsContextThreadLocal.
						setNestedFieldsContextWithSafeCloseable(
							nestedFieldsContext)) {

				return supply(nestedField, __ -> unsafeSupplier.get());
			}
		};
	}

	public static Map<String, UnsafeSupplier<Object, Exception>>
			supplyUnsafeSupplier(
				UnsafeFunction
					<String, UnsafeSupplier<Object, Exception>, Exception>
						unsafeFunction)
		throws Exception {

		NestedFieldsContext nestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();

		if (!_mustProcessNestedFields(nestedFieldsContext)) {
			return null;
		}

		Map<String, UnsafeSupplier<Object, Exception>>
			nestedFieldUnsafeSuppliers = new HashMap<>();

		nestedFieldsContext.incrementCurrentDepth();

		NestedFieldsContext clonedNestedFieldsContext =
			nestedFieldsContext.clone();

		for (String nestedField : nestedFieldsContext.getNestedFields()) {
			UnsafeSupplier<Object, Exception> unsafeSupplier =
				unsafeFunction.apply(nestedField);

			if (unsafeSupplier == null) {
				continue;
			}

			nestedFieldUnsafeSuppliers.put(
				nestedField,
				() -> {
					try (SafeCloseable safeCloseable =
							NestedFieldsContextThreadLocal.
								setNestedFieldsContextWithSafeCloseable(
									clonedNestedFieldsContext)) {

						return unsafeSupplier.get();
					}
				});
		}

		nestedFieldsContext.decrementCurrentDepth();

		return nestedFieldUnsafeSuppliers;
	}

	private static NestedFieldsContext _createNestedFieldsContext(
		String nestedField, NestedFieldsContext nestedFieldsContext) {

		String prefix = nestedField + ".";

		List<String> nestedFields = TransformUtil.transform(
			nestedFieldsContext.getNestedFields(),
			currentNestedField -> {
				if (currentNestedField.equals(nestedField)) {
					return nestedField;
				}

				if (currentNestedField.startsWith(prefix)) {
					return currentNestedField.substring(prefix.length());
				}

				return null;
			});

		NestedFieldsContext scopedNestedFieldsContext = new NestedFieldsContext(
			nestedFieldsContext.getDepth(), nestedFieldsContext.getMessage(),
			nestedFields, nestedFieldsContext.getPathParameters(),
			nestedFieldsContext.getQueryParameters(),
			nestedFieldsContext.getResourceVersion());

		for (int i = 0; i < nestedFieldsContext.getCurrentDepth(); i++) {
			scopedNestedFieldsContext.incrementCurrentDepth();
		}

		return scopedNestedFieldsContext;
	}

	private static boolean _mustProcessNestedFields(
		NestedFieldsContext nestedFieldsContext) {

		if ((nestedFieldsContext != null) &&
			(nestedFieldsContext.getCurrentDepth() <
				nestedFieldsContext.getDepth()) &&
			ListUtil.isNotEmpty(nestedFieldsContext.getNestedFields())) {

			return true;
		}

		return false;
	}

}