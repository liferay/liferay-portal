import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import getCN from 'classnames';
import React from 'react';
import type {Observer, Size} from '@clayui/modal/lib/types';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {MODAL_SIZE_XL} from 'shared/components/modals/sizes';
import {noop} from 'lodash';
import {OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';

interface ISearchableEntitiesTableModalProps {
	[key: string]: any;
	className?: string;
	defaultParams?: {[key: string]: any};
	initialDelta?: number;
	initialOrderIOMap: OrderedMap<string, OrderParams>;
	observer: Observer;
	onClose: () => void;
	size?: Size;
	title: string;
}

const SearchableEntitiesTableModal: React.FC<
	ISearchableEntitiesTableModalProps
> = ({
	className,
	initialDelta = 10,
	initialOrderIOMap = createOrderIOMap(NAME),
	observer,
	onClose = noop,
	size = MODAL_SIZE_XL,
	title = 'entities',
	...otherProps
}) => {
	const {
		delta,
		onDeltaChange,
		onOrderIOMapChange,
		onPageChange,
		onQueryChange,
		orderIOMap,
		page,
		query,
	} = useStatefulPagination(undefined, {
		initialDelta,
		initialOrderIOMap,
	});

	return (
		<ClayModal
			className={getCN('searchable-entities-table-modal-root', className)}
			observer={observer}
			size={size}
		>
			<ClayModal.Header>{title}</ClayModal.Header>

			<ClayModal.Body className="p-0">
				<SearchableEntityTable
					{...otherProps}
					autoFocusSearch
					delta={delta}
					onDeltaChange={onDeltaChange}
					onOrderIOMapChange={onOrderIOMapChange}
					onPageChange={onPageChange}
					onQueryChange={onQueryChange}
					orderIOMap={orderIOMap}
					page={page}
					query={query}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							className="button-root"
							displayType="primary"
							onClick={onClose}
						>
							{Liferay.Language.get('done')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default SearchableEntitiesTableModal;
