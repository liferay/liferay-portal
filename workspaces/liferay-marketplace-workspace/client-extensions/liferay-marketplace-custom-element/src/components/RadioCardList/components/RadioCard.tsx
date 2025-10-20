/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayRadio} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import {ReactNode} from 'react';

import './RadioCard.scss';

interface RadioCardProps {
	activeRadio: boolean | undefined;
	description?: ReactNode;
	disabled?: boolean;
	fullTitle?: boolean;
	imageURL?: string;
	index?: number;
	label?: string;
	leftRadio?: boolean;
	selectRadio: () => void;
	showImage?: boolean;
	title: ReactNode;
}

const NewRadioCard = ({
	activeRadio,
	description,
	disabled,
	fullTitle = false,
	imageURL,
	index,
	label,
	leftRadio,
	selectRadio,
	showImage,
	title,
}: RadioCardProps) => {
	return (
		<div
			className={classNames(
				'align-items-center cursor-pointer d-flex form-control justify-content-between mb-5 px-0 py-4 radio-card',
				{
					'bg-transparent': !activeRadio,
					'radio-disabled': disabled,
					'radio-selected': activeRadio,
				}
			)}
			key={index}
			onClick={() => selectRadio()}
		>
			<div className="col">
				<div
					className={classNames('d-flex align-items-center col', {
						'mb-2': description,
					})}
				>
					{leftRadio && (
						<div className="col-1">
							<ClayRadio
								checked={activeRadio}
								onChange={() => selectRadio()}
								type="radio"
								value={String(title)}
							/>
						</div>
					)}

					<div className="align-items-center col d-flex px-0">
						{showImage && (
							<div
								className={classNames(
									'd-flex justify-content-center',
									{
										'col-2 pr-0': leftRadio,
										'col-3': !leftRadio,
									}
								)}
							>
								<ClaySticker shape="circle" size="lg">
									{imageURL ? (
										<ClaySticker.Image
											alt="placeholder"
											src={imageURL}
										/>
									) : (
										<ClayIcon symbol="picture" />
									)}
								</ClaySticker>
							</div>
						)}

						<div
							className={classNames({
								'col-10': !fullTitle,
								'col-12 pr-0': fullTitle,
								'pl-0': !leftRadio,
							})}
						>
							{title}
						</div>
					</div>

					{label && (
						<ClayLabel
							className="radio-card-label"
							displayType="info"
						>
							{label}
						</ClayLabel>
					)}
				</div>

				{description && (
					<div className="col d-flex justify-content-end">
						<p
							className={classNames('mb-0 text-paragraph', {
								'col-10': showImage,
								'col-11': !showImage,
								'pl-6': !leftRadio && showImage,
							})}
						>
							{description}
						</p>
					</div>
				)}
			</div>

			{!leftRadio && (
				<div className="col-2">
					<ClayRadio
						checked={activeRadio}
						onChange={() => selectRadio()}
						type="radio"
						value={String(title)}
					/>
				</div>
			)}
		</div>
	);
};

export default NewRadioCard;
