/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React, {useEffect, useState} from 'react';

import './ObjectEntryStatusDataRenderer.scss';

import {fetch, sub} from 'frontend-js-web';

function formatDisplayDate(value: Date) {
	const date = new Date(value);

	const day = date?.getDate();

	const hours = date?.toLocaleTimeString('default', {
		hour: '2-digit',
		minute: '2-digit',
	});

	const month = date?.toLocaleDateString('default', {month: 'short'});

	const year = date?.getFullYear();

	return `${month} ${day}, ${year} ${hours}`;
}

function getDisplayType(status: string) {
	switch (status) {
		case 'approved':
			return 'success';
		case 'empty':
		case 'expired':
			return 'warning';
		case 'pending':
		case 'scheduled':
			return 'info';
		default:
			return 'secondary';
	}
}

export default function ObjectEntryStatusDataRenderer({
	itemData,
	restContextPath,
}: {
	itemData: ObjectEntry;
	restContextPath: string;
}) {
	const [versions, setVersions] = useState<ObjectEntry[]>([]);

	const displayDate = itemData.displayDate
		? new Date(itemData.displayDate)
		: null;

	const hasApprovedVersion = versions.some(
		(version: ObjectEntry) => version.status.label === 'approved'
	);

	const statusLabel = itemData.status.label;

	const showApprovedVersionLabel =
		hasApprovedVersion && statusLabel === 'scheduled';

	const versionNumber = itemData.systemProperties?.version?.number;

	useEffect(() => {
		if (versionNumber) {
			const makeFetch = async () => {
				const response = await fetch(
					`${restContextPath}/by-external-reference-code/${itemData.externalReferenceCode}/versions`
				);

				const data = await response.json();

				setVersions(data.items);
			};

			makeFetch();
		}
	}, [itemData.externalReferenceCode, restContextPath, versionNumber]);

	return (
		<div className="lfr-objects__object-entry-status">
			{showApprovedVersionLabel && (
				<ClayLabel displayType="success">
					{Liferay.Language.get('approved')}
				</ClayLabel>
			)}

			<ClayLabel displayType={getDisplayType(statusLabel)}>
				<ClayLabel.ItemExpand>
					{itemData.status.label_i18n}
				</ClayLabel.ItemExpand>

				{displayDate && statusLabel === 'scheduled' && (
					<ClayLabel.ItemAfter>
						<ClayTooltipProvider>
							<div
								title={sub(
									Liferay.Language.get(
										'this-entry-will-be-published-on-x'
									),
									`${formatDisplayDate(displayDate)}`
								)}
							>
								<ClayIcon symbol="question-circle-full" />
							</div>
						</ClayTooltipProvider>
					</ClayLabel.ItemAfter>
				)}
			</ClayLabel>
		</div>
	);
}
