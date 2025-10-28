import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import Constants, {OrderByDirections} from 'shared/util/constants';
import getCN from 'classnames';
import React from 'react';
import {getDefaultSortOrder, invertSortOrder} from 'shared/util/pagination';
import {noop} from 'lodash';
import {OrderParams} from 'shared/util/records';
import {setUriQueryValues} from 'shared/util/router';

const {
	pagination: {cur: defaultPage}
} = Constants;

interface IHeaderCellProps {
	children: React.ReactNode;
	className?: string;
	field: string;
	headerLink?: boolean;
	onSortOrderChange?: (orderParams: OrderParams) => void;
	sortable?: boolean;
	sortOrder: OrderByDirections;
}

const ButtonSort = ({children, sortOrder}) => (
	<div className='align-items-center d-flex justify-content-between'>
		<div className='text-truncate'>{children}</div>

		<ClayButton
			aria-label={
				sortOrder === OrderByDirections.Ascending
					? Liferay.Language.get('ascending')
					: Liferay.Language.get('descending')
			}
			className='component-action ml-2'
			size='sm'
		>
			{!sortOrder ? (
				<span className='opacity-25'>
					<ClayIcon className='icon-root' symbol='order-arrow' />
				</span>
			) : (
				<ClayIcon
					className='icon-root'
					symbol={
						sortOrder === OrderByDirections.Ascending
							? 'order_arrow_ascending'
							: 'order_arrow_descending'
					}
				/>
			)}
		</ClayButton>
	</div>
);

const HeaderCell: React.FC<IHeaderCellProps> = ({
	children,
	className,
	field,
	headerLink = false,
	onSortOrderChange = noop,
	sortOrder,
	sortable = true
}) => (
	<th className={getCN('table-head-title', className)}>
		{sortable ? (
			headerLink ? (
				<ClayLink
					button
					className='button-root w-100'
					displayType='unstyled'
					href={setUriQueryValues({
						field,
						page: defaultPage,
						sortOrder: sortOrder
							? invertSortOrder(sortOrder)
							: getDefaultSortOrder(field)
					})}
				>
					<ButtonSort sortOrder={sortOrder}>{children}</ButtonSort>
				</ClayLink>
			) : (
				<ClayButton
					className='inline-item text-truncate-inline'
					displayType='unstyled'
					onClick={() => {
						onSortOrderChange(
							new OrderParams({
								field,
								sortOrder: invertSortOrder(sortOrder)
							})
						);
					}}
				>
					<ButtonSort sortOrder={sortOrder}>{children}</ButtonSort>
				</ClayButton>
			)
		) : (
			children
		)}
	</th>
);

export default HeaderCell;
