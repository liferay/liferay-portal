/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker, Text} from '@clayui/core';
import Form from '@clayui/form';
import {useId} from 'frontend-js-components-web';
import React from 'react';

import PreviewOption from '../../types/PreviewOption';

interface Props {
	namespace: string;
	onSelectPreviewOption: (key: React.Key) => void;
	previewOptions: PreviewOption[];
	selectedPreviewOption: PreviewOption;
}

const TriggerLabel = React.forwardRef(
	(
		{
			selectedItem,
			...otherProps
		}: {selectedItem: {label: string; value: string}},
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
					{selectedItem.label}
				</Text>
			</button>
		);
	}
);

function PreviewSelector({
	namespace,
	onSelectPreviewOption,
	previewOptions,
	selectedPreviewOption,
}: Props) {
	const previewById = useId();
	const previewByLabelId = useId();

	return (
		<Form.Group>
			<label htmlFor={previewById} id={previewByLabelId}>
				{Liferay.Language.get('preview-by')}
			</label>

			<input
				id={`${namespace}segmentsOrExperiences`}
				name={`${namespace}segmentsOrExperiences`}
				type="hidden"
				value={selectedPreviewOption.value}
			/>

			<Picker
				UNSAFE_menuClassName="cadmin"
				aria-labelledby={previewByLabelId}
				as={TriggerLabel}
				id={previewById}
				items={previewOptions}
				messages={{
					itemDescribedby: Liferay.Language.get(
						'you-are-currently-on-a-text-element,-inside-of-a-list-box'
					),
					itemSelected: Liferay.Language.get('x-selected'),
					scrollToBottomAriaLabel:
						Liferay.Language.get('scroll-to-bottom'),
					scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
				}}
				onSelectionChange={onSelectPreviewOption}
				selectedItem={selectedPreviewOption}
				selectedKey={selectedPreviewOption.value}
			>
				{({label, value}) => (
					<Option key={value} textValue={label}>
						<Text aria-hidden color="secondary" size={3}>
							{label}
						</Text>
					</Option>
				)}
			</Picker>
		</Form.Group>
	);
}

export default PreviewSelector;
