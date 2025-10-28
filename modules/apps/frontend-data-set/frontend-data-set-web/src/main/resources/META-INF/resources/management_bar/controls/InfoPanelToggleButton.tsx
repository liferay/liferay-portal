/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import classnames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';

const InfoPanelToggleButton = ({symbol}: {symbol: string}) => {
	const {infoPanelId, infoPanelOpen, onInfoPanelToggleButtonClick} =
		useContext(FrontendDataSetContext);

	const tooltipText = sub(
		infoPanelOpen
			? Liferay.Language.get('hide-x')
			: Liferay.Language.get('show-x'),
		Liferay.Language.get('info-panel')
	);

	return (
		<ClayButtonWithIcon
			aria-controls={infoPanelId}
			aria-label={tooltipText}
			className={classnames('nav-link nav-link-monospaced', {
				active: infoPanelOpen,
			})}
			displayType="unstyled"
			onClick={() => onInfoPanelToggleButtonClick()}
			size="sm"
			symbol={symbol}
			title={tooltipText}
		/>
	);
};

export default InfoPanelToggleButton;
