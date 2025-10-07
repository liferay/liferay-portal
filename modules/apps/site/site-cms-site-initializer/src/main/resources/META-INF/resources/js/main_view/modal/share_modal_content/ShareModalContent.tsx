/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {useResource} from '@clayui/data-provider';
import ClayDropDown from '@clayui/drop-down';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import ClayMultiSelect from '@clayui/multi-select';
import ClayPanel from '@clayui/panel';
import ClaySticker from '@clayui/sticker';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import ExpirationDateSelector, {
	formatDateForView,
	formatDateToISO,
} from './ExpirationDateSelector';
import PermissionSelector from './PermissionSelector';

import '../../../../css/components/ShareModalContent.scss';
import CollaboratorService from '../../../common/services/CollaboratorService';
import {UserAccount, UserGroup} from '../../../common/types/UserAccount';

const COLLABORATOR_TYPE = {
	USER: 'User',
	USER_GROUP: 'UserGroup',
};

export interface Collaborator {
	actionIds: string;
	dateExpired?: string;
	error?: string;
	share: boolean;
	toBeShared?: boolean;
	type: typeof COLLABORATOR_TYPE.USER | typeof COLLABORATOR_TYPE.USER_GROUP;
	user: UserAccount | UserGroup;
}

function CollaboratorListItem({
	actionIds,
	dateExpired,
	error,
	onChangeUser,
	onRemoveUser,
	share,
	toBeShared,
	type = COLLABORATOR_TYPE.USER,
	user,
}: {
	actionIds: string;
	dateExpired?: string;
	error?: string;
	onChangeUser: (user: UserAccount | UserGroup, property: object) => void;
	onRemoveUser: (user: UserAccount | UserGroup) => void;
	share: boolean;
	toBeShared?: boolean;
	type: typeof COLLABORATOR_TYPE.USER | typeof COLLABORATOR_TYPE.USER_GROUP;
	user: UserAccount | UserGroup;
}) {
	const handleChangeUserProperties = (propertyObj: object) => {
		onChangeUser(user, propertyObj);
	};

	return (
		<li
			className="border-0 c-px-0 c-py-1 list-group-item list-group-item-flex"
			key={`collaborator-${user.id}`}
		>
			<div className="autofit-col pl-0">
				<ClaySticker displayType="secondary" shape="circle" size="sm">
					{type === COLLABORATOR_TYPE.USER ? (
						'image' in user && user.image ? (
							<img
								alt={user.name}
								className="sticker-img"
								src={(user as UserAccount).image}
							/>
						) : (
							<ClayIcon symbol="user" />
						)
					) : (
						<ClayIcon symbol="users" />
					)}
				</ClaySticker>
			</div>

			<div className="autofit-col autofit-col-expand">
				<div className="align-items-center d-flex justify-content-between">
					<div className="d-flex text-truncate">
						<span className="text-3 text-truncate text-weight-semi-bold">
							{user.name}
						</span>

						{toBeShared && (
							<span className="inline-item inline-item-after label label-inverse-light">
								<span className="label-item label-item-expand text-nowrap">
									{Liferay.Language.get('to-be-shared')}
								</span>
							</span>
						)}
					</div>

					<div>
						<PermissionSelector
							actionIds={actionIds}
							onChange={handleChangeUserProperties}
						/>
					</div>
				</div>

				{error ? (
					<div className="text-2 text-danger">{error}</div>
				) : (
					dateExpired && (
						<div className="text-2">
							{sub(Liferay.Language.get('access-expires-x'), [
								formatDateForView(dateExpired),
							])}

							<ClayButtonWithIcon
								aria-label={sub(
									Liferay.Language.get('clear-x'),
									[Liferay.Language.get('expiration-date')]
								)}
								borderless
								className="c-ml-1 inline-item"
								displayType="secondary"
								monospaced
								onClick={() =>
									handleChangeUserProperties({
										dateExpired: '',
										error: '',
									})
								}
								size="xs"
								symbol="trash"
							/>
						</div>
					)
				)}
			</div>

			<div className="autofit-col p-0">
				<div className="d-flex">
					<ExpirationDateSelector
						dateExpired={dateExpired}
						onChange={handleChangeUserProperties}
					/>

					<ClayDropDown
						hasLeftSymbols={true}
						trigger={
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get(
									'more-options'
								)}
								borderless
								displayType="secondary"
								monospaced
								size="xs"
								symbol="ellipsis-v"
							/>
						}
					>
						<ClayDropDown.ItemList>
							<ClayDropDown.Item
								aria-label={Liferay.Language.get(
									'allow-resharing'
								)}
								key={`share-${user.id}`}
								onClick={() =>
									handleChangeUserProperties({
										share: !share,
									})
								}
								symbolLeft={share ? 'check-small' : ''}
							>
								{Liferay.Language.get('allow-resharing')}
							</ClayDropDown.Item>

							<ClayDropDown.Item
								aria-label={Liferay.Language.get(
									'remove-access'
								)}
								key={`remove-${user.id}`}
								onClick={() => onRemoveUser(user)}
							>
								{Liferay.Language.get('remove-access')}
							</ClayDropDown.Item>
						</ClayDropDown.ItemList>
					</ClayDropDown>
				</div>
			</div>
		</li>
	);
}

export default function ShareModalContent({
	autocompleteURL = '',
	closeModal,
	collaboratorURL = '',
	creator,
	initialCollaborators = [],
	itemId,
	title = '',
}: {
	autocompleteURL: string;
	closeModal: () => void;
	collaboratorURL: string;
	creator: {
		contentType: string;
		id: string;
		image?: string;
		name: string;
	};
	initialCollaborators: Collaborator[];
	itemId: number;
	title: string;
}) {
	const [autocompleteValue, setAutocompleteValue] = useState('');
	const [autocompleteNetworkStatus, setAutocompleteNetworkStatus] =
		useState(4);
	const [collaborators, setCollaborators] =
		useState<Collaborator[]>(initialCollaborators);
	const [loading, setLoading] = useState(false);

	const {resource: users} = useResource({
		fetchOptions: {
			credentials: 'include',
			headers: new Headers({'x-csrf-token': Liferay.authToken}),
			method: 'GET',
		},
		fetchRetry: {
			attempts: 0,
		},
		link: `${window.location.origin}${autocompleteURL}`,
		onNetworkStatusChange: setAutocompleteNetworkStatus,
		variables: {search: autocompleteValue},
	});

	const handleAddUser = (user: UserAccount | UserGroup, type: string) => {
		setCollaborators((collaborators) => {
			return collaborators.every(
				(collaborator) => collaborator.user.id !== user.id
			) && creator.id !== user.id
				? [
						{
							actionIds: 'VIEW',
							share: false,
							toBeShared: true,
							type,
							user,
						},
						...collaborators,
					]
				: collaborators;
		});

		setAutocompleteValue('');
	};

	const handleRemoveUser = async (
		user: UserAccount | UserGroup
	): Promise<void> => {
		setCollaborators((collaborator) =>
			collaborator.filter(
				(collaborator) => collaborator.user.id !== user.id
			)
		);
	};

	const handleChangeUser = (
		user: UserAccount | UserGroup,
		property: object
	) => {
		setCollaborators((collaborator) =>
			collaborator.map((item) => {
				if (item.user.id === user.id) {
					return {
						...item,
						...property,
					};
				}

				return item;
			})
		);
	};

	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault();

		if (collaborators.some(({error}) => !!error)) {
			return;
		}

		setLoading(true);

		const {error} = await CollaboratorService.updateCollaborators(
			collaboratorURL,
			itemId,
			collaborators.map(
				({actionIds, dateExpired, share, type, user}) => ({
					actionIds: actionIds.split(','),
					...(!!dateExpired && {
						dateExpired: formatDateToISO(dateExpired),
					}),
					id: user.id,
					share,
					type,
				})
			)
		);

		setLoading(false);

		if (error) {
			openToast({
				message:
					error ||
					Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
		else {
			openToast({
				message: sub(
					collaborators.some(({toBeShared}) => !!toBeShared)
						? Liferay.Language.get('x-was-shared-successfully')
						: Liferay.Language.get('x-was-updated-successfully'),
					title
				),
				type: 'success',
			});

			closeModal();
		}
	};

	const _isCollaboratorsUpdated = () =>
		JSON.stringify(collaborators) !== JSON.stringify(initialCollaborators);

	return (
		<div className="share-modal-content">
			<ClayModal.Header>
				{sub(Liferay.Language.get('share-x'), `"${title}"`)}
			</ClayModal.Header>

			<ClayModal.Body scrollable={true}>
				<ClayForm.Group>
					<ClayInput.Group>
						<ClayInput.GroupItem>
							<label htmlFor="collaboratorAutocomplete">
								{Liferay.Language.get(
									'add-people-to-collaborate'
								)}
							</label>

							<ClayMultiSelect
								id="collaboratorAutocomplete"
								items={[]}
								loadingState={autocompleteNetworkStatus}
								onChange={setAutocompleteValue}
								placeholder={Liferay.Language.get(
									'enter-name-email-or-groups'
								)}
								sourceItems={
									autocompleteValue && !!users?.items?.length
										? users.items?.map((item: any) => {
												if (
													item.entryClassName?.includes(
														COLLABORATOR_TYPE.USER_GROUP
													)
												) {
													return {
														type: COLLABORATOR_TYPE.USER_GROUP,
														user: {
															id: item.embedded.id.toString(),
															name: item.embedded
																.name,
														},
													};
												}

												return {
													type: COLLABORATOR_TYPE.USER,
													user: {
														emailAddress:
															item.embedded
																.emailAddress,
														id: item.embedded.id.toString(),
														image: item.embedded
															.image,
														name: item.embedded
															.name,
													},
												};
											})
										: []
								}
								value={autocompleteValue}
							>
								{({
									type,
									user,
								}: {
									type: string;
									user: UserAccount | UserGroup;
								}) => (
									<ClayMultiSelect.Item
										key={`autocomplete-${type}-${user.id}`}
										onClick={() =>
											handleAddUser(user, type)
										}
										textValue={user.name}
									>
										<div className="autofit-row autofit-row-center">
											<div className="autofit-col c-mr-1">
												<ClaySticker
													className="sticker-user-icon"
													size="sm"
												>
													{type ===
													COLLABORATOR_TYPE.USER ? (
														'image' in user &&
														user.image ? (
															<div className="sticker-overlay">
																<img
																	className="sticker-img"
																	src={
																		user.image
																	}
																/>
															</div>
														) : (
															<ClayIcon symbol="user" />
														)
													) : (
														<ClayIcon symbol="users" />
													)}
												</ClaySticker>
											</div>

											<div className="autofit-col">
												<span className="text-weight-semibold">
													<span className="c-mr-1">
														{user.name}
													</span>

													{'emailAddress' in user &&
														`(${user.emailAddress})`}
												</span>
											</div>
										</div>
									</ClayMultiSelect.Item>
								)}
							</ClayMultiSelect>
						</ClayInput.GroupItem>
					</ClayInput.Group>
				</ClayForm.Group>

				<ClayForm.Group>
					<ClayPanel
						className="border-0"
						collapsable
						defaultExpanded={true}
						displayTitle={
							<div className="panel-title text-secondary">
								{Liferay.Language.get('who-has-access') +
									` (` +
									sub(
										Liferay.Language.get('x-users'),
										collaborators.length + 1
									) +
									`)`}
							</div>
						}
						displayType="unstyled"
					>
						<ClayPanel.Body>
							<ul className="c-mb-0 list-group">
								{collaborators.map((item) => (
									<CollaboratorListItem
										key={`listItem-${item.type}-${item.user.id}`}
										onChangeUser={handleChangeUser}
										onRemoveUser={handleRemoveUser}
										{...item}
									/>
								))}

								{creator.id && (
									<li
										className="border-0 c-px-0 c-py-1 list-group-item list-group-item-flex"
										key={`listItem-creator-${creator.id}`}
									>
										<div className="autofit-col pl-0">
											<ClaySticker
												displayType="secondary"
												shape="circle"
												size="sm"
											>
												{creator.contentType ===
												'UserAccount' ? (
													'image' in creator &&
													creator.image ? (
														<img
															alt={creator.name}
															className="sticker-img"
															src={creator.image}
														/>
													) : (
														<ClayIcon symbol="user" />
													)
												) : (
													<ClayIcon symbol="users" />
												)}
											</ClaySticker>
										</div>

										<div className="autofit-col autofit-col-expand">
											<span className="text-3 text-truncate text-weight-semi-bold">
												{creator.name}
											</span>
										</div>

										<div className="autofit-col">
											<span className="text-2 text-secondary text-weight-semi-bold">
												{Liferay.Language.get('owner')}
											</span>
										</div>
									</li>
								)}
							</ul>
						</ClayPanel.Body>
					</ClayPanel>
				</ClayForm.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={loading || !_isCollaboratorsUpdated()}
							displayType="primary"
							onClick={handleSubmit}
							type="submit"
						>
							{loading && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation"
									></span>
								</span>
							)}

							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</div>
	);
}
