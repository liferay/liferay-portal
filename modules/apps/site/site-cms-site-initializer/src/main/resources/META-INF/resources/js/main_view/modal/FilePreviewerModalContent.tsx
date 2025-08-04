/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayModal from '@clayui/modal';
import classNames from 'classnames';

// @ts-ignore

import {ImagePreviewer} from 'document-library-preview-image';
import React from 'react';

type File = {
	externalReferenceCode: string;
	id: number;
	link: {
		href: string;
		label: string;
	};
	name: string;
	previewURL: string;
	thumbnailURL: string;
};

export default function FilePreviewerModalContent(file: File) {
	const {link, name, thumbnailURL} = file;
	const params = new URLSearchParams(thumbnailURL);
	const hasImagePreview = params.has('imageThumbnail');

	return (
		<>
			<ClayModal.Header>
				<div className="autofit-row autofit-row-center">
					<div className="autofit-col autofit-col-expand">
						<div className="text-truncate">{name}</div>
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

			<ClayModal.Body
				className={classNames({
					'bg-light': !hasImagePreview,
				})}
			>
				{hasImagePreview ? (
					<ImagePreviewer alt={name} imageURL={link.href} />
				) : (
					<ClayEmptyState
						description={Liferay.Language.get(
							'hmm-looks-like-this-item-does-not-have-a-preview-we-can-show-you'
						)}
						imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state_preview.svg`}
						title={Liferay.Language.get('no-preview-available')}
					/>
				)}
			</ClayModal.Body>
		</>
	);
}
