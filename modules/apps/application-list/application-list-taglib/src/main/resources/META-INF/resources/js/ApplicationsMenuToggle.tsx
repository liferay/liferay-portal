/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React, {useEffect, useState} from 'react';

interface Props {
	initialState: boolean;
}

function ApplicationsMenuToggle({initialState}: Props) {
	const [visible, setVisible] = useState(initialState);

	async function toggle() {
		Liferay.fire('applicationsMenuStateRequested', {visible: !visible});
	}

	useEffect(() => {
		function handleStateChanged({visible}: {visible: boolean}) {
			setVisible(visible);
		}

		Liferay.on('applicationsMenuStateChanged', handleStateChanged);

		return () =>
			Liferay.detach('applicationsMenuStateChanged', handleStateChanged);
	}, []);

	return (
		<button
			className="btn btn-monospaced btn-sm control-menu-nav-link lfr-portal-tooltip"
			id="com_liferay_application_list_taglib_applications_menu_toggle"
			onClick={toggle}
			role="tab"
		>
			{visible ? (
				<ClayIcon symbol="product-menu-open" />
			) : (
				<ClayIcon symbol="product-menu-closed" />
			)}
		</button>
	);
}

export default ApplicationsMenuToggle;
