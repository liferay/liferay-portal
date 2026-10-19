/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {AlignPoints} from './useOverlayPosition';

type GetOffset = (points: AlignPoints) => [number, number];

type Props = {
	getOffset: GetOffset;
	points: AlignPoints;
	sourceElement: HTMLElement;
	targetElement: HTMLElement;
};

type VerticalPoints = {
	above: AlignPoints;
	below: AlignPoints;
};

const VERTICAL_OPPOSITES = {b: 't', t: 'b'} as const;

const constrainedSources = new WeakMap<
	HTMLElement,
	{constrained: string; previous: string}
>();

function constrainMaxHeight(sourceElement: HTMLElement, maxHeight: number) {
	const constrained = `${maxHeight}px`;

	constrainedSources.set(sourceElement, {
		constrained,
		previous: sourceElement.style.maxHeight,
	});

	sourceElement.style.maxHeight = constrained;
}

function flipVertically(points: AlignPoints) {
	return points.map(
		(point) =>
			VERTICAL_OPPOSITES[point[0] as keyof typeof VERTICAL_OPPOSITES] +
			point[1]
	) as unknown as AlignPoints;
}

function getAvailableSpace(
	verticalPoints: VerticalPoints,
	targetElement: HTMLElement,
	getOffset: GetOffset
) {
	const {bottom, top} = targetElement.getBoundingClientRect();

	return {
		above: Math.floor(top - getGap(verticalPoints.above, getOffset)),
		below: Math.floor(
			document.documentElement.clientHeight -
				bottom -
				getGap(verticalPoints.below, getOffset)
		),
	};
}

function getGap(points: AlignPoints, getOffset: GetOffset) {
	return Math.abs(getOffset(points)?.[1] ?? 0);
}

function getVerticalPoints(points: AlignPoints): VerticalPoints | null {
	const [sourcePoint, targetPoint] = points;

	if (sourcePoint[0] === 't' && targetPoint[0] === 'b') {
		return {above: flipVertically(points), below: points};
	}

	if (sourcePoint[0] === 'b' && targetPoint[0] === 't') {
		return {above: points, below: flipVertically(points)};
	}

	return null;
}

function restoreMaxHeight(sourceElement: HTMLElement) {
	const constrainedSource = constrainedSources.get(sourceElement);

	if (!constrainedSource) {
		return;
	}

	if (sourceElement.style.maxHeight === constrainedSource.constrained) {
		sourceElement.style.maxHeight = constrainedSource.previous;
	}

	constrainedSources.delete(sourceElement);
}

function constrainToLargerSide({
	getOffset,
	points,
	sourceElement,
	targetElement,
}: Props): AlignPoints {
	restoreMaxHeight(sourceElement);

	const verticalPoints = getVerticalPoints(points);

	if (!verticalPoints) {
		return points;
	}

	const availableSpace = getAvailableSpace(
		verticalPoints,
		targetElement,
		getOffset
	);

	const {height} = sourceElement.getBoundingClientRect();

	if (height <= availableSpace.above || height <= availableSpace.below) {
		return points;
	}

	const maxHeight = Math.max(availableSpace.above, availableSpace.below);

	if (maxHeight <= 0) {
		return points;
	}

	constrainMaxHeight(sourceElement, maxHeight);

	return availableSpace.below >= availableSpace.above
		? verticalPoints.below
		: verticalPoints.above;
}

export function constrainHeightToViewport(props: Props): AlignPoints {
	const {scrollTop} = props.sourceElement;

	const points = constrainToLargerSide(props);

	props.sourceElement.scrollTop = scrollTop;

	return points;
}
