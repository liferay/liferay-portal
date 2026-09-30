/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

const styles: React.CSSProperties = {
	border: 0,
	clip: 'rect(0 0 0 0)',
	clipPath: 'inset(50%)',
	height: '1px',
	margin: '0 -1px -1px 0',
	overflow: 'hidden',
	padding: 0,
	position: 'absolute',
	whiteSpace: 'nowrap',
	width: '1px',
};

type Props = {
	children?: React.ReactNode;
	liveAnnouncer?: boolean;
} & React.HTMLAttributes<HTMLDivElement>;

export const VisuallyHidden = React.forwardRef<HTMLDivElement, Props>(
	function VisuallyHidden({children, liveAnnouncer, ...props}, ref) {
		return (
			<div
				{...props}
				data-live-announcer={liveAnnouncer}
				ref={ref}
				style={liveAnnouncer ? {...styles, position: 'fixed'} : styles}
			>
				{children}
			</div>
		);
	}
);
