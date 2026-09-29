/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ReactPortal} from '@liferay/frontend-js-react-web';
import React, {useMemo, useState} from 'react';

import ElementVariationsSimulationModal from './ElementVariationsSimulationModal';
import ElementVariationsSimulationNewTabModal from './ElementVariationsSimulationNewTabModal';

type Props = Omit<
	React.ComponentProps<typeof ElementVariationsSimulationModal>,
	'onClose'
>;

export default function ElementVariationsSimulation(props: Props) {
	const container = useMemo(
		() => document.getElementById('elementVariationsSimulationContainer'),
		[]
	);

	const [openModal, setOpenModal] = useState<'newTab' | 'simulation' | false>(
		false
	);

	if (!container || !props.audiences.length) {
		return null;
	}

	return (
		<>
			<ReactPortal container={container}>
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('simulation')}
					className="control-menu-nav-link d-md-inline-flex d-none"
					displayType="unstyled"
					onClick={() => setOpenModal('simulation')}
					size="sm"
					symbol="simulation-menu-closed"
					title={Liferay.Language.get('simulation')}
				/>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('simulation')}
					className="control-menu-nav-link d-md-none"
					displayType="unstyled"
					onClick={() => setOpenModal('newTab')}
					size="sm"
					symbol="simulation-menu-closed"
					title={Liferay.Language.get('simulation')}
				/>
			</ReactPortal>

			{openModal === 'simulation' ? (
				<ElementVariationsSimulationModal
					{...props}
					onClose={() => setOpenModal(false)}
				/>
			) : null}

			{openModal === 'newTab' ? (
				<ElementVariationsSimulationNewTabModal
					{...props}
					onClose={() => setOpenModal(false)}
				/>
			) : null}
		</>
	);
}
