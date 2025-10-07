/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayPanel from '@clayui/panel';
import ClayTabs from '@clayui/tabs';
import classNames from 'classnames';
import {InputLocalized, openModal} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import RequiredMark from '../../../components/RequiredMark';
import Search from '../../../components/Search';
import ValidationFeedback from '../../../components/ValidationFeedback';
import {
	DEFAULT_FETCH_HEADERS,
	OBJECT_RELATIONSHIP,
} from '../../../utils/constants';
import getDataSetResourceURL from '../../../utils/getDataSetResourceURL';
import openDefaultFailureToast from '../../../utils/openDefaultFailureToast';
import openDefaultSuccessToast from '../../../utils/openDefaultSuccessToast';
import {IDataSet} from '../../../utils/types';
import {EActionTarget, EActionType, IAction} from '../Actions';

enum EAsyncActionMethod {
	DELETE = 'DELETE',
	GET = 'GET',
	PATCH = 'PATCH',
	POST = 'POST',
	PUT = 'PUT',
}

const ACTION_TARGETS = [
	{
		label: Liferay.Language.get('link'),
		value: EActionTarget.LINK,
	},
	{
		label: Liferay.Language.get('modal'),
		value: EActionTarget.MODAL,
	},
	{
		label: Liferay.Language.get('side-panel'),
		value: EActionTarget.SIDEPANEL,
	},
];

const ITEM_ACTION_TARGETS = [
	{
		label: Liferay.Language.get('async'),
		value: EActionTarget.ASYNC,
	},
	{
		label: Liferay.Language.get('headless'),
		value: EActionTarget.HEADLESS,
	},
].concat(ACTION_TARGETS);

const MESSAGE_TYPES = [
	{
		label: Liferay.Language.get('info'),
		value: 'info',
	},
	{
		label: Liferay.Language.get('secondary'),
		value: 'secondary',
	},
	{
		label: Liferay.Language.get('success'),
		value: 'success',
	},
	{
		label: Liferay.Language.get('danger'),
		value: 'danger',
	},
	{
		label: Liferay.Language.get('warning'),
		value: 'warning',
	},
];

const MODAL_SIZES = [
	{
		label: Liferay.Language.get('full-screen'),
		value: 'full-screen',
	},
	{
		label: Liferay.Language.get('large'),
		value: 'lg',
	},
	{
		label: Liferay.Language.get('small'),
		value: 'sm',
	},
];

const translationExists = ({translations}: {translations: any}) => {
	return Boolean(Object.keys(translations).find((key) => translations[key]));
};

const ActionForm = ({
	activeTab,
	dataSet,
	editing = false,
	initialValues,
	namespace,
	onCancel,
	onSave,
	spritemap,
}: {
	activeTab: number;
	dataSet: IDataSet;
	editing?: boolean;
	initialValues?: IAction;
	namespace: string;
	onCancel: () => void;
	onSave: () => void;
	spritemap: string;
}) => {
	const [activeMessageTab, setActiveMessageTab] = useState(0);
	const [availableIconSymbols, setAvailableIconSymbols] = useState<
		Array<{label: string; value: string}>
	>([]);
	const [
		confirmationMessageTranslations,
		setConfirmationMessageTranslations,
	] = useState(initialValues?.confirmationMessage_i18n ?? {});
	const [errorMessageTranslations, setErrorMessageTranslations] = useState(
		initialValues?.errorMessage_i18n ?? {}
	);
	const [labelTranslations, setLabelTranslations] = useState(
		initialValues?.label_i18n ?? {}
	);
	const [requestBodyValidationError, setRequestBodyValidationError] =
		useState(false);
	const [labelValidationError, setLabelValidationError] = useState(false);
	const [permissionKeyValidationError, setPermissionKeyValidationError] =
		useState(false);
	const [saveButtonDisabled, setSaveButtonDisabled] = useState(false);
	const [successMessageTranslations, setSuccessMessageTranslations] =
		useState(initialValues?.successMessage_i18n ?? {});
	const [titleTranslations, setTitleTranslations] = useState(
		initialValues?.title_i18n ?? {}
	);
	const [urlValidationError, setURLValidationError] = useState(false);

	const [actionData, setActionData] = useState({
		confirmationMessage: initialValues?.confirmationMessage ?? '',
		confirmationMessageType:
			initialValues?.confirmationMessageType ?? 'warning',
		icon: initialValues?.icon ?? '',
		label: initialValues?.label ?? '',
		method: initialValues?.method ?? '',
		modalSize: initialValues?.modalSize ?? '',
		permissionKey: initialValues?.permissionKey ?? '',
		requestBody: initialValues?.requestBody ?? '',
		target: initialValues?.target ?? 'link',
		title: initialValues?.title ?? '',
		type: initialValues?.type ?? '',
		url: initialValues?.url ?? '',
	} as IAction);

	const isRequestBodyInputValid = (value: string | undefined) => {
		if (!value) {
			return true;
		}

		if (!value.match(/{[^}]*}/)) {
			return false;
		}

		try {
			JSON.parse(value);

			return true;
		}
		catch {
			return false;
		}
	};

	const onActionTargetChange = (event: any) => {
		const target = event.target.value;

		setActionData({
			...actionData,
			method:
				target === EActionTarget.ASYNC ? EAsyncActionMethod.DELETE : '',
			modalSize:
				target === EActionTarget.MODAL ? MODAL_SIZES[0].value : '',
			target,
		});

		if (target !== EActionTarget.HEADLESS) {
			setPermissionKeyValidationError(false);
		}
	};

	const saveAction = async () => {
		setSaveButtonDisabled(true);

		const {
			confirmationMessageType,
			icon,
			method,
			modalSize,
			permissionKey,
			requestBody,
			target,
			url,
		} = actionData;

		const type: string =
			activeTab === 0 ? EActionType.ITEM : EActionType.CREATION;

		const body = {
			confirmationMessage_i18n: confirmationMessageTranslations,
			icon,
			label_i18n: labelTranslations,
			method,
			modalSize,
			permissionKey,
			requestBody,
			target,
			title_i18n: titleTranslations,
			type,
			url,
		} as any;

		if (Object.keys(confirmationMessageTranslations).length) {
			body.confirmationMessageType = confirmationMessageType;
		}

		if (
			actionData.target === EActionTarget.ASYNC ||
			actionData.target === EActionTarget.HEADLESS
		) {
			body.errorMessage_i18n = errorMessageTranslations;
			body.successMessage_i18n = successMessageTranslations;
		}

		if (actionData.target === EActionTarget.ASYNC) {
			body.method = method;
		}

		let apiURL;
		let fetchMethod;

		if (editing) {
			apiURL = getDataSetResourceURL({
				dataSetERC: dataSet.externalReferenceCode,
				relatedResourceERC: initialValues?.externalReferenceCode,
				relationship: OBJECT_RELATIONSHIP.DATA_SET_ACTIONS,
			});

			fetchMethod = 'PATCH';
		}
		else {
			apiURL = getDataSetResourceURL({
				dataSetERC: dataSet.externalReferenceCode,
				relationship: OBJECT_RELATIONSHIP.DATA_SET_ACTIONS,
			});

			fetchMethod = 'POST';
		}

		const response = await fetch(apiURL, {
			body: JSON.stringify(body),
			headers: DEFAULT_FETCH_HEADERS,
			method: fetchMethod,
		});

		if (!response.ok) {
			setSaveButtonDisabled(false);

			openDefaultFailureToast();

			return;
		}

		setSaveButtonDisabled(false);

		openDefaultSuccessToast();

		onSave();
	};

	const validate = () => {
		let valid: boolean = true;

		const {permissionKey, requestBody, target, url} = actionData;

		if (
			!translationExists({
				translations: labelTranslations,
			})
		) {
			valid = false;

			setLabelValidationError(true);
		}

		if (!url && target !== EActionTarget.HEADLESS) {
			valid = false;

			setURLValidationError(true);
		}

		if (
			target === EActionTarget.ASYNC ||
			target === EActionTarget.HEADLESS
		) {
			if (!isRequestBodyInputValid(requestBody)) {
				valid = false;

				setRequestBodyValidationError(true);
			}
		}

		if (!permissionKey && target === EActionTarget.HEADLESS) {
			valid = false;

			setPermissionKeyValidationError(true);
		}

		return valid;
	};

	useEffect(() => {
		const getIcons = async () => {
			const response = await fetch(spritemap);

			const responseText = await response.text();

			if (responseText.length) {
				const spritemapDocument = new DOMParser().parseFromString(
					responseText,
					'text/xml'
				);

				const symbolElements =
					spritemapDocument.querySelectorAll('symbol');

				const iconSymbols = Array.from(symbolElements!).map(
					(element) => ({
						label: element.id,
						value: element.id,
					})
				);

				setAvailableIconSymbols(iconSymbols);
			}
		};

		getIcons();
	}, [spritemap]);

	const confirmationMessageFormElementId = `${namespace}ConfirmationMessage`;
	const confirmationMessageTypeFormElementId = `${namespace}ConfirmationMessageType`;
	const errorMessageFormElementId = `${namespace}ErrorMessage`;
	const iconFormElementId = `${namespace}Icon`;
	const labelFormElementId = `${namespace}Label`;
	const methodFormElementId = `${namespace}Method`;
	const modalSizeFormElementId = `${namespace}ModalSize`;
	const permissionKeyFormElementId = `${namespace}PermissionKey`;
	const requestBodyFormElementId = `${namespace}RequestBody`;
	const successMessageFormElementId = `${namespace}SuccessMessage`;
	const titleFormElementId = `${namespace}Title`;
	const typeFormElementId = `${namespace}Type`;
	const urlFormElementId = `${namespace}URL`;

	const ModalBody = ({closeModal}: {closeModal: Function}) => {
		const [filteredIconSymbols, setFilteredIconSymbols] =
			useState<Array<{label: string; value: string}>>(
				availableIconSymbols
			);
		const [query, setQuery] = useState('');

		const onSearch = (query: string) => {
			setQuery(query);

			const regexp = new RegExp(query, 'i');

			setFilteredIconSymbols(
				query
					? availableIconSymbols.filter((item) =>
							String(item.value).match(regexp)
						)
					: availableIconSymbols
			);
		};

		return (
			<>
				<Search onSearch={onSearch} query={query} />

				<ClayLayout.SheetSection>
					<ul className="list-unstyled mt-4 row">
						{filteredIconSymbols.map((item) => {
							return (
								<li
									className="col-md-4"
									key={item.value}
									onClick={() => {
										setActionData({
											...actionData,
											icon: item.value,
										});
										closeModal();
									}}
								>
									<ClayButton
										borderless
										displayType="secondary"
										size="sm"
									>
										<ClayIcon
											className="mr-2"
											spritemap={spritemap}
											symbol={item.value}
										/>

										{item.label}
									</ClayButton>
								</li>
							);
						})}
					</ul>
				</ClayLayout.SheetSection>
			</>
		);
	};

	return (
		<>
			<h2 className="mb-0 p-4">
				{editing && initialValues?.label}

				{!editing &&
					activeTab === 0 &&
					Liferay.Language.get('new-item-action')}

				{!editing &&
					activeTab === 1 &&
					Liferay.Language.get('new-creation-action')}
			</h2>

			<ClayPanel
				collapsable
				defaultExpanded
				displayTitle={Liferay.Language.get('display-options')}
				displayType="unstyled"
			>
				<ClayPanel.Body>
					<ClayLayout.Row>
						<ClayLayout.Col size={8}>
							<InputLocalized
								error={
									labelValidationError
										? Liferay.Language.get(
												'this-field-is-required'
											)
										: undefined
								}
								id={labelFormElementId}
								label={Liferay.Language.get('label')}
								onChange={(translations) => {
									setLabelTranslations(translations);

									setLabelValidationError(
										!translationExists({
											translations,
										})
									);
								}}
								placeholder={Liferay.Language.get(
									'action-name'
								)}
								required
								translations={labelTranslations}
							/>
						</ClayLayout.Col>

						<ClayLayout.Col size={4}>
							<ClayForm.Group>
								<label htmlFor={iconFormElementId}>
									{Liferay.Language.get('icon')}
								</label>

								<ClayInput.Group>
									<ClayInput.GroupItem prepend shrink>
										<ClayInput.GroupText>
											<ClayIcon
												spritemap={spritemap}
												symbol={actionData.icon}
											/>
										</ClayInput.GroupText>
									</ClayInput.GroupItem>

									<ClayInput.GroupItem append>
										<ClayInput
											onChange={({target: {value}}) =>
												setActionData({
													...actionData,
													icon: value,
												})
											}
											placeholder={Liferay.Language.get(
												'no-icon-selected'
											)}
											type="text"
											value={actionData.icon}
										/>
									</ClayInput.GroupItem>

									<ClayButtonWithIcon
										aria-label={
											actionData.icon
												? Liferay.Language.get(
														'change-icon'
													)
												: Liferay.Language.get(
														'add-icon'
													)
										}
										className="ml-2"
										displayType="secondary"
										id={iconFormElementId}
										onClick={() =>
											openModal({
												bodyComponent: ModalBody,
												containerProps: {
													className:
														'dsm-actions-icon-selection-modal',
												},
												size: 'lg',
												title: Liferay.Language.get(
													'select-an-icon'
												),
											})
										}
										symbol={
											actionData.icon !== ''
												? 'change'
												: 'plus'
										}
									/>

									{actionData.icon !== '' && (
										<ClayButtonWithIcon
											aria-label={Liferay.Language.get(
												'remove-icon'
											)}
											className="ml-2"
											displayType="secondary"
											onClick={() =>
												setActionData({
													...actionData,
													icon: '',
												})
											}
											symbol="trash"
										/>
									)}
								</ClayInput.Group>
							</ClayForm.Group>
						</ClayLayout.Col>
					</ClayLayout.Row>
				</ClayPanel.Body>
			</ClayPanel>

			<ClayPanel
				collapsable
				defaultExpanded
				displayTitle={Liferay.Language.get('action-behavior')}
				displayType="unstyled"
			>
				<ClayPanel.Body>
					<ClayLayout.Row justify="start">
						<ClayLayout.Col size={4}>
							<ClayForm.Group>
								<label htmlFor={typeFormElementId}>
									{Liferay.Language.get('type')}

									<RequiredMark />
								</label>

								<ClaySelectWithOption
									disabled={editing}
									id={typeFormElementId}
									onChange={onActionTargetChange}
									options={
										activeTab === 0
											? ITEM_ACTION_TARGETS
											: ACTION_TARGETS
									}
									placeholder={Liferay.Language.get(
										'please-select-an-option'
									)}
									value={actionData.target}
								/>
							</ClayForm.Group>
						</ClayLayout.Col>

						{actionData.target === EActionTarget.ASYNC && (
							<ClayLayout.Col size={4}>
								<ClayForm.Group>
									<label htmlFor={methodFormElementId}>
										{Liferay.Language.get('method')}

										<RequiredMark />
									</label>

									<ClaySelectWithOption
										id={methodFormElementId}
										onChange={(event) =>
											setActionData({
												...actionData,
												method: event.target.value,
											})
										}
										options={Object.values(
											EAsyncActionMethod
										).map((method) => ({
											label: method,
											value: method,
										}))}
										placeholder={Liferay.Language.get(
											'please-select-an-option'
										)}
										value={actionData.method}
									/>
								</ClayForm.Group>
							</ClayLayout.Col>
						)}

						{actionData.target === EActionTarget.MODAL && (
							<ClayLayout.Col size={4}>
								<ClayForm.Group>
									<label htmlFor={modalSizeFormElementId}>
										{Liferay.Language.get('variant')}

										<RequiredMark />
									</label>

									<ClaySelectWithOption
										id={modalSizeFormElementId}
										onChange={(event) =>
											setActionData({
												...actionData,
												modalSize: event.target.value,
											})
										}
										options={MODAL_SIZES}
										placeholder={Liferay.Language.get(
											'please-select-an-option'
										)}
										value={actionData.modalSize}
									/>
								</ClayForm.Group>
							</ClayLayout.Col>
						)}
					</ClayLayout.Row>

					{(actionData.target === EActionTarget.MODAL ||
						actionData.target === EActionTarget.SIDEPANEL) && (
						<ClayLayout.Row>
							<ClayLayout.Col>
								<InputLocalized
									helpMessage={Liferay.Language.get(
										'side-panel-title-help'
									)}
									id={titleFormElementId}
									label={Liferay.Language.get('title')}
									onChange={(translations) => {
										setTitleTranslations(translations);
									}}
									placeholder={
										actionData.target ===
										EActionTarget.MODAL
											? Liferay.Language.get(
													'add-the-title-of-the-modal'
												)
											: Liferay.Language.get(
													'add-the-title-of-the-side-panel'
												)
									}
									translations={titleTranslations}
								/>
							</ClayLayout.Col>
						</ClayLayout.Row>
					)}

					{actionData.target !== EActionTarget.HEADLESS && (
						<ClayLayout.Row justify="start">
							<ClayLayout.Col lg>
								<ClayForm.Group
									className={classNames({
										'has-error': urlValidationError,
									})}
								>
									<label htmlFor={urlFormElementId}>
										{Liferay.Language.get('url')}

										<RequiredMark />
									</label>

									<ClayInput
										component="textarea"
										id={urlFormElementId}
										onChange={(event) => {
											const url = event.target.value;

											setActionData({
												...actionData,
												url,
											});

											setURLValidationError(!url);
										}}
										placeholder={Liferay.Language.get(
											'add-a-url-here'
										)}
										value={actionData.url}
									/>

									{urlValidationError && (
										<ValidationFeedback />
									)}
								</ClayForm.Group>
							</ClayLayout.Col>
						</ClayLayout.Row>
					)}

					{(actionData.target === EActionTarget.HEADLESS ||
						actionData.target === EActionTarget.ASYNC) && (
						<ClayLayout.Row justify="start">
							<ClayLayout.Col lg>
								<ClayForm.Group
									className={classNames({
										'has-error': requestBodyValidationError,
									})}
								>
									<label htmlFor={requestBodyFormElementId}>
										{Liferay.Language.get('request-body')}

										<span
											className="label-icon lfr-portal-tooltip ml-2"
											title={Liferay.Language.get(
												'request-body-help'
											)}
										>
											<ClayIcon symbol="question-circle-full" />
										</span>
									</label>

									<ClayInput
										component="textarea"
										id={requestBodyFormElementId}
										onChange={(event) => {
											const requestBody =
												event.target.value;

											setActionData({
												...actionData,
												requestBody,
											});

											setRequestBodyValidationError(
												!isRequestBodyInputValid(
													requestBody
												)
											);
										}}
										placeholder={Liferay.Language.get(
											'add-a-request-body-here'
										)}
										value={actionData.requestBody}
									/>

									{requestBodyValidationError && (
										<ValidationFeedback
											message={Liferay.Language.get(
												'this-field-must-contain-a-valid-json'
											)}
										/>
									)}
								</ClayForm.Group>
							</ClayLayout.Col>
						</ClayLayout.Row>
					)}

					<ClayLayout.Row justify="start">
						<ClayLayout.Col>
							<ClayForm.Group
								className={classNames({
									'has-error': permissionKeyValidationError,
								})}
							>
								<label htmlFor={permissionKeyFormElementId}>
									{Liferay.Language.get(
										'headless-action-key'
									)}

									{actionData.target ===
										EActionTarget.HEADLESS && (
										<RequiredMark />
									)}

									<span
										className="label-icon lfr-portal-tooltip ml-2"
										title={Liferay.Language.get(
											'headless-action-key-help'
										)}
									>
										<ClayIcon symbol="question-circle-full" />
									</span>
								</label>

								<ClayInput
									id={permissionKeyFormElementId}
									onChange={(event) => {
										const permissionKey =
											event.target.value;

										setActionData({
											...actionData,
											permissionKey,
										});

										if (
											actionData.target ===
											EActionTarget.HEADLESS
										) {
											setPermissionKeyValidationError(
												!permissionKey
											);
										}
									}}
									placeholder={Liferay.Language.get(
										'add-a-value-here'
									)}
									value={actionData.permissionKey}
								/>

								{permissionKeyValidationError && (
									<ValidationFeedback />
								)}
							</ClayForm.Group>
						</ClayLayout.Col>
					</ClayLayout.Row>

					{activeTab === 0 && (
						<ClayLayout.Row>
							<ClayLayout.Col size={8}>
								<ClayForm.Group>
									<InputLocalized
										data-testid="confirmationMessageInput"
										id={confirmationMessageFormElementId}
										label={Liferay.Language.get(
											'confirmation-message'
										)}
										onChange={
											setConfirmationMessageTranslations
										}
										placeholder={Liferay.Language.get(
											'add-a-message-here'
										)}
										tooltip={Liferay.Language.get(
											'the-user-will-see-this-message-before-performing-the-action'
										)}
										translations={
											confirmationMessageTranslations
										}
									/>
								</ClayForm.Group>
							</ClayLayout.Col>

							<ClayLayout.Col size={4}>
								<ClayForm.Group>
									<label
										htmlFor={
											confirmationMessageTypeFormElementId
										}
									>
										{Liferay.Language.get('message-type')}
									</label>

									<ClaySelectWithOption
										id={
											confirmationMessageTypeFormElementId
										}
										onChange={(event) =>
											setActionData({
												...actionData,
												confirmationMessageType:
													event.target.value,
											})
										}
										options={MESSAGE_TYPES}
										value={
											actionData.confirmationMessageType
										}
									/>
								</ClayForm.Group>
							</ClayLayout.Col>
						</ClayLayout.Row>
					)}
				</ClayPanel.Body>
			</ClayPanel>

			{(actionData.target === EActionTarget.ASYNC ||
				actionData.target === EActionTarget.HEADLESS) && (
				<ClayPanel
					collapsable
					defaultExpanded
					displayTitle={Liferay.Language.get('status-messages')}
					displayType="unstyled"
				>
					<ClayPanel.Body>
						<ClayForm.Text className="c-pb-3">
							{Liferay.Language.get(
								'you-can-write-status-messages-related-to-this-action'
							)}
						</ClayForm.Text>

						<ClayTabs
							activation="automatic"
							active={activeMessageTab}
							className="status-messages-tabs"
							onActiveChange={(tab: number) => {
								setActiveMessageTab(tab);
							}}
						>
							<ClayTabs.Item>
								{Liferay.Language.get('success')}
							</ClayTabs.Item>

							<ClayTabs.Item>
								{Liferay.Language.get('error')}
							</ClayTabs.Item>
						</ClayTabs>

						<ClayTabs.Content
							active={activeMessageTab}
							className="status-messages-content"
							fade
						>
							<ClayTabs.TabPane
								aria-label={Liferay.Language.get('success')}
							>
								<ClayForm.Group>
									<InputLocalized
										data-testid="successStatusMessageInput"
										id={successMessageFormElementId}
										label={Liferay.Language.get('message')}
										onChange={setSuccessMessageTranslations}
										placeholder={Liferay.Language.get(
											'add-a-message-here'
										)}
										tooltip={Liferay.Language.get(
											'the-user-will-see-this-message-if-the-action-is-successful'
										)}
										translations={
											successMessageTranslations
										}
									/>
								</ClayForm.Group>
							</ClayTabs.TabPane>

							<ClayTabs.TabPane
								aria-label={Liferay.Language.get('error')}
								className="error-status-message-tab-pane"
							>
								<ClayForm.Group>
									<InputLocalized
										data-testid="errorStatusMessageInput"
										id={errorMessageFormElementId}
										label={Liferay.Language.get('message')}
										onChange={setErrorMessageTranslations}
										placeholder={Liferay.Language.get(
											'add-a-message-here'
										)}
										tooltip={Liferay.Language.get(
											'the-user-will-see-this-message-if-the-action-fails'
										)}
										translations={errorMessageTranslations}
									/>
								</ClayForm.Group>
							</ClayTabs.TabPane>
						</ClayTabs.Content>
					</ClayPanel.Body>
				</ClayPanel>
			)}

			<ClayButton.Group className="pb-4 px-4" spaced>
				<ClayButton
					disabled={saveButtonDisabled}
					onClick={() => {
						const valid = validate();

						if (valid) {
							saveAction();
						}
					}}
				>
					{Liferay.Language.get('save')}
				</ClayButton>

				<ClayButton displayType="secondary" onClick={onCancel}>
					{Liferay.Language.get('cancel')}
				</ClayButton>
			</ClayButton.Group>
		</>
	);
};

export default ActionForm;
