/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ReactPortal} from '@liferay/frontend-js-react-web';
import React, {useMemo, useState} from 'react';

import ElementVariationsSimulationModal from './ElementVariationsSimulationModal';

type Props = Omit<
	React.ComponentProps<typeof ElementVariationsSimulationModal>,
	'onClose'
>;

export default function ElementVariationsSimulation(props: Props) {
	const container = useMemo(
		() => document.getElementById('elementVariationsSimulationContainer'),
		[]
	);

	const [openModal, setOpenModal] = useState(false);

	if (!container || !props.audiences.length) {
		return null;
	}

	return (
		<>
			<ReactPortal container={container}>
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('simulation')}
					className="control-menu-nav-link"
					displayType="unstyled"
					onClick={() => setOpenModal(true)}
					size="sm"
					symbol="simulation-menu-closed"
					title={Liferay.Language.get('simulation')}
				/>
			</ReactPortal>

			{openModal ? (
				<ElementVariationsSimulationModal
					{...props}
					onClose={() => setOpenModal(false)}
				/>
			) : null}
		</>
	);
}
