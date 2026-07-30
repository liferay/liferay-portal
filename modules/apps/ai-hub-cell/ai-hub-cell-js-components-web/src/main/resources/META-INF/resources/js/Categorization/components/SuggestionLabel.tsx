/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import classNames from 'classnames';
import React from 'react';

import {Suggestion} from '../types';

interface SuggestionLabelProps {
	disabled?: boolean;
	onDismiss: (suggestion: Suggestion) => void;
	suggestion: Suggestion;
}

export default function SuggestionLabel({
	disabled = false,
	onDismiss,
	suggestion,
}: SuggestionLabelProps) {
	return (
		<ClayLabel
			className={classNames('categorization-suggestion-label mr-2 mt-2', {
				'categorization-suggestion-label--new': suggestion.isNew,
			})}
			closeButtonProps={{
				'aria-label': Liferay.Language.get('remove'),
				disabled,
				'onClick': () => onDismiss(suggestion),
			}}
			displayType="secondary"
			inverse
			size="lg"
		>
			<span className="align-items-center d-inline-flex">
				{suggestion.isNew && (
					<ClayIcon
						className="mr-1"
						spritemap={Liferay.Icons.spritemap}
						symbol="stars"
					/>
				)}

				{suggestion.name}
			</span>
		</ClayLabel>
	);
}
