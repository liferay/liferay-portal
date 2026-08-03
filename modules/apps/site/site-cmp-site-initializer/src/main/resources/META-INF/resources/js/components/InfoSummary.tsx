/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useState} from 'react';

import './InfoSummary.scss';

import {ClayButtonWithIcon} from '@clayui/button';

interface InfoItem {
	label: string;
	value: string | React.ReactElement;
}

interface InfoSummaryProps {
	defaultOpen?: boolean;
	items: InfoItem[];
	title?: string;
}

export default function InfoSummary({
	defaultOpen = true,
	items,
	title = Liferay.Language.get('info'),
}: InfoSummaryProps) {
	const [isOpen, setIsOpen] = useState(defaultOpen);

	return (
		<div className="lfr-cmp__info-summary-container">
			<div className="lfr-cmp__info-summary">
				<div className="lfr-cmp__info-summary-header-container">
					<span className="lfr-cmp__info-summary-header-content">
						{title}
					</span>

					<ClayButtonWithIcon
						displayType="unstyled"
						onClick={() => setIsOpen((prev) => !prev)}
						symbol={isOpen ? 'angle-down' : 'angle-right'}
					/>
				</div>

				<div
					className={classNames(
						'lfr-cmp__info-summary-content-container',
						{
							'lfr-cmp__info-summary-content-container--hidden':
								!isOpen,
						}
					)}
				>
					{items.map((item) => (
						<div
							className="lfr-cmp__info-summary-content-item"
							key={item.label}
						>
							<span className="lfr-cmp__info-summary-content-label">
								{item.label}{' '}
							</span>

							<div className="lfr-cmp__info-summary-content-value">
								{item.value}
							</div>
						</div>
					))}
				</div>
			</div>
		</div>
	);
}
