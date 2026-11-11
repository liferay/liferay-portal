/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useRef} from 'react';

export function getFormSnapshot(form, namespace, isIgnored = () => false) {
	const excludedNames = new Set([
		`${namespace}formDate`,
		`${namespace}languageId`,
	]);

	const values = new Map();

	for (const element of form.elements) {
		if (
			element.name &&
			element.name.startsWith(namespace) &&
			!excludedNames.has(element.name) &&
			!isIgnored(element)
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

export function useFormChanges(namespace) {
	const formChangesRef = useRef(null);

	useEffect(() => {
		const form = document.getElementById(`${namespace}fm1`);

		if (!form) {
			return;
		}

		const formChanges = trackFormChanges(form, namespace);

		formChangesRef.current = formChanges;

		return () => {
			formChanges.detach();

			formChangesRef.current = null;
		};
	}, [namespace]);

	const hasFormChanges = useCallback(
		(snapshot) => formChangesRef.current?.hasChanges(snapshot) ?? true,
		[]
	);

	const markFormSaved = useCallback(() => {
		formChangesRef.current?.markSaved();
	}, []);

	const takeFormSnapshot = useCallback(
		() => formChangesRef.current?.takeSnapshot(),
		[]
	);

	return {hasFormChanges, markFormSaved, takeFormSnapshot};
}

function trackFormChanges(form, namespace) {
	const getSnapshot = () =>
		getFormSnapshot(
			form,
			namespace,
			(element) =>
				element.classList.contains('language-value') ||
				element.name === `${namespace}availableLocales` ||
				element.name.endsWith('_edited')
		);

	let savedSnapshot = getSnapshot();

	const addRenderedFields = () => {
		for (const [name, value] of getSnapshot()) {
			if (!savedSnapshot.has(name)) {
				savedSnapshot.set(name, value);
			}
		}
	};

	const hasNewFields = (mutationRecords) =>
		mutationRecords.some(
			({addedNodes, type}) =>
				type === 'attributes' ||
				Array.from(addedNodes).some(
					(node) => node.name || node.querySelector?.('[name]')
				)
		);

	const mutationObserver = new MutationObserver((mutationRecords) => {
		if (hasNewFields(mutationRecords)) {
			addRenderedFields();
		}
	});

	mutationObserver.observe(form, {
		attributeFilter: ['name'],
		childList: true,
		subtree: true,
	});

	return {
		detach() {
			mutationObserver.disconnect();
		},
		hasChanges(snapshot = savedSnapshot) {
			if (hasNewFields(mutationObserver.takeRecords())) {
				addRenderedFields();
			}

			return isFormSnapshotDifferent(getSnapshot(), snapshot);
		},
		markSaved() {
			savedSnapshot = getSnapshot();
		},
		takeSnapshot: getSnapshot,
	};
}
