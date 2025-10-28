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

const ImportDataDefinitionModal = ({
	importDataDefinitionURL,
	nameMaxLength,
	portletNamespace,
}) => {
	const [visible, setVisible] = useState(false);
	const inputFileRef = useRef();
	const [name, setName] = useState('');
	const importDataDefinitionModalComponentId = `${portletNamespace}importDataDefinitionModal`;
	const importDataDefinitionFormId = `${portletNamespace}importDataDefinitionForm`;
	const jsonFileInputId = `${portletNamespace}jsonFile`;
	const nameInputId = `${portletNamespace}name`;
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
			setName('');
		},
	});

	useEffect(() => {
		Liferay.component(
			importDataDefinitionModalComponentId,
			{
				open: () => {
					setVisible(true);
				},
			},
			{
				destroyOnNavigate: true,
			}
		);

		return () =>
			Liferay.destroyComponent(importDataDefinitionModalComponentId);
	}, [importDataDefinitionModalComponentId, setVisible]);

	return visible ? (
		<ClayModal observer={observer} size="md">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('import-structure')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm
					action={importDataDefinitionURL}
					encType="multipart/form-data"
					id={importDataDefinitionFormId}
					method="POST"
				>
					<ClayAlert
						displayType="info"
						title={`${Liferay.Language.get('info')}:`}
					>
						{Liferay.Language.get(
							'the-import-process-will-run-in-the-background-and-may-take-a-few-minutes'
						)}
					</ClayAlert>

					<ClayForm.Group>
						<label htmlFor={nameInputId}>
							{Liferay.Language.get('name')}
						</label>

						<ClayInput
							id={nameInputId}
							maxLength={nameMaxLength}
							name={nameInputId}
							onChange={(event) => setName(event.target.value)}
							type="text"
							value={name}
						/>
					</ClayForm.Group>

					<ClayForm.Group>
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
							disabled={!inputFile || !name}
							form={importDataDefinitionFormId}
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

ImportDataDefinitionModal.propTypes = {
	importDataDefinitionURL: PropTypes.string,
	nameMaxLength: PropTypes.string,
	portletNamespace: PropTypes.string,
};

export default ImportDataDefinitionModal;
