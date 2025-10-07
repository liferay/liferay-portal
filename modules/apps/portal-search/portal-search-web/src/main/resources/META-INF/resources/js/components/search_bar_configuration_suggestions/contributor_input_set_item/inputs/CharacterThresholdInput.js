/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import getCN from 'classnames';
import React from 'react';

function CharacterThresholdInput({index, onBlur, onChange, touched, value}) {
	return (
		<ClayInput.GroupItem
			className={getCN({
				'has-error': typeof value !== undefined && value < 0 && touched,
			})}
		>
			<label htmlFor={`characterThreshold${index}`}>
				{Liferay.Language.get('character-threshold')}

				<ClayTooltipProvider>
					<span
						className="c-ml-2"
						data-tooltip-align="top"
						tabIndex={0}
						title={Liferay.Language.get(
							'character-threshold-for-displaying-suggestions-contributor-help'
						)}
					>
						<ClayIcon symbol="question-circle-full" />
					</span>
				</ClayTooltipProvider>
			</label>

			<ClayInput
				aria-label={Liferay.Language.get('character-threshold')}
				id={`characterThreshold${index}`}
				min="0"
				onBlur={onBlur}
				onChange={onChange}
				type="number"
				value={value || ''}
			/>
		</ClayInput.GroupItem>
	);
}

export default CharacterThresholdInput;
