import {get, noop, uniqueId} from 'lodash';

export const ERROR_TYPES = {
	FILE_LIMIT: 'FILE_LIMIT',
	INVALID_FILE: 'INVALID_FILE',
	MULTIPLE_FILES: 'MULTIPLE_FILES',
	UPLOAD_FAILURE: 'UPLOAD_FAILURE',
};

export function normalizeFiles(files) {
	if (files instanceof Event) {
		files = files.target.files;
	}
	else if (files instanceof File) {
		files = [files];
	}

	const filesArr = [];

	for (let i = 0; i < files.length; i++) {
		filesArr.push(files[i]);
	}

	return filesArr;
}

export default class FileUploader {
	constructor(config = {}) {
		this.fileTypes = config.fileTypes || [];
		this.maxFileCount = get(config, 'maxFileCount', 1);
		this.onChange = config.onChange || noop;
		this.onError = config.onError || noop;
		this.uploadURL = config.uploadURL;
		this.useJaxRS = config.useJaxRS;
		this.addFiles = this.addFiles.bind(this);
	}

	render() {
		const id = uniqueId('fileUploader');

		let inputNode = document.getElementById(id);

		if (!inputNode) {
			inputNode = document.createElement('input');

			inputNode.setAttribute('hidden', true);
			inputNode.setAttribute('id', id);
			inputNode.setAttribute('type', 'file');

			if (this.fileTypes && Array.isArray(this.fileTypes)) {
				inputNode.setAttribute('accept', this.fileTypes.join(','));
			}

			if (this.maxFileCount > 1) {
				inputNode.setAttribute('multiple', true);
			}

			document.body.appendChild(inputNode);

			inputNode.addEventListener('change', this.addFiles);
		}

		this._inputNode = inputNode;

		return this;
	}

	destroy() {
		if (this._inputNode) {
			this._inputNode.removeEventListener('change', this.addFiles);

			this._inputNode.remove();
		}
	}

	/**
	 * Adds files that, if valid, are then immediately uploaded
	 * @param {FileList|File|Event|Array<File>} files - A file, any
	 * array-like container of files, or an Event object from a file input.
	 */
	addFiles(files) {
		files = normalizeFiles(files);

		const error = this.validateFiles(files);

		if (error) {
			this.emitError(error);
		}
		else {
			this.upload(files);
		}
	}

	clearInputData() {
		this._inputNode.value = '';

		if (this._reader) {
			this._reader.abort();
		}
	}

	/**
	 * Opens a file dialog for the user to select and immediately
	 * upload a file.
	 */
	openDialog() {
		const {_inputNode} = this;

		this.clearInputData();

		_inputNode.click();
	}

	emitError(error) {
		const {onError} = this;

		if (onError) {
			onError(error);
		}
	}

	onLoad(name, _id, event) {
		const {response, status} = event.target;

		this.onChange({
			_id,
			completed: true,
			name,
			response,
			status,
		});
	}

	onProgress(name, _id, event) {
		const {loaded, total} = event;

		this.onChange({
			_id,
			name,
			progress: (loaded / total) * 100,
			size: total,
		});
	}

	onStart(name, _id, url) {
		this.onChange({
			_id,
			name,
			progress: 0,
			url,
		});
	}

	sendRequest(file) {
		this._reader = new FileReader();

		this._reader.onload = (event) => {
			const localFilePath = event.target.result;

			const request = new XMLHttpRequest();

			const {name} = file;

			let data = file;

			if (this.useJaxRS) {
				request.open('PUT', `${this.uploadURL}/${name}`, true);

				request.setRequestHeader('content-type', 'multipart/form-data');
			}
			else {
				data = new FormData();

				data.append('multipartFile', file);

				request.open('POST', this.uploadURL, true);
			}

			const id = uniqueId('fileUpload');

			request.addEventListener(
				'error',
				this.emitError.bind(this, ERROR_TYPES.UPLOAD_FAILURE)
			);
			request.addEventListener('load', this.onLoad.bind(this, name, id));
			request.upload.addEventListener(
				'progress',
				this.onProgress.bind(this, name, id)
			);

			this.onStart(name, id, localFilePath);

			request.send(data);
		};

		this._reader.readAsDataURL(file);
	}

	upload(files) {
		files.forEach((file) => {
			this.sendRequest(file);
		});
	}

	validateFiles(files) {
		if (this.maxFileCount === 1 && files.length !== 1) {
			return ERROR_TYPES.MULTIPLE_FILES;
		}

		if (files.length > this.maxFileCount) {
			return ERROR_TYPES.FILE_LIMIT;
		}

		const validType = files.every((file) =>
			this.fileTypes.some((type) => file.name.endsWith(type))
		);

		if (!validType) {
			return ERROR_TYPES.INVALID_FILE;
		}
	}
}
