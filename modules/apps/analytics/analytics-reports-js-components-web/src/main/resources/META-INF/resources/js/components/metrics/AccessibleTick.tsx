/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useState} from 'react';

export type IAccessibleTickProps = {
	index: number;
	onTickBlur?: () => void;
	payload?: any;
	showTooltip?: (props: IAccessibleTickProps) => void;
	title?: string;
	visible: boolean;
	x: number;
	y: number;
};

const AccessibleTick: React.FC<
	IAccessibleTickProps & {activeTabIndex: boolean}
> = ({activeTabIndex, index, onTickBlur, payload, showTooltip, x, y}) => {
	const [active, setActive] = useState(false);

	return (
		<g transform={`translate(${x},${y})`}>
			<line
				className={classNames('accessibility-tick-line', {active})}
				onBlur={() => {
					setActive(false);
				}}
				onFocus={() => {
					setActive(true);
				}}
				onKeyDown={(event) => {
					if (
						event.key === 'Escape' ||
						(event.key === 'Tab' && event.shiftKey && index === 0)
					) {
						onTickBlur?.();
					}

					if (showTooltip) {
						if (event.key === 'Tab') {
							showTooltip({
								index: index + 1,
								payload,
								visible: true,
								x,
								y: 0,
							});

							return;
						}

						if (event.key === 'Tab' && event.shiftKey) {
							showTooltip({
								index: index - 1,
								payload,
								visible: true,
								x,
								y: 0,
							});

							return;
						}
					}
				}}
				tabIndex={activeTabIndex ? 0 : -1}
				x1={0}
				x2={0}
				y1={-20}
				y2={-255}
			/>
		</g>
	);
};

export default AccessibleTick;
