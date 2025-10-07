/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMutation} from '@apollo/client';
import ClayForm from '@clayui/form';
import classNames from 'classnames';
import {FieldArray, Formik} from 'formik';
import {useEffect, useState} from 'react';
import {Badge, Button} from '~/components';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {STATUS_CODE} from '~/features/project/utils/constants';
import {
	addTeamMembersInvitation,
	assignUserAccountWithAccount,
	assignUserAccountWithAccountAndAccountRole,
	deleteAccountUserAccount,
	patchUserAccount,
} from '~/services/liferay/graphql/queries';
import {addContactRoleNameByEmailByProject} from '~/services/liferay/rest/raysource/TeamMembers';
import i18n from '~/utils/I18n';
import {ROLE_TYPES, SLA_TYPES} from '~/utils/constants';
import getInitialInvite from '~/utils/getInitialInvite';
import getProjectRoles from '~/utils/getProjectRoles';
import isSupportSeatRole from '~/utils/isSupportSeatRole';

import Layout from '../../../../components/FormLayout';
import TeamMemberInputs from './TeamMemberInputs';

import './InviteTeamMembersForm.css';

const INITIAL_INVITES_COUNT = 1;
const MAXIMUM_SUPPORT_SEATS_DEFAULT = -1;
const MAXIMUM_INVITES_COUNT = 10;
const UNLIMITED_SUPPORT_SEATS = 9999;

const DEFAULT_WARNING = {
	message: i18n.translate('one-or-more-requests-may-have-failed'),
	title: i18n.translate('Warning'),
	type: 'warning',
};

const InviteTeamMembersPage = ({
	availableSupportSeatsCount = 0,
	errors,
	handlePage,
	leftButton,
	mutateUserData,
	oAuthToken,
	project,
	setFieldValue,
	setTouched,
	touched,
	values,
}) => {
	const {articleAccountSupportURL, client, provisioningServerAPI} =
		useAppPropertiesContext();

	const [addTeamMemberInvitation] = useMutation(addTeamMembersInvitation);
	const [updateUserAccount] = useMutation(patchUserAccount);
	const [assignUserWithAccount] = useMutation(assignUserAccountWithAccount);
	const [assignUserAccountWithAccountRole] = useMutation(
		assignUserAccountWithAccountAndAccountRole,
		{
			awaitRefetchQueries: true,
			refetchQueries: ['getUserAccountsByAccountExternalReferenceCode'],
		}
	);
	const [deleteUserAccount] = useMutation(deleteAccountUserAccount);

	const [baseButtonDisabled, setBaseButtonDisabled] = useState(true);
	const [hasInitialError, setInitialError] = useState();
	const [accountMemberRole, setAccountMemberRole] = useState();
	const [accountRolesOptions, setAccountRolesOptions] = useState([]);
	const [accountRoles, setAccountRoles] = useState([]);
	const [availableAdminsRoles, setAvailableAdminsRoles] = useState(1);
	const [isLoadingUserInvitation, setIsLoadingUserInvitation] =
		useState(false);
	const [showEmptyEmailError, setshowEmptyEmailError] = useState(false);
	const [roleSelectorFilled, setRoleSelectorFilled] = useState(false);

	const projectHasSLAGoldPlatinum =
		project?.slaCurrent?.includes(SLA_TYPES.gold) ||
		project?.slaCurrent?.includes(SLA_TYPES.platinum);

	const isUnlimitedSupportSeats =
		project.maxRequestors === MAXIMUM_SUPPORT_SEATS_DEFAULT;

	useEffect(() => {
		const getRoles = async () => {
			const roles = await getProjectRoles(client, project);

			if (roles) {
				const accountMember = roles?.find(
					({name}) => name === ROLE_TYPES?.member.name
				);

				setAccountMemberRole(accountMember);

				setFieldValue(
					'invites[0].role',
					availableSupportSeatsCount < 1
						? [accountMember]
						: [
								roles?.find(
									({name}) =>
										name === ROLE_TYPES?.requester.name ||
										name === ROLE_TYPES?.admin.name
								),
							]
				);

				for (let i = 1; i < INITIAL_INVITES_COUNT; i++) {
					setFieldValue(`invites[${i}].role`, [accountMember]);
				}

				setAccountRoles(roles);
				setAccountRolesOptions(
					roles?.map((role) => ({
						disabled: false,
						label: role.name,
						value: role.id,
					}))
				);
			}
		};

		getRoles();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [availableSupportSeatsCount, client, setFieldValue]);

	useEffect(() => {
		if (values && accountRoles?.length) {
			const totalAdmins = values.invites?.reduce(
				(totalInvites, currentInvite) => {
					if (
						currentInvite?.role?.name ===
							ROLE_TYPES.requester.name ||
						currentInvite?.role?.name === ROLE_TYPES.admin.name
					) {
						return ++totalInvites;
					}

					return totalInvites;
				},
				0
			);

			const remainingAdmins = availableSupportSeatsCount - totalAdmins;

			return project.maxRequestors === MAXIMUM_SUPPORT_SEATS_DEFAULT
				? setAvailableAdminsRoles(UNLIMITED_SUPPORT_SEATS)
				: setAvailableAdminsRoles(remainingAdmins);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [values, project, accountRoles, availableSupportSeatsCount]);

	useEffect(() => {
		const inviteMembers =
			values?.invites?.filter(({email}) => email)?.length || 0;
		const totalEmails = values?.invites?.length || 0;
		const failedEmails =
			errors?.invites?.filter((email) => email)?.length || 0;

		const hasSupportSeatRoleInvited = values?.invites?.some((invite) =>
			invite.role.some((roleSelected) =>
				isSupportSeatRole(roleSelected.name)
			)
		);
		const supportSeatRoleInvitedCount = values?.invites.flatMap((invite) =>
			invite.role.filter((roleSelected) =>
				isSupportSeatRole(roleSelected.name)
			)
		).length;

		if (inviteMembers) {
			const successfullyEmails = totalEmails - failedEmails;
			if (
				availableSupportSeatsCount === 0 &&
				!isUnlimitedSupportSeats &&
				hasSupportSeatRoleInvited
			) {
				setBaseButtonDisabled(true);
			}
			else if (
				!isUnlimitedSupportSeats &&
				availableSupportSeatsCount < supportSeatRoleInvitedCount
			) {
				setBaseButtonDisabled(true);
			}
			else {
				setInitialError(false);
				setBaseButtonDisabled(successfullyEmails !== totalEmails);
				setshowEmptyEmailError(false);
			}
		}
		else if (touched['invites']?.some((field) => field?.email)) {
			setInitialError(true);
			setBaseButtonDisabled(true);
		}
	}, [
		touched,
		values,
		availableSupportSeatsCount,
		errors,
		project.maxRequestors,
		isUnlimitedSupportSeats,
	]);

	const handleSubmit = async () => {
		const inviteMembers = values?.invites?.filter(({email}) => email) || [];

		if (!inviteMembers.length) {
			setInitialError(true);
			setBaseButtonDisabled(true);
			setTouched({
				invites: [{email: true}],
			});
		}

		setIsLoadingUserInvitation(true);

		let displaySuccess = true;
		const invitedAccounts = [];

		const context = {
			displayErrors: false,
			displayServerError: false,
			displaySuccess: false,
		};

		for (const inviteMember of inviteMembers) {
			try {
				const {data} = await assignUserWithAccount({
					context,
					variables: {
						accountKey: project.accountKey,
						emailAddress: inviteMember.email,
					},
				});

				const createdOrAssignedUser =
					data?.createAccountUserAccountByExternalReferenceCodeByEmailAddress;

				if (!createdOrAssignedUser) {
					throw new Error(
						`Failed to create or assign user ${inviteMember.email}`
					);
				}

				const needsNameUpdate =
					createdOrAssignedUser.givenName !==
						inviteMember.givenName ||
					createdOrAssignedUser.familyName !==
						inviteMember.familyName;

				if (needsNameUpdate) {
					try {
						await updateUserAccount({
							context,
							variables: {
								userAccount: {
									emailAddress: inviteMember.email,
									familyName: inviteMember.familyName,
									givenName: inviteMember.givenName,
								},
								userAccountId: createdOrAssignedUser.id,
							},
						});
					}
					catch (error) {
						console.error(
							`Failed to update name for ${inviteMember.email}`,
							error
						);

						Liferay.Util.openToast({
							...DEFAULT_WARNING,
							message: i18n.sub(
								'user-x-was-invited-successfully-but-there-was-a-problem-updating-their-name',
								[inviteMember.givenName]
							),
						});
					}
				}

				const invitedMemberRoles = inviteMember.role || [];

				for (const inviteRole of invitedMemberRoles) {
					try {
						await addContactRoleNameByEmailByProject(
							project.accountKey,
							encodeURI(inviteMember.email),
							inviteMember.givenName,
							inviteMember.familyName,
							oAuthToken,
							provisioningServerAPI,
							inviteRole.raysourceName
						);

						await assignUserAccountWithAccountRole({
							context,
							variables: {
								accountKey: project.accountKey,
								accountRoleId: inviteRole.id,
								emailAddress: inviteMember.email,
							},
						});
					}
					catch (error) {
						if (error.cause === STATUS_CODE.conflict) {
							await assignUserAccountWithAccountRole({
								context,
								variables: {
									accountKey: project.accountKey,
									accountRoleId: inviteRole.id,
									emailAddress: inviteMember.email,
								},
							});
						}
						else {
							await deleteUserAccount({
								context,
								variables: {
									accountKey: project.accountKey,
									emailAddress: inviteMember.email,
								},
							});

							throw new Error('Error', {cause: error.cause});
						}
					}
				}

				invitedAccounts.push(inviteMember);
			}
			catch (error) {
				console.error(error);

				displaySuccess = false;

				Liferay.Util.openToast({
					...DEFAULT_WARNING,
					message: i18n.sub('unable-to-invite-x', [
						inviteMember.givenName,
					]),
				});
			}
		}

		if (invitedAccounts.length) {
			const newMembersData = await addTeamMemberInvitation({
				context: {
					displaySuccess,
					type: 'liferay-rest',
				},
				notifyOnNetworkStatusChange: false,
				variables: {
					TeamMembersInvitation: invitedAccounts.flatMap(
						({email, familyName, givenName, role}) =>
							role?.map((roleInvited) => ({
								email,
								familyName,
								givenName,
								r_accountEntryToDXPCloudEnvironment_accountEntryId:
									project?.id,
								role: roleInvited,
							}))
					),
				},
			});

			if (newMembersData) {
				if (mutateUserData) {
					mutateUserData(newMembersData);
				}
				handlePage();
			}
		}

		setIsLoadingUserInvitation(false);
	};

	const isAnyEmptyEmail = () => {
		const hasEmptyEmails = values?.invites?.some(({email}) => !email);

		setshowEmptyEmailError(hasEmptyEmails);

		return hasEmptyEmails;
	};

	return (
		<Layout
			footerProps={{
				leftButton: (
					<Button borderless onClick={handlePage}>
						{leftButton}
					</Button>
				),
				middleButton: (
					<Button
						disabled={
							baseButtonDisabled ||
							isLoadingUserInvitation ||
							!roleSelectorFilled
						}
						displayType="primary"
						isLoading={isLoadingUserInvitation}
						onClick={handleSubmit}
					>
						{i18n.translate('send-invitations')}
					</Button>
				),
			}}
			headerProps={{
				helper: i18n.translate(
					'team-members-will-receive-an-email-invitation-to-access-this-project-on-customer-portal'
				),
				title: i18n.translate('invite-your-team-members'),
			}}
		>
			{hasInitialError && (
				<Badge>
					<span className="pl-1">
						{i18n.translate(
							'add-at-least-one-user-s-email-to-send-an-invitation'
						)}
					</span>
				</Badge>
			)}

			<FieldArray
				name="invites"
				render={({pop, push}) => (
					<>
						<div
							className={classNames('overflow-auto px-3', {
								'invites-form': project.maxRequestors > 0,
							})}
						>
							<div className="px-3">
								<label>{i18n.translate('project-name')}</label>

								<p className="invites-project-name text-neutral-6 text-paragraph-lg">
									<strong>{project.name}</strong>
								</p>
							</div>

							<ClayForm.Group className="m-0">
								{values?.invites?.map((invite, index) => (
									<TeamMemberInputs
										administratorsAssetsAvailable={
											availableAdminsRoles
										}
										disableError={hasInitialError}
										errors={errors}
										id={index}
										invite={invite}
										key={index}
										options={accountRolesOptions}
										placeholderEmail={`username@${
											project?.code?.toLowerCase() ||
											'example'
										}.com`}
										selectOnChange={(roleSelected) => {
											const isPartnerMember =
												roleSelected.partnerMemberRoles
													.active;

											if (isPartnerMember) {
												const memberRoles =
													roleSelected
														.partnerMemberRoles
														.roles;
												const updatedMemberRoles =
													memberRoles.filter(
														(role) => role.active
													);

												return updatedMemberRoles?.map(
													(updateRole, roleIndex) => {
														setFieldValue(
															`invites[${index}].role[${roleIndex}]`,
															accountRoles?.find(
																({id}) =>
																	id ===
																	+updateRole.value
															)
														);
													}
												);
											}

											const accountRoleItem =
												Object.values(
													roleSelected
												).filter((role) => role.active);

											return accountRoleItem?.map(
												(updateRole, roleIndex) => {
													setFieldValue(
														`invites[${index}].role[${roleIndex}]`,
														accountRoles?.find(
															({id}) =>
																id ===
																+updateRole.value
														)
													);
												}
											);
										}}
										setRoleSelectorFilled={
											setRoleSelectorFilled
										}
									/>
								))}
							</ClayForm.Group>

							{showEmptyEmailError && (
								<Badge badgeClassName="cp-badge-error-message">
									<span className="pl-1">
										{i18n.translate(
											'please-enter-an-email-address'
										)}
									</span>
								</Badge>
							)}

							<div className="ml-3 my-4">
								{values?.invites?.length > 1 && (
									<Button
										className="mr-3 py-2 text-brandy-secondary"
										displayType="secondary"
										onClick={() => {
											const removedItem = pop();

											if (
												removedItem.role.name ===
													ROLE_TYPES.admin.name ||
												removedItem.role.name ===
													ROLE_TYPES.requester.name
											) {
												setAvailableAdminsRoles(
													(previousAdmins) =>
														previousAdmins + 1
												);
											}
										}}
										prependIcon="hr"
										small
									>
										{i18n.translate('remove-this-member')}
									</Button>
								)}

								{values?.invites?.length <
									MAXIMUM_INVITES_COUNT && (
									<Button
										className="btn-outline-primary cp-btn-add-members py-2 rounded-xs"
										onClick={() => {
											setBaseButtonDisabled(false);
											setRoleSelectorFilled(false);

											const hasEmptyEmails =
												isAnyEmptyEmail();

											if (!hasEmptyEmails) {
												push(
													getInitialInvite([
														accountMemberRole,
													])
												);
											}
										}}
										prependIcon="plus"
										small
									>
										{i18n.translate('add-more-members')}
									</Button>
								)}
							</div>
						</div>
						{project.maxRequestors > 0 && (
							<div className="invites-helper px-3">
								<div className="mx-3 pt-3">
									<h5 className="text-neutral-7">
										{`${
											projectHasSLAGoldPlatinum
												? i18n.translate(
														'support-seats'
													)
												: i18n.translate(
														'administrator-roles'
													)
										}
										  ${i18n.sub('available-x-of-x', [
												availableAdminsRoles < 0
													? 0
													: availableAdminsRoles,
												project.maxRequestors,
											])}`}
									</h5>

									<p className="mb-0 text-neutral-7 text-paragraph-sm">
										{project.maxRequestors > 1
											? i18n.sub(
													'only-x-members-for-this-project-including-yourself-can-have-role-permissions-administrators-requesters-to-open-support-tickets',
													[project.maxRequestors]
												)
											: i18n.sub(
													'only-x-member-for-this-project-including-yourself-can-have-role-permissions-administrators-requesters-to-open-support-tickets',
													[project.maxRequestors]
												)}

										<a
											className="font-weight-bold text-neutral-9"
											href={articleAccountSupportURL}
											rel="noreferrer"
											target="_blank"
										>
											{i18n.translate(
												'learn-more-about-customer-portal-roles'
											)}
										</a>
									</p>
								</div>
							</div>
						)}
					</>
				)}
			/>
		</Layout>
	);
};

const InviteTeamMembersForm = (props) => {
	return (
		<Formik
			initialValues={{
				invites: [...new Array(INITIAL_INVITES_COUNT)].map(() =>
					getInitialInvite()
				),
			}}
			validateOnChange
		>
			{(formikProps) => (
				<InviteTeamMembersPage {...props} {...formikProps} />
			)}
		</Formik>
	);
};

export default InviteTeamMembersForm;
