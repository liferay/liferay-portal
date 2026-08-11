/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

const HelpTooltipIcon = ({
	className = 'ml-1',
	message,
}: {
	className?: string;
	message: string;
}) => (
	<span
		aria-label={message}
		className={classNames('lfr-portal-tooltip text-secondary', className)}
		data-title={message}
		onMouseDown={(event) => event.preventDefault()}
		role="img"
		tabIndex={0}
	>
		<ClayIcon symbol="question-circle" />
	</span>
);

export default HelpTooltipIcon;
