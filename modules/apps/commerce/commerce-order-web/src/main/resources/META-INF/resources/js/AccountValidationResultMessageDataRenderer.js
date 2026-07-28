/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';

export default function AccountValidationResultMessageDataRenderer({
	additionalProps,
	value,
}) {
	return additionalProps?.resultMessages?.[value] || value;
}

AccountValidationResultMessageDataRenderer.propTypes = {
	additionalProps: PropTypes.shape({
		resultMessages: PropTypes.objectOf(PropTypes.string),
	}),
	value: PropTypes.string,
};
