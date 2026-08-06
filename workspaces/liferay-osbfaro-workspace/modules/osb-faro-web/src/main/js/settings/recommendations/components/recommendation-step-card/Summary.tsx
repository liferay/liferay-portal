import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import Constants from 'shared/util/constants';
import Form from 'shared/components/form';
import getCN from 'classnames';
import InfoPopover from 'shared/components/InfoPopover';
import React, {useEffect, useState} from 'react';
import RecommendationActivitiesQuery from '../../queries/RecommendationActivitiesQuery';
import RecommendationPageAssetsQuery from '../../queries/RecommendationPageAssetsQuery';
import {
	Filter,
	getPropertiesFromItems,
	JOB_RUN_DATA_PERIODS_LABEL_MAP,
	JOB_RUN_DATA_PERIODS_RANGE_KEY_MAP,
	JOB_RUN_FREQUENCIES_LABEL_MAP,
	JOB_TYPES_LABEL_MAP,
	JobProperty,
} from '../../utils/utils';
import {get} from 'lodash';
import {
	JobRunDataPeriods,
	JobRunFrequencies,
	JobRunStatuses,
	JobTypes,
} from 'shared/util/constants';
import {toLocale} from 'shared/util/numbers';
import {useQuery} from '@apollo/client';

const {
	pagination: {orderDescending},
} = Constants;

interface ISummaryProps {
	currentStep: number;
	includePreviousPeriod: boolean;
	itemFilters: Filter[];
	jobRunStatus: JobRunStatuses;
	name: string;
	runDataPeriod: JobRunDataPeriods;
	runFrequency: JobRunFrequencies;
	setFieldValue: (
		field: string,
		value: any,
		shouldValidate?: boolean
	) => void;
	setStep: (step: number) => void;
	type: JobTypes;
}

const EVENTS_THRESHOLD: number = 1000;

const Summary: React.FC<ISummaryProps> = ({
	currentStep,
	includePreviousPeriod,
	itemFilters,
	jobRunStatus,
	name,
	runDataPeriod,
	runFrequency,
	setFieldValue,
	setStep,
	type,
}) => {
	const [disabled, setDisabled] = useState(false);

	const propertyFilters: JobProperty[] = getPropertiesFromItems(itemFilters);

	const {data: pageAssetsData} = useQuery(RecommendationPageAssetsQuery, {
		variables: {
			propertyFilters,
			size: 0,
			sort: {
				column: 'title',
				type: orderDescending.toUpperCase(),
			},
			start: 0,
		},
	});

	const {data: activitiesData} = useQuery(RecommendationActivitiesQuery, {
		variables: {
			applicationId: 'Page',
			eventContextPropertyFilters: propertyFilters,
			eventId: 'pageUnloaded',
			rangeKey: JOB_RUN_DATA_PERIODS_RANGE_KEY_MAP[runDataPeriod],
			size: 0,
			start: 0,
		},
	});

	const {data: activitiesDataWithPrevious} = useQuery(
		RecommendationActivitiesQuery,
		{
			variables: {
				applicationId: 'Page',
				eventContextPropertyFilters: propertyFilters,
				eventId: 'pageUnloaded',
				rangeKey:
					Number(JOB_RUN_DATA_PERIODS_RANGE_KEY_MAP[runDataPeriod]) *
					2,
				size: 0,
				start: 0,
			},
		}
	);

	const activitiesTotal: number = get(
		activitiesData,
		['activities', 'total'],
		0
	);

	const activitiesWithPreviousTotal: number = get(
		activitiesDataWithPrevious,
		['activities', 'total'],
		0
	);

	const notEnoughActivities: boolean = activitiesTotal < EVENTS_THRESHOLD;
	const notEnoughActivitiesWithPrevious: boolean =
		activitiesWithPreviousTotal < EVENTS_THRESHOLD;

	useEffect(() => {
		if (
			jobRunStatus === JobRunStatuses.Running ||
			(activitiesData && !includePreviousPeriod && notEnoughActivities) ||
			(activitiesDataWithPrevious &&
				includePreviousPeriod &&
				notEnoughActivitiesWithPrevious)
		) {
			setDisabled(true);

			setFieldValue('runNow', false);
		}
		else {
			setDisabled(false);

			setFieldValue('runNow', true);
		}
	}, [activitiesDataWithPrevious, activitiesData]);

	const render2StepsBackButton = (
		children: React.ReactNode
	): React.ReactNode => (
		<ClayButton
			className="button-root"
			displayType="unstyled"
			onClick={
				notEnoughActivitiesWithPrevious
					? () => setStep(currentStep - 2)
					: undefined
			}
		>
			{children}
		</ClayButton>
	);

	return (
		<div className="summary-root">
			<div className="title">{Liferay.Language.get('summary')}</div>

			<table className="summary-table table table-autofit table-nowrap table-row-no-bordered">
				<tbody>
					<tr>
						<td className="summary-name table-cell-expand">
							{Liferay.Language.get('name')}
						</td>

						<td className="summary-value">{name}</td>
					</tr>

					<tr>
						<td className="summary-name table-cell-expand">
							{Liferay.Language.get('model-type')}
						</td>

						<td className="summary-value">
							{JOB_TYPES_LABEL_MAP[type]}
						</td>
					</tr>

					<tr>
						<td className="summary-name table-cell-expand">
							{Liferay.Language.get('training-frequency')}
						</td>

						<td className="summary-value">
							{JOB_RUN_FREQUENCIES_LABEL_MAP[runFrequency]}
						</td>
					</tr>

					<tr>
						<td className="summary-name table-cell-expand">
							{Liferay.Language.get('training-period')}
						</td>

						<td className="summary-value">
							{JOB_RUN_DATA_PERIODS_LABEL_MAP[runDataPeriod]}
						</td>
					</tr>

					<tr
						className={getCN({
							'insufficient-events': notEnoughActivities,
						})}
					>
						<td className="summary-name table-cell-expand">
							{notEnoughActivities && (
								<ClayIcon
									className="icon-root"
									symbol="warning-full"
								/>
							)}

							{render2StepsBackButton(
								`${Liferay.Language.get(
									'events'
								)} (${Liferay.Language.get('as-of-today')})`
							)}
						</td>

						<td className="summary-value">
							{render2StepsBackButton(toLocale(activitiesTotal))}
						</td>
					</tr>

					{includePreviousPeriod && (
						<tr
							className={getCN({
								'insufficient-events':
									notEnoughActivitiesWithPrevious,
							})}
						>
							<td className="summary-name table-cell-expand including-previous-period">
								{notEnoughActivitiesWithPrevious && (
									<ClayIcon
										className="icon-root"
										symbol="warning-full"
									/>
								)}

								{render2StepsBackButton(
									`${Liferay.Language.get(
										'events'
									)} (${Liferay.Language.get(
										'including-previous-period'
									)})`
								)}
							</td>

							<td className="summary-value">
								{render2StepsBackButton(
									toLocale(activitiesWithPreviousTotal)
								)}
							</td>
						</tr>
					)}

					<tr>
						<td className="summary-name table-cell-expand">
							{`${Liferay.Language.get(
								'items'
							)} (${Liferay.Language.get('as-of-today')})`}
						</td>

						<td className="summary-value">
							{toLocale(
								get(pageAssetsData, ['pageAssets', 'total'], 0)
							)}
						</td>
					</tr>
				</tbody>
			</table>

			<Form.Group>
				<Form.GroupItem>
					<Form.Checkbox
						data-testid="auto-start-training-checkbox"
						disabled={disabled}
						displayInline
						label={Liferay.Language.get(
							'automatically-start-training'
						)}
						name="runNow"
					/>

					<InfoPopover
						className="auto-start-training-help-icon"
						content={Liferay.Language.get(
							'start-training-at-a-later-date-by-deselecting-this-option'
						)}
					/>
				</Form.GroupItem>
			</Form.Group>
		</div>
	);
};

export default Summary;
