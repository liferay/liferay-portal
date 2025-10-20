/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReactNode} from 'react';

import RadioCard from './components/RadioCard';

type RadioCardContent<T = any> = {
	children?: ReactNode;
	description?: ReactNode;
	disabled?: boolean;
	fullTitle?: boolean;
	id: number | string;
	imageURL?: string;
	label?: string;
	selected: boolean;
	title: ReactNode;
	value: T;
};

type RadioCardListProps<T> = {
	contentList: RadioCardContent<T>[];
	leftRadio?: boolean;
	onSelect: (value: RadioOption<T>) => void;
	showImage?: boolean;
};

const RadioCardList = <T extends unknown>({
	contentList,
	leftRadio,
	onSelect,
	showImage,
}: RadioCardListProps<T>) => {
	const handleSelectRadio = (selectedRadio: RadioOption<T>) => {
		onSelect(selectedRadio);
	};

	return (
		<div className="mb-0 w-100">
			{contentList.map((content, index) => (
				<RadioCard
					activeRadio={content.selected}
					description={content.description}
					disabled={content.disabled}
					fullTitle={content.fullTitle}
					imageURL={content.imageURL}
					index={index}
					key={index}
					label={content.label}
					leftRadio={leftRadio}
					selectRadio={() =>
						handleSelectRadio({index, value: content.value})
					}
					showImage={showImage}
					title={content.title}
				/>
			))}
		</div>
	);
};

export default RadioCardList;
