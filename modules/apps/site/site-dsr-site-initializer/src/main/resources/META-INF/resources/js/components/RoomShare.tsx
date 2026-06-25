/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayDatePicker from '@clayui/date-picker';
import DropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayModal from '@clayui/modal';
import MultiSelect from '@clayui/multi-select';
import ClaySticker from '@clayui/sticker';
import {openToast} from 'frontend-js-components-web';
import React, {useCallback, useEffect, useState} from 'react';

import RoomService from '../common/services/RoomService';
import {IRoomShareProps, IUserAccount} from '../common/utils/types';

export const DSR_SITE_ROLES = [
	{
		description: Liferay.Language.get(
			'users-can-manage-pages-and-documents-add-room-comments-and-share-the-room'
		),
		key: 'DSR Room Collaborator',
		label: Liferay.Language.get('room-collaborator'),
	},
	{
		description: Liferay.Language.get(
			'users-can-view-room-content-add-comments-upload-documents-and-share-the-room'
		),
		key: 'DSR Content Contributor',
		label: Liferay.Language.get('content-contributor'),
	},
	{
		description: Liferay.Language.get(
			'users-can-view-documents-and-add-comments-but-cannot-upload-documents'
		),
		key: 'Site Member',
		label: Liferay.Language.get('viewer'),
	},
];
const ASSIGNABLE_ROLE_KEYS_BY_ROLE_KEY: Record<string, string[]> = {
	'DSR Content Contributor': ['DSR Content Contributor', 'Site Member'],
	'DSR Room Collaborator': ['DSR Content Contributor', 'Site Member'],
};
const EXPIRATION_WARNING_DAYS = 7;
const OWNER_ROLE_KEY = 'Site Owner';

function getDateInputValue(membershipExpirationDate?: string): string {
	if (!membershipExpirationDate) {
		return '';
	}

	const date = new Date(membershipExpirationDate);

	return [
		String(date.getUTCFullYear()),
		String(date.getUTCMonth() + 1).padStart(2, '0'),
		String(date.getUTCDate()).padStart(2, '0'),
	].join('-');
}

function getExpirationDateTime(date: string): string | undefined {
	if (!date) {
		return undefined;
	}

	const [year, month, day] = date.split('-').map(Number);

	return new Date(Date.UTC(year, month - 1, day, 23, 59, 59)).toISOString();
}

function getExpirationLabel(membershipExpirationDate: string): string {
	return new Date(membershipExpirationDate).toLocaleDateString(
		Liferay.ThemeDisplay.getLanguageId().replace('_', '-'),
		{
			day: 'numeric',
			month: 'short',
			timeZone: 'UTC',
			year: 'numeric',
		}
	);
}

function isExpiringSoon(membershipExpirationDate: string): boolean {
	const daysUntilExpiration =
		(new Date(membershipExpirationDate).getTime() - Date.now()) /
		(1000 * 60 * 60 * 24);

	return daysUntilExpiration <= EXPIRATION_WARNING_DAYS;
}

function getRoleLabel(roleKey: string | undefined): string {
	const role = DSR_SITE_ROLES.find((option) => option.key === roleKey);

	return role ? role.label : Liferay.Language.get('viewer');
}

function getUserDisplayName(user: IUserAccount): string {
	return user.name || user.emailAddress || '';
}

function getUserInitials(
	name: string | undefined,
	email: string | undefined
): string {
	if (name) {
		const trimmedName = name.trim();

		if (trimmedName.length) {
			return trimmedName[0].toUpperCase();
		}
	}

	if (email && !!email.length) {
		return email[0].toUpperCase();
	}

	return '';
}

function isCompleteDate(value: string): boolean {
	return /^\d{4}-\d{2}-\d{2}$/.test(value);
}

function isEmailAddressValid(email: string) {
	const emailRegex = /.+@.+\..+/i;

	return emailRegex.test(email);
}

function RoomShare({
	canAssignAllRoles = false,
	closeModal,
	readOnly = false,
	roomId,
}: IRoomShareProps) {
	const [bannerDismissed, setBannerDismissed] = useState(false);
	const [editingDate, setEditingDate] = useState('');
	const [editingDatePickerExpanded, setEditingDatePickerExpanded] =
		useState(false);
	const [editingUserId, setEditingUserId] = useState<number | null>(null);
	const [emailAddresses, setEmailAddresses] = useState<
		Array<{label: string; value: string}>
	>([]);
	const [expirationDate, setExpirationDate] = useState('');
	const [expirationDatePickerExpanded, setExpirationDatePickerExpanded] =
		useState(false);
	const [loading, setLoading] = useState(false);
	const [roleKey, setRoleKey] = useState('Site Member');
	const [users, setUsers] = useState<IUserAccount[]>([]);
	const currentUserId = Number(Liferay.ThemeDisplay.getUserId());
	const minExpirationDate = getDateInputValue(new Date().toISOString());

	const currentUserRoleKey = users.find(
		(user) => user.id === currentUserId
	)?.roleKey;

	const canManageAllRoles =
		canAssignAllRoles || currentUserRoleKey === OWNER_ROLE_KEY;

	const assignableRoleKeys = canManageAllRoles
		? DSR_SITE_ROLES.map((role) => role.key)
		: ASSIGNABLE_ROLE_KEYS_BY_ROLE_KEY[currentUserRoleKey ?? ''] ?? [];

	const assignableRoles = DSR_SITE_ROLES.filter((role) =>
		assignableRoleKeys.includes(role.key)
	);

	const loadUsers = useCallback(async () => {
		setLoading(true);

		try {
			const [usersList, invitedMembersList] = await Promise.all([
				RoomService.getRoomUserAccounts(roomId),
				RoomService.getRoomInvitedMembers(roomId),
			]);

			setUsers([
				...usersList,
				...invitedMembersList.map((invitedMember) => ({
					emailAddress: invitedMember.emailAddress,
					id: invitedMember.id,
					isInvitedMember: true,
					membershipExpirationDate:
						invitedMember.membershipExpirationDate,
					name: '',
					ownerId: invitedMember.ownerId,
					roleKey: invitedMember.roleKey,
				})),
			]);
		}
		catch (error) {
			const errorMessage = (error as Error).message;

			openToast({
				message: errorMessage,
				type: 'danger',
			});
		}
		finally {
			setLoading(false);
		}
	}, [roomId]);

	const handleInvite = useCallback(async () => {
		if (!emailAddresses.length) {
			openToast({
				message: Liferay.Language.get('please-enter-an-email-address'),
				type: 'danger',
			});

			return;
		}

		const emails = emailAddresses.map((item) => item.value);

		const invalidEmails = emails.filter(
			(email) => !isEmailAddressValid(email)
		);

		if (invalidEmails.length) {
			openToast({
				message: Liferay.Language.get(
					'please-enter-a-valid-email-address'
				),
				type: 'danger',
			});

			return;
		}

		setLoading(true);

		try {
			await Promise.all(
				emails.map((email) =>
					RoomService.addRoomUserAccount(roomId, {
						emailAddress: email,
						membershipExpirationDate:
							getExpirationDateTime(expirationDate),
						roleKey,
					})
				)
			);

			setEmailAddresses([]);
			setExpirationDate('');
			setRoleKey('Site Member');

			openToast({
				message:
					emails.length === 1
						? Liferay.Language.get('user-was-invited-successfully')
						: Liferay.Language.get(
								'users-were-invited-successfully'
							),
				type: 'success',
			});

			await loadUsers();
		}
		catch (error) {
			openToast({
				message: (error as Error).message,
				type: 'danger',
			});
		}
		finally {
			setLoading(false);
		}
	}, [emailAddresses, expirationDate, loadUsers, roleKey, roomId]);

	const handleRemoveUser = useCallback(
		async (userId: number, isInvitedMember?: boolean) => {
			setLoading(true);

			try {
				if (isInvitedMember) {
					await RoomService.deleteRoomInvitedMember(roomId, userId);
				}
				else {
					await RoomService.deleteRoomUserAccount(roomId, userId);
				}

				openToast({
					message: Liferay.Language.get(
						'user-was-removed-successfully'
					),
					type: 'success',
				});

				loadUsers();
			}
			catch (error) {
				openToast({
					message: Liferay.Language.get('an-error-occurred'),
					type: 'danger',
				});
			}
			finally {
				setLoading(false);
			}
		},
		[loadUsers, roomId]
	);

	const handleUpdateUser = useCallback(
		async ({
			isInvitedMember,
			membershipExpirationDate,
			roleKey,
			userId,
		}: {
			isInvitedMember?: boolean;
			membershipExpirationDate?: string;
			roleKey?: string;
			userId: number;
		}) => {
			setLoading(true);

			try {
				const userAccount = {
					membershipExpirationDate,
					roleKey,
				};

				if (isInvitedMember) {
					await RoomService.updateRoomInvitedMember(
						roomId,
						userId,
						userAccount
					);
				}
				else {
					await RoomService.updateRoomUserAccount(
						roomId,
						userId,
						userAccount
					);
				}

				setEditingDate('');
				setEditingUserId(null);

				openToast({
					message: Liferay.Language.get(
						'your-request-completed-successfully'
					),
					type: 'success',
				});

				await loadUsers();
			}
			catch (error) {
				openToast({
					message: (error as Error).message,
					type: 'danger',
				});
			}
			finally {
				setLoading(false);
			}
		},
		[loadUsers, roomId]
	);

	useEffect(() => {
		loadUsers();
	}, [loadUsers]);

	const canEditMember = (user: IUserAccount): boolean => {
		if (readOnly || user.roleKey === OWNER_ROLE_KEY) {
			return false;
		}

		if (
			canManageAllRoles ||
			(user.isInvitedMember && user.ownerId === currentUserId)
		) {
			return true;
		}

		return assignableRoles.some((role) => role.key === user.roleKey);
	};

	const renderContent = () => {
		const expiringSoonCount = users.filter(
			(user) =>
				user.membershipExpirationDate &&
				isExpiringSoon(user.membershipExpirationDate)
		).length;

		return (
			<>
				{!bannerDismissed && expiringSoonCount > 0 && (
					<ClayAlert
						displayType="info"
						onClose={() => setBannerDismissed(true)}
					>
						{Liferay.Util.sub(
							expiringSoonCount === 1
								? Liferay.Language.get(
										'x-user-has-access-expiring-within-x-days'
									)
								: Liferay.Language.get(
										'x-users-have-access-expiring-within-x-days'
									),
							String(expiringSoonCount),
							String(EXPIRATION_WARNING_DAYS)
						)}
					</ClayAlert>
				)}

				<div className="mb-4">
					<label className="d-block mb-3">
						{Liferay.Language.get('email-addresses')}
					</label>

					<div className="align-items-end d-flex">
						<div className="dsr-site-role-input flex-grow-1 mr-3 position-relative">
							<MultiSelect
								allowDuplicateValues={false}
								autoFocus={true}
								data-testid="emailAddressesInput"
								disabled={loading || readOnly}
								inputName="userEmailAddresses"
								items={emailAddresses}
								onItemsChange={(emails: Array<any>) =>
									setEmailAddresses(emails)
								}
								placeholder={Liferay.Language.get(
									'type-a-comma-or-press-enter-to-input-email-addresses'
								)}
							/>

							<DropDown
								closeOnClick={true}
								trigger={
									<ClayButton
										className="dsr-site-role-trigger-button"
										data-testid="roleKeyButton"
										disabled={loading || readOnly}
										displayType="secondary"
										size="xs"
									>
										{getRoleLabel(roleKey)}
									</ClayButton>
								}
								triggerIcon="caret-bottom"
							>
								<DropDown.ItemList items={assignableRoles}>
									{(item: any) => (
										<DropDown.Item
											data-testid={`roleKeyItem_${item.label}`}
											key={item.key}
											onClick={() => setRoleKey(item.key)}
										>
											<div className="font-weight-semi-bold">
												{item.label}
											</div>

											<div className="small text-secondary">
												{item.description}
											</div>
										</DropDown.Item>
									)}
								</DropDown.ItemList>
							</DropDown>
						</div>

						<ClayButton
							data-testid="inviteButton"
							disabled={loading || readOnly}
							onClick={handleInvite}
						>
							{Liferay.Language.get('invite')}
						</ClayButton>
					</div>

					<div className="align-items-center border d-flex justify-content-between mt-3 px-3 py-2 rounded">
						<span>
							<ClayIcon
								className="mr-2 text-secondary"
								symbol="calendar"
							/>

							{Liferay.Language.get('access-valid-until')}

							<span className="ml-2 text-2 text-secondary">
								{Liferay.Language.get('optional')}
							</span>
						</span>

						<div className="dsr-expiration-date-picker">
							<ClayDatePicker
								disabled={loading || readOnly}
								expanded={expirationDatePickerExpanded}
								min={minExpirationDate}
								onChange={(value: string) => {
									setExpirationDate(value);

									if (isCompleteDate(value)) {
										setExpirationDatePickerExpanded(false);
									}
								}}
								onExpandedChange={
									setExpirationDatePickerExpanded
								}
								placeholder="YYYY-MM-DD"
								time={false}
								value={expirationDate}
								years={{
									end: new Date().getFullYear() + 10,
									start: new Date().getFullYear(),
								}}
							/>
						</div>
					</div>
				</div>

				<div className="mt-4">
					<div className="text-3 text-secondary">
						{Liferay.Util.sub(
							Liferay.Language.get('who-has-access-x-users'),
							String(users.length)
						)}
					</div>

					<div className="mt-3 text-3">
						{users.map((user) => (
							<div
								className="align-items-center d-flex justify-content-between mb-3 user-row"
								key={user.id}
							>
								<div className="align-items-center d-flex">
									<div className="mr-2">
										{user.image ? (
											<ClaySticker
												shape="circle"
												size="lg"
											>
												<ClaySticker.Image
													alt={getUserDisplayName(
														user
													)}
													src={user.image}
												/>
											</ClaySticker>
										) : (
											<ClaySticker
												className="sticker-user-icon"
												shape="circle"
												size="lg"
											>
												{getUserInitials(
													user.name,
													user.emailAddress
												)}
											</ClaySticker>
										)}
									</div>

									<div className="align-items-center d-flex">
										<span className="font-weight-semi-bold">
											{getUserDisplayName(user)}
										</span>
									</div>
								</div>

								<div className="align-items-center d-flex">
									{user.roleKey === OWNER_ROLE_KEY ? (
										<span className="text-secondary">
											{Liferay.Language.get('owner')}
										</span>
									) : (
										<>
											{editingUserId === user.id ? (
												<div className="align-items-center d-flex mr-3">
													<div className="dsr-expiration-date-picker">
														<ClayDatePicker
															disabled={loading}
															expanded={
																editingDatePickerExpanded
															}
															min={
																minExpirationDate
															}
															onChange={(
																value: string
															) => {
																setEditingDate(
																	value
																);

																if (
																	isCompleteDate(
																		value
																	)
																) {
																	setEditingDatePickerExpanded(
																		false
																	);
																}
															}}
															onExpandedChange={
																setEditingDatePickerExpanded
															}
															placeholder="YYYY-MM-DD"
															time={false}
															value={editingDate}
															years={{
																end:
																	new Date().getFullYear() +
																	10,
																start: new Date().getFullYear(),
															}}
														/>
													</div>

													<ClayButton
														className="ml-2 text-secondary"
														data-testid={`confirmExpiration_${user.id}`}
														disabled={loading}
														displayType="unstyled"
														onClick={() =>
															handleUpdateUser({
																isInvitedMember:
																	user.isInvitedMember,
																membershipExpirationDate:
																	getExpirationDateTime(
																		editingDate
																	),
																roleKey:
																	user.roleKey,
																userId: user.id,
															})
														}
													>
														<ClayIcon symbol="check" />
													</ClayButton>

													<ClayButton
														className="ml-3 text-secondary"
														disabled={loading}
														displayType="unstyled"
														onClick={() => {
															setEditingDate('');
															setEditingUserId(
																null
															);
														}}
													>
														<ClayIcon symbol="times" />
													</ClayButton>
												</div>
											) : (
												<div className="align-items-center d-flex mr-3">
													{user.membershipExpirationDate ? (
														<ClayLabel
															className="dsr-expiration-label"
															displayType={
																isExpiringSoon(
																	user.membershipExpirationDate
																)
																	? 'warning'
																	: 'info'
															}
														>
															{isExpiringSoon(
																user.membershipExpirationDate
															) && (
																<ClayIcon
																	className="mr-1"
																	symbol="warning-full"
																/>
															)}

															{getExpirationLabel(
																user.membershipExpirationDate
															)}
														</ClayLabel>
													) : (
														<span className="text-secondary">
															{Liferay.Language.get(
																'no-expiration'
															)}
														</span>
													)}

													{canEditMember(user) && (
														<ClayButton
															className="ml-2 text-secondary"
															data-testid={`editExpiration_${user.id}`}
															disabled={loading}
															displayType="unstyled"
															onClick={() => {
																setEditingDate(
																	getDateInputValue(
																		user.membershipExpirationDate
																	)
																);
																setEditingUserId(
																	user.id
																);
															}}
														>
															<ClayIcon symbol="pencil" />
														</ClayButton>
													)}
												</div>
											)}

											{canEditMember(user) ? (
												<DropDown
													closeOnClick
													trigger={
														<ClayButton
															className="text-secondary"
															disabled={loading}
															displayType="unstyled"
														>
															{getRoleLabel(
																user.roleKey
															)}

															<ClayIcon
																className="ml-1"
																symbol="caret-bottom"
															/>
														</ClayButton>
													}
												>
													<DropDown.ItemList
														items={assignableRoles.filter(
															(role) =>
																role.key !==
																user.roleKey
														)}
													>
														{(item: any) => (
															<DropDown.Item
																data-testid={`memberRoleKeyItem_${item.label}`}
																key={item.key}
																onClick={() =>
																	handleUpdateUser(
																		{
																			isInvitedMember:
																				user.isInvitedMember,
																			membershipExpirationDate:
																				user.membershipExpirationDate,
																			roleKey:
																				item.key,
																			userId: user.id,
																		}
																	)
																}
															>
																<div className="font-weight-semi-bold">
																	{item.label}
																</div>

																<div className="small text-secondary">
																	{
																		item.description
																	}
																</div>
															</DropDown.Item>
														)}
													</DropDown.ItemList>
												</DropDown>
											) : (
												<span className="text-secondary">
													{getRoleLabel(user.roleKey)}
												</span>
											)}

											{canEditMember(user) && (
												<ClayButton
													className="ml-3 text-secondary"
													disabled={loading}
													displayType="unstyled"
													onClick={() =>
														handleRemoveUser(
															user.id,
															user.isInvitedMember
														)
													}
												>
													<ClayIcon symbol="trash" />
												</ClayButton>
											)}
										</>
									)}
								</div>
							</div>
						))}
					</div>
				</div>
			</>
		);
	};

	if (closeModal) {
		return (
			<>
				<ClayModal.Header>
					{Liferay.Language.get('share')}
				</ClayModal.Header>

				<ClayModal.Body>{renderContent()}</ClayModal.Body>
			</>
		);
	}

	return renderContent();
}

export default RoomShare;
