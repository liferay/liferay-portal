/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EditState, Overlay, rotatedSize} from '../state/types';
import {coverScale} from './geometry';
import {overlayCenter, textWidth} from './overlayShapes';
import {pointsBounds} from './strokeGeometry';

export type Matrix = readonly [number, number, number, number, number, number];

export const IDENTITY: Matrix = [1, 0, 0, 1, 0, 0];

export function multiply(first: Matrix, second: Matrix): Matrix {
	const [a1, b1, c1, d1, tx1, ty1] = first;
	const [a2, b2, c2, d2, tx2, ty2] = second;

	return [
		a1 * a2 + c1 * b2,
		b1 * a2 + d1 * b2,
		a1 * c2 + c1 * d2,
		b1 * c2 + d1 * d2,
		a1 * tx2 + c1 * ty2 + tx1,
		b1 * tx2 + d1 * ty2 + ty1,
	];
}

export function invert(matrix: Matrix): Matrix {
	const [a, b, c, d, tx, ty] = matrix;

	const det = a * d - b * c;

	return [
		d / det,
		-b / det,
		-c / det,
		a / det,
		(c * ty - d * tx) / det,
		(b * tx - a * ty) / det,
	];
}

export function applyToPoint(
	matrix: Matrix,
	x: number,
	y: number
): [number, number] {
	const [a, b, c, d, tx, ty] = matrix;

	return [a * x + c * y + tx, b * x + d * y + ty];
}

export function applyToVector(
	matrix: Matrix,
	x: number,
	y: number
): [number, number] {
	const [a, b, c, d] = matrix;

	return [a * x + c * y, b * x + d * y];
}

function translation(tx: number, ty: number): Matrix {
	return [1, 0, 0, 1, tx, ty];
}

function rotationDeg(degrees: number): Matrix {
	const radians = (degrees * Math.PI) / 180;

	const cos = Math.cos(radians);
	const sin = Math.sin(radians);

	return [cos, sin, -sin, cos, 0, 0];
}

function about(inner: Matrix, cx: number, cy: number): Matrix {
	return multiply(
		translation(cx, cy),
		multiply(inner, translation(-cx, -cy))
	);
}

export function imageMatrix(
	state: Pick<
		EditState,
		'angle' | 'flipHorizontal' | 'rotation' | 'sourceHeight' | 'sourceWidth'
	>
): Matrix {
	const bounds = rotatedSize(state as EditState);

	let matrix: Matrix = IDENTITY;

	switch (state.rotation) {
		case 90:
			matrix = multiply(
				translation(state.sourceHeight, 0),
				rotationDeg(90)
			);
			break;

		case 180:
			matrix = multiply(
				translation(state.sourceWidth, state.sourceHeight),
				rotationDeg(180)
			);
			break;

		case 270:
			matrix = multiply(
				translation(0, state.sourceWidth),
				rotationDeg(270)
			);
			break;

		default:
			break;
	}

	if (state.angle) {
		const centerX = bounds.width / 2;
		const centerY = bounds.height / 2;

		const scale = coverScale(bounds.width, bounds.height, state.angle);

		const straighten = multiply(
			about(rotationDeg(state.angle), centerX, centerY),
			about([scale, 0, 0, scale, 0, 0], centerX, centerY)
		);

		matrix = multiply(straighten, matrix);
	}

	if (state.flipHorizontal) {
		matrix = multiply(
			multiply(translation(bounds.width, 0), [-1, 0, 0, 1, 0, 0]),
			matrix
		);
	}

	return matrix;
}

export function similarityOf(matrix: Matrix): {
	degrees: number;
	reflected: boolean;
	scale: number;
} {
	const [a, b, c, d] = matrix;

	return {
		degrees: (Math.atan2(b, a) * 180) / Math.PI,
		reflected: a * d - b * c < 0,
		scale: Math.hypot(a, b),
	};
}

const round = (value: number) => Math.round(value * 100) / 100;

function foldBoxRotation(
	width: number,
	height: number,
	rotation: number
): {height: number; rotation: number; width: number} {
	const folded = ((rotation % 360) + 360) % 360;

	const quarterTurns = Math.floor(folded / 90);

	const swapped = quarterTurns % 2 === 1;

	return {
		height: swapped ? width : height,
		rotation: round(folded - quarterTurns * 90),
		width: swapped ? height : width,
	};
}

function foldRotation(rotation: number, degrees: number): number | undefined {
	return round((((rotation + degrees) % 360) + 360) % 360) || undefined;
}

export function transformOverlay(overlay: Overlay, matrix: Matrix): Overlay {
	const {degrees, reflected, scale} = similarityOf(matrix);

	if (reflected) {
		throw new Error('transformOverlay: reflected matrix');
	}

	switch (overlay.kind) {
		case 'arrow': {
			const [x, y] = applyToPoint(matrix, overlay.x, overlay.y);
			const [dx, dy] = applyToVector(matrix, overlay.dx, overlay.dy);

			return {
				...overlay,
				dx: round(dx),
				dy: round(dy),
				thickness: round(overlay.thickness * scale),
				x: round(x),
				y: round(y),
			};
		}

		case 'circle':
		case 'shape': {
			const [cx, cy] = applyToPoint(
				matrix,
				overlay.x + overlay.width / 2,
				overlay.y + overlay.height / 2
			);

			const folded = foldBoxRotation(
				overlay.width * scale,
				overlay.height * scale,
				(overlay.rotation ?? 0) + degrees
			);

			return {
				...overlay,
				height: round(folded.height),
				rotation: folded.rotation || undefined,
				width: round(folded.width),
				x: round(cx - folded.width / 2),
				y: round(cy - folded.height / 2),
			};
		}

		case 'stroke': {
			const absolute: Array<[number, number]> = [];

			for (let index = 0; index < overlay.points.length; index += 2) {
				absolute.push(
					applyToPoint(
						matrix,
						overlay.x + overlay.points[index],
						overlay.y + overlay.points[index + 1]
					)
				);
			}

			const flat = absolute.flat();

			const box = pointsBounds(flat);

			return {
				...overlay,
				points: flat.map((value, index) =>
					round(value - (index % 2 === 0 ? box.x : box.y))
				),
				width: round(overlay.width * scale),
				x: round(box.x),
				y: round(box.y),
			};
		}

		case 'text': {
			const center = overlayCenter(overlay);

			const [cx, cy] = applyToPoint(matrix, center.x, center.y);

			const fontSize = round(overlay.fontSize * scale);

			const width = textWidth(overlay.text, overlay.fontFamily, fontSize);

			return {
				...overlay,
				fontSize,
				rotation: foldRotation(overlay.rotation ?? 0, degrees),
				x: round(cx - width / 2),
				y: round(cy + 0.4 * fontSize),
			};
		}

		default:
			return overlay;
	}
}
