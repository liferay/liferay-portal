/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {getBezierPath} from 'react-flow-renderer';

import {getEdgeParams} from './utils';

const FloatingConnectionLine = ({
	sourceNode,
	sourcePosition,
	targetPosition,
	targetX,
	targetY,
}) => {
	if (!sourceNode) {
		return null;
	}

	const targetNode = {
		__rf: {height: 1, position: {x: targetX, y: targetY}, width: 1},
		id: 'connection-target',
	};

	const {sx, sy} = getEdgeParams(sourceNode, targetNode);
	const d = getBezierPath({
		sourcePosition,
		sourceX: sx,
		sourceY: sy,
		targetPosition,
		targetX,
		targetY,
	});

	return (
		<g>
			<path className="animated" d={d} fill="none" strokeWidth={1.5} />

			<circle cx={targetX} cy={targetY} r={3} strokeWidth={1.5} />
		</g>
	);
};

export default FloatingConnectionLine;
