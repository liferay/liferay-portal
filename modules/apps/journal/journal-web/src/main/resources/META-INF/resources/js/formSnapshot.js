/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getFormSnapshot(form, namespace) {
	const excludedNames = new Set([
		`${namespace}formDate`,
		`${namespace}languageId`,
	]);

	const values = new Map();

	for (const element of form.elements) {
		if (
			element.name &&
			element.name.startsWith(namespace) &&
			!excludedNames.has(element.name)
		) {
			values.set(element.name, (element.value ?? '').trim());
		}
	}

	return values;
}

export function isFormSnapshotDifferent(snapshot1, snapshot2) {
	if (snapshot1.size !== snapshot2.size) {
		return true;
	}

	for (const [name, value] of snapshot1) {
		if (snapshot2.get(name) !== value) {
			return true;
		}
	}

	return false;
}
