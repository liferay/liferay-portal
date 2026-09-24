/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LengthInput} from '@liferay/layout-js-components-web';
import PropTypes from 'prop-types';
import React from 'react';

export default function LengthFrontendToken({
	frontendToken,
	labelAfter,
	onValueSelect,
	value,
}) {
	return (
		<LengthInput
			field={frontendToken}
			labelAfter={labelAfter}
			onValueSelect={(_, value) => onValueSelect(value)}
			value={value}
		/>
	);
}

LengthFrontendToken.propTypes = {
	frontendToken: PropTypes.shape({label: PropTypes.string.isRequired})
		.isRequired,
	onValueSelect: PropTypes.func.isRequired,
	value: PropTypes.string,
};
