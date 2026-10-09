import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import Form from 'shared/components/form';
import Label from 'shared/components/Label';
import React from 'react';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect, ConnectedProps} from 'react-redux';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {OrderedMap} from 'immutable';

export type CategoryItem = {id: string; name: string};

const CATEGORY_COLUMNS = [
	{
		accessor: 'name',
		className: 'table-cell-expand',
		label: Liferay.Language.get('name'),
		sortable: true,
	},
];

const connector = connect(null, {close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface ISelectCategoryFromModalProps extends PropsFromRedux {
	channelId: string;
	groupId: string;
	onCategoriesChange: (categories: CategoryItem[]) => void;
	selectedCategories: CategoryItem[];
	vocabularyId: string;
}

const SelectCategoryFromModal: React.FC<ISelectCategoryFromModalProps> = ({
	channelId,
	close,
	groupId,
	onCategoriesChange,
	open,
	selectedCategories,
	vocabularyId,
}) => {
	const categoriesDataFn = async ({
		delta = 5,
		page = 1,
		query,
	}: {
		delta?: number;
		page?: number;
		query?: string;
	}): Promise<{items: CategoryItem[]; total: number}> => {
		const result = await API.categories.search({
			channelId,
			groupId,
			keywords: query ?? '',
			page,
			pageSize: delta,
			vocabularyId,
		});

		return {
			items: (result.items ?? []) as CategoryItem[],
			total: result.totalCount ?? 0,
		};
	};

	const handleOpenModal = () => {
		open(modalTypes.SEARCHABLE_TABLE_MODAL, {
			columns: CATEGORY_COLUMNS,
			dataSourceFn: categoriesDataFn,
			initialOrderIOMap: createOrderIOMap(NAME),
			initialSelectedItems: selectedCategories,
			onClose: close,
			onSubmit: (items: OrderedMap<string, CategoryItem>) => {
				onCategoriesChange(
					items
						.valueSeq()
						.filter(Boolean)
						.map((item) => ({
							id: item!.id,
							name: item!.name,
						}))
						.toArray()
				);

				close();
			},
			rowIdentifier: 'id',
			submitMessage: Liferay.Language.get('select'),
			title: Liferay.Language.get('select-category'),
		});
	};

	const handleRemoveCategory = (id: string) => {
		onCategoriesChange(selectedCategories.filter((c) => c.id !== id));
	};

	if (selectedCategories.length === 0) {
		return (
			<Form.GroupItem shrink>
				<ClayButton
					className="button-root"
					displayType="secondary"
					onClick={handleOpenModal}
				>
					{`+ ${Liferay.Language.get('add-category')}`}
				</ClayButton>
			</Form.GroupItem>
		);
	}

	return (
		<>
			<Form.GroupItem shrink>
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
						{selectedCategories.map((category) => (
							<Label
								display="secondary"
								key={category.id}
								onRemove={() =>
									handleRemoveCategory(category.id)
								}
							>
								{category.name}
							</Label>
						))}

						<ClayButton
							aria-label={Liferay.Language.get('clear')}
							className="button-root text-secondary"
							displayType="unstyled"
							onClick={() => onCategoriesChange([])}
						>
							<ClayIcon
								className="icon-root"
								symbol="times-circle"
							/>
						</ClayButton>
					</div>
				</div>
			</Form.GroupItem>

			<Form.GroupItem shrink>
				<ClayButton displayType="secondary" onClick={handleOpenModal}>
					{Liferay.Language.get('select')}
				</ClayButton>
			</Form.GroupItem>
		</>
	);
};

export default connector(SelectCategoryFromModal);
