/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ButtonWithIcon} from '@clayui/core';
import {ClayTooltipProvider} from '@clayui/tooltip';
import i18n from '~/utils/I18n';
import {ButtonDropDown} from '~/components';
import MenuUserActions from './components/MenuUserActions';

const OptionsColumn = ({
	edit,
	highPriorityContactsNames,
	onCancel,
	onEdit,
	onRemove,
	onSave,
	saveDisabled,
	userAccount,
}) => {
	const userOptions = [
		{
			customOptionStyle: 'pr-5',
			label: i18n.translate('edit'),
			onClick: () => {
				onEdit();
			},
		},
		{
			customOptionStyle: 'pr-5',
			disabled: highPriorityContactsNames.includes(
				userAccount.emailAddress
			),
			label: i18n.translate('remove'),
			onClick: () => onRemove(),
			tooltip: i18n.translate(
				'this-team-member-is-assigned-as-an-incident-contact-and-cannot-be-removed'
			),
		},
	];

	return edit ? (
		<MenuUserActions
			onCancel={() => onCancel()}
			onSave={() => onSave()}
			saveDisabled={saveDisabled}
		/>
	) : (
		<ClayTooltipProvider>
			<span>
				<ButtonDropDown
					customDropDownButton={
						<ButtonWithIcon
							aria-label={i18n.translate('manage-user-options')}
							className="text-secondary"
    						displayType="unstyled"
							small
							spritemap={Liferay.Icons.spritemap}
							symbol="ellipsis-v"
						/>
					}
					items={userOptions}
					menuElementAttrs={{
						className: 'p-0',
					}}
					menuWidth="shrink"
				/>
			</span>
		</ClayTooltipProvider>
	);
};

export default OptionsColumn;
