/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {KeyedMutator} from 'swr';

import {Action} from '../../utils/constants';

type DropDownActionProps<T = any> = {
	action: Action;
	item: T;
	mutate?: KeyedMutator<T>;
	onBeforeClickAction?: () => any;
	setActive: (active: boolean) => void;
};

const DropDownAction: React.FC<DropDownActionProps> = ({
	action: {disabled, hidden, icon, name, onClick},
	item,
	mutate = () => {},
	onBeforeClickAction,
	setActive,
}) => {
	if (name === 'divider') {
		return <ClayDropDown.Divider />;
	}

	return (
		<ClayDropDown.Item
			disabled={
				typeof disabled === 'function' ? disabled(item) : disabled
			}
			hidden={typeof hidden === 'function' ? hidden(item) : hidden}
			onClick={(event) => {
				event.preventDefault();

				setActive(false);

				if (onClick) {
					if (onBeforeClickAction) {
						onBeforeClickAction();
					}

					onClick(item, mutate as KeyedMutator<any>);
				}
			}}
		>
			{icon && <ClayIcon className="mr-2" symbol={icon} />}

			{typeof name === 'function' ? name(item) : name}
		</ClayDropDown.Item>
	);
};

export default DropDownAction;
