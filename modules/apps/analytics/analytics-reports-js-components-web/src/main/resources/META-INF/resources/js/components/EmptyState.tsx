/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import classNames from 'classnames';
import React from 'react';

interface IEmptyStateProps extends React.HTMLAttributes<HTMLElement> {
	description: string;
	externalImage?: {
		src: string;
		style: object;
	};
	imgSrc?: string;
	maxWidth?: number;
	title: string;
}

const EmptyState: React.FC<IEmptyStateProps> = ({
	children,
	className,
	description,
	externalImage,
	imgSrc,
	maxWidth = 268,
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
				imgSrc={imgSrc}
				small
				title={title}
			>
				{children}
			</ClayEmptyState>
		</div>
	</div>
);

export default EmptyState;
