/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClayForm, {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import {useSelector, useStateDispatch} from '../../contexts/StateContext';
import selectPublishedFields from '../../selectors/selectPublishedFields';
import {Field, UploadField} from '../../utils/field';
import Input from '../Input';

const FILE_SOURCE_OPTIONS = [
	{
		label: Liferay.Language.get('upload-directly-from-users-computer'),
		value: 'userComputer',
	},
	{
		label: Liferay.Language.get(
			'upload-or-select-from-documents-and-media-item-selector'
		),
		value: 'documentsAndMedia',
	},
];

export default function getUploadFieldComponents(): {
	FirstSectionComponent?: React.FC<{disabled?: boolean; field: Field}>;
	SecondSectionComponent?: React.FC<{disabled?: boolean; field: Field}>;
} {
	return {
		FirstSectionComponent,
	};
}

function FirstSectionComponent({
	disabled,
	field,
}: {
	disabled?: boolean;
	field: Field;
}) {
	const uploadField = field as UploadField;

	const dispatch = useStateDispatch();
	const publishedFields = useSelector(selectPublishedFields);

	const isPublished = publishedFields.has(field.uuid);

	const id = useId();

	return (
		<>
			<ClayForm.Group className="mb-3">
				<label htmlFor={id}>
					{Liferay.Language.get('request-files')}

					<ClayIcon
						className="ml-1 reference-mark"
						focusable="false"
						role="presentation"
						symbol="asterisk"
					/>
				</label>

				<Picker
					aria-label={Liferay.Language.get('file-source')}
					disabled={disabled || isPublished}
					id={id}
					items={FILE_SOURCE_OPTIONS}
					onSelectionChange={(fileSource: React.Key) => {
						dispatch({
							settings: {
								acceptedFileExtensions:
									uploadField.settings.acceptedFileExtensions,
								fileSource,
								maximumFileSize:
									uploadField.settings.maximumFileSize,
							},
							type: 'update-field',
							uuid: field.uuid,
						});
					}}
					selectedKey={uploadField.settings.fileSource}
				>
					{(item) => <Option key={item.value}>{item.label}</Option>}
				</Picker>
			</ClayForm.Group>

			{uploadField.settings.fileSource === 'userComputer' ? (
				<ClayForm.Group className="mb-3">
					<ClayCheckbox
						checked={
							uploadField.settings.showFilesInDocumentsAndMedia ||
							false
						}
						disabled={disabled || isPublished}
						label={Liferay.Language.get(
							'show-files-in-documents-and-media'
						)}
						onChange={(event) => {
							dispatch({
								settings: {
									...uploadField.settings,
									showFilesInDocumentsAndMedia:
										event.target.checked,
									storageDLFolderPath:
										'/' + Liferay.Language.get('new'),
								},
								type: 'update-field',
								uuid: field.uuid,
							});
						}}
					/>
				</ClayForm.Group>
			) : null}

			{uploadField.settings.showFilesInDocumentsAndMedia ? (
				<Input
					disabled={disabled || isPublished}
					helpMessage={sub(
						Liferay.Language.get(
							'input-the-path-of-the-chosen-folder-in-documents-and-media-an-example-of-a-valid-path-is-x'
						),
						'/myDocumentsAndMediaFolder'
					)}
					label={Liferay.Language.get('storage-folder')}
					onValueChange={(value) => {
						dispatch({
							settings: {
								...uploadField.settings,
								storageDLFolderPath: value,
							},
							type: 'update-field',
							uuid: field.uuid,
						});
					}}
					value={uploadField.settings.storageDLFolderPath!}
				/>
			) : null}

			<Input
				disabled={disabled}
				label={Liferay.Language.get('accepted-file-extensions')}
				onValueChange={(value) => {
					dispatch({
						settings: {
							...uploadField.settings,
							acceptedFileExtensions: value,
						},
						type: 'update-field',
						uuid: field.uuid,
					});
				}}
				value={uploadField.settings.acceptedFileExtensions}
			/>

			<Input
				disabled={disabled}
				helpMessage={Liferay.Language.get(
					'set-the-maximum-file-size-in-megabytes'
				)}
				label={Liferay.Language.get('maximum-file-size')}
				onValueChange={(value) => {
					dispatch({
						settings: {
							...uploadField.settings,
							maximumFileSize: parseInt(value, 10),
						},
						type: 'update-field',
						uuid: field.uuid,
					});
				}}
				value={String(uploadField.settings.maximumFileSize)}
			/>
		</>
	);
}
