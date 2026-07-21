/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {ClayDropDownWithItems} from '@clayui/drop-down';
import {navigate} from 'frontend-js-web';
import React, {ComponentProps} from 'react';

import {
	confirmDeleteDesignLibrary,
	openConnectedSitesModal,
	openManageMembersModal,
} from './actions/breadcrumbActions';

type ActionTarget = 'connected-sites' | 'delete' | 'manage-members';

export interface ActionDropdownItemProps {
	descriptiveName?: string;
	externalReferenceCode?: string;
	hasAssignMembersPermission?: boolean;
	href?: string;
	label?: string;
	ownerId?: string;
	redirect?: string;
	target?: string;
}
interface DesignLibraryBreadcrumbProps {
	actionItems?: ComponentProps<typeof ClayDropDownWithItems>['items'] &
		ActionDropdownItemProps;
	breadcrumbItems: {active: boolean; href?: string; label: string}[];
}

function ActionDropdownItem({
	descriptiveName = '',
	externalReferenceCode = '',
	hasAssignMembersPermission = false,
	href = '',
	label = '',
	ownerId = '',
	redirect,
	target,
	...props
}: ActionDropdownItemProps) {
	const handleClick = () => {
		const actions: Record<ActionTarget, () => void> = {
			'connected-sites': () => {
				openConnectedSitesModal({externalReferenceCode});
			},

			'delete': () => {
				confirmDeleteDesignLibrary({descriptiveName, href, redirect});
			},

			'manage-members': () => {
				openManageMembersModal({
					externalReferenceCode,
					hasAssignMembersPermission,
					headerTitle: label,
					ownerId,
				});
			},
		};

		const action = target ? actions[target as ActionTarget] : null;

		if (action) {
			action();
		}
		else {
			navigate(href);
		}
	};

	return (
		<ClayDropDown.Item onClick={handleClick} {...props}>
			{label}
		</ClayDropDown.Item>
	);
}

export default function DesignLibraryBreadcrumb({
	actionItems,
	breadcrumbItems,
}: DesignLibraryBreadcrumbProps) {
	return (
		<div className="autofit-row autofit-row-center bg-white design-library-breadcrumb px-4 py-3">
			<ClayBreadcrumb className="px-0 py-1" items={breadcrumbItems} />

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
							{actionItems.map((item, i) => (
								<ActionDropdownItem key={i} {...item} />
							))}
						</ClayDropDown.ItemList>
					</ClayDropDown>
				</div>
			)}
		</div>
	);
}
