/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

interface AIAssistantTriggerProps
	extends Omit<React.ComponentProps<typeof ClayButton>, 'displayType'> {
	hideLabel?: boolean;
	label?: string;
	round?: boolean;
}

const AIAssistantTrigger = React.forwardRef<
	HTMLButtonElement,
	AIAssistantTriggerProps
>(({className, hideLabel = false, label, round = true, ...otherProps}, ref) => {
	const triggerLabel = label ?? Liferay.Language.get('ai-assistant');

	return (
		<ClayButton
			aria-label={triggerLabel}
			borderless
			{...otherProps}
			className={classNames('ai-assistant-chat__trigger', className)}
			displayType="secondary"
			monospaced={round && hideLabel}
			ref={ref}
			rounded={round}
		>
			<ClayIcon
				height={16}
				spritemap={Liferay.Icons.spritemap}
				symbol="stars"
				width={16}
			/>

			{!hideLabel && (
				<span className="ai-assistant-chat__trigger-label">
					{triggerLabel}
				</span>
			)}
		</ClayButton>
	);
});

AIAssistantTrigger.displayName = 'AIAssistantTrigger';

export default AIAssistantTrigger;
