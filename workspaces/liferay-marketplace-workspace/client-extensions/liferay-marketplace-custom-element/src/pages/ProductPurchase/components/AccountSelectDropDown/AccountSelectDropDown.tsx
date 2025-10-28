/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {useEffect, useRef, useState} from 'react';

import './AccountSelectDropDown.scss';

interface Option {
	key: string;
	name: string;
	text: string;
}

interface DropdDownProps {
	onChange: (value: string) => void;
	options: Option[];
	value?: Option;
}

export default function AcountSelectDropDown({
	onChange,
	options,
	value,
}: DropdDownProps) {
	const [isOpen, setIsOpen] = useState(false);
	const [selected, setSelected] = useState<Option | undefined>(value);
	const dropDownRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		function handleClickOutside(event: MouseEvent) {
			if (
				dropDownRef.current &&
				!dropDownRef.current.contains(event.target as Node)
			) {
				setIsOpen(false);
			}
		}
		document.addEventListener('click', handleClickOutside);

		return () => document.removeEventListener('click', handleClickOutside);
	}, []);

	function handleSelect(option: Option) {
		setSelected(option);
		setIsOpen(false);
		onChange(option.key);
	}

	return (
		<div className="custom-drop-down-container" ref={dropDownRef}>
			<button onClick={() => setIsOpen((open) => !open)}>
				<div className="align-items-center d-flex justify-content-between">
					{value?.name}

					<ClayIcon symbol="caret-bottom" />
				</div>
			</button>

			{isOpen && (
				<ul>
					{options.map((option) => (
						<li
							className={classNames({
								'drop-down-item-selected':
									selected?.key === option.key,
							})}
							key={option.key}
							onClick={() => handleSelect(option)}
						>
							<div className="align-items-center d-flex justify-content-between">
								<div className="d-flex flex-column">
									<b>{option.name}</b>

									<small>{option.text}</small>
								</div>
								{selected?.key === option.key && (
									<div>
										<ClayIcon
											className="ml-2"
											color="#004AD7"
											fontSize={14}
											symbol="check"
										/>
									</div>
								)}
							</div>
						</li>
					))}
				</ul>
			)}
		</div>
	);
}
