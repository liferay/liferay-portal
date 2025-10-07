/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import {
	API,
	ManagementToolbar,
	constantsUtils,
	invalidateRequired,
	openToast,
	useForm,
} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import {ILearnResourceContext} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {defaultLanguageId} from '../util/constants';
import {BasicInfoContainer} from './BasicInfoContainer/BasicInfoContainer';
import ContentContainer from './ContentContainer/ContentContainer';
import DefinitionOfTermsContainer from './DefinitionOfTermsContainer/DefinitionOfTermsContainer';
import {SettingsContainer} from './SettingsContainer/SettingsContainer';

import './EditNotificationTemplate.scss';

const HEADERS = new Headers({
	'Accept': 'application/json',
	'Content-Type': 'application/json',
});

export type NotificationTemplateError = {
	bcc?: string;
	body?: string;
	cc?: string;
	description?: string;
	from?: string;
	fromName?: string;
	name?: string;
	subject?: string;
	to?: string;
	type?: string;
};

interface EditNotificationTemplateProps {
	backURL: string;
	baseResourceURL: string;
	editorConfig: object;
	externalReferenceCode: string;
	learnResources: ILearnResourceContext;
	notificationTemplateId: number;
	notificationTemplateType: 'email' | 'userNotification' | '';
	portletNamespace: string;
}

export function validate(values: NotificationTemplate) {
	const errors: NotificationTemplateError = {};

	if (!values.name) {
		errors.name = constantsUtils.REQUIRED_MSG;
	}

	if (!values.subject[defaultLanguageId]) {
		errors.subject = Liferay.Language.get('required');
	}

	if (values.type === 'email') {
		const [recipient] = values.recipients as EmailRecipients[];

		if (!recipient.from) {
			errors.from = constantsUtils.REQUIRED_MSG;
		}

		if (!recipient.fromName[defaultLanguageId]) {
			errors.fromName = constantsUtils.REQUIRED_MSG;
		}

		if (recipient.toType !== 'subscribers' && recipient.toType !== 'term') {
			if (
				!Array.isArray(recipient.to) &&
				!(recipient.to as LocalizedValue<string>)[defaultLanguageId]
			) {
				errors.to = constantsUtils.REQUIRED_MSG;
			}

			if (Array.isArray(recipient.to) && !recipient.to.length) {
				errors.to = constantsUtils.REQUIRED_MSG;
			}
		}

		if (recipient.toType === 'term' && !recipient.to) {
			errors.to = constantsUtils.REQUIRED_MSG;
		}
	}

	return errors;
}

export default function EditNotificationTemplate({
	backURL,
	baseResourceURL,
	editorConfig,
	externalReferenceCode,
	learnResources,
	notificationTemplateId = 0,
	notificationTemplateType,
	portletNamespace,
}: EditNotificationTemplateProps) {
	notificationTemplateId = Number(notificationTemplateId);

	const [isSubmitted, setIsSubmitted] = useState(false);

	const [objectDefinitions, setObjectDefinitions] = useState<
		ObjectDefinition[]
	>([]);

	const [selectedLocale, setSelectedLocale] = useState<Locale>(
		Liferay.ThemeDisplay.getDefaultLanguageId
	);

	const [templateTitle, setTemplateTitle] = useState<string>('');

	const onSubmit = async (notification: NotificationTemplate) => {
		if (isSubmitted) {
			return;
		}

		const notificationValue = {...notification};

		setIsSubmitted(true);

		const response = await fetch(
			notificationTemplateId !== 0
				? `/o/notification/v1.0/notification-templates/${notificationTemplateId}`
				: '/o/notification/v1.0/notification-templates',
			{
				body: JSON.stringify(notificationValue),
				headers: HEADERS,
				method: notificationTemplateId !== 0 ? 'PUT' : 'POST',
			}
		);

		if (response.ok) {
			openToast({
				message: Liferay.Language.get(
					'notification-template-was-saved-successfully'
				),
				type: 'success',
			});

			window.location.assign(document.referrer);
		}
		else if (response.status === 400) {
			const {title} = (await response.json()) as {
				title: string;
			};

			openToast({
				message: title,
				type: 'danger',
			});
		}
		else {
			openToast({
				message: Liferay.Language.get('an-error-occurred'),
				type: 'danger',
			});
		}
	};

	let recipientInitialValue: any;

	if (
		notificationTemplateType === '' ||
		notificationTemplateType === 'email'
	) {
		recipientInitialValue = [
			{
				bcc: '',
				bccType: 'email',
				cc: '',
				ccType: 'email',
				from: '',
				fromName: {
					[defaultLanguageId]: '',
				},
				singleRecipient: false,
				to: {
					[defaultLanguageId]: '',
				},
				toType: 'email',
			} as EmailRecipients,
		];
	}
	else {
		recipientInitialValue = [];
	}

	const initialValues: NotificationTemplate = {
		attachmentObjectFieldIds: [],
		body: {
			[defaultLanguageId]: '',
		},
		description: '',
		editorType: 'richText' as EditorTypeOptions,
		externalReferenceCode: '',
		name: '',
		objectDefinitionExternalReferenceCode: '',
		objectDefinitionId: 0,
		recipientType:
			notificationTemplateType === 'userNotification' ? 'term' : 'email',
		recipients: recipientInitialValue,
		subject: {
			[defaultLanguageId]: '',
		},
		system: false,
		type: notificationTemplateType,
	};

	const {errors, setValues, validateSubmit, values} = useForm({
		initialValues,
		onSubmit,
		validate,
	});

	useEffect(() => {
		const makeFetch = async () => {
			if (notificationTemplateId !== 0) {
				const {
					attachmentObjectFieldIds,
					body,
					description,
					editorType,
					externalReferenceCode,
					name,
					objectDefinitionExternalReferenceCode,
					objectDefinitionId,
					recipientType,
					recipients,
					subject,
					system,
					type,
				} = await API.getNotificationTemplateById(
					notificationTemplateId
				);

				let newRecipients = recipients;

				if (type === 'email') {
					newRecipients = [
						{
							...recipients[0],
							bcc: recipients[0].bcc ?? '',
							bccType: recipients[0].bccType ?? 'email',
							cc: recipients[0].cc ?? '',
							ccType: recipients[0].ccType ?? 'email',
							toType: recipients[0].toType ?? 'email',
						},
					];
				}

				setValues({
					...values,
					attachmentObjectFieldIds,
					body,
					description,
					editorType,
					externalReferenceCode,
					name,
					objectDefinitionExternalReferenceCode,
					objectDefinitionId,
					recipientType,
					recipients: newRecipients,
					subject,
					system,
					type,
				});

				setTemplateTitle(name);
			}
			else {
				setTemplateTitle(
					Liferay.Language.get('untitled-notification-template')
				);
			}

			const objectDefinitionsItems = await API.getObjectDefinitions();

			setObjectDefinitions(objectDefinitionsItems);
		};

		makeFetch();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [notificationTemplateId]);

	return (
		<ClayForm>
			<ManagementToolbar
				backURL={backURL}
				badgeClassName={
					values.type === 'email' ? 'label-success' : 'label-info'
				}
				badgeLabel={
					values.type === 'email'
						? Liferay.Language.get('email')
						: Liferay.Language.get('user-notification')
				}
				entityId={notificationTemplateId}
				hasPublishPermission={true}
				hasUpdatePermission={true}
				helpMessage={Liferay.Language.get(
					'internal-key-to-reference-the-notification-template'
				)}
				label={templateTitle}
				objectDefinitionExternalReferenceCode={
					invalidateRequired(values.externalReferenceCode)
						? externalReferenceCode
						: values.externalReferenceCode
				}
				objectDefinitionExternalReferenceCodeSaveURL={`/o/notification/v1.0/notification-templates/${notificationTemplateId}`}
				onExternalReferenceCodeChange={(value) => {
					setValues({
						externalReferenceCode: value,
					});
				}}
				onGetEntity={() =>
					API.getNotificationTemplateById(notificationTemplateId)
				}
				onSubmit={validateSubmit}
				portletNamespace={portletNamespace}
				showEntityDetails={notificationTemplateId !== 0}
			/>

			<div className="lfr__notification-template-container">
				<div className="lfr__notification-template-cards">
					<div
						className={classNames(
							{
								row: !(values.type === 'email'),
							},
							{
								'lfr__notification-template-basic-info':
									values.type === 'email',
							}
						)}
					>
						<div
							className={classNames(
								{
									'col-lg-6': !(values.type === 'email'),
								},
								'lfr__notification-template-card'
							)}
						>
							<BasicInfoContainer
								errors={errors}
								setValues={setValues}
								values={values}
							/>
						</div>

						<div
							className={classNames({
								'col-lg-6 lfr__notification-template-card': !(
									values.type === 'email'
								),
							})}
						>
							<SettingsContainer
								baseResourceURL={baseResourceURL}
								errors={errors}
								learnResources={learnResources}
								selectedLocale={selectedLocale}
								setValues={setValues}
								values={values}
							/>
						</div>
					</div>

					<ContentContainer
						baseResourceURL={baseResourceURL}
						editorConfig={editorConfig}
						errors={errors}
						objectDefinitions={objectDefinitions}
						selectedLocale={selectedLocale}
						setSelectedLocale={setSelectedLocale}
						setValues={setValues}
						values={values}
					/>

					<DefinitionOfTermsContainer
						baseResourceURL={baseResourceURL}
						objectDefinitions={objectDefinitions}
					/>
				</div>
			</div>
		</ClayForm>
	);
}
