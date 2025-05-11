/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import React from 'react';

export interface IBaseCard extends React.HTMLAttributes<HTMLElement> {
	Preferences?: React.ReactNode;
	description?: string;
	title: string;
}

const BaseCard: React.FC<IBaseCard> = ({
	Preferences,
	children,
	description,
	title,
}) => {
	return (
		<div className="cms-dashboard__base-card p-3 rounded-lg sheet">
			<div className="cms-dashboard__base-card__header d-flex">
				<div className="align-items-center d-flex flex-grow-1">
					<Text size={4} weight="semi-bold">
						{title.toUpperCase()}
					</Text>
				</div>

				{Preferences}
			</div>

			{description && (
				<div className="mt-1">
					<Text color="secondary" size={3}>
						{description}
					</Text>
				</div>
			)}

			<div className="mt-3">{children}</div>
		</div>
	);
};

export {BaseCard};
