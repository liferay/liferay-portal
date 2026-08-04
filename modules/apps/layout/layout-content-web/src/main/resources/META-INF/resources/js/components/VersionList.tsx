/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import {dateUtils, sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {Layout, config} from '../config';
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

type Row = {
	key: string;
	name: string;
	status: Status;
	version?: PageVersion;
};

export default function VersionList({
	layout,
	searching,
	versions,
}: {
	layout?: Layout;
	searching: boolean;
	versions: PageVersion[];
}) {
	const [selectedKey, setSelectedKey] = useState<string>();

	const rows: Row[] = [
		...(layout ? [{key: 'current', ...layout}] : []),
		...versions.map((version) => ({
			key: version.externalReferenceCode,
			name: version.name,
			status: version.status,
			version,
		})),
	];

	const {getItemProps} = useKeyboardNavigation({itemCount: rows.length});

	if (!rows.length) {
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

	const activeKey = selectedKey ?? rows[0].key;

	return (
		<ClayList
			aria-label={Liferay.Language.get('version-history')}
			className="mb-0 version-history__list"
			role="listbox"
		>
			{rows.map(({key, name, status, version}, index) => {
				const navigationProps = getItemProps(index);

				const selected = activeKey === key;

				const {displayType, label} = STATUSES[status];

				return (
					<ClayList.Item
						active={selected}
						aria-selected={selected}
						flex
						key={key}
						onClick={() => setSelectedKey(key)}
						onFocus={navigationProps.onFocus}
						onKeyDown={(event) => {
							if (event.key === 'Enter' || event.key === ' ') {
								event.preventDefault();

								setSelectedKey(key);

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
					</ClayList.Item>
				);
			})}
		</ClayList>
	);
}
