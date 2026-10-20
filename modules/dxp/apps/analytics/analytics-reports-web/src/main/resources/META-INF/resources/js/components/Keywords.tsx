/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import PropTypes from 'prop-types';
import React, {useContext, useMemo, useState} from 'react';

import {StoreStateContext} from '../context/StoreContext';
import {numberFormat} from '../utils/numberFormat';
import Hint from './Hint';

const KEYWORD_VALUE_TYPE = [
	{label: Liferay.Language.get('traffic'), name: 'traffic'},
	{label: Liferay.Language.get('search-volume'), name: 'volume'},
	{label: Liferay.Language.get('position[ranking]'), name: 'position'},
];

type CountryKeyworks = {
	countryCode: string;
	countryName: string;
	keywords: {
		keyword: string;
		position: number;
		searchVolume: number;
		traffic: number;
	}[];
};

interface Props {
	currentPage: {
		data: {
			countrySearchKeywords: CountryKeyworks[];
			helpMessage: string;
			name: string;
			share: number;
			title: string;
			value: number;
		};
		view: string;
	};
}

export default function Keywords({currentPage}: Props) {
	const [isDropdownOpen, setIsDropdownOpen] = useState(false);

	const [keywordValueType, setKeywordValueType] = useState(
		KEYWORD_VALUE_TYPE[0]
	);

	const {languageTag, publishedToday} = useContext(StoreStateContext);

	const countries: Array<{
		countryCode: string;
		countryName: string;
	}> = useMemo(() => {
		const dataKeys = new Set();

		return currentPage.data.countrySearchKeywords.reduce(
			(acc, {countryCode, countryName}) => {
				if (dataKeys.has(countryCode)) {
					return acc;
				}

				dataKeys.add(countryCode);

				return acc.concat({countryCode, countryName});
			},
			[] as {countryCode: string; countryName: string}[]
		);
	}, [currentPage.data.countrySearchKeywords]);

	const [currentCountry, setCurrentCountry] = useState(
		countries[0].countryCode
	);

	const keywords = useMemo(() => {
		const countryKeywords =
			currentCountry &&
			currentPage.data.countrySearchKeywords.find((country) => {
				return country.countryCode === currentCountry;
			});

		return countryKeywords ? countryKeywords.keywords : [];
	}, [currentPage.data.countrySearchKeywords, currentCountry]);

	const handleCountrySelection: React.ChangeEventHandler<
		HTMLSelectElement
	> = (event) => {
		const country = event.target.value;
		setCurrentCountry(country);
	};

	const handleKeywordValueType = (valueTypeName: string) => {
		const newKeywordValueType = KEYWORD_VALUE_TYPE.find(
			(keywordValueType) => {
				return keywordValueType.name === valueTypeName;
			}
		);

		if (newKeywordValueType) {
			setKeywordValueType(newKeywordValueType);
		}
	};

	return (
		<>
			<div className="mt-3 sheet-subtitle">
				{Liferay.Language.get('best-keywords')}
			</div>

			<div className="mb-3 text-secondary">
				{Liferay.Language.get('best-keywords-description')}
			</div>

			<div className="form-group">
				<label htmlFor="countrySelect">
					{Liferay.Language.get('select-country')}
				</label>

				<ClaySelect
					aria-label={Liferay.Language.get('select-country')}
					className="bg-white"
					defaultValue={currentCountry}
					id="countrySelect"
					onChange={handleCountrySelection}
				>
					{countries.map((item) => (
						<ClaySelect.Option
							key={item.countryCode}
							label={item.countryName}
							value={item.countryCode}
						/>
					))}
				</ClaySelect>
			</div>

			<ClayList className="list-group-keywords-list">
				<ClayList.Item flex>
					<ClayList.ItemField expand>
						<ClayList.ItemTitle className="text-truncate-inline">
							<span className="text-truncate">
								{Liferay.Language.get('best-keyword')}

								<span className="text-secondary">
									<Hint
										message={Liferay.Language.get(
											'best-keyword-help'
										)}
										title={Liferay.Language.get(
											'best-keyword'
										)}
									/>
								</span>
							</span>
						</ClayList.ItemTitle>
					</ClayList.ItemField>

					<ClayList.ItemField>
						<ClayDropDown
							active={isDropdownOpen}
							onActiveChange={setIsDropdownOpen}
							trigger={
								<ClayButton
									className="px-0 text-body"
									displayType="link"
									small
								>
									<span className="font-weight-semi-bold">
										<span className="pr-2">
											{keywordValueType.label}
										</span>

										<ClayIcon symbol="caret-bottom" />
									</span>
								</ClayButton>
							}
						>
							<ClayDropDown.ItemList>
								{KEYWORD_VALUE_TYPE.map((valueType, index) => (
									<ClayDropDown.Item
										active={
											valueType.name ===
											keywordValueType.name
										}
										key={index}
										onClick={() => {
											handleKeywordValueType(
												valueType.name
											);
											setIsDropdownOpen(false);
										}}
									>
										{valueType.label}
									</ClayDropDown.Item>
								))}
							</ClayDropDown.ItemList>
						</ClayDropDown>
					</ClayList.ItemField>
				</ClayList.Item>

				<>
					{!publishedToday &&
						keywords.map(
							({keyword, position, searchVolume, traffic}) => {
								return (
									<ClayList.Item flex key={keyword}>
										<ClayList.ItemField expand>
											<ClayList.ItemText>
												<span
													className="text-truncate-inline"
													data-tooltip-align="top"
													title={keyword}
												>
													<span className="text-secondary text-truncate">
														{keyword}
													</span>
												</span>
											</ClayList.ItemText>
										</ClayList.ItemField>

										<ClayList.ItemField expand>
											<span className="align-self-end font-weight-semi-bold text-dark">
												{!languageTag
													? ''
													: numberFormat(
															languageTag,
															keywordValueType.name ===
																'traffic'
																? traffic
																: keywordValueType.name ===
																	  'volume'
																	? searchVolume
																	: position
														)}
											</span>
										</ClayList.ItemField>
									</ClayList.Item>
								);
							}
						)}
				</>
				<>
					{(publishedToday || !keywords.length) && (
						<ClayList.Item flex>
							<ClayList.ItemField expand>
								<ClayList.ItemText>
									<span className="text-secondary">
										{Liferay.Language.get(
											'there-are-no-best-keywords-yet'
										)}
									</span>
								</ClayList.ItemText>
							</ClayList.ItemField>
						</ClayList.Item>
					)}
				</>
			</ClayList>
		</>
	);
}

Keywords.propTypes = {
	currentPage: PropTypes.object.isRequired,
};
