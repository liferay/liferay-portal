/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/spaces/SpaceMembersWithList.scss';

import LoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import React, {useEffect, useId, useMemo, useRef, useState} from 'react';

import {MembersListItem} from './MemberListItem';
import {SearcheableSpaceMembersList} from './SearcheableSpaceMembersList';
import {SpaceMembersInputWithSelect} from './SpaceMembersInputWithSelect';
export interface SpaceMembersWithListProps {
	assetLibraryCreatorUserId: string;
	className?: string;
	externalReferenceCode: string;
	hasAssignMembersPermission: boolean;
	onHasSelectedMembersChange?: (hasSelectedMembers: boolean) => void;
	pageSize?: number;
}

import {SelectOptions} from './SpaceMembersSelectOptions';
import {useSpaceMembers} from './hooks/useSpaceMembers';

const DEFAULT_PAGE_SIZE = 20;

export function SpaceMembersWithList({
	assetLibraryCreatorUserId,
	externalReferenceCode,
	hasAssignMembersPermission,
	className,
	onHasSelectedMembersChange,
	pageSize = DEFAULT_PAGE_SIZE,
}: SpaceMembersWithListProps) {
	const listLabelId = useId();
	const currentUserId = Liferay.ThemeDisplay.getUserId();
	const {
		addMember,
		loadMore,
		removeMember,
		search,
		state,
		updateMemberRoles,
	} = useSpaceMembers(externalReferenceCode, pageSize);
	const {
		groups,
		isFetching: isFetchingMembers,
		isSearching,
		roles: spacePermissionsRoles,
		users,
	} = state;

	const isLoading = isFetchingMembers || isSearching;

	const [selectedOption, setSelectedOption] = useState(SelectOptions.USERS);
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
	}, [sentinelRef, loadMore, selectedOption, isFetchingMembers, isSearching]);

	useEffect(() => {
		const hasMembers = !!users.items.length || !!groups.items.length;
		onHasSelectedMembersChange?.(hasMembers);
	}, [onHasSelectedMembersChange, users.items, groups.items]);

	const hasMembersSelected = useMemo(() => {
		if (selectedOption === SelectOptions.USERS) {
			return users.items.length;
		}

		return groups.items.length;
	}, [selectedOption, users.items, groups.items]);

	const excludeMembers = useMemo(() => {
		if (selectedOption === SelectOptions.USERS) {
			return users.items;
		}

		return groups.items;
	}, [selectedOption, users.items, groups.items]);

	return (
		<div className={classNames('space-members-with-list', className)}>
			{hasAssignMembersPermission ? (
				<SpaceMembersInputWithSelect
					excludeMembers={excludeMembers}
					onAutocompleteItemSelected={(item) =>
						addMember(item, selectedOption)
					}
					onSelectChange={(value) => {
						setSelectedOption(value);
					}}
					selectValue={selectedOption}
				/>
			) : (
				<SearcheableSpaceMembersList
					onSearch={(value) => search(selectedOption, value)}
					onSelectChange={(value) => {
						setSelectedOption(value);
					}}
					selectValue={selectedOption}
				/>
			)}

			{!hasMembersSelected ? (
				<div className="border-top c-ml-n4 c-mr-n4 c-p-4 c-pb-0 text-center">
					<p className="c-mb-1 c-mt-2 font-weight-semi-bold text-4">
						{Liferay.Language.get('no-members-yet')}
					</p>

					<p className="c-m-0 text-3 text-secondary">
						{Liferay.Language.get('add-members-to-this-space')}
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
						{selectedOption === SelectOptions.USERS ? (
							<MembersListItem
								assetLibraryCreatorUserId={
									assetLibraryCreatorUserId
								}
								currentUserId={currentUserId}
								hasAssignMembersPermission={
									hasAssignMembersPermission
								}
								itemType="user"
								items={users.items}
								onRemoveItem={(item) =>
									removeMember(item, selectedOption)
								}
								onUpdateItemRoles={(item, newRoles) =>
									updateMemberRoles(
										item,
										newRoles,
										selectedOption
									)
								}
								roles={spacePermissionsRoles}
							/>
						) : (
							<MembersListItem
								hasAssignMembersPermission={
									hasAssignMembersPermission
								}
								itemType="group"
								items={groups.items}
								onRemoveItem={(item) =>
									removeMember(item, selectedOption)
								}
								onUpdateItemRoles={(item, newRoles) =>
									updateMemberRoles(
										item,
										newRoles,
										selectedOption
									)
								}
								roles={spacePermissionsRoles}
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

						<div ref={sentinelRef} />
					</ul>
				</>
			)}
		</div>
	);
}
