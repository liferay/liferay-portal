/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal from '@clayui/modal';
import React from 'react';

import {ManageMembersList} from './ManageMembersList';
import {AddMembersInputApi, MembersConfig} from './types';

import '../css/components/ManageMembersModal.scss';

export interface ManageMembersModalProps {
	config: MembersConfig;
	emptyStateDescription: string;
	externalReferenceCode: string;
	filter?: string;
	hasAssignMembersPermission: boolean;
	headerTitle: string;
	ownerId?: string;
	renderAddMembersInput?: (api: AddMembersInputApi) => React.ReactNode;
}

export default function ManageMembersModal({
	config,
	emptyStateDescription,
	externalReferenceCode,
	filter,
	hasAssignMembersPermission,
	headerTitle,
	ownerId,
	renderAddMembersInput,
}: ManageMembersModalProps) {
	return (
		<div className="manage-member-modal">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{headerTitle}
			</ClayModal.Header>

			<ClayModal.Body>
				<ManageMembersList
					config={config}
					emptyStateDescription={emptyStateDescription}
					externalReferenceCode={externalReferenceCode}
					filter={filter}
					hasAssignMembersPermission={hasAssignMembersPermission}
					ownerId={ownerId}
					renderAddMembersInput={renderAddMembersInput}
				/>
			</ClayModal.Body>
		</div>
	);
}
