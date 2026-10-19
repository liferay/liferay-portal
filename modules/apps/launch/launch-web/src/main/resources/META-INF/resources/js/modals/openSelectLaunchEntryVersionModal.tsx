/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';
import React from 'react';

import {LaunchEntryVersion} from '../api/launches';
import SelectLaunchEntryVersionModal from '../components/SelectLaunchEntryVersionModal';

export default function openSelectLaunchEntryVersionModal({
	baseVersionKey,
	launchEntryVersions,
	onSelect,
}: {
	baseVersionKey?: string;
	launchEntryVersions: LaunchEntryVersion[];
	onSelect: (versionKey: string) => void;
}) {
	const container = document.createElement('div');

	document.body.appendChild(container);

	const root = render(
		<SelectLaunchEntryVersionModal
			baseVersionKey={baseVersionKey}
			launchEntryVersions={launchEntryVersions}
			onClose={() => {
				root?.unmount();

				container.remove();
			}}
			onSelect={onSelect}
		/>,
		{},
		container
	);
}
