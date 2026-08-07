/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {ClayDropDownWithItems} from '@clayui/drop-down';
import ClaySticker from '@clayui/sticker';
import {FeatureIndicator, openToast} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import React, {ComponentProps} from 'react';

import {manageMembersAction} from '../../index';
import DefaultPermissionModalContent from '../../main_view/default_permission/DefaultPermissionModalContent';
import {DefaultPermissionModalContentProps} from '../../main_view/default_permission/DefaultPermissionTypes';
import importStructuresAction from '../../main_view/props_transformer/actions/importStructuresAction';
import manageConnectedSitesAction, {
	ManageConnectedSitesData,
} from '../../main_view/props_transformer/actions/manageConnectedSitesAction';
import {ManageMembersData} from '../../main_view/props_transformer/actions/manageMembersAction';
import ApiHelper from '../services/ApiHelper';
import {LogoColor} from '../types/Space';
import {openCMSModal} from '../utils/openCMSModal';
import {displayErrorToast} from '../utils/toastUtil';
import SpaceSticker from './SpaceSticker';

export interface ActionDropdownItemProps {
	confirmationMessage?: string;
	confirmationTitle?: string;
	defaultPermissionAdditionalProps?: DefaultPermissionModalContentProps;
	href?: string;
	manageConnectedSitesData?: ManageConnectedSitesData;
	manageMembersData?: ManageMembersData;
	redirect?: string;
	size?: 'full-screen' | 'lg' | 'md' | 'sm';
	successMessage?: string;
	target?:
		| 'asyncDelete'
		| 'asyncPost'
		| 'asyncPut'
		| 'defaultPermissionsModal'
		| 'importStructureModal'
		| 'link'
		| 'manageConnectedSitesModal'
		| 'manageMembersModal'
		| 'modal';
}

interface Props extends Pick<React.ComponentProps<typeof ClaySticker>, 'size'> {
	actionItems?: ComponentProps<typeof ClayDropDownWithItems>['items'] &
		ActionDropdownItemProps;
	breadcrumbItems: BreadcrumbItem[];
	displayType?: LogoColor;
	freeTier?: boolean;
	hideSpace?: boolean;
}

export interface BreadcrumbItem {
	active?: boolean;
	href?: string;
	label: string;
	onClick?: () => void;
}

function ActionDropdownItem({
	confirmationMessage,
	confirmationTitle,
	defaultPermissionAdditionalProps,
	href = '',
	label,
	manageConnectedSitesData,
	manageMembersData,
	redirect,
	size = 'full-screen',
	successMessage,
	target = 'link',
	title,
	...props
}: {label: string; title?: string} & ActionDropdownItemProps) {
	const handleTargetAction = async () => {
		function handleAsyncTargetAction(error: string | null) {
			if (!error) {
				openToast({
					message:
						successMessage ||
						Liferay.Language.get(
							'your-request-completed-successfully'
						),
					type: 'success',
				});

				if (redirect) {
					navigate(redirect);
				}
			}
			else {
				displayErrorToast(error);
			}
		}

		const loadData = () => {
			if (redirect) {
				navigate(redirect);
			}
		};

		if (target === 'asyncDelete') {
			const {error} = await ApiHelper.delete(href);

			handleAsyncTargetAction(error);
		}
		else if (target === 'asyncPost') {
			const {error} = await ApiHelper.post(href);

			handleAsyncTargetAction(error);
		}
		else if (target === 'asyncPut') {
			const {error} = await ApiHelper.put(href);

			handleAsyncTargetAction(error);
		}
		else if (
			target === 'defaultPermissionsModal' &&
			defaultPermissionAdditionalProps
		) {
			openCMSModal({
				contentComponent: ({closeModal}: {closeModal: () => void}) =>
					DefaultPermissionModalContent({
						...defaultPermissionAdditionalProps,
						closeModal,
					}),
				size: 'full-screen',
			});
		}
		else if (target === 'importStructureModal') {
			importStructuresAction(href, () => {
				navigate(redirect || window.location.href);
			});
		}
		else if (
			target === 'manageConnectedSitesModal' &&
			manageConnectedSitesData
		) {
			manageConnectedSitesAction(manageConnectedSitesData, loadData);
		}
		else if (target === 'manageMembersModal' && manageMembersData) {
			manageMembersAction(manageMembersData, loadData);
		}
		else if (target === 'modal') {
			openCMSModal({
				onClose: () => {
					if (redirect) {
						navigate(redirect);
					}
				},
				size,
				title: title || label,
				url: href,
			});
		}
		else {
			navigate(href);
		}
	};

	const handleClick = () => {
		if (confirmationMessage) {
			openCMSModal({
				bodyHTML: confirmationMessage,
				buttons: [
					{
						autoFocus: true,
						displayType: 'secondary',
						label: Liferay.Language.get('cancel'),
						type: 'cancel',
					},
					{
						displayType: 'danger',
						label: Liferay.Language.get('delete'),
						onClick: ({
							processClose,
						}: {
							processClose: () => void;
						}) => {
							processClose();
							handleTargetAction();
						},
					},
				],
				center: true,
				role: 'alertdialog',
				status: 'danger',
				title: confirmationTitle || Liferay.Language.get('delete'),
			});
		}
		else {
			handleTargetAction();
		}
	};

	return (
		<ClayDropDown.Item onClick={handleClick} {...props}>
			{label}
		</ClayDropDown.Item>
	);
}

export default function Breadcrumb({
	actionItems,
	breadcrumbItems,
	displayType,
	freeTier,
	hideSpace,
	size,
}: Props) {
	const isTitle = breadcrumbItems.length === 1;

	return (
		<section
			aria-label={Liferay.Language.get('breadcrumb')}
			className="autofit-row autofit-row-center cms-breadcrumb px-4"
		>
			{!hideSpace && (
				<div className="autofit-col mr-3">
					<SpaceSticker
						displayType={displayType}
						hideName
						name={breadcrumbItems[0]?.label}
						size={size}
					/>
				</div>
			)}

			<div className="autofit-col">
				{isTitle ? (
					<div className="c-gap-2 d-flex">
						<h2 className="font-weight-semi-bold mb-0 text-7 text-dark">
							{breadcrumbItems[0]?.label}
						</h2>

						{freeTier && (
							<FeatureIndicator interactive type="enterprise" />
						)}
					</div>
				) : (
					<ClayBreadcrumb className="p-0" items={breadcrumbItems} />
				)}
			</div>

			{actionItems && (
				<div className="autofit-col ml-1">
					<ClayDropDown
						hasLeftSymbols={actionItems.some(
							({symbolLeft}) => !!symbolLeft
						)}
						hasRightSymbols={actionItems.some(
							({symbolRight}) => !!symbolRight
						)}
						trigger={
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get(
									'more-actions'
								)}
								className="component-action"
								displayType="unstyled"
								size="sm"
								symbol="ellipsis-v"
							/>
						}
					>
						<ClayDropDown.ItemList>
							{actionItems.map((item: any, i) => (
								<ActionDropdownItem key={i} {...item} />
							))}
						</ClayDropDown.ItemList>
					</ClayDropDown>
				</div>
			)}
		</section>
	);
}
