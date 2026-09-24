/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ColorPicker} from '@liferay/layout-js-components-web';
import PropTypes from 'prop-types';
import React from 'react';

export default function ColorFrontendToken({
	frontendToken,
	frontendTokensValues,
	labelAfter,
	onValueSelect,
	tokenValues,
	value,
}) {
	return (
		<ColorPicker
			editedTokenValues={frontendTokensValues}
			field={frontendToken}
			labelAfter={labelAfter}
			onValueSelect={(_, value) => onValueSelect(value)}
			tokenValues={tokenValues}
			value={value}
		/>
	);
}

ColorFrontendToken.propTypes = {
	frontendToken: PropTypes.shape({label: PropTypes.string.isRequired})
		.isRequired,
	onValueSelect: PropTypes.func.isRequired,
	value: PropTypes.string,
};
