/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {useFormik} from 'formik';
import {fetch, sub} from 'frontend-js-web';
import React, {useMemo, useState} from 'react';

import {LearnMessageWithoutContext} from '../../shared/LearnMessage';
import Input from './Input';
import SubmitWarningModal from './SubmitWarningModal';
import TestConfigurationButton from './TestConfigurationButton';
import {TEXT_EMBEDDING_PROVIDER_TYPES} from './constants';

const DEFAULT_TEXT_EMBEDDING_PROVIDER_CONFIGURATIONS = {
	attributes: {
		accessToken: '',
		autoTruncate: true,
		hostAddress: '',
		maxCharacterCount: 500,
		model: '',
		modelTimeout: 25,
		textTruncationStrategy: 'beginning',
	},
	embeddingVectorDimensions: 768,
	languageIds: ['en_US'],
	modelClassNames: [
		'com.liferay.blogs.model.BlogsEntry',
		'com.liferay.document.library.kernel.model.DLFileEntry',
		'com.liferay.journal.model.JournalArticle',
		'com.liferay.knowledge.base.model.KBArticle',
		'com.liferay.wiki.model.WikiPage',
	],
	providerName: TEXT_EMBEDDING_PROVIDER_TYPES.HUGGING_FACE_INFERENCE_API,
};

/**
 * Determines if two values are unequal. If one of the items is
 * an integer, both are parsed to integers before comparison. If the
 * items are arrays, their order is not considered.
 *
 * @param {Array|integer|string} item1
 * @param {Array|integer|string} item2
 * @returns {boolean}
 */
function isNotEqual(item1, item2) {
	if (Number.isInteger(item1) || Number.isInteger(item2)) {
		return parseInt(item1, 10) !== parseInt(item2, 10);
	}

	if (Array.isArray(item1) && Array.isArray(item2)) {
		return (
			item1.length !== item2.length ||
			item1.some((str) => !item2.includes(str)) ||
			item2.some((str) => !item1.includes(str))
		);
	}

	return item1 !== item2;
}

function parseJSONString(jsonString) {
	if (typeof jsonString === 'undefined' || jsonString === '') {
		return '';
	}

	try {
		return JSON.parse(jsonString);
	}
	catch (error) {
		if (process.env.NODE_ENV === 'development') {
			console.error(error);
		}

		return jsonString;
	}
}

/**
 * Converts an array of JSON strings into an array of JSON objects.
 *
 * Example:
 * parseArrayOfJSONStrings(["{}"]);
 * => [{}]
 * @param {Array} array
 * @returns {Array}
 */
function parseArrayOfJSONStrings(array) {
	return array.map((string) => parseJSONString(string));
}

/**
 * Determines the value of textEmbeddingProviderConfigurationJSONs based
 * on the initial prop and set of available providers.
 * @param {Array} initialTextEmbeddingProviderConfigurationJSONs
 * Initial configurations of the text embedding provider, as an
 * array of stringified objects.
 * @param {object} availableProviders
 * @returns {Array} Cleaned up list of provider configurations.
 */
function resolveInitialTextEmbeddingProviderConfigurationJSONs(
	initialTextEmbeddingProviderConfigurationJSONs,
	availableTextEmbeddingProviders
) {
	const initialTextEmbeddingProviderConfigurationsArray =
		parseArrayOfJSONStrings(initialTextEmbeddingProviderConfigurationJSONs);

	if (!initialTextEmbeddingProviderConfigurationsArray.length) {
		return [
			{
				...DEFAULT_TEXT_EMBEDDING_PROVIDER_CONFIGURATIONS,
				providerName: resolveProviderName(
					availableTextEmbeddingProviders
				),
			},
		];
	}

	return initialTextEmbeddingProviderConfigurationsArray.map(
		(configurations) => ({
			...configurations,
			providerName: resolveProviderName(
				availableTextEmbeddingProviders,
				configurations.providerName
			),
		})
	);
}

/**
 * Gets the valid string that should be set for providerName. This covers cases
 * where the providerName should not be set to a provider that's been
 * blacklisted.
 * @param {object} availableProviders
 * @param {string} [providerName]
 * @returns {string}
 */
function resolveProviderName(availableProviders, providerName) {
	if (!providerName || !availableProviders[providerName]) {
		return Object.keys(availableProviders)[0];
	}

	return providerName;
}

/**
 * Formats the object into an array of label and value, important for inputs
 * that offer selection. If object is actually a flat array, this formats
 * the items into label-value pairs.
 *
 * Examples:
 * transformToLabelValueArray({en_US: 'English', es_ES: 'Spanish'})
 * => [{label: 'English', value: 'en_US'}, {label: 'Spanish', value: 'es_ES'}]
 * transformToLabelValueArray(['one', 'two'])
 * => [{label: 'one', value: 'one'}, {label: 'two', value: 'two'}]
 *
 * @param {Array|object} items
 * @return {Array}
 */
const transformToLabelValueArray = (items = {}) => {
	if (Array.isArray(items)) {
		return items.map((item) =>
			item.value && item.label
				? item
				: {
						label: item,
						value: item,
					}
		);
	}

	return Object.entries(items).map(([value, label]) => ({
		label,
		value,
	}));
};

/**
 * Form within semantic search settings page, configures text embedding provider and
 * indexing settings.
 * This can be found on: System Settings > Search Experiences > Semantic Search
 */
export default function ({
	availableEmbeddingVectorDimensions,
	availableLanguageDisplayNames,
	availableModelClassNames,
	availableTextEmbeddingProviders,
	availableTextTruncationStrategies,
	formName,
	initialTextEmbeddingCacheTimeout,
	initialTextEmbeddingProviderConfigurationJSONs,
	initialTextEmbeddingsEnabled,
	learnMessages,
	namespace = '',
	redirectURL,
}) {
	const resolvedInitialTextEmbeddingProviderConfigurationJSONs = useMemo(
		() =>
			resolveInitialTextEmbeddingProviderConfigurationJSONs(
				initialTextEmbeddingProviderConfigurationJSONs,
				availableTextEmbeddingProviders
			),
		[
			initialTextEmbeddingProviderConfigurationJSONs,
			availableTextEmbeddingProviders,
		]
	);

	const [showSubmitWarningModal, setShowSubmitWarningModal] = useState(false);

	const _handleFormikSubmit = async (values, actions) => {
		const {
			attributes = {},
			languageIds,
			modelClassNames,
			providerName,
			embeddingVectorDimensions,
		} = values.textEmbeddingProviderConfigurationJSONs[0];

		const {
			accessToken,
			apiKey,
			autoTruncate,
			basicAuthPassword,
			basicAuthUsername,
			dimensions,
			hostAddress,
			location,
			maxCharacterCount,
			model,
			modelTimeout,
			projectId,
			textTruncationStrategy,
			user,
		} = attributes;

		const textEmbeddingProviderSettings =
			providerName ===
			TEXT_EMBEDDING_PROVIDER_TYPES.HUGGING_FACE_INFERENCE_API
				? {
						accessToken,
						model,
						modelTimeout,
					}
				: providerName ===
					  TEXT_EMBEDDING_PROVIDER_TYPES.HUGGING_FACE_INFERENCE_ENDPOINT
					? {
							accessToken,
							hostAddress,
						}
					: providerName === TEXT_EMBEDDING_PROVIDER_TYPES.OPENAI
						? {
								apiKey,
								dimensions,
								model,
								user,
							}
						: providerName === TEXT_EMBEDDING_PROVIDER_TYPES.TXTAI
							? {
									basicAuthPassword,
									basicAuthUsername,
									hostAddress,
								}
							: providerName ===
								  TEXT_EMBEDDING_PROVIDER_TYPES.VERTEX_AI
								? {
										autoTruncate,
										location,
										model,
										projectId,
									}
								: {};

		const responseData = await fetch(
			'/o/search/v1.0/embeddings/validate-provider-configuration',
			{
				body: JSON.stringify({
					attributes: {
						maxCharacterCount,
						textTruncationStrategy,
						...textEmbeddingProviderSettings,
					},
					embeddingVectorDimensions,
					languageIds,
					modelClassNames,
					providerName,
				}),
				headers: new Headers({
					'Accept': 'application/json',
					'Accept-Language':
						Liferay.ThemeDisplay.getBCP47LanguageId(),
					'Content-Type': 'application/json',
				}),
				method: 'POST',
			}
		)
			.then((response) => {
				actions.setSubmitting(false);

				return response.json();
			})
			.catch((error) => {
				actions.setSubmitting(false);

				setShowSubmitWarningModal(true);

				if (process.env.NODE_ENV === 'development') {
					console.error(error);
				}
			});

		if (
			responseData.errorMessage ||
			Number(responseData.expectedDimensions) === 0 ||
			Number(responseData.expectedDimensions) !==
				Number(embeddingVectorDimensions) ||
			responseData.message
		) {
			setShowSubmitWarningModal(true);
		}
		else {
			submitForm(document[formName]);
		}
	};

	const _handleFormikValidate = (values) => {
		const errors = {};

		const textEmbeddingProviderConfigurationJSONsErrors =
			values.textEmbeddingProviderConfigurationJSONs?.map(
				(textEmbeddingProviderConfigurationJSON) => {
					const textEmbeddingProviderConfigurationJSONError = {
						attributes: {}, // Sets empty values to avoid undefined errors when setting values.
					};

					// Validate "Types" field.

					if (
						!textEmbeddingProviderConfigurationJSON.modelClassNames
							?.length
					) {
						textEmbeddingProviderConfigurationJSONError.modelClassNames =
							sub(
								Liferay.Language.get('the-x-field-is-required'),
								[Liferay.Language.get('types')]
							);
					}

					// Validate "Hugging Face Access Token" field.

					if (
						textEmbeddingProviderConfigurationJSON.providerName ===
							TEXT_EMBEDDING_PROVIDER_TYPES.HUGGING_FACE_INFERENCE_API ||
						textEmbeddingProviderConfigurationJSON.providerName ===
							TEXT_EMBEDDING_PROVIDER_TYPES.HUGGING_FACE_INFERENCE_ENDPOINT
					) {
						if (
							!textEmbeddingProviderConfigurationJSON.attributes
								?.accessToken ||
							textEmbeddingProviderConfigurationJSON.attributes
								?.accessToken === ''
						) {
							textEmbeddingProviderConfigurationJSONError.attributes.accessToken =
								Liferay.Language.get('this-field-is-required');
						}
					}

					// Validate "Languages" field.

					if (
						!textEmbeddingProviderConfigurationJSON.languageIds
							?.length
					) {
						textEmbeddingProviderConfigurationJSONError.languageIds =
							sub(
								Liferay.Language.get('the-x-field-is-required'),
								[Liferay.Language.get('languages')]
							);
					}

					// Validate "Max Character Count" field.

					if (
						!textEmbeddingProviderConfigurationJSON.attributes
							?.maxCharacterCount === ''
					) {
						textEmbeddingProviderConfigurationJSONError.attributes.maxCharacterCount =
							Liferay.Language.get('this-field-is-required');
					}
					else {
						if (
							textEmbeddingProviderConfigurationJSON.attributes
								?.maxCharacterCount < 50
						) {
							textEmbeddingProviderConfigurationJSONError.attributes.maxCharacterCount =
								sub(
									Liferay.Language.get(
										'please-enter-a-value-greater-than-or-equal-to-x'
									),
									['50']
								);
						}
					}

					if (
						textEmbeddingProviderConfigurationJSON.providerName ===
						TEXT_EMBEDDING_PROVIDER_TYPES.HUGGING_FACE_INFERENCE_API
					) {

						// Validate "Model" field.

						if (
							!textEmbeddingProviderConfigurationJSON.attributes
								?.model ||
							textEmbeddingProviderConfigurationJSON.attributes
								?.model === ''
						) {
							textEmbeddingProviderConfigurationJSONError.attributes.model =
								Liferay.Language.get('this-field-is-required');
						}

						// Validate "Model Timeout" field.

						if (
							!textEmbeddingProviderConfigurationJSON.attributes
								?.modelTimeout ||
							textEmbeddingProviderConfigurationJSON.attributes
								?.modelTimeout === ''
						) {
							textEmbeddingProviderConfigurationJSONError.attributes.modelTimeout =
								Liferay.Language.get('this-field-is-required');
						}
						else {
							if (
								textEmbeddingProviderConfigurationJSON
									.attributes?.modelTimeout < 0
							) {
								textEmbeddingProviderConfigurationJSONError.attributes.modelTimeout =
									sub(
										Liferay.Language.get(
											'please-enter-a-value-greater-than-or-equal-to-x'
										),
										['0']
									);
							}

							if (
								textEmbeddingProviderConfigurationJSON
									.attributes?.modelTimeout > 60
							) {
								textEmbeddingProviderConfigurationJSONError.attributes.modelTimeout =
									sub(
										Liferay.Language.get(
											'please-enter-a-value-less-than-or-equal-to-x'
										),
										['60']
									);
							}
						}
					}

					// Validate "Host Address" field.

					if (
						textEmbeddingProviderConfigurationJSON.providerName ===
							TEXT_EMBEDDING_PROVIDER_TYPES.HUGGING_FACE_INFERENCE_ENDPOINT ||
						textEmbeddingProviderConfigurationJSON.providerName ===
							TEXT_EMBEDDING_PROVIDER_TYPES.TXTAI
					) {
						if (
							!textEmbeddingProviderConfigurationJSON.attributes
								?.hostAddress ||
							textEmbeddingProviderConfigurationJSON.attributes
								?.hostAddress === ''
						) {
							textEmbeddingProviderConfigurationJSONError.attributes.hostAddress =
								Liferay.Language.get('this-field-is-required');
						}
					}

					if (
						textEmbeddingProviderConfigurationJSON.providerName ===
						TEXT_EMBEDDING_PROVIDER_TYPES.OPENAI
					) {

						// Validate "apiKey" field.

						if (
							!textEmbeddingProviderConfigurationJSON.attributes
								?.apiKey ||
							textEmbeddingProviderConfigurationJSON.attributes
								?.apiKey === ''
						) {
							textEmbeddingProviderConfigurationJSONError.attributes.apiKey =
								Liferay.Language.get('this-field-is-required');
						}

						// Validate "Model" field.

						if (
							!textEmbeddingProviderConfigurationJSON.attributes
								?.model ||
							textEmbeddingProviderConfigurationJSON.attributes
								?.model === ''
						) {
							textEmbeddingProviderConfigurationJSONError.attributes.model =
								Liferay.Language.get('this-field-is-required');
						}
					}

					if (
						textEmbeddingProviderConfigurationJSON.providerName ===
						TEXT_EMBEDDING_PROVIDER_TYPES.VERTEX_AI
					) {

						// Validate "Location" field.

						if (
							!textEmbeddingProviderConfigurationJSON.attributes
								?.location ||
							textEmbeddingProviderConfigurationJSON.attributes
								?.location === ''
						) {
							textEmbeddingProviderConfigurationJSONError.attributes.location =
								Liferay.Language.get('this-field-is-required');
						}

						// Validate "Model" field.

						if (
							!textEmbeddingProviderConfigurationJSON.attributes
								?.model ||
							textEmbeddingProviderConfigurationJSON.attributes
								?.model === ''
						) {
							textEmbeddingProviderConfigurationJSONError.attributes.model =
								Liferay.Language.get('this-field-is-required');
						}

						// Validate "Project ID" field.

						if (
							!textEmbeddingProviderConfigurationJSON.attributes
								?.projectId ||
							textEmbeddingProviderConfigurationJSON.attributes
								?.projectId === ''
						) {
							textEmbeddingProviderConfigurationJSONError.attributes.projectId =
								Liferay.Language.get('this-field-is-required');
						}
					}

					return textEmbeddingProviderConfigurationJSONError;
				}
			);

		// Update "errors.textEmbeddingProviderConfigurationJSONs" only if it has errors

		if (
			textEmbeddingProviderConfigurationJSONsErrors.some(
				({attributes, languageIds, modelClassNames}) =>
					!!Object.keys(attributes).length ||
					languageIds ||
					modelClassNames
			)
		) {
			errors.textEmbeddingProviderConfigurationJSONs =
				textEmbeddingProviderConfigurationJSONsErrors;
		}

		// Validate "Text Embedding Cache Timeout" field.

		if (values.textEmbeddingCacheTimeout === '') {
			errors.textEmbeddingCacheTimeout = Liferay.Language.get(
				'this-field-is-required'
			);
		}
		else if (values.textEmbeddingCacheTimeout < 0) {
			errors.textEmbeddingCacheTimeout = sub(
				Liferay.Language.get(
					'please-enter-a-value-greater-than-or-equal-to-x'
				),
				['0']
			);
		}

		return errors;
	};

	const formik = useFormik({
		initialValues: {
			textEmbeddingCacheTimeout: initialTextEmbeddingCacheTimeout,
			textEmbeddingProviderConfigurationJSONs:
				resolvedInitialTextEmbeddingProviderConfigurationJSONs,
			textEmbeddingsEnabled: initialTextEmbeddingsEnabled,
		},
		onSubmit: _handleFormikSubmit,
		validate: _handleFormikValidate,
		validateOnMount: true,
	});

	const _handleCheckboxChange = (name) => (event) => {
		formik.setFieldValue(name, event.target.checked);
	};

	const _handleInputBlur = (name) => () => {
		formik.setFieldTouched(name);
	};

	const _handleInputChange = (name) => (val) => {
		formik.setFieldValue(name, val);
	};

	const _handleSubmit = () => {
		if (document[formName].checkValidity()) {
			formik.handleSubmit();
		}
		else {
			document[formName].reportValidity();
		}
	};

	const _handleSubmitWarningModalClose = () => {
		setShowSubmitWarningModal(false);
	};

	const _handleSubmitWarningModalSave = () => {
		_handleSubmitWarningModalClose();

		submitForm(document[formName]);
	};

	const _isProviderConfigurationDirty = () => {
		return formik.values.textEmbeddingProviderConfigurationJSONs?.some(
			(config, index) => {
				return (
					[
						'embeddingVectorDimensions',
						'providerName',
						'modelClassNames',
					].some((property) => {
						return isNotEqual(
							resolvedInitialTextEmbeddingProviderConfigurationJSONs[
								index
							][property],
							config[property]
						);
					}) ||
					[
						'accessToken',
						'apiKey',
						'dimensions',
						'basicAuthPassword',
						'basicAuthUsername',
						'hostAddress',
						'location',
						'model',
						'projectId',
						'modelTimeout',
						'user',
					].some((property) => {
						return isNotEqual(
							resolvedInitialTextEmbeddingProviderConfigurationJSONs[
								index
							].attributes[property],
							config.attributes[property]
						);
					})
				);
			}
		);
	};

	const _isTextEmbeddingsEnabledDirty = () =>
		formik.values.textEmbeddingsEnabled !== initialTextEmbeddingsEnabled;

	const _renderEmbeddingProviderConfigurationInputs = (index) => {
		return (
			<>
				<div className="sheet-section">
					<h3 className="sheet-subtitle">
						{Liferay.Language.get(
							'text-embedding-provider-settings'
						)}
					</h3>

					<ClayForm.Group>
						<ClayCheckbox
							aria-label={Liferay.Language.get(
								'text-embeddings-enabled'
							)}
							checked={!!formik.values.textEmbeddingsEnabled}
							disabled={formik.isSubmitting}
							label={Liferay.Language.get(
								'text-embeddings-enabled'
							)}
							name={`${namespace}textEmbeddingsEnabled`}
							onChange={_handleCheckboxChange(
								'textEmbeddingsEnabled'
							)}
							value={!!formik.values.textEmbeddingsEnabled}
						/>
					</ClayForm.Group>

					<Input
						disabled={formik.isSubmitting}
						error={
							formik.errors
								?.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.providerName
						}
						items={transformToLabelValueArray(
							availableTextEmbeddingProviders
						)}
						label={Liferay.Language.get('text-embedding-provider')}
						name={`textEmbeddingProviderConfigurationJSONs[${index}].providerName`}
						onBlur={_handleInputBlur(
							`textEmbeddingProviderConfigurationJSONs[${index}].providerName`
						)}
						onChange={_handleInputChange(
							`textEmbeddingProviderConfigurationJSONs[${index}].providerName`
						)}
						type="select"
						value={
							formik.values
								?.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.providerName
						}
					>
						{formik.values
							.textEmbeddingProviderConfigurationJSONs?.[index]
							?.providerName ===
							TEXT_EMBEDDING_PROVIDER_TYPES.HUGGING_FACE_INFERENCE_API && (
							<ClayForm.FeedbackGroup>
								<ClayForm.Text>
									{Liferay.Language.get(
										'text-embedding-provider-hugging-face-inference-api-help'
									)}

									<LearnMessageWithoutContext
										className="ml-1"
										learnMessages={learnMessages}
										resourceKey="semantic-search"
									/>
								</ClayForm.Text>
							</ClayForm.FeedbackGroup>
						)}

						{formik.values
							.textEmbeddingProviderConfigurationJSONs?.[index]
							?.providerName ===
							TEXT_EMBEDDING_PROVIDER_TYPES.HUGGING_FACE_INFERENCE_ENDPOINT && (
							<ClayForm.FeedbackGroup>
								<ClayForm.Text>
									{Liferay.Language.get(
										'text-embedding-provider-hugging-face-inference-endpoint-help'
									)}

									<LearnMessageWithoutContext
										className="ml-1"
										learnMessages={learnMessages}
										resourceKey="semantic-search"
									/>
								</ClayForm.Text>
							</ClayForm.FeedbackGroup>
						)}

						{formik.values
							.textEmbeddingProviderConfigurationJSONs?.[index]
							?.providerName ===
							TEXT_EMBEDDING_PROVIDER_TYPES.VERTEX_AI && (
							<ClayForm.FeedbackGroup>
								<ClayForm.Text>
									{Liferay.Language.get(
										'text-embedding-provider-vertex-ai-authentication-help'
									)}

									<LearnMessageWithoutContext
										className="ml-1"
										learnMessages={learnMessages}
										resourceKey="semantic-search"
									/>
								</ClayForm.Text>
							</ClayForm.FeedbackGroup>
						)}
					</Input>

					{formik.values.textEmbeddingProviderConfigurationJSONs?.[
						index
					]?.providerName === TEXT_EMBEDDING_PROVIDER_TYPES.TXTAI && (
						<>
							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.hostAddress
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-host-address-help'
								)}
								label={Liferay.Language.get('host-address')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.hostAddress`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.hostAddress`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.hostAddress`
								)}
								required
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.hostAddress
								}
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.hostAddress
								}
							/>

							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.basicAuthUsername
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-basic-auth-username-help'
								)}
								label={Liferay.Language.get(
									'basic-auth-username'
								)}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.basicAuthUsername`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.basicAuthUsername`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.basicAuthUsername`
								)}
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.basicAuthUsername
								}
							/>

							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.basicAuthPassword
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-basic-auth-password-help'
								)}
								label={Liferay.Language.get(
									'basic-auth-password'
								)}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.basicAuthPassword`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.basicAuthPassword`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.basicAuthPassword`
								)}
								type="password"
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.basicAuthPassword
								}
							/>
						</>
					)}

					{formik.values.textEmbeddingProviderConfigurationJSONs?.[
						index
					]?.providerName ===
						TEXT_EMBEDDING_PROVIDER_TYPES.HUGGING_FACE_INFERENCE_API && (
						<>
							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.accessToken
								}
								label={Liferay.Language.get('access-token')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.accessToken`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.accessToken`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.accessToken`
								)}
								required
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.accessToken
								}
								type="password"
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.accessToken
								}
							/>

							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.model
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-model-help'
								)}
								label={Liferay.Language.get('model')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.model`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.model`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.model`
								)}
								providerName={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.providerName
								}
								required
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.model
								}
								type="model"
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.model
								}
							>
								<ClayForm.FeedbackGroup>
									<ClayForm.Text>
										{Liferay.Language.get(
											'begin-typing-and-select-a-model'
										)}
									</ClayForm.Text>
								</ClayForm.FeedbackGroup>
							</Input>

							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.modelTimeout
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-hugging-face-inference-api-model-timeout-help'
								)}
								label={Liferay.Language.get('model-timeout')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.modelTimeout`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.modelTimeout`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.modelTimeout`
								)}
								options={{max: 60, min: 0}}
								required
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.modelTimeout
								}
								type="number"
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.modelTimeout
								}
							/>
						</>
					)}

					{formik.values.textEmbeddingProviderConfigurationJSONs?.[
						index
					]?.providerName ===
						TEXT_EMBEDDING_PROVIDER_TYPES.HUGGING_FACE_INFERENCE_ENDPOINT && (
						<>
							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.accessToken
								}
								label={Liferay.Language.get('access-token')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.accessToken`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.accessToken`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.accessToken`
								)}
								required
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.accessToken
								}
								type="password"
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.accessToken
								}
							/>
							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.hostAddress
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-host-address-help'
								)}
								label={Liferay.Language.get('host-address')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.hostAddress`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.hostAddress`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.hostAddress`
								)}
								required
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.hostAddress
								}
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.hostAddress
								}
							/>
						</>
					)}

					{formik.values.textEmbeddingProviderConfigurationJSONs?.[
						index
					]?.providerName ===
						TEXT_EMBEDDING_PROVIDER_TYPES.OPENAI && (
						<>
							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.apiKey
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-api-key-help'
								)}
								label={Liferay.Language.get('api-key')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.apiKey`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.apiKey`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.apiKey`
								)}
								required
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.apiKey
								}
								type="password"
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.apiKey
								}
							/>

							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.dimensions
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-openai-dimensions-help'
								)}
								label={Liferay.Language.get('dimensions')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.dimensions`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.dimensions`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.dimensions`
								)}
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.dimensions
								}
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.dimensions
								}
							/>

							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.model
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-model-help'
								)}
								label={Liferay.Language.get('model')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.model`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.model`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.model`
								)}
								required
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.model
								}
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.model
								}
							/>

							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.user
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-openai-user-help'
								)}
								label={Liferay.Language.get('user')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.user`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.user`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.user`
								)}
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.user
								}
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.user
								}
							/>
						</>
					)}

					{formik.values.textEmbeddingProviderConfigurationJSONs?.[
						index
					]?.providerName ===
						TEXT_EMBEDDING_PROVIDER_TYPES.VERTEX_AI && (
						<>
							<ClayCheckbox
								aria-label={Liferay.Language.get(
									'auto-truncate'
								)}
								checked={
									!!formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.autoTruncate
								}
								disabled={formik.isSubmitting}
								label={
									<>
										{Liferay.Language.get('auto-truncate')}
										<ClayTooltipProvider>
											<span
												className="ml-2"
												title={Liferay.Language.get(
													'text-embedding-provider-vertex-ai-auto-truncate-help'
												)}
											>
												<ClayIcon symbol="question-circle-full" />
											</span>
										</ClayTooltipProvider>
									</>
								}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.autoTruncate`}
								onChange={_handleCheckboxChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.autoTruncate`
								)}
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.autoTruncate
								}
							/>
							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.location
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-vertex-ai-location-help'
								)}
								label={Liferay.Language.get('location')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.location`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.location`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.location`
								)}
								required
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.location
								}
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.location
								}
							/>

							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.model
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-model-help'
								)}
								label={Liferay.Language.get('model')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.model`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.model`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.model`
								)}
								required
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.model
								}
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.model
								}
							/>

							<Input
								disabled={formik.isSubmitting}
								error={
									formik.errors
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.projectId
								}
								helpText={Liferay.Language.get(
									'text-embedding-provider-vertex-ai-project-id-help'
								)}
								label={Liferay.Language.get('project-id')}
								name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.projectId`}
								onBlur={_handleInputBlur(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.projectId`
								)}
								onChange={_handleInputChange(
									`textEmbeddingProviderConfigurationJSONs[${index}].attributes.projectId`
								)}
								required
								touched={
									formik.touched
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.projectId
								}
								value={
									formik.values
										.textEmbeddingProviderConfigurationJSONs?.[
										index
									]?.attributes?.projectId
								}
							/>
						</>
					)}

					<Input
						disabled={formik.isSubmitting}
						error={
							formik.errors
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.embeddingVectorDimensions
						}
						helpText={Liferay.Language.get(
							'text-embedding-provider-embedding-vector-dimensions-help'
						)}
						items={transformToLabelValueArray(
							availableEmbeddingVectorDimensions
						)}
						label={Liferay.Language.get(
							'embedding-vector-dimensions'
						)}
						name={`textEmbeddingProviderConfigurationJSONs[${index}].embeddingVectorDimensions`}
						onBlur={_handleInputBlur(
							`textEmbeddingProviderConfigurationJSONs[${index}].embeddingVectorDimensions`
						)}
						onChange={_handleInputChange(
							`textEmbeddingProviderConfigurationJSONs[${index}].embeddingVectorDimensions`
						)}
						type="select"
						value={
							formik.values
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.embeddingVectorDimensions
						}
					/>

					<TestConfigurationButton
						accessToken={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes?.accessToken
						}
						apiKey={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes?.apiKey
						}
						autoTruncate={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes?.autoTruncate
						}
						availableTextEmbeddingProviders={
							availableTextEmbeddingProviders
						}
						basicAuthPassword={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes.basicAuthPassword
						}
						basicAuthUsername={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes.basicAuthUsername
						}
						dimensions={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes?.dimensions
						}
						disabled={formik.isSubmitting}
						embeddingVectorDimensions={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.embeddingVectorDimensions
						}
						errors={
							formik.errors
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]
						}
						hostAddress={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes.hostAddress
						}
						languageIds={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.languageIds
						}
						location={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes?.location
						}
						maxCharacterCount={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes?.maxCharacterCount
						}
						model={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes?.model
						}
						modelClassNames={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.modelClassNames
						}
						modelTimeout={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes?.modelTimeout
						}
						projectId={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes?.projectId
						}
						textEmbeddingCacheTimeout={
							formik.values.textEmbeddingCacheTimeout
						}
						textEmbeddingProvider={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.providerName
						}
						textEmbeddingsEnabled={
							formik.values.textEmbeddingsEnabled
						}
						textTruncationStrategy={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes.textTruncationStrategy
						}
						user={
							formik.values
								.textEmbeddingProviderConfigurationJSONs[index]
								?.attributes?.user
						}
					/>
				</div>

				<div className="sheet-section">
					<h3 className="sheet-subtitle">
						{Liferay.Language.get('index-settings')}
					</h3>

					<Input
						disabled={formik.isSubmitting}
						error={
							formik.errors
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.attributes?.maxCharacterCount
						}
						helpText={Liferay.Language.get(
							'text-embedding-provider-max-character-count-help'
						)}
						label={Liferay.Language.get('max-character-count')}
						name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.maxCharacterCount`}
						onBlur={_handleInputBlur(
							`textEmbeddingProviderConfigurationJSONs[${index}].attributes.maxCharacterCount`
						)}
						onChange={_handleInputChange(
							`textEmbeddingProviderConfigurationJSONs[${index}].attributes.maxCharacterCount`
						)}
						options={{min: 50}}
						required
						touched={
							formik.touched
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.attributes?.maxCharacterCount
						}
						type="number"
						value={
							formik.values
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.attributes?.maxCharacterCount
						}
					>
						<ClayForm.FeedbackGroup>
							<ClayForm.Text>
								{Liferay.Language.get(
									'text-embedding-provider-max-character-count-refer-to-doc-help'
								)}
							</ClayForm.Text>
						</ClayForm.FeedbackGroup>
					</Input>

					<Input
						disabled={formik.isSubmitting}
						error={
							formik.errors
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.attributes?.textTruncationStrategy
						}
						helpText={Liferay.Language.get(
							'text-embedding-provider-text-truncation-strategy-help'
						)}
						items={transformToLabelValueArray(
							availableTextTruncationStrategies
						)}
						label={Liferay.Language.get('text-truncation-strategy')}
						name={`textEmbeddingProviderConfigurationJSONs[${index}].attributes.textTruncationStrategy`}
						onBlur={_handleInputBlur(
							`textEmbeddingProviderConfigurationJSONs[${index}].attributes.textTruncationStrategy`
						)}
						onChange={_handleInputChange(
							`textEmbeddingProviderConfigurationJSONs[${index}].attributes.textTruncationStrategy`
						)}
						type="select"
						value={
							formik.values
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.attributes?.textTruncationStrategy
						}
					/>

					<Input
						disabled={formik.isSubmitting}
						error={
							formik.errors
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.modelClassNames
						}
						helpText={Liferay.Language.get(
							'text-embedding-provider-types-help'
						)}
						items={transformToLabelValueArray(
							availableModelClassNames
						)}
						label={Liferay.Language.get('types')}
						name={`textEmbeddingProviderConfigurationJSONs[${index}].modelClassNames`}
						onBlur={_handleInputBlur(
							`textEmbeddingProviderConfigurationJSONs[${index}].modelClassNames`
						)}
						onChange={_handleInputChange(
							`textEmbeddingProviderConfigurationJSONs[${index}].modelClassNames`
						)}
						required
						touched={
							formik.touched
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.modelClassNames
						}
						type="multiple"
						value={
							formik.values
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.modelClassNames
						}
					/>

					<Input
						disabled={formik.isSubmitting}
						error={
							formik.errors
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.languageIds
						}
						helpText={Liferay.Language.get(
							'text-embedding-provider-languages-help'
						)}
						items={transformToLabelValueArray(
							availableLanguageDisplayNames
						)}
						label={Liferay.Language.get('languages')}
						name={`textEmbeddingProviderConfigurationJSONs[${index}].languageIds`}
						onBlur={_handleInputBlur(
							`textEmbeddingProviderConfigurationJSONs[${index}].languageIds`
						)}
						onChange={_handleInputChange(
							`textEmbeddingProviderConfigurationJSONs[${index}].languageIds`
						)}
						required
						touched={
							formik.touched
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.languageIds
						}
						type="multiple"
						value={
							formik.values
								.textEmbeddingProviderConfigurationJSONs?.[
								index
							]?.languageIds
						}
					/>
				</div>

				<div className="sheet-section">
					<h3 className="sheet-subtitle">
						{Liferay.Language.get('search-settings')}
					</h3>

					<Input
						disabled={formik.isSubmitting}
						error={formik.errors.textEmbeddingCacheTimeout}
						helpText={Liferay.Language.get(
							'text-embedding-cache-timeout-help'
						)}
						label={Liferay.Language.get(
							'text-embedding-cache-timeout'
						)}
						name={`${namespace}textEmbeddingCacheTimeout`}
						onBlur={_handleInputBlur('textEmbeddingCacheTimeout')}
						onChange={_handleInputChange(
							'textEmbeddingCacheTimeout'
						)}
						options={{min: 0}}
						required
						touched={formik.touched.textEmbeddingCacheTimeout}
						type="number"
						value={formik.values.textEmbeddingCacheTimeout}
					/>
				</div>
			</>
		);
	};

	return (
		<div className="semantic-search-settings-root">
			{_renderEmbeddingProviderConfigurationInputs(0)}

			<SubmitWarningModal
				message={Liferay.Language.get(
					'unsuccessful-connection-warning'
				)}
				onClose={_handleSubmitWarningModalClose}
				onSubmit={_handleSubmitWarningModalSave}
				visible={showSubmitWarningModal}
			/>

			<input
				name={`${namespace}textEmbeddingProviderConfigurationJSONs`}
				type="hidden"
				value={formik.values.textEmbeddingProviderConfigurationJSONs
					.map((configurationObject) =>
						JSON.stringify(configurationObject)
					)
					.join('|')}
			/>

			{formik.values.textEmbeddingsEnabled &&
				(_isTextEmbeddingsEnabledDirty() ||
					_isProviderConfigurationDirty()) && (
					<ClayAlert displayType="info">
						{Liferay.Language.get('reindex-required-alert')}
					</ClayAlert>
				)}

			<ClayButton.Group spaced>
				<ClayButton
					disabled={formik.isSubmitting}
					onClick={_handleSubmit}
				>
					{formik.isSubmitting && (
						<span className="inline-item inline-item-before">
							<span
								aria-hidden="true"
								className="loading-animation"
							></span>
						</span>
					)}

					{Liferay.Language.get('save')}
				</ClayButton>

				<a className="btn btn-cancel btn-secondary" href={redirectURL}>
					{Liferay.Language.get('cancel')}
				</a>
			</ClayButton.Group>
		</div>
	);
}
