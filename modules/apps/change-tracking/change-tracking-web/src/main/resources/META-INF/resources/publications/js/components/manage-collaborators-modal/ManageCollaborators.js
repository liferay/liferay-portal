/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {useResource} from '@clayui/data-provider';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayForm, {ClayInput, ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import ClayMultiSelect from '@clayui/multi-select';
import ClaySticker from '@clayui/sticker';
import ClayTable from '@clayui/table';
import ClayTabs from '@clayui/tabs';
import {openConfirmModal, openToast} from 'frontend-js-components-web';
import {fetch, getOpener, objectToFormData, sub} from 'frontend-js-web';
import React, {useCallback, useRef, useState} from 'react';

import CollaboratorRow from './CollaboratorRow';

const TABS = {
	collaborators: 0,
	link: 1,
};

const ManageCollaborators = ({
	autocompleteUserURL,
	getCollaboratorsURL,
	getSharePublicationLinkURL,
	getTemplateCollaboratorsURL,
	inviteUsersURL,
	isPublicationTemplate,
	namespace,
	onlyForm,
	readOnly,
	roles,
	setCollaboratorData,
	setShowModal,
	sharePublicationLink,
	showModal,
	showShareLinkTab,
	spritemap,
	trigger,
	verifyEmailAddressURL,
}) => {
	const [active, setActive] = useState(false);
	const [emailAddressErrorMessages, setEmailAddressErrorMessages] = useState(
		[]
	);
	const [link, setLink] = useState(sharePublicationLink);
	const [multiSelectValue, setMultiSelectValue] = useState('');
	const [networkStatus, setNetworkStatus] = useState(4);
	const [selectedItems, setSelectedItems] = useState({});
	const [selectedUserData, setSelectedUserData] = useState({});
	const [sharePublicationLinkVisible, setSharePublicationLinkVisible] =
		useState(!!sharePublicationLink);
	const [tab, setTab] = useState(TABS.collaborators);
	const [updatedRoles, setUpdatedRoles] = useState({});

	let defaultRole = roles[0];

	for (let i = 0; i < roles.length; i++) {
		if (roles[i].default) {
			defaultRole = roles[i];

			break;
		}
	}

	const [selectedRole, setSelectedRole] = useState(defaultRole);

	const {observer, onClose} = useModal({
		onClose: () => {
			setShowModal(false);

			if (onlyForm) {
				window.top.Liferay.fire('close-modal');
			}
		},
	});

	const {refetch: collaboratorsRefetch, resource: collaboratorsResource} =
		useResource({
			fetchOptions: {
				credentials: 'include',
				headers: new Headers({'x-csrf-token': Liferay.authToken}),
				method: 'GET',
			},
			fetchRetry: {
				attempts: 0,
			},
			link:
				isPublicationTemplate && !!getTemplateCollaboratorsURL
					? getTemplateCollaboratorsURL
					: getCollaboratorsURL,
		});

	const {resource: autocompleteResource} = useResource({
		fetchOptions: {
			credentials: 'include',
			headers: new Headers({'x-csrf-token': Liferay.authToken}),
			method: 'GET',
		},
		fetchRetry: {
			attempts: 0,
		},
		link: autocompleteUserURL,
		onNetworkStatusChange: setNetworkStatus,
		variables: {
			[`${namespace}keywords`]: multiSelectValue,
		},
	});

	const autocompleteUsers = autocompleteResource;
	const collaborators = collaboratorsResource;
	const emailValidationInProgressRef = useRef(false);

	const copyToClipboard = async () => {
		try {
			await navigator.clipboard.writeText(link);

			openToast({
				message: Liferay.Language.get('copied-link-to-the-clipboard'),
			});
		}
		catch (error) {
			openToast({
				message: error,
				title: Liferay.Language.get('an-error-occurred'),
				type: 'warning',
			});
		}
	};

	const handleChange = useCallback((value) => {
		if (!emailValidationInProgressRef.current) {
			setMultiSelectValue(value);
		}
	}, []);

	const handleItemsChange = useCallback(
		(items) => {
			emailValidationInProgressRef.current = true;

			Promise.all(
				items.map((item) => {
					if (item.userId) {
						return Promise.resolve({item});
					}

					if (!isEmailAddressValid(item.value)) {
						return Promise.resolve({
							error: sub(
								Liferay.Language.get(
									'x-is-not-a-valid-email-address'
								),
								item.value
							),
							item,
						});
					}

					return fetch(verifyEmailAddressURL, {
						body: objectToFormData({
							[`${namespace}emailAddress`]: item.value,
						}),
						method: 'POST',
					})
						.then((response) => response.json())
						.then(({errorMessage, user}) => {
							if (errorMessage) {
								return {
									error: errorMessage,
									item,
								};
							}
							else if (user) {
								return {
									item: {
										emailAddress: user.emailAddress,
										fullName: user.fullName,
										hasPublicationsAccess:
											user.hasPublicationsAccess,
										label: item.label,
										userId: user.userId,
										value: item.value,
									},
								};
							}

							return {
								error: sub(
									Liferay.Language.get(
										'user-x-does-not-exist'
									),
									item.value
								),
								item,
							};
						});
				})
			).then((results) => {
				emailValidationInProgressRef.current = false;

				const erroredResults = results.filter(({error}) => !!error);

				setEmailAddressErrorMessages(
					erroredResults.map(({error}) => error)
				);

				if (!erroredResults.length) {
					setMultiSelectValue('');
				}

				if (erroredResults.length === 1) {
					setMultiSelectValue(erroredResults[0].item.value);
				}

				const newSelectedItems = JSON.parse(
					JSON.stringify(selectedItems)
				);
				const newSelectedUserData = JSON.parse(
					JSON.stringify(selectedUserData)
				);
				const newUpdatedRoles = JSON.parse(
					JSON.stringify(updatedRoles)
				);

				results
					.filter(({error}) => !error)
					.map(({item}) => {
						if (
							collaborators &&
							!!collaborators.find(
								(collaborator) =>
									collaborator.emailAddress ===
									item.emailAddress
							)
						) {
							newUpdatedRoles[item.userId.toString()] =
								selectedRole;

							return;
						}

						const user = JSON.parse(JSON.stringify(item));

						user.new = true;

						newSelectedItems[user.userId.toString()] = selectedRole;
						newSelectedUserData[user.userId.toString()] = user;
					});

				setSelectedUserData(newSelectedUserData);

				setSelectedItems(newSelectedItems);
				setUpdatedRoles(newUpdatedRoles);
			});
		},
		[
			collaborators,
			namespace,
			selectedItems,
			selectedRole,
			selectedUserData,
			updatedRoles,
			verifyEmailAddressURL,
		]
	);

	const handleShareLinkToggle = () => {
		fetch(getSharePublicationLinkURL, {
			body: objectToFormData({
				[`${namespace}shareable`]: !sharePublicationLinkVisible,
			}),
			method: 'POST',
		})
			.then((response) => response.json())
			.then((json) => {
				if (json) {
					setLink(json.sharePublicationLink);
					setSharePublicationLinkVisible(json.shareable);
				}
			})
			.catch((error) => {
				openToast({
					message: error,
					title: Liferay.Language.get('an-error-occurred'),
					type: 'warning',
				});
			});
	};

	const handleSubmit = (event) => {
		event.preventDefault();

		const publicationsUserRoleUserIds = [];
		const publicationsUserRoleEmailAddresses = [];
		const roleValues = [];
		const userIds = [];

		const selectedItemsKeys = Object.keys(selectedItems);

		for (let i = 0; i < selectedItemsKeys.length; i++) {
			const user = selectedUserData[selectedItemsKeys[i]];

			if (!user.hasPublicationsAccess) {
				publicationsUserRoleEmailAddresses.push(user.emailAddress);
				publicationsUserRoleUserIds.push(user.userId);
			}

			roleValues.push(selectedItems[selectedItemsKeys[i]].value);
			userIds.push(selectedItemsKeys[i]);
		}

		const langKey =
			publicationsUserRoleUserIds.length > 1
				? Liferay.Language.get(
						'you-are-inviting-users-x-who-do-not-have-access-to-publications'
					)
				: Liferay.Language.get(
						'you-are-inviting-user-x-who-does-not-have-access-to-publications'
					);

		if (publicationsUserRoleUserIds.length) {
			openConfirmModal({
				message: sub(
					langKey,
					publicationsUserRoleEmailAddresses.join(', ')
				),
				onConfirm: (isConfirmed) => {
					if (publicationsUserRoleUserIds.length && isConfirmed) {
						sendInvite(
							publicationsUserRoleUserIds,
							roleValues,
							userIds
						);
					}
				},
			});
		}
		else {
			sendInvite(publicationsUserRoleUserIds, roleValues, userIds);
		}
	};

	const isEmailAddressValid = (email) => {
		const emailRegex = /.+@.+\..+/i;

		return emailRegex.test(email);
	};

	const renderCollaborators = () => {
		let users = [];

		if (collaborators && !!collaborators.length) {
			users = collaborators.slice(0);
		}

		const keys = Object.keys(selectedItems);

		for (let i = 0; i < keys.length; i++) {
			users.push(selectedUserData[keys[i]]);
		}

		if (!users.length) {
			return '';
		}

		return (
			<ClayForm.Group>
				<ClayTable hover={false}>
					<ClayTable.Body>
						{users
							.sort((a, b) => {
								const aIsUpdated =
									Object.prototype.hasOwnProperty.call(
										selectedItems,
										a.userId.toString()
									) ||
									Object.prototype.hasOwnProperty.call(
										updatedRoles,
										a.userId.toString()
									);
								const bIsUpdated =
									Object.prototype.hasOwnProperty.call(
										selectedItems,
										b.userId.toString()
									) ||
									Object.prototype.hasOwnProperty.call(
										updatedRoles,
										b.userId.toString()
									);

								if (aIsUpdated && !bIsUpdated) {
									return -1;
								}
								else if (!aIsUpdated && bIsUpdated) {
									return 1;
								}

								if (a.isOwner) {
									return -1;
								}
								else if (b.isOwner) {
									return 1;
								}

								if (a.isCurrentUser) {
									return -1;
								}
								else if (b.isCurrentUser) {
									return 1;
								}

								if (a.emailAddress < b.emailAddress) {
									return -1;
								}

								return 1;
							})
							.map((user) => (
								<CollaboratorRow
									handleSelect={(role) =>
										updateRole(role, user)
									}
									key={user.userId}
									readOnly={readOnly}
									roles={roles}
									selectedItems={selectedItems}
									spritemap={spritemap}
									updatedRoles={updatedRoles}
									user={user}
								/>
							))}
					</ClayTable.Body>
				</ClayTable>
			</ClayForm.Group>
		);
	};

	const renderCollaboratorsTrigger = () => {
		if (!collaborators || !collaborators.length) {
			return (
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('invite-users')}
					className="rounded-circle"
					data-tooltip-align="top"
					displayType="secondary"
					onClick={() => {
						setTab(TABS.collaborators);

						setShowModal(true);
					}}
					small
					symbol="plus"
					title={Liferay.Language.get('invite-users')}
				/>
			);
		}

		const columns = [];

		if (!readOnly) {
			columns.push(
				<div className="autofit-col" key="invite-users">
					<ClaySticker
						className="sticker-user-icon user-icon-color-0"
						data-tooltip-align="top"
						size="md"
						title={Liferay.Language.get('invite-users')}
					>
						<ClayIcon symbol="plus" />
					</ClaySticker>
				</div>
			);
		}

		const users = collaborators.sort((a, b) => {
			if (a.isOwner) {
				return -1;
			}
			else if (b.isOwner) {
				return 1;
			}

			if (a.isCurrentUser) {
				return -1;
			}
			else if (b.isCurrentUser) {
				return 1;
			}

			if (a.emailAddress < b.emailAddress) {
				return -1;
			}

			return 1;
		});

		for (let i = 0; i < 3 && i < users.length; i++) {
			const user = users[i];

			columns.push(
				<div className="autofit-col" key={user.userId}>
					<ClaySticker
						className={`sticker-user-icon ${
							user.portraitURL
								? ''
								: 'user-icon-color-' + (user.userId % 10)
						}`}
						data-tooltip-align="top"
						size="md"
						title={user.fullName}
					>
						{user.portraitURL ? (
							<div className="sticker-overlay">
								<img
									className="sticker-img"
									src={user.portraitURL}
								/>
							</div>
						) : (
							<ClayIcon symbol="user" />
						)}
					</ClaySticker>
				</div>
			);
		}

		if (!users.length) {
			columns.push(
				<div className="autofit-col" key="collaborators">
					<ClaySticker
						className="sticker-user-icon user-icon-color-0"
						data-tooltip-align="top"
						size="md"
						title={
							readOnly
								? Liferay.Language.get('view-collaborators')
								: Liferay.Language.get('invite-users')
						}
					>
						<ClayIcon symbol="users" />
					</ClaySticker>
				</div>
			);
		}
		else if (users.length > 3) {
			columns.push(
				<div className="autofit-col" key="collaborators">
					<ClaySticker
						className="btn-secondary"
						data-tooltip-align="top"
						size="md"
						title={
							readOnly
								? Liferay.Language.get('view-collaborators')
								: Liferay.Language.get('invite-users')
						}
					>
						{'+' + (users.length - 3)}
					</ClaySticker>
				</div>
			);
		}

		return (
			<ClayButton
				aria-label={Liferay.Language.get('view-collaborators')}
				displayType="unstyled"
				onClick={() => {
					setTab(TABS.collaborators);

					setShowModal(true);
				}}
			>
				<div className="autofit-row">{columns}</div>
			</ClayButton>
		);
	};

	const renderForm = () => {
		if (tab === TABS.link) {
			return (
				<ClayForm>
					<ClayForm.Group>
						<span className="text-secondary toggle-switch-text-left">
							{Liferay.Language.get(
								'allow-anyone-with-the-link-to-view-this-publication'
							)}
						</span>

						<ClayToggle
							onToggle={handleShareLinkToggle}
							toggled={sharePublicationLinkVisible}
						/>
					</ClayForm.Group>

					{!sharePublicationLinkVisible || (
						<ClayForm.Group>
							<ClayInput.Group>
								<ClayInput.GroupItem>
									<ClayInput
										readOnly
										type="text"
										value={link}
									/>
								</ClayInput.GroupItem>

								<ClayInput.GroupItem shrink>
									<ClayButton
										displayType="secondary"
										onClick={copyToClipboard}
									>
										{Liferay.Language.get('copy')}
									</ClayButton>
								</ClayInput.GroupItem>
							</ClayInput.Group>
						</ClayForm.Group>
					)}
				</ClayForm>
			);
		}

		return (
			<ClayForm onSubmit={handleSubmit}>
				{renderSelect()}

				{renderCollaborators()}

				{readOnly || (
					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									aria-label={Liferay.Language.get('cancel')}
									displayType="secondary"
									onClick={() => {
										if (
											Object.keys(selectedItems) === 0 &&
											Object.keys(updatedRoles) === 0
										) {
											onClose();
											resetForm();
										}
										else {
											openConfirmModal({
												message: Liferay.Language.get(
													'discard-unsaved-changes'
												),
												onConfirm: (isConfirmed) => {
													if (isConfirmed) {
														onClose();
														resetForm();
													}
												},
											});
										}
									}}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								{renderSubmit()}
							</ClayButton.Group>
						}
					/>
				)}
			</ClayForm>
		);
	};

	const renderModal = () => {
		if (!showModal) {
			return '';
		}

		const headers = [];

		if (!showShareLinkTab) {
			headers.push(
				<div className="autofit-col">
					<ClaySticker
						className="sticker-use-icon user-icon-color-0"
						displayType="secondary"
						shape="circle"
					>
						<ClayIcon symbol="users" />
					</ClaySticker>
				</div>
			);
		}

		headers.push(
			<div className="autofit-col">
				<div className="modal-title">
					{showShareLinkTab
						? Liferay.Language.get('share-access')
						: readOnly
							? Liferay.Language.get('view-collaborators')
							: Liferay.Language.get('invite-users')}
				</div>
			</div>
		);

		return (
			<ClayModal
				className="publications-invite-users-modal"
				observer={observer}
				size="lg"
				spritemap={spritemap}
			>
				<ClayModal.Header>
					<div className="autofit-row">{headers}</div>
				</ClayModal.Header>

				{showShareLinkTab ? renderTabs() : ''}

				<div className="inline-scroller modal-body publications-invite-users-modal-body">
					{renderForm()}
				</div>
			</ClayModal>
		);
	};

	const renderSelect = () => {
		if (readOnly) {
			return '';
		}

		const dropdownItems = [];

		for (let i = 0; i < roles.length; i++) {
			dropdownItems.push({
				description: roles[i].shortDescription,
				label: roles[i].label,
				onClick: () => {
					setActive(false);
					setSelectedRole(roles[i]);
				},
				symbolLeft:
					selectedRole.value === roles[i].value ? 'check' : '',
			});
		}

		return (
			<ClayForm.Group
				className={emailAddressErrorMessages.length ? 'has-error' : ''}
			>
				<ClayInput.Group>
					<ClayInput.GroupItem>
						<label htmlFor={`${namespace}userEmailAddress`}>
							{Liferay.Language.get('people')}
						</label>

						<ClayInput.Group>
							<ClayInput.GroupItem>
								<ClayMultiSelect
									inputName={`${namespace}userEmailAddress`}
									items={[]}
									loadingState={networkStatus}
									onChange={handleChange}
									onItemsChange={handleItemsChange}
									placeholder={Liferay.Language.get(
										'enter-name-or-email-address'
									)}
									sourceItems={
										multiSelectValue && autocompleteUsers
											? autocompleteUsers.map((user) => {
													return {
														emailAddress:
															user.emailAddress,
														fullName: user.fullName,
														hasPublicationsAccess:
															user.hasPublicationsAccess,
														isOwner: user.isOwner,
														label: user.fullName,
														portraitURL:
															user.portraitURL,
														userId: user.userId,
														value: user.emailAddress,
													};
												})
											: []
									}
									spritemap={spritemap}
									value={multiSelectValue}
								>
									{(item) => (
										<ClayMultiSelect.Item
											data-tooltip-align="top"
											disabled={item.isOwner}
											key={item.userId}
											textValue={item.label}
											title={
												item.isOwner
													? Liferay.Language.get(
															'cannot-update-permissions-for-an-owner'
														)
													: ''
											}
										>
											<div className="autofit-row autofit-row-center">
												<div className="autofit-col mr-3">
													<ClaySticker
														className={`sticker-user-icon ${
															item.portraitURL
																? ''
																: 'user-icon-color-' +
																	(item.userId %
																		10)
														}`}
														size="lg"
													>
														{item.portraitURL ? (
															<div className="sticker-overlay">
																<img
																	className="sticker-img"
																	src={
																		item.portraitURL
																	}
																/>
															</div>
														) : (
															<ClayIcon symbol="user" />
														)}
													</ClaySticker>
												</div>

												<div className="autofit-col">
													<strong>
														{item.fullName}
													</strong>

													<span>
														{item.emailAddress}
													</span>
												</div>
											</div>
										</ClayMultiSelect.Item>
									)}
								</ClayMultiSelect>
							</ClayInput.GroupItem>

							<ClayInput.GroupItem shrink>
								<ClayDropDown
									active={active}
									alignmentPosition={Align.BottomLeft}
									hasLeftSymbols={true}
									menuWidth="sm"
									onActiveChange={setActive}
									spritemap={spritemap}
									trigger={
										<ClayButton
											aria-label={
												selectedRole.longDescription
											}
											data-tooltip-align="top"
											displayType="secondary"
											title={selectedRole.longDescription}
										>
											{selectedRole.label}

											<span className="inline-item inline-item-after">
												<ClayIcon
													spritemap={spritemap}
													symbol="caret-bottom"
												/>
											</span>
										</ClayButton>
									}
								>
									<ClayDropDown.ItemList>
										<ClayDropDown.Group>
											{dropdownItems.map((item, i) => (
												<ClayDropDown.Item
													key={i}
													onClick={item.onClick}
													symbolLeft={item.symbolLeft}
												>
													<strong>
														{item.label}
													</strong>

													<div>
														{item.description}
													</div>
												</ClayDropDown.Item>
											))}
										</ClayDropDown.Group>
									</ClayDropDown.ItemList>
								</ClayDropDown>
							</ClayInput.GroupItem>
						</ClayInput.Group>

						{!!emailAddressErrorMessages.length && (
							<ClayForm.FeedbackGroup>
								{emailAddressErrorMessages.map(
									(emailAddressErrorMessage) => (
										<ClayForm.FeedbackItem
											key={emailAddressErrorMessage}
										>
											{emailAddressErrorMessage}
										</ClayForm.FeedbackItem>
									)
								)}
							</ClayForm.FeedbackGroup>
						)}
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayForm.Group>
		);
	};

	const renderShareLinkTrigger = () => {
		if (!showShareLinkTab) {
			return '';
		}

		return (
			<ClayButtonWithIcon
				className="sticker-user-icon user-icon-color-0"
				data-tooltip-align="top"
				displayType="unstyled"
				onClick={() => {
					setTab(TABS.link);

					setShowModal(true);
				}}
				small
				style={{
					opacity: sharePublicationLinkVisible ? 1 : 0.4,
				}}
				symbol="link"
				title={
					sharePublicationLinkVisible
						? Liferay.Language.get('publication-sharing-enabled')
						: Liferay.Language.get('publication-sharing-disabled')
				}
			/>
		);
	};

	const renderSubmit = () => {
		return (
			<ClayButton
				aria-label={Liferay.Language.get('submit')}
				disabled={
					!Object.keys(selectedItems).length &&
					!Object.keys(updatedRoles).length
				}
				displayType="primary"
				type="submit"
			>
				{!Object.keys(updatedRoles).length && !isPublicationTemplate
					? Liferay.Language.get('send')
					: Liferay.Language.get('save')}
			</ClayButton>
		);
	};

	const renderTabs = () => {
		if (!showShareLinkTab) {
			return '';
		}

		return (
			<ClayTabs>
				<ClayTabs.Item
					active={tab === TABS.collaborators}
					innerProps={{
						'aria-controls': 'invite-collaborators',
					}}
					onClick={() => setTab(TABS.collaborators)}
				>
					{readOnly
						? Liferay.Language.get('view-collaborators')
						: Liferay.Language.get('invite-users')}
				</ClayTabs.Item>

				<ClayTabs.Item
					active={tab === TABS.link}
					innerProps={{
						'aria-controls': 'share-link',
					}}
					onClick={() => setTab(TABS.link)}
				>
					{Liferay.Language.get('share-link')}
				</ClayTabs.Item>
			</ClayTabs>
		);
	};

	const renderTrigger = () => {
		if (trigger) {
			return trigger;
		}

		return (
			<ClayButton.Group spaced>
				{renderCollaboratorsTrigger()}

				{renderShareLinkTrigger()}
			</ClayButton.Group>
		);
	};

	const resetForm = () => {
		setEmailAddressErrorMessages([]);
		setMultiSelectValue('');
		setSelectedItems({});
		setSelectedRole(defaultRole);
		setSelectedUserData({});
		setUpdatedRoles({});

		emailValidationInProgressRef.current = false;
	};

	const sendInvite = (publicationsUserRoleUserIds, roleValues, userIds) => {
		const updatedRolesKeys = Object.keys(updatedRoles);

		if (
			isPublicationTemplate &&
			(!!userIds.length || !!collaborators.length)
		) {
			for (let i = 0; i < collaborators.length; i++) {
				const collaboratorUserId = collaborators[i].userId;

				if (updatedRoles[collaboratorUserId]) {
					if (updatedRoles[collaboratorUserId].value === -1) {

						// if updated to Remove collaborator

						continue;
					}

					roleValues.push(updatedRoles[collaboratorUserId].value);
				}
				else {
					roleValues.push(collaborators[i].roleValue);
				}

				publicationsUserRoleUserIds.push(collaboratorUserId);
				userIds.push(collaboratorUserId);
			}

			setCollaboratorData({
				[`publicationsUserRoleUserIds`]:
					publicationsUserRoleUserIds.join(','),
				[`roleValues`]: roleValues.join(','),
				[`userIds`]: userIds.join(','),
			});

			showNotification(
				Liferay.Language.get(
					'selected-users-have-been-associated-with-the-template'
				),
				false,
				false
			);
		}
		else {
			for (let i = 0; i < updatedRolesKeys.length; i++) {
				roleValues.push(updatedRoles[updatedRolesKeys[i]].value);
				userIds.push(updatedRolesKeys[i]);
			}

			const formData = {
				[`${namespace}publicationsUserRoleUserIds`]:
					publicationsUserRoleUserIds.join(','),
				[`${namespace}roleValues`]: roleValues.join(','),
				[`${namespace}userIds`]: userIds.join(','),
			};

			fetch(inviteUsersURL, {
				body: objectToFormData(formData),
				method: 'POST',
			})
				.then((response) => response.json())
				.then(({errorMessage, successMessage}) => {
					if (errorMessage) {
						showNotification(errorMessage, true, true);

						return;
					}

					showNotification(successMessage, false, true);
				})
				.catch((error) => {
					showNotification(error.message, true, true);
				});
		}
	};

	const showNotification = (message, error, reset) => {
		const parentOpenToast = getOpener().Liferay.Util.openToast;

		const openToastParams = {message};

		if (error) {
			openToastParams.title = Liferay.Language.get('error');
			openToastParams.type = 'danger';
		}

		collaboratorsRefetch();
		onClose();
		parentOpenToast(openToastParams);

		if (reset) {
			resetForm();
		}
	};

	const updateRole = (role, user) => {
		if (user.new) {
			const json = {};

			const keys = Object.keys(selectedItems);

			for (let i = 0; i < keys.length; i++) {
				if (keys[i] !== user.userId.toString()) {
					json[keys[i]] = selectedItems[keys[i]];
				}
			}

			if (role.value >= 0) {
				json[user.userId.toString()] = role;
			}

			setSelectedItems(json);

			return;
		}

		const json = {};

		const keys = Object.keys(updatedRoles);

		for (let i = 0; i < keys.length; i++) {
			if (keys[i] !== user.userId.toString()) {
				json[keys[i]] = updatedRoles[keys[i]];
			}
		}

		let savedRoleValue = null;

		if (collaborators) {
			for (let i = 0; i < collaborators.length; i++) {
				if (collaborators[i].userId === user.userId) {
					savedRoleValue = collaborators[i].roleValue;

					break;
				}
			}
		}

		if (
			savedRoleValue !== role.value ||
			!Object.prototype.hasOwnProperty.call(
				updatedRoles,
				user.userId.toString()
			)
		) {
			json[user.userId.toString()] = role;
		}

		setUpdatedRoles(json);
	};

	return onlyForm ? (
		<>{renderForm()}</>
	) : (
		<>
			{renderModal()}
			{renderTrigger()}
		</>
	);
};

const ManageCollaboratorsWithStateHook = ({...props}) => {
	const [showModal, setShowModal] = useState(false);

	return (
		<ManageCollaborators
			setShowModal={setShowModal}
			showModal={showModal}
			{...props}
		/>
	);
};

export default ManageCollaboratorsWithStateHook;
export {ManageCollaborators, ManageCollaboratorsWithStateHook};
