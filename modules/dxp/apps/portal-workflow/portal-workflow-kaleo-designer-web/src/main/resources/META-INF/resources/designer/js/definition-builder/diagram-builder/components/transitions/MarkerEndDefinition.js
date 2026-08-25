/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

const markerEndId = 'arrowclosed';

function getMarkerEndId(edgeId) {
	return `${markerEndId}-${edgeId}`;
}

export default function MarkerEndDefinition({edgeId}) {
	return (
		<defs>
			<marker
				className="react-flow__arrowhead"
				id={getMarkerEndId(edgeId)}
				markerHeight="8"
				markerWidth="20"
				orient="auto"
				refX="0"
				refY="0"
				viewBox="-5 -5 10 10"
			>
				<polyline
					points="-4,-3 0,0 -4,3 -4,-3"
					strokeLinecap="round"
					strokeLinejoin="round"
					strokeWidth="1"
				/>
			</marker>
		</defs>
	);
}

export {getMarkerEndId};
