/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React from 'react';

import DSRequestStatusLabel from '../ds_request_details/DSRequestStatusLabel';

export default function DSRequestStatusDataRenderer({value}) {
	return <DSRequestStatusLabel status={value} />;
}

DSRequestStatusDataRenderer.propTypes = {
	value: PropTypes.string,
};
