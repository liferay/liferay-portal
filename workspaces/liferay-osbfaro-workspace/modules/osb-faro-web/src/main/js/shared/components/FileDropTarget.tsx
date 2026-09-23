import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import DropTarget, {TYPES} from 'shared/components/DropTarget';
import FileUploader, {ERROR_TYPES} from '../util/FileUploader';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import React from 'react';
import TextTruncate from 'shared/components/TextTruncate';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {connect, ConnectedProps} from 'react-redux';
import {getLocale} from 'shared/util/locale';
import {round} from 'lodash';
import {sub} from 'shared/util/lang';

const ERROR_LANG_MAP = {
	[ERROR_TYPES.FILE_LIMIT]: Liferay.Language.get(
		'concurrent-file-upload-limit-reached'
	),
	[ERROR_TYPES.INVALID_FILE]: Liferay.Language.get('invalid-file-type'),
	[ERROR_TYPES.MULTIPLE_FILES]: Liferay.Language.get(
		'multiple-files-are-not-allowed'
	),
	[ERROR_TYPES.UPLOAD_FAILURE]: Liferay.Language.get(
		'an-unexpected-error-occurred-while-uploading-your-file'
	),
};

export const getFileSizeLabel = (value: number): string => {
	const kb = 1024;
	const mb = Math.pow(kb, 2);

	const kbSize = round(value / kb, 1);

	if (kbSize < 1000) {
		return sub(Liferay.Language.get('x-kb'), [
			kbSize.toLocaleString(getLocale()),
		]) as string;
	}

	return sub(Liferay.Language.get('x-mb'), [
		round(value / mb, 1).toLocaleString(getLocale()),
	]) as string;
};

type File = {
	completed?: boolean;
	name: string;
	progress?: number;
	response?: string;
	size?: number;
	status?: number;
	_id: string;
};

const getFileStatusIcon = (file: File) => {
	const error = file && file.status && file.status >= 400;

	if (file && file.completed) {
		if (error) {
			return (
				<ClayIcon
					className="failure-invert icon-root"
					symbol="exclamation-full"
				/>
			);
		}

		return (
			<ClayIcon
				className="icon-root success-invert"
				symbol="check-circle-full"
			/>
		);
	}

	return <Loading />;
};

interface IFileItemProps {
	file: File;
	onCancel: () => void;
}

export const FileItem: React.FC<IFileItemProps> = ({file, onCancel}) => {
	const error = file && file.status && file.status >= 400;

	return (
		<div className="file-item">
			<div className="d-flex flex-row align-items-center justify-content-between">
				<div>
					<div
						className={getCN('file-name d-flex', {
							completed: file.completed,
						})}
					>
						<TextTruncate title={file.name}>
							{file.name}
						</TextTruncate>

						<div className="status-wrapper">
							{getFileStatusIcon(file)}
						</div>
					</div>

					{error && (
						<div className="error-message">
							{Liferay.Language.get(
								'there-was-an-error-uploading-this-file.-please-remove-and-try-again'
							)}
						</div>
					)}
				</div>

				<div className="d-flex align-items-center">
					{file && file.completed && !error && !!file.size && (
						<div className="file-size">
							{getFileSizeLabel(file.size)}
						</div>
					)}

					<ClayButton
						className="button-root"
						displayType="link"
						onClick={onCancel}
						size="sm"
					>
						{Liferay.Language.get('remove')}
					</ClayButton>
				</div>
			</div>
		</div>
	);
};

const connector = connect(null, {addAlert});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IFileDropTargetProps extends PropsFromRedux {
	className?: string;
	fileTypes: string[];
	onChange: (file: File | null) => void;
	uploadURL: string;
	useJaxRS?: boolean;
}

export class FileDropTarget extends React.Component<IFileDropTargetProps> {
	state: {file: File | null} = {
		file: null,
	};

	constructor(props: IFileDropTargetProps) {
		super(props);
		this.handleCancel = this.handleCancel.bind(this);
		this.handleFileDrop = this.handleFileDrop.bind(this);
		this.handleFileProgress = this.handleFileProgress.bind(this);
		this.handleFileSelector = this.handleFileSelector.bind(this);
		this.handleUploadError = this.handleUploadError.bind(this);
	}

	componentDidMount() {
		const {fileTypes, uploadURL, useJaxRS} = this.props;

		this._uploader = new FileUploader({
			fileTypes,
			onChange: this.handleFileProgress,
			onError: this.handleUploadError,
			uploadURL,
			useJaxRS,
		}).render();
	}

	private _uploader: any;

	handleCancel() {
		const {onChange} = this.props;

		this._uploader.clearInputData();

		this.setState({file: null});

		if (onChange) {
			onChange(null);
		}
	}

	handleFileDrop(data: any) {
		this._uploader.addFiles(data.files);
	}

	handleFileProgress(updatedFile: any) {
		const {
			props: {onChange},
			state: {file},
		} = this;

		const newFile: File = {
			...(file as object),
			...updatedFile,
		} as File;

		this.setState({file: newFile});

		if (onChange) {
			onChange(newFile);
		}
	}

	handleFileSelector() {
		this._uploader.openDialog();
	}

	handleUploadError(error: any) {
		const {addAlert} = this.props;

		addAlert({
			alertType: Alert.Types.Warning,
			message: ERROR_LANG_MAP[error as keyof typeof ERROR_LANG_MAP],
		});
	}

	render() {
		const {
			props: {className},
			state: {file},
		} = this;

		return (
			<div className={getCN('file-drop-target-root', className)}>
				{file ? (
					<div className="uploaded-file">
						<FileItem file={file} onCancel={this.handleCancel} />
					</div>
				) : (
					<DropTarget
						message={Liferay.Language.get(
							'or-drag-and-drop-files-here'
						)}
						onDrop={this.handleFileDrop}
						targetType={[TYPES.FILE]}
					>
						<ClayButton
							className="button-root"
							displayType="secondary"
							onClick={this.handleFileSelector}
							size="sm"
						>
							{Liferay.Language.get('select-file')}
						</ClayButton>
					</DropTarget>
				)}
			</div>
		);
	}
}

export default connector(FileDropTarget);
