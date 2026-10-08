/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNode} from 'react-flow-renderer';

import {
	retrieveAccountRoles,
	retrieveRole,
	retrieveRoles,
	retrieveUsersBy,
} from '../../util/fetchUtil';

const populateNotificationsData = (
	accountEntryId,
	initialElements,
	setElements
) => {
	for (let i = 0; i < initialElements.length; i++) {
		const element = initialElements[i];

		if (isNode(element)) {
			if (element.data.notifications) {
				const recipients = element.data.notifications.recipients;

				recipients.map((recipient, index) => {
					if (recipient[0] !== undefined) {
						recipient = recipient[0];
					}

					if (recipient?.assignmentType?.[0] === 'roleId') {
						retrieveRole(
							recipient.roleExternalReferenceCode,
							recipient.roleId
						)
							.then((response) => response.json())
							.then((response) => {
								initialElements[
									i
								].data.notifications.recipients[
									index
								][0].sectionsData = {
									id: response.id,
									name: response.name,
									roleType: response.roleType,
								};

								setElements([...initialElements]);
							});
					}
					else if (recipient?.assignmentType?.[0] === 'roleType') {
						Promise.all([
							retrieveRoles(),
							retrieveAccountRoles(accountEntryId),
						]).then(([response1, response2]) =>
							Promise.all([
								response1.json(),
								response2.json(),
							]).then(([roles, accountRoles]) => {
								const items = roles.items.concat(
									accountRoles.items
								);

								const roleName =
									element?.data?.notifications?.recipients[
										index
									]?.[0]?.roleName;

								if (!roleName) {
									return;
								}

								const roleNames = [];

								roleName.forEach((name) => {
									const role = items.find(
										(item) => item.name === name
									);

									if (
										!element?.data?.notifications
											?.recipients?.[index]?.[0]?.roleName
									) {
										initialElements[
											i
										].data.notifications.recipients[
											index
										][0].roleName = [];
									}
									roleNames.push(role?.name);
								});

								initialElements[
									i
								].data.notifications.recipients[
									index
								][0].roleName = roleNames;

								setElements([...initialElements]);
							})
						);
					}
					else if (
						recipient?.assignmentType?.[0] === 'user' &&
						(recipient.emailAddress ||
							recipient.screenName ||
							recipient.userId)
					) {
						const sectionsData = [];

						let filterTypeRetrieveUsersBy =
							Object.keys(recipient)[1];
						const keywordRetrieveUsersBy =
							Object.values(recipient)[1];

						if (filterTypeRetrieveUsersBy === 'screenName') {
							filterTypeRetrieveUsersBy = 'alternateName';
						}
						else if (filterTypeRetrieveUsersBy === 'userId') {
							filterTypeRetrieveUsersBy =
								filterTypeRetrieveUsersBy
									.toLocaleLowerCase()
									.replace('user', '');
						}

						retrieveUsersBy(
							filterTypeRetrieveUsersBy,
							keywordRetrieveUsersBy
						)
							.then((response) => response.json())
							.then(({items}) => {
								items.forEach((item, index) => {
									sectionsData.push({
										emailAddress: item.emailAddress,
										identifier: `${Date.now()}-${index}`,
										name: item.name,
										screenName: item.alternateName,
										userId: item.id,
									});
								});
							})
							.then(() => {
								initialElements[
									i
								].data.notifications.recipients[
									index
								][0].sectionsData = sectionsData;

								setElements([...initialElements]);
							});
					}
				});
			}
			if (element.data?.taskTimers?.timerNotifications[0]?.recipients) {
				const recipients =
					element.data.taskTimers.timerNotifications[0].recipients;

				recipients.map((recipient, index) => {
					if (recipient[0] !== undefined) {
						recipient = recipient[0];
					}

					if (recipient?.assignmentType?.[0] === 'roleId') {
						retrieveRole(
							recipient.roleExternalReferenceCode,
							recipient.roleId
						)
							.then((response) => response.json())
							.then((response) => {
								if (
									response &&
									element?.data?.taskTimers
										?.timerNotifications?.[0]?.recipients?.[
										index
									]
								) {
									const newSectionsData = {
										id: response.id,
										name: response.name,
										roleType: response.roleType,
									};
									initialElements[
										i
									].data.taskTimers.timerNotifications[0].recipients[
										index
									][0].sectionsData = newSectionsData;

									setElements([...initialElements]);
								}
							});
					}
					else if (recipient?.assignmentType?.[0] === 'roleType') {
						Promise.all([
							retrieveRoles(),
							retrieveAccountRoles(accountEntryId),
						]).then(([response1, response2]) =>
							Promise.all([
								response1.json(),
								response2.json(),
							]).then(([roles, accountRoles]) => {
								const items = roles.items.concat(
									accountRoles.items
								);

								let roleName =
									element?.data?.taskTimers
										?.timerNotifications[0]?.recipients[
										index
									]?.[0]?.roleName;

								if (!roleName) {
									return;
								}

								if (!Array.isArray(roleName)) {
									roleName = [roleName];
								}
								const roleNames = [];

								roleName.forEach((name) => {
									const role = items.find(
										(item) => item.name === name
									);

									if (
										!element?.data?.taskTimers
											?.timerNotifications[0]
											?.recipients?.[index]?.[0]?.roleName
									) {
										initialElements[
											i
										].data.taskTimers.timerNotifications[0].recipients[
											index
										][0].roleName = [];
									}
									roleNames.push(role?.name);
								});

								initialElements[
									i
								].data.taskTimers.timerNotifications[0].recipients[
									index
								][0].roleName = roleNames;

								setElements([...initialElements]);
							})
						);
					}
					else if (
						recipient?.assignmentType?.[0] === 'user' &&
						(recipient.emailAddress ||
							recipient.screenName ||
							recipient.userId)
					) {
						let filterTypeRetrieveUsersBy =
							Object.keys(recipient)[1];
						const keywordRetrieveUsersBy =
							Object.values(recipient)[1];

						if (filterTypeRetrieveUsersBy === 'screenName') {
							filterTypeRetrieveUsersBy = 'alternateName';
						}
						else if (filterTypeRetrieveUsersBy === 'userId') {
							filterTypeRetrieveUsersBy =
								filterTypeRetrieveUsersBy
									.toLocaleLowerCase()
									.replace('user', '');
						}

						retrieveUsersBy(
							filterTypeRetrieveUsersBy,
							keywordRetrieveUsersBy
						)
							.then((response) => response.json())
							.then(({items}) => {
								const sectionsData = [];

								items.forEach((item, index) => {
									sectionsData.push({
										emailAddress: item.emailAddress,
										identifier: `${Date.now()}-${index}`,
										name: item.name,
										screenName: item.alternateName,
										userId: item.id,
									});
								});

								if (
									sectionsData &&
									element?.data?.taskTimers
										?.timerNotifications?.[0]?.recipients?.[
										index
									]
								) {
									initialElements[
										i
									].data.taskTimers.timerNotifications[0].recipients[
										index
									][0].sectionsData = sectionsData;
								}

								setElements([...initialElements]);
							});
					}
				});
			}
		}
	}
};

export default populateNotificationsData;
