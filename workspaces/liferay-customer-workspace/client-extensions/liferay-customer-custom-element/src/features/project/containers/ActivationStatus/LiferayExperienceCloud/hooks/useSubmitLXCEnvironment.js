/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMutation} from '@apollo/client';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {
	STATUS_CODE,
	STATUS_TAG_TYPE_NAMES,
} from '~/features/project/utils/constants';
import {
	addContactRoleLiferay,
	addContactRoleRaysource,
	removeContactRoleLiferay,
	removeContactRoleRaysource,
	updateLiferayContact,
	updateRaysourceContact,
} from '~/features/project/utils/getHighPriorityContacts';
import SearchBuilder from '~/lib/SearchBuilder';
import NotificationQueueService from '~/services/actions/notificationAction';
import {
	useCreateAdminLiferayExperienceCloud,
	useCreateLiferayExperienceCloudEnvironment,
} from '~/services/liferay/graphql/liferay-experience-cloud-environments';
import {
	getLiferayExperienceCloudEnvironments,
	updateAccountSubscriptionGroups,
} from '~/services/liferay/graphql/queries';
import {getOrRequestToken} from '~/services/liferay/security/auth/getOrRequestToken';

export default function useSubmitLXCEnvironment(
	handleChangeForm,
	project,
	setFormAlreadySubmitted,
	addHighPriorityContactList,
	removeHighPriorityContactList,
	subscriptionGroupLxcId,
	handleLoadingSubmitButton,
	values
) {
	const {client} = useAppPropertiesContext();

	const {featureFlags, provisioningServerAPI} = useAppPropertiesContext();

	const [createLiferayExperienceCloudEnvironment] =
		useCreateLiferayExperienceCloudEnvironment();

	const [updateAccountSubscriptionGroupsInfo] = useMutation(
		updateAccountSubscriptionGroups
	);

	const [createAdminLiferayExperienceCloud] =
		useCreateAdminLiferayExperienceCloud();

	const handleSubmitLxcEnvironment = async () => {
		const lxcActivationFields = values?.lxc;

		const liferayExperienceCloudStatus = async () => {
			const {data} = await client.query({
				query: getLiferayExperienceCloudEnvironments,
				variables: {
					filter: SearchBuilder.eq('accountKey', project.accountKey),
				},
			});

			if (data) {
				const status =
					!!data?.c?.liferayExperienceCloudEnvironments?.items
						?.length;

				return status;
			}

			return false;
		};

		const alreadySubmitted = await liferayExperienceCloudStatus();

		if (alreadySubmitted) {
			setFormAlreadySubmitted(true);

			return;
		}

		const handleDataSubmit = async () => {
			try {
				const {data} = await createLiferayExperienceCloudEnvironment({
					variables: {
						LiferayExperienceCloudEnvironment: {
							accountKey: project.accountKey,
							analyticsCloudOwnersEmailAddress:
								lxcActivationFields.analyticsCloudOwnersEmailAddress,
							incidentManagementEmailAddress:
								lxcActivationFields.incidentManagementEmail,
							incidentManagementFullName:
								lxcActivationFields.incidentManagementFullName,
							primaryRegion: lxcActivationFields.primaryRegion,
							projectId: lxcActivationFields.projectId,
							r_liferayExperienceCloudEnvironment_accountEntryId:
								project.id,
						},
					},
				});

				if (data) {
					const liferayExperienceCloudEnvironmentId =
						data.createLiferayExperienceCloudEnvironment?.id;

					await updateAccountSubscriptionGroupsInfo({
						context: {
							displaySuccess: false,
							type: 'liferay-rest',
						},
						variables: {
							accountSubscriptionGroup: {
								accountKey: project.accountKey,
								activationStatus:
									STATUS_TAG_TYPE_NAMES.inProgress,
								r_accountEntryToAccountSubscriptionGroup_accountEntryId:
									project.id,
							},
							id: subscriptionGroupLxcId,
						},
					});

					await Promise.all(
						lxcActivationFields?.admins?.map(
							({email, fullName}) => {
								return createAdminLiferayExperienceCloud({
									variables: {
										AdminLiferayExperienceCloud: {
											emailAddress: email,
											fullName,
											githubUsername: '...',
											liferayExperienceCloudEnvironmentId,
											r_accountEntryToAdminLiferayExperienceCloud_accountEntryId:
												project.id,
										},
									},
								});
							}
						)
					);

					if (featureFlags.includes('LPS-181031')) {
						const adminInfo = lxcActivationFields?.admins?.map(
							({email, fullName}) => {
								const [firstName, ...lastNames] =
									fullName.split(' ');
								const lastName = lastNames.join(' ');

								return `
									<strong>First Name -</strong> ${firstName}<br>
									<strong>Last Name - </strong>${lastName}<br>
									<strong>Email Address - </strong>${email}
									<br><br>`;
							}
						);

						const notificationTemplateService =
							new NotificationQueueService(client);

						await notificationTemplateService.send(
							'SETUP-LXC-ENVIRONMENT',
							{
								'[%ANALYTICS_CLOUD_OWNERS_EMAIL_ADDRESS%]':
									lxcActivationFields.analyticsCloudOwnersEmailAddress,
								'[%DATE_AND_TIME_SUBMITTED%]':
									new Date().toUTCString(),
								'[%PROJECT_ADMIN%]': adminInfo.join(''),
								'[%PROJECT_CODE%]': project.code,
								'[%PROJECT_DATA_CENTER_REGION%]':
									lxcActivationFields.primaryRegion,
								'[%PROJECT_ID%]': lxcActivationFields.projectId,
							}
						);
					}
				}
			}
			catch (error) {
				console.error(error);
			}
		};

		try {
			handleLoadingSubmitButton(true);

			const oAuthToken = await getOrRequestToken();

			if (featureFlags.includes('LPS-159127')) {
				try {
					await updateRaysourceContact(
						addContactRoleRaysource,
						addHighPriorityContactList,
						oAuthToken,
						project,
						provisioningServerAPI
					);

					await updateLiferayContact(
						addHighPriorityContactList,
						addContactRoleLiferay,
						project,
						client
					);
				}
				catch (error) {
					if (error.cause === STATUS_CODE.conflict) {
						await updateLiferayContact(
							addHighPriorityContactList,
							addContactRoleLiferay,
							project,
							client
						);
					}
					else {
						throw new Error('Error', {cause: error.cause});
					}
				}

				await updateRaysourceContact(
					removeContactRoleRaysource,
					removeHighPriorityContactList,
					oAuthToken,
					project,
					provisioningServerAPI
				);

				await updateLiferayContact(
					removeHighPriorityContactList,
					removeContactRoleLiferay,
					project,
					client
				);
			}

			await handleDataSubmit();
			handleChangeForm(true);
		}
		catch (error) {
			console.error(error);
		}
		finally {
			handleLoadingSubmitButton(false);
		}
	};

	return handleSubmitLxcEnvironment;
}
