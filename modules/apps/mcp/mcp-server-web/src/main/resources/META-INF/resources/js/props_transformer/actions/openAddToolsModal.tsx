/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import React from 'react';

import AddToolsModal from '../../profiles/AddToolsModal';

interface OpenAddToolsModalProps {
	loadData: () => void;
	profileERC: string;
}

export default function openAddToolsModal({
	loadData,
	profileERC,
}: OpenAddToolsModalProps) {
	openModal({
		className: 'modal-height-full',
		contentComponent: ({closeModal}: {closeModal: () => void}) => (
			<AddToolsModal
				onAdded={loadData}
				onClose={closeModal}
				profileERC={profileERC}
			/>
		),
		size: 'lg',
	});
}
