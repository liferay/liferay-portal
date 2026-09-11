import React from 'react';
import {ClayPaginationWithBasicItems} from '@clayui/pagination';
import {PaginationBar} from '@clayui/pagination-bar';

interface ISidebarPaginationProps {
	activePage: number;
	onPageChange: (page: number) => void;
	totalPages: number;
}

const SidebarPagination: React.FC<ISidebarPaginationProps> = ({
	activePage,
	onPageChange,
	totalPages,
}) => {
	if (totalPages <= 1) {
		return null;
	}

	return (
		<PaginationBar className="justify-content-center sidebar-pagination">
			<ClayPaginationWithBasicItems
				active={activePage}
				onActiveChange={onPageChange}
				totalPages={totalPages}
			/>
		</PaginationBar>
	);
};

export default SidebarPagination;
