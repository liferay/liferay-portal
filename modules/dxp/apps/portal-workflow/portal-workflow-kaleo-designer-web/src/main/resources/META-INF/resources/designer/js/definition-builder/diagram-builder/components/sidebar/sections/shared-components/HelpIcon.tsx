/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

interface HelpIconProps {
	className?: string;
	message: string;
}

export default function HelpIcon({className, message}: HelpIconProps) {
	return (
		<span
			aria-label={message}
			className={classNames('lfr-portal-tooltip', className)}
			role="img"
			tabIndex={0}
			title={message}
		>
			<ClayIcon className="text-muted" symbol="question-circle-full" />
		</span>
	);
}
