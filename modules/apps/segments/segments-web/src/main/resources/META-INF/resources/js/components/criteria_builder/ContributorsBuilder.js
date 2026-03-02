/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import {
	FeatureIndicator,
	LearnMessage,
	LearnResourcesContext,
} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useMemo, useState} from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {contributorShape, propertyGroupShape} from '../../utils/types.es';
import {getPluralMessage} from '../../utils/utils';
import CriteriaSidebar from '../criteria_sidebar/CriteriaSidebar';
import Conjunction from './Conjunction.es';
import CriteriaBuilder from './CriteriaBuilder';
import EmptyPlaceholder from './EmptyPlaceholder.es';

export default function ContributorsBuilder({
	contributors = [],
	editing,
	emptyContributors,
	isSegmentationDisabledAlertDismissed,
	isSegmentationEnabled,
	learnResources,
	membersCount = 0,
	membersCountLoading = false,
	onAlertClose = () => {},
	onConjunctionChange = () => {},
	onPreviewMembers = () => {},
	onQueryChange = () => {},
	propertyGroups,
	renderEmptyValuesErrors = false,
}) {
	const propertyKey = useMemo(() => {
		const firstContributorNotEmpty = contributors.find(
			(contributor) => contributor.query !== ''
		);

		return firstContributorNotEmpty
			? firstContributorNotEmpty.propertyKey
			: propertyGroups[0].propertyKey;
	}, [contributors, propertyGroups]);

	const [editingId, setEditingId] = useState(propertyKey);

	const _handleCriteriaChange = (criteriaChange, index) => {
		onQueryChange(criteriaChange, index);
	};

	const _handleCriteriaEdit = (id, editing) => {
		setEditingId(editing ? undefined : id);
	};

	const showDisabledSegmentationAlert =
		!isSegmentationEnabled && !isSegmentationDisabledAlertDismissed;

	return (
		<DndProvider backend={HTML5Backend}>
			<div
				className={classNames('contributor-builder-root h-100', {
					editing,
				})}
			>
				<div
					className={classNames(
						'criteria-builder-section-sidebar overflow-hidden',
						{
							'criteria-builder-section-sidebar--with-warning':
								showDisabledSegmentationAlert,
						}
					)}
				>
					<CriteriaSidebar
						onTitleClicked={_handleCriteriaEdit}
						propertyGroups={propertyGroups}
						propertyKey={editingId}
					/>
				</div>

				<div
					className="c-pr-0 criteria-builder-section-main d-flex h-100 w-100"
					role="application"
				>
					<div className="contributor-container h-100 position-absolute w-100">
						{renderEmptyValuesErrors && (
							<section className="alert-danger criteria-builder-empty-errors-alert position-sticky top-0">
								<div className="c-pr-0 criteria-builder-empty-errors-alert__inner">
									<ClayAlert
										className="border-bottom-0"
										displayType="danger"
										onClose={onAlertClose}
										variant="stripe"
									>
										{Liferay.Language.get(
											'values-need-to-be-added-to-properties-for-the-segment-to-be-saved'
										)}
									</ClayAlert>
								</div>
							</section>
						)}

						<ClayLayout.ContainerFluid>
							<div className="c-p-4 content-wrapper">
								<ClayAlert
									displayType="warning"
									title={
										Liferay.Language.get('warning') + ':'
									}
								>
									{Liferay.Language.get(
										'segments-deprecation-warning-message'
									) + ' '}

									<LearnResourcesContext.Provider
										value={learnResources['segments-web']}
									>
										<LearnMessage
											resource="segments-web"
											resourceKey="analytics-cloud"
										/>
									</LearnResourcesContext.Provider>
								</ClayAlert>

								<ClayLayout.Sheet className="c-pb-4">
									<div className="c-mb-4 d-flex flex-wrap justify-content-between mb-4">
										<h2 className="c-mb-2 sheet-title">
											{Liferay.Language.get('conditions')}

											<span className="inline-item inline-item-after">
												<FeatureIndicator
													interactive={true}
													learnResourceContext={
														learnResources[
															'frontend-js-components-web'
														]
													}
													type="deprecated"
												/>
											</span>
										</h2>

										<div className="c-ml-2 criterion-string">
											<div className="btn-group">
												<div className="btn-group-item d-flex flex-wrap inline-item">
													{membersCountLoading && (
														<ClayLoadingIndicator
															className="c-mr-4"
															small
														/>
													)}

													{!membersCountLoading && (
														<span className="c-mr-4">
															{Liferay.Language.get(
																'conditions-match'
															)}

															<b className="c-ml-2 font-weight-bold text-dark">
																{getPluralMessage(
																	Liferay.Language.get(
																		'x-member'
																	),
																	Liferay.Language.get(
																		'x-members'
																	),
																	membersCount
																)}
															</b>
														</span>
													)}

													<ClayButton
														displayType="secondary"
														onClick={
															onPreviewMembers
														}
														small
														type="button"
													>
														{Liferay.Language.get(
															'view-members'
														)}
													</ClayButton>
												</div>
											</div>
										</div>
									</div>

									{emptyContributors &&
										(editingId === undefined ||
											!editing) && <EmptyPlaceholder />}

									{contributors
										.filter((criteria) => {
											const editingCriteria =
												editingId ===
													criteria.propertyKey &&
												editing;
											const emptyCriteriaQuery =
												criteria.query === '';

											return (
												editingCriteria ||
												!emptyCriteriaQuery
											);
										})
										.map((criteria, i) => {
											return (
												<React.Fragment key={i}>
													{i !== 0 && (
														<>
															<Conjunction
																className="c-mb-4 c-ml-0 c-mt-4"
																conjunctionName={
																	criteria.conjunctionId
																}
																editing={
																	editing
																}
																onSelect={
																	onConjunctionChange
																}
															/>
														</>
													)}

													<CriteriaBuilder
														criteria={
															criteria.criteriaMap
														}
														editing={editing}
														emptyContributors={
															emptyContributors
														}
														entityName={
															criteria.entityName
														}
														modelLabel={
															criteria.modelLabel
														}
														onChange={
															_handleCriteriaChange
														}
														propertyKey={
															criteria.propertyKey
														}
														renderEmptyValuesErrors={
															renderEmptyValuesErrors
														}
														supportedProperties={
															criteria.properties
														}
													/>
												</React.Fragment>
											);
										})}
								</ClayLayout.Sheet>
							</div>
						</ClayLayout.ContainerFluid>
					</div>
				</div>
			</div>
		</DndProvider>
	);
}

ContributorsBuilder.propTypes = {
	contributors: PropTypes.arrayOf(contributorShape),
	editing: PropTypes.bool.isRequired,
	emptyContributors: PropTypes.bool.isRequired,
	isSegmentationDisabledAlertDismissed: PropTypes.bool,
	isSegmentationEnabled: PropTypes.bool,
	membersCount: PropTypes.number,
	membersCountLoading: PropTypes.bool,
	onAlertClose: PropTypes.func,
	onConjunctionChange: PropTypes.func,
	onPreviewMembers: PropTypes.func,
	onQueryChange: PropTypes.func,
	previewMembersURL: PropTypes.string,
	propertyGroups: PropTypes.arrayOf(propertyGroupShape),
	renderEmptyValuesErrors: PropTypes.bool,
};
