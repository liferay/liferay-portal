/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import React, {useState} from 'react';
import {KeyedMutator} from 'swr';

import i18n from '../../i18n';
import {Action} from '../../utils/constants';
import DropDownAction from './DropDownAction';

const {ItemList} = ClayDropDown;

type DropDownProps = {
	actions: Action[];
	item: any;
	mutate?: KeyedMutator<any>;
	position?: any;
	trigger: any;
};

const DropDown: React.FC<DropDownProps> = ({
	actions,
	item,
	mutate,
	position = Align.RightCenter,
	trigger,
}) => {
	const [active, setActive] = useState(false);

	if (!actions.length) {
		return null;
	}

	return (
		<ClayDropDown
			active={active}
			alignmentPosition={position}
			onActiveChange={setActive}
			trigger={
				trigger || (
					<ClayButtonWithIcon
						aria-label={i18n.translate('more-actions')}
						className="page-link"
						displayType="unstyled"
						symbol="ellipsis-v"
					/>
				)
			}
		>
			<ItemList>
				{actions.map((action, index) => (
					<DropDownAction
						action={action}
						item={item}
						key={index}
						mutate={mutate}
						setActive={setActive}
					/>
				))}
			</ItemList>
		</ClayDropDown>
	);
};

export default DropDown;
