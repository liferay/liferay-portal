/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import {Input} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React from 'react';

import {normalizeFieldSettings} from '../../../../utils/fieldSettings';
import {ObjectFieldErrors} from '../../ObjectFieldFormBase';
import SpacePicker from './SpacePicker';

interface IAttachmentPropertiesProps {
	errors: ObjectFieldErrors;
	modelBuilder?: boolean;
	objectFieldSettings: ObjectFieldSetting[];
	onSettingsChange: (setting: ObjectFieldSetting) => void;
	onSubmit?: (values?: Partial<ObjectField>) => void;
	values: Partial<ObjectField>;
}

export function AttachmentProperties({
	errors,
	modelBuilder,
	objectFieldSettings,
	onSettingsChange,
	onSubmit,
	values,
}: IAttachmentPropertiesProps) {
	const settings = normalizeFieldSettings(objectFieldSettings);

	const usesCMSBasicDocumentStorage =
		settings.showFilesInLibrary &&
		settings.fileSource === 'userComputerToCMSBasicDocument';

	const usesDocumentsAndMediaStorage =
		settings.showFilesInLibrary &&
		settings.fileSource === 'userComputerToDocumentsAndMedia';

	const handleSpaceChange = (value: string) => {
		const setting: ObjectFieldSetting = {
			name: 'storageDepotGroup' as ObjectFieldSettingName,
			value,
		};

		const updatedSettings = [
			...objectFieldSettings.filter(
				(setting) => setting.name !== 'storageDepotGroup'
			),
			setting,
		];

		onSettingsChange(setting);

		if (onSubmit) {
			onSubmit({
				...values,
				objectFieldSettings: updatedSettings,
			});
		}
	};

	return (
		<>
			<ClayForm.Group>
				{usesDocumentsAndMediaStorage ? (
					<Input
						error={errors.storageDLFolderPath}
						feedbackMessage={sub(
							Liferay.Language.get(
								'input-the-path-of-the-chosen-folder-in-documents-and-media-an-example-of-a-valid-path-is-x'
							),
							'/myDocumentsAndMediaFolder'
						)}
						id="storageDLFolderPath"
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
				) : (
					usesCMSBasicDocumentStorage && (
						<div
							className={classNames({
								row: !modelBuilder,
							})}
						>
							<div
								className={classNames({
									'col-lg-2': !modelBuilder,
								})}
							>
								<SpacePicker
									error={errors.storageDepotGroup}
									onChange={handleSpaceChange}
									value={settings.storageDepotGroup as string}
								/>
							</div>

							<div
								className={classNames({
									'col-lg-10': !modelBuilder,
								})}
							>
								<Input
									error={errors.storageDLFolderPath}
									feedbackMessage={sub(
										Liferay.Language.get(
											'input-the-path-of-the-chosen-folder-in-cms-files-an-example-of-a-valid-path-is-x'
										),
										'/myCMSFolder'
									)}
									id="storageDepotFolderPathInput"
									label={Liferay.Language.get(
										'cms-storage-folder'
									)}
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
									value={
										settings.storageDLFolderPath as string
									}
								/>
							</div>
						</div>
					)
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
