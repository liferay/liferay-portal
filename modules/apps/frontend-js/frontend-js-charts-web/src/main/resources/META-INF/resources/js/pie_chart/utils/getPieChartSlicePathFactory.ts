/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Point} from '../types/Point';
import {SliceAngles} from '../types/SliceAngles';
import {getPointOnCircle} from './getPointOnCircle';
import {isFullCircle} from './isFullCircle';

interface GetPieChartSlicePathFactoryParameters {
	centerX: number;
	centerY: number;
	innerRadius: number;
	outerRadius: number;
}

export function getPieChartSlicePathFactory({
	centerX,
	centerY,
	innerRadius,
	outerRadius,
}: GetPieChartSlicePathFactoryParameters): (angles: SliceAngles) => string {
	const isRingShaped = innerRadius > 0;

	return isRingShaped
		? getRingShapedPieChartSlicePathFactory(
				centerX,
				centerY,
				outerRadius,
				innerRadius
			)
		: getWedgeShapedPieChartSlicePathFactory(centerX, centerY, outerRadius);
}

function getWedgeShapedPieChartSlicePathFactory(
	centerX: number,
	centerY: number,
	outerRadius: number
): (angles: SliceAngles) => string {
	return (angles: SliceAngles): string => {
		if (isFullCircle(angles.sweepAngle)) {
			return _buildFullCirclePath(centerX, centerY, outerRadius);
		}

		return _buildWedgePath(
			centerX,
			centerY,
			getPointOnCircle(centerX, centerY, outerRadius, angles.startAngle),
			getPointOnCircle(centerX, centerY, outerRadius, angles.endAngle),
			outerRadius,
			angles.sweepAngle > Math.PI ? 1 : 0
		);
	};
}

function getRingShapedPieChartSlicePathFactory(
	centerX: number,
	centerY: number,
	outerRadius: number,
	innerRadius: number
): (angles: SliceAngles) => string {
	return (angles: SliceAngles): string => {
		if (isFullCircle(angles.sweepAngle)) {
			return _buildFullRingPath(
				centerX,
				centerY,
				outerRadius,
				innerRadius
			);
		}

		return _buildRingSegmentPath(
			getPointOnCircle(centerX, centerY, outerRadius, angles.startAngle),
			getPointOnCircle(centerX, centerY, outerRadius, angles.endAngle),
			getPointOnCircle(centerX, centerY, innerRadius, angles.startAngle),
			getPointOnCircle(centerX, centerY, innerRadius, angles.endAngle),
			outerRadius,
			innerRadius,
			angles.sweepAngle > Math.PI ? 1 : 0
		);
	};
}

function _buildFullCirclePath(
	centerX: number,
	centerY: number,
	outerRadius: number
): string {
	return (
		`M ${centerX - outerRadius} ${centerY} A ${outerRadius} ${outerRadius} 0 1 1 ${centerX + outerRadius} ${centerY} ` +
		`A ${outerRadius} ${outerRadius} 0 1 1 ${centerX - outerRadius} ${centerY} Z`
	);
}

function _buildWedgePath(
	centerX: number,
	centerY: number,
	outerStart: Point,
	outerEnd: Point,
	outerRadius: number,
	largeArcFlag: number
): string {
	return (
		`M ${centerX} ${centerY} L ${outerStart.x} ${outerStart.y} ` +
		`A ${outerRadius} ${outerRadius} 0 ${largeArcFlag} 1 ${outerEnd.x} ${outerEnd.y} Z`
	);
}

function _buildFullRingPath(
	centerX: number,
	centerY: number,
	outerRadius: number,
	innerRadius: number
): string {
	const outerRing = _buildFullCirclePath(centerX, centerY, outerRadius);

	return (
		`${outerRing} ` +
		`M ${centerX - innerRadius} ${centerY} A ${innerRadius} ${innerRadius} 0 1 0 ${centerX + innerRadius} ${centerY} ` +
		`A ${innerRadius} ${innerRadius} 0 1 0 ${centerX - innerRadius} ${centerY} Z`
	);
}

function _buildRingSegmentPath(
	outerStart: Point,
	outerEnd: Point,
	innerStart: Point,
	innerEnd: Point,
	outerRadius: number,
	innerRadius: number,
	largeArcFlag: number
): string {
	return (
		`M ${outerStart.x} ${outerStart.y} A ${outerRadius} ${outerRadius} 0 ${largeArcFlag} 1 ${outerEnd.x} ${outerEnd.y} ` +
		`L ${innerEnd.x} ${innerEnd.y} A ${innerRadius} ${innerRadius} 0 ${largeArcFlag} 0 ${innerStart.x} ${innerStart.y} Z`
	);
}
