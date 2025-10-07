/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {ClayDropDownWithItems} from '@clayui/drop-down';
import ClaySticker from '@clayui/sticker';
import {openConfirmModal, openModal} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import React, {ComponentProps} from 'react';

import ApiHelper from '../services/ApiHelper';
import {
	displayErrorToast,
	displayRequestSuccessToast,
} from '../utils/toastUtil';
import SpaceSticker from './SpaceSticker';

interface ActionDropdownItemProps {
	confirmationMessage?: string;
	href?: string;
	redirect?: string;
	size?: 'full-screen' | 'lg' | 'md' | 'sm';
	target?: 'asyncDelete' | 'link' | 'modal';
}

interface Props
	extends Pick<
		React.ComponentProps<typeof ClaySticker>,
		'displayType' | 'size'
	> {
	actionItems?: ComponentProps<typeof ClayDropDownWithItems>['items'] &
		ActionDropdownItemProps;
	breadcrumbItems: BreadcrumbItem[];
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
	href = '',
	label,
	redirect,
	size = 'full-screen',
	target = 'link',
	...props
}: {label: string} & ActionDropdownItemProps) {
	const handleTargetAction = async () => {
		if (target === 'modal') {
			openModal({
				size,
				title: label,
				url: href,
			});
		}
		else if (target === 'asyncDelete') {
			const {error} = await ApiHelper.delete(href);

			if (!error) {
				displayRequestSuccessToast();

				if (redirect) {
					navigate(redirect);
				}
			}
			else {
				displayErrorToast(error);
			}
		}
		else {
			navigate(href);
		}
	};

	const handleClick = () => {
		if (confirmationMessage) {
			openConfirmModal({
				message: confirmationMessage,
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						handleTargetAction();
					}
				},
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
	hideSpace,
	size,
}: Props) {
	const isTitle = breadcrumbItems.length === 1;

	return (
		<div
			aria-label={Liferay.Language.get('breadcrumb')}
			className="autofit-row autofit-row-center px-4"
			style={{height: '72px'}}
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

			<div className="autofit-col cms-breadcrumb">
				{isTitle ? (
					<h2 className="font-weight-semi-bold mb-0 text-7 text-dark">
						{breadcrumbItems[0]?.label}
					</h2>
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
		</div>
	);
}
