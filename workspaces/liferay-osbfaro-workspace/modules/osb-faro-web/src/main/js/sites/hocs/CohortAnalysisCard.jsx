import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import CohortAnalysis from 'sites/components/cohort-analysis';
import CohortQuery from 'shared/queries/CohortQuery';
import Form from 'shared/components/form';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useContext, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {compose} from 'shared/hoc';
import {
	DAY,
	INTERVAL_OPTIONS,
	VISITORS,
	VISITORS_TYPE_OPTIONS
} from 'sites/components/cohort-analysis/utils';
import {graphql} from '@apollo/client/react/hoc';
import {mapPropsToOptions, mapResultToProps} from './mappers/cohort-query';
import {Option, Picker} from '@clayui/core';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {withError, withLoading} from 'shared/hoc/util';

const withEmpty =
	Component =>
	({empty, ...otherProps}) => {
		if (empty) {
			return (
				<NoResultsDisplay
					description={
						<>
							<span className='mr-1'>
								{Liferay.Language.get(
									'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
								)}
							</span>

							<ClayLink
								href={URLConstants.SitesDashboardCohortAnalysis}
								key='DOCUMENTATION'
								target='_blank'
							>
								{Liferay.Language.get(
									'learn-more-about-cohort-analysis'
								)}
							</ClayLink>
						</>
					}
					title={Liferay.Language.get(
						'there-are-no-sessions-on-the-selected-period'
					)}
				/>
			);
		}

		return <Component {...otherProps} />;
	};

const CohortAnalysisWithData = compose(
	graphql(CohortQuery, {
		options: mapPropsToOptions,
		props: mapResultToProps
	}),
	withError({page: false}),
	withEmpty,
	withLoading()
)(CohortAnalysis);

const CohortAnalysisCard = () => {
	const {accountId, router} = useContext(BasePage.Context);

	const [interval, setInterval] = useState(DAY);
	const [visitorsType, setVisitorsType] = useState(VISITORS);

	const {
		params: {channelId}
	} = router;

	return (
		<Card
			className='cohort-analysis-card-root'
			reportContainer={ReportContainer.CohortAnalysisCard}
		>
			<Card.Header>
				<Card.Title>
					{Liferay.Language.get('cohort-analysis')}
				</Card.Title>
			</Card.Header>

			<Card.Body>
				<Form.Group autoFit>
					<Form.GroupItem shrink>
						<Picker
							className='visitors-type-select'
							items={VISITORS_TYPE_OPTIONS}
							onSelectionChange={setVisitorsType}
							selectedKey={visitorsType}
						>
							{({label, value}) => (
								<Option key={value}>{label}</Option>
							)}
						</Picker>
					</Form.GroupItem>

					<Form.GroupItem label shrink>
						{Liferay.Language.get('broken-down-by')}
					</Form.GroupItem>

					<Form.GroupItem shrink>
						<Picker
							className='interval-select'
							items={INTERVAL_OPTIONS}
							onSelectionChange={setInterval}
							selectedKey={interval}
						>
							{({label, value}) => (
								<Option key={value}>{label}</Option>
							)}
						</Picker>
					</Form.GroupItem>
				</Form.Group>

				<CohortAnalysisWithData
					accountId={accountId}
					channelId={channelId}
					interval={interval}
					visitorsType={visitorsType}
				/>
			</Card.Body>
		</Card>
	);
};

export default CohortAnalysisCard;
