/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import {useMutation, useQuery} from 'graphql-hooks';
import React, {useCallback, useContext, useState} from 'react';

import {AppContext} from '../../AppContext.es';
import Alert from '../../components/Alert.es';
import DeleteQuestion from '../../components/DeleteQuestion.es';
import Link from '../../components/Link.es';
import PaginatedList from '../../components/PaginatedList.es';
import QuestionRow from '../../components/QuestionRow.es';
import TagsLayout from '../../components/TagsLayout.es';
import useTags from '../../hooks/useTags.es';
import {withRouter} from '../../hooks/withRouter.es';
import {
	getSubscriptionsQuery,
	unsubscribeMyUserAccountQuery,
} from '../../utils/client.es';
import {historyPushWithSlug} from '../../utils/utils.es';

export default withRouter(({history, location, params}) => {
	const context = useContext(AppContext);

	const [info, setInfo] = useState({});
	const [questionToDelete, setQuestionToDelete] = useState({});

	const {changePage, error, loading, orderBy, page, pageSize, search, tags} =
		useTags({
			baseURL: `/questions/subscriptions/${params.creatorId}`,
			filter: 'subscribed eq true',
			history,
			location,
		});

	const {data: threads, refetch: refetchThread} = useQuery(
		getSubscriptionsQuery,
		{
			variables: {
				contentType: 'MessageBoardThread',
			},
		}
	);

	if (threads && threads.myUserAccountSubscriptions.items) {
		threads.myUserAccountSubscriptions.items =
			threads.myUserAccountSubscriptions.items.filter(
				(thread) => thread.graphQLNode.showAsQuestion
			);
	}

	const {data: topics, refetch: refetchTopics} = useQuery(
		getSubscriptionsQuery,
		{
			variables: {
				contentType: 'MessageBoardSection',
			},
		}
	);

	const [unsubscribe] = useMutation(unsubscribeMyUserAccountQuery);

	const [showDeleteModalPanel, setShowDeleteModalPanel] = useState(false);

	const historyPushParser = historyPushWithSlug(history.push);

	const linkTagPage = location.pathname.split('/')[2];

	const actions = useCallback(
		(data) => {
			const question = data.graphQLNode;

			const actions = [
				{
					label: Liferay.Language.get('unsubscribe'),
					onClick: () => {
						unsubscribe({
							variables: {
								subscriptionId: data.id,
							},
						}).then(() => {
							refetchThread();
							refetchTopics();
							setInfo({
								title: 'You have unsubscribed from this asset successfully.',
							});
						});
					},
				},
			];

			if (question.actions && question.actions.delete) {
				actions.push({
					label: Liferay.Language.get('delete'),
					onClick: () => {
						setQuestionToDelete(question);
						setShowDeleteModalPanel(true);
					},
				});
			}

			if (question.actions && question.actions.replace) {
				actions.push({
					label: Liferay.Language.get('edit'),
					onClick: () => {
						historyPushParser(
							`/questions/${
								context.useTopicNamesInURL
									? question.messageBoardSection.title
									: question.messageBoardSection.id
							}/${data.graphQLNode.friendlyUrlPath}/edit`
						);
					},
				});
			}

			if (question.headline) {
				actions.push({
					label: Liferay.Language.get('reply'),
					onClick: () => {
						historyPushParser(
							`/questions/${
								context.useTopicNamesInURL
									? question.messageBoardSection.title
									: question.messageBoardSection.id
							}/${question.friendlyUrlPath}`
						);
					},
				});
			}

			return actions;
		},
		[
			context.useTopicNamesInURL,
			historyPushParser,
			refetchThread,
			refetchTopics,
			unsubscribe,
		]
	);

	return (
		<section className="questions-section questions-section-list">
			<div className="c-p-5 questions-container row">
				<div className="c-mt-3 c-mx-auto c-px-0 w-100">
					<h2 className="sheet-subtitle">
						{Liferay.Language.get('tags')}
					</h2>

					<div className="c-mt-3 row">
						<PaginatedList
							activeDelta={pageSize}
							activePage={page}
							changeDelta={(pageSize) =>
								changePage(search, page, pageSize)
							}
							changePage={(page) =>
								changePage(search, page, pageSize)
							}
							data={tags}
							emptyState={
								<ClayEmptyState
									description={Liferay.Language.get(
										'sorry,-no-results-were-found'
									)}
									image={
										<ClayIcon
											className="c-empty-state-icon c-empty-state-icon-empty-state"
											illustration
											symbol="empty-state"
										/>
									}
									title={Liferay.Language.get(
										'there-are-no-results'
									)}
								/>
							}
							loading={loading}
						>
							{(tag) => (
								<div className="col-md-4" key={tag.id}>
									<TagsLayout
										context={context.siteKey}
										linkPage={linkTagPage}
										orderBy={orderBy}
										page={page}
										pageSize={pageSize}
										search={search}
										tag={tag}
									/>
								</div>
							)}
						</PaginatedList>
					</div>

					<Alert info={error} />

					<h2 className="mt-5 sheet-subtitle">
						{Liferay.Language.get('topics')}
					</h2>

					{topics &&
						topics.myUserAccountSubscriptions.items &&
						!topics.myUserAccountSubscriptions.items.length && (
							<ClayEmptyState
								description={Liferay.Language.get(
									'sorry,-no-results-were-found'
								)}
								title={Liferay.Language.get(
									'there-are-no-results'
								)}
							/>
						)}

					<div className="row">
						{topics &&
							topics.myUserAccountSubscriptions.items &&
							topics.myUserAccountSubscriptions.items.map(
								(data) => (
									<div
										className="col-md-4 question-tags"
										key={data.graphQLNode.id}
									>
										<div className="card card-interactive card-interactive-primary card-type-template template-card-horizontal">
											<div className="card-body">
												<div className="card-row">
													<div className="autofit-col autofit-col-expand">
														<Link
															title={
																data.graphQLNode
																	.title
															}
															to={`/questions/${
																context.useTopicNamesInURL
																	? data
																			.graphQLNode
																			.title
																	: data
																			.graphQLNode
																			.id
															}`}
														>
															<div className="autofit-section">
																<div className="card-title">
																	<span className="text-truncate">
																		{
																			data
																				.graphQLNode
																				.title
																		}
																	</span>
																</div>
															</div>
														</Link>
													</div>

													<div className="autofit-col">
														<ClayDropDownWithItems
															items={actions(
																data
															)}
															trigger={
																<ClayButtonWithIcon
																	displayType="unstyled"
																	small
																	symbol="ellipsis-v"
																/>
															}
														/>
													</div>
												</div>
											</div>
										</div>
									</div>
								)
							)}
					</div>

					<h2 className="mt-5 sheet-subtitle">
						{Liferay.Language.get('questions')}
					</h2>

					<div>
						{threads &&
							threads.myUserAccountSubscriptions.items &&
							!threads.myUserAccountSubscriptions.items
								.length && (
								<ClayEmptyState
									description={Liferay.Language.get(
										'sorry,-no-results-were-found'
									)}
									title={Liferay.Language.get(
										'there-are-no-results'
									)}
								/>
							)}

						{threads &&
							threads.myUserAccountSubscriptions.items &&
							threads.myUserAccountSubscriptions.items.map(
								(data) => (
									<div key={data.id}>
										<QuestionRow
											context={context}
											currentSection={
												context.useTopicNamesInURL
													? data.graphQLNode
															.messageBoardSection &&
														data.graphQLNode
															.messageBoardSection
															.title
													: (data.graphQLNode
															.messageBoardSection &&
															data.graphQLNode
																.messageBoardSection
																.id) ||
														context.rootTopicId
											}
											items={actions(data)}
											question={data.graphQLNode}
											showSectionLabel={true}
										/>
									</div>
								)
							)}

						<DeleteQuestion
							deleteModalVisibility={showDeleteModalPanel}
							question={questionToDelete}
							setDeleteModalVisibility={setShowDeleteModalPanel}
						/>
					</div>
				</div>
			</div>

			<Alert displayType="success" info={info} />
		</section>
	);
});
