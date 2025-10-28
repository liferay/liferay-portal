/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import ClayToolbar from '@clayui/toolbar';
import {TranslationAdminSelector} from 'frontend-js-components-web';
import {localStorage} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useContext, useEffect, useRef, useState} from 'react';
import {isEdge, isNode} from 'react-flow-renderer';

import {DefinitionBuilderContext} from '../../../DefinitionBuilderContext';
import {defaultLanguageId} from '../../../constants';
import {detectGroovyOrJavaScript} from '../../../diagram-builder/util/detectGroovyOrJavaScript';
import {xmlNamespace} from '../../../source-builder/constants';
import DeserializeUtil from '../../../source-builder/deserializeUtil';
import {serializeDefinition} from '../../../source-builder/serializeUtil';
import XMLUtil from '../../../source-builder/xmlUtil';
import {getAvailableLocalesObject} from '../../../util/availableLocales';
import {
	publishDefinitionRequest,
	saveDefinitionRequest,
} from '../../../util/fetchUtil';
import {isObjectEmpty} from '../../../util/utils';
import {GroovyScriptWarningModal} from './GroovyScriptWarningModal';

export default function UpperToolbar({
	displayNames,
	isView,
	languageIds,
	portletNamespace,
}) {
	const {
		active,
		alertMessage,
		alertType,
		allowScriptContentToBeExecutedOrIncluded,
		blockingError,
		currentEditor,
		definitionDescription,
		definitionName,
		definitionTitle,
		definitionTitleTranslations,
		elements,
		scriptManagementConfigurationPortletURL,
		selectedLanguageId,
		setAlertMessage,
		setAlertType,
		setBlockingError,
		setDefinitionDescription,
		setDefinitionName,
		setDefinitionTitle,
		setDefinitionTitleTranslations,
		setDeserialize,
		setElements,
		setHadGroovyOrJavaScriptBefore,
		setHasGroovyOrJavaScript,
		setSelectedLanguageId,
		setShowAlert,
		setShowDefinitionInfo,
		setSourceView,
		setWorkflowDefinitionVersions,
		showAlert,
		sourceView,
		workflowDefinitionVersions,
	} = useContext(DefinitionBuilderContext);

	const [showGroovyScriptWarningModal, setShowGroovyScriptWarningModal] =
		useState(false);

	const [translations, setTranslations] = useState(
		definitionTitleTranslations
	);

	function findEmptyElements(element, language) {
		if (element.data.label && !(language in element.data.label)) {
			return true;
		}
	}

	function setAlert(alertMessage, alertType, showAlert) {
		setAlertMessage(alertMessage);
		setAlertType(alertType);
		setShowAlert(showAlert);
	}

	const inputRef = useRef(null);

	const availableLocales = getAvailableLocalesObject(
		displayNames,
		languageIds
	);

	const errorTitle = () => {
		if (blockingError.errorType === 'duplicated') {
			return Liferay.Language.get(
				'you-have-the-same-name-in-two-nodes'
			).slice(0, -1);
		}
		else if (blockingError.errorType === 'emptyField') {
			return Liferay.Language.get('some-fields-need-to-be-filled').slice(
				0,
				-1
			);
		}
		else if (blockingError.errorType === 'assignment') {
			return Liferay.Language.get('warning');
		}
		else {
			return Liferay.Language.get('error');
		}
	};

	const getXMLContent = () => {
		if (!sourceView) {
			const xmlDefinition = serializeDefinition(
				xmlNamespace,
				{
					description: definitionDescription,
					name: definitionName,
					version: workflowDefinitionVersions.length,
				},
				elements.filter(isNode),
				elements.filter(isEdge)
			);

			return (
				XMLUtil.validateDefinition(xmlDefinition) && {
					metadata: {
						description: definitionDescription,
						name: definitionName,
						version: workflowDefinitionVersions.length,
					},
					xmlDefinition,
				}
			);
		}
		else {
			const xmlDefinition = currentEditor.getData();

			if (XMLUtil.validateDefinition(xmlDefinition)) {
				const deserializeUtil = new DeserializeUtil();

				deserializeUtil.updateXMLDefinition(
					encodeURIComponent(xmlDefinition)
				);

				const metadata = deserializeUtil.getMetadata();

				setDefinitionName(metadata.name);
				setDefinitionDescription(metadata.description);
				setElements(deserializeUtil.getElements());

				return {metadata, xmlDefinition};
			}
		}

		return false;
	};

	const handleInvalidXMLBlockingError = () => {
		setBlockingError(() => ({
			errorMessage: Liferay.Language.get(
				'please-select-a-valid-xml-file'
			),
			errorType: 'invalidXML',
		}));
	};

	const onSelectedLanguageIdChange = (id) => {
		if (id) {
			setSelectedLanguageId(id);
		}
	};

	const definitionNotPublished =
		!workflowDefinitionVersions.length || !active;

	const redirectToSavedDefinition = (name, version) => {
		const definitionURL = new URL(window.location.href);

		definitionURL.searchParams.set(
			portletNamespace + 'draftVersion',
			Number.parseFloat(version).toFixed(1)
		);
		definitionURL.searchParams.set(portletNamespace + 'name', name);

		window.location.replace(definitionURL);
	};

	const saveOrPublishDefinition = async (
		localStorageKeyName,
		saveOrPublishDefinitionRequest,
		successAlertMessage
	) => {
		if (blockingError.errorType !== '') {
			setAlert(blockingError.errorMessage, 'danger', true);

			return;
		}

		const validXMLDefinition = getXMLContent();

		if (!validXMLDefinition) {
			handleInvalidXMLBlockingError();

			return;
		}

		const {
			metadata: {name, version},
			xmlDefinition,
		} = validXMLDefinition;

		const publishedOrSavedDefinitionResponse =
			await saveOrPublishDefinitionRequest({
				active,
				content: xmlDefinition,
				name,
				title: definitionTitle,
				title_i18n: definitionTitleTranslations,
				version,
			});

		const publishedOrSavedDefinitionResponseJSON =
			await publishedOrSavedDefinitionResponse.json();

		if (!publishedOrSavedDefinitionResponse.ok) {
			setAlert(
				publishedOrSavedDefinitionResponseJSON.title,
				'danger',
				true
			);

			return;
		}

		if (!allowScriptContentToBeExecutedOrIncluded) {
			setHadGroovyOrJavaScriptBefore(false);
		}

		setDefinitionName(publishedOrSavedDefinitionResponseJSON.name);

		setWorkflowDefinitionVersions((prevValues) => [
			{
				creatorName:
					publishedOrSavedDefinitionResponseJSON.creator?.name,
				dateCreated:
					publishedOrSavedDefinitionResponseJSON.dateModified,
				version: String(
					parseInt(publishedOrSavedDefinitionResponseJSON.version, 10)
				),
			},
			...prevValues,
		]);

		if (publishedOrSavedDefinitionResponseJSON.version === '1') {
			localStorage.setItem(
				localStorageKeyName,
				true,
				localStorage.TYPES.FUNCTIONAL
			);
			redirectToSavedDefinition(
				publishedOrSavedDefinitionResponseJSON.name,
				publishedOrSavedDefinitionResponseJSON.version
			);

			return;
		}

		setAlert(successAlertMessage, 'success', true);

		return;
	};

	const publishDefinition = async () => {
		if (
			!allowScriptContentToBeExecutedOrIncluded &&
			detectGroovyOrJavaScript(elements, setHasGroovyOrJavaScript)
		) {
			setShowGroovyScriptWarningModal(true);

			return;
		}

		if (!definitionTitle) {
			setAlert(
				Liferay.Language.get('name-workflow-before-publish'),
				'danger',
				true
			);

			return;
		}

		saveOrPublishDefinition(
			'firstPublished',
			publishDefinitionRequest,
			definitionNotPublished
				? Liferay.Language.get('workflow-published-successfully')
				: Liferay.Language.get('workflow-updated-successfully')
		);
	};

	const saveDefinition = async () => {
		if (
			!allowScriptContentToBeExecutedOrIncluded &&
			detectGroovyOrJavaScript(elements, setHasGroovyOrJavaScript)
		) {
			setShowGroovyScriptWarningModal(true);

			return;
		}

		saveOrPublishDefinition(
			'firstSaved',
			saveDefinitionRequest,
			Liferay.Language.get('workflow-saved')
		);
	};

	useEffect(() => {
		if (isObjectEmpty(definitionTitleTranslations)) {
			setDefinitionTitleTranslations({
				[defaultLanguageId]: Liferay.Language.get('new-workflow'),
			});
		}

		if (selectedLanguageId) {
			setDefinitionTitle(definitionTitleTranslations[selectedLanguageId]);
		}
	}, [
		selectedLanguageId,
		setDefinitionTitle,
		setDefinitionTitleTranslations,
		definitionTitleTranslations,
	]);

	useEffect(() => {
		let languageId = defaultLanguageId;

		if (selectedLanguageId) {
			languageId = selectedLanguageId;
		}

		setDefinitionTitleTranslations((previous) => ({
			...previous,
			[languageId]: definitionTitle,
		}));

		languageIds.map((currentLanguage) => {
			const emptyLabel = elements?.find((elements) =>
				findEmptyElements(elements, currentLanguage)
			);
			if (!emptyLabel && definitionTitleTranslations[currentLanguage]) {
				setTranslations((previous) => ({
					...previous,
					[currentLanguage]: true,
				}));
			}
			else {
				setTranslations((previous) => ({
					...previous,
					[currentLanguage]: false,
				}));
			}
		});

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [definitionTitle, elements]);

	useEffect(() => {
		if (localStorage.getItem('firstSaved', localStorage.TYPES.FUNCTIONAL)) {
			setAlert(Liferay.Language.get('workflow-saved'), 'success', true);
			localStorage.removeItem('firstSaved');
		}
		else if (
			localStorage.getItem(
				'firstPublished',
				localStorage.TYPES.FUNCTIONAL
			)
		) {
			setAlert(
				Liferay.Language.get('workflow-published-successfully'),
				'success',
				true
			);
			localStorage.removeItem('firstPublished');
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	useEffect(() => {
		if (blockingError.errorType === 'assignment') {
			setAlert(blockingError.errorMessage, 'warning', true);
		}
		else if (blockingError.errorType === 'invalidXML') {
			setAlert(blockingError.errorMessage, 'danger', true);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [blockingError]);

	const resetAlert = () => {
		setShowAlert(false);
		if (
			blockingError.errorType === 'assignment' ||
			blockingError.errorType === 'invalidXML'
		) {
			setBlockingError({errorType: ''});
		}
	};

	return (
		<>
			<ClayToolbar className="upper-toolbar">
				<ClayLayout.ContainerFluid>
					<ClayToolbar.Nav>
						<ClayToolbar.Item>
							<TranslationAdminSelector
								activeLanguageIds={languageIds}
								adminMode
								availableLocales={availableLocales}
								defaultLanguageId={defaultLanguageId}
								onSelectedLanguageIdChange={
									onSelectedLanguageIdChange
								}
								translations={translations}
							/>
						</ClayToolbar.Item>

						<ClayToolbar.Item expand>
							<ClayInput
								autoComplete="off"
								className="form-control-inline"
								disabled={isView}
								id="definition-title"
								onChange={({target: {value}}) => {
									setDefinitionTitle(value);
								}}
								placeholder={Liferay.Language.get(
									'untitled-workflow'
								)}
								ref={inputRef}
								type="text"
								value={definitionTitle || ''}
							/>
						</ClayToolbar.Item>

						{workflowDefinitionVersions.length !== 0 && (
							<ClayToolbar.Item>
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get(
										'definition-info'
									)}
									displayType="secondary"
									onClick={() =>
										setShowDefinitionInfo(
											(previous) => !previous
										)
									}
									symbol="info-circle-open"
									title={Liferay.Language.get(
										'definition-info'
									)}
								/>
							</ClayToolbar.Item>
						)}

						<ClayToolbar.Item>
							<ClayButton
								aria-label={Liferay.Language.get('cancel')}
								displayType="secondary"
								onClick={() => {
									window.history.back();
								}}
								title={Liferay.Language.get('cancel')}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>
						</ClayToolbar.Item>

						{definitionNotPublished && (
							<ClayToolbar.Item>
								<ClayButton
									aria-label={Liferay.Language.get('save')}
									disabled={isView}
									displayType="secondary"
									onClick={saveDefinition}
									title={Liferay.Language.get('save')}
								>
									{Liferay.Language.get('save')}
								</ClayButton>
							</ClayToolbar.Item>
						)}

						<ClayToolbar.Item>
							<ClayButton
								aria-label={
									definitionNotPublished
										? Liferay.Language.get('publish')
										: Liferay.Language.get('update')
								}
								disabled={isView}
								displayType="primary"
								onClick={publishDefinition}
								title={
									definitionNotPublished
										? Liferay.Language.get('publish')
										: Liferay.Language.get('update')
								}
							>
								{definitionNotPublished
									? Liferay.Language.get('publish')
									: Liferay.Language.get('update')}
							</ClayButton>
						</ClayToolbar.Item>

						<ClayToolbar.Item>
							{sourceView ? (
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get(
										'diagram-view'
									)}
									displayType="secondary"
									onClick={() => {
										if (
											XMLUtil.validateDefinition(
												currentEditor.getData()
											)
										) {
											setSourceView(false);
											setDeserialize(true);
										}
										else {
											handleInvalidXMLBlockingError();
										}
									}}
									symbol="rules"
									title={Liferay.Language.get('diagram-view')}
								/>
							) : (
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get(
										'source-view'
									)}
									displayType="secondary"
									onClick={() => setSourceView(true)}
									symbol="code"
									title={Liferay.Language.get('source-view')}
								/>
							)}
						</ClayToolbar.Item>
					</ClayToolbar.Nav>
				</ClayLayout.ContainerFluid>
			</ClayToolbar>

			{showAlert && (
				<ClayAlert.ToastContainer>
					<ClayAlert
						autoClose={5000}
						closeButtonAriaLabel={Liferay.Language.get('close')}
						displayType={alertType}
						onClose={() => resetAlert()}
						title={
							alertType === 'success'
								? `${Liferay.Language.get('success')}:`
								: `${errorTitle()}:`
						}
					>
						{alertMessage}
					</ClayAlert>
				</ClayAlert.ToastContainer>
			)}

			{showGroovyScriptWarningModal && (
				<GroovyScriptWarningModal
					scriptManagementConfigurationPortletURL={
						scriptManagementConfigurationPortletURL
					}
					setShowGroovyScriptWarningModal={() => {
						setShowGroovyScriptWarningModal(false);
					}}
				/>
			)}
		</>
	);
}

UpperToolbar.propTypes = {
	definitionTitleTranslations: PropTypes.object,
	displayNames: PropTypes.arrayOf(PropTypes.string).isRequired,
	languageIds: PropTypes.arrayOf(PropTypes.string).isRequired,
	title: PropTypes.PropTypes.string.isRequired,
	version: PropTypes.PropTypes.string.isRequired,
};
