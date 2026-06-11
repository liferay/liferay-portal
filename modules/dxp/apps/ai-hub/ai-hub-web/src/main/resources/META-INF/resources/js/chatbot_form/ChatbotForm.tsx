/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import ClayForm, {ClayInput, ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayMultiSelect from '@clayui/multi-select';
import ClayPanel from '@clayui/panel';
import {Provider} from '@clayui/provider';
import {openToast} from '@liferay/object-js-components-web';
import {InputLocalized} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import './ChatbotForm.scss';
import {getAgentDefinitions} from '../agent_definition_form/services/AgentDefinitionService';
import Toolbar from '../components/ToolBar';
import {
	disassociateChatbotFromAgentDefinition,
	getChatbotDefinition,
	postChatbotDefinition,
	putChatbotAgentDefinitionRelationship,
	putChatbotDefinition,
} from './services/ChatbotService';
import {Chatbot} from './types/Chatbot';

type AgentDefinitionOption = {
	externalReferenceCode: string;
	title: string;
};

function getPortalURL(portalURL: string) {
	if (!portalURL) {
		return '';
	}

	try {
		const url = new URL(portalURL);

		if (url.protocol !== 'http:' && url.protocol !== 'https:') {
			return '';
		}

		return url.toString().replace(/\/$/, '');
	}
	catch {
		return '';
	}
}

function generateEmbedCode(externalReferenceCode: string, portalURL: string) {
	return `
<link href="${portalURL}/documents/d/global/index-css" rel="stylesheet">

<script>
	(function () {
		function loadWidget() {
			if (document.getElementById('aihub-chatbot-widget-script')) {
				return;
			}

			var scriptElement = document.createElement('script');

			scriptElement.id = 'aihub-chatbot-widget-script';
			scriptElement.setAttribute('ai-hub-url', '${portalURL}');
			scriptElement.setAttribute('chatbot-external-reference-code', '${externalReferenceCode}');
			scriptElement.src = '${portalURL}/documents/d/global/index-js';

			document.body.appendChild(scriptElement);
		}

		if (document.readyState === 'loading') {
			document.addEventListener('DOMContentLoaded', loadWidget);
		}
		else {
			loadWidget();
		}
	})();
</script>`;
}

function readFileAsBase64(file: File): Promise<string> {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();

		reader.onerror = () => reject(reader.error);
		reader.onload = () => {
			const dataUrl = reader.result as string;

			resolve(dataUrl.substring(dataUrl.indexOf(',') + 1));
		};

		reader.readAsDataURL(file);
	});
}

export default function ChatbotForm({
	accountEntryExternalReferenceCode,
	avatarAcceptedFileExtensions,
	avatarMaximumFileSize,
	avatarMaximumFileSizeLabel,
	avatarUploadTip,
	backURL,
	externalReferenceCode,
	portalURL,
	readOnly,
}: {
	accountEntryExternalReferenceCode: string;
	avatarAcceptedFileExtensions: string;
	avatarMaximumFileSize: number;
	avatarMaximumFileSizeLabel: string;
	avatarUploadTip: string;
	backURL: string;
	externalReferenceCode: string;
	portalURL: string;
	readOnly: boolean;
}) {
	const [availableAgentDefinitions, setAvailableAgentDefinitions] = useState<
		AgentDefinitionOption[]
	>([]);
	const [agentDefinitionsLoaded, setAgentDefinitionsLoaded] = useState(false);
	const [formData, setFormData] = useState<Chatbot>({} as Chatbot);
	const [
		existingChatbotExternalReferenceCode,
		setExistingChatbotExternalReferenceCode,
	] = useState(externalReferenceCode);
	const [selectedAgentDefinitions, setSelectedAgentDefinitions] = useState<
		AgentDefinitionOption[]
	>([]);
	const [
		originalSelectedAgentDefinitions,
		setOriginalSelectedAgentDefinitions,
	] = useState<AgentDefinitionOption[]>([]);
	const [avatarChanged, setAvatarChanged] = useState(false);
	const [avatarLoading, setAvatarLoading] = useState(false);
	const avatarInputRef = useRef<HTMLInputElement>(null);

	useEffect(() => {
		getAgentDefinitions()
			.then((response) => {
				setAvailableAgentDefinitions(
					(response.items || []).map(
						(item: AgentDefinitionOption) => ({
							externalReferenceCode: item.externalReferenceCode,
							title: item.title,
						})
					)
				);
			})
			.catch(() => {})
			.finally(() => {
				setAgentDefinitionsLoaded(true);
			});
	}, []);

	const handleInputChange = (
		event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
	) => {
		const {name, value} = event.target;

		setFormData((prev) => ({
			...prev,
			[name]: value,
		}));
	};

	const handleSelectAvatar = () => {
		const fileInput = avatarInputRef.current;

		if (fileInput) {
			fileInput.value = '';
			fileInput.click();
		}
	};

	const handleAvatarChange = async (
		event: React.ChangeEvent<HTMLInputElement>
	) => {
		const file = event.target.files?.[0];

		if (!file) {
			return;
		}

		if (avatarMaximumFileSize > 0 && file.size > avatarMaximumFileSize) {
			openToast({
				message: sub(
					Liferay.Language.get(
						'please-enter-a-file-with-a-valid-file-size-no-larger-than-x'
					),
					avatarMaximumFileSizeLabel
				),
				type: 'danger',
			});

			return;
		}

		setAvatarLoading(true);

		try {
			const fileBase64 = await readFileAsBase64(file);

			setFormData((prev) => ({
				...prev,
				avatar: {
					fileBase64,
					mimeType: file.type,
					name: file.name,
				},
				avatarFileName: file.name,
			}));

			setAvatarChanged(true);
		}
		catch (error) {
			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
		finally {
			setAvatarLoading(false);
		}
	};

	const handleClearAvatar = () => {
		if (!formData.avatar) {
			return;
		}

		setFormData((prev) => ({
			...prev,
			avatar: null,
			avatarFileName: undefined,
		}));

		setAvatarChanged(true);
	};

	const handleCopyEmbedCode = () => {
		const code = generateEmbedCode(
			formData.externalReferenceCode,
			getPortalURL(portalURL)
		);

		navigator.clipboard.writeText(code).then(() => {
			openToast({
				message: Liferay.Language.get('copied-to-clipboard'),
				type: 'success',
			});
		});
	};

	const handleSubmit = async () => {
		try {
			const {avatar, ...rest} = formData;

			const payload = {
				...rest,
				r_accountToAIHubChatbots_accountEntryERC:
					accountEntryExternalReferenceCode,
				title:
					formData.title_i18n?.['en_US'] ||
					Object.values(formData.title_i18n ?? {})[0] ||
					'',
				...(avatarChanged && {avatar}),
			};

			let chatbotExternalReferenceCode =
				existingChatbotExternalReferenceCode;

			if (existingChatbotExternalReferenceCode) {
				await putChatbotDefinition(
					existingChatbotExternalReferenceCode,
					payload
				);
			}
			else {
				const created = await postChatbotDefinition(payload);

				chatbotExternalReferenceCode = created.externalReferenceCode;

				setExistingChatbotExternalReferenceCode(
					chatbotExternalReferenceCode
				);

				setFormData((prev) => ({
					...prev,
					externalReferenceCode: chatbotExternalReferenceCode,
				}));
			}

			await Promise.all(
				selectedAgentDefinitions.map((agent) =>
					putChatbotAgentDefinitionRelationship(
						chatbotExternalReferenceCode,
						agent.externalReferenceCode
					)
				)
			);

			// Determine removed agents by comparing with original selection

			const removedAgents = originalSelectedAgentDefinitions.filter(
				(original) =>
					!selectedAgentDefinitions.some(
						(current) =>
							current.externalReferenceCode ===
							original.externalReferenceCode
					)
			);

			await Promise.all(
				removedAgents.map((agent) =>
					disassociateChatbotFromAgentDefinition(
						chatbotExternalReferenceCode,
						agent.externalReferenceCode
					)
				)
			);

			// Update original selection after successful submission

			setOriginalSelectedAgentDefinitions(selectedAgentDefinitions);

			setAvatarChanged(false);

			openToast({
				message: Liferay.Language.get('chatbot-was-saved-successfully'),
				type: 'success',
			});
		}
		catch (error) {
			openToast({
				message:
					(error instanceof Error && error.message) ||
					Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
	};

	useEffect(() => {
		async function fetchFormData() {
			if (!externalReferenceCode) {
				setFormData({
					active: false,
					avatar: undefined,
					description: '',
					disclaimerMessage_i18n: {},
					externalReferenceCode: '',
					introMessage_i18n: {},
					notificationMessage_i18n: {},
					placeholderMessage_i18n: {},
					r_accountToAIHubChatbots_accountEntryERC:
						accountEntryExternalReferenceCode,
					title_i18n: {},
				});

				setSelectedAgentDefinitions([]);
				setOriginalSelectedAgentDefinitions([]);
				setAvatarChanged(false);

				return;
			}

			try {
				const chatbotDefinition = await getChatbotDefinition(
					externalReferenceCode
				);

				const avatarAttachment =
					chatbotDefinition.avatar &&
					typeof chatbotDefinition.avatar === 'object'
						? chatbotDefinition.avatar
						: null;

				setFormData({
					active: chatbotDefinition.active ?? false,
					avatar: avatarAttachment
						? avatarAttachment.id
						: chatbotDefinition.avatar,
					avatarFileName: avatarAttachment?.name,
					description: chatbotDefinition.description,
					disclaimerMessage_i18n:
						chatbotDefinition.disclaimerMessage_i18n,
					externalReferenceCode:
						chatbotDefinition.externalReferenceCode,
					introMessage_i18n: chatbotDefinition.introMessage_i18n,
					notificationMessage_i18n:
						chatbotDefinition.notificationMessage_i18n,
					placeholderMessage_i18n:
						chatbotDefinition.placeholderMessage_i18n,
					r_accountToAIHubChatbots_accountEntryERC:
						chatbotDefinition.r_accountToAIHubChatbots_accountEntryERC,
					title_i18n: chatbotDefinition.title_i18n,
				});

				const agentDefinitions = (
					chatbotDefinition.agentDefinitionsToChatbots ?? []
				).map((a: {externalReferenceCode: string; title: string}) => ({
					externalReferenceCode: a.externalReferenceCode,
					title: a.title,
				}));

				setSelectedAgentDefinitions(agentDefinitions);
				setOriginalSelectedAgentDefinitions(agentDefinitions);
				setAvatarChanged(false);
			}
			catch (error) {
				openToast({
					message: Liferay.Language.get(
						'failed-to-load-chatbot-data'
					),
					type: 'danger',
				});
			}
		}

		fetchFormData();
	}, [accountEntryExternalReferenceCode, externalReferenceCode]);

	return (
		<>
			<Toolbar
				backURL={backURL}
				title={
					externalReferenceCode
						? Liferay.Language.get('edit-chatbot')
						: Liferay.Language.get('create-chatbot')
				}
			>
				<Toolbar.Item>
					<ClayLink
						aria-label={Liferay.Language.get('cancel')}
						borderless
						button
						displayType="secondary"
						href={backURL}
						small
					>
						{Liferay.Language.get('cancel')}
					</ClayLink>
				</Toolbar.Item>

				<Toolbar.Item>
					<Button
						aria-label={Liferay.Language.get('save')}
						data-title="Save Button"
						data-title-set-as-html
						disabled={readOnly}
						onClick={handleSubmit}
						size="sm"
					>
						{Liferay.Language.get('save')}
					</Button>
				</Toolbar.Item>
			</Toolbar>

			<ClayLayout.ContainerFluid className="chatbot-form">
				<ClayForm>
					<ClayLayout.Row>
						<ClayLayout.Col md={12}>
							<ClayPanel
								className="chatbot-details"
								collapsable={false}
								title={Liferay.Language.get('details')}
							>
								<ClayPanel.Body>
									<div className="chatbot-details-header">
										<h2>
											{Liferay.Language.get('details')}
										</h2>

										<ClayToggle
											disabled={readOnly}
											label={Liferay.Language.get(
												'enable-chatbot'
											)}
											name="enabled-toggle"
											onBlur={(
												event: React.FocusEvent<HTMLInputElement>
											) => {
												event.stopPropagation();
											}}
											onToggle={() =>
												setFormData((prev) => ({
													...prev,
													active: !prev.active,
												}))
											}
											toggled={formData.active}
										/>
									</div>

									<ClayForm.Group>
										<InputLocalized
											disabled={readOnly}
											id="title"
											label={Liferay.Language.get(
												'title'
											)}
											name="title_i18n"
											onChange={(value) =>
												setFormData((prev) => ({
													...prev,
													title_i18n: value,
												}))
											}
											onSelectedLocaleChange={() => {}}
											placeholder={Liferay.Language.get(
												'title'
											)}
											required={true}
											translations={
												(formData.title_i18n as LocalizedValue<string>) ||
												{}
											}
										/>
									</ClayForm.Group>

									<ClayForm.Group>
										<label htmlFor="externalReferenceCode">
											{Liferay.Language.get(
												'external-reference-code'
											)}

											<span className="ml-1 reference-mark text-warning">
												<ClayIcon symbol="asterisk" />
											</span>
										</label>

										<ClayInput
											disabled={readOnly}
											id="externalReferenceCode"
											name="externalReferenceCode"
											onChange={handleInputChange}
											placeholder={Liferay.Language.get(
												'external-reference-code'
											)}
											required={true}
											type="text"
											value={
												formData.externalReferenceCode ??
												''
											}
										/>
									</ClayForm.Group>

									<ClayForm.Group>
										<label htmlFor="description">
											{Liferay.Language.get(
												'description'
											)}
										</label>

										<textarea
											className="form-control"
											disabled={readOnly}
											id="description"
											name="description"
											onChange={handleInputChange}
											placeholder={Liferay.Language.get(
												'description'
											)}
											rows={3}
											value={formData.description ?? ''}
										/>
									</ClayForm.Group>

									<ClayForm.Group>
										<label htmlFor="avatar">
											{Liferay.Language.get('avatar')}
										</label>

										<div className="chatbot-avatar">
											<Button
												aria-label={sub(
													Liferay.Language.get(
														'select-x'
													),
													Liferay.Language.get(
														'avatar'
													)
												)}
												disabled={
													avatarLoading || readOnly
												}
												displayType="secondary"
												onClick={handleSelectAvatar}
												small
											>
												{avatarLoading && (
													<span
														aria-hidden="true"
														className="loading-animation loading-animation-sm mr-2"
													/>
												)}

												{Liferay.Language.get(
													'select-file'
												)}
											</Button>

											{formData.avatar && (
												<>
													<span>
														{formData.avatarFileName ||
															Liferay.Language.get(
																'current-file'
															)}
													</span>

													<Button
														disabled={readOnly}
														displayType="danger"
														onClick={
															handleClearAvatar
														}
														small
													>
														{Liferay.Language.get(
															'clear'
														)}
													</Button>
												</>
											)}

											<input
												accept={avatarAcceptedFileExtensions
													.split(',')
													.map(
														(extension) =>
															`.${extension.trim()}`
													)
													.join(',')}
												id="avatar"
												onChange={handleAvatarChange}
												ref={avatarInputRef}
												style={{display: 'none'}}
												type="file"
											/>
										</div>

										<small className="form-text text-secondary">
											{avatarUploadTip}
										</small>
									</ClayForm.Group>

									<ClayForm.Group>
										<label>
											{Liferay.Language.get(
												'assigned-agents'
											)}
										</label>

										{agentDefinitionsLoaded && (
											<ClayMultiSelect
												allowDuplicateValues={false}
												allowsCustomLabel={false}
												disabled={readOnly}
												inputName="assignedAgents"
												items={selectedAgentDefinitions}
												locator={{
													label: 'title',
													value: 'externalReferenceCode',
												}}
												onItemsChange={(items) => {
													setSelectedAgentDefinitions(
														items
													);
												}}
												sourceItems={
													availableAgentDefinitions
												}
												spritemap={
													Liferay.Icons.spritemap
												}
											/>
										)}
									</ClayForm.Group>

									<ClayForm.Group>
										<InputLocalized
											disabled={readOnly}
											id="notificationMessage"
											label={Liferay.Language.get(
												'notification-message'
											)}
											name="notificationMessage_i18n"
											onChange={(value) =>
												setFormData((prev) => ({
													...prev,
													notificationMessage_i18n:
														value,
												}))
											}
											onSelectedLocaleChange={() => {}}
											placeholder={Liferay.Language.get(
												'notification-message'
											)}
											translations={
												(formData.notificationMessage_i18n as LocalizedValue<string>) ||
												{}
											}
										/>
									</ClayForm.Group>

									<ClayForm.Group>
										<InputLocalized
											disabled={readOnly}
											id="placeholderMessage"
											label={Liferay.Language.get(
												'placeholder-message'
											)}
											name="placeholderMessage_i18n"
											onChange={(value) =>
												setFormData((prev) => ({
													...prev,
													placeholderMessage_i18n:
														value,
												}))
											}
											onSelectedLocaleChange={() => {}}
											placeholder={Liferay.Language.get(
												'placeholder-message'
											)}
											translations={
												(formData.placeholderMessage_i18n as LocalizedValue<string>) ||
												{}
											}
										/>
									</ClayForm.Group>

									<ClayForm.Group>
										<InputLocalized
											disabled={readOnly}
											id="introMessage"
											label={Liferay.Language.get(
												'intro-message'
											)}
											name="introMessage_i18n"
											onChange={(value) =>
												setFormData((prev) => ({
													...prev,
													introMessage_i18n: value,
												}))
											}
											onSelectedLocaleChange={() => {}}
											placeholder={Liferay.Language.get(
												'intro-message'
											)}
											translations={
												(formData.introMessage_i18n as LocalizedValue<string>) ||
												{}
											}
										/>
									</ClayForm.Group>

									<ClayForm.Group>
										<InputLocalized
											disabled={readOnly}
											id="disclaimerMessage"
											label={Liferay.Language.get(
												'disclaimer-message'
											)}
											name="disclaimerMessage_i18n"
											onChange={(value) =>
												setFormData((prev) => ({
													...prev,
													disclaimerMessage_i18n:
														value,
												}))
											}
											onSelectedLocaleChange={() => {}}
											placeholder={Liferay.Language.get(
												'disclaimer-message'
											)}
											translations={
												(formData.disclaimerMessage_i18n as LocalizedValue<string>) ||
												{}
											}
										/>
									</ClayForm.Group>
								</ClayPanel.Body>
							</ClayPanel>

							<div className="chatbot-code-card">
								<div className="chatbot-code-header">
									<strong>
										{Liferay.Language.get('chatbot-code')}
									</strong>

									<Provider
										spritemap={Liferay.Icons.spritemap}
									>
										<ClayIcon
											className="chatbot-help-icon"
											symbol="question-circle"
										/>
									</Provider>

									<button
										className="chatbot-code-copy"
										disabled={!getPortalURL(portalURL)}
										onClick={handleCopyEmbedCode}
										type="button"
									>
										<Provider
											spritemap={Liferay.Icons.spritemap}
										>
											<ClayIcon symbol="copy" />
										</Provider>
									</button>
								</div>

								<ClayForm.Group>
									<label>
										{Liferay.Language.get('description')}
									</label>

									<textarea
										className="chatbot-code-textarea form-control"
										readOnly
										value={
											formData.externalReferenceCode &&
											getPortalURL(portalURL)
												? generateEmbedCode(
														formData.externalReferenceCode,
														getPortalURL(portalURL)
													)
												: ''
										}
									/>
								</ClayForm.Group>
							</div>
						</ClayLayout.Col>
					</ClayLayout.Row>
				</ClayForm>
			</ClayLayout.ContainerFluid>
		</>
	);
}
