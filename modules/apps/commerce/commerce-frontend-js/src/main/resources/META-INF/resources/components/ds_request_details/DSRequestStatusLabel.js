/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import PropTypes from 'prop-types';
import React from 'react';

const DISPLAY_TYPES = {
	completed: 'success',
	created: 'secondary',
	declined: 'danger',
	expired: 'warning',
	sent: 'info',
	signed: 'success',
	voided: 'danger',
};

const STATUS_LABELS = {
	completed: Liferay.Language.get('completed'),
	created: Liferay.Language.get('waiting'),
	declined: Liferay.Language.get('declined'),
	expired: Liferay.Language.get('expired'),
	sent: Liferay.Language.get('sent'),
	signed: Liferay.Language.get('signed'),
	voided: Liferay.Language.get('voided'),
};

export function getDSRequestStatusDisplayType(status) {
	return DISPLAY_TYPES[status] || 'secondary';
}

export function getDSRequestStatusLabel(status) {
	return STATUS_LABELS[status] || status;
}

export default function DSRequestStatusLabel({status}) {
	if (!status) {
		return null;
	}

	return (
		<ClayLabel displayType={getDSRequestStatusDisplayType(status)}>
			{getDSRequestStatusLabel(status)}
		</ClayLabel>
	);
}

DSRequestStatusLabel.propTypes = {
	status: PropTypes.string,
};
