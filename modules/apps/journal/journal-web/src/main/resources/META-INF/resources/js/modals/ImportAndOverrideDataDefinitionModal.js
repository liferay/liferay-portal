/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import PropTypes from 'prop-types';
import React, {useEffect, useRef, useState} from 'react';

const ImportAndOverrideDataDefinitionModal = ({portletNamespace}) => {
	const [visible, setVisible] = useState(false);
	const [importAndOverrideStructureURL, setImportAndOverrideStructureURL] =
		useState('');
	const inputFileRef = useRef();
	const importAndOverrideDataDefinitionModalComponentId = `${portletNamespace}importAndOverrideDataDefinitionModal`;
	const importAndOverrideDataDefinitionFormId = `${portletNamespace}importAndOverrideDataDefinitionForm`;
	const jsonFileInputId = `${portletNamespace}jsonFile`;
	const [{fileName, inputFile, inputFileValue}, setFile] = useState({
		fileName: '',
		inputFile: null,
		inputFileValue: '',
	});
	const {observer, onClose} = useModal({
		onClose: () => {
			setVisible(false);
			setFile({
				fileName: '',
				inputFile: null,
				inputFileValue: '',
			});
		},
	});

	useEffect(() => {
		Liferay.component(
			importAndOverrideDataDefinitionModalComponentId,
			{
				open: (importAndOverrideDDMStructureURL) => {
					setImportAndOverrideStructureURL(
						importAndOverrideDDMStructureURL
					);
					setVisible(true);
				},
			},
			{
				destroyOnNavigate: true,
			}
		);

		return () =>
			Liferay.destroyComponent(
				importAndOverrideDataDefinitionModalComponentId
			);
	}, [importAndOverrideDataDefinitionModalComponentId, setVisible]);

	return visible ? (
		<ClayModal observer={observer} size="md">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('import-structure')}
			</ClayModal.Header>

			<ClayModal.Body className="pt-0 px-0">
				<ClayForm
					action={importAndOverrideStructureURL}
					encType="multipart/form-data"
					id={importAndOverrideDataDefinitionFormId}
					method="POST"
				>
					<ClayAlert
						displayType="warning"
						title={`${Liferay.Language.get('warning')}:`}
						variant="stripe"
					>
						{Liferay.Language.get(
							'there-are-content-references-to-this-structure.-you-may-lose-data-if-a-field-name-is-renamed-or-removed'
						)}
					</ClayAlert>

					<ClayForm.Group className="pb-0 pt-5 px-4">
						<label htmlFor={jsonFileInputId}>
							{Liferay.Language.get('json-file')}
						</label>

						<ClayInput.Group>
							<ClayInput.GroupItem prepend>
								<ClayInput
									disabled
									id={jsonFileInputId}
									type="text"
									value={fileName}
								/>
							</ClayInput.GroupItem>

							<ClayInput.GroupItem append shrink>
								<ClayButton
									displayType="secondary"
									onClick={() => inputFileRef.current.click()}
								>
									{Liferay.Language.get('select')}
								</ClayButton>
							</ClayInput.GroupItem>

							{inputFile && (
								<ClayInput.GroupItem shrink>
									<ClayButton
										displayType="secondary"
										onClick={() => {
											setFile({
												fileName: '',
												inputFile: null,
												inputFileValue: '',
											});
										}}
									>
										{Liferay.Language.get('clear')}
									</ClayButton>
								</ClayInput.GroupItem>
							)}
						</ClayInput.Group>
					</ClayForm.Group>

					<input
						className="d-none"
						name={jsonFileInputId}
						onChange={({target}) => {
							const [inputFile] = target.files;
							setFile({
								fileName: inputFile.name,
								inputFile,
								inputFileValue: target.value,
							});
						}}
						ref={inputFileRef}
						type="file"
						value={inputFileValue}
					/>
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={!inputFile}
							form={importAndOverrideDataDefinitionFormId}
							type="submit"
						>
							{Liferay.Language.get('import')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	) : null;
};

ImportAndOverrideDataDefinitionModal.propTypes = {
	importAndOverrideDDMStructureURL: PropTypes.string,
	portletNamespace: PropTypes.string,
};

export default ImportAndOverrideDataDefinitionModal;
