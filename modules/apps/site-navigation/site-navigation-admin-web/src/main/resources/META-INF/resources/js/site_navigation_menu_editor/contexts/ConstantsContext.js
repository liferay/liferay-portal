/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React, {useContext} from 'react';

const ConstantsContext = React.createContext({});

export function useConstants() {
	return useContext(ConstantsContext);
}

export function ConstantsProvider({children, constants}) {
	return (
		<ConstantsContext.Provider value={constants}>
			{children}
		</ConstantsContext.Provider>
	);
}

ConstantsProvider.propTypes = {
	constants: PropTypes.shape({
		displayTemplateOptions: PropTypes.array,
		editSiteNavigationMenuItemParentURL: PropTypes.string,
		editSiteNavigationMenuItemURL: PropTypes.string,
		editSiteNavigationMenuSettingsURL: PropTypes.string,
		portletId: PropTypes.string,
		portletNamespace: PropTypes.string,
		previewSiteNavigationMenuURL: PropTypes.string,
		redirect: PropTypes.string,
		siteNavigationMenuId: PropTypes.string,
		siteNavigationMenuItems: PropTypes.arrayOf(
			PropTypes.shape({
				children: PropTypes.array.isRequired,
				siteNavigationMenuItemId: PropTypes.string.isRequired,
			}).isRequired
		),
		siteNavigationMenuName: PropTypes.string,
		spritemap: PropTypes.string,
	}),
};
