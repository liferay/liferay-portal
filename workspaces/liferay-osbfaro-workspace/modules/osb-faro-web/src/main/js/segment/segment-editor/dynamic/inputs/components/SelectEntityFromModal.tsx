import Form from 'shared/components/form';
import getCN from 'classnames';
import Input from 'shared/components/Input';
import React from 'react';
import SearchableTableModal from 'shared/components/modals/SearchableTableModal';
import SearchableTableModalGraphql from 'shared/components/modals/SearchableTableModalGraphql';
import {Columns} from 'shared/types';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {detailsListColumns} from 'shared/util/table-columns';
import {OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';
import {useModal} from '@clayui/modal';

interface ISelectEntityFromModalProps {
	columns: Columns;
	dataSourceFn?: (params: {[key: string]: any}) => Promise<any>;
	entity?: {dataSourceName?: string; [key: string]: any};
	error: boolean;
	graphqlProps?: {[key: string]: any};
	groupId?: string;
	initialDelta?: number;
	initialOrderIOMap: OrderedMap<string, OrderParams>;
	noResultsIcon?: string;
	noResultsProps?: {[key: string]: any};
	onSubmit: (items: OrderedMap<string, object>) => void;
	orderByOptions?: {label: string; value: any}[];
	renderEntity: (entity: any) => React.ReactNode;
	submitMessage?: string;
	title: string;
}

const SelectEntityFromModal: React.FC<ISelectEntityFromModalProps> = ({
	columns,
	dataSourceFn,
	entity,
	error,
	graphqlProps,
	groupId,
	initialDelta = 10,
	initialOrderIOMap = createOrderIOMap(NAME),
	onSubmit,
	renderEntity,
	submitMessage = Liferay.Language.get('add'),
	title,
	...otherProps
}) => {
	const entityModal = useModal();

	const handleModal = () => entityModal.onOpenChange(true);

	const dataSourceOptions = graphqlProps || {dataSourceFn};

	const ModalComponent: React.ElementType = graphqlProps
		? SearchableTableModalGraphql
		: SearchableTableModal;

	return (
		<Form.GroupItem>
			<Input.Group className="select-entity-group">
				<Input.GroupItem
					className={getCN({
						'has-error': error,
					})}
				>
					<Input.Group className="select-input-root">
						<Input.GroupItem>
							<Input />
						</Input.GroupItem>

						<div className="selected-item-container">
							{renderEntity(entity)}
						</div>
					</Input.Group>
				</Input.GroupItem>

				<Input.Button
					displayType="secondary"
					onClick={handleModal}
					position="append"
				>
					{Liferay.Language.get('select')}
				</Input.Button>
			</Input.Group>

			{entityModal.open && (
				<ModalComponent
					{...dataSourceOptions}
					columns={[
						...columns,
						{
							...detailsListColumns.getDataSourceName(groupId),
							className: 'table-cell-expand',
							sortable: false,
						},
					]}
					dataSourceParams={{groupId}}
					initialDelta={initialDelta}
					initialOrderIOMap={initialOrderIOMap}
					initialSelectedItems={entity?.id ? [entity] : undefined}
					observer={entityModal.observer}
					onClose={() => entityModal.onOpenChange(false)}
					onSubmit={(items: OrderedMap<string, object>) => {
						onSubmit(items);

						entityModal.onOpenChange(false);
					}}
					submitMessage={submitMessage}
					title={title}
					{...otherProps}
				/>
			)}
		</Form.GroupItem>
	);
};

export default SelectEntityFromModal;
