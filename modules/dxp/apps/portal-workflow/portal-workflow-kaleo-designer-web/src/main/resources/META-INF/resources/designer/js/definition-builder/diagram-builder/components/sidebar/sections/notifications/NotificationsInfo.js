/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React, {useContext, useEffect, useState} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import SidebarPanel from '../../SidebarPanel';
import BaseNotificationsInfo from '../shared-components/BaseNotificationsInfo';
import {getRecipientType} from './utils';

let executionTypeOptions = [
	{
		label: Liferay.Language.get('on-entry'),
		value: 'onEntry',
	},
	{
		label: Liferay.Language.get('on-exit'),
		value: 'onExit',
	},
];

const NotificationsInfo = ({
	accountEntryId,
	identifier,
	index: notificationIndex,
	sectionsLength,
	setSections,
	...restProps
}) => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	const [executionType, setExecutionType] = useState(
		selectedItem.data.notifications?.executionType?.[notificationIndex] ||
			(selectedItem.type === 'task' ? 'onAssignment' : 'onEntry')
	);

	const [internalSections, setInternalSections] = useState([
		{identifier: `${Date.now()}-0`},
	]);

	const [notificationDescription, setNotificationDescription] = useState(
		selectedItem.data.notifications?.description?.[notificationIndex] || ''
	);

	const [notificationName, setNotificationName] = useState(
		selectedItem.data.notifications?.name?.[notificationIndex] || ''
	);

	const [notificationTypeEmail, setNotificationTypeEmail] = useState(
		selectedItem.data.notifications?.notificationTypes?.[
			notificationIndex
		]?.some((value) => value.notificationType === 'email') || false
	);

	const [
		notificationTypeUserNotification,
		setNotificationTypeUserNotification,
	] = useState(
		selectedItem.data.notifications?.notificationTypes?.[
			notificationIndex
		]?.some((value) => value.notificationType === 'user-notification') ||
			false
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

	if (
		selectedItem.data.notifications?.recipients?.[notificationIndex]
			?.length !== 0
	) {
		if (
			!selectedItem.data.notifications?.recipients?.[
				notificationIndex
			]?.[0]
		) {
			recipientTypeHolder = getRecipientType(
				selectedItem.data.notifications?.recipients?.[notificationIndex]
			);
		}
		else {
			recipientTypeHolder = getRecipientType(
				selectedItem.data.notifications?.recipients?.[
					notificationIndex
				][0]
			);
		}
	}
	else {
		recipientTypeHolder = 'assetCreator';
	}

	const [recipientType, setRecipientType] = useState(recipientTypeHolder);

	const [template, setTemplate] = useState(
		selectedItem.data.notifications?.template?.[notificationIndex] || ''
	);

	const [templateLanguage, setTemplateLanguage] = useState(
		selectedItem.data.notifications?.templateLanguage?.[
			notificationIndex
		] || 'freemarker'
	);

	const deleteSection = () => {
		setSections((prevSections) => {
			const newSections = prevSections.filter(
				(prevSection) => prevSection.identifier !== identifier
			);

			updateSelectedItem(newSections);

			return newSections;
		});
	};

	const roleRecipientUpdateSelectedItem = (role) => {
		setSelectedItem((previousItem) => {
			const newRecipients = [
				...previousItem.data.notifications.recipients,
			];

			newRecipients[notificationIndex][0] = {
				...newRecipients[notificationIndex][0],
				assignmentType: ['roleId'],
				roleExternalReferenceCode: role.externalReferenceCode,
				roleId: role.id,
				roleType: role.roleType,
				sectionsData: {
					id: role.id,
					name: role.name,
					roleType: role.roleType,
				},
			};

			return {
				...previousItem,
				data: {
					...previousItem.data,
					notifications: {
						...previousItem.data.notifications,
						recipients: newRecipients,
					},
				},
			};
		});
	};

	const roleTypeRecipientUpdateSelectedItem = (values) => {
		setSelectedItem((previousItem) => {
			const newRecipients = [
				...previousItem.data.notifications.recipients,
			];

			newRecipients[notificationIndex][0] = {
				...newRecipients[notificationIndex][0],
				assignmentType: ['roleType'],
				autoCreate: values.map(({autoCreate}) => autoCreate),
				roleName: values.map(({roleName}) => roleName),
				roleType: values.map(({roleType}) => roleType),
			};

			return {
				...previousItem,
				data: {
					...previousItem.data,
					notifications: {
						...previousItem.data.notifications,
						recipients: newRecipients,
					},
				},
			};
		});
	};

	const scriptedRecipientUpdateSelectedItem = ({target}) => {
		setSelectedItem((previousItem) => {
			const newRecipients = [
				...previousItem.data.notifications.recipients,
			];

			newRecipients[notificationIndex][0] = {
				...newRecipients[notificationIndex][0],
				assignmentType: ['scriptedRecipient'],
				script: [target.value],
			};

			return {
				...previousItem,
				data: {
					...previousItem.data,
					notifications: {
						...previousItem.data.notifications,
						recipients: newRecipients,
					},
				},
			};
		});
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
			executionType,
			name: notificationName,
			notificationTypes,
			template,
			templateLanguage,
		});
	};

	const updateSelectedItem = (values) => {
		const initialValues = {
			descriptionValues: [],
			executionTypeValues: [],
			nameValues: [],
			notificationTypesValues: [],
			templateLanguageValues: [],
			templateValues: [],
		};

		values.map(
			({
				description,
				executionType,
				name,
				notificationTypes,
				template,
				templateLanguage,
			}) => {
				initialValues.descriptionValues.push(
					description ? description : ''
				);
				initialValues.executionTypeValues.push(
					executionType ? executionType : null
				);
				initialValues.nameValues.push(name ? name : '');
				initialValues.notificationTypesValues.push(
					notificationTypes ? notificationTypes : null
				);
				initialValues.templateLanguageValues.push(
					templateLanguage ? templateLanguage : null
				);
				initialValues.templateValues.push(template ?? null);
			}
		);
		setSelectedItem((previousItem) => ({
			...previousItem,
			data: {
				...previousItem.data,
				notifications: {
					description: initialValues.descriptionValues,
					executionType: initialValues.executionTypeValues,
					name: initialValues.nameValues,
					notificationTypes: initialValues.notificationTypesValues,
					recipients: !previousItem.data.notifications?.recipients
						? [
								[
									{
										assignmentType: ['user'],
									},
								],
							]
						: [...previousItem.data.notifications.recipients],
					template: initialValues.templateValues,
					templateLanguage: initialValues.templateLanguageValues,
				},
			},
		}));
	};

	const updateNotificationInfo = (item) => {
		if (item.name && item.template && item.notificationTypes.length) {
			setSections((prev) => {
				prev[notificationIndex] = {
					...prev[notificationIndex],
					...item,
				};

				updateSelectedItem(prev);

				return prev;
			});
		}
	};

	const userRecipientUpdateSelectedItem = (values) => {
		setSelectedItem((previousItem) => {
			const newRecipients = [
				...previousItem.data.notifications.recipients,
			];

			newRecipients[notificationIndex][0] = {
				...newRecipients[notificationIndex][0],
				assignmentType: ['user'],
				emailAddress: values.map(({emailAddress}) => emailAddress),
				sectionsData: values.map((values) => values),
			};

			return {
				...previousItem,
				data: {
					...previousItem.data,
					notifications: {
						...previousItem.data.notifications,
						recipients: newRecipients,
					},
				},
			};
		});
	};

	if (selectedItem.type === 'task') {
		if (
			!executionTypeOptions
				.map((option) => option.value)
				.includes('onAssignment')
		) {
			executionTypeOptions.unshift({
				label: Liferay.Language.get('on-assignment'),
				value: 'onAssignment',
			});
		}
	}
	else if (selectedItem.type !== 'task') {
		executionTypeOptions = executionTypeOptions.filter(({value}) => {
			return value !== 'onAssignment';
		});
	}

	useEffect(() => {
		if (selectedItem.data.notifications) {
			setSelectedItem((previousItem) => {
				const recipientDetails = {};

				if (recipientType === 'assetCreator') {
					recipientDetails.assignmentType = ['user'];

					if (
						selectedItem.data.notifications.recipients[
							notificationIndex
						]?.[0]
					) {
						delete selectedItem.data.notifications?.recipients?.[
							notificationIndex
						][0].emailAddress;
					}
				}
				else if (recipientType === 'taskAssignees') {
					recipientDetails.assignmentType = ['taskAssignees'];
				}

				if (
					previousItem.data.notifications.recipients[
						notificationIndex
					]?.[0]
				) {
					previousItem.data.notifications.recipients[
						notificationIndex
					][0] = {
						...previousItem.data.notifications.recipients[
							notificationIndex
						][0],
						...recipientDetails,
					};
				}
				else {
					previousItem.data.notifications.recipients[
						notificationIndex
					] = [recipientDetails];
				}

				return {...previousItem};
			});
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [notificationIndex, recipientType, setSelectedItem]);

	useEffect(() => {
		if (!selectedItem) {
			return;
		}
		let sectionsData = [];

		const recipients =
			selectedItem?.data?.notifications &&
			(selectedItem?.data?.notifications?.recipients?.[
				notificationIndex
			]?.[0] ||
				selectedItem?.data?.notifications?.recipients?.[
					notificationIndex
				]);

		if (recipients && recipientType === 'roleType') {
			for (let i = 0; i < recipients?.roleType?.length; i++) {
				sectionsData.push({
					autoCreate: recipients?.autoCreate?.[i],
					identifier: `${Date.now()}-${i}`,
					roleName: recipients?.roleName?.[i],
					roleType: recipients?.roleType?.[i],
				});
			}
		}
		else if (recipients?.sectionsData && recipientType === 'user') {
			sectionsData = recipients?.sectionsData;
		}

		if (sectionsData.length) {
			setInternalSections(sectionsData);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('information')}>
			<BaseNotificationsInfo
				accountEntryId={accountEntryId}
				defaultScript={
					selectedItem.data.notifications?.recipients?.[
						notificationIndex
					]?.[0]?.script?.[0]
				}
				defaultScriptLanguage={
					selectedItem.data.notifications?.recipients?.[
						notificationIndex
					]?.[0]?.scriptLanguage
				}
				deleteSection={deleteSection}
				executionType={executionType}
				executionTypeOptions={executionTypeOptions}
				handleClickCapture={(scriptLanguage) =>
					setSelectedItem((previousItem) => {
						previousItem.data.notifications.recipients[
							notificationIndex
						][0] = {
							...previousItem.data.notifications.recipients[
								notificationIndex
							][0],
							scriptLanguage: [scriptLanguage],
						};

						return {...previousItem};
					})
				}
				identifier={identifier}
				internalSections={internalSections}
				items={items}
				notificationDescription={notificationDescription}
				notificationIndex={notificationIndex}
				notificationName={notificationName}
				notificationTypeEmail={notificationTypeEmail}
				notificationTypeUserNotification={
					notificationTypeUserNotification
				}
				recipientType={recipientType}
				roleRecipientUpdateSelectedItem={
					roleRecipientUpdateSelectedItem
				}
				roleTypeRecipientUpdateSelectedItem={
					roleTypeRecipientUpdateSelectedItem
				}
				scriptedRecipientUpdateSelectedItem={
					scriptedRecipientUpdateSelectedItem
				}
				sectionsData={
					selectedItem.data.notifications?.recipients?.[
						notificationIndex
					]?.sectionsData ||
					selectedItem.data.notifications?.recipients?.[
						notificationIndex
					]?.[0]?.sectionsData
				}
				sectionsLength={sectionsLength}
				selectedItem={selectedItem}
				setExecutionType={setExecutionType}
				setInternalSections={setInternalSections}
				setItems={setItems}
				setNotificationDescription={setNotificationDescription}
				setNotificationName={setNotificationName}
				setNotificationTypeEmail={setNotificationTypeEmail}
				setNotificationTypeUserNotification={
					setNotificationTypeUserNotification
				}
				setRecipientType={setRecipientType}
				setSections={setSections}
				setTemplate={setTemplate}
				setTemplateLanguage={setTemplateLanguage}
				showAddButton
				template={template}
				templateLanguage={templateLanguage}
				updateNotificationType={updateNotificationType}
				userRecipientUpdateSelectedItem={
					userRecipientUpdateSelectedItem
				}
				{...restProps}
			/>
		</SidebarPanel>
	);
};

NotificationsInfo.propTypes = {
	setContentName: PropTypes.func.isRequired,
};

export default NotificationsInfo;
