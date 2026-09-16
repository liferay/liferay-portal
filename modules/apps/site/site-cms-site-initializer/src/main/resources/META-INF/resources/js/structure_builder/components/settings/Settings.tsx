/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import React from 'react';

import {getImage} from '../../../common/utils/getImage';
import useSelectedItem from '../../hooks/useSelectedItem';
import GroupSettings from './GroupSettings';
import ReferencedStructureSettings from './ReferencedStructureSettings';
import RelatedContentSettings from './RelatedContentSettings';
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
				key={item.referencedStructure.uuid}
				referencedStructure={item.referencedStructure}
			/>
		);
	}

	if (item.type === 'related-content') {
		return (
			<RelatedContentSettings
				disabled={item.referenced}
				key={item.relatedContent.uuid}
				relatedContent={item.relatedContent}
			/>
		);
	}

	if (item.type === 'group') {
		return (
			<GroupSettings
				disabled={item.referenced}
				group={item.group}
				key={item.group.uuid}
			/>
		);
	}

	if (item.type === 'field') {
		return (
			<StructureFieldSettings
				field={item.field}
				isReferenced={item.referenced}
				key={item.field.uuid}
			/>
		);
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
