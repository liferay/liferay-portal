/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import classnames from 'classnames';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';

const InfoPanelToggleButton = ({symbol}: {symbol: string}) => {
	const {infoPanelId, infoPanelOpen, onInfoPanelToggleButtonClick} =
		useContext(FrontendDataSetContext);

	return (
		<ClayButtonWithIcon
			aria-controls={infoPanelId}
			aria-label={Liferay.Language.get('toggle-info-panel')}
			className={classnames('nav-link nav-link-monospaced', {
				active: infoPanelOpen,
			})}
			displayType="unstyled"
			onClick={() => {
				onInfoPanelToggleButtonClick();
			}}
			size="sm"
			symbol={symbol}
		/>
	);
};

export default InfoPanelToggleButton;
