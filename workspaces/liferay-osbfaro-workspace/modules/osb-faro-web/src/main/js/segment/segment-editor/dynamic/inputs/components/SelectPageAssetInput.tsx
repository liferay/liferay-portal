import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import Form from 'shared/components/form';
import getCN from 'classnames';
import Label from 'shared/components/Label';
import Loading, {Align} from 'shared/components/Loading';
import React, {useEffect, useMemo, useRef, useState} from 'react';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect, ConnectedProps} from 'react-redux';
import {COUNT, createOrderIOMap} from 'shared/util/pagination';
import {
	activityAssetsListColumns,
	detailsListColumns,
} from 'shared/util/table-columns';
import {getEventId} from '../../utils/activity-keys';
import {Option, Picker} from '@clayui/core';
import {OrderedMap} from 'immutable';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useRequest} from 'shared/hooks/useRequest';

export type PageAssetItem = {id: string; name: string};

// A single chosen page/asset paired with its built activityKey
// (applicationId#eventId#assetId). N selections map to one criterion.

type PageAssetSelection = {
	activityKey: string;
	id: string;
	name: string;
};

// What the input reports to the behavior criterion. When `selections` is empty
// the criterion matches every asset of the selected type (applicationId eq /
// eventId eq); otherwise it targets those specific assets by activityKey.

export type BehaviorSelection = {
	applicationId: string;
	eventId: string;
	objectDefinitionName?: string;
	selections: PageAssetSelection[];
};

type AssetSummaryType = {id: string; name: string};

type SelectorType = 'page' | 'assetType';

// Event actions that only apply to assets (never pages). For these, the type
// selector is forced to "Asset Type" and shown as static text, not a dropdown.
// Click is asset-only too: there is no page-click event (analytics-client-js
// only emits blogClicked/webContentClicked), so a "click on a page" would fall
// back to pageViewed and reload as View.

const ASSET_ONLY_ACTIONS = [
	'click',
	'comment',
	'download',
	'impression',
	'submit',
];

// Maps an asset-summary type id to its analytics applicationId. Known DXP types
// map to their applicationId; anything else (e.g. new CMS objects) is treated
// as ObjectEntry.

const APPLICATION_ID_MAP: {[key: string]: string} = {
	blog: 'Blog',
	blogs: 'Blog',
	document: 'Document',
	documentsandmedia: 'Document',
	form: 'Form',
	forms: 'Form',
	webcontent: 'WebContent',
};

const getCanonicalKey = (value: string): string =>
	value.toLowerCase().replace(/[^a-z0-9]/g, '');

const resolveApplicationId = (typeId: string): string =>
	APPLICATION_ID_MAP[getCanonicalKey(typeId)] ?? 'ObjectEntry';

// The canonical asset-summary type slug and localized label for each known DXP
// applicationId. These are fixed: a DXP type is offered for every event it
// supports, regardless of whether the channel has tracked activity for it.
// Labels use literal Liferay.Language.get keys because it is a build-time macro
// — it cannot take a variable key.

const DXP_ASSET_TYPES: {
	[applicationId: string]: AssetSummaryType & {label: string};
} = {
	Blog: {id: 'blogs', label: Liferay.Language.get('blogs'), name: 'blogs'},
	Document: {
		id: 'document',
		label: Liferay.Language.get('documents-and-media'),
		name: 'document',
	},
	Form: {id: 'forms', label: Liferay.Language.get('forms'), name: 'forms'},
	WebContent: {
		id: 'webContent',
		label: Liferay.Language.get('web-content'),
		name: 'webContent',
	},
};

// A predefined DXP type id maps to its friendly label; every other type (e.g.
// ObjectEntry object-definition names) shows the raw value returned by
// asset-summary-types.

const PREDEFINED_ASSET_TYPE_LABELS: {[typeId: string]: string} =
	Object.fromEntries(
		Object.values(DXP_ASSET_TYPES).map(({id, label}) => [id, label])
	);

export const getAssetTypeLabel = (
	typeId: string,
	fallbackName: string = typeId
): string => PREDEFINED_ASSET_TYPE_LABELS[typeId] ?? fallbackName;

// Every asset type the picker offers, independent of the behavior: the fixed DXP
// types plus the object-definition names (dynamic, from the asset-summary-types
// request, fetched only on LDP plans). Exported to unit-test the picker contents.

export const getCompatibleAssetTypes = (
	assetSummaryTypes: AssetSummaryType[]
): AssetSummaryType[] => {
	const dxpTypes = Object.values(DXP_ASSET_TYPES);

	const objectDefinitionTypes = assetSummaryTypes.filter(
		(type) => resolveApplicationId(type.id) === 'ObjectEntry'
	);

	return [...dxpTypes, ...objectDefinitionTypes];
};

// activityKey = applicationId#eventId#assetId — the format the backend stores
// and matches against tracked events.

const buildActivityKey = (
	applicationId: string,
	action: string | undefined,
	assetId: string
): string => `${applicationId}#${getEventId(applicationId, action)}#${assetId}`;

// activity/asset returns a `count` of the specific event. Label it to match the
// criterion's action, falling back to the view count (pages, clicks, views).

const COUNT_COLUMN_BY_ACTION: {[key: string]: {[key: string]: any}} = {
	comment: activityAssetsListColumns.commentCount,
	download: activityAssetsListColumns.downloadCount,
	submit: activityAssetsListColumns.submissionCount,
};

const getCountColumn = (action?: string, actionLabel?: string) => {
	const column =
		COUNT_COLUMN_BY_ACTION[action ?? ''] ??
		activityAssetsListColumns.viewCount;

	return actionLabel ? {...column, label: actionLabel} : column;
};

// Mirrors the base SelectEntityFromModal listing: name + data-source asset key,
// the event count, and the data source. Shared by pages and assets since both
// list from the same activity/asset endpoint (which keys items by the same id
// the activityKey stores).

const getColumns = (groupId: string, countColumn: {[key: string]: any}) => [
	activityAssetsListColumns.nameUrl,
	countColumn,
	{
		...detailsListColumns.getDataSourceName(groupId),
		className: 'table-cell-expand',
		sortable: false,
	},
];

const PAGE_OR_ASSET_TYPE_OPTIONS = [
	{label: Liferay.Language.get('page'), value: 'page'},
	{label: Liferay.Language.get('asset-type'), value: 'assetType'},
];

// A Picker trigger that mirrors the default Clay select trigger, but swaps the
// caret (the form-control-select background) for a loading spinner while the
// asset types load and disables itself, keeping the placeholder value visible.

interface IAssetTypeTriggerProps
	extends React.ButtonHTMLAttributes<HTMLButtonElement> {
	label?: string;
	loading?: boolean;
}

const AssetTypeTrigger = React.forwardRef<
	HTMLButtonElement,
	IAssetTypeTriggerProps
>(({label, loading, ...rest}, ref) => (
	<button
		{...rest}
		className={getCN(
			'form-control form-control-select-secondary operator-input',
			{'form-control-select': !loading}
		)}
		disabled={loading}
		ref={ref}
		type="button"
	>
		{label}

		{loading && <Loading align={Align.Right} />}
	</button>
));

const connector = connect(null, {close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface ISelectPageAssetInputProps extends PropsFromRedux {
	action?: string;
	actionLabel?: string;

	// applicationId of the current selection, used on reload to infer Page vs
	// Asset Type and preselect the type. Undefined only until a type resolves.

	applicationId?: string;
	channelId: string;
	groupId: string;
	objectDefinitionName?: string;
	onSelectionsChange?: (behaviorSelection: BehaviorSelection) => void;
	selectedItems?: Array<PageAssetItem & {activityKey?: string}>;
}

const SelectPageAssetInput: React.FC<ISelectPageAssetInputProps> = ({
	action,
	actionLabel,
	applicationId,
	channelId,
	close,
	groupId,
	objectDefinitionName,
	onSelectionsChange,
	open,
	selectedItems = [],
}) => {
	const isAssetOnly = !!action && ASSET_ONLY_ACTIONS.includes(action);

	const [selectorType, setSelectorType] = useState<SelectorType>(
		isAssetOnly || applicationId !== 'Page' ? 'assetType' : 'page'
	);

	// The type the picker starts on: the DXP slug for the applicationId hint (on
	// drop/reload). A new criterion starts with no type, showing the "Select a
	// type" placeholder; the reload effect refines an ObjectEntry hint once the
	// object-definition types load.

	const [assetType, setAssetType] = useState<string>(
		() =>
			(applicationId && applicationId !== 'Page'
				? DXP_ASSET_TYPES[applicationId]?.id
				: undefined) ?? ''
	);

	// Preselect the reloaded type only once, so it never fights a later user
	// change.

	const didPreselectRef = useRef(false);

	// Object definitions only exist on LDP plans, so a non-LDP plan has no
	// object-definition types to fetch — it offers just the fixed DXP types.

	const ldpEnabled = useLDPEnabled({groupId});

	const shouldRequestAssetTypes = ldpEnabled;

	const {data: assetTypesData, loading} = useRequest<
		{
			channelId: string;
			groupId: string;
			page: number;
			pageSize: number;
			rangeKey: null;
		},
		{items: AssetSummaryType[]}
	>({
		dataSourceFn: (variables) => API.assets.searchTypes(variables),
		skipRequest: !shouldRequestAssetTypes,
		variables: {
			channelId,
			groupId,
			page: 1,
			pageSize: 100,
			rangeKey: null,
		},
	});

	// All-or-nothing loading: while the (LDP-only) asset-summary-types request is
	// in flight, the type picker shows a loading indicator instead of a partial
	// list. Guarded by shouldRequestAssetTypes because useRequest leaves `loading`
	// true when the request is skipped (non-LDP), which must not block the picker.

	const isLoadingAssetTypes = shouldRequestAssetTypes && loading;

	const compatibleAssetTypes = useMemo(
		() => getCompatibleAssetTypes(assetTypesData?.items ?? []),
		[assetTypesData]
	);

	// The type dropdown offers the types compatible with the event; a type is
	// required, so there is no "Any" option.

	const assetTypeOptions = useMemo(
		() =>
			compatibleAssetTypes.map(({id, name}) => ({
				label: getAssetTypeLabel(id, name),
				value: id,
			})),
		[compatibleAssetTypes]
	);

	// The label shown on the collapsed picker: the selected type's label, or the
	// "Select a type" placeholder when nothing is chosen yet.

	const assetTypeLabel = useMemo(
		() =>
			assetType
				? getAssetTypeLabel(
						assetType,
						compatibleAssetTypes.find(({id}) => id === assetType)
							?.name
					)
				: Liferay.Language.get('select-a-type'),
		[assetType, compatibleAssetTypes]
	);

	// On reload, preselect the type matching the saved applicationId. DXP types
	// resolve synchronously in the initial state; this only needs to refine an
	// ObjectEntry hint once its object-definition types load. It runs once so it
	// never overrides a later user change.

	useEffect(() => {
		if (
			didPreselectRef.current ||
			!applicationId ||
			applicationId === 'Page' ||
			(shouldRequestAssetTypes && !assetTypesData)
		) {
			return;
		}

		const matchByObjectDefinition =
			applicationId === 'ObjectEntry' && objectDefinitionName;

		const matchingType = compatibleAssetTypes.find((type) =>
			matchByObjectDefinition
				? type.id === objectDefinitionName
				: resolveApplicationId(type.id) === applicationId
		);

		if (matchingType) {
			setAssetType(matchingType.id);
		}

		didPreselectRef.current = true;
	}, [
		applicationId,
		assetTypesData,
		compatibleAssetTypes,
		objectDefinitionName,
		shouldRequestAssetTypes,
	]);

	const isPage = !isAssetOnly && selectorType === 'page';

	// Reports the criterion intent: the applicationId/eventId of the selected
	// type (matching every asset of it when no specific asset is chosen) plus
	// the chosen assets. The type/isPage are passed explicitly so a just-changed
	// value is used instead of the not-yet-committed state.

	const emit = (
		items: Array<PageAssetItem & {activityKey?: string}> = [],
		typeValue: string = assetType,
		isPageValue: boolean = isPage
	) => {

		// No asset type selected yet (and not Page): report an empty applicationId
		// so the behavior criterion stays invalid until the user picks a type.

		if (!isPageValue && !typeValue) {
			onSelectionsChange?.({
				applicationId: '',
				eventId: '',
				selections: [],
			});

			return;
		}

		const applicationId = isPageValue
			? 'Page'
			: resolveApplicationId(typeValue);

		onSelectionsChange?.({
			applicationId,
			eventId: getEventId(applicationId, action),
			objectDefinitionName:
				applicationId === 'ObjectEntry' ? typeValue : undefined,
			selections: items.map((item) => ({
				activityKey:
					item.activityKey ??
					buildActivityKey(applicationId, action, item.id),
				id: item.id,
				name: item.name,
			})),
		});
	};

	const handleOpenModal = () => {

		// Pages and assets both list from the activity/asset endpoint, keyed by
		// the same id the activityKey stores. currentApplicationId drives both
		// the listing query and the activityKey built on submit, so the rows,
		// the pre-selected checkboxes, and the emitted keys all stay aligned.

		const currentApplicationId = isPage
			? 'Page'
			: resolveApplicationId(assetType);

		const eventId = getEventId(currentApplicationId, action);

		const dataSourceFn = async ({
			delta = 10,
			orderIOMap,
			page = 1,
			query,
		}: {
			[key: string]: any;
		}): Promise<{items: PageAssetItem[]; total: number}> => {
			const result = await API.activities.searchAssets({
				applicationId: currentApplicationId,
				channelId,
				cur: page,
				delta,
				eventId,
				groupId,

				// For object entries the applicationId is the generic
				// 'ObjectEntry'; pass the selected object definition name so the
				// backend can narrow the listing to it (backend support pending).

				...(currentApplicationId === 'ObjectEntry' && {
					objectDefinitionName: assetType,
				}),
				orderIOMap,
				query: query ?? '',
			});

			return {
				items: (result.items ?? []).map((item: any) => ({
					count: item.count,
					dataSourceAssetPK: item.dataSourceAssetPK,
					dataSourceName: item.dataSourceName,
					id: item.id,
					name: item.name,
				})),

				// activity/asset returns `total`; a falsy total disables the
				// modal's "select all" checkbox.

				total: result.total ?? result.totalCount ?? 0,
			};
		};

		const countColumn = getCountColumn(action, actionLabel);

		open(modalTypes.SEARCHABLE_TABLE_MODAL, {
			columns: getColumns(groupId, countColumn),
			dataSourceFn,
			initialOrderIOMap: createOrderIOMap(COUNT),
			initialSelectedItems: selectedItems,
			noResultsIcon: 'web-content',
			onClose: close,
			onSubmit: (items: OrderedMap<string, PageAssetItem>) => {
				emit(
					items
						.valueSeq()
						.filter(Boolean)
						.map((item) => ({id: item!.id, name: item!.name}))
						.toArray()
				);

				close();
			},
			orderByOptions: [{label: countColumn.label, value: COUNT}],
			rowIdentifier: 'id',
			submitMessage: Liferay.Language.get('select'),
			title: isPage
				? Liferay.Language.get('select-page')
				: Liferay.Language.get('select-asset'),
		});
	};

	const handleSelectorTypeChange = (value: SelectorType) => {
		const nextIsPage = value === 'page';

		// Switching to Asset Type clears the selection so the user must pick a
		// type (the criterion is invalid until then); switching to Page keeps the
		// current type value (unused).

		const nextAssetType = nextIsPage ? assetType : '';

		setSelectorType(value);
		setAssetType(nextAssetType);
		emit([], nextAssetType, nextIsPage);
	};

	const handleAssetTypeChange = (value: string) => {
		setAssetType(value);
		emit([], value, isPage);
	};

	const selectLabel = isPage
		? Liferay.Language.get('add-pages')
		: Liferay.Language.get('add-assets');

	return (
		<>
			<Form.GroupItem className="entity-name mr-0" label shrink>
				{Liferay.Language.get('on').toLowerCase()}
			</Form.GroupItem>

			{isAssetOnly ? (
				<Form.GroupItem className="display-value" label shrink>
					<b>{Liferay.Language.get('asset-type')}</b>
				</Form.GroupItem>
			) : (
				<Form.GroupItem shrink>
					<Picker
						aria-label={Liferay.Language.get('page-or-asset-type')}
						className="operator-input"
						items={PAGE_OR_ASSET_TYPE_OPTIONS}
						onSelectionChange={(value) =>
							handleSelectorTypeChange(value as SelectorType)
						}
						selectedKey={selectorType}
					>
						{({label, value}) => (
							<Option key={value}>{label}</Option>
						)}
					</Picker>
				</Form.GroupItem>
			)}

			{!isPage && (
				<Form.GroupItem shrink>
					<Picker
						aria-label={Liferay.Language.get('asset-type')}
						as={AssetTypeTrigger}
						className="operator-input"
						items={assetTypeOptions}
						label={assetTypeLabel}
						loading={isLoadingAssetTypes}
						onSelectionChange={(value) =>
							handleAssetTypeChange(value as string)
						}
						selectedKey={assetType || undefined}
					>
						{({label, value}) => (
							<Option key={value}>{label}</Option>
						)}
					</Picker>
				</Form.GroupItem>
			)}

			{selectedItems.length > 0 ? (
				<>
					<div className="w-100" />

					<Form.GroupItem className="mt-2" shrink>
						<div
							className="input-group-item input-list-root"
							style={{maxWidth: '52rem', width: 'fit-content'}}
						>
							<div
								className="form-control form-control-tag-group"
								style={{
									maxWidth: '52rem',
									overflowY: 'auto',
									width: 'fit-content',
								}}
							>
								{selectedItems.map((item) => (
									<Label
										display="secondary"
										key={item.id}
										onRemove={() =>
											emit(
												selectedItems.filter(
													({id}) => id !== item.id
												)
											)
										}
									>
										{item.name}
									</Label>
								))}

								<ClayButton
									aria-label={Liferay.Language.get('clear')}
									className="button-root text-secondary"
									displayType="unstyled"
									onClick={() => emit([])}
								>
									<ClayIcon
										className="icon-root"
										symbol="times-circle"
									/>
								</ClayButton>
							</div>
						</div>
					</Form.GroupItem>

					<Form.GroupItem className="mt-2" shrink>
						<ClayButton
							displayType="secondary"
							onClick={handleOpenModal}
						>
							{Liferay.Language.get('select')}
						</ClayButton>
					</Form.GroupItem>
				</>
			) : (
				<Form.GroupItem shrink>
					<ClayButton
						className="button-root"
						disabled={
							!isPage && (!assetType || isLoadingAssetTypes)
						}
						displayType="secondary"
						onClick={handleOpenModal}
					>
						<ClayIcon symbol="plus" />

						<span className="ml-2">{selectLabel}</span>
					</ClayButton>
				</Form.GroupItem>
			)}
		</>
	);
};

export default connector(SelectPageAssetInput);
