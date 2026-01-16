/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {ReactNode} from 'react';

import './DetailedCard.scss';

type DetailedCardProps = {
	cardIcon?: string;
	cardIconAltText: string;
	cardTitle: string;
	children: ReactNode;
	className?: string;
	clayIcon?: string;
	headerActions?: ReactNode;
	sizing?: 'lg';
};

export function DetailedCard({
	cardIcon,
	cardIconAltText,
	cardTitle,
	children,
	className,
	clayIcon,
	headerActions,
}: DetailedCardProps) {
	return (
		<div className={className}>
			<div className="detailed-card-container">
				<div className="detailed-card-header">
					<h2>{cardTitle}</h2>

					{headerActions && (
						<div className="align-items-center d-flex ml-auto mr-4">
							{headerActions}
						</div>
					)}

					<div className="detailed-card-header-icon-container">
						{clayIcon ? (
							<ClayIcon
								className="detailed-card-header-clay-icon"
								symbol={clayIcon}
							/>
						) : (
							<img alt={cardIconAltText} src={cardIcon} />
						)}
					</div>
				</div>

				{children}
			</div>
		</div>
	);
}
