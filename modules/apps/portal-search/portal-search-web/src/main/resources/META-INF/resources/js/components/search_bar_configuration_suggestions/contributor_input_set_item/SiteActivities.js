/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {LearnMessage} from 'frontend-js-components-web';
import React, {useRef, useState} from 'react';

import {
	CONTRIBUTOR_TYPES,
	CONTRIBUTOR_TYPES_ASAH_DEFAULT_DISPLAY_GROUP_NAMES,
	CONTRIBUTOR_TYPES_DEFAULT_ATTRIBUTES,
} from '../../../utils/types/contributorTypes';
import InputSetItemHeader from './InputSetItemHeader';
import CharacterThresholdInput from './inputs/CharacterThresholdInput';
import ContentTypesInput from './inputs/ContentTypesInput';
import DisplayGroupNameInput from './inputs/DisplayGroupNameInput';
import MatchDisplayLanguageInput from './inputs/MatchDisplayLanguageInput';
import MinimumSearchesInput from './inputs/MinimumSearchesInput';
import SizeInput from './inputs/SizeInput';
import TimeRangeInput from './inputs/TimeRangeInput';

function getSiteActivitiesContributorActivityOptions() {
	const options = [
		{
			contributorName: CONTRIBUTOR_TYPES.ASAH_TOP_SEARCH_SITE_ACTIVITY,
			description: Liferay.Language.get('top-searches-help'),
			learnMessageResourceKey: 'search-bar-suggestions-site-activities',
			title: Liferay.Language.get('top-searches'),
		},
		{
			contributorName: CONTRIBUTOR_TYPES.ASAH_RECENT_SEARCH_SITE_ACTIVITY,
			description: Liferay.Language.get('trending-searches-help'),
			learnMessageResourceKey: 'search-bar-suggestions-site-activities',
			title: Liferay.Language.get('trending-searches'),
		},
	];

	if (Liferay.FeatureFlags['LPS-176691']) {
		return options.concat([
			{
				contributorName:
					CONTRIBUTOR_TYPES.ASAH_RECENT_SEARCHES_USER_ACTIVITY,
				description: Liferay.Language.get('recent-searches-help'),
				learnMessageResourceKey:
					'search-bar-suggestions-site-activities',
				title: Liferay.Language.get('recent-searches'),
			},
			{
				contributorName:
					CONTRIBUTOR_TYPES.ASAH_RECENT_PAGES_USER_ACTIVITY,
				description: Liferay.Language.get('recent-pages-help'),
				learnMessageResourceKey:
					'search-bar-suggestions-site-activities',
				title: Liferay.Language.get('recent-pages'),
			},
			{
				contributorName:
					CONTRIBUTOR_TYPES.ASAH_RECENT_SITES_USER_ACTIVITY,
				description: Liferay.Language.get('recent-sites-help'),
				learnMessageResourceKey:
					'search-bar-suggestions-site-activities',
				title: Liferay.Language.get('recent-sites'),
			},
			{
				contributorName:
					CONTRIBUTOR_TYPES.ASAH_RECENT_ASSETS_USER_ACTIVITY,
				description: Liferay.Language.get('recently-viewed-help'),
				learnMessageResourceKey:
					'search-bar-suggestions-site-activities',
				title: Liferay.Language.get('recently-viewed'),
			},
		]);
	}

	return options;
}

function SiteActivities({index, onBlur, onInputSetItemChange, touched, value}) {
	const [showActivityDropdown, setShowActivityDropdown] = useState(false);

	const alignElementRef = useRef();

	const SITE_ACTIVITIES_CONTRIBUTOR_ACTIVITY_OPTIONS =
		getSiteActivitiesContributorActivityOptions();

	const _handleChangeAttributeInput = (property) => (event) => {
		onInputSetItemChange(index, {
			attributes: {
				...value.attributes,
				[property]: event.target.value,
			},
		});
	};

	const _handleChangeAttributeValue = (property) => (newValue) => {
		onInputSetItemChange(index, {
			attributes: {
				...value.attributes,
				[property]: newValue,
			},
		});
	};

	const _handleActivityDropdownClick = () => {
		setShowActivityDropdown(!showActivityDropdown);
	};

	const _handleActivityInputClick = (contributorName) => () => {
		onInputSetItemChange(index, {
			attributes: CONTRIBUTOR_TYPES_DEFAULT_ATTRIBUTES[contributorName],
			contributorName,
			displayGroupName:
				CONTRIBUTOR_TYPES_ASAH_DEFAULT_DISPLAY_GROUP_NAMES[
					contributorName
				] || '',
			size: '3',
		});

		setShowActivityDropdown(false);
	};

	return (
		<>
			<InputSetItemHeader>
				<InputSetItemHeader.Title>
					{Liferay.Language.get(
						'site-activities-suggestions-contributor'
					)}
				</InputSetItemHeader.Title>

				<InputSetItemHeader.Description>
					{Liferay.Language.get(
						'site-activities-suggestions-contributor-help'
					)}

					<LearnMessage
						className="c-ml-1"
						resource="portal-search-web"
						resourceKey="search-bar-suggestions-site-activities"
					/>
				</InputSetItemHeader.Description>
			</InputSetItemHeader>

			<div className="c-mb-3 form-group-autofit">
				<ClayInput.GroupItem>
					<label htmlFor={`activity${index}`}>
						<span>
							{Liferay.Language.get('activity')}

							<span className="reference-mark">
								<ClayIcon symbol="asterisk" />
							</span>
						</span>
					</label>

					<ClayButton
						aria-label={Liferay.Language.get(
							'suggestion-contributor'
						)}
						className="form-control form-control-select"
						displayType="unstyled"
						id={`activity${index}`}
						onClick={_handleActivityDropdownClick}
						ref={alignElementRef}
					>
						{
							SITE_ACTIVITIES_CONTRIBUTOR_ACTIVITY_OPTIONS.find(
								({contributorName}) =>
									contributorName === value.contributorName
							)?.title
						}
					</ClayButton>

					<ClayDropDown.Menu
						active={showActivityDropdown}
						alignElementRef={alignElementRef}
						closeOnClickOutside
						onSetActive={setShowActivityDropdown}
						style={{
							maxWidth:
								alignElementRef.current &&
								alignElementRef.current.clientWidth + 'px',
						}}
					>
						<ClayDropDown.ItemList
							items={SITE_ACTIVITIES_CONTRIBUTOR_ACTIVITY_OPTIONS}
						>
							{(item) => (
								<ClayDropDown.Item
									active={
										value.contributorName ===
										item.contributorName
									}
									key={item.name}
									onClick={_handleActivityInputClick(
										item.contributorName
									)}
								>
									<div>{item.title}</div>

									<div className="text-2">
										{item.description}

										<LearnMessage
											className="c-ml-1"
											resource="portal-search-web"
											resourceKey={
												item.learnMessageResourceKey
											}
										/>
									</div>
								</ClayDropDown.Item>
							)}
						</ClayDropDown.ItemList>
					</ClayDropDown.Menu>
				</ClayInput.GroupItem>
			</div>

			<div className="c-mb-3 form-group-autofit">
				<DisplayGroupNameInput
					index={index}
					onBlur={onBlur('displayGroupName')}
					onChange={onInputSetItemChange(index, 'displayGroupName')}
					touched={touched.displayGroupName}
					value={value.displayGroupName}
				/>

				<SizeInput
					index={index}
					onBlur={onBlur('size')}
					onChange={onInputSetItemChange(index, 'size')}
					touched={touched.size}
					value={value.size}
				/>
			</div>

			{[
				CONTRIBUTOR_TYPES.ASAH_TOP_SEARCH_SITE_ACTIVITY,
				CONTRIBUTOR_TYPES.ASAH_RECENT_SEARCH_SITE_ACTIVITY,
			].includes(value.contributorName) ? (
				<div className="c-mb-0 form-group-autofit">
					<CharacterThresholdInput
						index={index}
						onBlur={onBlur('attributes.characterThreshold')}
						onChange={_handleChangeAttributeInput(
							'characterThreshold'
						)}
						touched={touched['attributes.characterThreshold']}
						value={value.attributes?.characterThreshold}
					/>

					<MatchDisplayLanguageInput
						index={index}
						onChange={_handleChangeAttributeInput(
							'matchDisplayLanguageId'
						)}
						value={value.attributes?.matchDisplayLanguageId}
					/>

					<MinimumSearchesInput
						index={index}
						onBlur={onBlur('attributes.minCounts')}
						onChange={_handleChangeAttributeInput('minCounts')}
						touched={touched['attributes.minCounts']}
						value={value.attributes?.minCounts}
					/>
				</div>
			) : [
					CONTRIBUTOR_TYPES.ASAH_RECENT_PAGES_USER_ACTIVITY,
					CONTRIBUTOR_TYPES.ASAH_RECENT_SITES_USER_ACTIVITY,
			  ].includes(value.contributorName) ? (
				<div className="c-mb-0 form-group-autofit">
					<CharacterThresholdInput
						index={index}
						onBlur={onBlur('attributes.characterThreshold')}
						onChange={_handleChangeAttributeInput(
							'characterThreshold'
						)}
						touched={touched['attributes.characterThreshold']}
						value={value.attributes?.characterThreshold}
					/>

					<TimeRangeInput
						index={index}
						onBlur={onBlur('attributes.rangeKey')}
						onChange={_handleChangeAttributeValue('rangeKey')}
						touched={touched['attributes.rangeKey']}
						value={value.attributes?.rangeKey}
					/>
				</div>
			) : (
				<>
					<div className="c-mb-3 form-group-autofit">
						<CharacterThresholdInput
							index={index}
							onBlur={onBlur('attributes.characterThreshold')}
							onChange={_handleChangeAttributeInput(
								'characterThreshold'
							)}
							touched={touched['attributes.characterThreshold']}
							value={value.attributes?.characterThreshold}
						/>

						{value.contributorName ===
							CONTRIBUTOR_TYPES.ASAH_RECENT_SEARCHES_USER_ACTIVITY && (
							<MatchDisplayLanguageInput
								index={index}
								onChange={_handleChangeAttributeInput(
									'matchDisplayLanguageId'
								)}
								value={value.attributes?.matchDisplayLanguageId}
							/>
						)}

						{value.contributorName ===
							CONTRIBUTOR_TYPES.ASAH_RECENT_ASSETS_USER_ACTIVITY && (
							<ContentTypesInput
								index={index}
								onBlur={onBlur('attributes.contentTypes')}
								onChange={_handleChangeAttributeValue(
									'contentTypes'
								)}
								touched={touched['attributes.contentTypes']}
								value={value.attributes?.contentTypes}
							/>
						)}
					</div>

					<div className="c-mb-0 form-group-autofit">
						{value.contributorName ===
							CONTRIBUTOR_TYPES.ASAH_RECENT_SEARCHES_USER_ACTIVITY && (
							<MinimumSearchesInput
								index={index}
								onBlur={onBlur('attributes.minCounts')}
								onChange={_handleChangeAttributeInput(
									'minCounts'
								)}
								touched={touched['attributes.minCounts']}
								value={value.attributes?.minCounts}
							/>
						)}

						<TimeRangeInput
							index={index}
							onChange={_handleChangeAttributeValue('rangeKey')}
							value={value.attributes?.rangeKey}
						/>
					</div>
				</>
			)}
		</>
	);
}

export default SiteActivities;
