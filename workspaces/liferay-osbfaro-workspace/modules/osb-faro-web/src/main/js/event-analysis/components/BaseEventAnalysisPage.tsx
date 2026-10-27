import BaseEditPage from 'shared/components/base-edit-page';
import ConditionsPanel from 'shared/components/conditions-panel/ConditionsPanel';
import DownloadPDFReport, {
	ReportContainer,
} from 'shared/components/download-report/DownloadPDFReport';
import EventAnalysisBuilder from '../components/event-analysis-editor/event-analysis-builder';
import EventAnalysisEditor from '../components/event-analysis-editor';
import Form from 'shared/components/form';
import NavigationWarning from 'shared/components/NavigationWarning';
import React, {useMemo, useState} from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert, RangeSelectors} from 'shared/types';
import {
	Breakdowns,
	CalculationTypes,
	Event,
	Filters,
} from 'event-analysis/utils/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {
	CreateEventAnalysisMutation,
	EventAnalysisMutationData,
	EventAnalysisMutationVariables,
	UpdateEventAnalysisMutation,
} from 'event-analysis/queries/EventAnalysisQuery';
import {DEFAULT_RANGE_SELECTORS} from 'shared/hooks/useQueryRangeSelectors';
import {getSafeRangeSelectors} from 'shared/util/util';
import {hasChanges} from 'shared/util/react';
import {omit} from 'lodash';
import {Routes, toRoute} from 'shared/util/router';
import {SortableChipTypes} from '../components/event-analysis-editor/event-analysis-builder/SortableChipTypes';
import {useAttributes} from '../components/event-analysis-editor/context/attributes';
import {useChannelContext} from 'shared/context/channel';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useDispatch} from 'react-redux';
import {useField} from 'formik';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useMutation} from '@apollo/client';
import {useParams} from 'react-router-dom';
import {useReportContainer} from 'shared/components/download-report/DownloadReportContext';

enum MessageKeys {
	NameCannotBeBlank = 'name-cannot-be-blank',
	NameIsAlreadyUsed = 'name-is-already-used',
}

const ERRORS = {
	[MessageKeys.NameCannotBeBlank]: {
		alertType: Alert.Types.Error,
		message: Liferay.Language.get('name-cannot-be-blank'),
	},
	[MessageKeys.NameIsAlreadyUsed]: {
		alertType: Alert.Types.Warning,
		message: Liferay.Language.get(
			'this-analysis-name-is-currently-in-use.-please-try-a-different-one'
		),
	},
};

const CONDITION_DRAG_TYPES = [
	SortableChipTypes.Breakdown,
	SortableChipTypes.Filter,
];

const FORM_ID = 'eventAnalysisForm';

interface IBaseEventAnalysisPageProps
	extends React.HTMLAttributes<HTMLElement> {
	breakdowns?: Breakdowns;
	compareToPrevious?: boolean;
	event?: Event | null;
	filters?: Filters;
	name?: string;
	rangeSelectors?: RangeSelectors;
}

const EventAnalysisTitle: React.FC = () => {
	const [{onBlur, onChange, value}] = useField<string>('name');

	return (
		<BaseEditPage.Title
			id="name"
			label={Liferay.Language.get('title')}
			name="name"
			onBlur={onBlur}
			onChange={onChange}
			placeholder={Liferay.Language.get('new-analysis')}
			required
			value={value}
		/>
	);
};

const BaseEventAnalysisPage: React.FC<IBaseEventAnalysisPageProps> = ({
	compareToPrevious: initialCompareToPrevious = false,
	event: initialEvent = null as Event | null,
	name: initialName = '',
	rangeSelectors: rangeSelectorsProp,
}) => {
	const dispatch = useDispatch();

	useReportContainer(ReportContainer.EventAnalysisPage);

	const dataSourceStates = useDataSources();

	const history = useHistoryAdapter();

	const {selectedChannel} = useChannelContext();

	const {
		channelId = '',
		groupId = '',
		id: eventAnalysisId,
	} = useParams<{channelId: string; groupId: string; id: string}>();

	const [compareToPrevious, setCompareToPrevious] = useState<boolean>(
		initialCompareToPrevious ?? false
	);
	const [event, setEvent] = useState<Event | null>(initialEvent);
	const [initialRangeSelectors] = useState<RangeSelectors>(() => ({
		...DEFAULT_RANGE_SELECTORS,
		...rangeSelectorsProp,
	}));
	const [rangeSelectors, setRangeSelectors] = useState<
		RangeSelectors | undefined
	>(initialRangeSelectors);
	const [submitted, setSubmitted] = useState<boolean>(false);
	const [type, setType] = useState<CalculationTypes>(CalculationTypes.Total);

	const currentUser = useCurrentUser();

	const {
		breakdownOrder,
		breakdowns,
		changed: attributesContextChanged,
		filterOrder,
		filters,
	} = useAttributes();

	const Mutation = eventAnalysisId
		? UpdateEventAnalysisMutation
		: CreateEventAnalysisMutation;

	const [saveEventAnalysis] = useMutation<
		EventAnalysisMutationData,
		EventAnalysisMutationVariables
	>(Mutation);

	const handleSubmit = (
		{name}: {name: string},
		{setSubmitting}: {setSubmitting: (submitting: boolean) => void}
	) => {
		dispatch(
			open(
				modalTypes.LOADING_MODAL,
				{
					message: Liferay.Language.get(
						'this-will-only-take-a-moment'
					),
					title: eventAnalysisId
						? Liferay.Language.get('updating')
						: Liferay.Language.get('creating'),
				},
				{closeOnBlur: false}
			)
		);

		saveEventAnalysis({
			variables: {
				analysisType: type,
				channelId,
				compareToPrevious,
				eventAnalysisBreakdowns: breakdownOrder.map((breakdownId) =>
					omit(breakdowns[breakdownId], 'id')
				),
				eventAnalysisFilters: filterOrder.map((filterId) =>
					omit(filters[filterId], 'id')
				),
				eventAnalysisId,
				eventDefinitionId: event!.id,
				name,
				userId: String(currentUser.userId),
				userName: currentUser.name,
				...getSafeRangeSelectors(rangeSelectors!),
			},
		})
			.then(() => {
				setSubmitting(false);
				setSubmitted(true);

				dispatch(close());

				history.push(
					toRoute(Routes.EVENT_ANALYSIS, {
						channelId,
						groupId,
					})
				);

				dispatch(
					addAlert({
						alertType: Alert.Types.Success,
						message: Liferay.Language.get(
							'the-analysis-was-saved-successfully'
						),
					})
				);
			})
			.catch(
				({
					graphQLErrors,
				}: {
					graphQLErrors: {messageKey: MessageKeys}[];
				}) => {
					setSubmitting(false);
					setSubmitted(false);

					dispatch(close());

					const {alertType, message} =
						ERRORS[graphQLErrors[0].messageKey];

					dispatch(
						addAlert({
							alertType,
							message,
							timeout: false,
						})
					);
				}
			);
	};

	const compareToPreviousChanged: boolean =
		initialCompareToPrevious !== compareToPrevious;

	const eventChanged: boolean = useMemo(
		() => hasChanges(initialEvent || {}, event || {}, 'id'),
		[initialEvent, event]
	);

	const rangeSelectorsChanged: boolean = useMemo(
		() =>
			hasChanges(
				(initialRangeSelectors ?? {}) as object,
				(rangeSelectors ?? {}) as object,
				'rangeStart',
				'rangeKey',
				'rangeEnd'
			),
		[initialRangeSelectors, rangeSelectors]
	);

	const onCompareToPreviousChange = (compareToPrevious: boolean) => {
		setCompareToPrevious(compareToPrevious);
	};

	const onEventChange = (event: Event | null) => {
		setEvent(event);
	};

	const onRangeSelectorsChange = (rangeSelectors: RangeSelectors) => {
		setRangeSelectors(rangeSelectors);
	};

	const onTypeChange = (type: CalculationTypes) => {
		setType(type);
	};

	// Built outside the form's render prop so typing the title does not re-render them

	const conditionsPanel = (
		<BaseEditPage.Sidebar
			label={Liferay.Language.get('conditions-library')}
			position="left"
		>
			<ConditionsPanel
				dragTypes={CONDITION_DRAG_TYPES}
				title={Liferay.Language.get('conditions-library')}
			>
				<EventAnalysisBuilder
					event={event ?? undefined}
					onEventChange={onEventChange}
				/>
			</ConditionsPanel>
		</BaseEditPage.Sidebar>
	);

	const editor = (
		<EventAnalysisEditor
			channelId={channelId}
			compareToPrevious={compareToPrevious}
			event={event}
			onCompareToPreviousChange={onCompareToPreviousChange}
			onRangeSelectorsChange={onRangeSelectorsChange}
			onTypeChange={onTypeChange}
			rangeSelectors={rangeSelectors!}
			type={type}
		/>
	);

	return (
		<BaseEditPage documentTitle={Liferay.Language.get('event-analysis')}>
			<div
				className="d-flex flex-column"
				id={ReportContainer.EventAnalysisPage}
			>
				<Form
					initialValues={{
						name: initialName,
					}}
					onSubmit={handleSubmit}
				>
					{({dirty, handleSubmit, isSubmitting, values: {name}}) => {
						const hasChanges =
							attributesContextChanged ||
							dirty ||
							compareToPreviousChanged ||
							eventChanged ||
							rangeSelectorsChanged;

						const listURL = toRoute(Routes.EVENT_ANALYSIS, {
							channelId,
							groupId,
						});

						return (
							<>
								<BaseEditPage.Toolbar
									backURL={listURL}
									title={
										eventAnalysisId
											? Liferay.Language.get(
													'edit-event-analysis'
												)
											: Liferay.Language.get(
													'new-event-analysis'
												)
									}
								>
									<BaseEditPage.Toolbar.Item>
										<DownloadPDFReport
											disabled={!!dataSourceStates.empty}
											infoMessage={Liferay.Language.get(
												'the-report-will-be-downloaded-exactly-as-it-is-displayed-on-your-screen.-please-verify-if-the-desired-tabs-and-filters-are-selected-before-proceeding'
											)}
											subtitle={selectedChannel?.name}
											title={Liferay.Language.get(
												'event-analysis-report'
											)}
										/>
									</BaseEditPage.Toolbar.Item>

									<BaseEditPage.Toolbar.Divider />

									<BaseEditPage.Toolbar.Cancel
										href={listURL}
									/>

									<BaseEditPage.Toolbar.Save
										disabled={
											!name ||
											!event?.id ||
											!hasChanges ||
											isSubmitting
										}
										form={FORM_ID}
										label={Liferay.Language.get(
											'save-analysis'
										)}
										type="submit"
									/>
								</BaseEditPage.Toolbar>

								<BaseEditPage.Body>
									{conditionsPanel}

									<BaseEditPage.Main className="page-container">
										<Form.Form
											id={FORM_ID}
											onSubmit={handleSubmit}
										>
											<NavigationWarning
												when={
													!submitted &&
													hasChanges &&
													!isSubmitting
												}
											/>

											<div className="mb-4">
												<EventAnalysisTitle />
											</div>
										</Form.Form>

										{editor}
									</BaseEditPage.Main>
								</BaseEditPage.Body>
							</>
						);
					}}
				</Form>
			</div>
		</BaseEditPage>
	);
};

export default BaseEventAnalysisPage;
