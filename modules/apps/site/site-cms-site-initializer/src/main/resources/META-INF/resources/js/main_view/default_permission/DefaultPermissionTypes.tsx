/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface Action {
	guestUnsupported?: boolean;
	key: string;
	label: string;
}

export interface ActionsMap {
	[key: string]: Array<Action>;
}

export interface AssetRoleSelectedActions {
	[key: string]: RoleSelectedActions;
}

export interface AssetType {
	key: DefaultAssetTypes;
	label: string;
}

export interface BulkDefaultPermissionModalContentProps {
	actions: ActionsMap;
	className: string;
	closeModal: () => void;
	roles: Role[];
	selectedData: any;
}

export interface BulkPermissionModalContentProps {
	actions: ActionsMap;
	className: string;
	closeModal: () => void;
	roles: Role[];
	selectedData: any;
}

export interface CheckedRoleActions {
	[key: string]: boolean;
}

export interface CMSDefaultPermissionObjectEntryDTO {
	classExternalReferenceCode: string;
	className: string;
	defaultPermissions: string;
	depotGroupId: number;
	externalReferenceCode: string;
	id: number;
	treePath: string;
}

export enum DefaultAssetTypes {
	L_CONTENTS = 'L_CONTENTS',
	L_FILES = 'L_FILES',
	OBJECT_ENTRY_FOLDERS = 'OBJECT_ENTRY_FOLDERS',
}

export interface DefaultPermissionFormContainerProps {
	actions: ActionsMap;
	disabled?: boolean;
	infoBoxMessage?: string;
	onChange?: (data: AssetRoleSelectedActions) => void;
	roles: Role[];
	types?: AssetType[];
	values?: AssetRoleSelectedActions;
}

export interface DefaultPermissionFormProps {
	actions: Action[];
	disabled?: boolean;
	infoBoxMessage?: string;
	onChange?: (data: CheckedRoleActions) => void;
	roles: Role[];
	values?: RoleSelectedActions;
}

export interface DefaultPermissionModalContentProps {
	actions: ActionsMap;
	classExternalReferenceCode: string;
	className: string;
	closeModal: () => void;
	roles: Role[];
}

export interface Role {
	actions?: string[];
	key: string;
	name: string;
	type: string;
}

export interface RoleSelectedActions {
	[key: string]: string[];
}

export interface SpacesBulkPermissionModalContentProps {
	actions: Action[];
	className: string;
	closeModal: () => void;
	roles: Role[];
	selectedData: any;
}
