import AcquisitionsQuery, {
	AcquisitionsQueryData,
	AcquisitionsQueryVariables,
} from 'shared/queries/AcquisitionsQuery';
import BaseCard from 'shared/components/base-card';
import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import CardTabs from 'shared/components/CardTabs';
import ClayLink from '@clayui/link';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import React, {useContext, useState} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import Table from 'shared/components/table';
import URLConstants from 'shared/util/url-constants';
import {ACQUISITION_LABEL_MAP} from 'shared/util/lang';
import {AcquisitionTypes, CompositionTypes} from 'shared/util/constants';
import {ApolloError, useQuery} from '@apollo/client';
import {Column} from 'shared/components/table/Row';

import {compositionListColumns} from 'shared/util/table-columns';
import {getSafeRangeSelectors} from 'shared/util/util';
import {RangeSelectors} from 'shared/types';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';

const ROW_IDENTIFIER = 'name';

const {Channel, Referrer, SourceMedium} = AcquisitionTypes;

const getColumnsFn = (acquisitionType: AcquisitionTypes) => {
	const label =
		ACQUISITION_LABEL_MAP[
			acquisitionType as keyof typeof ACQUISITION_LABEL_MAP
		];

	return ({maxCount, totalCount}: {maxCount: number; totalCount: number}) => [
		compositionListColumns.getName({
			label,
			maxWidth: 200,
			sortable: true,
			tooltip: true,
		}),
		compositionListColumns.getRelativeMetricBar({
			label: Liferay.Language.get('sessions'),
			maxCount,
			totalCount,
		}),
		compositionListColumns.getPercentOf({
			metricName: Liferay.Language.get('sessions'),
			totalCount,
		}),
	];
};

const tabs = [
	{
		getColumns: getColumnsFn(Channel),
		rowIdentifier: ROW_IDENTIFIER,
		tabId: Channel,
		title: Liferay.Language.get('channels'),
	},
	{
		getColumns: getColumnsFn(SourceMedium),
		rowIdentifier: ROW_IDENTIFIER,
		tabId: SourceMedium,
		title: Liferay.Language.get('source-medium'),
	},
	{
		getColumns: getColumnsFn(Referrer),
		rowIdentifier: ROW_IDENTIFIER,
		tabId: Referrer,
		title: Liferay.Language.get('referrers'),
	},
];

interface IAcquisitionsCardProps extends React.HTMLAttributes<HTMLElement> {
	compositionBagName: CompositionTypes;
	label: string;
	legacyDropdownRangeKey?: boolean;
}

const AcquisitionsCard: React.FC<IAcquisitionsCardProps> = ({
	className,
	compositionBagName,
	label,
	legacyDropdownRangeKey,
}) => (
	<BaseCard
		className={className}
		label={label}
		legacyDropdownRangeKey={legacyDropdownRangeKey ?? true}
		reportContainer={ReportContainer.AcquisitionsCard}
	>
		{({rangeSelectors}) => (
			<AcquisitionsCardWithData
				compositionBagName={compositionBagName}
				rangeSelectors={rangeSelectors}
			/>
		)}
	</BaseCard>
);

interface IAcquisitionsCard extends Partial<IAcquisitionsCardProps> {
	rangeSelectors: RangeSelectors;
}

const AcquisitionsCardWithData: React.FC<IAcquisitionsCard> = ({
	compositionBagName,
	rangeSelectors,
}) => {
	const [activeTabId, setActiveTabId] = useState<string>(tabs[0].tabId);
	const {
		accountId,
		router: {
			params: {channelId},
		},
	} = useContext(BasePage.Context);
	const {data, error, loading} = useQuery<
		AcquisitionsQueryData,
		AcquisitionsQueryVariables
	>(AcquisitionsQuery, {
		variables: {
			...getSafeRangeSelectors(rangeSelectors),
			accountId,
			activeTabId,
			channelId,
			size: 5,
			start: 0,
		},
	});

	const activeTab = tabs.find(({tabId}) => tabId === activeTabId) ?? tabs[0];
	const {getColumns, rowIdentifier} = activeTab;

	const {
		compositions = [],
		maxCount = 0,
		total = 0,
		totalCount = 0,
	} = (compositionBagName &&
		(data as Record<string, any>)?.[compositionBagName]) ||
	{};

	return (
		<Card.Body className="w-100 d-flex flex-column flex-grow-1" noPadding>
			<CardTabs
				activeTabId={activeTabId}
				onChange={(tabId) => setActiveTabId(tabId)}
				tabs={tabs.map(({tabId, title}) => ({tabId, title}))}
			/>

			<AcquisitionsCardWithStatesRenderer
				empty={!total}
				error={error}
				loading={loading}
			>
				<Table
					className="flex-grow-1 table-hover"
					columns={
						getColumns({
							items: compositions,
							maxCount,
							total,
							totalCount,
						} as any) as Column[]
					}
					items={compositions}
					rowIdentifier={rowIdentifier}
				/>
			</AcquisitionsCardWithStatesRenderer>
		</Card.Body>
	);
};

interface IAcquisitionsCardWithStatesRendererProps
	extends React.HTMLAttributes<HTMLElement> {
	empty?: boolean;
	error?: ApolloError;
	loading?: boolean;
}

const AcquisitionsCardWithStatesRenderer: React.FC<
	IAcquisitionsCardWithStatesRendererProps
> = ({children, empty, error, loading}) => (
	<StatesRenderer empty={empty} error={!!error} loading={loading}>
		<StatesRenderer.Loading />
		<StatesRenderer.Empty
			description={
				<>
					<span className="mr-1">
						{Liferay.Language.get(
							'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
						)}
					</span>

					<ClayLink
						href={URLConstants.SitesDashboardAcquisitions}
						key="DOCUMENTATION"
						target="_blank"
					>
						{Liferay.Language.get('learn-more-about-acquisitions')}
					</ClayLink>
				</>
			}
			showIcon={false}
			title={Liferay.Language.get(
				'there-are-no-sessions-on-the-selected-period'
			)}
		/>
		<StatesRenderer.Error apolloError={error}>
			<ErrorDisplay />
		</StatesRenderer.Error>
		<StatesRenderer.Success>{children}</StatesRenderer.Success>
	</StatesRenderer>
);

export default AcquisitionsCard;
