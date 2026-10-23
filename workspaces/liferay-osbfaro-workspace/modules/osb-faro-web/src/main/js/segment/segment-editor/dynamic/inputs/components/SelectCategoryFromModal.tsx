import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import ClayLabel from '@clayui/label';
import Form from 'shared/components/form';
import React from 'react';
import SearchableTableModal from 'shared/components/modals/SearchableTableModal';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {OrderedMap} from 'immutable';
import {useModal} from '@clayui/modal';

export type CategoryItem = {id: string; name: string};

const CATEGORY_COLUMNS = [
	{
		accessor: 'name',
		className: 'table-cell-expand',
		label: Liferay.Language.get('name'),
		sortable: true,
	},
];

interface ISelectCategoryFromModalProps {
	channelId: string;
	groupId: string;
	onCategoriesChange: (categories: CategoryItem[]) => void;
	selectedCategories: CategoryItem[];
	vocabularyId: string;
}

const SelectCategoryFromModal: React.FC<ISelectCategoryFromModalProps> = ({
	channelId,
	groupId,
	onCategoriesChange,
	selectedCategories,
	vocabularyId,
}) => {
	const categoriesModal = useModal();

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

	const getModalProps = () => ({
		columns: CATEGORY_COLUMNS,
		dataSourceFn: categoriesDataFn,
		initialOrderIOMap: createOrderIOMap(NAME),
		initialSelectedItems: selectedCategories,
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

			categoriesModal.onOpenChange(false);
		},
		rowIdentifier: 'id',
		submitMessage: Liferay.Language.get('select'),
		title: Liferay.Language.get('select-category'),
	});

	const handleOpenModal = () => categoriesModal.onOpenChange(true);

	const modal = categoriesModal.open && (
		<SearchableTableModal
			{...getModalProps()}
			observer={categoriesModal.observer}
			onClose={() => categoriesModal.onOpenChange(false)}
		/>
	);

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

				{modal}
			</Form.GroupItem>
		);
	}

	return (
		<>
			{modal}

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
							<ClayLabel
								closeButtonProps={{
									'aria-label': Liferay.Language.get('close'),
									onClick: () =>
										handleRemoveCategory(category.id),
								}}
								displayType="secondary"
								inverse
								key={category.id}
							>
								{category.name}
							</ClayLabel>
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

export default SelectCategoryFromModal;
