import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import Constants from 'shared/util/constants';
import Loading from 'shared/components/Loading';
import React, {useEffect} from 'react';
import RecommendationPageAssetsQuery from '../../queries/RecommendationPageAssetsQuery';
import RuleItem from '../RuleItem';
import Table from 'shared/components/table';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect} from 'react-redux';
import {FieldArray} from 'formik';
import {Filter, getPropertiesFromItems} from '../../utils/utils';
import {get} from 'lodash';
import {Modal} from 'shared/types';
import {toLocale} from 'shared/util/numbers';
import {useLazyQuery, useQuery} from '@apollo/client';

const {
	pagination: {orderDescending},
} = Constants;

const CountCell = (params: {[key: string]: any}) => {
	const {
		className,
		close,
		data: {name, value},
		open,
	} = params as {
		className: string;
		close: Modal.close;
		data: Filter;
		open: Modal.open;
	};
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
					open(modalTypes.MATCHING_PAGES_MODAL, {
						itemFilters: [{name, value}],
						onClose: close,
					});
				}}
			>
				{toLocale(get(data, ['pageAssets', 'total'], 0))}
			</ClayButton>
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
	close: Modal.close;
	groupId: string;
	itemFilters: Filter[];
	open: Modal.open;
}

const Items: React.FC<IItemsProps> = ({close, groupId, itemFilters, open}) => {
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
						open(modalTypes.MATCHING_PAGES_MODAL, {
							itemFilters,
							onClose: close,
							useNegateValue: true,
						});
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

								open(modalTypes.NEW_RULE_MODAL, {
									groupId,
									onClose: close,
									onSubmit: (filter: Filter) => {
										if (
											!itemFilters.find(
												(item) => item.id === filter.id
											)
										) {
											arrayHelpers.push(filter);
										}

										close();
									},
								});
							}}
						>
							{Liferay.Language.get('new-rule')}
						</ClayButton>

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
											cellRendererProps: {close, open},
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
		</div>
	);
};

export default connect(null, {close, open})(Items);
