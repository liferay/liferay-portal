/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext, useEffect, useState} from 'react';

import {DefinitionBuilderContext} from '../../../../../../DefinitionBuilderContext';
import {DiagramBuilderContext} from '../../../../../DiagramBuilderContext';
import {getRecipientType} from '../../notifications/utils';
import BaseNotificationsInfo from '../../shared-components/BaseNotificationsInfo';

const ActionTypeNotification = ({
	actionData,
	actionSectionsIndex,
	actionType,
	sectionsLength,
	setActionSections,
	...restProps
}) => {
	const {accountEntryId} = useContext(DefinitionBuilderContext);
	const {selectedItem} = useContext(DiagramBuilderContext);

	const identifier = actionData?.identifier;

	const [internalSections, setInternalSections] = useState([
		{identifier: `${Date.now()}-0`},
	]);

	const [notificationDescription, setNotificationDescription] = useState(
		actionData?.description || ''
	);

	const [notificationName, setNotificationName] = useState(
		actionData?.name || ''
	);

	const [notificationTypeEmail, setNotificationTypeEmail] = useState(
		actionData?.notificationTypes?.some(
			(value) => value.notificationType === 'email'
		) || false
	);

	const [
		notificationTypeUserNotification,
		setNotificationTypeUserNotification,
	] = useState(
		actionData?.notificationTypes?.some(
			(value) => value.notificationType === 'user-notification'
		) || false
	);

	const notificationTypesOptions = [
		{
			children: [
				{
					checked: notificationTypeEmail,
					label: Liferay.Language.get('email'),
					type: 'checkbox',
					value: 'email',
				},
				{
					checked: notificationTypeUserNotification,
					label: Liferay.Language.get('user-notification'),
					type: 'checkbox',
					value: 'userNotification',
				},
			],
			label: '',
			value: 'notificationTypes',
		},
	];

	const [items, setItems] = useState(notificationTypesOptions);

	let recipientTypeHolder;

	if (actionData?.recipients?.length !== 0) {
		if (!actionData?.recipients?.[0]) {
			recipientTypeHolder = getRecipientType(actionData?.recipients);
		}
		else {
			recipientTypeHolder = getRecipientType(actionData?.recipients[0]);
		}
	}
	else {
		recipientTypeHolder = 'assetCreator';
	}

	const [recipientType, setRecipientType] = useState(recipientTypeHolder);

	const [template, setTemplate] = useState(actionData?.template || '');

	const [templateLanguage, setTemplateLanguage] = useState(
		actionData?.templateLanguage || 'freemarker'
	);

	const deleteSection = () => {
		setActionSections((prevSections) => {
			const newSections = prevSections.filter(
				(prevSection) => prevSection.identifier !== identifier
			);

			return newSections;
		});
	};

	const roleRecipientUpdateSelectedItem = (role) => {
		setActionSections((prevSections) => {
			const updatedSections = [...prevSections];

			updatedSections[actionSectionsIndex] = {
				...prevSections[actionSectionsIndex],
				recipients: {
					assignmentType: ['roleId'],
					roleExternalReferenceCode: role.externalReferenceCode,
					roleId: role.id,
					roleType: role.roleType,
					sectionsData: {
						id: role.id,
						name: role.name,
						roleType: role.roleType,
					},
				},
			};

			return updatedSections;
		});
	};

	const roleTypeRecipientUpdateSelectedItem = (values) => {
		setActionSections((prevSections) => {
			const updatedSections = [...prevSections];

			updatedSections[actionSectionsIndex] = {
				...prevSections[actionSectionsIndex],
				recipients: {
					assignmentType: ['roleType'],
					autoCreate: values.map(({autoCreate}) => autoCreate),
					roleName: values.map(({roleName}) => roleName),
					roleType: values.map(({roleType}) => roleType),
				},
			};

			return updatedSections;
		});
	};

	const scriptedRecipientUpdateSelectedItem = ({target}) => {
		setActionSections((prevSections) => {
			const updatedSections = [...prevSections];

			updatedSections[actionSectionsIndex] = {
				...prevSections[actionSectionsIndex],
				recipients: {
					...prevSections[actionSectionsIndex]?.recipients,
					assignmentType: ['scriptedRecipient'],
					script: [target.value],
				},
			};

			return updatedSections;
		});
	};

	const updateNotificationInfo = (item) => {
		if (item.name && item.template && item.notificationTypes.length) {
			setActionSections((previousSections) => {
				const updatedSections = [...previousSections];

				updatedSections[actionSectionsIndex] = {
					...previousSections[actionSectionsIndex],
					...item,
					actionType,
				};

				return updatedSections;
			});
		}
	};

	const updateNotificationType = () => {
		const notificationTypes = [];

		if (notificationTypeEmail) {
			notificationTypes.push({notificationType: 'email'});
		}

		if (notificationTypeUserNotification) {
			notificationTypes.push({
				notificationType: 'user-notification',
			});
		}

		updateNotificationInfo({
			description: notificationDescription,
			name: notificationName,
			notificationTypes,
			template,
			templateLanguage,
		});
	};

	const userRecipientUpdateSelectedItem = (values) => {
		setActionSections((prevSections) => {
			const updatedSections = [...prevSections];

			updatedSections[actionSectionsIndex] = {
				...prevSections[actionSectionsIndex],
				recipients: {
					assignmentType: ['user'],
					emailAddress: values.map(({emailAddress}) => emailAddress),
					sectionsData: values.map((values) => values),
				},
			};

			return updatedSections;
		});
	};

	useEffect(() => {
		if (
			actionData?.recipients &&
			recipientType !== 'assetCreator' &&
			recipientType !== 'taskAssignees'
		) {
			return;
		}

		let recipients = {};

		if (recipientType === 'assetCreator') {
			recipients = {assignmentType: ['user']};
		}
		else if (recipientType === 'taskAssignees') {
			recipients = {assignmentType: ['taskAssignees']};
		}

		setActionSections((prevSections) => {
			const updatedSections = [...prevSections];

			updatedSections[actionSectionsIndex] = {
				...prevSections[actionSectionsIndex],
				recipients,
			};

			return updatedSections;
		});

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [recipientType]);

	useEffect(() => {
		let sectionsData = [];

		const recipients = actionData?.recipients;

		if (recipients && recipientType === 'roleType') {
			if (Array.isArray(recipients.roleName)) {
				for (let i = 0; i < recipients.roleName.length; i++) {
					sectionsData.push({
						autoCreate: recipients.autoCreate?.[i],
						identifier: `${Date.now()}-${i}`,
						roleName: recipients.roleName?.[i],
						roleType: recipients.roleType[i],
					});
				}
			}
			else {
				sectionsData.push({
					autoCreate: recipients.autoCreate,
					identifier: `${Date.now()}-0`,
					roleName: recipients.roleName?.[0],
					roleType: recipients.roleType,
				});
			}
		}
		else if (
			actionData?.recipients?.sectionsData &&
			recipientType === 'user'
		) {
			sectionsData = actionData?.recipients?.sectionsData;
		}

		if (sectionsData.length) {
			setInternalSections(sectionsData);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<BaseNotificationsInfo
			accountEntryId={accountEntryId}
			defaultScript={
				actionData?.recipients?.script ||
				actionData?.recipients?.[0]?.script?.[0]
			}
			defaultScriptLanguage={
				actionData?.recipients?.scriptLanguage ||
				actionData?.recipients?.[0]?.scriptLanguage
			}
			deleteSection={deleteSection}
			handleClickCapture={(scriptLanguage) =>
				setActionSections((prevSections) => {
					const updatedSections = [...prevSections];

					updatedSections[actionSectionsIndex] = {
						...prevSections[actionSectionsIndex],
						recipients: {
							...prevSections?.[actionSectionsIndex]?.recipients,
							scriptLanguage: [scriptLanguage],
						},
					};

					return updatedSections;
				})
			}
			identifier={identifier}
			internalSections={internalSections}
			items={items}
			notificationDescription={notificationDescription}
			notificationIndex={actionSectionsIndex}
			notificationName={notificationName}
			notificationTypeEmail={notificationTypeEmail}
			notificationTypeUserNotification={notificationTypeUserNotification}
			recipientType={recipientType}
			roleRecipientUpdateSelectedItem={roleRecipientUpdateSelectedItem}
			roleTypeRecipientUpdateSelectedItem={
				roleTypeRecipientUpdateSelectedItem
			}
			scriptedRecipientUpdateSelectedItem={
				scriptedRecipientUpdateSelectedItem
			}
			sectionsData={actionData?.recipients?.sectionsData}
			sectionsLength={sectionsLength}
			selectedItem={selectedItem}
			setInternalSections={setInternalSections}
			setItems={setItems}
			setNotificationDescription={setNotificationDescription}
			setNotificationName={setNotificationName}
			setNotificationTypeEmail={setNotificationTypeEmail}
			setNotificationTypeUserNotification={
				setNotificationTypeUserNotification
			}
			setRecipientType={setRecipientType}
			setSections={setActionSections}
			setTemplate={setTemplate}
			setTemplateLanguage={setTemplateLanguage}
			template={template}
			templateLanguage={templateLanguage}
			updateNotificationType={updateNotificationType}
			userRecipientUpdateSelectedItem={userRecipientUpdateSelectedItem}
			{...restProps}
		/>
	);
};

export default ActionTypeNotification;
