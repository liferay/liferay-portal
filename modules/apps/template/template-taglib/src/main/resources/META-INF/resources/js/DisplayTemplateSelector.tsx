/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import DropDown from '@clayui/drop-down';
import Form, {ClayInput} from '@clayui/form';
import {InternalDispatch} from '@clayui/shared';
import React, {useState} from 'react';

type Props = {
	namespace: string;
	props: {
		displayStyle: string;
		displayStyleGroupId: string;
		displayStyleGroupKey: string;
		items: [
			{
				items: Array<{
					groupId: number;
					groupKey: string;
					label: string;
					value: string;
				}>;
				label: string;
			},
		];
	};
};

const SEPARATOR = '__';

export function getOptionData(option: string) {
	const [groupId, groupKey, ...value] = option.split(SEPARATOR);

	return {groupId, groupKey, value: value.join('-')};
}

export default function DisplayTemplateSelector({namespace, props}: Props) {
	const {displayStyle, displayStyleGroupId, displayStyleGroupKey, items} =
		props;

	const [selectedDisplayStyle, setSelectedDisplayStyle] = useState({
		groupId: displayStyleGroupId,
		groupKey: displayStyleGroupKey,
		name: displayStyle,
	});

	const onSelectionChangeHandlder: InternalDispatch<React.Key> = (
		option: React.Key
	) => {
		if (typeof option !== 'string') {
			return;
		}

		const {groupId, groupKey, value} = getOptionData(option);

		setSelectedDisplayStyle({
			groupId,
			groupKey,
			name: value,
		});

		Liferay.fire('templateSelector:changedTemplate', {
			value,
		});
	};

	return (
		<>
			<ClayInput
				id={`${namespace}preferences--displayStyle--`}
				name={`${namespace}preferences--displayStyle--`}
				type="hidden"
				value={selectedDisplayStyle.name}
			/>

			<ClayInput
				id={`${namespace}displayStyleGroupId`}
				name={`${namespace}preferences--displayStyleGroupId--`}
				type="hidden"
				value={selectedDisplayStyle.groupId}
			/>

			<ClayInput
				id={`${namespace}displayStyleGroupKey`}
				name={`${namespace}preferences--displayStyleGroupKey--`}
				type="hidden"
				value={selectedDisplayStyle.groupKey}
			/>

			<Form.Group>
				<label htmlFor={`${namespace}displayStyle`}>
					{Liferay.Language.get('display-template')}
				</label>

				<Picker
					UNSAFE_menuClassName="cadmin"
					className="display-template-selector"
					id={`${namespace}displayStyle`}
					items={items}
					messages={{
						itemDescribedby: Liferay.Language.get(
							'you-are-currently-on-a-text-element,-inside-of-a-list-box'
						),
						itemSelected: Liferay.Language.get('x-selected'),
						scrollToBottomAriaLabel:
							Liferay.Language.get('scroll-to-bottom'),
						scrollToTopAriaLabel:
							Liferay.Language.get('scroll-to-top'),
					}}
					onSelectionChange={onSelectionChangeHandlder}
					selectedKey={`${selectedDisplayStyle.groupId}${SEPARATOR}${selectedDisplayStyle.groupKey}${SEPARATOR}${selectedDisplayStyle.name}`}
				>
					{(group) => (
						<DropDown.Group
							header={group.label}
							items={group.items}
						>
							{(item) => (
								<Option
									key={`${
										item.groupId
											? item.groupId
											: displayStyleGroupId
									}${SEPARATOR}${
										item.groupKey
											? item.groupKey
											: displayStyleGroupKey
									}${SEPARATOR}${item.value}`}
								>
									{item.label}
								</Option>
							)}
						</DropDown.Group>
					)}
				</Picker>
			</Form.Group>
		</>
	);
}
