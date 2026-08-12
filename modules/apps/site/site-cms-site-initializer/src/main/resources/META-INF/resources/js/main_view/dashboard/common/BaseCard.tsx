/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import classNames from 'classnames';
import React from 'react';

export interface IBaseCard extends React.HTMLAttributes<HTMLElement> {
	Preferences?: React.ReactNode;
	ariaLevel?: number;
	contentClassName?: string;
	description?: string;
	role?: string;
	title: string;
	uppercaseTitle?: boolean;
}

const BaseCard: React.FC<IBaseCard> = ({
	Preferences,
	ariaLevel,
	children,
	className,
	contentClassName,
	description,
	role,
	title,
	uppercaseTitle = true,
}) => {
	return (
		<div
			className={classNames(
				'cms-dashboard__base-card p-3 rounded-lg sheet',
				className
			)}
		>
			<div className="cms-dashboard__base-card__header d-flex">
				<div
					aria-level={ariaLevel}
					className="align-items-center d-flex flex-grow-1"
					role={role}
				>
					<Text size={4} weight="semi-bold">
						{uppercaseTitle ? title.toUpperCase() : title}
					</Text>
				</div>

				{Preferences}
			</div>

			{description && (
				<Text color="secondary" size={3}>
					{description}
				</Text>
			)}

			<div
				className={classNames(
					'd-flex',
					'flex-column',
					'justify-content-center',
					'mt-3',
					contentClassName
				)}
			>
				{children}
			</div>
		</div>
	);
};

export {BaseCard};
