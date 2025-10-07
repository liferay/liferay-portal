/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';

import EmptyState from '../../../../../../components/EmptyState';
import Loading from '../../../../../../components/Loading';
import Table from '../../../../../../components/Table/Table';
import i18n from '../../../../../../i18n';

type DownloadTableProps = {
	loading: boolean;
	virtualItems: VirtualItem[];
};

const DownloadTable: React.FC<DownloadTableProps> = ({
	loading,
	virtualItems,
}) => {
	if (loading) {
		return <Loading />;
	}

	if (!virtualItems?.length) {
		return (
			<EmptyState
				description={i18n.translate('no-results-found')}
				title={i18n.translate('no-results-found')}
				type="EMPTY_SEARCH"
			/>
		);
	}

	return (
		<Table
			columns={[
				{
					key: 'version',
					render: (version) => version,
					title: i18n.translate('supported-version'),
				},
				{
					align: 'right',
					key: 'version',
					render: (_, item) => (
						<ClayButton
							displayType="secondary"
							onClick={() => window.open(item.url)}
							size="sm"
						>
							{i18n.translate('download')}
						</ClayButton>
					),
				},
			]}
			rows={virtualItems}
		/>
	);
};

export default DownloadTable;
