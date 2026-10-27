import * as API from 'shared/api';
import ClayLabel from '@clayui/label';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import LifecycleSticker from './LifecycleSticker';
import Loading from 'shared/components/Loading';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useState} from 'react';
import SearchInput from 'shared/components/SearchInput';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {formatDateToTimeZone, SHORT_DATE_TIME_FORMAT} from 'shared/util/date';
import {getStageCountLabel} from 'lifecycle/utils/lifecycleLabels';
import {ILifecycle, ILifecycleDetail} from 'shared/api/lifecycle';
import {PageActions} from 'shared/components/base-page/Header';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useDebounce} from 'shared/hooks/useDebounce';
import {useLifecycleActions} from 'lifecycle/hooks/useLifecycleActions';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';
import {useTimeZone} from 'shared/hooks/useTimeZone';

const LIFECYCLES_PAGE_SIZE = 20;

const SEARCH_DEBOUNCE_DELAY = 250;

interface ILifecycleRowActionsProps {
	lifecycle: ILifecycle;
	onDefaultChange: () => void;
	onShowDetails: () => void;
	onView?: () => void;
}

const LifecycleRowActions: React.FC<ILifecycleRowActionsProps> = ({
	lifecycle,
	onDefaultChange,
	onShowDetails,
	onView,
}) => {
	const actions = useLifecycleActions({
		lifecycle,
		onDefaultChange,
		onShowDetails,
		onView,
	});

	return (
		<PageActions
			actions={actions}
			actionsDisplayLimit={0}
			triggerAriaLabel={
				sub(Liferay.Language.get('actions-for-x'), [
					lifecycle.name || lifecycle.id,
				]) as string
			}
		/>
	);
};

const getMeta = (
	{authorName, modifiedDate}: ILifecycleDetail,
	timeZoneId: string
) => {
	if (modifiedDate == null) {
		return undefined;
	}

	const date = formatDateToTimeZone(
		modifiedDate,
		SHORT_DATE_TIME_FORMAT,
		timeZoneId
	);

	return authorName
		? (sub(Liferay.Language.get('x-by-x'), [date, authorName]) as string)
		: date;
};

interface ILifecycleSwitchListProps {
	defaultChangeCount: number;
	onDefaultChange: (lifecycleId: string) => void;
	onNavigate: () => void;
	onShowDetails: (lifecycleId: string) => void;
	openLifecycleERC?: string;
}

const LifecycleSwitchList: React.FC<ILifecycleSwitchListProps> = ({
	defaultChangeCount,
	onDefaultChange,
	onNavigate,
	onShowDetails,
	openLifecycleERC,
}) => {
	const {channelId, groupId} = useParams();

	const {timeZoneId} = useTimeZone();

	const [query, setQuery] = useState('');

	const [pageState, setPageState] = useState({page: 1, search: ''});

	const [refreshCount, setRefreshCount] = useState(0);

	const search = useDebounce(query, SEARCH_DEBOUNCE_DELAY);

	const page = pageState.search === search ? pageState.page : 1;

	const variables = {
		channelId,
		defaultChangeCount,
		groupId: groupId!,
		page,
		pageSize: LIFECYCLES_PAGE_SIZE,
		refreshCount,
		search,
	};

	const {data, error, loading} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecyclesPage,
		variables,
	});

	const refresh = () => setRefreshCount((count) => count + 1);

	const lifecycles = data?.items ?? [];

	const totalCount = data?.totalCount ?? 0;

	const renderLifecycles = () => {
		if (loading) {
			return <Loading />;
		}

		if (error) {
			return <ErrorDisplay onReload={refresh} spacer />;
		}

		if (!lifecycles.length) {
			return (
				<NoResultsDisplay
					spacer
					title={Liferay.Language.get('no-results-found')}
				/>
			);
		}

		return (
			<ClayList>
				{lifecycles.map((lifecycle) => {
					const meta = getMeta(lifecycle, timeZoneId);

					const open =
						lifecycle.externalReferenceCode === openLifecycleERC;

					return (
						<ClayList.Item active={open} flex key={lifecycle.id}>
							<ClayList.ItemField>
								{lifecycle.defaultAccountLifecycle ? (
									<LifecycleSticker pinned />
								) : (
									<ClaySticker
										aria-hidden
										className="invisible"
									/>
								)}
							</ClayList.ItemField>

							<ClayList.ItemField expand>
								<ClayList.ItemTitle
									aria-current={open ? 'page' : undefined}
									href={toRoute(Routes.LIFECYCLE_DASHBOARD, {
										channelId,
										groupId,
										lifecycleId:
											lifecycle.externalReferenceCode,
									})}
									onClick={onNavigate}
								>
									{lifecycle.name || lifecycle.id}
								</ClayList.ItemTitle>

								{lifecycle.description && (
									<ClayList.ItemText subtext>
										{lifecycle.description}
									</ClayList.ItemText>
								)}

								{meta && (
									<ClayList.ItemText subtext>
										{meta}
									</ClayList.ItemText>
								)}

								{!!lifecycle.stages?.length && (
									<div className="mt-2">
										<ClayLabel
											displayType="info"
											inverse
											size="sm"
										>
											<strong>
												{getStageCountLabel(
													lifecycle.stages.length
												)}
											</strong>
										</ClayLabel>
									</div>
								)}
							</ClayList.ItemField>

							<ClayList.ItemField>
								<LifecycleRowActions
									lifecycle={lifecycle}
									onDefaultChange={() =>
										onDefaultChange(lifecycle.id)
									}
									onShowDetails={() =>
										onShowDetails(lifecycle.id)
									}
									onView={open ? undefined : onNavigate}
								/>
							</ClayList.ItemField>
						</ClayList.Item>
					);
				})}
			</ClayList>
		);
	};

	return (
		<div className="d-flex flex-column h-100">
			<SearchInput
				aria-label={Liferay.Language.get('search')}
				className="mb-3"
				onChange={setQuery}
				value={query}
			/>

			<div className="flex-grow-1">{renderLifecycles()}</div>

			{!loading && !error && !!totalCount && (
				<ClayPaginationBarWithBasicItems
					active={page}
					activeDelta={LIFECYCLES_PAGE_SIZE}
					className="flex-column justify-content-center mt-3"
					deltas={[{label: LIFECYCLES_PAGE_SIZE}]}
					labels={{
						paginationResults: Liferay.Language.get(
							'showing-x-to-x-of-x-entries'
						),
						perPageItems: Liferay.Language.get('x-items'),
						selectPerPageItems: Liferay.Language.get('x-items'),
					}}
					onActiveChange={(nextPage: number) =>
						setPageState({page: nextPage, search})
					}
					showDeltasDropDown={false}
					totalItems={totalCount}
				/>
			)}
		</div>
	);
};

export default LifecycleSwitchList;
