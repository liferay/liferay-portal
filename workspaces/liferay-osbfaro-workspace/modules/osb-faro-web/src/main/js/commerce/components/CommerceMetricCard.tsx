import BaseCard from 'shared/components/base-card';
import Card from 'shared/components/Card';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import React, {useState} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import TrendComponent from 'shared/components/Trend';
import {ApolloError, DocumentNode, useQuery} from '@apollo/client';

import {getIcon, getStatsColor} from 'shared/util/metrics';
import {getSafeRangeSelectors} from 'shared/util/util';
import {RangeSelectors, SafeRangeSelectors} from 'shared/types';
import {sub} from 'shared/util/lang';
import {formatPercent, toCurrency} from 'shared/util/numbers';
import {Trend} from 'commerce/utils/types';
import {useLocale} from 'shared/hooks/useLocale';
import {useParams} from 'react-router-dom';

import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

type Currency = {
	currencyCode: string;
	trend: Trend;
	value: string;
};

interface ICommerceMetricCardProps<TGraphQlData>
	extends React.HTMLAttributes<HTMLElement> {
	description: string;
	emptyTitle: string;
	label: string;
	mapper: (result: TGraphQlData) => Currency[];
	Query: DocumentNode;
}

interface ICommerceMetricCardWithStatesRendererProps
	extends React.HTMLAttributes<HTMLElement> {
	empty?: boolean;
	emptyTitle: string;
	error?: ApolloError;
	loading?: boolean;
}

interface TGraphQlVariables extends SafeRangeSelectors {
	channelId: string;
}

const CommerceCardWithStatesRenderer: React.FC<
	ICommerceMetricCardWithStatesRendererProps
> = ({children, empty = false, emptyTitle, error, loading = false}) => (
	<StatesRenderer empty={empty} error={!!error} loading={loading}>
		<StatesRenderer.Loading />
		<StatesRenderer.Empty
			description={Liferay.Language.get(
				'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
			)}
			showIcon={false}
			title={emptyTitle}
		/>
		<StatesRenderer.Error apolloError={error}>
			<ErrorDisplay />
		</StatesRenderer.Error>
		<StatesRenderer.Success>{children}</StatesRenderer.Success>
	</StatesRenderer>
);

function CommerceMetricCard<TGraphQlData>({
	Query,
	description,
	emptyTitle,
	label,
	mapper,
}: ICommerceMetricCardProps<TGraphQlData>): React.ReactElement {
	const {channelId} = useParams<{channelId: string}>();
	const initialRangeSelectors = useQueryRangeSelectors();
	const [rangeSelectors, setRangeSelectors] = useState<RangeSelectors>(
		initialRangeSelectors
	);
	const {data, error, loading} = useQuery<TGraphQlData, TGraphQlVariables>(
		Query,
		{
			fetchPolicy: 'network-only',
			variables: {
				channelId,
				...getSafeRangeSelectors(rangeSelectors),
			},
		}
	);
	const locale = useLocale();

	const result = data ? mapper(data) : [];

	const {currencyCode, trend, value} = getCurrency(result);

	return (
		<BaseCard
			className="commerce-card-root"
			label={label}
			legacyDropdownRangeKey={false}
			minHeight={298}
		>
			{({rangeSelectors}) => {
				setRangeSelectors(rangeSelectors);

				return (
					<Card.Body className="align-items-center justify-content-center">
						<CommerceCardWithStatesRenderer
							empty={!result?.length}
							emptyTitle={emptyTitle}
							error={error}
							loading={loading}
						>
							<h1 className="commerce-card-currency font-size-lg-3x font-weight-semibold mb-2">
								{toCurrency(
									parseFloat(value),
									currencyCode,
									locale
								)}
							</h1>

							<div className="d-flex align-items-center mb-2">
								<span className="font-size-sm-1x text-secondary">
									{sub(
										Liferay.Language.get('x-vs-previous'),
										[
											<TrendComponent
												className="d-inline"
												color={getStatsColor(
													trend.trendClassification
												)}
												icon={getIcon(trend.percentage)}
												key="TREND"
												label={formatPercent(
													Math.abs(trend.percentage)
												)}
											/>,
										],
										false
									)}
								</span>
							</div>

							<p className="font-size-sm-1x text-center">
								{description}
							</p>
						</CommerceCardWithStatesRenderer>
					</Card.Body>
				);
			}}
		</BaseCard>
	);
}

function getCurrency(currencies: Currency[]): Currency {
	const defaultCurrencyCode = 'USD';

	if (!currencies || !currencies.length) {
		return {
			currencyCode: defaultCurrencyCode,
			trend: {
				percentage: 0,
				trendClassification: 'NEUTRAL',
			},
			value: '0',
		};
	}

	return (
		currencies?.find(
			({currencyCode}) => currencyCode === defaultCurrencyCode
		) ?? currencies[0]
	);
}

export default CommerceMetricCard;
