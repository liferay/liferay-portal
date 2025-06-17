/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useQuery} from '@apollo/client';
import ClayForm, {ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {FieldArray, Formik} from 'formik';

import {useEffect, useMemo, useState} from 'react';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import SearchBuilder from '~/lib/SearchBuilder';
import NotificationQueueService from '~/services/actions/notificationAction';
import {
	HIGH_PRIORITY_CONTACT_CATEGORIES,
	addContactRoleLiferay,
	addContactRoleRaysource,
	removeContactRoleLiferay,
	removeContactRoleRaysource,
	updateLiferayContact,
	updateRaysourceContact,
} from '~/features/project/utils/getHighPriorityContacts';
import {
	STATUS_CODE,
	STATUS_TAG_TYPE_NAMES,
} from '~/features/project/utils/constants';
import i18n from '~/utils/I18n';
import {Button, Input, Select} from '~/components';
import SetupHighPriorityContactForm from '~/features/project/containers/HighPriorityContacts/SetupHighPriorityContact';
import {
	addAdminDXPCloud,
	addDXPCloudEnvironment,
	getDXPCloudEnvironment,
	getDXPCloudPageInfo,
	getListTypeDefinitions,
	updateAccountSubscriptionGroups,
} from '~/services/liferay/graphql/queries';
import {getOrRequestToken} from '~/services/liferay/security/auth/getOrRequestToken';
import getInitialDXPAdmin from '~/utils/getInitialDXPAdmin';
import getKebabCase from '~/utils/getKebabCase';
import sortLiferayVersions from '~/utils/sortLiferayVersions';
import {isLowercaseAndNumbers} from '~/utils/validations.form';
import Layout from '../../../../components/FormLayout';
import AdminInputs from './AdminInputs';

import './SetupDXPCloudForm.css';

const INITIAL_SETUP_ADMIN_COUNT = 1;
const MAXIMUM_NUMBER_OF_CHARACTERS = 77;

const HA_DR_FILTER = 'HA DR';
const STD_DR_FILTER = 'Std DR';

const SetupDXPCloudPage = ({
	client,
	dxpVersion,
	errors,
	handlePage,
	leftButton,
	listType,
	project,
	setFieldValue,
	setFormAlreadySubmitted,
	subscriptionGroupId,
	touched,
	values,
}) => {
	const [isLoadingSubmitButton, setIsLoadingSubmitButton] = useState(false);
	const [baseButtonDisabled, setBaseButtonDisabled] = useState(true);
	const [dxpVersions, setDxpVersions] = useState([]);
	const [selectedVersion, setSelectedVersion] = useState(dxpVersion || '');
	const {data} = useQuery(getDXPCloudPageInfo, {
		variables: {
			accountSubscriptionsFilter: `(accountKey eq '${project.accountKey}') and (hasDisasterDataCenterRegion eq true or (name eq '${HA_DR_FILTER}' or name eq '${STD_DR_FILTER}'))`,
		},
	});
	const {featureFlags, provisioningServerAPI} = useAppPropertiesContext();

	const [addHighPriorityContact, setAddHighPriorityContact] = useState([]);
	const [removeHighPriorityContact, setRemoveHighPriorityContact] = useState(
		[]
	);
	const [isMultiSelectEmpty, setIsMultiSelectEmpty] = useState(false);

	const [step, setStep] = useState(1);

	const handlePreviousStep = () => {
		setStep(step - 1);
	};

	const handleNextStep = () => {
		setStep(step + 1);
	};
	useEffect(() => {
		const fetchListTypeDefinitions = async () => {
			const {data: typeDefinitionResponse} = await client.query({
				query: getListTypeDefinitions,
				variables: {
					filter: SearchBuilder.eq('name', listType),
				},
			});

			const items =
				typeDefinitionResponse?.listTypeDefinitions?.items[0]
					?.listTypeEntries;

			if (items?.length) {
				const sortedItems = sortLiferayVersions([...items]);
				setDxpVersions(sortedItems);

				setSelectedVersion(
					sortedItems.find((item) => item.name === dxpVersion)
						?.name || sortedItems[0].name
				);
			}
		};

		fetchListTypeDefinitions();
	}, [client, dxpVersion, listType]);

	const dXPCDataCenterRegions = useMemo(
		() =>
			data?.c?.dXPCDataCenterRegions?.items.map(({name}) => ({
				label: i18n.translate(getKebabCase(name)),
				value: getKebabCase(name),
			})) || [],
		[data]
	);

	const hasDisasterRecovery = data?.c?.accountSubscriptions?.totalCount > 0;

	useEffect(() => {
		if (dXPCDataCenterRegions.length) {
			setFieldValue(
				'dxp.dataCenterRegion',
				dXPCDataCenterRegions[0].value
			);

			if (hasDisasterRecovery) {
				setFieldValue(
					'dxp.disasterDataCenterRegion',
					dXPCDataCenterRegions[1].value
				);
			}
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [dXPCDataCenterRegions, hasDisasterRecovery]);

	useEffect(() => {
		const hasTouched = !Object.keys(touched).length;
		const hasError = Object.keys(errors).length;

		setBaseButtonDisabled(hasTouched || hasError);
	}, [touched, errors]);

	const handleSubmit = async () => {
		setIsLoadingSubmitButton(true);
		const dxp = values?.dxp;

		const getDXPCloudActivationSubmitedStatus = async (accountKey) => {
			const {data: dxpCloudEnvironmentData} = await client.query({
				query: getDXPCloudEnvironment,
				variables: {
					filter: SearchBuilder.eq('accountKey', accountKey),
				},
			});

			if (dxpCloudEnvironmentData) {
				const status =
					!!dxpCloudEnvironmentData.c?.dXPCloudEnvironments?.items
						?.length;

				return status;
			}

			return false;
		};

		const alreadySubmitted = await getDXPCloudActivationSubmitedStatus(
			project.accountKey
		);

		if (alreadySubmitted) {
			setFormAlreadySubmitted(true);
		}

		const handleDataSubmit = async () => {
			const {data: addDXPCloudEnvironmentResponse} = await client.mutate({
				context: {
					displaySuccess: false,
					type: 'liferay-rest',
				},
				mutation: addDXPCloudEnvironment,
				variables: {
					DXPCloudEnvironment: {
						accountKey: project.accountKey,
						dataCenterRegion: dxp.dataCenterRegion,
						disasterDataCenterRegion: dxp.disasterDataCenterRegion,
						projectId: dxp.projectId,
						r_accountEntryToDXPCloudEnvironment_accountEntryId:
							project?.id,
					},
				},
			});

			if (addDXPCloudEnvironmentResponse) {
				const dxpCloudEnvironmentId =
					addDXPCloudEnvironmentResponse?.createDXPCloudEnvironment
						?.id;

				await Promise.all(
					dxp.admins.map(({email, firstName, github, lastName}) =>
						client.mutate({
							context: {
								displaySuccess: false,
								type: 'liferay-rest',
							},
							mutation: addAdminDXPCloud,
							variables: {
								AdminDXPCloud: {
									dxpCloudEnvironmentId,
									emailAddress: email,
									firstName,
									githubUsername: github,
									lastName,
									r_accountEntryToAdminDXPCloud_accountEntryId:
										project?.id,
								},
							},
						})
					)
				);
			}

			await client.mutate({
				context: {
					type: 'liferay-rest',
				},
				mutation: updateAccountSubscriptionGroups,
				variables: {
					accountSubscriptionGroup: {
						accountKey: project.accountKey,
						activationStatus: STATUS_TAG_TYPE_NAMES.inProgress,
						r_accountEntryToAccountSubscriptionGroup_accountEntryId:
							project.id,
					},
					id: subscriptionGroupId,
				},
			});

			if (featureFlags.includes('LPS-187767')) {
				const notificationTemplateService =
					new NotificationQueueService(client);

				try {
					const adminInfo = dxp?.admins?.map(
						({email, firstName, github, lastName}) => {
							return `
							<strong>Email Address - </strong> ${email}<br>
							<strong>First Name - </strong>${firstName}<br>
							<strong>Last Name - </strong>${lastName}<br>
							<strong>GitHub ID - </strong>${github}<br><br>`;
						}
					);

					await notificationTemplateService.send(
						'SETUP-DXP-CLOUD-ENVIRONMENT',
						{
							'[%DATE_AND_TIME_SUBMITTED%]':
								new Date().toUTCString(),
							'[%PROJECT_CODE%]': project.code,
							'[%PROJECT_DATA_CENTER_REGION%]':
								dxp?.dataCenterRegion,
							'[%PROJECT_DISASTER_CENTER_REGION%]':
								dxp?.disasterDataCenterRegion
									? `Primary Disaster Center Region - ${dxp?.disasterDataCenterRegion}`
									: '',
							'[%PROJECT_ID%]': dxp?.projectId,
							'[%PROJECT_VERSION%]': selectedVersion,
							'[%PROJECT_ADMIN_INFO%]': adminInfo.join(''),
						}
					);
				}
				catch (error) {
					console.error(error);
				}
			}
		};

		if (!alreadySubmitted && dxp) {
			try {
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
		}
	};

	const handleButtonClick = () => {
		if (step === 1) {
			handlePage(false);
		}
		else {
			handlePreviousStep();
		}
	};

	const updateMultiSelectEmpty = (error) => {
		setIsMultiSelectEmpty(error);
	};

	return featureFlags.includes('LPS-159127') ? (
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
						{step === 1 ? leftButton : i18n.translate('previous')}
					</Button>
				),
				middleButton: (
					<Button
						disabled={
							step === 1
								? baseButtonDisabled
								: isMultiSelectEmpty || isLoadingSubmitButton
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
					'we-ll-need-a-few-details-to-finish-building-your-liferay-paas-environment'
				),
				title: i18n.translate('set-up-liferay-paas'),
			}}
		>
			{step === 1 && (
				<div>
					<FieldArray
						name="dxp.admins"
						render={({pop, push}) => (
							<>
								<div className="d-flex justify-content-between mb-2 pb-1 pl-3">
									<div className="mr-4 pr-2">
										<label>
											{i18n.translate('project-name')}
										</label>

										<p className="lxc-sm-project-name text-neutral-6 text-paragraph-lg">
											<strong>
												{project.name.length >
												MAXIMUM_NUMBER_OF_CHARACTERS
													? project.name.substring(
															0,
															MAXIMUM_NUMBER_OF_CHARACTERS
														) + '...'
													: project.name}
											</strong>
										</p>
									</div>

									<div className="flex-fill">
										<label>
											{i18n.translate(
												'liferay-dxp-version'
											)}
										</label>

										<div className="position-relative">
											<ClayIcon
												className="select-icon"
												symbol="caret-bottom"
											/>

											<ClaySelect
												className="bg-neutral-1 border-0 font-weight-bold mr-2 pr-6"
												onChange={({target}) => {
													setSelectedVersion(
														target.value
													);
												}}
												value={selectedVersion}
											>
												{dxpVersions.map((version) => (
													<ClaySelect.Option
														className="font-weight-bold options"
														key={version.key}
														label={version.name}
													/>
												))}
											</ClaySelect>
										</div>
									</div>
								</div>
								<ClayForm.Group className="mb-0">
									<ClayForm.Group className="mb-0 pb-1">
										<Input
											groupStyle="pb-1"
											helper={i18n.translate(
												'lowercase-letters-and-numbers-only-the-project-id-cannot-be-changed'
											)}
											label={i18n.translate('project-id')}
											name="dxp.projectId"
											required
											type="text"
											validations={[
												(value) =>
													isLowercaseAndNumbers(
														value
													),
											]}
										/>

										<Select
											groupStyle="mb-0"
											label={i18n.translate(
												'primary-data-center-region'
											)}
											name="dxp.dataCenterRegion"
											options={dXPCDataCenterRegions.map(
												(option) => ({
													...option,
													disabled:
														option.value ===
														values.dxp
															.disasterDataCenterRegion,
												})
											)}
											required
										/>

										{!!hasDisasterRecovery && (
											<Select
												groupStyle="mb-0 pt-2"
												id="disasterRecovery"
												label="Disaster Recovery Data Center Region"
												name="dxp.disasterDataCenterRegion"
												options={dXPCDataCenterRegions.map(
													(option) => ({
														...option,
														disabled:
															option.value ===
															values.dxp
																.dataCenterRegion,
													})
												)}
												required
											/>
										)}
									</ClayForm.Group>

									{values.dxp.admins.map((admin, index) => (
										<AdminInputs
											admin={admin}
											id={index}
											key={index}
										/>
									))}
								</ClayForm.Group>
								{values?.dxp?.admins?.length >
									INITIAL_SETUP_ADMIN_COUNT && (
									<Button
										className="ml-3 my-2 text-brandy-secondary"
										displayType="secondary"
										onClick={() => {
											pop();
											setBaseButtonDisabled(false);
										}}
										prependIcon="hr"
										small
									>
										{i18n.translate('remove-this-admin')}
									</Button>
								)}
								<Button
									className="btn-outline-primary cp-btn-add-lxc-sm ml-3 my-2 rounded-xs"
									disabled={baseButtonDisabled}
									onClick={() => {
										push(
											getInitialDXPAdmin(
												values?.dxp?.admins
											)
										);
										setBaseButtonDisabled(true);
									}}
									prependIcon="plus"
									small
								>
									{i18n.translate('add-another-admin')}
								</Button>
							</>
						)}
					/>
				</div>
			)}

			{step === 2 && (
				<div>
					<SetupHighPriorityContactForm
						addContactList={setAddHighPriorityContact}
						disableSubmit={updateMultiSelectEmpty}
						filter={
							HIGH_PRIORITY_CONTACT_CATEGORIES.criticalIncident
						}
						removedContactList={setRemoveHighPriorityContact}
					/>
				</div>
			)}
		</Layout>
	) : (
		<Layout
			className="pt-1 px-3"
			footerProps={{
				leftButton: (
					<Button borderless onClick={() => handlePage()}>
						{leftButton}
					</Button>
				),
				middleButton: (
					<Button
						disabled={baseButtonDisabled}
						displayType="primary"
						onClick={() => handleSubmit()}
					>
						{i18n.translate('submit')}
					</Button>
				),
			}}
			headerProps={{
				helper: i18n.translate(
					'we-ll-need-a-few-details-to-finish-building-your-liferay-paas-environment'
				),
				title: i18n.translate('set-up-liferay-paas'),
			}}
		>
			<FieldArray
				name="dxp.admins"
				render={({pop, push}) => (
					<>
						<div className="d-flex justify-content-between mb-2 pb-1 pl-3">
							<div className="mr-4 pr-2">
								<label>{i18n.translate('project-name')}</label>

								<p className="lxc-sm-project-name text-neutral-6 text-paragraph-lg">
									<strong>
										{project.name.length >
										MAXIMUM_NUMBER_OF_CHARACTERS
											? project.name.substring(
													0,
													MAXIMUM_NUMBER_OF_CHARACTERS
												) + '...'
											: project.name}
									</strong>
								</p>
							</div>

							<div className="flex-fill">
								<label>
									{i18n.translate('liferay-dxp-version')}
								</label>

								<div className="position-relative">
									<ClayIcon
										className="select-icon"
										symbol="caret-bottom"
									/>

									<ClaySelect
										className="bg-neutral-1 border-0 font-weight-bold mr-2 pr-6"
										onChange={({target}) => {
											setSelectedVersion(target.value);
										}}
										value={selectedVersion}
									>
										{dxpVersions.map((version) => (
											<ClaySelect.Option
												className="font-weight-bold options"
												key={version.key}
												label={version.name}
											/>
										))}
									</ClaySelect>
								</div>
							</div>
						</div>
						<ClayForm.Group className="mb-0">
							<ClayForm.Group className="mb-0 pb-1">
								<Input
									groupStyle="pb-1"
									helper={i18n.translate(
										'lowercase-letters-and-numbers-only-the-project-id-cannot-be-changed'
									)}
									label={i18n.translate('project-id')}
									name="dxp.projectId"
									required
									type="text"
									validations={[
										(value) => isLowercaseAndNumbers(value),
									]}
								/>

								<Select
									groupStyle="mb-0"
									label={i18n.translate(
										'primary-data-center-region'
									)}
									name="dxp.dataCenterRegion"
									options={dXPCDataCenterRegions.map(
										(option) => ({
											...option,
											disabled:
												option.value ===
												values.dxp
													.disasterDataCenterRegion,
										})
									)}
									required
								/>

								{!!hasDisasterRecovery && (
									<Select
										groupStyle="mb-0 pt-2"
										id="disasterRecovery"
										label="Disaster Recovery Data Center Region"
										name="dxp.disasterDataCenterRegion"
										options={dXPCDataCenterRegions.map(
											(option) => ({
												...option,
												disabled:
													option.value ===
													values.dxp.dataCenterRegion,
											})
										)}
										required
									/>
								)}
							</ClayForm.Group>

							{values.dxp.admins.map((admin, index) => (
								<AdminInputs
									admin={admin}
									id={index}
									key={index}
								/>
							))}
						</ClayForm.Group>
						{values?.dxp?.admins?.length >
							INITIAL_SETUP_ADMIN_COUNT && (
							<Button
								className="ml-3 my-2 text-brandy-secondary"
								displayType="secondary"
								onClick={() => {
									pop();
									setBaseButtonDisabled(false);
								}}
								prependIcon="hr"
								small
							>
								{i18n.translate('remove-this-admin')}
							</Button>
						)}
						<Button
							className="btn-outline-primary cp-btn-add-lxc-sm ml-3 my-2 rounded-xs"
							disabled={baseButtonDisabled}
							onClick={() => {
								push(getInitialDXPAdmin(values?.dxp?.admins));
								setBaseButtonDisabled(true);
							}}
							prependIcon="plus"
							small
						>
							{i18n.translate('add-another-admin')}
						</Button>
					</>
				)}
			/>
		</Layout>
	);
};

const SetupDXPCloudForm = (props) => {
	return (
		<Formik
			initialValues={{
				dxp: {
					admins: [getInitialDXPAdmin()],
					dataCenterRegion: '',
					disasterDataCenterRegion: '',
					projectId: '',
				},
			}}
			validateOnChange
		>
			{(formikProps) => <SetupDXPCloudPage {...props} {...formikProps} />}
		</Formik>
	);
};

export default SetupDXPCloudForm;
