import * as API from 'shared/api';
import Card from 'shared/components/Card';
import Loading from 'shared/components/Loading';
import React, {useMemo} from 'react';
import Table from 'shared/components/table';
import {Text as ClayText} from '@clayui/core';
import {compose, withToolbar} from 'shared/hoc';
import {
	DateCell,
	PropertyCell,
	SourceCell,
} from 'shared/components/table/cell-components';
import {formatDateToTimeZone, getCustomDateFormat} from 'shared/util/date';
import {SectionHeader} from 'shared/components/SectionHeader';
import {useQueryParams} from 'shared/hooks/useQueryParams';
import {useRequest} from 'shared/hooks/useRequest';
import {withEmpty} from 'cerebro-shared/hocs/utils';
import {withError, withLoading} from 'shared/hoc/util';

/**
 * Details List Columns for CDP
 */
export const detailsListCDPColumns = {
	getDataSourceName: (groupId: string) => ({
		accessor: 'dataSourceName',
		cellRenderer: SourceCell,
		cellRendererProps: {groupId},
		label: Liferay.Language.get('data-source'),
		sortable: false,
	}),
	getDateModified: () => ({
		accessor: 'dateModified',
		cellRenderer: DateCell,
		cellRendererProps: {
			dateFormatter: (date: string | number) =>
				formatDateToTimeZone(date, getCustomDateFormat()),
			datePath: 'dateModified',
		},
		label: Liferay.Language.get('last-modified'),
		sortable: false,
	}),
	name: {
		accessor: 'name',
		cellRenderer: PropertyCell,
		className: 'table-cell-expand',
		label: `${Liferay.Language.get(
			'attribute-name'
		)} | ${Liferay.Language.get('value')}`,
		sortable: false,
	},
	sourceName: {
		accessor: 'sourceName',
		className: 'table-cell-expand-small',
		label: Liferay.Language.get('source-name'),
		sortable: false,
	},
};

const DetailsCard = ({
	className = '',
	description,
	loading,
	title,
	value,
}: {
	className?: string;
	description: string;
	loading: boolean;
	title: string;
	value: React.ReactNode;
}) => (
	<Card className={`w-100 ${className}`}>
		<Card.Header>
			<ClayText weight="semi-bold">{title}</ClayText>
		</Card.Header>
		<Card.Body className="d-flex flex-column">
			<span className="text-secondary">{description}</span>
			<h2 className="mt-2 text-secondary">
				{loading ? <Loading /> : value}
			</h2>
		</Card.Body>
	</Card>
);

const IndividualDetailsCDP = ({
	children: emptyState,
	groupId,
	individualId,
	showEmptyState,
}: {
	children?: React.ReactNode;
	groupId: string;
	individualId: string;
	showEmptyState: boolean;
}) => {
	const {query} = useQueryParams();

	const response = useRequest({
		dataSourceFn: API.individuals.fetchDetails,
		variables: {groupId, individualId},
	});

	const {data: individualDetails, loading} = response as {
		data: {
			custom: {
				[key: string]: {
					dataSourceId: string;
					name: string;
					sourceName: string;
					value: string;
				}[];
			};
			demographics: {
				[key: string]: {
					dataSourceId: string;
					name: string;
					sourceName: string;
					value: string;
				}[];
			};
		};
		loading: boolean;
	};

	const {filteredItems, sourcesCount, totalOriginal} = useMemo(() => {
		if (!individualDetails) {
			return {filteredItems: [], sourcesCount: 0, totalOriginal: 0};
		}

		const {custom, demographics} = individualDetails;

		const demographicsList = Object.values(demographics).reduce(
			(acc, currentArray) => acc.concat(currentArray),
			[]
		);
		const customList = Object.values(custom).reduce(
			(acc, currentArray) => acc.concat(currentArray),
			[]
		);

		const flatCustomList = customList.map((attr) => ({
			...attr,
			name: `custom-${attr.name}`,
			sourceName: `[${Liferay.Language.get('custom-field')}] ${
				attr.sourceName
			}`,
		}));

		const allFields = [...demographicsList, ...flatCustomList];

		const filtered = allFields.filter((item) => {
			if (!query) return true;
			const searchTerm = query.toLowerCase();

			return (
				(item.name && item.name.toLowerCase().includes(searchTerm)) ||
				(item.sourceName &&
					item.sourceName.toLowerCase().includes(searchTerm)) ||
				(item.value &&
					String(item.value).toLowerCase().includes(searchTerm))
			);
		});

		return {
			filteredItems: filtered,
			sourcesCount: new Set(
				allFields.map((e) => e.dataSourceId).filter(Boolean)
			).size,
			totalOriginal: allFields.length,
		};
	}, [individualDetails, query]);

	const listProps = {
		empty: !loading && filteredItems.length === 0,
		error: false,
		items: filteredItems,
		loading,
		query,
		total: filteredItems.length,
	};

	return (
		<div className="mt-4">
			<SectionHeader
				icon="fieldset"
				title={Liferay.Language.get('all-attributes')}
			/>

			{showEmptyState ? (
				emptyState
			) : (
				<>
					<div className="d-flex flex-row justify-content-between">
						<DetailsCard
							className="mr-2"
							description={Liferay.Language.get(
								'displays-the-total-count-of-data-sources-associated-with-this-profile'
							)}
							loading={loading}
							title={Liferay.Language.get('data-sources')}
							value={sourcesCount}
						/>

						<DetailsCard
							description={Liferay.Language.get(
								'displays-the-total-number-of-enriched-individual-attributes-for-this-profile'
							)}
							loading={loading}
							title={Liferay.Language.get(
								'individual-attributes'
							)}
							value={totalOriginal}
						/>
					</div>

					<Card pageDisplay>
						<ListComponent
							{...listProps}
							columns={[
								detailsListCDPColumns.name,
								detailsListCDPColumns.sourceName,
								detailsListCDPColumns.getDataSourceName(
									groupId
								),
								detailsListCDPColumns.getDateModified(),
							]}
							entityLabel={Liferay.Language.get('all-attributes')}
							rowIdentifier="name"
						/>
					</Card>
				</>
			)}
		</div>
	);
};

const ListComponent = compose(
	withToolbar({
		disableSearch: false,
		showDropdownRangeKey: false,
		showFilterAndOrder: false,
	}),
	withLoading({spacer: true}),
	withError({page: false}),
	withEmpty()
)(Table) as React.ComponentType<any>;

export default IndividualDetailsCDP;
