/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import React from 'react';

import openSelectionModal from '../modal/commands/openSelectionModal';

function UnifiedSidebarSiteSelector({
	eventName,
	url,
}: {
	eventName: string;
	url: string;
}) {
	const openSiteSelector = () => {
		openSelectionModal({
			containerProps: {
				className: 'cadmin',
			},
			id: 'com_liferay_application_list_taglib_side_navigation_selectSite',
			onSelect({url}: {url: string}) {
				Liferay.Util.navigate(url);
			},
			selectEventName: eventName,
			title: Liferay.Language.get('select-site'),
			url,
		});
	};

	return (
		<ClayButtonWithIcon
			aria-label={Liferay.Language.get('go-to-other-site')}
			borderless
			className="c-ml-auto"
			data-qa-id="sideNavigationSiteSelectorButton"
			displayType="secondary"
			monospaced
			onClick={openSiteSelector}
			size="sm"
			symbol="sites"
			title={Liferay.Language.get('go-to-other-site')}
		/>
	);
}

export default UnifiedSidebarSiteSelector;
