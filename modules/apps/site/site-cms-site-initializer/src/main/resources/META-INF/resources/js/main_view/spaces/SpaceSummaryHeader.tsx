/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import React from 'react';

import manageConnectedSitesAction, {
	ManageConnectedSitesData,
} from '../props_transformer/actions/manageConnectedSitesAction';
import manageMembersAction, {
	ManageMembersData,
} from '../props_transformer/actions/manageMembersAction';

export enum SpaceSummaryHeaderActions {
	OPEN_MEMBERS_MODAL = 'open-members-modal',
	OPEN_SITES_MODAL = 'open-sites-modal',
}

export type SpaceSummaryHeaderPermissions = {
	hasAssignMembersPermission: boolean;
	hasConnectSitesPermission: boolean;
};

type SpaceModalPropsType = {
	action: SpaceSummaryHeaderActions;
	assetLibraryCreatorUserId: string;
	externalReferenceCode: string;
};

interface SpaceSummaryHeaderProps {
	label: string;
	permissions?: SpaceSummaryHeaderPermissions;
	spaceModalProps?: SpaceModalPropsType;
	title: string;
	url: string;
}

export default function SpaceSummaryHeader({
	label,
	permissions,
	spaceModalProps,
	title,
	url,
}: SpaceSummaryHeaderProps) {
	const loadData = () => window.location.reload();

	const openMembersModal = (props: SpaceModalPropsType) => {
		const {assetLibraryCreatorUserId, externalReferenceCode} = props;

		const data: ManageMembersData = {
			assetLibraryCreatorUserId,
			externalReferenceCode,
			hasAssignMembersPermission: Boolean(
				permissions?.hasAssignMembersPermission
			),
			title,
		};

		manageMembersAction(data, loadData);
	};

	const getActionCallback = () => {
		if (
			spaceModalProps?.action ===
			SpaceSummaryHeaderActions.OPEN_MEMBERS_MODAL
		) {
			return openMembersModal(spaceModalProps);
		}
		else if (
			spaceModalProps?.action ===
			SpaceSummaryHeaderActions.OPEN_SITES_MODAL
		) {
			const data: ManageConnectedSitesData = {
				externalReferenceCode: spaceModalProps.externalReferenceCode,
				hasConnectSitesPermission: Boolean(
					permissions?.hasConnectSitesPermission
				),
			};

			return manageConnectedSitesAction(data, loadData);
		}
	};

	return (
		<div className="align-items-center d-flex justify-content-between">
			<h2 className="font-weight-semi-bold m-0 text-4">{title}</h2>

			{url ? (
				<ClayLink className="text-3 text-weight-semi-bold" href={url}>
					{label}
				</ClayLink>
			) : (
				<ClayButton
					className="text-3 text-weight-semi-bold"
					displayType="link"
					onClick={getActionCallback}
					size="sm"
				>
					{label}
				</ClayButton>
			)}
		</div>
	);
}
