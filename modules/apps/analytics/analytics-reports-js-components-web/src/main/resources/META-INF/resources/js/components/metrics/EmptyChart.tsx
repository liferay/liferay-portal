/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayLink from '@clayui/link';
import classNames from 'classnames';
import React from 'react';

export interface IChartEmptyStateProps {
	children?: React.ReactNode;
	description?: string;
	link?: {
		title: string;
		url: string;
	};
	show?: boolean;
	title?: string;
}

const ChartEmptyState: React.FC<IChartEmptyStateProps> = ({
	children,
	description,
	link,
	show,
	title,
}) => {
	return (
		<div
			className={classNames('empty-chart', {
				'empty-chart--show': show,
			})}
		>
			{children}

			{show && (
				<div className="empty-chart-content">
					<ClayEmptyState
						description={description}
						small
						title={title}
					/>

					<ClayLink href={link?.url} target="_blank">
						{link?.title}
					</ClayLink>
				</div>
			)}
		</div>
	);
};

export default ChartEmptyState;
