import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import Modal from 'shared/components/modal';
import React, {useEffect, useState} from 'react';
import Table, {Column} from 'shared/components/table';
import {
	ACTION_TYPES,
	useSelectionContext,
	withSelectionProvider,
} from 'shared/context/selection';
import {fromJS, List} from 'immutable';
import {sub} from 'shared/util/lang';

interface IBatchActionModalProps extends React.HTMLAttributes<HTMLDivElement> {
	actionOptions: {
		actionCountString: string;
		options: {label: string; value: string}[];
		optionsLabel: string;
	};
	columns: Column[];
	editableAttr: string;
	fitContent: boolean;
	items: any[];
	onClose: () => void;
	onSave: (params: {
		edits: {[key: string]: string};
		ids: string[];
	}) => Promise<any>;
	title: string;
}

const BatchActionModal: React.FC<IBatchActionModalProps> = ({
	actionOptions: {actionCountString = '', options = [], optionsLabel = ''},
	className,
	columns = [],
	editableAttr = '',
	fitContent = false,
	items = [],
	onClose,
	onSave,
	title = '',
}) => {
	const [itemsIList, setItemsIList] = useState<List<any>>(fromJS(items));
	const [selectedKey, setSelectedKey] = useState(optionsLabel);

	const {selectedItems: selectedItemsIOMap, selectionDispatch} =
		useSelectionContext();

	useEffect(() => {
		items.length &&
			selectionDispatch?.({payload: {items}, type: ACTION_TYPES.add});
	}, []);

	const handleEdits = (newVal: string) => {
		setItemsIList(
			itemsIList.map((itemIMap) =>
				selectedItemsIOMap.has(itemIMap.get('id'))
					? itemIMap.set(editableAttr, newVal)
					: itemIMap
			) as List<any>
		);
		setSelectedKey(newVal);
	};

	const handleItemsChange = (item: {[key: string]: any}) => {
		selectionDispatch?.({payload: {item}, type: ACTION_TYPES.toggle});
	};

	const handleSave = () => {
		onSave({
			edits: {[editableAttr]: selectedKey},
			ids: selectedItemsIOMap.keySeq().toArray(),
		}).then(onClose);
	};

	return (
		<Modal
			className={getCN(
				className,
				'scroll-container',
				'batch-action-modal-root',
				{
					'fit-content': fitContent,
				}
			)}
			size="lg"
		>
			<Modal.Header onClose={onClose} title={title} />

			<Modal.Body>
				<div>
					<ClayDropDown
						closeOnClick
						trigger={
							<ClayButton
								className="button-root"
								displayType="secondary"
							>
								<span>{selectedKey}</span>

								<ClayIcon
									className="icon-root ml-2"
									symbol="caret-bottom"
								/>
							</ClayButton>
						}
					>
						{options.map((option) => (
							<ClayDropDown.Item
								className="set-max-zindex"
								key={option.value}
								onClick={() => handleEdits(option.value)}
							>
								{option.label}
							</ClayDropDown.Item>
						))}
					</ClayDropDown>

					<p className="text-secondary">
						{sub(
							actionCountString,
							[
								<b key="selectedCount">
									{selectedItemsIOMap.size}
								</b>,
							],
							false
						)}
					</p>
				</div>

				<Table
					columns={columns}
					items={itemsIList.toJS()}
					onSelectItemsChange={handleItemsChange}
					rowIdentifier="id"
					selectedItemsIOMap={selectedItemsIOMap}
					showCheckbox
				/>
			</Modal.Body>

			<Modal.Footer>
				<ClayButton
					className="button-root"
					displayType="secondary"
					onClick={onClose}
				>
					{Liferay.Language.get('cancel')}
				</ClayButton>

				<ClayButton
					className="button-root"
					disabled={
						selectedKey === optionsLabel ||
						selectedItemsIOMap.isEmpty()
					}
					displayType="primary"
					onClick={handleSave}
				>
					{Liferay.Language.get('save')}
				</ClayButton>
			</Modal.Footer>
		</Modal>
	);
};

export default withSelectionProvider(BatchActionModal);
