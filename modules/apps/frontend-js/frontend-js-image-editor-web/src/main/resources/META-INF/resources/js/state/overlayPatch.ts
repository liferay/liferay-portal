/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Overlay} from './types';

type Kind = Overlay['kind'];

const EDITABLE_KEYS: {[K in Kind]: ReadonlySet<string>} = {
	text: new Set([
		'color',
		'fontFamily',
		'fontSize',
		'opacity',
		'rotation',
		'text',
		'x',
		'y',
	]),
};

const STRING_KEYS = new Set(['color', 'fontFamily', 'text']);

const AT_LEAST_ONE = new Set(['fontSize']);

function validate(key: string, value: unknown): unknown {
	if (STRING_KEYS.has(key)) {
		return typeof value === 'string' ? value : undefined;
	}

	if (typeof value !== 'number' || !Number.isFinite(value)) {
		return undefined;
	}

	if (key === 'opacity') {
		return Math.min(100, Math.max(0, value));
	}

	if (AT_LEAST_ONE.has(key)) {
		return Math.max(1, value);
	}

	return value;
}

export function patchFor<O extends Overlay>(
	_overlay: O
): (patch: Partial<Omit<O, 'id' | 'kind'>>) => Partial<Overlay> {
	return (patch) => patch as Partial<Overlay>;
}

export function patchOverlay(
	overlay: Overlay,
	patch: Partial<Overlay>
): Overlay {
	const allowed = EDITABLE_KEYS[overlay.kind];

	let next: Overlay | null = null;

	for (const [key, raw] of Object.entries(patch)) {
		if (!allowed.has(key)) {
			continue;
		}

		const value = validate(key, raw);

		if (value === undefined) {
			continue;
		}

		if ((overlay as unknown as Record<string, unknown>)[key] === value) {
			continue;
		}

		if (!next) {
			next = {...overlay};
		}

		(next as unknown as Record<string, unknown>)[key] = value;
	}

	return next ?? overlay;
}
