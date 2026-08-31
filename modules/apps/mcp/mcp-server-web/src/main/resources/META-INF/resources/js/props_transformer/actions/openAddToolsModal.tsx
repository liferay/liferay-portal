/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';
import React, {useState} from 'react';

import AddToolsModal from '../../profiles/AddToolsModal';

interface AddToolsModalHostProps {
	loadData: () => void;
	profileERC: string;
}

function AddToolsModalHost({loadData, profileERC}: AddToolsModalHostProps) {
	const [open, setOpen] = useState(true);

	if (!open) {
		return null;
	}

	return (
		<AddToolsModal
			onAdded={loadData}
			onClose={() => setOpen(false)}
			profileERC={profileERC}
		/>
	);
}

export default function openAddToolsModal({
	loadData,
	profileERC,
}: AddToolsModalHostProps) {
	render(
		AddToolsModalHost,
		{loadData, profileERC},
		document.createElement('div')
	);
}
