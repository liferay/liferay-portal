/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

interface SectionHeaderProps {
	first?: boolean;
	id?: string;
	subtitle?: string;
	subtitleId?: string;
	title: string;
}

export default function SectionHeader({
	first,
	id,
	subtitle,
	subtitleId,
	title,
}: SectionHeaderProps) {
	return (
		<div className={classNames('mb-4 sheet-header', {'mt-5': !first})}>
			<div className="font-weight-bold mb-1 text-7" id={id}>
				{title}
			</div>

			{subtitle && (
				<p className="sheet-text text-secondary" id={subtitleId}>
					{subtitle}
				</p>
			)}
		</div>
	);
}
