import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import Constants from 'shared/util/constants';
import Loading from 'shared/components/Loading';
import React, {useEffect} from 'react';
import MatchingPagesModal from 'shared/components/modals/MatchingPagesModal';
import NewRuleModal from 'shared/components/modals/NewRuleModal';
import RecommendationPageAssetsQuery from '../../queries/RecommendationPageAssetsQuery';
import RuleItem from '../RuleItem';
import Table from 'shared/components/table';
import {FieldArray} from 'formik';
import {Filter, getPropertiesFromItems} from '../../utils/utils';
import {get} from 'lodash';
import {toLocale} from 'shared/util/numbers';
import {useLazyQuery, useQuery} from '@apollo/client';
import {useModal} from '@clayui/modal';

const {
	pagination: {orderDescending},
} = Constants;

const CountCell = (params: {[key: string]: any}) => {
	const {
		className,
		data: {name, value},
	} = params as {
		className: string;
		data: Filter;
	};

	const matchingPagesModal = useModal();

	const {data, loading} = useQuery(RecommendationPageAssetsQuery, {
		variables: {
			propertyFilters: [
				{
					filter: value,
					negate: false,
				},
			],
			size: 0,
			sort: {
				column: 'title',
				type: orderDescending.toUpperCase(),
			},
			start: 0,
		},
	});

	if (loading) {
		return (
			<td className={className}>
				<Loading />
			</td>
		);
	}

	return (
		<td className={className}>
			<ClayButton
				className="button-root matching-pages-modal-button"
				displayType="unstyled"
				onClick={() => {
					matchingPagesModal.onOpenChange(true);
				}}
			>
				{toLocale(get(data, ['pageAssets', 'total'], 0))}
			</ClayButton>

			{matchingPagesModal.open && (
				<MatchingPagesModal
					itemFilters={[{name, value}]}
					observer={matchingPagesModal.observer}
					onClose={() => matchingPagesModal.onOpenChange(false)}
				/>
			)}
		</td>
	);
};

const RuleCell = (params: {[key: string]: any}) => {
	const {
		className,
		data: {name, value},
	} = params as {className: string; data: Filter};

	return (
		<td className={className}>
			<RuleItem name={name} value={value} />
		</td>
	);
};

interface IItemsProps {
	itemFilters: Filter[];
}

const Items: React.FC<IItemsProps> = ({itemFilters}) => {
	const newRuleModal = useModal();
	const totalPagesModal = useModal();

	const [getPageAssetsTotal, {data, loading: pagesTotalLoading}] =
		useLazyQuery(RecommendationPageAssetsQuery);

	useEffect(() => {
		getPageAssetsTotal({
			variables: {
				propertyFilters: getPropertiesFromItems(itemFilters),
				size: 0,
				sort: {
					column: 'title',
					type: orderDescending.toUpperCase(),
				},
				start: 0,
			},
		});
	}, [itemFilters]);

	const renderTotalPages = (): React.ReactNode => {
		if (pagesTotalLoading) {
			return (
				<div>
					<Loading />
				</div>
			);
		}

		return (
			<div>
				<ClayButton
					className="button-root matching-pages-modal-button"
					displayType="unstyled"
					onClick={() => {
						totalPagesModal.onOpenChange(true);
					}}
				>
					{toLocale(get(data, ['pageAssets', 'total'], 0))}
				</ClayButton>
			</div>
		);
	};

	return (
		<div className="items-root">
			<div className="title">{Liferay.Language.get('add-items')}</div>

			<div className="secondary-info">
				{Liferay.Language.get(
					'create-rules-to-match-your-urls-and-page-metadata.-if-you-dont-define-rules,-the-recommendation-model-will-use-all-urls-from-this-workspace-to-train-the-recommendations-model'
				)}
			</div>

			<FieldArray name="itemFilters">
				{(arrayHelpers) => (
					<>
						<ClayButton
							className="button-root new-rule-button"
							displayType="secondary"
							onClick={() => {

								// Maybe add a toast alert to inform the user that this already exists therefore it was not added

								newRuleModal.onOpenChange(true);
							}}
						>
							{Liferay.Language.get('new-rule')}
						</ClayButton>

						{newRuleModal.open && (
							<NewRuleModal
								observer={newRuleModal.observer}
								onClose={() => newRuleModal.onOpenChange(false)}
								onSubmit={(filter: Filter) => {
									if (
										!itemFilters.find(
											(item) => item.id === filter.id
										)
									) {
										arrayHelpers.push(filter);
									}

									newRuleModal.onOpenChange(false);
								}}
							/>
						)}

						{!!itemFilters.length && (
							<>
								<Table
									columns={[
										{
											accessor: 'name',
											cellRenderer: RuleCell,
											className:
												'rule-cell table-cell-expand',
											label: Liferay.Language.get('rule'),
											sortable: false,
										},
										{
											accessor: 'value',
											cellRenderer: CountCell,
											className:
												'count-cell table-column-text-end',
											label: Liferay.Language.get(
												'matching-items'
											),
											sortable: false,
										},
									]}
									items={itemFilters}
									renderInlineRowActions={({data, items}) => (
										<span>
											<ClayButton
												aria-label={Liferay.Language.get(
													'remove'
												)}
												borderless
												className="button-root"
												displayType="secondary"
												onClick={() => {
													arrayHelpers.remove(
														items.findIndex(
															(itemData: {
																[
																	key: string
																]: any;
															}) =>
																itemData.id ===
																data.id
														)
													);
												}}
												outline
											>
												<ClayIcon
													className="icon-root"
													symbol="times"
												/>
											</ClayButton>
										</span>
									)}
									rowIdentifier={['name', 'value']}
								/>

								<div className="total-included-pages d-flex justify-content-between">
									<div>
										{Liferay.Language.get(
											'total-included-pages'
										)}
									</div>

									{renderTotalPages()}
								</div>
							</>
						)}
					</>
				)}
			</FieldArray>

			{totalPagesModal.open && (
				<MatchingPagesModal
					itemFilters={itemFilters}
					observer={totalPagesModal.observer}
					onClose={() => totalPagesModal.onOpenChange(false)}
					useNegateValue
				/>
			)}
		</div>
	);
};

export default Items;
