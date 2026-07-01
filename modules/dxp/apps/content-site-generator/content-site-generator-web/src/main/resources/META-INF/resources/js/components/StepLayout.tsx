/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import MultiStepProgress from './MultiStepProgress';

interface IProps {
	activeStep: number;
	children: React.ReactNode;
	sidebar?: React.ReactNode;
}

export default function StepLayout({activeStep, children, sidebar}: IProps) {
	const mainContent = (
		<div
			className={
				sidebar
					? 'content-site-generator__shell-main'
					: 'content-site-generator__shell-main content-site-generator__shell-main--centered'
			}
		>
			<div className="content-site-generator__progress">
				<MultiStepProgress activeStep={activeStep} />
			</div>

			{children}
		</div>
	);

	if (sidebar) {
		return (
			<div className="content-site-generator__shell">
				<div className="content-site-generator__shell-sidebar">
					{sidebar}
				</div>

				{mainContent}
			</div>
		);
	}

	return mainContent;
}
