/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayRadio, ClayRadioGroup, ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayPanel from '@clayui/panel';
import ClaySticker from '@clayui/sticker';
import React, {useState} from 'react';

import CustomPanel from '../../shared/CustomPanel';
import {
	IFrameworkConfiguration,
	ISearchableType,
	ISelectedSubtype,
} from '../../utils/types';
import SelectTypes from './SelectTypes';

const QUERY_CONTRIBUTORS_OPTIONS = {
	CUSTOMIZE: 'customize',
	DISABLE_ALL: 'disable-all',
	ENABLE_ALL: 'enable-all',
};

function Source({
	applyIndexerClauses,
	assetSubtypesMap,
	clauseContributorsList,
	frameworkConfig,
	onApplyIndexerClausesChange,
	onAssetSubtypesMapChange,
	onChangeClauseContributorsVisibility,
	onChangeIndexerClausesHelpVisibility,
	onChangeQueryContributorsHelpVisibility,
	onFetchSearchableTypes,
	onFrameworkConfigChange,
	searchableTypes,
}: {
	applyIndexerClauses: boolean;
	assetSubtypesMap: {[key: string]: string};
	clauseContributorsList: string[];
	frameworkConfig: IFrameworkConfiguration;
	onApplyIndexerClausesChange: (value: boolean) => void;
	onAssetSubtypesMapChange: (subtypes: ISelectedSubtype[]) => void;
	onChangeClauseContributorsVisibility: (visible?: boolean) => void;
	onChangeIndexerClausesHelpVisibility: (visible?: boolean) => void;
	onChangeQueryContributorsHelpVisibility: (visible?: boolean) => void;
	onFetchSearchableTypes: () => Promise<{
		searchableTypes: ISearchableType[];
	}>;
	onFrameworkConfigChange: (config: IFrameworkConfiguration) => void;
	searchableTypes: ISearchableType[];
}) {
	const [selectAllTypes, setSelectAllTypes] = useState(
		frameworkConfig.searchableAssetTypes?.length === 0
	);
	const [queryContributorsSetting, setQueryContributorsSetting] = useState(
		frameworkConfig.clauseContributorsIncludes?.[0] === '*' &&
			!frameworkConfig.clauseContributorsExcludes?.length
			? QUERY_CONTRIBUTORS_OPTIONS.ENABLE_ALL
			: frameworkConfig.clauseContributorsExcludes?.[0] === '*' &&
				  !frameworkConfig.clauseContributorsIncludes?.length
				? QUERY_CONTRIBUTORS_OPTIONS.DISABLE_ALL
				: QUERY_CONTRIBUTORS_OPTIONS.CUSTOMIZE
	);

	const _handleApplyIndexerClausesChange = () => {
		onApplyIndexerClausesChange(!applyIndexerClauses);
	};

	const _handleQueryContributorsSettingChange = (value: string) => {
		setQueryContributorsSetting(value);

		if (value === QUERY_CONTRIBUTORS_OPTIONS.ENABLE_ALL) {
			onFrameworkConfigChange({
				clauseContributorsExcludes: [],
				clauseContributorsIncludes: ['*'],
			});

			onChangeClauseContributorsVisibility(false);
		}
		else if (value === QUERY_CONTRIBUTORS_OPTIONS.DISABLE_ALL) {
			onFrameworkConfigChange({
				clauseContributorsExcludes: ['*'],
				clauseContributorsIncludes: [],
			});

			onChangeClauseContributorsVisibility(false);
		}
		else {
			onFrameworkConfigChange({
				clauseContributorsExcludes: [],
				clauseContributorsIncludes: clauseContributorsList,
			});
		}
	};

	const _handleSelectAllTypesChange = (selectAll: boolean) => {
		setSelectAllTypes(selectAll);

		onFrameworkConfigChange({
			searchableAssetTypes: [],
		});
	};

	return (
		<CustomPanel classNames="source" title={Liferay.Language.get('source')}>
			<ClayPanel.Group flush small>
				<ClayPanel
					className="searchable-types"
					collapsable
					defaultExpanded
					displayTitle={Liferay.Language.get(
						'searchable-types-and-subtypes'
					)}
					displayType="unstyled"
					showCollapseIcon
				>
					<ClayPanel.Body>
						<ClayRadioGroup
							onChange={(value: string | number) =>
								_handleSelectAllTypesChange(value === 'true')
							}
							value={selectAllTypes.toString()}
						>
							<ClayRadio
								label={Liferay.Language.get(
									'all-searchable-types'
								)}
								value="true"
							/>

							<ClayRadio
								label={Liferay.Language.get('selected-types')}
								value="false"
							/>
						</ClayRadioGroup>

						{!selectAllTypes && (
							<>
								<div className="sheet-text">
									{Liferay.Language.get(
										'select-the-searchable-types-description'
									)}
								</div>

								<SelectTypes
									assetSubtypesMap={assetSubtypesMap}
									initialSelectedTypes={
										frameworkConfig.searchableAssetTypes
									}
									onAssetSubtypesMapChange={
										onAssetSubtypesMapChange
									}
									onFetchSearchableTypes={
										onFetchSearchableTypes
									}
									onFrameworkConfigChange={
										onFrameworkConfigChange
									}
									searchableTypes={searchableTypes}
								/>
							</>
						)}
					</ClayPanel.Body>
				</ClayPanel>

				<ClayPanel
					collapsable
					defaultExpanded={false}
					displayTitle={
						<ClayPanel.Title>
							<span className="panel-title text-uppercase">
								{Liferay.Language.get(
									'search-framework-indexer-clauses'
								)}
							</span>

							<ClaySticker
								displayType="secondary"
								onClick={(event) => {
									event.stopPropagation();
									onChangeIndexerClausesHelpVisibility();
								}}
							>
								<ClayIcon symbol="question-circle" />
							</ClaySticker>
						</ClayPanel.Title>
					}
					displayType="unstyled"
					showCollapseIcon
				>
					<ClayPanel.Body>
						<ClayToggle
							label={
								applyIndexerClauses
									? Liferay.Language.get('on')
									: Liferay.Language.get('off')
							}
							onToggle={_handleApplyIndexerClausesChange}
							toggled={!!applyIndexerClauses}
						/>

						{!applyIndexerClauses && (
							<div className="has-warning">
								<ClayForm.FeedbackItem className="c-pb-3">
									<ClayForm.FeedbackIndicator symbol="warning-full" />

									{Liferay.Language.get('warning-colon')}

									<span className="c-pl-1 font-weight-normal">
										{Liferay.Language.get(
											'search-framework-indexer-clauses-warning'
										)}
									</span>
								</ClayForm.FeedbackItem>
							</div>
						)}
					</ClayPanel.Body>
				</ClayPanel>

				<ClayPanel
					collapsable
					defaultExpanded={false}
					displayTitle={
						<ClayPanel.Title>
							<span className="panel-title text-uppercase">
								{Liferay.Language.get(
									'search-framework-query-contributors'
								)}
							</span>

							<ClaySticker
								displayType="secondary"
								onClick={(event) => {
									event.stopPropagation();
									onChangeQueryContributorsHelpVisibility();
								}}
							>
								<ClayIcon symbol="question-circle" />
							</ClaySticker>
						</ClayPanel.Title>
					}
					displayType="unstyled"
					showCollapseIcon
				>
					<ClayPanel.Body>
						<ClayRadioGroup
							onChange={(value: string | number) => {
								_handleQueryContributorsSettingChange(
									value as string
								);
							}}
							value={queryContributorsSetting}
						>
							<ClayRadio
								label={Liferay.Language.get('enable-all')}
								value={QUERY_CONTRIBUTORS_OPTIONS.ENABLE_ALL}
							/>

							<ClayRadio
								label={Liferay.Language.get('disable-all')}
								value={QUERY_CONTRIBUTORS_OPTIONS.DISABLE_ALL}
							/>

							<ClayRadio
								label={Liferay.Language.get('action.CUSTOMIZE')}
								value={QUERY_CONTRIBUTORS_OPTIONS.CUSTOMIZE}
							/>
						</ClayRadioGroup>

						{queryContributorsSetting ===
							QUERY_CONTRIBUTORS_OPTIONS.CUSTOMIZE && (
							<>
								<div className="has-warning">
									<ClayForm.FeedbackItem className="c-pb-3">
										<ClayForm.FeedbackIndicator symbol="warning-full" />

										{Liferay.Language.get('warning-colon')}

										<span className="c-pl-1 font-weight-normal">
											{Liferay.Language.get(
												'search-framework-query-contributors-warning'
											)}
										</span>
									</ClayForm.FeedbackItem>
								</div>

								<ClayButton
									displayType="secondary"
									onClick={() =>
										onChangeClauseContributorsVisibility(
											true
										)
									}
									size="sm"
								>
									{Liferay.Language.get(
										'customize-contributors'
									)}
								</ClayButton>
							</>
						)}
					</ClayPanel.Body>
				</ClayPanel>
			</ClayPanel.Group>
		</CustomPanel>
	);
}
export default React.memo(Source);
