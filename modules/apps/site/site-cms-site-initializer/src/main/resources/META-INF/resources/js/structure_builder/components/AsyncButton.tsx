/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useState} from 'react';

type Props = {
	disabled?: boolean;
	displayType?: 'primary' | 'secondary';
	label: string;
	onClick: () => Promise<void>;
};

type Status = 'loading' | 'idle';

export default function AsyncButton({
	disabled,
	displayType = 'primary',
	label,
	onClick,
}: Props) {
	const [status, setStatus] = useState<Status>('idle');

	return (
		<ClayButton
			className="align-items-center c-gap-2 d-flex"
			disabled={disabled || status === 'loading'}
			displayType={displayType}
			onClick={async () => {
				setStatus('loading');

				await onClick();

				setStatus('idle');
			}}
			size="sm"
		>
			{status === 'loading' ? (
				<ClayLoadingIndicator className="m-0" />
			) : null}

			{label}
		</ClayButton>
	);
}
