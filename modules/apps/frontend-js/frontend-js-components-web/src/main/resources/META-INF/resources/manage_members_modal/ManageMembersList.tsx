/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import LoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import React, {useEffect, useId, useMemo, useRef, useState} from 'react';

import {MemberListItem} from './MemberListItem';
import {SearcheableMembersList} from './SearcheableMembersList';
import {useMembers} from './hooks/useMembers';
import {AddMembersInputApi, MemberType, MembersConfig} from './types';

interface ManageMembersListProps {
	className?: string;
	config: MembersConfig;
	emptyStateDescription: string;
	externalReferenceCode: string;
	filter?: string;
	hasAssignMembersPermission: boolean;
	onChange?: () => void;
	onHasSelectedMembersChange?: (hasSelectedMembers: boolean) => void;
	ownerId?: string;
	pageSize?: number;
	renderAddMembersInput?: (api: AddMembersInputApi) => React.ReactNode;
}

const DEFAULT_PAGE_SIZE = 20;

export function ManageMembersList({
	className,
	config,
	emptyStateDescription,
	externalReferenceCode,
	filter,
	hasAssignMembersPermission,
	onChange,
	onHasSelectedMembersChange,
	ownerId,
	pageSize = DEFAULT_PAGE_SIZE,
	renderAddMembersInput,
}: ManageMembersListProps) {
	const listLabelId = useId();
	const currentUserId = Liferay.ThemeDisplay.getUserId();
	const {
		addMember,
		loadMore,
		removeMember,
		search,
		state,
		updateMemberRoles,
	} = useMembers(config, externalReferenceCode, pageSize, onChange);
	const {
		groups,
		isFetching: isFetchingMembers,
		isSearching,
		roles,
		users,
	} = state;

	const isLoading = isFetchingMembers || isSearching;

	const [selectedOption, setSelectedOption] = useState(MemberType.USERS);
	const sentinelRef = useRef(null);

	useEffect(() => {
		if (!sentinelRef.current) {
			return;
		}

		const observer = new IntersectionObserver(
			(entries) => {
				if (entries[0].isIntersecting && !isLoading) {
					loadMore(selectedOption);
				}
			},
			{
				threshold: 1,
			}
		);

		observer.observe(sentinelRef.current);

		return () => {
			observer.disconnect();
		};
	}, [loadMore, selectedOption, isFetchingMembers, isSearching, isLoading]);

	useEffect(() => {
		const hasMembers = !!users.items.length || !!groups.items.length;
		onHasSelectedMembersChange?.(hasMembers);
	}, [onHasSelectedMembersChange, users.items, groups.items]);

	const hasMembersSelected = useMemo(() => {
		if (selectedOption === MemberType.USERS) {
			return users.items.length;
		}

		return groups.items.length;
	}, [selectedOption, users.items, groups.items]);

	const defaultRole = useMemo(() => {
		return roles.find(
			(role) =>
				role.externalReferenceCode ===
				config.defaultRoleExternalReferenceCode
		);
	}, [config.defaultRoleExternalReferenceCode, roles]);

	const excludeMembers = useMemo(() => {
		if (selectedOption === MemberType.USERS) {
			return users.items;
		}

		return groups.items;
	}, [selectedOption, users.items, groups.items]);

	return (
		<div className={classNames('manage-members-list', className)}>
			{hasAssignMembersPermission ? (
				renderAddMembersInput?.({
					excludeMembers,
					filter,
					onAutocompleteItemSelected: async (item) => {
						const isAlreadyMember = excludeMembers.some(
							(existingItem) => existingItem.id === item.id
						);

						await addMember(item, selectedOption);

						if (!isAlreadyMember && defaultRole) {
							await updateMemberRoles(
								item,
								[defaultRole.name],
								selectedOption,
								false
							);
						}
					},
					onSelectChange: setSelectedOption,
					selectValue: selectedOption,
				})
			) : (
				<SearcheableMembersList
					onSearch={(value) => search(selectedOption, value)}
					onSelectChange={setSelectedOption}
					selectValue={selectedOption}
				/>
			)}

			{!hasMembersSelected ? (
				<div className="border-top c-ml-n4 c-mr-n4 c-p-4 c-pb-0 text-center">
					<p className="c-mb-1 c-mt-2 font-weight-semi-bold text-4">
						{Liferay.Language.get('no-members-yet')}
					</p>

					<p className="c-m-0 text-3 text-secondary">
						{emptyStateDescription}
					</p>
				</div>
			) : (
				<>
					<label className="d-block" id={listLabelId}>
						{Liferay.Language.get('who-has-access')}
					</label>
					<ul
						aria-labelledby={listLabelId}
						className="c-mt-3 c-p-0 list-unstyled members-list"
					>
						{selectedOption === MemberType.USERS ? (
							<MemberListItem
								currentUserId={currentUserId}
								defaultRole={defaultRole}
								hasAssignMembersPermission={
									hasAssignMembersPermission
								}
								itemType="user"
								items={users.items}
								onRemoveItem={(item) => {
									return removeMember(item, selectedOption);
								}}
								onUpdateItemRoles={(item, newRoles) => {
									return updateMemberRoles(
										item,
										newRoles,
										selectedOption
									);
								}}
								ownerId={ownerId}
								roleNames={config.roleNames}
								roles={roles}
							/>
						) : (
							<MemberListItem
								defaultRole={defaultRole}
								hasAssignMembersPermission={
									hasAssignMembersPermission
								}
								itemType="group"
								items={groups.items}
								onRemoveItem={(item) => {
									return removeMember(item, selectedOption);
								}}
								onUpdateItemRoles={(item, newRoles) => {
									return updateMemberRoles(
										item,
										newRoles,
										selectedOption
									);
								}}
								roleNames={config.roleNames}
								roles={roles}
							/>
						)}

						{isLoading && (
							<li className="d-flex justify-content-center">
								<LoadingIndicator
									displayType="secondary"
									size="sm"
								/>
							</li>
						)}

						<li ref={sentinelRef} role="presentation" />
					</ul>
				</>
			)}
		</div>
	);
}
