/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function pointsBounds(points: number[]): {
	height: number;
	width: number;
	x: number;
	y: number;
} {
	let maxX = -Infinity;
	let maxY = -Infinity;
	let minX = Infinity;
	let minY = Infinity;

	for (let index = 0; index < points.length; index += 2) {
		maxX = Math.max(maxX, points[index]);
		maxY = Math.max(maxY, points[index + 1]);
		minX = Math.min(minX, points[index]);
		minY = Math.min(minY, points[index + 1]);
	}

	return {
		height: maxY - minY,
		width: maxX - minX,
		x: minX,
		y: minY,
	};
}

export function pointsToPath(
	points: number[],
	smooth: boolean,
	closed = false
): string {
	const count = points.length / 2;

	if (!count) {
		return '';
	}

	const at = (rawIndex: number): [number, number] => {
		let index = rawIndex;

		if (closed) {
			index = ((rawIndex % count) + count) % count;
		}
		else {
			index = Math.min(Math.max(rawIndex, 0), count - 1);
		}

		return [points[index * 2], points[index * 2 + 1]];
	};

	let path = `M${at(0)[0]} ${at(0)[1]}`;

	if (count === 1) {

		// A dot: a zero-length segment that round linecaps turn into a
		// filled circle of the stroke's own width.

		return `${path} l0.01 0`;
	}

	const segments = closed ? count : count - 1;

	for (let index = 0; index < segments; index++) {
		const [x1, y1] = at(index);
		const [x2, y2] = at(index + 1);

		if (!smooth) {
			path += ` L${x2} ${y2}`;
			continue;
		}

		const [x0, y0] = at(index - 1);
		const [x3, y3] = at(index + 2);

		// Catmull-Rom to bezier, tension 1/6.

		const c1x = x1 + (x2 - x0) / 6;
		const c1y = y1 + (y2 - y0) / 6;
		const c2x = x2 - (x3 - x1) / 6;
		const c2y = y2 - (y3 - y1) / 6;

		path += ` C${round(c1x)} ${round(c1y)} ${round(c2x)} ${round(
			c2y
		)} ${x2} ${y2}`;
	}

	return closed ? `${path} Z` : path;
}

/**
 * Deterministic pseudo-randomness (mulberry32). The hand-drawn style must
 * wobble the same way on the stage, on the export and on every re-render,
 * so the wobble is a function of a stored seed, never of the clock.
 */
export function simplifyPoints(points: number[], epsilon: number): number[] {
	const count = points.length / 2;

	if (count <= 2) {
		return [...points];
	}

	const keep = new Array(count).fill(false);

	keep[0] = true;
	keep[count - 1] = true;

	const stack: Array<[number, number]> = [[0, count - 1]];

	while (stack.length) {
		const [first, last] = stack.pop()!;

		const firstX = points[first * 2];
		const firstY = points[first * 2 + 1];
		const lastX = points[last * 2];
		const lastY = points[last * 2 + 1];

		const runX = lastX - firstX;
		const runY = lastY - firstY;
		const length = Math.hypot(runX, runY) || 1;

		let farthest = 0;
		let farthestIndex = -1;

		for (let index = first + 1; index < last; index++) {
			const distance =
				Math.abs(
					runX * (firstY - points[index * 2 + 1]) -
						runY * (firstX - points[index * 2])
				) / length;

			if (distance > farthest) {
				farthest = distance;
				farthestIndex = index;
			}
		}

		if (farthest > epsilon && farthestIndex > 0) {
			keep[farthestIndex] = true;

			stack.push([first, farthestIndex], [farthestIndex, last]);
		}
	}

	const kept: number[] = [];

	for (let index = 0; index < count; index++) {
		if (keep[index]) {
			kept.push(points[index * 2], points[index * 2 + 1]);
		}
	}

	return kept;
}

export function seededRandom(seed: number): () => number {
	let state = seed >>> 0;

	return () => {
		state = (state + 0x6d2b79f5) >>> 0;

		let mixed = Math.imul(state ^ (state >>> 15), 1 | state);

		mixed ^= mixed + Math.imul(mixed ^ (mixed >>> 7), 61 | mixed);

		return ((mixed ^ (mixed >>> 14)) >>> 0) / 4294967296;
	};
}

/**
 * A hand-drawn ellipse: sampled around the circumference with jittered
 * radii, closed with the same smoothing as the rectangle.
 */
export function sketchyEllipsePath(
	centerX: number,
	centerY: number,
	radiusX: number,
	radiusY: number,
	seed: number
): string {
	const random = seededRandom(seed);

	const jitter = Math.max(Math.min(radiusX, radiusY) * 0.04, 1.5);

	const points: number[] = [];

	const steps = 12;

	for (let step = 0; step < steps; step++) {
		const angle = (step / steps) * Math.PI * 2;

		const wobble = (random() * 2 - 1) * jitter;

		points.push(
			centerX + Math.cos(angle) * (radiusX + wobble),
			centerY + Math.sin(angle) * (radiusY + wobble)
		);
	}

	return pointsToPath(points, true, true);
}

/**
 * A hand-drawn rectangle: each side sampled into a few points, each point
 * nudged by the seeded jitter, the whole thing drawn as a smooth closed
 * curve. The jitter scales with the shorter side so a small shape wobbles
 * subtly and a poster-sized one visibly, like a real hand would.
 */
export function sketchyRectPath(
	x: number,
	y: number,
	width: number,
	height: number,
	seed: number
): string {
	const random = seededRandom(seed);

	const jitter = Math.max(Math.min(width, height) * 0.02, 1.5);

	const nudge = () => (random() * 2 - 1) * jitter;

	const corners = [
		[x, y],
		[x + width, y],
		[x + width, y + height],
		[x, y + height],
	];

	const points: number[] = [];

	for (let side = 0; side < 4; side++) {
		const [fromX, fromY] = corners[side];
		const [toX, toY] = corners[(side + 1) % 4];

		// The corner itself plus two waypoints along the side.

		for (const t of [0, 0.34, 0.67]) {
			points.push(
				fromX + (toX - fromX) * t + nudge(),
				fromY + (toY - fromY) * t + nudge()
			);
		}
	}

	return pointsToPath(points, true, true);
}

function round(value: number): number {
	return Math.round(value * 100) / 100;
}
