/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayPopover from '@clayui/popover';
import React, {useState} from 'react';

const getToTooltipText = (value: string) => {
	const toArray = value.split(',');
	const lastItem = toArray.length > 1 ? toArray.pop() : null;

	return toArray.length > 1
		? `${toArray.join(',')} ${Liferay.Language.get(
				'and'
			).toLocaleLowerCase()} ${lastItem}.`
		: value;
};

export function NotificationQueueEntryToDataRenderer({value}: {value: string}) {
	const [isPopoverVisible, setPopoverVisible] = useState(false);

	return (
		<ClayPopover
			data-testid="clayPopover"
			disableScroll
			header={Liferay.Language.get('to[recipient]')}
			onShowChange={setPopoverVisible}
			show={isPopoverVisible}
			style={{width: 256}}
			trigger={
				<span
					className="ddm-tooltip"
					onMouseOut={() => setPopoverVisible(false)}
					onMouseOver={() => setPopoverVisible(true)}
				>
					{value.length > 25
						? value.slice(0, 25).trim() + '...'
						: value}
				</span>
			}
		>
			{getToTooltipText(value)}
		</ClayPopover>
	);
}
