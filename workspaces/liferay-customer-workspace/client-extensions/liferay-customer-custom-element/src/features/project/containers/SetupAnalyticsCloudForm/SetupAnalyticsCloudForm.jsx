/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useQuery} from '@apollo/client';
import ClayForm, {ClayCheckbox} from '@clayui/form';
import {FieldArray, Formik} from 'formik';
import {useEffect, useMemo, useState} from 'react';
import {Button, Input, Select} from '~/components';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {
	STATUS_CODE,
	STATUS_TAG_TYPE_NAMES,
} from '~/features/project/utils/constants';
import {
	HIGH_PRIORITY_CONTACT_CATEGORIES,
	addContactRoleLiferay,
	addContactRoleRaysource,
	removeContactRoleLiferay,
	removeContactRoleRaysource,
	updateLiferayContact,
	updateRaysourceContact,
} from '~/features/project/utils/getHighPriorityContacts';
import useBannedDomains from '~/hooks/useBannedDomains';
import SearchBuilder from '~/lib/SearchBuilder';
import NotificationQueueService from '~/services/actions/notificationAction';
import {
	addAnalyticsCloudWorkspace,
	addIncidentReportAnalyticsCloud,
	getAnalyticsCloudPageInfo,
	getAnalyticsCloudWorkspace,
	updateAccountSubscriptionGroups,
} from '~/services/liferay/graphql/queries';
import {getOrRequestToken} from '~/services/liferay/security/auth/getOrRequestToken';
import i18n from '~/utils/I18n';
import getInitialAnalyticsInvite from '~/utils/getInitialAnalyticsInvite';
import getKebabCase from '~/utils/getKebabCase';
import {
	isValidEmail,
	isValidEmailDomain,
	isValidFriendlyURL,
	maxLength,
} from '~/utils/validations.form';

import Layout from '../../../../components/FormLayout';
import SetupHighPriorityContactForm from '../HighPriorityContacts/SetupHighPriorityContact';
import IncidentReportInput from './IncidentReportInput';

const BLANK_TEXT = '< none >';
const FETCH_DELAY_AFTER_TYPING = 500;
const INITIAL_SETUP_ADMIN_COUNT = 1;
const MAX_LENGTH = 255;

const getAnalyticsCloudSubmittedStatus = async (client, accountKey) => {
	const {data} = await client.query({
		query: getAnalyticsCloudWorkspace,
		variables: {
			filter: SearchBuilder.eq('accountKey', accountKey),
		},
	});

	return !!data?.c?.analyticsCloudWorkspaces?.items?.length;
};

const SetupAnalyticsCloudPage = ({
	client,
	errors,
	handlePage,
	leftButton,
	project,
	setFieldValue,
	setFormAlreadySubmitted,
	subscriptionGroupId,
	touched,
	values,
}) => {
	const [isLoadingSubmitButton, setIsLoadingSubmitButton] = useState(false);
	const [baseButtonDisabled, setBaseButtonDisabled] = useState(true);
	const [addHighPriorityContact, setAddHighPriorityContact] = useState([]);
	const [termAndConditionsChecked, setTermAndConditionsChecked] =
		useState(false);
	const [removeHighPriorityContact, setRemoveHighPriorityContact] = useState(
		[]
	);
	const [currentHighPriorityContacts, setCurrentHighPriorityContacts] =
		useState([]);
	const [step, setStep] = useState(1);

	const handlePreviousStep = () => {
		setStep(step - 1);
	};

	const handleNextStep = () => {
		setStep(step + 1);
	};
	const [isMultiSelectEmpty, setIsMultiSelectEmpty] = useState(false);

	const bannedDomainsOwnerEmail = useBannedDomains(
		values?.activations?.ownerEmailAddress,
		FETCH_DELAY_AFTER_TYPING
	);

	const bannedDomainsAllowedDomains = useBannedDomains(
		values?.activations?.allowedEmailDomains,
		FETCH_DELAY_AFTER_TYPING
	);

	useEffect(() => {}, [values?.activations?.allowedEmailDomains]);

	const {data} = useQuery(getAnalyticsCloudPageInfo, {
		variables: {
			accountSubscriptionsFilter: `(accountKey eq '${project?.accountKey}') and (hasDisasterDataCenterRegion eq true)`,
		},
	});

	const {featureFlags, provisioningServerAPI} = useAppPropertiesContext();

	const analyticsDataCenterLocations = useMemo(
		() =>
			data?.c?.analyticsCloudDataCenterLocations?.items.map(({name}) => ({
				label: i18n.translate(getKebabCase(name)),
				value: name,
			})) || [],
		[data]
	);

	const hasDisasterRecovery = !!data?.c?.accountSubscriptions?.items?.length;

	useEffect(() => {
		if (analyticsDataCenterLocations.length) {
			setFieldValue(
				'activations.dataCenterLocation',
				analyticsDataCenterLocations[0].value
			);

			if (hasDisasterRecovery) {
				setFieldValue(
					'activations.disasterDataCenterLocation',
					analyticsDataCenterLocations[0].value
				);
			}
		}
	}, [analyticsDataCenterLocations, hasDisasterRecovery, setFieldValue]);

	useEffect(() => {
		const hasTouched = !Object.keys(touched).length;
		const hasError = Object.keys(errors).length;

		setBaseButtonDisabled(hasTouched || hasError);
	}, [touched, errors]);

	const emailsCriticalIncident = currentHighPriorityContacts
		.concat(addHighPriorityContact)
		.filter(
			(contact) =>
				!removeHighPriorityContact.some(
					({email}) => email === contact.email
				)
		)
		.map(({email}) => email)
		.join(', ');

	const handleSubmit = async () => {
		setIsLoadingSubmitButton(true);

		const analyticsCloud = values?.activations;

		const alreadySubmitted = await getAnalyticsCloudSubmittedStatus(
			client,
			project.accountKey
		);

		if (alreadySubmitted) {
			return setFormAlreadySubmitted(true);
		}

		const handleDataSubmit = async () => {
			const {data} = await client.mutate({
				context: {
					displaySuccess: false,
					type: 'liferay-rest',
				},
				mutation: addAnalyticsCloudWorkspace,
				variables: {
					analyticsCloudWorkspace: {
						accountKey: project.accountKey,
						allowedEmailDomains: analyticsCloud.allowedEmailDomains,
						dataCenterLocation: analyticsCloud.dataCenterLocation,
						ownerEmailAddress: analyticsCloud.ownerEmailAddress,
						r_accountEntryToAnalyticsCloudWorkspace_accountEntryId:
							project?.id,
						timeZone: analyticsCloud.timeZone,
						workspaceFriendlyUrl:
							analyticsCloud.workspaceFriendlyUrl,
						workspaceName: analyticsCloud.workspaceName,
					},
				},
			});

			if (data) {
				const analyticsCloudWorkspaceId =
					data?.createAnalyticsCloudWorkspace?.id;

				await client.mutate({
					context: {
						displaySuccess: false,
						type: 'liferay-rest',
					},
					mutation: updateAccountSubscriptionGroups,
					variables: {
						accountSubscriptionGroup: {
							accountKey: project.accountKey,
							activationStatus: STATUS_TAG_TYPE_NAMES.inProgress,
							r_accountEntryToAccountSubscriptionGroup_accountEntryId:
								project?.id,
						},
						id: subscriptionGroupId,
					},
				});

				if (!featureFlags.includes('LPS-159127')) {
					await Promise.all(
						analyticsCloud?.incidentReportContact?.map(
							({email}) => {
								return client.mutate({
									context: {
										displaySuccess: false,
										type: 'liferay-rest',
									},
									mutation: addIncidentReportAnalyticsCloud,
									variables: {
										IncidentReportContactAnalyticsCloud: {
											analyticsCloudWorkspaceId,
											emailAddress: email,
											r_accountEntryToIncidentReportContactAC_accountEntryId:
												project.id,
										},
									},
								});
							}
						)
					);
				}

				if (featureFlags.includes('LPS-181031')) {
					const notificationTemplateService =
						new NotificationQueueService(client);

					await notificationTemplateService.send(
						'SETUP-ANALYTICS-CLOUD-ENVIRONMENT',
						{
							'[%AC_DATA_CENTER_LOCATION%]':
								analyticsCloud.dataCenterLocation,
							'[%AC_DATA_TIME%]': new Date().toUTCString(),
							'[%AC_EMAIL_DOMAINS%]':
								analyticsCloud.allowedEmailDomains ||
								BLANK_TEXT,
							'[%AC_INCIDENT_REPORT_CONTACT%]':
								emailsCriticalIncident,
							'[%AC_OWNER_EMAIL%]':
								analyticsCloud.ownerEmailAddress,
							'[%AC_TIME_ZONE%]':
								analyticsCloud.timeZone || BLANK_TEXT,
							'[%AC_WORKSPACE_FRIENDLY_URL%]':
								analyticsCloud.workspaceFriendlyUrl ||
								BLANK_TEXT,
							'[%AC_WORKSPACE_NAME%]':
								analyticsCloud.workspaceName,
							'[%PROJECT_ID%]': project?.code,
							'[%PROJECT_SALESFORCE_ACCOUNT_LINK%]':
								project?.salesforceAccountKey
									? 'https://liferay.lightning.force.com/lightning/r/Account/' +
										project?.salesforceAccountKey +
										'/view'
									: BLANK_TEXT,
							'[%PROJECT_SALESFORCE_PROJECT_LINK%]':
								project?.salesforceProjectKey
									? 'https://liferay.lightning.force.com/lightning/r/Project__c/' +
										project?.salesforceProjectKey +
										'/view'
									: BLANK_TEXT,
						}
					);
				}
			}
		};

		try {
			setIsLoadingSubmitButton(true);

			const oAuthToken = await getOrRequestToken();

			if (featureFlags.includes('LPS-159127')) {
				try {
					await updateRaysourceContact(
						addContactRoleRaysource,
						addHighPriorityContact,
						oAuthToken,
						project,
						provisioningServerAPI
					);

					await updateLiferayContact(
						addHighPriorityContact,
						addContactRoleLiferay,
						project,
						client
					);
				}
				catch (error) {
					if (error.cause === STATUS_CODE.conflict) {
						await updateLiferayContact(
							addHighPriorityContact,
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
					removeHighPriorityContact,
					oAuthToken,
					project,
					provisioningServerAPI
				);

				await updateLiferayContact(
					removeHighPriorityContact,
					removeContactRoleLiferay,
					project,
					client
				);
			}

			handleDataSubmit();
			setIsLoadingSubmitButton(false);

			handlePage(true);
		}
		catch (error) {
			setIsLoadingSubmitButton(false);
		}
	};

	const handleButtonClick = () => {

		// eslint-disable-next-line no-unused-expressions
		step === 1 ? handlePage(false) : handlePreviousStep();
	};

	const updateMultiSelectEmpty = (error) => {
		setIsMultiSelectEmpty(error);
	};

	return featureFlags.includes('LPS-159127') ? (
		<>
			<Layout
				className="pt-1 px-3"
				footerProps={{
					leftButton: (
						<Button
							borderless
							className="text-neutral-10"
							onClick={() => {
								handleButtonClick();
							}}
						>
							{step === 1
								? leftButton
								: i18n.translate('previous')}
						</Button>
					),
					middleButton: (
						<Button
							disabled={
								step === 1
									? baseButtonDisabled ||
										!termAndConditionsChecked
									: isMultiSelectEmpty ||
										isLoadingSubmitButton
							}
							displayType="primary"
							isLoading={isLoadingSubmitButton}
							onClick={step === 1 ? handleNextStep : handleSubmit}
						>
							{step === 1
								? i18n.translate('next')
								: i18n.translate('submit')}
						</Button>
					),
				}}
				headerProps={{
					helper: i18n.translate(
						'we-ll-need-a-few-details-to-finish-creating-your-analytics-cloud-workspace'
					),
					title: i18n.translate('set-up-analytics-cloud'),
				}}
			>
				{step === 1 && (
					<div>
						<FieldArray
							name="activations.incidentReportContact"
							render={() => (
								<>
									<ClayForm.Group className="pb-1">
										<div>
											<h4 className="ml-3 text-neutral-10">
												{project.name}
											</h4>
										</div>

										<Input
											groupStyle="pb-1"
											helper={i18n.translate(
												'this-user-will-create-and-manage-the-analytics-cloud-workspace-and-must-have-a-liferay-com-account-the-owner-email-can-be-updated-via-a-support-ticket-if-needed'
											)}
											label={i18n.translate(
												'owner-email'
											)}
											name="activations.ownerEmailAddress"
											placeholder="user@company.com"
											required
											type="email"
											validations={[
												(value) =>
													isValidEmail(
														value,
														bannedDomainsOwnerEmail
													),
											]}
										/>

										<Input
											groupStyle="pb-1"
											label={i18n.translate(
												'workspace-name'
											)}
											name="activations.workspaceName"
											placeholder="superbank1"
											required
											type="text"
											validations={[
												(value) =>
													maxLength(
														value,
														MAX_LENGTH
													),
											]}
										/>

										<Select
											groupStyle="pb-1"
											helper={i18n.translate(
												'select-a-server-location-for-your-data-to-be-stored'
											)}
											key={analyticsDataCenterLocations}
											label={i18n.translate(
												'data-center-location'
											)}
											name="activations.dataCenterLocation"
											options={
												analyticsDataCenterLocations
											}
											required
										/>

										{hasDisasterRecovery && (
											<Select
												groupStyle="mb-0 pt-2"
												label={i18n.translate(
													'Disaster Recovery Data Center Location'
												)}
												name="activations.disasterDataCenterLocation"
												options={
													analyticsDataCenterLocations
												}
												required
											/>
										)}

										<Input
											groupStyle="pb-1"
											helper={i18n.translate(
												'please-note-that-the-friendly-url-cannot-be-changed-once-added'
											)}
											label={i18n.translate(
												'workspace-friendly-url'
											)}
											name="activations.workspaceFriendlyUrl"
											placeholder="/myurl"
											type="text"
											validations={[
												(value) =>
													isValidFriendlyURL(value),
											]}
										/>

										<Input
											groupStyle="pb-1"
											helper={i18n.translate(
												'anyone-with-an-email-address-at-the-provided-domains-can-request-access-to-your-workspace-if-multiple-separate-domains-by-commas'
											)}
											label={i18n.translate(
												'allowed-email-domains'
											)}
											name="activations.allowedEmailDomains"
											placeholder="@mycompany.com"
											type="text"
											validations={[
												() =>
													isValidEmailDomain(
														bannedDomainsAllowedDomains
													),
											]}
										/>

										<Input
											groupStyle="pb-1"
											helper={i18n.translate(
												'enter-the-timezone-to-be-used-for-all-data-reporting-in-your-workspace'
											)}
											label={i18n.translate('time-zone')}
											name="activations.timeZone"
											placeholder="UTC-04:00"
											type="text"
										/>

										<div className="ml-3">
											<ClayCheckbox
												checked={
													termAndConditionsChecked
												}
												label={
													<div
														dangerouslySetInnerHTML={{
															__html: i18n.sub(
																'by-checking-this-box-and-clicking-next-below-i-as-an-authorized-representative-of-x-acknowledge-that-x-accepts-the-x-terms-and-conditions-and-privacy-policy-x-these-terms-will-govern-x-s-use-of-liferay-analytics-cloud-unless-x-has-entered-into-a-separate-agreement-with-liferay-that-governs-x-s-use-of-liferay-analytics-cloud',
																[
																	project.name,
																	'<a href="https://www.liferay.com/documents/d/guest/1012410" rel="noreferrer noopener" target="_blank">',
																	'</a>',
																]
															),
														}}
													/>
												}
												onChange={() =>
													setTermAndConditionsChecked(
														(
															previousTermAndConditionsChecked
														) =>
															!previousTermAndConditionsChecked
													)
												}
											/>
										</div>
									</ClayForm.Group>
								</>
							)}
						/>
					</div>
				)}

				{step === 2 && (
					<div>
						<SetupHighPriorityContactForm
							addContactList={setAddHighPriorityContact}
							currentHighPriorityContacts={
								setCurrentHighPriorityContacts
							}
							disableSubmit={updateMultiSelectEmpty}
							filter={
								HIGH_PRIORITY_CONTACT_CATEGORIES.criticalIncident
							}
							removedContactList={setRemoveHighPriorityContact}
						/>
					</div>
				)}
			</Layout>
		</>
	) : (
		<Layout
			className="pt-1 px-3"
			footerProps={{
				leftButton: (
					<Button
						borderless
						className="text-neutral-10"
						onClick={() => handlePage(false)}
					>
						{leftButton}
					</Button>
				),
				middleButton: (
					<Button
						disabled={
							baseButtonDisabled || !termAndConditionsChecked
						}
						displayType="primary"
						onClick={handleSubmit}
					>
						{i18n.translate('submit')}
					</Button>
				),
			}}
			headerProps={{
				helper: i18n.translate(
					'we-ll-need-a-few-details-to-finish-creating-your-analytics-cloud-workspace'
				),
				title: i18n.translate('set-up-analytics-cloud'),
			}}
		>
			<FieldArray
				name="activations.incidentReportContact"
				render={({pop, push}) => (
					<>
						<ClayForm.Group className="pb-1">
							<div>
								<h4 className="ml-3 text-neutral-10">
									{project.name}
								</h4>
							</div>

							<Input
								groupStyle="pb-1"
								helper={i18n.translate(
									'this-user-will-create-and-manage-the-analytics-cloud-workspace-and-must-have-a-liferay-com-account-the-owner-email-can-be-updated-via-a-support-ticket-if-needed'
								)}
								label={i18n.translate('owner-email')}
								name="activations.ownerEmailAddress"
								placeholder="user@company.com"
								required
								type="email"
								validations={[
									(value) =>
										isValidEmail(
											value,
											bannedDomainsOwnerEmail
										),
								]}
							/>

							<Input
								groupStyle="pb-1"
								label={i18n.translate('workspace-name')}
								name="activations.workspaceName"
								placeholder="superbank1"
								required
								type="text"
								validations={[
									(value) => maxLength(value, MAX_LENGTH),
								]}
							/>

							<Select
								groupStyle="pb-1"
								helper={i18n.translate(
									'select-a-server-location-for-your-data-to-be-stored'
								)}
								key={analyticsDataCenterLocations}
								label={i18n.translate('data-center-location')}
								name="activations.dataCenterLocation"
								options={analyticsDataCenterLocations}
								required
							/>

							{hasDisasterRecovery && (
								<Select
									groupStyle="mb-0 pt-2"
									label={i18n.translate(
										'Disaster Recovery Data Center Location'
									)}
									name="activations.disasterDataCenterLocation"
									options={analyticsDataCenterLocations}
									required
								/>
							)}

							<Input
								groupStyle="pb-1"
								helper={i18n.translate(
									'please-note-that-the-friendly-url-cannot-be-changed-once-added'
								)}
								label={i18n.translate('workspace-friendly-url')}
								name="activations.workspaceFriendlyUrl"
								placeholder="/myurl"
								type="text"
								validations={[
									(value) => isValidFriendlyURL(value),
								]}
							/>

							<Input
								groupStyle="pb-1"
								helper={i18n.translate(
									'anyone-with-an-email-address-at-the-provided-domains-can-request-access-to-your-workspace-if-multiple-separate-domains-by-commas'
								)}
								label={i18n.translate('allowed-email-domains')}
								name="activations.allowedEmailDomains"
								placeholder="@mycompany.com"
								type="text"
								validations={[
									() =>
										isValidEmailDomain(
											bannedDomainsAllowedDomains
										),
								]}
							/>

							<Input
								groupStyle="pb-1"
								helper={i18n.translate(
									'enter-the-timezone-to-be-used-for-all-data-reporting-in-your-workspace'
								)}
								label={i18n.translate('time-zone')}
								name="activations.timeZone"
								placeholder="UTC-04:00"
								type="text"
							/>

							<div className="ml-3">
								<ClayCheckbox
									checked={termAndConditionsChecked}
									label={
										<div
											dangerouslySetInnerHTML={{
												__html: i18n.sub(
													'by-checking-this-box-and-clicking-submit-below-i-as-an-authorized-representative-of-x-acknowledge-that-x-accepts-the-x-terms-and-conditions-and-privacy-policy-x-these-terms-will-govern-x-s-use-of-liferay-analytics-cloud-unless-x-has-entered-into-a-separate-agreement-with-liferay-that-governs-x-s-use-of-liferay-analytics-cloud',
													[
														project.name,
														'<a href="https://www.liferay.com/documents/d/guest/1012410" rel="noreferrer noopener" target="_blank">',
														'</a>',
													]
												),
											}}
										/>
									}
									onChange={() =>
										setTermAndConditionsChecked(
											(
												previousTermAndConditionsChecked
											) =>
												!previousTermAndConditionsChecked
										)
									}
								/>
							</div>

							<ClayForm.Group>
								{values?.activations?.incidentReportContact?.map(
									(activation, index) => (
										<IncidentReportInput
											activation={activation}
											id={index}
											key={index}
										/>
									)
								)}
							</ClayForm.Group>
						</ClayForm.Group>

						{values?.activations?.incidentReportContact?.length >
							INITIAL_SETUP_ADMIN_COUNT && (
							<Button
								className="ml-3 my-2 text-brandy-secondary"
								displayType="secondary"
								onClick={() => pop()}
								prependIcon="hr"
								small
							>
								{i18n.translate(
									'remove-incident-report-contact'
								)}
							</Button>
						)}

						<Button
							className="btn-outline-primary ml-3 my-2 rounded-xs"
							onClick={() => {
								push(
									getInitialAnalyticsInvite(
										values?.activations
											?.incidentReportContact
									)
								);
							}}
							prependIcon="plus"
							small
						>
							{i18n.translate('add-incident-report-contact')}
						</Button>
					</>
				)}
			/>
		</Layout>
	);
};

const SetupAnalyticsCloudForm = (props) => {
	return (
		<Formik
			initialValues={{
				activations: {
					allowedEmailDomains: '',
					dataCenterLocation: '',
					disasterDataCenterLocation: '',
					incidentReportContact: [getInitialAnalyticsInvite()],
					ownerEmailAddress: '',
					timeZone: '',
					workspaceName: '',
					workspaceURL: '',
				},
			}}
			validateOnChange
		>
			{(formikProps) => (
				<SetupAnalyticsCloudPage {...props} {...formikProps} />
			)}
		</Formik>
	);
};

export default SetupAnalyticsCloudForm;
