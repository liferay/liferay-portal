/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayCard from '@clayui/card';
import {ClayCheckbox} from '@clayui/form';
import React from 'react';

interface Props {
	imagesURL: string[];
	onSelectedChange: Function;
	selectedImages: number[];
}

export function ImagesResult({
	imagesURL,
	onSelectedChange,
	selectedImages,
}: Props) {
	return (
		<>
			<label>{Liferay.Language.get('image-results')}</label>

			<ul className="card-page card-page-equal-height">
				{imagesURL.map((imageURL, index) => (
					<li
						className="card-page-item card-page-item-asset"
						key={index}
						role="menuitem"
					>
						<ClayCard displayType="image" selectable>
							<ClayCheckbox
								checked={selectedImages.includes(index)}
								onChange={() => onSelectedChange(index)}
							>
								<ClayCard.AspectRatio className="card-item-first card-item-last">
									<img
										className="aspect-ratio-item-center-middle aspect-ratio-item-fluid"
										src={imageURL}
									/>
								</ClayCard.AspectRatio>
							</ClayCheckbox>
						</ClayCard>
					</li>
				))}
			</ul>
		</>
	);
}
