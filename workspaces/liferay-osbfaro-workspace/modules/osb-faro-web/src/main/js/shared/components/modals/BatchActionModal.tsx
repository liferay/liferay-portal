import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import React, {useEffect, useState} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import Table from 'shared/components/table';
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
	columns: any[];
	editableAttr: string;
	fitContent: boolean;
	items: any[];
	observer: Observer;
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
	observer,
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

	// Edit Permissions keeps its layout without dividers until its UX review.

	return (
		<ClayModal
			className={getCN(className, 'scroll-container', {
				'fit-content': fitContent,
			})}
			observer={observer}
			size="lg"
		>
			<ClayModal.Header className="border-0">{title}</ClayModal.Header>

			<ClayModal.Body className="border-0">
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
								key={option.value}
								onClick={() => handleEdits(option.value)}
							>
								{option.label}
							</ClayDropDown.Item>
						))}
					</ClayDropDown>

					<p className="py-3 text-secondary">
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
			</ClayModal.Body>

			<ClayModal.Footer
				className="border-0"
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
							disabled={
								selectedKey === optionsLabel ||
								selectedItemsIOMap.isEmpty()
							}
							displayType="primary"
							onClick={handleSave}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default withSelectionProvider(BatchActionModal);
