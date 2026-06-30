/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React from 'react';

const PickerTrigger = React.forwardRef<HTMLButtonElement, any>(
	(
		{borderless, children, triggerClassName, triggerIcon, ...otherProps},
		ref
	) => (
		<ClayButton
			{...otherProps}
			borderless={borderless}
			className={triggerClassName}
			displayType="secondary"
			ref={ref}
			size="sm"
		>
			{triggerIcon && <ClayIcon className="mr-2" symbol={triggerIcon} />}

			{children}

			<ClayIcon className="ml-2" symbol="caret-bottom" />
		</ClayButton>
	)
);

export default PickerTrigger;
