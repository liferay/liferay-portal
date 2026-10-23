import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import Constants from 'shared/util/constants';
import RadioGroup from 'shared/components/RadioGroup';
import React, {useEffect, useState} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import RecommendationPageAssetsQuery from 'settings/recommendations/queries/RecommendationPageAssetsQuery';
import StringMatchInput from 'settings/recommendations/components/StringMatchInput';
import Table from 'shared/components/table';
import {compose} from 'redux';
import {
	createOrderIOMap,
	getSortFromOrderIOMap,
	TITLE,
} from 'shared/util/pagination';
import {EXCLUDE, INCLUDE} from 'settings/recommendations/utils/utils';
import {isArray, isString} from 'lodash';
import {useLazyQuery} from '@apollo/client';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';
import {withEmpty, withPaginationBar} from 'shared/hoc';

const {
	pagination: {cur: defaultPage},
} = Constants;

interface INewRuleModalProps {
	observer: Observer;
	onClose: () => void;
	onSubmit: (value: {id: string; name: string; value: string}) => void;
}

const NewRuleModal: React.FC<INewRuleModalProps> = ({
	observer,
	onClose,
	onSubmit,
}) => {
	const {
		delta,
		onDeltaChange,
		onOrderIOMapChange,
		onPageChange,
		orderIOMap,
		page,
	} = useStatefulPagination(undefined, {
		initialOrderIOMap: createOrderIOMap(TITLE),
	});
	const [initialRender, setInitialRender] = useState(true);
	const [metadata, setMetadata] = useState('');
	const [stringMatch, setStringMatch] = useState('');

	// TODO: LRAC-6335 Re-enable Exact Match checkbox

	const [exactMatch] = useState(false);
	const [includeExclude, setIncludeExclude] = useState(INCLUDE);

	useEffect(() => {
		setInitialRender(false);
	}, []);

	const [getPageAssets, {data, loading}] = useLazyQuery(
		RecommendationPageAssetsQuery,
		{
			fetchPolicy: 'network-only',
		}
	);

	const filter: string = `${metadata} ${
		exactMatch ? '=' : '~'
	} ${stringMatch}`;

	const fetchPageAssets = (): void => {
		getPageAssets({
			variables: {
				propertyFilters: [
					{
						filter,
						negate: false,
					},
				],
				size: delta,
				sort: getSortFromOrderIOMap(orderIOMap),
				start: (page - 1) * delta,
			},
		});
	};

	useEffect(() => {
		if (!initialRender) {
			fetchPageAssets();
		}
	}, [delta, orderIOMap, page]);

	const handleFindMatches = (): void => {
		if (page !== defaultPage) {
			onPageChange(defaultPage);
		}
		else {
			fetchPageAssets();
		}
	};

	const TableWithPagination = compose<any>(
		withEmpty({spacer: true}),
		withPaginationBar()
	)(Table);

	return (
		<ClayModal observer={observer} size="lg">
			<ClayModal.Header>
				{Liferay.Language.get('new-rule')}
			</ClayModal.Header>

			<ClayModal.Body>
				<div>
					<RadioGroup
						checked={includeExclude}
						inline
						name="includeExclude"
						onChange={setIncludeExclude}
					>
						<RadioGroup.Option
							key={INCLUDE}
							label={Liferay.Language.get('include')}
							value={INCLUDE}
						/>

						<RadioGroup.Option
							key={EXCLUDE}
							label={Liferay.Language.get('exclude')}
							value={EXCLUDE}
						/>
					</RadioGroup>
				</div>

				<div>
					<div>
						<span className="font-weight-semi-bold mr-1">
							{Liferay.Language.get('string-match')}
						</span>

						<span>{`(${Liferay.Language.get('regex-only')})`}</span>

						<div className="d-flex">
							<StringMatchInput
								className="flex-grow-1"
								focusOnInit={!initialRender}
								metadata={metadata}
								onEnterClick={handleFindMatches}
								onMetadataChange={setMetadata}
								onStringMatchChange={setStringMatch}
								stringMatch={stringMatch}
							/>

							<div className="d-flex flex-column justify-content-center ml-3">
								<ClayButton
									className="button-root"
									disabled={!stringMatch || !metadata}
									displayType="secondary"
									onClick={handleFindMatches}
								>
									{Liferay.Language.get('find-matches')}
								</ClayButton>
							</div>
						</div>
					</div>

					{/* TODO: LRAC-6335 Re-enable Exact Match checkbox
						<div className='exact-match'>
						<Checkbox
							checked={exactMatch}
							displayInline
							label={Liferay.Language.get('exact-match')}
							name='exactMatch'
						onChange={event =>
							setExactMatch(event.target.checked)
							}
						/>

						<InfoPopover
							className='exact-match-help-icon'
						content={Liferay.Language.get(
							'use-exact-match-to-add-specific-urls'
							)}
						/>
						</div>
					*/}
				</div>

				{(loading || data) && (
					<div className="mt-4">
						<div className="font-weight-semi-bold">
							{Liferay.Language.get('matched-items')}
						</div>

						<div className="text-secondary">
							{Liferay.Language.get(
								'item-sets-can-vary-per-period-depending-on-interactions.-metadata-matches-pages-with-at-least-one-view-event'
							)}
						</div>

						<TableWithPagination
							columns={[
								{
									accessor: 'title',
									className:
										'table-cell-expand text-truncate',
									label: Liferay.Language.get('page-name'),
								},
								{
									accessor: metadata ? metadata : 'url',
									className:
										'table-cell-expand text-truncate',
									dataFormatter: (val: unknown) => {
										if (isString(val)) {
											return val;
										}
										else if (isArray(val)) {
											return val
												.map(
													({
														value,
													}: {
														value: string;
													}) => value
												)
												.join(', ');
										}
									},
									label: metadata ? metadata : 'url',
									sortable: false,
								},
							]}
							delta={delta}
							items={data ? data.pageAssets.pageAssets : []}
							loading={loading}
							onDeltaChange={onDeltaChange}
							onOrderIOMapChange={onOrderIOMapChange}
							onPageChange={onPageChange}
							orderIOMap={orderIOMap}
							page={page}
							total={data ? data.pageAssets.total : 0}
						/>
					</div>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							className="button-root"
							displayType="secondary"
							onClick={onClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							className="button-root"
							disabled={!stringMatch || !metadata}
							onClick={() =>
								onSubmit({
									id: `${includeExclude} - ${filter}`,
									name: includeExclude,
									value: filter,
								})
							}
						>
							{Liferay.Language.get('add-rule')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default NewRuleModal;
