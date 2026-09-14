/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import React from 'react';

import RestrictFieldsModal from '../../profiles/restrict_fields/RestrictFieldsModal';
import {ProfileToolActionContext} from '../../types';

export default function openRestrictFieldsModal({
	itemData,
	loadData,
}: ProfileToolActionContext) {
	openModal({
		className: 'modal-height-full',
		contentComponent: ({closeModal}: {closeModal: () => void}) => (
			<RestrictFieldsModal
				onClose={closeModal}
				onSaved={loadData}
				profileTool={itemData}
			/>
		),
		size: 'lg',
	});
}
