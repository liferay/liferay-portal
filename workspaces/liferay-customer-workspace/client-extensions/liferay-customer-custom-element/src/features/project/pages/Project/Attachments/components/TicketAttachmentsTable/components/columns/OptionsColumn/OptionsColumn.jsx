/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ButtonWithIcon} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import i18n from '~/utils/I18n';
import {ButtonDropDown} from '~/components';

const OptionsColumn = ({
	hasDeletePermissions,
	onDownload,
	onOpenChange,
	setSelectedTicketAttachment,
	ticketAttachment,
}) => {
	const userOptions = [
		{
			customOptionStyle: '',
			icon: <ClayIcon symbol="download" />,
			label: i18n.translate('download'),
			onClick: () => {
				onDownload(ticketAttachment?.downloadUrl);
			},
		},
		{
			customOptionStyle: '',
			disabled: !hasDeletePermissions,
			icon: <ClayIcon symbol="trash" />,
			label: i18n.translate('delete'),
			onClick: () => {
				onOpenChange(true);
				setSelectedTicketAttachment(ticketAttachment);
			},
		},
	];

	return (
		<ClayTooltipProvider>
			<span>
				<ButtonDropDown
					customDropDownButton={
						<ButtonWithIcon
							aria-label={i18n.translate('manage-user-options')}
							className="text-dark"
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
