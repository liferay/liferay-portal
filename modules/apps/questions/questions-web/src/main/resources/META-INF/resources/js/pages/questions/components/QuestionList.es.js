/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import React from 'react';

import Alert from '../../../components/Alert.es';
import PaginatedList from '../../../components/PaginatedList.es';
import QuestionRow from '../../../components/QuestionRow.es';
import lang from '../../../utils/lang.es';
import {getFullPath} from '../../../utils/utils.es';

const QuestionList = ({
	buildParams,
	changePage,
	context,
	creatorId,
	error,
	historyPushParser,
	loading,
	navigateToNewQuestion,
	page,
	pageSize,
	questions,
	search,
	section,
	sectionTitle,
	urlParams = {},
}) => {
	if (section) {
		return (
			<div className="c-mx-auto c-px-0 col-xl-10 pt-2">
				<PaginatedList
					activeDelta={pageSize}
					activePage={page}
					changeDelta={(pageSize) =>
						changePage({...urlParams, page, pageSize, search})
					}
					data={questions}
					emptyState={
						sectionTitle && !search ? (
							<ClayEmptyState
								description={Liferay.Language.get(
									'there-are-no-questions-inside-this-topic-be-the-first-to-ask-something'
								)}
								imgSrc={
									context.includeContextPath +
									'/assets/empty_questions_list.png'
								}
								title={Liferay.Language.get(
									'this-topic-is-empty'
								)}
							>
								{((context.redirectToLogin &&
									!themeDisplay.isSignedIn()) ||
									context.canCreateThread) && (
									<ClayButton
										displayType="primary"
										onClick={navigateToNewQuestion}
									>
										{Liferay.Language.get('ask-question')}
									</ClayButton>
								)}
							</ClayEmptyState>
						) : (
							<ClayEmptyState
								description={Liferay.Language.get(
									'sorry,-no-results-were-found'
								)}
								title={Liferay.Language.get(
									'there-are-no-results'
								)}
							/>
						)
					}
					hrefConstructor={(page) =>
						`${getFullPath('questions')}${
							context.historyRouterBasePath ? '' : '#/'
						}questions/${sectionTitle}?${buildParams({
							...urlParams,
							page,
						})}`
					}
					loading={loading}
					totalCount={questions.totalCount}
				>
					{(question) => (
						<QuestionRow
							context={context}
							creatorId={creatorId}
							currentSection={sectionTitle}
							key={question.id}
							question={question}
							showSectionLabel={
								!!section.numberOfMessageBoardSections
							}
						/>
					)}
				</PaginatedList>

				<ClayButton
					aria-label={Liferay.Language.get('ask-question')}
					className="btn-monospaced d-block d-sm-none position-fixed questions-button shadow"
					displayType="primary"
					onClick={navigateToNewQuestion}
				>
					<ClayIcon symbol="pencil" />

					<span className="sr-only">
						{Liferay.Language.get('ask-question')}
					</span>
				</ClayButton>

				<Alert info={error} />
			</div>
		);
	}

	return (
		<ClayEmptyState
			aria-label={Liferay.Language.get('this-topic-is-empty')}
			className="c-mx-auto c-px-0 col-xl-10"
			description={lang.sub(
				Liferay.Language.get(
					'the-link-you-followed-may-be-broken-or-the-topic-no-longer-exists'
				),
				[sectionTitle]
			)}
			imgSrc={
				context.includeContextPath + '/assets/empty_questions_list.png'
			}
			title={Liferay.Language.get('this-topic-is-empty')}
		>
			<ClayButton
				displayType="primary"
				onClick={() => historyPushParser('/questions')}
			>
				{Liferay.Language.get('home')}
			</ClayButton>
		</ClayEmptyState>
	);
};

export default QuestionList;
