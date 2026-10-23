import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import MetadataTag from 'settings/recommendations/components/MetadataTag';
import React from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import RecommendationPageAssetsQuery from 'settings/recommendations/queries/RecommendationPageAssetsQuery';
import {
	createOrderIOMap,
	getSortFromOrderIOMap,
	TITLE,
} from 'shared/util/pagination';
import {
	EXCLUDE,
	Filter,
	getFilterValueBreakdown,
} from 'settings/recommendations/utils/utils';
import {getMapResultToProps} from 'shared/hoc/mappers/metrics';
import {MODAL_SIZE_XL} from 'shared/components/modals/sizes';
import {graphql} from '@apollo/client/react/hoc';
import {isArray, isString} from 'lodash';
import {omit} from 'lodash';
import {OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';
import {withBaseResults, withStatefulPagination} from 'shared/hoc';

type MatchingFilter = Pick<Filter, 'name' | 'value'>;

const withData = () =>
	graphql(RecommendationPageAssetsQuery, {
		options: ({
			delta,
			itemFilters,
			orderIOMap,
			page,
			query,
			useNegateValue,
		}: {
			delta: number;
			itemFilters: MatchingFilter[];
			orderIOMap: OrderedMap<string, OrderParams>;
			page: number;
			query: string;
			useNegateValue: boolean;
		}) => ({
			fetchPolicy: 'no-cache',
			variables: {
				keywords: query,
				propertyFilters: itemFilters.map(({name, value}) => ({
					filter: value,
					negate: useNegateValue ? name === EXCLUDE : false,
				})),
				size: delta,
				sort: getSortFromOrderIOMap(orderIOMap),
				start: (page - 1) * delta,
			},
		}),
		props: getMapResultToProps(({pageAssets: {pageAssets, total}}) => ({
			items: pageAssets,
			total,
		})),
	});

const TableWithData = withStatefulPagination(
	withBaseResults(withData, {
		emptyDescription: Liferay.Language.get(
			'there-were-no-matching-items-for-this-string'
		),
		emptyTitle: Liferay.Language.get('no-matches-found'),
		getColumns: ({secondColumnHeader}: {secondColumnHeader?: string}) => [
			{
				accessor: 'title',
				className: 'table-cell-expand text-truncate',
				label: Liferay.Language.get('page-name'),
			},
			{
				accessor: secondColumnHeader || 'url',
				className: 'table-cell-expand text-secondary text-truncate',
				dataFormatter: (val: unknown) => {
					if (isString(val)) {
						return val;
					}
					else if (isArray(val)) {
						return val
							.map(({value}: {value: string}) => value)
							.join(', ');
					}
				},
				label: secondColumnHeader || 'url',
				sortable: false,
			},
		],
		showDropdownRangeKey: false,
	}),
	{
		initialDelta: 10,
		initialOrderIOMap: createOrderIOMap(TITLE),
	},
	(props: {[key: string]: any}) => omit(props, 'onSearchValueChange'),
	false
);

interface IMatchingPagesModalProps {
	itemFilters: MatchingFilter[];
	observer: Observer;
	onClose: () => void;
	useNegateValue?: boolean;
}

const MatchingPagesModal: React.FC<IMatchingPagesModalProps> = ({
	itemFilters,
	observer,
	onClose,
	useNegateValue = false,
}) => {
	const {name, value} = itemFilters[0];

	const {exactMatchSign, metadataTag, rule} = getFilterValueBreakdown(value);

	const customFilter = itemFilters.length === 1 && metadataTag;

	return (
		<ClayModal observer={observer} size={MODAL_SIZE_XL}>
			<ClayModal.Header>
				{Liferay.Language.get('matching-pages')}
			</ClayModal.Header>

			<ClayModal.Body>
				{!!customFilter && !useNegateValue && (
					<div>
						<span className="font-weight-semi-bold mr-2 text-secondary">
							{`${
								name === EXCLUDE
									? Liferay.Language.get('exclude')
									: Liferay.Language.get('include')
							}:`}
						</span>

						<MetadataTag value={metadataTag} />

						<span className="ml-2 text-secondary">
							{exactMatchSign ? `"${rule}"` : rule}
						</span>
					</div>
				)}
			</ClayModal.Body>

			<TableWithData
				itemFilters={itemFilters}
				secondColumnHeader={customFilter}
				useNegateValue={useNegateValue}
			/>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							className="button-root"
							displayType="primary"
							onClick={onClose}
						>
							{Liferay.Language.get('done')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default MatchingPagesModal;
