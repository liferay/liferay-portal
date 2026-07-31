import BaseCard from 'shared/components/base-card';
import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import ChartTooltip, {
	Alignments,
	Weights,
} from 'shared/components/chart-tooltip';
import ClayLink from '@clayui/link';
import HeatmapChart from 'shared/components/HeatmapChart';
import PropTypes from 'prop-types';
import React, {useContext} from 'react';
import ReactDOMServer from 'react-dom/server';
import URLConstants from 'shared/util/url-constants';
import VisitorsByTimeQuery from 'shared/queries/VisitorsByTimeQuery';
import {compose} from 'shared/hoc';
import {graphql, OperationOption} from '@apollo/client/react/hoc';
import {IBasePageContext} from 'shared/types';
import {
	mapPropsToOptions,
	mapResultToProps,
} from './mappers/visitors-by-time-query';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {sub} from 'shared/util/lang';
import {withEmpty, withError, withLoading} from 'shared/hoc';

export const formatHour = (hour: string) => {
	const hourAsNumber = parseInt(hour);
	const suffix = hourAsNumber >= 12 ? 'PM' : 'AM';
	let hourDisplay = hourAsNumber;

	if (hourAsNumber === 0) {
		hourDisplay = 12;
	}
	else if (hourAsNumber > 12) {
		hourDisplay = hourAsNumber - 12;
	}

	return `${hourDisplay} ${suffix}`;
};

export const renderTooltip = ({
	column,
	row,
	value,
}: {
	column: string;
	row: string;
	value: number;
}) =>
	ReactDOMServer.renderToString(
		<ChartTooltip
			header={[
				{
					columns: [
						{
							label: `${column} - ${formatHour(row)}`,
							weight: Weights.Semibold,
						},
					],
				},
			]}
			rows={[
				{
					columns: [
						{
							align: Alignments.Center,
							label: sub(Liferay.Language.get('x-visitors'), [
								value.toLocaleString(),
							]) as string,
						},
					],
				},
			]}
		/>
	);

const HeatmapChartWithData = compose<any>(
	graphql(VisitorsByTimeQuery, {
		options: mapPropsToOptions,
		props: mapResultToProps,
	} as OperationOption<object, object>),
	withLoading(),
	withError({page: false}),
	withEmpty({
		description: (
			<>
				<span className="mr-1">
					{Liferay.Language.get(
						'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
					)}
				</span>

				<ClayLink
					href={URLConstants.SitesDashboardVisitorsByDayAndTime}
					key="DOCUMENTATION"
					target="_blank"
				>
					{Liferay.Language.get(
						'learn-more-about-visitors-by-day-and-time'
					)}
				</ClayLink>
			</>
		),
		title: Liferay.Language.get(
			'there-are-no-visitors-on-the-selected-period'
		),
	})
)(HeatmapChart);

interface IVisitorsByTimeCardProps extends React.HTMLAttributes<HTMLElement> {
	label: string;
	minHeight?: number;
}

const VisitorsByTimeCard: React.FC<IVisitorsByTimeCardProps> = ({
	className,
	label,
	minHeight,
}) => {
	const {accountId, router} = useContext(
		BasePage.Context as React.Context<IBasePageContext>
	);

	return (
		<BaseCard
			className={className}
			label={label}
			legacyDropdownRangeKey={false}
			minHeight={minHeight}
			reportContainer={ReportContainer.VisitorsByTimeCard}
		>
			{({rangeSelectors}) => (
				<Card.Body>
					<HeatmapChartWithData
						accountId={accountId}
						columnAxisFormatter={(col: string) => col.slice(0, 3)}
						rangeSelectors={rangeSelectors}
						renderTooltip={renderTooltip}
						router={router}
						rowAxisFormatter={formatHour}
					/>
				</Card.Body>
			)}
		</BaseCard>
	);
};

VisitorsByTimeCard.propTypes = {
	className: PropTypes.string,
	label: PropTypes.string.isRequired,
};

export default VisitorsByTimeCard;
