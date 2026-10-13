/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';
import React from 'react';

import NewLaunchModal from '../components/NewLaunchModal';

export default function openNewLaunchModal({
	viewLaunchURL,
}: {
	viewLaunchURL: string;
}) {
	const container = document.createElement('div');

	document.body.appendChild(container);

	const root = render(
		<NewLaunchModal
			onClose={() => {
				root?.unmount();

				container.remove();
			}}
			viewLaunchURL={viewLaunchURL}
		/>,
		{},
		container
	);
}
