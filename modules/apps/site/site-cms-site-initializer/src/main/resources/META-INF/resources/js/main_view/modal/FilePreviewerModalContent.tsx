/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayModal from '@clayui/modal';
import React from 'react';

import {IAssetFile} from '../../common/types/AssetType';
import FilePreview from './asset_navigation_view/FilePreview';

interface FilePreviewerModalContentProps {
	file: IAssetFile;
	headerName?: string;
}

export default function FilePreviewerModalContent({
	file,
	headerName,
}: FilePreviewerModalContentProps) {
	const {link, name} = file;

	return (
		<>
			<ClayModal.Header>
				<div className="autofit-row autofit-row-center">
					<div className="autofit-col autofit-col-expand">
						<div className="text-truncate">
							{headerName ? headerName : name}
						</div>
					</div>

					<div className="autofit-col pr-3">
						<ClayLink
							button
							displayType="primary"
							href={link.href}
							small
						>
							<span className="inline-item inline-item-before">
								<ClayIcon symbol="download" />
							</span>

							{Liferay.Language.get('download')}
						</ClayLink>
					</div>
				</div>
			</ClayModal.Header>

			<ClayModal.Body>
				<FilePreview file={file} />
			</ClayModal.Body>
		</>
	);
}
