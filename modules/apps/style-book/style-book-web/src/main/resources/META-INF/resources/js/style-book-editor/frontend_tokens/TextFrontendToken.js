/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import {useControlledState} from '@liferay/layout-js-components-web';
import {useId} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React from 'react';

export default function TextFrontendToken({
	frontendToken,
	labelAfter,
	onValueSelect,
	value,
}) {
	const {label} = frontendToken;
	const [nextValue, setNextValue] = useControlledState(value);

	const id = useId();

	return (
		<ClayForm.Group small>
			<label htmlFor={id}>{label}</label>

			{labelAfter}

			<ClayInput
				id={id}
				onBlur={() => {
					if (nextValue !== value) {
						onValueSelect(nextValue);
					}
				}}
				onChange={(event) => {
					setNextValue(event.target.value);
				}}
				type="text"
				value={nextValue}
			/>
		</ClayForm.Group>
	);
}

TextFrontendToken.propTypes = {
	frontendToken: PropTypes.shape({
		label: PropTypes.string.isRequired,
	}).isRequired,
	onValueSelect: PropTypes.func.isRequired,
	value: PropTypes.any,
};
