/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import React from 'react';

interface Props {
	imageSizes: string[];
	portletNamespace: string;
}

const NUMBER_OF_IMAGES_OPTIONS = ['1', '2', '3', '4'].map((option) => ({
	label: option,
}));

export function FormImage({imageSizes, portletNamespace}: Props) {
	const promptId = `${portletNamespace}prompt`;
	const sizeId = `${portletNamespace}size`;
	const numberOfImagesId = `${portletNamespace}numberOfImages`;

	return (
		<>
			<ClayForm.Group>
				<label htmlFor={promptId}>
					{Liferay.Language.get('description')}

					<ClayIcon
						className="c-ml-1 reference-mark"
						focusable="false"
						role="presentation"
						symbol="asterisk"
					/>
				</label>

				<ClayInput
					id={promptId}
					name={promptId}
					placeholder={Liferay.Language.get('write-something')}
					required
					type="text"
				/>
			</ClayForm.Group>

			<ClayLayout.Row gutters>
				<ClayLayout.Col>
					<ClayForm.Group>
						<label htmlFor={sizeId}>
							{Liferay.Language.get('image-size')}
						</label>

						<ClaySelectWithOption
							id={sizeId}
							name={sizeId}
							options={imageSizes.map((label) => ({
								label,
								value: label,
							}))}
						/>
					</ClayForm.Group>
				</ClayLayout.Col>

				<ClayLayout.Col>
					<ClayForm.Group>
						<label htmlFor={numberOfImagesId}>
							{Liferay.Language.get(
								'number-of-images-to-generate'
							)}
						</label>

						<ClaySelectWithOption
							id={numberOfImagesId}
							name={numberOfImagesId}
							options={NUMBER_OF_IMAGES_OPTIONS}
						/>
					</ClayForm.Group>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</>
	);
}
