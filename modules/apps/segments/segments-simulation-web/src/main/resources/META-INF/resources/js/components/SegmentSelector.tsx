/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker, Text} from '@clayui/core';
import Form from '@clayui/form';
import {useId} from 'frontend-js-components-web';
import React from 'react';

import SegmentEntry from '../../types/SegmentEntry';

interface Props {
	namespace: string;
	onSelectSegmentEntry: (key: React.Key) => void;
	segmentsEntries: SegmentEntry[];
	selectedSegmentEntry: SegmentEntry;
}

const TriggerLabel = React.forwardRef(
	(
		{selectedItem, ...otherProps}: {selectedItem: SegmentEntry},
		ref: React.LegacyRef<HTMLButtonElement>
	) => {
		if (!selectedItem) {
			return null;
		}

		return (
			<button
				{...otherProps}
				className="btn btn-block btn-secondary btn-sm form-control-select"
				ref={ref}
			>
				<Text size={3} weight="normal">
					{selectedItem.name}
				</Text>
			</button>
		);
	}
);
function SegmentSelector({
	namespace,
	onSelectSegmentEntry,
	segmentsEntries,
	selectedSegmentEntry,
}: Props) {
	const selectorId = useId();
	const labelId = useId();

	return (
		<>
			{segmentsEntries.length < 2 ? (
				<p>{Liferay.Language.get('no-segments-have-been-added-yet')}</p>
			) : (
				<Form.Group>
					<label htmlFor={selectorId} id={labelId}>
						{Liferay.Language.get('segment')}
					</label>

					<input
						id={`${namespace}segmentsEntryId`}
						name={`${namespace}segmentsEntryId`}
						type="hidden"
						value={selectedSegmentEntry.id}
					/>

					<Picker
						UNSAFE_menuClassName="cadmin"
						aria-labelledby={labelId}
						as={TriggerLabel}
						id={selectorId}
						items={segmentsEntries}
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
						onSelectionChange={onSelectSegmentEntry}
						selectedItem={selectedSegmentEntry}
						selectedKey={selectedSegmentEntry.id.toString()}
					>
						{({id, name}) => (
							<Option key={id} textValue={name}>
								<Text aria-hidden color="secondary" size={3}>
									{name}
								</Text>
							</Option>
						)}
					</Picker>
				</Form.Group>
			)}
		</>
	);
}

export default SegmentSelector;
