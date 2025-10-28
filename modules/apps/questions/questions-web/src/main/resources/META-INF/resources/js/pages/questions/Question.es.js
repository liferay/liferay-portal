/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayTabs from '@clayui/tabs';
import ClayUpperToolbar from '@clayui/upper-toolbar';
import {FlagsModal} from '@liferay/flags-taglib';
import classNames from 'classnames';
import {useMutation} from 'graphql-hooks';
import React, {
	useCallback,
	useContext,
	useEffect,
	useRef,
	useState,
} from 'react';
import {Helmet} from 'react-helmet';
import {withRouter} from 'react-router-dom';

import {AppContext} from '../../AppContext.es';
import Alert from '../../components/Alert.es';
import Answer from '../../components/Answer.es';
import ArticleBodyRenderer from '../../components/ArticleBodyRenderer.es';
import Breadcrumb from '../../components/Breadcrumb.es';
import CreatorRow from '../../components/CreatorRow.es';
import DefaultQuestionsEditor from '../../components/DefaultQuestionsEditor.es';
import DeleteQuestion from '../../components/DeleteQuestion.es';
import EditedTimestamp from '../../components/EditedTimestamp.es';
import Link from '../../components/Link.es';
import PaginatedList from '../../components/PaginatedList.es';
import Rating from '../../components/Rating.es';
import SectionLabel from '../../components/SectionLabel.es';
import SubscritionCheckbox from '../../components/SubscribeCheckbox.es';
import TagList from '../../components/TagList.es';
import {
	createAnswerQuery,
	getMessages,
	getSubscriptionsQuery,
	getThread,
	getUserActivityQuery,
	subscribeQuery,
	unMarkAsAnswerMessageBoardMessageQuery,
} from '../../utils/client.es';
import {ALL_SECTIONS_ID} from '../../utils/contants.es';
import lang from '../../utils/lang.es';
import {
	deleteCacheKey,
	getContextLink,
	getErrorObject,
	getFullPath,
	historyPushWithSlug,
	processGraphQLError,
} from '../../utils/utils.es';
import useActiviyQuestionKebabOptions from './hooks/useActivityQuestionKebabOptions.es';
import useFlagsContainer from './hooks/useFlagsContainer.es';

const tabs = [
	{label: Liferay.Language.get('newest'), sortBy: 'dateCreated:desc'},
	{label: Liferay.Language.get('oldest'), sortBy: 'dateCreated:asc'},
];

const Question = ({
	display = {
		actions: true,
		activity: false,
		addAnswer: true,
		breadcrumb: true,
		flags: true,
		rating: true,
		showAnswer: true,
		showSignature: false,
		styled: false,
	},
	history,
	questionId,
	sectionTitle,
}) => {
	const sectionRef = useRef(null);

	const runScroll = () =>
		sectionRef.current.scrollIntoView({
			behavior: 'smooth',
			block: 'start',
		});

	const [activeIndex, setActiveIndex] = useState(0);

	const [allowSubscription, setAllowSubscription] = useState(false);
	const [answers, setAnswers] = useState({});
	const [createAnswer] = useMutation(createAnswerQuery);
	const [error, setError] = useState(null);
	const [isModerate, setIsModerate] = useState(false);
	const [isPageScroll, setIsPageScroll] = useState(false);
	const [isPostButtonDisabled, setIsPostButtonDisabled] = useState(true);

	const [isVisibleEditor, setIsVisibleEditor] = useState(false);
	const [loading, setLoading] = useState(true);
	const [loadingAnswer, setLoadingAnswer] = useState(true);
	const [page, setPage] = useState(1);
	const [pageSize, setPageSize] = useState(20);
	const [question, setQuestion] = useState({});
	const [showDeleteModalPanel, setShowDeleteModalPanel] = useState(false);
	const [subscribe] = useMutation(subscribeQuery);
	const context = useContext(AppContext);
	const editorRef = useRef('');
	const historyPushParser = historyPushWithSlug(history.push);

	const flagsContainerProps = useFlagsContainer({
		content: question,
		context,
		questionId: question.id,
		showIcon: false,
	});

	const {kebabOptions, setIsSubscribe} = useActiviyQuestionKebabOptions({
		activityPage: display.activity,
		context,
		onClickReport: () => flagsContainerProps.handleClickShow(),
		question,
		questionId,
		sectionTitle,
		setError,
		setShowDeleteModalPanel,
	});

	const fetchMessages = useCallback(() => {
		const sortBy = tabs[activeIndex].sortBy;

		if (question && question.id) {
			getMessages(question.id, page, pageSize, sortBy).then(
				({data: {messageBoardThreadMessageBoardMessages}}) => {
					setAnswers(messageBoardThreadMessageBoardMessages);
					setLoadingAnswer(false);
				}
			);

			setIsSubscribe(question.subscribed);
		}
	}, [activeIndex, question, page, pageSize, setIsSubscribe]);

	useEffect(() => {
		if (!questionId) {
			return;
		}

		getThread(questionId, context.siteKey)
			.then(({data: {messageBoardThreadByFriendlyUrlPath}, error}) => {
				if (error) {
					const errorObject = getErrorObject(
						error.graphQLErrors[0].extensions.exception.errno,
						Liferay.Language.get(
							'the-link-you-followed-may-be-broken-or-the-question-no-longer-exists'
						),
						Liferay.Language.get('the-question-is-not-found')
					);

					setError(errorObject);

					setLoading(false);
				}
				else {
					setQuestion(messageBoardThreadByFriendlyUrlPath);
					setLoading(false);
				}
			})
			.catch((error) => {
				if (process.env.NODE_ENV === 'development') {
					console.error(error);
				}
				const errorObject = getErrorObject(
					error.graphQLErrors[0].extensions.exception.errno,
					'the-link-you-followed-may-be-broken-or-the-question-no-longer-exists',
					'the-question-is-not-found'
				);
				setError(errorObject);
				setLoading(false);
			});
	}, [questionId, context.siteKey]);

	sectionTitle =
		sectionTitle || sectionTitle === ALL_SECTIONS_ID
			? sectionTitle
			: question.messageBoardSection &&
				question.messageBoardSection.title;

	useEffect(() => {
		document.title = (question && question.title) || questionId;
	}, [question, questionId]);

	useEffect(() => {
		fetchMessages();
	}, [activeIndex, fetchMessages]);

	const questionVisited = context?.questionsVisited?.includes(question.id);

	useEffect(() => {
		setIsSubscribe(question.subscribed);
	}, [question.subscribed, setIsSubscribe]);

	useEffect(() => {
		if (question.id && context?.questionsVisited && !questionVisited) {
			context.setQuestionsVisited([
				...context.questionsVisited,
				question.id,
			]);
		}
	}, [context, question, questionVisited]);

	const onSubscription = useCallback(
		async ({
			allowSubscription: _allowSubscription = allowSubscription,
		} = {}) => {
			if (question.subscribed || !_allowSubscription) {
				return;
			}

			await subscribe({
				variables: {
					messageBoardThreadId: question.id,
				},
			});

			deleteCacheKey(getSubscriptionsQuery, {
				contentType: 'MessageBoardThread',
			});

			setQuestion({...question, subscribed: true});
		},
		[allowSubscription, question, subscribe, setQuestion]
	);

	const onShowEditorAnswer = () => {
		const threadAnswers = answers.items || [];

		const hasUserAnswered = threadAnswers.some(
			(answer) => answer.creator.id === Number(context.userId)
		);
		if (!context.trustedUser && hasUserAnswered) {
			setIsModerate(true);
		}

		return setIsVisibleEditor(true);
	};

	const onCreateAnswer = async () => {
		setIsPostButtonDisabled(true);

		try {
			const {error} = await createAnswer({
				fetchOptionsOverrides: getContextLink(
					`${sectionTitle}/${questionId}`
				),
				variables: {
					articleBody: editorRef.current.getContent(),
					messageBoardThreadId: question.id,
				},
			});

			if (error) {
				setIsPostButtonDisabled(false);

				return processGraphQLError(error);
			}

			editorRef.current.clearContent();

			await onSubscription();

			fetchMessages();

			deleteCacheKey(getUserActivityQuery, {
				filter: `creatorId eq ${context.userId}`,
				page: 1,
				pageSize: 20,
				siteKey: context.siteKey,
			});

			setIsVisibleEditor(false);
		}
		catch (error) {
			processGraphQLError(error);
		}

		setIsPostButtonDisabled(false);
	};

	const deleteAnswer = useCallback(
		(answer) => {
			setAnswers({
				...answers,
				items: [
					...answers.items?.filter(
						(otherAnswer) => answer.id !== otherAnswer.id
					),
				],
				totalCount: answers.totalCount - 1,
			});
		},
		[answers]
	);

	const [unMarkAsAnswerMessageBoardMessage] = useMutation(
		unMarkAsAnswerMessageBoardMessageQuery
	);

	const answerChange = useCallback(
		(answerId) => {
			const answer = answers.items?.find(
				(answer) => answer.showAsAnswer && answer.id !== answerId
			);

			if (answer) {
				unMarkAsAnswerMessageBoardMessage({
					variables: {
						messageBoardMessageId: answer.id,
					},
				}).then(() => {
					fetchMessages();
				});
			}
		},
		[unMarkAsAnswerMessageBoardMessage, answers.items, fetchMessages]
	);

	useEffect(() => {
		const body = document.body;
		const html = document.documentElement;

		const docHeight = Math.max(
			body.scrollHeight,
			body.offsetHeight,
			html.clientHeight,
			html.scrollHeight,
			html.offsetHeight
		);

		const winHeight = window.innerHeight;
		setIsPageScroll(docHeight > winHeight);
	}, [question, answers]);

	return (
		<section
			className={classNames('', {
				'c-mt-2': display.styled,
				'questions-section questions-section-single': !display.styled,
			})}
		>
			{display.breadcrumb && (
				<Breadcrumb
					section={
						question.messageBoardSection || context.rootTopicId
					}
				/>
			)}

			{error && (
				<div className="questions-container row">
					<div className="c-mx-auto c-px-0 col-xl-10">
						<ClayEmptyState
							description={error.message}
							imgSrc={
								context.includeContextPath +
								'/assets/empty_questions_list.png'
							}
							title={error.title}
						>
							<ClayButton
								aria-label={Liferay.Language.get('home')}
								displayType="primary"
								onClick={() => historyPushParser('/questions')}
							>
								{Liferay.Language.get('home')}
							</ClayButton>
						</ClayEmptyState>
					</div>
				</div>
			)}

			{isModerate && (
				<ClayAlert.ToastContainer>
					<ClayAlert
						autoClose={6000}
						closeButtonAriaLabel={Liferay.Language.get('close')}
						displayType="warning"
						onClose={() => setIsModerate(false)}
						title={Liferay.Language.get(
							'are-you-sure-you-want-to-add-another-answer'
						)}
						variant="inline"
					>
						<div>
							{Liferay.Language.get(
								'you-can-add-a-comment-to-continue-the-existing-thread'
							)}
						</div>

						<ClayAlert.Footer>
							<ClayButton.Group className="ml-0">
								<ClayButton
									alert
									aria-label={Liferay.Language.get('cancel')}
									onClick={() => setIsModerate(false)}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>
							</ClayButton.Group>
						</ClayAlert.Footer>
					</ClayAlert>
				</ClayAlert.ToastContainer>
			)}

			<div
				className={classNames({
					'c-mt-2 px-3': display.styled,
					'c-mt-5': !display.styled,
				})}
			>
				{!loading && !error && (
					<div className="questions-container row">
						{display.actions && display.rating && (
							<div className="col-md-1 text-md-center">
								<Rating
									aggregateRating={question.aggregateRating}
									disabled={!!question.locked}
									entityId={question.id}
									myRating={
										question.myRating &&
										question.myRating.ratingValue
									}
									type="Thread"
								/>
							</div>
						)}

						<div
							className={classNames({
								'col-md-10': !display.styled,
								'col-md-12 ': display.styled,
							})}
						>
							<div
								className={classNames({
									'align-items-top flex-column-reverse flex-md-row justify-content-between':
										display.styled,
									'align-items-top flex-column-reverse flex-md-row row':
										!display.styled,
								})}
							>
								<div
									className={classNames({
										'c-mt-2 c-mt-md-0': display.styled,
										'c-mt-4 c-mt-md-0 w-100':
											!display.styled,
									})}
								>
									{!!question.messageBoardSection &&
										!!question.messageBoardSection
											.numberOfMessageBoardSections && (
											<Link
												to={`/questions/${
													context.useTopicNamesInURL
														? sectionTitle
														: question
																.messageBoardSection
																.id
												}`}
											>
												<SectionLabel
													section={
														question.messageBoardSection
													}
												/>
											</Link>
										)}

									<div className="d-flex flex-row justify-content-between">
										<h1
											className={classNames(
												'c-mt-2 text-6 question-headline text-weight-bold',
												{
													'question-seen':
														question.seen,
												}
											)}
										>
											{question.headline}

											{question.status &&
												question.status !==
													'approved' && (
													<span className="c-ml-2">
														<ClayLabel displayType="info">
															{question.status}
														</ClayLabel>
													</span>
												)}

											{!!question.locked && (
												<span className="c-ml-2">
													<ClayIcon symbol="lock" />
												</span>
											)}
										</h1>

										<div className="d-flex mt-2">
											<ClayUpperToolbar.Item>
												<ClayDropDownWithItems
													items={kebabOptions}
													trigger={
														<ClayButtonWithIcon
															displayType="unstyled"
															small
															symbol="ellipsis-v"
														/>
													}
												/>
											</ClayUpperToolbar.Item>
										</div>
									</div>

									<div className="align-items-start d-flex justify-content-start small text-secondary">
										<EditedTimestamp
											dateCreated={question.dateCreated}
											dateModified={question.dateModified}
											operationText={Liferay.Language.get(
												'asked'
											)}
										/>

										{`/ ${lang.sub(
											Liferay.Language.get(
												'viewed-x-times'
											),
											[question.viewCount]
										)} `}

										{question.dateCreated !==
											question.dateModified && (
											<span className="question-edited">
												(
												{Liferay.Language.get('edited')}
												)
											</span>
										)}
									</div>
								</div>
							</div>

							<div className="c-mt-4">
								<div>
									<ArticleBodyRenderer {...question} />
								</div>
							</div>

							<div className="c-mt-4" ref={sectionRef}>
								<TagList
									sectionTitle={sectionTitle}
									tags={question.keywords}
								/>
							</div>

							<div className="c-mt-4 position-relative questions-creator text-center text-md-right">
								<CreatorRow
									answers={answers}
									isContentReviewer={
										context.isContentReviewer
									}
									question={question}
								/>
							</div>

							<h3 className="c-mt-4 text-secondary">
								{loadingAnswer
									? `${Liferay.Language.get(
											'loading-answers'
										)}`
									: `${
											answers.totalCount
										} ${Liferay.Language.get('answers')}`}
							</h3>

							<ClayTabs
								active={activeIndex}
								className="mb-2"
								modern
								onActiveChange={setActiveIndex}
							>
								{tabs.map((tab, index) => (
									<ClayTabs.Item
										active={activeIndex === index}
										innerProps={{
											'aria-controls': `tabpanel-${index}`,
										}}
										key={index}
										onClick={() => setActiveIndex(index)}
									>
										{tab.label}
									</ClayTabs.Item>
								))}
							</ClayTabs>

							<ClayTabs.Content activeIndex={activeIndex} fade>
								<div
									className={classNames({
										'c-mt-3 font-weight-normal':
											display.styled,
									})}
								>
									<PaginatedList
										activeDelta={pageSize}
										activePage={page}
										changeDelta={setPageSize}
										changePage={setPage}
										data={answers}
									>
										{(answer) => (
											<Answer
												answer={answer}
												answerChange={answerChange}
												answers={answers}
												canMarkAsAnswer={
													!question.locked &&
													!!question.actions.replace
												}
												context={context}
												deleteAnswer={deleteAnswer}
												display={display}
												editable={!question.locked}
												key={answer.id}
												onSubscription={onSubscription}
												question={question}
												showItems={display.showAnswer}
												showSignature={
													display.showSignature
												}
												styledItems={display.styled}
											/>
										)}
									</PaginatedList>
								</div>

								{display.actions &&
									question &&
									question.status !== 'pending' &&
									question.actions &&
									question.actions['reply-to-thread'] && (
										<div className="c-mt-5">
											{isVisibleEditor && (
												<>
													<DefaultQuestionsEditor
														label={Liferay.Language.get(
															'your-answer'
														)}
														onContentLengthValid={
															setIsPostButtonDisabled
														}
														question={question}
														ref={editorRef}
													/>

													<SubscritionCheckbox
														checked={
															allowSubscription
														}
														setChecked={
															setAllowSubscription
														}
													/>
												</>
											)}

											{display.addAnswer &&
												!isVisibleEditor && (
													<ClayButton
														displayType="primary"
														onClick={
															onShowEditorAnswer
														}
													>
														{Liferay.Language.get(
															'add-answer'
														)}
													</ClayButton>
												)}

											{!question.locked &&
												isVisibleEditor && (
													<ClayButton
														disabled={
															isPostButtonDisabled
														}
														displayType="primary"
														onClick={onCreateAnswer}
													>
														{context.trustedUser
															? Liferay.Language.get(
																	'post-answer'
																)
															: Liferay.Language.get(
																	'submit-for-workflow'
																)}
													</ClayButton>
												)}
										</div>
									)}
							</ClayTabs.Content>
						</div>
					</div>
				)}

				<Alert info={error} />
			</div>

			{flagsContainerProps.flagsModal.reportDialogOpen && (
				<FlagsModal
					handleClose={flagsContainerProps.flagsModal.onClose}
					handleSubmit={flagsContainerProps.handleSubmitReport}
					{...flagsContainerProps.flagsModal}
				/>
			)}

			{answers?.totalCount > 0 && isPageScroll && !display?.preview && (
				<div className="scroll-to-element">
					<ClayButtonWithIcon
						displayType="secondary"
						fontSize={22}
						onClick={runScroll}
						symbol="angle-down"
						title={Liferay.Language.get('go-to-answers')}
					/>
				</div>
			)}

			{question && (
				<Helmet>
					<title>{question.headline}</title>

					<link
						href={`${getFullPath(
							context.historyRouterBasePath || 'questions'
						)}${
							context.historyRouterBasePath
								? context.historyRouterBasePath
								: '#'
						}/questions/${sectionTitle}/${questionId}`}
						rel="canonical"
					/>
				</Helmet>
			)}

			<DeleteQuestion
				deleteModalVisibility={showDeleteModalPanel}
				question={question}
				setDeleteModalVisibility={setShowDeleteModalPanel}
			/>
		</section>
	);
};

export default withRouter(
	({
		history,
		match: {
			params: {questionId, sectionTitle},
			url,
		},
	}) => (
		<Question
			history={history}
			questionId={questionId}
			sectionTitle={sectionTitle}
			url={url}
		/>
	)
);

export {Question};
