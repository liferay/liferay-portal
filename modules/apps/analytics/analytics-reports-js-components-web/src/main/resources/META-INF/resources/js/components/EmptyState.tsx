/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

interface IEmptyStateProps extends React.HTMLAttributes<HTMLElement> {
	description: string;
	externalImage?: {
		src: string;
		style: object;
	};
	maxWidth?: number;
	symbol?: string;
	title: string;
}

const EmptyState: React.FC<IEmptyStateProps> = ({
	children,
	className,
	description,
	externalImage,
	maxWidth = 268,
	symbol,
	title,
}) => (
	<div
		className={classNames('d-flex justify-content-center pt-6', className)}
	>
		<div
			className="align-items-center d-flex flex-column justify-content-center text-center"
			style={{maxWidth}}
		>
			{externalImage && (
				<div style={{...externalImage.style}}>
					<img src={externalImage.src} style={{width: '100%'}} />
				</div>
			)}

			<ClayEmptyState
				description={description}
				image={
					symbol ? (
						<ClayIcon
							className={`c-empty-state-icon c-empty-state-icon-${symbol}`}
							illustration
							symbol={symbol}
						/>
					) : undefined
				}
				small
				title={title}
			>
				{children}
			</ClayEmptyState>
		</div>
	</div>
);

export default EmptyState;
