/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayToolbar from '@clayui/toolbar';
import {useFormik} from 'formik';
import {FieldBase, openToast} from 'frontend-js-components-web';
import {navigate, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';
import {v4 as uuidv4} from 'uuid';

import {SpaceInput} from '../../common/components/SpaceSelector';
import {FieldText} from '../../common/components/forms';
import {required, validate} from '../../common/components/forms/validations';
import FolderService, {TFolder} from '../../common/services/FolderService';
import SpaceService from '../../common/services/SpaceService';
import {Space} from '../../common/types/Space';
import {getFormattedLabel} from '../../common/utils/getFormattedText';

interface EditFolderProps {
	backURL: string;
	folderId: string;
}

const EditFolder: React.FC<EditFolderProps> = ({backURL, folderId}) => {
	const [folderData, setFolderData] = useState<
		Pick<TFolder, 'description' | 'title'> & {
			space: Pick<Space, 'name' | 'settings'>;
		}
	>({
		description: '',
		space: {
			name: 'Default',
			settings: {
				logoColor: 'outline-0',
			},
		},
		title: '',
	});
	const [isLoading, setIsLoading] = useState<boolean>(true);

	const folderSpaceInputId = `${uuidv4()}folderSpace`;

	useEffect(() => {
		const fetchFolderData = async () => {
			setIsLoading(true);
			try {
				const response = await FolderService.getFolder(folderId);
				const space = await SpaceService.getSpace(
					response.scope?.externalReferenceCode!
				);

				setFolderData({...response, space});
			}
			catch (error: any) {
				throw new Error(
					error.message || Liferay.Language.get('an-error-occurred')
				);
			}
			finally {
				setIsLoading(false);
			}
		};

		fetchFolderData();
	}, [folderId]);

	const {
		errors,
		handleChange,
		handleSubmit,
		isSubmitting,
		setValues,
		values,
	} = useFormik({
		initialValues: {
			folderDescription: folderData.description,
			folderName: folderData.title,
			folderSpace: folderData.space,
		},
		onSubmit: async (formValues) => {
			const newFolderValues: TFolder = {
				description: formValues.folderDescription,
				id: parseInt(folderId, 10),
				title: formValues.folderName,
			};

			const {error} = await FolderService.updateFolder(newFolderValues);

			if (!error) {
				navigate(backURL);

				openToast({
					message: sub(
						Liferay.Language.get('x-was-updated-successfully'),
						getFormattedLabel(formValues.folderName)
					),
					type: 'success',
				});
			}
			else {
				openToast({
					message: error,
					type: 'danger',
				});
			}
		},
		validate: (values) =>
			validate(
				{
					folderName: [required],
				},
				values
			),
	});

	useEffect(() => {
		if (folderData) {
			setValues({
				folderDescription: folderData.description,
				folderName: folderData.title,
				folderSpace: folderData.space,
			});
		}
	}, [folderData, setValues]);

	return (
		<div className="edit-folder">
			<ClayToolbar className="container-fluid" light>
				<ClayToolbar.Nav>
					<ClayToolbar.Item>
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('back')}
							borderless
							displayType="secondary"
							monospaced
							onClick={() => navigate(backURL)}
							size="sm"
							symbol="angle-left"
							title={Liferay.Language.get('back')}
						/>
					</ClayToolbar.Item>

					<ClayToolbar.Item className="text-left" expand>
						<h2 className="font-weight-semi-bold m-0 text-5">
							{isLoading ? (
								<span className="loading-animation"></span>
							) : (
								folderData?.title
							)}
						</h2>
					</ClayToolbar.Item>

					<ClayToolbar.Item>
						<ClayButton
							borderless
							displayType="secondary"
							onClick={() => navigate(backURL)}
							size="sm"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</ClayToolbar.Item>

					<ClayToolbar.Item>
						<ClayButton
							disabled={isSubmitting}
							displayType="primary"
							form="formEditFolder"
							size="sm"
							type="submit"
						>
							{isSubmitting && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation"
									></span>
								</span>
							)}

							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayToolbar.Item>
				</ClayToolbar.Nav>
			</ClayToolbar>

			<div className="container-fluid container-fluid-max-md mt-4">
				<ClayForm id="formEditFolder" onSubmit={handleSubmit}>
					<h3 className="font-weight-semi-bold mb-4 text-6">
						{Liferay.Language.get('basic-info')}
					</h3>

					<FieldText
						errorMessage={errors.folderName}
						label={Liferay.Language.get('name')}
						name="folderName"
						onChange={handleChange}
						required
						value={values.folderName}
					/>

					<FieldBase
						id={folderSpaceInputId}
						label={Liferay.Language.get('space')}
						required
					>
						<SpaceInput
							aria-readonly
							id={folderSpaceInputId}
							readOnly
							spaceLogoColor={
								values.folderSpace.settings?.logoColor
							}
							spaceName={values.folderSpace.name}
							value={values.folderSpace.name}
						/>
					</FieldBase>

					<FieldText
						component="textarea"
						label={Liferay.Language.get('description')}
						name="folderDescription"
						onChange={handleChange}
						value={values.folderDescription}
					/>
				</ClayForm>
			</div>
		</div>
	);
};

export default EditFolder;
