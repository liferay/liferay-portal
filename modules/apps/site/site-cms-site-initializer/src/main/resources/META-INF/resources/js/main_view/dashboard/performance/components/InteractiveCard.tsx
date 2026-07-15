/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClaySticker from '@clayui/sticker';
import {TrendClassification} from '@liferay/analytics-reports-js-components-web';
import classNames from 'classnames';
import React from 'react';

import {MetricValue} from '../../common/MetricValue';

import './InteractiveCard.scss';

export type MetricColor =
	| 'dark'
	| 'green'
	| 'orange'
	| 'pink'
	| 'purple'
	| 'red';

const STICKER_DISPLAY_TYPES: Record<
	MetricColor,
	React.ComponentProps<typeof ClaySticker>['displayType']
> = {
	dark: 'outline-0',
	green: 'outline-3',
	orange: 'outline-5',
	pink: 'outline-8',
	purple: 'outline-1',
	red: 'outline-4',
};

type Props = {
	active?: boolean;
	color: MetricColor;
	description?: string;
	hoverContent?: React.ReactNode;
	icon: string;
	loading?: boolean;
	onClick?: () => void;
	title: string;
	trend?: {
		classification: TrendClassification;
		percentage: number;
	};
	value?: React.ReactNode;
};

export default function InteractiveCard({
	active = false,
	color,
	description,
	hoverContent,
	icon,
	loading = false,
	onClick,
	title,
	trend,
	value,
}: Props) {
	return (
		<ClayButton
			className={classNames(
				'cms-dashboard__interactive-card h-100 p-3 rounded-lg sheet text-left w-100',
				{active}
			)}
			displayType="unstyled"
			onClick={onClick}
		>
			<div className="align-items-center d-flex">
				<div className="flex-grow-1">
					<Text size={4} weight="semi-bold">
						{title}
					</Text>
				</div>

				<ClaySticker
					borderless
					className="flex-shrink-0"
					displayType={STICKER_DISPLAY_TYPES[color]}
				>
					<ClayIcon symbol={icon} />
				</ClaySticker>
			</div>

			{description ? (
				<Text color="secondary" size={3}>
					{description}
				</Text>
			) : null}

			<div className="mt-2 position-relative">
				<div className="cms-dashboard__interactive-card__metric d-flex flex-column justify-content-center">
					{loading ? (
						<ClayLoadingIndicator size="sm" />
					) : (
						trend && (
							<MetricValue
								textWeight="bold"
								trend={trend}
								value={value}
								valueClassName="text-lowercase"
							/>
						)
					)}
				</div>

				{hoverContent ? (
					<div className="cms-dashboard__interactive-card__hover-content position-absolute">
						{hoverContent}
					</div>
				) : null}
			</div>
		</ClayButton>
	);
}
