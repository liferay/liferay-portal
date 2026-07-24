/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import React, {useCallback, useEffect, useState} from 'react';

import ApiHelper from '../../common/services/ApiHelper';
import {DISPLAY_UPDATED} from '../../common/utils/events';
import ACTIONS from '../props_transformer/actions/creationMenuActions';
import manageConnectedSitesAction, {
	ManageConnectedSitesData,
} from '../props_transformer/actions/manageConnectedSitesAction';
import manageMembersAction, {
	ManageMembersData,
} from '../props_transformer/actions/manageMembersAction';
import addOnClickToCreationMenuItems from '../props_transformer/utils/addOnClickToCreationMenuItems';

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
	filter?: string;
};

interface SpaceSummaryHeaderProps {
	apiURL: string;
	creationMenu?: any;
	label: string;
	onOpenMembersModal?: (
		data: ManageMembersData,
		loadData?: () => void
	) => void;
	permissions?: SpaceSummaryHeaderPermissions;
	spaceModalProps?: SpaceModalPropsType;
	title: string;
	url: string;
}

export default function SpaceSummaryHeader({
	apiURL,
	creationMenu,
	label,
	onOpenMembersModal,
	permissions,
	spaceModalProps,
	title,
	url,
}: SpaceSummaryHeaderProps) {
	const [active, setActive] = useState(false);
	const [showViewAll, setShowViewAll] = useState(false);

	const loadData = () => window.location.reload();

	const fetchItems = useCallback(() => {
		if (!apiURL) {
			setShowViewAll(false);

			return;
		}

		ApiHelper.get<{
			items: any;
			lastPage: number;
			page: number;
			totalCount: number;
		}>(apiURL)
			.then((response) => {
				setShowViewAll(Boolean(response.data?.totalCount));
			})
			.catch(() => setShowViewAll(false));
	}, [apiURL]);

	useEffect(() => {
		Liferay.on(DISPLAY_UPDATED, fetchItems);

		fetchItems();

		return () => {
			Liferay.detach(DISPLAY_UPDATED, fetchItems);
		};
	}, [fetchItems]);

	const CreationMenu = () => {
		if (!creationMenu?.primaryItems?.length) {
			return null;
		}

		return (
			<ClayDropDown
				active={active}
				className="ml-3"
				onActiveChange={setActive}
				trigger={
					<ClayButtonWithIcon
						aria-label={`Add ${title}`}
						data-canonical-name={`Add ${title}`}
						displayType="secondary"
						small
						symbol="plus"
						title={`Add ${title}`}
					/>
				}
			>
				<ClayDropDown.ItemList>
					{addOnClickToCreationMenuItems(
						creationMenu.primaryItems,
						ACTIONS
					).map((item: any, index: number) => (
						<ClayDropDown.Item
							key={index}
							onClick={() => {
								setActive(false);
								item.onClick({loadData});
							}}
						>
							{item.icon && (
								<span className="pr-2">
									<ClayIcon symbol={item.icon} />
								</span>
							)}

							{item.label}
						</ClayDropDown.Item>
					))}
				</ClayDropDown.ItemList>
			</ClayDropDown>
		);
	};

	const openMembersModal = (props: SpaceModalPropsType) => {
		const {assetLibraryCreatorUserId, externalReferenceCode, filter} =
			props;

		const data: ManageMembersData = {
			assetLibraryCreatorUserId,
			externalReferenceCode,
			filter,
			hasAssignMembersPermission: Boolean(
				permissions?.hasAssignMembersPermission
			),
			title,
		};

		if (onOpenMembersModal) {
			onOpenMembersModal(data, loadData);

			return;
		}

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

			<div className="align-items-center d-flex">
				{showViewAll &&
					(url ? (
						<ClayLink
							className="text-3 text-weight-semi-bold"
							data-canonical-name={label}
							displayType="unstyled"
							href={url}
						>
							{label}
						</ClayLink>
					) : (
						<ClayButton
							className="text-3 text-weight-semi-bold"
							data-canonical-name={label}
							displayType="link"
							onClick={getActionCallback}
							size="sm"
						>
							{label}
						</ClayButton>
					))}

				<CreationMenu />
			</div>
		</div>
	);
}
