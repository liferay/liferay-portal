import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import getCN from 'classnames';
import InfoPopover from 'shared/components/InfoPopover';
import React, {useId} from 'react';
import {ACCOUNT_NAME, createOrderIOMap, NAME} from 'shared/util/pagination';
import {ACCOUNTS, INDIVIDUALS} from 'shared/util/router';
import {
	accountsListColumns,
	individualsListColumns,
} from 'shared/util/table-columns';
import {ClayToggle} from '@clayui/form';
import {close, modalTypes, open} from 'shared/actions/modals';
import {Criteria} from './utils/types';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {toLocale} from 'shared/util/numbers';
import {useDispatch} from 'react-redux';
import {useField} from 'formik';
import {useRequest} from 'shared/hooks/useRequest';
import {validateSegmentInputs} from './utils/utils';

type MembersParams = {
	channelId: string;
	criteriaString: string;
	delta?: number;
	groupId: string;
	includeAnonymousUsers: boolean;
	orderIOMap?: any;
	page?: number;
	query?: string;
	segmentCategory: SegmentCategories;
};

export const fetchMembers = ({
	channelId,
	criteriaString,
	delta,
	groupId,
	includeAnonymousUsers,
	orderIOMap,
	page,
	query,
	segmentCategory,
}: MembersParams): Promise<{items: any[]; total: number}> => {
	if (segmentCategory === SegmentCategories.Account) {
		return API.accounts
			.searchByFilter({
				channelId,
				filter: criteriaString,
				groupId,
				includeAnonymousUsers,
				orderIOMap,
				page,
				pageSize: delta,
				query,
			})
			.then(
				({
					items,
					totalCount,
				}: {
					items: Array<Record<string, any>>;
					totalCount: number;
				}) => ({items, total: totalCount})
			);
	}

	return API.individuals.search({
		channelId,
		delta,
		filter: criteriaString,
		groupId,
		includeAnonymousUsers,
		orderIOMap,
		page,
		query,
	});
};

const IncludeAnonymousToggle: React.FC = () => {
	const [field, , {setValue}] = useField<boolean>('includeAnonymousUsers');

	const id = useId();

	return (
		<ClayToggle
			containerProps={{
				className: getCN('mb-0', {'text-secondary': !field.value}),
			}}
			id={id}
			label={Liferay.Language.get('include-anonymous')}
			name={field.name}
			onBlur={(event) => field.onBlur(event)}
			onToggle={setValue}
			toggled={!!field.value}
		/>
	);
};

interface ISegmentMembershipProps {
	channelId: string;
	criteria: Criteria;
	criteriaString: string;
	groupId: string;
	includeAnonymousUsers: boolean;
	segmentCategory: SegmentCategories;
	segmentType: SegmentTypes;
}

const SegmentMembership: React.FC<ISegmentMembershipProps> = ({
	channelId,
	criteria,
	criteriaString,
	groupId,
	includeAnonymousUsers,
	segmentCategory,
	segmentType,
}) => {
	const dispatch = useDispatch();

	const criteriaValid = !!criteria && validateSegmentInputs(criteria);

	const isAccountSegment = segmentCategory === SegmentCategories.Account;
	const isBatch = segmentType === SegmentTypes.Batch;

	const baseParams = {
		channelId,
		criteriaString,
		groupId,
		includeAnonymousUsers,
		segmentCategory,
	};

	const {data, loading} = useRequest<MembersParams, {total: number}>({
		dataSourceFn: fetchMembers,
		debounceDelay: 400,
		initialState: {data: null, error: false, loading: false},
		resetStateIfSkipingRequest: true,
		skipRequest: !criteriaValid,
		variables: {...baseParams, delta: 0},
	});

	const membersCount = criteriaValid ? data?.total ?? 0 : 0;

	const viewLabel = isAccountSegment
		? Liferay.Language.get('view-accounts')
		: Liferay.Language.get('view-members');

	const invalidCriteriaMessageId = useId();

	const handlePreviewClick = () =>
		dispatch(
			open(modalTypes.SEARCHABLE_ENTITIES_TABLE_MODAL, {
				...(isAccountSegment && {initialDelta: 20}),
				columns: isAccountSegment
					? [accountsListColumns.getAccountName({channelId, groupId})]
					: [
							{
								...individualsListColumns.name,
								className: 'w-50',
							},
							{
								...individualsListColumns.accountName,
								className: 'w-50',
								label: Liferay.Language.get('account-name'),
							},
						],
				dataSourceFn: (params: Partial<MembersParams>) =>
					fetchMembers({...baseParams, ...params}),
				entityLabel: isAccountSegment
					? Liferay.Language.get('accounts')
					: Liferay.Language.get('individuals'),
				entityType: isAccountSegment ? ACCOUNTS : INDIVIDUALS,
				initialOrderIOMap: createOrderIOMap(
					isAccountSegment ? ACCOUNT_NAME : NAME
				),
				onClose: () => dispatch(close()),
				rowIdentifier: 'id',
				size: 'lg',
				title: isAccountSegment
					? Liferay.Language.get('segment-accounts')
					: Liferay.Language.get('segment-membership'),
			})
		);

	return (
		<>
			{isBatch && (
				<>
					<div className="align-items-center c-gap-2 d-flex">
						<IncludeAnonymousToggle />

						<InfoPopover
							content={Liferay.Language.get(
								'criteria-containing-individual-or-account-attributes-excludes-anonymous-individuals'
							)}
						/>
					</div>

					<span className="align-self-stretch border-left" />

					<span aria-live="polite">
						<Text size={3} weight="semi-bold">
							{sub(
								isAccountSegment
									? Liferay.Language.get('total-accounts-x')
									: Liferay.Language.get('total-members-x'),
								[
									loading ? (
										<ClayLoadingIndicator
											className="d-inline-block"
											key="LOADING"
											size="sm"
										/>
									) : (
										<span key="TOTAL_MEMBERS_COUNT">
											{toLocale(membersCount)}
										</span>
									),
								],
								false
							)}
						</Text>
					</span>
				</>
			)}

			<div className="align-items-center c-gap-2 d-flex">
				<ClayButton
					aria-describedby={
						criteriaValid ? undefined : invalidCriteriaMessageId
					}
					aria-label={isBatch ? viewLabel : undefined}
					borderless
					className="rounded-lg"
					data-testid="preview-criteria-button"
					disabled={!criteriaValid || !membersCount}
					displayType="secondary"
					monospaced={isBatch}
					onClick={handlePreviewClick}
					size="sm"
					title={viewLabel}
				>
					<ClayIcon
						className={getCN({'mr-2': !isBatch})}
						symbol="view"
					/>

					{!isBatch && viewLabel}
				</ClayButton>

				{!criteriaValid && (
					<span className="sr-only" id={invalidCriteriaMessageId}>
						{Liferay.Language.get(
							'some-of-your-criteria-are-incomplete-or-invalid'
						)}
					</span>
				)}
			</div>
		</>
	);
};

export default SegmentMembership;
