import * as API from 'shared/api';
import ClayMultiSelect from '@clayui/multi-select';
import Form from 'shared/components/form';
import React, {useState} from 'react';
import {CustomValue} from 'shared/util/records';
import {
	DEFAULT_PAGE_SIZE,
	IPaginatedDataSourceParams,
	usePaginatedRequest,
} from 'shared/hooks/usePaginatedRequest';
import {getCampaignIds} from '../utils/custom-inputs';
import {ISegmentEditorCustomInputBase} from '../utils/types';
import {Item} from '@clayui/multi-select/lib/types';
import {List} from 'immutable';
import {sub} from 'shared/util/lang';
import {useCampaignNames} from 'shared/hooks/useCampaignNames';

const SEARCH_DELAY = 300;

interface ICampaignInputProps extends ISegmentEditorCustomInputBase {
	touched: boolean;
	valid: boolean;
}

const CampaignInput: React.FC<ICampaignInputProps> = ({
	channelId,
	displayValue,
	groupId,
	onChange,
	operatorRenderer: OperatorDropdown,
	property,
	touched,
	valid,
	value,
}) => {
	const [active, setActive] = useState(false);
	const [query, setQuery] = useState('');

	const ids = getCampaignIds(value);

	const {names} = useCampaignNames({
		channelId: channelId!,
		groupId: groupId!,
		ids,
	});

	const dataSourceFn = (params: IPaginatedDataSourceParams) =>
		API.campaigns
			.searchCampaignNames({channelId, groupId: groupId!, ...params})
			.then(({items, totalCount}) => ({items, total: totalCount}));

	const {items, networkState, onLoadMore} = usePaginatedRequest<{
		id: string;
		name: string;
	}>({
		dataSourceFn,
		debounceDelay: SEARCH_DELAY,
		pageSize: DEFAULT_PAGE_SIZE,
		query,
		skip: !active,
	});

	const getLabel = (id: string) => {
		const name = names[id] ?? items.find((item) => item.id === id)?.name;

		if (name) {
			return name;
		}

		return names[id] === null
			? (sub(Liferay.Language.get('undefined-x'), [
					property.label,
				]) as string)
			: Liferay.Language.get('loading');
	};

	const handleItemsChange = (newItems: Item[]) => {
		const newIds = newItems.map(({value}) => String(value));

		setQuery('');

		onChange({
			touched: true,
			valid: newIds.length > 0,
			value: value.setIn(
				['criterionGroup', 'items', 0, 'value'],
				List(newIds)
			) as CustomValue,
		});
	};

	return (
		<div className="criteria-statement">
			<Form.Group autoFit>
				<Form.GroupItem className="entity-name" label shrink>
					{property.entityName}
				</Form.GroupItem>

				<Form.GroupItem className="display-value" label shrink>
					{displayValue}
				</Form.GroupItem>

				<OperatorDropdown />

				<Form.GroupItem>
					<ClayMultiSelect
						allowsCustomLabel={false}
						aria-label={property.label}
						clearAllTitle={Liferay.Language.get('clear-all')}
						closeButtonAriaLabel={Liferay.Language.get('remove-x')}
						isValid={valid || !touched}
						items={ids.map((id) => ({
							label: getLabel(id),
							value: id,
						}))}
						loadingState={networkState}
						onActiveChange={setActive}
						onChange={setQuery}
						onItemsChange={handleItemsChange}
						onLoadMore={onLoadMore}
						sourceItems={items
							.filter(({id}) => !ids.includes(id))
							.map(({id, name}) => ({label: name, value: id}))}
						value={query}
					/>
				</Form.GroupItem>
			</Form.Group>
		</div>
	);
};

export default CampaignInput;
