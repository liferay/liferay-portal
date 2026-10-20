/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import {formatStorage} from 'frontend-js-web';
import React from 'react';

import {ImportPreview} from '../../../types/exportImportPreview';
import formatDate from '../../../utils/formatDate';

function MetadataItem({
	icon,
	label,
	value,
}: {
	icon: string;
	label: string;
	value: string;
}) {
	return (
		<div className="align-items-start border d-flex p-3 rounded">
			<span className="sticker sticker-secondary sticker-sm">
				<ClayIcon symbol={icon} />
			</span>

			<dl className="mb-0 ml-3">
				<dt className="font-weight-semi-bold">{label}</dt>

				<dd className="mb-0 text-secondary">{value}</dd>
			</dl>
		</div>
	);
}

export default function FileSummary({
	importPreview,
}: {
	importPreview: ImportPreview;
}) {
	const {author, exportDate, fileName, fileSize} = importPreview;

	return (
		<ClayLayout.Sheet className="mt-5">
			<ClayLayout.SheetHeader className="mb-1">
				<div className="mb-2 sheet-title">
					{Liferay.Language.get('file-summary')}
				</div>

				{fileName && (
					<div className="sheet-text text-3 text-secondary">
						{fileName}
					</div>
				)}
			</ClayLayout.SheetHeader>

			<ClayLayout.Row>
				<ClayLayout.Col md={4}>
					<MetadataItem
						icon="export"
						label={Liferay.Language.get('exported')}
						value={exportDate ? formatDate(exportDate) : '—'}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col md={4}>
					<MetadataItem
						icon="user"
						label={Liferay.Language.get('author')}
						value={author ?? '—'}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col md={4}>
					<MetadataItem
						icon="paperclip"
						label={Liferay.Language.get('size')}
						value={
							typeof fileSize === 'number'
								? formatStorage(fileSize, {
										addSpaceBeforeSuffix: true,
									})
								: '—'
						}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</ClayLayout.Sheet>
	);
}
