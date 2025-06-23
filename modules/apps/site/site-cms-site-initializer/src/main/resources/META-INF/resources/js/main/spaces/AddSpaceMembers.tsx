/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/spaces/AddSpaceMembers.scss';

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import {openToast} from 'frontend-js-components-web';
import {navigate, sub} from 'frontend-js-web';
import React, {useEffect, useId, useState} from 'react';

import SpaceService from '../../services/SpaceService';
import {UserAccount, UserGroup} from '../../types/UserAccount';
import {getImage} from '../util/getImage';
import {MembersListItem} from './MemberListItem';
import {NewSpaceFormSection} from './NewSpaceFormSection';
import {
	SelectOptions,
	SpaceMembersInputWithSelect,
} from './SpaceMembersInputWithSelect';

export interface AddSpaceMembersProps {
	assetLibraryCreatorUserId: string;
	assetLibraryId: string;
	assetLibraryName: string;
	baseAssetLibraryURL: string;
}

export function AddSpaceMembers({
	assetLibraryCreatorUserId,
	assetLibraryId,
	assetLibraryName,
	baseAssetLibraryURL,
}: AddSpaceMembersProps) {
	const currentUserId = Liferay.ThemeDisplay.getUserId();
	const [selectedOption, setSelectedOption] = useState(SelectOptions.USERS);
	const [selectedUsers, setSelectedUsers] = useState<UserAccount[]>([]);
	const [selectedUserGroups, setSelectedUserGroups] = useState<UserGroup[]>(
		[]
	);

	useEffect(() => {
		const fetchMembers = async () => {
			const [spaceUsers, spaceUserGroups] = await Promise.all([
				SpaceService.getSpaceUsers({
					spaceId: assetLibraryId,
				}),
				SpaceService.getSpaceUserGroups({
					spaceId: assetLibraryId,
				}),
			]);

			setSelectedUsers(spaceUsers);
			setSelectedUserGroups(spaceUserGroups);
		};

		fetchMembers();
	}, [assetLibraryId]);

	const onAutocompleteItemSelected = async (
		item: UserAccount | UserGroup
	) => {
		if (selectedOption === SelectOptions.USERS) {
			if (selectedUsers.some((user) => user.id === item.id)) {
				return;
			}

			setSelectedUsers([item as UserAccount, ...selectedUsers]);

			const {error} = await SpaceService.linkUserToSpace({
				spaceId: assetLibraryId,
				userId: item.id,
			});

			if (error) {
				openToast({
					message: sub(
						Liferay.Language.get('failed-to-add-user-x-to-space'),
						[`<strong>${item.name}</strong>`]
					),
					type: 'danger',
				});
			}
			else {
				openToast({
					message: sub(
						Liferay.Language.get(
							'user-x-successfully-added-to-space'
						),
						[`<strong>${item.name}</strong>`]
					),
					type: 'success',
				});
			}

			return;
		}

		if (selectedUserGroups.some((group) => group.id === item.id)) {
			return;
		}

		setSelectedUserGroups([item, ...selectedUserGroups]);

		const {error} = await SpaceService.linkUserGroupToSpace({
			spaceId: assetLibraryId,
			userGroupId: item.id,
		});

		if (error) {
			openToast({
				message: sub(
					Liferay.Language.get('failed-to-add-group-x-to-space'),
					[`<strong>${item.name}</strong>`]
				),
				type: 'danger',
			});
		}
		else {
			openToast({
				message: sub(
					Liferay.Language.get('group-x-successfully-added-to-space'),
					[`<strong>${item.name}</strong>`]
				),
				type: 'success',
			});
		}
	};

	const onRemoveItem = async (item: UserAccount | UserGroup) => {
		if (selectedOption === SelectOptions.USERS) {
			setSelectedUsers(selectedUsers.filter((u) => u.id !== item.id));

			const {error} = await SpaceService.unlinkUserFromSpace({
				spaceId: assetLibraryId,
				userId: item.id,
			});

			if (error) {
				openToast({
					message: sub(
						Liferay.Language.get(
							'unable-to-remove-user-x-from-space'
						),
						[`<strong>${item.name}</strong>`]
					),
					type: 'danger',
				});
			}
			else {
				openToast({
					message: sub(
						Liferay.Language.get(
							'user-x-successfully-removed-from-space'
						),
						[`<strong>${item.name}</strong>`]
					),
					type: 'success',
				});
			}

			return;
		}

		setSelectedUserGroups(
			selectedUserGroups.filter((u) => u.id !== item.id)
		);

		const {error} = await SpaceService.unlinkUserGroupFromSpace({
			spaceId: assetLibraryId,
			userGroupId: item.id,
		});

		if (error) {
			openToast({
				message: sub(
					Liferay.Language.get(
						'unable-to-remove-group-x-from-space-x'
					),
					[`<strong>${item.name}</strong>`]
				),
				type: 'success',
			});
		}
		else {
			openToast({
				message: sub(
					Liferay.Language.get(
						'group-x-successfully-removed-from-space-x'
					),
					[`<strong>${item.name}</strong>`]
				),
				type: 'success',
			});
		}
	};

	const onContinueBtnClick = () => {
		navigate(baseAssetLibraryURL + assetLibraryId);
	};

	const hasMembers = selectedUsers?.length > 1 || selectedUserGroups?.length;
	const listLabelId = useId();

	return (
		<ClayLayout.Row className="add-space-members">
			<ClayLayout.Col className="mw-50 px-9 w-50">
				<NewSpaceFormSection
					description={Liferay.Language.get(
						'add-team-members-to-this-space-to-start-collaborating'
					)}
					step={2}
					title={sub(
						Liferay.Language.get('add-members-to-x'),
						assetLibraryName
					)}
					withForm={false}
				>
					<SpaceMembersInputWithSelect
						onAutocompleteItemSelected={onAutocompleteItemSelected}
						onSelectChange={setSelectedOption}
						selectValue={selectedOption}
					/>

					<label className="d-block" id={listLabelId}>
						{Liferay.Language.get('who-has-access')}
					</label>

					<ul aria-labelledby={listLabelId} className="members-list">
						{selectedOption === SelectOptions.USERS ? (
							<MembersListItem
								assetLibraryCreatorUserId={
									assetLibraryCreatorUserId
								}
								currentUserId={currentUserId}
								emptyMessage={Liferay.Language.get(
									'this-space-has-no-user-yet'
								)}
								itemType="user"
								items={selectedUsers}
								onRemoveItem={onRemoveItem}
							/>
						) : (
							<MembersListItem
								emptyMessage={Liferay.Language.get(
									'this-space-has-no-group-yet'
								)}
								itemType="group"
								items={selectedUserGroups}
								onRemoveItem={onRemoveItem}
							/>
						)}
					</ul>

					<ClayButton.Group className="mb-0 w-100" spaced vertical>
						<ClayButton
							className="mt-4"
							onClick={onContinueBtnClick}
						>
							{hasMembers
								? Liferay.Language.get('continue')
								: Liferay.Language.get(
										'continue-without-members'
									)}
						</ClayButton>
					</ClayButton.Group>
				</NewSpaceFormSection>
			</ClayLayout.Col>

			<ClayLayout.Col>
				<img
					aria-hidden="true"
					src={getImage('add_space_members_illustration.svg')}
				></img>
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
}

export default AddSpaceMembers;
