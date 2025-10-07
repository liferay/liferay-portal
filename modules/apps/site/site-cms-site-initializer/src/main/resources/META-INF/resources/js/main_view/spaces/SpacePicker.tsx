/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClaySticker from '@clayui/sticker';
import {useId} from 'frontend-js-components-web';
import React from 'react';

import {logoColors} from '../../common/components/SpaceSticker';
import {LogoColor} from '../../common/types/Space';

const Trigger = React.forwardRef(
	(
		{
			id,
			label,
			logoColor,
			...otherProps
		}: {
			id: string;
			label: string;
			logoColor: LogoColor;
		},
		ref: React.Ref<HTMLButtonElement>
	) => {
		return (
			<button
				{...otherProps}
				className="d-flex form-control form-control-select"
				id={id}
				ref={ref}
			>
				<ClaySticker
					className="mr-2"
					displayType={logoColor}
					size="sm"
				/>

				{label}
			</button>
		);
	}
);

export default function SpacePicker({
	label,
	logoColor,
	onChangeValue,
}: {
	label?: string;
	logoColor: LogoColor;
	onChangeValue: (value: LogoColor) => void;
}) {
	const id = useId();

	return (
		<div className="form-group">
			{label ? <label htmlFor={id}>{label}</label> : null}

			<Picker
				as={Trigger}
				id={id}
				items={Object.keys(logoColors).map((logoColor) => ({
					label: logoColors[logoColor as LogoColor],
					logoColor,
				}))}
				label={logoColors[logoColor]}
				logoColor={logoColor}
				onSelectionChange={(selectedKey: React.Key) => {
					onChangeValue(selectedKey as LogoColor);
				}}
				selectedKey={logoColor}
			>
				{({label, logoColor}) => (
					<Option key={logoColor} textValue={logoColor}>
						<ClaySticker
							className="mr-2"
							displayType={logoColor as LogoColor}
							size="sm"
						/>

						{label}
					</Option>
				)}
			</Picker>
		</div>
	);
}
