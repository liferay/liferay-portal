/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayList from '@clayui/list';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import FeatureFlagToggle from './FeatureFlagToggle';

export type TFeatureFlags = {
	companyId: number;
	dependenciesFulfilled: boolean;
	dependencyKeys: Array<string>;
	description: string;
	enabled: boolean;
	key: string;
	title: string;
};

interface IFeatureFlagListProps {
	featureFlags: Array<TFeatureFlags>;
}

const FeatureFlagList: React.FC<IFeatureFlagListProps> = ({featureFlags}) => {
	const [items, setItems] = useState<TFeatureFlags[]>(featureFlags);

	return (
		<>
			{!items.length && (
				<ClayEmptyState
					description={Liferay.Language.get(
						'no-feature-flags-were-found'
					)}
					imgProps={{
						alt: Liferay.Language.get(
							'no-feature-flags-were-found'
						),
					}}
					imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.svg`}
					imgSrcReducedMotion={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state_reduced_motion.svg`}
				/>
			)}

			{!!items.length && (
				<ClayList>
					{items.map(
						({
							companyId,
							dependenciesFulfilled,
							dependencyKeys,
							description,
							enabled,
							key,
							title,
						}: TFeatureFlags) => {
							return (
								<ClayList.Item flex key={key}>
									<ClayList.ItemField expand>
										<ClayList.ItemTitle>
											{`${title} `}

											<span className="small text-secondary">
												({key})
											</span>
										</ClayList.ItemTitle>

										<ClayList.ItemText>
											{description}
										</ClayList.ItemText>

										{!!dependencyKeys.length && (
											<ClayList.ItemText>
												{sub(
													Liferay.Language.get(
														'dependencies-x'
													),
													dependencyKeys.join(', ')
												)}
											</ClayList.ItemText>
										)}
									</ClayList.ItemField>

									<ClayList.ItemField>
										<FeatureFlagToggle
											ariaLabel={title}
											companyId={companyId}
											disabled={!dependenciesFulfilled}
											enabled={enabled}
											featureFlagKey={key}
											inputName={key}
											onItemsChange={(newItems) => {
												setItems((items) =>
													items.map((item) => {
														const newItem =
															newItems.find(
																(newItem) => {
																	return (
																		newItem.key ===
																		item.key
																	);
																}
															);

														if (newItem) {
															return {
																...item,
																...newItem,
															};
														}

														return item;
													})
												);
											}}
										/>
									</ClayList.ItemField>
								</ClayList.Item>
							);
						}
					)}
				</ClayList>
			)}
		</>
	);
};

export default FeatureFlagList;
