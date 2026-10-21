/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React, {ReactNode, useId} from 'react';

import SectionHeader from './SectionHeader';

export default function ScopeSection({
	children,
	subtitle,
}: {
	children: ReactNode;
	subtitle: string;
}) {
	const labelId = useId();
	const subtitleId = useId();

	return (
		<div
			aria-describedby={subtitleId}
			aria-labelledby={labelId}
			role="group"
		>
			<SectionHeader
				id={labelId}
				subtitle={subtitle}
				subtitleId={subtitleId}
				title={Liferay.Language.get('scope')}
			/>

			<ClayLayout.Sheet>{children}</ClayLayout.Sheet>
		</div>
	);
}
