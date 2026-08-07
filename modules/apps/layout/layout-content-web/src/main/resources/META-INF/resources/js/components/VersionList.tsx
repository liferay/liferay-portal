/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import {dateUtils, sub} from 'frontend-js-web';
import React, {ComponentProps} from 'react';

import {config} from '../config';
import useKeyboardNavigation from '../hooks/useKeyboardNavigation';
import {PageVersion, Status} from '../types/PageVersion';

const STATUSES: Record<
	Status,
	{displayType: 'secondary' | 'success'; label: string}
> = {
	approved: {
		displayType: 'success',
		label: Liferay.Language.get('published'),
	},
	draft: {
		displayType: 'secondary',
		label: Liferay.Language.get('draft'),
	},
};

type ActionItems = ComponentProps<typeof ClayDropDownWithItems>['items'];

export type ListItem = {
	key: string;
	name: string;
	status: Status;
	version?: PageVersion;
};

export default function VersionList({
	items,
	onDelete,
	onSelect,
	searching,
	selectedKey,
}: {
	items: ListItem[];
	onDelete?: (version: PageVersion) => void;
	onSelect: (key: string) => void;
	searching: boolean;
	selectedKey?: string;
}) {
	const {getItemProps} = useKeyboardNavigation({itemCount: items.length});

	if (!items.length) {
		if (searching) {
			return (
				<ClayEmptyState
					description={Liferay.Language.get(
						'try-again-with-a-different-search'
					)}
					imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.svg`}
					small
					title={Liferay.Language.get('no-results-found')}
				/>
			);
		}

		return (
			<ClayEmptyState
				description=""
				small
				title={Liferay.Language.get('there-are-no-results')}
			/>
		);
	}

	const activeKey = selectedKey ?? items[0].key;

	return (
		<ClayList
			aria-label={Liferay.Language.get('version-history')}
			className="mb-0 version-history__list"
			role="listbox"
		>
			{items.map(({key, name, status, version}, index) => {
				const navigationProps = getItemProps(index);

				const selected = activeKey === key;

				const {displayType, label} = STATUSES[status];

				const actionItems = buildActionItems({onDelete, version});

				return (
					<ClayList.Item
						active={selected}
						aria-selected={selected}
						flex
						key={key}
						onClick={() => onSelect(key)}
						onFocus={navigationProps.onFocus}
						onKeyDown={(event) => {
							if (event.key === 'Enter' || event.key === ' ') {
								event.preventDefault();

								onSelect(key);

								return;
							}

							navigationProps.onKeyDown(event);
						}}
						ref={navigationProps.ref}
						role="option"
						tabIndex={navigationProps.tabIndex}
					>
						<ClayList.ItemField className="px-2">
							{version?.creator ? (
								<ClaySticker shape="circle">
									<ClaySticker.Image
										alt={version.creator.name}
										src={
											version.creator.image ??
											config.defaultUserImageSrc
										}
									/>
								</ClaySticker>
							) : (
								<ClaySticker displayType="secondary">
									<ClayIcon symbol="sheets" />
								</ClaySticker>
							)}
						</ClayList.ItemField>

						<ClayList.ItemField className="px-2" expand>
							<ClayList.ItemTitle>{name}</ClayList.ItemTitle>

							{version?.creator ? (
								<ClayList.ItemText>
									{sub(
										Liferay.Language.get(
											'modified-by-x,-x'
										),
										[
											version.creator.name,
											`${dateUtils.format(new Date(version.dateModified), 'P')} ${dateUtils.format(new Date(version.dateModified), 'p')}`,
										]
									)}
								</ClayList.ItemText>
							) : null}

							<ClayList.ItemText>
								{version
									? sub(Liferay.Language.get('version-x'), [
											version.version,
										])
									: Liferay.Language.get('current-page')}
							</ClayList.ItemText>

							<ClayList.ItemText>
								<ClayLabel displayType={displayType}>
									{label}
								</ClayLabel>
							</ClayList.ItemText>
						</ClayList.ItemField>

						{actionItems.length ? (
							<ClayList.ItemField className="px-2">
								<ClayDropDownWithItems
									items={actionItems}
									trigger={
										<ClayButtonWithIcon
											aria-label={Liferay.Language.get(
												'show-options'
											)}
											borderless
											displayType="secondary"
											onClick={(event) =>
												event.stopPropagation()
											}
											small
											symbol="ellipsis-v"
										/>
									}
								/>
							</ClayList.ItemField>
						) : null}
					</ClayList.Item>
				);
			})}
		</ClayList>
	);
}

function buildActionItems({
	onDelete,
	version,
}: {
	onDelete?: (version: PageVersion) => void;
	version?: PageVersion;
}) {
	const items: ActionItems = [];

	if (version?.actions?.delete) {
		items.push({
			label: Liferay.Language.get('delete-version'),
			onClick: (event) => {
				event.stopPropagation();

				onDelete?.(version);
			},
			symbolLeft: 'trash',
		});
	}

	return items;
}
