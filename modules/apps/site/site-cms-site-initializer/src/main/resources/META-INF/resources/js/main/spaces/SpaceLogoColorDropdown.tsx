/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/components/SpaceLogoColorDropdown.scss';

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayForm from '@clayui/form';
import Icon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import React, {useId, useMemo, useState} from 'react';

import {LogoColor, logoColors} from '../components/SpaceSticker';

interface SpaceColorDropdownProps {
	className?: string;
	onChange?: (color: LogoColor) => void;
}

export default function SpaceColorDropdown({
	className,
	onChange,
}: SpaceColorDropdownProps) {
	const logoColorToLabelMap = useMemo(
		() => ({
			'outline-0': Liferay.Language.get('gray'),
			'outline-1': Liferay.Language.get('purple'),
			'outline-2': Liferay.Language.get('yellow'),
			'outline-3': Liferay.Language.get('green'),
			'outline-4': Liferay.Language.get('red'),
			'outline-5': Liferay.Language.get('orange'),
			'outline-6': Liferay.Language.get('teal'),
			'outline-7': Liferay.Language.get('blue'),
			'outline-8': Liferay.Language.get('pink'),
			'outline-9': Liferay.Language.get('white'),
		}),
		[]
	);

	type LogoLabelType = keyof typeof logoColorToLabelMap;
	const COLORS: Array<{color: LogoColor; label: string}> = useMemo(
		() =>
			logoColors.map((colorName: LogoLabelType) => {
				return {
					color: colorName,
					label: logoColorToLabelMap[colorName],
				};
			}),
		[logoColorToLabelMap]
	);

	const [active, setActive] = useState(false);
	const [selected, setSelected] = useState(COLORS[0]);
	const dropdownTriggerId = useId();

	return (
		<ClayForm.Group
			className={classNames('space-logo-color-dropdown', className)}
		>
			<label htmlFor={dropdownTriggerId}>
				{Liferay.Language.get('space-color')}
			</label>

			<ClayDropDown
				active={active}
				onActiveChange={setActive}
				trigger={
					<ClayButton displayType="secondary" id={dropdownTriggerId}>
						<div className="align-items-center d-flex font-weight-normal text-dark">
							<ClaySticker
								className="mr-2"
								displayType={selected.color as LogoColor}
								size="sm"
							/>

							{selected.label}
						</div>

						<Icon
							className="m-0"
							fontSize={11}
							symbol="caret-double"
						/>
					</ClayButton>
				}
			>
				<ClayDropDown.ItemList>
					{COLORS.map((item, index) => (
						<ClayDropDown.Item
							key={index}
							onClick={() => {
								setSelected(item);
								setActive(false);
								onChange?.(item.color as LogoColor);
							}}
						>
							<ClaySticker
								className="mr-2"
								displayType={`outline-${index}` as any}
								size="sm"
							/>

							{item.label}
						</ClayDropDown.Item>
					))}
				</ClayDropDown.ItemList>
			</ClayDropDown>
		</ClayForm.Group>
	);
}
