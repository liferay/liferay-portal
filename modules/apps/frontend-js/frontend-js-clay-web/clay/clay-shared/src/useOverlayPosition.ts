/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useCallback, useEffect, useLayoutEffect} from 'react';

import {doAlign} from './doAlign';
import {observeRect} from './observeRect';

type Props = {
	alignmentByViewport?: boolean;
	alignmentPosition?: number | AlignPoints;
	autoBestAlign?: boolean;
	constrainHeight?: boolean;
	getOffset?: (points: AlignPoints) => [number, number];
	isOpen: boolean;
	ref: React.RefObject<HTMLElement>;
	triggerRef: React.RefObject<HTMLElement>;
};

const ALIGN_INVERSE = {
	0: 'TopCenter',
	1: 'TopRight',
	2: 'RightCenter',
	3: 'BottomRight',
	4: 'BottomCenter',
	5: 'BottomLeft',
	6: 'LeftCenter',
	7: 'TopLeft',
	8: 'RightTop',
	9: 'RightBottom',
	10: 'LeftTop',
	11: 'LeftBottom',
} as const;

const ALIGN_MAP = {
	BottomCenter: ['tc', 'bc'],
	BottomLeft: ['tl', 'bl'],
	BottomRight: ['tr', 'br'],
	Center: ['cc', 'cc'],
	LeftBottom: ['br', 'bl'],
	LeftCenter: ['cr', 'cl'],
	LeftTop: ['tr', 'tl'],
	RightBottom: ['bl', 'br'],
	RightCenter: ['cl', 'cr'],
	RightTop: ['tl', 'tr'],
	TopCenter: ['bc', 'tc'],
	TopLeft: ['bl', 'tl'],
	TopRight: ['br', 'tr'],
} as const;

export type AlignPoints = (typeof ALIGN_MAP)[keyof typeof ALIGN_MAP];

/**
 * For backwards compatability, we are creating a util here so that `metal-position`
 * number values are used in the same manner and result in the same alignment direction.
 */
function getAlignPoints(val: keyof typeof ALIGN_INVERSE) {
	return ALIGN_MAP[ALIGN_INVERSE[val]];
}

const BOTTOM_OFFSET = [0, 4] as const;
const LEFT_OFFSET = [-4, 0] as const;
const RIGHT_OFFSET = [4, 0] as const;
const TOP_OFFSET = [0, -4] as const;

const OFFSET_MAP = {
	bctc: TOP_OFFSET,
	blbr: RIGHT_OFFSET,
	bltl: TOP_OFFSET,
	brbl: LEFT_OFFSET,
	brtr: TOP_OFFSET,
	clcr: RIGHT_OFFSET,
	crcl: LEFT_OFFSET,
	tcbc: BOTTOM_OFFSET,
	tlbl: BOTTOM_OFFSET,
	tltr: RIGHT_OFFSET,
	trbr: BOTTOM_OFFSET,
	trtl: LEFT_OFFSET,
};

const useIsomorphicLayoutEffect =
	typeof window === 'undefined' ? useEffect : useLayoutEffect;

function defaultOffset(points: AlignPoints) {
	return OFFSET_MAP[points.join('') as keyof typeof OFFSET_MAP] as [
		number,
		number,
	];
}

const VERTICAL_FLIP = {b: 't', t: 'b'} as const;

const constrainedSources = new WeakSet<HTMLElement>();

function constrainHeightToViewport(
	points: AlignPoints,
	sourceElement: HTMLElement,
	targetElement: HTMLElement,
	getOffset: (points: AlignPoints) => [number, number]
): AlignPoints {
	if (constrainedSources.has(sourceElement)) {
		sourceElement.style.maxHeight = '';

		constrainedSources.delete(sourceElement);
	}

	const [sourcePoint, targetPoint] = points;

	if (
		!(sourcePoint[0] in VERTICAL_FLIP) ||
		sourcePoint[0] === targetPoint[0]
	) {
		return points;
	}

	const flippedPoints = points.map(
		(point) =>
			VERTICAL_FLIP[point[0] as keyof typeof VERTICAL_FLIP] + point[1]
	) as unknown as AlignPoints;

	const [abovePoints, belowPoints] =
		sourcePoint[0] === 't'
			? [flippedPoints, points]
			: [points, flippedPoints];

	const {height} = sourceElement.getBoundingClientRect();
	const {bottom, top} = targetElement.getBoundingClientRect();

	const spaceAbove = Math.floor(
		top - Math.abs(getOffset(abovePoints)?.[1] ?? 0)
	);
	const spaceBelow = Math.floor(
		document.documentElement.clientHeight -
			bottom -
			Math.abs(getOffset(belowPoints)?.[1] ?? 0)
	);

	const space = Math.max(spaceAbove, spaceBelow);

	if (height <= spaceAbove || height <= spaceBelow || space <= 0) {
		return points;
	}

	sourceElement.style.maxHeight = `${space}px`;

	constrainedSources.add(sourceElement);

	return spaceBelow >= spaceAbove ? belowPoints : abovePoints;
}

export function useOverlayPosition(
	{
		alignmentByViewport,
		alignmentPosition = 5,
		autoBestAlign = true,
		constrainHeight = false,
		getOffset = defaultOffset,
		isOpen,
		ref,
		triggerRef,
	}: Props,
	deps: Array<any> = [isOpen]
) {
	const alignElement = useCallback(() => {
		if (triggerRef.current && ref.current) {
			let points = alignmentPosition;

			if (typeof points === 'number') {
				points = getAlignPoints(points as keyof typeof ALIGN_INVERSE);
			}

			if (constrainHeight) {
				points = constrainHeightToViewport(
					points,
					ref.current,
					triggerRef.current,
					getOffset
				);
			}

			doAlign({
				offset: getOffset(points),
				overflow: {
					adjustX: autoBestAlign,
					adjustY: autoBestAlign,
					alwaysByViewport: alignmentByViewport,
				},
				points,
				sourceElement: ref.current,
				targetElement: triggerRef.current,
			});
		}
	}, []);

	useIsomorphicLayoutEffect(() => {
		if (isOpen && triggerRef.current) {
			alignElement();

			return observeRect(triggerRef.current, alignElement);
		}
	}, deps);

	useIsomorphicLayoutEffect(() => {
		if (isOpen && ref.current) {
			alignElement();

			return observeRect(ref.current, alignElement);
		}
	}, deps);
}
