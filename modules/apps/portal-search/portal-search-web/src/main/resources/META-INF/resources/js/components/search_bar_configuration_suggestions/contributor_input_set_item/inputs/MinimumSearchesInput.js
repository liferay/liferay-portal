/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import getCN from 'classnames';
import {sub} from 'frontend-js-web';
import React from 'react';

function MinimumSearchesInput({index, onBlur, onChange, touched, value}) {
	return (
		<ClayInput.GroupItem
			className={getCN({
				'has-error': (!value || value < 0) && touched,
			})}
		>
			<label htmlFor={`minimumSearches${index}`}>
				<span>
					{Liferay.Language.get('minimum-searches')}

					<span className="reference-mark">
						<ClayIcon symbol="asterisk" />
					</span>
				</span>

				<ClayTooltipProvider>
					<span
						className="c-ml-2"
						data-tooltip-align="top"
						tabIndex={0}
						title={Liferay.Language.get('minimum-searches-help')}
					>
						<ClayIcon symbol="question-circle-full" />
					</span>
				</ClayTooltipProvider>
			</label>

			<ClayInput
				aria-label={Liferay.Language.get('minimum-searches')}
				id={`minimumSearches${index}`}
				min="0"
				onBlur={onBlur}
				onChange={onChange}
				required
				type="number"
				value={value || ''}
			/>

			{value < 0 && touched && (
				<div className="form-feedback-group">
					<div className="form-feedback-item">
						{sub(
							Liferay.Language.get(
								'please-enter-a-value-greater-than-or-equal-to-x'
							),
							'0'
						)}
					</div>
				</div>
			)}
		</ClayInput.GroupItem>
	);
}

export default MinimumSearchesInput;
