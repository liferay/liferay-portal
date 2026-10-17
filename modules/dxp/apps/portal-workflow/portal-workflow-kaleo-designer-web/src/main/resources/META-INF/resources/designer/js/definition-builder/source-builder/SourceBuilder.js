/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayToolbar from '@clayui/toolbar';
import {CodeMirrorEditor} from '@liferay/object-js-components-web';
import React, {useContext, useEffect, useState} from 'react';
import {isEdge, isNode} from 'react-flow-renderer';

import XMLUtil from '../../../js/definition-builder/source-builder/xmlUtil';
import {DefinitionBuilderContext} from '../DefinitionBuilderContext';
import {xmlNamespace} from './constants';
import DeserializeUtil from './deserializeUtil';
import {serializeDefinition} from './serializeUtil';

export default function SourceBuilder() {
	const {
		blockingError,
		currentEditor,
		definitionDescription,
		definitionName,
		elements,
		setBlockingError,
		setCurrentEditor,
		workflowDefinitionVersions,
	} = useContext(DefinitionBuilderContext);
	const [loading, setLoading] = useState(true);
	const [showImportSuccessMessage, setShowImportSuccessMessage] =
		useState(false);

	useEffect(() => {
		function loadXmlContent() {
			if (currentEditor && elements) {
				const metadata = {
					description: definitionDescription,
					name: definitionName,
					version: workflowDefinitionVersions.length,
				};

				const currentData = currentEditor.getValue();
				let currentElements;

				if (currentData) {
					const deserializeUtil = new DeserializeUtil();

					deserializeUtil.updateXMLDefinition(
						encodeURIComponent(currentData)
					);

					currentElements = deserializeUtil.getElements();
				}
				else {
					currentElements = elements;
				}

				const xmlContent = serializeDefinition(
					xmlNamespace,
					metadata,
					currentElements.filter(isNode),
					currentElements.filter(isEdge)
				);

				if (xmlContent) {
					if (currentEditor.getValue() !== xmlContent) {
						currentEditor.setValue(xmlContent);
					}
				}

				setLoading(false);
			}
		}

		if (currentEditor) {
			loadXmlContent();
		}
	}, [
		currentEditor,
		definitionDescription,
		definitionName,
		elements,
		workflowDefinitionVersions.length,
	]);

	useEffect(() => {
		if (blockingError.errorType === 'invalidXML') {
			document.addEventListener('keydown', () => {
				setBlockingError({errorType: ''});
			});

			return () => {
				document.removeEventListener('keydown', () => {
					setBlockingError({errorType: ''});
				});
			};
		}
	}, [blockingError, setBlockingError]);

	const writeDefinitionMessage = Liferay.Language.get(
		'write-your-definition-or-x'
	).substring(0, 25);

	const importFileMessage =
		Liferay.Language.get('import-a-file').toLowerCase();

	function handleInvalidXMLBlockingError() {
		setBlockingError(() => ({
			errorMessage: Liferay.Language.get(
				'please-select-a-valid-xml-file'
			),
			errorType: 'invalidXML',
		}));
	}

	function loadFile(event) {
		setBlockingError({errorType: ''});

		const files = event.target.files;

		if (files[0].type === 'text/xml') {
			const reader = new FileReader();

			reader.onloadend = (event) => {
				if (
					event.target.readyState === FileReader.DONE &&
					XMLUtil.validateDefinition(event.target.result)
				) {
					currentEditor.setValue(event.target.result);

					const fileInput = document.querySelector('#fileInput');

					fileInput.value = '';

					setShowImportSuccessMessage(true);
				}
				else {
					handleInvalidXMLBlockingError();
				}
			};

			reader.readAsText(files[0]);
		}
		else if (files[0].type !== 'text/xml') {
			handleInvalidXMLBlockingError();
		}
	}

	return (
		<>
			<ClayToolbar className="source-toolbar">
				<ClayLayout.ContainerFluid>
					<ClayToolbar.Nav>
						<ClayToolbar.Item>
							<span>{Liferay.Language.get('source')}</span>
						</ClayToolbar.Item>

						<ClayToolbar.Item>
							<div className="import-file">
								<ClayIcon symbol="document-code" />

								<span>{writeDefinitionMessage}</span>

								<label className="pt-1" htmlFor="fileInput">
									<ClayLink className="ml-1">
										{`${importFileMessage}.`}
									</ClayLink>
								</label>

								<input
									id="fileInput"
									onChange={(event) => loadFile(event)}
									type="file"
								/>
							</div>
						</ClayToolbar.Item>
					</ClayToolbar.Nav>
				</ClayLayout.ContainerFluid>
			</ClayToolbar>

			{loading && (
				<ClayLoadingIndicator
					displayType="primary"
					shape="squares"
					size="md"
				/>
			)}

			<CodeMirrorEditor
				lineWrapping={true}
				mode="xml"
				onChange={() => {}}
				readOnly={false}
				ref={setCurrentEditor}
			/>

			{showImportSuccessMessage && (
				<ClayAlert.ToastContainer>
					<ClayAlert
						autoClose={5000}
						closeButtonAriaLabel={Liferay.Language.get('close')}
						displayType="success"
						onClose={() => setShowImportSuccessMessage(false)}
						title={`${Liferay.Language.get('success')}:`}
					>
						{Liferay.Language.get(
							'definition-imported-successfully'
						)}
					</ClayAlert>
				</ClayAlert.ToastContainer>
			)}

			{blockingError.errorType === 'invalidXML' && (
				<ClayAlert.ToastContainer>
					<ClayAlert
						autoClose={5000}
						displayType="danger"
						onClose={() => setBlockingError({errorType: ''})}
						title={`${Liferay.Language.get('error')}:`}
					>
						{Liferay.Language.get('please-select-a-valid-xml-file')}
					</ClayAlert>
				</ClayAlert.ToastContainer>
			)}
		</>
	);
}
