/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

interface FormSectionProps {
	children: React.ReactNode;
	className?: string;
	title: string;
}

export function FormSection({
	children,
	className = '',
	title,
}: FormSectionProps) {
	return (
		<ClayLayout.Sheet className={className}>
			<div className="font-weight-bold mb-4 text-7">{title}</div>

			{children}
		</ClayLayout.Sheet>
	);
}
