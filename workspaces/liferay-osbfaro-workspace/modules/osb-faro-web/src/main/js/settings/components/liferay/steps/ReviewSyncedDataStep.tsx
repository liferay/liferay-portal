import ClayForm from '@clayui/form';
import DataSourceQuery, {
	DataSource,
	DataSourceSyncData,
} from 'shared/queries/DataSourceQuery';
import React, {useEffect, useState} from 'react';
import {CREATE_DATE} from 'shared/util/pagination';
import {DataSourceTypes, OrderByDirections} from 'shared/util/constants';
import {ReviewSyncedDataFragment} from '../ReviewSyncedDataFragment';
import {updateSearchParams} from 'settings/components/base-page/utis';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useInterval} from 'shared/hooks/useInterval';
import {useLazyQuery} from '@apollo/client';
import {WizardPageButtonGroup} from 'settings/components/base-page/WizardPageButtonGroup';

const TIMEOUT_INTERVAL = 5000;

interface IReviewSyncedDataStepProps {
	onNext: () => void;
	onPrev: () => void;
}

const ReviewSyncedDataStep = ({onNext, onPrev}: IReviewSyncedDataStepProps) => {
	const history = useHistoryAdapter();
	const [dataSource, setDataSource] = useState<DataSource>({
		contactsSyncDetails: {selected: false},
		id: '',
		sitesSyncDetails: {selected: false},
	});
	const [getDataSources, {data}] = useLazyQuery<DataSourceSyncData>(
		DataSourceQuery,
		{
			fetchPolicy: 'network-only',
			variables: {
				size: 1,
				sort: {
					column: CREATE_DATE,
					type: OrderByDirections.Descending,
				},
				type: DataSourceTypes.Liferay,
			},
		}
	);

	useInterval<void>(getDataSources, TIMEOUT_INTERVAL);

	useEffect(() => {
		if (data) {
			const dataSource = data.dataSources[0];

			setDataSource(dataSource);
		}
	}, [data]);

	useEffect(() => {
		getDataSources();
	}, []);

	return (
		<ClayForm
			onSubmit={async (event) => {
				event.preventDefault();

				updateSearchParams(history, 'dataSourceId', dataSource.id);

				onNext();
			}}
		>
			<ReviewSyncedDataFragment
				contactsSelected={dataSource?.contactsSyncDetails?.selected}
				sitesSelected={dataSource?.sitesSyncDetails?.selected}
			/>

			<WizardPageButtonGroup
				nextButtonLabel={Liferay.Language.get('continue')}
				onCancel={() => {
					updateSearchParams(history, 'dataSourceId', dataSource.id);

					onPrev();
				}}
				prevButtonLabel={Liferay.Language.get('previous')}
			/>
		</ClayForm>
	);
};

export {ReviewSyncedDataStep};
