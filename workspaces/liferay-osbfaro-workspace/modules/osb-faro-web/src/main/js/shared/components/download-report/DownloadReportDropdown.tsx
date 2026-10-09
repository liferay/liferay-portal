import ClayButton from '@clayui/button';
import ClayDropdown from '@clayui/drop-down';
import ClayIcon from 'shared/components/Icon';
import DownloadPDFReport from './DownloadPDFReport';
import Loading, {Align} from '../Loading';
import React from 'react';
import {CSVType, useMutationObserver} from './utils';
import {DownloadStaticCSVReport} from './DownloadStaticCSVReport';
import {useDataSources} from 'shared/context/dataSources';

const reportType = {
	CSV: {
		Component: (props: any) => (
			<DownloadStaticCSVReport
				segmentId={props.segmentId}
				type={CSVType.Membership}
				{...props}
			/>
		),
		label: Liferay.Language.get('csv-file'),
		value: 'csv',
	},
	PDF: {
		Component: (props: any) => (
			<DownloadPDFReport showDateRange={false} {...props} />
		),
		label: Liferay.Language.get('pdf-file'),
		value: 'pdf',
	},
};

interface IDownloadReportDropdownProps {
	className?: string;
	label?: string;
	onClick?: (event: React.MouseEvent) => void;
	segmentId?: string;
	subtitle: string;
	title: string;
}

const DownloadReportDropdown: React.FC<IDownloadReportDropdownProps> = ({
	className,
	label,
	onClick,
	segmentId,
	subtitle,
	title,
}) => {
	const {loadingCount} = useMutationObserver();
	const dataSourceStates = useDataSources();

	return (
		<>
			<ClayDropdown
				className={className}
				closeOnClick
				trigger={
					<ClayButton
						borderless
						displayType="secondary"
						onClick={onClick}
						size="sm"
					>
						<ClayIcon className="mr-2" symbol="download" />

						{Liferay.Language.get('download-reports')}

						<ClayIcon className="ml-2" symbol="caret-bottom" />
					</ClayButton>
				}
			>
				<ClayDropdown.ItemList>
					{Object.values(reportType).map((dropdownItem) => (
						<dropdownItem.Component
							disabled={
								dataSourceStates.empty ||
								(dropdownItem.value === 'pdf' && !!loadingCount)
							}
							key={dropdownItem.value}
							label={label}
							segmentId={segmentId}
							subtitle={subtitle}
							title={title}
						>
							<ClayDropdown.Item>
								{dropdownItem.value === 'pdf' &&
									!!loadingCount && (
										<Loading align={Align.Left} />
									)}

								{dropdownItem.label}
							</ClayDropdown.Item>
						</dropdownItem.Component>
					))}
				</ClayDropdown.ItemList>
			</ClayDropdown>
		</>
	);
};

export {DownloadReportDropdown};
