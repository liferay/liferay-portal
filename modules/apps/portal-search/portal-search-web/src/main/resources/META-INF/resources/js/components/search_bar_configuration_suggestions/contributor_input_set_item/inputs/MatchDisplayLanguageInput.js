/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React from 'react';

function MatchDisplayLanguageInput({index, onChange, value}) {
	return (
		<ClayInput.GroupItem>
			<label htmlFor={`matchDisplayLanguage${index}`}>
				{Liferay.Language.get('match-display-language')}

				<ClayTooltipProvider>
					<span
						className="c-ml-2"
						data-tooltip-align="top"
						tabIndex={0}
						title={Liferay.Language.get(
							'match-display-language-help'
						)}
					>
						<ClayIcon symbol="question-circle-full" />
					</span>
				</ClayTooltipProvider>
			</label>

			<ClaySelect
				aria-label={Liferay.Language.get('match-display-language')}
				id={`matchDisplayLanguage${index}`}
				onChange={onChange}
				value={value}
			>
				<ClaySelect.Option
					label={Liferay.Language.get('true')}
					value={true}
				/>

				<ClaySelect.Option
					label={Liferay.Language.get('false')}
					value={false}
				/>
			</ClaySelect>
		</ClayInput.GroupItem>
	);
}

export default MatchDisplayLanguageInput;
