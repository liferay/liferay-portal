import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import EventAnalysisEditor from '../components/event-analysis-editor';
import EventAnalysisToolbar from '../components/EventAnalysisToolbar';
import Form from 'shared/components/form';
import NavigationWarning from 'shared/components/NavigationWarning';
import React, {useContext, useMemo, useState} from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert, RangeSelectors} from 'shared/types';
import {AttributesContext} from '../components/event-analysis-editor/context/attributes';
import {
	Breakdowns,
	CalculationTypes,
	Event,
	Filters,
} from 'event-analysis/utils/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose, withRangeKey} from 'shared/hoc';
import {connect, ConnectedProps} from 'react-redux';
import {
	CreateEventAnalysisMutation,
	EventAnalysisMutationData,
	EventAnalysisMutationVariables,
	UpdateEventAnalysisMutation,
} from 'event-analysis/queries/EventAnalysisQuery';
import {getSafeRangeSelectors} from 'shared/util/util';
import {hasChanges} from 'shared/util/react';
import {omit} from 'lodash';
import {Routes, toRoute} from 'shared/util/router';
import {useChannelContext} from 'shared/context/channel';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useParams} from 'react-router-dom';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useMutation} from '@apollo/client';
import {WithRangeKeyProps} from 'shared/hoc/WithRangeKey';

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

const connector = connect(null, {
	addAlert,
	close,
	open,
});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IBaseEventAnalysisPageProps
	extends WithRangeKeyProps,
		PropsFromRedux,
		React.HTMLAttributes<HTMLElement> {
	breakdowns?: Breakdowns;
	compareToPrevious?: boolean;
	event?: Event | null;
	filters?: Filters;
	name?: string;
}

const BaseEventAnalysisPage: React.FC<IBaseEventAnalysisPageProps> = ({
	addAlert,
	close,
	compareToPrevious: initialCompareToPrevious = false,
	event: initialEvent = null as Event | null,
	name: initialName = '',
	open,
	rangeSelectors: initialRangeSelectors,
}) => {
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
	} = useContext(AttributesContext);

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
		open(
			modalTypes.LOADING_MODAL,
			{
				message: Liferay.Language.get('this-will-only-take-a-moment'),
				title: eventAnalysisId
					? Liferay.Language.get('creating')
					: Liferay.Language.get('updating'),
			},
			{closeOnBlur: false}
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

				close();

				history.push(
					toRoute(Routes.EVENT_ANALYSIS, {
						channelId,
						groupId,
					})
				);

				addAlert({
					alertType: Alert.Types.Success,
					message: Liferay.Language.get(
						'the-analysis-was-saved-successfully'
					),
				});
			})
			.catch(
				({
					graphQLErrors,
				}: {
					graphQLErrors: {messageKey: MessageKeys}[];
				}) => {
					setSubmitting(false);
					setSubmitted(false);

					close();

					const {alertType, message} =
						ERRORS[graphQLErrors[0].messageKey];

					addAlert({
						alertType,
						message,
						timeout: false,
					});
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

	return (
		<BasePage
			className="create-event-analysis-root"
			documentTitle={Liferay.Language.get('event-analysis')}
		>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId,
						groupId,
						label: selectedChannel?.name ?? '',
					}),
					breadcrumbs.getEventAnalysis({channelId, groupId}),
				]}
				groupId={groupId}
			>
				<BasePage.Header.TitleSection
					title={Liferay.Language.get('event-analysis')}
				/>
			</BasePage.Header>

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

					return (
						<Form.Form onSubmit={handleSubmit}>
							<NavigationWarning
								when={!submitted && hasChanges && !isSubmitting}
							/>

							<BasePage.SubHeader>
								<EventAnalysisToolbar
									isValid={
										!!name &&
										!!event?.id &&
										hasChanges &&
										!isSubmitting
									}
								/>
							</BasePage.SubHeader>
						</Form.Form>
					);
				}}
			</Form>

			<BasePage.Body>
				<EventAnalysisEditor
					channelId={channelId}
					compareToPrevious={compareToPrevious}
					event={event!}
					onCompareToPreviousChange={onCompareToPreviousChange}
					onEventChange={onEventChange}
					onRangeSelectorsChange={onRangeSelectorsChange}
					onTypeChange={onTypeChange}
					rangeSelectors={rangeSelectors!}
					type={type}
				/>
			</BasePage.Body>
		</BasePage>
	);
};

export default compose<any>(connector, withRangeKey)(BaseEventAnalysisPage);
