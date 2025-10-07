/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';

import './AccountAndAppCard.scss';

interface AppCardProps {
	category: string;
	className?: string;
	logo: string;
	title: string | JSX.Element;
}

export function AccountAndAppCard({
	category,
	className,
	logo,
	title,
}: AppCardProps) {
	return (
		<div className={classNames('card-container', className)}>
			<div className="card-logo">
				<img
					alt="logo"
					className="card-logo-image"
					draggable="false"
					src={logo}
				/>
			</div>

			<div className="card-info">
				<span className="card-info-description">{category}</span>

				<span className="card-info-text">{title}</span>
			</div>
		</div>
	);
}
