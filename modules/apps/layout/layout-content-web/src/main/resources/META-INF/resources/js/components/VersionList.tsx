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
import React from 'react';

import {PageVersion, VersionStatus} from '../types/PageVersion';

const DRAFT_KEY = 'draft';

const STATUSES: Record<
	VersionStatus,
	{displayType: 'secondary' | 'success'; label: string}
> = {
	Approved: {
		displayType: 'success',
		label: Liferay.Language.get('published'),
	},
	Draft: {
		displayType: 'secondary',
		label: Liferay.Language.get('draft'),
	},
};

type Row = {
	key: string;
	name: string;
	version?: PageVersion;
};

export default function VersionList({
	draftName,
	versions,
}: {
	draftName?: string;
	versions: PageVersion[];
}) {
	const rows: Row[] = [
		...(draftName ? [{key: DRAFT_KEY, name: draftName}] : []),
		...versions.map((version) => ({
			key: version.externalReferenceCode,
			name: version.name,
			version,
		})),
	];

	if (!rows.length) {
		return (
			<ClayEmptyState
				description=""
				small
				title={Liferay.Language.get('there-are-no-results')}
			/>
		);
	}

	return (
		<ClayList className="mb-0">
			{rows.map(({key, name, version}) => {
				const status = version
					? STATUSES[version.status]
					: STATUSES.Draft;

				return (
					<ClayList.Item flex key={key}>
						<ClayList.ItemField className="px-2">
							{version?.creator ? (
								<ClaySticker shape="circle">
									<ClaySticker.Image
										alt={version.creator.name}
										src={version.creator.image}
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
											'modified-by-x-on-x'
										),
										[
											version.creator.name,
											`${dateUtils.format(new Date(version.dateModified), 'P')} ${dateUtils.format(new Date(version.dateModified), 'p')}`,
										]
									)}
								</ClayList.ItemText>
							) : null}

							{version ? (
								<ClayList.ItemText>
									{sub(Liferay.Language.get('version-x'), [
										version.version,
									])}
								</ClayList.ItemText>
							) : null}

							<ClayList.ItemText>
								<ClayLabel displayType={status.displayType}>
									{status.label}
								</ClayLabel>
							</ClayList.ItemText>
						</ClayList.ItemField>
					</ClayList.Item>
				);
			})}
		</ClayList>
	);
}
