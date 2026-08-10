import Card from 'shared/components/Card';
import classNames from 'classnames';
import ClayIcon from '@clayui/icon';
import Loading from 'shared/components/Loading';
import React, {ReactNode} from 'react';
import {formatPercent} from 'shared/util/numbers';
import {getIcon, getStatsColor} from 'shared/util/metrics';
import {isNil} from 'lodash';
import {Text} from '@clayui/core';
import {TrendClassification} from 'segment/types';
import {TREND_PLACEHOLDER} from '../util/constants';

interface IMetricCardTrend {
	percentage: number;
	trendClassification: TrendClassification;
}
interface IMetricCardProps {
	className?: string;
	description: string;
	loading?: boolean;
	minHeight?: number;
	renderTrendLabel?: (percentageNode: ReactNode) => ReactNode;
	title: string;
	trend?: IMetricCardTrend;
	trendClassName?: string;
	value: ReactNode;
}

const MetricCard: React.FC<IMetricCardProps> = ({
	className,
	description,
	loading = false,
	minHeight,
	renderTrendLabel,
	title,
	trend,
	trendClassName,
	value,
}) => {
	if (loading) {
		return (
			<Card
				className={classNames(className, 'flex-fill p-3 w-100')}
				minHeight={minHeight}
			>
				<Card.Body>
					<Loading />
				</Card.Body>
			</Card>
		);
	}

	const percentageColor = getStatsColor(trend?.trendClassification || '');

	return (
		<Card
			className={classNames(className, 'flex-fill p-3 w-100')}
			minHeight={minHeight}
		>
			<Card.Title>
				<div className="text-uppercase text-weight-semi-bold">
					<Text>{title}</Text>
				</div>
			</Card.Title>

			<Card.Body className="justify-content-between d-flex" noPadding>
				<div className="mt-2">
					<Text color="secondary" size={3}>
						{description}
					</Text>
				</div>

				<div>
					<div className="mt-2 text-lowercase text-weight-semi-bold">
						<Text size={7}>{value}</Text>
					</div>

					<div
						className={classNames('text-secondary', trendClassName)}
						data-testid="metric-card-trend"
					>
						{renderTrendLabel ? (
							<>
								{!isNil(trend?.trendClassification) &&
									trend?.trendClassification !==
										TrendClassification.Neutral && (
										<ClayIcon
											style={{color: percentageColor}}
											symbol={
												getIcon(
													trend?.percentage ?? 0
												) ?? ''
											}
										/>
									)}

								{renderTrendLabel(
									<span
										className="mr-1"
										key="percentage"
										style={{color: percentageColor}}
									>
										{formatPercent(
											Math.abs(trend?.percentage ?? 0),
											1
										)}
									</span>
								)}
							</>
						) : (
							TREND_PLACEHOLDER
						)}
					</div>
				</div>
			</Card.Body>
		</Card>
	);
};

export default MetricCard;
