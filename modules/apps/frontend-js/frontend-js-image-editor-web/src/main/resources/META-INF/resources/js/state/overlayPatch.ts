/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Overlay} from './types';

type Kind = Overlay['kind'];

const BOX_KEYS = [
	'borderColor',
	'borderWidth',
	'color',
	'height',
	'opacity',
	'rotation',
	'sketchSeed',
	'width',
	'x',
	'y',
];

const EDITABLE_KEYS: {[K in Kind]: ReadonlySet<string>} = {
	arrow: new Set([
		'color',
		'dx',
		'dy',
		'head',
		'opacity',
		'thickness',
		'x',
		'y',
	]),
	circle: new Set(BOX_KEYS),
	emoji: new Set([
		'character',
		'name',
		'opacity',
		'rotation',
		'size',
		'x',
		'y',
	]),
	redact: new Set([
		'height',
		'level',
		'opacity',
		'rotation',
		'style',
		'width',
		'x',
		'y',
	]),
	shape: new Set(BOX_KEYS),
	stroke: new Set([
		'color',
		'opacity',
		'points',
		'rotation',
		'smooth',
		'width',
		'x',
		'y',
	]),
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

const STRING_KEYS = new Set([
	'borderColor',
	'character',
	'color',
	'fontFamily',
	'name',
	'text',
]);

const ENUM_KEYS: Record<string, ReadonlySet<string>> = {
	head: new Set(['filled', 'open']),
	level: new Set(['coarse', 'fine', 'medium', 'tiny']),
	style: new Set(['blur', 'pixel']),
};

const CLEARABLE_KEYS = new Set(['borderColor', 'borderWidth', 'sketchSeed']);

const AT_LEAST_ONE = new Set([
	'fontSize',
	'height',
	'size',
	'thickness',
	'width',
]);

function validate(key: string, value: unknown): unknown {
	if (key === 'smooth') {
		return typeof value === 'boolean' ? value : undefined;
	}

	if (key === 'points') {
		return Array.isArray(value) &&
			value.length >= 2 &&
			value.length % 2 === 0 &&
			value.every((entry) => Number.isFinite(entry))
			? value
			: undefined;
	}

	if (ENUM_KEYS[key]) {
		return typeof value === 'string' && ENUM_KEYS[key].has(value)
			? value
			: undefined;
	}

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

	if (key === 'borderWidth') {
		return Math.max(0, value);
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

		const clearing = raw === undefined && CLEARABLE_KEYS.has(key);

		const value = clearing ? undefined : validate(key, raw);

		if (value === undefined && !clearing) {
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
