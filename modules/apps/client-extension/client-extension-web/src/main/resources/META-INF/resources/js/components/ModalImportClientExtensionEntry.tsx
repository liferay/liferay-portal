/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {fetch} from 'frontend-js-web';
import React, {FormEvent, useEffect, useRef, useState} from 'react';

interface ModalImportClientExtensionEntryProps {
	importClientExtensionEntryURL: string;
	portletNamespace: string;
	successURL: string;
}

type TFile = {
	fileName?: string;
	inputFile?: File | null;
	inputFileValue?: string;
};

export default function ModalImportClientExtensionEntry({
	importClientExtensionEntryURL,
	portletNamespace,
	successURL,
}: ModalImportClientExtensionEntryProps) {
	const [error, setError] = useState<string>('');
	const [visible, setVisible] = useState(false);
	const inputFileRef = useRef() as React.MutableRefObject<HTMLInputElement>;
	const importClientExtensionEntryModalComponentId = `${portletNamespace}importClientExtensionEntryModal`;
	const importClientExtensionEntryFormId = `${portletNamespace}importClientExtensionEntryForm`;
	const importClientExtensionEntryJSONInputId = `${portletNamespace}importClientExtensionEntryJSONInput`;
	const [{fileName, inputFile, inputFileValue}, setFile] = useState<TFile>(
		{}
	);

	const {observer, onClose} = useModal({
		onClose: () => {
			setError('');
			setFile({
				fileName: '',
				inputFile: null,
				inputFileValue: '',
			});
			setVisible(false);
		},
	});

	const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		const formData = new FormData(event.currentTarget);

		try {
			const response = await fetch(importClientExtensionEntryURL, {
				body: formData,
				method: 'POST',
			});

			if (response.status !== 200) {
				setError(
					response.statusText || `HTTP error: ${response.status}`
				);
			}
			else {
				const json = await response.json();

				if (json.error) {
					setError(json.error);
				}
				else {
					window.location.href = successURL;
				}
			}
		}
		catch (error) {
			setError(Liferay.Language.get('an-error-occurred'));

			console.error(error);
		}
	};

	useEffect(() => {
		Liferay.component(
			importClientExtensionEntryModalComponentId,
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
			Liferay.destroyComponent(
				importClientExtensionEntryModalComponentId
			);
	}, [importClientExtensionEntryModalComponentId, setVisible]);

	return visible ? (
		<ClayModal center observer={observer}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('import-client-extension')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm

					// @ts-ignore

					id={importClientExtensionEntryFormId}
					onSubmit={handleSubmit}
				>
					{error && (
						<ClayAlert displayType="danger">{error}</ClayAlert>
					)}

					<ClayForm.Group>
						<label htmlFor={importClientExtensionEntryJSONInputId}>
							{Liferay.Language.get('json-file')}
						</label>

						<ClayInput.Group>
							<ClayInput.GroupItem prepend>
								<ClayInput
									disabled
									id={importClientExtensionEntryJSONInputId}
									type="text"
									value={fileName}
								/>
							</ClayInput.GroupItem>

							<ClayInput.GroupItem append shrink>
								<ClayButton
									displayType="secondary"
									onClick={() => {
										setError('');
										inputFileRef.current.click();
									}}
								>
									{Liferay.Language.get('select')}
								</ClayButton>
							</ClayInput.GroupItem>

							{inputFile && (
								<ClayInput.GroupItem shrink>
									<ClayButton
										displayType="secondary"
										onClick={() => {
											setError('');
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
						name={importClientExtensionEntryJSONInputId}
						onChange={({target}) => {
							const inputFile = target.files?.item(0);

							setFile({
								fileName: inputFile?.name,
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
							form={importClientExtensionEntryFormId}
							type="submit"
						>
							{Liferay.Language.get('import')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	) : null;
}
