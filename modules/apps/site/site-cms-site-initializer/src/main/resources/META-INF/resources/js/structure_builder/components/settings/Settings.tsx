/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import React from 'react';

import {getImage} from '../../../main/util/getImage';
import useSelectedItem from '../../contexts/hooks/useSelectedItem';
import ReferencedStructureSettings from './ReferencedStructureSettings';
import StructureFieldSettings from './StructureFieldSettings';
import StructureSettings from './StructureSettings';

export default function Settings() {
	const item = useSelectedItem();

	if (item.type === 'multiselection') {
		return <MultiselectionState />;
	}

	if (item.type === 'referenced-structure') {
		return (
			<ReferencedStructureSettings
				referencedStructure={item.referencedStructure}
			/>
		);
	}

	if (item.type === 'field') {
		return <StructureFieldSettings field={item.field} />;
	}

	if (item.type === 'referenced-field') {
		return <StructureFieldSettings disabled field={item.field} />;
	}

	return <StructureSettings />;
}

function MultiselectionState() {
	return (
		<ClayEmptyState
			className="justify-content-center structure-builder__empty-state"
			description=""
			imgSrc={getImage('multiselection_state.svg')}
			imgSrcReducedMotion={getImage('multiselection_state.svg')}
			small
			title={Liferay.Language.get('multiple-items-selected')}
		/>
	);
}
