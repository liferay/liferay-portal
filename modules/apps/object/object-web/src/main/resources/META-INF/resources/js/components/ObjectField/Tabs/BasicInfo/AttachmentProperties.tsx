/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import {Input} from '@liferay/object-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import {normalizeFieldSettings} from '../../../../utils/fieldSettings';
import {ObjectFieldErrors} from '../../ObjectFieldFormBase';

interface IAttachmentPropertiesProps {
	errors: ObjectFieldErrors;
	objectFieldSettings: ObjectFieldSetting[];
	onSettingsChange: (setting: ObjectFieldSetting) => void;
	onSubmit?: () => void;
}

export function AttachmentProperties({
	errors,
	objectFieldSettings,
	onSettingsChange,
	onSubmit,
}: IAttachmentPropertiesProps) {
	const settings = normalizeFieldSettings(objectFieldSettings);

	return (
		<>
			<ClayForm.Group>
				{settings.showFilesInDocumentsAndMedia && (
					<Input
						error={errors.storageDLFolderPath}
						feedbackMessage={sub(
							Liferay.Language.get(
								'input-the-path-of-the-chosen-folder-in-documents-and-media-an-example-of-a-valid-path-is-x'
							),
							'/myDocumentsAndMediaFolder'
						)}
						label={Liferay.Language.get('storage-folder')}
						maxLength={255}
						onBlur={(event) => {
							event.stopPropagation();

							if (onSubmit) {
								onSubmit();
							}
						}}
						onChange={({target: {value}}) =>
							onSettingsChange({
								name: 'storageDLFolderPath',
								value,
							})
						}
						required
						value={settings.storageDLFolderPath as string}
					/>
				)}
			</ClayForm.Group>
			<Input
				component="textarea"
				error={errors.acceptedFileExtensions}
				feedbackMessage={Liferay.Language.get(
					'enter-the-list-of-file-extensions-users-can-upload-use-commas-to-separate-extensions'
				)}
				label={Liferay.Language.get('accepted-file-extensions')}
				onBlur={(event) => {
					event.stopPropagation();

					if (onSubmit) {
						onSubmit();
					}
				}}
				onChange={({target: {value}}) =>
					onSettingsChange({name: 'acceptedFileExtensions', value})
				}
				required
				value={settings.acceptedFileExtensions as string}
			/>

			<Input
				error={errors.maximumFileSize}
				feedbackMessage={Liferay.Language.get('maximum-file-size-help')}
				id="maximumFileSize"
				label={Liferay.Language.get('maximum-file-size')}
				min={0}
				onBlur={(event) => {
					event.stopPropagation();

					if (onSubmit) {
						onSubmit();
					}
				}}
				onChange={({target: {value}}) =>
					onSettingsChange({
						name: 'maximumFileSize',
						value: value && Number(value),
					})
				}
				required
				type="number"
				value={settings.maximumFileSize as number}
			/>
		</>
	);
}
