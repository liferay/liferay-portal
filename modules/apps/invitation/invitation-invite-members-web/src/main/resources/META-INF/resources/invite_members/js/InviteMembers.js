/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker, Text} from '@clayui/core';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClayModal, {useModal} from '@clayui/modal';
import ClayTable from '@clayui/table';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {debounce, fetch, sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

export function InviteMembers({
	assignRolesPermission,
	getAvailableUsersURL,
	manageTeamsPermission,
	portletNamespace,
	roles,
	scopeGroupId,
	sendInvitesURL,
	teams,
}) {
	const {observer, onOpenChange, open} = useModal();

	const [invitedEmails, setInvitedEmails] = useState([]);
	const [invitedUsers, setInvitedUsers] = useState([]);
	const [invitedUsersIds, setInvitedUsersIds] = useState([]);
	const [roleId, setRoleId] = useState('');
	const [teamId, setTeamId] = useState('');
	const [users, setUsers] = useState([]);
	const [viewMoreCount, setViewMoreCount] = useState(1);

	const fetchAvailableUsers = useCallback(
		({end = 50, keywords = ''} = {}) => {
			const url = new URL(getAvailableUsersURL);

			const body = new URLSearchParams(
				Liferay.Util.ns(portletNamespace, {
					end,
					keywords,
					start: 0,
				})
			);

			fetch(url, {
				body,
				method: 'POST',
			})
				.then((response) => response.json())
				.then(({users}) => {
					setUsers(users);
				});
		},
		[getAvailableUsersURL, portletNamespace]
	);

	useEffect(() => {
		fetchAvailableUsers({end: viewMoreCount * 50});
	}, [fetchAvailableUsers, viewMoreCount]);

	const onAddEmailClickHandler = () => {
		const emailValue = document.getElementById(
			`${portletNamespace}emailAddress`
		).value;

		if (
			emailValue &&
			!invitedEmails.find((email) => email === emailValue)
		) {
			setInvitedEmails([...invitedEmails, emailValue]);
		}
	};

	const onUserClickHandler = (user) => {
		if (
			invitedUsers.find(
				(invitedUser) => invitedUser.userId === user.userId
			)
		) {
			removeInvitedUser(user);
		}
		else {
			setInvitedUsers([...invitedUsers, user]);

			setInvitedUsersIds([...invitedUsersIds, user.userId]);
		}
	};

	const removeInvitedUser = ({userId}) => {
		setInvitedUsers(
			invitedUsers.filter((invitedUser) => invitedUser.userId !== userId)
		);

		setInvitedUsersIds(
			invitedUsersIds.filter(
				(invitedUsersId) => invitedUsersId !== userId
			)
		);
	};

	const searchUsersCallback = (event) => {
		const debouncedFetch = debounce((keywords) => {
			fetchAvailableUsers({keywords});
		}, 1000);

		debouncedFetch(event.target.value);
	};

	const Users = () => {
		return users.map((user) => {
			return (
				<div key={user.userId}>
					<ClayList.Item className="border-0 p-1" flex>
						<ClayList.ItemField className="justify-content-center">
							{invitedUsersIds.find(
								(invitedUsersId) =>
									invitedUsersId === user.userId
							) ? (
								<ClayButtonWithIcon
									aria-label={sub(
										Liferay.Language.get('remove-x'),
										Liferay.Language.get('user')
									)}
									className="lfr-portal-tooltip"
									displayType="unstyled"
									onClick={() => onUserClickHandler(user)}
									size="xs"
									symbol="times"
									title={Liferay.Language.get('remove')}
								/>
							) : user.hasPendingMemberRequest ? (
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get(
										'user-already-invited'
									)}
									className="lfr-portal-tooltip"
									displayType="unstyled"
									onClick={() => onUserClickHandler(user)}
									size="xs"
									symbol="check"
									title={Liferay.Language.get(
										'user-already-invited'
									)}
								/>
							) : (
								<ClayButtonWithIcon
									aria-label={sub(
										Liferay.Language.get('add-x'),
										Liferay.Language.get('user')
									)}
									className="lfr-portal-tooltip"
									displayType="unstyled"
									onClick={() => onUserClickHandler(user)}
									size="xs"
									symbol="plus"
									title={Liferay.Language.get('add')}
								/>
							)}
						</ClayList.ItemField>

						<ClayList.ItemField className="justify-content-center pl-0">
							{user.userFullName}
						</ClayList.ItemField>

						<ClayList.ItemField className="justify-content-center pl-0 pt-1">
							<Text color="muted" size={2}>
								{user.userEmailAddress}
							</Text>
						</ClayList.ItemField>
					</ClayList.Item>
				</div>
			);
		});
	};

	const InvitedEmails = () => {
		return (
			<ClayTable borderless>
				<ClayTable.Body>
					{invitedEmails.map((invitedEmail) => {
						return (
							<ClayTable.Row key={invitedEmail}>
								<ClayTable.Cell className="pb-0 pt-0">
									<Text size={3} weight="semi-bold">
										{invitedEmail}
									</Text>
								</ClayTable.Cell>

								<ClayTable.Cell className="pb-0 pt-0 text-right">
									<ClayButtonWithIcon
										aria-label={sub(
											Liferay.Language.get('remove-x'),
											[invitedEmail]
										)}
										className="lfr-portal-tooltip"
										displayType="unstyled"
										onClick={() => {
											setInvitedEmails(
												invitedEmails.filter(
													(emailToRemove) =>
														emailToRemove !==
														invitedEmail
												)
											);
										}}
										symbol="times"
										title={Liferay.Language.get('remove')}
									/>
								</ClayTable.Cell>
							</ClayTable.Row>
						);
					})}
				</ClayTable.Body>
			</ClayTable>
		);
	};

	const InvitedUsers = () => {
		return (
			<ClayTable borderless>
				<ClayTable.Body>
					{invitedUsers.map((invitedUser) => {
						return (
							<ClayTable.Row key={invitedUser.userId}>
								<ClayTable.Cell className="pb-0 pt-0">
									<p className="c-mb-0 font-weight-semi-bold small">
										{invitedUser.userFullName}
									</p>
								</ClayTable.Cell>

								<ClayTable.Cell className="pb-0 pt-0">
									{invitedUser.userEmailAddress}
								</ClayTable.Cell>

								<ClayTable.Cell className="pb-0 pt-0 text-right">
									<ClayButtonWithIcon
										aria-label={sub(
											Liferay.Language.get('remove-x'),
											[invitedUser.userFullName]
										)}
										className="lfr-portal-tooltip"
										displayType="unstyled"
										onClick={() => {
											removeInvitedUser(invitedUser);
										}}
										symbol="times"
										title={Liferay.Language.get('remove')}
									/>
								</ClayTable.Cell>
							</ClayTable.Row>
						);
					})}
				</ClayTable.Body>
			</ClayTable>
		);
	};

	return (
		<>
			{open && (
				<ClayModal
					className="so-portlet-invite-members"
					observer={observer}
					size="md"
				>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('invite-members')}
					</ClayModal.Header>

					<ClayModal.Body scrollable>
						<ClayForm
							action={sendInvitesURL}
							id={`${portletNamespace}fm`}
							method="post"
							name={`${portletNamespace}fm`}
						>
							<ClayInput
								name={`${portletNamespace}groupId`}
								type="hidden"
								value={scopeGroupId}
							/>

							<ClayInput
								name={`${portletNamespace}receiverUserIds`}
								type="hidden"
								value={invitedUsersIds.join(',')}
							/>

							<ClayInput
								name={`${portletNamespace}receiverEmailAddresses`}
								type="hidden"
								value={invitedEmails.join(',')}
							/>

							<ClayInput
								name={`${portletNamespace}invitedRoleId`}
								type="hidden"
								value={roleId}
							/>

							<ClayInput
								name={`${portletNamespace}invitedTeamId`}
								type="hidden"
								value={teamId}
							/>

							<div className="c-mb-3">
								<label>
									{Liferay.Language.get('find-members')}
								</label>

								<span className="small text-muted">
									<ClayIcon
										className="ml-2 mr-1"
										symbol="check"
									/>

									{Liferay.Language.get(
										'previous-invitation-was-sent'
									)}
								</span>

								<ClayForm.Group className="input-text-wrapper">
									<ClayInput
										id={`${portletNamespace}inviteUserSearch`}
										name={`${portletNamespace}userName`}
										onChange={(event) => {
											searchUsersCallback(event);
										}}
										placeholder={Liferay.Language.get(
											'search'
										)}
									/>
								</ClayForm.Group>

								<div
									className="search"
									id={`${portletNamespace}membersList`}
								>
									{!users.length && (
										<Text color="muted" size={3}>
											{Liferay.Language.get(
												'there-are-no-users-to-invite'
											)}
										</Text>
									)}

									{!!users.length && <Users />}

									{users.length / (viewMoreCount * 50) >
										1 && (
										<div className="d-flex justify-content-center">
											<ClayButton
												displayType="secondary"
												onClick={() => {
													setViewMoreCount(
														viewMoreCount + 1
													);
												}}
											>
												{Liferay.Language.get(
													'view-more'
												)}
											</ClayButton>
										</div>
									)}
								</div>

								<label>
									{Liferay.Language.get(
										'email-addresses-to-send-invite'
									)}

									<ClayTooltipProvider>
										<span
											data-tooltip-align="top"
											title={Liferay.Language.get(
												'to-add,-click-members-on-the-top-list'
											)}
										>
											<ClayIcon
												className="ml-1 text-secondary"
												symbol="question-circle-full"
											/>
										</span>
									</ClayTooltipProvider>
								</label>

								<div
									id={`${portletNamespace}invitedMembersList`}
								>
									{!!invitedUsers && <InvitedUsers />}
								</div>

								<div className="button-holder controls">
									<ClayForm.Group>
										<label
											htmlFor={`${portletNamespace}emailAddress`}
										>
											{Liferay.Language.get(
												'invite-by-email'
											)}
										</label>

										<ClayInput.Group>
											<ClayInput.GroupItem prepend>
												<ClayInput
													id={`${portletNamespace}emailAddress`}
													type="text"
												/>
											</ClayInput.GroupItem>

											<ClayInput.GroupItem append shrink>
												<ClayButton
													className="c-mb-3"
													displayType="secondary"
													name={`${portletNamespace}emailButton`}
													onClick={() =>
														onAddEmailClickHandler()
													}
													type="button"
												>
													{Liferay.Language.get(
														'add-email-address'
													)}
												</ClayButton>
											</ClayInput.GroupItem>
										</ClayInput.Group>
									</ClayForm.Group>
								</div>

								<label>
									{Liferay.Language.get(
										'email-addresses-to-send-invite'
									)}
								</label>

								<div id={`${portletNamespace}invitedEmailList`}>
									{!!invitedEmails && <InvitedEmails />}
								</div>

								{roles.length !== 0 &&
									assignRolesPermission && (
										<ClayForm.Group>
											<label
												htmlFor="roleSelector"
												id="roleSelectorLabel"
											>
												{Liferay.Language.get(
													'invite-to-role'
												)}
											</label>

											<Picker
												aria-labelledby="roleSelectorLabel"
												id="roleSelector"
												items={roles}
												messages={{
													itemDescribedby:
														Liferay.Language.get(
															'you-are-currently-on-a-text-element,-inside-of-a-list-box'
														),
													itemSelected:
														Liferay.Language.get(
															'x-selected'
														),
													scrollToBottomAriaLabel:
														Liferay.Language.get(
															'scroll-to-bottom'
														),
													scrollToTopAriaLabel:
														Liferay.Language.get(
															'scroll-to-top'
														),
												}}
												onSelectionChange={(roleId) =>
													setRoleId(roleId)
												}
											>
												{({label, value}) => (
													<Option
														key={value}
														textValue={label}
													>
														{label}
													</Option>
												)}
											</Picker>
										</ClayForm.Group>
									)}

								{teams.length !== 0 &&
									manageTeamsPermission && (
										<ClayForm.Group>
											<label
												htmlFor="teamSelector"
												id="teamSelectorLabel"
											>
												{Liferay.Language.get(
													'invite-to-team'
												)}
											</label>

											<Picker
												aria-labelledby="teamSelectorLabel"
												id="teamSelector"
												items={teams}
												onSelectionChange={(teamId) =>
													setTeamId(teamId)
												}
											>
												{({label, value}) => (
													<Option
														key={value}
														textValue={label}
													>
														{label}
													</Option>
												)}
											</Picker>
										</ClayForm.Group>
									)}
							</div>
						</ClayForm>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={() => onOpenChange(false)}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton
									form={`${portletNamespace}fm`}
									type="submit"
								>
									{Liferay.Language.get('send-invitations')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}
			<ClayButton
				displayType="secondary"
				onClick={() => onOpenChange(true)}
			>
				{Liferay.Language.get('invite-members')}
			</ClayButton>
		</>
	);
}
