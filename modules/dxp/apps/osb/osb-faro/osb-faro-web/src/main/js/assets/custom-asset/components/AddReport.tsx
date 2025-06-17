import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import getCN from 'classnames';
import React, {useEffect, useState} from 'react';
import Thumbs from './Thumbs';
import {ASSET_METRICS} from 'shared/util/constants';
import {isEmpty} from 'lodash';

interface IAddReportProps extends React.HTMLAttributes<HTMLDivElement> {
	isEmptyDashboard?: boolean;
	onGetReport: (report: Report) => void;
}

interface IFormAddReport {
	onBlurReport: (event: React.SyntheticEvent) => void;
	onChangeReport: (event: React.SyntheticEvent) => void;
	onInputReport: (event: React.SyntheticEvent) => void;
	onSelectThumbReport: (event: React.SelectHTMLAttributes<Event>) => void;
}

type Report = {
	chartType: string;
	metric: string;
	title: string;
};

const thumbChartItems = [
	{
		selected: true,
		svg: 'cerebro_thumb_line_chart',
		text: Liferay.Language.get(
			'not-possible-to-change-the-visualization-type'
		),
		value: 'line'
	}
];

const FormAddReport: React.FC<IFormAddReport> = ({
	onBlurReport,
	onChangeReport,
	onInputReport,
	onSelectThumbReport
}) => (
	<div className='w-100'>
		<div className='row'>
			<div className='col-sm-4'>
				<div className='form-group'>
					<label htmlFor='reportNameInput'>
						{Liferay.Language.get('report-name')}
					</label>

					<input
						className='form-control'
						id='reportNameInput'
						maxLength={90}
						onInput={onInputReport}
						placeholder={Liferay.Language.get(
							'enter-a-name-for-this-report'
						)}
						type='text'
					/>
				</div>
			</div>
		</div>

		<div className='row'>
			<div className='col-sm-4'>
				<div className='form-group'>
					<label htmlFor='metricSelector'>
						{Liferay.Language.get('metric')}
					</label>

					<select
						className='form-control'
						id='metricSelector'
						onBlur={onBlurReport}
						onChange={onChangeReport}
					>
						<option value=''>
							{Liferay.Language.get('select-a-metric')}
						</option>

						{ASSET_METRICS.sort((previous, current) =>
							previous.selectTitle.localeCompare(
								current.selectTitle
							)
						).map(({key, selectTitle}) => (
							<option key={key} value={key}>
								{selectTitle}
							</option>
						))}
					</select>
				</div>
			</div>
		</div>

		<div className='row'>
			<div className='col-sm-12'>
				<div className='form-group'>
					<label>{Liferay.Language.get('visualization')}</label>

					<Thumbs
						items={thumbChartItems}
						onSelectThumb={onSelectThumbReport}
					/>
				</div>
			</div>
		</div>
	</div>
);

const AddReport: React.FC<IAddReportProps> = ({
	isEmptyDashboard = false,
	onGetReport
}) => {
	const [displayFormAddReport, setDisplayFormAddReport] = useState<boolean>(
		false
	);
	const [isEnableToSave, setIsEnableToSave] = useState<boolean>(false);
	const [report, setReport] = useState<Report>({
		chartType: '',
		metric: '',
		title: ''
	});

	useEffect(() => {
		setIsEnableToSave(true);

		Object.keys(report).forEach(key => {
			if (isEmpty(report[key])) {
				setIsEnableToSave(false);

				return;
			}
		});
	}, [report]);

	const handleClickAddReport = (): void => {
		setDisplayFormAddReport(true);

		setReport({
			chartType: '',
			metric: '',
			title: ''
		});
	};

	const handleClickCancelReport = (): void => {
		setDisplayFormAddReport(false);
	};

	const handleClickSaveReport = (): void => {
		onGetReport(report);

		setDisplayFormAddReport(false);
	};

	const handleChangeReportTitle = ({target}): void => {
		setReport({
			...report,
			title: target.value.trim().slice(0, 90)
		});
	};

	const handleChangeSelectMetric = ({target}): void => {
		setReport({
			...report,
			metric: target.value
		});
	};

	const handleGetSelectedChartType = ({value}): void => {
		setReport({
			...report,
			chartType: value
		});
	};

	const classnames = getCN('analytics-add-report', {
		'analytics-add-report-empty-dashboard': isEmptyDashboard
	});

	return (
		<Card className={classnames}>
			{displayFormAddReport ? (
				<>
					<Card.Header>
						<Card.Title>
							{Liferay.Language.get('add-report')}
						</Card.Title>
					</Card.Header>
					<Card.Body>
						<FormAddReport
							onBlurReport={handleChangeSelectMetric}
							onChangeReport={handleChangeSelectMetric}
							onInputReport={handleChangeReportTitle}
							onSelectThumbReport={handleGetSelectedChartType}
						/>
					</Card.Body>
					<Card.Footer>
						<ClayButton
							className='button-root mr-4'
							disabled={!isEnableToSave}
							onClick={
								isEnableToSave
									? handleClickSaveReport
									: undefined
							}
						>
							{Liferay.Language.get('save')}
						</ClayButton>

						<ClayButton
							className='button-root'
							displayType='secondary'
							onClick={handleClickCancelReport}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</Card.Footer>
				</>
			) : (
				<div className='analytics-add-report-button'>
					<ClayButton
						className='button-root'
						displayType='secondary'
						onClick={handleClickAddReport}
					>
						{Liferay.Language.get('add-report')}
					</ClayButton>
				</div>
			)}
		</Card>
	);
};

export default AddReport;
