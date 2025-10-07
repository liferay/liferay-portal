/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import classNames from 'classnames';
import React from 'react';

import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';
import formatActionURL from '../../../common/utils/formatActionURL';
import ContentPreview from './ContentPreview';
import FilePreview from './FilePreview';

const Arrow = ({
	ariaLabel,
	className,
	control,
	handleClick,
}: {
	ariaLabel: string;
	className?: string;
	control: 'next' | 'previous';
	handleClick: any;
}) => (
	<div
		className={classNames(
			`position-absolute carousel-${control}`,
			className
		)}
	>
		<ClayButtonWithIcon
			aria-label={ariaLabel}
			displayType="secondary"
			onClick={handleClick}
			rounded
			symbol={`angle-${{next: 'right', previous: 'left'}[control]}`}
		/>
	</div>
);

export default function Carousel({
	contentViewURL,
	currentItem,
	handleClickNext,
	handleClickPrevious,
	showArrows = true,
}: {
	contentViewURL: string;
	currentItem: ISearchAssetObjectEntry;
	handleClickNext: () => void;
	handleClickPrevious: () => void;
	showArrows: boolean;
}) {
	return (
		<div className="carousel d-flex h-100">
			{showArrows && (
				<>
					<Arrow
						ariaLabel={Liferay.Language.get('previous')}
						className="ml-4"
						control="previous"
						handleClick={handleClickPrevious}
					/>
					<Arrow
						ariaLabel={Liferay.Language.get('next')}
						className="mr-4"
						control="next"
						handleClick={handleClickNext}
					/>
				</>
			)}

			<div className="h-100 mx-6 preview-container py-4 w-100">
				{currentItem.embedded?.file ? (
					<FilePreview file={currentItem.embedded.file} />
				) : (
					<ContentPreview
						url={formatActionURL(currentItem, contentViewURL)}
					/>
				)}
			</div>
		</div>
	);
}
