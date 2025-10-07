/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {API, ManagementToolbar} from '@liferay/object-js-components-web';
import React from 'react';

interface ObjectManagementToolbarProps {
	backURL: string;
	hasPublishObjectPermission: boolean;
	hasUpdateObjectDefinitionPermission: boolean;
	isApproved: boolean;
	isRootDescendantNode: boolean;
	isRootNode: boolean;
	label: string;
	loading: boolean;
	objectDefinitionExternalReferenceCode: string;
	objectDefinitionId: number;
	onSubmit: (draft: boolean) => void;
	portletNamespace: string;
	screenNavigationCategoryKey: string;
	system: boolean;
}

export default function ObjectManagementToolbar({
	backURL,
	hasPublishObjectPermission,
	hasUpdateObjectDefinitionPermission,
	isApproved,
	isRootDescendantNode,
	isRootNode,
	label,
	loading,
	objectDefinitionExternalReferenceCode,
	objectDefinitionId,
	onSubmit,
	portletNamespace,
	screenNavigationCategoryKey,
	system,
}: ObjectManagementToolbarProps) {
	const inheritanceLabel = isRootDescendantNode
		? Liferay.Language.get('inherited')
		: isRootNode
			? Liferay.Language.get('root-object')
			: Liferay.Language.get('standard');

	return (
		<ManagementToolbar
			backURL={backURL}
			badgeClassName={system ? 'label-info' : 'label-warning'}
			badgeLabel={
				system
					? Liferay.Language.get('system')
					: Liferay.Language.get('custom')
			}
			className="border-bottom"
			enableBoxShadow={false}
			entityId={objectDefinitionId}
			hasPublishPermission={hasPublishObjectPermission}
			hasUpdatePermission={hasUpdateObjectDefinitionPermission}
			helpMessage={Liferay.Language.get(
				'unique-key-for-referencing-the-object-definition'
			)}
			inheritanceClassName={
				isRootDescendantNode || isRootNode
					? 'label-inverse-info'
					: 'label-inverse-secondary'
			}
			inheritanceLabel={inheritanceLabel}
			isApproved={isApproved}
			isRootDescendantNode={isRootDescendantNode}
			label={label}
			loading={loading}
			objectDefinitionExternalReferenceCode={
				objectDefinitionExternalReferenceCode
			}
			objectDefinitionExternalReferenceCodeSaveURL={`/o/object-admin/v1.0/object-definitions/${objectDefinitionId}`}
			onGetEntity={() => API.getObjectDefinitionById(objectDefinitionId)}
			onSubmit={onSubmit}
			portletNamespace={portletNamespace}
			screenNavigationCategoryKey={screenNavigationCategoryKey}
		/>
	);
}
